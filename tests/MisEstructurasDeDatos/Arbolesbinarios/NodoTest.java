package MisEstructurasDeDatos.Arbolesbinarios;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NodoTest {

    // Constructores
    @Test
    void constructores() {
        Nodo<Integer> nVacio = new Nodo<>();
        assertNotNull(nVacio.getDatos());

        Nodo<Integer> nDato = new Nodo<>(10);
        assertEquals(10, nDato.getDatos().get(0));

        Nodo<Integer> nCompleto = new Nodo<>(null, null, 20);
        assertEquals(20, nCompleto.getDatos().get(0));
    }

    // Getters, Setters y toString
    @Test
    void gettersSettersYtoString() {
        Nodo<Integer> n = new Nodo<>(10);
        Nodo<Integer> izq = new Nodo<>(5);
        Nodo<Integer> der = new Nodo<>(15);

        n.setIzquierda(izq);
        n.setDerecha(der);

        assertEquals(izq, n.getIzquierda());
        assertEquals(der, n.getDerecha());

        // todos los casos del toString
        // 1. Ambos hijos
        assertTrue(n.toString().contains("TIENE IZQUIERDA") && n.toString().contains("TIENE DERECHA"));
        // 2. Solo izquierda
        n.setDerecha(null);
        assertTrue(n.toString().contains("TIENE IZQUIERDA") && n.toString().contains("-/->"));
        // 3. Solo derecha
        n.setIzquierda(null);
        n.setDerecha(der);
        assertTrue(n.toString().contains("<-/-") && n.toString().contains("TIENE DERECHA"));
        // 4. Hoja
        n.setDerecha(null);
        assertEquals("<-/- (NODO= [ 10 ] ) -/->", n.toString().trim());

        // CompareTo
        assertEquals(0, n.compareTo(izq));
    }

    // Operaciones Recursivas
    @Test
    void operacionesRecursivas() {
        Nodo<Integer> raiz = new Nodo<>(10);
        raiz.add(5);
        raiz.add(15);
        raiz.add(10); // Repetido

        // Repeticiones
        assertEquals(2, raiz.getRepeticionesNodo(10));
        assertEquals(1, raiz.getRepeticionesNodo(5));
        assertEquals(0, raiz.getRepeticionesNodo(99)); // No existe

        // Grado y Altura
        assertEquals(2, raiz.getGrado());
        assertEquals(2, raiz.getAltura());

        Nodo<Integer> soloRaiz = new Nodo<>(10);
        assertEquals(0, soloRaiz.getGrado());

        // isNodoInArbol
        assertTrue(raiz.isNodoInArbol(5));
        assertTrue(raiz.isNodoInArbol(15));
        assertFalse(raiz.isNodoInArbol(99));

        // Nivel
        assertEquals(0, raiz.getNivel(10));
        assertEquals(1, raiz.getNivel(5));
        assertEquals(1, raiz.getNivel(15));
        assertEquals(-1, raiz.getNivel(99));

        // Nodo Mas Grande / Pequeño
        assertEquals(5, raiz.getNodoMasPequeño().getDatos().get(0));
        assertEquals(15, raiz.getNodoMasGrande().getDatos().get(0));
    }

    // Estructura y Listas
    @Test
    void estructuraYListas() {
        Nodo<Integer> raiz = new Nodo<>(10);
        raiz.add(5);
        raiz.add(15);

        // Listas por nivel
        ListaSimplementeEnlazada<ListaSimplementeEnlazada<Integer>> listaDatos = new ListaSimplementeEnlazada<>();
        raiz.getListaDatosNivel(listaDatos, 1);
        assertEquals(2, listaDatos.getSize());

        ListaSimplementeEnlazada<Nodo<Integer>> listaNodos = new ListaSimplementeEnlazada<>();
        raiz.getListaNodosNivel(listaNodos, 0);
        assertEquals(1, listaNodos.getSize());

        // Hojas y Hijos
        assertEquals(2, raiz.numeroHijos());
        ListaSimplementeEnlazada<ListaSimplementeEnlazada<Integer>> hojas = new ListaSimplementeEnlazada<>();
        raiz.getNodosHoja(hojas);
        assertEquals(2, hojas.getSize());

        // Homogeneo y Completo
        assertTrue(raiz.isArbolHomogeneo(raiz));
        assertTrue(raiz.isArbolCompleto(0, 1));
        assertFalse(raiz.isArbolCompleto(0, 2)); // Nivel equivocado

        // Casi Completo
        assertTrue(raiz.isArbolCasiCompleto(0, 1));

        Nodo<Integer> incompleto = new Nodo<>(10);
        incompleto.add(5); // Solo hijo izquierdo
        assertFalse(incompleto.isArbolCompleto(0, 1));
    }

    // Del y Add
    @Test
    void DelYAdd() {
        Nodo<Integer> raiz = new Nodo<>(10);
        raiz.add(5);
        raiz.add(15);
        raiz.add(10);

        // Borrar un dato de una lista con varios
        raiz.delDato(10);
        assertEquals(1, raiz.getDatos().getSize());

        // Borrar nodo entero
        raiz.delNodo(5);
        assertNull(raiz.getIzquierda());

        // Borrar nodo con dos hijos
        Nodo<Integer> complejo = new Nodo<>(20);
        complejo.add(10);
        complejo.add(30);
        complejo.add(25);
        complejo.add(35);
        complejo.delNodo(20);
        assertEquals(25, complejo.getDatos().get(0));

        // ProcesoBorrado
        Nodo<Integer> soloIzq = new Nodo<>(10);
        soloIzq.setIzquierda(new Nodo<>(5));
        assertEquals(5, soloIzq.delNodo(10).getDatos().get(0));

        // Add nodo
        Nodo<Integer> n1 = new Nodo<>(100);
        raiz.add(n1);
        assertEquals(100, raiz.getDerecha().getDerecha().getDatos().get(0));
        raiz.add(new Nodo<>(10));
    }

    // Equilibrio y Rotaciones
    @Test
    void equilibrioYRotaciones() {
        // Rotación Simple Derecha
        Nodo<Integer> n2 = new Nodo<>(30);
        n2.add(20);
        n2.add(10);
        Nodo<Integer> r2 = n2.equilibrarBase();
        assertEquals(20, r2.getDatos().get(0));

        // Rotación Simple Izquierda
        Nodo<Integer> n1 = new Nodo<>(10);
        n1.add(20);
        n1.add(30);
        Nodo<Integer> r1 = n1.equilibrarBase();
        assertEquals(20, r1.getDatos().get(0));

        // Rotación Doble
        Nodo<Integer> n3 = new Nodo<>(30);
        n3.add(10);
        n3.add(20); // Zig-zag
        assertNotNull(n3.equilibrarBase());

        Nodo<Integer> n4 = new Nodo<>(10);
        n4.add(30);
        n4.add(20); // Zig-zag
        assertNotNull(n4.equilibrarBase());

        // Casos con nodos nulos
        assertNull(new Nodo<Integer>().rotacionHaciaIzquierda(null));
        assertNull(new Nodo<Integer>().rotacionHaciaDerecha(null));
    }

    // Caminos
    @Test
    void caminos() {
        Nodo<Integer> raiz = new Nodo<>(10);
        raiz.add(5);
        raiz.add(15);

        ListaSimplementeEnlazada<ListaSimplementeEnlazada<Integer>> caminoD = new ListaSimplementeEnlazada<>();
        assertTrue(raiz.getCaminoDatos(caminoD, 15));
        assertFalse(raiz.getCaminoDatos(caminoD, 99));

        ListaSimplementeEnlazada<Nodo<Integer>> caminoN = new ListaSimplementeEnlazada<>();
        assertTrue(raiz.getCaminoNodos(caminoN, 5));

        ListaSimplementeEnlazada<ListaSimplementeEnlazada<Integer>> lista = new ListaSimplementeEnlazada<>();
        raiz.ordenCentral(lista);
        raiz.preOrden(lista);
        raiz.postOrden(lista);
        assertEquals(9, lista.getSize());
    }

}