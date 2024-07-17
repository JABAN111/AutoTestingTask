package TestTask.DataClasses;

import TestTask.Managers.CollectionManager;

import java.lang.reflect.Field;

public class StudentIdGenerator{
    private int lastID;
    private static CollectionManager collectionManager = CollectionManager.getInstance();
    private StudentIdGenerator(){}
    public static int generateId(){
        return collectionManager.getStudentList().stream().mapToInt(Student::getId).max().orElse(0) + 1;
    }
}
