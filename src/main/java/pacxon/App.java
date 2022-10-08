package pacxon;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private Canvas canvas;
	private static Stage stage;
	private AnimationTimer timer;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			//Construct a main window with a canvas.  
			Group root = new Group();
			canvas = new Canvas(900, 580);
			root.getChildren().add(canvas);
			Scene scene = new Scene(root, 900, 580);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.resizableProperty().set(true);
			primaryStage.setTitle("Pacxon");
			primaryStage.show();
			//Exit program when main window is closed
			primaryStage.setOnCloseRequest(this::exitProgram);
			timer = new DrawingThread(canvas);
			timer.start();

			stage = primaryStage;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void stop() throws Exception {
		timer.stop();
		super.stop();
	}

	public static void changeSize(Point2D size){
		stage.setWidth(size.getX());
		stage.setHeight(size.getY());
	}
	
	private void exitProgram(WindowEvent evt) {
		System.exit(0);
	}
}