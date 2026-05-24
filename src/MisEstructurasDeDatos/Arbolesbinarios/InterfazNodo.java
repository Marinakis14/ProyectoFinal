package MisEstructurasDeDatos.Arbolesbinarios;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;

public interface InterfazNodo<T extends Comparable<T>> {

    // --- Getters ---
    Nodo<T> getIzquierda();
    Nodo<T> getDerecha();
    ListaSimplementeEnlazada<T> getDatos();

    // --- Operaciones y Métodos Recursivos ---
    int getGrado();
    int getAltura();
    boolean isNodoInArbol(T dato);
    int getNivel(T dato);
    void getListaDatosNivel(ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> elementosArbol, int nivel);
    int numeroHijos();
    void ADD(T dato);
    void DEL(T dato);
    ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> getCamino();

    // --- Estructura del Árbol ---
    boolean isArbolHomogeneo(Nodo<T> nodoActual);
    void isArbolCompleto();
    void isArbolCasiCompleto();

    // --- Métodos de Recorrido ---
    void ordenCentral(ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> elementosArbol);
    void preOrden(ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> elementosArbol);
    void postOrden(ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> elementosArbol);

    // --- Representación ---
    String toString();
}