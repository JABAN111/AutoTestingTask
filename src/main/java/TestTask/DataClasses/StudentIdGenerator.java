package TestTask.DataClasses;

import TestTask.Managers.CollectionManager;

/**
 * The StudentIdGenerator class provides a method to generate unique IDs for students.
 */
public class StudentIdGenerator {
    private static final CollectionManager collectionManager = CollectionManager.getInstance();

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private StudentIdGenerator() {}

    /**
     * Generates a unique ID for a new student.
     * The ID is generated based on the last existing ID in the student list.
     * @return a unique ID for a new student
     */
    public static int generateId() {
        return collectionManager.getStudentList().stream().mapToInt(Student::getId).max().orElse(0) + 1;
    }
}
