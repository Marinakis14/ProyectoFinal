package MisEstructurasDeDatos.ListasPilasYColas;


public class Pila<T extends Comparable<T>> implements InterfazPila<T> {

    // Variable privada de la instancia pila
    // Vamos a utilizar la ListaSimplementeEnlazada que hemos creado para la pila
    private ListaSimplementeEnlazada<T> lista;

    // Constructor para inicializar la pila
    public Pila() {
        this.lista = new ListaSimplementeEnlazada<>();
    }

    // Metodo para apilar o añadir un elemento a la pila (en la parte de arriba)
    @Override
    public void push(T dato) {
        // en una pila el ultimo que llega se pone siempre arriba
        lista.addStart(dato); // este metodo nos permite añadir el elemento de manera inmediata
    }

    // Metodo para desapilar o quitar un elemento de la pila (el de arriba)
    @Override
    public T pop() {
        // en una pila el se saca siempre el ultimo elemento en entrar
        return lista.delFirst(); // usamos el metodo para borrar un dato de la lista de manera inmediata y devolverlo
    }

    // Metodo para copiar la pila entera con los mismos elementos en el mismo orden
    @Override
    public InterfazPila<T> copy() {
        Pila<T> copiaDeLaPila = new Pila<>();
        // para hacer una copia como tenemos todos los elementos de la pila guardados en una lista
        // solo tenemos que duplicar esa lista y hacer esa lista la base de nuestra nueva pila
        InterfazMiIterador<T> iterador = lista.getIterador(); // usamos el iterador para recorrer la lista que tenemos

        ListaSimplementeEnlazada<T> copiaDeLaLista = copiaDeLaPila.lista; // sacamos la lista de la nueva pila

        while (iterador.hasNext()) {
            copiaDeLaLista.addEnd(iterador.next()); // lo añadimos al final para que tenga el mismo orden
        }

        return copiaDeLaPila;
    }

    // Metodo para ver el elemento que está mas arriba en la pila sin quitarlo
    @Override
    public T top() {
        // hay que comprobar que la pila no este vacia
        if (isEmpty()) return null;
        return lista.get(0); // usamos el metodo de la lista para obtener un elemento dada su posicion
    }

    // Metodo para buscar un elemento y devolver en que posicion de la pila esta (empezando a contar desde arriba)
    @Override
    public int search(T dato) {
        int posicion = lista.getPosicion(dato); // reutilizamos el metodo getPosicion() de nuestra lista
        // hay que tener en cuenta la posibilidad de que no este el elemento
        // nuestro metodo ya nos devolvia -1 si no estaba en la lista
        if (posicion == -1) return -1;
        return posicion + 1; // sumamos 1 para devolver la posicion (0 es la primera posicion, 1 la segunda...)
    }

    // Metodo que devuelve si la pila esta vacia o no
    @Override
    public boolean isEmpty() {
        return lista.isEmpty(); // reciclamos el metodo isEmpty() de la lista
    }

    // Metodo que devuelve el tamaño de la pila
    @Override
    public int getSize() {
        return lista.getSize(); // reciclamos el metodo getSize() de la lista
    }

    // Metodo que vacia la pila por completo
    @Override
    public void clear() {
        lista.clear(); // reciclamos el metodo clear() de la lista
    }

    // Metodo para crear una cadena con los elementos de la pila
    @Override
    public String toString() {
        // comprobamos que no esta vacia
        if (isEmpty()) return "[PILA VACIA]";

        // creamos una cadena vertical con los elementos de la pila
        String pila = "---- PILA ----\n";
        // para recorrer los elementos usamos el iterador
        InterfazMiIterador<T> iterador = lista.getIterador();
        while (iterador.hasNext()) {
            pila += " - " + iterador.next() + "\n";
        }

        return pila;
    }

}
