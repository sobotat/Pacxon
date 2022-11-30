package pacxon;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pacxon.listeners.GameChangeListener;
import pacxon.listeners.InputListener;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Game {

    private LinkedList<Level> levels;
    protected final GameChangeListener gameChangeListener;
    private int currentLevel = 0;
    private final int numberOfMaps;
    private int lives = 3;

    private boolean gameRunning;

    private boolean debug;
    private boolean godMode;

    int blockSize = 20;

    public Game(int numberOfMaps, boolean debug){
        this.debug = debug;
        this.numberOfMaps = numberOfMaps;

        gameChangeListener = new GameChangeListener() {
            @Override
            public void levelWon() {
                System.out.println("Percent " + levels.get(currentLevel).percentFilledOfMap());

                App.gameViewController.getHudListener().levelWon();

                Timer timer = new Timer(true);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        nextLevel();
                    }
                }, 3000);
            }

            @Override
            public void gameOver() {
                System.out.println("\033[1;31mGame Over\033[0m");
                App.gameViewController.getHudListener().gameOver();
                gameRunning = false;
            }

            @Override
            public void restartGame() {
                System.out.println("\n\n\n\033[1;38mRestarting Game\033[0m");
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
            String fileName = String.format("level%02d.txt", i);
            levels.add(new Level(fileName, this));
        }

        App.gameViewController.getHudListener().levelChanged(currentLevel);
        App.gameViewController.getHudListener().livesChanged(lives);
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

    // Game State
    public void startGame(){
        setUpGame(numberOfMaps);
        gameRunning = true;
        levels.get(currentLevel).setUpBonuses();
        System.out.println("\033[32mGame Started\033[0m");
    }
    public void stopGame() {
        gameRunning = false;
    }
    public void nextLevel() {
        if(currentLevel < levels.size() - 1) {
            currentLevel++;
            lives++;
            levels.get(currentLevel).setUpBonuses();
            App.gameViewController.getHudListener().levelChanged(currentLevel);
            App.gameViewController.getHudListener().livesChanged(lives);
            return;
        }

        App.gameViewController.getHudListener().gameWon();
        System.out.println("Game Won");
        gameRunning = false;
    }

    // Lives
    public void removeLife(){
        if(godMode){
            System.out.println("\033[1;34mYou was saved by most Powerful Power of God\033[0m");
            return;
        }

        lives--;

        if (lives > 0)
            System.out.println("Remaining lives \033[1;34m" + lives + "\033[0m");
        else
            gameChangeListener.gameOver();

        App.gameViewController.getHudListener().livesChanged(lives);
    }
    public void resetLife() {
        lives = 3;
        App.gameViewController.getHudListener().livesChanged(lives);
    }

    // Setters
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }

    // Getters
    public GameChangeListener getGameChangeListener() {
        return gameChangeListener;
    }
    public boolean isDebug() {
        return debug;
    }
    public boolean isGodMode() {
        return godMode;
    }
    public boolean isGameRunning() {
        return gameRunning;
    }
    public InputListener getCurrentInputListener(){
        return levels.get(currentLevel).getPlayerInputListener();
    }
}
