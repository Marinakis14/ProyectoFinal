package Valdris.logic.ai;

import Valdris.logic.ai.ArbolDecisionIA.AccionIA;
import Valdris.logic.combat.CombatResult;
import Valdris.model.effects.EffectProcessingResult;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.EnemyType;

/**
 * Resultado estructurado de una acción de IA enemiga.
 *
 * <p>La IA modifica el estado de la sala y de las unidades como antes, pero
 * devuelve este resumen para que TurnManager pueda registrar eventos concretos
 * de movimiento, ataque, invocación, efectos o espera.</p>
 */
public class AIActionResult implements Comparable<AIActionResult> {

    // -- Atributos ------------------------------------------------------------

    /** Acción decidida o ejecutada por la IA. */
    private final AccionIA accion;

    /** Tipo del enemigo que actuó. */
    private final EnemyType enemyType;

    /** Nombre visible del actor para log, o null si se usa el tipo. */
    private final String nombreActor;

    /** ID de la sala donde actuó. */
    private final String salaId;

    /** Fila origen antes de actuar. */
    private final int filaOrigen;

    /** Columna origen antes de actuar. */
    private final int colOrigen;

    /** Fila destino después de actuar. */
    private final int filaDestino;

    /** Columna destino después de actuar. */
    private final int colDestino;

    /** Resultado de combate si la acción atacó. */
    private final CombatResult combatResult;

    /** Resultado de efectos procesados antes de actuar. */
    private final EffectProcessingResult effectProcessingResult;

    /** Efecto aplicado directamente por la IA, por ejemplo Controller. */
    private final EffectType efectoAplicado;

    /** Tipo de enemigo invocado, o null si no hubo invocación. */
    private final EnemyType tipoInvocado;

    /** Fila donde apareció el invocado. */
    private final int filaInvocado;

    /** Columna donde apareció el invocado. */
    private final int colInvocado;

    /** Motivo de espera o no actuación, si existe. */
    private final String motivo;

    /** ID del drop generado por muerte de efectos, o null. */
    private final String dropItemId;

    /** Nombre de habilidad especial usada, o null. */
    private final String habilidadEspecial;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un resultado de IA.
     *
     * @param accion acción ejecutada
     * @param enemyType tipo de enemigo
     * @param salaId id de sala
     * @param filaOrigen fila inicial
     * @param colOrigen columna inicial
     * @param filaDestino fila final
     * @param colDestino columna final
     * @param combatResult resultado de combate
     * @param effectProcessingResult resultado de efectos previos
     * @param efectoAplicado efecto aplicado
     * @param tipoInvocado tipo invocado
     * @param filaInvocado fila invocada
     * @param colInvocado columna invocada
     * @param motivo motivo adicional
     * @param dropItemId id de drop generado por efectos
     */
    public AIActionResult(AccionIA accion, EnemyType enemyType, String salaId,
                          int filaOrigen, int colOrigen, int filaDestino, int colDestino,
                          CombatResult combatResult, EffectProcessingResult effectProcessingResult,
                          EffectType efectoAplicado, EnemyType tipoInvocado,
                          int filaInvocado, int colInvocado, String motivo, String dropItemId) {
        this(accion, enemyType, salaId, filaOrigen, colOrigen, filaDestino, colDestino,
            combatResult, effectProcessingResult, efectoAplicado, tipoInvocado, filaInvocado, colInvocado,
            motivo, dropItemId, null, null);
    }

    /**
     * Crea un resultado de IA con nombre visible y habilidad especial.
     *
     * @param accion acción ejecutada
     * @param enemyType tipo de enemigo
     * @param salaId id de sala
     * @param filaOrigen fila inicial
     * @param colOrigen columna inicial
     * @param filaDestino fila final
     * @param colDestino columna final
     * @param combatResult resultado de combate
     * @param effectProcessingResult resultado de efectos previos
     * @param efectoAplicado efecto aplicado
     * @param tipoInvocado tipo invocado
     * @param filaInvocado fila invocada
     * @param colInvocado columna invocada
     * @param motivo motivo adicional
     * @param dropItemId id de drop generado por efectos
     * @param nombreActor nombre visible del actor
     * @param habilidadEspecial nombre de la habilidad usada
     */
    public AIActionResult(AccionIA accion, EnemyType enemyType, String salaId,
                          int filaOrigen, int colOrigen, int filaDestino, int colDestino,
                          CombatResult combatResult, EffectProcessingResult effectProcessingResult,
                          EffectType efectoAplicado, EnemyType tipoInvocado,
                          int filaInvocado, int colInvocado, String motivo, String dropItemId,
                          String nombreActor, String habilidadEspecial) {
        this.accion = accion;
        this.enemyType = enemyType;
        this.nombreActor = nombreActor;
        this.salaId = salaId;
        this.filaOrigen = filaOrigen;
        this.colOrigen = colOrigen;
        this.filaDestino = filaDestino;
        this.colDestino = colDestino;
        this.combatResult = combatResult;
        this.effectProcessingResult = effectProcessingResult;
        this.efectoAplicado = efectoAplicado;
        this.tipoInvocado = tipoInvocado;
        this.filaInvocado = filaInvocado;
        this.colInvocado = colInvocado;
        this.motivo = motivo;
        this.dropItemId = dropItemId;
        this.habilidadEspecial = habilidadEspecial;
    }

    // -- Getters --------------------------------------------------------------

    /** @return acción ejecutada */
    public AccionIA getAccion() {
        return accion;
    }

    /** @return tipo de enemigo */
    public EnemyType getEnemyType() {
        return enemyType;
    }

    /**
     * Devuelve el nombre visible del actor.
     *
     * @return nombre visible, o el tipo de enemigo si no hay nombre narrativo
     */
    public String getNombreActor() {
        if (nombreActor != null && !nombreActor.isEmpty()) {
            return nombreActor;
        }
        return enemyType == null ? "ENEMIGO" : enemyType.name();
    }

    /** @return id de sala */
    public String getSalaId() {
        return salaId;
    }

    /** @return fila origen */
    public int getFilaOrigen() {
        return filaOrigen;
    }

    /** @return columna origen */
    public int getColOrigen() {
        return colOrigen;
    }

    /** @return fila destino */
    public int getFilaDestino() {
        return filaDestino;
    }

    /** @return columna destino */
    public int getColDestino() {
        return colDestino;
    }

    /** @return resultado de combate, o null */
    public CombatResult getCombatResult() {
        return combatResult;
    }

    /** @return resultado de efectos procesados, o null */
    public EffectProcessingResult getEffectProcessingResult() {
        return effectProcessingResult;
    }

    /** @return efecto aplicado, o null */
    public EffectType getEfectoAplicado() {
        return efectoAplicado;
    }

    /** @return tipo invocado, o null */
    public EnemyType getTipoInvocado() {
        return tipoInvocado;
    }

    /** @return fila de invocación */
    public int getFilaInvocado() {
        return filaInvocado;
    }

    /** @return columna de invocación */
    public int getColInvocado() {
        return colInvocado;
    }

    /** @return motivo adicional, o null */
    public String getMotivo() {
        return motivo;
    }

    /** @return id de drop generado por efectos, o null */
    public String getDropItemId() {
        return dropItemId;
    }

    /** @return nombre de habilidad especial, o null */
    public String getHabilidadEspecial() {
        return habilidadEspecial;
    }

    /**
     * Indica si la acción movió al enemigo.
     *
     * @return true si la posición cambió
     */
    public boolean huboMovimiento() {
        return filaOrigen != filaDestino || colOrigen != colDestino;
    }

    // -- Comparación ----------------------------------------------------------

    /**
     * Compara resultados por tipo y acción para compatibilidad con LSE.
     *
     * @param other resultado comparado
     * @return resultado de comparación
     */
    @Override
    public int compareTo(AIActionResult other) {
        if (other == null) {
            return 1;
        }
        String thisKey = key();
        String otherKey = other.key();
        return thisKey.compareTo(otherKey);
    }

    /**
     * Construye una clave simple de comparación.
     *
     * @return clave textual
     */
    private String key() {
        String tipoTexto = enemyType == null ? "" : enemyType.name();
        String accionTexto = accion == null ? "" : accion.name();
        return tipoTexto + "|" + accionTexto + "|" + filaOrigen + "|" + colOrigen;
    }
}
