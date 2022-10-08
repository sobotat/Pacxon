package pacxon;

import javafx.scene.canvas.GraphicsContext;

import java.util.LinkedList;

public class Game {

    LinkedList<Level> levels;
    int currentLevel = 0;
    int lives = 3;

    boolean gameRunning;
    boolean gameWin;

    public Game(int numberOfMaps){
        levels = new LinkedList<>();
        Level.generateLevelTemplate();

        for (int i = 1; i <= numberOfMaps; i++) {
            String fileName = String.format("level0%d.txt", i);
            levels.add(new Level(fileName));
        }
    }

    public void draw(GraphicsContext gc){
        int blockSize = 25;
        levels.get(currentLevel).draw(gc, blockSize);
    }
}
