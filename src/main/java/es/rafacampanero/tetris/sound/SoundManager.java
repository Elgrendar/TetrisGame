package es.rafacampanero.tetris.sound;

import javafx.scene.media.AudioClip;

import java.net.URL;

public class SoundManager {
    private static AudioClip move, rotate, clear;

    public static void init() {
        move = load("sounds/move.wav");
        rotate = load("sounds/rotate.wav");
        clear = load("sounds/clear.wav");
    }

    private static AudioClip load(String resourcePath) {
        try {
            URL url = SoundManager.class.getClassLoader().getResource(resourcePath);
            if (url == null) return null;
            return new AudioClip(url.toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void playMove() {
        if (move != null) move.play();
    }

    public static void playRotate() {
        if (rotate != null) rotate.play();
    }

    public static void playClear() {
        if (clear != null) clear.play();
    }
}
