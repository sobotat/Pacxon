package pacxon;

import java.util.LinkedList;

public class Level {

    LinkedList<Map> maps;
    int currentLevel = 0;
    int lives = 3;

    boolean gameRunning;
    boolean gameWin;

    public Level(int numberOfMaps){
        maps = new LinkedList<>();

        for (int i = 1; i <= numberOfMaps; i++) {
            String fileName = String.format("level0%d.txt", i);
            maps.add(new Map(fileName, true));
        }
    }
}
