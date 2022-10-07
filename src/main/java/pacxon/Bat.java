package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bat {

    private final Game game;
    private Point2D position;
    private double direction;
    private Point2D size;

    public Bat(Game game, Point2D position, double direction){
        this.game = game;
        this.position = position;
        this.direction = direction;

        this.size = new Point2D(game.getBlockSize(), 75);
    }

    public void simulate(double deltaTime){
        position = new Point2D(position.getX(), position.getY() + (direction * deltaTime));

        if (position.getY() <= game.getBlockSize()){
            direction = direction < 0 ? direction * -1 : direction;
        }else if(position.getY() >= game.getHeight() - size.getY()){
            direction = direction < 0 ? direction : direction * -1;
        }
    }

    public void draw(GraphicsContext gc){
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(position.getX(), position.getY(), size.getX(), size.getY());
    }
}
