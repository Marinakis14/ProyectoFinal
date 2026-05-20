package Valdris.model.items;

import Valdris.model.enums.ItemType;
import Valdris.model.units.Player;

/**
 * Accesorio equipable por el jugador en la ranura de accesorio.
 *
 * <p>Los accesorios cubren dos funciones distintas dentro de Valdris. Algunos
 * son objetos de combate que aportan ataque, movimiento o defensa. Otros son
 * objetos narrativos de progresión, como llaves, semillas o fragmentos que
 * permiten acceder a salas o detectar elementos ocultos.</p>
 *
 * <p>Solo puede haber un accesorio equipado a la vez. Equiparlo consume la
 * acción de uso de item del turno, pero sus efectos pasivos se consultan desde
 * el jugador o desde la lógica de mapa cuando corresponde.</p>
 *
 * @see Item
 * @see Player
 */
public class Accessory extends Item {

    // -- Atributos ------------------------------------------------------------

    /** Bonus de ataque aportado mientras el accesorio está equipado. */
    private int bonusAtaque;

    /** Bonus de movimiento aportado mientras el accesorio está equipado. */
    private int bonusMov;

    /** Bonus de defensa aportado mientras el accesorio está equipado. */
    private int bonusDef;

    /** Indica si el accesorio sirve principalmente para progresión narrativa. */
    private boolean esNarrativo;

    /** Descripción del efecto narrativo, o null si es un accesorio de combate. */
    private String efectoNarrativo;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un accesorio sin bonus ni efecto narrativo configurado.
     *
     * @param id identificador único del accesorio
     * @param nombre nombre visible del accesorio
     */
    public Accessory(String id, String nombre) {
        super(id, nombre, ItemType.ACCESSORY, "Accesorio equipable: " + nombre);
        this.bonusAtaque = 0;
        this.bonusMov = 0;
        this.bonusDef = 0;
        this.esNarrativo = false;
        this.efectoNarrativo = null;
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Equipa el accesorio en la ranura correspondiente del jugador.
     *
     * @param player jugador que equipa el accesorio
     */
    @Override
    public void use(Player player) {
        if (player != null) {
            player.setAccesorioEquipado(this);
        }
    }

    /**
     * Configura los bonus de combate del accesorio.
     *
     * <p>Se usa para accesorios como amuletos o anillos que modifican
     * estadísticas mientras están equipados.</p>
     *
     * @param bonusAtaque bonus de ataque
     * @param bonusMov bonus de movimiento
     * @param bonusDef bonus de defensa
     */
    public void setBonus(int bonusAtaque, int bonusMov, int bonusDef) {
        this.bonusAtaque = bonusAtaque;
        this.bonusMov = bonusMov;
        this.bonusDef = bonusDef;
    }

    /**
     * Configura el accesorio como objeto narrativo de progresión.
     *
     * <p>Los accesorios narrativos se guardan en la misma ranura, pero su valor
     * principal es desbloquear rutas, revelar trampas o cumplir requisitos de
     * historia.</p>
     *
     * @param efectoNarrativo descripción del efecto narrativo
     */
    public void setEfectoNarrativo(String efectoNarrativo) {
        this.esNarrativo = true;
        this.efectoNarrativo = efectoNarrativo;
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el bonus de ataque del accesorio.
     *
     * @return bonus de ataque
     */
    public int getBonusAtaque() {
        return bonusAtaque;
    }

    /**
     * Devuelve el bonus de movimiento del accesorio.
     *
     * @return bonus de movimiento
     */
    public int getBonusMov() {
        return bonusMov;
    }

    /**
     * Devuelve el bonus de defensa del accesorio.
     *
     * @return bonus de defensa
     */
    public int getBonusDef() {
        return bonusDef;
    }

    /**
     * Indica si el accesorio es de progresión narrativa.
     *
     * @return true si su función principal es narrativa
     */
    public boolean isNarrativo() {
        return esNarrativo;
    }

    /**
     * Devuelve la descripción del efecto narrativo.
     *
     * @return texto del efecto narrativo, o null si no aplica
     */
    public String getEfectoNarrativo() {
        return efectoNarrativo;
    }
}
