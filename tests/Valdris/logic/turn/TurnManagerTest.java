package Valdris.logic.turn;

import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidAttackException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.model.enums.CellType;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EnemyType;
import Valdris.model.enums.Phase;
import Valdris.model.items.Accessory;
import Valdris.model.items.Weapon;
import Valdris.model.map.Chest;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
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
    void testCederTurno_vaAENEMY_TURN() {
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
    void ejecutarUsoItem_nullEquivaleASaltarUsoItem() throws GameStateException {
        // Arrange
        turnManager.saltarMovimiento();
        turnManager.saltarRecogida();

        // Act
        turnManager.ejecutarUsoItem(null);

        // Assert
        assertTrue(player.isHaUsadoItem());
        assertEquals(Phase.ATTACK, turnManager.getFaseActual());
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
    void ejecutarTurnoEnemigos_timerAgotadoLanzaGameStateException() {
        // Arrange
        room.setTurnosRestantes(1);
        turnManager.cederTurno();

        // Act + Assert
        assertThrows(GameStateException.class, () -> turnManager.ejecutarTurnoEnemigos());
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
}
