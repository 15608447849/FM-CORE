package redis.provide;

import redis.config.JedisClusterBuild;

import java.util.Set;


public class RedisSetProvide extends RedisProvide {
	
	public RedisSetProvide(JedisClusterBuild jedisClusterBuild) {
		super(jedisClusterBuild);
	}

	/**
	 * 添加元素
	 * 
	 * @param colname 集合名
	 * @param val 元素值
	 * @return 0:添加失败 1:添加成功
	 */
	public Long addElement(String colname,String val) {
		return build.getJedisCluster().sadd(colname, val);
	}
	
	/**
	 * 根据元素值校验集合中是否存在元素
	 * 
	 * @param colname 集合名
	 * @param val 元素值
	 * @return true:存在 false:不存在
	 */
	public Boolean existElement(String colname, String val) {
		return build.getJedisCluster().sismember(colname, val);
	}
	
	/**
	 * 根据元素值删除集合中包含该元素值所有元素
	 * 
	 * @param colname 集合名
	 * @param val 元素值
	 * @return 0:代表删除失败
	 */
	public Long deleteElementByVal(String colname, String val) {
		return build.getJedisCluster().srem(colname, val);
	}
	
	
	/**
	 * 获取所有的元素
	 * 
	 * @param colname 集合名
	 * @return
	 */
	public Set<String> getAllElements(String colname) {
		return build.getJedisCluster().smembers(colname);
	}
	
	/**
	 * 获取集合的成员个数
	 * 
	 * @param colname 集合名
	 * @return
	 */
	public Long size(String colname) {
		return build.getJedisCluster().scard(colname);
	}

}
