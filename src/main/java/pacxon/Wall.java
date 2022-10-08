package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Wall {

    private static ArrayList<Image> textures;

    public static void draw(GraphicsContext gc, Point2D position, int blockSize, int style){
        if(textures == null)
            return;

        gc.drawImage(textures.get(style), position.getX() * blockSize, position.getY() * blockSize, blockSize, blockSize);
    }

    public static void loadTextures() {
        Wall.textures = new ArrayList<>();
        Api.addTexture( Wall.textures,"walls/wall_straight.png");
        Api.addTexture( Wall.textures,"walls/wall_curve.png");
        Api.addTexture( Wall.textures,"walls/wall_tcross.png");
        Api.addTexture( Wall.textures,"walls/wall_cross.png");
        Api.addTexture( Wall.textures,"walls/wall_end.png");
        Api.addTexture( Wall.textures,"walls/wall_point.png");
    }
}
