package bottle.excel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: leeping
 * @Date: 2020/5/27 10:48
 */
public class LeePoiExcel {

    public static boolean isDebugLog = false;

    public static List<ExcelElement> convertItem(int startIndex,List<Object[]> datas){
        List<ExcelElement> list = new ArrayList<>();

        for (int y = 0 ; y<datas.size();y++){
            Object[] rows = datas.get(y);
            for (int x=0; x<rows.length;x++){
                list.add(new ExcelItem(x,y+startIndex,rows[x]));
            }
        }
        return list;
    }

    public static HSSFWorkbook export(HSSFWorkbook workbook, String sheetName , List<ExcelElement> heads,List<Object[]> datas){
            //创建Excel工作薄对象
        if (workbook == null){
            workbook = new HSSFWorkbook();
        }
        if (sheetName == null){
            sheetName = "sheet1";
        }
        //创建Excel工作表对象
        HSSFSheet sheet = workbook.createSheet(sheetName);

        int startIndex = 0;
        if (heads!=null){
            Iterator<ExcelElement> it = heads.iterator();
            while (it.hasNext()){
                ExcelElement head = it.next();
                it.remove();
                head.execute(workbook,sheet);
            }
            startIndex = 1;
        }
        List<ExcelElement> items = convertItem(startIndex,datas);
        Iterator<ExcelElement> it = items.iterator();
        while (it.hasNext()){
            ExcelElement head = it.next();
            it.remove();
            head.execute(workbook,sheet);
        }
        return workbook;
    }

    public static HSSFWorkbook export(String sheetName , List<ExcelElement> heads,List<Object[]> datas){
       return export(new HSSFWorkbook(),sheetName,heads,datas);
    }

    public static void export(OutputStream out,String sheetName , List<ExcelElement> heads, List<Object[]> datas) throws IOException {
        HSSFWorkbook workbook =  LeePoiExcel.export(new HSSFWorkbook(),sheetName,heads,datas);
        workbook.write(out);
    }



}
