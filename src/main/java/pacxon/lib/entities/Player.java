package pacxon.lib.entities;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import pacxon.App;
import pacxon.lib.api.Files;
import pacxon.lib.Collisionable;
import pacxon.lib.Level;
import pacxon.lib.listeners.InputListener;

import java.util.ArrayList;

@Log4j2
public class Player extends Entity{

    @Getter protected InputListener inputListener;
    protected Direction wantedDirection = Direction.STOP;
    private boolean creatingRoute = false;
    public ArrayList<Point2D> route = new ArrayList<>();

    public Player(Level level, Point2D startPosition, Point2D direction, int speed) {
        super( level, startPosition, direction, speed);
        loadTextures();

        inputListener = (type, keyCode) -> {
            if(type.equals(KeyEvent.KEY_PRESSED.toString()) && keyCode == KeyCode.W){
                wantedDirection = Direction.UP;
            }
            if(type.equals(KeyEvent.KEY_PRESSED.toString()) && keyCode == KeyCode.S){
                wantedDirection = Direction.DOWN;
            }
            if(type.equals(KeyEvent.KEY_PRESSED.toString()) && keyCode == KeyCode.A){
                wantedDirection = Direction.LEFT;
            }
            if(type.equals(KeyEvent.KEY_PRESSED.toString()) && keyCode == KeyCode.D){
                wantedDirection = Direction.RIGHT;
            }

            if(type.equals(KeyEvent.KEY_RELEASED.toString())){
                wantedDirection = Direction.STOP;
            }
        };
    }

    @Override
    public void update(double deltaTime) {
        this.position = position.add(direction.multiply(deltaTime * speed));
        this.positionRounded = new Point2D(Math.round(position.getX()), Math.round(position.getY()));

        Level.LevelPoint currentLevelPoint = level.tryGetPointOnMap((int)positionRounded.getX(), (int)positionRounded.getY(), Level.LevelPoint.Wall);
        if( Level.LevelPoint.Wall == currentLevelPoint && creatingRoute){
            creatingRoute = false;
            route.clear();
            level.getLevelChangeListener().startFill();
        }

        if(!lastChangePosition.equals(positionRounded)) {
            if (Math.abs(position.getX() - positionRounded.getX()) < 0.08 &&
                Math.abs(position.getY() - positionRounded.getY()) < 0.08) {

                nextPosition = positionRounded.add(direction);
                nextPosition = new Point2D(Math.round(nextPosition.getX()), Math.round(nextPosition.getY()));

                // Out Of Map CHECK
                Point2D nextWantedPosition = positionRounded.add(wantedDirection.direction);
                if(!(nextWantedPosition.getX() >= 0 && nextWantedPosition.getX() <= level.getMapSize().getX() - 1 &&
                     nextWantedPosition.getY() >= 0 && nextWantedPosition.getY() <= level.getMapSize().getY() - 1) ){
                    wantedDirection = Direction.STOP;
                }

                if(!(Level.LevelPoint.Empty == currentLevelPoint && wantedDirection == Direction.STOP)) {
                    currentDirection = wantedDirection;
                }

                if (currentDirection != Direction.STOP) {
                    lastChangePosition = positionRounded;

                    if(Level.LevelPoint.Empty == currentLevelPoint) {
                        creatingRoute = true;
                        route.add(positionRounded);
                        level.getLevelChangeListener().changeToRoute(positionRounded);
                    }
                }

                changeDirection(currentDirection.direction);
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc, int blockSize, int currentAnimation, boolean debug){
        gc.drawImage(textures.get(currentDirection.animationId * 2 + currentAnimation), position.getX() * blockSize, position.getY() * blockSize, blockSize, blockSize);

        if(debug)
            drawDebug(gc, blockSize, 4, Color.ORANGE);
    }

    @Override
    public void loadTextures() {
        textures = new ArrayList<>();
        Files.addTexture( textures, "characters/p/player_left1.png");
        Files.addTexture( textures, "characters/p/player_left2.png");
        Files.addTexture( textures, "characters/p/player_right1.png");
        Files.addTexture( textures, "characters/p/player_right2.png");
        Files.addTexture( textures, "characters/p/player_up1.png");
        Files.addTexture( textures, "characters/p/player_up2.png");
        Files.addTexture( textures, "characters/p/player_down1.png");
        Files.addTexture( textures, "characters/p/player_down2.png");
        Files.addTexture( textures, "characters/p/player_stop1.png");
        Files.addTexture( textures, "characters/p/player_stop2.png");
    }

    // Collision
    @Override
    public void hitBy(Collisionable obj) {
        if(!obj.equals(this)){
            if(level.isNpcCanBeKilled())
                return;

            log.info(App.getLogTextRB().getString("player_was") + "\033[1;31m" + App.getLogTextRB().getString("killed") + "\033[0m");

            level.getGame().removeLife();

            position = new Point2D(0, 0);
            direction = new Point2D(0, 0);
            wantedDirection = Direction.STOP;
            currentDirection = Direction.STOP;

            for(Point2D positionOfWall : route){
                level.getLevelChangeListener().changeToEmpty(positionOfWall);
            }
            route.clear();
        }
    }
}