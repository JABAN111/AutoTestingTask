package TestTask.DataClasses;

import java.util.Objects;

/**
 * The Student class is a POJO (Plain Old Java Object) class that holds information about students.
 */
public class Student {
    private final int id;
    private String name;

    /**
     * Constructs a Student with the specified ID and name.
     *
     * @param id the ID of the student
     * @param name the name of the student
     */
    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructs a Student with the specified name. The ID is generated automatically.
     *
     * @param name the name of the student
     */
    public Student(String name) {
        this.id = StudentIdGenerator.generateId();
        this.name = name;
    }

    /**
     * @return the ID of the student
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name of the student
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the student.
     * @param name the new name of the student
     */
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "{" +
                "id: " + id +
                ", name: " + name + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id == student.id && Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
