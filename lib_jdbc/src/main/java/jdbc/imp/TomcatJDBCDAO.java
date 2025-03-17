package jdbc.imp;

import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.*;
import bottle.tuples.Tuple2;
import jdbc.define.slice.DatabaseSliceRule;
import jdbc.define.slice.TableManySliceRule;
import jdbc.define.slice.TableSliceRule;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.io.StringReader;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jdbc.define.option.JDBCUtils.pauseStatementGetTableNames;


/**
 * @Author: leeping
 * @Date: 2020/2/20 13:48
 */
public class TomcatJDBCDAO {

    /**
     * 原生态SQL查询时，指定的查询表对象固定格式的前缀字符串。
     */
    public static final String PREFIX_REGEX = "{{?";
    /**
     * 原生态SQL查询时，指定的查询表对象固定格式的后缀字符串。
     */
    public static final String SUFFIX_REGEX = "}}";

    private static final String RGE = "\\{\\{\\?(.*?)\\}\\}";

    private static final Pattern pattern = Pattern.compile(RGE);// 匹配的字符串

    public static TableSliceRule tableRole = null;

    public static TableManySliceRule tableManyRole = null;

    public static DatabaseSliceRule databaseRole = null;


    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//时间格式化工具
    private static final String[] sqlTimeFunNameArray = {"current_timestamp[(]\\s*[)]","current_date[(]\\s*[)]","current_time[(]\\s*[)]","now[(]\\s*[)]","curdate[(]\\s*[)]","curtime[(]\\s*[)]"};
    private static final String[] _sqlTimeFunNameArray = {"current_timestamp","current_date","current_time","######@@@@#####","curdate","curtime"};

    private static List<String> regSubStr(String sql) {
        List<String> list = new ArrayList<>();
        try {
            Matcher m = pattern.matcher(sql);
            while (m.find()) list.add(m.group(1));
        } catch (Exception ignored) {}

        if (list.isEmpty()){
            try {
                // 尝试解析SQL语句
                Set<String> tables = pauseStatementGetTableNames(sql);
                if (tables!=null && !tables.isEmpty()) list.addAll(tables);
            }catch (Exception ignored){}
        }
        if (list.isEmpty())
            throw new JDBCException("没有找到符合规则{{?TABLE_NAME}}的表名或SQL语法解析失败 SQL:\t"+sql);
        return list;
    }




    private static String replaceAllCaseInsensitive(String source, String oldStr,String newStr){
        return Pattern.compile(oldStr, Pattern.CASE_INSENSITIVE).matcher(source).replaceAll(newStr);
    }

    /* 转换SQL的时间函数 */
    private static String convertSqlTimeFun(String sql,String timeStr){
        for (int i=0;i<sqlTimeFunNameArray.length;i++){
            String str = sqlTimeFunNameArray[i];
            String _str = _sqlTimeFunNameArray[i];
            sql = replaceAllCaseInsensitive(sql,str,_str);
            sql = replaceAllCaseInsensitive(sql,_str,"'"+timeStr+"'");
        }
        return sql;
    }


    /* 返回使用的表名之一(最后一个),原始sql */
    private static Tuple2<String, String> sqlConvertNative(String sql, int table_slice) {
        List<String> tableList = regSubStr(sql);

        String _tableName = null;
        for (String tableName : tableList) {
            _tableName = tableName.trim();
            if (tableRole != null) {
                _tableName = tableRole.convert(_tableName, table_slice);
                if (_tableName == null || _tableName.length() == 0)
                    throw new JDBCException("'table_slice'(" + table_slice + ") is invalid");
            }
            sql = sql.replace(PREFIX_REGEX + tableName + SUFFIX_REGEX, _tableName); //还原sql语句
        }
        return new Tuple2<>(_tableName, sql);
    }


    /* 转换sql,返回库名 */
    private static String transformNativeSql(List<String> sqlList, List<String> nativeSqlList, int table_slice) {
        if (sqlList == null || sqlList.size() == 0) throw new JDBCException("sqlList is empty");
        String _tableName = null;
        String timeStr =  sdf.format(new Date());
        for (String sql : sqlList) {
            Tuple2<String, String> tuple2 = sqlConvertNative(sql, table_slice);
            _tableName = tuple2.getValue0();
            String nativeSql = convertSqlTimeFun(tuple2.getValue1(),timeStr);
            nativeSqlList.add(nativeSql);
        }
        return _tableName;
    }

    /* 表名/过程名/函数名 匹配 数据库名 */
    private static String tableNameMatchDatabase(DataBaseType dataBaseType,boolean isCallProcedure,String _tableName, int db_slice) {
        //通过一个表名/过程名/函数名 查询库名列表
        List<String> dbList = isCallProcedure ?
                TomcatJDBC.getDataBasesByProcedureName(dataBaseType,_tableName) :
                TomcatJDBC.getDataBasesByTableName(dataBaseType,_tableName);

        if (dbList == null) throw new JDBCException("找不到表名所在的数据库: " + _tableName);
        String dbName = dbList.get(0);//默认选中第一个数据库
        if (databaseRole != null) {
            String _dbName = databaseRole.convert(dbList, db_slice);
            if (_dbName == null || _dbName.length() == 0)
                throw new JDBCException("'db_slice'(" + db_slice + ") is invalid");
            dbName = _dbName;
        }
        return dbName;
    }

    /* 返回:  0-库名, 1-完整的sql */
    private static Tuple2<String, List<String>> ergodicSqlFindAllTableNameReturnSQL(DataBaseType dataBaseType,boolean isCallProcedure,List<String> sqlList,int db_slice, int table_slice) {
        //所有得完整的sql
        List<String> nativeSqlList = new ArrayList<>();
        String _tableName = transformNativeSql(sqlList, nativeSqlList, table_slice);
        String dbName = tableNameMatchDatabase(dataBaseType,isCallProcedure,_tableName, db_slice);
        return new Tuple2<>(dbName, nativeSqlList);
    }

    /* 获取具体的数据库执行对象 及 SQL语句 */
    private static Tuple2<JDBCSessionFacadeWrap, List<String>> getDaoOp(DataBaseType dataBaseType,boolean isCallProcedure,List<String> sqlList, int db_slice, int table_slice) {
        // 返回: 库名/ 获取原始sql列表
        Tuple2<String, List<String>> tuple = ergodicSqlFindAllTableNameReturnSQL(dataBaseType,isCallProcedure,sqlList, db_slice, table_slice);
        //2. 获取连接池操作对象
        JDBCSessionFacade op = TomcatJDBC.getFacade(dataBaseType,tuple.getValue0());
        return new Tuple2<>(new JDBCSessionFacadeWrap(op), tuple.getValue1());
    }

    /* 根据 分表标识产生多条sql语句 */
    private static Tuple2<JDBCSessionFacadeWrap, List<String>> getQueryDaoOpMany(DataBaseType dataBaseType,String sql, int... table_slices) {
        List<String> sqlList = new ArrayList<>();
        String _tableName = null;
        for (int table_slice : table_slices) {
            try {
                Tuple2<String, String> tuple2 = sqlConvertNative(sql, table_slice);
                _tableName = tuple2.getValue0();
                sqlList.add(tuple2.getValue1());
            } catch (JDBCException e) {
                JDBCLogger.error(null, e);
            }
        }
        String database = tableNameMatchDatabase(dataBaseType,false,_tableName, 0);
        // 获取连接池操作对象
        JDBCSessionFacade op = TomcatJDBC.getFacade(dataBaseType,database);

        return new Tuple2<>(new JDBCSessionFacadeWrap(op), sqlList);
    }

    /* 根据分表标识产生SQL */
    private static Tuple2<JDBCSessionFacadeWrap, String> getNativeSql(DataBaseType dataBaseType,String sql, int[] table_slices) {
        List<String> tableList = regSubStr(sql); //获取表名列表

        String _tableName = null;
        for (String tableName : tableList) {
            _tableName = tableName.trim();
            if (tableManyRole != null) {
                _tableName = tableManyRole.convert(_tableName, table_slices);
                if (_tableName == null || _tableName.length() == 0)
                    throw new JDBCException("'table_slices'(" + Arrays.toString(table_slices) + ") is invalid");
            }
            sql = sql.replace(PREFIX_REGEX + tableName + SUFFIX_REGEX, _tableName); //还原sql语句
        }

        String database = tableNameMatchDatabase(dataBaseType,false,_tableName, 0);
        //2. 获取连接池操作对象
        JDBCSessionFacade op = TomcatJDBC.getFacade(dataBaseType,database);
        return new Tuple2<>(new JDBCSessionFacadeWrap(op), sql);
    }

    /**
     * 执行存储过程
     */
    public static List<Object[]> call(String sql, Object[] inParam, int outParamStartPos, int[] outParamTypes) {
        List<String> sqlList = new ArrayList<>();
        sqlList.add(sql);
        Tuple2<JDBCSessionFacadeWrap, List<String>> tuple = getDaoOp(DataBaseType.mysql,true,sqlList,0,0);
        return tuple.getValue0().call(tuple.getValue1().get(0),inParam,outParamStartPos,outParamTypes);
    }

    /**
     * 执行存储过程
     */
    public static List<Object[]> call(String sql, Object[] inParam) {
        return call(sql,inParam,1,new int[Types.NULL]);
    }

    /**
     * 普通修改
     */
    public static int update(DataBaseType dataBaseType,String sql, Object[] params, int db_slice, int table_slice) {
        List<String> sqlList = new ArrayList<>();
        sqlList.add(sql);
        Tuple2<JDBCSessionFacadeWrap, List<String>> tuple = getDaoOp(dataBaseType,false,sqlList, db_slice, table_slice);
        return tuple.getValue0().execute(tuple.getValue1().get(0), params);
    }

    /**
     * 普通修改
     */
    public static int update(String sql, Object[] params) {
        return update(DataBaseType.mysql,sql, params, 0, 0);
    }

    /**
     * 普通修改
     */
    public static int update(String sql) {
        return update(sql, new Object[]{});
    }


    /**
     * 普通修改
     */
    public static int update_clickHouse(String sql, Object[] params) {
        return update(DataBaseType.clickhouse,sql, params, 0, 0);
    }

    /**
     * 普通修改
     */
    public static int update_clickHouse(String sql) {
        return update_clickHouse(sql, new Object[]{});
    }


    /**
     * 批量修改
     */
    public static int[] update(DataBaseType dataBaseType,String sql, List<Object[]> paramList, int batchSize, int db_slice, int table_slice) {
        List<String> sqlList = new ArrayList<>();
        sqlList.add(sql);
        Tuple2<JDBCSessionFacadeWrap, List<String>> tuple = getDaoOp(dataBaseType,false,sqlList, db_slice, table_slice);
        return tuple.getValue0().executeBatch(tuple.getValue1().get(0), paramList, batchSize);
    }

    /**
     * 批量修改
     */
    public static int[] update(String sql, List<Object[]> paramList,int batchSize, int db_slice, int table_slice) {
        return update(DataBaseType.mysql,sql, paramList, batchSize, db_slice, table_slice);
    }

    /**
     * 批量修改
     */
    public static int[] update(String sql, List<Object[]> paramList, int batchSize) {
        return update(sql, paramList, batchSize, 0, 0);
    }

    /**
     * 批量修改
     */
    public static int[] update(String sql, List<Object[]> paramList) {
        return update(sql, paramList, paramList.size());
    }


    /**
     * 批量修改
     */
    public static int[] update_clickHouse(String sql, List<Object[]> paramList, int batchSize,int db_slice, int table_slice) {
        return update(DataBaseType.clickhouse,sql, paramList, batchSize, db_slice, table_slice);
    }

    /**
     * 批量修改
     */
    public static int[] update_clickHouse(String sql, List<Object[]> paramList, int batchSize) {
        return update_clickHouse(sql, paramList, batchSize, 0, 0);
    }

    /**
     * 批量修改
     */
    public static int[] update_clickHouse(String sql, List<Object[]> paramList) {
        return update_clickHouse(sql, paramList, paramList.size());
    }

    /**
     * 事务修改
     */
    public static int update(List<String> sqlList, List<Object[]> paramList, int db_slice, int table_slice, boolean ignoreUnaffectedRows) {
        Tuple2<JDBCSessionFacadeWrap, List<String>> tuple = getDaoOp(DataBaseType.mysql,false, sqlList, db_slice, table_slice);
        return tuple.getValue0().executeTransaction(tuple.getValue1(), paramList, ignoreUnaffectedRows);
    }

    /**
     * 事务修改
     */
    public static int update(List<String> sqlList, List<Object[]> paramList, int db_slice, int table_slice) {
        return update(sqlList, paramList, db_slice, table_slice, false);
    }

    /**
     * 事务修改
     */
    public static int update(List<String> sqlList, List<Object[]> paramList) {
        return update(sqlList, paramList, 0, 0);
    }

    /**
     * 事务修改
     */
    public static int update(List<String> sqlList, List<Object[]> paramList, boolean ignoreUnaffectedRows) {
        return update(sqlList, paramList, 0, 0, ignoreUnaffectedRows);
    }


    /**
     * 执行原始SQL
     */
    public static int executeOrigin(DataBaseType dataBaseType,String dbName, String sql, Object[] param) {
        JDBCSessionFacade facade = TomcatJDBC.getFacade(dataBaseType,dbName);
        if (facade == null) throw new IllegalArgumentException("数据库类型或库名不正确,找不到可执行的数据库对象");
        return facade.execute(sql, param);
    }

    /**
     * 执行原始SQL
     */
    public static int[] executeOriginBatch(DataBaseType dataBaseType,String dbName, String sql, List<Object[]> paramList) {
        JDBCSessionFacade facade = TomcatJDBC.getFacade(dataBaseType,dbName);
        if (facade == null) throw new IllegalArgumentException("数据库类型或库名不正确,找不到可执行的数据库对象");
        return facade.executeBatch(sql, paramList,paramList.size());
    }

    /**
     * 执行原始SQL
     */
    public static int executeOriginTranslate(DataBaseType dataBaseType,String dbName, List<String> sqlList, List<Object[]> paramList,boolean ignoreUnaffectedRows) {
        JDBCSessionFacade facade = TomcatJDBC.getFacade(dataBaseType,dbName);
        if (facade == null) throw new IllegalArgumentException("数据库类型或库名不正确,找不到可执行的数据库对象");
        return facade.executeTransaction(sqlList, paramList,ignoreUnaffectedRows);
    }

    /**
     * 原始sql查询
     */
    public static List<Object[]> queryByOriginal(DataBaseType dataBaseType,String databaseName, String sql, Page page, Object... params) {
        JDBCSessionFacade facade = TomcatJDBC.getFacade(dataBaseType,databaseName);
        if (facade == null) throw new IllegalArgumentException("数据库类型或库名不正确,找不到可执行的数据库对象");
        return facade.query(sql, params, page);
    }

    /**
     * 原始sql查询
     * 自动转对象
     */
    public static <T> List<T> queryByOriginal(DataBaseType dataBaseType,String databaseName, String sql, Page page,Class<T> beanClass, Object... params) {
        JDBCSessionFacade facade = TomcatJDBC.getFacade(dataBaseType,databaseName);
        if (facade == null) throw new IllegalArgumentException("数据库类型或库名不正确,找不到可执行的数据库对象");
        return facade.query(sql, params,beanClass, page);
    }

    /**
     * 执行原始存储过程
     */
    public static List<Object[]> executeOrigin(DataBaseType dataBaseType,String dbName, String sql, Object[] param, int outParamStartPos, int[] outParamTypes) {
        JDBCSessionFacade facade = TomcatJDBC.getFacade(dataBaseType,dbName);
        if (facade == null) throw new IllegalArgumentException("数据库类型或库名不正确,找不到可执行的数据库对象");
        return facade.call(sql, param,outParamStartPos,outParamTypes);
    }

    /*********************************************************************************************************************************************************************************************************************/


    /**
     * 分库/分表/分页 查询
     */
    public static List<Object[]> query(DataBaseType dataBaseType,String sql, Object[] params, Page page, int db_slice, int table_slice) {
        List<String> sqlList = new ArrayList<>();
        sqlList.add(sql);
        Tuple2<JDBCSessionFacadeWrap, List<String>> tuple = getDaoOp(dataBaseType,false,sqlList, db_slice, table_slice);
        return tuple.getValue0().query(tuple.getValue1().get(0), params, page);
    }

    /**
     * 分表/分页 查询
     */
    public static List<Object[]> queryView(DataBaseType dataBaseType,String sql, Object[] params, Page page, int... table_slices) {
        Tuple2<JDBCSessionFacadeWrap, String> tuple = getNativeSql(dataBaseType,sql, table_slices);
        return tuple.getValue0().query(tuple.getValue1(), params, page);
    }

    /**
     * 分表/分页 查询
     */
    public static List<Object[]> queryMany(DataBaseType dataBaseType,String sql, Object[] params, Page page, int... table_slices) {
        Tuple2<JDBCSessionFacadeWrap, List<String>> tuple = getQueryDaoOpMany(dataBaseType,sql, table_slices);
        return tuple.getValue0().queryMany(tuple.getValue1(), params, page);
    }

    /**
     * 分库/分表/分页 查询
     * 根据对象
     */
    public static <T> List<T> query(DataBaseType dataBaseType,String sql, Object[] params, Page page, Class<T> beanClass, int db_slice, int table_slice) {
        List<String> sqlList = new ArrayList<>();
        sqlList.add(sql);
        Tuple2<JDBCSessionFacadeWrap, List<String>> tuple = getDaoOp(dataBaseType,false,sqlList, db_slice, table_slice);
        return tuple.getValue0().query(tuple.getValue1().get(0), params, beanClass, page);
    }

    /**
     * 分表/分页 查询
     */
    public static <T> List<T> queryView(DataBaseType dataBaseType,String sql, Object[] params, Page page,Class<T> beanClass, int... table_slices) {
        Tuple2<JDBCSessionFacadeWrap, String> tuple = getNativeSql(dataBaseType,sql, table_slices);
        return tuple.getValue0().query(tuple.getValue1(), params,beanClass, page);
    }

    /**
     * 分表/分页 查询
     * 根据对象
     */
    public static <T> List<T> queryMany(DataBaseType dataBaseType,String sql, Object[] params, Page page, Class<T> beanClass, int... table_slices) {
        Tuple2<JDBCSessionFacadeWrap, List<String>> tuple = getQueryDaoOpMany(dataBaseType,sql, table_slices);
        return tuple.getValue0().queryMany(tuple.getValue1(), params, beanClass, page);
    }

    /***********************************************************************************************************************************************************/

    /**
     * 分库/分表/分页 查询
     * mysql
     */
    public static List<Object[]> query(String sql, Object[] params,Page page, int db_slice, int table_slice) {
        return query(DataBaseType.mysql,sql, params, page, db_slice, table_slice);
    }

    /**
     * 分库/分表/分页 查询
     * 根据对象
     * mysql
     */
    public static <T> List<T> query(String sql, Object[] params,Page page, Class<T> beanClass,int db_slice, int table_slice) {
        return query(DataBaseType.mysql,sql, params, page,beanClass, db_slice, table_slice);
    }

    /**
     * 分页查询
     * mysql
     */
    public static List<Object[]> query(String sql, Object[] params, Page page) {
        return query(sql, params, page, 0, 0);
    }

    /**
     * 分页查询
     * 根据对象
     * mysql
     */
    public static <T> List<T> query(String sql, Object[] params, Page page, Class<T> beanClass) {
        return query(sql, params, page, beanClass, 0, 0);
    }

    /**
     * 分页查询
     * mysql
     */
    public static List<Object[]> query(String sql,Page page) {
        return query(sql, new Object[]{}, page);
    }

    /**
     * 分页查询
     * 根据对象
     * mysql
     */
    public static <T> List<T> query(String sql, Page page,Class<T> beanClass) {
        return query(sql, new Object[]{} ,page, beanClass);
    }

    /**
     * 不分页查询
     * mysql
     */
    public static List<Object[]> queryNoPage(String sql, Object[] params){
        return query(sql,params,new Page());
    }

    /**
     * 不分页查询
     * 根据对象
     * mysql
     */
    public static <T> List<T> queryNoPage(String sql, Object[] params, Class<T> beanClass) {
        return query(sql, params, new Page(), beanClass);
    }

    /**
     * 不分页查询
     * mysql
     */
    public static List<Object[]> queryNoPage(String sql) {
        return queryNoPage(sql,new Object[]{});
    }

    /**
     * 不分页查询,
     * 根据对象
     * mysql
     */
    public static <T> List<T> queryNoPage(String sql, Class<T> beanClass) {
        return queryNoPage(sql, new Object[]{}, beanClass);
    }

    /***************************************************************************************************************************************************************************************************************/

    /**
     * 分库/分表/分页 查询
     * clickHouse
     */
    public static List<Object[]> query_clickHouse(String sql, Object[] params,Page page, int db_slice, int table_slice) {
        return query(DataBaseType.clickhouse,sql, params, page, db_slice, table_slice);
    }

    /**
     * 分库/分表/分页 查询
     * 根据对象
     * mysql
     */
    public static <T> List<T> query_clickHouse(String sql, Object[] params,Page page, Class<T> beanClass,int db_slice, int table_slice) {
        return query(DataBaseType.clickhouse,sql, params, page,beanClass, db_slice, table_slice);
    }

    /**
     * 分页查询
     * mysql
     */
    public static List<Object[]> query_clickHouse(String sql, Object[] params, Page page) {
        return query_clickHouse(sql, params, page, 0, 0);
    }

    /**
     * 分页查询
     * 根据对象
     * mysql
     */
    public static <T> List<T> query_clickHouse(String sql, Object[] params, Page page, Class<T> beanClass) {
        return query_clickHouse(sql, params, page, beanClass, 0, 0);
    }

    /**
     * 分页查询
     * mysql
     */
    public static List<Object[]> query_clickHouse(String sql,Page page) {
        return query_clickHouse(sql, new Object[]{}, page);
    }

    /**
     * 分页查询
     * 根据对象
     * mysql
     */
    public static <T> List<T> query_clickHouse(String sql, Page page,Class<T> beanClass) {
        return query_clickHouse(sql, new Object[]{} ,page, beanClass);
    }

    /**
     * 不分页查询
     * mysql
     */
    public static List<Object[]> queryNoPage_clickHouse(String sql, Object[] params){
        return query_clickHouse(sql,params,new Page());
    }

    /**
     * 不分页查询
     * 根据对象
     * mysql
     */
    public static <T> List<T> queryNoPage_clickHouse(String sql, Object[] params, Class<T> beanClass) {
        return query_clickHouse(sql, params, new Page(), beanClass);
    }

    /**
     * 不分页查询
     * mysql
     */
    public static List<Object[]> queryNoPage_clickHouse(String sql) {
        return queryNoPage_clickHouse(sql,new Object[]{});
    }

    /**
     * 不分页查询
     * 根据对象
     * mysql
     */
    public static <T> List<T> queryNoPage_clickHouse(String sql, Class<T> beanClass) {
        return queryNoPage_clickHouse(sql, new Object[]{}, beanClass);
    }

}
