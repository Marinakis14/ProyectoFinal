package Valdris.model.units;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.model.effects.Effect;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.ItemType;
import Valdris.model.items.Accessory;
import Valdris.model.items.Armor;
import Valdris.model.items.Item;
import Valdris.model.items.Weapon;

/**
 * Personaje controlado por el jugador durante una partida.
 *
 * <p>Valdris se juega con un único personaje elegido al inicio. Esa elección
 * determina los HP máximos, ataque base, movimiento y rango base. El personaje
 * puede mejorar su rendimiento con armas, armaduras, escudos, accesorios y
 * pociones encontrados durante la exploración.</p>
 *
 * <p>El jugador también guarda el estado de acciones usadas en el turno actual.
 * TurnManager consulta estos flags para respetar el orden de fases y evitar que
 * se repita una acción más de una vez por turno.</p>
 *
 * @see Unit
 * @see CharacterType
 * @see Item
 */
public class Player extends Unit implements Comparable<Player> {

    // -- Atributos ------------------------------------------------------------

    /** Personaje elegido al inicio de la partida. */
    private final CharacterType tipo;

    /** Inventario completo del jugador. */
    private final ListaSimplementeEnlazada<Item> inventario;

    /** Objetos narrativos de progresión separados del inventario de combate. */
    private final ListaSimplementeEnlazada<Item> itemsNarrativos;

    /** Arma equipada en la mano principal. */
    private Weapon armaEquipada;

    /** Escudo o pieza de mano secundaria equipada. */
    private Armor escudoEquipado;

    /** Armadura equipada en el torso. */
    private Armor armaduraEquipada;

    /** Accesorio equipado en la ranura de accesorio. */
    private Accessory accesorioEquipado;

    /** Indica si el jugador ya usó la acción de movimiento este turno. */
    private boolean haMovido;

    /** Indica si el jugador ya usó la acción de recogida este turno. */
    private boolean haRecogido;

    /** Indica si el jugador ya usó la acción de item este turno. */
    private boolean haUsadoItem;

    /** Indica si el jugador ya usó la acción de ataque este turno. */
    private boolean haAtacado;

    /** Bonus de ataque que se consume en el próximo ataque resuelto. */
    private int bonusAtaqueTemporal;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un jugador con las estadísticas base del personaje elegido.
     *
     * @param tipo personaje jugable seleccionado
     */
    public Player(CharacterType tipo) {
        super(getHpBase(tipo), getAtaqueBase(tipo), getDefensaBase(tipo), getMovBase(tipo), getRangoBase(tipo), 0, 0);
        this.tipo = tipo;
        this.inventario = new ListaSimplementeEnlazada<>();
        this.itemsNarrativos = new ListaSimplementeEnlazada<>();
        this.armaEquipada = null;
        this.escudoEquipado = null;
        this.armaduraEquipada = null;
        this.accesorioEquipado = null;
        this.bonusAtaqueTemporal = 0;
        resetAcciones();
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Añade un item al inventario del jugador.
     *
     * @param item item que se añade
     */
    public void addItem(Item item) {
        if (item != null && item.getTipo() == ItemType.NARRATIVE) {
            if (!tieneItemNarrativo(item.getId())) {
                itemsNarrativos.addEnd(item);
            }
        } else if (item != null) {
            inventario.addEnd(item);
        }
    }

    /**
     * Elimina un item del inventario del jugador.
     *
     * @param item item que se elimina
     */
    public void removeItem(Item item) {
        if (item != null && item.getTipo() == ItemType.NARRATIVE) {
            itemsNarrativos.del(item);
        } else if (item != null) {
            inventario.del(item);
        }
    }

    /**
     * Usa o equipa un item del inventario.
     *
     * <p>La lógica concreta depende de la subclase de item: las armas y piezas
     * defensivas se equipan, los accesorios activan su ranura y las pociones se
     * consumen.</p>
     *
     * @param item item que se usa
     */
    public void equip(Item item) {
        if (item != null) {
            item.use(this);
        }
    }

    /**
     * Actualiza el arma equipada en la mano principal.
     *
     * @param weapon nueva arma equipada
     */
    public void setArmaEquipada(Weapon weapon) {
        this.armaEquipada = weapon;
    }

    /**
     * Actualiza el escudo o mano secundaria equipada.
     *
     * @param armor nuevo escudo equipado
     */
    public void setEscudoEquipado(Armor armor) {
        this.escudoEquipado = armor;
    }

    /**
     * Actualiza la armadura equipada en el torso.
     *
     * @param armor nueva armadura equipada
     */
    public void setArmaduraEquipada(Armor armor) {
        this.armaduraEquipada = armor;
    }

    /**
     * Actualiza el accesorio equipado.
     *
     * @param accessory nuevo accesorio equipado
     */
    public void setAccesorioEquipado(Accessory accessory) {
        this.accesorioEquipado = accessory;
    }

    /**
     * Calcula el ataque total del jugador.
     *
     * <p>Si hay arma equipada, el arma reemplaza el ataque base del personaje y
     * se aplica su afinidad. Después se suma el bonus de ataque del accesorio,
     * si existe.</p>
     *
     * @return ataque total actual
     */
    @Override
    public int getAtaqueTotal() {
        int ataque;
        if (armaEquipada != null) {
            ataque = armaEquipada.getDanoEfectivo(tipo);
        } else {
            ataque = getAtaqueBase();
        }
        if (accesorioEquipado != null) {
            ataque += accesorioEquipado.getBonusAtaque();
        }
        ataque += bonusAtaqueTemporal;
        return ataque;
    }

    /**
     * Calcula la defensa total del jugador.
     *
     * <p>La defensa se compone de defensa base, armadura, escudo y bonus de
     * accesorio. Los personajes jugables tienen una defensa base común para
     * suavizar el daño recibido durante las primeras zonas.</p>
     *
     * @return defensa total actual
     */
    @Override
    public int getDefensaTotal() {
        int defensa = getDefensaBase();
        if (armaduraEquipada != null) {
            defensa += armaduraEquipada.getDefensa();
        }
        if (escudoEquipado != null) {
            defensa += escudoEquipado.getDefensa();
        }
        if (accesorioEquipado != null) {
            defensa += accesorioEquipado.getBonusDef();
        }
        return defensa;
    }

    /**
     * Devuelve el movimiento efectivo del jugador.
     *
     * <p>Primero se aplica la reducción por efectos desde Unit y después se
     * suma el bonus de movimiento del accesorio, si existe.</p>
     *
     * @return movimiento efectivo actual
     */
    @Override
    public int getMovEfectivo() {
        int movimiento = super.getMovEfectivo();
        if (accesorioEquipado != null) {
            movimiento += accesorioEquipado.getBonusMov();
        }
        return movimiento;
    }

    /**
     * Devuelve el rango efectivo del jugador.
     *
     * @return rango del arma equipada, o rango base si no hay arma
     */
    @Override
    public int getRangoEfectivo() {
        if (armaEquipada != null) {
            return armaEquipada.getRango();
        }
        return getRango();
    }

    /**
     * Añade un bonus de ataque temporal al próximo ataque del jugador.
     *
     * <p>Se usa para consumibles como el Elixir de Combate. El bonus se acumula
     * si se aplican varias fuentes antes de atacar, aunque las reglas de turno
     * normales solo permiten usar un item por turno.</p>
     *
     * @param bonus puntos de ataque temporal añadidos
     */
    public void addBonusAtaqueTemporal(int bonus) {
        if (bonus > 0) {
            bonusAtaqueTemporal += bonus;
        }
    }

    /**
     * Consume cualquier bonus de ataque temporal pendiente.
     */
    public void consumirBonusAtaqueTemporal() {
        bonusAtaqueTemporal = 0;
    }

    /**
     * Reinicia las acciones usadas al comienzo de un nuevo turno del jugador.
     */
    public void resetAcciones() {
        haMovido = false;
        haRecogido = false;
        haUsadoItem = false;
        haAtacado = false;
    }

    /**
     * Indica si el jugador es inmune a un efecto por su equipo defensivo.
     *
     * @param effectType tipo de efecto consultado
     * @return true si armadura o escudo bloquean el efecto
     */
    public boolean tieneInmunidad(EffectType effectType) {
        if (effectType == null) {
            return false;
        }
        if (armaduraEquipada != null && armaduraEquipada.getInmunidad() == effectType) {
            return true;
        }
        return escudoEquipado != null && escudoEquipado.getInmunidad() == effectType;
    }

    /**
     * Añade un efecto al jugador respetando inmunidades de equipo.
     *
     * @param effect efecto que se intenta aplicar
     */
    @Override
    public void addEfecto(Effect effect) {
        if (effect == null || tieneInmunidad(effect.getTipo())) {
            return;
        }
        super.addEfecto(effect);
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el tipo de personaje elegido.
     *
     * @return personaje jugable
     */
    public CharacterType getTipo() {
        return tipo;
    }

    /**
     * Devuelve el inventario del jugador.
     *
     * @return lista de items del inventario
     */
    public ListaSimplementeEnlazada<Item> getInventario() {
        return inventario;
    }

    /**
     * Devuelve los objetos narrativos de progresión del jugador.
     *
     * @return lista separada de items narrativos
     */
    public ListaSimplementeEnlazada<Item> getItemsNarrativos() {
        return itemsNarrativos;
    }

    /**
     * Comprueba si el jugador conserva un objeto narrativo concreto.
     *
     * @param itemId identificador del objeto narrativo
     * @return true si el item está en la sección narrativa
     */
    public boolean tieneItemNarrativo(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return false;
        }
        for (int i = 0; i < itemsNarrativos.getSize(); i++) {
            Item item = itemsNarrativos.get(i);
            if (item != null && itemId.equals(item.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Devuelve el arma equipada.
     *
     * @return arma equipada, o null si no hay
     */
    public Weapon getArmaEquipada() {
        return armaEquipada;
    }

    /**
     * Devuelve el escudo equipado.
     *
     * @return escudo equipado, o null si no hay
     */
    public Armor getEscudoEquipado() {
        return escudoEquipado;
    }

    /**
     * Devuelve la armadura equipada.
     *
     * @return armadura equipada, o null si no hay
     */
    public Armor getArmaduraEquipada() {
        return armaduraEquipada;
    }

    /**
     * Devuelve el accesorio equipado.
     *
     * @return accesorio equipado, o null si no hay
     */
    public Accessory getAccesorioEquipado() {
        return accesorioEquipado;
    }

    /**
     * Devuelve el bonus de ataque temporal pendiente.
     *
     * @return bonus que se sumará al próximo ataque
     */
    public int getBonusAtaqueTemporal() {
        return bonusAtaqueTemporal;
    }

    /**
     * Indica si el jugador ya se movió este turno.
     *
     * @return true si ya usó movimiento
     */
    public boolean isHaMovido() {
        return haMovido;
    }

    /**
     * Actualiza si el jugador ya se movió este turno.
     *
     * @param haMovido nuevo estado de movimiento
     */
    public void setHaMovido(boolean haMovido) {
        this.haMovido = haMovido;
    }

    /**
     * Indica si el jugador ya recogió este turno.
     *
     * @return true si ya usó recogida
     */
    public boolean isHaRecogido() {
        return haRecogido;
    }

    /**
     * Actualiza si el jugador ya recogió este turno.
     *
     * @param haRecogido nuevo estado de recogida
     */
    public void setHaRecogido(boolean haRecogido) {
        this.haRecogido = haRecogido;
    }

    /**
     * Indica si el jugador ya usó un item este turno.
     *
     * @return true si ya usó item
     */
    public boolean isHaUsadoItem() {
        return haUsadoItem;
    }

    /**
     * Actualiza si el jugador ya usó un item este turno.
     *
     * @param haUsadoItem nuevo estado de uso de item
     */
    public void setHaUsadoItem(boolean haUsadoItem) {
        this.haUsadoItem = haUsadoItem;
    }

    /**
     * Indica si el jugador ya atacó este turno.
     *
     * @return true si ya atacó
     */
    public boolean isHaAtacado() {
        return haAtacado;
    }

    /**
     * Actualiza si el jugador ya atacó este turno.
     *
     * @param haAtacado nuevo estado de ataque
     */
    public void setHaAtacado(boolean haAtacado) {
        this.haAtacado = haAtacado;
    }

    // -- Métodos auxiliares ---------------------------------------------------

    /**
     * Devuelve el HP base asociado a un personaje.
     *
     * @param tipo personaje consultado
     * @return HP máximo inicial
     */
    private static int getHpBase(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return 75;
        }
        if (tipo == CharacterType.DORATH) {
            return 80;
        }
        return 110;
    }

    /**
     * Devuelve el ataque base asociado a un personaje.
     *
     * @param tipo personaje consultado
     * @return ataque base
     */
    private static int getAtaqueBase(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return 17;
        }
        if (tipo == CharacterType.DORATH) {
            return 19;
        }
        return 23;
    }

    /**
     * Devuelve la defensa base asociada a un personaje.
     *
     * @param tipo personaje consultado
     * @return defensa base
     */
    private static int getDefensaBase(CharacterType tipo) {
        return 3;
    }

    /**
     * Devuelve el movimiento base asociado a un personaje.
     *
     * @param tipo personaje consultado
     * @return movimiento base
     */
    private static int getMovBase(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return 5;
        }
        if (tipo == CharacterType.DORATH) {
            return 2;
        }
        return 3;
    }

    /**
     * Devuelve el rango base asociado a un personaje.
     *
     * @param tipo personaje consultado
     * @return rango base
     */
    private static int getRangoBase(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return 3;
        }
        if (tipo == CharacterType.DORATH) {
            return 4;
        }
        return 1;
    }

    // -- Comparación ----------------------------------------------------------

    /**
     * Compara jugadores por personaje y posición.
     *
     * <p>Normalmente solo existe un jugador por partida, pero este método
     * permite almacenarlo en estructuras propias si una lógica auxiliar lo
     * necesitara.</p>
     *
     * @param other jugador con el que se compara
     * @return resultado de comparar tipo y posición
     */
    @Override
    public int compareTo(Player other) {
        if (other == null) {
            return 1;
        }
        int resultado = tipo.compareTo(other.tipo);
        if (resultado != 0) {
            return resultado;
        }
        if (getFilaActual() != other.getFilaActual()) {
            return getFilaActual() - other.getFilaActual();
        }
        return getColActual() - other.getColActual();
    }
}
