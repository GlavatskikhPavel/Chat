package ru.glavatskikh.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    public TextArea textArea;

    @FXML
    public TextField textField;

    private Socket socket;
    private final int PORT = 7777;
    private final String ADDRESS = "localhost";
    private DataInputStream in;
    private DataOutputStream out;

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String massage = textField.getText();
        textField.clear();
        try {
            out.writeUTF(massage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        textField.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> textField.requestFocus());
        new Thread(() -> {
            try {
                socket = new Socket(ADDRESS, PORT);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                while (true) {
                    String message = null;
                    try {
                        message = in.readUTF();
                        if ("/exit".equals(message)) {
                            textArea.appendText("Выход");
                        }
                        textArea.appendText(message + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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