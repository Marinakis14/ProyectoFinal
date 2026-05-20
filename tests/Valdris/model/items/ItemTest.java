package Valdris.model.items;

import Valdris.model.enums.ItemType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el comportamiento comun heredado de {@link Item}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase base.</p>
 */
class ItemTest {

    // -- Fixture -------------------------------------------------------------

    private Weapon espada;
    private Weapon arco;

    @BeforeEach
    void setUp() {
        espada = new Weapon("W1", "Espada Oxidada", 8, 0, 1);
        arco = new Weapon("W2", "Arco de Madera", 7, 0, 3);
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_inicializaDatosComunes() {
        assertEquals("W1", espada.getId());
        assertEquals("Espada Oxidada", espada.getNombre());
        assertEquals(ItemType.WEAPON, espada.getTipo());
        assertEquals("Arma equipable: Espada Oxidada", espada.getDescripcion());
    }

    // -- compareTo -----------------------------------------------------------

    @Test
    void compareTo_ordenaPorId() {
        assertTrue(espada.compareTo(arco) < 0);
        assertTrue(arco.compareTo(espada) > 0);
    }

    @Test
    void compareTo_mismoIdDevuelveCero() {
        // Arrange
        Weapon copia = new Weapon("W1", "Otra Espada", 9, 1, 1);

        // Act + Assert
        assertEquals(0, espada.compareTo(copia));
    }

    @Test
    void compareTo_nullDevuelvePositivo() {
        assertTrue(espada.compareTo(null) > 0);
    }

    // -- toString ------------------------------------------------------------

    @Test
    void toString_formatoCorrecto() {
        assertEquals("[WEAPON] Espada Oxidada", espada.toString());
    }
}
