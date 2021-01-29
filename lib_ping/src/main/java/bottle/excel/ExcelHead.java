package bottle.excel;

import bottle.util.Log4j;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.awt.Color;


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
    void execute(Workbook workbook, Sheet sheet, Row row, Cell cell, CellStyle style) {
        if (isDebug) Log4j.info("设置表头: " +  cell.getRowIndex() +" , "+cell.getColumnIndex() + " , " + headName);

        cell.setCellValue(headName);

    }
}
