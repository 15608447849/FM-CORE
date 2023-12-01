package framework.server;



import bottle.objectref.ObjectPoolManager;
import bottle.util.GoogleGsonUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
public class Result{

   private Result(){

   }

   static Result create() {
      try {
         return (Result) ObjectPoolManager.createObject(Result.class);
      } catch (Exception e) {
        return new Result();
      }
   }


    interface CODE{
      int INTERCEPT = -2;
      int FAIL = -1;
      int ERROR = 0;
      int SUCCESS = 200;
   }

   private int code = CODE.FAIL;

   private String message;

   private String text;

   private Object data;

   private String error;

   private HashMap<String,Object> map;

   // 分页信息
   private Object pageInfo;

   // 拓展对象
   private Object expand;

   //是否成功
   public boolean isSuccess(){
      return code == CODE.SUCCESS;
   }

   //是否拦截
   public boolean isIntercept(){
      return code == CODE.INTERCEPT;
   }

   public Result success(String message,Object data){
      this.code = CODE.SUCCESS;
      this.message = message;
      this.data = data;
      return this;
   }

   public Result success(Object data){
      this.code = CODE.SUCCESS;
      this.data = data;
      return this;
   }

   public Result success(String message){
      return success(message,null);
   }

   public Result fail(String message){
      return fail(message,null);
   }

   public Result fail(String message,Object data){
      this.code = CODE.FAIL;
      this.message = message;
      this.data = data;
      return this;
   }

   //添加参数
   public Result addParam(String key,Object value){
      if (map == null) map = new HashMap<>();
      map.put(key,value);
      return this;
   }

   public Result setMessage(String message){
      this.message = message;
      return this;
   }

   public Result setData(Object data){
      this.data = data;
      return this;
   }

   public Result setExpand(Object expand){
      this.expand = expand;
      return this;
   }

   //拦截
   public Result intercept(String cause){
      this.code = CODE.INTERCEPT;
      this.message = cause;
      return this;
   }

   //拦截
   public Result intercept(int code , String cause){
      this.code = code;
      this.message = cause;
      return this;
   }

   //错误
   public Result error(String msg,String error) {
      this.code = CODE.ERROR;
      this.message = msg;
      this.error = error;
      return this;
   }

   /* 设置查询后的分页信息 */
   public Result setPageInfo(Object page) {
     this.pageInfo = page;
      return this;
   }

   public Object getData() {
      return data;
   }

   public Result setText(Object text) {
      this.code = CODE.SUCCESS;
      this.text = String.valueOf(text);
      return this;
   }

   public String getMessage(){return message; }

   public String getText() {
      return text;
   }

   public <T> T getData(Class<T> cls){
      try {
         String temp = GoogleGsonUtil.javaBeanToJson(data);
         return GoogleGsonUtil.jsonToJavaBean(temp,cls);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   public <T> T getData(Type type){
      try {
         String temp = GoogleGsonUtil.javaBeanToJson(data);
         return GoogleGsonUtil.jsonToJavaBean(temp,type);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   public int getCode() {
      return code;
   }

   public void clear() {
      code = CODE.FAIL;
      message = null;
      text = null;
      data = null;
      error = null;
      map = null;
      pageInfo = null;
      expand = null;
   }

}
