package pacxon;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Objects;

public class Api {

    public static void addTexture( ArrayList<Image> textures,String fileLocation){
        Image img = new Image(
                Objects.requireNonNull( Api.class.getResourceAsStream("/pacxon/" + fileLocation)));

        if (img.isError()) {
            System.out.println("Img Load Failed");
            System.out.println(img.exceptionProperty().get().getMessage());
        }else
            textures.add(img);
    }
}
