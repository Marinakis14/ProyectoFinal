package MisEstructurasDeDatos.ListasPilasYColas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColaTest {

    @Test
    void enqueueDequeueFrontYBackRespetanFIFO() {
        Cola<String> cola = new Cola<>();

        cola.enqueue("jugador");
        cola.enqueue("enemigo1");
        cola.enqueue("enemigo2");

        assertEquals("jugador", cola.front());
        assertEquals("enemigo2", cola.back());
        assertEquals(3, cola.getSize());
        assertEquals("jugador", cola.dequeue());
        assertEquals("enemigo1", cola.front());
    }

    @Test
    void searchCopyYClearFuncionanSinAlterarOriginal() {
        Cola<Integer> cola = new Cola<>();
        cola.enqueue(10);
        cola.enqueue(20);

        InterfazCola<Integer> copia = cola.copy();
        assertEquals(2, cola.search(20));
        assertEquals(10, copia.dequeue());
        assertEquals(2, cola.getSize());

        cola.clear();

        assertTrue(cola.isEmpty());
        assertNull(cola.front());
        assertEquals("[COLA VACIA]", cola.toString());
    }
}
