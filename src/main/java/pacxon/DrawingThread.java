package pacxon;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import pacxon.listeners.InputListener;

public class DrawingThread extends AnimationTimer {

	private final GraphicsContext gc;

	private final Game game;

	private long lastTime;

	public DrawingThread(Canvas canvas, Scene scene, Game game) {
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
		InputListener inputListener = game.getCurrentInputListener();
		String type = keyEvent.getEventType().getName();
		KeyCode keyCode = keyEvent.getCode();

		switch (keyEvent.getCode()){
			case LEFT -> keyCode = KeyCode.A;
			case RIGHT -> keyCode = KeyCode.D;
			case UP -> keyCode = KeyCode.W;
			case DOWN -> keyCode = KeyCode.S;
		}

		if(keyCode == KeyCode.W || keyCode == KeyCode.S || keyCode == KeyCode.A || keyCode == KeyCode.D)
			inputListener.keyPressed(type, keyCode);
		else if(keyCode == KeyCode.L && type.equals(KeyEvent.KEY_RELEASED.toString())){
			System.out.println("Changing to \033[1;36mNext Level\033[0m");
			game.nextLevel();
		}else if (keyCode == KeyCode.R && type.equals(KeyEvent.KEY_RELEASED.toString())){
			game.gameChangeListener.restartGame();

			game.resetLife();
			game.startGame();
		}
		else if (keyCode == KeyCode.ESCAPE && type.equals(KeyEvent.KEY_RELEASED.toString())){
			App.switchToStartMenu();
		}else if (keyCode == KeyCode.V && type.equals(KeyEvent.KEY_RELEASED.toString())){
			game.setDebug(!game.isDebug());
			System.out.println((game.isDebug() ? "\033[1;32mDebugView Activated\033[0m" : "\033[1;31mDebugView Deactivated\033[0m"));
		}else if (keyCode == KeyCode.G && type.equals(KeyEvent.KEY_RELEASED.toString())){
			game.setGodMode(!game.isGodMode());
			System.out.println((game.isGodMode() ? "\033[1;32mGodMode Activated\033[0m" : "\033[1;31mGodMode Deactivated\033[0m"));
		}
	}
}
