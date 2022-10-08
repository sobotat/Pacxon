module lab01 {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires org.json;
    requires com.google.gson;
    opens pacxon to javafx.fxml;
    exports pacxon;
}