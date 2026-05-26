package MisEstructurasDeDatos.Grafos;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GrafoTest {

    // Clase interna auxiliar para que el Grafo pueda extraer "Nombre" y "Tipo"
    private static class DatosDummy implements InterfazDatosNodo {
        private String nombre;
        private String tipo;

        public DatosDummy(String tipo, String nombre) {
            this.tipo = tipo;
            this.nombre = nombre;
        }

        @Override public String getNombre() { return nombre; }
        @Override public String getTipo() { return tipo; }
        @Override public String toString() { return nombre; }

        @Override
        public int compareTo(InterfazDatosNodo o) {
            return 0;
        }
    }

    @Test
    public void testConstructor() {
        Grafo<String, String> grafo = new Grafo<>();
        assertNotNull(grafo.getTipos());
        assertNotNull(grafo.getNodos());
        assertNotNull(grafo.getAristas());
        assertTrue(grafo.getTipos().isEmpty());
    }

    @Test
    public void testAddTipo() {
        Grafo<String, String> grafo = new Grafo<>();
        grafo.addTipo("persona");
        grafo.addTipo("persona"); // Duplicado, no debería añadirse
        grafo.addTipo("lugar");

        assertEquals(2, grafo.getTipos().getSize());
    }

    @Test
    public void testAddNodo() {
        Grafo<String, String> grafo = new Grafo<>();
        NodoGrafo<String> nodo = new NodoGrafo<>("persona:Albert Einstein");

        grafo.addNodo(nodo);
        grafo.addNodo(new NodoGrafo<>("persona:Albert Einstein")); // Mismo contenido

        assertEquals(1, grafo.getNodos().getSize());
    }

    @Test
    public void testAddAristaConObjeto() {
        Grafo<String, String> grafo = new Grafo<>();
        NodoGrafo<String> origen = new NodoGrafo<>("Einstein");
        NodoGrafo<String> destino = new NodoGrafo<>("Ulm");
        Arista<String, String> arista = new Arista<>(origen, "nace_en", destino);

        grafo.addArista(arista);

        assertEquals(2, grafo.getNodos().getSize());
        assertEquals(1, grafo.getAristas().getSize());
    }

    @Test
    public void testAddAristaConParametros() {
        Grafo<String, String> grafo = new Grafo<>();
        NodoGrafo<String> origen = new NodoGrafo<>("Einstein");
        NodoGrafo<String> destino = new NodoGrafo<>("Ulm");

        grafo.addArista(origen, "nace_en", destino);

        assertEquals(1, grafo.getAristas().getSize());
        assertEquals("nace_en", grafo.getAristas().get(0).getDato()); // Era getDato, no getPredicado
    }

    @Test
    public void testBuscarNodoPorIdExistente() {
        Grafo<String, String> grafo = new Grafo<>();
        NodoGrafo<String> nodo = new NodoGrafo<>("Einstein");
        grafo.addNodo(nodo);

        assertEquals(nodo, grafo.buscarNodoPorId(nodo.getId())); // Se busca por long, no por String
    }

    @Test
    public void testGetVecinos() {
        Grafo<String, String> grafo = new Grafo<>();
        NodoGrafo<String> einstein = new NodoGrafo<>("Einstein");
        NodoGrafo<String> ulm = new NodoGrafo<>("Ulm");

        grafo.addArista(einstein, "nace_en", ulm);

        ListaSimplementeEnlazada<NodoGrafo<String>> vecinos = grafo.getVecinos(einstein);
        assertEquals(1, vecinos.getSize());
        assertEquals("Ulm", vecinos.get(0).getDatos());
    }

    @Test
    public void testCaminoMinimoExistente() {
        Grafo<DatosDummy, String> grafo = new Grafo<>();
        DatosDummy d1 = new DatosDummy("persona", "Einstein");
        DatosDummy d2 = new DatosDummy("lugar", "Ulm");
        DatosDummy d3 = new DatosDummy("pais", "Alemania");

        grafo.addArista(new NodoGrafo<>(d1), "nace_en", new NodoGrafo<>(d2));
        grafo.addArista(new NodoGrafo<>(d2), "esta_en", new NodoGrafo<>(d3));

        // Para que buscarNodoPorNombre funcione, el dato debe ser DatosDummy
        ListaSimplementeEnlazada<NodoGrafo<DatosDummy>> camino = grafo.caminoMinimo("Einstein", "Alemania");

        assertFalse(camino.isEmpty());
        assertEquals(3, camino.getSize());
    }

    @Test
    public void testEsDisjuntoTrue() {
        Grafo<String, String> grafo = new Grafo<>();
        NodoGrafo<String> e = new NodoGrafo<>("Einstein");
        NodoGrafo<String> u = new NodoGrafo<>("Ulm");
        NodoGrafo<String> a = new NodoGrafo<>("Antonio");

        grafo.addArista(e, "nace_en", u);
        grafo.addNodo(a); // Antonio está suelto

        assertTrue(grafo.esDisjunto());
    }

    @Test
    public void testGetDestinosPorPredicado() {
        Grafo<String, String> grafo = new Grafo<>();
        NodoGrafo<String> e = new NodoGrafo<>("Einstein");
        NodoGrafo<String> u = new NodoGrafo<>("Ulm");

        grafo.addArista(e, "nace_en", u);
    }

    @Test
    public void testPersonasMismaCiudadQue() {
        Grafo<DatosDummy, String> grafo = new Grafo<>();
        DatosDummy e = new DatosDummy("persona", "Einstein");
        DatosDummy v = new DatosDummy("persona", "Ventura");
        DatosDummy u = new DatosDummy("lugar", "Ulm");

        grafo.addArista(new NodoGrafo<>(e), "nace_en", new NodoGrafo<>(u));
        grafo.addArista(new NodoGrafo<>(v), "nace_en", new NodoGrafo<>(u));

        ListaSimplementeEnlazada<NodoGrafo<DatosDummy>> resultado = grafo.personasMismaCiudadQue("Einstein");

        assertEquals(1, resultado.getSize());
        assertEquals("Ventura", resultado.get(0).getDatos().getNombre());
    }
}