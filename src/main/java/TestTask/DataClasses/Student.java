package TestTask.DataClasses;

import java.util.Objects;

/**
 * POJO class for keeping information about students
 */

public class Student {
    private int id;
    private String name;

    public Student(int id,String name) {
        this.id = id;
        this.name = name;
    }
    public Student(String name){
        this.id = StudentIdGenerator.generateId();
        this.name = name;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

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
