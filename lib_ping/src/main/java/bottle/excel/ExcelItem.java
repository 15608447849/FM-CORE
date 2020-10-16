package bottle.excel;

import bottle.util.Log4j;
import bottle.util.TimeTool;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;

import java.util.Date;

import static bottle.excel.LeePoiExcel.isDebugLog;

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
    void execute(HSSFWorkbook workbook, HSSFSheet sheet, HSSFRow row, HSSFCell cell) {
        String _value = String.valueOf(value);

        if (isDebugLog) Log4j.info("设置格子: " + cell.getColumnIndex() + " , "+ cell.getRowIndex() + " " +_value);

        /* 判断小数类型 */
        if (value instanceof Double || value instanceof Float || value instanceof Integer){
            cell.setCellValue(Double.valueOf(_value));
            return;
        }
        /* 判断日期类型 */
        Object[] arr = TimeTool.parseDate(_value,null);
       if (arr!=null){
            cell.setCellValue((Date) arr[1]);
            HSSFCellStyle style = workbook.createCellStyle();
            HSSFDataFormat format= workbook.createDataFormat();
           style.setDataFormat(format.getFormat(arr[0].toString()));
            cell.setCellStyle(style);//设置日期样式
            return;
        }
        /* 其他全部作为字符串处理 */
        cell.setCellValue(_value);
    }
}
