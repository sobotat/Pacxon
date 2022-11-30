package pacxon;

import javafx.scene.image.Image;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class Files {

    public static void writeString( String fileName, String text) {
        try {
            File file = new File( "src/main/resources/pacxon/" + fileName);
            if (file.getParentFile().mkdir()){
                System.out.println("Dir was created");
            }
            FileWriter fileWriter;

            fileWriter = new FileWriter(file);

            fileWriter.flush();
            fileWriter.write(text);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("WriteString IOException -> " + fileName);
            e.printStackTrace();
        }
    }

    public static String readString(String fileName){
        StringBuilder result = new StringBuilder();

        try{
            File file = new File( "src/main/resources/pacxon/" + fileName);
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
            System.out.println("Read String -> " + fileName + "\n" + e.getMessage());
        }

        return result.toString();
    }

    public static void addTexture(ArrayList<Image> textures, String fileLocation){
        Image img = new Image(
                Objects.requireNonNull( Files.class.getResourceAsStream("/pacxon/" + fileLocation)));

        if (img.isError()) {
            System.out.println("Img Load Failed");
            System.out.println(img.exceptionProperty().get().getMessage());
        }else
            textures.add(img);
    }
}
