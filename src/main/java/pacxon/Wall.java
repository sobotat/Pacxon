package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Wall {

    public enum WallStyle {
        STRAIGHT, CURVE, T_CROSS, CROSS, END, POINT, POINT_DARK, POINT_ROUTE
    }

    private static ArrayList<Image> textures;

    public static void draw(GraphicsContext gc, Point2D position, int blockSize, int style){
        if(textures == null)
            return;

        gc.drawImage(textures.get(style), position.getX() * blockSize, position.getY() * blockSize, blockSize, blockSize);
    }

    public static void loadTextures() {
        Wall.textures = new ArrayList<>();
        Files.addTexture( Wall.textures,"walls/wall_straight.png");
        Files.addTexture( Wall.textures,"walls/wall_curve.png");
        Files.addTexture( Wall.textures,"walls/wall_tcross.png");
        Files.addTexture( Wall.textures,"walls/wall_cross.png");
        Files.addTexture( Wall.textures,"walls/wall_end.png");
        Files.addTexture( Wall.textures,"walls/wall_point.png");
        Files.addTexture( Wall.textures,"walls/wall_point_dark.png");
        Files.addTexture( Wall.textures,"walls/wall_point_route.png");
    }
}
