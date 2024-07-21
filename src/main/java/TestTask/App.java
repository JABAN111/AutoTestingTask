package TestTask;

import TestTask.Commands.AbstractCommand;
import TestTask.Commands.CommandFactory;
import TestTask.Commands.CommandType;
import TestTask.Commands.Exception.InvalidArgs;
import TestTask.FileHandling.JsonParser;
import TestTask.Managers.CollectionManager;
import TestTask.Managers.CommandManager;
import TestTask.ServerHandling.Exceptions.AuthorizationFailed;
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

    private static final int DEFAULT_PORT = 21;
    private static final String DEFAULT_LOCAL_FILE = "input.json";

    public static void main(String[] args) {
        if (args.length != 4) {
            LOGGER.severe("You should run the program with these four args: login password ip(of FTP server) pathToJsonFileOnServer");
            System.exit(-1);
        }

        String user = args[0];
        String pwd = args[1];
        String ip = args[2];
        String pathToJsonFile = args[3];

        try {
            FTPClientHandler ftpClient = setupFTPClient(user, pwd, ip);
            fetchAndLoadData(ftpClient, pathToJsonFile);

            processUserInput(ftpClient);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Connection refused", e);
            System.exit(-1);
        } catch (AuthorizationFailed e) {
            LOGGER.log(Level.SEVERE, "Login or password is incorrect", e);
            System.exit(-1);
        }
    }

    private static FTPClientHandler setupFTPClient(String user, String pwd, String ip) throws IOException, AuthorizationFailed {
        FTPClientHandler ftpClient = new FTPClientHandler(ip, DEFAULT_PORT);
        ftpClient.authorization(user, pwd);
        return ftpClient;
    }

    private static void fetchAndLoadData(FTPClientHandler ftpClient, String pathToJsonFile) throws IOException {
        ftpClient.getFileFromServer(pathToJsonFile, DEFAULT_LOCAL_FILE);
        collectionManager.setStudentList(JsonParser.readJsonFile(DEFAULT_LOCAL_FILE));
    }

    private static void processUserInput(FTPClientHandler ftpClient) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String inputLine;
            while (true) {
                printPossibleInput();
                waitingUser();
                inputLine = reader.readLine();
                if (inputLine == null || inputLine.trim().isEmpty()) continue;

                String[] partsOfInput = inputLine.split(" ");
                CommandType gotCommand = getCommandType(partsOfInput[0]);
                if (gotCommand == null) continue;

                if (gotCommand != CommandType.DISCONNECT) {
                    try {
                        System.out.println(CommandManager.executor(gotCommand, partsOfInput));
                    }catch (InvalidArgs e) {
                        LOGGER.log(Level.SEVERE, "Invalid argument", e.getMessage());
                    }
                } else {
                    reader.close();
                    handleDisconnect(ftpClient);
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading input", e);
        }
    }

    private static CommandType getCommandType(String input) {
        try {
            return CommandType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Unknown command: " + input);
            return null;
        }
    }

    private static void handleDisconnect(FTPClientHandler ftpClient) throws IOException {
        LOGGER.info("Завершение сеанса, сохраняем файл на сервере...");
        JsonParser.writeStudentToFile(collectionManager.getStudentList(), DEFAULT_LOCAL_FILE);
        ftpClient.sendFile(DEFAULT_LOCAL_FILE);
        ftpClient.disconnect();
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
