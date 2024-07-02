package jdbc.define.session;

import jdbc.define.exception.JDBCException;
import jdbc.define.option.DataBaseType;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author: leeping
 * @Date: 2019/8/16 9:30
 */
public abstract class JDBCSessionManagerAbs extends SessionManagerAbs<Connection> {

    private DataBaseType dataBaseType;
    private String host;
    private int port;
    private String username;
    private String password;
    private String dataBaseName;

   protected void setDataBaseInfo(DataBaseType dataBaseType,String host,int port,String userName,String password,String dataBaseName){
       this.dataBaseType = dataBaseType;
       this.host = host;
       this.port = port;
       this.username = userName;
       this.password = password;
       this.dataBaseName = dataBaseName;
   }

    public DataBaseType getDataBaseType(){
        return dataBaseType;
    }


    public String getHost() {
        return host;
    }

    public int getPort() {return port;}

    public String getUsername() {return username;}

    public String getPassword() {return password;}

    public String getDataBaseName() {
        return dataBaseName;
    }


    @Override
    public void beginTransaction() {
        Connection session = getSession();
        try {
            if (!session.getAutoCommit()) throw new JDBCException("session auto commit is false,unable open transaction");
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
        return "「" + dataBaseType+","+ host +":"+port+","+username+","+dataBaseName+"」";
    }
}
