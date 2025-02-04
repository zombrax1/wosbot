package org.wos.launcher;

import java.io.IOException;

import cl.camodev.utiles.CssPaths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class FxApp extends Application {

	private static Scene scene;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(LauncherLayoutController.class.getResource("LauncherLayout.fxml"));
		LauncherLayoutController controller = new LauncherLayoutController();
		fxmlLoader.setController(controller);
		Parent root = fxmlLoader.load();
		scene = new Scene(root, 900, 500);
		scene.getStylesheets().add(CssPaths.getCssPath());
		stage.setScene(scene);
		stage.setTitle("Launcher");
		stage.show();
	}

}