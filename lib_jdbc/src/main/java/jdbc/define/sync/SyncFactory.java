package jdbc.define.sync;

import bottle.objectref.ObjectRefUtil;
import bottle.properties.abs.ApplicationPropertiesBase;
import bottle.properties.annotations.PropertiesFilePath;
import bottle.properties.annotations.PropertiesName;
import bottle.util.GoogleGsonUtil;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.JDBCSessionFacade;

/**
 * @Author: leeping
 * @Date: 2020/9/1 12:58
 */
@PropertiesFilePath("/database.properties")
public class SyncFactory {

    @PropertiesName("sync.execute")
    private static String syncImpClassPath = SyncImpDefault.class.getName();

    private static SyncI syncI = null;

    static {
        ApplicationPropertiesBase.initStaticFields(SyncFactory.class);
        if(syncImpClassPath != null){
            try {
                syncI = (SyncI) ObjectRefUtil.createObject(syncImpClassPath);
            } catch (Exception e) {
                JDBCLogger.error("无法创建数据库同步操作对象("+syncImpClassPath+")",e);
            }
        }
    }

    public static void syncHandle(JDBCSessionFacade op, SyncTask task){
        if (syncI!=null){
            boolean isSuccess = syncI.executeTask(op,task);
            if (!isSuccess){
                JDBCLogger.print("同步数据库失败\n"+GoogleGsonUtil.javaBeanToJson(task));
            }
        }
    }
}
