module lab01 {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    opens pacxon to javafx.fxml;
    exports pacxon;
}