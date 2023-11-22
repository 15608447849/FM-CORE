package jdbc.imp;


import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.DataBaseType;
import jdbc.define.session.JDBCSessionManagerAbs;
import bottle.tuples.Tuple2;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.lang.reflect.Field;

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
    public void initialize(Properties props) {
        try {
            String url = props.getProperty("url");
            int aIndex = url.indexOf(":");
            int bIndex = url.indexOf("//");
            int cIndex = url.indexOf("?") > 0 ? url.indexOf("?") : url.length();

            String dataBaseTypeStr = url.substring(aIndex+1,bIndex-1);
            DataBaseType dataBaseType = DataBaseType.valueOf(dataBaseTypeStr);

            String addressInfoStr = url.substring(bIndex + 2, cIndex);
            int dIndex = addressInfoStr.indexOf("/");
            String address = addressInfoStr.substring(0, dIndex);
            String host = address.split(":")[0];
            int port = Integer.parseInt(address.split(":")[1]);

            String dataBaseName = addressInfoStr.substring(dIndex + 1);

            PoolProperties poolProperties = new PoolProperties();
            setPoolPropertiesValue(poolProperties,props);
            initPropDefault(dataBaseType, poolProperties);
            String username = poolProperties.getUsername();
            String password = poolProperties.getPassword();
            // 创建数据源
            dataSource = new DataSource(poolProperties);

            // 设置数据库连接信息
            setDataBaseInfo(dataBaseType, host, port, username, password, dataBaseName);

        } catch (Exception e) {
            throw new JDBCException(e);
        }
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

    @SuppressWarnings("unchecked")
    private static void initPropDefault(DataBaseType dataBaseType,PoolProperties poolProperties) {

        poolProperties.setUrl(
                addKVToURL(poolProperties.getUrl(),
                        new Tuple2<>("verifyServerCertificate","false"),
                        new Tuple2<>("useSSL","false"),
                        new Tuple2<>("autoReconnect","true"),
                        new Tuple2<>("failOverReadOnly","false"),
                        new Tuple2<>("useUnicode","true"),
                        new Tuple2<>("characterEncoding","utf8"),
                        new Tuple2<>("rewriteBatchedStatements","true"),
                        new Tuple2<>("serverTimezone","Asia/Shanghai"))
        );
        JDBCLogger.print("数据库连接URL = " + poolProperties.getUrl());

        switch (dataBaseType){
            case mysql:
                poolProperties.setDriverClassName("com.mysql.cj.jdbc.Driver");
                break;
            case clickhouse:
                poolProperties.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");

                break;
                default:throw new IllegalArgumentException("数据库类型异常,无法加载"+ dataBaseType.name() +"驱动");
        }

        poolProperties.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

        //SQL查询，该查询将用于验证来自此池的连接，然后再将其返回给调用方。如果指定，此查询不必返回任何数据，而不能抛出SQLException。默认值为null。如果未指定，则将通过isValid（）方法验证连接
        poolProperties.setValidationQuery("SELECT 1");

        //设置此属性才能运行池清洁器/测试线程
        //指明连接是否被空闲连接回收器( 如果有) 进行检验。 如果检测失败， 则连接将被从池中去除。注意： 设置为true 后如果要生效，validationQuery 参数必须设置为非空字符串
        poolProperties.setTestWhileIdle(true);
        //指明是否在从池中取出连接前进行检验， 如果检验失败， 则从池中去除连接并尝试取出另一个。注意： 设置为true 后如果要生效，validationQuery 参数必须设置为非空字符串
        //参考validationInterval以获得更有效的验证
        poolProperties.setTestOnBorrow(true);
        //指明是否在归还到池中前进行检验 注意： 设置为true 后如果要生效，validationQuery 参数必须设置为非空字符串
        poolProperties.setTestOnReturn(false);

        poolProperties.setInitialSize(1);

        poolProperties.setMaxActive(Runtime.getRuntime().availableProcessors() * 64 + 1);

        // 最大空闲连接： 连接池中容许保持空闲状态的最大连接数量， 超过的空闲连接将被释放， 如果设置为负数表示不限制
        //如果启用，将定期检查限制连接，如果空闲时间超过minEvictableIdleTimeMillis 则释放连接
        poolProperties.setMaxIdle(10);
        //最小空闲连接： 连接池中容许保持空闲状态的最小连接数量， 低于这个数量将创建新的连接， 如果设置为0 则不创建
        //如果连接验证失败将缩小这个值 ( 参考 testWhileIdle)
        poolProperties.setMinIdle(1);

         //连接空闲的最短时间。默认值为60000（60秒）
        poolProperties.setMinEvictableIdleTimeMillis(60 * 1000);
        // 在空闲连接回收器线程运行期间休眠的时间值， 以毫秒为单位。 如果设置为非正数， 则不运行空闲连接回收器线程
        //这个值不应该小于1秒，它决定线程多久验证连接或丢弃连接
        poolProperties.setTimeBetweenEvictionRunsMillis(2 * 60 * 1000);
        // 在每次空闲连接回收器线程( 如果有) 运行时检查的连接数量
//        poolProperties.setNumTestsPerEvictionRun(0);

        //无可用连接时 池将等待连接返回的最大毫秒数  如果设置为-1 表示无限等待
        poolProperties.setMaxWait(-1);

        //避免过多的验证，最多只能在此频率下运行验证-时间,以毫秒为单位。
        //如果连接应进行验证，但之前已在此时间间隔内进行验证，则不会再次对其进行验证。默认值为3000（3秒）
        poolProperties.setValidationInterval(30000);

        boolean isPrintLogAbandoned = true;
        if (dataBaseType == DataBaseType.clickhouse){
            isPrintLogAbandoned = false;
        }
        //标记为放弃连接的应用程序代码记录堆栈跟踪。记录废弃的连接会增加每次连接借用的开销，因为必须生成堆栈跟踪
        poolProperties.setLogAbandoned(isPrintLogAbandoned);
        //对执行超过指定时间的连接对象进行删除,放止连接泄露
        poolProperties.setRemoveAbandoned(true);
        //泄露的连接可以被删除的超时值， 单位秒  应设置为应用中查询执行最长的时间
        poolProperties.setRemoveAbandonedTimeout(5 * 60);

        //如果您希望在连接上建立一道屏障防止连接关闭之后被重新使用，设置这个属性为true。这个属性用来预防线程保持已关闭连接的引用，并在上面执行查询动作
        poolProperties.setUseDisposableConnectionFacade(true);

    }

    // url=jdbc:mysql://localhost:3306/erp-global?
    // verifyServerCertificate=false&useSSL=false&autoReconnect=true&failOverReadOnly=false&useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&serverTimezone=UTC
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
    @SuppressWarnings("unchecked")
    private static String addKVToURL(String url, Tuple2<String,String>... arr){
        for (Tuple2<String,String> it : arr){
            url = _addKVToURL(url,it.getValue0(),it.getValue1());
        }
        return url;
    }

    private static void setPoolPropertiesValue(PoolProperties poolProperties, Properties props) {
        Field[] fields = poolProperties.getClass().getDeclaredFields();

        String name = null;
        String value = null;
        for (Field field : fields){
            try {
                name = field.getName();//获取属性的名字
                value = props.getProperty(name);
                if (value == null) continue;

                field.setAccessible(true);
                Class<?> filedType =  field.getType();
                Object oValue = TomcatJDBCTool.convertStringToBaseType(value,filedType);
                field.set(poolProperties, oValue);
            } catch (Exception e) {
               JDBCLogger.print("数据库连接池配置参数不正确, name = "+ name+", value = "+ value);
            }
        }
    }

    @Override
    protected Connection getInternalConnection() throws SQLException {
        return this.dataSource.getConnection();

    }


    @Override
    public void closeSessionAll() {
        //关闭全部连接
        if (dataSource!=null) {
            //关闭全部连接
            dataSource.close(true);
        }
    }


}
