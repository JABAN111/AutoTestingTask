package TestTask.Commands;

import TestTask.Commands.Exception.InvalidArgs;
import TestTask.DataClasses.Student;

import java.util.List;

/**
 * Interface for each command that extends AbstractCommand
 */
public interface Icommand {
    String getDescription();
    List<Student> execute(String[] args) throws InvalidArgs;
}

