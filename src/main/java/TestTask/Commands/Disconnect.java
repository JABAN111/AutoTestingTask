package TestTask.Commands;

import TestTask.DataClasses.Student;

import java.util.List;

public class Disconnect extends AbstractCommand{
    public Disconnect() {
        super(CommandType.DISCONNECT,"Завершение работы");
    }

    @Override
    public List<Student> execute(String[] args) {
        //временно затычка из-за нежелания плодить еще один синглтон
        return null;
    }
}
