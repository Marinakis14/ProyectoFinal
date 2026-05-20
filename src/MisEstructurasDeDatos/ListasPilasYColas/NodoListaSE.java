package MisEstructurasDeDatos.ListasPilasYColas;

/**
 * Clase que representa un nodo en una Lista Simplemente Enlazada
 */

public class NodoListaSE<T> {

    // Variables privadas de la instancia
    private final T dato;   // Al ser final, el dato no cambia una vez asignado
    private NodoListaSE<T> siguiente;   // Hace referencia al siguiente nodo

    // Constructores

    /**
     * Construye una instancia NodoListaSE con el dato dado que apunta a null
     */
    public NodoListaSE(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    /**
     * Construye una instancia NodoListaSE con el dato y el Nodo dado que apunta a dicho Nodo
     */
    public NodoListaSE(T dato, NodoListaSE<T> siguiente) {
        this.dato = dato;
        this.siguiente = siguiente;
    }

    // Getter para la variable dato
    public T getDato() {
        return dato;
    }

    // Getter para el elemento siguiente
    public NodoListaSE<T> getSiguiente() {
        return siguiente;
    }

    // Setter para el elemento siguiente
    public void setSiguiente(NodoListaSE<T> siguiente) {
        this.siguiente = siguiente;
    }

    // Como la variable dato es final no tiene Setter

    /**
     * Construimos un metodo toString() para esta instancia con la estructura Nodo(dato)
     */
    @Override
    public String toString() {
        return "NodoSE(" + dato + ")";
    }

}
