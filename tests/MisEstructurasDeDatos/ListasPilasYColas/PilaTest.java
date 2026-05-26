package MisEstructurasDeDatos.ListasPilasYColas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PilaTest {

    @Test
    void pushPopYTopRespetanLIFO() {
        Pila<String> pila = new Pila<>();

        pila.push("base");
        pila.push("medio");
        pila.push("cima");

        assertEquals("cima", pila.top());
        assertEquals(3, pila.getSize());
        assertEquals("cima", pila.pop());
        assertEquals("medio", pila.top());
    }

    @Test
    void searchCopyYClearFuncionanSinAlterarOriginal() {
        Pila<Integer> pila = new Pila<>();
        pila.push(1);
        pila.push(2);

        InterfazPila<Integer> copia = pila.copy();
        assertEquals(1, pila.search(2));
        assertEquals(2, copia.pop());
        assertEquals(2, pila.getSize());

        pila.clear();

        assertTrue(pila.isEmpty());
        assertNull(pila.top());
        assertEquals("[PILA VACIA]", pila.toString());
    }
}
