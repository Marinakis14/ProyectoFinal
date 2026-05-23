package Valdris.persistence;

/**
 * Resumen exportable de una partida.
 *
 * <p>No se usa para continuar una partida, sino para mostrar o guardar el
 * resultado final con el log acumulativo y datos fiables ya disponibles en el
 * modelo.</p>
 */
public class GameSummary {

    /** Personaje elegido. */
    public String tipoPersonaje;

    /** Sala actual o sala final. */
    public String idRoomActual;

    /** HP del jugador al exportar. */
    public int hpJugador;

    /** Turno global alcanzado. */
    public int turnoGlobal;

    /** IDs de inventario normal. */
    public String[] itemsInventario;

    /** IDs de objetos narrativos. */
    public String[] itemsNarrativos;

    /** IDs de salas exploradas. */
    public String[] salasExploradas;

    /** Log estructurado completo de operaciones registradas. */
    public GameState.GameLogEntryDTO[] logEventos;
}
