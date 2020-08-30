package redis.provide;

import redis.config.JedisClusterBuild;

import java.util.List;

/**
 * Redis列表是简单的字符串列表，按照插入顺序排序。你可以添加一个元素到列表的头部（左边）或者尾部（右边）一个列表最多可以包含 4294967295 个元素 (每个列表超过40亿个元素)。

 * @author JiangWenGuang
 * @since 2018-06-14
 * @version 1.0
 * 
 * @param <T>
 */
public class RedisListProvide extends RedisProvide {
	
	public RedisListProvide(JedisClusterBuild jedisClusterBuild) {
		super(jedisClusterBuild);
	}

	/**
	 * 添加列表头部元素
	 * 
	 * @param colname 集合名
	 * @param val 元素值
	 * @return 0:添加失败 1:添加成功
	 */
	public Long addHeadElement(String colname,String val) {
		return build.getJedisCluster().lpush(colname, val.toString());
	}
	
	/**
	 * 添加列表尾部元素
	 * 
	 * @param colname 集合名
	 * @param val 元素值
	 * @return 0:添加失败 1:添加成功
	 */
	public Long addEndElement(String colname,String val) {
		return build.getJedisCluster().rpush(colname, val);
	}
	
	/**
	 * 删除头部元素
	 *  
	 * @param colname 集合名
	 * @return 移除的元素值
	 */
	public String removeHeadElement(String colname) {
		return build.getJedisCluster().lpop(colname);
	}
	
	/**
	 * 删除尾部元素
	 *  
	 * @param colname 集合名
	 * @return 移除的元素值
	 */
	public String removeEndElement(String colname) {
		return build.getJedisCluster().rpop(colname);
	}
	
	/**
	 * 根据索引更新集合中元素值
	 * 
	 * @param colname 集合名
	 * @param index 索引值
	 * @param val 更新后的元素值
	 */
	public String updateElementByIndex(String colname,int index,String val) {
		return build.getJedisCluster().lset(colname, index, val);
	}

	/**
	 * 获取索引位置的元素
	 */
		public String getElementByIndex(String colname,long index){
		if (index<size(colname)){
			return build.getJedisCluster().lindex(colname,index);
		}
		return null;
	}

	/**
	 * 根据元素值删除集合中包含该元素值所有元素
	 * 
	 * @param colname 集合名
	 * @param val 元素值
	 * @return 0:代表删除失败
	 */
	public Long deleteElementByVal(String colname, String val) {
		List<String> list = getAllElements(colname);
		int num = 0;
		if(list != null && list.size() > 0) {
			for(String v : list) {
				if(v.equals(val)) {
					num++;
				}
			}
		}
		return build.getJedisCluster().lrem(colname, num, val);
	}
	
	/**
	 * 根据元素值和删除个数删除集合中包含该元素值元素
	 * 
	 * @param colname 集合名
	 * @param delNum 删除的个数(有重复时)
	 * @param val 元素值
	 * @return 0:代表删除失败
	 */
	public Long deleteElementByNumAndVal(String colname,int delNum, String val) {
		return build.getJedisCluster().lrem(colname, delNum, val);
	}
	
	/**
	 * 获取所有的元素
	 * 
	 * @param colname 集合名
	 * @return
	 */
	public List<String> getAllElements(String colname) {
		return build.getJedisCluster().lrange(colname, 0, -1);
	}


	/**
	 * 获取列表的长度
	 * 
	 * @param colname 集合名
	 * @return
	 */
	public Long size(String colname) {
		return build.getJedisCluster().llen(colname);
	}

 }
