package TestTask.Managers;

import TestTask.DataClasses.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * The CollectionManager class is responsible for managing a collection of students.
 * It uses the singleton pattern
 */
public class CollectionManager {
    private static CollectionManager collectionManager;
    private List<Student> studentList;

    /**
     * Private constructor to prevent instantiation.
     * Initializes the student list.
     */
    private CollectionManager() {
        studentList = new ArrayList<>();
    }

    /**
     * <code>CollectionManager</code> is a Singleton class, this method is providing the instance of this class
     *
     * @return the single instance of CollectionManager
     */
    public static CollectionManager getInstance() {
        if (collectionManager == null) {
            collectionManager = new CollectionManager();
        }
        return collectionManager;
    }

    /**
     * @return the list of students
     */
    public List<Student> getStudentList() {
        return studentList;
    }

    /**
     * @param studentList the new list of students
     */
    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }
}
