package jdbc.imp;

import bottle.tuples.Tuple5;
import bottle.util.StringUtil;
import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.DataBaseType;
import jdbc.define.option.JDBCSessionFacade;
import jdbc.define.session.JDBCSessionManagerAbs;
import jdbc.define.slice.DatabaseSliceRule;
import jdbc.define.slice.TableManySliceRule;
import jdbc.define.slice.TableSliceRule;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author: leeping
 * @Date: 2019/8/16 12:36
 */
public class TomcatJDBC {

    /* 表->列结构 */
    public static final class TableRow {
        public final String tableName;//表名
        public final String rowName;//列名
        public final String rowMark;//备注
        public final int index;//下标

        private TableRow(String tableName, String rowName, String rowMark, int index) {
            this.tableName = tableName;
            this.rowName = rowName;
            this.rowMark = rowMark;
            this.index = index;
        }

        @Override
        public String toString() {
            return rowName;
        }
    }

    /* 数据库类型@数据库名 <-> 数据库连接池对象 */
    private final static Map<String , TomcatJDBCPool> databasePoolMap = new HashMap<>();

    /* 数据库类型@数据库名 <-> 所有表名 */
    private final static Map<String,Set<String>> dbTableAllMap = new HashMap<>();

    /* 数据库类型@表名 <-> 数据库类型@数据库名 */
    private final static Map<String,Set<String>> tableDbAllMap = new HashMap<>();

    /* 数据库类型@存储过程/函数名字 <-> 数据库类型@数据库名 */
    private final static Map<String,Set<String>> dbProcedureAllMap = new HashMap<>();

    /* 数据库类型@存储过程/函数名 <-> 数据库类型@数据库名 */
    private final static Map<String,Set<String>> procedureDbAllMap = new HashMap<>();

    /* 数据库类型@表名 <-> 列对象列表 */
    private final static Map<String,List<TableRow>> tableRowsAllMap = new HashMap<>();

    /* 获取文件 */
    private static File[] getDicFile(String dirName,Class<?> clazz) throws Exception {
        //优先加载外部,外部不存在, 加载resource
        String dirPath = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
        File dir = new File(dirPath+"/resources/"+dirName);
        if (!dir.exists()){
            boolean isCreate = dir.mkdirs();
            if (!isCreate) throw new RuntimeException("无法创建文件夹: " + dir);
            File resourcesOut = dir.getParentFile();
            URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            try(JarFile localJarFile = new JarFile(new File(url.getPath()));){

                Enumeration<JarEntry> entries = localJarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.isDirectory()){
                        continue;
                    }
                    String innerPath = jarEntry.getName();
                    if(innerPath.startsWith(dirName)){
                        try(InputStream in =  clazz.getClassLoader().getResourceAsStream(innerPath)){
                            if (in==null) continue;
                            try(FileOutputStream out = new FileOutputStream(new File(resourcesOut,innerPath))){
                                byte[] bytes = new byte[1024];
                                int len;
                                while ( (len = in.read(bytes) )> 0 ){
                                    out.write(bytes,0,len);
                                }
                            }
                        }
                    }
                }

            }

        }

        if (!dir.isDirectory()) throw new FileNotFoundException(dirName +" is not exits directory");
        return Objects.requireNonNull(dir.listFiles());
    }

    /* 获取文件流 */
    private static InputStream getResourceConfig(String config) throws IOException {
        //优先加载外部,外部不存在, 加载resource
        String dirPath = new File(TomcatJDBC.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
        File file = new File(dirPath+"/resources/"+config);
        if (file.exists() && file.isFile() && file.length() > 0) return Files.newInputStream(file.toPath());
        return TomcatJDBC.class.getClassLoader().getResourceAsStream(config);
    }

    /** 使用一个resources目录下指定目录所有配置文件初始化连接池 */
    public static void initialize(String dirName,Class<?> clazz) throws Exception{
        File[] files = getDicFile(dirName==null?"": dirName,clazz);
        if (files==null || files.length == 0) throw new IllegalArgumentException("dir name = '"+dirName+"' ,class = '"+clazz+"' No connection profile exists.");
        initialize(files);
    }

    /** 使用多个配置文件名 在resource中查询配置并初始化连接池 */
    public static void initialize(String... configList) throws Exception{
      initialize(Arrays.asList(configList));
    }

    /** 初始化连接池对象 */
    public static void initialize(List<String> configList) throws Exception{
        for (String fileName : configList){
            try(InputStream is = getResourceConfig(fileName)){
                if (is == null){
                    JDBCLogger.print("【警告】 尝试加载数据库配置文件 " + fileName +" 无法获取文件流内容");
                    continue;
                }
                genDatabaseInfo(is);
            }
        }
        genTableInfo();
    }

    /** 使用多个配置文件名 在resource中查询配置并初始化连接池 */
    public static void initializeCatchException(String... configList){
        try {
            TomcatJDBC.initialize(configList);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** 多个配置文件初始化连接池 */
    public static void  initialize(File... files)throws Exception{
        for (File file : files){
            try(InputStream is = Files.newInputStream(file.toPath())){
                genDatabaseInfo(is);
            }
        }
        genTableInfo();
    }

    /** 根据属性对象初始化连接池 */
    public static TomcatJDBCPool initializeByProperties(Properties prop) throws JDBCException{
        if (prop == null) return null;
        TomcatJDBCPool pool = new TomcatJDBCPool();
        pool.initialize(prop);
        return pool;
    }
    /** 根据数据流对象初始化连接池 */
    public static TomcatJDBCPool initializeByInputStream(InputStream is) throws JDBCException{
        if (is == null) return null;
        TomcatJDBCPool pool = new TomcatJDBCPool();
        try {
            Properties properties = new Properties();
            properties.load(is);
            pool.initialize(properties);
        } catch (IOException e) {
            throw new JDBCException("无法加载JDBC配置文件: "+ e);
        }
        return pool;
    }

    /** 加载数据库 */
    private synchronized static void genDatabaseInfo(InputStream is) throws JDBCException {
        TomcatJDBCPool pool = initializeByInputStream(is);
        if (pool==null) return;

        String  databaseTypeStr = pool.getDataBaseType().name();
        String databaseName = pool.getDataBaseName();
        String k = databaseTypeStr+"@"+databaseName;
        TomcatJDBCPool _pool = databasePoolMap.get(k);
        if (_pool!=null) throw  new JDBCException("相同KEY("+k+")已存在数据库连接池: "+ _pool);
        databasePoolMap.put(k,pool);
    }

    private static void clearDataBaseInfo(){
        tableRowsAllMap.clear();
        procedureDbAllMap.clear();
        dbProcedureAllMap.clear();
        tableDbAllMap.clear();
        dbTableAllMap.clear();
    }

    /* 刷新数据表信息 */
    private synchronized static void genTableInfo() {
        clearDataBaseInfo();
        //生成数据库表信息

        for (Map.Entry<String, TomcatJDBCPool> entry : databasePoolMap.entrySet()) {
            TomcatJDBCPool pool = entry.getValue();
            createDataBaseInfo(pool);
            pool.closeSession();
        }

    }

    /** 加载数据库内 表,函数,过程,列字段 信息 */
    private static void createDataBaseInfo(JDBCSessionManagerAbs pool) {
        DataBaseType databaseType = pool.getDataBaseType();//数据库类型
        String  databaseTypeStr = databaseType.name();
        String databaseName = pool.getDataBaseName();//数据库名

        String queryTableSQL = null;
        String queryTableRowSQL = null;
        String queryProcedureFunctionSQL = null;

        if (databaseType == DataBaseType.mysql){
            queryTableSQL = "SELECT table_name FROM information_schema.tables WHERE table_schema='"+databaseName+"'";
            queryTableRowSQL = "SELECT COLUMN_NAME, column_comment FROM INFORMATION_SCHEMA.Columns WHERE TABLE_SCHEMA='"+databaseName+"' AND table_name=? ";
            queryProcedureFunctionSQL = "SELECT name FROM mysql.proc WHERE db='"+databaseName+"'";
        }
        if (databaseType == DataBaseType.clickhouse){
            queryTableSQL = "SELECT name FROM system.tables WHERE database = '"+databaseName+"'";
            queryTableRowSQL = "SELECT name,type FROM system.columns WHERE database = '"+databaseName+"' AND table = ? ";
        }

        JDBCSessionFacade facade = new JDBCSessionFacade(pool);

        if (queryProcedureFunctionSQL!=null){
            /* 查询全部存储过程及函数 */
            List<Object[]> procedureFunctionLines = facade.query(queryProcedureFunctionSQL,null,null);
            //过程/函数列表
            Set<String> procedureSet = dbProcedureAllMap.computeIfAbsent(databaseTypeStr+"@"+databaseName,k -> new HashSet<>());
            for (Object[] row : procedureFunctionLines){
                String proFunName = String.valueOf(row[0]);

                procedureSet.add(databaseTypeStr+"@"+proFunName);

                Set<String> dataBaseSet = procedureDbAllMap.computeIfAbsent(databaseTypeStr+"@"+proFunName, k -> new HashSet<>()); //过程/函数 名获取db对应的列表
                dataBaseSet.add(proFunName);
            }
        }

        if (queryTableSQL != null){
            /* 查询全部数据表 */
            List<Object[]> tableLines = facade.query(queryTableSQL,null,null);
            //获取数据库的表列表
            Set<String> tablesSet = dbTableAllMap.computeIfAbsent(databaseTypeStr+"@"+databaseName,k -> new HashSet<>());
            for (Object[] row : tableLines ){
                String tableName = String.valueOf(row[0]);
                //数据库关联表名
                tablesSet.add(databaseTypeStr+"@"+tableName);

                //表名关联数据库
                Set<String> dataBaseSet = tableDbAllMap.computeIfAbsent(databaseTypeStr+"@"+tableName, k -> new HashSet<>()); //表名获取db对应的列表
                dataBaseSet.add(databaseName);
                //生成表的所有行对象
                List<Object[]> tableRowLines = facade.query(queryTableRowSQL,new Object[]{tableName},null);
                List<TableRow> keyList = tableRowsAllMap.computeIfAbsent(databaseTypeStr+"@"+tableName, k -> new ArrayList<>());
                genTableKeys(databaseTypeStr+"@"+tableName,tableRowLines,keyList);
            }
        }

    }

    //生成表的列对象
    private static void genTableKeys(String tableName,List<Object[]>  lines,List<TableRow> keyList){
        String rowName;//列名
        String rowMark;//备注
        for (int index = 0;index<lines.size();index++ ){
            Object[] row = lines.get(index);
            rowName = String.valueOf(row[0]);
            rowMark = String.valueOf(row[1]);
            keyList.add(new TableRow(tableName,rowName,rowMark,index));
        }
    }

    public static void destroy() {
        clearDataBaseInfo();
        //关闭数据库连接
        Iterator<Map.Entry<String ,TomcatJDBCPool>> it = databasePoolMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String ,TomcatJDBCPool> entry = it.next();
            it.remove();
            TomcatJDBCPool pool = entry.getValue();
            pool.unInitialize();
        }

    }

    /**
     * 获取数据库连接信息:主机,端口,用户名,密码,数据库
     * */
    public static Tuple5<String,Integer,String,String,String> getDataBasePoolConnectInfo(DataBaseType dataBaseType, String databaseName){
        TomcatJDBCPool dbp = getDataBasePool(dataBaseType, databaseName);
        return new Tuple5<>(dbp.getHost(),dbp.getPort(),dbp.getUsername(),dbp.getPassword(),dbp.getDataBaseName());
    }

    /* 获取数据库池对象 */
    private static TomcatJDBCPool getDataBasePool(DataBaseType dataBaseType, String databaseName){
        String k = dataBaseType.name() +"@"+databaseName;
        TomcatJDBCPool dbPool =  databasePoolMap.get(k);
        if (dbPool == null) JDBCLogger.print("【异常】找不到指定的数据库 '"+k+"'");
        return dbPool;
    }

    /* 检测连接是否有效 */
    private static JDBCSessionFacade checkDBConnection(JDBCSessionFacade facade) {
        if (facade.checkDBConnectionValid()) return facade;
        //此连接池无效
        JDBCLogger.print("【异常】数据库连接不可用 " + facade.getManager());
        return null;
    }

    /* 获取一个具体的数据库连接池操作对象*/
    public static JDBCSessionFacade getFacade(DataBaseType dataBaseType,String databaseName) {
        TomcatJDBCPool pool = getDataBasePool(dataBaseType,databaseName);
        if (pool!=null){
            return checkDBConnection( new JDBCSessionFacade(pool) );
        }
       return null;
    }

    //获取指定表名的所有键
    public static List<TableRow> getTableRowKeyList(DataBaseType dataBaseType,String tableSimple, String tableCustomFlag){
        String tableName = tableSimple + (StringUtil.isEmpty(tableCustomFlag)?"":tableCustomFlag);
        return tableRowsAllMap.get(dataBaseType.name()+"@"+tableName);
    }

    //根据表名,键下标 获取键对象
    public static TableRow getTableRowByTableNameAndRowIndex(DataBaseType dataBaseType,String tableSimple, String tableCustomFlag, int index){
        List<TableRow> list = getTableRowKeyList(dataBaseType,tableSimple,tableCustomFlag);
        if (index < list.size()) return list.get(index);
        return null;
    }


    public static List<String> getDataBasesByTableName(DataBaseType dataBaseType,String tableName){
        Set<String> set = tableDbAllMap.get(dataBaseType.name() + "@" + tableName);
        return set==null ? null : new ArrayList<>(set);
    }

    public static List<String> getDataBasesByProcedureName(DataBaseType dataBaseType,String tableName){
        Set<String> set = procedureDbAllMap.get(dataBaseType.name() + "@" + tableName);
        return set==null ? null : new ArrayList<>(set);
    }

    private static void databaseInfoToString(TomcatJDBCPool pool,StringBuilder sb) {
        sb.append("\n\t 数据库: "+ pool );
        String key = pool.getDataBaseType()+"@"+pool.getDataBaseName();
        Set<String> list = dbTableAllMap.get(key);
        if (list!=null){
            sb.append("\n\t\t表总数: "+ list.size());
            for (String tableName : list){
                String _tableName = tableName.replace(pool.getDataBaseType()+"@","");
                sb.append("\n\t\t\t").append(_tableName);
                List<TomcatJDBC.TableRow> rows = tableRowsAllMap.get(pool.getDataBaseType()+"@"+_tableName);
                sb.append("\t列数: " + rows.size());
                for (TomcatJDBC.TableRow row : rows){
                    sb.append("\n\t\t\t\t").append(row.index+" "+row.rowName+" " + row.rowMark);
                }
            }
        }
        list = dbProcedureAllMap.get(key);
        if (list!=null && list.size()>0){
            sb.append("\n\t\t存储过程和函数总数: "+ list.size());
            for (String p_fName : list){
                sb.append("\n\t\t\t\t").append(p_fName);
            }
        }
        sb.append("\n");
    }

    public static String printDatabaseInfo() {
        List<TomcatJDBCPool> list = new ArrayList<>(databasePoolMap.values());
        StringBuilder sb = new StringBuilder("数据库初始化情况:");
        for (TomcatJDBCPool pool : list){
            databaseInfoToString(pool,sb);
        }
        return sb.toString();
    }


}
