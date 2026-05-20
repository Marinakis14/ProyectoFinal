package Valdris.model.enums;

/**
 * Define todos los tipos posibles de celda en una sala del dungeon.
 *
 * <p>El tipo de celda determina si es transitable, si activa algun mecanismo
 * al pisarla y como se renderiza en la interfaz grafica.</p>
 *
 * @see Valdris.model.map.Cell
 */
public enum CellType {

    /** Suelo normal. Transitable sin restricciones. */
    FLOOR,

    /** Pared. No transitable nunca. Bloquea la linea de vision. */
    WALL,

    /** Puerta abierta. Transitable. Conecta con otra sala o pasillo. */
    DOOR,

    /**
     * Puerta oculta. Se renderiza como WALL hasta que el jugador la descubre.
     * Una vez revelada se comporta como DOOR.
     */
    DOOR_HIDDEN,

    /**
     * Puerta cerrada con llave. No transitable hasta que el jugador
     * tenga el accesorio de llave correspondiente equipado.
     */
    DOOR_LOCKED,

    /**
     * Escaleras. Transitable. Cambia de piso en las Minas de Karath (Zona 3).
     * Activa {@code TurnManager.changeRoom()} al pisarlas.
     */
    STAIRS,

    /**
     * Runa del suelo. Transitable. Activa un mecanismo al pisarla
     * (usado en el acertijo de runas de S4-C).
     */
    RUNE,

    /**
     * Palanca. Transitable. Se activa con una accion de uso explicita
     * del jugador, no automaticamente al pisarla.
     */
    LEVER,

    /**
     * Trampa oculta. Se renderiza como FLOOR hasta que el jugador la pisa
     * o tiene equipada la Semilla Resonante (AC2) en radio 2.
     * Al pisarla aplica un efecto de estado al jugador.
     */
    TRAP
}