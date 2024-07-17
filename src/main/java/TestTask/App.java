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

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private static final CollectionManager collectionManager = CollectionManager.getInstance();
    private static final Map<CommandType, AbstractCommand> commandMap = CommandFactory.getMapCommands();
    //program should start like java -jar name.jar LOGIN MYPWD localhost input.json
    public static void main(String[] args) {
        if (args.length != 4) {
            LOGGER.severe("You should run the program with these four args: login password ip(of FTP server) pathToJsonFileOnServer");
            System.exit(-1);
        }

        String user = args[0];
        String pwd = args[1];
        String ip = args[2];
        String pathToJsonFile = args[3];

        int port = 21; // default port
        String localFile = "input.json"; //default file to save data

        try {
            FTPClientHandler ftpClient = new FTPClientHandler(ip, port);
            ftpClient.authorization(user, pwd);
            ftpClient.getFileFromServer(pathToJsonFile, localFile);

            try (BufferedReader BFRUser = new BufferedReader(new InputStreamReader(System.in))) {
                String inputLine;
                while (true) {
                    printPossibleInput();
                    waitingUser();
                    inputLine = BFRUser.readLine();
                    if (inputLine == null || inputLine.isEmpty() || inputLine.equals(" ")) continue;

                    String[] partsOfInput = inputLine.split(" ");
                    CommandType gotCommand;

                    try {
                        gotCommand = CommandType.valueOf(partsOfInput[0]);
                    } catch (IllegalArgumentException e) {
                        LOGGER.warning("Unknown command: " + partsOfInput[0]);
                        continue;
                    }

                    if (gotCommand != CommandType.DISCONNECT) {
                        LOGGER.info(CommandManager.executor(gotCommand, partsOfInput).toString());
                    } else {
                        LOGGER.info("Завершение сеанса, сохраняем файл на сервере...");
                        JsonParser.writeStudentToFile(collectionManager.getStudentList(), localFile);
                        ftpClient.sendFile(localFile);
                        ftpClient.disconnect();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Connection refused");
            System.exit(-1);
        } catch (AuthorizationFailed e) {
            LOGGER.log(Level.SEVERE, "Login or password is incorrect");
            System.exit(-1);
        }
    }

    private static void printPossibleInput() {
        System.out.println("Введите команду из списка в формате command_name <arg1>:\n");
        for (CommandType type : commandMap.keySet()) {
            System.out.println("Команда: " + type + " описание: " + commandMap.get(type).getDescription());
        }
    }

    private static void waitingUser() {
        System.out.print("> ");
    }
}
