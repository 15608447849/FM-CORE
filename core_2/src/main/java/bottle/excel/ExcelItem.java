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

        if (ExcelElement.isDebug) Log4j.info("设置格子: 坐标=(" +  cell.getRowIndex() + ","+ cell.getColumnIndex() + ") 内容=" +_value +"  样式="+ style.getDataFormatString());


        if (style.getDataFormatString().equals("0")){
            // 整数格式
            cell.setCellValue(Integer.valueOf(_value));
            return;
        }
        if (style.getDataFormatString().startsWith("0.")){
            // 小数格式
            cell.setCellValue(Double.valueOf(_value));
            return;
        }

        /* 其他全部作为字符串处理 */
        cell.setCellValue(_value);
    }
}
