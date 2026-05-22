package Valdris.logic.puzzle;

import Valdris.exceptions.InvalidMoveException;
import Valdris.model.enums.CellType;
import Valdris.model.enums.CharacterType;
import Valdris.model.map.Cell;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link PuzzleManager}.
 *
 * <p>Patrón: Arrange -> Act -> Assert.
 * Cubre la lógica común de secuencias para palancas y runas.</p>
 */
class PuzzleManagerTest {

    // -- Fixture -------------------------------------------------------------

    private Room room;
    private Dungeon dungeon;
    private Player player;

    @BeforeEach
    void setUp() {
        room = new Room("R-PUZ", "Sala de puzzle", 4, 4);
        dungeon = new Dungeon();
        dungeon.addRoom(room);
        player = new Player(CharacterType.KAEL);
    }

    // -- Activación ----------------------------------------------------------

    @Test
    void activate_conCeldaRegistradaGuardaIndice() throws InvalidMoveException {
        // Arrange
        Cell leverA = room.getCell(0, 1);
        Cell leverB = room.getCell(0, 2);
        room.addLeverCell(leverA);
        room.addLeverCell(leverB);
        room.setCorrectSequence(new int[] {1});

        // Act
        boolean activada = PuzzleManager.activate(room, leverB);

        // Assert
        assertTrue(activada);
        assertArrayEquals(new int[] {1}, room.getSecuenciaActivada());
    }

    @Test
    void activate_porTriggerIdEncuentraRunasYPalancas() throws InvalidMoveException {
        // Arrange
        Cell rune = room.getCell(1, 1);
        rune.setTipo(CellType.RUNE);
        rune.setTriggerId("rune_0");
        room.addRuneCell(rune);
        room.setCorrectSequence(new int[] {0});

        // Act
        boolean activada = PuzzleManager.activate(room, "rune_0");

        // Assert
        assertTrue(activada);
        assertTrue(room.isSequenceComplete());
        assertTrue(PuzzleManager.checkSequence(room));
    }

    @Test
    void activate_conCeldaNoRegistradaDevuelveFalse() throws InvalidMoveException {
        assertFalse(PuzzleManager.activate(room, room.getCell(0, 0)));
        assertFalse(PuzzleManager.activate(room, "no_existe"));
    }

    // -- Resultado -----------------------------------------------------------

    @Test
    void resolverActivacion_secuenciaCorrectaActivaPasadizo() throws InvalidMoveException {
        // Arrange
        Room destino = new Room("R-SEC", "Sala secreta", 3, 3);
        Cell lever = room.getCell(0, 1);
        room.addLeverCell(lever);
        room.setCorrectSequence(new int[] {0});
        room.setPuzzleSuccessTarget("secret_puzzle");
        dungeon.connectHidden(room, destino, "pasadizo puzzle", "secret_puzzle");

        // Act
        boolean activada = PuzzleManager.resolverActivacion(room, lever, dungeon, player);

        // Assert
        assertTrue(activada);
        assertTrue(room.isPuzzleResolved());
        assertTrue(dungeon.isHiddenPassageActive("secret_puzzle"));
    }

    @Test
    void resolverActivacion_secuenciaIncorrectaDaniaJugadorYReinicia() throws InvalidMoveException {
        // Arrange
        Cell leverA = room.getCell(0, 1);
        Cell leverB = room.getCell(0, 2);
        room.addLeverCell(leverA);
        room.addLeverCell(leverB);
        room.setCorrectSequence(new int[] {1, 0});
        int hpAntes = player.getHp();

        // Act
        PuzzleManager.resolverActivacion(room, leverA, dungeon, player);
        PuzzleManager.resolverActivacion(room, leverB, dungeon, player);

        // Assert
        assertEquals(hpAntes - 3, player.getHp());
        assertFalse(room.isPuzzleResolved());
        assertEquals(0, room.getSecuenciaActivada().length);
    }

    @Test
    void applySuccess_sinDungeonMarcaPuzzleResuelto() {
        assertTrue(PuzzleManager.applySuccess(room, null));
        assertTrue(room.isPuzzleResolved());
    }

    @Test
    void resetSequence_limpiaActivaciones() throws InvalidMoveException {
        // Arrange
        Cell lever = room.getCell(0, 1);
        room.addLeverCell(lever);
        room.setCorrectSequence(new int[] {0});
        PuzzleManager.activate(room, lever);

        // Act
        PuzzleManager.resetSequence(room);

        // Assert
        assertEquals(0, room.getSecuenciaActivada().length);
    }
}
