package pacxon.controllers;

import javafx.fxml.FXML;
import javafx.stage.WindowEvent;
import pacxon.App;

public class StartMenuController {

    @FXML
    void onStartGameClicked() {
        App.switchToGame();
    }

    @FXML
    void onExitClicked() {
        App.getApp().exitProgram(new WindowEvent(App.getPrimaryStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
