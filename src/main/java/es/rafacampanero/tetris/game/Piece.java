package es.rafacampanero.tetris.game;

import java.awt.Color;

/**
 * Representa una pieza del Tetris.
 */
public class Piece {

    // Tipos de piezas Tetris (I, O, T, S, Z, J, L)
    public enum Type {
        I, O, T, S, Z, J, L
    }

    private Type type;
    private Color color;
    private int[][] shape; // Matriz 2D de la pieza
    private int x, y; // Posición en el tablero

    public Piece(Type type) {
        this.type = type;
        this.x = 3; // posición inicial centralizada
        this.y = 0; // arriba del tablero
        setShapeAndColor();
    }

    private void setShapeAndColor() {
        switch (type) {
            case I:
                shape = new int[][] {
                        { 1, 1, 1, 1 }
                };
                color = Color.CYAN;
                break;
            case O:
                shape = new int[][] {
                        { 1, 1 },
                        { 1, 1 }
                };
                color = Color.YELLOW;
                break;
            case T:
                shape = new int[][] {
                        { 0, 1, 0 },
                        { 1, 1, 1 }
                };
                color = Color.MAGENTA;
                break;
            case S:
                shape = new int[][] {
                        { 0, 1, 1 },
                        { 1, 1, 0 }
                };
                color = Color.GREEN;
                break;
            case Z:
                shape = new int[][] {
                        { 1, 1, 0 },
                        { 0, 1, 1 }
                };
                color = Color.RED;
                break;
            case J:
                shape = new int[][] {
                        { 1, 0, 0 },
                        { 1, 1, 1 }
                };
                color = Color.BLUE;
                break;
            case L:
                shape = new int[][] {
                        { 0, 0, 1 },
                        { 1, 1, 1 }
                };
                color = Color.ORANGE;
                break;
        }
    }

    public int[][] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Rota la pieza 90 grados en el sentido de las agujas del reloj.
     */
    public void rotate() {
        int filas = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][filas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][filas - 1 - i] = shape[i][j];
            }
        }
        shape = rotated;
    }

    /**
     * Devuelve una copia rotada de la pieza (sin modificar la original)
     */
    public Piece getRotatedCopy() {
        Piece copy = new Piece(this.type);
        int filas = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][filas];
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][filas - 1 - i] = shape[i][j];
            }
        }
        copy.shape = rotated;
        copy.setX(this.x);
        copy.setY(this.y);
        return copy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void moveDown() {
        y++;
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }
}
