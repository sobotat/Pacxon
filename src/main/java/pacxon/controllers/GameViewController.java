package pacxon.controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.Text;
import pacxon.DrawingThread;
import pacxon.lib.Game;
import pacxon.lib.listeners.HUDListener;

public class GameViewController {

    AnimationTimer timer;
    HUDListener hudListener;
    Game game;
    @FXML
    Canvas canvas_Game;
    @FXML
    Text tv_MapFill, tv_Level, tv_Lives;

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
            public void levelWon() {
                tv_Level.setText("Level Won");
            }
            @Override
            public void gameWon() {
                tv_Level.setText("Game Won");
            }
            @Override
            public void gameOver() {
                tv_Level.setText("Game Over");
            }
            @Override
            public void livesChanged(int lives) {
                tv_Lives.setText("Lives " + lives);
            }
        };

        game = new Game(1, hudListener,false, "src/main/resources/pacxon/");
        timer = new DrawingThread(canvas_Game, scene, game);
        timer.start();
    }

    public void startGame(){
        game.startGame();
    }

    public void stopGame(){
        game.stopGame();
    }
}
