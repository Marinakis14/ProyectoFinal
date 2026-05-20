package MisEstructurasDeDatos.ListasPilasYColas;


public class Cola<T extends Comparable<T>> implements InterfazCola<T> {

    // Variables
    // Vamos a usar una Lista Doblemente Enlazada para la cola ya que asi podemos acceder rapidamente
    // no solo al principio sino también al final, para sacar elementos rapidamente
    private ListaDoblementeEnlazada<T> lista;

    // Constructor
    public Cola() {
        // creamos una nueva LDE vacia
        this.lista = new ListaDoblementeEnlazada<>();
    }

    // Metodo para añadir elementos al final de la cola
    @Override
    public void enqueue(T dato) {
        // en una cola los elementos siempre se añaden al final
        lista.addEnd(dato);
    }

    // Metodo para sacar el primer elemento de la cola
    @Override
    public T dequeue() {
        // en una cola los elementos siempre se sacan del principio
        return lista.delFirst();
    }

    // Metodo que devuelve el primer elemento sin sacarlo
    @Override
    public T front() {
        if (isEmpty()) return null; // Comprobamos que no este vacia
        return lista.get(0); // Devolvemos el primer elemento de la cola
    }

    // Metodo que devuelve el ultimo elemento sin sacarlo
    @Override
    public T back() {
        if (isEmpty()) return null; // Comprobamos que no este vacia
        return lista.get(lista.getSize() - 1); // Devolvemos el ultimo elemento de la cola
    }

    // Metodo que busca y devuelve un elemento concreto de la cola
    @Override
    public int search(T dato) {
        int posicion = lista.getPosicion(dato); // reutilizamos el metodo getPosicion() de nuestra lista
        // hay que tener en cuenta la posibilidad de que no este el elemento
        // nuestro metodo ya nos devolvia -1 si no estaba en la lista
        if (posicion == -1) return -1;
        return posicion + 1; // sumamos 1 para devolver la posicion (0 es la primera posicion, 1 la segunda...)
    }

    // Metodo para crear una copia de la cola con los mismo elementos y en el mismo orden
    @Override
    public InterfazCola<T> copy() {
        Cola<T> copiaDeLaCola = new Cola<>();
        // para hacer una copia como tenemos todos los elementos de la cola guardados en una lista
        // solo tenemos que duplicar esa lista y hacer esa lista la base de nuestra nueva cola
        InterfazMiIterador<T> iterador = lista.getIterador(); // usamos el iterador para recorrer la lista que tenemos

        ListaDoblementeEnlazada<T> copiaDeLaLista = copiaDeLaCola.lista; // sacamos la lista de la nueva cola

        while (iterador.hasNext()) {
            copiaDeLaLista.addEnd(iterador.next()); // lo añadimos al final para que tenga el mismo orden
        }

        return copiaDeLaCola;
    }

    // Devuelve si la cola esta vacia o no
    @Override
    public boolean isEmpty() {
        // reutilizamos el metodo de la LDE
        return lista.isEmpty();
    }

    // Devuelve el tamaño de la cola
    @Override
    public int getSize() {
        // reutilizamos el metodo de la LDE
        return lista.getSize();
    }

    // Vacia la cola por completo
    @Override
    public void clear() {
        // reutilizamos el metodo de la LDE
        lista.clear();

    }

    // Metodo para crear una cadena con los elementos de la pila
    @Override
    public String toString() {
        // comprobamos que no esta vacia
        if (isEmpty()) return "[COLA VACIA]";

        // creamos una cadena horizontal con los elementos de la cola
        String cola = "PRINCIPIO ||";
        // para recorrer los elementos usamos el iterador
        InterfazMiIterador<T> iterador = lista.getIterador();
        while (iterador.hasNext()) {
            cola += " " + iterador.next() + " |"; // esta estructura es para que la cadena salga bien
        }
        cola += "| FINAL";

        return cola;
    }
}
