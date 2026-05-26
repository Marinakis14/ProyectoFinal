package Valdris.model.map;

import Valdris.model.enums.CellType;
import Valdris.model.items.Item;
import Valdris.model.units.Unit;

/**
 * Representa una celda individual dentro de una sala del dungeon.
 *
 * <p>Una celda concentra la información mínima que necesitan movimiento,
 * combate, recogida de objetos, accesos entre salas y renderizado: su tipo, la
 * unidad que la ocupa, el item que puede haber en el suelo, el contenedor
 * interactivo opcional y los datos de acceso o trigger asociados.</p>
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

    /** Indica si el acceso requiere usarlo desde una dirección concreta. */
    private boolean accessFacingConfigurado;

    /** Diferencia de fila desde el acceso hasta la celda válida de uso. */
    private int accessFacingDeltaFila;

    /** Diferencia de columna desde el acceso hasta la celda válida de uso. */
    private int accessFacingDeltaCol;

    /** Item narrativo requerido para desbloquear o usar este acceso. */
    private String requiredItemId;

    /** Identificador lógico para triggers de secretos, runas o palancas. */
    private String triggerId;

    /** Marca visual para resaltar celdas alcanzables en la interfaz. */
    private boolean highlighted;

    /** Indica que la celda está reservada como llegada de acceso. */
    private boolean reservedForAccess;

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
        this.accessFacingConfigurado = false;
        this.accessFacingDeltaFila = 0;
        this.accessFacingDeltaCol = 0;
        this.requiredItemId = null;
        this.triggerId = null;
        this.highlighted = false;
        this.reservedForAccess = false;
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Indica si una unidad puede entrar en esta celda.
     *
     * <p>Una celda no es transitable si es pared, acceso de sala, escalera,
     * palanca, contenedor o si ya hay una unidad ocupándola. Las puertas,
     * escaleras y palancas se usan desde una celda adyacente durante la fase de
     * interacción, no pisando directamente la celda.</p>
     *
     * @return true si la celda puede usarse como destino de movimiento
     */
    public boolean isWalkable() {
        if (unit != null) {
            return false;
        }
        if (tipo == CellType.WALL || isAccessCell() || tipo == CellType.LEVER) {
            return false;
        }
        if (container != null) {
            return false;
        }
        return true;
    }

    /**
     * Indica si la celda representa cualquier tipo de puerta o escalera.
     *
     * @return true si es un acceso entre salas
     */
    public boolean isAccessCell() {
        return isDoor() || isStairs();
    }

    /**
     * Indica si la celda es una puerta abierta, cerrada u oculta.
     *
     * @return true si el tipo actual es de puerta
     */
    public boolean isDoor() {
        return tipo == CellType.DOOR || tipo == CellType.DOOR_LOCKED || tipo == CellType.DOOR_HIDDEN;
    }

    /**
     * Indica si la celda es una escalera ascendente o descendente.
     *
     * @return true si la celda es una escalera
     */
    public boolean isStairs() {
        return tipo == CellType.STAIRS_UP || tipo == CellType.STAIRS_DOWN;
    }

    /**
     * Indica si el acceso puede usarse desde una celda adyacente.
     *
     * <p>Las puertas ocultas no son interactuables hasta ser reveladas. Las
     * puertas cerradas sí son interactuables para que TurnManager pueda intentar
     * desbloquearlas con el item narrativo requerido.</p>
     *
     * @return true si puede resolverse como acceso
     */
    public boolean isInteractuableAccess() {
        if (tipo == CellType.DOOR_HIDDEN && !descubierta) {
            return false;
        }
        return tipo == CellType.DOOR || tipo == CellType.DOOR_LOCKED || isStairs();
    }

    /**
     * Indica si la celda bloquea línea de visión y ataques a distancia.
     *
     * <p>Las unidades también bloquean visión cuando ocupan una celda
     * intermedia. La línea de visión no consulta origen ni destino, así que el
     * atacante y el objetivo no se bloquean a sí mismos.</p>
     *
     * @return true si la celda corta la visión como obstáculo intermedio
     */
    public boolean bloqueaVision() {
        return unit != null || tipo == CellType.WALL || tipo == CellType.STAIRS_UP;
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
     * Configura desde qué lado puede usarse un acceso con orientación.
     *
     * <p>Se usa especialmente para escaleras colocadas dentro de una sala. Solo
     * se aceptan direcciones ortogonales inmediatas. Cualquier otro valor limpia
     * la orientación y deja el acceso sin frente configurado.</p>
     *
     * @param deltaFila diferencia de fila desde el acceso hasta la celda frontal
     * @param deltaCol diferencia de columna desde el acceso hasta la celda frontal
     */
    public void setAccessFacing(int deltaFila, int deltaCol) {
        if (Math.abs(deltaFila) + Math.abs(deltaCol) != 1) {
            this.accessFacingConfigurado = false;
            this.accessFacingDeltaFila = 0;
            this.accessFacingDeltaCol = 0;
            return;
        }
        this.accessFacingConfigurado = true;
        this.accessFacingDeltaFila = deltaFila;
        this.accessFacingDeltaCol = deltaCol;
    }

    /**
     * Indica si el jugador está en la celda correcta para usar el acceso.
     *
     * <p>Las puertas pueden usarse desde cualquier celda ortogonal adyacente,
     * porque se colocan en paredes y solo existe una celda frontal real. Las
     * escaleras exigen orientación configurada para evitar accesos laterales.</p>
     *
     * @param filaJugador fila del jugador
     * @param colJugador columna del jugador
     * @param filaAcceso fila del acceso
     * @param colAcceso columna del acceso
     * @return true si la posición del jugador permite usar el acceso
     */
    public boolean isUsableFrom(int filaJugador, int colJugador, int filaAcceso, int colAcceso) {
        if (!isInteractuableAccess()) {
            return false;
        }
        int deltaFila = filaJugador - filaAcceso;
        int deltaCol = colJugador - colAcceso;
        if (Math.abs(deltaFila) + Math.abs(deltaCol) != 1) {
            return false;
        }
        if (isStairs()) {
            return accessFacingConfigurado && deltaFila == accessFacingDeltaFila
                && deltaCol == accessFacingDeltaCol;
        }
        return true;
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

    /**
     * Indica si el acceso tiene una orientación configurada.
     *
     * @return true si existe una celda frontal específica
     */
    public boolean hasAccessFacing() {
        return accessFacingConfigurado;
    }

    /**
     * Devuelve la diferencia de fila de la celda frontal del acceso.
     *
     * @return delta de fila configurado
     */
    public int getAccessFacingDeltaFila() {
        return accessFacingDeltaFila;
    }

    /**
     * Devuelve la diferencia de columna de la celda frontal del acceso.
     *
     * @return delta de columna configurado
     */
    public int getAccessFacingDeltaCol() {
        return accessFacingDeltaCol;
    }

    /**
     * Configura el item narrativo requerido para este acceso.
     *
     * @param requiredItemId id del item requerido, o null si no exige ninguno
     */
    public void setRequiredItemId(String requiredItemId) {
        this.requiredItemId = requiredItemId;
    }

    /**
     * Devuelve el item narrativo requerido para este acceso.
     *
     * @return id requerido, o null si no hay requisito
     */
    public String getRequiredItemId() {
        return requiredItemId;
    }

    /**
     * Indica si el acceso exige un item narrativo.
     *
     * @return true si hay id requerido configurado
     */
    public boolean hasRequiredItem() {
        return requiredItemId != null && !requiredItemId.isEmpty();
    }

    /**
     * Configura el identificador de trigger de la celda.
     *
     * @param triggerId identificador lógico del trigger
     */
    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    /**
     * Devuelve el identificador de trigger de la celda.
     *
     * @return id de trigger, o null si no hay
     */
    public String getTriggerId() {
        return triggerId;
    }

    /**
     * Indica si la celda tiene trigger asociado.
     *
     * @return true si hay trigger configurado
     */
    public boolean hasTrigger() {
        return triggerId != null && !triggerId.isEmpty();
    }

    /**
     * Indica si la celda está resaltada para la interfaz.
     *
     * @return true si está resaltada
     */
    public boolean isHighlighted() {
        return highlighted;
    }

    /**
     * Configura el resaltado visual de la celda.
     *
     * @param highlighted nuevo estado de resaltado
     */
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    /**
     * Limpia el resaltado visual de la celda.
     */
    public void clearHighlight() {
        this.highlighted = false;
    }

    /**
     * Indica si la celda está reservada como llegada de puerta o escalera.
     *
     * @return true si el generador debe mantenerla libre
     */
    public boolean isReservedForAccess() {
        return reservedForAccess;
    }

    /**
     * Configura si la celda queda reservada para llegada de acceso.
     *
     * @param reservedForAccess nuevo estado de reserva
     */
    public void setReservedForAccess(boolean reservedForAccess) {
        this.reservedForAccess = reservedForAccess;
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
        if (colDestino != other.colDestino) {
            return colDestino - other.colDestino;
        }
        if (accessFacingConfigurado != other.accessFacingConfigurado) {
            return accessFacingConfigurado ? 1 : -1;
        }
        if (accessFacingDeltaFila != other.accessFacingDeltaFila) {
            return accessFacingDeltaFila - other.accessFacingDeltaFila;
        }
        if (accessFacingDeltaCol != other.accessFacingDeltaCol) {
            return accessFacingDeltaCol - other.accessFacingDeltaCol;
        }
        resultado = compararNullable(requiredItemId, other.requiredItemId);
        if (resultado != 0) {
            return resultado;
        }
        resultado = compararNullable(triggerId, other.triggerId);
        if (resultado != 0) {
            return resultado;
        }
        if (highlighted != other.highlighted) {
            return highlighted ? 1 : -1;
        }
        if (reservedForAccess != other.reservedForAccess) {
            return reservedForAccess ? 1 : -1;
        }
        return 0;
    }

    /**
     * Compara dos textos admitiendo null.
     *
     * @param a primer texto
     * @param b segundo texto
     * @return resultado de comparación
     */
    private int compararNullable(String a, String b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        return a.compareTo(b);
    }
}
