package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Entity {

    Point2D position;
    Point2D startPosition;
    int direction, directionNext;
    ArrayList<Image> textures;

    public Entity(Point2D startPosition){
        this.startPosition = startPosition;
        this.position = startPosition;
        this.direction = -1;
        this.directionNext = -1;
    }

    abstract public void draw(GraphicsContext gc, int blockSize);
    abstract public void loadTextures();
}
