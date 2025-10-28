package es.rafacampanero.tetris;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JFrame;

/**
 * Clase principal del juego Tetris.
 * Crea la ventana base y carga el idioma por defecto (español).
 * 
 * @author rafacampanero.es
 * @version 1.0
 */
public class App {

    // Recurso de idioma
    public static ResourceBundle mensajes;

    public static void main(String[] args) {
        // Carga de idioma por defecto (español)
        Locale locale = new Locale("es");
        mensajes = ResourceBundle.getBundle("languages.strings", locale);

        // Crear la ventana principal
        JFrame ventana = new JFrame(mensajes.getString("game.title"));
        ventana.setSize(800, 600);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null); // centrada
        ventana.setResizable(true);

        // Crear y agregar el panel del juego
        GamePanel panel = new GamePanel();
        ventana.add(panel);

        ventana.setVisible(true);
    }
}
