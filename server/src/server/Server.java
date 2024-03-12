package server;

import server.interfaces.AuthService;
import server.services.AuthServiceImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private final int PORT = 7777;
    private List<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        authService = new AuthServiceImpl();
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен");
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Клиент соединился");
                new ClientHandler(this, clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Ошибка старта сервера");
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

    public void broadcastMessage(String message, String senderNickName) {
        for (ClientHandler client : clients) {
            client.sendMessage(String.format("[ %s ]: %s", senderNickName, message));
        }
    }

    public void subscribe(ClientHandler handler) {
        clients.add(handler);
    }

    public void unsubscribe(ClientHandler handler) {
        clients.remove(handler);
    }
}
