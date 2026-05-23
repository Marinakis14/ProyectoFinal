package Valdris.logic.vision;

import Valdris.exceptions.InvalidMoveException;
import Valdris.model.map.Room;

/**
 * Calcula si dos posiciones de una sala tienen linea de vision directa.
 *
 * <p>La linea de vision se usa para ataques a distancia y habilidades de
 * enemigos como Archer, Sniper, Destructor y Controller. La implementacion usa
 * Bresenham para recorrer las celdas intermedias entre origen y destino sin
 * comprobar las celdas extremas.</p>
 *
 * <p>Por decisión de diseño actual, las paredes, las escaleras ascendentes y
 * las unidades bloquean visión cuando aparecen como celdas intermedias. Las
 * escaleras descendentes, trampas, runas, palancas y puertas no se tratan como
 * obstáculos visuales dentro de una sala.</p>
 */
public final class LineaDeVision {

    // -- Constructor privado -------------------------------------------------

    /**
     * Evita instanciar la clase porque todos sus metodos son estaticos.
     */
    private LineaDeVision() {
    }

    // -- Metodos de logica ----------------------------------------------------

    /**
     * Indica si hay vision directa entre dos celdas de una sala.
     *
     * <p>Las celdas de origen y destino no se comprueban, porque pueden estar
     * ocupadas por las unidades implicadas en el ataque. Las unidades solo
     * bloquean si están en una celda intermedia. Si alguna posición está fuera
     * de rango, devuelve false.</p>
     *
     * @param room sala consultada
     * @param f1 fila de origen
     * @param c1 columna de origen
     * @param f2 fila de destino
     * @param c2 columna de destino
     * @return true si ninguna celda intermedia bloquea la visión
     */
    public static boolean tieneVision(Room room, int f1, int c1, int f2, int c2) {
        if (room == null || !room.isEnRango(f1, c1) || !room.isEnRango(f2, c2)) {
            return false;
        }
        if (f1 == f2 && c1 == c2) {
            return true;
        }

        int dx = Math.abs(c2 - c1);
        int dy = Math.abs(f2 - f1);
        int sx = c1 < c2 ? 1 : -1;
        int sy = f1 < f2 ? 1 : -1;
        int err = dx - dy;
        int fila = f1;
        int col = c1;

        while (!(fila == f2 && col == c2)) {
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                col += sx;
            }
            if (e2 < dx) {
                err += dx;
                fila += sy;
            }

            if (fila == f2 && col == c2) {
                break;
            }
            if (bloqueaVision(room, fila, col)) {
                return false;
            }
        }
        return true;
    }

    // -- Metodos auxiliares ---------------------------------------------------

    /**
     * Comprueba si una celda intermedia bloquea la visión.
     *
     * @param room sala consultada
     * @param fila fila intermedia
     * @param col columna intermedia
     * @return true si la celda bloquea visión o no puede consultarse
     */
    private static boolean bloqueaVision(Room room, int fila, int col) {
        try {
            return room.getCell(fila, col).bloqueaVision();
        } catch (InvalidMoveException e) {
            return true;
        }
    }
}
