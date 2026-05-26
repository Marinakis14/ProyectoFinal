package Valdris.logic.generation;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.bfs.BFSMovimiento;
import Valdris.model.enums.CellType;
import Valdris.model.enums.EnemyType;
import Valdris.model.enums.MiniBossType;
import Valdris.model.items.Item;
import Valdris.model.map.Cell;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.MiniBossEnemy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link DungeonGenerator}.
 *
 * <p>Patrón: Arrange -> Act -> Assert.
 * Verifica la topología oficial, salas fijas, puzzles, secretos, items y
 * colocación segura de enemigos.</p>
 */
class DungeonGeneratorTest {

    // -- Fixture -------------------------------------------------------------

    private Dungeon dungeon;

    @BeforeEach
    void setUp() {
        double[] tiradas = new double[80];
        double[] tiradasDrops = new double[160];
        dungeon = DungeonGenerator.generarMundo(tiradas, tiradas, new double[] {0.0, 0.0, 0.0, 0.4}, tiradasDrops);
    }

    // -- Salas ---------------------------------------------------------------

    @Test
    void generarMundo_creaTreintaYCuatroSalasYEmpiezaEnS1A() {
        assertEquals(34, dungeon.getGrafo().getNodos().getSize());
        assertEquals("S1-A", dungeon.getRoomActual().getId());
        assertRoom("S1-A", "Aldea Abandonada", 7, 8);
        assertRoom("S3-F", "Cámara del Golem", 10, 11);
        assertRoom(DungeonGenerator.PASILLO_FINAL, "Pasillo Final", 3, 8);
        assertRoom("S5-D", "El Núcleo", 10, 11);
    }

    @Test
    void generarMundo_configuraLimitesDeTurnoPorTipoDeSala() {
        assertTimer("S1-A", 20);
        assertSinTimer("S1-C");
        assertSinTimer("S2-C");
        assertSinTimer("S3-B");
        assertSinTimer("S4-C");
        assertTimer("S1-D", 35);
        assertTimer("S2-E", 35);
        assertTimer("S3-F", 35);
        assertTimer("S4-E", 35);
        assertTimer("S5-C", 35);
        assertTimer("S3-E", 25);
        assertSinTimer(DungeonGenerator.PASILLO_1_2);
        assertSinTimer(DungeonGenerator.PASILLO_FINAL);
        assertTimer("S5-D", 50);
    }

    @Test
    void generarMundo_conexionesPrincipalesRespetanMapaYNoRetornoFinal() {
        assertAdyacente("S1-A", "S1-B");
        assertAdyacente("S1-B", "S1-C");
        assertAdyacente("S2-B", "S2-C");
        assertAdyacente("S2-B", "S2-D");
        assertAdyacente("S3-D", "S3-E");
        assertAdyacente("S4-B", "S4-D");
        assertAdyacente("S4-B", "S4-C");
        assertAdyacente("S5-C", DungeonGenerator.PASILLO_FINAL);
        assertAdyacente(DungeonGenerator.PASILLO_FINAL, "S5-D");
        assertFalse(esAdyacente("S5-D", DungeonGenerator.PASILLO_FINAL));
    }

    @Test
    void generarMundo_registraSecretosSinActivarlosYBloqueaS5SecConFragmento() throws InvalidMoveException {
        assertNotNull(dungeon.getHiddenPassage("S1_SECRET"));
        assertNotNull(dungeon.getHiddenPassage("S2_SECRET"));
        assertNotNull(dungeon.getHiddenPassage("S3_SECRET"));
        assertNotNull(dungeon.getHiddenPassage("S4_SECRET"));
        assertFalse(dungeon.isHiddenPassageActive("S1_SECRET"));
        assertFalse(esAdyacente("S1-B", "S1-SEC"));

        Room s5b = dungeon.getRoomById("S5-B");
        Cell puerta = s5b.getCell(s5b.getFilas() - 1, s5b.getCols() / 2);
        assertEquals(CellType.DOOR_LOCKED, puerta.getTipo());
        assertEquals("AC3", puerta.getRequiredItemId());
        assertEquals("S5-SEC", puerta.getSalaDestino().getId());
    }

    // -- Puzzles -------------------------------------------------------------

    @Test
    void generarMundo_configuraPuzzlesConDanioPorZonaYSecuenciaPermutada() {
        assertPuzzle("S1-C", 5, true, false, "PUZZLE_S1_C");
        assertPuzzle("S2-C", 6, true, false, "PUZZLE_S2_C");
        assertPuzzle("S3-B", 7, true, false, "PUZZLE_S3_B");
        assertPuzzle("S4-C", 8, false, true, "PUZZLE_S4_C");
    }

    // -- Items ---------------------------------------------------------------

    @Test
    void generarMundo_colocaCofresSecretosEItemZonaCinco() throws InvalidMoveException {
        assertCofreCentral("S1-SEC", "P5");
        assertCofreCentral("S2-SEC", "A4");
        assertCofreCentral("S3-SEC", "W9");
        assertCofreCentral("S4-SEC", "N1");

        Room p45 = dungeon.getRoomById(DungeonGenerator.PASILLO_4_5);
        Item item = p45.getCell(p45.getFilas() / 2, p45.getCols() / 2).getItem();
        assertNotNull(item);
        assertEquals("P4", item.getId());

        Room pfinal = dungeon.getRoomById(DungeonGenerator.PASILLO_FINAL);
        assertNull(pfinal.getCell(pfinal.getFilas() / 2, pfinal.getCols() / 2).getItem());
    }

    // -- Enemigos ------------------------------------------------------------

    @Test
    void generarMundo_colocaEnemigosNuevosYMiniBossesConDrops() {
        assertCantidadTipo("S4-A", EnemyType.CONSTRUCTO, 2);
        assertCantidadTipo("S5-A", EnemyType.SOMBRA_ABSORBIDA, 2);
        assertCantidadTipo("S5-A", EnemyType.ECO_DE_MAGIA, 1);

        assertMiniBoss("S1-D", MiniBossType.ALCALDE_CORRUPTO, "AC1");
        assertMiniBoss("S2-E", MiniBossType.ESPIRITU_MADRE, "AC2");
        assertMiniBoss("S3-F", MiniBossType.GOLEM, "AC3");
        assertMiniBoss("S4-E", MiniBossType.GUARDIAN_SIN_NOMBRE, "AC4");
        assertMiniBoss("S5-C", MiniBossType.EL_FILTRO, null);
    }

    @Test
    void generarMundo_asignaDropsAEnemigosNormales() {
        // Arrange
        Room room = dungeon.getRoomById("S1-A");

        // Act
        Enemy enemy = room.getEnemigos().get(0);

        // Assert
        assertNotNull(enemy.getDropItem());
        assertEquals("P1", enemy.getDropItem().getId());
    }

    @Test
    void generarMundo_noColocaDosEnemigosEnLaMismaCelda() {
        String[] ids = {
            "S1-A", "S1-B", "S1-C", "S1-D", "S1-SEC", DungeonGenerator.PASILLO_1_2,
            "S2-A", "S2-B", "S2-C", "S2-D", "S2-E", "S2-SEC", DungeonGenerator.PASILLO_2_3,
            "S3-A", "S3-B", "S3-C", "S3-D", "S3-E", "S3-F", "S3-SEC", DungeonGenerator.PASILLO_3_4,
            "S4-A", "S4-B", "S4-C", "S4-D", "S4-E", "S4-SEC", DungeonGenerator.PASILLO_4_5,
            "S5-A", "S5-B", "S5-C", "S5-SEC", DungeonGenerator.PASILLO_FINAL, "S5-D"
        };
        for (int i = 0; i < ids.length; i++) {
            Room room = dungeon.getRoomById(ids[i]);
            ListaSimplementeEnlazada<Enemy> enemigos = room.getEnemigos();
            for (int a = 0; a < enemigos.getSize(); a++) {
                for (int b = a + 1; b < enemigos.getSize(); b++) {
                    Enemy primero = enemigos.get(a);
                    Enemy segundo = enemigos.get(b);
                    boolean mismaCelda = primero.getFilaActual() == segundo.getFilaActual()
                        && primero.getColActual() == segundo.getColActual();
                    assertFalse(mismaCelda, "Enemigos duplicados en " + room.getId());
                }
            }
        }
    }

    // -- Conectividad estructural ------------------------------------------

    @Test
    void generarMundo_mantieneLlegadasDeAccesosTransitables() throws InvalidMoveException {
        String[] ids = idsSalas();
        for (int i = 0; i < ids.length; i++) {
            Room room = dungeon.getRoomById(ids[i]);
            limpiarUnidades(room);
            for (int fila = 0; fila < room.getFilas(); fila++) {
                for (int col = 0; col < room.getCols(); col++) {
                    Cell cell = room.getCell(fila, col);
                    if (cell.isReservedForAccess()) {
                        assertTrue(cell.isWalkable(), "Llegada bloqueada en " + room.getId()
                            + " (" + fila + "," + col + ")");
                    }
                }
            }
        }
    }

    @Test
    void generarMundo_mantieneSalidasAbiertasAlcanzablesDesdeLaEntrada() throws InvalidMoveException {
        String[] ids = idsSalas();
        for (int i = 0; i < ids.length; i++) {
            Room room = dungeon.getRoomById(ids[i]);
            limpiarUnidades(room);
            for (int fila = 0; fila < room.getFilas(); fila++) {
                for (int col = 0; col < room.getCols(); col++) {
                    Cell cell = room.getCell(fila, col);
                    if (esSalidaAbierta(cell)) {
                        assertTrue(tieneCeldaUsoAlcanzable(room, cell, fila, col),
                            "Salida abierta inalcanzable en " + room.getId()
                                + " (" + fila + "," + col + ")");
                    }
                }
            }
        }
    }

    @Test
    void generarMundo_mantieneCofresPuzzlesYTriggersAlcanzables() throws InvalidMoveException {
        String[] ids = idsSalas();
        for (int i = 0; i < ids.length; i++) {
            Room room = dungeon.getRoomById(ids[i]);
            limpiarUnidades(room);
            for (int fila = 0; fila < room.getFilas(); fila++) {
                for (int col = 0; col < room.getCols(); col++) {
                    Cell cell = room.getCell(fila, col);
                    if (cell.getContainer() != null || cell.getTipo() == CellType.LEVER) {
                        assertTrue(tieneCeldaAdyacenteAlcanzable(room, fila, col),
                            "Interaccion adyacente inalcanzable en " + room.getId()
                                + " (" + fila + "," + col + ")");
                    } else if (cell.getTipo() == CellType.RUNE || esTriggerDeSuelo(cell)) {
                        assertTrue(esCeldaAlcanzable(room, fila, col),
                            "Objetivo de suelo inalcanzable en " + room.getId()
                                + " (" + fila + "," + col + ")");
                    }
                }
            }
        }
    }

    // -- Helpers -------------------------------------------------------------

    private String[] idsSalas() {
        return new String[] {
            "S1-A", "S1-B", "S1-C", "S1-D", "S1-SEC", DungeonGenerator.PASILLO_1_2,
            "S2-A", "S2-B", "S2-C", "S2-D", "S2-E", "S2-SEC", DungeonGenerator.PASILLO_2_3,
            "S3-A", "S3-B", "S3-C", "S3-D", "S3-E", "S3-F", "S3-SEC", DungeonGenerator.PASILLO_3_4,
            "S4-A", "S4-B", "S4-C", "S4-D", "S4-E", "S4-SEC", DungeonGenerator.PASILLO_4_5,
            "S5-A", "S5-B", "S5-C", "S5-SEC", DungeonGenerator.PASILLO_FINAL, "S5-D"
        };
    }

    private void assertRoom(String id, String nombre, int filas, int cols) {
        Room room = dungeon.getRoomById(id);
        assertNotNull(room);
        assertEquals(nombre, room.getNombre());
        assertEquals(filas, room.getFilas());
        assertEquals(cols, room.getCols());
    }

    private void assertTimer(String id, int turnos) {
        Room room = dungeon.getRoomById(id);
        assertNotNull(room);
        assertTrue(room.hasRoomTimer(), id + " deberia tener limite");
        assertEquals(turnos, room.getTurnosMaximos());
        assertEquals(turnos, room.getTurnosRestantes());
    }

    private void assertSinTimer(String id) {
        Room room = dungeon.getRoomById(id);
        assertNotNull(room);
        assertFalse(room.hasRoomTimer(), id + " no deberia tener limite");
        assertEquals(-1, room.getTurnosMaximos());
        assertEquals(-1, room.getTurnosRestantes());
    }

    private void assertAdyacente(String origen, String destino) {
        assertTrue(esAdyacente(origen, destino), origen + " no conecta con " + destino);
    }

    private boolean esAdyacente(String origen, String destino) {
        Room room = dungeon.getRoomById(origen);
        ListaSimplementeEnlazada<Room> adyacentes = dungeon.getSalasAdyacentes(room);
        for (int i = 0; i < adyacentes.getSize(); i++) {
            if (destino.equals(adyacentes.get(i).getId())) {
                return true;
            }
        }
        return false;
    }

    private void assertPuzzle(String id, int danio, boolean palancas, boolean runas, String target) {
        Room room = dungeon.getRoomById(id);
        assertEquals(danio, room.getPuzzleFailureDamage());
        assertEquals(target, room.getPuzzleSuccessTarget());
        assertEquals(palancas ? 3 : 0, room.getLeverCells().getSize());
        assertEquals(runas ? 3 : 0, room.getRuneCells().getSize());
        assertPermutation3(room.getCorrectSequence());
    }

    private void assertPermutation3(int[] sequence) {
        assertEquals(3, sequence.length);
        boolean[] vistos = new boolean[3];
        for (int i = 0; i < sequence.length; i++) {
            assertTrue(sequence[i] >= 0 && sequence[i] < 3);
            vistos[sequence[i]] = true;
        }
        assertTrue(vistos[0]);
        assertTrue(vistos[1]);
        assertTrue(vistos[2]);
    }

    private void assertCofreCentral(String idSala, String itemId) throws InvalidMoveException {
        Room room = dungeon.getRoomById(idSala);
        Cell cell = room.getCell(room.getFilas() / 2, room.getCols() / 2);
        assertNotNull(cell.getContainer());
        assertEquals(1, cell.getContainer().getItems().getSize());
        assertEquals(itemId, cell.getContainer().getItems().get(0).getId());
    }

    private void assertCantidadTipo(String idSala, EnemyType tipo, int cantidad) {
        Room room = dungeon.getRoomById(idSala);
        int encontrados = 0;
        for (int i = 0; i < room.getEnemigos().getSize(); i++) {
            Enemy enemy = room.getEnemigos().get(i);
            if (!(enemy instanceof MiniBossEnemy) && enemy.getTipo() == tipo) {
                encontrados++;
            }
        }
        assertEquals(cantidad, encontrados);
    }

    private void assertMiniBoss(String idSala, MiniBossType tipo, String dropId) {
        Room room = dungeon.getRoomById(idSala);
        MiniBossEnemy boss = null;
        for (int i = 0; i < room.getEnemigos().getSize(); i++) {
            Enemy enemy = room.getEnemigos().get(i);
            if (enemy instanceof MiniBossEnemy) {
                boss = (MiniBossEnemy) enemy;
            }
        }
        assertNotNull(boss);
        assertEquals(tipo, boss.getTipoMiniBoss());
        if (dropId == null) {
            assertNull(boss.getDropItem());
        } else {
            assertNotNull(boss.getDropItem());
            assertEquals(dropId, boss.getDropItem().getId());
        }
    }

    private void limpiarUnidades(Room room) throws InvalidMoveException {
        for (int i = 0; i < room.getEnemigos().getSize(); i++) {
            Enemy enemy = room.getEnemigos().get(i);
            if (room.isEnRango(enemy.getFilaActual(), enemy.getColActual())) {
                room.getCell(enemy.getFilaActual(), enemy.getColActual()).removeUnit();
            }
        }
    }

    private boolean esSalidaAbierta(Cell cell) {
        return cell != null && cell.hasDestinoAcceso()
            && (cell.getTipo() == CellType.DOOR || cell.isStairs());
    }

    private boolean esTriggerDeSuelo(Cell cell) {
        return cell != null && cell.hasTrigger() && !cell.isAccessCell()
            && cell.getTipo() != CellType.LEVER && cell.getTipo() != CellType.RUNE;
    }

    private boolean tieneCeldaUsoAlcanzable(Room room, Cell acceso, int fila, int col) {
        int[][] direcciones = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
        };
        for (int i = 0; i < direcciones.length; i++) {
            int filaUso = fila + direcciones[i][0];
            int colUso = col + direcciones[i][1];
            if (room.isEnRango(filaUso, colUso)
                && acceso.isUsableFrom(filaUso, colUso, fila, col)
                && esCeldaAlcanzable(room, filaUso, colUso)) {
                return true;
            }
        }
        return false;
    }

    private boolean tieneCeldaAdyacenteAlcanzable(Room room, int fila, int col) {
        int[][] direcciones = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
        };
        for (int i = 0; i < direcciones.length; i++) {
            int filaUso = fila + direcciones[i][0];
            int colUso = col + direcciones[i][1];
            if (room.isEnRango(filaUso, colUso) && esCeldaAlcanzable(room, filaUso, colUso)) {
                return true;
            }
        }
        return false;
    }

    private boolean esCeldaAlcanzable(Room room, int fila, int col) {
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(
            room, room.getFilaJugador(), room.getColJugador(), fila, col);
        return !camino.isEmpty();
    }
}
