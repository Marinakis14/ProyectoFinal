package Valdris.model.map;

import Valdris.model.enums.CellType;
import Valdris.model.enums.CharacterType;
import Valdris.model.items.Weapon;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Cell}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class CellTest {

    // -- Fixture -------------------------------------------------------------

    private Cell celdaSuelo;
    private Player jugador;
    private Weapon item;

    @BeforeEach
    void setUp() {
        celdaSuelo = new Cell(CellType.FLOOR);
        jugador = new Player(CharacterType.KAEL);
        item = new Weapon("W1", "Espada de prueba", 8, 0, 1);
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_sueloInicializaCeldaDescubiertaYVacia() {
        assertEquals(CellType.FLOOR, celdaSuelo.getTipo());
        assertTrue(celdaSuelo.isDescubierta());
        assertNull(celdaSuelo.getUnit());
        assertNull(celdaSuelo.getItem());
    }

    @Test
    void constructor_puertaOcultaEmpiezaSinDescubrir() {
        // Arrange + Act
        Cell puertaOculta = new Cell(CellType.DOOR_HIDDEN);

        // Assert
        assertEquals(CellType.DOOR_HIDDEN, puertaOculta.getTipo());
        assertFalse(puertaOculta.isDescubierta());
    }

    // -- isWalkable ----------------------------------------------------------

    @Test
    void isWalkable_sueloVacioEsTransitable() {
        assertTrue(celdaSuelo.isWalkable());
    }

    @Test
    void isWalkable_paredYPuertaCerradaNoSonTransitables() {
        // Arrange
        Cell pared = new Cell(CellType.WALL);
        Cell puertaCerrada = new Cell(CellType.DOOR_LOCKED);

        // Act + Assert
        assertFalse(pared.isWalkable());
        assertFalse(puertaCerrada.isWalkable());
    }

    @Test
    void isWalkable_puertaOcultaSoloEsTransitableTrasRevelarse() {
        // Arrange
        Cell puertaOculta = new Cell(CellType.DOOR_HIDDEN);

        // Act + Assert
        assertFalse(puertaOculta.isWalkable());

        puertaOculta.revelar();

        assertTrue(puertaOculta.isWalkable());
        assertEquals(CellType.DOOR, puertaOculta.getTipo());
        assertTrue(puertaOculta.isDescubierta());
    }

    @Test
    void isWalkable_celdaOcupadaNoEsTransitable() {
        // Act
        celdaSuelo.setUnit(jugador);

        // Assert
        assertFalse(celdaSuelo.isWalkable());
    }

    // -- Unit ----------------------------------------------------------------

    @Test
    void setUnit_y_removeUnit_actualizanOcupante() {
        // Act
        celdaSuelo.setUnit(jugador);

        // Assert
        assertSame(jugador, celdaSuelo.getUnit());

        // Act
        celdaSuelo.removeUnit();

        // Assert
        assertNull(celdaSuelo.getUnit());
    }

    // -- Item ----------------------------------------------------------------

    @Test
    void setItem_y_removeItem_actualizanItemDeLaCelda() {
        // Act
        celdaSuelo.setItem(item);

        // Assert
        assertSame(item, celdaSuelo.getItem());

        // Act
        Weapon retirado = (Weapon) celdaSuelo.removeItem();

        // Assert
        assertSame(item, retirado);
        assertNull(celdaSuelo.getItem());
    }

    @Test
    void removeItem_celdaSinItemDevuelveNull() {
        assertNull(celdaSuelo.removeItem());
    }

    // -- Tipo y revelado -----------------------------------------------------

    @Test
    void setTipo_puertaOcultaMarcaCeldaComoNoDescubierta() {
        // Act
        celdaSuelo.setTipo(CellType.DOOR_HIDDEN);

        // Assert
        assertEquals(CellType.DOOR_HIDDEN, celdaSuelo.getTipo());
        assertFalse(celdaSuelo.isDescubierta());
    }

    @Test
    void setTipo_tipoNoOcultoMarcaCeldaComoDescubierta() {
        // Arrange
        celdaSuelo.setTipo(CellType.DOOR_HIDDEN);

        // Act
        celdaSuelo.setTipo(CellType.TRAP);

        // Assert
        assertEquals(CellType.TRAP, celdaSuelo.getTipo());
        assertTrue(celdaSuelo.isDescubierta());
    }

    // -- compareTo -----------------------------------------------------------

    @Test
    void compareTo_celdasEquivalentesDevuelveCero() {
        // Arrange
        Cell otra = new Cell(CellType.FLOOR);

        // Act + Assert
        assertEquals(0, celdaSuelo.compareTo(otra));
    }

    @Test
    void compareTo_nullDevuelvePositivo() {
        assertTrue(celdaSuelo.compareTo(null) > 0);
    }
}
