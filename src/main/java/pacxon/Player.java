package pacxon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class Player extends Entity{
    public Player(Point2D startPosition) {
        super(startPosition);
        loadTextures();
    }

    @Override
    public void draw(GraphicsContext gc, int blockSize){
        //gc.setFill(Color.DARKRED);
        //gc.fillRect(position.getX() * blockSize, position.getY() * blockSize, blockSize, blockSize);
        gc.drawImage(textures.get(0), position.getX() * blockSize, position.getY() * blockSize, blockSize, blockSize);
    }

    @Override
    public void loadTextures() {
        addTexture("characters/p/player_stop1.png");
        addTexture("characters/p/player_stop2.png");
        addTexture("characters/p/player_left1.png");
        addTexture("characters/p/player_left2.png");
        addTexture("characters/p/player_right1.png");
        addTexture("characters/p/player_right2.png");
        addTexture("characters/p/player_up1.png");
        addTexture("characters/p/player_up2.png");
        addTexture("characters/p/player_down1.png");
        addTexture("characters/p/player_down2.png");
    }
}
