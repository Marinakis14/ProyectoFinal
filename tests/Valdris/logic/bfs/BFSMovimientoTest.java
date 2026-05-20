package Valdris.logic.bfs;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.InvalidMoveException;
import Valdris.model.enums.CellType;
import Valdris.model.enums.EnemyType;
import Valdris.model.map.Cell;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link BFSMovimiento}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class BFSMovimientoTest {

    // -- Fixture -------------------------------------------------------------

    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room("R-BFS", "Sala BFS", 5, 5);
    }

    // -- getCellsInRange -----------------------------------------------------

    @Test
    void getCellsInRange_movimientoCeroDevuelveListaVacia() {
        // Act
        ListaSimplementeEnlazada<Cell> resultado = BFSMovimiento.getCellsInRange(room, 2, 2, 0);

        // Assert
        assertEquals(0, resultado.getSize());
    }

    @Test
    void getCellsInRange_origenFueraDeRangoDevuelveListaVacia() {
        // Act
        ListaSimplementeEnlazada<Cell> resultado = BFSMovimiento.getCellsInRange(room, -1, 2, 3);

        // Assert
        assertEquals(0, resultado.getSize());
    }

    @Test
    void getCellsInRange_movimientoUnoDevuelveCuatroCeldasOrtogonales() throws InvalidMoveException {
        // Act
        ListaSimplementeEnlazada<Cell> resultado = BFSMovimiento.getCellsInRange(room, 2, 2, 1);

        // Assert
        assertEquals(4, resultado.getSize());
        assertTrue(contieneCelda(resultado, room.getCell(1, 2)));
        assertTrue(contieneCelda(resultado, room.getCell(3, 2)));
        assertTrue(contieneCelda(resultado, room.getCell(2, 1)));
        assertTrue(contieneCelda(resultado, room.getCell(2, 3)));
        assertFalse(contieneCelda(resultado, room.getCell(2, 2)));
    }

    @Test
    void getCellsInRange_noAtraviesaParedes() throws InvalidMoveException {
        // Arrange
        room.setCellType(2, 3, CellType.WALL);

        // Act
        ListaSimplementeEnlazada<Cell> resultado = BFSMovimiento.getCellsInRange(room, 2, 2, 1);

        // Assert
        assertEquals(3, resultado.getSize());
        assertFalse(contieneCelda(resultado, room.getCell(2, 3)));
    }

    @Test
    void getCellsInRange_noIncluyeCeldasOcupadas() throws InvalidMoveException {
        // Arrange
        Enemy enemy = new Enemy(EnemyType.WARRIOR, 2, 3, "R-BFS");
        room.getCell(2, 3).setUnit(enemy);

        // Act
        ListaSimplementeEnlazada<Cell> resultado = BFSMovimiento.getCellsInRange(room, 2, 2, 1);

        // Assert
        assertEquals(3, resultado.getSize());
        assertFalse(contieneCelda(resultado, room.getCell(2, 3)));
    }

    @Test
    void getCellsInRange_respetaLimitesDeLaSala() throws InvalidMoveException {
        // Act
        ListaSimplementeEnlazada<Cell> resultado = BFSMovimiento.getCellsInRange(room, 0, 0, 1);

        // Assert
        assertEquals(2, resultado.getSize());
        assertTrue(contieneCelda(resultado, room.getCell(1, 0)));
        assertTrue(contieneCelda(resultado, room.getCell(0, 1)));
    }

    @Test
    void getCellsInRange_movimientoDosIncluyeDistanciaDos() throws InvalidMoveException {
        // Act
        ListaSimplementeEnlazada<Cell> resultado = BFSMovimiento.getCellsInRange(room, 2, 2, 2);

        // Assert
        assertEquals(12, resultado.getSize());
        assertTrue(contieneCelda(resultado, room.getCell(0, 2)));
        assertTrue(contieneCelda(resultado, room.getCell(4, 2)));
        assertTrue(contieneCelda(resultado, room.getCell(2, 0)));
        assertTrue(contieneCelda(resultado, room.getCell(2, 4)));
    }

    @Test
    void getCellsInRange_nullDevuelveListaVacia() {
        // Act
        ListaSimplementeEnlazada<Cell> resultado = BFSMovimiento.getCellsInRange(null, 0, 0, 3);

        // Assert
        assertEquals(0, resultado.getSize());
    }

    // -- getCamino -----------------------------------------------------------

    @Test
    void getCamino_origenIgualDestinoDevuelveUnaCelda() throws InvalidMoveException {
        // Act
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(room, 2, 2, 2, 2);

        // Assert
        assertEquals(1, camino.getSize());
        assertSame(room.getCell(2, 2), camino.get(0));
    }

    @Test
    void getCamino_destinoFueraDeRangoDevuelveListaVacia() {
        // Act
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(room, 0, 0, 9, 9);

        // Assert
        assertEquals(0, camino.getSize());
    }

    @Test
    void getCamino_caminoRectoIncluyeOrigenYDestino() throws InvalidMoveException {
        // Act
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(room, 0, 0, 0, 3);

        // Assert
        assertEquals(4, camino.getSize());
        assertSame(room.getCell(0, 0), camino.get(0));
        assertSame(room.getCell(0, 1), camino.get(1));
        assertSame(room.getCell(0, 2), camino.get(2));
        assertSame(room.getCell(0, 3), camino.get(3));
    }

    @Test
    void getCamino_rodeaObstaculo() throws InvalidMoveException {
        // Arrange
        room.setCellType(0, 1, CellType.WALL);

        // Act
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(room, 0, 0, 0, 2);

        // Assert
        assertEquals(5, camino.getSize());
        assertSame(room.getCell(0, 0), camino.get(0));
        assertSame(room.getCell(0, 2), camino.get(camino.getSize() - 1));
        assertFalse(contieneCelda(camino, room.getCell(0, 1)));
    }

    @Test
    void getCamino_destinoBloqueadoDevuelveListaVacia() throws InvalidMoveException {
        // Arrange
        room.setCellType(0, 2, CellType.WALL);

        // Act
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(room, 0, 0, 0, 2);

        // Assert
        assertEquals(0, camino.getSize());
    }

    @Test
    void getCamino_destinoOcupadoDevuelveListaVacia() throws InvalidMoveException {
        // Arrange
        Enemy enemy = new Enemy(EnemyType.WARRIOR, 0, 2, "R-BFS");
        room.getCell(0, 2).setUnit(enemy);

        // Act
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(room, 0, 0, 0, 2);

        // Assert
        assertEquals(0, camino.getSize());
    }

    @Test
    void getCamino_sinRutaDevuelveListaVacia() throws InvalidMoveException {
        // Arrange
        room.setCellType(0, 1, CellType.WALL);
        room.setCellType(1, 0, CellType.WALL);

        // Act
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(room, 0, 0, 4, 4);

        // Assert
        assertEquals(0, camino.getSize());
    }

    @Test
    void getCamino_nullDevuelveListaVacia() {
        // Act
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(null, 0, 0, 1, 1);

        // Assert
        assertEquals(0, camino.getSize());
    }

    // -- Helpers -------------------------------------------------------------

    private boolean contieneCelda(ListaSimplementeEnlazada<Cell> celdas, Cell celda) {
        for (int i = 0; i < celdas.getSize(); i++) {
            if (celdas.get(i) == celda) {
                return true;
            }
        }
        return false;
    }
}
