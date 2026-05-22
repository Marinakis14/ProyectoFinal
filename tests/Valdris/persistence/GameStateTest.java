package Valdris.persistence;

import Valdris.logic.generation.DungeonGenerator;
import Valdris.logic.turn.TurnManager;
import Valdris.model.enums.CharacterType;
import Valdris.model.map.Dungeon;
import Valdris.model.units.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para los DTOs de persistencia.
 *
 * <p>Patrón: Arrange -> Act -> Assert. Estas pruebas no validan la conversión
 * JSON completa, sino que fijan que los contenedores planos usados por Gson
 * exponen los campos necesarios sin referencias cíclicas del modelo.</p>
 */
class GameStateTest {

    // -- GameState -----------------------------------------------------------

    @Test
    void gameState_permiteGuardarCamposPlanosDePartida() {
        GameState state = new GameState();
        state.idRoomActual = "S1-A";
        state.tipoPersonaje = "KAEL";
        state.faseActual = "MOVEMENT";
        state.turnoGlobal = 3;
        state.itemsInventario = new String[] {"W1", "P3"};
        state.itemsNarrativos = new String[] {"AC1"};
        state.pasadizosActivos = new String[] {"S1_SECRET"};
        state.log = new String[] {"Entrada en sala S1-A."};

        assertEquals("S1-A", state.idRoomActual);
        assertEquals("KAEL", state.tipoPersonaje);
        assertEquals("MOVEMENT", state.faseActual);
        assertEquals(3, state.turnoGlobal);
        assertEquals("W1", state.itemsInventario[0]);
        assertEquals("AC1", state.itemsNarrativos[0]);
        assertEquals("S1_SECRET", state.pasadizosActivos[0]);
        assertEquals("Entrada en sala S1-A.", state.log[0]);
    }

    @Test
    void dtoInternos_representanSalaCeldaContenedorEnemigoYEfecto() {
        GameState.EffectStateDTO effect = new GameState.EffectStateDTO();
        effect.tipo = "SLOW";
        effect.turnos = 2;

        GameState.ContainerStateDTO container = new GameState.ContainerStateDTO();
        container.id = "CHEST_S1";
        container.nombre = "Cofre";
        container.abierto = true;
        container.itemsRestantes = new String[0];

        GameState.CellStateDTO cell = new GameState.CellStateDTO();
        cell.fila = 2;
        cell.col = 3;
        cell.tipo = "DOOR_LOCKED";
        cell.descubierta = true;
        cell.itemId = "P3";
        cell.container = container;

        GameState.RoomStateDTO room = new GameState.RoomStateDTO();
        room.idSala = "S1-C";
        room.explorada = true;
        room.correctSequence = new int[] {2, 0, 1};
        room.activeSequence = new int[] {2};
        room.celdas = new GameState.CellStateDTO[] {cell};

        GameState.EnemyStateDTO enemy = new GameState.EnemyStateDTO();
        enemy.idSala = "S1-D";
        enemy.tipoEnemigo = "WARRIOR";
        enemy.hp = 12;
        enemy.vivo = true;
        enemy.efectos = new GameState.EffectStateDTO[] {effect};

        assertEquals("SLOW", enemy.efectos[0].tipo);
        assertEquals(2, enemy.efectos[0].turnos);
        assertTrue(room.explorada);
        assertEquals(2, room.correctSequence[0]);
        assertEquals("DOOR_LOCKED", room.celdas[0].tipo);
        assertEquals("CHEST_S1", room.celdas[0].container.id);
        assertTrue(enemy.vivo);
    }

    // -- LoadedGame y GameSummary -------------------------------------------

    @Test
    void loadedGame_exponeDungeonJugadorYTurnManagerReconstruidos() {
        Dungeon dungeon = DungeonGenerator.generarMundo();
        Player player = new Player(CharacterType.SYRA);
        TurnManager turnManager = new TurnManager(dungeon, player);

        LoadedGame loaded = new LoadedGame(dungeon, player, turnManager);

        assertSame(dungeon, loaded.getDungeon());
        assertSame(player, loaded.getPlayer());
        assertSame(turnManager, loaded.getTurnManager());
    }

    @Test
    void gameSummary_guardaDatosFinalesYLogCompleto() {
        GameSummary summary = new GameSummary();
        summary.tipoPersonaje = "DORATH";
        summary.idRoomActual = "S5-D";
        summary.hpJugador = 44;
        summary.turnoGlobal = 18;
        summary.itemsInventario = new String[] {"W12"};
        summary.itemsNarrativos = new String[] {"AC1", "AC2", "AC3", "AC4"};
        summary.salasExploradas = new String[] {"S1-A", "S5-D"};
        summary.log = new String[] {"Ataque final."};

        assertEquals("DORATH", summary.tipoPersonaje);
        assertEquals("S5-D", summary.idRoomActual);
        assertEquals(44, summary.hpJugador);
        assertEquals(18, summary.turnoGlobal);
        assertEquals("W12", summary.itemsInventario[0]);
        assertEquals("AC4", summary.itemsNarrativos[3]);
        assertEquals("S5-D", summary.salasExploradas[1]);
        assertEquals("Ataque final.", summary.log[0]);
    }
}
