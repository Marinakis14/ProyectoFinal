package MisEstructurasDeDatos.ListasPilasYColas;


public class LDEOrdenada<T extends Comparable<T>> extends ListaDoblementeEnlazada<T> {

    // Para que solamente se pueda usar el nuevo metodo addOrdenado y asi la lista este correcta en esta clase,
    // sobreescribimos todos los demás metodos del tipo add de la clase ListaDoblementeEnlazada para evitar problemas

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
        NodoListaDE<T> nuevoElemento = new NodoListaDE<>(dato);
        if (isEmpty()) {
            setPrimero(nuevoElemento);
            setUltimo(nuevoElemento);
        } else if (getPrimero().getDato().compareTo(dato) >= 0) {
            nuevoElemento.setSiguiente(getPrimero());
            getPrimero().setAnterior(nuevoElemento);
            setPrimero(nuevoElemento);
        } else if (getUltimo().getDato().compareTo(dato) <= 0) {
            super.addEnd(dato); // usamos addEnd para insertar al final
            return;
        } else {
            NodoListaDE<T> actual = getPrimero();
            while (actual != null && actual.getDato().compareTo(dato) < 0) {
                actual = actual.getSiguiente();
            }
            nuevoElemento.setSiguiente(actual);
            nuevoElemento.setAnterior(actual.getAnterior());
            actual.getAnterior().setSiguiente(nuevoElemento);
            actual.setAnterior(nuevoElemento);
        }
        setSize(getSize() + 1);
    }

}
