package bottle.tcps.ftc.imps;

/**
 * Created by user on 2017/11/23.
 */
public class FTCProtocol {
//    public static final String C_FILE_BACKUP_QUEST = EncryptUtil.encryption("客户端文件同步请求");
//    public static final String S_FILE_BACKUP_QUEST_ACK = EncryptUtil.encryption("服务端文件同步请求回执");
//
//    public static final String C_FILE_BACKUP_TRS_START = EncryptUtil.encryption("客户端文件传输到服务端-开始");
//    public static final String S_FILE_BACKUP_NOTIFY_STREAM = EncryptUtil.encryption("服务端通知客户端传输流");
//
//    public static final String C_FILE_BACKUP_TRS_END = EncryptUtil.encryption("客户端文件传输到服务端-结束");
//    public static final String S_FILE_BACKUP_TRS_OVER = EncryptUtil.encryption("服务端文件传输事件完成");
//
//    public static final String C_FILE_LIST_VERIFY_QUEST = EncryptUtil.encryption("客户端文件列表确认请求");
//    public static final String S_FILE_LIST_VERIFY_ACK = EncryptUtil.encryption("服务端文件列表确认回执");


    public static final String C_FILE_BACKUP_QUEST = "客户端-同步文件-请求";
    public static final String S_FILE_BACKUP_QUEST_ACK = "服务端-同步文件-请求回执";

    public static final String C_FILE_BACKUP_TRS_START = "客户端-传输文件-请求";
    public static final String S_FILE_BACKUP_NOTIFY_STREAM = "服务端-传输文件-请求回执-进入等待客户端传输文件字节数据处理状态";

    public static final String C_FILE_BACKUP_TRS_END = "客户端-文件传输结束-通知";
    public static final String S_FILE_BACKUP_TRS_OVER = "服务端-文件传输结束完成-响应";

}
