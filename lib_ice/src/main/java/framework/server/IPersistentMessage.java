package framework.server;

import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/4/10 10:37
 */
public interface IPersistentMessage {

    /** 存储消息到数据库*/
    long waitSendMessage(IPMessage message);

    /** 转换消息 */
    String convertMessage(IPMessage message);

    /** 改变数据库指定消息的状态 */
    void sendMessageSuccess(IPMessage message) ;

    /** 检查指定标识是否存在离线消息 */
    List<IPMessage> getOfflineMessageFromIdentityName(String identityName);
}
