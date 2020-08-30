package redis.provide;

import redis.config.JedisClusterBuild;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis hash 是一个string类型的field和value的映射表，hash特别适合用于存储对象。Redis 中每个 hash 可以存储 4294967295 键值对（40多亿）。
 */
public class RedisHashProvide extends RedisProvide {
	

	public RedisHashProvide(JedisClusterBuild jedisClusterBuild) {
		super(jedisClusterBuild);
	}

	/**
	 * 添加元素
	 * 
	 * @param colname 集合名字 
	 * @param key 哈希表 key
	 * @param val 哈希表value
	 * @return 0:添加失败 1:添加成功
	 */
	public Long putElement(String colname,String key,String val) {
		return build.getJedisCluster().hset(colname,key, val);
	}
	
	/**
	 * 根据哈希表 key是否存在元素
	 * 
	 * @param colname 集合名字 
	 * @param key 哈希表 key
	 * @return true:存在 false:不存在
	 */
	public Boolean existsByKey(String colname,String key) {
		return build.getJedisCluster().hexists(colname,key);
	}
	
	/**
	 * 根据哈希表 key删除元素
	 * 
	 * @param colname 集合名字 
	 * @param key 哈希表 key
	 * @return 0:代表删除失败
	 */
	public Long delByKey(String colname,String key) {
		return build.getJedisCluster().hdel(colname,key);
	}
	
	/**
	 * 根据哈希表 key获取哈希表value
	 * 
	 * @param colname 集合名字 
	 * @param key 哈希表 key
	 * @return
	 */
	public String getValByKey(String colname,String key) {
		return build.getJedisCluster().hget(colname, key);
	}
	
	/**
	 * 根据集合名字获取元素个数
	 * 
	 * @param colname 集合名字
	 */
	public Long size(String colname) {
		return build.getJedisCluster().hlen(colname);
	}
	
	/**
	 * 根据集合名字获取所有哈希表 key
	 * 
	 * @param colname 集合名字
	 * @return
	 */
	public Set<String> getAllKeys(String colname) {
		return build.getJedisCluster().hkeys(colname);
	}
	
	/**
	 * 根据集合名字获取所有哈希表 value
	 * 
	 * @param colname 集合名字
	 * @return
	 */
	public List<String> getAllVals(String colname) {
		return build.getJedisCluster().hvals(colname);
	}

	/**
	 * 根据集合名字获取所有哈希表 key-val
	 * @param colname 集合名字
	 * @return
	 */
	public Map<String,String> getAllHash(String colname){
		return build.getJedisCluster().hgetAll(colname);
	}

	/**
	 * 设置一个哈希表到一个集合中
	 *
	 * @param colname 集合名字
	 * @param hash 哈希表
	 * @return
	 */
	public String putHashMap(String colname,Map<String,String> hash){
		return build.getJedisCluster().hmset(colname, hash);
	}

	/**
	 * 根据哈希表 key删除元素
	 *
	 * @param colname 集合名字
	 * @param keys 哈希表 key集合
	 * @return 0:代表删除失败
	 */
	public Long delByKeys(String colname, String ... keys){
		return build.getJedisCluster().hdel(colname, keys);
	}

	public long incrByKey(String colname, String key, int increment){
		return build.getJedisCluster().hincrBy(colname, key, increment);
	}
}
