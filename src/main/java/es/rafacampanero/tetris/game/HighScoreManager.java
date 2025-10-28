package es.rafacampanero.tetris.game;

import java.io.*;
import java.util.*;

/**
 * Gestiona el muro de la fama (High Scores).
 * Guarda y carga las puntuaciones desde un archivo binario.
 */
public class HighScoreManager {

    private static final String FILE_PATH = "src/main/resources/data/highscores.dat";
    private static final int MAX_SCORES = 20; // máximo número de puntuaciones guardadas

    private List<ScoreEntry> highScores;

    /**
     * Constructor: carga las puntuaciones desde el archivo (si existe).
     */
    public HighScoreManager() {
        highScores = new ArrayList<>();
        loadScores();
    }

    /**
     * Añade una nueva puntuación al muro de la fama.
     * Si la lista supera el máximo, elimina la más baja.
     */
    public void addScore(String name, int score) {
        highScores.add(new ScoreEntry(name, score));
        Collections.sort(highScores);
        if (highScores.size() > MAX_SCORES) {
            highScores = highScores.subList(0, MAX_SCORES);
        }
        saveScores();
    }

    /**
     * Devuelve la lista de puntuaciones ordenada.
     */
    public List<ScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }

    /**
     * Comprueba si una puntuación entra en el top 20.
     */
    public boolean qualifiesForHighScore(int score) {
        if (highScores.size() < MAX_SCORES) return true;
        return score > highScores.get(highScores.size() - 1).getScore();
    }

    /**
     * Carga las puntuaciones desde el archivo binario.
     */
    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("No se encontró el archivo de puntuaciones, se creará uno nuevo.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            highScores = (List<ScoreEntry>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error al cargar las puntuaciones: " + e.getMessage());
        }
    }

    /**
     * Guarda las puntuaciones en el archivo binario.
     */
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Error al guardar las puntuaciones: " + e.getMessage());
        }
    }

    /**
     * Clase interna que representa una entrada del muro de la fama.
     */
    public static class ScoreEntry implements Serializable, Comparable<ScoreEntry> {
        private String name;
        private int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score); // orden descendente
        }

        @Override
        public String toString() {
            return name + " - " + score;
        }
    }
}
