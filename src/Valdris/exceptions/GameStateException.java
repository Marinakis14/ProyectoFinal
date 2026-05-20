package Valdris.exceptions;

/**
 * Excepción lanzada cuando el estado global de la partida es inválido.
 *
 * <p>Cubre errores como temporizadores agotados, salas inexistentes, problemas
 * de carga o guardado y cualquier situación que impida continuar la partida de
 * forma coherente.</p>
 */
public class GameStateException extends Exception {

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea una excepción de estado de partida con un mensaje descriptivo.
     *
     * @param message explicación del error de estado
     */
    public GameStateException(String message) {
        super(message);
    }
}
