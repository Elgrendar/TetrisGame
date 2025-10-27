package es.rafacampanero.tetris.game;

import javafx.scene.paint.Color;

public class Board {
    private final int cols;
    private final int rows;
    private final Color[][] cells;

    public Board(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.cells = new Color[rows][cols];
    }

    public void setCell(int c, int r, Color color) {
        if (inBounds(c, r)) cells[r][c] = color;
    }

    public Color getColor(int c, int r) {
        if (!inBounds(c, r)) return Color.BLACK;
        return cells[r][c];
    }

    private boolean inBounds(int c, int r) {
        return c >= 0 && c < cols && r >= 0 && r < rows;
    }

    // TODO: añadir check de líneas, eliminación y demás
}
