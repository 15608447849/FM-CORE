package framework.server;

import Ice.Communicator;

/**
 * @Author: leeping
 * @Date: 2019/4/11 16:42
 */
public interface Initializer {
    void initialization(String serverName, String groupName,Communicator communicator);
    int priority();
    void onDestroy();
}
