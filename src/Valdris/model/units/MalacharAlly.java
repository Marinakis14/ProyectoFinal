package Valdris.model.units;

import Valdris.model.effects.Effect;
import Valdris.model.enums.EffectType;

/**
 * Aliado NPC que acompaña al jugador durante el combate final.
 *
 * <p>Malachar no se almacena como enemigo de la sala porque no debe poder ser
 * atacado por el jugador ni debe seleccionar al jugador como objetivo. Sigue
 * siendo una unidad para ocupar celda, bloquear movimiento, bloquear línea de
 * visión y recibir daño de ataques en área del Parásito.</p>
 *
 * <p>Cuando su HP llega a cero no muere. Entra en recuperación durante dos
 * turnos aliados completos, se representa con PARALYSIS y después vuelve con
 * parte de su vida.</p>
 */
public class MalacharAlly extends Unit implements Comparable<MalacharAlly> {

    // -- Constantes -----------------------------------------------------------

    /** HP máximo de Malachar durante el combate final. */
    public static final int HP_MAX = 35;

    /** Ataque base aliado. */
    public static final int ATAQUE_BASE = 9;

    /** Defensa base aliada. */
    public static final int DEFENSA_BASE = 4;

    /** Movimiento base aliado. */
    public static final int MOV_BASE = 1;

    /** Rango base aliado. */
    public static final int RANGO_BASE = 2;

    /** Turnos completos que permanece sin actuar tras caer. */
    public static final int TURNOS_RECUPERACION = 2;

    /** HP con el que vuelve tras recuperarse. */
    public static final int HP_RECUPERADO = 18;

    // -- Atributos ------------------------------------------------------------

    /** Turnos de recuperación pendientes. */
    private int turnosRecuperacion;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea a Malachar en una posición de sala.
     *
     * @param fila fila inicial
     * @param col columna inicial
     */
    public MalacharAlly(int fila, int col) {
        super(HP_MAX, ATAQUE_BASE, DEFENSA_BASE, MOV_BASE, RANGO_BASE, fila, col);
        this.turnosRecuperacion = 0;
    }

    // -- Daño y recuperación --------------------------------------------------

    /**
     * Recibe daño y entra en recuperación en lugar de morir.
     *
     * @param cantidad daño recibido
     */
    @Override
    public void recibirDanio(int cantidad) {
        if (cantidad <= 0 || estaEnRecuperacion()) {
            return;
        }
        super.recibirDanio(cantidad);
        if (getHp() <= 0) {
            setHp(1);
            turnosRecuperacion = TURNOS_RECUPERACION;
            addEfecto(new Effect(EffectType.PARALYSIS, TURNOS_RECUPERACION));
        }
    }

    /**
     * Procesa un turno completo de recuperación.
     *
     * <p>Devuelve true si Malachar debe perder su acción este turno. En el
     * segundo turno de recuperación se cura, pero tampoco actúa hasta el turno
     * aliado siguiente.</p>
     *
     * @return true si no puede actuar por recuperación
     */
    public boolean procesarRecuperacionTurno() {
        if (!estaEnRecuperacion()) {
            return false;
        }
        turnosRecuperacion--;
        if (turnosRecuperacion <= 0) {
            turnosRecuperacion = 0;
            setHp(HP_RECUPERADO);
            removeEfecto(EffectType.PARALYSIS);
        }
        return true;
    }

    /**
     * Indica si Malachar está recuperándose.
     *
     * @return true si tiene turnos de recuperación pendientes
     */
    public boolean estaEnRecuperacion() {
        return turnosRecuperacion > 0;
    }

    // -- Getters y setters ----------------------------------------------------

    /**
     * Devuelve los turnos de recuperación pendientes.
     *
     * @return turnos pendientes
     */
    public int getTurnosRecuperacion() {
        return turnosRecuperacion;
    }

    /**
     * Restaura los turnos de recuperación desde persistencia.
     *
     * @param turnosRecuperacion turnos guardados
     */
    public void setTurnosRecuperacion(int turnosRecuperacion) {
        if (turnosRecuperacion <= 0) {
            this.turnosRecuperacion = 0;
            removeEfecto(EffectType.PARALYSIS);
        } else {
            this.turnosRecuperacion = turnosRecuperacion;
            addEfecto(new Effect(EffectType.PARALYSIS, turnosRecuperacion));
        }
    }

    // -- Comparación ----------------------------------------------------------

    /**
     * Compara aliados por posición.
     *
     * @param other aliado comparado
     * @return resultado de comparar fila y columna
     */
    @Override
    public int compareTo(MalacharAlly other) {
        if (other == null) {
            return 1;
        }
        if (getFilaActual() != other.getFilaActual()) {
            return getFilaActual() - other.getFilaActual();
        }
        return getColActual() - other.getColActual();
    }
}
