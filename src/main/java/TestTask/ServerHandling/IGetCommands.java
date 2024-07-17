package TestTask.ServerHandling;

import java.io.IOException;
import java.nio.file.Files;

public interface IGetCommands {
//    File getFile()
    ResponseStatus getFileFromServer(String remotePath, String localPath) throws IOException;
}
