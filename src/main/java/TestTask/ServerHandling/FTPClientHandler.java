package TestTask.ServerHandling;

import java.io.*;
import java.net.Socket;


public class FTPClientHandler implements IServerHandling {
    private final Socket socket;
    private final BufferedReader bfReader;
    private final BufferedWriter bfWriter;

    /**
     * Public constructor of FTPClientHandler, when initialize sending a request to FTP server to connect
     * @param serverIP of the FTP server
     * @param port of the FTP server
     */
    public FTPClientHandler(String serverIP, int port) throws IOException {
        socket = new Socket(serverIP, port);
        bfReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bfWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        System.out.println(bfReader.readLine());
    }

    /**
     * Method that sending request for the FTP server for authorization and
     *
     * @param login    USER login
     * @param password for the specified user
     * @return ResponseStatus of logging
     * @throws AuthorizationFailed if login or password was incorrect
     */
    @Override
    public ResponseStatus authorization(String login, String password) throws AuthorizationFailed {
        String statusOfLogging;
        try {
            sendCommandWithArgs("USER", new String[]{login});
            bfReader.readLine(); // Password required...
            sendCommandWithArgs("PASS", new String[]{password});
            statusOfLogging = bfReader.readLine();
            if (!statusOfLogging.startsWith("2")) {
                throw new AuthorizationFailed();
            }
            System.out.println(statusOfLogging);
        } catch (IOException e) {
            throw new AuthorizationFailed();
        }
        return ResponseStatus.SUCCESS;
    }

    @Override
    public ResponseStatus sendCommandWithoutArgs(String command) throws IOException {
        bfWriter.write(command + "\r\n");
        bfWriter.flush();
        return ResponseStatus.SUCCESS;
    }

    @Override
    public ResponseStatus sendCommandWithArgs(String command, String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(" ").append(arg);
        }
        bfWriter.write(command + sb + "\r\n");
        bfWriter.flush();
        return ResponseStatus.SUCCESS;
    }

    @Override
    public ResponseStatus disconnect() {
        try {
            sendCommandWithoutArgs("QUIT");
            socket.close();
            System.out.println("Disconnected from FTP server");
            return ResponseStatus.SUCCESS;
        } catch (IOException e) {
            System.err.println("Error while disconnecting " + e.getMessage());
            System.exit(-1);
            return ResponseStatus.FAILURE;
        }
    }

    /**
     * By default, server can be in two stats:active and passive.This method switch context to Passive for working with files
     * @return Socket which is ready to sharing files
     * @throws IOException if server couldn't switch context
     */
    private Socket changeModeToPasv() throws IOException {
        sendCommandWithoutArgs("PASV");
        String response = bfReader.readLine();

        if (!response.startsWith("2")) {
            //todo заменить на кастомную ошибку(поменять javadoc тоже)
            throw new IOException();
        }

        String[] parts = response.split("[()]")[1].split(",");
        String ip = String.join(".", parts[0], parts[1], parts[2], parts[3]);
        int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);

        return new Socket(ip,port);
    }

    /**
     * Getting from the server specified file and save it locally to a specified path
     * @param remotePath where the file is located on server, if file didn't exist throw new exception
     * @param localPath where to save file. If a file doesn't exist, create a new one with a specified name in a path
     * @return status of operation
     *
     */
    @Override
    public ResponseStatus getFileFromServer(String remotePath, String localPath) throws IOException {
        Socket dataSocket = changeModeToPasv();
        String response;
        sendCommandWithArgs("RETR", new String[]{remotePath});
        response = bfReader.readLine();
        if (!response.startsWith("150") && !response.startsWith("125")) {
            System.err.println("Failed to retrieve file: " + response);
            dataSocket.close();
            return ResponseStatus.FAILURE;
        }
        //todo завершить запись в свой файл
        StringBuilder sb = new StringBuilder();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {
            String line;
            line = fileReader.readLine();
            sb.append(line);
            while ((line = fileReader.readLine()) != null) {
                sb.append("\n").append(line);
            }
        }
        System.out.println("Временно записывает в StringBuilder получаемый файл: " + sb);
        dataSocket.close();
        response = bfReader.readLine();
        if (response.startsWith("226")) {
            return ResponseStatus.SUCCESS;
        }

        return ResponseStatus.FAILURE;
    }

    /**
     * Sending local file to the server
     * @param pathToLocalFile which would be sent to the server
     * @return ResponseStatus of operation
// * @throws IOException if
     */
    @Override
    public ResponseStatus sendFile(String pathToLocalFile) throws IOException {
        Socket dataSocket = changeModeToPasv();
        String response;

        File file = new File(pathToLocalFile);
        if (!file.exists() || !file.canRead()) {
            System.err.println("File does not exist or cannot be read: " + pathToLocalFile);
            dataSocket.close();
            return ResponseStatus.FAILURE;
        }
        sendCommandWithArgs("STOR", new String[]{file.getName()});
        response = bfReader.readLine();
        if (!response.startsWith("150") && !response.startsWith("125")) {
            System.err.println("Failed to store file: " + response);
            dataSocket.close();
            return ResponseStatus.FAILURE;
        }
        try (BufferedReader bfFileReader = new BufferedReader(new FileReader(file));
             BufferedWriter bfSocketWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()))
        ) {
            String line;
            while ((line = bfFileReader.readLine()) != null) {
                bfSocketWriter.write(line);
            }
        }

        dataSocket.close();
        response = bfReader.readLine();
        if (response.startsWith("226")) {
            return ResponseStatus.SUCCESS;
        }

        return ResponseStatus.FAILURE;
    }


}
