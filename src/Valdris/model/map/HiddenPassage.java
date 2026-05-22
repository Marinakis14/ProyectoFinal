package Valdris.model.map;

/**
 * Representa una conexión oculta entre dos salas del dungeon.
 *
 * <p>Un pasadizo oculto no existe como arista real del grafo hasta que un
 * trigger, puzzle o evento lo activa. Esto permite que los caminos mínimos y la
 * navegación entre salas ignoren rutas secretas mientras no han sido
 * descubiertas.</p>
 *
 * @see Dungeon
 * @see Room
 */
public class HiddenPassage implements Comparable<HiddenPassage> {

    // -- Atributos ------------------------------------------------------------

    /** Identificador único del pasadizo oculto. */
    private final String id;

    /** Sala origen del pasadizo. */
    private final Room origen;

    /** Sala destino del pasadizo. */
    private final Room destino;

    /** Descripción usada al crear la arista del grafo. */
    private final String descripcion;

    /** Indica si al activarse crea conexión en ambos sentidos. */
    private final boolean bidireccional;

    /** Indica si el pasadizo ya fue activado. */
    private boolean active;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un pasadizo oculto pendiente de activación.
     *
     * @param id identificador único del pasadizo
     * @param origen sala origen
     * @param destino sala destino
     * @param descripcion descripción de la conexión
     * @param bidireccional true si debe permitir ida y vuelta
     */
    public HiddenPassage(String id, Room origen, Room destino, String descripcion, boolean bidireccional) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.descripcion = descripcion;
        this.bidireccional = bidireccional;
        this.active = false;
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Marca el pasadizo como activado.
     */
    public void activar() {
        this.active = true;
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el identificador del pasadizo.
     *
     * @return id único
     */
    public String getId() {
        return id;
    }

    /**
     * Devuelve la sala origen.
     *
     * @return sala origen
     */
    public Room getOrigen() {
        return origen;
    }

    /**
     * Devuelve la sala destino.
     *
     * @return sala destino
     */
    public Room getDestino() {
        return destino;
    }

    /**
     * Devuelve la descripción de la conexión.
     *
     * @return texto de arista
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Indica si el pasadizo debe ser bidireccional.
     *
     * @return true si crea dos aristas al activarse
     */
    public boolean isBidireccional() {
        return bidireccional;
    }

    /**
     * Indica si el pasadizo ya está activo.
     *
     * @return true si ya se añadió al grafo
     */
    public boolean isActive() {
        return active;
    }

    // -- Comparación ----------------------------------------------------------

    /**
     * Compara pasadizos por id.
     *
     * @param other pasadizo comparado
     * @return resultado lexicográfico por id
     */
    @Override
    public int compareTo(HiddenPassage other) {
        if (other == null) {
            return 1;
        }
        if (id == null && other.id == null) {
            return 0;
        }
        if (id == null) {
            return -1;
        }
        if (other.id == null) {
            return 1;
        }
        return id.compareTo(other.id);
    }
}
