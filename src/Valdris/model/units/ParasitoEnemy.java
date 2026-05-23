package Valdris.model.units;

import Valdris.model.enums.EffectType;
import Valdris.model.enums.EnemyType;

/**
 * Enemigo final del Núcleo Profundo.
 *
 * <p>El Parásito tiene tres fases. Cada fase cambia su ataque, movimiento,
 * vida visible y comportamiento táctico. Al perder la primera capa pasa a fase
 * 2 y recupera vida de fase; al perder la segunda capa entra en fase 3,
 * recupera vida y prepara Devorar Luz como explosión de transición.</p>
 */
public class ParasitoEnemy extends Enemy {

    // -- Constantes de fase ---------------------------------------------------

    /** Fase inicial: coraza exterior. */
    public static final int FASE_CORAZA = 1;

    /** Segunda fase: forma desgarrada. */
    public static final int FASE_DESGARRADA = 2;

    /** Tercera fase: esencia oscura. */
    public static final int FASE_ESENCIA = 3;

    /** HP máximo de fase 1. */
    public static final int HP_FASE_1 = 180;

    /** HP restaurado al entrar en fase 2. */
    public static final int HP_FASE_2 = 140;

    /** HP restaurado al entrar en fase 3. */
    public static final int HP_FASE_3 = 75;

    /** Umbral de cambio desde fase 1. */
    public static final int UMBRAL_FASE_2 = 120;

    /** Umbral de cambio desde fase 2. */
    public static final int UMBRAL_FASE_3 = 60;

    /** Rango común de ataques directos. */
    public static final int RANGO_DIRECTO = 3;

    /** Defensa común del Parásito. */
    public static final int DEFENSA_BASE = 10;

    // -- Atributos ------------------------------------------------------------

    /** Fase actual del Parásito. */
    private int phase;

    /** Cooldown acumulado para AOE de fases 2 y 3. */
    private int aoeCooldown;

    /** Indica si Devorar Luz ya fue ejecutado. */
    private boolean devorarLuzUsado;

    /** Indica si hay una transición pendiente de registrar. */
    private boolean phaseTransitionPending;

    /** Indica si Devorar Luz debe ejecutarse al registrar la fase 3. */
    private boolean devorarLuzPendiente;

    /** Indica si el próximo turno enemigo se consume por transición. */
    private boolean skipNextActionByTransition;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea el Parásito en su fase inicial.
     *
     * @param fila fila inicial
     * @param col columna inicial
     * @param idSala sala de aparición
     */
    public ParasitoEnemy(int fila, int col, String idSala) {
        super(EnemyType.PARASITO, HP_FASE_1, getAtaqueFase(FASE_CORAZA), DEFENSA_BASE,
            getMovimientoFase(FASE_CORAZA), RANGO_DIRECTO, fila, col, idSala);
        this.phase = FASE_CORAZA;
        this.aoeCooldown = 0;
        this.devorarLuzUsado = false;
        this.phaseTransitionPending = false;
        this.devorarLuzPendiente = false;
        this.skipNextActionByTransition = false;
        setMiniJefe(true);
    }

    // -- Daño y fases ---------------------------------------------------------

    /**
     * Recibe daño y comprueba si debe cambiar de fase.
     *
     * @param cantidad daño recibido
     */
    @Override
    public void recibirDanio(int cantidad) {
        if (cantidad <= 0 || !isVivo()) {
            return;
        }
        super.recibirDanio(cantidad);
        revisarTransicion();
    }

    /**
     * Revisa umbrales de transición tras perder HP.
     */
    private void revisarTransicion() {
        if (phase == FASE_CORAZA && getHp() <= UMBRAL_FASE_2) {
            phase = FASE_DESGARRADA;
            setHp(HP_FASE_2);
            aoeCooldown = 0;
            phaseTransitionPending = true;
            skipNextActionByTransition = true;
        } else if (phase == FASE_DESGARRADA && getHp() <= UMBRAL_FASE_3) {
            phase = FASE_ESENCIA;
            setHp(HP_FASE_3);
            aoeCooldown = 0;
            phaseTransitionPending = true;
            devorarLuzPendiente = !devorarLuzUsado;
            skipNextActionByTransition = true;
        }
    }

    /**
     * Consume la marca de transición pendiente.
     *
     * @return true si había transición pendiente
     */
    public boolean consumirTransicionPendiente() {
        boolean pendiente = phaseTransitionPending;
        phaseTransitionPending = false;
        return pendiente;
    }

    /**
     * Consume la marca de Devorar Luz pendiente.
     *
     * @return true si debe ejecutarse Devorar Luz
     */
    public boolean consumirDevorarLuzPendiente() {
        boolean pendiente = devorarLuzPendiente && !devorarLuzUsado;
        devorarLuzPendiente = false;
        if (pendiente) {
            devorarLuzUsado = true;
        }
        return pendiente;
    }

    /**
     * Consume la acción perdida por transición.
     *
     * @return true si el turno debe saltarse
     */
    public boolean consumirSaltoPorTransicion() {
        boolean pendiente = skipNextActionByTransition;
        skipNextActionByTransition = false;
        return pendiente;
    }

    // -- Estadísticas dinámicas ----------------------------------------------

    /**
     * Devuelve el ataque de la fase actual.
     *
     * @return ataque total del Parásito
     */
    @Override
    public int getAtaqueTotal() {
        return getAtaqueFase(phase);
    }

    /**
     * Devuelve el daño base de la fase actual.
     *
     * @return daño base actual
     */
    @Override
    public int getDanoBase() {
        return getAtaqueTotal();
    }

    /**
     * Devuelve el movimiento de la fase actual, respetando SLOW.
     *
     * @return movimiento efectivo actual
     */
    @Override
    public int getMovEfectivo() {
        int movimiento = getMovimientoFase(phase);
        if (tieneEfecto(EffectType.SLOW)) {
            return (int) Math.ceil(movimiento / 2.0);
        }
        return movimiento;
    }

    /**
     * Devuelve el rango directo del Parásito.
     *
     * @return rango efectivo
     */
    @Override
    public int getRangoEfectivo() {
        return RANGO_DIRECTO;
    }

    /**
     * Devuelve la vida máxima visible de la fase actual.
     *
     * @return HP máximo de fase
     */
    @Override
    public int getHpMax() {
        if (phase == FASE_ESENCIA) {
            return HP_FASE_3;
        }
        if (phase == FASE_DESGARRADA) {
            return HP_FASE_2;
        }
        return HP_FASE_1;
    }

    /**
     * Ajusta el HP actual respetando el máximo visible de la fase actual.
     *
     * @param hp nuevo HP
     */
    @Override
    public void setHp(int hp) {
        if (hp < 0) {
            super.setHp(0);
        } else {
            super.setHp(Math.min(hp, getHpMax()));
        }
    }

    // -- Getters y setters ----------------------------------------------------

    /**
     * Devuelve la fase actual.
     *
     * @return fase actual
     */
    public int getPhase() {
        return phase;
    }

    /**
     * Restaura la fase desde persistencia.
     *
     * @param phase fase guardada
     */
    public void setPhase(int phase) {
        if (phase < FASE_CORAZA) {
            this.phase = FASE_CORAZA;
        } else if (phase > FASE_ESENCIA) {
            this.phase = FASE_ESENCIA;
        } else {
            this.phase = phase;
        }
    }

    /**
     * Devuelve el cooldown AOE acumulado.
     *
     * @return turnos acumulados
     */
    public int getAoeCooldown() {
        return aoeCooldown;
    }

    /**
     * Ajusta el cooldown AOE.
     *
     * @param aoeCooldown nuevo valor
     */
    public void setAoeCooldown(int aoeCooldown) {
        this.aoeCooldown = Math.max(0, aoeCooldown);
    }

    /**
     * Incrementa el cooldown AOE.
     */
    public void incrementarAoeCooldown() {
        aoeCooldown++;
    }

    /**
     * Reinicia el cooldown AOE.
     */
    public void resetAoeCooldown() {
        aoeCooldown = 0;
    }

    /**
     * Indica si el AOE está listo.
     *
     * @return true si han pasado al menos dos turnos
     */
    public boolean isAoeListo() {
        return aoeCooldown >= 2;
    }

    /**
     * Indica si Devorar Luz ya se usó.
     *
     * @return true si ya fue ejecutado
     */
    public boolean isDevorarLuzUsado() {
        return devorarLuzUsado;
    }

    /**
     * Restaura el estado de Devorar Luz.
     *
     * @param devorarLuzUsado true si ya se usó
     */
    public void setDevorarLuzUsado(boolean devorarLuzUsado) {
        this.devorarLuzUsado = devorarLuzUsado;
        if (devorarLuzUsado) {
            this.devorarLuzPendiente = false;
        }
    }

    /**
     * Indica si hay transición pendiente.
     *
     * @return true si falta registrar una transición
     */
    public boolean isPhaseTransitionPending() {
        return phaseTransitionPending;
    }

    /**
     * Restaura si hay transición pendiente.
     *
     * @param phaseTransitionPending nuevo estado
     */
    public void setPhaseTransitionPending(boolean phaseTransitionPending) {
        this.phaseTransitionPending = phaseTransitionPending;
    }

    /**
     * Indica si Devorar Luz está pendiente.
     *
     * @return true si debe ejecutarse
     */
    public boolean isDevorarLuzPendiente() {
        return devorarLuzPendiente;
    }

    /**
     * Restaura si Devorar Luz está pendiente.
     *
     * @param devorarLuzPendiente nuevo estado
     */
    public void setDevorarLuzPendiente(boolean devorarLuzPendiente) {
        this.devorarLuzPendiente = devorarLuzPendiente;
    }

    /**
     * Indica si debe saltarse el próximo turno por transición.
     *
     * @return true si debe saltarse
     */
    public boolean isSkipNextActionByTransition() {
        return skipNextActionByTransition;
    }

    /**
     * Restaura el salto de turno por transición.
     *
     * @param skipNextActionByTransition nuevo estado
     */
    public void setSkipNextActionByTransition(boolean skipNextActionByTransition) {
        this.skipNextActionByTransition = skipNextActionByTransition;
    }

    // -- Datos estáticos ------------------------------------------------------

    /**
     * Devuelve el ataque asociado a una fase.
     *
     * @param phase fase consultada
     * @return ataque base
     */
    private static int getAtaqueFase(int phase) {
        if (phase == FASE_ESENCIA) {
            return 18;
        }
        if (phase == FASE_DESGARRADA) {
            return 20;
        }
        return 22;
    }

    /**
     * Devuelve el movimiento asociado a una fase.
     *
     * @param phase fase consultada
     * @return movimiento base
     */
    private static int getMovimientoFase(int phase) {
        if (phase == FASE_ESENCIA) {
            return 3;
        }
        if (phase == FASE_DESGARRADA) {
            return 2;
        }
        return 1;
    }
}
