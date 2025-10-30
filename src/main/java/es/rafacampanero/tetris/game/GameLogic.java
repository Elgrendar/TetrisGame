package es.rafacampanero.tetris.game;

import es.rafacampanero.tetris.App;
import es.rafacampanero.tetris.sound.SoundManager;

import java.awt.event.KeyEvent;

public class GameLogic {

    /**
     * Maneja la entrada del teclado para mover/rotar la pieza.
     * 
     * @param board
     * @param currentPiece
     * @param e
     */
    public void handleKey(int[][] board, Piece currentPiece, KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> moveLeft(board, currentPiece);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> moveRight(board, currentPiece);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> moveDown(board, currentPiece);
            case KeyEvent.VK_SPACE -> hardDrop(board, currentPiece);
            case KeyEvent.VK_W, KeyEvent.VK_UP -> rotate(board, currentPiece);
        }
    }

    /**
     * Comprueba si la pieza puede moverse a la posición (x, y).
     * 
     * @param board
     * @param piece
     * @param x
     * @param y
     * @return
     */
    public boolean canMove(int[][] board, Piece piece, int x, int y) {
        int[][] shape = piece.getShape();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int newX = x + j;
                    int newY = y + i;
                    if (newX < 0 || newX >= App.columnas || newY < 0 || newY >= App.filas)
                        return false;
                    if (board[newY][newX] != 0)
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Mueve la pieza a la izquierda si es posible.
     * 
     * @param board
     * @param piece
     */
    public void moveLeft(int[][] board, Piece piece) {
        if (canMove(board, piece, piece.getX() - 1, piece.getY())) {
            piece.moveLeft();
            SoundManager.playMove();
        }
    }

    /**
     * Mueve la pieza a la derecha si es posible.
     * 
     * @param board
     * @param piece
     */
    public void moveRight(int[][] board, Piece piece) {
        if (canMove(board, piece, piece.getX() + 1, piece.getY())) {
            piece.moveRight();
            SoundManager.playMove();
        }
    }

    /**
     * Mueve la pieza hacia abajo si es posible.
     * 
     * @param board
     * @param piece
     */
    public void moveDown(int[][] board, Piece piece) {
        if (canMove(board, piece, piece.getX(), piece.getY() + 1)) {
            piece.moveDown();
        }
    }

    /**
     * Deja caer la pieza hasta el fondo.
     * 
     * @param board
     * @param piece
     */
    public void hardDrop(int[][] board, Piece piece) {
        while (canMove(board, piece, piece.getX(), piece.getY() + 1)) {
            piece.moveDown();
        }
        // SoundManager.playDrop();
    }

    /**
     * Rota la pieza si es posible.
     * 
     * @param board
     * @param piece
     */
    public void rotate(int[][] board, Piece piece) {
        Piece rotated = piece.getRotatedCopy();
        if (canMove(board, rotated, rotated.getX(), rotated.getY())) {
            piece.rotate();
            SoundManager.playRotate();
        }
    }

    /**
     * Fusiona la pieza actual en el tablero.
     */
    public void lockPiece(int[][] board, Piece piece) {
        int[][] shape = piece.getShape();
        int colorId = piece.getColor().getRGB();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int x = piece.getX() + j;
                    int y = piece.getY() + i;
                    if (y >= 0 && y < App.filas && x >= 0 && x < App.columnas) {
                        if (board[y][x] == 0) {
                            board[y][x] = colorId;
                        }
                    }
                }
            }
        }
    }

    /**
     * Limpia las filas completas y devuelve cuántas se eliminaron.
     */
    public int clearFullRows(int[][] board) {
        int cleared = 0;

        for (int i = App.filas - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < App.columnas; j++) {
                if (board[i][j] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                cleared++;
                for (int k = i; k > 0; k--) {
                    board[k] = board[k - 1].clone();
                }
                board[0] = new int[App.columnas];
                i++;
                SoundManager.playClear();
            }
        }

        return cleared;
    }

    /**
     * Comprueba si se han eliminado todos los bloques de un puzzle.
     */
    public boolean isPuzzleCleared(int[][] board, int[][] puzzleMap) {
        for (int fila = 0; fila < App.filas; fila++) {
            for (int col = 0; col < App.columnas; col++) {
                if (board[fila][col] == 1)
                    return false;
            }
        }
        return true;
    }
}
