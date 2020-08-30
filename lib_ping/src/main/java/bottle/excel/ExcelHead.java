package bottle.excel;

import bottle.util.Log4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import static bottle.excel.LeePoiExcel.isDebugLog;

/**
 * @Author: leeping
 * @Date: 2020/5/27 10:50
 */
public class ExcelHead extends ExcelElement{

    private final String headName;
    public ExcelHead(int index,String headName) {
        super(index,0);
        this.headName = headName==null?"":headName;
    }

    @Override
    void execute(HSSFWorkbook workbook, HSSFSheet sheet, HSSFRow row, HSSFCell cell) {
        if (isDebugLog) Log4j.info("设置表头: " + cell.getColumnIndex() + " , "+ cell.getRowIndex() +" "+headName);
        cell.setCellValue(headName);
    }
}
