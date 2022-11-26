package pacxon.entities;

import javafx.geometry.Point2D;
import pacxon.Level;

public class NPC_Magenta extends NPC{

    public NPC_Magenta(Level level, Point2D startPosition, Direction direction, int bonusSpeed) {
        super(level, startPosition, direction, "m");
        speed += bonusSpeed;
    }
}
