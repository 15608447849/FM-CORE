package jdbc.define.option;
import jdbc.define.exception.JDBCException;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/5 10:05
 */
public class Page{
    private int pageIndex;
    private int pageSize;
    private int totalItems;
    private String selectTotalSql;
    private boolean isUse;
    private List<Object[]> selectTotalResult;

    private void initParam(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.isUse = !(this.pageIndex==0 && this.pageSize==0);
    }

    public Page(int pageIndex, int pageSize) {
       initParam(pageIndex,pageSize);
    }

    public Page(int[] array) {
        int pageIndex,pageSize;
        if (array == null || array.length < 2) {
            pageIndex = 0;
            pageSize  = 0;
        } else {
            pageIndex = array[0];
            pageSize = array[1];
        }
        initParam(pageIndex,pageSize);
    }

    public Page(){
        initParam(0,0);
    }

    public List<Object[]> getSelectTotalResult() {
        return selectTotalResult;
    }

    /* 数据库 分页 */
    public static String exeDb(JDBCSessionFacade jdbcSessionFacade, String sql, Object[] params, Page page) {
        if (page!=null && page.isUse()) {
            sql = page.execute(jdbcSessionFacade,sql,params);
        }
        return sql;
    }

    /* 内存分页 */
    static <T extends Object> List<T> exeMem(Page page, List<T> result) {
        if (page!=null){
            return page.executeResult(result);
        }
        return result;
    }

    private boolean isUse() {
        return isUse;
    }

    /* 设置查询总条数sql */
    public void setSelectTotalSql(String selectTotalSql) {
        this.selectTotalSql = selectTotalSql;
    }

    /* 数据库分页 */
    private String execute(DaoApi op,String sql,Object[] params){

        if (pageIndex <= 0 || pageSize <= 0 ) return sql; //不分页

        int lastIndex = sql.lastIndexOf(";");
        if (lastIndex>0) sql = sql.substring(0,lastIndex);

        //查询全部条数
        if (selectTotalSql ==null){
            selectTotalSql = "SELECT COUNT(0) FROM ( " + sql + " ) TEMP";
        }

        selectTotalResult = op.query(selectTotalSql, params,null);
        selectTotalSql = null;

        if (selectTotalResult.size() != 1){
            throw new JDBCException("【数据库分页异常】\n\t" + selectTotalSql+"\n\t"+ Arrays.toString(params));
        }

        this.totalItems = Integer.parseInt(String.valueOf(selectTotalResult.get(0)[0]));

        int index = (pageIndex - 1) * pageSize ;
        int number = pageSize;

        return sql + " LIMIT "+ index+ "," + number;
    }



    /* 内存分页 */
    public  <T extends Object> List<T> executeResult(List<T> result) {
        totalItems = result.size();
        int start = (pageIndex - 1) * pageSize;
        if (start<0) start = 0;
        if (start>totalItems) return result;
        if ( pageSize <= 0 ) return result;
        int end = start + pageSize;
        if (end>=totalItems) end = totalItems;
        return result.subList(start,end);
    }

}
