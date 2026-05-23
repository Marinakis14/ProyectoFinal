package Valdris.model.enums;

/**
 * Define los tipos de evento que pueden aparecer en el log de partida.
 *
 * <p>El log se usa para mostrar al jugador un resumen final y para alimentar
 * vistas de interfaz. Mantener los tipos como enum evita variaciones de texto
 * para la misma categoría y facilita filtrar eventos más adelante en JavaFX.</p>
 */
public enum LogEventType {

    /** Movimiento o salto de movimiento del jugador. */
    MOVEMENT,

    /** Recogida de items, cofres o salto de recogida. */
    PICKUP,

    /** Uso, consumo o equipamiento de items. */
    ITEM,

    /** Ataques, daño, muertes, drops y efectos aplicados por combate. */
    COMBAT,

    /** Acciones resueltas durante el turno enemigo. */
    ENEMY_TURN,

    /** Palancas, runas, secuencias y resolución de puzzles. */
    PUZZLE,

    /** Puertas, escaleras, pasadizos y cambios de acceso. */
    ACCESS,

    /** Entrada, exploración o diálogo de sala. */
    ROOM,

    /** Aplicación, daño o expiración de efectos de estado. */
    STATE,

    /** Eventos globales de partida, como inicio, victoria o derrota. */
    GAME
}
