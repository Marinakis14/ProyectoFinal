package Valdris.logic.turn;

import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidAttackException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.GameResult;
import Valdris.model.enums.Phase;
import Valdris.model.items.Weapon;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.MalacharAlly;
import Valdris.model.units.ParasitoEnemy;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del flujo de combate final gestionado por {@link TurnManager}.
 *
 * <p>Patrón: Arrange -> Act -> Assert. Cubre aparición de Malachar, inicio del
 * combate final, transición a fase 3 y victoria.</p>
 */
class TurnManagerFinalBossTest {

    private Dungeon dungeon;
    private Room room;
    private Player player;
    private TurnManager turnManager;

    @BeforeEach
    void setUp() throws InvalidMoveException {
        dungeon = new Dungeon();
        room = new Room("S5-D", "El Núcleo", 10, 11);
        dungeon.addRoom(room);
        dungeon.setRoomActual(room);
        player = new Player(CharacterType.KAEL);
        player.setPosicion(7, 4);
        room.getCell(7, 4).setUnit(player);
        turnManager = new TurnManager(dungeon, player);
    }

    @Test
    void onRoomEnter_colocaMalacharComoAliadoNoEnemigo() throws Exception {
        turnManager.onRoomEnter();

        MalacharAlly malachar = room.getAllyNpc();
        assertNotNull(malachar);
        assertSame(malachar, room.getCell(7, 3).getUnit());
        assertEquals(0, room.getEnemigos().getSize());
    }

    @Test
    void iniciarCombateFinal_requiereAdyacenciaYCreaParasito() throws Exception {
        turnManager.onRoomEnter();

        turnManager.iniciarCombateFinal();

        assertTrue(turnManager.isFinalCombatStarted());
        assertEquals(Phase.MOVEMENT, turnManager.getFaseActual());
        ParasitoEnemy parasito = buscarParasito();
        assertNotNull(parasito);
        assertEquals(5, parasito.getFilaActual());
        assertEquals(8, parasito.getColActual());
    }

    @Test
    void ataqueJugadorQueActivaFaseTresEjecutaDevorarLuz() throws Exception {
        turnManager.onRoomEnter();
        turnManager.iniciarCombateFinal();
        ParasitoEnemy parasito = buscarParasito();
        parasito.setPhase(2);
        parasito.setHp(61);
        player.equip(new Weapon("W-FINAL", "Arco final", 100, 0, 8));
        turnManager.setFaseActual(Phase.ATTACK);
        int hpInicial = player.getHp();

        turnManager.ejecutarAtaque(parasito);

        assertEquals(3, parasito.getPhase());
        assertTrue(parasito.isDevorarLuzUsado());
        assertTrue(player.tieneEfecto(EffectType.BLIND));
        assertEquals(hpInicial - 20, player.getHp());
        assertEquals(GameResult.IN_PROGRESS, turnManager.getGameResult());
    }

    @Test
    void derrotarParasitoEnFaseTresActivaVictoriaSacrificio() throws Exception {
        turnManager.onRoomEnter();
        turnManager.iniciarCombateFinal();
        ParasitoEnemy parasito = buscarParasito();
        parasito.setPhase(3);
        parasito.setHp(1);
        parasito.setDevorarLuzUsado(true);
        player.equip(new Weapon("W-FINAL", "Arco final", 100, 0, 8));
        turnManager.setFaseActual(Phase.ATTACK);

        turnManager.ejecutarAtaque(parasito);

        assertEquals(GameResult.VICTORY, turnManager.getGameResult());
        assertNotNull(turnManager.getEndingText());
        assertNotNull(turnManager.getFinalQuote());
        assertEquals(0, player.getHp());
        assertEquals(0, room.getAllyNpc().getHp());
    }

    private ParasitoEnemy buscarParasito() {
        for (int i = 0; i < room.getEnemigos().getSize(); i++) {
            Enemy enemy = room.getEnemigos().get(i);
            if (enemy instanceof ParasitoEnemy) {
                return (ParasitoEnemy) enemy;
            }
        }
        return null;
    }
}
