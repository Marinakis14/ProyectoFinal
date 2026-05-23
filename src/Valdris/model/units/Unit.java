package Valdris.model.units;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.model.effects.Effect;
import Valdris.model.effects.EffectProcessingResult;
import Valdris.model.enums.EffectType;

/**
 * Clase base abstracta para todas las unidades que participan en el combate.
 *
 * <p>Una unidad representa tanto al jugador como a cualquier enemigo. Contiene
 * los puntos de vida, estadísticas base, posición dentro de la sala actual y
 * la lista de efectos de estado activos.</p>
 *
 * <p>Las subclases especializan el cálculo de ataque, defensa y rango efectivo
 * según su equipo o tipo. La gestión común de daño, curación, movimiento base y
 * efectos se centraliza aquí para evitar duplicar lógica en {@link Player} y
 * {@link Enemy}.</p>
 *
 * @see Player
 * @see Enemy
 * @see Effect
 */
public abstract class Unit {

    // -- Atributos ------------------------------------------------------------

    /** HP actual de la unidad. Nunca debe quedar por debajo de 0. */
    private int hp;

    /** HP máximo de la unidad. Permanece fijo durante la partida. */
    private final int hpMax;

    /** Ataque base sin armas, afinidades ni modificadores temporales. */
    private final int ataqueBase;

    /** Defensa base sin armaduras, escudos ni accesorios. */
    private final int defensaBase;

    /** Puntos de movimiento base antes de aplicar efectos de estado. */
    private final int movBase;

    /** Rango de ataque base antes de aplicar armas o reglas especiales. */
    private final int rango;

    /** Fila actual de la unidad dentro de la sala. */
    private int filaActual;

    /** Columna actual de la unidad dentro de la sala. */
    private int colActual;

    /** Efectos de estado activos sobre la unidad. */
    private final ListaSimplementeEnlazada<Effect> efectosActivos;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea una unidad con sus estadísticas base y posición inicial.
     *
     * @param hpMax HP máximo e inicial de la unidad
     * @param ataqueBase ataque base sin modificadores
     * @param defensaBase defensa base sin equipo
     * @param movBase movimiento base
     * @param rango rango base
     * @param filaActual fila inicial
     * @param colActual columna inicial
     */
    protected Unit(int hpMax, int ataqueBase, int defensaBase, int movBase, int rango,
                   int filaActual, int colActual) {
        this.hp = hpMax;
        this.hpMax = hpMax;
        this.ataqueBase = ataqueBase;
        this.defensaBase = defensaBase;
        this.movBase = movBase;
        this.rango = rango;
        this.filaActual = filaActual;
        this.colActual = colActual;
        this.efectosActivos = new ListaSimplementeEnlazada<>();
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Reduce el HP actual de la unidad sin permitir valores negativos.
     *
     * @param cantidad daño recibido
     */
    public void recibirDanio(int cantidad) {
        if (cantidad <= 0) {
            return;
        }
        hp = Math.max(0, hp - cantidad);
    }

    /**
     * Cura a la unidad sin superar su HP máximo.
     *
     * @param cantidad puntos de vida recuperados
     */
    public void curar(int cantidad) {
        if (cantidad <= 0) {
            return;
        }
        hp = Math.min(hpMax, hp + cantidad);
    }

    /**
     * Indica si la unidad sigue viva.
     *
     * @return true si el HP actual es mayor que 0
     */
    public boolean isVivo() {
        return hp > 0;
    }

    /**
     * Añade un efecto de estado a la unidad.
     *
     * <p>Si ya existe un efecto del mismo tipo, se elimina primero para que la
     * nueva instancia reemplace su duración. Los efectos no se apilan.</p>
     *
     * @param effect efecto que se añade o reemplaza
     */
    public void addEfecto(Effect effect) {
        if (effect == null) {
            return;
        }

        for (int i = 0; i < efectosActivos.getSize(); i++) {
            Effect actual = efectosActivos.get(i);
            if (actual != null && actual.getTipo() == effect.getTipo()) {
                efectosActivos.del(actual);
                break;
            }
        }
        efectosActivos.addEnd(effect);
    }

    /**
     * Comprueba si la unidad tiene activo un efecto concreto.
     *
     * @param tipo tipo de efecto buscado
     * @return true si existe un efecto activo de ese tipo
     */
    public boolean tieneEfecto(EffectType tipo) {
        if (tipo == null) {
            return false;
        }
        for (int i = 0; i < efectosActivos.getSize(); i++) {
            Effect effect = efectosActivos.get(i);
            if (effect != null && effect.getTipo() == tipo) {
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina un efecto activo concreto de la unidad.
     *
     * <p>Los efectos no se apilan en esta implementación, pero el recorrido
     * elimina cualquier coincidencia para dejar el estado limpio si se invoca
     * desde una poción o una regla especial.</p>
     *
     * @param tipo tipo de efecto que se quiere retirar
     */
    public void removeEfecto(EffectType tipo) {
        if (tipo == null) {
            return;
        }
        for (int i = 0; i < efectosActivos.getSize(); i++) {
            Effect effect = efectosActivos.get(i);
            if (effect != null && effect.getTipo() == tipo) {
                efectosActivos.del(effect);
                i--;
            }
        }
    }

    /**
     * Procesa los efectos activos de la unidad.
     *
     * <p>Los efectos de daño directo, como CURSE y BURN, aplican 3 puntos de
     * daño. Después se decrementa la duración de todos los efectos y se eliminan
     * los que han expirado.</p>
     *
     * @return resumen del daño aplicado y efectos expirados
     */
    public EffectProcessingResult procesarEfectos() {
        EffectType[] expirados = new EffectType[efectosActivos.getSize()];
        int expiradosSize = 0;
        int danioTotal = 0;
        for (int i = 0; i < efectosActivos.getSize(); i++) {
            Effect effect = efectosActivos.get(i);
            if (effect == null) {
                continue;
            }

            if (effect.getTipo() == EffectType.CURSE || effect.getTipo() == EffectType.BURN) {
                recibirDanio(3);
                danioTotal += 3;
            }

            effect.decrementar();
            if (effect.isExpired()) {
                expirados[expiradosSize] = effect.getTipo();
                expiradosSize++;
                efectosActivos.del(effect);
                i--;
            }
        }
        return new EffectProcessingResult(danioTotal, copiarEfectos(expirados, expiradosSize));
    }

    /**
     * Recorta un array de efectos al tamaño usado.
     *
     * @param origen array temporal
     * @param size número de entradas válidas
     * @return array recortado
     */
    private EffectType[] copiarEfectos(EffectType[] origen, int size) {
        EffectType[] copia = new EffectType[size];
        for (int i = 0; i < size; i++) {
            copia[i] = origen[i];
        }
        return copia;
    }

    /**
     * Devuelve el movimiento efectivo tras aplicar efectos de estado.
     *
     * @return movimiento reducido por SLOW, o movimiento base si no aplica
     */
    public int getMovEfectivo() {
        if (tieneEfecto(EffectType.SLOW)) {
            return (int) Math.ceil(movBase / 2.0);
        }
        return movBase;
    }

    /**
     * Devuelve la defensa total de la unidad.
     *
     * <p>La implementación base solo devuelve defensa base. Player sobrescribe
     * este método para sumar armadura, escudo y accesorio.</p>
     *
     * @return defensa total actual
     */
    public int getDefensaTotal() {
        return defensaBase;
    }

    /**
     * Devuelve el ataque total de la unidad.
     *
     * <p>La implementación base solo devuelve ataque base. Player sobrescribe
     * este método para usar arma equipada, afinidad y accesorios.</p>
     *
     * @return ataque total actual
     */
    public int getAtaqueTotal() {
        return ataqueBase;
    }

    /**
     * Devuelve el rango efectivo de la unidad.
     *
     * @return rango actual usado por combate
     */
    public int getRangoEfectivo() {
        return rango;
    }

    /**
     * Actualiza la posición de la unidad dentro de la sala.
     *
     * @param fila nueva fila
     * @param col nueva columna
     */
    public void setPosicion(int fila, int col) {
        this.filaActual = fila;
        this.colActual = col;
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el HP actual de la unidad.
     *
     * @return HP actual
     */
    public int getHp() {
        return hp;
    }

    /**
     * Ajusta el HP actual respetando el rango entre 0 y HP máximo.
     *
     * @param hp nuevo HP actual
     */
    public void setHp(int hp) {
        if (hp < 0) {
            this.hp = 0;
        } else {
            this.hp = Math.min(hp, hpMax);
        }
    }

    /**
     * Devuelve el HP máximo de la unidad.
     *
     * @return HP máximo
     */
    public int getHpMax() {
        return hpMax;
    }

    /**
     * Devuelve el ataque base de la unidad.
     *
     * @return ataque base
     */
    public int getAtaqueBase() {
        return ataqueBase;
    }

    /**
     * Devuelve la defensa base de la unidad.
     *
     * @return defensa base
     */
    public int getDefensaBase() {
        return defensaBase;
    }

    /**
     * Devuelve el movimiento base de la unidad.
     *
     * @return movimiento base
     */
    public int getMovBase() {
        return movBase;
    }

    /**
     * Devuelve el rango base de la unidad.
     *
     * @return rango base
     */
    public int getRango() {
        return rango;
    }

    /**
     * Devuelve la fila actual de la unidad.
     *
     * @return fila actual
     */
    public int getFilaActual() {
        return filaActual;
    }

    /**
     * Devuelve la columna actual de la unidad.
     *
     * @return columna actual
     */
    public int getColActual() {
        return colActual;
    }

    /**
     * Devuelve la lista de efectos activos.
     *
     * @return efectos activos de la unidad
     */
    public ListaSimplementeEnlazada<Effect> getEfectosActivos() {
        return efectosActivos;
    }

}
