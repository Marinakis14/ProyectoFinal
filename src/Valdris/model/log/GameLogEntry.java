package Valdris.model.log;

import Valdris.model.enums.LogEventType;

/**
 * Entrada estructurada del log acumulativo de una partida.
 *
 * <p>Cada evento conserva los datos mínimos para mostrarlo como texto, filtrarlo
 * en una interfaz y exportarlo a JSON sin tener que interpretar cadenas libres.
 * La representación textual se concentra en {@link #toString()} para mantener
 * un formato uniforme en toda la aplicación.</p>
 */
public class GameLogEntry implements Comparable<GameLogEntry> {

    // -- Atributos ------------------------------------------------------------

    /** Turno global en el que ocurre el evento. */
    private final int turno;

    /** Tipo funcional del evento. */
    private final LogEventType tipo;

    /** Actor principal del evento, por ejemplo KAEL o WARRIOR. */
    private final String actor;

    /** Sala donde ocurre el evento, o null si no aplica. */
    private final String salaId;

    /** Mensaje visible para el jugador. */
    private final String mensaje;

    /** Detalle técnico opcional para JSON o depuración. */
    private final String detalle;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea una entrada de log estructurada.
     *
     * @param turno turno global del evento
     * @param tipo tipo de evento
     * @param actor actor principal
     * @param salaId sala donde ocurre
     * @param mensaje mensaje visible
     * @param detalle detalle opcional
     */
    public GameLogEntry(int turno, LogEventType tipo, String actor, String salaId,
                        String mensaje, String detalle) {
        this.turno = Math.max(0, turno);
        this.tipo = tipo == null ? LogEventType.SYSTEM : tipo;
        this.actor = actor;
        this.salaId = salaId;
        this.mensaje = mensaje == null ? "" : mensaje;
        this.detalle = detalle;
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el turno del evento.
     *
     * @return turno global
     */
    public int getTurno() {
        return turno;
    }

    /**
     * Devuelve el tipo de evento.
     *
     * @return tipo de log
     */
    public LogEventType getTipo() {
        return tipo;
    }

    /**
     * Devuelve el actor principal.
     *
     * @return actor, o null si no aplica
     */
    public String getActor() {
        return actor;
    }

    /**
     * Devuelve la sala asociada.
     *
     * @return id de sala, o null si no aplica
     */
    public String getSalaId() {
        return salaId;
    }

    /**
     * Devuelve el mensaje visible.
     *
     * @return mensaje del evento
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Devuelve el detalle opcional.
     *
     * @return detalle, o null si no aplica
     */
    public String getDetalle() {
        return detalle;
    }

    // -- Comparación ----------------------------------------------------------

    /**
     * Compara entradas por turno, tipo y mensaje para compatibilidad con LSE.
     *
     * @param other entrada comparada
     * @return resultado de comparación
     */
    @Override
    public int compareTo(GameLogEntry other) {
        if (other == null) {
            return 1;
        }
        int resultado = turno - other.turno;
        if (resultado != 0) {
            return resultado;
        }
        resultado = tipo.name().compareTo(other.tipo.name());
        if (resultado != 0) {
            return resultado;
        }
        return mensaje.compareTo(other.mensaje);
    }

    // -- Texto ---------------------------------------------------------------

    /**
     * Devuelve el formato visible de una entrada de log.
     *
     * @return texto de log para UI o resumen
     */
    @Override
    public String toString() {
        String texto = "Turno " + turno + " | " + tipo.name() + " | ";
        if (actor != null && !actor.isEmpty()) {
            texto += actor + ": ";
        }
        return texto + mensaje;
    }
}
