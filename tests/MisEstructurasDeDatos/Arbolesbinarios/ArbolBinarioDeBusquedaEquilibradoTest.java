package MisEstructurasDeDatos.Arbolesbinarios;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArbolBinarioDeBusquedaEquilibradoTest {

    @Test
    void DelYAdd() {
        // Solo hay que comprobar los metodos add y del que modificamos
        ArbolBinarioDeBusquedaEquilibrado<Integer> arbol = new ArbolBinarioDeBusquedaEquilibrado<>();
        arbol.add(2);
        arbol.add(3);
        arbol.add(4);
        arbol.add(5);
        arbol.delDato(3);
        arbol.delNodo(4);
        assertEquals("[ [ 5 ] ]\n[ [ 2 ] ]\n",arbol.toString());
    }
}
