package TestTask.Commands;

import TestTask.Commands.Exception.InvalidArgs;
import TestTask.DataClasses.Student;


import java.util.List;

public class RemoveStudentById extends AbstractCommand{
    public RemoveStudentById() {
        super(CommandType.REMOVE_BY_ID,"Удаление студента по id");
    }

    @Override
    public List<Student> execute(String[] args) throws InvalidArgs {
        if(args[1] == null || args[1].isEmpty()){
            throw new InvalidArgs("Invalid student id");
        }
        if(args.length != 2){
            throw new InvalidArgs("You should write only one id");
        }

        int id = Integer.parseInt(args[1]);
        if(id <= 0){
            throw new InvalidArgs("Student id should be greater than 0");
        }
        List<Student> list = collectionManager.getStudentList();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getId() == id){
                list.remove(i);
                break;
            }
        }
        return super.collectionManager.getStudentList();
    }
}
