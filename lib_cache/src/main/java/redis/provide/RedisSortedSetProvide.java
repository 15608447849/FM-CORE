package redis.provide;

import redis.config.JedisClusterBuild;

import java.util.Set;


public class RedisSortedSetProvide extends RedisProvide {

	public RedisSortedSetProvide(JedisClusterBuild jedisClusterBuild) {
		super(jedisClusterBuild);
	}

	/**
	 * 添加元素
	 * 
	 * @param colname 集合名
	 * @param score 分数(元素权重)
	 * @param val 元素值
	 * @return 0:添加失败 1:添加成功
	 */
	public Long addElement(String colname, double score, String val) {
		return build.getJedisCluster().zadd(colname, score, val);
	}
	
	/**
	 * 根据元素值删除元素
	 * 
	 * @param colname 集合名
	 * @param val 元素值
	 * @return 0:代表删除失败
	 */
	public Long delElement(String colname, String val) {
		return build.getJedisCluster().zrem(colname, val);
	}
	
	/**
	 * 获取集合所有元素的个数
	 * 
	 * @param colname 集合名
	 * @return 个数
	 */
	public Long size(String colname) {
		return build.getJedisCluster().zcard(colname);
	}
	
	/**
	 * 在分数区间内统计元素的个数
	 *  
	 * @param colname 集合名
	 * @param minscore 最小分数(元素权重)
	 * @param maxscore 最大分数(元素权重)
	 * @return 个数
	 */
	public Long countByScoreRange(String colname,double minscore, double maxscore) {
		return build.getJedisCluster().zcount(colname, minscore, maxscore);
	}
	
	/**
	 * 根据元素值获取元素的分数值(元素权重)
	 * 
	 * @param colname 集合名
	 * @return 个数
	 */
	public Double getScoreByVal(String colname, String val) {
		return build.getJedisCluster().zscore(colname, val);
	}
	
	/**
	 * 获取所有元素
	 * 
	 * @param colname 集合名
	 * @return
	 */
	public Set<String> getAllElements(String colname) {
		return build.getJedisCluster().zrange(colname, 0, -1);
	}
	
}
