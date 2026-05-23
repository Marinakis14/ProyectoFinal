package Valdris.model.enums;

/**
 * Define el estado final de una partida.
 *
 * <p>Se mantiene separado de {@link Phase} porque las fases representan el
 * ciclo táctico de un turno, mientras que el resultado indica si la partida
 * sigue activa o ya terminó con victoria o derrota.</p>
 */
public enum GameResult {

    /** La partida sigue en curso. */
    IN_PROGRESS,

    /** El jugador alcanzó el desenlace final de victoria. */
    VICTORY,

    /** El jugador fue derrotado antes de completar la partida. */
    DEFEAT
}
