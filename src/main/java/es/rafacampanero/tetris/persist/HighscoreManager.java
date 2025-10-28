package es.rafacampanero.tetris.persist;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager {
    private static final File FILE = new File("data/highscores.json");
    private static final ObjectMapper mapper = new ObjectMapper();

    public static class Entry {
        public String alias;
        public int score;
        public long date;

        public Entry() {}

        public Entry(String alias, int score) {
            this.alias = alias;
            this.score = score;
            this.date = Instant.now().toEpochMilli();
        }
    }

    public static List<Entry> load() {
        try {
            if (!FILE.exists()) {
                FILE.getParentFile().mkdirs();
                mapper.writeValue(FILE, new ArrayList<Entry>());
            }
            return mapper.readValue(FILE, new TypeReference<List<Entry>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void save(Entry e) {
        try {
            List<Entry> list = load();
            list.add(e);
            list.sort(Comparator.comparingInt((Entry x) -> x.score).reversed());
            mapper.writerWithDefaultPrettyPrinter().writeValue(FILE, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
