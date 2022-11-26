package pacxon;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pacxon.listeners.GameChangeListener;

import java.util.LinkedList;

public class Game {

    LinkedList<Level> levels;
    final GameChangeListener gameChangeListener;
    int currentLevel = 0;
    private int lives = 3;

    boolean gameRunning;

    boolean debug;

    int blockSize = 20;

    public Game(int numberOfMaps, boolean debug){
        gameRunning = true;
        this.debug = debug;
        //Level.generateLevelTemplate();

        setUpGame(numberOfMaps);

        gameChangeListener = new GameChangeListener() {
            @Override
            public void levelWon() {
                System.out.println("Percent " + levels.get(currentLevel).percentFilledOfMap());
                nextLevel();
            }

            @Override
            public void gameOver() {
                System.out.println("Game Over");
                gameRunning = false;
            }

            @Override
            public void restartGame() {
                System.out.println("Restarting Game");
                gameRunning = false;
                currentLevel = 0;
                setUpGame(numberOfMaps);
            }
        };
    }

    public void setUpGame(int numberOfMaps){
        levels = new LinkedList<>();

        Wall.loadTextures();
        for (int i = 1; i <= numberOfMaps; i++) {
            String fileName = String.format("level0%d.txt", i);
            levels.add(new Level(fileName, this));
        }

        App.gameViewController.getHudListener().levelChanged(currentLevel);
    }

    public void startGame(){
        gameRunning = true;
        System.out.println("Game Started");
    }

    public void removeLife(){
        lives--;

        if (lives > 0)
            System.out.println("Remaining lives " + lives);
        else
            gameChangeListener.gameOver();

        App.gameViewController.getHudListener().removeLife();
    }

    public void update(double deltaTime){
        if(gameRunning)
            levels.get(currentLevel).update(deltaTime);
    }

    public void draw(GraphicsContext gc){
        if(!gameRunning)
            return;

        clearCanvas(gc);
        levels.get(currentLevel).draw(gc, blockSize, debug);
    }

    private void clearCanvas(GraphicsContext gc){
        gc.setFill(Color.web("#282c30"));
        gc.fillRect(0,0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    public void resetLife() {
        lives = 3;
        App.gameViewController.getHudListener().resetLife();
    }

    public void nextLevel() {
        if(currentLevel < levels.size() - 1) {
            currentLevel++;
            App.gameViewController.getHudListener().levelChanged(currentLevel);
            return;
        }

        System.out.println("Game Won");
        gameRunning = false;
    }
}
