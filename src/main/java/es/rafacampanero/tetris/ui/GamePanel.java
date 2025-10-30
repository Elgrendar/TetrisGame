package es.rafacampanero.tetris.ui;

import es.rafacampanero.tetris.App;
import es.rafacampanero.tetris.game.GameLogic;
import es.rafacampanero.tetris.game.HighScoreManager;
import es.rafacampanero.tetris.game.Piece;
import es.rafacampanero.tetris.sound.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel {

    private int[][] board;
    private Piece currentPiece;
    private Random random;
    private final GameLogic logic = new GameLogic();

    private Thread gameThread;
    private boolean running = false;
    private int dropSpeed = 500;

    private int score = 0;
    private int linesCleared = 0;
    private int level = 1;
    private int linesPerLevel = 10;

    private final HighScoreManager highScoreManager = new HighScoreManager();
    private Runnable onGameOver;

    public GamePanel() {
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
                logic.handleKey(board, currentPiece, e);
                repaint();
            }
        });
    }

    private void spawnNewPiece() {
        Piece.Type[] types = Piece.Type.values();
        currentPiece = new Piece(types[random.nextInt(types.length)]);
    }

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

                    if (logic.canMove(board, currentPiece, currentPiece.getX(), currentPiece.getY() + 1)) {
                        currentPiece.moveDown();
                    } else {
                        logic.lockPiece(board, currentPiece);
                        int cleared = logic.clearFullRows(board);
                        linesCleared += cleared;

                        int newLevel = linesCleared / linesPerLevel + 1;
                        if (newLevel > level) {
                            level = newLevel;
                            dropSpeed = Math.max(100, 500 - (level - 1) * 50);
                        }

                        if (cleared > 0) {
                            switch (cleared) {
                                case 1 -> score += 100;
                                case 2 -> score += 300;
                                case 3 -> score += 500;
                                case 4 -> score += 800;
                            }
                        }

                        spawnNewPiece();

                        if (!logic.canMove(board, currentPiece, currentPiece.getX(), currentPiece.getY())) {
                            running = false;
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this,
                                        App.mensajes.getString("game.end"),
                                        App.mensajes.getString("game.end.title"),
                                        JOptionPane.INFORMATION_MESSAGE);

                                if (highScoreManager.qualifiesForHighScore(score)) {
                                    String name = JOptionPane.showInputDialog(
                                            this,
                                            App.mensajes.getString("game.highscore.entername"),
                                            "High Score",
                                            JOptionPane.PLAIN_MESSAGE);
                                    if (name != null && !name.isBlank())
                                        highScoreManager.addScore(name.trim(), score);
                                }

                                JFrame hsFrame = new JFrame("Muro de la Fama");
                                hsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                hsFrame.add(new HighScorePanel(highScoreManager));
                                hsFrame.pack();
                                hsFrame.setLocationRelativeTo(this);
                                hsFrame.setVisible(true);

                                hsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                                    @Override
                                    public void windowClosed(java.awt.event.WindowEvent e) {
                                        if (onGameOver != null)
                                            onGameOver.run();
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

    public void setOnGameOver(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int panelHeight = getHeight();
        int cell = App.tamanoCelda;

        // fondo
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, App.columnas * cell, App.filas * cell);

        // piezas fijas
        for (int fila = 0; fila < App.filas; fila++) {
            for (int col = 0; col < App.columnas; col++) {
                if (board[fila][col] != 0) {
                    g.setColor(new Color(board[fila][col]));
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

        // lateral puntuación
        int offsetX = App.columnas * cell + 20;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 18));
        g.drawString(App.mensajes.getString("game.score") + ": " + score, offsetX, 50);
        g.drawString(App.mensajes.getString("game.lines") + ": " + linesCleared, offsetX, 80);
        g.drawString(App.mensajes.getString("game.level") + ": " + level, offsetX, 110);
    }
}
