package MisEstructurasDeDatos.ListasPilasYColas;

/**
 * Interfaz para el Iterador
 */

public interface InterfazMiIterador<T> {

    /**
     * Comprueba si hay un siguiente elemento para recorrer
     */
    boolean hasNext();

    /**
     * Devuelve el siguiente elemento y avanza el puntero
     */
    T next();

}
