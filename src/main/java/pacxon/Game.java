package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Game {

    private final double width, height;
    private int score1, score2;
    private double blockSize;

    private Bat leftBat, rightBat;
    private Ball ball;

    public Game(double blockSize, double width, double height){
        this.blockSize = blockSize;
        this.width = width;
        this.height = height;

        this.ball = new Ball(this, new Point2D(250,150), new Point2D(100, 100));
        this.leftBat = new Bat(this, new Point2D(blockSize, 100), 80);
        this.rightBat = new Bat(this, new Point2D(width - blockSize * 2, 150), -80);
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getBlockSize() { return blockSize; }

    public void simulate(double deltaTime){
        ball.simulate(deltaTime);
        leftBat.simulate(deltaTime);
        rightBat.simulate(deltaTime);
    }

    public void draw(GraphicsContext gc){
        clearCanvas(gc);
        drawMap(gc);
        drawScore(gc, width / 2, 60 + blockSize, 15, 50);

        ball.draw( gc);
        leftBat.draw( gc);
        rightBat.draw( gc);
    }

    public void drawMap(GraphicsContext gc){
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0,0, width, blockSize);

        int x = (int)(width / 2) - (int)(blockSize / 2);
        int y = 0;
        while (y < height){
            gc.fillRect(x, y, blockSize, blockSize);

            y += blockSize * 2;
        }
    }

    // Draw Game Score
    public void drawScore( GraphicsContext gc, double cx, double y, double middle, int fontSize){

        gc.setFill(Color.GHOSTWHITE);
        gc.setFont(Font.font("San Serif", FontWeight.BOLD, fontSize));

        // Left
        gc.fillText(String.valueOf(score1), cx - middle - fontSize, y);

        // Right
        gc.fillText(String.valueOf(score2), cx + middle + (fontSize / 2), y);
    }

    // Resets Canvas
    public void clearCanvas(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0, width, height);
        //gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
    }
}
