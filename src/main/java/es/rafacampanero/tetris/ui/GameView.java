package es.rafacampanero.tetris.ui;

import es.rafacampanero.tetris.App;
import es.rafacampanero.tetris.game.Game;
import es.rafacampanero.tetris.game.GameLoop;
import es.rafacampanero.tetris.sound.SoundManager;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GameView {
    private final BorderPane root;
    private final Canvas canvas;
    private final Game game;
    private final GameLoop loop;

    public GameView(Stage stage) {
        root = new BorderPane();
        Group g = new Group();
        canvas = new Canvas(10 * 30 + 200, 20 * 30); // lateral para UI
        g.getChildren().add(canvas);
        root.setCenter(g);

        // Init SoundManager
        SoundManager.init();

        game = new Game(canvas.getGraphicsContext2D(), 10, 20, 30);
        loop = new GameLoop(game);

        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Input: flechas + WASD
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            switch (code) {
                case LEFT, A -> { game.moveLeft(); SoundManager.playMove(); }
                case RIGHT, D -> { game.moveRight(); SoundManager.playMove(); }
                case UP, W -> { game.rotate(); SoundManager.playRotate(); }
                case DOWN, S -> game.softDrop();
                case SPACE -> game.hardDrop();
                case P -> game.togglePause();
                default -> {}
            }
        });

        loop.start();
    }

    public BorderPane getRoot() {
        return root;
    }
}
