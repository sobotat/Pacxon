package pacxon.lib.bonuses;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pacxon.App;
import pacxon.lib.Level;

import java.util.Timer;
import java.util.TimerTask;

@Log4j2
public class StopBonus extends Bonus {

    public StopBonus(Level level, Point2D position, int time, int spawnDelay) {
        super(level, "stop-b", position, time, spawnDelay);

        loadTextures();
    }

    @Override
    void activateBonus() {
        if(alreadyActivated || !spawned)
            return;

        alreadyActivated = true;
        level.setTmpSpeedForNPC(0);

        log.info("Stop Bonus \033[1;32m" + App.getLogTextRB().getString("activated") + "\033[0m");

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               level.resetSpeedForNPC();
               log.info("Stop Bonus \033[1;31m" + App.getLogTextRB().getString("deactivated") + "\033[0m");
            }
        }, time * 1000L);
    }

    @Override
    public void draw(GraphicsContext gc, int blockSize, int currentAnimation, boolean debug) {
        if(!(alreadyActivated || !spawned))
            gc.drawImage(textures.get(0), position.getX() * blockSize, position.getY() * blockSize, blockSize, blockSize);

        if (debug) {
            drawDebug(gc, blockSize, 4, Color.LIGHTBLUE);
        }
    }

    @Override
    public void drawDebug(GraphicsContext gc, int blockSize, int offset, Color color) {
        gc.setFill(color);
        gc.fillRect( position.getX() * blockSize + offset, position.getY() * blockSize + offset,
                    blockSize - offset * 2, blockSize - offset * 2);
    }
}
