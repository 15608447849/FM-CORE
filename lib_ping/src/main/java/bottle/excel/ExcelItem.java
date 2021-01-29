package bottle.excel;

import bottle.util.Log4j;
import bottle.util.TimeTool;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import java.util.Date;



/**
 * @Author: leeping
 * @Date: 2020/5/27 13:34
 */
public class ExcelItem extends ExcelElement {

    private Object value;

    public ExcelItem(int x, int y,Object value) {
        super(x, y);
        this.value = value == null? "" : value;
    }

    @Override
    void execute(Workbook workbook, Sheet sheet, Row row, Cell cell,CellStyle style) {
        String _value = String.valueOf(value);

//        if (isDebug) Log4j.info("设置格子: " +  cell.getRowIndex() + " , "+ cell.getColumnIndex() + " , " +_value);

        /* 判断小数类型 */
        if (value instanceof Double || value instanceof Float || value instanceof Integer){
            cell.setCellValue(Double.valueOf(_value));
            return;
        }

        /* 判断日期类型 */
        Object[] arr = TimeTool.parseDate(_value);
        if (arr!=null){
            cell.setCellValue(TimeTool.date2Str( ((Date) arr[1]),arr[0].toString()));
            return;
        }

        /* 其他全部作为字符串处理 */
        cell.setCellValue(_value);
    }
}
