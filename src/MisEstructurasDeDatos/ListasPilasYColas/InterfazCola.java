package MisEstructurasDeDatos.ListasPilasYColas;

/**
 * Interfaz para el TAD de tipo cola
 *
 * @param <T> Tipo de datos que almacenará la cola
 */

public interface InterfazCola<T extends Comparable<T>> {

    /**
     * Encolar: añade un elemento al final de la cola
     */
    void enqueue(T dato);

    /**
     * Desencolar: quita el primer elemento de la cola
     */
    T dequeue();

    /**
     * Devuelve el primer elemento sin quitarlo
     */
    T front();

    /**
     * Devuelve el ultimo elemento sin quitarlo
     */
    T back();

    /**
     * Buscar un elemento y devolver en que posicion de la cola esta (empezando a contar por el primero en entrar)
     */
    int search(T dato);

    /**
     * Copiar la cola entera con los mismos elementos en el mismo orden
     */
    InterfazCola<T> copy();

    /**
     * Devuelve si la cola esta vacia o no
     */
    boolean isEmpty();

    /**
     * Devuelve el tamaño de la cola
     */
    int getSize();

    /**
     * Vacía la cola por completo
     */
    void clear();

}
