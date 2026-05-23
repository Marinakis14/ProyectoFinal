package Valdris.logic.vision;

import Valdris.exceptions.InvalidMoveException;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.CellType;
import Valdris.model.enums.EnemyType;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link LineaDeVision}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class LineaDeVisionTest {

    // -- Fixture -------------------------------------------------------------

    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room("R-VISION", "Sala de vision", 7, 7);
    }

    // -- Casos base ----------------------------------------------------------

    @Test
    void tieneVision_mismaCeldaDevuelveTrue() {
        assertTrue(LineaDeVision.tieneVision(room, 3, 3, 3, 3));
    }

    @Test
    void tieneVision_roomNullDevuelveFalse() {
        assertFalse(LineaDeVision.tieneVision(null, 0, 0, 0, 1));
    }

    @Test
    void tieneVision_posicionFueraDeRangoDevuelveFalse() {
        assertFalse(LineaDeVision.tieneVision(room, -1, 0, 0, 1));
        assertFalse(LineaDeVision.tieneVision(room, 0, 0, 9, 9));
    }

    // -- Lineas libres -------------------------------------------------------

    @Test
    void tieneVision_horizontalLibreDevuelveTrue() {
        assertTrue(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }

    @Test
    void tieneVision_verticalLibreDevuelveTrue() {
        assertTrue(LineaDeVision.tieneVision(room, 1, 3, 5, 3));
    }

    @Test
    void tieneVision_diagonalLibreDevuelveTrue() {
        assertTrue(LineaDeVision.tieneVision(room, 1, 1, 5, 5));
    }

    @Test
    void tieneVision_lineaInclinadaLibreDevuelveTrue() {
        assertTrue(LineaDeVision.tieneVision(room, 1, 1, 4, 6));
    }

    // -- Paredes -------------------------------------------------------------

    @Test
    void tieneVision_horizontalConParedIntermediaDevuelveFalse() throws InvalidMoveException {
        // Arrange
        room.setCellType(2, 3, CellType.WALL);

        // Act + Assert
        assertFalse(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }

    @Test
    void tieneVision_verticalConParedIntermediaDevuelveFalse() throws InvalidMoveException {
        // Arrange
        room.setCellType(3, 3, CellType.WALL);

        // Act + Assert
        assertFalse(LineaDeVision.tieneVision(room, 1, 3, 5, 3));
    }

    @Test
    void tieneVision_diagonalConParedIntermediaDevuelveFalse() throws InvalidMoveException {
        // Arrange
        room.setCellType(3, 3, CellType.WALL);

        // Act + Assert
        assertFalse(LineaDeVision.tieneVision(room, 1, 1, 5, 5));
    }

    @Test
    void tieneVision_paredEnOrigenNoBloquea() throws InvalidMoveException {
        // Arrange
        room.setCellType(2, 1, CellType.WALL);

        // Act + Assert
        assertTrue(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }

    @Test
    void tieneVision_paredEnDestinoNoBloquea() throws InvalidMoveException {
        // Arrange
        room.setCellType(2, 5, CellType.WALL);

        // Act + Assert
        assertTrue(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }

    // -- Celdas especiales ---------------------------------------------------

    @Test
    void tieneVision_stairsUpIntermediaBloqueaVision() throws InvalidMoveException {
        // Arrange
        room.setCellType(2, 3, CellType.STAIRS_UP);

        // Act + Assert
        assertFalse(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }

    @Test
    void tieneVision_stairsDownIntermediaNoBloqueaVision() throws InvalidMoveException {
        // Arrange
        room.setCellType(2, 3, CellType.STAIRS_DOWN);

        // Act + Assert
        assertTrue(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }

    @Test
    void tieneVision_trampaNoBloqueaVision() throws InvalidMoveException {
        // Arrange
        room.setCellType(2, 3, CellType.TRAP);

        // Act + Assert
        assertTrue(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }

    @Test
    void tieneVision_palancaNoBloqueaVision() throws InvalidMoveException {
        // Arrange
        room.setCellType(2, 3, CellType.LEVER);

        // Act + Assert
        assertTrue(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }

    @Test
    void tieneVision_runaNoBloqueaVision() throws InvalidMoveException {
        // Arrange
        room.setCellType(2, 3, CellType.RUNE);

        // Act + Assert
        assertTrue(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }

    // -- Unidades -----------------------------------------------------------

    @Test
    void tieneVision_unidadIntermediaBloqueaVision() throws InvalidMoveException {
        // Arrange
        Enemy enemigo = new Enemy(EnemyType.WARRIOR, 2, 3, room.getId());
        room.getCell(2, 3).setUnit(enemigo);

        // Act + Assert
        assertFalse(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }

    @Test
    void tieneVision_unidadEnOrigenNoBloquea() throws InvalidMoveException {
        // Arrange
        Player jugador = new Player(CharacterType.KAEL);
        jugador.setPosicion(2, 1);
        room.getCell(2, 1).setUnit(jugador);

        // Act + Assert
        assertTrue(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }

    @Test
    void tieneVision_unidadEnDestinoNoBloquea() throws InvalidMoveException {
        // Arrange
        Enemy enemigo = new Enemy(EnemyType.WARRIOR, 2, 5, room.getId());
        room.getCell(2, 5).setUnit(enemigo);

        // Act + Assert
        assertTrue(LineaDeVision.tieneVision(room, 2, 1, 2, 5));
    }
}
