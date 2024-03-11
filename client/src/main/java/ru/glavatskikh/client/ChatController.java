package ru.glavatskikh.client;

import commands.Commands;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

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

    @FXML
    public TextField login;

    @FXML
    public PasswordField password;

    @FXML
    public HBox authPanel;

    @FXML
    public HBox messagePanel;

    private boolean authenticated;

    private String nickName;

    private Socket socket;
    private final int PORT = 7777;
    private final String ADDRESS = "localhost";
    private DataInputStream in;
    private DataOutputStream out;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        messagePanel.setVisible(authenticated);
        messagePanel.setManaged(authenticated);
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        if (!authenticated) {
            nickName = "";
        }
    }

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
        authenticated = false;
//        Platform.runLater(() -> textField.requestFocus());

    }

    @FXML
    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }
        new Thread(() -> {
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(Commands.AUTH + " " + login.getText() + " " + password.getText());
                password.clear();

                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith("/")) {
                        if (Commands.EXIT.equals(message)) {
                            throw new RuntimeException();
                        }
                        if (message.startsWith(Commands.AUTH_OK)) {
                            String[] tokens = message.split(" ");
                            nickName = tokens[1];
                            setAuthenticated(true);
                            break;
                        }
                        if (message.equals(Commands.AUTH_FAIL)) {
                            textArea.appendText("Login or password is incorrect\n");
                            throw new RuntimeException();
                        }
                    }
                    textArea.appendText(message);
                }
                while (true) {
                    String message;
                    try {
                        message = in.readUTF();
                        if (message.startsWith("/")) {
                            if (Commands.EXIT.equals(message)) {
                                textArea.appendText("Выход");
                                break;
                            }
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

    private void connect() {
        try {
            socket = new Socket(ADDRESS, PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}