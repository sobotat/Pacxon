package pacxon.bonuses;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import pacxon.Api;
import pacxon.Collisionable;
import pacxon.Drawable;
import pacxon.Level;
import pacxon.entities.Player;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Bonus implements Collisionable, Drawable {

    ArrayList<Image> textures = new ArrayList<>();
    Level level;
    String type;
    Point2D position;
    Timer timer;
    int time, spawnDelay;
    boolean alreadyActivated, spawned;

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
                System.out.println("New Bonus \033[1;34mSpawn\033[0m");
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
        Api.addTexture( textures, "bonus/" + type + ".png");
    }
}
