package bottle.excel;

import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.util.Log4j;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.RETURN_BLANK_AS_NULL;

/**
 * @Author: leeping
 * @Date: 2020/5/27 10:48
 */
public class LeePoiExcel {



    /******************************************************************* 导出 ********************************************************/



    private static void export_execute(Workbook workbook,List<ExcelElement> heads, List<Object[]>  datas){
        //写入表头
        int sheetIndex = 0;

        Sheet sheet = workbook.createSheet("sheet"+sheetIndex);

        int rowsIndex = 0;
        if (heads!=null){
            //创建head单元格
            CellStyle headStyle = workbook.createCellStyle();
            setHeadStyle(workbook,headStyle,"黑体",14);
            for (ExcelElement head : heads){
                head._execute(workbook,sheet,headStyle);
            }
            rowsIndex = 1;
        }

        //创建content单元格样式
        CellStyle contentStyle = workbook.createCellStyle();
        setContentStyle(workbook,contentStyle);

        long t = System.currentTimeMillis();

        //循环行
        for (Object[] rows : datas){
            if (ExcelElement.isDebug) Log4j.info(sheet.getSheetName() + "当前执行: "+ rowsIndex);
            //循环列
            for (int x=0; x<rows.length;x++){
                try{
                    //写入内容
                    new ExcelItem(x,rowsIndex, rows[x])._execute(workbook,sheet,contentStyle);
                }catch (Exception e){
                    Log4j.error("写入excel异常",e);
                    return;
                }
            }
            rowsIndex++;

            if(workbook instanceof HSSFWorkbook){
                if (rowsIndex>=65535){
                    sheetIndex++;
                    sheet = workbook.createSheet("sheet"+sheetIndex);
                    rowsIndex=0;
                }
            }
        }

        if (ExcelElement.isDebug) Log4j.info("执行时长: "+ (System.currentTimeMillis() - t));
    }


    /* 设置内容单元格样式 */
    private static void setContentStyle(Workbook workbook, CellStyle style) {
        // 设置日期样式
        DataFormat format= workbook.createDataFormat();
        style.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /* 设置表头单元格样式 */
    private static void setHeadStyle(Workbook workbook,CellStyle style,String fontName,int fontHeight) {

        /*设置单元格 字体类型,大小*/
        Font font = workbook.createFont();
        font.setBold(true);

        if (fontName!=null){
            font.setFontName(fontName);
        }

        if (fontHeight>0){
            font.setFontHeightInPoints((short) fontHeight);
        }

        style.setFont(font);

        style.setBorderLeft(BorderStyle.THIN);//左边框
        style.setBorderRight(BorderStyle.THIN);//右边框
//        style.setBorderTop(BorderStyle.THIN);//上边框
//        style.setBorderBottom(BorderStyle.THIN); //下边框

        /* https://blog.csdn.net/tolcf/article/details/48346697 */
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    //导出
    public static void export(OutputStream out, List<ExcelElement> heads, List<Object[]> datas) throws IOException {
        //创建Excel工作薄对象
//        Workbook workbook = new HSSFWorkbook();
//        Workbook  workbook = new XSSFWorkbook();
        Workbook  workbook = new SXSSFWorkbook();
        exportSetExcelType(workbook,out,heads,datas);
    }

    //导出
    public static void exportSetExcelType(Workbook workbook , OutputStream out, List<ExcelElement> heads, List<Object[]> datas) throws IOException {
        export_execute(workbook,heads,datas);
        workbook.write(out);
    }

    // 导出
    public static void export(OutputStream out,String[] headsArr,List<Object[]> datas) throws IOException{
        List<ExcelElement> heads = new ArrayList<>();
        for(int i = 0; i<headsArr.length;i++){
            heads.add(new ExcelHead(i,headsArr[i]));
        }
        export(out,heads,datas);
    }

    /******************************************************************* 导入 ********************************************************/
    public interface ImportExcelHandleI{
        InputStream convertInputStream(String url) throws IOException;
    }

    private static class ImportExcelHandleDefault implements ImportExcelHandleI{
        @Override
        public InputStream convertInputStream(String url) throws IOException{
            URI uri = URI.create(url);

            if (uri.getScheme().equals("http") || uri.getScheme().equals("https")){
                URL _url = uri.toURL();
                HttpURLConnection conn = (HttpURLConnection)_url.openConnection();
                conn.setConnectTimeout(30*1000);
                return conn.getInputStream();
            }
            if (uri.getScheme().equals("file")){
                return new FileInputStream(uri.getPath());
            }
            return null;
        }
    }
    // 对具体实体类赋值
    // 使用注解标识出每个字段取值
   public static <Type>  List<Type> importExcel(String url,String sheetName, boolean isExistHead, ImportExcelHandleI handle,Class<Type> classBeanType){
        if (classBeanType == null) throw new IllegalArgumentException("需要配置数据实体类");

        List<Type> result = new ArrayList<>();

       Workbook wb = getWorkbook(url,handle);
       if (wb == null)  throw new IllegalArgumentException("无法读取EXCEL数据内容");

       Sheet sheet = wb.getSheet(sheetName);//获取工作副本
       //获取第一行
       int currentIndex = sheet.getFirstRowNum();

       //获取总行数
       int totalIndex = sheet.getLastRowNum();

       String[] headArr = null; // 用于对实例数据查询指定名称的下标
       if (isExistHead){
           // 获取第一行 head
           Row headRow = sheet.getRow(currentIndex++);
           if(headRow == null) throw new IllegalArgumentException("excel不正确");

           headArr = new String[headRow.getLastCellNum()];

           //赋值 head
           for (int i = 0; i < headArr.length; i++) {
               Cell cell = headRow.getCell(i,RETURN_BLANK_AS_NULL);
               headArr[i] = getCellValue(cell);
           }
       }

       while (currentIndex<totalIndex){
           Row row = sheet.getRow(currentIndex++);
           Type bean = genCellDataToBean(row,headArr,classBeanType);
           result.add(bean);
       }
        return result;
    }

    private static <Type> Type genCellDataToBean(Row row, String[] headArr, Class<Type> classBeanType) {
        //获取类所有字段
        Field[] fields = classBeanType.getDeclaredFields();
        try {
            //创建实例类
            Constructor<Type> constructor = classBeanType.getDeclaredConstructor();
            constructor.setAccessible(true);
            Type bean = constructor.newInstance();
            for (Field field : fields){

                field.setAccessible(true);
                //获取注解
                ImportFieldName importFieldName = field.getAnnotation(ImportFieldName.class);

                if (importFieldName == null) continue;

                int index = importFieldName.index();
                if (index == -1){
                    String headName = importFieldName.name();
                    if (headName.equals("")) continue;

                    //遍历头,获取下标
                    for (int i = 0; i< headArr.length;i++){
                        if (headArr[i].equals(headName)){
                            index = i;
                        }
                    }
                }

                if (index == -1) continue;

                Cell cell = row.getCell(index,RETURN_BLANK_AS_NULL);
                String value =  getCellValue(cell);
                ApplicationPropertiesBase.assignmentByType(bean,field,value);

            }
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Workbook getWorkbook(String url, ImportExcelHandleI handle) {
        if (handle == null){
            handle = new ImportExcelHandleDefault();
        }
        Workbook wb = null;
        try(InputStream in = handle.convertInputStream(url)) {
            if (in == null) throw new IllegalArgumentException("无法读取内容,URL = "+ url);
            wb = new XSSFWorkbook(in);
        }catch (Exception ignored){

            try(InputStream in = handle.convertInputStream(url)) {
                wb =   new HSSFWorkbook(in);
            }catch (Exception ignored2){
            }
        }
        return wb;
    }

    /**
     * 根据excel单元格类型获取excel单元格值
     * @param cell
     * @return
     */
    private static String getCellValue(Cell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            CellType curType = cell.getCellTypeEnum();
            if (curType.equals(CellType.NUMERIC)){
                short format = cell.getCellStyle().getDataFormat();
                if(format == 14 || format == 31 || format == 57 || format == 58){ 	//excel中的时间格式
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    double value = cell.getNumericCellValue();
                    Date date = DateUtil.getJavaDate(value);
                    cellvalue = sdf.format(date);
                }
                // 判断当前的cell是否为Date
                else if (HSSFDateUtil.isCellDateFormatted(cell)) {  //先注释日期类型的转换，在实际测试中发现HSSFDateUtil.isCellDateFormatted(cell)只识别2014/02/02这种格式。
                    // 如果是Date类型则，取得该Cell的Date值           // 对2014-02-02格式识别不出是日期格式
                    Date date = cell.getDateCellValue();
                    DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                    cellvalue= formater.format(date);
                } else { // 如果是纯数字
                    // 取得当前Cell的数值
                    cellvalue = NumberToTextConverter.toText(cell.getNumericCellValue());

                }
            }
            else if (curType.equals(CellType.STRING)){
                // 取得当前的Cell字符串
                cellvalue = cell.getStringCellValue().replaceAll("'", "''");
            }
        }
        return cellvalue.trim();
    }

}
