package jdbc.imp;

import bottle.util.EncryptUtil;
import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.DataBaseType;
import jdbc.define.session.JDBCSessionManagerAbs;
import jdbc.define.tuples.Tuple2;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;


import java.io.*;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @Author: leeping
 * @Date: 2019/8/16 10:04
 */
public class TomcatJDBCPool extends JDBCSessionManagerAbs{

    private DataSource dataSource;

    @Override
    public void initialize(Object... args) {
        try {
            if (args.length >= 1) {
                if (args[0] instanceof InputStream) {
                    loadProperties((InputStream) args[0]);
                    return;
                }
            }
            throw new InvalidParameterException("tomcat-jdbc connection pool initialize fail");
        } catch (Exception e) {
            throw new JDBCException(e);
        }
    }



    private void loadProperties(InputStream is) throws IOException {
        Properties props = new Properties();
        props.load(is);

        String seqStr = props.getProperty("node.seq");
        seq = Integer.parseInt(seqStr);

        String url = props.getProperty("url");

        int aIndex = url.indexOf(":");
        int bIndex = url.indexOf("//");
        int cIndex = url.indexOf("?") > 0 ? url.indexOf("?") : url.length();

        String dataBaseTypeStr = url.substring(aIndex+1,bIndex-1);
        dataBaseType = DataBaseType.valueOf(dataBaseTypeStr);

        String addressInfoStr = url.substring(bIndex + 2, cIndex);
        int dIndex = addressInfoStr.indexOf("/");
        address = addressInfoStr.substring(0, dIndex);
        dataBaseName = addressInfoStr.substring(dIndex + 1);
        identity = EncryptUtil.encryption(dataBaseTypeStr+"@"+address+"@"+dataBaseName);

        PoolProperties poolProperties = new PoolProperties();
        setPoolPropertiesValue(poolProperties,props);
        initPropDefault(poolProperties);

        dataSource = new DataSource();
        dataSource.setPoolProperties(poolProperties);
        loadDefaultTransactionIsolationLevel();
    }

    /*
      #启动池时创建的初始连接数。默认值为10
      initialSize=5
      #可以同时从该池分配的最大活动连接数。默认值为100
      maxActive=500

      #始终应保留在池中的已建立连接的最小数目,如果验证查询失败，则连接池可以缩小到该数字以下。默认值源自initialSize
      minIdle=5
      #始终应保留在池中的最大连接数,默认值为 maxActive：100 并且空闲时间长于minEvictableIdleTimeMillis 释放时间的连接
      maxIdle=500

      #指示空闲对象退出者（如果有）是否将验证对象,如果对象验证失败，则会将其从池中删除。默认值为，false并且必须设置此属性才能运行池清洁器/测试线程（另请参见timeBetweenEvictionRunsMillis）
      testWhileIdle=true

      #空闲连接验证/清除线程的运行之间要休眠的毫秒数。此值不应在1秒内设置。它决定了我们检查空闲，被放弃的连接的频率以及验证空闲连接的频率。默认值为5000（5秒）。
      timeBetweenEvictionRunsMillis=120000

      #避免过多的验证，最多只能在此频率下运行验证-时间以毫秒为单位。如果连接应进行验证，但之前已在此时间间隔内进行验证，则不会再次对其进行验证。默认值为3000（3秒）
      validationInterval=30000
      #一个对象在有资格被驱逐之前可以在池中空闲的最短时间。默认值为60000（60秒）
      minEvictableIdleTimeMillis=60000

      #是否清除已经超过“removeAbandonedTimout”设置的无效连接
      removeAbandoned=true
      #超时（以秒为单位），可以删除已废弃（正在使用）的连接。默认值为60（60秒）。该值应设置为您的应用程序可能具有的最长运行查询
      removeAbandonedTimeout=120

      #在引发异常之前，池将等待（无可用连接时）连接返回的最大毫秒数 默认值为30000（30秒）
      maxWait=5000
      */
    private void initPropDefault(PoolProperties poolProperties) {
        poolProperties.setUrl(
                addKVToURL(poolProperties.getUrl(),
                        new Tuple2<>("verifyServerCertificate","false"),
                        new Tuple2<>("useSSL","false"),
                        new Tuple2<>("autoReconnect","true"),
                        new Tuple2<>("failOverReadOnly","false"),
                        new Tuple2<>("useUnicode","true"),
                        new Tuple2<String, String>("characterEncoding","utf8"),
                        new Tuple2<>("rewriteBatchedStatements","true"),
                        new Tuple2<String, String>("serverTimezone","Asia/Shanghai"))
        );
        JDBCLogger.print("数据库连接URL = " + poolProperties.getUrl());

        switch (dataBaseType){
            case mysql:
                poolProperties.setDriverClassName("com.mysql.cj.jdbc.Driver");
                break;
            case clickhouse:
                poolProperties.setDriverClassName("ru.yandex.clickhouse.ClickHouseDriver");
                break;
                default:throw new IllegalArgumentException("数据库类型异常,无法加载"+ dataBaseType.name() +"驱动");
        }

        poolProperties.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

        //SQL查询，该查询将用于验证来自此池的连接，然后再将其返回给调用方。如果指定，此查询不必返回任何数据，而不能抛出SQLException。默认值为null。如果未指定，则将通过isValid（）方法验证连接
        poolProperties.setValidationQuery("SELECT 1");

        //设置此属性才能运行池清洁器/测试线程
        poolProperties.setTestWhileIdle(true);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setTestOnReturn(false);

         //连接空闲的最短时间。默认值为60000（60秒）
        poolProperties.setMinEvictableIdleTimeMillis(60000);

        //无可用连接时 池将等待连接返回的最大毫秒数
        poolProperties.setMaxWait(10000);

        //避免过多的验证，最多只能在此频率下运行验证-时间,以毫秒为单位。
        // 如果连接应进行验证，但之前已在此时间间隔内进行验证，则不会再次对其进行验证。默认值为3000（3秒）
        poolProperties.setValidationInterval(30000);

        //标记为放弃连接的应用程序代码记录堆栈跟踪。记录废弃的连接会增加每次连接借用的开销，因为必须生成堆栈跟踪
        poolProperties.setLogAbandoned(false);
        //对执行超过指定时间的连接对象进行删除,放置连接泄露
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setRemoveAbandonedTimeout(300);

    }

// url=jdbc:mysql://localhost:3306/erp-global?verifyServerCertificate=false&useSSL=false&autoReconnect=true&failOverReadOnly=false&useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&serverTimezone=UTC
    private static String _addKVToURL(String url,String kStr,String vStr){
        int sPoint = url.indexOf(kStr);
        if ( sPoint > 0){
            String sub = url.substring(sPoint);

            int i = sub.indexOf("&");

            if (i>0){
                sub = sub.substring(0,sub.indexOf("&"));
            }

            String[] arr = sub.split("=");

            if (!arr[1].equals(vStr)){
                String rpSub = sub.replaceAll(arr[1],vStr);
                url = url.replaceAll(sub,rpSub);
            }

        }else {
            url = url + (url.lastIndexOf("&") == url.length()-1 || url.lastIndexOf("?") == url.length()-1?"":"&") + kStr + "=" + vStr;
        }
        return url;
    }

    private static String addKVToURL(String url, Tuple2<String,String>... arr){
        for (Tuple2<String,String> it : arr){
            url = _addKVToURL(url,it.getValue0(),it.getValue1());
        }
        return url;
    }

    private void setPoolPropertiesValue(PoolProperties poolProperties, Properties props) {
        Field[] fields = poolProperties.getClass().getDeclaredFields();
        for (Field field : fields){
            try {
                String name = field.getName();//获取属性的名字
                String value = props.getProperty(name);
                if (value == null) continue;
                field.setAccessible(true);
                Class<?> filedType =  field.getType();
                Object oValue = TomcatJDBCTool.convertStringToBaseType(value,filedType);
                field.set(poolProperties, oValue);
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
    }

    @Override
    protected Connection getInternalConnection() throws SQLException {
        Connection connection = this.dataSource.getConnection();
        return connection;
    }

    @Override
    public void setConnectionFail() {
        //关闭全部连接
        if (dataSource!=null) {
            //关闭全部连接
            dataSource.close(true);
        }
    }

    @Override
    public void unInitialize() {
        try {
            //关闭全部
            setConnectionFail();
            super.unInitialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
