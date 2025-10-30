package es.rafacampanero.tetris.ui;

import es.rafacampanero.tetris.App;
import es.rafacampanero.tetris.game.HighScoreManager;

import javax.swing.*;
import java.awt.*;

/**
 * Panel del menú principal con opciones: Empezar, Muro de la Fama,
 * Configuración.
 */
public class MainMenuPanel extends JPanel {

    private final JFrame parentFrame;
    private final HighScoreManager highScoreManager;

    public MainMenuPanel(JFrame parent, HighScoreManager highScoreManager) {
        this.parentFrame = parent;
        this.highScoreManager = highScoreManager;
        setLayout(new GridBagLayout());
        setBackground(Color.DARK_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel(App.mensajes.getString("game.title"), SwingConstants.CENTER);
        title.setFont(new Font("Consolas", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(title, gbc);

        JButton startBtn = new JButton(App.mensajes.getString("menu.start"));
        gbc.gridy = 1;
        add(startBtn, gbc);

        JButton puzzlesBtn = new JButton(App.mensajes.getString("puzles.title"));
        gbc.gridy = 2;
        add(puzzlesBtn, gbc);

        JButton hsBtn = new JButton(App.mensajes.getString("menu.highscores"));
        gbc.gridy = 3;
        add(hsBtn, gbc);

        JButton cfgBtn = new JButton(App.mensajes.getString("menu.config"));
        gbc.gridy = 4;
        add(cfgBtn, gbc);

        // Acción: Empezar juego normal
        startBtn.addActionListener(e -> {
            GamePanel gamePanel = new GamePanel();

            // 👇 Definir qué hacer cuando termine el juego
            gamePanel.setOnGameOver(() -> {
                SwingUtilities.invokeLater(() -> {
                    parentFrame.getContentPane().removeAll();
                    parentFrame.add(new MainMenuPanel(parentFrame, highScoreManager));
                    parentFrame.revalidate();
                    parentFrame.repaint();
                });
            });

            // Crear panel de juego y arrancar

            parentFrame.getContentPane().removeAll();
            parentFrame.add(gamePanel);
            parentFrame.revalidate();
            parentFrame.repaint();
            gamePanel.requestFocusInWindow();
            gamePanel.start(); // arrancar el bucle

        });

        // Acción: Empezar modo puzles
        puzzlesBtn.addActionListener(e -> {

            PuzzlesPanel puzzlesPanel = new PuzzlesPanel();

            // 👇 Definir qué hacer cuando termine el juego
            puzzlesPanel.setOnGameOver(() -> {
                SwingUtilities.invokeLater(() -> {
                    parentFrame.getContentPane().removeAll();
                    parentFrame.add(new MainMenuPanel(parentFrame, highScoreManager));
                    parentFrame.revalidate();
                    parentFrame.repaint();
                });
            });

            // Crear panel de juego y arrancar
            parentFrame.getContentPane().removeAll();
            parentFrame.add(puzzlesPanel);
            parentFrame.revalidate();
            parentFrame.repaint();
            puzzlesPanel.requestFocusInWindow();
            puzzlesPanel.start(); // arrancar el bucle
        });

        // Acción: Muro de la Fama
        hsBtn.addActionListener(e ->

        {
            JFrame hsFrame = new JFrame(App.mensajes.getString("menu.highscores"));
            HighScorePanel panel = new HighScorePanel(highScoreManager);
            hsFrame.add(panel);
            hsFrame.pack();
            hsFrame.setLocationRelativeTo(parentFrame);
            hsFrame.setVisible(true);
            // Cuando se cierre la ventana, volvemos al menú (ya estamos en él)
        });

        // Acción: Configuración
        cfgBtn.addActionListener(e -> {
            JFrame cfgFrame = new JFrame(App.mensajes.getString("menu.config"));
            ConfigPanel cfg = new ConfigPanel();
            cfgFrame.add(cfg);
            cfgFrame.pack();
            cfgFrame.setLocationRelativeTo(parentFrame);
            cfgFrame.setVisible(true);
        });
    }
}
