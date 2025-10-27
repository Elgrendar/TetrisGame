package es.rafacampanero.tetris;

import es.rafacampanero.tetris.game.Piece;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Panel principal del juego Tetris.
 * 
 * Contiene el tablero, maneja la pieza actual, su movimiento y las colisiones.
 */
public class GamePanel extends JPanel {

    private final int filas = 20; // filas del tablero
    private final int columnas = 10; // columnas del tablero
    private final int tamanoCelda = 30; // tamaño de cada celda en píxeles

    private int[][] board; // matriz del tablero (0 = vacío, >0 = color)
    private Piece currentPiece; // pieza que está cayendo
    private Random random;
    private boolean gameOver = false; // ✅ Flag para controlar el fin del juego

    public GamePanel() {
        setPreferredSize(new Dimension(columnas * tamanoCelda, filas * tamanoCelda));
        setBackground(Color.DARK_GRAY);

        board = new int[filas][columnas];
        random = new Random();

        spawnNewPiece(); // generar la primera pieza

        // Configurar listener de teclado
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameOver)
                    return; // si ya terminó, ignorar teclas
                handleKey(e);
                repaint();
            }
        });

        // Iniciar bucle de juego
        startGameLoop();
    }

    /**
     * Maneja las teclas presionadas para mover o rotar la pieza.
     */
    private void handleKey(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (canMove(currentPiece, currentPiece.getX() - 1, currentPiece.getY()))
                    currentPiece.moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                if (canMove(currentPiece, currentPiece.getX() + 1, currentPiece.getY()))
                    currentPiece.moveRight();
                break;
            case KeyEvent.VK_DOWN:
                if (canMove(currentPiece, currentPiece.getX(), currentPiece.getY() + 1))
                    currentPiece.moveDown();
                break;
            case KeyEvent.VK_W:
                Piece rotated = currentPiece.getRotatedCopy();
                if (canMove(rotated, rotated.getX(), rotated.getY()))
                    currentPiece.rotate();
                break;
        }
    }

    /**
     * Inicia un thread que mueve la pieza hacia abajo cada intervalo de tiempo.
     */
    private void startGameLoop() {
        Thread gameThread = new Thread(() -> {
            try {
                while (!gameOver) {// ✅ se detiene cuando termina el juego
                    Thread.sleep(500); // velocidad de caída

                    if (canMove(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
                        currentPiece.moveDown();
                    } else {
                        // No puede bajar más -> "colisionó" con suelo o piezas
                        mergePieceToBoard(currentPiece);
                        clearFullRows();
                        spawnNewPiece();
                    }
                    repaint();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        gameThread.start();
    }

    /**
     * Verifica si la pieza puede moverse a la posición (x, y) sin chocar.
     */
    private boolean canMove(Piece piece, int x, int y) {
        int[][] shape = piece.getShape();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int newX = x + j;
                    int newY = y + i;

                    // Verificar bordes del tablero
                    if (newX < 0 || newX >= columnas || newY < 0 || newY >= filas)
                        return false;

                    // Verificar colisión con otras piezas
                    if (board[newY][newX] != 0)
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Inserta la pieza en el tablero cuando ya no puede bajar más.
     */
    private void mergePieceToBoard(Piece piece) {
        int[][] shape = piece.getShape();
        Color color = piece.getColor();
        int colorId = color.getRGB(); // usamos RGB como identificador único

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int x = piece.getX() + j;
                    int y = piece.getY() + i;
                    if (y >= 0 && y < filas && x >= 0 && x < columnas)
                        board[y][x] = colorId;
                }
            }
        }
    }

    /**
     * Borra filas completas y desplaza las filas superiores hacia abajo.
     */
    private void clearFullRows() {
        for (int i = filas - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < columnas; j++) {
                if (board[i][j] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                // Borrar fila y mover filas superiores hacia abajo
                for (int k = i; k > 0; k--) {
                    board[k] = board[k - 1].clone();
                }
                board[0] = new int[columnas]; // fila vacía arriba
                i++; // revisar misma fila de nuevo
            }
        }
    }

    /**
     * Genera una nueva pieza aleatoria y comprueba si hay espacio para colocarla.
     */
    private void spawnNewPiece() {
        Piece.Type[] types = Piece.Type.values();
        Piece.Type type = types[random.nextInt(types.length)];
        currentPiece = new Piece(type);

        // ✅ Si no hay espacio para colocar la nueva pieza → fin del juego
        if (!canMove(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            gameOver = true;
            repaint();
            JOptionPane.showMessageDialog(this, App.mensajes.getString("game.end"), App.mensajes.getString("game.end.title"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelHeight = getHeight();

        // Dibujar piezas ya colocadas en el tablero
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                if (board[fila][col] != 0) {
                    g.setColor(new Color(board[fila][col]));
                    int x = col * tamanoCelda;
                    // Dibuja desde abajo
                    int y = panelHeight - (filas - fila) * tamanoCelda;
                    g.fillRect(x, y, tamanoCelda, tamanoCelda);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, tamanoCelda, tamanoCelda);
                }
            }
        }

        // Dibujar la pieza actual
        int[][] shape = currentPiece.getShape();
        g.setColor(currentPiece.getColor());
        for (int fila = 0; fila < shape.length; fila++) {
            for (int col = 0; col < shape[fila].length; col++) {
                if (shape[fila][col] != 0) {
                    int x = (currentPiece.getX() + col) * tamanoCelda;
                    int y = panelHeight - (filas - (currentPiece.getY() + fila)) * tamanoCelda;
                    g.fillRect(x, y, tamanoCelda, tamanoCelda);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, tamanoCelda, tamanoCelda);
                    g.setColor(currentPiece.getColor());
                }
            }
        }

        // Dibujar la cuadrícula
        g.setColor(Color.GRAY);
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                int x = col * tamanoCelda;
                int y = panelHeight - (filas - fila) * tamanoCelda;
                g.drawRect(x, y, tamanoCelda, tamanoCelda);
            }
        }

        // ✅ Mostrar mensaje de “Game Over” en pantalla
        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            String msg = App.mensajes.getString("game.end.title");
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(msg);
            g.drawString(msg, (getWidth() - textWidth) / 2, getHeight() / 2);
        }
    }

}
