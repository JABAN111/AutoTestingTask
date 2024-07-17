package TestTask.ServerHandling;


import java.io.IOException;

public interface IServerHandling extends IPutCommands,IGetCommands{
    ResponseStatus sendCommandWithoutArgs(String command) throws IOException;
    ResponseStatus sendCommandWithArgs(String command, String[] args) throws IOException;
    ResponseStatus disconnect();
    ResponseStatus authorization(String login, String password) throws AuthorizationFailed;
}
