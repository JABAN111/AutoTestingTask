package TestTask.ServerHandling;

import java.io.IOException;

public interface IGetCommands {
    ResponseStatus getFileFromServer(String remotePath, String localPath) throws IOException;
}
