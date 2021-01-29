package jdbc.imp;

import jdbc.define.option.JDBCSessionFacade;
import jdbc.define.option.Page;

import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/18 1:15
 * 读写分离
 */
public class ReadWriteSeparateJDBCFacadeWrap extends MasterSlaveSyncJDBCFacadeWrap{
    private JDBCSessionFacade reader;

    ReadWriteSeparateJDBCFacadeWrap(JDBCSessionFacade op) {
        super(op);
        reader = TomcatJDBC.getFacade(op.getManager().getDataBaseType(),op.getManager().getDataBaseName(),false);
    }

    @Override
    public List<Object[]> query(String sql, Object[] params,Page page) {
        try {
            List<Object[]> list = reader.query(sql,params,page);
            if (list.size() == 0) throw new Exception();
            return list;
        } catch (Exception e) {
            return op.query(sql,params,page);
        }
    }

    @Override
    public <T> List<T> query(String sql, Object[] params, Class<T> beanClass,Page page) {
        try {
            List<T> list = reader.query(sql,params,beanClass,page);
            if (list.size() == 0) throw new Exception();
            return list;
        } catch (Exception e) {
            return op.query(sql,params,beanClass,page);
        }
    }

    @Override
    public List<Object[]> queryMany(List<String> sqlList, Object[] params, Page page) {
        try {
            List<Object[]> list = reader.queryMany(sqlList,params,page);
            if (list.size() == 0) throw new Exception();
            return list;
        } catch (Exception e) {
            return op.queryMany(sqlList,params,page);
        }
    }

    @Override
    public <T> List<T> queryMany(List<String> sqlList, Object[] params, Class<T> beanClass, Page page) {
        try {
            List<T> list = reader.queryMany(sqlList,params,beanClass,page);
            if (list.size() == 0) throw new Exception();
            return list;
        } catch (Exception e) {
            return op.queryMany(sqlList,params,beanClass,page);
        }
    }

}
