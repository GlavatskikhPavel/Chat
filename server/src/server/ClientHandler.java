package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

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
                while(true) {
                    String message = in.readUTF();
                    if ("/exit".equals(message)) {
                        System.out.println("Клиент вышел");
                        out.writeUTF(message + "\n");
                        break;
                    }
                    out.writeUTF(message);
                    System.out.println("Client " + message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }).start();

    }
}
