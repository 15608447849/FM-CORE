package framework.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class IceCallerObserver {

    public interface IceCallerSubscribeI {
        void onBefore(Object caller, Method callMethod);
        void onAfter(Object caller, Method callMethod);
        void onError(Object caller, Method callMethod, Throwable t);
    }

    private static final List<IceCallerSubscribeI> list = new ArrayList<>();

    public static void addSubscribe(IceCallerSubscribeI subscribe){
        list.add(subscribe);
    }

    public static void onBefore(Object caller, Method callMethod) {
        for (IceCallerSubscribeI it : list) {
            try{
                it.onBefore(caller,callMethod);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void onAfter(Object caller, Method callMethod) {
        for (IceCallerSubscribeI it : list) {
            try{
                it.onAfter(caller,callMethod);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void onError(Object caller, Method callMethod, Throwable t) {
        for (IceCallerSubscribeI it : list) {
            try{
                it.onError(caller, callMethod, t);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
