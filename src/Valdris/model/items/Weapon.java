package Valdris.model.items;

import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.ItemType;
import Valdris.model.units.Player;

/**
 * Arma equipable en la mano principal del jugador.
 *
 * <p>El daño base del arma reemplaza el ataque base del personaje mientras
 * está equipada. Después se aplica la afinidad del personaje, que puede sumar,
 * restar o dejar igual el daño efectivo del arma.</p>
 *
 * <p>Algunas armas avanzadas también tienen penetración de armadura o un
 * efecto especial con probabilidad de activarse durante el ataque. Estos datos
 * los configura {@code ItemGenerator} al crear cada arma concreta por ID.</p>
 *
 * @see Item
 * @see CharacterType
 * @see EffectType
 */
public class Weapon extends Item {

    // -- Atributos ------------------------------------------------------------

    /** Daño base del arma antes de aplicar afinidad de personaje. */
    private final int danoBase;

    /** Puntos de defensa enemiga que el arma ignora al calcular daño. */
    private final int penetracion;

    /** Bonus o penalización por personaje, indexado por {@code CharacterType.ordinal()}. */
    private final int[] afinidades;

    /** Efecto que el arma puede aplicar al atacar, o null si no tiene. */
    private EffectType efectoEspecial;

    /** Probabilidad entre 0.0 y 1.0 de aplicar el efecto especial. */
    private double probEfecto;

    /** Rango de ataque otorgado por el arma mientras está equipada. */
    private final int rango;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un arma con daño base, penetración y rango de ataque.
     *
     * @param id identificador único del arma
     * @param nombre nombre visible del arma
     * @param danoBase daño base antes de afinidad
     * @param pen puntos de defensa ignorados
     * @param rango rango de ataque del arma
     */
    public Weapon(String id, String nombre, int danoBase, int pen, int rango) {
        super(id, nombre, ItemType.WEAPON, "Arma equipable: " + nombre);
        this.danoBase = danoBase;
        this.penetracion = pen;
        this.rango = rango;
        this.afinidades = new int[CharacterType.values().length];
        this.efectoEspecial = null;
        this.probEfecto = 0.0;
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Establece el bonus o penalización de afinidad para un personaje.
     *
     * <p>La afinidad se suma al daño base del arma para calcular el daño
     * efectivo. Un valor negativo representa que el personaje usa peor ese arma.</p>
     *
     * @param tipo personaje al que se aplica la afinidad
     * @param bonus bonus o penalización de daño
     */
    public void setAfinidad(CharacterType tipo, int bonus) {
        if (tipo != null) {
            afinidades[tipo.ordinal()] = bonus;
        }
    }

    /**
     * Devuelve la afinidad configurada para un personaje.
     *
     * @param tipo personaje consultado
     * @return bonus de afinidad, o 0 si no hay afinidad definida
     */
    public int getAfinidad(CharacterType tipo) {
        if (tipo == null) {
            return 0;
        }
        return afinidades[tipo.ordinal()];
    }

    /**
     * Calcula el daño efectivo del arma para un personaje concreto.
     *
     * @param tipo personaje que usa el arma
     * @return daño base más afinidad del personaje
     */
    public int getDanoEfectivo(CharacterType tipo) {
        return danoBase + getAfinidad(tipo);
    }

    /**
     * Equipa el arma en la mano principal del jugador.
     *
     * @param player jugador que equipa el arma
     */
    @Override
    public void use(Player player) {
        if (player != null) {
            player.setArmaEquipada(this);
        }
    }

    /**
     * Intenta aplicar el efecto especial del arma.
     *
     * <p>Si el arma no tiene efecto configurado, devuelve null. Si lo tiene,
     * lanza una tirada con {@code Math.random()} y aplica el efecto cuando la
     * tirada queda por debajo de la probabilidad configurada.</p>
     *
     * @return efecto aplicado, o null si no se activa
     */
    public EffectType tryAplicarEfecto() {
        if (efectoEspecial == null) {
            return null;
        }
        if (Math.random() < probEfecto) {
            return efectoEspecial;
        }
        return null;
    }

    /**
     * Configura el efecto especial del arma y su probabilidad.
     *
     * @param efectoEspecial efecto que puede aplicar el arma
     * @param probEfecto probabilidad entre 0.0 y 1.0
     */
    public void setEfectoEspecial(EffectType efectoEspecial, double probEfecto) {
        this.efectoEspecial = efectoEspecial;
        if (probEfecto < 0.0) {
            this.probEfecto = 0.0;
        } else if (probEfecto > 1.0) {
            this.probEfecto = 1.0;
        } else {
            this.probEfecto = probEfecto;
        }
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el daño base del arma.
     *
     * @return daño base antes de afinidad
     */
    public int getDanoBase() {
        return danoBase;
    }

    /**
     * Devuelve la penetración de armadura del arma.
     *
     * @return puntos de defensa ignorados
     */
    public int getPenetracion() {
        return penetracion;
    }

    /**
     * Devuelve el efecto especial configurado.
     *
     * @return efecto especial, o null si el arma no tiene
     */
    public EffectType getEfectoEspecial() {
        return efectoEspecial;
    }

    /**
     * Devuelve la probabilidad de activar el efecto especial.
     *
     * @return probabilidad entre 0.0 y 1.0
     */
    public double getProbEfecto() {
        return probEfecto;
    }

    /**
     * Devuelve el rango de ataque del arma.
     *
     * @return rango en celdas
     */
    public int getRango() {
        return rango;
    }
}
