package Valdris.model.map;

import Valdris.model.enums.CellType;
import Valdris.model.items.Item;
import Valdris.model.units.Unit;

/**
 * Representa una celda individual dentro de una sala del dungeon.
 *
 * <p>Una celda concentra la información mínima que necesitan movimiento,
 * combate, recogida de objetos, accesos entre salas y renderizado: su tipo, la
 * unidad que la ocupa, el item que puede haber en el suelo y el contenedor
 * interactivo opcional.</p>
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

    /** Contenedor situado en la celda, o null si no hay ninguno. */
    private Container container;

    /** Indica si una puerta oculta ya fue descubierta por el jugador. */
    private boolean descubierta;

    /** Sala destino si la celda funciona como puerta o escalera. */
    private Room salaDestino;

    /** Fila de aparición del jugador en la sala destino. */
    private int filaDestino;

    /** Columna de aparición del jugador en la sala destino. */
    private int colDestino;

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
        this.container = null;
        this.descubierta = tipo != CellType.DOOR_HIDDEN;
        this.salaDestino = null;
        this.filaDestino = 0;
        this.colDestino = 0;
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
        if (container != null) {
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
     * Coloca un contenedor interactivo en la celda.
     *
     * <p>Una celda con contenedor no es transitable. El jugador debe situarse en
     * una celda adyacente y resolverlo desde la fase de recogida.</p>
     *
     * @param container contenedor que ocupa la celda
     */
    public void setContainer(Container container) {
        this.container = container;
    }

    /**
     * Elimina el contenedor de la celda.
     */
    public void removeContainer() {
        this.container = null;
    }

    /**
     * Configura el destino de una puerta o escalera.
     *
     * <p>El destino incluye la sala y la coordenada exacta donde aparecerá el
     * jugador tras cruzar el acceso. Si la sala es null, el acceso queda sin
     * destino funcional.</p>
     *
     * @param salaDestino sala a la que conduce la celda
     * @param filaDestino fila de aparición en la sala destino
     * @param colDestino columna de aparición en la sala destino
     */
    public void setDestinoAcceso(Room salaDestino, int filaDestino, int colDestino) {
        this.salaDestino = salaDestino;
        this.filaDestino = filaDestino;
        this.colDestino = colDestino;
    }

    /**
     * Elimina el destino funcional de la puerta o escalera.
     */
    public void limpiarDestinoAcceso() {
        this.salaDestino = null;
        this.filaDestino = 0;
        this.colDestino = 0;
    }

    /**
     * Indica si la celda tiene un destino de sala configurado.
     *
     * @return true si existe sala destino
     */
    public boolean hasDestinoAcceso() {
        return salaDestino != null;
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
     * Devuelve el contenedor colocado en la celda.
     *
     * @return contenedor actual, o null si no hay ninguno
     */
    public Container getContainer() {
        return container;
    }

    /**
     * Indica si la celda ya fue descubierta.
     *
     * @return true si no está oculta para el jugador
     */
    public boolean isDescubierta() {
        return descubierta;
    }

    /**
     * Devuelve la sala destino de esta puerta o escalera.
     *
     * @return sala destino, o null si no hay acceso configurado
     */
    public Room getSalaDestino() {
        return salaDestino;
    }

    /**
     * Devuelve la fila de aparición en la sala destino.
     *
     * @return fila destino configurada
     */
    public int getFilaDestino() {
        return filaDestino;
    }

    /**
     * Devuelve la columna de aparición en la sala destino.
     *
     * @return columna destino configurada
     */
    public int getColDestino() {
        return colDestino;
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
        if (item != other.item) {
            return item == null ? -1 : 1;
        }
        if (container != other.container) {
            return container == null ? -1 : 1;
        }
        if (salaDestino != other.salaDestino) {
            return salaDestino == null ? -1 : 1;
        }
        if (filaDestino != other.filaDestino) {
            return filaDestino - other.filaDestino;
        }
        return colDestino - other.colDestino;
    }
}
