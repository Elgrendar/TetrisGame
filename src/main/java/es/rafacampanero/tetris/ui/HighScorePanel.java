package es.rafacampanero.tetris.ui;

import es.rafacampanero.tetris.persist.HighScoreManager;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Panel para mostrar el muro de la fama (High Scores)
 */
public class HighScorePanel extends JPanel {

    public HighScorePanel(HighScoreManager manager) {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        JLabel title = new JLabel("🏆 " + "Muro de la Fama", SwingConstants.CENTER);
        title.setFont(new Font("Consolas", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        String[] columnas = { "#", "Alias", "Puntuación", "Fecha" };
        List<HighScoreManager.Entry> scores = HighScoreManager.load();

        String[][] data = new String[Math.min(scores.size(), 20)][4];
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (int i = 0; i < data.length; i++) {
            HighScoreManager.Entry e = scores.get(i);
            data[i][0] = String.valueOf(i + 1);
            data[i][1] = e.alias;
            data[i][2] = String.valueOf(e.score);
            data[i][3] = sdf.format(new Date(e.date));
        }

        JTable table = new JTable(data, columnas);
        table.setEnabled(false);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Consolas", Font.PLAIN, 14));
        table.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Cerrar");
        closeBtn.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.DARK_GRAY);
        bottom.add(closeBtn);
        add(bottom, BorderLayout.SOUTH);
    }
}
