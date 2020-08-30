package jdbc.define.sync;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.DaoApi;
import jdbc.define.option.DataBaseType;
import jdbc.define.option.JDBCSessionFacade;
import jdbc.define.tuples.Tuple2;
import jdbc.imp.TomcatJDBC;
import jdbc.imp.TomcatJDBCPool;
import java.util.*;

/**
 * @Author: leeping
 * @Date: 2019/8/17 21:47
 */
public class DefaultSyncImp implements SyncI {

    /* 执行同步 */
    private Tuple2<Boolean,String> executeSql(SyncTask task, JDBCSessionFacade dao) {

        boolean result = false;
        String error = "";
        try {
                if (!dao.checkDBConnectionValid()){
                   throw new IllegalStateException("数据库:"+ dao.getManager() +" 连接失败!");
                }

                List<String> sqlList = task.getSqlList();
                List<Object[]> paramList  = task.getParamList();
                String methodFlag = task.getMethodFlag();
                if (methodFlag == null) throw new IllegalAccessException("同步任务方法标识不存在");

                if(methodFlag.equals("execute")){
                    Object[] params = paramList!=null ? paramList.get(0) : null;
                    int i = dao.execute(sqlList.get(0),params);
                    error+="执行结果="+i;
                    if (i >= 0 ) result = true;
                    JDBCLogger.write("【执行同步】 execute 结果 = "+i + task);
                }

                if (methodFlag.equals("executeBatch")){
                    int[] i = dao.executeBatch(sqlList.get(0),paramList, task.getBatchSize());
                    error+="执行结果="+Arrays.toString(i);
                    if (i != null) result = true;
                    JDBCLogger.write("【执行同步】 executeBatch  结果 = "+ (i==null?"null":Arrays.toString(i)) + task );
                }

                if (methodFlag.equals("executeTransaction")){
                    DaoApi.TransactionCallback transactionCallback = null;
                    if (task.getTransactionCallbackClassPath()!=null && task.getTransactionCallbackClassPath().length()>0){
                        Class<?> cls = Class.forName(task.getTransactionCallbackClassPath());
                        transactionCallback = (DaoApi.TransactionCallback) cls.newInstance();
                    }
                    int i = dao.executeTransaction(sqlList,paramList,transactionCallback);
                    error+="执行结果="+i +" ,transactionCallback = " + transactionCallback;
                    if (i >= 0 ) result = true;
                    JDBCLogger.write("【执行同步】 executeTransaction  结果 = "+i + task);
                }

        } catch (Exception e) {
            JDBCLogger.error("【执行同步错误】",e);
            error = e.getMessage();
        }

        return new Tuple2<>(result,error);
    }

    private void executeTask(TomcatJDBCPool selectPool, SyncTask task, int index) {
        JDBCLogger.write("【准备同步】目标数据库: " + selectPool + " 任务: " + task +" 执行下标: "+ index);

        Tuple2<Boolean, String> result = executeSql(task, new JDBCSessionFacade(selectPool));

        boolean isSuccess = result.getValue0();
        String failCause = result.getValue1();

        if (isSuccess) {
            task.setState(task.getState()+1);//执行成功次数
            //记录成功的db标识
            task.getSuccessDbList().add(selectPool.getIdentity());
        } else {
            //记录失败原因
            task.setFailCause(task.getFailCause()+",index="+index+">>"+ failCause);
            JDBCLogger.print("【同步失败】目标数据库: " + selectPool  +" 任务: " + task);
        }

    }

    @Override
    public boolean executeTask(JDBCSessionFacade facade, SyncTask task) {
        try {

            if (task.getState() != 0) return true;

            TomcatJDBCPool currentPool = (TomcatJDBCPool)facade.getManager();
            //获取关联的同步数据库
            List<TomcatJDBCPool> list = TomcatJDBC.getSpecDataBasePoolList(DataBaseType.mysql,currentPool.getDataBaseName());

            if (list.size() == 1) {
                JDBCLogger.write("【同步数据库】" + currentPool.getDataBaseName() + " , 未设置备份库)");
                return true;
            }

            int index = task.getState();
            outer:
            for (TomcatJDBCPool selectPool : list) {
                if (selectPool.getIdentity().equals(currentPool.getIdentity())) {
                    //排除当前数据库
                    continue;
                }
                for (String identity : task.getSuccessDbList()) {
                    //排除已同步成功的数据库
                    if (selectPool.getIdentity().equals(identity)) {
                        continue outer;
                    }
                }
                executeTask(selectPool,task,index++);
            }

            if (task.getState() == index) {
              return true;
            }
        }catch (Exception e){
            JDBCLogger.error("同步数据库,执行错误,任务: "+task,e);
        }
        return false;
    }
}
