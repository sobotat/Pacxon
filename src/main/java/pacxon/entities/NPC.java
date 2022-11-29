package pacxon.entities;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import pacxon.Api;
import pacxon.Collisionable;
import pacxon.Level;

import java.util.ArrayList;

public class NPC extends Entity{

    String type;
    Level.LevelPoint hitTarget;
    boolean alive = true;

    public NPC(Level level, Point2D startPosition, Direction direction, String type) {
        super( level, startPosition, direction.direction, 3);

        this.hitTarget = Level.LevelPoint.Wall;
        this.currentDirection = direction;
        this.type = type;
        loadTextures();
    }

    @Override
    public void update(double deltaTime) {
        if(!alive)
            return;

        this.position = position.add(direction.multiply(deltaTime * speed));
        this.positionRounded = new Point2D(Math.round(position.getX()), Math.round(position.getY()));

        if(position.getX() <= 0 || position.getX() >= level.getMapSize().getX() -1 ||
           position.getY() <= 0||  position.getY() >= level.getMapSize().getY() -1 ){
            direction = direction.multiply(-1);
        }

        if(!lastChangePosition.equals(positionRounded)) {
            if ((Math.abs(position.getX() - positionRounded.getX()) < 0.35 &&
                 Math.abs(position.getY() - positionRounded.getY()) < 0.35 ) ){

                nextPosition = positionRounded.add(direction);
                nextPosition = new Point2D(Math.round(nextPosition.getX()), Math.round(nextPosition.getY()));

                Level.LevelPoint nextPoint;
                if(nextPosition.getX() < 0 || nextPosition.getX() > level.getMapSize().getX() ||
                   nextPosition.getY() < 0 || nextPosition.getY() > level.getMapSize().getY() ){
                    nextPoint = hitTarget;
                }else
                    nextPoint = level.tryGetPointOnMap((int) nextPosition.getX(), (int) nextPosition.getY(), hitTarget);

                if(nextPoint == hitTarget && lastChangePosition != positionRounded){
                    lastChangePosition = positionRounded;

                    Level.LevelPoint top = level.tryGetPointOnMap((int)positionRounded.getX(), (int)positionRounded.getY() - 1, hitTarget);
                    Level.LevelPoint bottom = level.tryGetPointOnMap((int)positionRounded.getX(), (int)positionRounded.getY() + 1, hitTarget);
                    Level.LevelPoint left = level.tryGetPointOnMap((int)positionRounded.getX() - 1, (int)positionRounded.getY(), hitTarget);
                    Level.LevelPoint right = level.tryGetPointOnMap((int)positionRounded.getX() + 1, (int)positionRounded.getY(), hitTarget);

                    switch (currentDirection){
                        case UP_LEFT -> {
                            if(left == hitTarget && top == hitTarget) {
                                currentDirection = Direction.DOWN_RIGHT;
                                break;
                            }
                            currentDirection = (left != hitTarget ? Direction.DOWN_LEFT : Direction.UP_RIGHT);
                        }
                        case UP_RIGHT -> {
                            if(right == hitTarget && top == hitTarget) {
                                currentDirection = Direction.DOWN_LEFT;
                                break;
                            }
                            currentDirection = (right != hitTarget ? Direction.DOWN_RIGHT : Direction.UP_LEFT);
                        }
                        case DOWN_LEFT -> {
                            if(left == hitTarget && bottom == hitTarget) {
                                currentDirection = Direction.UP_RIGHT;
                                break;
                            }
                            currentDirection = (left != hitTarget ? Direction.UP_LEFT : Direction.DOWN_RIGHT);
                        }
                        case DOWN_RIGHT -> {
                            if(right == hitTarget && bottom == hitTarget) {
                                currentDirection = Direction.UP_LEFT;
                                break;
                            }
                            currentDirection = (right != hitTarget ? Direction.UP_RIGHT : Direction.DOWN_LEFT);
                        }
                        case UP -> currentDirection = Direction.DOWN;
                        case DOWN -> currentDirection = Direction.UP;
                        case LEFT -> currentDirection = Direction.RIGHT;
                        case RIGHT -> currentDirection = Direction.LEFT;
                    }
                }

                changeDirection(currentDirection.direction);
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc, int blockSize, int currentAnimation, boolean debug){
        if(!alive)
            return;

        int animationId;
        if(!level.getNPCanBeKilled())
            animationId = currentDirection != Direction.STOP ? currentDirection.animationId * 2 : 0;
        else
            animationId = 8;

        gc.drawImage(textures.get(animationId + currentAnimation), position.getX() * blockSize, position.getY() * blockSize, blockSize, blockSize);

        if (debug) {
            drawDebug(gc, blockSize, 4, Color.DARKRED);
            drawNextPointDebug(gc, blockSize, 4, Color.MEDIUMSEAGREEN, direction);
        }
    }

    protected void kill(){
        alive = false;
    }

    @Override
    public void loadTextures() {
        textures = new ArrayList<>();
        Api.addTexture( textures,"characters/" + type + "/npc_left1.png");
        Api.addTexture( textures,"characters/" + type + "/npc_left2.png");
        Api.addTexture( textures,"characters/" + type + "/npc_right1.png");
        Api.addTexture( textures,"characters/" + type + "/npc_right2.png");
        Api.addTexture( textures,"characters/" + type + "/npc_up1.png");
        Api.addTexture( textures,"characters/" + type + "/npc_up2.png");
        Api.addTexture( textures,"characters/" + type + "/npc_down1.png");
        Api.addTexture( textures,"characters/" + type + "/npc_down2.png");
        Api.addTexture( textures,"characters/" + "g" + "/npc_blue1.png");
        Api.addTexture( textures,"characters/" + "g" + "/npc_blue2.png");
        Api.addTexture( textures,"characters/" + "g" + "/npc_white1.png");
        Api.addTexture( textures,"characters/" + "g" + "/npc_white2.png");
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public boolean isInCollision(Collisionable obj) {
        if(alive)
            return super.isInCollision(obj);
        else
            return false;
    }

    @Override
    public void hitBy(Collisionable obj) {
        if(obj instanceof Player && level.getNPCanBeKilled() && alive){
            System.out.println("Player have killed \033[1;31mNPC\033[0m");
            kill();
        }
    }
}
