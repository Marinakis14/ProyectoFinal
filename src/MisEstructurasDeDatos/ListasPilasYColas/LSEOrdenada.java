package MisEstructurasDeDatos.ListasPilasYColas;


public class LSEOrdenada<T extends Comparable<T>> extends ListaSimplementeEnlazada<T> {

    // Para que solamente se pueda usar el nuevo metodo addOrdenado y asi la lista este correcta en esta clase,
    // sobreescribimos todos los demás metodos del tipo add de la clase ListaSimplementeEnlazada para evitar problemas

    @Override
    public void addEnd(T dato) {
        addOrdenado(dato);
    }

    @Override
    public void addStart(T dato) {
        addOrdenado(dato);
    }

    @Override
    public void addAnywhere(T dato, int posicion) {
        addOrdenado(dato);
    }

    public void addOrdenado(T dato) {
        NodoListaSE<T> nuevoElemento = new NodoListaSE<>(dato);
        if (getPrimero() == null || getPrimero().getDato().compareTo(dato) > 0) {
            nuevoElemento.setSiguiente(getPrimero());
            setPrimero(nuevoElemento);
        } else {
            NodoListaSE<T> actual = getPrimero();
            while (actual.getSiguiente() != null && actual.getSiguiente().getDato().compareTo(dato) < 0) {
                actual = actual.getSiguiente();
            }
            nuevoElemento.setSiguiente(actual.getSiguiente());
            actual.setSiguiente(nuevoElemento);
        }
        setSize(getSize() + 1);
    }

}
