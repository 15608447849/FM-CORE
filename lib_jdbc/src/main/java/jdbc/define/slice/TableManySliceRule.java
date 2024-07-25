package jdbc.define.slice;

public interface TableManySliceRule {
    String convert(String tableName, int[] table_slices);
}
