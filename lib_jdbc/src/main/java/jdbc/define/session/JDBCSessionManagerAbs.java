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

    private TransactionIsolationLevel currentTransIsoLevel;

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
    public void loadDefaultTransactionIsolationLevel() {
        try {
            Connection session = getSession();
            int level = session.getTransactionIsolation();
            this.currentTransIsoLevel = TransactionIsolationLevel.fromInt(level);
        } catch (SQLException e) {
            throw new JDBCException(e);
        } finally {
            this.closeSession();
        }
    }

    @Override
    public TransactionIsolationLevel getCurrentTransactionIsolationLevel() {
        return this.currentTransIsoLevel;
    }

    @Override
    public void setSessionTransactionIsolationLevel(TransactionIsolationLevel transIsoLevel) {
        try {
            Connection session = getSession();
            session.setTransactionIsolation(transIsoLevel.toInt());
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
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
            } catch (SQLException e) {
                JDBCLogger.error(getClass().getSimpleName()+" - commit",e);

            }
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
            } catch (SQLException e) {
                JDBCLogger.error(getClass().getSimpleName()+" - rollback",e);
            }
        }
    }

    @Override
    public Connection getSession() {
        Connection session = super.getSession();
        try {
            if (session == null || session.isClosed()) {

                session = getInternalConnection();
                if (session == null || session.isClosed() ) throw new SQLException("db connection non-existent or closed");
                setSession(session);
//                JDBCLogger.print(Thread.currentThread().getName() +" < 创建 > "+session);
            }
//            JDBCLogger.print(Thread.currentThread().getName() +" < 获取 > "+session);
            return session;
        } catch (SQLException e) {
            setSession(null);
            throw new JDBCException(e);
        }
    }

    protected abstract Connection getInternalConnection() throws SQLException;

    @Override
    public void closeSession() {
        Connection session = super.getSession();
        setSession(null);
        if (session != null) {
            try {
                session.close();
//                JDBCLogger.print(Thread.currentThread().getName() +" < 关闭 > "+session);
            } catch (SQLException e) {
                throw new JDBCException(e);
            }
        }
    }

    @Override
    public void unInitialize() {
        closeSession();
        super.unInitialize();
    }





    @Override
    public String toString() {
        return "「"+address+" "+dataBaseName+","+seq+","+identity+"」";
    }
}
