package TestTask.Commands;

import java.util.*;

/**
 * The CommandFactory class is responsible for creating and providing instances of commands based on the CommandType.
 */
public class CommandFactory {
    private static final Map<CommandType, AbstractCommand> commandMap = new HashMap<>();
    //init all possibleCommands to the map
    static {
        commandMap.put(CommandType.ADD_STUDENT, new AddStudentCommand());
        commandMap.put(CommandType.GET_BY_NAME, new GetListByNameCommand());
        commandMap.put(CommandType.GET_BY_ID, new GetStudentByIdCommand());
        commandMap.put(CommandType.REMOVE_BY_ID, new RemoveStudentById());
        commandMap.put(CommandType.DISCONNECT, new Disconnect());
    }

    /**
     * Returns the command instance associated with the specified command type.
     *
     * @param commandType the type of the command to retrieve
     * @return the command instance associated with the specified command type
     * @throws IllegalArgumentException if the command type is invalid
     */
    public static AbstractCommand getCommand(CommandType commandType) {
        AbstractCommand command = commandMap.get(commandType);
        if (command == null) {
            throw new IllegalArgumentException("Invalid command type: " + commandType);
        }
        return command;
    }

    /**
     * @return the map of all command types and their corresponding command instances
     */
    public static Map<CommandType, AbstractCommand> getMapCommands() {
        return commandMap;
    }
}
