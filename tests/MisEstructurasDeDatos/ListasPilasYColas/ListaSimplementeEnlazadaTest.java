package MisEstructurasDeDatos.ListasPilasYColas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListaSimplementeEnlazadaTest {

    @Test
    void addStartAddEndYAddAnywhereMantienenOrdenYTamano() {
        ListaSimplementeEnlazada<Integer> lista = new ListaSimplementeEnlazada<>();

        lista.addEnd(2);
        lista.addStart(1);
        lista.addAnywhere(3, 2);
        lista.addAnywhere(9, 1);

        assertEquals(4, lista.getSize());
        assertEquals(1, lista.get(0));
        assertEquals(9, lista.get(1));
        assertEquals(2, lista.get(2));
        assertEquals(3, lista.get(3));
        assertEquals("[ 1, 9, 2, 3 ]", lista.toString());
    }

    @Test
    void delDelFirstGetYContainsGestionanElementos() {
        ListaSimplementeEnlazada<String> lista = new ListaSimplementeEnlazada<>();
        lista.addEnd("kael");
        lista.addEnd("syra");
        lista.addEnd("dorath");

        assertTrue(lista.contains("syra"));
        assertEquals(1, lista.getPosicion("syra"));
        assertEquals("syra", lista.get("syra"));
        assertEquals("kael", lista.delFirst());
        assertEquals("dorath", lista.del("dorath"));

        assertEquals(1, lista.getSize());
        assertFalse(lista.contains("dorath"));
        assertNull(lista.get(3));
    }

    @Test
    void clearVaciaLaLista() {
        ListaSimplementeEnlazada<Integer> lista = new ListaSimplementeEnlazada<>();
        lista.addEnd(1);
        lista.addEnd(2);

        lista.clear();

        assertTrue(lista.isEmpty());
        assertEquals(0, lista.getSize());
        assertEquals("[LISTA VACIA]", lista.toString());
    }
}
