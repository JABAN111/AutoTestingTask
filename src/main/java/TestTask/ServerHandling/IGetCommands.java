package TestTask.ServerHandling;

import java.nio.file.Files;

public interface IGetCommands {
//    File getFile()
    Files getFileFromServer(String path);
}
