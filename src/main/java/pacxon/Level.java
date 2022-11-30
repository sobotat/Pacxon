package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pacxon.bonuses.*;
import pacxon.entities.*;
import pacxon.listeners.InputListener;
import pacxon.listeners.LevelChangeListener;

import java.util.ArrayList;
import java.util.LinkedList;

public class Level {

    protected Point2D mapSize;
    private ArrayList<ArrayList<LevelPoint>> map;
    private ArrayList<Entity> entities;
    private ArrayList<Bonus> bonuses;
    protected Game game;

    private int currentAnimation = 0;
    private double timeFromLastAnimation;
    private boolean npcCanBeKilled;

    private final LevelChangeListener levelChangeListener;

    public enum LevelPoint{
        Wall, Wall_Route, Wall_Temp, Empty
    }

    public Level(String levelFileName, Game game){
        this.game = game;
        loadLevel(levelFileName);
        //loadEntitiesFromMap();
        generateMap( (int)mapSize.getX(), (int)mapSize.getY());
        App.gameViewController.getHudListener().mapFillPercentageChanged((int) percentFilledOfMap());

        levelChangeListener = new LevelChangeListener(){
            @Override
            public void changeToWall(Point2D position) {
                map.get((int) position.getY()).set((int) position.getX(), LevelPoint.Wall);
                updateCurrentLevelFillPercent();
            }

            @Override
            public void changeToRoute(Point2D position) {
                map.get((int) position.getY()).set((int) position.getX(), LevelPoint.Wall_Route);
            }

            @Override
            public void changeToEmpty(Point2D position) {
                map.get((int) position.getY()).set((int) position.getX(), LevelPoint.Empty);
            }

            @Override
            public void startFill() {
                LinkedList<Thread> threads = new LinkedList<>();

                for (Entity entity : entities) {
                    if(entity instanceof NPC npc){
                        if(!(npc instanceof NPC_Cyan) && npc.isAlive()) {
                            Runnable runnable = () -> {
                                fillMap(npc.getPositionRounded());
                            };
                            Thread thread = new Thread(runnable);
                            thread.setDaemon(true);
                            threads.add(thread);
                        }
                    }
                }

                if(game.isDebug())
                    System.out.println("\033[1;31mFill Started\033[0m");

                for(Thread thread : threads){
                    thread.start();
                }

                boolean running = true;
                int lastCountOfRunning = 0;
                while(running){
                    running = false;
                    int countOfRunning = 0;
                    for(Thread thread : threads){
                        if(thread.isAlive()) {
                            running = true;
                            countOfRunning++;
                        }
                    }

                    if(countOfRunning != lastCountOfRunning){
                        lastCountOfRunning = countOfRunning;
                        if(game.isDebug())
                            System.out.println("\t -> Remaining FillThreads [\033[1;31m" + lastCountOfRunning + "\033[0m]");
                    }
                }

                finishFill();
                App.gameViewController.getHudListener().mapFillPercentageChanged((int) percentFilledOfMap());
            }
        };
    }

    public void update(double deltaTime){
        timeFromLastAnimation += deltaTime;
        if(timeFromLastAnimation > 0.15){
            timeFromLastAnimation = 0;
            currentAnimation = currentAnimation == 0 ? 1 : 0;
        }

        Player player = null;
        for (Entity entity : entities) {
            entity.update(deltaTime);

            if(entity instanceof Player pl)
                player = pl;
        }

        if(player == null)
            return;

        for(Entity entity : entities) {
            if(!(entity instanceof Player)){
                if (entity.isInCollision(player)) {
                    player.hitBy(entity);
                    entity.hitBy(player);
                }

                for (Point2D point : player.route){
                    if(point.equals(entity.getNextPosition())) {
                        player.hitBy(entity);
                        return;
                    }
                }
            }
        }

        for(Bonus bonus : bonuses){
            if( bonus.isInCollision(player)){
                bonus.hitBy(player);
            }
        }
    }

    public void draw(GraphicsContext gc, int blockSize, boolean debug){

        for (int y = 0; y < map.size(); y++) {
            ArrayList<LevelPoint> row = map.get(y);
            for (int x = 0; x < row.size(); x++) {
                LevelPoint item = row.get(x);

                if (item == LevelPoint.Wall) {
                    Wall.draw(gc, new Point2D(x, y), blockSize, Wall.WallStyle.POINT.ordinal());
                }else if (item == LevelPoint.Wall_Route){
                    Wall.draw(gc, new Point2D(x, y), blockSize, Wall.WallStyle.POINT_ROUTE.ordinal());
                }else if (item == LevelPoint.Empty){
                    Wall.draw(gc, new Point2D(x,y), blockSize, Wall.WallStyle.POINT_DARK.ordinal());
                }
            }
        }

        for (Entity entity: entities) {
            entity.draw(gc, blockSize, currentAnimation, debug);
        }

        for (Bonus bonus: bonuses) {
            bonus.draw(gc, blockSize, currentAnimation, debug);
        }
    }

    public InputListener getPlayerInputListener(){
        for (Entity entity : entities) {
            if(entity instanceof Player player){
                return player.getInputListener();
            }
        }
        return null;
    }

    public LevelChangeListener getLevelChangeListener(){
        return levelChangeListener;
    }

    public LevelPoint tryGetPointOnMap( int x, int y, LevelPoint elseReturn){
        try{
            return map.get(y).get(x);
        }catch (IndexOutOfBoundsException e){
            return elseReturn;
        }
    }

    // Map and Level
    private void generateMap(int x, int y){
        map = new ArrayList<>(y);

        for (int i = 0; i < y; i++) {
            ArrayList<LevelPoint> line = new ArrayList<>(y);

            for (int j = 0; j < x; j++) {
                if(i == 0 || i == y - 1){
                    line.add(LevelPoint.Wall);
                }else{
                    if ((j == 0 || j == x - 1)) {
                        line.add(LevelPoint.Wall);
                    } else {
                        line.add(LevelPoint.Empty);
                    }
                }
            }
            map.add(line);
        }
    }
    private void loadLevel(String levelFileName){
        entities = new ArrayList<>();
        bonuses = new ArrayList<>();

        System.out.println("Loading Level \033[1;32m" + levelFileName +"\033[0m");
        String read = Files.readString( "/levels/" + levelFileName);
        if (read.equals("")) return;

        try {
            JSONObject levelObj = new JSONObject(read);
            int mapX = levelObj.getInt("mapX");
            int mapY = levelObj.getInt("mapY");
            mapSize = new Point2D(mapX, mapY);
            System.out.println("\033[1;32mMap\033[0m Loaded \t\t\t[" + mapX + "," + mapY + "]");

            JSONObject playerObj = levelObj.getJSONObject("Player");
            int playerPositionX = playerObj.getInt("positionX");
            int playerPositionY = playerObj.getInt("positionY");
            int playerSpeed = playerObj.getInt("speed");
            Point2D playerPosition = new Point2D( playerPositionX, playerPositionY);
            entities.add(new Player( this, playerPosition, new Point2D(0, 0), playerSpeed));
            System.out.println("\033[1;34mPlayer\033[0m Loaded on \t[" + playerPosition.getX() + "," + playerPosition.getY() + "]");

            JSONArray npcArrayObj = levelObj.getJSONArray("NPCs");
            for (int n = 0; n < npcArrayObj.length(); n++) {
                JSONObject npcObj = npcArrayObj.getJSONObject(n);
                String type = npcObj.getString("type");

                Point2D npcPosition = new Point2D(npcObj.getInt("positionX"),
                        npcObj.getInt("positionY"));
                Entity.Direction npcDirection = Entity.Direction.valueOf(npcObj.getString("direction"));

                switch (type){
                    case "c" -> {
                        int spawnDelay = npcObj.getInt("spawnDelay");
                        entities.add(new NPC_Cyan(this, npcPosition, npcDirection, spawnDelay));
                    }
                    case "m" -> {
                        int bonusSpeed = npcObj.getInt("bonusSpeed");
                        entities.add(new NPC_Magenta(this, npcPosition, npcDirection, bonusSpeed));
                    }
                    case "y" -> entities.add(new NPC_Yellow(this, npcPosition, npcDirection));
                    case "r" -> entities.add(new NPC_Red(this, npcPosition, npcDirection));
                    default -> entities.add(new NPC( this, npcPosition, npcDirection, type));
                }

                System.out.println("\033[1;36mNPC\033[0m Loaded on \t\t[" + npcPosition.getX() + "," + npcPosition.getY() + "]");
            }

            try {
                JSONArray bonusArrayObj = levelObj.getJSONArray("BONUS");
                for(int n = 0; n < bonusArrayObj.length(); n++){
                    JSONObject bonusObj = bonusArrayObj.getJSONObject(n);
                    String type = bonusObj.getString("type");
                    int time = bonusObj.getInt("time");
                    int spawnDelay = bonusObj.getInt("spawnDelay");
                    Point2D bonusPosition = new Point2D(bonusObj.getInt("positionX"),
                                                        bonusObj.getInt("positionY"));

                    switch (type){
                        case "stop-b" -> bonuses.add(new StopBonus(this, bonusPosition, time, spawnDelay));
                        case "slow-b" -> bonuses.add(new SlowBonus(this, bonusPosition, time, spawnDelay));
                        case "kill-b" -> bonuses.add(new KillBonus(this, bonusPosition, time, spawnDelay));
                        case "speed-b" -> {
                            int speed = bonusObj.getInt("speed");
                            bonuses.add(new SpeedBonus(this, bonusPosition, time, spawnDelay, speed));
                        }
                        default -> System.out.println("\033[1;31mWrong Bonus Type\033[0m");
                    }

                }
            } catch (JSONException e) {
                System.out.println("\033[1;31mError in Loading Bonuses\033[0m");
            }

        }catch (org.json.JSONException e){
            System.out.println("\033[1;31mError in Loading Level\033[0m");
        }
    }

    // Fill
    public void updateCurrentLevelFillPercent(){
        int percentFilledOfMap = (int)percentFilledOfMap();
        App.gameViewController.getHudListener().mapFillPercentageChanged(percentFilledOfMap);

        if(percentFilledOfMap >= 80)
            game.gameChangeListener.levelWon();
    }
    private void fillMap( Point2D position){
        LevelPoint levelPoint = tryGetPointOnMap((int)position.getX(), (int)position.getY(), LevelPoint.Wall);

        if(levelPoint != LevelPoint.Empty)
            return;

        map.get((int)position.getY()).set((int)position.getX(), LevelPoint.Wall_Temp);

        fillMap(new Point2D(position.getX(), position.getY() - 1));
        fillMap(new Point2D(position.getX(), position.getY() + 1));
        fillMap(new Point2D(position.getX() - 1, position.getY()));
        fillMap(new Point2D(position.getX() + 1, position.getY()));
    }
    private void finishFill(){
        for (ArrayList<LevelPoint> row: map) {
            for(int x = 0; x < row.size(); x++){
                LevelPoint levelPoint = row.get(x);

                if(levelPoint == LevelPoint.Empty) {
                    row.set(x, LevelPoint.Wall);
                }else if(levelPoint == LevelPoint.Wall_Temp){
                    row.set(x, LevelPoint.Empty);
                }else if(levelPoint == LevelPoint.Wall_Route){
                    row.set(x, LevelPoint.Wall);
                }
            }
        }
        updateCurrentLevelFillPercent();
    }
    private int countWalls(){
        int sum = 0;
        for (int y = 1; y < map.size() - 1; y++) {
            for (int x = 1; x < map.get(y).size() - 1; x++) {
                LevelPoint levelPoint = map.get(y).get(x);
                if(levelPoint == LevelPoint.Wall)
                    sum++;
            }
        }
        return sum;
    }
    public float percentFilledOfMap(){
        return ((float)countWalls() / (float)((mapSize.getX() - 2) * (mapSize.getY() - 2)) * 100);
    }

    // Speeds
    public void setTmpSpeedForNPC(int speed){
        for(Entity entity : entities){
            if(entity instanceof NPC){
                entity.setSpeed(speed);
            }
        }
    }
    public void resetSpeedForNPC(){
        for(Entity entity : entities){
            if(entity instanceof NPC){
                entity.resetSpeed();
            }
        }
    }
    public void setTmpSpeedForPlayer(int speed){
        for(Entity entity : entities){
            if(entity instanceof Player){
                entity.setSpeed(speed);
            }
        }
    }
    public void setSpeedForPlayerToOriginate(){
        for(Entity entity : entities){
            if(entity instanceof Player){
                entity.resetSpeed();
            }
        }
    }

    // Bonuses
    public void setUpBonuses(){
        for (Bonus bonus : bonuses){
            bonus.setUpBonus();
        }
    }
    public void enableNPCCanBeKilled(){
        npcCanBeKilled = true;
    }
    public void disableNPCCanBeKilled(){
        npcCanBeKilled = false;
    }

    // Getters
    public Point2D getMapSize(){
        return mapSize;
    }
    public Game getGame() {
        return game;
    }
    public boolean getNPCanBeKilled() {
        return npcCanBeKilled;
    }
}
