package es.rafacampanero.tetris.game;

/**
 * Define los mapas iniciales para el modo Puzles.
 * Cada nivel es un tablero de 20 filas x 10 columnas.
 * 0 = vacío, 1 = bloque inicial.
 */
public class PuzzleMaps {

    public static final int[][][] LEVELS = new int[10][20][10];

    static {
        // Nivel 0 - Fácil: un par de bloques
        LEVELS[0][19][4] = 1;
        LEVELS[0][19][5] = 1;

        // Nivel 1 - Fácil/medio
        LEVELS[1][18][3] = 1;
        LEVELS[1][18][4] = 1;
        LEVELS[1][19][4] = 1;
        LEVELS[1][19][5] = 1;

        // Nivel 2
        LEVELS[2][17][2] = 1;
        LEVELS[2][17][3] = 1;
        LEVELS[2][18][3] = 1;
        LEVELS[2][18][4] = 1;
        LEVELS[2][19][4] = 1;
        LEVELS[2][19][5] = 1;

        // Nivel 3
        for (int i = 0; i < 3; i++) {
            LEVELS[3][19][i + 3] = 1;
            LEVELS[3][18][i + 4] = 1;
        }

        // Nivel 4
        LEVELS[4][17][4] = 1;
        LEVELS[4][18][3] = 1;
        LEVELS[4][18][4] = 1;
        LEVELS[4][18][5] = 1;
        LEVELS[4][19][2] = 1;
        LEVELS[4][19][3] = 1;
        LEVELS[4][19][4] = 1;
        LEVELS[4][19][5] = 1;

        // Nivel 5
        for (int i = 0; i < 4; i++) {
            LEVELS[5][19][i + 2] = 1;
            LEVELS[5][18][i + 3] = 1;
        }

        // Nivel 6
        LEVELS[6][16][4] = 1;
        LEVELS[6][17][3] = 1;
        LEVELS[6][17][4] = 1;
        LEVELS[6][17][5] = 1;
        LEVELS[6][18][2] = 1;
        LEVELS[6][18][3] = 1;
        LEVELS[6][18][4] = 1;
        LEVELS[6][18][5] = 1;
        LEVELS[6][19][4] = 1;

        // Nivel 7
        for (int i = 0; i < 5; i++) {
            LEVELS[7][19][i + 1] = 1;
            LEVELS[7][18][i + 2] = 1;
        }

        // Nivel 8
        LEVELS[8][17][3] = 1;
        LEVELS[8][17][4] = 1;
        LEVELS[8][18][2] = 1;
        LEVELS[8][18][3] = 1;
        LEVELS[8][18][4] = 1;
        LEVELS[8][19][3] = 1;
        LEVELS[8][19][4] = 1;
        LEVELS[8][19][5] = 1;

        // Nivel 9 - Difícil
        for (int i = 0; i < 6; i++) {
            LEVELS[9][19][i + 2] = 1;
            if (i < 5)
                LEVELS[9][18][i + 3] = 1;
        }
    }
}
