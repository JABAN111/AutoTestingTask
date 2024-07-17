package TestTask.Commands;

import TestTask.DataClasses.Student;

import java.util.List;

public class AddStudentCommand extends AbstractCommand {
    public AddStudentCommand() {
        super(CommandType.ADD_STUDENT, "Добавление студента ( id генерируется автоматически)");
    }

    @Override
    public List<Student> execute(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("You should write only student name");
        }
        super.collectionManager.getStudentList().add(new Student(args[1]));

        return super.collectionManager.getStudentList();
    }
}
