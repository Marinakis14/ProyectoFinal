package Valdris.logic.combat;

import Valdris.model.enums.EffectType;

/**
 * Resultado estructurado de una acción de combate.
 *
 * <p>Permite que capas superiores registren logs precisos sin reconstruir lo
 * ocurrido comparando HP antes y después. El combate sigue modificando el
 * estado real de las unidades, pero devuelve un resumen fiable de la acción
 * resuelta.</p>
 */
public class CombatResult implements Comparable<CombatResult> {

    // -- Atributos ------------------------------------------------------------

    /** Daño final aplicado al defensor. */
    private final int danioAplicado;

    /** Indica si el ataque falló por BLIND. */
    private final boolean falloPorBlind;

    /** Indica si el defensor murió con este ataque. */
    private final boolean objetivoMuerto;

    /** HP restante del defensor tras resolver el ataque. */
    private final int hpRestanteObjetivo;

    /** HP máximo del defensor tras resolver el ataque. */
    private final int hpMaxObjetivo;

    /** Efecto primario aplicado por arma, o null. */
    private final EffectType efectoPrimarioAplicado;

    /** Efecto secundario aplicado por arma, o null. */
    private final EffectType efectoSecundarioAplicado;

    /** ID del drop generado al morir, o null. */
    private final String dropItemId;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un resultado de combate.
     *
     * @param danioAplicado daño final
     * @param falloPorBlind true si el ataque falló por BLIND
     * @param objetivoMuerto true si el objetivo murió
     * @param hpRestanteObjetivo HP restante
     * @param hpMaxObjetivo HP máximo
     * @param efectoPrimarioAplicado efecto primario aplicado
     * @param efectoSecundarioAplicado efecto secundario aplicado
     * @param dropItemId id del drop generado
     */
    public CombatResult(int danioAplicado, boolean falloPorBlind, boolean objetivoMuerto,
                        int hpRestanteObjetivo, int hpMaxObjetivo,
                        EffectType efectoPrimarioAplicado, EffectType efectoSecundarioAplicado,
                        String dropItemId) {
        this.danioAplicado = Math.max(0, danioAplicado);
        this.falloPorBlind = falloPorBlind;
        this.objetivoMuerto = objetivoMuerto;
        this.hpRestanteObjetivo = Math.max(0, hpRestanteObjetivo);
        this.hpMaxObjetivo = Math.max(0, hpMaxObjetivo);
        this.efectoPrimarioAplicado = efectoPrimarioAplicado;
        this.efectoSecundarioAplicado = efectoSecundarioAplicado;
        this.dropItemId = dropItemId;
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el daño aplicado.
     *
     * @return daño final
     */
    public int getDanioAplicado() {
        return danioAplicado;
    }

    /**
     * Indica si el ataque falló por BLIND.
     *
     * @return true si falló por BLIND
     */
    public boolean isFalloPorBlind() {
        return falloPorBlind;
    }

    /**
     * Indica si el objetivo murió.
     *
     * @return true si murió
     */
    public boolean isObjetivoMuerto() {
        return objetivoMuerto;
    }

    /**
     * Devuelve el HP restante del objetivo.
     *
     * @return HP restante
     */
    public int getHpRestanteObjetivo() {
        return hpRestanteObjetivo;
    }

    /**
     * Devuelve el HP máximo del objetivo.
     *
     * @return HP máximo
     */
    public int getHpMaxObjetivo() {
        return hpMaxObjetivo;
    }

    /**
     * Devuelve el efecto primario aplicado.
     *
     * @return efecto primario, o null
     */
    public EffectType getEfectoPrimarioAplicado() {
        return efectoPrimarioAplicado;
    }

    /**
     * Devuelve el efecto secundario aplicado.
     *
     * @return efecto secundario, o null
     */
    public EffectType getEfectoSecundarioAplicado() {
        return efectoSecundarioAplicado;
    }

    /**
     * Devuelve el id del drop generado.
     *
     * @return id de item, o null
     */
    public String getDropItemId() {
        return dropItemId;
    }

    // -- Comparación ----------------------------------------------------------

    /**
     * Compara resultados por daño y estado de muerte para compatibilidad LSE.
     *
     * @param other resultado comparado
     * @return resultado de comparación
     */
    @Override
    public int compareTo(CombatResult other) {
        if (other == null) {
            return 1;
        }
        int resultado = danioAplicado - other.danioAplicado;
        if (resultado != 0) {
            return resultado;
        }
        if (objetivoMuerto == other.objetivoMuerto) {
            return 0;
        }
        return objetivoMuerto ? 1 : -1;
    }
}
