package jdbc.define.sync;

import com.google.gson.*;
import jdbc.define.option.DaoApi;
import jdbc.imp.GenUniqueID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/17 20:47
 */
public final class SyncTask {
    private static GenUniqueID genUniqueID = new GenUniqueID(true);
    //标识-唯一
    private long id;
    //执行的sql集合
    private List<String> sqlList;
    //执行的参数集合
    private List<Object[]> paramList;
    //批量更新的批量大小
    private int batchSize = 0;
    //方法标识
    private String methodFlag;
    //当前状态
    private int state;
    //成功的数据库列表
    private List<String> successDbList;
    //事务执行是否忽略未影响的行
    private boolean ignoreUnaffectedRows;

    private String failCause;

    private SyncTask(){
        id = genUniqueID.currentTimestampLong();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
    }

    public List<Object[]> getParamList() {
        return paramList;
    }

    public void setParamList(List<Object[]> paramList) {
        this.paramList = paramList;
    }

    public String getMethodFlag() {
        return methodFlag;
    }

    public void setMethodFlag(String methodFlag) {
        this.methodFlag = methodFlag;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<String> getSuccessDbList() {
        if (successDbList == null) successDbList = new ArrayList<>();
        return successDbList;
    }

    public void setSuccessDbList(List<String> successDbList) {
        this.successDbList = successDbList;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public boolean isIgnoreUnaffectedRows() {
        return ignoreUnaffectedRows;
    }

    public void setIgnoreUnaffectedRows(boolean ignoreUnaffectedRows) {
        this.ignoreUnaffectedRows = ignoreUnaffectedRows;
    }

    public String getFailCause() {
        return failCause;
    }

    public void setFailCause(String failCause) {
        this.failCause = failCause;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder().append("\tID= ").append(id).append(",METHOD= ").append(methodFlag);
        if (sqlList != null) {
            for (int i = 0;i<sqlList.size();i++){
                s.append("\n").append("SQL-"+i+"\t").append(sqlList.get(i));
            }
        }
        if (paramList!=null){
            for (int i = 0;i<paramList.size();i++){
                s.append("\n").append("PARAM-"+i+"\t").append(Arrays.toString(paramList.get(i)));
            }
        }
        return s.toString();
    }


    public static class Factory{
        public static SyncTask create(String sql, Object[] params, String methodFlag) {
            SyncTask task = new SyncTask();
            task.sqlList = new ArrayList<>();
            task.sqlList.add(sql);
            task.paramList = new ArrayList<>();
            task.paramList.add( params);
            task.methodFlag = methodFlag;
            return task;
        }

        public static SyncTask create(String sql, List<Object[]> paramList,int batchSize, String methodFlag) {
            SyncTask task = new SyncTask();
            task.sqlList = new ArrayList<>();
            task.sqlList.add(sql);
            task.paramList = paramList;
            task.batchSize = batchSize;
            task.methodFlag = methodFlag;
            return task;
        }


        public static SyncTask create(List<String> sqlList, List<Object[]> paramList, boolean ignoreUnaffectedRows, String methodFlag) {
            SyncTask task = new SyncTask();
            task.sqlList = sqlList;
            task.paramList = paramList;
            task.methodFlag = methodFlag;
            ignoreUnaffectedRows = ignoreUnaffectedRows;
            return task;
        }

        public static SyncTask create(long id,List<String> sqlList,List<Object[]> paramList,int batchSize,boolean ignoreUnaffectedRows,String methodFlag,int state,List<String> successDbList) {
            SyncTask task = new SyncTask();
            task.id = id;
            task.sqlList = sqlList;
            task.paramList = paramList;
            task.batchSize = batchSize;
            task.ignoreUnaffectedRows = ignoreUnaffectedRows;
            task.methodFlag = methodFlag;
            task.state = state;
            task.successDbList = successDbList;
            return task;
        }
    }
}
