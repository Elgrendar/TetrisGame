package es.rafacampanero.tetris.game;

import javafx.scene.paint.Color;

public class Piece {
    public boolean[][] shape;
    public Color color;
    public int x = 3; // posición por defecto
    public int y = 0;

    public Piece(boolean[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    public void rotate() {
        int h = shape.length;
        int w = shape[0].length;
        boolean[][] rotated = new boolean[w][h];
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                rotated[c][h - 1 - r] = shape[r][c];
            }
        }
        shape = rotated;
    }

    // Factory methods para piezas clásicas
    public static Piece createI() {
        return new Piece(new boolean[][]{
                {true, true, true, true}
        }, Color.CYAN);
    }

    public static Piece createO() {
        return new Piece(new boolean[][]{
                {true, true},
                {true, true}
        }, Color.YELLOW);
    }

    public static Piece createT() {
        return new Piece(new boolean[][]{
                {false, true, false},
                {true, true, true}
        }, Color.PURPLE);
    }

    public static Piece createL() {
        return new Piece(new boolean[][]{
                {true, false},
                {true, false},
                {true, true}
        }, Color.ORANGE);
    }

    public static Piece createJ() {
        return new Piece(new boolean[][]{
                {false, true},
                {false, true},
                {true, true}
        }, Color.BLUE);
    }

    public static Piece createS() {
        return new Piece(new boolean[][]{
                {false, true, true},
                {true, true, false}
        }, Color.GREEN);
    }

    public static Piece createZ() {
        return new Piece(new boolean[][]{
                {true, true, false},
                {false, true, true}
        }, Color.RED);
    }
}
