package Valdris.model.items;

import Valdris.model.effects.Effect;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.ItemType;
import Valdris.model.units.Player;

/**
 * Poción consumible que aplica curación y, opcionalmente, un efecto adicional.
 *
 * <p>Las pociones se usan desde el inventario durante la fase de uso de item.
 * A diferencia de armas, armaduras y accesorios, una poción no queda equipada:
 * se consume y debe eliminarse del inventario tras resolver su efecto.</p>
 *
 * <p>La curación nunca debe superar los HP máximos del jugador. Esa regla se
 * aplica desde {@code Player.curar(int)}, porque el jugador conoce su vida
 * actual y su máximo real.</p>
 *
 * @see Item
 * @see Effect
 * @see EffectType
 */
public class Potion extends Item {

    // -- Atributos ------------------------------------------------------------

    /** Puntos de vida que recupera la poción al usarse. */
    private final int curacionHP;

    /** Efecto adicional aplicado por la poción, o null si solo cura. */
    private EffectType efectoExtra;

    /** Valor asociado al efecto extra, interpretado por la lógica de jugador. */
    private int valorEfecto;

    /** Primer efecto que esta poción elimina al usarse, o null si no limpia. */
    private EffectType efectoALimpiarPrimario;

    /** Segundo efecto que esta poción elimina al usarse, o null si no limpia. */
    private EffectType efectoALimpiarSecundario;

    /** Bonus de ataque temporal aplicado al próximo ataque del jugador. */
    private int bonusAtaqueTemporal;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea una poción con una cantidad fija de curación.
     *
     * @param id identificador único de la poción
     * @param nombre nombre visible de la poción
     * @param curacionHP puntos de vida que recupera al usarse
     */
    public Potion(String id, String nombre, int curacionHP) {
        super(id, nombre, ItemType.POTION, "Poción consumible: " + nombre);
        this.curacionHP = curacionHP;
        this.efectoExtra = null;
        this.valorEfecto = 0;
        this.efectoALimpiarPrimario = null;
        this.efectoALimpiarSecundario = null;
        this.bonusAtaqueTemporal = 0;
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Usa la poción sobre el jugador y la elimina del inventario.
     *
     * <p>Primero aplica la curación. Después, si hay un efecto extra
     * configurado, lo añade a los efectos activos del jugador. Finalmente retira
     * esta poción del inventario para mantener su comportamiento consumible.</p>
     *
     * @param player jugador que consume la poción
     */
    @Override
    public void use(Player player) {
        if (player == null) {
            return;
        }
        player.curar(curacionHP);
        if (efectoExtra != null) {
            player.addEfecto(new Effect(efectoExtra, getDuracionEfectoExtra()));
        }
        if (efectoALimpiarPrimario != null) {
            player.removeEfecto(efectoALimpiarPrimario);
        }
        if (efectoALimpiarSecundario != null) {
            player.removeEfecto(efectoALimpiarSecundario);
        }
        if (bonusAtaqueTemporal > 0) {
            player.addBonusAtaqueTemporal(bonusAtaqueTemporal);
        }
        player.removeItem(this);
    }

    /**
     * Configura el efecto adicional de la poción.
     *
     * <p>El valor se conserva para que la lógica de unidades o generación pueda
     * interpretarlo de forma consistente con la ficha concreta del item.</p>
     *
     * @param efectoExtra efecto aplicado al consumir la poción
     * @param valorEfecto valor asociado al efecto extra
     */
    public void setEfectoExtra(EffectType efectoExtra, int valorEfecto) {
        this.efectoExtra = efectoExtra;
        this.valorEfecto = valorEfecto;
    }

    /**
     * Configura los efectos que esta poción elimina al consumirse.
     *
     * <p>Se ofrecen dos ranuras explícitas porque el Antídoto elimina CURSE y
     * BLIND. Si una ranura es null, simplemente se ignora.</p>
     *
     * @param efectoPrimario primer efecto a limpiar
     * @param efectoSecundario segundo efecto a limpiar
     */
    public void setEfectosALimpiar(EffectType efectoPrimario, EffectType efectoSecundario) {
        this.efectoALimpiarPrimario = efectoPrimario;
        this.efectoALimpiarSecundario = efectoSecundario;
    }

    /**
     * Configura un bonus de ataque temporal para el próximo ataque.
     *
     * @param bonusAtaqueTemporal puntos de ataque que se añadirán temporalmente
     */
    public void setBonusAtaqueTemporal(int bonusAtaqueTemporal) {
        if (bonusAtaqueTemporal < 0) {
            this.bonusAtaqueTemporal = 0;
        } else {
            this.bonusAtaqueTemporal = bonusAtaqueTemporal;
        }
    }

    /**
     * Calcula una duración válida para crear el efecto extra.
     *
     * @return duración mínima de 1 turno
     */
    public int getDuracionEfectoExtra() {
        if (valorEfecto <= 0) {
            return 1;
        }
        return valorEfecto;
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve la cantidad de HP que cura esta poción.
     *
     * @return puntos de vida recuperados
     */
    public int getCuracionHP() {
        return curacionHP;
    }

    /**
     * Devuelve el efecto extra configurado.
     *
     * @return efecto adicional, o null si solo cura
     */
    public EffectType getEfectoExtra() {
        return efectoExtra;
    }

    /**
     * Devuelve el valor asociado al efecto extra.
     *
     * @return valor del efecto, o 0 si no aplica
     */
    public int getValorEfecto() {
        return valorEfecto;
    }

    /**
     * Devuelve el primer efecto que limpia esta poción.
     *
     * @return efecto primario a limpiar, o null si no aplica
     */
    public EffectType getEfectoALimpiarPrimario() {
        return efectoALimpiarPrimario;
    }

    /**
     * Devuelve el segundo efecto que limpia esta poción.
     *
     * @return efecto secundario a limpiar, o null si no aplica
     */
    public EffectType getEfectoALimpiarSecundario() {
        return efectoALimpiarSecundario;
    }

    /**
     * Devuelve el bonus de ataque temporal configurado.
     *
     * @return bonus temporal para el próximo ataque
     */
    public int getBonusAtaqueTemporal() {
        return bonusAtaqueTemporal;
    }
}
