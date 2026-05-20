package Valdris.model.effects;

import Valdris.model.enums.EffectType;

/**
 * Representa un efecto de estado activo sobre una unidad (jugador o enemigo).
 *
 * <p>Los efectos se almacenan en una {@code ListaSimplementeEnlazada<Effect>} dentro de
 * {@code Unit}. Al inicio de cada turno de la unidad se llama a {@code procesarEfectos()},
 * que decrementa todos los efectos activos y elimina los que han expirado.</p>
 *
 * <p>Si una unidad recibe un efecto que ya tiene activo, el efecto existente se
 * reemplaza (no se apilan).</p>
 *
 * @see EffectType
 */
public class Effect implements Comparable<Effect> {

    // ── Atributos ────────────────────────────────────────────────────────────

    /** Tipo de efecto que representa esta instancia. */
    private final EffectType tipo;

    /** Turnos que quedan hasta que el efecto desaparece. */
    private int turnosRestantes;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Crea un nuevo efecto del tipo indicado con la duracion especificada.
     *
     * @param tipo    tipo de efecto (SLOW, BLIND, CURSE, PARALYSIS o BURN)
     * @param turnos  duracion en turnos (debe ser > 0)
     * @throws IllegalArgumentException si turnos es menor o igual a 0
     */
    public Effect(EffectType tipo, int turnos) {
        if (turnos <= 0) {
            throw new IllegalArgumentException(
                "La duracion de un efecto debe ser mayor que 0. Recibido: " + turnos);
        }
        this.tipo = tipo;
        this.turnosRestantes = turnos;
    }

    // ── Metodos de logica ────────────────────────────────────────────────────

    /**
     * Decrementa en 1 el contador de turnos restantes.
     *
     * <p>Llamar este metodo al final de cada turno de la unidad afectada.
     * Despues de llamarlo, comprobar {@link #isExpired()} para saber si
     * el efecto debe eliminarse de la lista.</p>
     */
    public void decrementar() {
        turnosRestantes--;
    }

    /**
     * Indica si el efecto ha expirado y debe eliminarse de la unidad.
     *
     * @return {@code true} si {@code turnosRestantes <= 0}
     */
    public boolean isExpired() {
        return turnosRestantes <= 0;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    /**
     * Devuelve el tipo de este efecto.
     *
     * @return el {@link EffectType} de este efecto
     */
    public EffectType getTipo() {
        return tipo;
    }

    /**
     * Devuelve los turnos que le quedan al efecto antes de expirar.
     *
     * @return turnos restantes (puede ser 0 o negativo si ya expiro)
     */
    public int getTurnos() {
        return turnosRestantes;
    }

    /**
     * Devuelve los turnos que le quedan al efecto antes de expirar.
     *
     * @return turnos restantes (puede ser 0 o negativo si ya expiro)
     */
    public int getTurnosRestantes() {
        return turnosRestantes;
    }

    // ── Comparacion ─────────────────────────────────────────────────────────

    /**
     * Compara efectos por tipo para poder almacenarlos en las estructuras
     * enlazadas propias del proyecto.
     *
     * <p>En la logica del juego no debe haber dos efectos activos del mismo
     * tipo sobre una unidad. Por eso comparar por {@code tipo} es suficiente
     * para localizar, reemplazar o eliminar efectos en la lista.</p>
     *
     * @param other efecto con el que se compara
     * @return resultado de comparar los tipos de efecto
     */
    @Override
    public int compareTo(Effect other) {
        if (other == null) {
            return 1;
        }
        return tipo.compareTo(other.tipo);
    }

    // ── toString ─────────────────────────────────────────────────────────────

    /**
     * Representacion textual del efecto para logs de combate y depuracion.
     *
     * @return cadena con formato "[TIPO(Xt)]" donde X son los turnos restantes
     */
    @Override
    public String toString() {
        return "[" + tipo.name() + "(" + turnosRestantes + "t)]";
    }
}
