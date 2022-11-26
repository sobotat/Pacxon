package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.json.JSONArray;
import org.json.JSONObject;
import pacxon.entities.Entity;
import pacxon.entities.NPC;
import pacxon.entities.NPC_Cyan;
import pacxon.entities.Player;
import pacxon.listeners.InputListener;
import pacxon.listeners.LevelChangeListener;

import java.util.ArrayList;

public class Level {
    ArrayList<ArrayList<LevelPoint>> map;
    Point2D mapSize;
    ArrayList<Entity> entities;
    Game game;

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
                for (Entity entity : entities) {
                    if(entity instanceof NPC npc){
                        if(!(npc instanceof NPC_Cyan))
                            fillMap(npc.getPositionRounded());
                    }
                }
                finishFill();
                App.gameViewController.getHudListener().mapFillPercentageChanged((int) percentFilledOfMap());
            }
        };
    }

    public void update(double deltaTime){

        Player player = null;
        for (Entity entity : entities) {
            entity.update(deltaTime);

            if(entity instanceof Player p)
                player = p;
        }

        if(player == null)
            return;

        for(Entity entity : entities){
            if(player.equals(entity))
                continue;

            if(entity instanceof NPC_Cyan npc_cyan){
                if(!npc_cyan.isSpawned())
                    continue;

                if(player.getPositionRounded().getX() == npc_cyan.getPositionRounded().getX() &&
                   player.getPositionRounded().getX() == npc_cyan.getPositionRounded().getX() ){
                    System.out.println("Player was Hit by Cyan");
                    player.hit();
                    game.removeLife();
                }
                continue;
            }

            for (Point2D point : player.route){
                if(point.getX() == entity.getNextPosition().getX() &&
                   point.getY() == entity.getNextPosition().getY() ){

                    System.out.println("Player was Hit");
                    player.hit();
                    game.removeLife();

                    return;
                }
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
            entity.draw(gc, blockSize, debug);
        }
    }

    public InputListener getPlayerInputListener(){
        for (Entity entity : entities) {
            if(entity instanceof Player player){
                return player.inputListener;
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

        String read = Files.readString( "\\levels\\" + levelFileName);
        if (read.equals("")) return;

        try {
            JSONObject levelObj = new JSONObject(read);
            int mapX = levelObj.getInt("mapX");
            int mapY = levelObj.getInt("mapY");
            mapSize = new Point2D(mapX, mapY);
            System.out.println("Map Loaded [" + mapX + "," + mapY + "]");

            JSONObject playerObj = levelObj.getJSONObject("Player");
            int playerPositionX = playerObj.getInt("positionX");
            int playerPositionY = playerObj.getInt("positionY");
            Point2D playerPosition = new Point2D( playerPositionX, playerPositionY);
            entities.add(new Player( this, playerPosition, new Point2D(0, 0)));
            System.out.println("Player Loaded on [" + playerPosition.getX() + "," + playerPosition.getY() + "]");

            JSONArray npcArrayObj = levelObj.getJSONArray("NPCs");
            for (int n = 0; n < npcArrayObj.length(); n++) {
                JSONObject npcObj = npcArrayObj.getJSONObject(n);
                String type = npcObj.getString("type");

                Point2D npcPosition = new Point2D(npcObj.getInt("positionX"),
                        npcObj.getInt("positionY"));
                Entity.Direction npcDirection = Entity.Direction.valueOf(npcObj.getString("direction"));

                if(type.equals("c")) {
                    int spawnDelay = npcObj.getInt("spawnDelay");
                    entities.add(new NPC_Cyan(this, npcPosition, npcDirection, spawnDelay));
                }else
                    entities.add(new NPC( this, npcPosition, npcDirection, type));
                System.out.println("NPC Loaded on [" + npcPosition.getX() + "," + npcPosition.getY() + "]");
            }

        }catch (org.json.JSONException e){
            System.out.println("Error in Loading Level");
        }
    }

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

    public float percentFilledOfMap(){
        return ((float)countWalls() / (float)((mapSize.getX() - 2) * (mapSize.getY() - 2)) * 100);
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

    public Point2D getMapSize(){
        return mapSize;
    }
}
