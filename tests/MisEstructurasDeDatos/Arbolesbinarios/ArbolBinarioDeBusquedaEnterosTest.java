package MisEstructurasDeDatos.Arbolesbinarios;

import org.junit.jupiter.api.Test;

class ArbolBinarioDeBusquedaEnterosTest {

    @Test
    void todo() {
        ArbolBinarioDeBusquedaEnteros arbol = new ArbolBinarioDeBusquedaEnteros();;
        for (int i = 0; i < 129; i++) {
            arbol.add(i);
        }
        arbol.getSumaOrdenCentral(arbol.getRaiz());
        arbol.getSumaPreorder(arbol.getRaiz());
        arbol.getSumaPostorder(arbol.getRaiz());
    }
}