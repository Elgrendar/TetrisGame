package es.rafacampanero.tetris;

import es.rafacampanero.tetris.persist.HighScoreManager;
import es.rafacampanero.tetris.ui.MainMenuPanel;
import javax.swing.*;
import java.util.Locale;
/**
 * Clase principal del juego Tetris.
 * Crea la ventana base y carga el idioma por defecto (español).
 * 
 * @author rafacampanero.es
 * @version 1.0
 */


public class App {

    public static java.util.ResourceBundle mensajes;

    public static void main(String[] args) {
        Locale locale = new Locale("es");
        mensajes = java.util.ResourceBundle.getBundle("languages.strings", locale);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(mensajes.getString("game.title"));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(true);

            HighScoreManager hsManager = new HighScoreManager();

            MainMenuPanel menu = new MainMenuPanel(frame, hsManager);
            frame.add(menu);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
