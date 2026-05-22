package Valdris.model.map;

import Valdris.model.enums.CharacterType;
import Valdris.model.items.Weapon;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Chest}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Verifica que el cofre hereda correctamente las reglas de contenedor.</p>
 */
class ChestTest {

    // -- Fixture -------------------------------------------------------------

    private Chest chest;
    private Player jugador;
    private Weapon arma;

    @BeforeEach
    void setUp() {
        chest = new Chest("CH1", "Cofre antiguo");
        jugador = new Player(CharacterType.SYRA);
        arma = new Weapon("W5", "Punal del Errante", 14, 0, 1);
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_creaCofreCerradoYVacio() {
        assertEquals("CH1", chest.getId());
        assertEquals("Cofre antiguo", chest.getNombre());
        assertFalse(chest.isAbierto());
        assertTrue(chest.isVacio());
    }

    // -- Herencia ------------------------------------------------------------

    @Test
    void chest_esUnContainer() {
        assertInstanceOf(Container.class, chest);
    }

    // -- Abrir ---------------------------------------------------------------

    @Test
    void abrir_cofreEntregaContenidoAlJugador() {
        // Arrange
        chest.addItem(arma);

        // Act
        chest.abrir(jugador);

        // Assert
        assertTrue(chest.isAbierto());
        assertTrue(chest.isVacio());
        assertEquals(1, jugador.getInventario().getSize());
        assertSame(arma, jugador.getInventario().get(0));
    }
}
