package pacxon.bonuses;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pacxon.Level;

import java.util.Timer;
import java.util.TimerTask;

public class SpeedBonus extends Bonus {

    int speed;

    public SpeedBonus(Level level, Point2D position, int time, int spawnDelay, int speed) {
        super(level, "speed-b", position, time, spawnDelay);
        this.speed = speed;
        loadTextures();
    }

    @Override
    void activateBonus() {
        if(alreadyActivated || !spawned)
            return;

        alreadyActivated = true;
        level.setTmpSpeedForPlayer(speed);

        System.out.println("Speed Bonus \033[1;32mActivated\033[0m");

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               level.setSpeedForPlayerToOriginate();
                System.out.println("Speed Bonus \033[1;31mDeactivated\033[0m");
            }
        }, time * 1000L);
    }

    @Override
    public void draw(GraphicsContext gc, int blockSize, int currentAnimation, boolean debug) {
        if(!(alreadyActivated || !spawned))
            gc.drawImage(textures.get(0), position.getX() * blockSize, position.getY() * blockSize, blockSize, blockSize);

        if (debug) {
            drawDebug(gc, blockSize, 4, Color.LIGHTYELLOW);
        }
    }

    @Override
    public void drawDebug(GraphicsContext gc, int blockSize, int offset, Color color) {
        gc.setFill(color);
        gc.fillRect( position.getX() * blockSize + offset, position.getY() * blockSize + offset,
                    blockSize - offset * 2, blockSize - offset * 2);
    }
}
