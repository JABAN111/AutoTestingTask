package TestTask.Managers;

import TestTask.DataClasses.Student;

import java.util.ArrayList;
import java.util.List;

public class CollectionManager {
    private static CollectionManager collectionManager;
    private List<Student> studentList;
    private CollectionManager(){
        studentList = new ArrayList<>();
    }

    public static CollectionManager getInstance() {
        if (collectionManager == null)
            collectionManager = new CollectionManager();
        return collectionManager;
    }


    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }
}
