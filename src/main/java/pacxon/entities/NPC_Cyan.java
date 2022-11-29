package pacxon.entities;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pacxon.Collisionable;
import pacxon.Level;

import java.util.Timer;
import java.util.TimerTask;

public class NPC_Cyan extends NPC {

    boolean spawned;
    int spawnDelay;
    Timer timer;

    public NPC_Cyan(Level level, Point2D startPosition, Direction direction, int spawnDelay) {
        super(level, startPosition, direction, "c");

        this.spawnDelay = spawnDelay;
        this.hitTarget = Level.LevelPoint.Empty;
        this.nextPosition = new Point2D(-1, -1);
    }

    @Override
    public void update(double deltaTime) {
        if(spawned) {
            super.update(deltaTime);
            return;
        }

        Level.LevelPoint levelPoint = level.tryGetPointOnMap((int)position.getX(), (int)position.getY(), hitTarget);
        if(levelPoint == Level.LevelPoint.Wall && timer == null){
            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    spawned = true;
                }
            }, (spawnDelay * 1000L));
        }
    }

    @Override
    public void draw(GraphicsContext gc, int blockSize, int currentAnimation, boolean debug) {
        if(!spawned) {
            if(debug)
                drawDebug(gc, blockSize, 5, Color.CYAN);
            return;
        }

        super.draw(gc, blockSize, currentAnimation, debug);
    }

    public boolean isSpawned(){
        return spawned;
    }

    @Override
    public boolean isInCollision(Collisionable obj) {
        if(isSpawned() && alive)
            return super.isInCollision(obj);
        else
            return false;
    }
}
