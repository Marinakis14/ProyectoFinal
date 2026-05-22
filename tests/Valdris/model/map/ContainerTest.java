package Valdris.model.map;

import Valdris.model.enums.CharacterType;
import Valdris.model.items.Weapon;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el comportamiento comun de {@link Container}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica una regla de apertura, contenido o comparacion.</p>
 */
class ContainerTest {

    // -- Fixture -------------------------------------------------------------

    private TestContainer container;
    private Player jugador;
    private Weapon espada;
    private Weapon arco;

    @BeforeEach
    void setUp() {
        container = new TestContainer("C1", "Contenedor de prueba");
        jugador = new Player(CharacterType.KAEL);
        espada = new Weapon("W1", "Espada de prueba", 8, 0, 1);
        arco = new Weapon("W2", "Arco de prueba", 7, 0, 3);
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_inicializaDatosBasicosYCerrado() {
        assertEquals("C1", container.getId());
        assertEquals("Contenedor de prueba", container.getNombre());
        assertFalse(container.isAbierto());
        assertTrue(container.isVacio());
        assertEquals(0, container.getItems().getSize());
    }

    // -- Contenido -----------------------------------------------------------

    @Test
    void addItem_agregaItemsValidos() {
        // Act
        container.addItem(espada);
        container.addItem(arco);

        // Assert
        assertFalse(container.isVacio());
        assertEquals(2, container.getItems().getSize());
        assertSame(espada, container.getItems().get(0));
        assertSame(arco, container.getItems().get(1));
    }

    @Test
    void addItem_nullNoModificaContenido() {
        // Act
        container.addItem(null);

        // Assert
        assertTrue(container.isVacio());
        assertEquals(0, container.getItems().getSize());
    }

    // -- Abrir ---------------------------------------------------------------

    @Test
    void abrir_entregaTodosLosItemsAlInventarioYMarcaAbierto() {
        // Arrange
        container.addItem(espada);
        container.addItem(arco);

        // Act
        container.abrir(jugador);

        // Assert
        assertTrue(container.isAbierto());
        assertTrue(container.isVacio());
        assertEquals(2, jugador.getInventario().getSize());
        assertSame(espada, jugador.getInventario().get(0));
        assertSame(arco, jugador.getInventario().get(1));
    }

    @Test
    void abrir_contenedorVacioLoMarcaAbiertoSinEntregarItems() {
        // Act
        container.abrir(jugador);

        // Assert
        assertTrue(container.isAbierto());
        assertTrue(container.isVacio());
        assertEquals(0, jugador.getInventario().getSize());
    }

    @Test
    void abrir_dosVecesNoDuplicaContenido() {
        // Arrange
        container.addItem(espada);

        // Act
        container.abrir(jugador);
        container.abrir(jugador);

        // Assert
        assertTrue(container.isAbierto());
        assertEquals(1, jugador.getInventario().getSize());
        assertSame(espada, jugador.getInventario().get(0));
    }

    @Test
    void abrir_conJugadorNullNoAbreNiVaciaContenido() {
        // Arrange
        container.addItem(espada);

        // Act
        container.abrir(null);

        // Assert
        assertFalse(container.isAbierto());
        assertFalse(container.isVacio());
        assertEquals(1, container.getItems().getSize());
    }

    // -- Comparacion ---------------------------------------------------------

    @Test
    void compareTo_ordenaPorId() {
        // Arrange
        TestContainer posterior = new TestContainer("C2", "Otro contenedor");

        // Act + Assert
        assertTrue(container.compareTo(posterior) < 0);
        assertTrue(posterior.compareTo(container) > 0);
    }

    @Test
    void compareTo_mismoIdDevuelveCero() {
        // Arrange
        TestContainer copia = new TestContainer("C1", "Mismo id");

        // Act + Assert
        assertEquals(0, container.compareTo(copia));
    }

    @Test
    void compareTo_nullDevuelvePositivo() {
        assertTrue(container.compareTo(null) > 0);
    }

    // -- toString ------------------------------------------------------------

    @Test
    void toString_incluyeEstadoDeApertura() {
        // Act + Assert
        assertEquals("Contenedor de prueba (cerrado)", container.toString());

        container.abrir(jugador);

        assertEquals("Contenedor de prueba (abierto)", container.toString());
    }

    // -- Clase auxiliar ------------------------------------------------------

    /**
     * Implementacion minima para probar la clase abstracta.
     */
    private static final class TestContainer extends Container {

        /**
         * Crea un contenedor de prueba.
         *
         * @param id identificador del contenedor
         * @param nombre nombre visible del contenedor
         */
        private TestContainer(String id, String nombre) {
            super(id, nombre);
        }
    }
}
