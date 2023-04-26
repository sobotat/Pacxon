package pacxon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import pacxon.controllers.GameViewController;
import pacxon.controllers.StartMenuController;
import pacxon.lib.api.StatusAPI;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

@Log4j2
public class App extends Application {

	public static void main(String[] args) {

		// Locale
		Locale.setDefault(Locale.US);
		App.appLocale = Locale.getDefault();

		App.appLocale = new Locale("cs", "CZ");

		// ResourceBundles
		App.startMenuRB = ResourceBundle.getBundle("start_menu", App.appLocale);
		App.gameViewRB = ResourceBundle.getBundle("game_view", App.appLocale);
		App.logTextRB = ResourceBundle.getBundle("log_text", App.appLocale);

		// API Status
		try {
			StatusAPI statusAPI = StatusAPI.getClient();
			boolean status = statusAPI.getStatus();

			if (status) {
				launch(args);
			} else
				throw new Exception("Api Status " + App.getLogTextRB().getString("is") + " False");
		}catch (Exception e){
			log.error("\033[1;31m" + App.getLogTextRB().getString("connect_to_api_failed") + "\033[0m >> " + e.getMessage());
		}
	}

	private Canvas canvas;
	private static Stage stage;

	@Getter private static App app;
	@Getter private static StartMenuController startMenuController;
	@Getter private static GameViewController gameViewController;

	@Getter private static Locale appLocale;
	@Getter private static ResourceBundle startMenuRB;
	@Getter private static ResourceBundle gameViewRB;
	@Getter private static ResourceBundle logTextRB;

	@Getter private static Stage primaryStage;
	private static Scene startMenuScene, gameViewScene;

	@Override
	public void start(Stage primaryStage) {
		try {
			App.primaryStage = primaryStage;
			App.app = this;

			// CSS
			String css = Objects.requireNonNull(this.getClass().getResource("my_style.css")).toExternalForm();

			// Start View
			FXMLLoader loaderStartMenu = new FXMLLoader(getClass().getResource("start_menu.fxml"), startMenuRB);
			VBox rootStartMenu = loaderStartMenu.load();
			startMenuScene = new Scene(rootStartMenu);
			startMenuScene.getStylesheets().add(css);
			startMenuController = loaderStartMenu.getController();

			// Game View
			FXMLLoader loaderGameView = new FXMLLoader(getClass().getResource("game_view.fxml"), gameViewRB);
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
		log.info(App.logTextRB.getString("switching_to") + " \033[1;38mGame View\033[0m");
		if(primaryStage == null || gameViewScene == null)
			return;

		primaryStage.setScene(gameViewScene);
		gameViewController.startGame();
	}

	public static void switchToStartMenu(){
		log.info(App.logTextRB.getString("switching_to") + " \033[1;38mStart Menu\033[0m");
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
		log.info("\033[1;38m" + App.logTextRB.getString("exiting_game") + "\033[0m");
		gameViewController.stopGame();
		System.exit(0);
	}
}