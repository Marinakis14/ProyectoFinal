package MisEstructurasDeDatos.ListasPilasYColas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListaCircularTest {

    @Test
    void addStartAddEndYAddAnywherePermitenRecorrerPorIndice() {
        ListaCircular<Integer> lista = new ListaCircular<>();

        lista.addEnd(2);
        lista.addStart(1);
        lista.addAnywhere(9, 1);

        assertEquals(3, lista.getSize());
        assertEquals(1, lista.get(0));
        assertEquals(9, lista.get(1));
        assertEquals(2, lista.get(2));
        assertEquals("--> 1 -> 9 -> 2 -->", lista.toString());
    }

    @Test
    void delYDelFirstMantienenLaCircularidadVisible() {
        ListaCircular<String> lista = new ListaCircular<>();
        lista.addEnd("entrada");
        lista.addEnd("nucleo");
        lista.addEnd("salida");

        assertEquals("entrada", lista.delFirst());
        assertEquals("salida", lista.del("salida"));

        assertEquals(1, lista.getSize());
        assertEquals("nucleo", lista.get(0));
        assertTrue(lista.contains("nucleo"));
        assertFalse(lista.contains("salida"));
    }

    @Test
    void clearVaciaLaListaCircular() {
        ListaCircular<Integer> lista = new ListaCircular<>();
        lista.addEnd(7);

        lista.clear();

        assertTrue(lista.isEmpty());
        assertEquals(0, lista.getSize());
        assertEquals("[LISTA VACIA]", lista.toString());
    }
}
