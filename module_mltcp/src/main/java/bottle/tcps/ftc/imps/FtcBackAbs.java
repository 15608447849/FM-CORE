package bottle.tcps.ftc.imps;


import bottle.tcps.FTCLog;
import bottle.util.FileTool;


import java.io.File;

/**
 * Created by user on 2017/11/23.
 */
public class FtcBackAbs {
    //目录
    protected final String directory;

    public FtcBackAbs(String directory) {
        this.directory = FileTool.replaceFileSeparatorAndCheck(directory,null, File.pathSeparator);
        File file = new File(directory);
        if (!file.exists()){
            if (!file.mkdirs()){
                throw new IllegalArgumentException("无法创建目录: "+ directory);
            }
        }
        FTCLog.info("FTC 根目录: " + directory);
    }
    public String getDirectory() {
        return directory;
    }

}
