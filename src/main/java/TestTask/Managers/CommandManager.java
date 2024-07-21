package TestTask.Managers;

import TestTask.Commands.CommandFactory;
import TestTask.Commands.CommandType;
import TestTask.Commands.Exception.InvalidArgs;
import TestTask.DataClasses.Student;

import java.util.List;

/**
 * The CommandManager class is responsible for executing commands based on their type.
 * It uses the CommandFactory to retrieve the appropriate command instance.
 */
public class CommandManager {
    private CommandManager() {}

    /**
     * Executes a command based on the given command type and arguments.
     *
     * @param commandType the type of the command to execute
     * @param args the arguments for the command. First element of <code>args[0]</code> include the name of the command
     * @return a list of students resulting from the command execution
     */
    public static List<Student> executor(CommandType commandType, String[] args) throws InvalidArgs {
        return CommandFactory.getCommand(commandType).execute(args);
    }
}
