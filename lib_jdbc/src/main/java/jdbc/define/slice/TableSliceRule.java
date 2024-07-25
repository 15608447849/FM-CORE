package jdbc.define.slice;

//分表规则
public interface TableSliceRule {
    String convert(String tableName, int table_slice);
}
