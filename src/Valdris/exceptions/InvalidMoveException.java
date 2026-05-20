package Valdris.exceptions;

/**
 * Excepción lanzada cuando una acción de movimiento no es válida.
 *
 * <p>Se usa para posiciones fuera de rango, celdas no transitables o destinos
 * que no pertenecen al conjunto calculado por BFS. La interfaz debe capturarla
 * para informar al jugador sin romper el flujo de partida.</p>
 */
public class InvalidMoveException extends Exception {

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea una excepción de movimiento inválido con un mensaje descriptivo.
     *
     * @param message explicación del error de movimiento
     */
    public InvalidMoveException(String message) {
        super(message);
    }
}
