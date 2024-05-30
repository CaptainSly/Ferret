package io.azraein.ferret.system._javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

@Deprecated
public class FerretFXUI extends Application {

	@Override
	public void start(Stage primaryStage) {
		var engine = new FXEngine(1280, 1024);

		/*
		 * Engine is a BorderPane by default. It holds all the underlying LWJGL logic for rendering the
		 * OpenGL context to the JavaFX Canvas, but it also allows you to add JFX components to it.
		 * The Center Node position is taken, but the Top, left, right and bottom positions are still
		 * free.
		 *
		 * engine.setTop()
		 * engine.setLeft()
		 * engine.setRight()
		 * engine.setBottom()
		 */

		primaryStage.setScene(new Scene(engine));
		primaryStage.show();

		engine.getLwjglThread().start();
	}

}
