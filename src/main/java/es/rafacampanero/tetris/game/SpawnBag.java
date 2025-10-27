package es.rafacampanero.tetris.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SpawnBag {
    private static final List<Piece> bag = new ArrayList<>();
    private static final Random rnd = new Random();

    private static void refill() {
        bag.clear();
        bag.add(Piece.createI());
        bag.add(Piece.createO());
        bag.add(Piece.createT());
        bag.add(Piece.createL());
        bag.add(Piece.createJ());
        bag.add(Piece.createS());
        bag.add(Piece.createZ());
        Collections.shuffle(bag, rnd);
    }

    public static Piece next() {
        if (bag.isEmpty()) refill();
        // Clonar la pieza para no compartir el objeto
        Piece p = bag.remove(0);
        // Crear una nueva instancia con la misma forma/color
        Piece clone = new Piece(copyShape(p.shape), p.color);
        clone.x = 3;
        clone.y = 0;
        return clone;
    }

    private static boolean[][] copyShape(boolean[][] shape) {
        boolean[][] copy = new boolean[shape.length][shape[0].length];
        for (int i = 0; i < shape.length; i++) {
            System.arraycopy(shape[i], 0, copy[i], 0, shape[i].length);
        }
        return copy;
    }
}
