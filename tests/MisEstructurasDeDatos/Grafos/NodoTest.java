package MisEstructurasDeDatos.Grafos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NodoTest {

    @Test
    public void testConstructorYGetId() {
        NodoGrafo<String> nodo = new NodoGrafo<>("persona:Albert Einstein");

        assertTrue(nodo.getId() > 0);
        assertEquals("persona:Albert Einstein", nodo.getDatos());
    }

    @Test
    public void testSetId() {
        NodoGrafo<String> nodo = new NodoGrafo<>("persona:Albert Einstein");

        nodo.setId(500L);

        assertEquals(500L, nodo.getId());
    }

    @Test
    public void testEqualsMismoObjeto() {
        NodoGrafo<String> nodo = new NodoGrafo<>("persona:Albert Einstein");

        assertTrue(nodo.equals(nodo));
    }

    @Test
    public void testEqualsObjetoNoNodo() {
        NodoGrafo<String> nodo = new NodoGrafo<>("persona:Albert Einstein");

        assertFalse(nodo.equals("esto no es un nodo"));
    }

    @Test
    public void testEqualsNodosIguales() {
        NodoGrafo<String> nodo1 = new NodoGrafo<>("persona:Albert Einstein");
        NodoGrafo<String> nodo2 = new NodoGrafo<>("persona:Albert Einstein");

        nodo2.setId(nodo1.getId());

        assertEquals(nodo1, nodo2);
    }

    @Test
    public void testEqualsNodosDistintos() {
        NodoGrafo<String> nodo1 = new NodoGrafo<>("persona:Albert Einstein");
        NodoGrafo<String> nodo2 = new NodoGrafo<>("persona:Marie Curie");

        assertNotEquals(nodo1, nodo2);
    }

    @Test
    public void testEqualsConDatosNull() {
        NodoGrafo<String> nodo1 = new NodoGrafo<>(null);
        NodoGrafo<String> nodo2 = new NodoGrafo<>(null);

        assertNotEquals(nodo1, nodo2);
    }

    @Test
    public void testToString() {
        NodoGrafo<String> nodo = new NodoGrafo<>("persona:Albert Einstein");

        String resultado = nodo.toString();
        assertTrue(resultado.contains("ID:"));
        assertTrue(resultado.contains("persona:Albert Einstein"));
    }

    @Test
    public void testCompareToIguales() {
        NodoGrafo<String> nodo1 = new NodoGrafo<>("persona:Albert Einstein");
        NodoGrafo<String> nodo2 = new NodoGrafo<>("persona:Albert Einstein");
        nodo2.setId(nodo1.getId());

        assertEquals(0, nodo1.compareTo(nodo2));
    }

    @Test
    public void testCompareToDistintos() {
        NodoGrafo<String> nodo1 = new NodoGrafo<>("persona:Albert Einstein");
        NodoGrafo<String> nodo2 = new NodoGrafo<>("persona:Marie Curie");

        assertTrue(nodo1.compareTo(nodo2) < 0);
    }
}