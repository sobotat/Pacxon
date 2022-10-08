package pacxon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class Level {
    ArrayList<ArrayList<Character>> map;
    Point2D mapSize;
    ArrayList<Entity> entities;

    public Level(String levelFileName){
        loadLevel(levelFileName);
        //loadEntitiesFromMap();
        generateMap( (int)mapSize.getX(), (int)mapSize.getY());
    }

    public void draw(GraphicsContext gc, int blockSize){

        for (int y = 0; y < map.size(); y++) {
            ArrayList<Character> row = map.get(y);
            for (int x = 0; x < row.size(); x++) {
                char item = row.get(x);

                if (item == '#'){
                    //gc.setFill(Color.BLACK);
                    //gc.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
                    Wall.draw(gc, new Point2D(x,y), blockSize, 5);
                }
            }
        }

        for (Entity entity: entities) {
            entity.draw(gc, blockSize);
        }
    }

    private void generateMap(int x, int y){
        map = new ArrayList<>(y);

        for (int i = 0; i < y; i++) {
            ArrayList<Character> line = new ArrayList<>(y);

            for (int j = 0; j < x; j++) {
                if(i == 0 || i == y - 1){
                    line.add('#');
                }else{
                    if ((j == 0 || j == x - 1)) {
                        line.add('#');
                    } else {
                        line.add(' ');
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
            entities.add(new Player(playerPosition));
            System.out.println("Player Loaded on [" + playerPosition.getX() + "," + playerPosition.getY() + "]");

            JSONArray npcArrayObj = levelObj.getJSONArray("NPCs");
            for (int n = 0; n < npcArrayObj.length(); n++) {
                JSONObject npcObj = npcArrayObj.getJSONObject(n);

                Point2D npcPosition = new Point2D(npcObj.getInt("positionX"),
                                                  npcObj.getInt("positionY"));
                String type = npcObj.getString("type");
                entities.add(new NPC(npcPosition, type));
                System.out.println("NPC Loaded on [" + npcPosition.getX() + "," + npcPosition.getY() + "]");
            }

        }catch (org.json.JSONException e){
            System.out.println("Error in Loading Level");
        }
    }

    public static void generateLevelTemplate(){
        JSONObject levelObj = new JSONObject();
        levelObj.put("mapX", 10);
        levelObj.put("mapY", 5);

        JSONObject playerObj = new JSONObject();
        playerObj.put("positionX", 1);
        playerObj.put("positionY", 1);
        levelObj.put("Player", playerObj);

        JSONArray npcArrayObj = new JSONArray();
        for (int i = 0; i < 3; i++) {
            JSONObject npc = new JSONObject();
            npc.put("positionX", 1);
            npc.put("positionY", 1);
            npc.put("type", "r");
            npcArrayObj.put(npc);
        }
        levelObj.put("NPCs", npcArrayObj);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = JsonParser.parseString( levelObj.toString());
        String enhanceJson = gson.toJson(je);

        Files.writeString("\\levels\\template.txt", enhanceJson);
    }
}
