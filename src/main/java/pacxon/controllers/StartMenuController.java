package pacxon.controllers;

import javafx.fxml.FXML;
import javafx.stage.WindowEvent;
import pacxon.App;

public class StartMenuController {

    @FXML
    void onSelectLevelClicked() {
        App.switchToGame();
    }

    @FXML
    void onExitClicked() {
        App.app.exitProgram(new WindowEvent(App.primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
