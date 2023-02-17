package framework.server;

import Ice.Communicator;

public interface ScanApplicationClassCallback {

    void findClass(Class<?> classType) throws Exception;
    void executeClass(String serverName, String rpcGroupName, Communicator communicator);
    void destroy();
    ScanApplicationClassCallback next();

    class Adapter implements ScanApplicationClassCallback{
        @Override
        public void findClass(Class<?> classType) throws Exception {

        }

        @Override
        public void executeClass(String serverName, String rpcGroupName, Communicator communicator) {

        }

        @Override
        public void destroy() {

        }

        @Override
        public ScanApplicationClassCallback next() {
            return null;
        }
    }

}
