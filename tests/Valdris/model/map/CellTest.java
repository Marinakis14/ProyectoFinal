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
    private Chest chest;

    @BeforeEach
    void setUp() {
        celdaSuelo = new Cell(CellType.FLOOR);
        jugador = new Player(CharacterType.KAEL);
        item = new Weapon("W1", "Espada de prueba", 8, 0, 1);
        chest = new Chest("CH1", "Cofre de prueba");
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_sueloInicializaCeldaDescubiertaYVacia() {
        assertEquals(CellType.FLOOR, celdaSuelo.getTipo());
        assertTrue(celdaSuelo.isDescubierta());
        assertNull(celdaSuelo.getUnit());
        assertNull(celdaSuelo.getItem());
        assertNull(celdaSuelo.getContainer());
        assertFalse(celdaSuelo.hasDestinoAcceso());
        assertFalse(celdaSuelo.hasAccessFacing());
        assertFalse(celdaSuelo.hasTrigger());
        assertFalse(celdaSuelo.isHighlighted());
        assertFalse(celdaSuelo.isReservedForAccess());
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
    void isWalkable_paredPuertaYEscalerasNoSonTransitables() {
        // Arrange
        Cell pared = new Cell(CellType.WALL);
        Cell puerta = new Cell(CellType.DOOR);
        Cell puertaCerrada = new Cell(CellType.DOOR_LOCKED);
        Cell escaleraArriba = new Cell(CellType.STAIRS_UP);
        Cell escaleraAbajo = new Cell(CellType.STAIRS_DOWN);

        // Act + Assert
        assertFalse(pared.isWalkable());
        assertFalse(puerta.isWalkable());
        assertFalse(puertaCerrada.isWalkable());
        assertFalse(escaleraArriba.isWalkable());
        assertFalse(escaleraAbajo.isWalkable());
    }

    @Test
    void isWalkable_palancaNoEsTransitableYRunaSigueSiendolo() {
        // Arrange
        Cell palanca = new Cell(CellType.LEVER);
        Cell runa = new Cell(CellType.RUNE);

        // Act + Assert
        assertFalse(palanca.isWalkable());
        assertTrue(runa.isWalkable());
    }

    @Test
    void isWalkable_puertaOcultaReveladaSigueSiendoAccesoNoTransitable() {
        // Arrange
        Cell puertaOculta = new Cell(CellType.DOOR_HIDDEN);

        // Act + Assert
        assertFalse(puertaOculta.isWalkable());

        puertaOculta.revelar();

        assertFalse(puertaOculta.isWalkable());
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

    @Test
    void isWalkable_celdaConContenedorNoEsTransitable() {
        // Act
        celdaSuelo.setContainer(chest);

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

    // -- Container -----------------------------------------------------------

    @Test
    void setContainer_y_removeContainer_actualizanContenedorDeLaCelda() {
        // Act
        celdaSuelo.setContainer(chest);

        // Assert
        assertSame(chest, celdaSuelo.getContainer());

        // Act
        celdaSuelo.removeContainer();

        // Assert
        assertNull(celdaSuelo.getContainer());
    }

    // -- Acceso entre salas --------------------------------------------------

    @Test
    void setDestinoAcceso_configuraSalaYCoordenadasDestino() {
        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);

        // Act
        celdaSuelo.setDestinoAcceso(destino, 2, 3);

        // Assert
        assertTrue(celdaSuelo.hasDestinoAcceso());
        assertSame(destino, celdaSuelo.getSalaDestino());
        assertEquals(2, celdaSuelo.getFilaDestino());
        assertEquals(3, celdaSuelo.getColDestino());
    }

    @Test
    void limpiarDestinoAcceso_eliminaDestinoFuncional() {
        // Arrange
        Room destino = new Room("R2", "Sala destino", 4, 4);
        celdaSuelo.setDestinoAcceso(destino, 2, 3);

        // Act
        celdaSuelo.limpiarDestinoAcceso();

        // Assert
        assertFalse(celdaSuelo.hasDestinoAcceso());
        assertNull(celdaSuelo.getSalaDestino());
        assertEquals(0, celdaSuelo.getFilaDestino());
        assertEquals(0, celdaSuelo.getColDestino());
    }

    @Test
    void helpersDeAcceso_identificanPuertasEscalerasEInteraccion() {
        // Arrange
        Cell puerta = new Cell(CellType.DOOR);
        Cell puertaCerrada = new Cell(CellType.DOOR_LOCKED);
        Cell puertaOculta = new Cell(CellType.DOOR_HIDDEN);
        Cell escalera = new Cell(CellType.STAIRS_DOWN);

        // Act + Assert
        assertTrue(puerta.isAccessCell());
        assertTrue(puerta.isDoor());
        assertTrue(puerta.isInteractuableAccess());
        assertTrue(puertaCerrada.isInteractuableAccess());
        assertFalse(puertaOculta.isInteractuableAccess());
        assertTrue(escalera.isAccessCell());
        assertTrue(escalera.isStairs());
        assertTrue(escalera.isInteractuableAccess());
    }

    @Test
    void bloqueaVision_paredStairsUpYUnidadBloqueanPeroStairsDownNo() {
        // Arrange
        Cell ocupada = new Cell(CellType.FLOOR);
        ocupada.setUnit(jugador);

        // Act + Assert
        assertTrue(new Cell(CellType.WALL).bloqueaVision());
        assertTrue(new Cell(CellType.STAIRS_UP).bloqueaVision());
        assertTrue(ocupada.bloqueaVision());
        assertFalse(new Cell(CellType.STAIRS_DOWN).bloqueaVision());
        assertFalse(new Cell(CellType.DOOR).bloqueaVision());
        assertFalse(new Cell(CellType.RUNE).bloqueaVision());
    }

    @Test
    void setAccessFacing_validaDireccionOrtogonal() {
        // Arrange
        Cell escalera = new Cell(CellType.STAIRS_UP);

        // Act
        escalera.setAccessFacing(0, -1);

        // Assert
        assertTrue(escalera.hasAccessFacing());
        assertEquals(0, escalera.getAccessFacingDeltaFila());
        assertEquals(-1, escalera.getAccessFacingDeltaCol());

        // Act
        escalera.setAccessFacing(1, 1);

        // Assert
        assertFalse(escalera.hasAccessFacing());
        assertEquals(0, escalera.getAccessFacingDeltaFila());
        assertEquals(0, escalera.getAccessFacingDeltaCol());
    }

    @Test
    void isUsableFrom_escaleraSoloDesdeFrenteConfigurado() {
        // Arrange
        Cell escalera = new Cell(CellType.STAIRS_DOWN);
        escalera.setAccessFacing(0, -1);

        // Act + Assert
        assertTrue(escalera.isUsableFrom(2, 2, 2, 3));
        assertFalse(escalera.isUsableFrom(1, 3, 2, 3));
        assertFalse(escalera.isUsableFrom(2, 4, 2, 3));
    }

    @Test
    void isUsableFrom_puertaPermiteCualquierAdyacenteOrtogonal() {
        // Arrange
        Cell puerta = new Cell(CellType.DOOR);

        // Act + Assert
        assertTrue(puerta.isUsableFrom(2, 2, 2, 3));
        assertTrue(puerta.isUsableFrom(1, 3, 2, 3));
        assertFalse(puerta.isUsableFrom(1, 2, 2, 3));
    }

    @Test
    void requiredItemTriggerHighlightYReserva_seConfiguranCorrectamente() {
        // Act
        celdaSuelo.setRequiredItemId("AC1");
        celdaSuelo.setTriggerId("secret_s1");
        celdaSuelo.setHighlighted(true);
        celdaSuelo.setReservedForAccess(true);

        // Assert
        assertTrue(celdaSuelo.hasRequiredItem());
        assertEquals("AC1", celdaSuelo.getRequiredItemId());
        assertTrue(celdaSuelo.hasTrigger());
        assertEquals("secret_s1", celdaSuelo.getTriggerId());
        assertTrue(celdaSuelo.isHighlighted());
        assertTrue(celdaSuelo.isReservedForAccess());

        // Act
        celdaSuelo.clearHighlight();

        // Assert
        assertFalse(celdaSuelo.isHighlighted());
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
