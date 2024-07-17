package TestTask.Commands;

import TestTask.DataClasses.Student;
import TestTask.Managers.CollectionManager;

import java.util.List;

public class RemoveStudentById extends AbstractCommand{
    public RemoveStudentById() {
        super("removeById","Удаление студента по id");
    }

    @Override
    public List<Student> execute(String[] args) {
        if(args.length != 1){
            throw new IllegalArgumentException("You should write only one id");
        }
        List<Student> list = collectionManager.getStudentList();
        int id = Integer.parseInt(args[0]);
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getId() == id){
                list.remove(i);
                break;
            }
        }
        return null;
    }
}
