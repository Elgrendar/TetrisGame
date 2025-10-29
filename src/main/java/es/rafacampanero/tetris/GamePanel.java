package es.rafacampanero.tetris;

import es.rafacampanero.tetris.game.Piece;
import es.rafacampanero.tetris.game.HighScoreManager;
import es.rafacampanero.tetris.sound.SoundManager;
import es.rafacampanero.tetris.ui.HighScorePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Panel principal del juego Tetris.
 * Controla el tablero, piezas, colisiones, sonidos y puntuación.
 * 
 * Ahora el juego no arranca automáticamente: se debe llamar a start() desde
 * fuera.
 */
public class GamePanel extends JPanel {

    private int[][] board;
    private Piece currentPiece;
    private Random random;

    private Thread gameThread;
    private boolean running = false;
    private int dropSpeed = 500; // milisegundos

    // 🎯 Puntuación
    private int score = 0;
    private int linesCleared = 0;

    // 🏆 Gestor de puntuaciones
    private final HighScoreManager highScoreManager = new HighScoreManager();
    private Runnable onGameOver;

    public GamePanel() {
        // Aumentamos el ancho para mostrar puntuación
        setPreferredSize(new Dimension(App.columnas * App.tamanoCelda + 150, App.filas * App.tamanoCelda));
        setBackground(Color.DARK_GRAY);

        board = new int[App.filas][App.columnas];
        random = new Random();
        SoundManager.init();

        spawnNewPiece();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!running)
                    return;
                handleKey(e);
                repaint();
            }
        });
    }

    /**
     * Inicia el bucle principal del juego.
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

                        if (cleared > 0) {
                            SoundManager.playClear();
                            switch (cleared) {
                                case 1 -> score += 100;
                                case 2 -> score += 300;
                                case 3 -> score += 500;
                                case 4 -> score += 800;
                            }
                        }

                        spawnNewPiece();

                        // 🔚 Verificar si el juego termina
                        if (!canMove(currentPiece, currentPiece.getX(), currentPiece.getY())) {
                            running = false;

                            SwingUtilities.invokeLater(() -> {
                                // Mostrar "Game Over"
                                JOptionPane.showMessageDialog(
                                        this,
                                        App.mensajes.getString("game.end"),
                                        App.mensajes.getString("game.end.title"),
                                        JOptionPane.INFORMATION_MESSAGE);

                                // Si la puntuación califica para el top 20
                                if (highScoreManager.qualifiesForHighScore(score)) {
                                    String name = JOptionPane.showInputDialog(
                                            this,
                                            App.mensajes.getString("game.highscore.entername"),
                                            "High Score",
                                            JOptionPane.PLAIN_MESSAGE);
                                    if (name != null && !name.isBlank()) {
                                        highScoreManager.addScore(name.trim(), score);
                                    }
                                }

                                // Mostrar muro de la fama
                                JFrame hsFrame = new JFrame("Muro de la Fama");
                                hsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                hsFrame.add(new HighScorePanel(highScoreManager));
                                hsFrame.pack();
                                hsFrame.setLocationRelativeTo(this);
                                hsFrame.setVisible(true);

                                // 👇 Después de mostrar el muro, volver al menú principal
                                hsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                                    @Override
                                    public void windowClosed(java.awt.event.WindowEvent e) {
                                        if (onGameOver != null) {
                                            onGameOver.run();
                                        }
                                    }
                                });
                            });
                        }
                    }

                    repaint();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        gameThread.start();
    }

    /**
     * Detiene el juego y limpia el hilo.
     */
    public void stop() {
        running = false;
        if (gameThread != null) {
            gameThread.interrupt();
            try {
                gameThread.join(200);
            } catch (InterruptedException ignored) {
            }
            gameThread = null;
        }
    }

    public void setOnGameOver(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }

    private void handleKey(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> {
                if (canMove(currentPiece, currentPiece.getX() - 1, currentPiece.getY())) {
                    currentPiece.moveLeft();
                    SoundManager.playMove();
                }
            }
            case KeyEvent.VK_RIGHT -> {
                if (canMove(currentPiece, currentPiece.getX() + 1, currentPiece.getY())) {
                    currentPiece.moveRight();
                    SoundManager.playMove();
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
                    SoundManager.playRotate();
                }
            }
        }
    }

    private boolean canMove(Piece piece, int x, int y) {
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

    private void mergePieceToBoard(Piece piece) {
        int[][] shape = piece.getShape();
        int colorId = piece.getColor().getRGB();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int x = piece.getX() + j;
                    int y = piece.getY() + i;
                    if (y >= 0 && y < App.filas && x >= 0 && x < App.columnas) {
                        board[y][x] = colorId;
                    }
                }
            }
        }
    }

    private int clearFullRows() {
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
            }
        }

        return cleared;
    }

    private void spawnNewPiece() {
        Piece.Type[] types = Piece.Type.values();
        currentPiece = new Piece(types[random.nextInt(types.length)]);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int panelHeight = getHeight();

        // 🟦 Dibujar fondo del tablero
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, App.columnas * App.tamanoCelda, App.filas * App.tamanoCelda);

        // 🟪 Dibujar las piezas fijas en el tablero
        for (int fila = 0; fila < App.filas; fila++) {
            for (int col = 0; col < App.columnas; col++) {
                if (board[fila][col] != 0) {
                    g.setColor(new Color(board[fila][col]));
                    int x = col * App.tamanoCelda;
                    int y = panelHeight - (App.filas - fila) * App.tamanoCelda;
                    g.fillRect(x, y, App.tamanoCelda, App.tamanoCelda);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, App.tamanoCelda, App.tamanoCelda);
                }
            }
        }

        // 🧩 Dibujar la pieza actual
        int[][] shape = currentPiece.getShape();
        g.setColor(currentPiece.getColor());
        for (int fila = 0; fila < shape.length; fila++) {
            for (int col = 0; col < shape[fila].length; col++) {
                if (shape[fila][col] != 0) {
                    int x = (currentPiece.getX() + col) * App.tamanoCelda;
                    int y = panelHeight - (App.filas - (currentPiece.getY() + fila)) * App.tamanoCelda;
                    g.fillRect(x, y, App.tamanoCelda, App.tamanoCelda);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, App.tamanoCelda, App.tamanoCelda);
                    g.setColor(currentPiece.getColor());
                }
            }
        }

        // 🕹️ Dibujar la cuadrícula completa encima
        g.setColor(Color.WHITE);
        for (int fila = 0; fila < App.filas; fila++) {
            for (int col = 0; col < App.columnas; col++) {
                int x = col * App.tamanoCelda;
                int y = panelHeight - (App.filas - fila) * App.tamanoCelda;
                g.drawRect(x, y, App.tamanoCelda, App.tamanoCelda);
            }
        }

        // 📊 Lateral con puntuación
        int offsetX = App.columnas * App.tamanoCelda + 20;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 18));
        g.drawString(App.mensajes.getString("game.score") + ": " + score, offsetX, 50);
        g.drawString(App.mensajes.getString("game.lines") + ": " + linesCleared, offsetX, 80);
    }
}
