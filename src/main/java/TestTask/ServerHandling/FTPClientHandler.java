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
            authorization(login,pwd);
//            System.out.println("Мы выходим?...");
//            System.out.println(bfReader.readLine());
//            sendCommandWithoutArgs("HELP");
//            sendCommandWithoutArgs("HELP");
        } catch (IOException e) {
            System.err.println("Connection refused: " + e.getMessage());
            System.exit(-1);
        }
    }

    @Override
    public ResponseStatus authorization(String login, String password)  {
        sendCommandWithArgs("USER", new String[]{login});
        sendCommandWithArgs("PASS", new String[]{password});
        return null;
    }

    @Override
    public ResponseStatus sendCommandWithoutArgs(String command){
//        return null;
        try {
            bfWriter.write(command + "\r\n");
            bfWriter.flush();
//            responseFromServer();
            StringBuilder tmpResponse = new StringBuilder(); //= bfReader.readLine();
            while(bfReader.ready()){
                tmpResponse.append(bfReader.readLine());
            }
            System.out.println(tmpResponse);
            return null;
        } catch (IOException e) {
            System.err.println("Exception while sending command: " + e.getMessage());
            return ResponseStatus.FAILURE;
        }

    }

    @Override
    public ResponseStatus sendCommandWithArgs(String command, String[] args) {
        try{
            StringBuilder sb = new StringBuilder();
            for (String arg : args)
                sb.append(" ").append(arg);
            System.out.println("("+(command+sb)+")Sending...");
            bfWriter.write(command + sb+"\r\n");
            bfWriter.flush();
            String tmpResponse = bfReader.readLine();
            System.out.println(tmpResponse);

            return ResponseStatus.SUCCESS;
        }catch (IOException e){
            System.err.println("Exception while sending command: " + e.getMessage());
            return ResponseStatus.FAILURE;
        }
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
    public Files getFileFromServer(String path) {
        return null;
    }

    @Override
    public ResponseStatus sendFile() {
        return null;
    }
}
