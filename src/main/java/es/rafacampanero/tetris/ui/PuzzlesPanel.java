package es.rafacampanero.tetris.ui;

import es.rafacampanero.tetris.App;
import es.rafacampanero.tetris.game.GameLogic;
import es.rafacampanero.tetris.game.Piece;
import es.rafacampanero.tetris.game.PuzzleMaps;
import es.rafacampanero.tetris.sound.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class PuzzlesPanel extends JPanel {

    private int level = 0;
    private int[][] currentPuzzle;
    private Piece currentPiece;
    private int[][] board;
    private int dropSpeed;
    private Random random;
    private final GameLogic logic = new GameLogic();

    private Thread gameThread;
    private boolean running = false;
    private Runnable onGameOver;

    private static final int TOTAL_PUZZLES = 10;

    /**
     * Constructor de PuzzlesPanel.
     */
    public PuzzlesPanel() {
        setPreferredSize(new Dimension(App.columnas * App.tamanoCelda, App.filas * App.tamanoCelda));
        setBackground(Color.DARK_GRAY);
        board = new int[App.filas][App.columnas];
        random = new Random();
        loadPuzzle(level);
        
        SoundManager.init();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!running)
                    return;
                logic.handleKey(board, currentPiece, e);
                repaint();
            }
        });
    }

    /**
     * Carga el mapa del puzle para el nivel dado.
     * 
     * @param level
     */
    private void loadPuzzle(int level) {
        board = new int[App.filas][App.columnas];
        currentPuzzle = PuzzleMaps.LEVELS[level];
        // copiar los bloques del puzzle al tablero
        for (int fila = 0; fila < App.filas; fila++)
            System.arraycopy(currentPuzzle[fila], 0, board[fila], 0, App.columnas);

        dropSpeed = 500 - level * 30;
        spawnNewPiece();
    }

    /**
     * Genera una nueva pieza aleatoria.
     */
    private void spawnNewPiece() {
        Piece.Type[] types = Piece.Type.values();
        currentPiece = new Piece(types[random.nextInt(types.length)]);
    }

    /**
     * Inicia el hilo del juego para el modo Puzles.
     */
    public void start() {
        running = true;

        gameThread = new Thread(() -> {
            try {
                while (running) {
                    Thread.sleep(dropSpeed);

                    if (currentPiece == null)
                        continue;

                    if (logic.canMove(board, currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
                        currentPiece.moveDown();
                    } else {
                        // Bloquear la pieza en el tablero
                        logic.lockPiece(board, currentPiece);
                        // Limpiar filas completas
                        int cleared = logic.clearFullRows(board);

                        if (logic.isPuzzleCleared(board, currentPuzzle)) {
                            nextLevel();
                        }

                        spawnNewPiece();
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
     * Avanza al siguiente nivel o termina el juego si se completaron todos los
     * puzles.
     */
    private void nextLevel() {
        if (level + 1 < TOTAL_PUZZLES) {
            level++;
            loadPuzzle(level);
        } else {
            running = false;
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        App.mensajes.getString("puzles.complete"),
                        App.mensajes.getString("puzles.complete.title"),
                        JOptionPane.INFORMATION_MESSAGE);
                if (onGameOver != null)
                    onGameOver.run();
            });
        }
    }

    /**
     * Establece la acción a ejecutar cuando el juego termina.
     * 
     * @param onGameOver
     */
    public void setOnGameOver(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }

    /**
     * Dibuja el estado actual del juego.
     * 
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int panelHeight = getHeight();
        int cell = App.tamanoCelda;

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, App.columnas * cell, App.filas * cell);

        // piezas fijas
        for (int fila = 0; fila < App.filas; fila++) {
            for (int col = 0; col < App.columnas; col++) {
                if (board[fila][col] != 0) {
                    if (board[fila][col] == 1) {
                        g.setColor(Color.LIGHT_GRAY);
                    } else {
                        g.setColor(new Color(board[fila][col]));
                    }
                    int x = col * cell;
                    int y = panelHeight - (App.filas - fila) * cell;
                    g.fillRect(x, y, cell, cell);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, cell, cell);
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

        // pieza actual
        int[][] shape = currentPiece.getShape();
        g.setColor(currentPiece.getColor());
        for (int fila = 0; fila < shape.length; fila++) {
            for (int col = 0; col < shape[fila].length; col++) {
                if (shape[fila][col] != 0) {
                    int x = (currentPiece.getX() + col) * cell;
                    int y = panelHeight - (App.filas - (currentPiece.getY() + fila)) * cell;
                    g.fillRect(x, y, cell, cell);
                    g.setColor(currentPiece.getColor());
                    g.drawRect(x, y, cell, cell);
                }
            }
        }

        // mostrar nivel lateral
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 18));
        g.drawString("Level: " + (level + 1), App.columnas * cell + 20, 50);
    }
}
