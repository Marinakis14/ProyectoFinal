package Valdris.model.enums;

/**
 * Define las categorías de objetos que puede manejar el inventario del jugador.
 *
 * <p>El tipo de item determina cómo se usa en la fase {@link Phase#USE_ITEM},
 * en qué ranura de equipo puede colocarse y si se consume o permanece equipado.
 * La lógica concreta vive en las subclases de {@code Item}.</p>
 *
 * <p>El diseño del juego separa los objetos de combate de los objetos de
 * progresión narrativa. Por ejemplo, algunas llaves y fragmentos son
 * accesorios, pero su función principal es desbloquear rutas o salas secretas.</p>
 *
 * @see Valdris.model.items.Item
 * @see Valdris.model.units.Player
 */
public enum ItemType {

    /**
     * Arma equipable en la mano principal.
     * Define el daño efectivo y normalmente también el rango de ataque del
     * jugador mientras está equipada.
     */
    WEAPON,

    /**
     * Armadura equipable en el torso.
     * Aporta defensa pasiva y, en algunos casos, inmunidad o protección contra
     * efectos concretos.
     */
    ARMOR,

    /**
     * Escudo equipable en la mano secundaria.
     * Funciona como defensa adicional y puede bloquear o mitigar efectos de
     * estado si la ficha del item lo indica.
     */
    SHIELD,

    /**
     * Accesorio equipable en la ranura de accesorio.
     * Puede dar bonificaciones de combate o actuar como objeto narrativo de
     * progresión, como llaves, semillas o fragmentos.
     */
    ACCESSORY,

    /**
     * Poción consumible.
     * Se usa desde el inventario para curar, limpiar efectos o aplicar una
     * mejora puntual, y después debe retirarse del inventario.
     */
    POTION
}
