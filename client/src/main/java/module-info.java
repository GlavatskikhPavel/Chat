module ru.glavatskikh.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires commands;


    opens ru.glavatskikh.client to javafx.fxml;
    exports ru.glavatskikh.client;
}