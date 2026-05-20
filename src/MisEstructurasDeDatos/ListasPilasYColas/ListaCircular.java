package MisEstructurasDeDatos.ListasPilasYColas;


public class ListaCircular<T extends Comparable<T>> implements InterfazListas<T> {

    // Atributos de la instancia
    private NodoListaSE<T> primero;
    private NodoListaSE<T> ultimo;
    private int tamaño;

    // Constructor para inicializar la lista vacía
    public ListaCircular() {
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

    // Metodo para añadir un elemento al final de la lista
    @Override
    public void addEnd(T dato) {
        if (dato == null) return; // Evita que se añada un dato vacio a la lista, que no tendria sentido

        NodoListaSE<T> nuevoElemento = new NodoListaSE<>(dato);

        // Comprobar si la lista esta vacia
        if (isEmpty()) {
            primero = nuevoElemento;
            ultimo = nuevoElemento;
            // como es una lista circular hay que cerrar el circulo
            ultimo.setSiguiente(primero);
        } else {
            ultimo.setSiguiente(nuevoElemento);
            ultimo = nuevoElemento;
            ultimo.setSiguiente(primero);
        }
        tamaño++;
    }

    // Metodo para añadir un elemento al principio de la lista
    @Override
    public void addStart(T dato) {
        if (dato == null) return; // Evita que se añada un dato vacio a la lista, que no tendria sentido

        NodoListaSE<T> nuevoElemento = new NodoListaSE<>(dato);

        // Comprobar si la lista esta vacia
        if (isEmpty()) {
            primero = nuevoElemento;
            ultimo = nuevoElemento;
            // como es una lista circular hay que cerrar el circulo
            ultimo.setSiguiente(primero);
        } else {
            nuevoElemento.setSiguiente(primero);
            primero = nuevoElemento; // el nuevo elemento ahora es el primero y apunta al que estaba primero antes
            // como es una lista circular hay que conectar el primer elemento con el ultimo
            ultimo.setSiguiente(nuevoElemento);
        }
        tamaño++;
    }

    // Metodo para añadir un elemento en cualquier lugar de la lista
    @Override
    public void addAnywhere(T dato, int posicion) {
        if (dato == null) return; // Evita que se añada un dato vacio a la lista, que no tendria sentido

        // comprobar que la posicion dada es valida
        if (posicion < 0) return;
        if (posicion >= tamaño && tamaño != 0) return; // si el tamaño es 0 se puede añadir un elemento en la posicion 0

        if (posicion == 0) {
            // usamos addStart
            addStart(dato);
        } else if (posicion == tamaño) {
            // usamos addEnd
            addEnd(dato);
        } else {
            // llegamos hasta el elemento anterior a la posicion donde queremos añadir el nuevo elemento
            NodoListaSE<T> nuevoElemento = new NodoListaSE<>(dato);
            NodoListaSE<T> actual = primero;

            for (int i = 0; i < posicion - 1; i++) {
                actual = actual.getSiguiente();
            }
            // lo colocamos en su posicion
            nuevoElemento.setSiguiente(actual.getSiguiente());
            actual.setSiguiente(nuevoElemento);

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

        // Si el dato esta en el primer nodo podemos reusar nuestro metodo delFirst()
        if (primero.getDato().compareTo(dato) == 0) {
            return delFirst();
        }

        // Si el dato esta en otro nodo que no sea el primero
        NodoListaSE<T> anterior = primero;
        NodoListaSE<T> actual = primero.getSiguiente();

        // igual que antes tenemos que cambiar un poco el bucle para que no siga indefinidamente
        while (actual != primero) {
            if (actual.getDato().compareTo(dato) == 0) { // si encontramos el dato a borrar
                T datoEliminado = actual.getDato();
                anterior.setSiguiente(actual.getSiguiente());

                // si borramos el último dato hay que volver a "cerrar el circulo"
                if (actual == ultimo) {
                    ultimo = anterior;
                    ultimo.setSiguiente(primero);
                }

                tamaño--;
                return datoEliminado;
            }
            anterior = actual;
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

        T datoEliminado = primero.getDato();
        // Si solo hay un elemento
        if (tamaño == 1) {
            primero = null;
            ultimo = null;
        } else {
            // Borrar el primer elemento
            primero = primero.getSiguiente();
            ultimo.setSiguiente(primero); // esto conecta el ultimo al "nuevo primero"
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

        NodoListaSE<T> actual = primero;

        // Recorremos la lista buscando el dato teniendo cuidado con el bucle
        while (actual.getSiguiente() != primero) {
            if (actual.getDato().compareTo(dato) == 0) {
                return actual.getDato();
            }
            // Si no es el dato que buscamos pasamos al siguiente nodo de la lista
            actual = actual.getSiguiente();
        }

        // Con este bucle podemos asegurarnos de que acaba pero no comprueba el ultimo elemento (se sale antes)
        // Comprobamos el ultimo elemento
        if (ultimo.getDato().compareTo(dato) == 0) {
            return ultimo.getDato();
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
        NodoListaSE<T> actual = primero;
        for (int i = 0; i < posicion; i++) {
            actual = actual.getSiguiente();
        }
        // ahora que ya hemos llegado devolvemos el elemento de esa posicion
        return actual.getDato();
    }

    // Metodo para devolver la primera posicion en la que se encuentra un elemento concreto en la lista
    @Override
    public int getPosicion(T dato) {
        NodoListaSE<T> actual = primero;
        // vamos contando la posicion hasta encontrar el dato
        int contador = 0;
        // hay que tener cuidado con el bucle
        while (actual.getSiguiente() != primero) {
            if (actual.getDato().compareTo(dato) == 0) { // si lo encontramos devolvemos la posicion
                return contador;
            }
            actual = actual.getSiguiente();
            contador++;
        }
        // al igual que antes con este bucle no comprobamos el ultimo elemento asi que lo hacemos ahora
        if (ultimo.getDato().compareTo(dato) == 0) {
            return contador;
        }

        // si el elemento no esta en la lista devuelve un -1
        return -1;
    }

    // Metodo para comprobar si un elemento esta en la lista
    @Override
    public boolean contains(T dato) {
        return get(dato) != null;
    }

    // Metodo que devuelve una nueva instancia del iterador especifico para LSE empezando por el primero dato
    @Override
    public InterfazMiIterador<T> getIterador() {
        return new IteradorLC<>(primero);
    }

    // Metodo para crear una cadena con los elementos de la lista
    @Override
    public String toString() {
        if (isEmpty()) return "[LISTA VACIA]";

        String lista = "--> "; // ponemos una flecha para simbolizar que el primero esta conectado con el ultimo
        NodoListaSE<T> actual = primero;

        // para ir añadiendo los elementos de la lista a la cadena con cuidado de no caer en un bucle infinito
        while (actual.getSiguiente() != primero) {
            lista += actual.getDato() + " -> ";
            actual = actual.getSiguiente();
        }

        // añadimos el ultimo elemento que nos ha faltado
        lista += ultimo.getDato() + " -->"; // flecha para simbolizar que el ultimo esta conectado con el primero
        return lista;
    }
}
