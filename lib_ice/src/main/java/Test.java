import bottle.util.GoogleGsonUtil;
import framework.server.IReqArray;
import framework.server.IReqJson;
import framework.server.IReqKey;
import framework.server.IceSessionContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Test {
    private static final class S{
        private String name;

        public S(String name) {
            this.name = name;
        }
    }
    private static final class Bean{
        private int a;
        private int b;
        private List<S> sList;

        public Bean(int a, int b, List<S> sList) {
            this.a = a;
            this.b = b;
            this.sList = sList;
        }
    }


    public void FuncTs(IceSessionContext context,
                       @IReqJson List<HashMap<String,Bean>> list ,
                       @IReqKey("123") String SOID,
                       @IReqArray String[] args,
                       @IReqArray String[] args2,
                       @IReqKey("Obj.list") List<Bean> beans){


    }

    public String FuncTs2(@IReqArray(25) String[] arrays){
        return GoogleGsonUtil.javaBeanToJson(arrays) ;
    }

//    public String FuncTs3(Map<String,Object> b){
//        return GoogleGsonUtil.javaBeanToJson(b) ;
//    }



    public static void main(String[] args) throws Exception{
        List<S> slist = new ArrayList<>();
        HashMap<String,Bean> map = new HashMap<>();
        slist.add(new S("梨视频"));
        map.put("A1",new Bean(10,20,slist));
        slist.add(new S("梨视频2"));
        map.put("A2",new Bean(50,20,slist));
        List<HashMap<String,Bean>> list = new ArrayList<>();
        list.add(map);
        list.add(map);

//        String json  = new Test().FuncTs(list);
//        System.out.println(json);

//        HashMap<Object, Object> mm = new HashMap<>();
//        mm.put("111122,,s",map);
//        mm.put("111122,,3",list);
//        mm.put(map,list);
//        String json2 = GoogleGsonUtil.javaBeanToJson(mm);
//
//        System.out.println(json2);
//        Map<Object,Object> m2 = GoogleGsonUtil.string2Map(json2);
//
//        System.out.println(m2.get(map));



        String key = "other.sub.data";
        String[] keyArr = key.split("\\.");
        System.out.println(Arrays.toString(keyArr));


        if (true) return ;

        Class<?> callClass = Class.forName(Test.class.getCanonicalName());
        Method[] methods = callClass.getDeclaredMethods();

        for (Method method : methods) {
//            if (!method.getName().equals("FuncTs")) continue;

            System.out.println("方法: " +  method);
            Parameter[] parameters = method.getParameters();
//            Parameter[] parameters = method.getParameter();

            Class[] paramTypes = method.getParameterTypes();
            Type[] paramGenericType = method.getGenericParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> classType = paramTypes[i];
                Type genericType = paramGenericType[i];

//                System.out.println("****" + String[].class.isAssignableFrom(classType));

                System.out.println("\t形参: " + parameters[i]);
                Annotation[] annotations = parameters[i].getAnnotations();
                for (Annotation annotation : annotations) {
                    System.out.println("\t\t形参注解: " + annotation );

                    if (IReqArray.class.isAssignableFrom(annotation.annotationType())){
                        IReqArray apiRequestArray = (IReqArray) annotation;
                        System.out.println("\t\t\t\t" + apiRequestArray.value());
                    }


                }
                System.out.println("\t参数 " + classType.getName());
                System.out.println("\t泛型 " + genericType);




                /*
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType type = (ParameterizedType) genericType;
                    Type[] actualTypeArguments = type.getActualTypeArguments();

                    for (Type actualTypeArgument : actualTypeArguments) {
//                        Class typeArgClass = (Class) actualTypeArgument;
                        System.out.print("\t泛型 " + actualTypeArgument);

                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType type2 = (ParameterizedType) actualTypeArgument;
                            Type[] actualTypeArguments2 = type2.getActualTypeArguments();

                            for (Type actualTypeArgument2 : actualTypeArguments2) {
                                System.out.print("\t泛型 " + actualTypeArgument2);                            }

                        }

                    }
                }
                */
            }




        }
    }

}
