package pacxon;

import javafx.geometry.Point2D;

import java.io.*;
import java.util.ArrayList;

public class Map {
    ArrayList<String> map;
    ArrayList<Entity> entities;

    public Map(String mapFileName){
        loadMap(mapFileName);
        loadEntitiesFromMap();
    }

    public Map(String mapFileName, boolean debug){
        this(mapFileName);

        if (debug) {
            printMap();
        }
    }

    private void loadMap(String mapFileName){
        try{
        map = new ArrayList<>();
        File file = new File( "src/main/resources/pacxon/levels/" + mapFileName);

        if (file.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (br.ready()) {
                map.add(br.readLine());
            }
            br.close();
        }else{
            System.out.println("Error Map File Not Found :> " + mapFileName);
        }
        }catch (IOException e){
            System.out.println("Error in Load Map : " + e.getMessage());
        }
    }

    private void loadEntitiesFromMap(){
        entities = new ArrayList<>();

        for (int y = 0; y < map.size(); y++) {
            String row = map.get(y);
            for (int x = 0; x < row.length(); x++) {
                char item = row.charAt(x);

                if(item == 'p'){
                    entities.add(new Player( new Point2D(x,y), null));
                }else if (item == 'c' || item == 'm' || item == 'y' || item == 'r'){
                    entities.add(new NPC(new Point2D(x,y), null));
                }
            }
        }
    }

    public void printMap(){
        if(map.isEmpty())
            return;

        for (String x : map){
            System.out.println(x);
        }
        System.out.println("Level: X > " + map.get(1).length() + ", Y > " + map.size());
        System.out.println("Entities: " + entities.size() + "\n");
    }
}
