package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private final int PORT = 7777;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("������ �������");
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("������ ����������");
                new ClientHandler(this, clientSocket);
            }
        } catch (IOException e) {
            System.out.println("������ ������ �������");
            throw new RuntimeException(e);
        } finally {
            try {
                serverSocket.close();
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
