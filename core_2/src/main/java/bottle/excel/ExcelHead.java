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
    public final String styleFormat; // 单元格格式 https://blog.csdn.net/qq_34624315/article/details/81364330


    public ExcelHead(int index,String headName,String styleFormat) {
        super(index,0);
        this.headName = headName==null? "" : headName;
        this.styleFormat = styleFormat == null? "@" : styleFormat;
    }

    public ExcelHead(int index,String headName) {
        super(index,0);

        String[] arr = headName.split(",");
        String a;
        String b;
        if(arr.length == 1){
            a = headName;
            b = "@";// 文本格式
        }else{
            a = arr[0];
            b = arr[1];
        }
        this.headName = a;
        this.styleFormat = b;

    }

    @Override
    void execute(Workbook workbook, Sheet sheet, Row row, Cell cell, CellStyle style) {
        if (isDebug) Log4j.info("设置表头=(" +  cell.getRowIndex() +","+cell.getColumnIndex() + ") , 表头名=" + headName+" 样式="+ styleFormat);
        cell.setCellValue(headName);
    }
}
