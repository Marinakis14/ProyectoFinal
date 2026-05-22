package Valdris.model.units;

import Valdris.exceptions.InvalidMoveException;
import Valdris.model.enums.EnemyType;
import Valdris.model.items.Weapon;
import Valdris.model.map.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Enemy}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class EnemyTest {

    // -- Fixture -------------------------------------------------------------

    private Enemy enemigo;

    @BeforeEach
    void setUp() {
        enemigo = new Enemy(EnemyType.WARRIOR, 1, 2, "R1");
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_inicializaDatosComunes() {
        assertEquals(EnemyType.WARRIOR, enemigo.getTipo());
        assertEquals("R1", enemigo.getIdSala());
        assertEquals(1, enemigo.getFilaActual());
        assertEquals(2, enemigo.getColActual());
        assertEquals(1, enemigo.getFilaSpawn());
        assertEquals(2, enemigo.getColSpawn());
        assertNull(enemigo.getDropItem());
        assertFalse(enemigo.isMiniJefe());
    }

    @Test
    void constructor_inicializaStatsDeTodosLosTipos() {
        assertStats(EnemyType.WARRIOR, 35, 15, 8, 2, 1);
        assertStats(EnemyType.BERSERKER, 25, 13, 3, 4, 1);
        assertStats(EnemyType.GUARDIAN, 50, 15, 10, 1, 1);
        assertStats(EnemyType.ARCHER, 28, 10, 4, 3, 4);
        assertStats(EnemyType.SNIPER, 28, 18, 3, 2, 5);
        assertStats(EnemyType.DESTRUCTOR, 40, 6, 5, 0, 5);
        assertStats(EnemyType.CONTROLLER, 35, 4, 4, 2, 3);
        assertStats(EnemyType.SUMMONER, 45, 0, 6, 2, 0);
        assertStats(EnemyType.CONSTRUCTO, 45, 17, 10, 2, 1);
        assertStats(EnemyType.SOMBRA_ABSORBIDA, 45, 20, 8, 2, 1);
        assertStats(EnemyType.ECO_DE_MAGIA, 35, 22, 5, 2, 3);
    }

    // -- Drop ----------------------------------------------------------------

    @Test
    void onDeath_colocaDropEnCeldaActual() throws InvalidMoveException {
        // Arrange
        Room room = new Room("R1", "Sala de prueba", 3, 3);
        Weapon drop = new Weapon("W9", "Botin", 5, 0, 1);
        enemigo.setDropItem(drop);

        // Act
        enemigo.onDeath(room);

        // Assert
        assertSame(drop, room.getCell(1, 2).getItem());
    }

    @Test
    void onDeath_sinDropNoModificaCelda() throws InvalidMoveException {
        // Arrange
        Room room = new Room("R1", "Sala de prueba", 3, 3);

        // Act
        enemigo.onDeath(room);

        // Assert
        assertNull(room.getCell(1, 2).getItem());
    }

    @Test
    void onDeath_roomNullNoLanzaExcepcion() {
        assertDoesNotThrow(() -> enemigo.onDeath(null));
    }

    // -- Cooldown ------------------------------------------------------------

    @Test
    void cooldown_incrementaReiniciaYCompruebaUmbral() {
        // Act + Assert
        enemigo.incrementarCooldown();
        enemigo.incrementarCooldown();
        assertEquals(2, enemigo.getTurnosSinActuar());
        assertTrue(enemigo.isCooldownListo(2));

        enemigo.resetCooldown();
        assertEquals(0, enemigo.getTurnosSinActuar());
        assertFalse(enemigo.isCooldownListo(1));
    }

    @Test
    void setTurnosSinActuar_noPermiteValoresNegativos() {
        // Act
        enemigo.setTurnosSinActuar(-3);

        // Assert
        assertEquals(0, enemigo.getTurnosSinActuar());
    }

    // -- Getters y setters ---------------------------------------------------

    @Test
    void setters_actualizanDropYMiniJefe() {
        // Arrange
        Weapon drop = new Weapon("W8", "Reliquia", 7, 0, 1);

        // Act
        enemigo.setDropItem(drop);
        enemigo.setMiniJefe(true);

        // Assert
        assertSame(drop, enemigo.getDropItem());
        assertTrue(enemigo.isMiniJefe());
    }

    @Test
    void getDanoBase_devuelveAtaqueBase() {
        assertEquals(enemigo.getAtaqueBase(), enemigo.getDanoBase());
    }

    @Test
    void getPenetracionDefensa_soloEcoDeMagiaIgnoraDefensa() {
        // Arrange
        Enemy eco = new Enemy(EnemyType.ECO_DE_MAGIA, 0, 0, "R1");

        // Act + Assert
        assertEquals(0, enemigo.getPenetracionDefensa());
        assertEquals(3, eco.getPenetracionDefensa());
    }

    // -- compareTo -----------------------------------------------------------

    @Test
    void compareTo_comparaPorSalaPosicionYTipo() {
        // Arrange
        Enemy mismaCelda = new Enemy(EnemyType.WARRIOR, 1, 2, "R1");
        Enemy otraSala = new Enemy(EnemyType.WARRIOR, 1, 2, "R2");
        Enemy otraFila = new Enemy(EnemyType.WARRIOR, 2, 2, "R1");

        // Act + Assert
        assertEquals(0, enemigo.compareTo(mismaCelda));
        assertTrue(enemigo.compareTo(otraSala) < 0);
        assertTrue(enemigo.compareTo(otraFila) < 0);
        assertTrue(enemigo.compareTo(null) > 0);
    }

    // -- Helpers -------------------------------------------------------------

    private void assertStats(EnemyType tipo, int hp, int ataque, int defensa, int mov, int rango) {
        // Arrange + Act
        Enemy enemy = new Enemy(tipo, 0, 0, "R1");

        // Assert
        assertEquals(hp, enemy.getHpMax());
        assertEquals(ataque, enemy.getAtaqueBase());
        assertEquals(defensa, enemy.getDefensaBase());
        assertEquals(mov, enemy.getMovBase());
        assertEquals(rango, enemy.getRango());
    }
}
