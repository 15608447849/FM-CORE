package bottle.excel;

import org.apache.poi.ss.usermodel.*;

/**
 * @Author: leeping
 * @Date: 2020/5/27 12:02
 */
public abstract class ExcelElement {
    public static boolean isDebug = false;

    final int x;
    final int y;



    public ExcelElement(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int width;//行宽
    int height;//行高

    public ExcelElement setWidth(int width){
        this.width = width;
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


    void _execute(Workbook workbook, Sheet sheet, CellStyle style){
        if (workbook == null || sheet == null) return;

        Row row = sheet.getRow(y);
        if (row == null) row = sheet.createRow(y);
        Cell cell = row.getCell(x);
        if (cell == null)  cell = row.createCell(x);

        /* 设置单元格 宽度,高度 https://blog.csdn.net/aosica321/article/details/72320050 */
        if (width <= 0) width = 30;
        if (height <= 0) height = 15;
        sheet.setColumnWidth(x,width * 256);
        row.setHeight((short) (height * 20));

        execute(workbook,sheet,row,cell,style);

        if (style!=null) cell.setCellStyle(style);
    }

    abstract void execute(Workbook workbook, Sheet sheet,Row row,Cell cell,CellStyle style);

}
