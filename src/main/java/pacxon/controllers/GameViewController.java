package pacxon.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import pacxon.DrawingThread;
import pacxon.Game;
import pacxon.listeners.HUDListener;

import java.util.LinkedList;

public class GameViewController {

    private AnimationTimer timer;
    private HUDListener hudListener;
    Game game;
    @FXML
    Canvas canvas_Game;
    @FXML
    HBox hbox_Life;
    @FXML
    Text tv_MapFill, tv_Level;

    private final LinkedList<Node> removedLives = new LinkedList<>();

    public void setUpGame(Scene scene){
        hudListener = new HUDListener() {
            @Override
            public void mapFillPercentageChanged(int percents) {
                tv_MapFill.setText(percents + "%");
            }

            @Override
            public void levelChanged(int levelNumber) {
                tv_Level.setText("Level " + (levelNumber + 1));
            }

            @Override
            public void removeLife() {
                removedLives.add(hbox_Life.getChildren().remove(0));
            }

            @Override
            public void resetLife() {
                removedLives.addAll(hbox_Life.getChildren());
                hbox_Life.getChildren().clear();

                for (int i = removedLives.size() - 1; i >= 0; i--){
                    hbox_Life.getChildren().add(removedLives.get(i));
                }
                removedLives.clear();
            }
        };

        game = new Game(2, true);
        timer = new DrawingThread(canvas_Game, scene, game);
        timer.start();
    }

    public void startGame(){
        game.startGame();
    }

    public void stopGame(){
        game.stopGame();
    }

    public HUDListener getHudListener() {
        return hudListener;
    }
}
