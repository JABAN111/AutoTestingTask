package TestTask.Commands;

import TestTask.Commands.Exception.InvalidArgs;
import TestTask.DataClasses.Student;

import java.util.List;

public class AddStudentCommand extends AbstractCommand {
    public AddStudentCommand() {
        super(CommandType.ADD_STUDENT, "Добавление студента ( id генерируется автоматически)");
    }

    @Override
    public List<Student> execute(String[] args) throws InvalidArgs {
        if (args.length != 2) {
            throw new InvalidArgs("You should write only student name");
        }

        if(args[1] == null || (args[1] = args[1].trim()).isEmpty()){
            throw new InvalidArgs("Student name cannot be empty");
        }

        super.collectionManager.getStudentList().add(new Student(args[1]));

        return super.collectionManager.getStudentList();
    }
}
