package pacxon.entities;

import javafx.geometry.Point2D;
import pacxon.Level;

public class NPC_Yellow extends NPC {

    public NPC_Yellow(Level level, Point2D startPosition, Direction direction) {
        super(level, startPosition, direction, "y");
    }

    @Override
    public void update(double deltaTime) {
        if(currentDirection == Direction.STOP)
            return;

        this.position = position.add(direction.multiply(deltaTime * speed));
        this.positionRounded = new Point2D(Math.round(position.getX()), Math.round(position.getY()));

        if(!lastChangePosition.equals(positionRounded)) {
            if (Math.abs(position.getX() - positionRounded.getX()) < 0.05 &&
                Math.abs(position.getY() - positionRounded.getY()) < 0.05) {
                lastChangePosition = positionRounded;

                nextPosition = positionRounded.add(direction);
                nextPosition = new Point2D(Math.round(nextPosition.getX()), Math.round(nextPosition.getY()));

                Level.LevelPoint nextPoint = nextPoint = level.tryGetPointOnMap((int) nextPosition.getX(), (int) nextPosition.getY(), hitTarget);

                Level.LevelPoint top = level.tryGetPointOnMap((int) positionRounded.getX(), (int) positionRounded.getY() - 1, hitTarget);
                Level.LevelPoint bottom = level.tryGetPointOnMap((int) positionRounded.getX(), (int) positionRounded.getY() + 1, hitTarget);
                Level.LevelPoint left = level.tryGetPointOnMap((int) positionRounded.getX() - 1, (int) positionRounded.getY(), hitTarget);
                Level.LevelPoint right = level.tryGetPointOnMap((int) positionRounded.getX() + 1, (int) positionRounded.getY(), hitTarget);

                Level.LevelPoint forward;   Direction forwardDir;
                Level.LevelPoint backward;  Direction backwardDir;
                Level.LevelPoint toLeft;    Direction toLeftDir;
                Level.LevelPoint toRight;   Direction toRightDir;

                switch (currentDirection){
                    case LEFT -> {
                        forward = left; backward = right; toLeft = bottom; toRight = top;
                        forwardDir = Direction.LEFT; backwardDir = Direction.RIGHT; toLeftDir = Direction.DOWN; toRightDir = Direction.UP;
                    }
                    case RIGHT -> {
                        forward = right; backward = left; toLeft = top; toRight = bottom;
                        forwardDir = Direction.RIGHT; backwardDir = Direction.LEFT; toLeftDir = Direction.UP; toRightDir = Direction.DOWN;
                    }
                    case UP -> {
                        forward = top; backward = bottom; toLeft = left; toRight = right;
                        forwardDir = Direction.UP; backwardDir = Direction.DOWN; toLeftDir = Direction.LEFT; toRightDir = Direction.RIGHT;
                    }
                    case DOWN -> {
                        forward = bottom; backward = top; toLeft = right; toRight = left;
                        forwardDir = Direction.DOWN; backwardDir = Direction.UP; toLeftDir = Direction.RIGHT; toRightDir = Direction.LEFT;
                    }
                    default -> {
                        System.out.println("\033[1;31mWrong Direction Input to Yellow Ghost\033[0m");
                        currentDirection = Direction.STOP;
                        changeDirection(currentDirection.direction);
                        return;
                    }
                }

                //System.out.println(toLeft + " " + toRight + " | " + forward + " " + backward + " LDir " + currentDirection);

                if(forward != hitTarget){
                    if(toRight != hitTarget) {
                        // Turn Right
                        currentDirection = toRightDir;
                    }
                }else{
                    if(toLeft != hitTarget){
                        // Turn Left
                        currentDirection = toLeftDir;
                    }else if(toRight != hitTarget){
                        // Turn Right
                        currentDirection = toRightDir;
                    }else{
                        // Turn Back
                        currentDirection = backwardDir;
                    }
                }

                changeDirection(currentDirection.direction);
            }
        }
    }
}
