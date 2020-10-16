package bottle.excel;

import bottle.util.Log4j;
import org.apache.poi.hssf.usermodel.*;

import static bottle.excel.LeePoiExcel.isDebugLog;

/**
 * @Author: leeping
 * @Date: 2020/5/27 12:02
 */
public abstract class ExcelElement {
    final int x;
    final int y;

    public ExcelElement(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int width;//行宽
    short height;//行高

    public ExcelElement setWidth(int width){
        this.width = width * 256;
        return  this;
    }
    public ExcelElement setHeight(short height){
        this.height = height;
        return this;
    }

    short fontHeight;//字号

    public ExcelElement setFontHeight(short fontHeight){
        this.fontHeight = fontHeight;
        return this;
    }

    String fontName;//字体
    public ExcelElement setFontName(String fontName){
        this.fontName = fontName;
        return this;
    }

    void execute(HSSFWorkbook workbook, HSSFSheet sheet){
        if (workbook == null || sheet == null) return;

        HSSFRow row = sheet.getRow(y);
        if (row == null) row = sheet.createRow(y);
        HSSFCell cell = row.getCell(x);
        if (cell == null)  cell = row.createCell(x);

        /*设置字体*/
        HSSFFont font = workbook.createFont();
        boolean isSetFont = false;
        if (fontName!=null){
            font.setFontName(fontName);
            isSetFont = true;
        }
        if (fontHeight>0){
            font.setFontHeightInPoints(fontHeight);
            isSetFont = true;
        }
        if (isSetFont){
            HSSFCellStyle style = workbook.createCellStyle();
            style.setFont(font);
            cell.setCellStyle(style);
        }

        /*设置单元格宽度*/
        if (width>0){
            sheet.setColumnWidth(x,width);
        }
        if (height>0){
            row.setHeight(height);
        }
        execute(workbook,sheet,row,cell);
    }

    abstract void execute(HSSFWorkbook workbook, HSSFSheet sheet,HSSFRow row,HSSFCell cell);

}
