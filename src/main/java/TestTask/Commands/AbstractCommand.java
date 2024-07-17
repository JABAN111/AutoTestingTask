package TestTask.Commands;

import TestTask.DataClasses.Student;
import TestTask.Managers.CollectionManager;

import java.util.List;
import java.util.Objects;

public abstract class AbstractCommand implements Icommand{
    private final String commandName;
    private final String description;
    protected CollectionManager collectionManager = CollectionManager.getInstance();
    public AbstractCommand(String commandName, String description) {
        this.commandName = commandName;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getName() {
        return this.commandName;
    }

    @Override
    public abstract List<Student> execute(String[] args);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCommand that = (AbstractCommand) o;
        return Objects.equals(commandName, that.commandName) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandName, description);
    }

    @Override
    public String toString() {
        return "{" +
                "'commandName: '" + commandName + '\'' +
                ", 'description: '" + description + '\'' +
                '}';
    }
}
