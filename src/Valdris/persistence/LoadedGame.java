package Valdris.persistence;

import Valdris.logic.turn.TurnManager;
import Valdris.model.map.Dungeon;
import Valdris.model.units.Player;

/**
 * Resultado tipado de cargar una partida guardada.
 *
 * <p>Sustituye al {@code Object[]} propuesto por la guía para evitar casts
 * frágiles desde la capa superior. Contiene los tres objetos necesarios para
 * continuar la partida.</p>
 */
public class LoadedGame {

    // -- Atributos ------------------------------------------------------------

    /** Dungeon reconstruido. */
    private final Dungeon dungeon;

    /** Jugador reconstruido. */
    private final Player player;

    /** Gestor de turnos reconstruido. */
    private final TurnManager turnManager;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea el resultado de carga.
     *
     * @param dungeon dungeon reconstruido
     * @param player jugador reconstruido
     * @param turnManager gestor de turnos reconstruido
     */
    public LoadedGame(Dungeon dungeon, Player player, TurnManager turnManager) {
        this.dungeon = dungeon;
        this.player = player;
        this.turnManager = turnManager;
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el dungeon reconstruido.
     *
     * @return dungeon
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * Devuelve el jugador reconstruido.
     *
     * @return jugador
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Devuelve el gestor de turnos reconstruido.
     *
     * @return turn manager
     */
    public TurnManager getTurnManager() {
        return turnManager;
    }
}
