package Valdris.model.enums;

/**
 * Define los mini-bosses narrativos que cierran las zonas principales.
 *
 * <p>Estos enemigos tienen estadísticas y drops propios. Se separan de
 * {@link EnemyType} porque más adelante podrán tener habilidades especiales sin
 * llenar la clase base de enemigos normales con casos concretos.</p>
 */
public enum MiniBossType {

    /**
     * Mini-boss de Zona 1.
     * Derrotarlo entrega la Llave de Hierro.
     */
    ALCALDE_CORRUPTO,

    /**
     * Mini-boss de Zona 2.
     * Derrotarlo entrega la Semilla Resonante.
     */
    ESPIRITU_MADRE,

    /**
     * Mini-boss de Zona 3.
     * Usa Pisotón Sísmico como onda de área y entrega el Fragmento de Sello.
     */
    GOLEM,

    /**
     * Mini-boss de Zona 4.
     * Derrotarlo entrega el Fragmento de Voluntad.
     */
    GUARDIAN_SIN_NOMBRE,

    /**
     * Mini-boss de Zona 5 previo al Núcleo.
     * Ataca ignorando 5 puntos de defensa del jugador.
     */
    EL_FILTRO
}
