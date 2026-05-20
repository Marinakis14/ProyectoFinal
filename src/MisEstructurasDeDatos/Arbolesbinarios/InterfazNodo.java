package MisEstructurasDeDatos.Arbolesbinarios;

import java.util.ArrayList;

public interface InterfazNodo<T extends Comparable<T>> {

    // --- Getters ---
    Nodo<T> getIzquierda();
    Nodo<T> getDerecha();
    ArrayList<T> getDatos();

    // --- Operaciones y Métodos Recursivos ---
    int getGrado();
    int getAltura();
    boolean isNodoInArbol(T dato);
    int getNivel(T dato);
    void getListaDatosNivel(ArrayList<ArrayList<T>> elementosArbol, int nivel);
    int numeroHijos();
    void ADD(T dato);
    void DEL(T dato);
    ArrayList<ArrayList<T>> getCamino();

    // --- Estructura del Árbol ---
    boolean isArbolHomogeneo(Nodo<T> nodoActual);
    void isArbolCompleto();
    void isArbolCasiCompleto();

    // --- Métodos de Recorrido ---
    void ordenCentral(ArrayList<ArrayList<T>> elementosArbol);
    void preOrden(ArrayList<ArrayList<T>> elementosArbol);
    void postOrden(ArrayList<ArrayList<T>> elementosArbol);

    // --- Representación ---
    String toString();
}