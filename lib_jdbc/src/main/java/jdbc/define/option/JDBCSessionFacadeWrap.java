package jdbc.define.option;

import jdbc.define.exception.JDBCException;

import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/18 10:56
 */
public class JDBCSessionFacadeWrap implements DaoApi{

    protected JDBCSessionFacade op;

    public JDBCSessionFacadeWrap(JDBCSessionFacade op) {
        if (op == null) throw new JDBCException("无效的数据库连接操作对象");
        this.op = op;
    }

    @Override
    public List<Object[]> query(String sql, Object[] params,Page page) {
        return op.query(sql,params,page);
    }


    @Override
    public <T> List<T> query(String sql, Object[] params, Class<T> beanClass,Page page) {
        return op.query(sql,params,beanClass,page);
    }

    @Override
    public List<Object[]> queryMany(List<String> sqlList, Object[] params, Page page) {
        return op.queryMany(sqlList,params,page);
    }

    @Override
    public <T> List<T> queryMany(List<String> sqlList, Object[] params, Class<T> beanClass, Page page) {
        return op.queryMany(sqlList,params,beanClass,page);
    }

    @Override
    public int execute(String sql, Object[] params) {
        return op.execute(sql,params);
    }

    @Override
    public int[] executeBatch(String sql, List<Object[]> paramList,int batchSize) {
        return op.executeBatch(sql,paramList,batchSize);
    }

    @Override
    public int executeTransaction(List<String> sqlList, List<Object[]> paramList,boolean ignoreUnaffectedRows) {
        return op.executeTransaction(sqlList,paramList,ignoreUnaffectedRows);
    }

    @Override
    public List<Object[]> call(String callSql, Object[] inParam, int outParamStartPos, int[] outParamTypes) {
        return op.call(callSql,inParam,outParamStartPos,outParamTypes);
    }
}
