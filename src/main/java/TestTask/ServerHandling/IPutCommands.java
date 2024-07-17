package TestTask.ServerHandling;

import java.io.IOException;

public interface IPutCommands {
    ResponseStatus sendFile(String pathToLocalFile) throws IOException;
}
