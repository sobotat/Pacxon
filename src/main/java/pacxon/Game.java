package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.LinkedList;

public class Game {

    LinkedList<Level> levels;
    int currentLevel = 0;
    int lives = 3;

    boolean gameRunning;
    boolean gameWin;

    int blockSize = 20;

    public Game(int numberOfMaps){
        levels = new LinkedList<>();
        Wall.loadTextures();
        //Level.generateLevelTemplate();

        for (int i = 1; i <= numberOfMaps; i++) {
            String fileName = String.format("level0%d.txt", i);
            levels.add(new Level(fileName));
        }
    }

    public void draw(GraphicsContext gc){
        clearCanvas(gc);

        levels.get(currentLevel).draw(gc, blockSize);
    }

    private void clearCanvas(GraphicsContext gc){
        gc.setFill(Color.web("#282c30"));
        gc.fillRect(0,0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }
}
