package Valdris.logic.ai;

import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.ai.ArbolDecisionIA.AccionIA;
import Valdris.model.enums.CellType;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EnemyType;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link ArbolDecisionIA}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica una decision observable del arbol.</p>
 */
class ArbolDecisionIATest {

    // -- Fixture -------------------------------------------------------------

    private Room room;
    private Player player;

    @BeforeEach
    void setUp() {
        room = new Room("S1-A", "Sala de prueba", 7, 7);
        player = new Player(CharacterType.KAEL);
    }

    // -- Warrior y Berserker -------------------------------------------------

    @Test
    void warrior_enRangoDevuelveAtacar() {
        // Arrange
        Enemy warrior = crearEnemy(EnemyType.WARRIOR, 3, 3);
        player.setPosicion(3, 4);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.WARRIOR);

        // Act
        AccionIA accion = arbol.decidirAccion(warrior, room, player);

        // Assert
        assertEquals(AccionIA.ATACAR, accion);
    }

    @Test
    void berserker_fueraDeRangoConMovimientoDevuelveMover() {
        // Arrange
        Enemy berserker = crearEnemy(EnemyType.BERSERKER, 1, 1);
        player.setPosicion(5, 5);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.BERSERKER);

        // Act
        AccionIA accion = arbol.decidirAccion(berserker, room, player);

        // Assert
        assertEquals(AccionIA.MOVER, accion);
    }

    // -- Guardian ------------------------------------------------------------

    @Test
    void guardian_jugadorDentroDeZonaYEnRangoDevuelveAtacar() {
        // Arrange
        Enemy guardian = crearEnemy(EnemyType.GUARDIAN, 2, 2);
        player.setPosicion(2, 3);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.GUARDIAN);

        // Act
        AccionIA accion = arbol.decidirAccion(guardian, room, player);

        // Assert
        assertEquals(AccionIA.ATACAR, accion);
    }

    @Test
    void guardian_jugadorFueraDeZonaDevuelveEsperar() {
        // Arrange
        Enemy guardian = crearEnemy(EnemyType.GUARDIAN, 0, 0);
        guardian.setPosicion(5, 5);
        player.setPosicion(5, 6);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.GUARDIAN);

        // Act
        AccionIA accion = arbol.decidirAccion(guardian, room, player);

        // Assert
        assertEquals(AccionIA.ESPERAR, accion);
    }

    // -- Archer y Sniper -----------------------------------------------------

    @Test
    void archer_conVisionYEnRangoDevuelveAtacar() {
        // Arrange
        Enemy archer = crearEnemy(EnemyType.ARCHER, 3, 1);
        player.setPosicion(3, 5);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.ARCHER);

        // Act
        AccionIA accion = arbol.decidirAccion(archer, room, player);

        // Assert
        assertEquals(AccionIA.ATACAR, accion);
    }

    @Test
    void sniper_sinVisionPeroConMovimientoDevuelveMoverAZona() throws InvalidMoveException {
        // Arrange
        Enemy sniper = crearEnemy(EnemyType.SNIPER, 3, 1);
        player.setPosicion(3, 5);
        room.setCellType(3, 3, CellType.WALL);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.SNIPER);

        // Act
        AccionIA accion = arbol.decidirAccion(sniper, room, player);

        // Assert
        assertEquals(AccionIA.MOVER_A_ZONA, accion);
    }

    // -- Destructor ----------------------------------------------------------

    @Test
    void destructor_jugadorEnRadioDosManhattanDevuelveAOE() {
        // Arrange
        Enemy destructor = crearEnemy(EnemyType.DESTRUCTOR, 3, 3);
        player.setPosicion(4, 4);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.DESTRUCTOR);

        // Act
        AccionIA accion = arbol.decidirAccion(destructor, room, player);

        // Assert
        assertEquals(AccionIA.AOE, accion);
    }

    @Test
    void destructor_jugadorFueraDeRadioDosManhattanDevuelveEsperar() {
        // Arrange
        Enemy destructor = crearEnemy(EnemyType.DESTRUCTOR, 3, 3);
        player.setPosicion(5, 4);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.DESTRUCTOR);

        // Act
        AccionIA accion = arbol.decidirAccion(destructor, room, player);

        // Assert
        assertEquals(AccionIA.ESPERAR, accion);
    }

    // -- Controller ----------------------------------------------------------

    @Test
    void controller_conVisionYEnRangoDevuelveAplicarEfecto() {
        // Arrange
        Enemy controller = crearEnemy(EnemyType.CONTROLLER, 3, 2);
        player.setPosicion(3, 5);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.CONTROLLER);

        // Act
        AccionIA accion = arbol.decidirAccion(controller, room, player);

        // Assert
        assertEquals(AccionIA.APLICAR_EFECTO, accion);
    }

    @Test
    void controller_fueraDeRangoConMovimientoDevuelveMover() {
        // Arrange
        Enemy controller = crearEnemy(EnemyType.CONTROLLER, 1, 1);
        player.setPosicion(6, 6);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.CONTROLLER);

        // Act
        AccionIA accion = arbol.decidirAccion(controller, room, player);

        // Assert
        assertEquals(AccionIA.MOVER, accion);
    }

    // -- Summoner ------------------------------------------------------------

    @Test
    void summoner_cooldownListoDevuelveInvocar() {
        // Arrange
        Enemy summoner = crearEnemy(EnemyType.SUMMONER, 3, 3);
        summoner.setTurnosSinActuar(2);
        player.setPosicion(5, 5);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.SUMMONER);

        // Act
        AccionIA accion = arbol.decidirAccion(summoner, room, player);

        // Assert
        assertEquals(AccionIA.INVOCAR, accion);
    }

    @Test
    void summoner_cooldownNoListoConMovimientoDevuelveMover() {
        // Arrange
        Enemy summoner = crearEnemy(EnemyType.SUMMONER, 3, 3);
        summoner.setTurnosSinActuar(1);
        player.setPosicion(5, 5);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.SUMMONER);

        // Act
        AccionIA accion = arbol.decidirAccion(summoner, room, player);

        // Assert
        assertEquals(AccionIA.MOVER, accion);
    }

    // -- Casos defensivos ----------------------------------------------------

    @Test
    void decidirAccion_conDatosNulosDevuelveEsperar() {
        // Arrange
        Enemy warrior = crearEnemy(EnemyType.WARRIOR, 3, 3);
        ArbolDecisionIA arbol = new ArbolDecisionIA(EnemyType.WARRIOR);

        // Act + Assert
        assertEquals(AccionIA.ESPERAR, arbol.decidirAccion(null, room, player));
        assertEquals(AccionIA.ESPERAR, arbol.decidirAccion(warrior, null, player));
        assertEquals(AccionIA.ESPERAR, arbol.decidirAccion(warrior, room, null));
    }

    // -- Métodos auxiliares --------------------------------------------------

    /**
     * Crea y coloca un enemigo en la sala de pruebas.
     *
     * @param tipo tipo de enemigo
     * @param fila fila inicial
     * @param col columna inicial
     * @return enemigo creado
     */
    private Enemy crearEnemy(EnemyType tipo, int fila, int col) {
        Enemy enemy = new Enemy(tipo, fila, col, room.getId());
        room.addEnemigo(enemy);
        return enemy;
    }
}
