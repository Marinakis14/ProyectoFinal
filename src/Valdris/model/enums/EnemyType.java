package Valdris.model.enums;

/**
 * Define los tipos de enemigos normales que puede encontrar el jugador.
 *
 * <p>Los enemigos se agrupan en familias de comportamiento: guerreros,
 * arqueros y magos. Cada tipo tiene estadísticas base y una regla de IA
 * asociada que se resuelve durante la fase {@link Phase#ENEMY_TURN}.</p>
 *
 * <p>Los subtipos avanzados aparecen con más frecuencia a medida que avanza el
 * dungeon. En el Núcleo Profundo pueden combinarse todos los patrones de IA,
 * por lo que este enum sirve como punto común para generación, combate y
 * persistencia.</p>
 *
 * @see Valdris.model.units.Enemy
 * @see Valdris.logic.ai.IAEnemigo
 */
public enum EnemyType {

    /**
     * Guerrero base.
     * HP 35, ataque 15, defensa 8, movimiento 2 y rango 1. Persigue al jugador
     * usando BFS y ataca en cuerpo a cuerpo.
     */
    WARRIOR,

    /**
     * Berserker.
     * HP 25, ataque 13, defensa 3, movimiento 4 y rango 1. Presiona con carga
     * directa y también puede aparecer invocado por un Invocador.
     */
    BERSERKER,

    /**
     * Guardián.
     * HP 50, ataque 15, defensa 10, movimiento 1 y rango 1. Defiende una zona
     * fija alrededor de su posición de aparición.
     */
    GUARDIAN,

    /**
     * Arquero base.
     * HP 28, ataque 10, defensa 4, movimiento 3 y rango 4. Mantiene distancia y
     * requiere línea de visión para atacar.
     */
    ARCHER,

    /**
     * Francotirador.
     * HP 28, ataque 18, defensa 3, movimiento 2 y rango 5. Dispara con
     * cooldown de 2 turnos y usa los turnos intermedios para reposicionarse.
     */
    SNIPER,

    /**
     * Destructor.
     * HP 40, ataque 6 por celda, defensa 5, movimiento 0 y rango 5. No se
     * desplaza y ataca en área de radio 2.
     */
    DESTRUCTOR,

    /**
     * Controlador.
     * HP 35, ataque 4, defensa 4, movimiento 2 y rango 3. Aplica efectos como
     * SLOW, BLIND o CURSE para limitar las opciones del jugador.
     */
    CONTROLLER,

    /**
     * Invocador.
     * HP 45, ataque 0, defensa 6, movimiento 2 y rango 0. Invoca Berserkers
     * cada 2 turnos en celdas libres cercanas.
     */
    SUMMONER
}
