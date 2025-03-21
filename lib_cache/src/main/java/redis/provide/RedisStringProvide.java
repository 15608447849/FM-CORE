package redis.provide;
import redis.config.JedisClusterBuild;

public class RedisStringProvide extends RedisProvide {

	public RedisStringProvide(JedisClusterBuild jedisClusterBuild) {
		super(jedisClusterBuild);
	}

	/**
	 * 根据键获取值
	 * 
	 * @param key 键
	 * @return
	 */
	public String get(String key){
		return build.getJedisCluster().get(key);
	}
	
	/**
	 * 将 key中储存的数字值增一
	 * 
	 * @param key
	 * @return
	 */
	public Long increase(String key) {
		return build.getJedisCluster().incr(key);
	}
	
	/**
	 * 将 key所储存的值加上给定的增量值
	 * 
	 * @param key
	 * @param increment 增量值
	 * @return
	 */
	public Long increase(String key, int increment) {
		return build.getJedisCluster().incrBy(key, increment);
	}
	
	
	/**
	 * 将 key中储存的数字值减一
	 * 
	 * @param key
	 * @return
	 */
	public Long decrease(String key) {
		return build.getJedisCluster().decr(key);
	}
	
	/**
	 * key所储存的值减去给定的减量值
	 * 
	 * @param key
	 * @param decrement 减量值
	 * @return
	 */
	public Long decrease(String key, int decrement) {
		return build.getJedisCluster().decrBy(key, decrement);
	}
	
	/**
	 * 返回 key所储存的字符串值的长度
	 * 
	 * @param key 键
	 * @return
	 */
	public Long length(String key){
	    return build.getJedisCluster().strlen(key);
	}
	
	/**
	 * 设置指定 key的值
	 * 
	 * @param key 键
	 * @param obj 元素值
	 * @return OK:代表设置成功
	 */
	public String set(String key, String obj) {
		build.getJedisCluster().del(key);
	    return build.getJedisCluster().set(key, obj);
	}

	/**
	 * 只有在 key 不存在时设置 key的值。
	 * 
	 * @param key 键
	 * @param obj 元素值
	 */
	public void setByKeyNotExist(String key, String obj) {
	    build.getJedisCluster().setnx(key, obj);
	}
	
	/**
	 * 直接在原来的值追加新的内容
	 * 
	 * @param key 键
	 * @param appendVal 追加的新值
	 */
	public void append(String key, String appendVal) {
	    build.getJedisCluster().append(key, appendVal);
	}
	
	
	/**
	 * 根据键删除值
	 * 
	 * @param key 键
	 * @return 0:代表删除失败
	 */
	public Long delete(String key) {
	    return build.getJedisCluster().del(key);
	}

}
