package bottle.mq_active;

import bottle.MQLog;
import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;

import bottle.util.StringUtil;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;


import javax.jms.*;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


/**
 * @author 11842
 * @version 1.1.1
 * @description
 * @time 2019/11/25 18:12
 **/
@PropertiesFilePath("/activemq.properties")
public class MQPoolUtil {

    @PropertiesName("broker.url")
    public static String brokerUrl;
    @PropertiesName("jms.rmi")
    public static String jmsRmi;
    @PropertiesName("maxConnection")
    public static int maxConnection;
    @PropertiesName("maxSessionConnection")
    public static int maxSessionConnection;
    @PropertiesName("userName")
    public static String userName;
    @PropertiesName("password")
    public static String password;

    private static javax.jms.Connection connection;

    static {
        ApplicationPropertiesBase.initStaticFields(MQPoolUtil.class);
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(userName,password,brokerUrl);
        connectionFactory.setAlwaysSessionAsync(false);
        connectionFactory.setDispatchAsync(true);
        connectionFactory.setMaxThreadPoolSize(64);
        connectionFactory.setUseDedicatedTaskRunner(true);
        connectionFactory.setTrustAllPackages(true);
        connectionFactory.setUseAsyncSend(true);


        org.apache.activemq.pool.PooledConnectionFactory poolFactory = new org.apache.activemq.pool.PooledConnectionFactory(connectionFactory);
        poolFactory.setMaxConnections(maxConnection);
        poolFactory.setMaximumActiveSessionPerConnection(maxSessionConnection);
        try {
            connection = poolFactory.createConnection();
            connection.start();
        } catch (Exception e) {
            MQLog.error("MQ启动失败",e);
        }
    }

    /* 发送消息到消息队列 */
    public static boolean sendMessage(String msgType, String message){

        return sendMessageDelay(msgType,message,0);
    }

    public static boolean sendMessageDelay(String msgType, String message,long timeSec){
        Session session = null;
        MessageProducer messageProducer = null;
        try{
            MQLog.activemq_write("producer",String.format("[发送] %s\t%s",msgType, message));
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(msgType);
            messageProducer = session.createProducer(destination);
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);//持久化
            TextMessage textMessage = session.createTextMessage(message);
            if (timeSec>0) textMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,timeSec * 1000);
            messageProducer.send(textMessage);

            MQLog.activemq_write("producer",
                    String.format("[完成] %s\t%s\n",msgType, message));
            return true;
        }catch (Exception e){
            MQLog.activemq_write("producer",
                    String.format("[失败] %s\t%s\n", msgType, message));
            MQLog.error("[activeMQ] 发送消息失败,类型 = "+msgType,e);
        }finally {
            try {
                if (messageProducer != null) {
                    messageProducer.close();
                }
            } catch (JMSException ignored) { }
            try {
                if (session != null) {
                    session.close();
                }
            } catch (JMSException ignored) { }

        }
        return false;
    }


    public static abstract class AutoRegConsumer implements MessageListener{
        private final String msgType;

        public AutoRegConsumer(String msgType){
            this.msgType = msgType;
            MQPoolUtil.addConsumer(msgType,this);
        }

        @Override
        public void onMessage(Message message) {
            try{
                TextMessage textMessage = (TextMessage) message;
                String messageStr = textMessage.getText();
                MQLog.activemq_write("consumer/"+msgType,messageStr);
                _onMessage(messageStr);
            }catch (Exception e){
                MQLog.error("[activeMQ] 接受消息异常",e);
            }
        }

        abstract public void _onMessage(String message);
    }


    /* 添加监听者 */
    public static void addConsumer(String msgType,MessageListener listener){
        try {
            if (connection == null) return;
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue destination = session.createQueue(msgType);
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(listener);
            MQLog.info("MQ消费者自动注册完成, type: "+ msgType+ " instance: "+listener);
        } catch (JMSException e) {
            MQLog.error("无法添加MQ消费者,类型="+msgType,e);
        }
    }

    public static long getQueueSize(String msgType){
        try {
            JMXServiceURL url = new JMXServiceURL(jmsRmi);
            MBeanServerConnection c = JMXConnectorFactory.connect(url, null).getMBeanServerConnection();
            ObjectName objectName = new ObjectName("org.apache.activemq:brokerName=localhost,type=Broker");
            BrokerViewMBean brokerViewMBean = MBeanServerInvocationHandler.newProxyInstance(c,objectName,
                    BrokerViewMBean.class,true);
            for (ObjectName queue : brokerViewMBean.getQueues()) {
                QueueViewMBean queueBean = MBeanServerInvocationHandler.newProxyInstance
                        (c, queue, QueueViewMBean.class, true);
                if (!StringUtil.isEmpty(queueBean.getName()) && queueBean.getName().equals(msgType)) {
                    return queueBean.getQueueSize();
                }
            }
        } catch (Exception e) {
            MQLog.error("获取MQ执行类型队列大小失败,类型="+msgType,e);
        }
        return 0;
    }

    //销毁
    public static void destroy(){
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            MQLog.error("",e);
        }
    }

}
