package bottle.tcps.backup.client;

import bottle.tcps.backup.beans.TimerBean;
import bottle.tcps.backup.beans.Action;

import java.util.HashMap;

/**
 * Created by user on 2017/11/27.
 */
public class FBCTimeTaskOp{
    private final HashMap<String, TimerBean> timeTaskMap;
    private final FtcBackupClient client;
    public FBCTimeTaskOp(FtcBackupClient client) {
        this.timeTaskMap = new HashMap<>();
        this.client = client;
    }

    /**
     * 设置定时任务
     */
    public final void setTime(String timeStr){
        try{
            TimerBean tb = new TimerBean(timeStr, (Action<TimerBean>) timerBean -> {
//                Log.e("定时任务 - " + timeStr +" 开始");
                //启动 客户端遍历文件夹
                client.ergodicDirectory();

                if(timerBean.getType() == TimerBean.FIXED_POINT){
                    //定点
                    //从队列中移除
                    timerBean.cancel();
                    timeTaskMap.remove(timerBean.getDateStr());
                }
            });
            if (timeTaskMap.containsKey(timeStr)){
                timeTaskMap.get(timeTaskMap).cancel();
            }
            tb.schedule();
            timeTaskMap.put(timeStr,tb);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }











}
