package es.rafacampanero.tetris;

import es.rafacampanero.tetris.game.Piece;
import es.rafacampanero.tetris.sound.SoundManager;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Panel principal del juego Tetris.
 *
 * Controla el tablero, piezas, colisiones, sonidos y puntuación.
 *
 * Ahora exposa métodos start() y stop() para controlar el bucle desde fuera
 * (para que el juego no arranque automáticamente al crear el panel).
 */
public class GamePanel extends JPanel {

    private final int filas = 20;
    private final int columnas = 10;
    private final int tamanoCelda = 30;

    private int[][] board;
    private Piece currentPiece;
    private Random random;
    private int dropSpeed = 500; // milisegundos

    // 🎯 Variables de puntuación
    private int score = 0;
    private int linesCleared = 0;

    // Control del hilo de juego
    private Thread gameThread;
    private volatile boolean running = false; // bandera para controlar el hilo

    public GamePanel() {
        // Ahora el panel es más ancho para mostrar la puntuación
        setPreferredSize(new Dimension(columnas * tamanoCelda + 150, filas * tamanoCelda));
        setBackground(Color.DARK_GRAY);

        board = new int[filas][columnas];
        random = new Random();
        SoundManager.init();

        spawnNewPiece();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // si no estamos corriendo, ignoramos input de juego
                if (!running)
                    return;
                handleKey(e);
                repaint();
            }
        });

        // NOTA: NO arrancamos el bucle aquí. Llamarás a start() desde el menú.
        // startGameLoop(); <-- eliminado
    }

    /**
     * Inicia el hilo del juego. Llamar esto cuando el usuario elija "Empezar".
     */
    public void start() {
        if (running)
            return;
        running = true;
        gameThread = new Thread(() -> {
            try {
                while (running) {
                    Thread.sleep(dropSpeed);
                    if (currentPiece == null)
                        continue;

                    if (canMove(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
                        currentPiece.moveDown();
                    } else {
                        mergePieceToBoard(currentPiece);
                        int cleared = clearFullRows();
                        linesCleared += cleared;
                        // 💯 Añadir puntuación por filas borradas
                        if (cleared > 0) {
                            SoundManager.playClear();
                            switch (cleared) {
                                case 1 -> score += 100;
                                case 2 -> score += 300;
                                case 3 -> score += 500;
                                case 4 -> score += 800; // Tetris!
                            }
                        }

                        spawnNewPiece();

                        // 🔚 Verificar si hay piezas en la parte superior
                        if (!canMove(currentPiece, currentPiece.getX(), currentPiece.getY())) {
                            // DO NOT auto-reset here; delegate to caller or show dialog and stop
                            running = false; // detener el juego
                            // mostrar mensaje y gestionar highscores desde el UI que invocó start()
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this,
                                        App.mensajes.getString("game.end"),
                                        "Game Over", JOptionPane.INFORMATION_MESSAGE);
                            });
                        }
                    }
                    repaint();
                }
            } catch (InterruptedException e) {
                // Thread interrumpido, salimos limpiamente
                Thread.currentThread().interrupt();
            }
        });
        gameThread.start();
    }

    /**
     * Detiene el hilo del juego (vuelve al menú sin reiniciar automáticamente).
     */
    public void stop() {
        running = false;
        if (gameThread != null) {
            gameThread.interrupt();
            try {
                gameThread.join(200); // esperamos un corto tiempo
            } catch (InterruptedException ignored) {
            }
            gameThread = null;
        }
    }

    private void handleKey(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (canMove(currentPiece, currentPiece.getX() - 1, currentPiece.getY())) {
                    currentPiece.moveLeft();
                    SoundManager.playMove();
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (canMove(currentPiece, currentPiece.getX() + 1, currentPiece.getY())) {
                    currentPiece.moveRight();
                    SoundManager.playMove();
                }
                break;
            case KeyEvent.VK_DOWN:
                if (canMove(currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
                    currentPiece.moveDown();
                }
                break;
            case KeyEvent.VK_W:
                Piece rotated = currentPiece.getRotatedCopy();
                if (canMove(rotated, rotated.getX(), rotated.getY())) {
                    currentPiece.rotate();
                    SoundManager.playRotate();
                }
                break;
        }
    }

    private boolean canMove(Piece piece, int x, int y) {
        int[][] shape = piece.getShape();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int newX = x + j;
                    int newY = y + i;

                    if (newX < 0 || newX >= columnas || newY < 0 || newY >= filas)
                        return false;

                    if (board[newY][newX] != 0)
                        return false;
                }
            }
        }
        return true;
    }

    private void mergePieceToBoard(Piece piece) {
        int[][] shape = piece.getShape();
        Color color = piece.getColor();
        int colorId = color.getRGB();

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
     * Limpia filas completas y devuelve cuántas se eliminaron.
     */
    private int clearFullRows() {
        int linesCleared = 0;

        for (int i = filas - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < columnas; j++) {
                if (board[i][j] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                linesCleared++;
                for (int k = i; k > 0; k--) {
                    board[k] = board[k - 1].clone();
                }
                board[0] = new int[columnas];
                i++;
            }
        }
        return linesCleared;
    }

    private void spawnNewPiece() {
        Piece.Type[] types = Piece.Type.values();
        Piece.Type type = types[random.nextInt(types.length)];
        currentPiece = new Piece(type);
    }

    /**
     * Reinicia el juego tras un "Game Over".
     */
    private void resetGame() {
        board = new int[filas][columnas];
        score = 0;
        spawnNewPiece();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int panelHeight = getHeight();

        // 🟦 Dibujar tablero principal
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                if (board[fila][col] != 0) {
                    g.setColor(new Color(board[fila][col]));
                    int x = col * tamanoCelda;
                    int y = panelHeight - (filas - fila) * tamanoCelda;
                    g.fillRect(x, y, tamanoCelda, tamanoCelda);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, tamanoCelda, tamanoCelda);
                }
            }
        }

        // 🧩 Dibujar pieza actual
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

        // 🕹️ Cuadrícula
        g.setColor(Color.GRAY);
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                int x = col * tamanoCelda;
                int y = panelHeight - (filas - fila) * tamanoCelda;
                g.drawRect(x, y, tamanoCelda, tamanoCelda);
            }
        }

        // 📊 Panel lateral derecho con puntuación
        int offsetX = columnas * tamanoCelda + 20;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 18));
        g.drawString(App.mensajes.getString("game.score") + ": " + score, offsetX, 50);
        g.drawString(App.mensajes.getString("game.lines") + ": " + linesCleared, offsetX, 80);
    }
}
