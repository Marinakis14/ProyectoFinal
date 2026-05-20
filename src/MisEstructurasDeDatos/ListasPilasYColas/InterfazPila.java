package MisEstructurasDeDatos.ListasPilasYColas;

/**
 * Interfaz para el TAD de tipo pila
 *
 * @param <T> Tipo de datos que almacenará la pila
 */

public interface InterfazPila<T extends Comparable<T>> {

    /**
     * Apilar o añadir un elemento a la pila (en la parte de arriba)
     */
    void push(T dato);

    /**
     * Desapilar o quitar un elemento de la pila (el de arriba)
     */
    T pop();

    /**
     * Copiar la pila entera con los mismos elementos en el mismo orden
     */
    InterfazPila<T> copy();

    /**
     * Ver el elemento que está mas arriba en la pila sin quitarlo
     */
    T top();

    /**
     * Buscar un elemento y devolver en que posicion de la pila esta (empezando a contar desde arriba)
     */
    int search(T dato);

    /**
     * Devuelve si la pila esta vacia o no
     */
    boolean isEmpty();

    /**
     * Devuelve el tamaño de la pila
     */
    int getSize();

    /**
     * Vaciar la pila por completo
     */
    void clear();

}
