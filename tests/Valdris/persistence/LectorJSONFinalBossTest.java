package Valdris.persistence;

import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.turn.TurnManager;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.GameResult;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.MalacharAlly;
import Valdris.model.units.ParasitoEnemy;
import Valdris.model.units.Player;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de persistencia para el estado especial del combate final.
 *
 * <p>Patrón: Arrange -> Act -> Assert. Verifica que Malachar, el Parásito y el
 * resultado de partida sobreviven a guardado y carga JSON.</p>
 */
class LectorJSONFinalBossTest {

    @Test
    void guardarYCargar_reconstruyeCombateFinal() throws Exception {
        Dungeon dungeon = new Dungeon();
        Room room = new Room("S5-D", "El Núcleo", 10, 11);
        dungeon.addRoom(room);
        dungeon.setRoomActual(room);
        Player player = new Player(CharacterType.DORATH);
        player.setPosicion(7, 4);
        room.getCell(7, 4).setUnit(player);
        TurnManager turnManager = new TurnManager(dungeon, player);
        turnManager.onRoomEnter();
        turnManager.iniciarCombateFinal();

        MalacharAlly malachar = room.getAllyNpc();
        malachar.recibirDanio(50);
        ParasitoEnemy parasito = buscarParasito(room);
        parasito.setPhase(2);
        parasito.setHp(88);
        parasito.setAoeCooldown(2);
        turnManager.setFinalCombatStarted(true);
        turnManager.setGameResult(GameResult.IN_PROGRESS);

        File archivo = archivoTemporal("final-boss.json");
        LectorJSON.guardarPartida(dungeon, player, turnManager, archivo.getPath());

        LoadedGame loaded = LectorJSON.cargarPartida(archivo.getPath());
        Room loadedRoom = loaded.getDungeon().getRoomById("S5-D");
        MalacharAlly loadedMalachar = loadedRoom.getAllyNpc();
        ParasitoEnemy loadedParasito = buscarParasito(loadedRoom);

        assertTrue(loaded.getTurnManager().isFinalCombatStarted());
        assertEquals(GameResult.IN_PROGRESS, loaded.getTurnManager().getGameResult());
        assertNotNull(loadedMalachar);
        assertEquals(1, loadedMalachar.getHp());
        assertEquals(2, loadedMalachar.getTurnosRecuperacion());
        assertNotNull(loadedParasito);
        assertEquals(2, loadedParasito.getPhase());
        assertEquals(88, loadedParasito.getHp());
        assertEquals(2, loadedParasito.getAoeCooldown());
    }

    @Test
    void extraerGameSummary_incluyeResultadoYTextosFinales()
        throws GameStateException, InvalidMoveException {

        Dungeon dungeon = new Dungeon();
        Room room = new Room("S5-D", "El Núcleo", 10, 11);
        dungeon.addRoom(room);
        dungeon.setRoomActual(room);
        Player player = new Player(CharacterType.SYRA);
        player.setPosicion(7, 4);
        room.getCell(7, 4).setUnit(player);
        TurnManager turnManager = new TurnManager(dungeon, player);
        turnManager.onRoomEnter();
        turnManager.triggerEnding(CharacterType.SYRA);

        GameSummary summary = LectorJSON.extraerGameSummary(dungeon, player, turnManager);

        assertEquals("VICTORY", summary.gameResult);
        assertNotNull(summary.endingText);
        assertNotNull(summary.finalQuote);
        assertNull(summary.defeatReason);
    }

    private ParasitoEnemy buscarParasito(Room room) {
        for (int i = 0; i < room.getEnemigos().getSize(); i++) {
            Enemy enemy = room.getEnemigos().get(i);
            if (enemy instanceof ParasitoEnemy) {
                return (ParasitoEnemy) enemy;
            }
        }
        return null;
    }

    private File archivoTemporal(String nombre) {
        File directorio = new File("target/persistence-tests");
        directorio.mkdirs();
        File archivo = new File(directorio, System.nanoTime() + "-" + nombre);
        archivo.deleteOnExit();
        return archivo;
    }
}
