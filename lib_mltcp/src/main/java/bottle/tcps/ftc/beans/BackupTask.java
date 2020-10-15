package bottle.tcps.ftc.beans;

import java.net.InetSocketAddress;

public class BackupTask {

    private BackupFile backupFile;

    private InetSocketAddress serverAddress;

    private int loopCount = 0;

    public BackupTask( InetSocketAddress serverAddress,BackupFile backupFile) {
        this.backupFile = backupFile;
        this.serverAddress = serverAddress;
    }

    public BackupFile getBackupFile() {
        return backupFile;
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public BackupTask incLoopCount() {
        this.loopCount++;
        return this;
    }

    @Override
    public String toString() {
        return backupFile.getFullPath() + " >> " + serverAddress;
    }
}
