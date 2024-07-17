package TestTask.Commands;

import TestTask.DataClasses.Student;
import TestTask.Managers.CollectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetListByNameCommand extends AbstractCommand{
    public GetListByNameCommand() {
        super(CommandType.GET_BY_NAME, "Получение списка студентов по имени");
    }

    @Override
    public List<Student> execute(String[] args) {
        if(args.length != 2){
            throw new IllegalArgumentException("You should write only one name");
        }
        String studentName = args[1];
        return super.collectionManager.getStudentList().stream().filter(student -> student.getName().equals(studentName)).collect(Collectors.toList());
    }
}
