package jdbc.define.sync;

import jdbc.define.option.JDBCSessionFacade;
import jdbc.imp.TomcatJDBCPool;

import java.io.Closeable;

/**
 * @Author: leeping
 * @Date: 2019/8/17 20:38
 * 数据库同步接口
 */
public interface SyncI {
    boolean executeTask(JDBCSessionFacade facade, SyncTask task);
}
