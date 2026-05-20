package Valdris.exceptions;

/**
 * Excepción lanzada cuando una acción de ataque no es válida.
 *
 * <p>Se usa cuando el objetivo está fuera de rango, no hay enemigo seleccionado
 * o la fase actual no permite atacar. La lógica de turnos y el controlador de
 * interfaz deben capturarla para registrar el mensaje correspondiente.</p>
 */
public class InvalidAttackException extends Exception {

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea una excepción de ataque inválido con un mensaje descriptivo.
     *
     * @param message explicación del error de ataque
     */
    public InvalidAttackException(String message) {
        super(message);
    }
}
