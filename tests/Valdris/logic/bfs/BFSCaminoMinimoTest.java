package Valdris.logic.bfs;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link BFSCaminoMinimo}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class BFSCaminoMinimoTest {

    // -- Fixture -------------------------------------------------------------

    private Dungeon dungeon;
    private Room entrada;
    private Room bosque;
    private Room minas;
    private Room torre;
    private Room pasilloFinal;
    private Room nucleo;

    @BeforeEach
    void setUp() {
        dungeon = new Dungeon();
        entrada = new Room("S1-A", "Entrada", 5, 5);
        bosque = new Room("S2-A", "Bosque", 7, 7);
        minas = new Room("S3-A", "Minas", 7, 8);
        torre = new Room("S4-A", "Torre", 8, 8);
        pasilloFinal = new Room("S5-P", "Pasillo Final", 3, 8);
        nucleo = new Room("S5-D", "Nucleo Profundo", 10, 10);
    }

    // -- getCamino -----------------------------------------------------------

    @Test
    void getCamino_devuelveCaminoConOrigenYDestino() {
        // Arrange
        crearRutaLineal();

        // Act
        ListaSimplementeEnlazada<Room> camino = BFSCaminoMinimo.getCamino(dungeon, entrada, torre);

        // Assert
        assertEquals(4, camino.getSize());
        assertSame(entrada, camino.get(0));
        assertSame(bosque, camino.get(1));
        assertSame(minas, camino.get(2));
        assertSame(torre, camino.get(3));
    }

    @Test
    void getCamino_eligeRutaMasCorta() {
        // Arrange
        crearRutaLineal();
        dungeon.conectar(entrada, torre, "atajo");

        // Act
        ListaSimplementeEnlazada<Room> camino = BFSCaminoMinimo.getCamino(dungeon, entrada, torre);

        // Assert
        assertEquals(2, camino.getSize());
        assertSame(entrada, camino.get(0));
        assertSame(torre, camino.get(1));
    }

    @Test
    void getCamino_mismaSalaDevuelveUnaSala() {
        // Arrange
        dungeon.addRoom(entrada);

        // Act
        ListaSimplementeEnlazada<Room> camino = BFSCaminoMinimo.getCamino(dungeon, entrada, entrada);

        // Assert
        assertEquals(1, camino.getSize());
        assertSame(entrada, camino.get(0));
    }

    @Test
    void getCamino_respetaConexionUnidireccional() {
        // Arrange
        dungeon.conectarUnidireccional(pasilloFinal, nucleo, "punto de no retorno");

        // Act
        ListaSimplementeEnlazada<Room> ida = BFSCaminoMinimo.getCamino(dungeon, pasilloFinal, nucleo);
        ListaSimplementeEnlazada<Room> vuelta = BFSCaminoMinimo.getCamino(dungeon, nucleo, pasilloFinal);

        // Assert
        assertEquals(2, ida.getSize());
        assertSame(pasilloFinal, ida.get(0));
        assertSame(nucleo, ida.get(1));
        assertEquals(0, vuelta.getSize());
    }

    @Test
    void getCamino_salaNoRegistradaDevuelveListaVacia() {
        // Arrange
        dungeon.addRoom(entrada);

        // Act
        ListaSimplementeEnlazada<Room> camino = BFSCaminoMinimo.getCamino(dungeon, entrada, nucleo);

        // Assert
        assertEquals(0, camino.getSize());
    }

    @Test
    void getCamino_sinRutaDevuelveListaVacia() {
        // Arrange
        dungeon.addRoom(entrada);
        dungeon.addRoom(nucleo);

        // Act
        ListaSimplementeEnlazada<Room> camino = BFSCaminoMinimo.getCamino(dungeon, entrada, nucleo);

        // Assert
        assertEquals(0, camino.getSize());
    }

    @Test
    void getCamino_nullDevuelveListaVacia() {
        // Act + Assert
        assertEquals(0, BFSCaminoMinimo.getCamino(null, entrada, nucleo).getSize());
        assertEquals(0, BFSCaminoMinimo.getCamino(dungeon, null, nucleo).getSize());
        assertEquals(0, BFSCaminoMinimo.getCamino(dungeon, entrada, null).getSize());
    }

    @Test
    void getCamino_aceptaSalaEquivalentePorId() {
        // Arrange
        crearRutaLineal();
        Room entradaEquivalente = new Room("S1-A", "Entrada reconstruida", 5, 5);

        // Act
        ListaSimplementeEnlazada<Room> camino = BFSCaminoMinimo.getCamino(dungeon, entradaEquivalente, minas);

        // Assert
        assertEquals(3, camino.getSize());
        assertSame(entrada, camino.get(0));
        assertSame(bosque, camino.get(1));
        assertSame(minas, camino.get(2));
    }

    // -- getCaminoPorId ------------------------------------------------------

    @Test
    void getCaminoPorId_devuelveCaminoEntreIds() {
        // Arrange
        crearRutaLineal();

        // Act
        ListaSimplementeEnlazada<Room> camino =
            BFSCaminoMinimo.getCaminoPorId(dungeon, "S1-A", "S3-A");

        // Assert
        assertEquals(3, camino.getSize());
        assertSame(entrada, camino.get(0));
        assertSame(bosque, camino.get(1));
        assertSame(minas, camino.get(2));
    }

    @Test
    void getCaminoPorId_idInexistenteDevuelveListaVacia() {
        // Arrange
        crearRutaLineal();

        // Act
        ListaSimplementeEnlazada<Room> camino =
            BFSCaminoMinimo.getCaminoPorId(dungeon, "S1-A", "NO-EXISTE");

        // Assert
        assertEquals(0, camino.getSize());
    }

    @Test
    void getCaminoPorId_nullDevuelveListaVacia() {
        // Act + Assert
        assertEquals(0, BFSCaminoMinimo.getCaminoPorId(null, "S1-A", "S2-A").getSize());
        assertEquals(0, BFSCaminoMinimo.getCaminoPorId(dungeon, null, "S2-A").getSize());
        assertEquals(0, BFSCaminoMinimo.getCaminoPorId(dungeon, "S1-A", null).getSize());
    }

    // -- getDistancia --------------------------------------------------------

    @Test
    void getDistancia_devuelveNumeroDeConexiones() {
        // Arrange
        crearRutaLineal();

        // Act + Assert
        assertEquals(3, BFSCaminoMinimo.getDistancia(dungeon, entrada, torre));
        assertEquals(0, BFSCaminoMinimo.getDistancia(dungeon, entrada, entrada));
    }

    @Test
    void getDistancia_sinCaminoDevuelveMenosUno() {
        // Arrange
        dungeon.addRoom(entrada);
        dungeon.addRoom(nucleo);

        // Act + Assert
        assertEquals(-1, BFSCaminoMinimo.getDistancia(dungeon, entrada, nucleo));
    }

    // -- Helpers -------------------------------------------------------------

    private void crearRutaLineal() {
        dungeon.conectar(entrada, bosque, "puerta");
        dungeon.conectar(bosque, minas, "pasillo");
        dungeon.conectar(minas, torre, "escalera");
    }
}
