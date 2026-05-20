package MisEstructurasDeDatos.ListasPilasYColas;


public class IteradorLC<T> implements InterfazMiIterador<T> {

    // Variables de la clase
    private NodoListaSE<T> actual;
    private NodoListaSE<T> primero; // lo necesitamos para guardar cual es el primero dato y saber cuando volvemos a el
    private boolean esLaPrimeraVuelta; // lo necesitamos para saber si es la primera vez que pasamos por primero

    // Constructor
    public IteradorLC(NodoListaSE<T> primero) {
        this.actual = primero; // El iterador empieza aputando al primero elemento que nos dan
        this.primero = primero; // Para dejar guardado cual era el primer elemento
        this.esLaPrimeraVuelta = true; // Lo acabamos de crear
    }

    // Metodo para saber si la lista ha dado una vuelta completa o no
    @Override
    public boolean hasNext() { // cuando llega al ultimo elemento devuelve false
        // comprobar que la lista no esta vacia
        if (primero == null) return false;

        // hay que devolver true si el elemento es distinto del primer elemento
        // o si es la primera vez que pasamos por el primer elemento (EsLaPrimeraVez = true)
        if (esLaPrimeraVuelta || actual != primero) return true;
        return false;
    }
    // De esta manera el hasNext() nos sirve para crear bucles que solo den una vuelta a la lista
    // Si queremos dar mas de una vuelta podemos usar el next() todas las veces que queramos porque
    // siempre va a haber un siguiente elemento

    // Metodo que devuelve el elemento de la lista y pasa al siguiente elemento
    @Override
    public T next() {
        // comprobar que la lista no esta vacia
        if (primero == null) return null;

        T dato = actual.getDato();
        actual = actual.getSiguiente(); // pasa al siguiente elemento de la lista

        // como ya no puede ser la primera vez que pasamos por el primer elemento tenemos que cambiar
        esLaPrimeraVuelta = false;

        return dato; // devuelve el elemento actual
    }

}
