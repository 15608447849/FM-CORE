import bottle.excel.ExcelElement;
import bottle.excel.ExcelHead;
import bottle.excel.LeePoiExcel;
import bottle.util.Log4j;
import bottle.util.StringUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: leeping
 * @Date: 2020/5/27 11:53
 */
public class exceltest {
    public static void main(String[] args) throws Exception {
        ExcelElement.isDebug = true;
        /* 添加头 */
        List<ExcelElement> headList = new ArrayList<>();
        headList.add(new ExcelHead(0,"姓名"));
        headList.add(new ExcelHead(1,"年龄"));
        headList.add(new ExcelHead(2,"记录时间"));
        headList.add(new ExcelHead(3,"金额"));
        headList.add(new ExcelHead(4,"时间戳字符串"));

        /* 添加数据 */
        List<Object[]> lines = new ArrayList<>();
        for (int i = 0;i<200000;i++){
            lines.add(new Object[]{"李世平",i,"2020-12-01 23:59:59.0",0.001f,System.currentTimeMillis()});
        }
        System.out.println(lines.size());

            /*使用*/
        try(FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\excel_test.xlsx")){
            LeePoiExcel.export(out,headList,lines);
        }

//        try(ByteArrayOutputStream pos = new ByteArrayOutputStream()){
//            LeePoiExcel.export(pos,headList,lines);
//            System.out.println("OK");
//        }

    }
}
