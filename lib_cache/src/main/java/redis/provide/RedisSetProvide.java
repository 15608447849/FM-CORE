package redis.provide;

import redis.config.JedisClusterBuild;

import java.util.Set;

/**
 * Redis 的 Set 是 String 类型的无序集合。集合成员是唯一的，这就意味着集合中不能出现重复的数据。Redis 中集合是通过哈希表实现的，所以添加，删除，查找的复杂度都是 O(1)。
 * 集合中最大的成员数为 4294967295 (每个集合可存储40多亿个成员)。
 * 
 * @author JiangWenGuang
 * @since 2018-06-14
 * @version 1.0
 */
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
