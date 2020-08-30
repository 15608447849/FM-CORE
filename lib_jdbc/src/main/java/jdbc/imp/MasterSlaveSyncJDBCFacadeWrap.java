package jdbc.imp;

import bottle.util.GoogleGsonUtil;
import com.google.gson.Gson;
import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.JDBCSessionFacade;
import jdbc.define.option.JDBCSessionFacadeWrap;
import jdbc.define.sync.DefaultSyncImp;
import jdbc.define.sync.SyncI;
import jdbc.define.sync.SyncTask;

import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/18 1:15
 * 复制主从同步
 */
public class MasterSlaveSyncJDBCFacadeWrap extends JDBCSessionFacadeWrap{

    private final SyncI syncI = new DefaultSyncImp();

    public MasterSlaveSyncJDBCFacadeWrap(JDBCSessionFacade op) {
        super(op);
    }

    private void syncHandle(SyncTask task){
        boolean isSuccess = syncI.executeTask(op,task);
        if (!isSuccess){
            JDBCLogger.print("数据库同步失败,任务: "+ GoogleGsonUtil.javaBeanToJson(task));
        }
    }

    @Override
    public int execute(String sql, Object[] params) {
        syncHandle(SyncTask.Factory.create(sql,params,"execute"));
        return op.execute(sql,params);
    }

    @Override
    public int[] executeBatch(String sql, List<Object[]> paramList, int batchSize) {
        syncHandle( SyncTask.Factory.create(sql,paramList,batchSize,"executeBatch"));
        return op.executeBatch(sql,paramList,batchSize);
    }

    @Override
    public int executeTransaction(List<String> sqlList, List<Object[]> paramList,TransactionCallback callback) {
        syncHandle(SyncTask.Factory.create(sqlList,paramList,callback,"executeTransaction"));
        return op.executeTransaction(sqlList,paramList,callback);
    }

}
