package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Player extends Entity{
    public Player(Point2D startPosition, ArrayList<Image> textures) {
        super(startPosition, textures);
    }
}
