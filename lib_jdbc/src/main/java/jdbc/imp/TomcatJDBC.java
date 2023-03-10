package jdbc.imp;
import bottle.util.Log4j;
import bottle.util.StringUtil;
import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.DataBaseType;
import jdbc.define.option.JDBCSessionFacade;
import jdbc.define.session.JDBCSessionManagerAbs;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author: leeping
 * @Date: 2019/8/16 12:36
 */
public class TomcatJDBC {

    /* 表->列结构 */
    public static class TableRow {
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
    private final static Map<String , List<TomcatJDBCPool>> poolGroupMap = new HashMap<>();

    /* 数据库类型@数据库名 <-> 所有表名 */
    private final static Map<String,Set<String>> dbTableAllMap = new HashMap<>();

    /* 数据库类型@表名 <-> 数据库类型@数据库名 */
    private final static Map<String,Set<String>> tableDbAllMap = new HashMap<>();

    /* 数据库类型@存储过程/函数名字 <-> 数据库类型@数据库名 */
    private final static Map<String,Set<String>> dbProcedureAllMap = new HashMap<>();

    /* 数据库类型@存储过程/函数名 <-> 数据库类型@数据库名 */
    private final static Map<String,Set<String>> procedureDbAllMap = new HashMap<>();

    //数据库类型@表名 <->列对象列表
    private final static Map<String,List<TableRow>> tableRowsAllMap = new HashMap<>();

    /* 获取文件 */
    private static File[] getDicFile(String dirName,Class clazz) throws Exception {
//        Class clazz = TomcatJDBC.class;
        //优先加载外部,外部不存在, 加载resource
        String dirPath = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
        File dir = new File(dirPath+"/resources/"+dirName);
        if (!dir.exists()){
            boolean isCreate = dir.mkdirs();
            if (!isCreate) throw new RuntimeException("无法创建文件夹: " + dir);
            File resourcesOut = dir.getParentFile();
            URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            JarFile localJarFile = new JarFile(new File(url.getPath()));
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

        if (!dir.isDirectory()) throw new FileNotFoundException(dirName +" is not directory");
        return Objects.requireNonNull(dir.listFiles());
    }

    /* 获取文件流 */
    private static InputStream getResourceConfig(String config) throws FileNotFoundException {
        //优先加载外部,外部不存在, 加载resource
        String dirPath = new File(TomcatJDBC.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
        File file = new File(dirPath+"/resources/"+config);
        if (file.exists() && file.isFile() && file.length() > 0) return new FileInputStream(file);
        return TomcatJDBC.class.getClassLoader().getResourceAsStream(config);
    }

    /** 使用一个resources目录下指定目录所有配置文件初始化连接池 */
    public static void initialize(String dirName,Class clazz) throws Exception{
        File[] files = getDicFile(dirName==null?"":dirName,clazz);
      if (files==null || files.length == 0) throw new IllegalArgumentException("dir name = '"+dirName+"' ,class = '"+clazz+"' No connection profile exists.");
        initialize(files);
    }

    /** 使用多个配置文件名 在resource中查询配置并初始化连接池 */
    public static void initialize(String... configList) throws Exception{
      initialize(Arrays.asList(configList));
    }

    /** 使用多个配置文件名 在resource中查询配置并初始化连接池 */
    public static void initialize(boolean isEnableSync,List<String> configList) throws Exception{
        for (String fileName : configList){
            try(InputStream is = getResourceConfig(fileName)){
                if (is == null){
                    JDBCLogger.print("【警告】 尝试加载数据库配置文件 " + fileName +" 无法获取文件流内容");
                    continue;
                }
                genPoolObjectAlsoAddGroup(is);
            }
        }
        genTableInfo();
    }

    /** 初始化连接池对象 */
    public static void initialize(List<String> configList) throws Exception{
       initialize(true,configList);
    }

    /** 多个配置文件初始化连接池 */
    public static void  initialize(File... files)throws Exception{
        for (File file : files){
            try(InputStream is = new FileInputStream(file)){
                genPoolObjectAlsoAddGroup(is);
            }
        }
        genTableInfo();
    }

    /* 获取组标识 */
    private synchronized static void genPoolObjectAlsoAddGroup(InputStream is) {
        if (is == null) return;
        TomcatJDBCPool pool = new TomcatJDBCPool();
        pool.initialize(is);

        String  databaseTypeStr = pool.getDataBaseType().name();
        String databaseName = pool.getDataBaseName();

        List<TomcatJDBCPool> list = poolGroupMap.computeIfAbsent(databaseTypeStr+"@"+databaseName,k -> new ArrayList<>());
        list.add(pool);
        //排序 , 为 主从同步/读写分离 基础 , 规则定义, 第一个为 主数据库 , 其次都为从数据库
        list.sort(Comparator.comparing(TomcatJDBCPool::getSeq));
    }

    /* 刷新数据表信息 */
    private synchronized static void genTableInfo() {
        clearDataBaseInfo();
        //生成主数据库表信息
        Iterator<Map.Entry<String , List<TomcatJDBCPool>>> it = poolGroupMap.entrySet().iterator();
        Map.Entry<String , List<TomcatJDBCPool>> entry;
        List<TomcatJDBCPool> list;
        //每个组的主库
        TomcatJDBCPool masterPool;
        while (it.hasNext()){
            entry = it.next();
            list = entry.getValue();
            masterPool = list.get(0);
            createDataBaseInfo(masterPool);
//            JDBCLogger.print(printInitInfo(masterPool));
            masterPool.closeSession();
        }
    }

    private static void clearDataBaseInfo(){
        tableRowsAllMap.clear();
        procedureDbAllMap.clear();
        dbProcedureAllMap.clear();
        tableDbAllMap.clear();
        dbTableAllMap.clear();
    }

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
        //关闭数据库连接
        Iterator<Map.Entry<String , List<TomcatJDBCPool>>> it = poolGroupMap.entrySet().iterator();
        Map.Entry<String , List<TomcatJDBCPool>> entry;
        List<TomcatJDBCPool> list;
        while (it.hasNext()){
            entry = it.next();
            list = entry.getValue();
            for (TomcatJDBCPool pool : list){
                pool.unInitialize();
            }
        }
        clearDataBaseInfo();
    }

    private static String printInitInfo(TomcatJDBCPool pool) {

        String key = pool.getDataBaseType()+"@"+pool.getDataBaseName();
        StringBuilder sb = new StringBuilder(key);
        Set<String> list = dbTableAllMap.get(key);
        if (list!=null && list.size()>0){
            sb.append("\n##### 可用表 总数:\t"+ list.size());
            sb.append("\n");
            for (String tableName : list){
                String _tableName = tableName.replace(pool.getDataBaseType()+"@","");
                sb.append("表: ").append(_tableName);
                List<TableRow> rows = tableRowsAllMap.get(pool.getDataBaseType()+"@"+_tableName);
                if (rows.size()>0){
                    sb.append("\n\t列:\t");
                }
                for (TableRow row : rows){
                    sb.append(row.index+","+row.rowName+" ");
                }
                sb.append("\n");

            }
        }
        list = dbProcedureAllMap.get(key);
        if (list!=null && list.size()>0){
            sb.append("\n##### 可用存储过程/函数 总数:\t"+ list.size());
            sb.append("\n");
            for (String p_fName : list){
                sb.append(p_fName).append("; ");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    /* 存在备份库的情况下触发, 检测主库连接是否有效,无效切换到备份库进行操作 */
    private static JDBCSessionFacade checkDBConnection(JDBCSessionFacade facade, List<TomcatJDBCPool> list) {

        if (facade.checkDBConnectionValid()) return facade;
        //此连接池无效
        JDBCLogger.print("【异常】数据库连接不可用,请尝试恢复,数据库信息: "+ facade.getManager().getAddress()+" "+facade.getManager().getDataBaseName()+" "+ facade.getManager().getSeq());
        //获取任意一个连接
        for (TomcatJDBCPool pool : list){
            facade.setManager(pool);
            if (facade.checkDBConnectionValid()) {
                JDBCLogger.print("【异常】找到一个备份数据库,数据库信息: "+ facade.getManager().getAddress()+" "+facade.getManager().getDataBaseName()+" "+ facade.getManager().getSeq());
                return facade;
            }
        }
        return null;
    }

    /*获取执行的数据库池对象列表*/
    public static List<TomcatJDBCPool> getSpecDataBasePoolList(DataBaseType dataBaseType,String databaseName){
        List<TomcatJDBCPool> list =  poolGroupMap.get(dataBaseType.name() +"@"+databaseName);
        if (list == null) throw new JDBCException("usually nonexistent database by '"+databaseName+"'");
        return list;
    }

    /* 获取一个具体的数据库连接池操作对象 根据主库/从库*/
    public static JDBCSessionFacade getFacade(DataBaseType dataBaseType,String databaseName,boolean isMaster) {

        List<TomcatJDBCPool> list = getSpecDataBasePoolList(dataBaseType,databaseName);
        if (list!=null){
            JDBCSessionFacade facade;
            int index = 0;
            if (!isMaster && list.size() > 1){
                index = new Random().nextInt(list.size()-1)+1; //排除主库的其他所有从库
            }
            TomcatJDBCPool pool = list.get(index);
            facade = new JDBCSessionFacade(pool);
            if (list.size()>1){
                // 代表一定可以切换一个数据库,
                // 检查连接是否可用,
                // 不可用获取任意一个有效的数据库连接
                return checkDBConnection(facade,list);
            }else{
                return facade;
            }

        }
       return null;
    }

    //获取指定表名的所有键
    public static List<TableRow> getTableRowKeyList(DataBaseType dataBaseType,String tableSimple, String tableCustomFlag){
        String tableName = tableSimple + (StringUtil.isEmpty(tableCustomFlag)?"":tableCustomFlag);
        return tableRowsAllMap.get(dataBaseType.name()+"@"+tableName);
    }

    //根据表名/键下标 获取键对象
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

    //分表规则
    public interface TableSliceRule {
         String convert(String tableName,int table_slice);
    }

    public interface TableManySliceRule {
        String convert(String tableName,int[] table_slices);
    }

    //分库规则
    public interface DatabaseSliceRule {
        String convert(List<String> dbList, int db_slice);
    }

    public static void setTableSlice(TableSliceRule role) {
        TomcatJDBCDAO.tableRole = role;
    }

    public static void setTableManyRole(TableManySliceRule role) {
        TomcatJDBCDAO.tableManyRole = role;
    }

    public static void setDatabaseSlice(DatabaseSliceRule role) { TomcatJDBCDAO.databaseRole = role; }


}
