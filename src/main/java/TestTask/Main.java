package TestTask;

import TestTask.Commands.AbstractCommand;
import TestTask.Commands.CommandFactory;
import TestTask.Commands.CommandType;
import TestTask.FileHandling.JsonParser;
import TestTask.Managers.CollectionManager;
import TestTask.Managers.CommandManager;
import TestTask.ServerHandling.AuthorizationFailed;
import TestTask.ServerHandling.FTPClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Main {
    private static final CollectionManager collectionManager = CollectionManager.getInstance();

    private static final Map<CommandType, AbstractCommand> commandMap = CommandFactory.getMapCommands();
    private static final String[] possibleCommands = {"Получить список студентов по имени",
            "Получение студента по id",
            "Добавить студента",
            "Удалить студента по id",
            "Завершить работу"
    };

    public static void main(String[] args)  {
        if (args.length != 4) {
            System.err.println("You should run program with this four args: login password ip(of FTP server) pathToJsonFile");
            System.exit(-1);
        }
        String user = args[0];
        String pwd = args[1];
        String ip = args[2];
        String pathToJsonFile = args[3];

        int port = 21;//default port
        try {
            FTPClientHandler ftpClient = new FTPClientHandler(ip, port);
            ftpClient.authorization(user, pwd);
            ftpClient.getFileFromServer(pathToJsonFile,"input.json");
            collectionManager.setStudentList(JsonParser.readJsonFile("input.json"));
            System.out.println(collectionManager.getStudentList());
            BufferedReader BFRUser = new BufferedReader(new InputStreamReader(System.in));
            String inputLine;
            while (true){
                printPossibleInput();
                waitingUser();
                inputLine = BFRUser.readLine();
                String[] partsOfInput = inputLine.split(" ");
                CommandType gotCommand = CommandType.valueOf(partsOfInput[0]);
                if(gotCommand != CommandType.DISCONNECT){
                    System.out.println(CommandManager.executor(gotCommand, partsOfInput));
                }else{
                    System.out.println("Завершение сеанса, сохраняем файл на сервере...");
                    JsonParser.writeStudentToFile(collectionManager.getStudentList(),"input.json");
                    ftpClient.sendFile("input.json");
                    ftpClient.disconnect();
                    BFRUser.close();
                    System.exit(0);
                }
            }


        }catch (IOException e){
            System.err.println("Connection refused");
            System.exit(-1);
        }catch (AuthorizationFailed e){
            System.err.println(e.getMessage());
        }
    }

    public static void printPossibleInput(){
        System.out.println("Введите команду из списка в формате command_name <arg1>:\n");
        for (CommandType type : commandMap.keySet()) {
            System.out.println("Команда: " + type + " описание: " + commandMap.get(type).getDescription());
        }
    }
    public static void waitingUser(){
        System.out.print("> ");
    }
}