package Valdris.model.map;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.model.items.Item;
import Valdris.model.units.Player;

/**
 * Clase base para elementos del mapa que contienen items.
 *
 * <p>Un contenedor no es un item del inventario: permanece en una celda de la
 * sala y el jugador interactúa con él desde una posición adyacente durante la
 * fase de recogida. Por eso pertenece al paquete de mapa y no al paquete de
 * items.</p>
 *
 * <p>La clase conserva una lista propia de items y controla que el contenido
 * solo se entregue una vez. Las subclases concretas, como {@link Chest}, pueden
 * reutilizar esta lógica sin duplicar reglas de inventario.</p>
 *
 * @see Cell
 * @see Chest
 * @see Player
 */
public abstract class Container implements Comparable<Container> {

    // -- Atributos ------------------------------------------------------------

    /** Identificador único del contenedor dentro de la sala o del mundo. */
    private final String id;

    /** Nombre visible del contenedor para interfaz y logs. */
    private final String nombre;

    /** Items que todavia no han sido recogidos del contenedor. */
    private final ListaSimplementeEnlazada<Item> items;

    /** Indica si el contenedor ya fue abierto alguna vez. */
    private boolean abierto;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un contenedor vacío y cerrado.
     *
     * @param id identificador único del contenedor
     * @param nombre nombre visible del contenedor
     */
    protected Container(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.items = new ListaSimplementeEnlazada<>();
        this.abierto = false;
    }

    // -- Metodos de logica ----------------------------------------------------

    /**
     * Añade un item al contenido del contenedor.
     *
     * <p>Los valores null se ignoran para evitar entradas inválidas en la lista
     * propia del proyecto.</p>
     *
     * @param item item que se guardará en el contenedor
     */
    public void addItem(Item item) {
        if (item != null) {
            items.addEnd(item);
        }
    }

    /**
     * Abre el contenedor y entrega todos sus items al inventario del jugador.
     *
     * <p>Si el contenedor ya estaba abierto, no vuelve a entregar contenido.
     * Esto evita duplicar loot al interactuar varias veces con el mismo cofre.
     * Si el jugador es null, la operación se ignora y el contenedor permanece
     * cerrado.</p>
     *
     * @param player jugador que recibe el contenido
     */
    public void abrir(Player player) {
        if (player == null || abierto) {
            return;
        }
        for (int i = 0; i < items.getSize(); i++) {
            player.addItem(items.get(i));
        }
        items.clear();
        abierto = true;
    }

    /**
     * Restaura el estado de apertura desde persistencia.
     *
     * <p>No entrega contenido al jugador ni modifica la lista de items. Solo
     * debe usarse al reconstruir una partida guardada.</p>
     *
     * @param abierto true si el contenedor ya estaba abierto
     */
    public void restaurarAbierto(boolean abierto) {
        this.abierto = abierto;
    }

    /**
     * Indica si el contenedor no tiene items pendientes.
     *
     * @return true si no queda contenido por recoger
     */
    public boolean isVacio() {
        return items.isEmpty();
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el identificador del contenedor.
     *
     * @return id único del contenedor
     */
    public String getId() {
        return id;
    }

    /**
     * Devuelve el nombre visible del contenedor.
     *
     * @return nombre del contenedor
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve la lista de items pendientes del contenedor.
     *
     * @return contenido actual del contenedor
     */
    public ListaSimplementeEnlazada<Item> getItems() {
        return items;
    }

    /**
     * Indica si el contenedor ya fue abierto.
     *
     * @return true si ya se abrio al menos una vez
     */
    public boolean isAbierto() {
        return abierto;
    }

    // -- Comparacion ----------------------------------------------------------

    /**
     * Compara contenedores por identificador.
     *
     * @param other contenedor con el que se compara
     * @return resultado lexicográfico entre ids
     */
    @Override
    public int compareTo(Container other) {
        if (other == null) {
            return 1;
        }
        return id.compareTo(other.id);
    }

    // -- toString -------------------------------------------------------------

    /**
     * Devuelve una representación breve del contenedor.
     *
     * @return cadena con nombre y estado de apertura
     */
    @Override
    public String toString() {
        return nombre + (abierto ? " (abierto)" : " (cerrado)");
    }
}
