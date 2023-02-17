package jdbc.define.option;

import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/17 15:56
 * 数据库操作API
 */
public interface DaoApi {

    /** 查询数据 */
    List<Object[]> query(String sql,Object[] params,Page page);

    /** 查询数据 对象 */
    <T> List<T> query(String sql, Object[] params,Class<T> beanClass,Page page);

    /** 多SQL 查询数据 */
    List<Object[]> queryMany(List<String> sqlList,Object[] params,Page page);

    /** 多SQL 查询数据 对象 */
    <T> List<T> queryMany(List<String> sqlList, Object[] params,Class<T> beanClass,Page page);

    /** 新增,修改,删除  */
    int execute(String sql, Object[] params);

    /**  新增,修改,删除 批量执行 */
    int[] executeBatch(String sql,List<Object[]> paramList,int batchSize);

    /** 新增,修改,删除 事务执行 */
    int executeTransaction(List<String> sqlList,List<Object[]> paramList,boolean ignoreUnaffectedRows);

    /** 执行存储过程
     * outParamTypes : java.sql.Types
     * */
    List<Object[]> call(String callSql, Object[] inParam,int outParamStartPos, int[] outParamTypes);

}
