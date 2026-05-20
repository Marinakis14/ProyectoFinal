package Valdris.model.items;

import Valdris.model.enums.ItemType;
import Valdris.model.units.Player;

/**
 * Clase base de todos los objetos que puede encontrar, guardar o usar el jugador.
 *
 * <p>Un item representa cualquier elemento interactivo del inventario: armas,
 * armaduras, escudos, pociones y accesorios. Las subclases concretas definen
 * el comportamiento real al usarse, como equiparse en una ranura o consumir una
 * poción.</p>
 *
 * <p>El identificador {@code id} es importante para persistencia, generación
 * de objetos y reconstrucción desde JSON. Debe coincidir con los códigos de la
 * guía de diseño, por ejemplo {@code W1}, {@code A3}, {@code P2} o {@code AC1}.</p>
 *
 * @see Weapon
 * @see Armor
 * @see Potion
 * @see Accessory
 */
public abstract class Item implements Comparable<Item> {

    // -- Atributos ------------------------------------------------------------

    /** Identificador único del item usado por generadores y persistencia. */
    private final String id;

    /** Nombre visible del item en inventario, cofres y barra rápida. */
    private final String nombre;

    /** Categoría funcional del item dentro del sistema de inventario. */
    private final ItemType tipo;

    /** Texto descriptivo usado por la interfaz para explicar el efecto del item. */
    private final String descripcion;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un item base con sus datos comunes.
     *
     * @param id identificador único del item
     * @param nombre nombre visible del item
     * @param tipo categoría funcional del item
     * @param descripcion texto descriptivo del item
     */
    protected Item(String id, String nombre, ItemType tipo, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Aplica el efecto del item sobre el jugador.
     *
     * <p>Cada subclase implementa su propia lógica: las armas y piezas de
     * equipo se equipan, las pociones se consumen y los accesorios activan una
     * bonificación o una función narrativa.</p>
     *
     * @param player jugador que usa el item
     */
    public abstract void use(Player player);

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el identificador único del item.
     *
     * @return id usado por generación y persistencia
     */
    public String getId() {
        return id;
    }

    /**
     * Devuelve el nombre visible del item.
     *
     * @return nombre mostrado en la interfaz
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve la categoría funcional del item.
     *
     * @return tipo del item
     */
    public ItemType getTipo() {
        return tipo;
    }

    /**
     * Devuelve la descripción del item.
     *
     * @return texto descriptivo para inventario o tooltip
     */
    public String getDescripcion() {
        return descripcion;
    }

    // -- Comparación ----------------------------------------------------------

    /**
     * Compara items por su identificador para poder almacenarlos en las
     * estructuras enlazadas propias del proyecto.
     *
     * @param other item con el que se compara
     * @return resultado lexicográfico entre identificadores
     */
    @Override
    public int compareTo(Item other) {
        if (other == null) {
            return 1;
        }
        return id.compareTo(other.id);
    }

    // -- toString -------------------------------------------------------------

    /**
     * Devuelve una representación breve del item para listados de inventario.
     *
     * @return cadena con formato "[TIPO] nombre"
     */
    @Override
    public String toString() {
        return "[" + tipo + "] " + nombre;
    }
}
