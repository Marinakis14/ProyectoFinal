package MisEstructurasDeDatos.ListasPilasYColas;


public class IteradorLSE<T> implements InterfazMiIterador<T> {
    // Variables de la clase
    private NodoListaSE<T> actual;

    // Constructor
    public IteradorLSE(NodoListaSE<T> comienzo) {
        this.actual = comienzo; // El iterador empieza aputando al inicio de la lista
    }

    // Metodo para saber si la lista tiene un siguiente elemento
    @Override
    public boolean hasNext() {
        return actual != null;
    }

    // Metodo que devuelve el elemento de la lista y pasa al siguiente elemento
    @Override
    public T next() {
        if (hasNext()) { // comprueba que hay un siguiente dato
            T dato = actual.getDato();
            actual = actual.getSiguiente(); // pasa al siguiente elemento de la lista
            return dato; // devuelve el elemento actual
        }
        // Si no hay siguiente dato
        return null;
    }

}
