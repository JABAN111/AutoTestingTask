package TestTask.ServerHandling;


public interface IServerHandling extends IPutCommands,IGetCommands{
    ResponseStatus sendCommandWithoutArgs(String command);
    ResponseStatus sendCommandWithArgs(String command, String[] args);
//    String responseFromServer();
    ResponseStatus disconnect();
    ResponseStatus authorization(String login, String password);
}
