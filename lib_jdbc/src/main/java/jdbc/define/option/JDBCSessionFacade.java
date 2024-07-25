package jdbc.define.option;
import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.session.JDBCSessionManagerAbs;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.sql.Statement.SUCCESS_NO_INFO;



/**
 * @Author: leeping
 * @Date: 2019/8/16 11:30
 */
public class JDBCSessionFacade extends SessionOption<JDBCSessionManagerAbs, Connection> {

    public JDBCSessionFacade(JDBCSessionManagerAbs manager) {
        super(manager);
    }

    /* 监测连接状态 */
    @Override
    public boolean checkDBConnectionValid() {
            int result = 0;
            try {
                Connection conn = getSession();
                //等待用于验证连接的数据库操作完成的时间（秒）。如果超时时间在操作完成之前过期，则此方法返回false。值0表示数据库操作未应用超时
                boolean isOK = conn.isValid(300);
                if (isOK){
                    result = 1;
                }else{
                    try(PreparedStatement pst = JDBCUtils.prepareStatement(conn, "SELECT 1", false)){
                        if (pst!=null){
                            //可以执行select也可以执行DML（增删改）或DDL, 如果执行的select的sql语句，返回的是true, 如果执行的是DML或DDL，返回的是false
                            //根据返回的布尔值，调用不同的方法来获得结果，如果是true，调用getResultSet来获得查询得到的结果集，如果是false,调用getUpdateCount()获得受影响的行数
                           if ( pst.execute()){
                               try(ResultSet rs = pst.getResultSet()){
                                   if (rs!=null){
                                       if (rs.next()){
                                           result = rs.getInt(1);
                                       }
                                   }
                               }
                           }
                        }
                    }
                }
            } catch (Exception e) {
                JDBCLogger.error("【数据库错误】"+getManager()+"(SELECT 1) 监测连接无效,关闭连接", e);
            }finally {
                closeSession();
            }
            return result==1;
    }

    //查询
    @Override
    public List<Object[]> query(String sql, Object[] params,Page page) {
        List<Object[]> result = new ArrayList<>();
        // 限制语句类型
        if (!JDBCUtils.isSelectStatement(sql)) {
            JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数:\t"+JDBCUtils.param2String(params), new JDBCException("sql not query statement"));
            return result;
        }
        JDBCUtils.filterParam(params);
        sql = Page.executeDatabasePaging(this,sql,params,page);
        long conn_time = 0,exe_time = 0,read_time = 0 ;
        if (Page.checkQuery(page)){
            long t1 = System.currentTimeMillis();
            Connection conn = getSession();
            long t2 = System.currentTimeMillis();
            conn_time = t2 - t1;
            try(PreparedStatement pst = JDBCUtils.prepareStatement(conn, sql, false)){
                if (pst!=null){
                    JDBCUtils.setParameters(pst, params);
                    long t3 = System.currentTimeMillis();
                    if (pst.execute()){
                        long t4 = System.currentTimeMillis();
                        exe_time = t4 - t3;//执行用时
                        try(ResultSet rs = pst.getResultSet()){
                            if (rs!=null){
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
                        long t5 = System.currentTimeMillis();
                        read_time = t5 - t4;//读取用时
                    }
                }
              JDBCLogger.writeSlowQuery(sql,conn_time,exe_time,read_time);
            }catch (Exception e){
                result.clear();
                JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数:\t"+JDBCUtils.param2String(params), e);
            }finally {
                closeSession();
            }
        }
        return result;
    }

    //对象映射查询
    @Override
    public <T> List<T> query(String sql, Object[] params, Class<T> beanClass,Page page) {
        List<T> result = new ArrayList<>();
        // 限制语句类型
        if (!JDBCUtils.isSelectStatement(sql)) {
            JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数:\t"+JDBCUtils.param2String(params), new JDBCException("sql not query statement"));
            return result;
        }
        JDBCUtils.filterParam(params);
        sql = Page.executeDatabasePaging(this,sql,params,page);
        long conn_time = 0,exe_time = 0,read_time = 0 ;
        if (Page.checkQuery(page)){
            long t1 = System.currentTimeMillis();
            Connection conn = this.getSession();
            long t2 = System.currentTimeMillis();
            conn_time = t2 - t1;
            try (PreparedStatement pst = JDBCUtils.prepareStatement(conn, sql, false)){
                if (pst!=null){
                    JDBCUtils.setParameters(pst, params);
                    long t3 = System.currentTimeMillis();
                    if (pst.execute()){
                        long t4 = System.currentTimeMillis();
                        exe_time = t4 - t3;//执行用时
                        try(ResultSet rs = pst.getResultSet()){
                            if (rs!=null){
                                while(rs.next()) {
                                    try {
                                        T bean = JDBCUtils.createObject(beanClass);
                                        //获取本身和父级对象
                                        for (Class<?> clazz = bean.getClass() ; clazz != Object.class; clazz = clazz.getSuperclass()) {
                                            JDBCUtils.classAssignment(clazz,bean,rs);
                                        }
                                        result.add(bean);
                                    } catch (Exception e) {
                                        JDBCLogger.error("【数据反射赋值错误】", e);
                                    }
                                }
                            }
                        }
                        long t5 = System.currentTimeMillis();
                        read_time = t5 - t4;//读取用时
                    }
                }
                JDBCLogger.writeSlowQuery(sql,conn_time,exe_time,read_time);
            } catch (Exception e) {
                result.clear();
                JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数:\t"+JDBCUtils.param2String(params), e);
            }finally {
                closeSession();
            }
        }

        return result;
    }

    @Override
    public List<Object[]> queryMany(List<String> sqlList, Object[] params, Page page) {
        List<Object[]> result = new ArrayList<>();
        if (sqlList==null || sqlList.size() == 0) return result;
        for (String sql : sqlList){
            List<Object[]> tempResult = query(sql,params,null);
            if (tempResult!=null && !tempResult.isEmpty()){
                result.addAll(tempResult);
            }
        }
        return Page.executeMemoryPaging(page,result);
    }

    @Override
    public <T> List<T> queryMany(List<String> sqlList, Object[] params, Class<T> beanClass, Page page) {
        List<T> result = new ArrayList<>();
        if (sqlList==null || sqlList.size() == 0) return result;
        for (String sql : sqlList){
            List<T> tempResult = query(sql,params,beanClass,null);
            if (tempResult!=null && !tempResult.isEmpty()){
                result.addAll(tempResult);
            }
        }
        return Page.executeMemoryPaging(page,result);
    }

    @Override
    public int execute(String sql, Object[] params) {
        JDBCUtils.filterParam(params);
        int affectedRows = -1;
        Connection conn = getSession();
        try(PreparedStatement pst = JDBCUtils.prepareStatement(conn, sql, false)){
            JDBCUtils.setParameters(pst, params);
            if (!pst.execute()){
                affectedRows = pst.getUpdateCount();
            }
        } catch (Exception e) {
            affectedRows = -1;
            JDBCLogger.error(
                    "【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数:\t"+JDBCUtils.param2String(params),
                    e);
        }finally {
            closeSession();
        }
        return affectedRows;
    }

    @Override
    public int[] executeBatch(String sql, List<Object[]> paramList,int batchSize) {
        JDBCUtils.filterParam(paramList);
        if (paramList==null || paramList.size() == 0) return new int[]{execute(sql,null)};
        if (batchSize == 0) batchSize = paramList.size(); // 避免产生- java.lang.ArithmeticException: / by zero

        if (batchSize>paramList.size()) batchSize = paramList.size();

        int index = 0;
        int[] effect = new int[paramList.size()];

        Connection conn = null;
        try{
            conn = getSession();

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

                // 剩余部分
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

        }catch (Exception e){
            if (e instanceof SQLException){
                SQLException _e = (SQLException) e;
                if (e instanceof ru.yandex.clickhouse.except.ClickHouseUnknownException){
                    if (_e.getErrorCode() == 1002){
                        return executeBatch(sql,paramList,batchSize);
                    }
                }
            }

            effect = null;
            JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sql+"\n参数集合\n"+JDBCUtils.param2String(paramList), e);
        }finally {
            if (conn!=null){
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    JDBCLogger.error("批量执行,关闭自动提交失败",e);
                }
            }
            closeSession();
        }
        return effect;
    }

    @Override
    public int executeTransaction(List<String> sqlList, List<Object[]> paramList,boolean ignoreUnaffectedRows) {
        JDBCUtils.filterParam(paramList);
        if (sqlList.size() != paramList.size()) throw new JDBCException("parameters do not match. If there is no value, use 'null' placeholder");
        JDBCSessionManagerAbs m = getManager();
        int res;
        try {
            m.beginTransaction();
            for (int i = 0;i<sqlList.size();i++){
                String sql = sqlList.get(i);
                Object[] params = paramList.get(i);
                int result = execute(sql,params);

                if (result < 0) throw new SQLException("事务: SQL执行错误\n" + sql + "\n" +Arrays.toString(params));

                if (result == 0 && !ignoreUnaffectedRows) {
                    throw new SQLException("事务: 没有受影响的行\n"+ sql+"\n"+ Arrays.toString(params));
                }
            }
            m.commit();
            res = 1;
        }catch (Exception e){
            try { m.rollback(); } catch (Exception _e) {
                JDBCLogger.error("事务回滚错误",_e);
            }
            res = -1;
            JDBCLogger.error("【数据库错误】"+getManager()+"\nSQL:\t"+sqlList+"\n参数:\t"+JDBCUtils.param2String(paramList), e);
        }finally {
            closeSession();
        }
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
        }finally {
            closeSession();
        }
        return result;
    }

}
