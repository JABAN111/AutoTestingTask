package TestTask.ServerHandling;


import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class FTPClientHandler implements IServerHandling {
    private Socket socket;
    private BufferedReader bfReader;
    private BufferedWriter bfWriter;

    public FTPClientHandler(String serverIP, int port, String login, String pwd) {
        try {
            socket = new Socket(serverIP, port);
            bfReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bfWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println(bfReader.readLine());

        } catch (IOException e) {
            System.err.println("Connection refused: " + e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public ResponseStatus authorization(String login, String password) {
        String statusOfLogging = "";
        try {
            sendCommandWithArgs("USER",new String[]{login});
            System.out.println(bfReader.readLine());//password required...
            sendCommandWithArgs("PASS",new String[]{password});
            statusOfLogging = bfReader.readLine();
            if(!statusOfLogging.startsWith("230")){
                throw new AuthorizationFailed();
            }
            System.out.println(statusOfLogging);

            //fixme удалить, просто временно негде тестить
            System.out.println("закончили чтение: " + getFileFromServer("coolCode.txt", "data.txt"));
            sendCommandWithoutArgs("SYST");
            System.out.println(bfReader.readLine());
        }catch (IOException e){
            System.err.println("Authorization failed");
        }catch (AuthorizationFailed e){
            System.err.println(statusOfLogging);
            return ResponseStatus.AUTHORIZATION_FAILED;
        }
        return null;
    }

    @Override
    public ResponseStatus sendCommandWithoutArgs(String command) throws IOException {
        bfWriter.write(command + "\r\n");
        bfWriter.flush();
        return null;
    }

    @Override
    public ResponseStatus sendCommandWithArgs(String command, String[] args) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String arg : args)
            sb.append(" ").append(arg);
        System.out.println("Отправляется: " + command + sb);
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

    @Override
    public ResponseStatus getFileFromServer(String remotePath, String localPath) throws IOException {
        sendCommandWithoutArgs("PASV");
        String response = bfReader.readLine();

        if (!response.startsWith("227")) {
            System.out.println("Failed to enter passive mode: " + response);
            return ResponseStatus.FAILURE;
        }

        String[] parts = response.split("[()]")[1].split(",");
        String ip = String.join(".", parts[0], parts[1], parts[2], parts[3]);
        int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);
        Socket dataSocket = new Socket(ip, port);

        sendCommandWithoutArgs("RETR " + remotePath);
        response = bfReader.readLine();
        if (!response.startsWith("125")) {
            System.err.println("Failed to retrieve file: " + response);
            dataSocket.close();
            return ResponseStatus.FAILURE;
        }
        //todo завершить запись в свой файл //а еще заебошить создателей этого протокола
        try(BufferedReader fileReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))){
            StringBuilder sb = new StringBuilder();
            String line;
            line = fileReader.readLine();
            sb.append(line);
            while((line = fileReader.readLine()) != null){
                sb.append("\n").append(line);
            }
            System.out.println(sb);
        }


        dataSocket.close();
        response = bfReader.readLine();
        System.out.println(response);
//        response = readResponse();
        if (response.startsWith("226")) {
            return ResponseStatus.SUCCESS;
        }

        return ResponseStatus.FAILURE;
    }

    @Override
    public ResponseStatus sendFile() {
        return null;
    }
}
