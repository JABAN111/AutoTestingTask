package TestTask.Commands;

import TestTask.DataClasses.Student;
import TestTask.Managers.CollectionManager;

import java.util.List;
import java.util.Objects;

/**
 * The AbstractCommand class serves as a base class for all command types.
 * It implements the Icommand interface and provides common functionality for commands.
 */
public abstract class AbstractCommand implements Icommand {
    private final CommandType commandType;
    private final String description;
    protected CollectionManager collectionManager = CollectionManager.getInstance();

    /**
     * Constructs an AbstractCommand with the specified command type and description.
     *
     * @param commandType the type of the command(name)
     * @param description the description of the command
     */
    public AbstractCommand(CommandType commandType, String description) {
        this.commandType = commandType;
        this.description = description;
    }

    /**
     * Returns the description of the command.
     *
     * @return the description of the command
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the type of the command(name).
     *
     * @return the type of the command
     */
    @Override
    public CommandType getType() {
        return commandType;
    }

    /**
     * Executes the command with the given arguments.
     *
     * @param args the arguments for the command(<code>args[0]</code> is command)
     * @return a list of students resulting from the command execution
     */
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
