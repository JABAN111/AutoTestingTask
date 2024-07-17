package TestTask.Commands;

import TestTask.DataClasses.Student;
import TestTask.Managers.CollectionManager;

import java.util.List;
import java.util.Objects;

public abstract class AbstractCommand implements Icommand{
    private final CommandType commandType;
    private final String description;
    protected CollectionManager collectionManager = CollectionManager.getInstance();
    public AbstractCommand(CommandType commandType, String description) {
        this.commandType = commandType;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public CommandType getType() {
        return commandType;
    }

    @Override
    public abstract List<Student> execute(String[] args);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCommand that = (AbstractCommand) o;
        return Objects.equals(commandType, that.commandType) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandType, description);
    }

    @Override
    public String toString() {
        return "{" +
                "'commandName: '" + commandType + '\'' +
                ", 'description: '" + description + '\'' +
                '}';
    }
}
