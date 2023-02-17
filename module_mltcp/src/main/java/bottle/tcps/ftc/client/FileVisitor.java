package bottle.tcps.ftc.client;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by user on 2017/11/24.
 *
 */
public class FileVisitor extends SimpleFileVisitor<Path>{
    private static final String TAG = "文件夹遍历器";
    private final FtcBackupClient ftcBackupClient;
    private final Path home;

    public FileVisitor(FtcBackupClient ftcBackupClient) {
        this.ftcBackupClient = ftcBackupClient;
        home = Paths.get(ftcBackupClient.getDirectory());
    }

    @Override
    public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {
        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) {
        boolean isAdd = !ftcBackupClient.isFilterSuffixFile(filePath.toFile());
        if (isAdd){
            ftcBackupClient.addBackupFile(filePath.toFile());
        }
        return FileVisitResult.CONTINUE;
    }

    synchronized void startVisitor() throws IOException{
        Files.walkFileTree(home, this);
    }

}
