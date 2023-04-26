package pacxon.lib.entities;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import lombok.Getter;
import pacxon.lib.Collisionable;
import pacxon.lib.Drawable;
import pacxon.lib.Level;

import java.util.ArrayList;

public abstract class Entity implements Collisionable, Drawable {

    protected ArrayList<Image> textures = new ArrayList<>();
    @Getter protected Point2D position, positionRounded, lastChangePosition = new Point2D(-1, -1);
    @Getter protected Point2D startPosition, nextPosition;
    protected Point2D direction;
    protected Direction currentDirection = Direction.STOP;
    protected Level level;
    protected double speed, originalSpeed;

    public enum Direction{
        UP(new Point2D(0, -1),2), UP_LEFT(new Point2D(-1, -1), 0), UP_RIGHT(new Point2D(1, -1), 1),
        DOWN(new Point2D(0, 1),3), DOWN_LEFT(new Point2D(-1, 1), 0), DOWN_RIGHT(new Point2D(1, 1), 1),
        LEFT(new Point2D(-1, 0), 0), RIGHT(new Point2D(1, 0), 1), STOP(new Point2D(0, 0), 4);

        final Point2D direction;
        final int animationId;
        Direction(Point2D dir, int animationId) {
            this.direction = dir;
            this.animationId = animationId;
        }
    }

    public Entity( Level level, Point2D startPosition, Point2D direction, int speed){
        this.startPosition = startPosition;
        this.position = startPosition;
        this.positionRounded = new Point2D(Math.round(position.getX()), Math.round(position.getY()));
        this.direction = direction;
        this.level = level;

        this.speed = speed;
        this.originalSpeed = speed;
    }

    abstract public void update(double deltaTime);

    @Override
    abstract public void draw(GraphicsContext gc, int blockSize, int currentAnimation, boolean debug);

    @Override
    public void drawDebug(GraphicsContext gc, int blocksize, int offset, Color color){
        int posX = (int) positionRounded.getX();
        int posY = (int) positionRounded.getY();

        gc.setFill(color);
        gc.fillRect( posX * blocksize + offset, posY * blocksize + offset,
                    blocksize - offset * 2, blocksize - offset * 2);
    }

    public void drawNextPointDebug(GraphicsContext gc, int blocksize, int offset, Color color, Point2D direction){
        Point2D nextPoint = positionRounded.add(direction);
        nextPoint = new Point2D(Math.round(nextPoint.getX()), Math.round(nextPoint.getY()));

        gc.setFill(color);
        gc.fillRect( nextPoint.getX() * blocksize + offset, nextPoint.getY() * blocksize + offset,
                blocksize - offset * 2, blocksize - offset * 2);
    }

    @Override
    abstract public void loadTextures();

    protected void changeDirection(Point2D direction){
        this.direction = direction;
    }

    // Collisionable Interface
    @Override
    public Point2D getLocation() {
        return positionRounded;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    public void resetSpeed(){
        this.speed = originalSpeed;
    }
}
