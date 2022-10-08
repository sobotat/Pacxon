package pacxon;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class DrawingThread extends AnimationTimer {

	private final Canvas canvas;
	private final GraphicsContext gc;

	private final Game game;

	private long lastTime;

	public DrawingThread(Canvas canvas) {
		this.canvas = canvas;
		this.gc = canvas.getGraphicsContext2D();
		this.game = new Game(1);
		//this.game = new Game(15, canvas.getWidth(), canvas.getHeight());
	}

	@Override
	public void handle(long now) {
		if(lastTime > 0){
			double deltaTime = (now - lastTime) / 1e9;
			//game.simulate(deltaTime);
		}

		game.draw(gc);
		lastTime = now;
	}
}
