package drug.erp.commom;

import bottle.mq_active.MQPoolUtil;
import bottle.util.GoogleGsonUtil;

import java.util.HashMap;


public class BDSUtil {

    /** 门店信息变化 */
    public static String BDS_COMPANY = "BDS_COMPANY";
    /** 商品信息变化 */
    public static String BDS_DRUG = "BDS_DRUG";
    /** 订单信息变化 */
    public static String BDS_ORDER = "BDS_ORDER";
    /** 订单退款信息变化 */
    public static String BDS_ORDER_REFUND = "BDS_ORDER_REFUND";

    /** 数据变化时调用, 包括 数据新增,修改,删除 的业务节点, 在数据库变化后调用 */
    public static void notifyBusinessDataChange(String busKey,String tableOID ){
        HashMap<String,String> map = new HashMap<>();
        map.put("dataCapClassKey",busKey);
        map.put("mainTableOID",tableOID);
        MQPoolUtil.sendMessage("BusinessDataCollectStruct_MQ", GoogleGsonUtil.javaBeanToJson(map));
    }
}
