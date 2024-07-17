package TestTask.Managers;

import TestTask.Commands.CommandFactory;
import TestTask.Commands.CommandType;
import TestTask.DataClasses.Student;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private CommandManager(){}
    public static List<Student> executor(CommandType commandType, String[] args){
        return CommandFactory.getCommand(commandType).execute(args);
    }
}
