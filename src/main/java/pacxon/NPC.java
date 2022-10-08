package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class NPC extends Entity{

    String type;

    public NPC(Point2D startPosition, String type) {
        super(startPosition);

        this.type = type;
        loadTextures();
    }

    @Override
    public void draw(GraphicsContext gc, int blockSize){
        //gc.setFill(Color.AQUA);
        //gc.fillRect(position.getX() * blockSize, position.getY() * blockSize, blockSize, blockSize);
        gc.drawImage(textures.get(0), position.getX() * blockSize, position.getY() * blockSize, blockSize, blockSize);
    }

    @Override
    public void loadTextures() {
        addTexture("characters/" + type + "/npc_left1.png");
        addTexture("characters/" + type + "/npc_left2.png");
        addTexture("characters/" + type + "/npc_right1.png");
        addTexture("characters/" + type + "/npc_right2.png");
        addTexture("characters/" + type + "/npc_up1.png");
        addTexture("characters/" + type + "/npc_up2.png");
        addTexture("characters/" + type + "/npc_down1.png");
        addTexture("characters/" + type + "/npc_down2.png");
    }
}
