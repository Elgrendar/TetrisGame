package es.rafacampanero.tetris.ui;

import es.rafacampanero.tetris.game.HighScoreManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel para mostrar el Muro de la Fama (High Scores).
 * Puede usarse al finalizar la partida.
 */
public class HighScorePanel extends JPanel {

    private HighScoreManager highScoreManager;

    public HighScorePanel(es.rafacampanero.tetris.game.HighScoreManager highScoreManager2) {
        this.highScoreManager = highScoreManager2;
        setPreferredSize(new Dimension(400, 600));
        setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Título
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 24));
        g.drawString("Muro de la Fama", 80, 50);

        // Obtener lista de puntuaciones
        List<HighScoreManager.ScoreEntry> scores = highScoreManager.getHighScores();

        // Dibujar cada entrada
        g.setFont(new Font("Consolas", Font.PLAIN, 18));
        int y = 100;
        int rank = 1;
        for (HighScoreManager.ScoreEntry entry : scores) {
            g.setColor(Color.YELLOW);
            g.drawString(rank + ".", 50, y);
            g.setColor(Color.WHITE);
            g.drawString(entry.getName(), 80, y);
            g.drawString(String.valueOf(entry.getScore()), 280, y);
            y += 30;
            rank++;
        }

        // Si no hay puntuaciones
        if (scores.isEmpty()) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawString("No hay puntuaciones todavía.", 50, 150);
        }
    }
}
