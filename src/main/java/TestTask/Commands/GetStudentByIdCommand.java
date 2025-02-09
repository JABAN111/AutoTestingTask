package TestTask.Commands;

import TestTask.Commands.Exception.InvalidArgs;
import TestTask.DataClasses.Student;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetStudentByIdCommand extends AbstractCommand{
    public GetStudentByIdCommand() {
        super(CommandType.GET_BY_ID, "Получение информации о студенте по id");
    }

    @Override
    public List<Student> execute(String[] args) throws InvalidArgs {
        if(args.length != 2){
            System.out.println("пришла длина: " + args.length);
            Stream.of(args).forEach(System.out::println);
            throw new InvalidArgs("You should write only one id");
        }
        int id = Integer.parseInt(args[1]);
        return super.collectionManager.getStudentList().stream().filter(student -> student.getId() == id).collect(Collectors.toList());
    }
}
