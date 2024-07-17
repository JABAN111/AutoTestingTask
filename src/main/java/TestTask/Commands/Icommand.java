package TestTask.Commands;

import TestTask.DataClasses.Student;

import java.util.List;

/**
 * Interface for each command that extends AbstractCommand
 */
public interface Icommand {
    String getDescription();

    CommandType getType();

    List<Student> execute(String[] args);

}

