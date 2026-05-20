package Valdris.model.items;

import Valdris.model.enums.EffectType;
import Valdris.model.enums.ItemType;
import Valdris.model.units.Player;

/**
 * Armadura o escudo equipable por el jugador.
 *
 * <p>Las armaduras se colocan en el torso y los escudos en la mano secundaria.
 * Ambas piezas aportan defensa total mientras están equipadas. Algunas piezas
 * especiales también pueden dar inmunidad a un efecto de estado concreto.</p>
 *
 * <p>En el diseño de Valdris, la defensa del jugador depende del equipo y no
 * de una progresión permanente de nivel. Por eso esta clase concentra los
 * valores defensivos que se suman durante el cálculo de daño.</p>
 *
 * @see Item
 * @see EffectType
 */
public class Armor extends Item {

    // -- Atributos ------------------------------------------------------------

    /** Puntos de defensa que aporta esta pieza mientras está equipada. */
    private final int defensa;

    /** Efecto de estado al que hace inmune al portador, o null si no tiene. */
    private EffectType inmunidad;

    /** Indica si esta pieza ocupa la mano secundaria en lugar del torso. */
    private final boolean esEscudo;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea una pieza de armadura o escudo.
     *
     * @param id identificador único de la pieza
     * @param nombre nombre visible de la pieza
     * @param defensa puntos de defensa aportados
     * @param esEscudo true si se equipa como escudo, false si se equipa en torso
     */
    public Armor(String id, String nombre, int defensa, boolean esEscudo) {
        super(id, nombre, esEscudo ? ItemType.SHIELD : ItemType.ARMOR, "Equipo defensivo: " + nombre);
        this.defensa = defensa;
        this.esEscudo = esEscudo;
        this.inmunidad = null;
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Equipa esta pieza en la ranura defensiva correspondiente del jugador.
     *
     * <p>Si {@code esEscudo} es true, ocupa la mano secundaria. En caso
     * contrario, se equipa como armadura de torso.</p>
     *
     * @param player jugador que equipa la pieza
     */
    @Override
    public void use(Player player) {
        if (player == null) {
            return;
        }
        if (esEscudo) {
            player.setEscudoEquipado(this);
        } else {
            player.setArmaduraEquipada(this);
        }
    }

    /**
     * Configura la inmunidad especial de esta pieza.
     *
     * <p>Se usa para objetos concretos de la guía, como piezas que protegen de
     * SLOW o CURSE. Una pieza sin inmunidad mantiene este valor en null.</p>
     *
     * @param inmunidad efecto que bloquea esta pieza
     */
    public void setInmunidad(EffectType inmunidad) {
        this.inmunidad = inmunidad;
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve la defensa aportada por esta pieza.
     *
     * @return puntos de defensa
     */
    public int getDefensa() {
        return defensa;
    }

    /**
     * Indica si esta pieza se equipa como escudo.
     *
     * @return true si ocupa la mano secundaria
     */
    public boolean isEscudo() {
        return esEscudo;
    }

    /**
     * Devuelve el efecto de estado bloqueado por esta pieza.
     *
     * @return efecto inmune, o null si no bloquea ninguno
     */
    public EffectType getInmunidad() {
        return inmunidad;
    }
}
