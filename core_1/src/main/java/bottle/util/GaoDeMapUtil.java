package bottle.util;



import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;

/**
 * @Author: leeping
 * @Date: 2019/4/12 15:29
 */
public class GaoDeMapUtil {

//    private static String[] apiKeys = new String[]{
//            "c59217680590515b7c8369ff5e8fe124",//李世平
//            "b6ec1c997ac8ef26afd11b21cae2003e",//章广
//            "da009b945cd0372558e28172cba81a9c",//王兵富
//            "03b4343da153d4b5f5653badd0f767a0",//廖舟
//            "76094d7529daad32f548b48feedcdea3",//罗阳
//            "5ef21f0bcc58c9d8ac6371d7bb92323b",//小金
//            "aae4e6d95c62d7baa4701842d26836fb",//刘洋
//            "0e57c61aa53e9788c3d8ba5a2d023375",//小皮
//            "9266204ef40b756e0d3d71a83f4153ad"//祝震
//    };


    private static final List<String> apiKeys = new ArrayList<String>(){
        {
            //李世平
            add("c59217680590515b7c8369ff5e8fe124");
        }
    };

    public static void addKeys(String... keys){
        apiKeys.addAll(Arrays.asList(keys));
    }

    private static int currentApiIndex = 0;

    private static String getApiKey(){
        if (currentApiIndex >=apiKeys.size()) return null;
        return apiKeys.get(currentApiIndex);
    }

    public static class Point{
        public double lng;
        public double lat;

        public Point(double lng, double lat){
            this.lng = lng;
            this.lat = lat;
        }

        public Point(Double[] doubles){
            this(doubles[0],doubles[1]);
        }

        /**
         * 经度(longitude)在前，纬度(latitude)在后
         * @param pointStr "112.984200,28.205628"
         */
        public Point(String pointStr){
            this( parseDouble(pointStr.split(",")[0]), parseDouble(pointStr.split(",")[1]));
        }

        @Override
        public String toString() {
            return "{" + "lng=" + lng + ", lat=" + lat + '}';
        }
    }

    private static final class DataBean {
        String location;
        String adcode;
        String polyline;
        String country;
        Object province;
        Object city;
        Object district;
        Object township;
    }

    private static final class JsonBean{
        int status;
        int infocode;
        String info;
        List<DataBean> geocodes;
        String province;
        String city;
        List<DataBean> districts;
    }

    private final static String[] errorTips = new String[]{
            "INVALID_USER_KEY",
            "SERVICE_NOT_AVAILABLE",
            "INSUFFICIENT_PRIVILEGES",
            "USER_KEY_RECYCLED",
            "INVALID_REQUEST",
            "USER_DAILY_QUERY_OVER_LIMIT",
            "DAILY_QUERY_OVER_LIMIT",
            "ACCESS_TOO_FREQUENT"
    };

    private static void tryMoveApiKeyToNext(JsonBean bean){
        if (bean!=null){
            if (bean.status == 0){
                if (bean.info!=null){
                    for (String errinfo : errorTips){
                        if (bean.info.equals(errinfo)){
                            currentApiIndex++;
                            return;
                        }
                    }
                }

            }
        }

    }

    /**
     * 获取地址信息 index,当前执行次数
     */
    private static DataBean addressConvertLatLon(String address,int index){
        try {
            StringBuffer sb = new StringBuffer( "https://restapi.amap.com/v3/geocode/geo?");
            HashMap<String,String> map = new HashMap<>();
            map.put("key",getApiKey());
            map.put("address", URLEncoder.encode(address.trim(),"UTF-8"));
            map.put("city","");
            map.put("batch","false");
            map.put("sig","");
            map.put("output","JSON");
            map.put("callback","");
            String result = new HttpRequest().bindParam(sb,map).getRespondContent();
            if(StringUtil.isEmpty(result)) throw  new NullPointerException();
            JsonBean jsonBean = GoogleGsonUtil.jsonToJavaBean(result,JsonBean.class);
            tryMoveApiKeyToNext(jsonBean);
            if (jsonBean == null || jsonBean.status != 1 || jsonBean.geocodes.size() == 0) throw  new NullPointerException();
            return jsonBean.geocodes.get(0);
        } catch (Exception e) {
            index++;
            if (index<3) return addressConvertLatLon(address,index);
        }
        return null;
    }
    /**
     * 解析地址
     * @param address
     * @return
     */
    public static Map<String,String> addressResolution(String address){
        /*
         * java.util.regex是一个用正则表达式所订制的模式来对字符串进行匹配工作的类库包。它包括两个类：Pattern和Matcher Pattern
         *    一个Pattern是一个正则表达式经编译后的表现模式。 Matcher
         *    一个Matcher对象是一个状态机器，它依据Pattern对象做为匹配模式对字符串展开匹配检查。
         *    首先一个Pattern实例订制了一个所用语法与PERL的类似的正则表达式经编译后的模式，然后一个Matcher实例在这个给定的Pattern实例的模式控制下进行字符串的匹配工作。
         */
        String regex=
                "(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市)?" +
                "(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)?" +
                "(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?" +
                 "(?<town>[^区]+区|.+镇)?" +
                "(?<village>.*)";
        Matcher m= Pattern.compile(regex).matcher(address);
        String province=null,city=null,county=null,town=null,village=null;

        while(m.find()){
            if (province==null){
                province=m.group("province");
            }

            if(city == null){
                city=m.group("city");
            }

            if (county == null){
                county=m.group("county");
            }

            if (town == null){
                town=m.group("town");
            }

            if(village == null){
                village=m.group("village");
            }
        }

        Map<String,String> row=new LinkedHashMap<>();
            row.put("province",province==null?"":province.trim());
            row.put("city", city==null?"":city.trim());
            row.put("county", county==null?"":county.trim());
            row.put("town", town==null?"":town.trim());
            row.put("village", village==null?"":village.trim());
        return row;
    }


    //获取地区边界信息
    private static DataBean areaPolyline(String address,int index){
        try{
            String temp = address;
            if (index<4){
                Map<String,String>  map = addressResolution(address);
                if (!StringUtil.isEmpty(map.get("province"))){
                    temp = map.get("province");
                }
                if (!StringUtil.isEmpty(map.get("city"))){
                    temp = map.get("city");
                }
                if (!StringUtil.isEmpty(map.get("county"))){
                    temp = map.get("county");
                }
            }else{
                DataBean d = addressConvertLatLon(address, 0);
                temp = d.adcode;
            }

            temp = URLEncoder.encode(temp,"UTF-8");
            StringBuffer sb = new StringBuffer( "https://restapi.amap.com/v3/config/district?");
            HashMap<String,String> map = new HashMap<>();
            map.put("key",getApiKey());
            map.put("keywords",temp);
            map.put("subdistrict","0");
            if (index>=4) map.put("filter",temp);
            map.put("extensions","all");
            String result = new HttpRequest().bindParam(sb,map).getRespondContent();
            if(StringUtil.isEmpty(result)) throw  new NullPointerException();
            JsonBean jsonBean = GoogleGsonUtil.jsonToJavaBean(result,JsonBean.class);
            tryMoveApiKeyToNext(jsonBean);
            if (jsonBean == null || jsonBean.status != 1 || jsonBean.districts.size() == 0) throw  new NullPointerException();
            return jsonBean.districts.get(0);
        } catch (Exception e) {
            index++;
            if (index<6) return areaPolyline(address,index);
        }
        return null;
    }

    /**
     * 获取地址经纬度
     */
    public static  Point addressConvertLatLon(String address){
        return new Point(Objects.requireNonNull(addressConvertLatLon(address, 0)).location);
    }

    /**
     * 获取地址边界点Point
     */
    public static List<List<Point>> areaPolyline(String address){
            return handleSourcePolyline(Objects.requireNonNull(areaPolyline(address, 0)).polyline);
    }

    /**
     * ip转地址信息
     */
    private static String[] ipConvertAddress(String ip,int index){
        try {
            StringBuffer sb = new StringBuffer( "https://restapi.amap.com/v3/ip?");
            HashMap<String,String> map = new HashMap<>();
            map.put("key",getApiKey());
            map.put("ip",ip);
            String result = new HttpRequest().bindParam(sb,map).getRespondContent();
            if(StringUtil.isEmpty(result)) throw  new NullPointerException();
            JsonBean jsonBean = GoogleGsonUtil.jsonToJavaBean(result,JsonBean.class);
            if (jsonBean == null || jsonBean.status != 1) throw  new NullPointerException();
            tryMoveApiKeyToNext(jsonBean);
            return new String[]{jsonBean.province,jsonBean.city};
        } catch (NullPointerException e) {
            index++;
            if (index<3) return ipConvertAddress(ip,index);
        }
        return null;
    }


    /**
     * ip转换地址信息
     */
    public static String[] ipConvertAddress(String ip){
        return ipConvertAddress(ip.trim(),0);
    }


    /**
     * 地址转地址信息
     */
    private static String[] addressStrConvertAddress(String addressStr,int index,boolean pass3Lv){
        try {
            if (StringUtil.isEmpty(addressStr)) return null;
            StringBuffer sb = new StringBuffer( "https://restapi.amap.com/v3/geocode/geo?");
            HashMap<String,String> map = new HashMap<>();
            map.put("key",getApiKey());
            map.put("address",addressStr);

            String result = new HttpRequest().bindParam(sb,map).getRespondContent();

            if(StringUtil.isEmpty(result)) throw  new NullPointerException();
            JsonBean jsonBean = GoogleGsonUtil.jsonToJavaBean(result,JsonBean.class);
            if (jsonBean == null || jsonBean.status != 1 || jsonBean.geocodes==null || jsonBean.geocodes.isEmpty()) throw  new NullPointerException();
            tryMoveApiKeyToNext(jsonBean);
            DataBean d = jsonBean.geocodes.get(0);
            if (!(d.province instanceof String)) throw  new NullPointerException();
            String province = d.province.toString();

            String city = "省直辖";

            if (d.city instanceof String){
                city = d.city.toString();
            }
            String district = null;

            if (d.district instanceof String){
                district = d.district.toString();
            }

            if (StringUtil.isEmpty(district)){
                if (d.township instanceof String){
                    district = d.township.toString();
                }
            }

            if (StringUtil.isEmpty(district)){
                Map<String, String> _map = GaoDeMapUtil.addressResolution(addressStr);
                district =  _map.get("town");
                if (StringUtil.isEmpty(district)){
                    district = _map.get("county");
                }
            }

            if (StringUtil.isEmpty(district)){
                if (!pass3Lv){
                    throw new NullPointerException();
                }else{
                    district = "/";
                }
            }

            return new String[]{province,city,district};
        } catch (NullPointerException e) {
            index++;
            if (index<1) return addressStrConvertAddress(addressStr,index,pass3Lv);
        }
        return null;
    }

    /**
     * 地址转地址信息
     */
    public static String[] addressStrConvertAddress(String addressStr){
       return addressStrConvertAddress(addressStr,false);
    }

    public static String[] addressStrConvertAddress(String addressStr,boolean pass3L){
        return addressStrConvertAddress(addressStr,0,pass3L);
    }

    //判断点在线上
    private static boolean checkPointOnLine(Point2D.Double point, Point2D.Double pointS, Point2D.Double pointD) {
        Line2D line = new Line2D.Double(pointS,pointD);
        return line.contains(point);
    }

    /**一个点是否在多边形内或线上
     * @param point  要判断的点
     * @param polygon 组成的顶点坐标集合
     * @return true 包含
     */
    private static boolean checkPointOnRange(Point2D.Double point, List<Point2D.Double> polygon) {
//        if (polygon.contains(point)) return true;

//        //判断是否在线上
//        for (int i= 0 ; i < polygon.size() ; i++){
//            Point2D.Double pointS ;
//            Point2D.Double pointD ;
//            pointS = polygon.get(i);
//            pointD = polygon.get( i+1 == polygon.size() ? 0 : i+1);
//            if (checkPointOnLine(point,pointS,pointD)) return true;
//        }

        java.awt.geom.GeneralPath peneralPath = new java.awt.geom.GeneralPath();
        Point2D.Double first = polygon.get(0);
        // 通过移动到指定坐标（以双精度指定），将一个点添加到路径中
        peneralPath.moveTo(first.x, first.y);
        polygon.remove(0);
        for (Point2D.Double d : polygon) {
            // 通过绘制一条从当前坐标到新指定坐标（以双精度指定）的直线，将一个点添加到路径中。
            peneralPath.lineTo(d.x, d.y);
        }
        // 将几何多边形封闭
        peneralPath.lineTo(first.x, first.y);
        peneralPath.closePath();
        // 测试指定的 Point2D 是否在 Shape 的边界内。
        return peneralPath.contains(point);
    }



    private static String pointJsonToPoint2DJson(Object pointOrPointList){
        return Objects.requireNonNull(GoogleGsonUtil.javaBeanToJson(pointOrPointList)).replace("lng","x").replace("lat","y");
    }

    /**
     * @param singe 单个点
     * @param points 多边形的点线性集合
     * @return true - 包含在多边形内
     */
    public static boolean checkPointOnRange(Point singe,List<Point> points) {
        Point2D.Double point = GoogleGsonUtil.jsonToJavaBean(pointJsonToPoint2DJson(singe),Point2D.Double.class);
        List<Point2D.Double> polygon = GoogleGsonUtil.json2List(pointJsonToPoint2DJson(points),Point2D.Double.class);
        return checkPointOnRange(point,polygon);
    }

    // [lon1,lat1,lon2,lat2] -> List:[lng,lat]
    private static List<Double[]> pointArray2ListDouble(Double[] doubles){
        List<Double[]> list = new ArrayList<>();
        for (int i = 0 ; i<doubles.length ;i+=2){
            list.add(new Double[]{doubles[i],doubles[i+1]});
        }
        return list;
    }

    //[x,y;x,y]  -> list<double[]>
    private static String pointArray2ListDouble(String json){
       return GoogleGsonUtil.javaBeanToJson(pointArray2ListDouble(Objects.requireNonNull(GoogleGsonUtil.jsonToJavaBean(json, Double[].class))));
    }

    // list<double[]> -> List<Point>
    private static List<Point> listArrayJsonToPointJson(String json){
        //[[112.938888,28.228272],[112.988412,28.223999],[112.975598,28.220521]]
        try {
            List<Double[]> list = GoogleGsonUtil.json2List(json,Double[].class);
            List<Point> points = new ArrayList<>();
            if (list!=null && list.size()>0) {
                for(Double[] doubles : list){
                    points.add(new Point(doubles));
                }
            }
            return points;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //[{"lng":112.919358,"lat":28.219294}] -> [[112.975598,28.220521]]
    public static String pointJsonToListArrayJson(String json){
        try {
            List<Point> points = GoogleGsonUtil.json2List(json,Point.class);
            if (points.size() > 0){
                List<Double[]> list = new ArrayList<>();
                for (Point point : points) {
                    list.add(new Double[]{point.lng, point.lat});
                }
                return GoogleGsonUtil.javaBeanToJson(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //处理 | , -> {[x,y] [x,y]} -> LIST<POINT>[]
    private static List<List<Point>> handleSourcePolyline(String line){
        String[] arr = line.split("\\|");
        List<List<Point>> listArr = new ArrayList<>();
        for (String temp : arr){
            List<Point> list = listArrayJsonToPointJson(pointArray2ListDouble("[" + temp + "]"));
            listArr.add(list);
        }
        return listArr;
    }


    private static String getSysClipboardText() throws Exception{
        String ret = "";
        Clipboard sysClip = Toolkit.getDefaultToolkit()
                .getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);
        if(clipTf == null){
            return null;
        }
        // 检查内容是否是文本类型
        if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return (String) clipTf.
                    getTransferData(DataFlavor.stringFlavor);
        }
        return ret;
    }

    private static void setClipboardString(String text) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装文本内容
        Transferable trans = new StringSelection(text);
        // 把文本内容设置到系统剪贴板
        clipboard.setContents(trans, null);
    }

    public static void main(String[] args) throws Exception {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("地理位置逆编码");
        frame.setSize(200,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JLabel label = new JLabel("请直接复制ctrl+C, 自动解析为省市区地理信息");
        frame.getContentPane().add(label);
        new Thread(){
            String currentText = "";
            @Override
            public void run() {

                while (true){
                    try {
                        String sysClipboardText =  getSysClipboardText();
                        if (sysClipboardText!=null&&sysClipboardText.length()>0 && !currentText.equals(sysClipboardText)){

                            currentText = sysClipboardText;
                            String[] arr = GaoDeMapUtil.addressStrConvertAddress(URLEncoder.encode(currentText,"UTF-8"),true);
                            System.out.println(sysClipboardText+" -> "+ Arrays.toString(arr));
                            if (arr!=null){
                                currentText = Arrays.toString(arr);
                                label.setText(currentText);
                                setClipboardString(currentText);
                            }else{
                                label.setText("复制内容,无法解析");
                            }
                        }
                        Thread.sleep(500L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        frame.setVisible(true);
    }


}
