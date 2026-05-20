package MisEstructurasDeDatos.ListasPilasYColas;


public class IteradorLDE<T> implements InterfazMiIterador<T> {
    // Variables de la clase
    private NodoListaDE<T> actual;
    private boolean tipo; // true: hacia delante, false: hacia atrás

    // Constructor
    public IteradorLDE(NodoListaDE<T> comienzo, boolean tipo) {
        this.actual = comienzo;
        this.tipo = tipo;
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
            // ahora tenemos dos opciones
            if (tipo) { // si es true se recorre la lista de principio a fin
                actual = actual.getSiguiente(); // pasa al siguiente elemento de la lista
            } else { // si es false se recorre la lista en sentido contrario
                actual = actual.getAnterior(); // pasa al elemento anterior de la lista
            }
            return dato; // devuelve el elemento actual
        }
        // Si no hay siguiente dato
        return null;
    }

}
