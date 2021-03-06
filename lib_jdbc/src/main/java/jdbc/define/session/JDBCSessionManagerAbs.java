package jdbc.define.session;

import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.DataBaseType;
import jdbc.define.sync.SyncI;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author: leeping
 * @Date: 2019/8/16 9:30
 */
public abstract class JDBCSessionManagerAbs extends SessionManagerAbs<Connection> {

    protected DataBaseType dataBaseType;
    protected String address;
    protected String dataBaseName;
    protected Integer seq = -1;

    protected String identity = null;

    public String getAddress() {
        return address;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public String getIdentity(){
        return identity;
    }

    public Integer getSeq() {
        return seq;
    }

    /* 获取所有数据库类型 */
    public DataBaseType getDataBaseType(){
        return dataBaseType;
    }

    @Override
    public void beginTransaction() {
        Connection session = getSession();
        try {
            session.setAutoCommit(false);
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    @Override
    public void commit() {
        Connection session = getSession();
        try {
            session.commit();
        } catch (SQLException e) {
            throw new JDBCException(e);
        } finally {
            try {
                session.setAutoCommit(true);
            } catch (SQLException ignored) { }
        }
    }

    @Override
    public void rollback() {
            Connection session = getSession();
            try {
                session.rollback();
            } catch (SQLException e) {
                throw new JDBCException(e);
            } finally {
                try {
                    session.setAutoCommit(true);
                } catch (SQLException ignored) { }
            }

    }


    @Override
    public Connection getSession() {
        Connection session = super.getSession();
        try {
//            JDBCLogger.print(Thread.currentThread().getName() +" < ThreadLocal获取 > "+session);
            if (session == null || session.isClosed()) {
                session = getInternalConnection();
                if (session == null || session.isClosed() ) throw new SQLException("db connection non-existent or closed");
//                JDBCLogger.print(Thread.currentThread().getName() +" < 创建 > "+session);
                setSession(session);
//                JDBCLogger.print(Thread.currentThread().getName() +" < 连接池获取 > "+session);
            }


            return session;
        } catch (SQLException e) {
            closeSession();
            throw new JDBCException(e);
        }
    }

    protected abstract Connection getInternalConnection() throws SQLException;

    @Override
    public void closeSession() {
        Connection session = super.getSession();
        if (session != null) {
            try {
                if (!session.getAutoCommit()){
                    //不允许返回连接
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            setSession(null);
            try {
//                JDBCLogger.print(Thread.currentThread().getName() +" < 关闭 > "+session);
                session.close();// 返还连接

            } catch (SQLException e) {
                throw new JDBCException(e);
            }
        }
    }

    @Override
    public void unInitialize() {
        try {
            super.unInitialize();
            closeSessionAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "「"+address+" "+dataBaseName+","+seq+","+identity+"」";
    }
}
