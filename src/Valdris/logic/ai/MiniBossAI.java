package Valdris.logic.ai;

import Valdris.logic.ai.ArbolDecisionIA.AccionIA;
import Valdris.logic.combat.CombatManager;
import Valdris.logic.combat.CombatResult;
import Valdris.logic.vision.LineaDeVision;
import Valdris.model.effects.Effect;
import Valdris.model.effects.EffectProcessingResult;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.MiniBossType;
import Valdris.model.map.Room;
import Valdris.model.units.MiniBossEnemy;
import Valdris.model.units.Player;

/**
 * Ejecuta habilidades especiales de mini-bosses.
 *
 * <p>Los mini-bosses conservan la IA base de su {@code EnemyType} cuando su
 * habilidad no está cargada. Cuando el cooldown está listo, intentan colocar la
 * habilidad en ese mismo turno: si tras moverse no pueden usarla, pierden el
 * intento y reinician el cooldown.</p>
 */
public final class MiniBossAI {

    // -- Constantes -----------------------------------------------------------

    /** Cooldown de Alcalde, Espíritu Madre y Golem. */
    private static final int COOLDOWN_LARGO = 3;

    /** Cooldown del Guardián Sin Nombre. */
    private static final int COOLDOWN_GUARDIAN = 2;

    /** Daño fijo de Estocada Corrupta. */
    private static final int DANIO_ESTOCADA = 35;

    /** Daño fijo de Enredadera Paralizante. */
    private static final int DANIO_ENREDADERA = 12;

    /** Daño fijo de Pisotón Sísmico. */
    private static final int DANIO_PISOTON = 28;

    /** Daño fijo de Sentencia Arcana. */
    private static final int DANIO_SENTENCIA = 20;

    /** Radio del Pisotón Sísmico desde la celda única del Golem. */
    private static final int RADIO_PISOTON = 2;

    // -- Constructor privado -------------------------------------------------

    /**
     * Evita instanciar la clase porque todos sus métodos son estáticos.
     */
    private MiniBossAI() {
    }

    // -- Ejecución principal -------------------------------------------------

    /**
     * Ejecuta el turno de un mini-boss.
     *
     * @param boss mini-boss activo
     * @param room sala actual
     * @param player jugador objetivo
     * @param cm parámetro conservado por compatibilidad con la guía
     * @param filaOrigen fila inicial del turno
     * @param colOrigen columna inicial del turno
     * @param effects efectos procesados antes de actuar
     * @return resultado estructurado de IA
     */
    public static AIActionResult executeTurn(MiniBossEnemy boss, Room room, Player player, CombatManager cm,
                                             int filaOrigen, int colOrigen, EffectProcessingResult effects) {
        if (boss == null || room == null || player == null) {
            return null;
        }
        if (boss.getTipoMiniBoss() == MiniBossType.EL_FILTRO) {
            return conNombreNarrativo(boss,
                IAEnemigo.executeBaseAction(boss, room, player, cm, filaOrigen, colOrigen, effects));
        }
        if (!boss.isCooldownListo(getCooldown(boss))) {
            AIActionResult result = IAEnemigo.executeBaseAction(boss, room, player, cm,
                filaOrigen, colOrigen, effects);
            boss.incrementarCooldown();
            return conNombreNarrativo(boss, result);
        }

        if (puedeUsarEspecial(boss, room, player)) {
            return ejecutarEspecial(boss, room, player, filaOrigen, colOrigen, effects);
        }

        moverParaEspecial(boss, room, player);
        if (puedeUsarEspecial(boss, room, player)) {
            return ejecutarEspecial(boss, room, player, filaOrigen, colOrigen, effects);
        }

        boss.resetCooldown();
        AccionIA accion = boss.getTipoMiniBoss() == MiniBossType.ESPIRITU_MADRE
            ? AccionIA.MOVER_A_ZONA : AccionIA.MOVER;
        if (boss.getFilaActual() == filaOrigen && boss.getColActual() == colOrigen) {
            accion = AccionIA.ESPERAR;
        }
        return new AIActionResult(accion, boss.getTipo(), room.getId(),
            filaOrigen, colOrigen, boss.getFilaActual(), boss.getColActual(),
            null, effects, null, null, -1, -1, "ESPECIAL_PERDIDO_FUERA_DE_RANGO", null,
            boss.getNombreNarrativo(), getNombreEspecial(boss));
    }

    // -- Habilidades ----------------------------------------------------------

    /**
     * Ejecuta la habilidad especial ya validada.
     */
    private static AIActionResult ejecutarEspecial(MiniBossEnemy boss, Room room, Player player,
                                                   int filaOrigen, int colOrigen,
                                                   EffectProcessingResult effects) {
        String habilidad = getNombreEspecial(boss);
        CombatResult combat;
        EffectType efecto = getEfectoEspecial(boss);
        EffectType efectoAplicado = null;
        if (CombatManager.fallaAtaquePorBlind(boss, Math.random())) {
            combat = new CombatResult(0, true, false, player.getHp(), player.getHpMax(), null, null, null);
        } else {
            int danio = CombatManager.aplicarBonusCurse(player, getDanioEspecial(boss));
            player.recibirDanio(danio);
            if (efecto != null) {
                player.addEfecto(new Effect(efecto, getDuracionEfectoEspecial(efecto)));
                efectoAplicado = efecto;
            }
            combat = new CombatResult(danio, false, !player.isVivo(), player.getHp(), player.getHpMax(),
                null, null, null);
        }
        boss.resetCooldown();
        return new AIActionResult(AccionIA.HABILIDAD_ESPECIAL, boss.getTipo(), room.getId(),
            filaOrigen, colOrigen, boss.getFilaActual(), boss.getColActual(),
            combat, effects, efectoAplicado, null, -1, -1, null, null,
            boss.getNombreNarrativo(), habilidad);
    }

    /**
     * Indica si el especial puede impactar desde la posición actual.
     */
    private static boolean puedeUsarEspecial(MiniBossEnemy boss, Room room, Player player) {
        MiniBossType tipo = boss.getTipoMiniBoss();
        int distancia = distanciaManhattan(boss, player);
        if (tipo == MiniBossType.ALCALDE_CORRUPTO || tipo == MiniBossType.GUARDIAN_SIN_NOMBRE) {
            return distancia <= 1;
        }
        if (tipo == MiniBossType.ESPIRITU_MADRE) {
            return distancia <= boss.getRangoEfectivo()
                && LineaDeVision.tieneVision(room, boss.getFilaActual(), boss.getColActual(),
                    player.getFilaActual(), player.getColActual());
        }
        if (tipo == MiniBossType.GOLEM) {
            return distancia <= RADIO_PISOTON;
        }
        return false;
    }

    /**
     * Mueve al mini-boss para intentar colocar su especial.
     */
    private static void moverParaEspecial(MiniBossEnemy boss, Room room, Player player) {
        if (boss.getTipoMiniBoss() == MiniBossType.ESPIRITU_MADRE) {
            IAEnemigo.ejecutarMovimientoAZona(boss, room, player);
        } else {
            IAEnemigo.ejecutarMovimiento(boss, room, player);
        }
    }

    // -- Datos de habilidad ---------------------------------------------------

    /**
     * Devuelve el cooldown de la habilidad del mini-boss.
     */
    private static int getCooldown(MiniBossEnemy boss) {
        if (boss.getTipoMiniBoss() == MiniBossType.GUARDIAN_SIN_NOMBRE) {
            return COOLDOWN_GUARDIAN;
        }
        return COOLDOWN_LARGO;
    }

    /**
     * Devuelve el daño fijo de la habilidad.
     */
    private static int getDanioEspecial(MiniBossEnemy boss) {
        if (boss.getTipoMiniBoss() == MiniBossType.ALCALDE_CORRUPTO) {
            return DANIO_ESTOCADA;
        }
        if (boss.getTipoMiniBoss() == MiniBossType.ESPIRITU_MADRE) {
            return DANIO_ENREDADERA;
        }
        if (boss.getTipoMiniBoss() == MiniBossType.GOLEM) {
            return DANIO_PISOTON;
        }
        return DANIO_SENTENCIA;
    }

    /**
     * Devuelve el efecto aplicado por la habilidad.
     */
    private static EffectType getEfectoEspecial(MiniBossEnemy boss) {
        if (boss.getTipoMiniBoss() == MiniBossType.ESPIRITU_MADRE) {
            return EffectType.PARALYSIS;
        }
        if (boss.getTipoMiniBoss() == MiniBossType.GUARDIAN_SIN_NOMBRE) {
            return EffectType.CURSE;
        }
        return null;
    }

    /**
     * Devuelve la duración del efecto aplicado por la habilidad.
     */
    private static int getDuracionEfectoEspecial(EffectType efecto) {
        if (efecto == EffectType.CURSE) {
            return 2;
        }
        return 1;
    }

    /**
     * Devuelve el nombre visible de la habilidad.
     */
    private static String getNombreEspecial(MiniBossEnemy boss) {
        if (boss.getTipoMiniBoss() == MiniBossType.ALCALDE_CORRUPTO) {
            return "Estocada Corrupta";
        }
        if (boss.getTipoMiniBoss() == MiniBossType.ESPIRITU_MADRE) {
            return "Enredadera Paralizante";
        }
        if (boss.getTipoMiniBoss() == MiniBossType.GOLEM) {
            return "Pisotón Sísmico";
        }
        if (boss.getTipoMiniBoss() == MiniBossType.GUARDIAN_SIN_NOMBRE) {
            return "Sentencia Arcana";
        }
        return null;
    }

    // -- Utilidades -----------------------------------------------------------

    /**
     * Copia un resultado base usando el nombre narrativo del mini-boss.
     */
    private static AIActionResult conNombreNarrativo(MiniBossEnemy boss, AIActionResult result) {
        if (result == null) {
            return null;
        }
        return new AIActionResult(result.getAccion(), result.getEnemyType(), result.getSalaId(),
            result.getFilaOrigen(), result.getColOrigen(), result.getFilaDestino(), result.getColDestino(),
            result.getCombatResult(), result.getEffectProcessingResult(), result.getEfectoAplicado(),
            result.getTipoInvocado(), result.getFilaInvocado(), result.getColInvocado(), result.getMotivo(),
            result.getDropItemId(), boss.getNombreNarrativo(), result.getHabilidadEspecial());
    }

    /**
     * Calcula distancia Manhattan entre mini-boss y jugador.
     */
    private static int distanciaManhattan(MiniBossEnemy boss, Player player) {
        return Math.abs(boss.getFilaActual() - player.getFilaActual())
            + Math.abs(boss.getColActual() - player.getColActual());
    }
}
