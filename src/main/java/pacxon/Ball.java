package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ball {
    private final Game game;
    private Point2D position, direction;

    public Ball(Game game, Point2D position, Point2D direction){
        this.game = game;
        this.position = position;
        this.direction = direction;
    }

    public void simulate(double deltaTime){
        position = position.add(direction.multiply( deltaTime));

        if (position.getX() <= 0){
            direction = new Point2D( direction.getX() < 0 ? direction.getX() * -1 : direction.getX(),
                                    direction.getY());
        }else if(position.getX() >= game.getWidth() - game.getBlockSize()){
            direction = new Point2D( direction.getX() < 0 ? direction.getX() : direction.getX() * -1,
                    direction.getY());
        }
        if (position.getY() <= game.getBlockSize()){
            direction = new Point2D( direction.getX(),
                                     direction.getY() < 0 ? direction.getY() * -1 : direction.getY());
        }else if(position.getY() >= game.getHeight() - game.getBlockSize()){
            direction = new Point2D( direction.getX(),
                    direction.getY() < 0 ? direction.getY() : direction.getY() * -1 );
        }
    }

    public void draw(GraphicsContext gc){
        gc.setFill(Color.GHOSTWHITE);
        gc.fillRect(position.getX(), position.getY(), game.getBlockSize(), game.getBlockSize());
    }
}
