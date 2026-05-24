package Valdris.ui.model;

/**
 * Observador de cambios del modelo JavaFX de la partida.
 */
public interface GameModelListener extends Comparable<GameModelListener> {

    /**
     * Reacciona cuando cambia el estado visible de la partida.
     *
     * @param modelo modelo que ha cambiado
     */
    void onEstadoCambiado(GameModel modelo);

    /**
     * Compara listeners por identidad para poder almacenarlos en la LSE propia.
     *
     * @param other listener comparado
     * @return resultado de comparacion estable por identidad
     */
    @Override
    default int compareTo(GameModelListener other) {
        if (this == other) {
            return 0;
        }
        if (other == null) {
            return 1;
        }
        return Integer.compare(System.identityHashCode(this), System.identityHashCode(other));
    }
}
