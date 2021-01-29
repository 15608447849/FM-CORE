import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: leeping
 * @Date: 2020/9/8 15:08
 */
public class main {

    public static void main(String[] args) throws Exception{

//        LClassLoader loader = new LClassLoader(
//                "C:\\IDEAWORK\\FM-CORE\\core-lib-drug\\build\\libs",
//                "C:\\IDEAWORK\\FM-CORE\\core-lib-drug\\build\\classes\\java\\main"
//        );
//        Class<?>  clazz =
//                loader.findClass("lee.zp.DyLoadTest");

//        while (true){
//            try {
//                LClassLoader loader = new LClassLoader(
//                        "C:\\IDEAWORK\\FM-CORE\\core-lib-drug\\build\\libs",
//                        "C:\\IDEAWORK\\FM-CORE\\core-lib-drug\\build\\classes\\java\\main"
//                );
////        Class<?>  clazz =
//                loader.findClass("lee.zp.DyLoadTest");
//
//                Object obj = Class.forName("lee.zp.DyLoadTest").newInstance();
//                Method instanceExecute = obj.getClass().getMethod("instanceExecute");
//                instanceExecute.invoke(obj);
//                obj = null;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            Thread.sleep(1000);
//        }


        String str = "{\"instanceJson\":\"{\\\"oid\\\": \\\"1601286057000001013\\\", \\\"coid\\\": \\\"1571801414000001010\\\", \\\"type\\\": 1, \\\"areac\\\": \\\"430000000\\\", \\\"cname\\\": \\\"一块医药\\\", \\\"level\\\": 0, \\\"orgid\\\": \\\"1577783266000001009\\\", \\\"areacs\\\": [\\\"430000000\\\"], \\\"compid\\\": \\\"536871810\\\", \\\"docnum\\\": \\\"K003362\\\", \\\"header\\\": {\\\"docId\\\": \\\"K003362\\\", \\\"bizOrg\\\": \\\"1586526218000001009\\\", \\\"tempId\\\": \\\"0\\\", \\\"writer\\\": \\\"1583462229000001008\\\", \\\"applyOrg\\\": \\\"0\\\", \\\"writeDatetime\\\": \\\"2020-09-28 17:51:52\\\"}, \\\"mobile\\\": \\\"65231245789\\\", \\\"nature\\\": 3, \\\"address\\\": \\\"吉林省珲春市靖和街环亚山城小区29栋104号门市\\\", \\\"cstatus\\\": 0, \\\"orgName\\\": \\\"销售部\\\", \\\"payMode\\\": 0, \\\"taxType\\\": 0, \\\"areaName\\\": \\\"湖南省\\\", \\\"fullName\\\": \\\"珲春市祥云医药连锁有限公司\\\", \\\"payModes\\\": 0, \\\"settList\\\": [], \\\"workname\\\": \\\"李超\\\", \\\"creatTime\\\": \\\"17:40:57\\\", \\\"salesType\\\": 1, \\\"shortName\\\": \\\"HCSXY\\\", \\\"taxNumber\\\": \\\"91222404081913329T\\\", \\\"changeDate\\\": \\\"2020-09-29\\\", \\\"changeTime\\\": \\\"09:43:21\\\", \\\"companyOid\\\": \\\"1571801414000001010\\\", \\\"createDate\\\": \\\"2020-09-28\\\", \\\"natureName\\\": \\\"零售药店\\\", \\\"regcapital\\\": \\\"0\\\", \\\"wareHAreac\\\": \\\"0\\\", \\\"companyBack\\\": [{\\\"oid\\\": \\\"1601343075000001012\\\", \\\"compOid\\\": \\\"1601286057000001013\\\", \\\"cstatus\\\": 256, \\\"operatingType\\\": 2, \\\"depositBankCard\\\": \\\"22001686438055010256\\\", \\\"depositBankName\\\": \\\"中国建设银行股份有限公司珲春支行\\\"}], \\\"companyCard\\\": [{\\\"oid\\\": \\\"1601342308000001011\\\", \\\"cardId\\\": \\\"\\\", \\\"compOid\\\": \\\"1601286057000001013\\\", \\\"cusType\\\": 0, \\\"cardName\\\": \\\"营业执照\\\", \\\"cardType\\\": 44, \\\"issueDate\\\": \\\"\\\", \\\"mediaIndex\\\": 0, \\\"statusCode\\\": \\\"-1\\\", \\\"statusName\\\": \\\"正常\\\", \\\"validUntil\\\": \\\"\\\", \\\"cardDictType\\\": 10, \\\"cardTypeName\\\": \\\"营业执照\\\", \\\"oldvalidUntil\\\": \\\"\\\", \\\"operatingType\\\": 2, \\\"cardVerifiedType\\\": 0, \\\"cardFailureDayNum\\\": \\\"0\\\", \\\"oldyearlyInspection\\\": \\\"\\\", \\\"cardYearFailureDayNum\\\": \\\"0\\\"}, {\\\"oid\\\": \\\"1601342405000001011\\\", \\\"cardId\\\": \\\"\\\", \\\"compOid\\\": \\\"1601286057000001013\\\", \\\"cusType\\\": 0, \\\"cardName\\\": \\\"药品经营许可证\\\", \\\"cardType\\\": 37, \\\"issueDate\\\": \\\"\\\", \\\"mediaIndex\\\": 0, \\\"statusCode\\\": \\\"-1\\\", \\\"statusName\\\": \\\"正常\\\", \\\"validUntil\\\": \\\"\\\", \\\"cardDictType\\\": 10, \\\"cardTypeName\\\": \\\"药品经营许可证\\\", \\\"oldvalidUntil\\\": \\\"\\\", \\\"operatingType\\\": 2, \\\"cardVerifiedType\\\": 0, \\\"cardFailureDayNum\\\": \\\"0\\\", \\\"oldyearlyInspection\\\": \\\"\\\", \\\"cardYearFailureDayNum\\\": \\\"0\\\"}, {\\\"oid\\\": \\\"1601342600000001011\\\", \\\"cardId\\\": \\\"\\\", \\\"compOid\\\": \\\"1601286057000001013\\\", \\\"cusType\\\": 0, \\\"cardName\\\": \\\"GSP证书\\\", \\\"cardType\\\": 16, \\\"issueDate\\\": \\\"\\\", \\\"mediaIndex\\\": 0, \\\"statusCode\\\": \\\"-1\\\", \\\"statusName\\\": \\\"正常\\\", \\\"validUntil\\\": \\\"\\\", \\\"cardDictType\\\": 10, \\\"cardTypeName\\\": \\\"GSP证书\\\", \\\"oldvalidUntil\\\": \\\"\\\", \\\"operatingType\\\": 2, \\\"cardVerifiedType\\\": 0, \\\"cardFailureDayNum\\\": \\\"0\\\", \\\"oldyearlyInspection\\\": \\\"\\\", \\\"cardYearFailureDayNum\\\": \\\"0\\\"}, {\\\"oid\\\": \\\"1601343339000001011\\\", \\\"cardId\\\": \\\"\\\", \\\"compOid\\\": \\\"1601286057000001013\\\", \\\"cusType\\\": 0, \\\"cardName\\\": \\\"经营范围委托书(采购)\\\", \\\"cardType\\\": 30, \\\"issueDate\\\": \\\"\\\", \\\"mediaIndex\\\": 0, \\\"statusCode\\\": \\\"-1\\\", \\\"statusName\\\": \\\"正常\\\", \\\"validUntil\\\": \\\"\\\", \\\"cardDictType\\\": 10, \\\"cardTypeName\\\": \\\"经营范围委托书(采购)\\\", \\\"oldvalidUntil\\\": \\\"\\\", \\\"operatingType\\\": 2, \\\"cardVerifiedType\\\": 0, \\\"cardFailureDayNum\\\": \\\"0\\\", \\\"oldyearlyInspection\\\": \\\"\\\", \\\"cardYearFailureDayNum\\\": \\\"0\\\"}, {\\\"oid\\\": \\\"1601343462000001011\\\", \\\"cardId\\\": \\\"\\\", \\\"compOid\\\": \\\"1601286057000001013\\\", \\\"cusType\\\": 0, \\\"cardName\\\": \\\"身份证资料\\\", \\\"cardType\\\": 21, \\\"issueDate\\\": \\\"\\\", \\\"mediaIndex\\\": 0, \\\"statusCode\\\": \\\"-1\\\", \\\"statusName\\\": \\\"正常\\\", \\\"validUntil\\\": \\\"\\\", \\\"cardDictType\\\": 10, \\\"cardTypeName\\\": \\\"身份证资料\\\", \\\"oldvalidUntil\\\": \\\"\\\", \\\"operatingType\\\": 2, \\\"cardVerifiedType\\\": 0, \\\"cardFailureDayNum\\\": \\\"0\\\", \\\"oldyearlyInspection\\\": \\\"\\\", \\\"cardYearFailureDayNum\\\": \\\"0\\\"}, {\\\"oid\\\": \\\"1601342531000001011\\\", \\\"cardId\\\": \\\"\\\", \\\"compOid\\\": \\\"1601286057000001013\\\", \\\"cusType\\\": 0, \\\"cardName\\\": \\\"食品经营许可证\\\", \\\"cardType\\\": 23, \\\"issueDate\\\": \\\"\\\", \\\"mediaIndex\\\": 0, \\\"statusCode\\\": \\\"-1\\\", \\\"statusName\\\": \\\"正常\\\", \\\"validUntil\\\": \\\"\\\", \\\"cardDictType\\\": 10, \\\"cardTypeName\\\": \\\"食品经营许可证\\\", \\\"oldvalidUntil\\\": \\\"\\\", \\\"operatingType\\\": 2, \\\"cardVerifiedType\\\": 0, \\\"cardFailureDayNum\\\": \\\"0\\\", \\\"oldyearlyInspection\\\": \\\"\\\", \\\"cardYearFailureDayNum\\\": \\\"0\\\"}, {\\\"oid\\\": \\\"1601342696000001011\\\", \\\"cardId\\\": \\\"\\\", \\\"compOid\\\": \\\"1601286057000001013\\\", \\\"cusType\\\": 0, \\\"cardName\\\": \\\"第二类医疗器械经营备案凭证\\\", \\\"cardType\\\": 6, \\\"issueDate\\\": \\\"\\\", \\\"mediaIndex\\\": 0, \\\"statusCode\\\": \\\"-1\\\", \\\"statusName\\\": \\\"正常\\\", \\\"validUntil\\\": \\\"\\\", \\\"cardDictType\\\": 10, \\\"cardTypeName\\\": \\\"第二类医疗器械经营备案凭证\\\", \\\"oldvalidUntil\\\": \\\"\\\", \\\"operatingType\\\": 2, \\\"cardVerifiedType\\\": 0, \\\"cardFailureDayNum\\\": \\\"0\\\", \\\"oldyearlyInspection\\\": \\\"\\\", \\\"cardYearFailureDayNum\\\": \\\"0\\\"}], \\\"createwName\\\": \\\"贺思寒\\\", \\\"legalPerson\\\": \\\"严志祥\\\", \\\"manageLabel\\\": 0, \\\"payModeName\\\": \\\"\\\", \\\"chargePerson\\\": \\\"严志祥\\\", \\\"checkoutList\\\": [{\\\"oid\\\": \\\"1604990165000001019\\\", \\\"billAddr\\\": \\\"吉林省珲春市靖和街环亚山城小区29栋104号门市\\\", \\\"billBank\\\": \\\"中国建设银行股份有限公司珲春支行\\\", \\\"billName\\\": \\\"珲春市祥云医药连锁有限公司\\\", \\\"billPhone\\\": \\\"04337507222\\\", \\\"tacNumber\\\": \\\"91222404081913329T\\\", \\\"billAaccount\\\": \\\"22001686438055010256\\\"}], \\\"customerType\\\": 6, \\\"invoiceLimit\\\": \\\"0\\\", \\\"workflowLock\\\": 0, \\\"businessScope\\\": \\\"55472372\\\", \\\"companyZLDict\\\": [], \\\"customerState\\\": \\\"0\\\", \\\"firstDatetime\\\": \\\"\\\", \\\"invoiceLimits\\\": [], \\\"lastOrderTime\\\": \\\"\\\", \\\"otherAttrCode\\\": \\\"0\\\", \\\"salesTypeName\\\": \\\"终端\\\", \\\"settlementOid\\\": \\\"1601343116000001019\\\", \\\"transportMode\\\": 0, \\\"wareHouseList\\\": [{\\\"oid\\\": \\\"1601286789000001020\\\", \\\"areac\\\": \\\"430000000\\\", \\\"areacs\\\": [\\\"430000000\\\"], \\\"address\\\": \\\"吉林省珲春市靖和街环亚山城小区29栋104号门市\\\", \\\"cstatus\\\": \\\"0\\\", \\\"areaName\\\": \\\"湖南省\\\", \\\"comp_oid\\\": \\\"1601286057000001013\\\", \\\"contacts\\\": \\\"张丽丽\\\", \\\"Telephone\\\": \\\"04337567245\\\", \\\"operation\\\": 1, \\\"is_default\\\": \\\"1\\\"}], \\\"buyerWorkerOid\\\": \\\"1583462005000001008\\\", \\\"otherAttrCodes\\\": [], \\\"updateDatetime\\\": \\\"\\\", \\\"warehousePhone\\\": \\\"04337567245\\\", \\\"workflowStatus\\\": 1, \\\"changeWorkerOid\\\": \\\"0\\\", \\\"createWorkerOid\\\": \\\"1583462229000001008\\\", \\\"depositBankCard\\\": \\\"22001686438055010256\\\", \\\"depositBankName\\\": \\\"中国建设银行股份有限公司珲春支行\\\", \\\"historyTaskList\\\": [{\\\"taskOid\\\": \\\"0\\\", \\\"examineOrg\\\": \\\"销售审批节点\\\", \\\"examineDate\\\": \\\"2020-09-28 17:53:19\\\", \\\"examineUser\\\": \\\"贺思寒\\\", \\\"auditOpinion\\\": \\\"审核通过\\\", \\\"examineState\\\": 20}, {\\\"taskOid\\\": \\\"0\\\", \\\"examineOrg\\\": \\\"质管员审批节点\\\", \\\"examineDate\\\": \\\"2020-09-29 09:43:42\\\", \\\"examineUser\\\": \\\"刘纯\\\", \\\"auditOpinion\\\": \\\"审核合格\\\", \\\"examineState\\\": 20}, {\\\"taskOid\\\": \\\"0\\\", \\\"examineOrg\\\": \\\"质管经理审批节点\\\", \\\"examineDate\\\": \\\"2020-09-29 10:14:09\\\", \\\"examineUser\\\": \\\"刘思民\\\", \\\"auditOpinion\\\": \\\"审核合格\\\", \\\"examineState\\\": 20}, {\\\"taskOid\\\": \\\"0\\\", \\\"examineOrg\\\": \\\"质量负责人审批节点\\\", \\\"examineDate\\\": \\\"2020-09-29 10:16:24\\\", \\\"examineUser\\\": \\\"陈芳\\\", \\\"auditOpinion\\\": \\\"同意开户\\\", \\\"examineState\\\": 20}], \\\"controllingSales\\\": 3, \\\"customerTypeName\\\": \\\"普通\\\", \\\"extendpersonName\\\": \\\"许涛\\\", \\\"fullNameInitials\\\": \\\"HCSXY\\\", \\\"natureEnterprise\\\": 0, \\\"customerStateName\\\": \\\"正常\\\", \\\"prescriptionRange\\\": \\\"15\\\", \\\"qualityReputation\\\": \\\"1\\\", \\\"transportModeName\\\": \\\"\\\", \\\"warehouseContacts\\\": \\\"张丽丽\\\", \\\"enterpriseWorkerOid\\\": \\\"\\\", \\\"newCustomerJsonInfo\\\": \\\"\\\", \\\"extendpersonWorkerOid\\\": \\\"1599487746000001008\\\", \\\"compCusWorlflowStatusMsg\\\": 1}\"}";
        System.out.println(str);
        System.out.println(getDecodeJSONStr(str));



    }

    /**
     * 将奇数个转义字符变为偶数个
     * @param s
     * @return
     */
    public static String getDecodeJSONStr(String s){
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString()
                .replaceAll("%","%25")
                .replaceAll("\\+","%2B")
                .replaceAll("\\s+","%20")
                .replaceAll("/","%2F")
                .replaceAll("\\?","%3F")
                .replaceAll("#","%23")
                .replaceAll("&","%26")
                .replaceAll("=","%26")
                ;

    }
}
