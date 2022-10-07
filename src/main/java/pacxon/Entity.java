package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Entity {

    Point2D position;
    Point2D startPosition;
    int direction, directionNext;
    ArrayList<Image> textures;

    public Entity(Point2D startPosition, ArrayList<Image> textures){
        this.startPosition = startPosition;
        this.position = startPosition;
        this.direction = -1;
        this.directionNext = -1;
    }
}
