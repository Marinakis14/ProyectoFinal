package MisEstructurasDeDatos.Arbolesbinarios;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ArbolBinarioDeBusquedaTest {

    // Constructores
    ArbolBinarioDeBusqueda<Integer> aVacio = new ArbolBinarioDeBusqueda<>();
    ArbolBinarioDeBusqueda<Integer> aRaiz = new ArbolBinarioDeBusqueda<>(10);
    ArbolBinarioDeBusqueda<Integer> aCompleto = new ArbolBinarioDeBusqueda<>(new Nodo<>(20));

    @Test
    void gettersSettersYtoString() {
        // Test de Raiz
        Nodo<Integer> nuevaRaiz = new Nodo<>(5);
        aVacio.setRaiz(nuevaRaiz);
        assertEquals(nuevaRaiz, aVacio.getRaiz());

        // Test de toString y niveles
        aRaiz.add(5);
        aRaiz.add(15);
        assertNotNull(aRaiz.toString());
        assertNotNull(aVacio.toString());
    }

    @Test
    void operacionesArbol() {
        // isEmpty
        assertTrue(aVacio.isEmpty());
        assertFalse(aRaiz.isEmpty());

        // getIzquierda, getDerecha, getDato, getDatos
        aRaiz.add(5);
        Nodo<Integer> r = aRaiz.getRaiz();
        assertNotNull(aRaiz.getIzquierda(r));
        assertNull(aRaiz.getDerecha(r));
        assertNull(aRaiz.getIzquierda(null));
        assertNull(aRaiz.getDerecha(null));
        assertEquals(10, aRaiz.getDato(r));
        assertNull(aRaiz.getDato(null));
        assertNotNull(aRaiz.getDatos(r));

        // Repeticiones
        aRaiz.add(10);
        assertEquals(2, aRaiz.getRepeticionesDato(10));
        assertEquals(0, aRaiz.getRepeticionesDato(null));
        assertEquals(2, aRaiz.getRepeticionesNodo(r));
        assertEquals(0, aRaiz.getRepeticionesNodo(null));

        // Grado, Altura, Nivel
        assertEquals(0, aVacio.getGrado());
        assertEquals(1, aRaiz.getGrado());
        assertEquals(-1, aVacio.getAltura());
        assertEquals(0, aRaiz.getNivel(10));
        assertEquals(-1, aRaiz.getNivel(null));
        assertEquals(-1, aVacio.getNivel(10));
        assertEquals(-1, aRaiz.getNivel(99)); // No existe

        // Listas por niveles
        assertEquals("[LISTA VACIA]",aRaiz.getListaDatosNivel(-1).toString());
        assertEquals("[LISTA VACIA]",aRaiz.getListaDatosNivel(10).toString());
        assertNotNull(aRaiz.getListaDatosNivel(0));
        assertNotNull(aRaiz.getListaDatosNivel(1));

        assertEquals("[LISTA VACIA]",aRaiz.getListaNodosNivel(-1).toString());
        assertEquals("[LISTA VACIA]",aRaiz.getListaNodosNivel(10).toString());
        assertNotNull(aRaiz.getListaNodosNivel(0));
        assertNotNull(aRaiz.getListaNodosNivel(1));

        // Hojas y Extremos
        assertEquals(0, aVacio.getNodosHoja().getSize());
        assertNotNull(aRaiz.getNodosHoja());
        assertNull(aVacio.getNodoMasPequeño());
        assertNull(aVacio.getNodoMasGrande());
        assertNotNull(aRaiz.getNodoMasPequeño());
        assertNotNull(aRaiz.getNodoMasGrande());

        // isNodoInArbol
        assertFalse(aVacio.isNodoInArbol(10));
        assertFalse(aRaiz.isNodoInArbol(null));
        assertTrue(aRaiz.isNodoInArbol(5));

        // Add, delDato, delNodo
        aVacio.add(null);
        aVacio.add(50);
        aVacio.delDato(null);
        aVacio.delDato(50);
        aVacio.delNodo(null);
        aVacio.delNodo(50);
        aRaiz.delDato(5); // Borrar existente

        // Caminos
        assertEquals("[ null ]",aVacio.getCaminoDatos(10).toString());
        assertNull(aRaiz.getCaminoDatos(null));
        assertNotNull(aRaiz.getCaminoDatos(10));
        assertEquals("[ null ]",aVacio.getCaminoNodos(10).toString());
        assertNull(aRaiz.getCaminoNodos(null));
        assertNotNull(aRaiz.getCaminoNodos(10));
    }

    @Test
    void estructuraArbol() {
        // Homogeneo
        assertTrue(aCompleto.isArbolHomogeneo1());
        aCompleto.add(10);
        aCompleto.add(30);
        assertTrue(aCompleto.isArbolHomogeneo1());
        assertTrue(aCompleto.isArbolHomogeneo2());

        // Completo y CasiCompleto
        assertTrue(aVacio.isArbolCompleto());
        assertTrue(aVacio.isArbolCompleto2());
        assertTrue(aCompleto.isArbolCompleto2());
        assertTrue(aVacio.isArbolCasiCompleto());

        // Forzar ramas de isArbolCasiCompleto
        aCompleto.add(5); // Solo el 10 tiene hijo izquierdo
        assertFalse(aCompleto.isArbolCompleto());
        assertTrue(aCompleto.isArbolCasiCompleto());

        // Equilibrar y Subárboles
        assertEquals(aVacio, aVacio.equilibrar());
        assertNotNull(aCompleto.equilibrar());
        assertNotNull(aCompleto.getSubArbolIzquierda());
        assertNotNull(aCompleto.getSubArbolDerecha());
        // Cobertura rama getDatos(raiz) != null en getSubArbolDerecha
        assertNotNull(new ArbolBinarioDeBusqueda<Integer>(10).getSubArbolDerecha());
    }

    @Test
    void recorridos() {
        // Árbol vacío (cobertura ramas raiz != null)
        assertNotNull(aVacio.getListaOrdenCentral());
        assertNotNull(aVacio.getListaPreOrden());
        assertNotNull(aVacio.getListaPostOrden());

        // Árbol con datos
        aCompleto.add(10);
        assertFalse(aCompleto.getListaOrdenCentral().isEmpty());
        assertFalse(aCompleto.getListaPreOrden().isEmpty());
        assertFalse(aCompleto.getListaPostOrden().isEmpty());
    }
}