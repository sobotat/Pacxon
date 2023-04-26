package pacxon.lib.api;

import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class Files {
    private static final Logger logger = LogManager.getLogger(Files.class.getName());

    static String fileLocation = "src/main/resources/pacxon/";

    public static void setFileLocation(String fileLocation) {
        Files.fileLocation = fileLocation;
    }

    public static void writeString(String fileName, String text) {
        try {
            File file = new File( fileLocation + fileName);
            if (file.getParentFile().mkdir()){
                logger.info("Dir was created");
            }
            FileWriter fileWriter;

            fileWriter = new FileWriter(file);

            fileWriter.flush();
            fileWriter.write(text);
            fileWriter.close();
        } catch (IOException e) {
            logger.error("WriteString IOException -> " + fileName);
            e.printStackTrace();
        }
    }

    public static String readString(String fileName){
        StringBuilder result = new StringBuilder();

        try{
            File file = new File( fileLocation + fileName);
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                while (br.ready()) {
                    result.append(br.readLine()).append("\n");
                }
                br.close();
            }else{
                Files.writeString(fileName, "");
            }
        } catch (Exception e){
            logger.error("Read String -> " + fileName + "\n" + e.getMessage());
        }

        return result.toString();
    }

    public static void addTexture(ArrayList<Image> textures, String textureLocation){
        Image img = new Image(
                Objects.requireNonNull( Files.class.getResourceAsStream( "/pacxon/" + textureLocation)));

        if (img.isError()) {
            logger.error("Img Load Failed");
            logger.error(img.exceptionProperty().get().getMessage());
        }else
            textures.add(img);
    }
}
