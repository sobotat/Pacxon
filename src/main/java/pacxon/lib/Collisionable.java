package pacxon.lib;

import javafx.geometry.Point2D;

public interface Collisionable {

    default boolean isInCollision(Collisionable obj){
        return this.getLocation().equals(obj.getLocation());
    }

    void hitBy(Collisionable obj);
    Point2D getLocation();
}
