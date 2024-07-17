package TestTask.Commands;

import TestTask.DataClasses.Student;

import java.util.List;

/**
 * Interface for each command that extends AbstractCommand
 */
public interface Icommand {
    String getDescription();

    String getName();

    List<Student> execute(String[] args);

}

