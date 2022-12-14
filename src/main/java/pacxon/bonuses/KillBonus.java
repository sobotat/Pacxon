package pacxon.bonuses;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pacxon.Level;

import java.util.Timer;
import java.util.TimerTask;

public class KillBonus extends Bonus {

    public KillBonus(Level level, Point2D position, int time, int spawnDelay) {
        super(level, "kill-b", position, time, spawnDelay);

        loadTextures();
    }

    @Override
    void activateBonus() {
        if(alreadyActivated || !spawned)
            return;

        alreadyActivated = true;
        level.setTmpSpeedForNPC(1);
        level.enableNPCCanBeKilled();

        System.out.println("Kill Bonus \033[1;32mActivated\033[0m");

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               level.resetSpeedForNPC();
               level.disableNPCCanBeKilled();
                System.out.println("Kill Bonus \033[1;31mDeactivated\033[0m");
            }
        }, time * 1000L);
    }

    @Override
    public void draw(GraphicsContext gc, int blockSize, int currentAnimation, boolean debug) {
        if(!(alreadyActivated || !spawned))
            gc.drawImage(textures.get(0), position.getX() * blockSize, position.getY() * blockSize, blockSize, blockSize);

        if (debug) {
            drawDebug(gc, blockSize, 4, Color.RED);
        }
    }

    @Override
    public void drawDebug(GraphicsContext gc, int blockSize, int offset, Color color) {
        gc.setFill(color);
        gc.fillRect( position.getX() * blockSize + offset, position.getY() * blockSize + offset,
                    blockSize - offset * 2, blockSize - offset * 2);
    }
}
