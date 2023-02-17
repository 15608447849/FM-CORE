package jdbc.imp;

import bottle.util.EncryptUtil;
import bottle.util.Log4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static jdbc.imp.TomcatJDBCTool.intArrayToValue;

public class ClickHouseTool {

    private static final HashMap<String,String> table_keys_sql_map = new HashMap<>();
    public static int batchInsertDataToClickHouse(String tableName, String[] keys, List<Object[]> valueParams){
        // 批量插入数据到ck数据库
        if (keys == null || keys.length==0 || valueParams  == null) return -1;
        if (valueParams.size() == 0) return -1;

        String md5Key = EncryptUtil.encryption("I" + tableName + Arrays.toString(keys));
        String insertSQL = table_keys_sql_map.get(md5Key);
        if (insertSQL == null){
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO {{? ").append(tableName.trim()).append("}} ");
            sb.append("( ");
            for (int i = 0;i<keys.length;i++){
                sb.append("`").append(keys[i].trim()).append("`");
                if (i < keys.length-1) sb.append(",");
            }
            sb.append(" )");
            sb.append(" VALUES ");
            sb.append("( ");
            for (int i = 0;i<keys.length;i++){
                sb.append("?");
                if (i < keys.length-1) sb.append(",");
            }
            sb.append(" )");
            insertSQL = sb.toString();
            table_keys_sql_map.put(md5Key,insertSQL);
        }

        Log4j.info(insertSQL+"\n\t待插入条数: "+ valueParams.size());

        int[] res = TomcatJDBCDAO.update_clickHouse(insertSQL, valueParams);

        return intArrayToValue(res);
    }

    public static int batchInsertDataToClickHouse(String tableName, String keysStr, List<Object[]> valueParams){
        String[]  keys = keysStr.split(",");
        return batchInsertDataToClickHouse(tableName,keys,valueParams);
    }
}
