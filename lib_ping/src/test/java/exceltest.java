import bottle.excel.ExcelElement;
import bottle.excel.ExcelHead;
import bottle.excel.LeePoiExcel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: leeping
 * @Date: 2020/5/27 11:53
 */
public class exceltest {
    public static void main(String[] args) throws Exception {
        /* 添加头 */
        List<ExcelElement> headList = new ArrayList<>();
            headList.add(new ExcelHead(0,"姓名").setWidth(50).setFontName("黑体"));
            headList.add(new ExcelHead(1,"年龄").setWidth(30).setFontName("黑体"));
            headList.add(new ExcelHead(2,"记录时间").setWidth(60).setFontName("黑体"));
            headList.add(new ExcelHead(3,"金额").setWidth(30).setFontName("黑体"));
            headList.add(new ExcelHead(4,"时间戳字符串").setWidth(70).setFontName("黑体"));

        /* 添加数据 */
        List<Object[]> lines = new ArrayList<>();
            lines.add(new Object[]{"李世平",15,"2020-12-01 23:59:59.0",56.21f,System.currentTimeMillis()+""+System.currentTimeMillis()});
            lines.add(new Object[]{"李世平2",80,"2020-11-01 23:59:59",0.66d,System.currentTimeMillis()});
            lines.add(new Object[]{"李世平2",80,"2020-11-01",0.66d,System.currentTimeMillis()});
            lines.add(new Object[]{"李世平2",80,"23:59:59",0.66d,System.currentTimeMillis()});

            /*使用*/
        try(FileOutputStream out = new FileOutputStream("C:\\Users\\user\\Desktop\\zzzz.xls")){
            HSSFWorkbook workbook =  LeePoiExcel.export(new HSSFWorkbook(),"测试",headList,lines);
            workbook.write(out);
        }

    }
}
