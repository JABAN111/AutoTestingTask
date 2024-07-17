package TestTask.Commands;

import TestTask.DataClasses.Student;
import TestTask.Managers.CollectionManager;

import java.util.List;
import java.util.stream.Collectors;

public class GetStudentByIdCommand extends AbstractCommand{
    public GetStudentByIdCommand() {
        super("GetById", "Получение информации о студенте по id");
    }

    @Override
    public List<Student> execute(String[] args) {
        if(args.length != 1){
            throw new IllegalArgumentException("You should write only one id");
        }
        int id = Integer.parseInt(args[0]);
        return super.collectionManager.getStudentList().stream().filter(student -> student.getId() == id).collect(Collectors.toList());
    }
}
