package jdbc.imp;

import jdbc.define.option.JDBCSessionFacade;
import jdbc.define.option.JDBCSessionFacadeWrap;
import jdbc.define.sync.SyncTask;
import java.util.List;
import static jdbc.define.sync.SyncFactory.syncHandle;

/**
 * @Author: leeping
 * @Date: 2019/8/18 1:15
 * 复制主从同步
 */
public class MasterSlaveSyncJDBCFacadeWrap extends JDBCSessionFacadeWrap{

    MasterSlaveSyncJDBCFacadeWrap(JDBCSessionFacade op) {
        super(op);
    }

    @Override
    public int execute(String sql, Object[] params) {
        syncHandle(op,SyncTask.Factory.create(sql,params,"execute"));
        return op.execute(sql,params);
    }

    @Override
    public int[] executeBatch(String sql, List<Object[]> paramList, int batchSize) {
        syncHandle( op,SyncTask.Factory.create(sql,paramList,batchSize,"executeBatch"));
        return op.executeBatch(sql,paramList,batchSize);
    }

    @Override
    public int executeTransaction(List<String> sqlList, List<Object[]> paramList,TransactionCallback callback) {
        syncHandle( op,SyncTask.Factory.create(sqlList,paramList,callback,"executeTransaction"));
        return op.executeTransaction(sqlList,paramList,callback);
    }

}
