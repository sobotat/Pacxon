module pacxon {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    requires org.json;
    requires com.google.gson;

    requires org.apache.logging.log4j;
    requires lombok;
    requires jakarta.ws.rs;
    requires org.apache.cxf.rs.client;
    requires com.fasterxml.jackson.jakarta.rs.base;
    requires com.fasterxml.jackson.jakarta.rs.json;
    requires com.fasterxml.jackson.databind;
    requires jakarta.xml.bind;

    opens pacxon to javafx.fxml, javafx.graphics;
    opens pacxon.lib to javafx.fxml, javafx.graphics;
    opens pacxon.controllers to javafx.fxml, javafx.graphics;
    exports pacxon.lib;
    exports pacxon.controllers;
    exports pacxon.lib.api.entity;
    exports pacxon.lib.api;
    opens pacxon.lib.api.entity to javafx.fxml, javafx.graphics;

}