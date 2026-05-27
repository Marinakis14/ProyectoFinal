package Valdris.persistence;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.bfs.BFSMovimiento;
import Valdris.logic.generation.DungeonGenerator;
import Valdris.model.enums.CellType;
import Valdris.model.enums.EnemyType;
import Valdris.model.enums.MiniBossType;
import Valdris.model.map.Cell;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.MiniBossEnemy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del cargador de configuracion inicial desde JSON.
 *
 * <p>Verifican que el fichero declarativo contiene el mapa completo necesario
 * para crear una partida nueva sin depender de generacion procedural.</p>
 */
class DungeonConfigLoaderTest {

    /** Dungeon cargado desde JSON. */
    private Dungeon dungeon;

    @BeforeEach
    void setUp() throws GameStateException {
        dungeon = DungeonConfigLoader.cargarConfiguracionInicial();
    }

    @Test
    void cargarConfiguracionInicial_creaSalasInicialObjetivoYNoRetorno() {
        assertEquals(34, dungeon.getGrafo().getNodos().getSize());
        assertEquals("S1-A", dungeon.getIdSalaInicial());
        assertEquals("S5-D", dungeon.getIdSalaObjetivo());
        assertEquals("S1-A", dungeon.getRoomActual().getId());
        assertRoom("S1-A", "Aldea Abandonada", 7, 8);
        assertRoom("S5-D", "El Núcleo", 10, 11);

        assertAdyacente(DungeonGenerator.PASILLO_FINAL, "S5-D");
        assertFalse(esAdyacente("S5-D", DungeonGenerator.PASILLO_FINAL));
    }

    @Test
    void cargarConfiguracionInicial_aplicaLayoutsYAccesosEspeciales() throws InvalidMoveException {
        Room s1b = dungeon.getRoomById("S1-B");
        Cell secreta = s1b.getCell(4, 3);
        assertEquals(CellType.DOOR_HIDDEN, secreta.getTipo());
        assertEquals("S1-SEC", secreta.getSalaDestino().getId());
        assertEquals("S1_SECRET", secreta.getTriggerId());
        assertFalse(secreta.isDescubierta());
        assertEquals("S1_SECRET", s1b.getSecretTarget("S1_SECRET"));

        Room s1a = dungeon.getRoomById("S1-A");
        assertEquals(CellType.WALL, s1a.getCell(2, 4).getTipo());
        assertEquals(CellType.WALL, s1a.getCell(4, 4).getTipo());

        Room s5b = dungeon.getRoomById("S5-B");
        Cell bloqueada = s5b.getCell(4, 4);
        assertEquals(CellType.DOOR_LOCKED, bloqueada.getTipo());
        assertEquals("AC3", bloqueada.getRequiredItemId());
        assertEquals("S5-SEC", bloqueada.getSalaDestino().getId());

        Room s3b = dungeon.getRoomById("S3-B");
        Cell escalera = s3b.getCell(4, 5);
        assertEquals(CellType.STAIRS_DOWN, escalera.getTipo());
        assertTrue(escalera.hasAccessFacing());
        assertEquals(1, escalera.getAccessFacingDeltaFila());
        assertEquals(0, escalera.getAccessFacingDeltaCol());
    }

    @Test
    void cargarConfiguracionInicial_configuraPuzzlesCofresEItems() throws InvalidMoveException {
        Room s1c = dungeon.getRoomById("S1-C");
        assertEquals(5, s1c.getPuzzleFailureDamage());
        assertEquals("PUZZLE_S1_C", s1c.getPuzzleSuccessTarget());
        assertPermutation3(s1c.getCorrectSequence());
        assertEquals(3, s1c.getLeverCells().getSize());

        Room s4c = dungeon.getRoomById("S4-C");
        assertPermutation3(s4c.getCorrectSequence());
        assertEquals(3, s4c.getRuneCells().getSize());

        Room s1sec = dungeon.getRoomById("S1-SEC");
        Cell cofre = s1sec.getCell(2, 2);
        assertNotNull(cofre.getContainer());
        assertEquals("CH-S1-SEC", cofre.getContainer().getId());
        assertEquals(2, cofre.getContainer().getItems().getSize());
        assertEquals("W4", cofre.getContainer().getItems().get(0).getId());
        assertEquals("W5", cofre.getContainer().getItems().get(1).getId());

        Room s4sec = dungeon.getRoomById("S4-SEC");
        Cell cofreLegendario = s4sec.getCell(2, 2);
        assertEquals(3, cofreLegendario.getContainer().getItems().getSize());
        assertEquals("W10", cofreLegendario.getContainer().getItems().get(0).getId());
        assertEquals("W11", cofreLegendario.getContainer().getItems().get(1).getId());
        assertEquals("W12", cofreLegendario.getContainer().getItems().get(2).getId());

        Room p45 = dungeon.getRoomById(DungeonGenerator.PASILLO_4_5);
        assertItemEnPool(p45.getCell(1, 4).getItem().getId(), new String[] {"P3", "P4", "P5"});
    }

    @Test
    void cargarConfiguracionInicial_configuraEnemigosAleatoriosYMiniBosses() throws InvalidMoveException {
        Room s1a = dungeon.getRoomById("S1-A");
        assertEquals(3, s1a.getEnemigos().getSize());
        Enemy primero = s1a.getEnemigos().get(0);
        assertEquals(EnemyType.WARRIOR, primero.getTipo());
        assertPosicionesEnemigosValidas(s1a);
        assertDropsNormalesValidos(s1a);

        MiniBossEnemy golem = buscarMiniBoss("S3-F");
        assertNotNull(golem);
        assertEquals(MiniBossType.GOLEM, golem.getTipoMiniBoss());
        assertEquals("AC3", golem.getDropItem().getId());

        MiniBossEnemy filtro = buscarMiniBoss("S5-C");
        assertNotNull(filtro);
        assertEquals(MiniBossType.EL_FILTRO, filtro.getTipoMiniBoss());
        assertNull(filtro.getDropItem());
    }

    @Test
    void cargarConfiguracionInicial_configuraDialogosYTemporizadores() {
        Room s2a = dungeon.getRoomById("S2-A");
        assertTrue(s2a.hasRoomTimer());
        assertEquals(20, s2a.getTurnosMaximos());
        assertTrue(s2a.hasCharacterDialogue(Valdris.model.enums.CharacterType.SYRA));

        Room s1c = dungeon.getRoomById("S1-C");
        assertFalse(s1c.hasRoomTimer());
        assertEquals(-1, s1c.getTurnosMaximos());

        Room s5d = dungeon.getRoomById("S5-D");
        assertTrue(s5d.hasRoomTimer());
        assertEquals(50, s5d.getTurnosMaximos());
    }

    @Test
    void cargarConfiguracionInicial_mantieneElementosClaveAlcanzables() throws InvalidMoveException {
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
                    } else if (esSalidaAbierta(cell)) {
                        assertTrue(tieneCeldaUsoAlcanzable(room, cell, fila, col),
                            "Salida abierta inalcanzable en " + room.getId()
                                + " (" + fila + "," + col + ")");
                    } else if (cell.getContainer() != null || cell.getTipo() == CellType.LEVER) {
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

    /**
     * Verifica una sala basica.
     */
    private void assertRoom(String id, String nombre, int filas, int cols) {
        Room room = dungeon.getRoomById(id);
        assertNotNull(room);
        assertEquals(nombre, room.getNombre());
        assertEquals(filas, room.getFilas());
        assertEquals(cols, room.getCols());
    }

    /**
     * Verifica que dos salas son adyacentes.
     */
    private void assertAdyacente(String origen, String destino) {
        assertTrue(esAdyacente(origen, destino), origen + " no conecta con " + destino);
    }

    /**
     * Indica si existe arista saliente entre dos salas.
     */
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

    /**
     * Verifica que una secuencia contiene exactamente 0, 1 y 2.
     */
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

    /**
     * Verifica que un item pertenece a un pool esperado.
     */
    private void assertItemEnPool(String itemId, String[] pool) {
        for (int i = 0; i < pool.length; i++) {
            if (pool[i].equals(itemId)) {
                return;
            }
        }
        fail("Item fuera del pool esperado: " + itemId);
    }

    /**
     * Verifica que los enemigos ocupan celdas validas y distintas.
     */
    private void assertPosicionesEnemigosValidas(Room room) throws InvalidMoveException {
        ListaSimplementeEnlazada<Enemy> enemigos = room.getEnemigos();
        for (int i = 0; i < enemigos.getSize(); i++) {
            Enemy enemy = enemigos.get(i);
            Cell cell = room.getCell(enemy.getFilaActual(), enemy.getColActual());
            assertNotEquals(CellType.WALL, cell.getTipo());
            assertFalse(cell.isAccessCell());
            assertFalse(cell.isReservedForAccess());
            assertSame(enemy, cell.getUnit());
            for (int j = i + 1; j < enemigos.getSize(); j++) {
                Enemy otro = enemigos.get(j);
                boolean mismaCelda = enemy.getFilaActual() == otro.getFilaActual()
                    && enemy.getColActual() == otro.getColActual();
                assertFalse(mismaCelda, "Enemigos duplicados en " + room.getId());
            }
        }
    }

    /**
     * Verifica drops aleatorios validos para los enemigos normales de una sala.
     */
    private void assertDropsNormalesValidos(Room room) {
        for (int i = 0; i < room.getEnemigos().getSize(); i++) {
            Enemy enemy = room.getEnemigos().get(i);
            if (!(enemy instanceof MiniBossEnemy)) {
                assertNotNull(enemy.getDropItem());
                assertTrue(esDropValido(enemy.getTipo(), enemy.getDropItem().getId()));
            }
        }
    }

    /**
     * Indica si un item pertenece al pool de drop de un tipo de enemigo.
     */
    private boolean esDropValido(EnemyType tipo, String itemId) {
        if (tipo == EnemyType.WARRIOR || tipo == EnemyType.BERSERKER) {
            return "P1".equals(itemId) || "A1".equals(itemId);
        }
        if (tipo == EnemyType.GUARDIAN) {
            return "P2".equals(itemId) || "A2".equals(itemId);
        }
        if (tipo == EnemyType.ARCHER || tipo == EnemyType.SNIPER) {
            return "P1".equals(itemId) || "AC5".equals(itemId);
        }
        if (tipo == EnemyType.DESTRUCTOR || tipo == EnemyType.CONTROLLER) {
            return "P2".equals(itemId) || "AC6".equals(itemId);
        }
        if (tipo == EnemyType.SUMMONER) {
            return "P3".equals(itemId) || "AC8".equals(itemId);
        }
        if (tipo == EnemyType.CONSTRUCTO) {
            return "P5".equals(itemId) || "A7".equals(itemId);
        }
        if (tipo == EnemyType.SOMBRA_ABSORBIDA) {
            return "P4".equals(itemId) || "P5".equals(itemId);
        }
        if (tipo == EnemyType.ECO_DE_MAGIA) {
            return "P4".equals(itemId) || "AC7".equals(itemId);
        }
        return false;
    }

    /**
     * Devuelve los IDs de todas las salas del mapa.
     */
    private String[] idsSalas() {
        return new String[] {
            "S1-A", "S1-B", "S1-C", "S1-D", "S1-SEC", DungeonGenerator.PASILLO_1_2,
            "S2-A", "S2-B", "S2-C", "S2-D", "S2-E", "S2-SEC", DungeonGenerator.PASILLO_2_3,
            "S3-A", "S3-B", "S3-C", "S3-D", "S3-E", "S3-F", "S3-SEC", DungeonGenerator.PASILLO_3_4,
            "S4-A", "S4-B", "S4-C", "S4-D", "S4-E", "S4-SEC", DungeonGenerator.PASILLO_4_5,
            "S5-A", "S5-B", "S5-C", "S5-SEC", DungeonGenerator.PASILLO_FINAL, "S5-D"
        };
    }

    /**
     * Limpia unidades para comprobar conectividad estructural.
     */
    private void limpiarUnidades(Room room) throws InvalidMoveException {
        for (int i = 0; i < room.getEnemigos().getSize(); i++) {
            Enemy enemy = room.getEnemigos().get(i);
            if (room.isEnRango(enemy.getFilaActual(), enemy.getColActual())) {
                room.getCell(enemy.getFilaActual(), enemy.getColActual()).removeUnit();
            }
        }
    }

    /**
     * Indica si una celda es una salida abierta y usable.
     */
    private boolean esSalidaAbierta(Cell cell) {
        return cell != null && cell.hasDestinoAcceso()
            && (cell.getTipo() == CellType.DOOR || cell.isStairs());
    }

    /**
     * Indica si una celda es un trigger de suelo.
     */
    private boolean esTriggerDeSuelo(Cell cell) {
        return cell != null && cell.hasTrigger() && !cell.isAccessCell()
            && cell.getTipo() != CellType.LEVER && cell.getTipo() != CellType.RUNE;
    }

    /**
     * Comprueba si existe una celda de uso alcanzable para un acceso.
     */
    private boolean tieneCeldaUsoAlcanzable(Room room, Cell acceso, int fila, int col) {
        int[][] direcciones = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
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

    /**
     * Comprueba si existe una celda adyacente alcanzable.
     */
    private boolean tieneCeldaAdyacenteAlcanzable(Room room, int fila, int col) {
        int[][] direcciones = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int i = 0; i < direcciones.length; i++) {
            int filaUso = fila + direcciones[i][0];
            int colUso = col + direcciones[i][1];
            if (room.isEnRango(filaUso, colUso) && esCeldaAlcanzable(room, filaUso, colUso)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comprueba si una celda es alcanzable desde la entrada de la sala.
     */
    private boolean esCeldaAlcanzable(Room room, int fila, int col) {
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(
            room, room.getFilaJugador(), room.getColJugador(), fila, col);
        return !camino.isEmpty();
    }

    /**
     * Busca un mini-boss en una sala.
     */
    private MiniBossEnemy buscarMiniBoss(String idSala) {
        Room room = dungeon.getRoomById(idSala);
        for (int i = 0; i < room.getEnemigos().getSize(); i++) {
            Enemy enemy = room.getEnemigos().get(i);
            if (enemy instanceof MiniBossEnemy) {
                return (MiniBossEnemy) enemy;
            }
        }
        return null;
    }
}
