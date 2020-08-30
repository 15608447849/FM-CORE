package jdbc.define.option;
import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.session.JDBCSessionManagerAbs;
import java.sql.*;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Statement.SUCCESS_NO_INFO;



/**
 * @Author: leeping
 * @Date: 2019/8/16 11:30
 */
public class JDBCSessionFacade extends SessionOption<JDBCSessionManagerAbs, Connection> {

    public JDBCSessionFacade(JDBCSessionManagerAbs manager) {
        super(manager);
    }

    /* 监测连接对象是否有效 */
    private long lastConnectionCheckTime = 0;

    private boolean lastConnected = true;

    /* 监测连接状态 */
    @Override
    public boolean checkDBConnectionValid() {

            if (lastConnected){
                if ((System.currentTimeMillis()-lastConnectionCheckTime) < 30*1000){
                    return true;
                }

                lastConnectionCheckTime = System.currentTimeMillis();
            }

            int result = 0;


            try {
                Connection conn = getSession();
                //等待用于验证连接的数据库操作完成的时间（秒）。如果超时时间在操作完成之前过期，则此方法返回false。值0表示数据库操作未应用超时
                boolean isOK = conn.isValid(300);
                if (isOK){
                    result = 1;
                }else{

                    try(PreparedStatement pst = JDBCUtils.prepareStatement(conn, "SELECT 1", false)){
                        try(ResultSet rs = pst.executeQuery()){
                            if (rs.next()){
                                result = rs.getInt(1);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                this.closeSession();
                JDBCLogger.error("【数据库错误】"+getManager()+"(SELECT 1)监测连接无效,关闭连接", e);
            }
            lastConnected = result==1;
            return lastConnected;
    }



//    //查询
//    @Override
//    public List<Object[]> query(String sql, Object[] params,Page page) {
//        sql = Page.exeDb(this,sql,params,page);
//        List<Object[]> result = new ArrayList<>();
//        ResultSet rs = null;
//        PreparedStatement pst = null;
//        filterParam(params);
//        try {
//            Connection conn = this.getSession();
//            pst = prepareStatement(conn, sql, false);
//
//            setParameters(pst, params);
//            rs = pst.executeQuery();
//            if (rs == null) throw new SQLException("'query' ResultSet is null");
//            int cols = rs.getMetaData().getColumnCount(); //行数
//            while(rs.next()) {
//                Object[] arrays = new Object[cols];
//                for(int i = 0; i < cols; i++) {
//                    arrays[i] = rs.getObject(i + 1);
//                }
//                result.add(arrays);
//            }
//        } catch (Exception e) {
//            result.clear();
//            if(e instanceof SQLException && e.getMessage().contains("operation not allowed after ResultSet closed")){
//                return query(sql,params,page);
//            }
//
//            JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数:\t"+param2String(params), e);
//
//        } finally {
//            closeSqlObject(rs,pst);
//        }
//        return result;
//    }

    //查询
    @Override
    public List<Object[]> query(String sql, Object[] params,Page page) {
        JDBCUtils.filterParam(params);
        sql = Page.exeDb(this,sql,params,page);
        List<Object[]> result = new ArrayList<>();

        Connection conn = getSession();
        try(PreparedStatement pst = JDBCUtils.prepareStatement(conn, sql, false)){
            JDBCUtils.setParameters(pst, params);
            try(ResultSet rs = pst.executeQuery()){
                int cols = rs.getMetaData().getColumnCount(); //行数
                while(rs.next()) {
                    Object[] arrays = new Object[cols];
                        for(int i = 0; i < cols; i++) {
                            arrays[i] = rs.getObject(i + 1);
                        }
                    result.add(arrays);
            }
            }
        }catch (Exception e){
            result.clear();
            JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数:\t"+JDBCUtils.param2String(params), e);
        }

        return result;
    }

    //对象映射查询
    /* @Override
    public <T> List<T> query(String sql, Object[] params, Class<T> beanClass,Page page) {

        sql = Page.exeDb(this,sql,params,page);
        List<T> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement pst = null;
        filterParam(params);
        try {
            Connection conn = this.getSession();
            pst = prepareStatement(conn, sql, false);
            setParameters(pst, params);
            rs = pst.executeQuery();
            if (rs == null) throw new SQLException("'query' ResultSet ISNULL");
            int cols = rs.getMetaData().getColumnCount(); //行数
            while(rs.next()) {
                try {
                    T bean = createObject(beanClass);
                    //获取本身和父级对象
                    for (Class clazz = bean.getClass() ; clazz != Object.class; clazz = clazz.getSuperclass()) {
                       classAssignment(clazz,bean,rs);
                    }
                    result.add(bean);
                } catch (Exception e) {
                    JDBCLogger.error("【数据反射赋值错误】", e);
                }
            }
        } catch (Exception e) {
            result.clear();
            if(e instanceof SQLException && e.getMessage().equals("operation not allowed after ResultSet closed")){
                return query(sql,params,beanClass,page);
            }
            JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数:\t"+param2String(params), e);

        } finally {
            closeSqlObject(rs,pst);
        }

        return result;
    }*/

    //对象映射查询
    @Override
    public <T> List<T> query(String sql, Object[] params, Class<T> beanClass,Page page) {
        JDBCUtils.filterParam(params);
        sql = Page.exeDb(this,sql,params,page);
        List<T> result = new ArrayList<>();
        Connection conn = this.getSession();
        try (PreparedStatement pst = JDBCUtils.prepareStatement(conn, sql, false)){
            JDBCUtils.setParameters(pst, params);
            try(ResultSet rs = pst.executeQuery();){
                while(rs.next()) {
                    try {
                        T bean = JDBCUtils.createObject(beanClass);
                        //获取本身和父级对象
                        for (Class clazz = bean.getClass() ; clazz != Object.class; clazz = clazz.getSuperclass()) {
                            JDBCUtils.classAssignment(clazz,bean,rs);
                        }
                        result.add(bean);
                    } catch (Exception e) {
                        JDBCLogger.error("【数据反射赋值错误】", e);
                    }
                }
            }
        } catch (Exception e) {
            result.clear();
            JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数:\t"+JDBCUtils.param2String(params), e);
        }
        return result;
    }

    @Override
    public List<Object[]> queryMany(List<String> sqlList, Object[] params, Page page) {
        List<Object[]> result = new ArrayList<>();
        if (sqlList==null || sqlList.size() == 0){
            return result;
        }
        for (String sql : sqlList){
            List<Object[]> tempResult = query(sql,params,null);
            if (tempResult!=null && !tempResult.isEmpty()){
                result.addAll(tempResult);
            }
        }

        return Page.exeMem(page,result);
    }

    @Override
    public <T> List<T> queryMany(List<String> sqlList, Object[] params, Class<T> beanClass, Page page) {
        List<T> result = new ArrayList<>();
        if (sqlList==null || sqlList.size() == 0){
            return result;
        }
        for (String sql : sqlList){
            List<T> tempResult = query(sql,params,beanClass,null);
            if (tempResult!=null && !tempResult.isEmpty()){
                result.addAll(tempResult);
            }
        }
        return Page.exeMem(page,result);
    }

    @Override
    public int execute(String sql, Object[] params) {
        JDBCUtils.filterParam(params);
        int result;
        Connection conn = getSession();
        try(PreparedStatement pst = JDBCUtils.prepareStatement(conn, sql, false)){
            JDBCUtils.setParameters(pst, params);
            result = pst.executeUpdate();
        } catch (Exception e) {
            result = -1;
            JDBCLogger.error(
                    "【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数:\t"+JDBCUtils.param2String(params),
                    e);
        }
        return result;
    }

    @Override
    public int[] executeBatch(String sql, List<Object[]> paramList,int batchSize) {
        JDBCUtils.filterParam(paramList);
        if (paramList==null || paramList.size() == 0) return new int[]{execute(sql,null)};
        if (batchSize == 0) batchSize = paramList.size(); // 避免产生- java.lang.ArithmeticException: / by zero
        if (batchSize<paramList.size()) batchSize = paramList.size();
        int index = 0;
        int[] effect = new int[paramList.size()];

        try{
            Connection conn = getSession();
            conn.setAutoCommit(false);
            try(PreparedStatement pst = JDBCUtils.prepareStatement(conn, sql, false)){

                int start = 0;
                while(index < paramList.size()) {
                    Object[] objects = paramList.get(index);
                    JDBCUtils.setParameters(pst, objects);
                    pst.addBatch();
                    index++;

                    if (index % batchSize == 0) {
                        int[] resArr = pst.executeBatch();
                        conn.commit();
                        System.arraycopy(resArr, 0, effect, start, index - start);
                        pst.clearBatch();
                        start = index;
                    }
                }

                if (index % batchSize != 0) {
                    int[] resArr = pst.executeBatch();
                    conn.commit();
                    System.arraycopy(resArr, 0, effect, start, index - start);
                }

                for (int i=0;i<effect.length;i++){
                    if (effect[i] == SUCCESS_NO_INFO){
                        effect[i] = 1;
                    }
                }

            }
            conn.setAutoCommit(true);
        }catch (SQLException e){
            effect = null;
            JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数("+index+"):\t"+JDBCUtils.param2String(paramList.get(index)), e);
        }
        return effect;
    }

    @Override
    public int executeTransaction(List<String> sqlList, List<Object[]> paramList,TransactionCallback callback) {
        JDBCUtils.filterParam(paramList);
        if (sqlList.size() != paramList.size()) throw new JDBCException("parameters do not match. If there is no value, use 'null' placeholder");
        JDBCSessionManagerAbs m = getManager();
        int res = 0;
        if (m.isTransactionInvoking()) throw new JDBCException("transaction in progress");

        m.setTransactionInvoking(true);
        try {
            m.beginTransaction();

            for (int i = 0;i<sqlList.size();i++){
                res = execute(sqlList.get(i),paramList.get(i));
                if (res < 0) throw new SQLException("transaction execution error,result code is "+res+" , sql: "+ sqlList.get(i)+" , param: "+ Arrays.toString(paramList.get(i)));

                if (callback!=null){
                    res = callback.callback(sqlList.get(i),paramList.get(i),res);
                }
                if (res == 0) throw new SQLException("unaffected rows , sql: "+ sqlList.get(i)+" , param: "+ Arrays.toString(paramList.get(i)));
            }
            m.commit();
        }catch (Exception e){
            try { m.rollback(); } catch (Exception ignored) { }
            res = -1;
            JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sqlList+"\n参数:\t"+JDBCUtils.param2String(paramList), e);
        }
        m.setTransactionInvoking(false);
        return res;
    }

   /* 执行存储过程 并获取结果 */
    @Override
    public List<Object[]> call(String callSql, Object[] inParam,int outParamStartPos, int[] outParamTypes) {
        JDBCUtils.filterParam(inParam);

        if (outParamStartPos<=0) outParamStartPos = 1;

        if (outParamTypes == null)  outParamTypes = new int[Types.NULL];

        List<Object[]> result = new ArrayList<>();

        Connection conn = getSession();

        try (  CallableStatement cst = JDBCUtils.prepareStatement(conn, callSql, true) ){
            int inputParameterStartPosition = outParamStartPos == 1 ? outParamStartPos + outParamTypes.length : 1;
            JDBCUtils.setInputParameters(cst, inputParameterStartPosition, inParam);
            JDBCUtils.registerOutputParameters(cst, outParamStartPos, outParamTypes);
            boolean flag = cst.execute();
            try(ResultSet rs = cst.getResultSet()){
                if (flag && rs!=null){
                    int cols = rs.getMetaData().getColumnCount(); //行数
                    while(rs.next()) {
                        Object[] arrays = new Object[cols];
                        for(int i = 0; i < cols; i++) {
                            arrays[i] = rs.getObject(i + 1);
                        }
                        result.add(arrays);
                    }
                }
            }
        } catch (Exception e) {
            result.clear();
            JDBCLogger.error(
                    "【数据库错误】"+getManager()+"\nSQL:\t"+callSql+"\n参数:\t"+JDBCUtils.param2String(inParam), e);
        }
        return result;
    }

}
