package pacxon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pacxon.controllers.GameViewController;
import pacxon.controllers.StartMenuController;

import java.util.Objects;

public class App extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	private Canvas canvas;
	private static Stage stage;

	public static App app;
	public static StartMenuController startMenuController;
	public static GameViewController gameViewController;
	public static Stage primaryStage;
	private static Scene startMenuScene, gameViewScene, winViewScene, scoreViewScene;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			App.primaryStage = primaryStage;
			App.app = this;

			// CSS
			String css = Objects.requireNonNull(this.getClass().getResource("my_style.css")).toExternalForm();

			// StartMenu
			FXMLLoader loaderStartMenu = new FXMLLoader(getClass().getResource("start_menu.fxml"));
			VBox rootStartMenu = loaderStartMenu.load();
			startMenuScene = new Scene(rootStartMenu);
			startMenuScene.getStylesheets().add(css);
			startMenuController = loaderStartMenu.getController();

			// StartMenu
			FXMLLoader loaderGameView = new FXMLLoader(getClass().getResource("game_view.fxml"));
			VBox rootGameView = loaderGameView.load();
			gameViewScene = new Scene(rootGameView);
			gameViewScene.getStylesheets().add(css);
			gameViewController = loaderGameView.getController();
			gameViewController.setUpGame(gameViewScene);

			primaryStage.setScene(startMenuScene);
			primaryStage.resizableProperty().set(true);
			primaryStage.setTitle("Pacxon");
			primaryStage.show();

			primaryStage.setOnCloseRequest(this::exitProgram);
			stage = primaryStage;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void switchToGame(){
		System.out.println("Switching to \033[1;38mGame View\033[0m");
		if(primaryStage == null || gameViewScene == null)
			return;

		primaryStage.setScene(gameViewScene);
		gameViewController.startGame();
	}

	public static void switchToStartMenu(){
		System.out.println("Switching to \033[1;38mStart Menu\033[0m");
		if(primaryStage == null || startMenuScene == null)
			return;

		primaryStage.setScene(startMenuScene);
		gameViewController.stopGame();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}

	public static void changeSize(Point2D size){
		stage.setWidth(size.getX());
		stage.setHeight(size.getY());
	}

	public void exitProgram(WindowEvent evt) {
		System.out.println("\033[1;38mExiting Game ...\033[0m");
		gameViewController.stopGame();
		System.exit(0);
	}
}