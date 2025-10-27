package es.rafacampanero.tetris;

import es.rafacampanero.tetris.ui.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    public static ResourceBundle MESSAGES;

    @Override
    public void start(Stage primaryStage) {
        // Forzamos español por defecto (fácil de cambiar o detectar)
        Locale locale = new Locale("es");
        MESSAGES = ResourceBundle.getBundle("strings", locale);

        GameView view = new GameView(primaryStage);
        Scene scene = new Scene(view.getRoot());

        primaryStage.setTitle("Tetris - RafaCampanero");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(600);
        primaryStage.setWidth(600);
        primaryStage.setHeight(720);
        primaryStage.setResizable(true); // permite maximizar
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
