package pacxon.lib;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import pacxon.App;
import pacxon.lib.api.Files;
import pacxon.lib.listeners.GameChangeListener;
import pacxon.lib.listeners.HUDListener;
import pacxon.lib.listeners.InputListener;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

@Log4j2
public class Game {

    private LinkedList<Level> levels;
    @Getter protected final GameChangeListener gameChangeListener;
    @Getter protected final HUDListener hudListener;
    private int currentLevel = 0;
    private final int numberOfMaps;
    private int lives = 3;

    @Getter private boolean gameRunning;
    private LocalDateTime gameStartTime;

    @Getter private boolean debug;
    @Getter private boolean godMode;

    int blockSize = 20;

    public Game(int numberOfMaps, HUDListener hudListener, boolean debug, String filesLocation){
        this.debug = debug;
        this.numberOfMaps = numberOfMaps;
        this.hudListener = hudListener;
        hudListener.livesChanged(lives);

        setBaseFilesFolder(filesLocation);

        gameChangeListener = new GameChangeListener() {
            @Override
            public void levelWon() {
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
                log.info("\033[1;31m" + App.getLogTextRB().getString("game_over") + "\033[0m");
                hudListener.gameOver();
                gameRunning = false;
            }

            @Override
            public void restartGame() {
                log.info("\n\n\n\033[1;38m"+ App.getLogTextRB().getString("restarting_game") +"\033[0m");
                gameRunning = false;
                currentLevel = 0;
                setUpGame(numberOfMaps);
            }
        };
    }

    public void setUpGame(int numberOfMaps){
        gameStartTime = LocalDateTime.now();
        levels = new LinkedList<>();

        Wall.loadTextures();
        for (int i = 1; i <= numberOfMaps; i++) {
            String fileName = String.format("level%02d", i);
            Level level = new Level(fileName, this);

            if(!level.isInvalidLevel())
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
            log.info("\033[32m" + App.getLogTextRB().getString("game_started") + "\033[0m");
        }else {
            log.info("\033[31m" + App.getLogTextRB().getString("game_failed_to_start") + "\033[0m");
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
            log.info(App.getLogTextRB().getString("level_won"));
            return;
        }

        hudListener.gameWon();
        log.info(App.getLogTextRB().getString("game_won") + " - \033[1;36m" + playTime() + "\033[0m");
        gameRunning = false;
    }

    // Lives
    public void removeLife(){
        if(godMode){
            log.info("\033[1;34m" + App.getLogTextRB().getString("god_mode") + "\033[0m");
            return;
        }

        lives--;

        if (lives > 0)
            log.info(App.getLogTextRB().getString("remaining_lives") + " \033[1;34m" + lives + "\033[0m");
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
    public InputListener getCurrentInputListener(){
        if(currentLevel >= 0)
            return levels.get(currentLevel).getPlayerInputListener();
        else
            return null;
    }

    // PlayTime
    public String playTime(){
        LocalDateTime now = LocalDateTime.now();

        long gameStartSeconds = gameStartTime.toEpochSecond(ZoneOffset.UTC);
        long nowSeconds = now.toEpochSecond(ZoneOffset.UTC);

        double playTimeSeconds = nowSeconds - gameStartSeconds;

        // Hours
        long hour = (long)(playTimeSeconds / (double)(24 * 60));
        playTimeSeconds -= hour * (24 * 60);

        // Hours
        long minutes = (long)(playTimeSeconds / (double)(60));
        playTimeSeconds -= minutes * (60);

        long seconds = (long) playTimeSeconds;

        return String.format("%02dh %02dm %02ds", hour, minutes, seconds);
    }
}
