package pacxon.entities;

import javafx.geometry.Point2D;
import pacxon.Level;

public class NPC_Red extends NPC{

    public NPC_Red(Level level, Point2D startPosition, Direction direction) {
        super(level, startPosition, direction, "r");
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        if (Math.abs(position.getX() - positionRounded.getX()) < 0.35 &&
            Math.abs(position.getY() - positionRounded.getY()) < 0.35) {

            if(nextPosition.getX() == 0 || nextPosition.getX() == level.getMapSize().getX() -1 ||
               nextPosition.getY() == 0 || nextPosition.getY() == level.getMapSize().getY() -1 ){
                return;
            }

            Level.LevelPoint nextPoint = level.tryGetPointOnMap((int) nextPosition.getX(), (int) nextPosition.getY(), hitTarget);
            if(nextPoint == Level.LevelPoint.Wall) {
                level.getLevelChangeListener().changeToEmpty(nextPosition);
            }
        }
    }
}
