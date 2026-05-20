package Valdris.model.enums;

/**
 * Define los personajes jugables que pueden seleccionarse al inicio de la partida.
 *
 * <p>Valdris se juega con un único personaje por partida. La elección inicial
 * fija los puntos de vida máximos, ataque base, movimiento, rango base y
 * afinidades de armas durante toda la partida.</p>
 *
 * <p>Cualquier personaje puede recoger cualquier arma, pero las afinidades
 * hacen que algunas armas rindan mejor o peor según el personaje elegido.</p>
 *
 * @see Valdris.model.units.Player
 * @see Valdris.model.items.Weapon
 */
public enum CharacterType {

    /**
     * Kael, guerrero resistente de corto alcance.
     * HP 110, ataque base 18, movimiento 3 y rango 1. Empieza con la Espada
     * Oxidada y destaca como tanque cuerpo a cuerpo.
     */
    KAEL,

    /**
     * Syra, exploradora ágil de alcance medio.
     * HP 75, ataque base 12, movimiento 5 y rango 3. Empieza con el Arco de
     * Madera y compensa su menor vida con movilidad alta.
     */
    SYRA,

    /**
     * Dorath, mago de largo alcance.
     * HP 80, ataque base 14, movimiento 2 y rango 4. Empieza con el Bastón
     * Astillado y se apoya en alcance y penetración mágica.
     */
    DORATH
}
