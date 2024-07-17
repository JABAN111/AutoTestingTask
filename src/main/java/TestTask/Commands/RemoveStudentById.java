package TestTask.Commands;

import TestTask.DataClasses.Student;
import TestTask.Managers.CollectionManager;

import java.util.List;

public class RemoveStudentById extends AbstractCommand{
    public RemoveStudentById() {
        super(CommandType.REMOVE_BY_ID,"Удаление студента по id");
    }

    @Override
    public List<Student> execute(String[] args) {
        if(args.length != 2){
            throw new IllegalArgumentException("You should write only one id");
        }
        List<Student> list = collectionManager.getStudentList();
        int id = Integer.parseInt(args[1]);
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getId() == id){
                list.remove(i);
                break;
            }
        }
        return null;
    }
}
