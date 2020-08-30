package bottle.tcps.backup.server;

import java.io.File;

public interface Callback{
    void complete(File file);
}
