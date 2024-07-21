package TestTask.Commands;

import TestTask.Commands.Exception.InvalidArgs;
import TestTask.DataClasses.Student;

import java.util.List;
import java.util.stream.Collectors;

public class GetListByNameCommand extends AbstractCommand{
    public GetListByNameCommand() {
        super(CommandType.GET_BY_NAME, "Получение списка студентов по имени");
    }

    @Override
    public List<Student> execute(String[] args) throws InvalidArgs {
        if(args.length != 2){
            throw new InvalidArgs("You should write only one name");
        }
        String studentName = args[1];
        return super.collectionManager.getStudentList().stream().filter(student -> student.getName().equals(studentName)).collect(Collectors.toList());
    }
}
