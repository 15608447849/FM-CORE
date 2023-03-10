package bottle.mq_active;

import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;
import bottle.util.Log4j;
import bottle.util.StringUtil;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.jms.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.*;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Date;

import static bottle.util.Log4j.sdf;

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

    private static PooledConnection connection;

    static {
        ApplicationPropertiesBase.initStaticFields(MQPoolUtil.class);
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(userName,password,brokerUrl);
        connectionFactory.setAlwaysSessionAsync(false);
        connectionFactory.setDispatchAsync(true);
        connectionFactory.setMaxThreadPoolSize(64);
        connectionFactory.setUseDedicatedTaskRunner(true);
        connectionFactory.setTrustAllPackages(true);
        connectionFactory.setUseAsyncSend(true);
        PooledConnectionFactory poolFactory = new PooledConnectionFactory(connectionFactory);
        poolFactory.setMaxConnections(maxConnection);
        poolFactory.setMaximumActiveSessionPerConnection(maxSessionConnection);
        try {
            connection = (PooledConnection) poolFactory.createConnection();
            connection.start();
        } catch (JMSException e) {
            Log4j.error("MQ????????????",e);
        }
    }



    /* ??????????????????????????? */
    public static boolean sendMessage(String msgType, String message){
        Session session = null;
        MessageProducer messageProducer = null;
        try{
            Log4j.writeLogToSpecFile("./logs/activemq/producer",Log4j.sdfDict.format(new Date()),
                    String.format("%s [??????] %s\t%s\n", Log4j.sdf.format(new Date()),msgType, message));
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(msgType);
            messageProducer = session.createProducer(destination);
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);//?????????
            TextMessage textMessage = session.createTextMessage(message);
            messageProducer.send(textMessage);
            Log4j.writeLogToSpecFile("./logs/activemq/producer",Log4j.sdfDict.format(new Date()),
                    String.format("%s [??????] %s\t%s\n", Log4j.sdf.format(new Date()),msgType, message));
            return true;
        }catch (Exception e){
            Log4j.writeLogToSpecFile("./logs/activemq/producer",Log4j.sdfDict.format(new Date()),
                    String.format("%s [??????] %s\t%s\n", Log4j.sdf.format(new Date()),msgType, message));
            Log4j.error("[activeMQ] ??????????????????,?????? = "+msgType,e);
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
                Log4j.writeLogToSpecFile("./logs/activemq/consumer/"+msgType,Log4j.sdfDict.format(new Date()),
                        Log4j.sdf.format(new Date())+"\t"+messageStr+"\n");
                _onMessage(messageStr);
            }catch (Exception e){
              Log4j.error("[activeMQ] ??????????????????",e);
            }
        }

        abstract public void _onMessage(String message);
    }


    /* ??????????????? */
    public static void addConsumer(String msgType,MessageListener listener){
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue destination = session.createQueue(msgType);
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(listener);
            Log4j.info("MQ???????????????????????????, type: "+ msgType+ " instance: "+listener);
        } catch (JMSException e) {
            Log4j.error("????????????MQ?????????,??????="+msgType,e);
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
            Log4j.error("??????MQ??????????????????????????????,??????="+msgType,e);
        }
        return 0;
    }

    //??????
    public static void destroy(){
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            Log4j.error("",e);
        }
    }

}
