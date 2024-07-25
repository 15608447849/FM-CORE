package jdbc.define.slice;

import java.util.List;

//分库规则
public interface DatabaseSliceRule {
    String convert(List<String> dbList, int db_slice);
}
