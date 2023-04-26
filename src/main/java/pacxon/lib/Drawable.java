package pacxon.lib;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public interface Drawable {

    void draw(GraphicsContext gc, int blockSize, int currentAnimation, boolean debug);
    void drawDebug(GraphicsContext gc, int blockSize, int offset, Color color);
    void loadTextures();
}
