package pacxon.lib.bonuses;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pacxon.lib.api.Files;
import pacxon.lib.Collisionable;
import pacxon.lib.Drawable;
import pacxon.lib.Level;
import pacxon.lib.entities.Player;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Bonus implements Collisionable, Drawable {
    private static final Logger logger = LogManager.getLogger(Bonus.class.getName());

    protected ArrayList<Image> textures = new ArrayList<>();
    protected Level level;
    protected String type;
    protected Point2D position;
    protected Timer timer;
    protected int time, spawnDelay;
    protected boolean alreadyActivated, spawned;

    public Bonus(Level level, String type, Point2D position, int time, int spawnDelay) {
        this.level = level;
        this.type = type;
        this.position = position;
        this.time = time;
        this.spawnDelay = spawnDelay;
    }

    public void setUpBonus(){
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                spawned = true;
                timer = null;
                logger.info("New Bonus \033[1;34mSpawn\033[0m");
            }
        }, spawnDelay * 1000L);
    }

    abstract void activateBonus();

    @Override
    public void hitBy(Collisionable obj) {
        if(obj instanceof Player)
            activateBonus();
    }

    @Override
    public Point2D getLocation() {
        return position;
    }

    @Override
    public void loadTextures() {
        textures = new ArrayList<>();
        Files.addTexture( textures, "bonus/" + type + ".png");
    }
}
