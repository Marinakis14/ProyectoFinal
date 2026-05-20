package Valdris.model.items;

import Valdris.model.enums.CharacterType;
import Valdris.model.enums.ItemType;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Accessory}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class AccessoryTest {

    // -- Fixture -------------------------------------------------------------

    private Accessory accesorio;
    private Player jugador;

    @BeforeEach
    void setUp() {
        accesorio = new Accessory("AC1", "Amuleto de Prueba");
        jugador = new Player(CharacterType.KAEL);
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_inicializaValoresPorDefecto() {
        assertEquals(ItemType.ACCESSORY, accesorio.getTipo());
        assertEquals(0, accesorio.getBonusAtaque());
        assertEquals(0, accesorio.getBonusMov());
        assertEquals(0, accesorio.getBonusDef());
        assertFalse(accesorio.isNarrativo());
        assertNull(accesorio.getEfectoNarrativo());
    }

    // -- use -----------------------------------------------------------------

    @Test
    void use_equipaAccesorioEnJugador() {
        // Act
        accesorio.use(jugador);

        // Assert
        assertSame(accesorio, jugador.getAccesorioEquipado());
    }

    @Test
    void use_nullNoLanzaExcepcion() {
        assertDoesNotThrow(() -> accesorio.use(null));
    }

    // -- Bonus ---------------------------------------------------------------

    @Test
    void setBonus_actualizaTodosLosBonus() {
        // Act
        accesorio.setBonus(2, 1, 3);

        // Assert
        assertEquals(2, accesorio.getBonusAtaque());
        assertEquals(1, accesorio.getBonusMov());
        assertEquals(3, accesorio.getBonusDef());
    }

    // -- Efecto narrativo ----------------------------------------------------

    @Test
    void setEfectoNarrativo_marcaAccesorioComoNarrativo() {
        // Act
        accesorio.setEfectoNarrativo("Abre una puerta antigua");

        // Assert
        assertTrue(accesorio.isNarrativo());
        assertEquals("Abre una puerta antigua", accesorio.getEfectoNarrativo());
    }
}
