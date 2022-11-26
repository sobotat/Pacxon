package pacxon.listeners;

import javafx.geometry.Point2D;

public interface LevelChangeListener {

    void changeToWall(Point2D position);
    void changeToRoute(Point2D position);
    void changeToEmpty(Point2D position);

    void startFill();
}
