package redis.config;

import bottle.util.GoogleGsonUtil;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.provide.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Reids集群工厂
 * 
 * @author Administrator
 * @since 2018-06-14
 * @version 1.0
 *
 */
public class JedisClusterBuild {
	private final JedisCluster jedisCluster;

	private final RedisStringProvide stringProvide = new RedisStringProvide(this);
	private final RedisHashProvide mapProvide = new RedisHashProvide(this);
	private final RedisListProvide listProvide = new RedisListProvide(this);
	private final RedisSetProvide setProvide = new RedisSetProvide(this);
	private final RedisHashProvide redisHashProvide = new RedisHashProvide(this);
	private final RedisSortedSetProvide sortedSetProvide = new RedisSortedSetProvide(this);

	private JedisClusterBuild(JedisPoolConfig jedisPoolConfig,String[] hosts, String password){
		Set<HostAndPort> jedisClusterNodes= new HashSet<>();
		//Jedis Cluster will attempt to discover cluster nodes automatically
		for (String host : hosts) {
			String[] datas = host.split(":");
			jedisClusterNodes.add(new HostAndPort(datas[0], Integer.parseInt(datas[1])));
		}
		jedisCluster = new JedisCluster(jedisClusterNodes,5000, 1000, 3, password, jedisPoolConfig);
	}


	public JedisCluster getJedisCluster(){
		return jedisCluster;
	}

	public RedisStringProvide getStringProvide() {
		return stringProvide;
	}

	public RedisHashProvide getMapProvide() {
		return mapProvide;
	}

	public RedisListProvide getListProvide() {
		return listProvide;
	}

	public RedisSetProvide getSetProvide() {
		return setProvide;
	}

	public RedisHashProvide getRedisHashProvide() {
		return redisHashProvide;
	}

	public RedisSortedSetProvide getSortedSetProvide() {
		return sortedSetProvide;
	}


	public static JedisClusterBuild create(String[] hosts, String password,int maxTotal,int maxIdle,int minIdle,int minEvict,int maxWaitMillis,boolean testOnBorrow,int timeBERM){
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		//最大连接数，默认8
		poolConfig.setMaxTotal(maxTotal);
		//最大空闲数,默认8
		poolConfig.setMaxIdle(maxIdle);
		//最小空闲连接数，默认0
		poolConfig.setMinIdle(minIdle);
		//对象最小空闲时间，默认1800000毫秒(30分钟)
		poolConfig.setMinEvictableIdleTimeMillis(minEvict);
		//获取连接的最大等待毫秒数。如果设为小于0，则永远等待
		poolConfig.setMaxWaitMillis(maxWaitMillis);
		//在创建对象时检测对象是否有效，true是，默认值是false
		poolConfig.setTestOnCreate(true);
		//从对象池获取对象时检测对象是否有效，默认false
		poolConfig.setTestOnBorrow(testOnBorrow);

		//在向对象池中归还对象时是否检测对象有效，true是，默认值是false
		poolConfig.setTestOnReturn(true);
		//在检测空闲对象线程检测到对象不需要移除时，是否检测对象的有效性。true是，默认值是false
		poolConfig.setTestWhileIdle(true);
		//检测空闲对象线程每次检测的空闲对象的数量。默认值是3；如果这个值小于0,则每次检测的空闲对象数量等于当前空闲对象数量除以这个值的绝对值，并对结果向上取整
		poolConfig.setNumTestsPerEvictionRun(3);
		//是否启用后进先出, 默认true
		poolConfig.setLifo(true);
		//多长时候执行一次空闲对象检测。单位是毫秒数。如果小于等于0，则不执行检测线程。默认值是-1
		poolConfig.setTimeBetweenEvictionRunsMillis(timeBERM);
		//当对象池没有空闲对象时，新的获取对象的请求是否阻塞。true阻塞。默认值是true;
		poolConfig.setBlockWhenExhausted(true);
		///是否启用pool的jmx管理功能, 默认true
		poolConfig.setJmxEnabled(true);

		System.out.println(GoogleGsonUtil.javaBeanToJson(poolConfig));

		return new JedisClusterBuild(poolConfig,hosts,password);

	}

}
