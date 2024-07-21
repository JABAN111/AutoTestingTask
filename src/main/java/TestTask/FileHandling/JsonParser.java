package TestTask.FileHandling;

import TestTask.DataClasses.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The JsonParser class provides methods to read and write JSON files containing student information.
 * This class cannot be instantiated.
 */
public class JsonParser {
    /**
     * Private constructor to prevent instantiation.
     */
    private JsonParser() {}

    /**
     * Parses a JSON file and returns a list of students.
     *
     * @param filePath the path to the JSON file on the local machine
     * @return a list of students parsed from the JSON file
     * @throws IOException if there is an error reading the file
     */
    public static List<Student> readJsonFile(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        StringBuilder jsonContent = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            jsonContent.append(line);
        }

        reader.close();
        String jsonString = jsonContent.toString();
        jsonString = jsonString.substring(jsonString.indexOf('[') + 1, jsonString.lastIndexOf(']'));
        String[] studentEntries = jsonString.split("},\\s*\\{");

        for (String entry : studentEntries) {
            entry = entry.replace("{", "").replace("}", "").replace("\"", "");
            String[] keyValuePairs = entry.split(",");
            int id = 0;
            String name = "";

            for (String pair : keyValuePairs) {
                String[] keyValue = pair.split(":");
                if(keyValue.length < 2) {
                    continue;
                }
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                if (key.equals("id")) {
                    id = Integer.parseInt(value);
                } else if (key.equals("name")) {
                    name = value;
                }
            }
            if(id != 0)//find at least one student in input file
                students.add(new Student(id, name));
        }
        System.out.println(students);
        return students;
    }

    /**
     * Writes a list of students to a JSON file.
     *
     * @param students the list of students to write to the file
     * @param filePath the path to the JSON file on the local machine
     * @throws IOException if there is an error writing to the file
     */
    public static void writeStudentToFile(List<Student> students, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write("{\"students\": [");

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            writer.write("{");
            writer.write("\"id\": " + student.getId() + ",");
            writer.write("\"name\": \"" + student.getName() + "\"");
            writer.write("}");
            if (i < students.size() - 1) {
                writer.write(",");
            }
            writer.write("\n");
        }

        writer.write("]}");
        writer.close();
    }
}
