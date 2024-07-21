package TestTask.ServerHandling;

import TestTask.ServerHandling.Exceptions.AuthorizationFailed;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The FTPClientHandler class provides methods for connecting and communicating with an FTP server,
 * authorizing a user, sending commands, and transferring files.
 */
public class FTPClientHandler implements IServerHandling {
    private static final Logger LOGGER = Logger.getLogger(FTPClientHandler.class.getName());
    private static final String DISCONNECTED_MESSAGE = "Disconnected from FTP server";
    private static final String ERROR_DISCONNECTING_MESSAGE = "Error while disconnecting: ";

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
        try {
            sendCommandWithArgs("USER", new String[]{login});
            bfReader.readLine(); // ftp: Password required...
            sendCommandWithArgs("PASS", new String[]{password});
            String statusOfLogging = bfReader.readLine();
            if (!statusOfLogging.startsWith("2")) {
                throw new AuthorizationFailed();
            }
            return ResponseStatus.SUCCESS;
        } catch (IOException e) {
            throw new AuthorizationFailed();
        }
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
        StringBuilder sb = new StringBuilder(command);
        for (String arg : args) {
            sb.append(" ").append(arg);
        }
        bfWriter.write(sb.append("\r\n").toString());
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
            LOGGER.info(DISCONNECTED_MESSAGE);
            return ResponseStatus.SUCCESS;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, ERROR_DISCONNECTING_MESSAGE, e);
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
        validateFilePaths(remotePath, localPath);

        try (Socket dataSocket = changeModeToPasv()) {
            sendCommandWithArgs("RETR", new String[]{remotePath});
            String response = bfReader.readLine();
            if (!response.startsWith("1")) {
                LOGGER.severe("Failed to retrieve file: " + response);
                return ResponseStatus.FAILURE;
            }

            try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
                 FileWriter writer = new FileWriter(localPath)) {
                copyFileContent(fileReader, writer);
            }

            response = bfReader.readLine();
            return response.startsWith("2") ? ResponseStatus.SUCCESS : ResponseStatus.FAILURE;
        }
    }

    /**
     * Sends a local file to the FTP server.
     *
     * @param pathToLocalFile the path of the local file to send
     *
     * @return the response status of the operation
     * @throws IOException if an I/O error occurs
     */
    @Override
    public ResponseStatus sendFile(String pathToLocalFile) throws IOException {
        validateFilePath(pathToLocalFile);

        try (Socket dataSocket = changeModeToPasv()) {
            File file = new File(pathToLocalFile);
            if (!file.exists() || !file.canRead()) {
                LOGGER.severe("File does not exist or cannot be read: " + pathToLocalFile);
                throw new FileNotFoundException();
            }

            sendCommandWithArgs("STOR", new String[]{file.getName()});
            String response = bfReader.readLine();
            if (!response.startsWith("1")) {
                LOGGER.severe("Failed to store file: " + response);
                return ResponseStatus.FAILURE;
            }

            try (BufferedReader bfFileReader = new BufferedReader(new FileReader(file));
                 BufferedWriter bfSocketWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()))) {
                copyFileContent(bfFileReader, bfSocketWriter);
            }

            response = bfReader.readLine();
            return response.startsWith("2") ? ResponseStatus.SUCCESS : ResponseStatus.FAILURE;
        }
    }

    private void validateFilePaths(String... paths) throws FileNotFoundException {
        for (String path : paths) {
            if (path == null || path.isEmpty()) {
                throw new FileNotFoundException("Path cannot be null or empty");
            }
        }
    }

    private void validateFilePath(String path) throws FileNotFoundException {
        if (path == null || path.isEmpty()) {
            throw new FileNotFoundException("Path cannot be null or empty");
        }
    }

    private void copyFileContent(BufferedReader reader, Writer writer) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.write(System.lineSeparator());
        }
        writer.flush();
    }
}
