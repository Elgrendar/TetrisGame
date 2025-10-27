package es.rafacampanero.tetris.game;

import es.rafacampanero.tetris.App;
import es.rafacampanero.tetris.persist.HighscoreManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Game {
    private final GraphicsContext gc;
    private final int cols;
    private final int rows;
    private final int blockSize;

    private final Board board;
    private Piece current;
    private boolean paused = false;

    public Game(GraphicsContext gc, int cols, int rows, int blockSize) {
        this.gc = gc;
        this.cols = cols;
        this.rows = rows;
        this.blockSize = blockSize;
        this.board = new Board(cols, rows);
        this.current = SpawnBag.next();
    }

    // Called each frame from GameLoop
    public void update(double deltaSeconds) {
        if (paused) return;
        // por ahora sólo dibujamos
        render();
    }

    public void render() {
        // Fondo
        gc.setFill(Color.web("#111"));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // Dibujar casillas del tablero
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Color color = board.getColor(c, r);
                drawCell(c, r, color);
            }
        }

        // Dibujar pieza actual (ejemplo)
        if (current != null) {
            int baseX = current.x;
            int baseY = current.y;
            for (int r = 0; r < current.shape.length; r++) {
                for (int c = 0; c < current.shape[r].length; c++) {
                    if (current.shape[r][c]) {
                        drawCell(baseX + c, baseY + r, current.color);
                    }
                }
            }
        }
    }

    private void drawCell(int c, int r, Color color) {
        double x = c * blockSize + 20; // margen izquierdo
        double y = r * blockSize + 20; // margen superior
        gc.setFill(color == null ? Color.web("#222") : color);
        gc.fillRect(x, y, blockSize - 1, blockSize - 1); // -1 para ver líneas
    }

    public void moveLeft() {
        if (current != null) current.x -= 1;
    }

    public void moveRight() {
        if (current != null) current.x += 1;
    }

    public void rotate() {
        if (current != null) {
            current.rotate();
        }
    }

    public void softDrop() {
        if (current != null) current.y += 1;
    }

    public void hardDrop() {
        if (current != null) current.y += rows; // placeholder
    }

    public void togglePause() {
        paused = !paused;
    }
}
