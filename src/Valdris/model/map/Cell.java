package Valdris.model.map;

import Valdris.model.enums.CellType;
import Valdris.model.items.Item;
import Valdris.model.units.Unit;

/**
 * Representa una celda individual dentro de una sala del dungeon.
 *
 * <p>Una celda concentra la información mínima que necesitan movimiento,
 * combate, recogida de objetos y renderizado: su tipo, la unidad que la ocupa
 * y el item que puede haber en el suelo.</p>
 *
 * <p>La transitabilidad se decide con {@link #isWalkable()}. Esa lógica es
 * crítica para BFS, IA enemiga y validación de movimiento del jugador. Las
 * puertas ocultas empiezan sin descubrir, pero cuando se revelan se comportan
 * como una puerta normal.</p>
 *
 * @see CellType
 * @see Room
 */
public class Cell implements Comparable<Cell> {

    // -- Atributos ------------------------------------------------------------

    /** Tipo funcional de la celda dentro de la sala. */
    private CellType tipo;

    /** Unidad que ocupa la celda, o null si está vacía. */
    private Unit unit;

    /** Item colocado en la celda, o null si no hay ninguno. */
    private Item item;

    /** Indica si una puerta oculta ya fue descubierta por el jugador. */
    private boolean descubierta;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea una celda con el tipo indicado.
     *
     * <p>Las celdas normales empiezan descubiertas. Las puertas ocultas se
     * crean sin descubrir para que la interfaz pueda mostrarlas como pared hasta
     * que el jugador revele su existencia.</p>
     *
     * @param tipo tipo inicial de la celda
     */
    public Cell(CellType tipo) {
        this.tipo = tipo;
        this.unit = null;
        this.item = null;
        this.descubierta = tipo != CellType.DOOR_HIDDEN;
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Indica si una unidad puede entrar en esta celda.
     *
     * <p>Una celda no es transitable si es pared, puerta cerrada o si ya hay una
     * unidad ocupándola. Las puertas ocultas solo son transitables cuando han
     * sido reveladas.</p>
     *
     * @return true si la celda puede usarse como destino de movimiento
     */
    public boolean isWalkable() {
        if (unit != null) {
            return false;
        }
        if (tipo == CellType.WALL || tipo == CellType.DOOR_LOCKED) {
            return false;
        }
        if (tipo == CellType.DOOR_HIDDEN && !descubierta) {
            return false;
        }
        return true;
    }

    /**
     * Coloca una unidad en la celda.
     *
     * @param unit unidad que ocupa la celda
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * Elimina la unidad que ocupa la celda.
     */
    public void removeUnit() {
        this.unit = null;
    }

    /**
     * Coloca un item en la celda.
     *
     * @param item item situado en el suelo
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Retira y devuelve el item que había en la celda.
     *
     * @return item retirado, o null si no había ninguno
     */
    public Item removeItem() {
        Item itemRetirado = item;
        item = null;
        return itemRetirado;
    }

    /**
     * Revela una puerta oculta.
     *
     * <p>Al revelarse, la celda queda descubierta y su tipo pasa a ser
     * {@link CellType#DOOR}, de modo que movimiento, visión y renderizado la
     * tratan como una puerta abierta normal.</p>
     */
    public void revelar() {
        if (tipo == CellType.DOOR_HIDDEN) {
            descubierta = true;
            tipo = CellType.DOOR;
        }
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el tipo actual de la celda.
     *
     * @return tipo funcional de la celda
     */
    public CellType getTipo() {
        return tipo;
    }

    /**
     * Actualiza el tipo de la celda.
     *
     * @param tipo nuevo tipo de celda
     */
    public void setTipo(CellType tipo) {
        this.tipo = tipo;
        if (tipo == CellType.DOOR_HIDDEN) {
            this.descubierta = false;
        } else {
            this.descubierta = true;
        }
    }

    /**
     * Devuelve la unidad que ocupa la celda.
     *
     * @return unidad actual, o null si la celda está vacía
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Devuelve el item colocado en la celda.
     *
     * @return item actual, o null si no hay ninguno
     */
    public Item getItem() {
        return item;
    }

    /**
     * Indica si la celda ya fue descubierta.
     *
     * @return true si no está oculta para el jugador
     */
    public boolean isDescubierta() {
        return descubierta;
    }

    // -- Comparación ----------------------------------------------------------

    /**
     * Compara celdas por tipo, estado descubierto y contenido.
     *
     * <p>La comparación solo sirve para compatibilidad con las estructuras
     * enlazadas propias. No representa una ordenación espacial del mapa.</p>
     *
     * @param other celda con la que se compara
     * @return resultado de comparación estable entre celdas
     */
    @Override
    public int compareTo(Cell other) {
        if (other == null) {
            return 1;
        }
        int resultado = tipo.compareTo(other.tipo);
        if (resultado != 0) {
            return resultado;
        }
        if (descubierta != other.descubierta) {
            return descubierta ? 1 : -1;
        }
        if (unit != other.unit) {
            return unit == null ? -1 : 1;
        }
        if (item == other.item) {
            return 0;
        }
        return item == null ? -1 : 1;
    }
}
