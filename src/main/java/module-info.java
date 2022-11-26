module lab01 {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires org.json;
    requires com.google.gson;
    opens pacxon to javafx.fxml;
    exports pacxon;
    exports pacxon.entities;
    opens pacxon.entities to javafx.fxml;
    exports pacxon.controllers;
    opens pacxon.controllers to javafx.fxml;
    exports pacxon.listeners;
    opens pacxon.listeners to javafx.fxml;
}