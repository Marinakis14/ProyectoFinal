package Valdris.model.map;

/**
 * Cofre del mapa que contiene uno o varios items.
 *
 * <p>Los cofres se colocan en celdas no transitables y se resuelven desde la
 * fase de recogida cuando el jugador está en una celda adyacente. El contenido
 * concreto lo configurarán los generadores de mundo e items.</p>
 *
 * @see Container
 */
public class Chest extends Container {

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un cofre cerrado y sin contenido inicial.
     *
     * @param id identificador único del cofre
     * @param nombre nombre visible del cofre
     */
    public Chest(String id, String nombre) {
        super(id, nombre);
    }
}
