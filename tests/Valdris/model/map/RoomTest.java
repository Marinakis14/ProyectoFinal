package Valdris.model.map;

import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.model.enums.CellType;
import Valdris.model.enums.EnemyType;
import Valdris.model.units.Enemy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Room}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class RoomTest {

    // -- Fixture -------------------------------------------------------------

    private Room room;
    private Enemy enemigo;

    @BeforeEach
    void setUp() {
        room = new Room("R1", "Sala de prueba", 3, 4);
        enemigo = new Enemy(EnemyType.WARRIOR, 1, 2, "R1");
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_inicializaDatosBasicos() {
        assertEquals("R1", room.getId());
        assertEquals("Sala de prueba", room.getNombre());
        assertEquals(3, room.getFilas());
        assertEquals(4, room.getCols());
        assertEquals(0, room.getEnemigos().getSize());
        assertFalse(room.hasRoomTimer());
        assertEquals(-1, room.getTurnosRestantes());
        assertFalse(room.isExplorada());
        assertEquals(0, room.getFilaJugador());
        assertEquals(0, room.getColJugador());
    }

    @Test
    void constructor_inicializaTodasLasCeldasComoSuelo() throws InvalidMoveException {
        for (int fila = 0; fila < room.getFilas(); fila++) {
            for (int col = 0; col < room.getCols(); col++) {
                assertEquals(CellType.FLOOR, room.getCell(fila, col).getTipo());
            }
        }
    }

    // -- Celdas --------------------------------------------------------------

    @Test
    void getCell_posicionValidaDevuelveCelda() throws InvalidMoveException {
        assertNotNull(room.getCell(1, 2));
    }

    @Test
    void getCell_posicionFueraDeRangoLanzaInvalidMoveException() {
        assertThrows(InvalidMoveException.class, () -> room.getCell(-1, 0));
        assertThrows(InvalidMoveException.class, () -> room.getCell(0, 4));
    }

    @Test
    void setCellType_actualizaTipoDeCelda() throws InvalidMoveException {
        // Act
        room.setCellType(0, 1, CellType.WALL);

        // Assert
        assertEquals(CellType.WALL, room.getCell(0, 1).getTipo());
    }

    @Test
    void isEnRango_detectaLimitesDeLaSala() {
        assertTrue(room.isEnRango(0, 0));
        assertTrue(room.isEnRango(2, 3));
        assertFalse(room.isEnRango(-1, 0));
        assertFalse(room.isEnRango(3, 0));
        assertFalse(room.isEnRango(0, 4));
    }

    // -- Enemigos ------------------------------------------------------------

    @Test
    void addEnemigo_agregaEnemigoYLoColocaEnSuCelda() throws InvalidMoveException {
        // Act
        room.addEnemigo(enemigo);

        // Assert
        assertEquals(1, room.getEnemigos().getSize());
        assertSame(enemigo, room.getCell(1, 2).getUnit());
    }

    @Test
    void addEnemigo_noDuplicaEnemigoEquivalente() {
        // Arrange
        Enemy equivalente = new Enemy(EnemyType.WARRIOR, 1, 2, "R1");

        // Act
        room.addEnemigo(enemigo);
        room.addEnemigo(equivalente);

        // Assert
        assertEquals(1, room.getEnemigos().getSize());
    }

    @Test
    void addEnemigo_nullNoModificaLista() {
        // Act
        room.addEnemigo(null);

        // Assert
        assertEquals(0, room.getEnemigos().getSize());
    }

    @Test
    void removeEnemigo_eliminaEnemigoYLimpiaCelda() throws InvalidMoveException {
        // Arrange
        room.addEnemigo(enemigo);

        // Act
        room.removeEnemigo(enemigo);

        // Assert
        assertEquals(0, room.getEnemigos().getSize());
        assertNull(room.getCell(1, 2).getUnit());
    }

    @Test
    void removeEnemigo_nullNoLanzaExcepcion() {
        assertDoesNotThrow(() -> room.removeEnemigo(null));
    }

    // -- Temporizador --------------------------------------------------------

    @Test
    void decrementarTimer_sinTimerNoModificaEstado() {
        assertDoesNotThrow(() -> room.decrementarTimer());
        assertFalse(room.hasRoomTimer());
        assertEquals(-1, room.getTurnosRestantes());
    }

    @Test
    void decrementarTimer_conTimerReduceTurnos() throws GameStateException {
        // Arrange
        room.setTurnosRestantes(2);

        // Act
        room.decrementarTimer();

        // Assert
        assertEquals(1, room.getTurnosRestantes());
    }

    @Test
    void decrementarTimer_alLlegarACeroLanzaGameStateException() {
        // Arrange
        room.setTurnosRestantes(1);

        // Act + Assert
        assertThrows(GameStateException.class, () -> room.decrementarTimer());
    }

    @Test
    void setHasRoomTimer_falseReiniciaTurnosRestantes() {
        // Arrange
        room.setTurnosRestantes(5);

        // Act
        room.setHasRoomTimer(false);

        // Assert
        assertFalse(room.hasRoomTimer());
        assertEquals(-1, room.getTurnosRestantes());
    }

    // -- Celda libre cercana -------------------------------------------------

    @Test
    void getCeldaLibreCercana_devuelveCeldaActualSiEstaLibre() throws InvalidMoveException {
        // Act
        Cell resultado = room.getCeldaLibreCercana(1, 1);

        // Assert
        assertSame(room.getCell(1, 1), resultado);
    }

    @Test
    void getCeldaLibreCercana_saltaCeldaBloqueadaYOcupada() throws InvalidMoveException {
        // Arrange
        room.setCellType(1, 1, CellType.WALL);
        room.getCell(0, 0).setUnit(enemigo);

        // Act
        Cell resultado = room.getCeldaLibreCercana(1, 1);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isWalkable());
        assertNotSame(room.getCell(1, 1), resultado);
        assertNotSame(room.getCell(0, 0), resultado);
    }

    @Test
    void getCeldaLibreCercana_devuelveNullSiNoHayCeldasTransitables() throws InvalidMoveException {
        // Arrange
        for (int fila = 0; fila < room.getFilas(); fila++) {
            for (int col = 0; col < room.getCols(); col++) {
                room.setCellType(fila, col, CellType.WALL);
            }
        }

        // Act
        Cell resultado = room.getCeldaLibreCercana(1, 1);

        // Assert
        assertNull(resultado);
    }

    // -- Getters y setters ---------------------------------------------------

    @Test
    void setters_actualizanExploradaYPosicionDelJugador() {
        // Act
        room.setExplorada(true);
        room.setFilaJugador(2);
        room.setColJugador(3);

        // Assert
        assertTrue(room.isExplorada());
        assertEquals(2, room.getFilaJugador());
        assertEquals(3, room.getColJugador());
    }

    // -- compareTo -----------------------------------------------------------

    @Test
    void compareTo_comparaPorId() {
        // Arrange
        Room misma = new Room("R1", "Otra sala", 2, 2);
        Room posterior = new Room("R2", "Sala posterior", 2, 2);

        // Act + Assert
        assertEquals(0, room.compareTo(misma));
        assertTrue(room.compareTo(posterior) < 0);
        assertTrue(room.compareTo(null) > 0);
    }
}
