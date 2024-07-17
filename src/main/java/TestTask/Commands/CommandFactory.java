package TestTask.Commands;

import java.util.*;

public class CommandFactory {
    private static final Map<CommandType, AbstractCommand> commandMap = new HashMap<>();

    static {
        commandMap.put(CommandType.ADD_STUDENT, new AddStudentCommand());
        commandMap.put(CommandType.GET_BY_NAME, new GetListByNameCommand());
        commandMap.put(CommandType.GET_BY_ID, new GetStudentByIdCommand());
        commandMap.put(CommandType.REMOVE_BY_ID, new RemoveStudentById());
        commandMap.put(CommandType.DISCONNECT, new Disconnect());
    }

    public static AbstractCommand getCommand(CommandType commandType) {
        AbstractCommand command = commandMap.get(commandType);
        if (command == null) {
            throw new IllegalArgumentException("Invalid command type: " + commandType);
        }
        return command;
    }
    public static Map<CommandType,AbstractCommand> getMapCommands(){
        return commandMap;
    }
}
