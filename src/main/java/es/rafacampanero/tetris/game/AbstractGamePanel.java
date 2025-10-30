package es.rafacampanero.tetris.game;

import es.rafacampanero.tetris.App;
import java.awt.event.KeyEvent;

public abstract class AbstractGamePanel {

    protected int[][] board;
    protected Piece currentPiece;

    protected boolean canMove(Piece piece, int x, int y) {
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

    protected void handleKey(KeyEvent e) {
        if (currentPiece == null)
            return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> {
                if (canMove(currentPiece, currentPiece.getX() - 1, currentPiece.getY())) {
                    currentPiece.moveLeft();
                }
            }
            case KeyEvent.VK_RIGHT -> {
                if (canMove(currentPiece, currentPiece.getX() + 1, currentPiece.getY())) {
                    currentPiece.moveRight();
                }
            }
            case KeyEvent.VK_DOWN -> {
                if (canMove(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
                    currentPiece.moveDown();
                }
            }
            case KeyEvent.VK_W, KeyEvent.VK_UP -> {
                Piece rotated = currentPiece.getRotatedCopy();
                if (canMove(rotated, rotated.getX(), rotated.getY())) {
                    currentPiece.rotate();
                }
            }
        }
    }
}
