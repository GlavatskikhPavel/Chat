package server;

import commands.Commands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickName;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith("/")) {
                        if (message.startsWith(Commands.AUTH)) {
                            String[] tokens = message.split(" ");
                            String login = tokens[1];
                            String password = tokens[2];
                            String currentNickName = server.getAuthService().getNickName(login, password);
                            if (currentNickName != null) {
                                nickName = currentNickName;
                                out.writeUTF(Commands.AUTH_OK + " " + currentNickName);
                                server.subscribe(this);
                                break;
                            } else {
                                out.writeUTF(Commands.AUTH_FAIL);
                            }
                        }
                    }
                }
                while(true) {
                    String message = in.readUTF();
                    if (Commands.EXIT.equals(message)) {
                        System.out.println("Клиент вышел");
                        out.writeUTF(message);
                        break;
                    }
                    server.broadcastMessage(message);
                    System.out.println("Client " + message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    server.unsubscribe(this);
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
