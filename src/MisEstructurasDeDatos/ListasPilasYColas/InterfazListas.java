package MisEstructurasDeDatos.ListasPilasYColas;

/**
 * Interfaz genérica para los TADs de tipo Lista
 *
 * @param <T> Tipo de datos que almacenará la lista
 */

public interface InterfazListas<T> {

    /**
     * Inserta un dato al final de la lista
     */
    void addEnd(T dato);

    /**
     * Insertar un dato al principio de la lista
     */
    void addStart(T dato);

    /**
     * Insertar un dato en una posicion especifica de la lista
     */
    void addAnywhere(T dato, int posicion);

    /**
     * Busca y devuelve un dato específico
     */
    T get(T dato);

    /**
     * Busca y devuelve el dato que se encuentra en una posicion especifica de la lista
     */
    T get(int posicion);

    /**
     * Busca y devuelve la primera posicion en la que se encuentra un elemento concreto en la lista
     */
    int getPosicion(T dato);

    /**
     * Comprueba si un dato especifico esta o no en la lista
     */
    boolean contains(T dato);

    /**
     * Elimina un dato de la lista y lo devuelve
     */
    T del(T dato);

    /**
     * Elimina el primer dato de la lista y lo devuelve (lo hemos creado especialmente para la pila y la cola)
     */
    T delFirst();

    /**
     * Vacia la lista por completo
     */
    void clear();

    /**
     * Indica si la lista no tiene elementos
     */
    boolean isEmpty();

    /**
     * Devuelve el número de elementos actuales
     */
    int getSize();

    /**
     * Devuelve un iterador para recorrer la lista
     */
    InterfazMiIterador<T> getIterador();

}
