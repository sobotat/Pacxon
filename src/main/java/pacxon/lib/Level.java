package pacxon.lib;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pacxon.lib.api.entity.*;
import pacxon.lib.bonuses.*;
import pacxon.lib.entities.*;
import pacxon.lib.listeners.InputListener;
import pacxon.lib.listeners.LevelChangeListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Level {
    private static final Logger logger = LogManager.getLogger(Level.class.getName());

    protected Point2D mapSize;
    private ArrayList<ArrayList<LevelPoint>> map;
    private ArrayList<Entity> entities;
    private ArrayList<Bonus> bonuses;
    protected Game game;
    private boolean invalidLevel = false;

    private int currentAnimation = 0;
    private double timeFromLastAnimation;
    private boolean npcCanBeKilled;

    private LevelChangeListener levelChangeListener;

    public enum LevelPoint{
        Wall, Wall_Route, Wall_Temp, Empty
    }

    public Level(String levelFileName, Game game){
        this.game = game;

        boolean loadSuccess = loadLevel(levelFileName);
        if(!loadSuccess) {
            game.stopGame();
            invalidLevel = true;
            return;
        }

        //loadEntitiesFromMap();
        generateMap( (int)mapSize.getX(), (int)mapSize.getY());
        game.getHudListener().mapFillPercentageChanged((int) percentFilledOfMap());

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
                    logger.debug("\033[1;31mFill Started\033[0m");

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
                            logger.debug("\t -> Remaining FillThreads [\033[1;31m" + lastCountOfRunning + "\033[0m]");
                    }
                }

                finishFill();
                game.getHudListener().mapFillPercentageChanged((int) percentFilledOfMap());
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
    private boolean loadLevel(String levelId){
        entities = new ArrayList<>();
        bonuses = new ArrayList<>();

        logger.info("Loading Level \033[1;32m" + levelId +"\033[0m");

        try {
            LevelEntity levelEntity = LevelEntity.getClient().getLevel(levelId);

            if(levelEntity == null) {
                logger.error("\033[1;31mError level is NULL\033[0m");
                return false;
            }

            logger.debug(levelEntity);

            int mapX = levelEntity.getMap().getSizeX();
            int mapY = levelEntity.getMap().getSizeY();
            mapSize = new Point2D(mapX, mapY);
            logger.info("\033[1;32mMap\033[0m Loaded \t\t\t[" + mapX + "," + mapY + "]");

            PlayerEntity playerObj = levelEntity.getPlayer();
            int playerPositionX = playerObj.getPositionX();
            int playerPositionY = playerObj.getPositionY();
            int playerSpeed = playerObj.getSpeed();
            Point2D playerPosition = new Point2D( playerPositionX, playerPositionY);
            entities.add(new Player( this, playerPosition, new Point2D(0, 0), playerSpeed));
            logger.info("\033[1;34mPlayer\033[0m Loaded on \t[" + playerPosition.getX() + "," + playerPosition.getY() + "]");

            List<NPCEntity> npcArrayObj = levelEntity.getNpcs();
            for (NPCEntity npcObj : npcArrayObj) {
                String type = npcObj.getType();

                Point2D npcPosition = new Point2D(npcObj.getPositionX(), npcObj.getPositionY());
                Entity.Direction npcDirection = Entity.Direction.valueOf(npcObj.getDirection());

                switch (type){
                    case "c" -> {
                        int spawnDelay = npcObj.getSpawnDelay();
                        entities.add(new NPC_Cyan(this, npcPosition, npcDirection, spawnDelay));
                    }
                    case "m" -> {
                        int bonusSpeed = npcObj.getBonusSpeed();
                        entities.add(new NPC_Magenta(this, npcPosition, npcDirection, bonusSpeed));
                    }
                    case "y" -> entities.add(new NPC_Yellow(this, npcPosition, npcDirection));
                    case "r" -> entities.add(new NPC_Red(this, npcPosition, npcDirection));
                    default -> entities.add(new NPC( this, npcPosition, npcDirection, type));
                }

                logger.info("\033[1;36mNPC\033[0m Loaded on \t\t[" + npcPosition.getX() + "," + npcPosition.getY() + "]");
            }

            List<MapBonusEntity> bonusArrayObj = levelEntity.getBonuses();
            for(MapBonusEntity bonusObj : bonusArrayObj){
                BonusEntity bonus = bonusObj.getBonus();
                String type = bonus.getType();
                int time = bonus.getTime();
                int spawnDelay = bonus.getSpawnDelay();
                Point2D bonusPosition = new Point2D(bonusObj.getPositionX(),
                                                    bonusObj.getPositionY());

                switch (type){
                    case "stop-b" -> bonuses.add(new StopBonus(this, bonusPosition, time, spawnDelay));
                    case "slow-b" -> bonuses.add(new SlowBonus(this, bonusPosition, time, spawnDelay));
                    case "kill-b" -> bonuses.add(new KillBonus(this, bonusPosition, time, spawnDelay));
                    case "speed-b" -> {
                        int speed = bonus.getSpeed();
                        bonuses.add(new SpeedBonus(this, bonusPosition, time, spawnDelay, speed));
                    }
                    default -> logger.error("\033[1;31mWrong Bonus Type\033[0m");
                }

            }

        }catch (Exception e){
            logger.error("\033[1;31mError in Loading Level >> " + e.getMessage() + "\033[0m");
            //logger.error("\033[1;31m >> " + e.getMessage() + "\033[0m");
            return false;
        }

        return true;
    }

    // Fill
    public void updateCurrentLevelFillPercent(){
        int percentFilledOfMap = (int)percentFilledOfMap();
        game.getHudListener().mapFillPercentageChanged(percentFilledOfMap);

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
    public boolean getIsInvalid() {
        return invalidLevel;
    }
}
