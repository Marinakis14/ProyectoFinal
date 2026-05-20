package Valdris.model.enums;

/**
 * Define las fases del ciclo de turnos de una partida.
 *
 * <p>El turno del jugador sigue un orden fijo: movimiento, recogida, uso de
 * item y ataque. Después actúan los enemigos y el ciclo vuelve a comenzar en
 * movimiento si la partida no ha terminado.</p>
 *
 * <p>Cada fase limita las acciones disponibles para que el controlador pueda
 * validar entradas de la interfaz y lanzar las excepciones personalizadas
 * correspondientes cuando el jugador intenta actuar fuera de fase.</p>
 *
 * @see Valdris.logic.turn.TurnManager
 */
public enum Phase {

    /**
     * Fase de movimiento.
     * El jugador puede moverse una vez usando las celdas alcanzables por BFS.
     * También puede activar cambios de sala al pisar puertas o escaleras.
     */
    MOVEMENT,

    /**
     * Fase de recogida.
     * Permite resolver objetos o contenedores accesibles tras el movimiento,
     * respetando el límite de una acción de recogida por turno.
     */
    PICKUP,

    /**
     * Fase de uso de item.
     * Permite usar una poción o equipar un arma, armadura, escudo o accesorio.
     * Equipar un arma no cuenta como atacar.
     */
    USE_ITEM,

    /**
     * Fase de ataque.
     * El jugador puede atacar una vez a un enemigo en rango válido. Las armas
     * de alcance requieren que la lógica de combate confirme línea de visión.
     */
    ATTACK,

    /**
     * Fase de enemigos.
     * Cada enemigo vivo de la sala ejecuta su IA, se procesan efectos de fin de
     * turno y se reinician las acciones del jugador para el siguiente ciclo.
     */
    ENEMY_TURN
}
