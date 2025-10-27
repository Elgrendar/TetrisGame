package es.rafacampanero.tetris.game;

import javafx.animation.AnimationTimer;

public class GameLoop extends AnimationTimer {
    private final Game game;
    private long last = -1;

    public GameLoop(Game game) {
        this.game = game;
    }

    @Override
    public void handle(long now) {
        if (last < 0) last = now;
        double delta = (now - last) / 1_000_000_000.0; // segundos
        last = now;
        game.update(delta);
    }
}
