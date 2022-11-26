package pacxon;

import javafx.geometry.Point2D;

public interface Collisionable {

    default boolean isInCollision(Collisionable obj){
        return (this.getLocation().getX() == obj.getLocation().getX() &&
                this.getLocation().getY() == obj.getLocation().getY());
    }

    void hitBy(Collisionable obj);
    Point2D getLocation();
}
