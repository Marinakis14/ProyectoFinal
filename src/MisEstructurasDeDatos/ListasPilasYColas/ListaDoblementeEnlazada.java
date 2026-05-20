package MisEstructurasDeDatos.ListasPilasYColas;


public class ListaDoblementeEnlazada<T extends Comparable<T>> implements InterfazListas<T> {

    // Atributos de la instancia
    private NodoListaDE<T> primero;
    private NodoListaDE<T> ultimo;
    private int tamaño;

    // Constructor para inicializar la lista vacía
    public ListaDoblementeEnlazada() {
        this.primero = null;
        this.ultimo = null;
        this.tamaño = 0;
    }

    // Metodo para verificar si la lista esta vacia o no
    @Override
    public boolean isEmpty() {
        return primero == null;
    }

    // Metodo que devuelve el tamaño de la lista
    @Override
    public int getSize() {
        return tamaño;
    }

    // Metodo para modificar el tamaño de la lista
    public void setSize(int tamaño) {
        this.tamaño = tamaño;
    }

    // Metodo que devuelve el primer elemento de la lista
    public NodoListaDE<T> getPrimero() {
        return primero;
    }

    // Metodo para modificar el primer elemento de la lista
    public void setPrimero(NodoListaDE<T> primero) {
        this.primero = primero;
    }

    // Metodo que devuelve el ultimo elemento de la lista
    public NodoListaDE<T> getUltimo() {
        return ultimo;
    }

    // Metodo para modificar el ultimo elemento de la lista
    public void setUltimo(NodoListaDE<T> ultimo) {
        this.ultimo = ultimo;
    }

    // Metodo para añadir un elemento al final de la lista
    @Override
    public void addEnd(T dato) {
        if (dato == null) return; // Evita que se añada un dato vacio a la lista, que no tendria sentido

        NodoListaDE<T> nuevoElemento = new NodoListaDE<>(dato);

        // Comprobar si la lista esta vacia
        if (isEmpty()) {
            primero = nuevoElemento;
            ultimo = nuevoElemento; // como solo hay un elemento el primero es el mismo que el ultimo
        } else {
            ultimo.setSiguiente(nuevoElemento); // conectamos el ultimo elemento de la lista con el que añadimos
            nuevoElemento.setAnterior(ultimo); // conectamos el elemento que añadimos con el ultimo de la lista
            ultimo = nuevoElemento; // el ultimo elemento de la lista ahora es el que hemos añadido
        }
        tamaño++;
    }

    // Metodo para añadir un elemento al principio de la lista
    @Override
    public void addStart(T dato) {
        if (dato == null) return; // Evita que se añada un dato vacio a la lista, que no tendria sentido

        NodoListaDE<T> nuevoElemento = new NodoListaDE<>(dato);

        // Comprobar si la lista esta vacia
        if (isEmpty()) {
            primero = nuevoElemento;
            ultimo = nuevoElemento; // como solo hay un elemento el primero es el mismo que el ultimo
        } else {
            nuevoElemento.setSiguiente(primero); // conectamos el elemento que añadimos con el primer elemento
            primero.setAnterior(nuevoElemento); // conectamos el primer elemento con el que añadimos
            primero = nuevoElemento; // el primer elemento de la lista ahora es el que hemos añadido
        }
        tamaño++;
    }

    // Metodo para añadir un elemento en cualquier lugar de la lista
    @Override
    public void addAnywhere(T dato, int posicion) {
        if (dato == null) return; // Evita que se añada un dato vacio a la lista, que no tendria sentido

        // comprobar que la posicion dada es valida
        if (posicion < 0) return;
        if (posicion > tamaño) return;

        if (posicion == 0) { // comprobamos si se añade al principio
            // usamos addStart
            addStart(dato);
        } else if (posicion == tamaño) { // comprobamos si se añade al final
            // usamos addEnd
            addEnd(dato);
        } else { // si esta en medio de la lista
            // llegamos hasta el elemento anterior a la posicion donde queremos añadir el nuevo elemento
            NodoListaDE<T> nuevoElemento = new NodoListaDE<>(dato);
            NodoListaDE<T> actual = primero;

            for (int i = 0; i < posicion - 1; i++) {
                actual = actual.getSiguiente();
            }
            // lo añadimos
            nuevoElemento.setSiguiente(actual.getSiguiente());
            actual.getSiguiente().setAnterior(nuevoElemento);
            actual.setSiguiente(nuevoElemento);
            nuevoElemento.setAnterior(actual);

            tamaño++;
        }
    }

    // Metodo para borrar un elemento
    @Override
    public T del(T dato) {
        // Comprobar que la lista no esta vacia
        if (isEmpty()) return null;

        // Comprobar que el dato a borrar no es null
        if (dato == null) return null;

        NodoListaDE<T> actual = primero;

        // Buscamos el dato y lo borramos
        while (actual != null) {
            if (actual.getDato().compareTo(dato) == 0) { // si encontramos el dato a borrar
                T datoEliminado = actual.getDato();
                // Ahora hay que tener en cuenta todas las posibilidades
                if (tamaño == 1) { // Si es el unico elemento de la lista
                    primero = null;
                    ultimo = null;
                } else if (actual == primero) { // si es el primer elemento de la lista pero hay mas despues
                    primero = primero.getSiguiente();
                    primero.setAnterior(null);
                } else if (actual == ultimo) { // si es el ultimo pero hay mas elementos antes
                    ultimo = ultimo.getAnterior();
                    ultimo.setSiguiente(null);
                } else { // si esta en medio
                    actual.getAnterior().setSiguiente(actual.getSiguiente()); // el anterior salta al siguiente
                    actual.getSiguiente().setAnterior(actual.getAnterior()); // el siguiente salta al anterior
                }
                tamaño--;
                return datoEliminado;
            }
            actual = actual.getSiguiente();
        }

        // Si no se encuentra el dato en toda la lista
        return null;
    }

    // Metodo para borrar el primer elemento de la lista
    @Override
    public T delFirst() {
        // Comprobar que la lista no esta vacia
        if (isEmpty()) return null;

        // Borrar el primer elemento
        T datoEliminado = primero.getDato();

        // Hay que tener en cuenta que la lista puede tener solo un elemento
        if (tamaño == 1) {
            primero = ultimo = null;
        } else {
            primero.getSiguiente().setAnterior(null);
            primero = primero.getSiguiente();
        }
        tamaño--;
        return datoEliminado;
    }

    // Como la lista esta doblemente enlazada es muy facil crear el mismo metodo pero para borrar el ultimo elemento
    public T delLast() {
        // Comprobar que la lista no esta vacia
        if (isEmpty()) return null;

        // Borrar el ulitmo elemento
        T datoEliminado = ultimo.getDato();

        // Hay que tener en cuenta que la lista puede tener solo un elemento
        if (tamaño == 1) {
            primero = ultimo = null;
        } else {
            ultimo.getAnterior().setSiguiente(null);
            ultimo = ultimo.getAnterior();
        }
        tamaño--;
        return datoEliminado;
    }

    // Metodo para vaciar la lista por completo
    @Override
    public void clear() {
        this.primero = null;
        this.ultimo = null;
        this.tamaño = 0;
    }

    // Metodo para sacar un dato
    @Override
    public T get(T dato) {
        // Comprobar que la lista no esta vacia
        if (isEmpty()) return null;

        // Comprobar que el dato a buscar no es null
        if (dato == null) return null;

        NodoListaDE<T> actual = primero;

        // Recorremos la lista buscando el dato
        while (actual != null) {
            if (actual.getDato().compareTo(dato) == 0) {
                return actual.getDato();
            }
            // Si no es el dato que buscamos pasamos al siguiente nodo de la lista
            actual = actual.getSiguiente();
        }

        // Si el dato no esta en la lista
        return null;
    }

    // Metodo para buscar un elemento que se encuentra en una posicione especifica de la lista
    @Override
    public T get(int posicion) {
        // comprobar que la posicion dada es valida
        if (posicion < 0) return null;
        if (posicion >= tamaño) return null;

        // llegamos hasta la posicion de la lista
        NodoListaDE<T> actual = primero;
        for (int i = 0; i < posicion; i++) {
            actual = actual.getSiguiente();
        }
        // ahora que ya hemos llegado devolvemos el elemento de esa posicion
        return actual.getDato();
    }

    // Metodo para devolver la primera posicion en la que se encuentra un elemento concreto en la lista
    @Override
    public int getPosicion(T dato) {
        NodoListaDE<T> actual = primero;
        // vamos contando la posicion hasta encontrar el dato
        int contador = 0;
        while (actual != null) {
            if (actual.getDato().compareTo(dato) == 0) { // si lo encontramos devolvemos la posicion
                return contador;
            }
            actual = actual.getSiguiente();
            contador++;
        }
        // si el elemento no esta en la lista devuelve un -1
        return -1;
    }

    // Metodo para comprobar si un elemento esta en la lista
    @Override
    public boolean contains(T dato) {
        return get(dato) != null;
    }

    // Metodo que devuelve una nueva instancia del iterador especifico para LDE empezando por el primero dato
    @Override
    public InterfazMiIterador<T> getIterador() {
        return new IteradorLDE<>(primero, true);
    }

    // Devuelve una nueva instancia de iterador específico para LDE que recorre la lista en sentido contrario
    // empezando por el último elemento
    public IteradorLDE<T> getIteradorInverso() {
        return new IteradorLDE<>(ultimo, false);
    }

    // Metodo para crear una cadena con los elementos de la lista
    @Override
    public String toString() {
        if (isEmpty()) return "[LISTA VACIA]";

        String lista = "[ ";
        NodoListaDE<T> actual = primero;
        while (actual != null) { // para ir añadiendo los elementos de la lista a la cadena
            lista += actual.getDato();
            if (actual.getSiguiente() != null) {
                lista += " <-> ";
            }
            actual = actual.getSiguiente();
        }
        lista += " ]";
        return lista;
    }

}
