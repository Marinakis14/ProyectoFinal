package Valdris.model.effects;

import Valdris.model.enums.EffectType;

/**
 * Resultado estructurado de procesar efectos activos de una unidad.
 *
 * <p>La unidad sigue modificando su propio HP y lista de efectos, pero este
 * resultado permite que la lógica de turnos registre daño periódico y
 * expiraciones relevantes en el log de partida.</p>
 */
public class EffectProcessingResult implements Comparable<EffectProcessingResult> {

    // -- Atributos ------------------------------------------------------------

    /** Daño total aplicado por efectos durante el procesamiento. */
    private final int danioAplicado;

    /** Efectos que han expirado durante el procesamiento. */
    private final EffectType[] efectosExpirados;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un resultado de procesamiento de efectos.
     *
     * @param danioAplicado daño total aplicado
     * @param efectosExpirados efectos que han terminado
     */
    public EffectProcessingResult(int danioAplicado, EffectType[] efectosExpirados) {
        this.danioAplicado = Math.max(0, danioAplicado);
        this.efectosExpirados = copiar(efectosExpirados);
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el daño aplicado por efectos.
     *
     * @return daño total
     */
    public int getDanioAplicado() {
        return danioAplicado;
    }

    /**
     * Devuelve una copia de los efectos expirados.
     *
     * @return efectos expirados
     */
    public EffectType[] getEfectosExpirados() {
        return copiar(efectosExpirados);
    }

    /**
     * Indica si hubo daño o expiraciones relevantes.
     *
     * @return true si el resultado contiene algún evento
     */
    public boolean tieneEventos() {
        return danioAplicado > 0 || efectosExpirados.length > 0;
    }

    // -- Comparación ----------------------------------------------------------

    /**
     * Compara resultados por daño y cantidad de expiraciones.
     *
     * @param other resultado comparado
     * @return resultado de comparación
     */
    @Override
    public int compareTo(EffectProcessingResult other) {
        if (other == null) {
            return 1;
        }
        int resultado = danioAplicado - other.danioAplicado;
        if (resultado != 0) {
            return resultado;
        }
        return efectosExpirados.length - other.efectosExpirados.length;
    }

    // -- Utilidades -----------------------------------------------------------

    /**
     * Copia un array admitiendo null.
     *
     * @param origen array origen
     * @return copia no nula
     */
    private static EffectType[] copiar(EffectType[] origen) {
        if (origen == null) {
            return new EffectType[0];
        }
        EffectType[] copia = new EffectType[origen.length];
        for (int i = 0; i < origen.length; i++) {
            copia[i] = origen[i];
        }
        return copia;
    }
}
