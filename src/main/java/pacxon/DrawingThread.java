package pacxon;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import pacxon.listeners.InputListener;

public class DrawingThread extends AnimationTimer {

	private final Scene scene;
	private final Canvas canvas;
	private final GraphicsContext gc;

	private final Game game;

	private long lastTime;

	public DrawingThread(Canvas canvas, Scene scene, Game game) {
		this.scene = scene;
		this.canvas = canvas;
		this.gc = canvas.getGraphicsContext2D();
		this.game = game;

		scene.setOnKeyPressed(keyEvent -> keyboardHandler(keyEvent));
		scene.setOnKeyReleased(keyEvent -> keyboardHandler(keyEvent));
	}

	@Override
	public void handle(long now) {
		if(lastTime > 0){
			double deltaTime = (now - lastTime) / 1e9;
			//game.simulate(deltaTime);

			game.update(deltaTime);
		}

		game.draw(gc);
		lastTime = now;
	}


	private void keyboardHandler(KeyEvent keyEvent){
		InputListener inputListener = game.levels.get(game.currentLevel).getPlayerInputListener();
		String type = keyEvent.getEventType().getName();
		KeyCode keyCode = keyEvent.getCode();

		if(keyCode == KeyCode.W || keyCode == KeyCode.S || keyCode == KeyCode.A || keyCode == KeyCode.D)
			inputListener.keyPressed(type, keyCode);
		else if(keyCode == KeyCode.L && type.equals(KeyEvent.KEY_RELEASED.toString())){
			System.out.println("Change Level");
			game.nextLevel();
		}else if (keyCode == KeyCode.R && type.equals(KeyEvent.KEY_RELEASED.toString())){
			game.gameChangeListener.restartGame();

			game.resetLife();
			game.startGame();
		}
	}
}
