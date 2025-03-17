package redis.provide;

import redis.config.JedisClusterBuild;

import java.util.ArrayList;
import java.util.List;


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
		List<String> list = size(colname) < 10000 ? getAllElements(colname) : getAllElementsUsePage(colname);
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
	 * 获取所有的元素-分页
	 *
	 * @param colname 集合名
	 * @return
	 */
	public List<String> getAllElementsUsePage(String colname) {
		List<String> list = new ArrayList<>();
		long total = size(colname);
		long page_size = 10000;// 每页的数量
		long current_page = 0  ;// 当前页码
		while (current_page * page_size < total) {
			long start = current_page * page_size;
			long end = Math.min(start + page_size - 1, total - 1);
			// 使用LRANGE获取分页数据
			System.out.println(colname + " "+ start+" -- "+ end);
			List<String> part = build.getJedisCluster().lrange(colname, start, end);
			if (part!=null&&part.size()>0) list.addAll(part);

			current_page ++;
		}
		return list;
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
