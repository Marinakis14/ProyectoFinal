package Valdris.model.map;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.model.enums.CellType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Dungeon}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class DungeonTest {

    // -- Fixture -------------------------------------------------------------

    private Dungeon dungeon;
    private Room salaInicial;
    private Room salaBosque;
    private Room salaFinal;

    @BeforeEach
    void setUp() {
        dungeon = new Dungeon();
        salaInicial = new Room("S1-A", "Entrada de los Campos Grises", 5, 5);
        salaBosque = new Room("S2-A", "Umbral del Bosque", 7, 7);
        salaFinal = new Room("S5-D", "Nucleo Profundo", 10, 10);
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_inicializaDungeonVacio() {
        assertEquals(0, dungeon.getGrafo().getNodos().getSize());
        assertEquals(0, dungeon.getGrafo().getAristas().getSize());
        assertNull(dungeon.getRoomActual());
        assertNull(dungeon.getNodoActual());
    }

    // -- addRoom -------------------------------------------------------------

    @Test
    void addRoom_agregaSalaAlGrafo() {
        // Act
        dungeon.addRoom(salaInicial);

        // Assert
        assertEquals(1, dungeon.getGrafo().getNodos().getSize());
        assertSame(salaInicial, dungeon.getRoomById("S1-A"));
    }

    @Test
    void addRoom_primeraSalaPasaASerActual() {
        // Act
        dungeon.addRoom(salaInicial);

        // Assert
        assertSame(salaInicial, dungeon.getRoomActual());
        assertNotNull(dungeon.getNodoActual());
        assertSame(salaInicial, dungeon.getNodoActual().getDatos());
    }

    @Test
    void addRoom_nullNoModificaDungeon() {
        // Act
        dungeon.addRoom(null);

        // Assert
        assertEquals(0, dungeon.getGrafo().getNodos().getSize());
        assertNull(dungeon.getRoomActual());
    }

    @Test
    void addRoom_noDuplicaSalaEquivalente() {
        // Arrange
        Room mismaId = new Room("S1-A", "Otra entrada", 6, 6);

        // Act
        dungeon.addRoom(salaInicial);
        dungeon.addRoom(mismaId);

        // Assert
        assertEquals(1, dungeon.getGrafo().getNodos().getSize());
    }

    // -- conectar ------------------------------------------------------------

    @Test
    void conectar_creaConexionBidireccional() {
        // Act
        dungeon.conectar(salaInicial, salaBosque, "puerta norte");

        // Assert
        ListaSimplementeEnlazada<Room> vecinasInicial = dungeon.getSalasAdyacentes(salaInicial);
        ListaSimplementeEnlazada<Room> vecinasBosque = dungeon.getSalasAdyacentes(salaBosque);

        assertEquals(2, dungeon.getGrafo().getNodos().getSize());
        assertEquals(2, dungeon.getGrafo().getAristas().getSize());
        assertSame(salaBosque, vecinasInicial.get(0));
        assertSame(salaInicial, vecinasBosque.get(0));
    }

    @Test
    void conectar_nullNoCreaAristas() {
        // Act
        dungeon.conectar(salaInicial, null, "sin destino");

        // Assert
        assertEquals(0, dungeon.getGrafo().getAristas().getSize());
    }

    @Test
    void conectar_guardaDescripcionEnLasAristas() {
        // Act
        dungeon.conectar(salaInicial, salaBosque, "puerta norte");

        // Assert
        assertEquals("puerta norte", dungeon.getGrafo().getAristas().get(0).getDato());
        assertEquals("puerta norte", dungeon.getGrafo().getAristas().get(1).getDato());
    }

    @Test
    void conectar_noDuplicaAristasEquivalentes() {
        // Act
        dungeon.conectar(salaInicial, salaBosque, "puerta norte");
        dungeon.conectar(salaInicial, salaBosque, "puerta norte");

        // Assert
        assertEquals(2, dungeon.getGrafo().getAristas().getSize());
        assertEquals(1, dungeon.getSalasAdyacentes(salaInicial).getSize());
        assertEquals(1, dungeon.getSalasAdyacentes(salaBosque).getSize());
    }

    // -- conectarUnidireccional ---------------------------------------------

    @Test
    void conectarUnidireccional_creaSoloAristaDeOrigenADestino() {
        // Act
        dungeon.conectarUnidireccional(salaBosque, salaFinal, "punto de no retorno");

        // Assert
        ListaSimplementeEnlazada<Room> vecinasBosque = dungeon.getSalasAdyacentes(salaBosque);
        ListaSimplementeEnlazada<Room> vecinasFinal = dungeon.getSalasAdyacentes(salaFinal);

        assertEquals(1, dungeon.getGrafo().getAristas().getSize());
        assertSame(salaFinal, vecinasBosque.get(0));
        assertEquals(0, vecinasFinal.getSize());
    }

    @Test
    void conectarUnidireccional_nullNoCreaAristas() {
        // Act
        dungeon.conectarUnidireccional(null, salaFinal, "sin origen");

        // Assert
        assertEquals(0, dungeon.getGrafo().getAristas().getSize());
    }

    @Test
    void conectarUnidireccional_noDuplicaAristaEquivalente() {
        // Act
        dungeon.conectarUnidireccional(salaBosque, salaFinal, "punto de no retorno");
        dungeon.conectarUnidireccional(salaBosque, salaFinal, "punto de no retorno");

        // Assert
        assertEquals(1, dungeon.getGrafo().getAristas().getSize());
        assertEquals(1, dungeon.getSalasAdyacentes(salaBosque).getSize());
    }

    // -- Pasadizos ocultos ---------------------------------------------------

    @Test
    void connectHidden_registraPasadizoSinCrearArista() {
        // Act
        dungeon.connectHidden(salaInicial, salaBosque, "pasadizo secreto", "secret_s1");

        // Assert
        assertEquals(2, dungeon.getGrafo().getNodos().getSize());
        assertEquals(0, dungeon.getGrafo().getAristas().getSize());
        assertEquals(1, dungeon.getHiddenPassages().getSize());
        assertNotNull(dungeon.getHiddenPassage("secret_s1"));
        assertFalse(dungeon.isHiddenPassageActive("secret_s1"));
        assertSame(salaBosque, dungeon.getHiddenPassageTarget("secret_s1"));
    }

    @Test
    void activateHiddenPassage_creaConexionBidireccionalSoloUnaVez() {
        // Arrange
        dungeon.connectHidden(salaInicial, salaBosque, "pasadizo secreto", "secret_s1");

        // Act
        boolean activado = dungeon.activateHiddenPassage("secret_s1");
        boolean repetido = dungeon.activateHiddenPassage("secret_s1");

        // Assert
        assertTrue(activado);
        assertTrue(repetido);
        assertTrue(dungeon.isHiddenPassageActive("secret_s1"));
        assertEquals(2, dungeon.getGrafo().getAristas().getSize());
        assertEquals(1, dungeon.getSalasAdyacentes(salaInicial).getSize());
        assertEquals(1, dungeon.getSalasAdyacentes(salaBosque).getSize());
    }

    @Test
    void activateHiddenPassage_unidireccionalCreaUnaSolaArista() {
        // Arrange
        dungeon.connectHidden(salaBosque, salaFinal, "pasadizo final", "secret_s5", false);

        // Act
        dungeon.activateHiddenPassage("secret_s5");

        // Assert
        assertTrue(dungeon.isHiddenPassageActive("secret_s5"));
        assertEquals(1, dungeon.getGrafo().getAristas().getSize());
        assertEquals(1, dungeon.getSalasAdyacentes(salaBosque).getSize());
        assertEquals(0, dungeon.getSalasAdyacentes(salaFinal).getSize());
    }

    @Test
    void activateHiddenPassage_idInexistenteDevuelveFalse() {
        assertFalse(dungeon.activateHiddenPassage("NO_EXISTE"));
        assertFalse(dungeon.isHiddenPassageActive("NO_EXISTE"));
        assertNull(dungeon.getHiddenPassageTarget("NO_EXISTE"));
    }

    // -- getSalasAdyacentes --------------------------------------------------

    @Test
    void getSalasAdyacentes_salaSinConexionesDevuelveListaVacia() {
        // Arrange
        dungeon.addRoom(salaInicial);

        // Act
        ListaSimplementeEnlazada<Room> adyacentes = dungeon.getSalasAdyacentes(salaInicial);

        // Assert
        assertEquals(0, adyacentes.getSize());
    }

    @Test
    void getSalasAdyacentes_salaNoRegistradaDevuelveListaVacia() {
        // Act
        ListaSimplementeEnlazada<Room> adyacentes = dungeon.getSalasAdyacentes(salaInicial);

        // Assert
        assertEquals(0, adyacentes.getSize());
    }

    // -- getRoomById ---------------------------------------------------------

    @Test
    void getRoomById_devuelveSalaExistente() {
        // Arrange
        dungeon.addRoom(salaInicial);

        // Act + Assert
        assertSame(salaInicial, dungeon.getRoomById("S1-A"));
    }

    @Test
    void getRoomById_idInexistenteDevuelveNull() {
        // Arrange
        dungeon.addRoom(salaInicial);

        // Act + Assert
        assertNull(dungeon.getRoomById("NO-EXISTE"));
        assertNull(dungeon.getRoomById(null));
    }

    // -- setRoomActual -------------------------------------------------------

    @Test
    void setRoomActual_actualizaSalaYNodoActual() {
        // Arrange
        dungeon.addRoom(salaInicial);
        dungeon.addRoom(salaBosque);

        // Act
        dungeon.setRoomActual(salaBosque);

        // Assert
        assertSame(salaBosque, dungeon.getRoomActual());
        assertNotNull(dungeon.getNodoActual());
        assertSame(salaBosque, dungeon.getNodoActual().getDatos());
    }

    @Test
    void setRoomActual_salaNoRegistradaLaAgregaAlGrafo() {
        // Act
        dungeon.setRoomActual(salaFinal);

        // Assert
        assertEquals(1, dungeon.getGrafo().getNodos().getSize());
        assertSame(salaFinal, dungeon.getRoomActual());
        assertSame(salaFinal, dungeon.getRoomById("S5-D"));
    }

    @Test
    void setRoomActual_nullLimpiaSalaYNodoActual() {
        // Arrange
        dungeon.addRoom(salaInicial);

        // Act
        dungeon.setRoomActual(null);

        // Assert
        assertNull(dungeon.getRoomActual());
        assertNull(dungeon.getNodoActual());
    }

    // -- Integracion ligera con Room -----------------------------------------

    @Test
    void salaRecuperadaMantieneEstadoDeRoom() throws Exception {
        // Arrange
        salaInicial.setCellType(0, 0, CellType.DOOR);
        dungeon.addRoom(salaInicial);

        // Act
        Room recuperada = dungeon.getRoomById("S1-A");

        // Assert
        assertEquals(CellType.DOOR, recuperada.getCell(0, 0).getTipo());
    }

    // -- compareTo -----------------------------------------------------------

    @Test
    void compareTo_comparaPorNumeroDeSalasYRoomActual() {
        // Arrange
        Dungeon otro = new Dungeon();
        dungeon.addRoom(salaInicial);

        // Act + Assert
        assertTrue(dungeon.compareTo(otro) > 0);

        otro.addRoom(salaInicial);
        assertEquals(0, dungeon.compareTo(otro));
        assertTrue(dungeon.compareTo(null) > 0);
    }
}
