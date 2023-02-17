package redis;

import bottle.log.LogBean;
import bottle.log.LogLevel;
import bottle.log.PrintLogThread;
import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;
import redis.clients.jedis.JedisCluster;
import redis.config.JedisClusterBuild;
import redis.provide.RedisHashProvide;
import redis.provide.RedisListProvide;
import redis.provide.RedisSetProvide;
import redis.provide.RedisStringProvide;

@PropertiesFilePath("/redis.properties")
public class RedisCacheUtil {

    @PropertiesName("redis.hosts")
    public static String pro_hosts ;
    @PropertiesName("redis.password")
    public static String password;
    @PropertiesName("redis.maxTotal")
    public static int maxTotal ;
    @PropertiesName("redis.maxIdle")
    public static int maxIdle ;
    @PropertiesName("redis.minEvictableIdleTimeMilli")
    public static int minEvict;
    @PropertiesName("redis.testOnBorrow")
    public static boolean testOnBorrow;
    @PropertiesName("redis.timeBetweenEvictionRunsMillis")
    public static int timeBERM;
    @PropertiesName("redis.maxWaitMillis")
    public static int maxWaitMillis;

    private static final JedisClusterBuild build;

    static {
        ApplicationPropertiesBase.initStaticFields(RedisCacheUtil.class);
        PrintLogThread.addMessageQueue(new LogBean(LogLevel.info,
                "REDIS集群, pro_hosts="+pro_hosts+" , password = "+ password));
        build = JedisClusterBuild.create(pro_hosts.split(","),password,maxTotal,maxIdle,3,minEvict,maxWaitMillis,testOnBorrow,timeBERM);
    }

    public static JedisCluster getJedisCluster(){
        return build.getJedisCluster();
    }

    public static RedisStringProvide getStringProvide() {
        return build.getStringProvide();
    }

    public static RedisListProvide getListProvide() {
        return build.getListProvide();
    }

    public static RedisSetProvide getSetProvide(){return build.getSetProvide();}

    public static RedisHashProvide getHashProvide(){return build.getRedisHashProvide();}


}
