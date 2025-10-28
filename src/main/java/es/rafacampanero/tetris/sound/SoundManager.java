package es.rafacampanero.tetris.sound;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * Gestor de sonidos para el juego Tetris.
 * Carga y reproduce efectos de sonido (move, rotate, clear).
 */
public class SoundManager {

    private static Clip moveClip;
    private static Clip rotateClip;
    private static Clip clearClip;

    /**
     * Inicializa los efectos de sonido cargando los ficheros.
     */
    public static void init() {
        moveClip = loadClip("sounds/move.wav");
        rotateClip = loadClip("sounds/rotate.wav");
        clearClip = loadClip("sounds/clear.wav");
    }

    /**
     * Carga un Clip desde un recurso en la carpeta resources.
     * 
     * @param path ruta relativa dentro de resources.
     * @return Clip cargado o null si fallo.
     */
    private static Clip loadClip(String path) {
        try {
            URL url = SoundManager.class.getClassLoader().getResource(path);
            if (url == null) {
                System.err.println("No se encontró el recurso de sonido: " + path);
                return null;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reproduce el sonido de “move” desde el inicio.
     */
    public static void playMove() {
        if (moveClip != null) {
            moveClip.stop();
            moveClip.setFramePosition(0);
            moveClip.start();
        }
    }

    /**
     * Reproduce el sonido de “rotate” desde el inicio.
     */
    public static void playRotate() {
        if (rotateClip != null) {
            rotateClip.stop();
            rotateClip.setFramePosition(0);
            rotateClip.start();
        }
    }

    /**
     * Reproduce el sonido de “clear” (fila eliminada) desde el inicio.
     */
    public static void playClear() {
        if (clearClip != null) {
            clearClip.stop();
            clearClip.setFramePosition(0);
            clearClip.start();
        }
    }
}
