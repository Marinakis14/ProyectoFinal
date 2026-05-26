package Valdris.logic.turn;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidAttackException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.generation.DungeonGenerator;
import Valdris.model.effects.Effect;
import Valdris.model.enums.CellType;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.EnemyType;
import Valdris.model.enums.GameResult;
import Valdris.model.enums.LogEventType;
import Valdris.model.enums.MiniBossType;
import Valdris.model.enums.Phase;
import Valdris.model.items.Accessory;
import Valdris.model.items.Weapon;
import Valdris.model.map.Cell;
import Valdris.model.map.Chest;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.MiniBossEnemy;
import Valdris.model.units.ParasitoEnemy;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link TurnManager}.
 *
 * <p>Patrón: Arrange -> Act -> Assert.
 * Incluye los casos de la ficha T.9 y casos extra para las decisiones de
 * movimiento, recogida, contenedores y accesos entre salas.</p>
 */
class TurnManagerTest {

    // -- Fixture -------------------------------------------------------------

    private Dungeon dungeon;
    private Room room;
    private Player player;
    private TurnManager turnManager;

    @BeforeEach
    void setUp() throws InvalidMoveException {
        dungeon = new Dungeon();
        room = new Room("R1", "Sala inicial", 5, 5);
        dungeon.addRoom(room);
        dungeon.setRoomActual(room);

        player = new Player(CharacterType.KAEL);
        player.setPosicion(2, 2);
        room.setFilaJugador(2);
        room.setColJugador(2);
        room.getCell(2, 2).setUnit(player);

        turnManager = new TurnManager(dungeon, player);
    }

    // -- Ficha T.9 -----------------------------------------------------------

    @Test
    void testFaseInicial_MOVEMENT() {
        assertEquals(Phase.MOVEMENT, turnManager.getFaseActual());
        assertEquals(0, turnManager.getTurnoGlobal());
    }

    @Test
    void testEjecutarMovimiento_avanzaFase() throws InvalidMoveException, GameStateException {
        // Act
        turnManager.ejecutarMovimiento(2, 3);

        // Assert
        assertEquals(Phase.PICKUP, turnManager.getFaseActual());
        assertTrue(player.isHaMovido());
        assertEquals(2, player.getFilaActual());
        assertEquals(3, player.getColActual());
        assertNull(room.getCell(2, 2).getUnit());
        assertSame(player, room.getCell(2, 3).getUnit());
    }

    @Test
    void testEjecutarMovimiento_celdaInvalida() throws InvalidMoveException {
        // Arrange
        room.setCellType(2, 3, CellType.WALL);

        // Act + Assert
        assertThrows(InvalidMoveException.class, () -> turnManager.ejecutarMovimiento(2, 3));
        assertEquals(Phase.MOVEMENT, turnManager.getFaseActual());
    }

    @Test
    void ejecutarMovimiento_noAceptaSueloFueraDeRangoAunqueSeaEquivalente() throws InvalidMoveException {
        // Act + Assert
        assertThrows(InvalidMoveException.class, () -> turnManager.ejecutarMovimiento(0, 0));
        assertEquals(Phase.MOVEMENT, turnManager.getFaseActual());
        assertEquals(2, player.getFilaActual());
        assertEquals(2, player.getColActual());
        assertSame(player, room.getCell(2, 2).getUnit());
    }

    @Test
    void testCederTurno_vaAENEMY_TURN() throws GameStateException {
        // Act
        turnManager.cederTurno();

        // Assert
        assertEquals(Phase.ENEMY_TURN, turnManager.getFaseActual());
    }

    @Test
    void testEjecutarTurnoEnemigos_incrementaTurno() throws GameStateException {
        // Arrange
        turnManager.cederTurno();

        // Act
        turnManager.ejecutarTurnoEnemigos();

        // Assert
        assertEquals(1, turnManager.getTurnoGlobal());
        assertEquals(500, turnManager.getTurnoGlobalMaximo());
        assertEquals(Phase.MOVEMENT, turnManager.getFaseActual());
    }

    @Test
    void testEjecutarAtaque_fueraDeRango() throws GameStateException {
        // Arrange
        Enemy enemigo = new Enemy(EnemyType.WARRIOR, 0, 0, "R1");
        avanzarHastaAtaqueSinAcciones();

        // Act + Assert
        assertThrows(InvalidAttackException.class, () -> turnManager.ejecutarAtaque(enemigo));
        assertEquals(Phase.ATTACK, turnManager.getFaseActual());
    }

    @Test
    void testResetAcciones_trasEnemyTurn()
        throws InvalidMoveException, GameStateException {

        // Arrange
        turnManager.ejecutarMovimiento(2, 3);
        turnManager.saltarRecogida();
        turnManager.saltarUsoItem();
        turnManager.cederTurno();

        // Act
        turnManager.ejecutarTurnoEnemigos();

        // Assert
        assertFalse(player.isHaMovido());
        assertFalse(player.isHaRecogido());
        assertFalse(player.isHaUsadoItem());
        assertFalse(player.isHaAtacado());
        assertEquals(Phase.MOVEMENT, turnManager.getFaseActual());
    }

    // -- Saltos explícitos ---------------------------------------------------

    @Test
    void saltarMovimiento_avanzaAPickupSinMoverJugador() throws GameStateException {
        // Act
        turnManager.saltarMovimiento();

        // Assert
        assertEquals(Phase.PICKUP, turnManager.getFaseActual());
        assertTrue(player.isHaMovido());
        assertEquals(2, player.getFilaActual());
        assertEquals(2, player.getColActual());
    }

    @Test
    void saltarRecogida_avanzaAUseItem() throws GameStateException {
        // Arrange
        turnManager.saltarMovimiento();

        // Act
        turnManager.saltarRecogida();

        // Assert
        assertEquals(Phase.USE_ITEM, turnManager.getFaseActual());
        assertTrue(player.isHaRecogido());
    }

    @Test
    void saltarUsoItem_avanzaAAttack() throws GameStateException {
        // Arrange
        turnManager.saltarMovimiento();
        turnManager.saltarRecogida();

        // Act
        turnManager.saltarUsoItem();

        // Assert
        assertEquals(Phase.ATTACK, turnManager.getFaseActual());
        assertTrue(player.isHaUsadoItem());
    }

    @Test
    void accionFueraDeFase_lanzaGameStateException() {
        assertThrows(GameStateException.class, () -> turnManager.saltarRecogida());
        assertThrows(GameStateException.class, () -> turnManager.saltarUsoItem());
    }

    // -- Items de suelo ------------------------------------------------------

    @Test
    void ejecutarMovimiento_recogeItemDeSueloAutomaticamente()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Weapon arma = new Weapon("W4", "Espada Larga", 18, 0, 1);
        room.getCell(2, 3).setItem(arma);

        // Act
        turnManager.ejecutarMovimiento(2, 3);

        // Assert
        assertNull(room.getCell(2, 3).getItem());
        assertEquals(1, player.getInventario().getSize());
        assertSame(arma, player.getInventario().get(0));
        assertEquals(Phase.PICKUP, turnManager.getFaseActual());
        assertEquals(LogEventType.PICKUP, turnManager.getLog().get(0).getTipo());
        assertEquals(LogEventType.MOVEMENT, turnManager.getLog().get(1).getTipo());
    }

    // -- Contenedores --------------------------------------------------------

    @Test
    void ejecutarRecogida_abreCofreAdyacenteYEntregaContenido()
        throws GameStateException, InvalidMoveException {

        // Arrange
        Chest chest = new Chest("CH1", "Cofre secreto");
        Weapon arma = new Weapon("W5", "Punal del Errante", 14, 0, 1);
        chest.addItem(arma);
        room.getCell(2, 3).setContainer(chest);
        turnManager.saltarMovimiento();

        // Act
        turnManager.ejecutarRecogida();

        // Assert
        assertTrue(chest.isAbierto());
        assertTrue(chest.isVacio());
        assertEquals(1, player.getInventario().getSize());
        assertSame(arma, player.getInventario().get(0));
        assertEquals(Phase.USE_ITEM, turnManager.getFaseActual());
    }

    @Test
    void ejecutarRecogida_sinContenedorAdyacenteSoloAvanzaFase() throws GameStateException {
        // Arrange
        turnManager.saltarMovimiento();

        // Act
        turnManager.ejecutarRecogida();

        // Assert
        assertEquals(0, player.getInventario().getSize());
        assertTrue(player.isHaRecogido());
        assertEquals(Phase.USE_ITEM, turnManager.getFaseActual());
    }

    // -- Uso de item ---------------------------------------------------------

    @Test
    void ejecutarUsoItem_equipaItemYAvanzaAAtaque() throws GameStateException {
        // Arrange
        Weapon arma = new Weapon("W1", "Espada Oxidada", 16, 0, 1);
        turnManager.saltarMovimiento();
        turnManager.saltarRecogida();

        // Act
        turnManager.ejecutarUsoItem(arma);

        // Assert
        assertSame(arma, player.getArmaEquipada());
        assertTrue(player.isHaUsadoItem());
        assertEquals(Phase.ATTACK, turnManager.getFaseActual());
    }

    @Test
    void ejecutarUsoItem_nullLanzaGameStateException() throws GameStateException {
        // Arrange
        turnManager.saltarMovimiento();
        turnManager.saltarRecogida();

        // Act + Assert
        assertThrows(GameStateException.class, () -> turnManager.ejecutarUsoItem(null));
        assertFalse(player.isHaUsadoItem());
        assertEquals(Phase.USE_ITEM, turnManager.getFaseActual());
    }

    // -- Ataque --------------------------------------------------------------

    @Test
    void ejecutarAtaque_enRangoDaniaEnemigoYAvanzaAEnemyTurn()
        throws GameStateException, InvalidAttackException {

        // Arrange
        Enemy enemigo = new Enemy(EnemyType.WARRIOR, 2, 3, "R1");
        room.addEnemigo(enemigo);
        avanzarHastaAtaqueSinAcciones();
        int hpAntes = enemigo.getHp();

        // Act
        turnManager.ejecutarAtaque(enemigo);

        // Assert
        assertTrue(enemigo.getHp() < hpAntes);
        assertTrue(player.isHaAtacado());
        assertEquals(Phase.ENEMY_TURN, turnManager.getFaseActual());
        assertEquals(LogEventType.COMBAT, turnManager.getLog().get(3).getTipo());
        assertTrue(turnManager.getLog().get(3).getMensaje().contains("inflige"));
    }

    @Test
    void ejecutarAtaque_mataEnemigoLoRetiraDeSalaYLimpiaCeldaConservandoDrop()
        throws GameStateException, InvalidAttackException, InvalidMoveException {

        // Arrange
        Weapon arma = new Weapon("W-KILL", "Mandoble", 100, 0, 1);
        Weapon drop = new Weapon("W-DROP", "Botin", 5, 0, 1);
        Enemy enemigo = new Enemy(EnemyType.WARRIOR, 2, 3, "R1");
        enemigo.setHp(1);
        enemigo.setDropItem(drop);
        room.addEnemigo(enemigo);
        player.setArmaEquipada(arma);
        avanzarHastaAtaqueSinAcciones();

        // Act
        turnManager.ejecutarAtaque(enemigo);

        // Assert
        assertFalse(enemigo.isVivo());
        assertEquals(0, room.getEnemigos().getSize());
        assertNull(room.getCell(2, 3).getUnit());
        assertSame(drop, room.getCell(2, 3).getItem());
        assertTrue(existeLog(LogEventType.COMBAT, "muere"));
        assertTrue(existeLog(LogEventType.PICKUP, "deja caer"));
    }

    @Test
    void ejecutarAtaque_objetivoNullLanzaInvalidAttackException() throws GameStateException {
        // Arrange
        avanzarHastaAtaqueSinAcciones();

        // Act + Assert
        assertThrows(InvalidAttackException.class, () -> turnManager.ejecutarAtaque(null));
    }

    // -- Cambio de sala ------------------------------------------------------

    @Test
    void usarAccesoAdyacente_enPuertaConDestinoCambiaSalaYColocaJugador()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        dungeon.conectar(room, destino, "puerta este");
        destino.setFilaJugador(1);
        destino.setColJugador(1);
        room.setCellType(2, 3, CellType.DOOR);
        room.getCell(2, 3).setDestinoAcceso(destino, 1, 1);
        turnManager.saltarMovimiento();

        // Act
        turnManager.usarAccesoAdyacente();

        // Assert
        assertSame(destino, dungeon.getRoomActual());
        assertTrue(destino.isExplorada());
        assertEquals(1, player.getFilaActual());
        assertEquals(1, player.getColActual());
        assertSame(player, destino.getCell(1, 1).getUnit());
        assertNull(room.getCell(2, 2).getUnit());
        assertEquals(Phase.USE_ITEM, turnManager.getFaseActual());
    }

    @Test
    void usarAccesoAdyacente_conEnemigosVivosNoCambiaSala()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        dungeon.conectar(room, destino, "puerta este");
        room.setCellType(2, 3, CellType.DOOR);
        room.getCell(2, 3).setDestinoAcceso(destino, 1, 1);
        room.addEnemigo(new Enemy(EnemyType.WARRIOR, 1, 1, "R1"));
        turnManager.saltarMovimiento();

        // Act + Assert
        assertThrows(GameStateException.class, () -> turnManager.usarAccesoAdyacente());
        assertSame(room, dungeon.getRoomActual());
        assertEquals(Phase.PICKUP, turnManager.getFaseActual());
    }

    @Test
    void getDistanciaSalidaAbiertaMasCercana_devuelveCeroSiJugadorEstaJuntoAPuerta()
        throws InvalidMoveException {

        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        dungeon.conectar(room, destino, "puerta este");
        room.setCellType(2, 3, CellType.DOOR);
        room.getCell(2, 3).setDestinoAcceso(destino, 1, 1);

        // Act + Assert
        assertEquals(0, turnManager.getDistanciaSalidaAbiertaMasCercana());
        assertFalse(turnManager.hayEnemigosVivosSalaActual());
    }

    @Test
    void getDistanciaSalidaAbiertaMasCercana_mideHastaCeldaUsableDePuerta()
        throws InvalidMoveException {

        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        dungeon.conectar(room, destino, "puerta este");
        room.setCellType(2, 4, CellType.DOOR);
        room.getCell(2, 4).setDestinoAcceso(destino, 1, 1);

        // Act + Assert
        assertEquals(1, turnManager.getDistanciaSalidaAbiertaMasCercana());
    }

    @Test
    void getDistanciaSalidaAbiertaMasCercana_devuelveMenosUnoSiNoHaySalidaAbierta()
        throws InvalidMoveException {

        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        dungeon.conectar(room, destino, "puerta cerrada");
        room.setCellType(2, 3, CellType.DOOR_LOCKED);
        room.getCell(2, 3).setDestinoAcceso(destino, 1, 1);

        // Act + Assert
        assertEquals(-1, turnManager.getDistanciaSalidaAbiertaMasCercana());
    }

    @Test
    void getDistanciaSalidaAbiertaMasCercana_devuelveMenosUnoSiQuedanEnemigosVivos()
        throws InvalidMoveException {

        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        dungeon.conectar(room, destino, "puerta este");
        room.setCellType(2, 3, CellType.DOOR);
        room.getCell(2, 3).setDestinoAcceso(destino, 1, 1);
        room.addEnemigo(new Enemy(EnemyType.WARRIOR, 1, 1, "R1"));

        // Act + Assert
        assertTrue(turnManager.hayEnemigosVivosSalaActual());
        assertEquals(-1, turnManager.getDistanciaSalidaAbiertaMasCercana());
    }

    @Test
    void getCaminoReveladoSalaActual_eligeRutaConMenorCosteGlobal()
        throws InvalidMoveException {

        // Arrange
        Room destinoLargo = new Room("R-LARGO", "Rama larga", 5, 5);
        Room intermedia = new Room("R-MEDIA", "Intermedia", 5, 5);
        Room destinoCorto = new Room("R-CORTO", "Rama corta", 5, 5);
        Room finalRoom = new Room("S5-D", "Nucleo", 5, 5);
        dungeon.conectar(room, destinoLargo, "ruta larga");
        dungeon.conectar(destinoLargo, intermedia, "paso largo");
        dungeon.conectar(intermedia, finalRoom, "final largo");
        dungeon.conectar(room, destinoCorto, "ruta corta");
        dungeon.conectar(destinoCorto, finalRoom, "final corto");
        room.setCellType(2, 4, CellType.DOOR);
        room.getCell(2, 4).setDestinoAcceso(destinoLargo, 1, 1);
        room.setCellType(4, 2, CellType.DOOR);
        room.getCell(4, 2).setDestinoAcceso(destinoCorto, 1, 1);

        // Act
        ListaSimplementeEnlazada<Cell> camino = turnManager.getCaminoReveladoSalaActual();

        // Assert
        assertEquals("R-CORTO", turnManager.getIdSiguienteSalaObjetivoGlobal());
        assertEquals(1, turnManager.getDistanciaSalidaGlobal());
        assertEquals(2, turnManager.getSalasHastaObjetivoGlobal());
        assertSame(room.getCell(4, 2), camino.get(camino.getSize() - 1));
    }

    @Test
    void getCaminoReveladoSalaActual_desempataPorIdDeDestino()
        throws InvalidMoveException {

        // Arrange
        Room destinoA = new Room("A_DEST", "Destino A", 5, 5);
        Room destinoB = new Room("B_DEST", "Destino B", 5, 5);
        Room finalRoom = new Room("S5-D", "Nucleo", 5, 5);
        dungeon.conectar(room, destinoA, "ruta A");
        dungeon.conectar(destinoA, finalRoom, "final A");
        dungeon.conectar(room, destinoB, "ruta B");
        dungeon.conectar(destinoB, finalRoom, "final B");
        room.setCellType(2, 4, CellType.DOOR);
        room.getCell(2, 4).setDestinoAcceso(destinoB, 1, 1);
        room.setCellType(4, 2, CellType.DOOR);
        room.getCell(4, 2).setDestinoAcceso(destinoA, 1, 1);

        // Act
        ListaSimplementeEnlazada<Cell> camino = turnManager.getCaminoReveladoSalaActual();

        // Assert
        assertEquals("A_DEST", turnManager.getIdSiguienteSalaObjetivoGlobal());
        assertEquals(1, turnManager.getDistanciaSalidaGlobal());
        assertEquals(2, turnManager.getSalasHastaObjetivoGlobal());
        assertSame(room.getCell(4, 2), camino.get(camino.getSize() - 1));
    }

    @Test
    void getCaminoReveladoSalaActual_muestraRutaAunqueQuedenEnemigosVivos()
        throws InvalidMoveException {

        // Arrange
        Room finalRoom = new Room("S5-D", "Nucleo", 5, 5);
        dungeon.conectar(room, finalRoom, "ruta final");
        room.setCellType(2, 4, CellType.DOOR);
        room.getCell(2, 4).setDestinoAcceso(finalRoom, 1, 1);
        room.addEnemigo(new Enemy(EnemyType.WARRIOR, 1, 1, "R1"));

        // Act
        ListaSimplementeEnlazada<Cell> camino = turnManager.getCaminoReveladoSalaActual();

        // Assert
        assertTrue(turnManager.hayEnemigosVivosSalaActual());
        assertEquals("S5-D", turnManager.getIdSiguienteSalaObjetivoGlobal());
        assertEquals(1, turnManager.getDistanciaSalidaGlobal());
        assertFalse(camino.isEmpty());
        assertSame(room.getCell(2, 4), camino.get(camino.getSize() - 1));
    }

    @Test
    void getCaminoReveladoSalaActual_ignoraUnidadesEnRutaVisual()
        throws InvalidMoveException {

        // Arrange
        Room finalRoom = new Room("S5-D", "Nucleo", 5, 5);
        dungeon.conectar(room, finalRoom, "ruta final");
        room.setCellType(2, 4, CellType.DOOR);
        room.getCell(2, 4).setDestinoAcceso(finalRoom, 1, 1);
        room.addEnemigo(new Enemy(EnemyType.WARRIOR, 2, 3, "R1"));

        // Act
        ListaSimplementeEnlazada<Cell> camino = turnManager.getCaminoReveladoSalaActual();

        // Assert
        assertTrue(turnManager.hayEnemigosVivosSalaActual());
        assertFalse(camino.isEmpty());
        assertSame(room.getCell(2, 4), camino.get(camino.getSize() - 1));
    }

    @Test
    void getCaminoReveladoSalaActual_usaPuertaBloqueadaDeProgreso()
        throws InvalidMoveException {

        // Arrange
        Room intermedia = new Room("R-BLOQUEADA", "Puerta de puzzle", 5, 5);
        Room finalRoom = new Room("S5-D", "Nucleo", 5, 5);
        dungeon.addRoom(intermedia);
        dungeon.addRoom(finalRoom);
        dungeon.conectar(intermedia, finalRoom, "ruta tras puzzle");
        room.setCellType(2, 4, CellType.DOOR_LOCKED);
        room.getCell(2, 4).setDestinoAcceso(intermedia, 1, 1);
        room.getCell(2, 4).setTriggerId("PUZZLE_TEST");

        // Act
        ListaSimplementeEnlazada<Cell> camino = turnManager.getCaminoReveladoSalaActual();

        // Assert
        assertEquals("R-BLOQUEADA", turnManager.getIdSiguienteSalaObjetivoGlobal());
        assertEquals(1, turnManager.getDistanciaSalidaGlobal());
        assertEquals(2, turnManager.getSalasHastaObjetivoGlobal());
        assertFalse(camino.isEmpty());
        assertSame(room.getCell(2, 4), camino.get(camino.getSize() - 1));
    }

    @Test
    void getCaminoReveladoSalaActual_funcionaEnMundoGeneradoDesdeS1A()
        throws InvalidMoveException {

        // Arrange
        double[] tiradas = new double[80];
        double[] tiradasDrops = new double[160];
        Dungeon generado = DungeonGenerator.generarMundo(tiradas, tiradas, tiradas, tiradasDrops);
        Room salaInicial = generado.getRoomById("S1-A");
        Player jugador = new Player(CharacterType.KAEL);
        jugador.setPosicion(salaInicial.getFilaJugador(), salaInicial.getColJugador());
        salaInicial.getCell(jugador.getFilaActual(), jugador.getColActual()).setUnit(jugador);
        TurnManager managerGenerado = new TurnManager(generado, jugador);

        // Act
        ListaSimplementeEnlazada<Cell> camino = managerGenerado.getCaminoReveladoSalaActual();

        // Assert
        assertEquals("S1-B", managerGenerado.getIdSiguienteSalaObjetivoGlobal());
        assertTrue(managerGenerado.getSalasHastaObjetivoGlobal() > 0);
        assertFalse(camino.isEmpty());
    }

    @Test
    void getCaminoReveladoSalaActual_devuelveVacioSiNoHayRutaGlobal()
        throws InvalidMoveException {

        // Arrange
        Room destino = new Room("R-SIN-FINAL", "Sin final", 5, 5);
        dungeon.conectar(room, destino, "ruta sin final");
        room.setCellType(2, 4, CellType.DOOR);
        room.getCell(2, 4).setDestinoAcceso(destino, 1, 1);

        // Act
        ListaSimplementeEnlazada<Cell> camino = turnManager.getCaminoReveladoSalaActual();

        // Assert
        assertTrue(camino.isEmpty());
        assertNull(turnManager.getIdSiguienteSalaObjetivoGlobal());
        assertEquals(-1, turnManager.getDistanciaSalidaGlobal());
        assertEquals(-1, turnManager.getSalasHastaObjetivoGlobal());
    }

    @Test
    void ejecutarMovimiento_noPermitePisarPuerta() throws InvalidMoveException {
        // Arrange
        room.setCellType(2, 3, CellType.DOOR);

        // Act + Assert
        assertThrows(InvalidMoveException.class, () -> turnManager.ejecutarMovimiento(2, 3));
        assertSame(room, dungeon.getRoomActual());
        assertEquals(Phase.MOVEMENT, turnManager.getFaseActual());
    }

    @Test
    void usarAccesoAdyacente_enEscaleraSoloDesdeFrenteConfigurado()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room destino = new Room("R2-UP", "Planta superior", 4, 4);
        dungeon.conectar(room, destino, "escalera arriba");
        room.setCellType(2, 3, CellType.STAIRS_UP);
        room.getCell(2, 3).setAccessFacing(0, -1);
        room.getCell(2, 3).setDestinoAcceso(destino, 1, 1);
        destino.getCell(1, 1).setReservedForAccess(true);
        turnManager.saltarMovimiento();

        // Act
        turnManager.usarAccesoAdyacente();

        // Assert
        assertSame(destino, dungeon.getRoomActual());
        assertEquals(1, player.getFilaActual());
        assertEquals(1, player.getColActual());
        assertEquals(Phase.USE_ITEM, turnManager.getFaseActual());
    }

    @Test
    void usarAccesoAdyacente_escaleraDesdeLadoIncorrectoLanzaGameStateException()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room destino = new Room("R2-UP", "Planta superior", 4, 4);
        dungeon.conectar(room, destino, "escalera arriba");
        room.setCellType(2, 3, CellType.STAIRS_UP);
        room.getCell(2, 3).setAccessFacing(-1, 0);
        room.getCell(2, 3).setDestinoAcceso(destino, 1, 1);
        turnManager.saltarMovimiento();

        // Act + Assert
        assertThrows(GameStateException.class, () -> turnManager.usarAccesoAdyacente());
        assertSame(room, dungeon.getRoomActual());
        assertEquals(Phase.PICKUP, turnManager.getFaseActual());
    }

    @Test
    void usarAccesoAdyacente_destinoBloqueadoLanzaInvalidMoveException()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        dungeon.conectar(room, destino, "puerta este");
        room.setCellType(2, 3, CellType.DOOR);
        room.getCell(2, 3).setDestinoAcceso(destino, 1, 1);
        destino.setCellType(1, 1, CellType.WALL);
        turnManager.saltarMovimiento();

        // Act + Assert
        assertThrows(InvalidMoveException.class, () -> turnManager.usarAccesoAdyacente());
        assertSame(room, dungeon.getRoomActual());
        assertEquals(Phase.PICKUP, turnManager.getFaseActual());
    }

    @Test
    void usarAccesoAdyacente_puertaCerradaConLlaveSeAbreYCambiaSala()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        dungeon.conectar(room, destino, "puerta cerrada");
        room.setCellType(2, 3, CellType.DOOR_LOCKED);
        room.getCell(2, 3).setRequiredItemId("AC1");
        room.getCell(2, 3).setDestinoAcceso(destino, 1, 1);
        player.addItem(new Accessory("AC1", "Llave de Hierro"));
        turnManager.saltarMovimiento();

        // Act
        turnManager.usarAccesoAdyacente();

        // Assert
        assertEquals(CellType.DOOR, room.getCell(2, 3).getTipo());
        assertSame(destino, dungeon.getRoomActual());
        assertEquals(Phase.USE_ITEM, turnManager.getFaseActual());
    }

    @Test
    void usarAccesoAdyacente_puertaCerradaSinLlaveNoSeAbre()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        dungeon.conectar(room, destino, "puerta cerrada");
        room.setCellType(2, 3, CellType.DOOR_LOCKED);
        room.getCell(2, 3).setRequiredItemId("AC1");
        room.getCell(2, 3).setDestinoAcceso(destino, 1, 1);
        turnManager.saltarMovimiento();

        // Act + Assert
        assertThrows(GameStateException.class, () -> turnManager.usarAccesoAdyacente());
        assertEquals(CellType.DOOR_LOCKED, room.getCell(2, 3).getTipo());
        assertSame(room, dungeon.getRoomActual());
        assertEquals(Phase.PICKUP, turnManager.getFaseActual());
    }

    @Test
    void changeRoom_colocaJugadorEnEntradaConfigurada()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        dungeon.addRoom(destino);
        destino.setFilaJugador(3);
        destino.setColJugador(2);

        // Act
        turnManager.changeRoom(destino);

        // Assert
        assertSame(destino, dungeon.getRoomActual());
        assertTrue(destino.isExplorada());
        assertEquals(3, player.getFilaActual());
        assertEquals(2, player.getColActual());
        assertSame(player, destino.getCell(3, 2).getUnit());
        assertNull(room.getCell(2, 2).getUnit());
    }

    // -- Entrada de sala, triggers y log ------------------------------------

    @Test
    void changeRoom_llamaOnRoomEnterYGeneraDialogoUnaSolaVez()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        dungeon.addRoom(destino);
        destino.setFilaJugador(1);
        destino.setColJugador(1);
        destino.addCharacterDialogue(CharacterType.KAEL, "Kael reconoce la sala.");

        // Act
        turnManager.changeRoom(destino);

        // Assert
        assertEquals("Kael reconoce la sala.", turnManager.getLastDialogue());
        assertTrue(destino.wasDialogueShown(CharacterType.KAEL));
        assertEquals("Kael reconoce la sala.", turnManager.consumeLastDialogue());
        assertNull(turnManager.getLastDialogue());
        assertEquals(2, turnManager.getLog().getSize());
        assertEquals(LogEventType.ROOM, turnManager.getLog().get(0).getTipo());

        // Act
        turnManager.onRoomEnter();

        // Assert
        assertNull(turnManager.getLastDialogue());
    }

    @Test
    void addLog_guardaHistorialAcumulativo() {
        // Act
        turnManager.addLog(LogEventType.GAME, null, "Evento 1", null);
        turnManager.addLog(LogEventType.GAME, null, "Evento 2", null);
        turnManager.addLog(LogEventType.GAME, null, null, null);

        // Assert
        assertEquals(2, turnManager.getLog().getSize());
        assertEquals(LogEventType.GAME, turnManager.getLog().get(0).getTipo());
        assertEquals("Evento 1", turnManager.getLog().get(0).getMensaje());
        assertEquals("Evento 2", turnManager.getLog().get(1).getMensaje());
        assertEquals("Turno 0 | GAME | Evento 1", turnManager.getLogTextos()[0]);
    }

    @Test
    void ejecutarMovimiento_activaTriggerSecretoDeCeldaActual()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room secreta = new Room("R-SEC", "Sala secreta", 3, 3);
        dungeon.connectHidden(room, secreta, "pasadizo oculto", "secret_trigger");
        room.getCell(2, 3).setTriggerId("trigger_1");
        room.addSecretTrigger("trigger_1", "secret_trigger");

        // Act
        turnManager.ejecutarMovimiento(2, 3);

        // Assert
        assertTrue(dungeon.isHiddenPassageActive("secret_trigger"));
        assertEquals(Phase.PICKUP, turnManager.getFaseActual());
    }

    @Test
    void ejecutarMovimiento_sobreRunaRegistraSecuenciaYActivaPasadizo()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room secreta = new Room("R-RUNE", "Sala de runas", 3, 3);
        dungeon.connectHidden(room, secreta, "pasadizo de runas", "secret_rune");
        room.setCellType(2, 3, CellType.RUNE);
        room.addRuneCell(room.getCell(2, 3));
        room.setCorrectSequence(new int[] {0});
        room.setPuzzleSuccessTarget("secret_rune");

        // Act
        turnManager.ejecutarMovimiento(2, 3);

        // Assert
        assertTrue(room.isPuzzleResolved());
        assertTrue(dungeon.isHiddenPassageActive("secret_rune"));
        assertEquals(Phase.PICKUP, turnManager.getFaseActual());
    }

    @Test
    void activarPalancaAdyacente_resuelveInteraccionDePickup()
        throws InvalidMoveException, GameStateException {

        // Arrange
        Room secreta = new Room("R-LEV", "Sala de palanca", 3, 3);
        dungeon.connectHidden(room, secreta, "pasadizo de palanca", "secret_lever");
        room.setCellType(2, 3, CellType.LEVER);
        room.addLeverCell(room.getCell(2, 3));
        room.setCorrectSequence(new int[] {0});
        room.setPuzzleSuccessTarget("secret_lever");
        turnManager.saltarMovimiento();

        // Act
        turnManager.activarPalancaAdyacente();

        // Assert
        assertTrue(room.isPuzzleResolved());
        assertTrue(dungeon.isHiddenPassageActive("secret_lever"));
        assertTrue(player.isHaRecogido());
        assertEquals(Phase.USE_ITEM, turnManager.getFaseActual());
        assertTrue(existeLog(LogEventType.PUZZLE, "Combinación correcta"));
    }

    @Test
    void activarPalancaAdyacente_secuenciaIncorrectaRegistraFeedback()
        throws InvalidMoveException, GameStateException {

        // Arrange
        room.setCellType(2, 3, CellType.LEVER);
        room.setCellType(1, 3, CellType.LEVER);
        room.addLeverCell(room.getCell(2, 3));
        room.addLeverCell(room.getCell(1, 3));
        room.setCorrectSequence(new int[] {1, 0});
        room.setPuzzleFailureDamage(6);
        int hpAntes = player.getHp();
        turnManager.saltarMovimiento();

        // Act
        turnManager.activarPalancaAdyacente();
        turnManager.saltarUsoItem();
        turnManager.cederTurno();
        turnManager.ejecutarTurnoEnemigos();
        turnManager.ejecutarMovimiento(1, 2);
        turnManager.activarPalancaAdyacente();

        // Assert
        assertEquals(hpAntes - 6, player.getHp());
        assertFalse(room.isPuzzleResolved());
        assertEquals(0, room.getSecuenciaActivada().length);
        assertTrue(existeLog(LogEventType.PUZZLE, "Combinación incorrecta"));
    }

    @Test
    void activarPalancaAdyacente_muestraPistasProgresivasPorFallo()
        throws InvalidMoveException, GameStateException {

        // Arrange
        room.setCellType(2, 3, CellType.LEVER);
        room.setCellType(1, 3, CellType.LEVER);
        room.addLeverCell(room.getCell(2, 3));
        room.addLeverCell(room.getCell(1, 3));
        room.setCorrectSequence(new int[] {1, 0});
        room.setPuzzleFailureDamage(1);

        // Act
        fallarPuzzleDeDosPalancas();
        fallarPuzzleDeDosPalancas();

        // Assert
        assertEquals(2, room.getPuzzleFailureCount());
        assertTrue(existeLog(LogEventType.PUZZLE, "Pista: empieza por 2."));
        assertTrue(existeLog(LogEventType.PUZZLE, "Pista: la siguiente es la 1."));
        assertTrue(existeLog(LogEventType.PUZZLE, "Pista: la combinación correcta es: 2, 1."));
    }

    // -- Turno enemigo -------------------------------------------------------

    @Test
    void ejecutarTurnoEnemigos_decrementaTimerDeSala() throws GameStateException {
        // Arrange
        room.setTurnosRestantes(3);
        turnManager.cederTurno();

        // Act
        turnManager.ejecutarTurnoEnemigos();

        // Assert
        assertEquals(2, room.getTurnosRestantes());
    }

    @Test
    void ejecutarTurnoEnemigos_timerAgotadoActivaDerrota() throws GameStateException {
        // Arrange
        room.setTurnosRestantes(1);
        turnManager.cederTurno();

        // Act
        turnManager.ejecutarTurnoEnemigos();

        // Assert
        assertEquals(GameResult.DEFEAT, turnManager.getGameResult());
        assertTrue(turnManager.getDefeatReason().contains("limite de turnos"));
    }

    @Test
    void ejecutarTurnoEnemigos_turnoGlobalMaximoActivaDerrota() throws GameStateException {
        // Arrange
        turnManager.setTurnoGlobal(499);
        turnManager.cederTurno();

        // Act
        turnManager.ejecutarTurnoEnemigos();

        // Assert
        assertEquals(500, turnManager.getTurnoGlobal());
        assertEquals(GameResult.DEFEAT, turnManager.getGameResult());
        assertTrue(turnManager.getDefeatReason().contains("limite global"));
    }

    @Test
    void accionesPublicasJugador_lanzanGameStateExceptionSiPartidaTerminada() {
        // Arrange
        turnManager.setGameResult(GameResult.VICTORY);

        // Act + Assert
        assertThrows(GameStateException.class, () -> turnManager.ejecutarMovimiento(2, 3));
        assertThrows(GameStateException.class, () -> turnManager.saltarMovimiento());
        assertThrows(GameStateException.class, () -> turnManager.ejecutarRecogida());
        assertThrows(GameStateException.class, () -> turnManager.saltarRecogida());
        assertThrows(GameStateException.class, () -> turnManager.usarAccesoAdyacente());
        assertThrows(GameStateException.class, () -> turnManager.activarPalancaAdyacente());
        assertThrows(GameStateException.class, () -> turnManager.ejecutarUsoItem(null));
        assertThrows(GameStateException.class, () -> turnManager.saltarUsoItem());
        assertThrows(GameStateException.class, () -> turnManager.ejecutarAtaque(new Enemy(EnemyType.WARRIOR, 2, 3, "R1")));
        assertThrows(GameStateException.class, () -> turnManager.cederTurno());
        assertThrows(GameStateException.class, () -> turnManager.activarTriggerActual());
        assertThrows(GameStateException.class, () -> turnManager.activarRunaActual());
        assertThrows(GameStateException.class, () -> turnManager.ejecutarTurnoEnemigos());
        assertEquals(Phase.MOVEMENT, turnManager.getFaseActual());
    }

    @Test
    void ejecutarTurnoEnemigos_enemigoInvocadoNoActuaHastaSiguienteTurno()
        throws GameStateException {

        // Arrange
        Enemy summoner = new Enemy(EnemyType.SUMMONER, 1, 1, "R1");
        summoner.incrementarCooldown();
        summoner.incrementarCooldown();
        room.addEnemigo(summoner);
        turnManager.cederTurno();

        // Act
        turnManager.ejecutarTurnoEnemigos();

        // Assert
        assertEquals(2, room.getEnemigos().getSize());
        Enemy invocado = room.getEnemigos().get(1);
        assertEquals(EnemyType.BERSERKER, invocado.getTipo());
        assertEquals(0, invocado.getTurnosSinActuar());
    }

    @Test
    void ejecutarTurnoEnemigos_registraAccionConcretaDeEnemigo() throws GameStateException {
        // Arrange
        Enemy warrior = new Enemy(EnemyType.WARRIOR, 2, 3, "R1");
        room.addEnemigo(warrior);
        int hpInicial = player.getHp();
        turnManager.cederTurno();

        // Act
        turnManager.ejecutarTurnoEnemigos();

        // Assert
        assertTrue(player.getHp() < hpInicial);
        assertTrue(existeLog(LogEventType.ENEMY_TURN, "inflige"));
    }

    @Test
    void ejecutarTurnoEnemigos_registraDanioYExpiracionDeEfectosDelJugador() throws GameStateException {
        // Arrange
        player.addEfecto(new Effect(EffectType.BURN, 1));
        turnManager.cederTurno();

        // Act
        turnManager.ejecutarTurnoEnemigos();

        // Assert
        assertTrue(existeLog(LogEventType.STATE, "recibe 3 daño por efectos"));
        assertTrue(existeLog(LogEventType.STATE, "BURN expira"));
    }

    @Test
    void ejecutarTurnoEnemigos_enemigoMuertoPorEfectosNoSaltaAlSiguiente()
        throws GameStateException, InvalidMoveException {
        // Arrange
        Enemy quemado = new Enemy(EnemyType.WARRIOR, 1, 1, "R1");
        quemado.setHp(1);
        quemado.setDropItem(new Weapon("W-DROP", "Botin", 5, 0, 1));
        quemado.addEfecto(new Effect(EffectType.BURN, 1));
        room.addEnemigo(quemado);

        Enemy atacante = new Enemy(EnemyType.WARRIOR, 2, 3, "R1");
        room.addEnemigo(atacante);
        int hpInicial = player.getHp();
        turnManager.cederTurno();

        // Act
        turnManager.ejecutarTurnoEnemigos();

        // Assert
        assertFalse(room.getEnemigos().contains(quemado));
        assertSame(quemado.getDropItem(), room.getCell(1, 1).getItem());
        assertTrue(player.getHp() < hpInicial);
        assertTrue(existeLog(LogEventType.COMBAT, "muere por efectos"));
        assertTrue(existeLog(LogEventType.ENEMY_TURN, "Turno enemigo resuelto"));
    }

    @Test
    void ejecutarTurnoEnemigos_registraHabilidadEspecialDeMiniBoss() throws GameStateException {
        // Arrange
        MiniBossEnemy alcalde = new MiniBossEnemy(MiniBossType.ALCALDE_CORRUPTO, 2, 3, "R1");
        alcalde.setTurnosSinActuar(3);
        room.addEnemigo(alcalde);
        int hpInicial = player.getHp();
        turnManager.cederTurno();

        // Act
        turnManager.ejecutarTurnoEnemigos();

        // Assert
        assertEquals(hpInicial - 35, player.getHp());
        assertTrue(existeLog(LogEventType.ENEMY_TURN, "Estocada Corrupta"));
        assertTrue(existeLog(LogEventType.ENEMY_TURN, "Alcalde Corrupto"));
    }

    @Test
    void ejecutarTurnoEnemigos_pulsoParasitoSumaDanioPorCurse() throws GameStateException {

        // Arrange
        ParasitoEnemy parasito = new ParasitoEnemy(2, 0, "R1");
        parasito.setPhase(ParasitoEnemy.FASE_DESGARRADA);
        parasito.setAoeCooldown(2);
        room.addEnemigo(parasito);
        player.addEfecto(new Effect(EffectType.CURSE, 2));
        int hpJugadorInicial = player.getHp();
        turnManager.cederTurno();

        // Act
        turnManager.ejecutarTurnoEnemigos();

        // Assert
        assertEquals(hpJugadorInicial - 15, player.getHp());
        assertTrue(existeLog(LogEventType.ENEMY_TURN, "Pulso del Núcleo"));
    }

    // -- Métodos auxiliares --------------------------------------------------

    /**
     * Avanza hasta ATTACK usando los saltos explícitos de fases previas.
     *
     * @throws GameStateException si alguna fase no permite avanzar
     */
    private void avanzarHastaAtaqueSinAcciones() throws GameStateException {
        turnManager.saltarMovimiento();
        turnManager.saltarRecogida();
        turnManager.saltarUsoItem();
    }

    /**
     * Ejecuta una secuencia incorrecta de dos palancas y deja el turno listo para repetir.
     *
     * @throws InvalidMoveException si falla el movimiento de preparación
     * @throws GameStateException si alguna fase no permite avanzar
     */
    private void fallarPuzzleDeDosPalancas() throws InvalidMoveException, GameStateException {
        if (turnManager.getFaseActual() == Phase.MOVEMENT) {
            if (player.getFilaActual() == 2 && player.getColActual() == 2) {
                turnManager.saltarMovimiento();
            } else {
                turnManager.ejecutarMovimiento(2, 2);
            }
        }
        turnManager.activarPalancaAdyacente();
        turnManager.saltarUsoItem();
        turnManager.cederTurno();
        turnManager.ejecutarTurnoEnemigos();
        turnManager.ejecutarMovimiento(1, 2);
        turnManager.activarPalancaAdyacente();
        turnManager.saltarUsoItem();
        turnManager.cederTurno();
        turnManager.ejecutarTurnoEnemigos();
    }

    /**
     * Busca una entrada de log por tipo y fragmento de mensaje.
     *
     * @param tipo tipo esperado
     * @param fragmento fragmento del mensaje
     * @return true si existe
     */
    private boolean existeLog(LogEventType tipo, String fragmento) {
        for (int i = 0; i < turnManager.getLog().getSize(); i++) {
            if (turnManager.getLog().get(i).getTipo() == tipo
                && turnManager.getLog().get(i).getMensaje().contains(fragmento)) {
                return true;
            }
        }
        return false;
    }

}
