package MisEstructurasDeDatos.ListasPilasYColas;

/**
 * Clase que representa un nodo en una Lista Doblemente Enlazada
 */

public class NodoListaDE<T> {

    // Variables privadas de la instancia
    private final T dato;   // Al ser final, el dato no cambia una vez asignado
    private NodoListaDE<T> siguiente;   // Hace referencia al siguiente nodo
    private NodoListaDE<T> anterior;   // Hace referencia al nodo anterior

    // Constructores

    /**
     * Construye una instancia NodoListaDE con el dato dado que apunta a null
     */
    public NodoListaDE(T dato) {
        this.dato = dato;
        this.siguiente = null;
        this.anterior = null;
    }

    // Getter para la variable dato
    public T getDato() {
        return dato;
    }

    // Getter para el elemento siguiente
    public NodoListaDE<T> getSiguiente() {
        return siguiente;
    }

    // Setter para el elemento siguiente
    public void setSiguiente(NodoListaDE<T> siguiente) {
        this.siguiente = siguiente;
    }

    // Como la variable dato es final no tiene Setter

    // Getter para el elemento anterior
    public NodoListaDE<T> getAnterior() {
        return anterior;
    }

    // Setter para el elemento anterior
    public void setAnterior(NodoListaDE<T> anterior) {
        this.anterior = anterior;
    }

    /**
     * Construimos un metodo toString() para esta instancia con la estructura Nodo(dato)
     */
    @Override
    public String toString() {
        return "NodoDE(" + dato + ")";
    }

}
