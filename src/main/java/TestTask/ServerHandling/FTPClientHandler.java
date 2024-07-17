package TestTask.ServerHandling;

import TestTask.FileHandling.JsonParser;
import TestTask.Managers.CollectionManager;

import java.io.*;
import java.net.Socket;

/**
 * The FTPClientHandler class provides methods for connecting and communicating with an FTP server,
 * authorizing a user, sending commands, and transferring files.
 */
public class FTPClientHandler implements IServerHandling {
    private final Socket socket;
    private final BufferedReader bfReader;
    private final BufferedWriter bfWriter;

    /**
     * Constructs an FTPClientHandler and connects to the FTP server.
     *
     * @param serverIP the IP address of the FTP server
     * @param port the port of the FTP server
     * @throws IOException if an I/O error occurs when creating the socket
     */
    public FTPClientHandler(String serverIP, int port) throws IOException {
        socket = new Socket(serverIP, port);
        bfReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        bfWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bfReader.readLine();
    }

    /**
     * Authorizes the user with the FTP server.
     *
     * @param login the user's login name
     * @param password the user's password
     * @return the response status of the authorization
     * @throws AuthorizationFailed if the login or password is incorrect
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
        } catch (IOException e) {
            throw new AuthorizationFailed();
        }
        return ResponseStatus.SUCCESS;
    }

    /**
     * Sends a command to the FTP server without arguments.
     *
     * @param command the command to send
     * @return the response status of the operation
     * @throws IOException if an I/O error occurs
     */
    @Override
    public ResponseStatus sendCommandWithoutArgs(String command) throws IOException {
        bfWriter.write(command + "\r\n");
        bfWriter.flush();
        return ResponseStatus.SUCCESS;
    }

    /**
     * Sends a command to the FTP server with arguments.
     *
     * @param command the command to send
     * @param args the arguments for the command
     * @return the response status of the operation
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Disconnects from the FTP server.
     *
     * @return the response status of the disconnection
     */
    @Override
    public ResponseStatus disconnect() {
        try {
            sendCommandWithoutArgs("QUIT");
            socket.close();
            System.out.println("Disconnected from FTP server");
            return ResponseStatus.SUCCESS;
        } catch (IOException e) {
            System.err.println("Error while disconnecting: " + e.getMessage());
            System.exit(-1);
            return ResponseStatus.FAILURE;
        }
    }

    /**
     * Switches the FTP client to passive mode.
     *
     * @return a socket ready for file transfer
     * @throws IOException if an I/O error occurs
     */
    private Socket changeModeToPasv() throws IOException {
        sendCommandWithoutArgs("PASV");
        String response = bfReader.readLine();

        if (!response.startsWith("2")) {
            throw new IOException("Failed to switch to passive mode");
        }

        String[] parts = response.split("[()]")[1].split(",");
        String ip = String.join(".", parts[0], parts[1], parts[2], parts[3]);
        int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);

        return new Socket(ip, port);
    }

    /**
     * Retrieves a file from the FTP server and saves it locally.
     *
     * @param remotePath the path of the file on the server
     * @param localPath the path to save the file locally
     * @return the response status of the operation
     * @throws IOException if an I/O error occurs while operating
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
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
             FileWriter writer = new FileWriter(localPath, false)) {
            String line;
            line = fileReader.readLine();
            writer.append(line);
            while ((line = fileReader.readLine()) != null) {
                writer.append("\n").append(line);
            }
            writer.flush();
        }
        dataSocket.close();
        response = bfReader.readLine();
        if (response.startsWith("226")) {
            CollectionManager collectionManager = CollectionManager.getInstance();
            collectionManager.setStudentList(JsonParser.readJsonFile(localPath));
            return ResponseStatus.SUCCESS;
        }

        return ResponseStatus.FAILURE;
    }

    /**
     * Sends a local file to the FTP server.
     *
     * @param pathToLocalFile the path of the local file to send
     * @return the response status of the operation
     * @throws IOException if an I/O error occurs
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
             BufferedWriter bfSocketWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()))) {
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
