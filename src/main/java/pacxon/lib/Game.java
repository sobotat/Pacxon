package pacxon.lib;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pacxon.lib.api.Files;
import pacxon.lib.listeners.GameChangeListener;
import pacxon.lib.listeners.HUDListener;
import pacxon.lib.listeners.InputListener;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Game {
    private static final Logger logger = LogManager.getLogger(Game.class.getName());

    private LinkedList<Level> levels;
    protected final GameChangeListener gameChangeListener;
    protected final HUDListener hudListener;
    private int currentLevel = 0;
    private final int numberOfMaps;
    private int lives = 3;

    private boolean gameRunning;

    private boolean debug;
    private boolean godMode;

    int blockSize = 20;

    public Game(int numberOfMaps, HUDListener hudListener, boolean debug, String filesLocation){
        this.debug = debug;
        this.numberOfMaps = numberOfMaps;
        this.hudListener = hudListener;

        setBaseFilesFolder(filesLocation);

        gameChangeListener = new GameChangeListener() {
            @Override
            public void levelWon() {
                logger.info("Percent " + levels.get(currentLevel).percentFilledOfMap());

                hudListener.levelWon();

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
                logger.info("\033[1;31mGame Over\033[0m");
                hudListener.gameOver();
                gameRunning = false;
            }

            @Override
            public void restartGame() {
                logger.info("\n\n\n\033[1;38mRestarting Game\033[0m");
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
            String fileName = String.format("level%02d", i);
            Level level = new Level(fileName, this);

            if(!level.getIsInvalid())
                levels.add(level);
        }

        if(levels.size() == 0){
            currentLevel = -1;
            gameRunning = false;
        }

        hudListener.levelChanged(currentLevel);
        hudListener.livesChanged(lives);
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
        gameRunning = true;
        setUpGame(numberOfMaps);

        if(gameRunning) {
            levels.get(currentLevel).setUpBonuses();
            logger.info("\033[32mGame Started\033[0m");
        }else {
            logger.info("\033[31mGame Failed to Start\033[0m");
        }
    }
    public void stopGame() {
        gameRunning = false;
    }
    public void nextLevel() {
        if(currentLevel < levels.size() - 1) {
            currentLevel++;
            lives++;
            levels.get(currentLevel).setUpBonuses();
            hudListener.levelChanged(currentLevel);
            hudListener.livesChanged(lives);
            hudListener.mapFillPercentageChanged(0);
            logger.info("Level Won");
            return;
        }

        hudListener.gameWon();
        logger.info("Game Won");
        gameRunning = false;
    }

    // Lives
    public void removeLife(){
        if(godMode){
            logger.info("\033[1;34mYou was saved by most Powerful Power of God\033[0m");
            return;
        }

        lives--;

        if (lives > 0)
            logger.info("Remaining lives \033[1;34m" + lives + "\033[0m");
        else
            gameChangeListener.gameOver();

        hudListener.livesChanged(lives);
    }
    public void resetLife() {
        lives = 3;
        hudListener.livesChanged(lives);
    }

    // Setters
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }
    public void setBaseFilesFolder(String fileLocation){ Files.setFileLocation(fileLocation);}

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
        if(currentLevel >= 0)
            return levels.get(currentLevel).getPlayerInputListener();
        else
            return null;
    }
    public HUDListener getHudListener() {
        return hudListener;
    }
}
