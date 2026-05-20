package Valdris.model.enums;

/**
 * Define todos los efectos de estado que pueden afectar a unidades del juego.
 *
 * <p>Los efectos se guardan en la lista de efectos activos de cada unidad y se
 * procesan al avanzar los turnos. Su impacto principal es alterar movimiento,
 * daño recibido o disponibilidad de acciones durante un número limitado de
 * turnos.</p>
 *
 * <p>Según la especificación principal del proyecto, si una unidad recibe un
 * efecto que ya tiene activo, se reinicia o reemplaza su duración en lugar de
 * acumular varias copias del mismo efecto.</p>
 *
 * @see Valdris.model.effects.Effect
 * @see Valdris.model.units.Unit
 */
public enum EffectType {

    /**
     * Ralentización. Dura 2 turnos.
     * Reduce el movimiento efectivo a {@code ceil(movBase / 2.0)}.
     * Puede venir de enemigos controladores o de armas como el Arco Élfico.
     */
    SLOW,

    /**
     * Ceguera. Dura 2 turnos.
     * Reduce el movimiento efectivo a {@code ceil(movBase / 2.0)} y representa
     * desorientación táctica durante el turno afectado.
     */
    BLIND,

    /**
     * Maldición. Dura 2 turnos.
     * Aumenta el daño recibido o aplica daño adicional según la lógica de
     * combate y turnos definida para la unidad afectada.
     */
    CURSE,

    /**
     * Parálisis. Dura 1 turno según la especificación base del proyecto.
     * Impide que la unidad afectada pueda moverse o atacar durante su turno.
     */
    PARALYSIS,

    /**
     * Quemadura. Dura 1 turno.
     * Aplica daño al inicio del turno del afectado antes de resolver sus
     * acciones normales.
     */
    BURN
}
