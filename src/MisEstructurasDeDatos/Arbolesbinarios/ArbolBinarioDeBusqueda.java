package MisEstructurasDeDatos.Arbolesbinarios;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;

public class ArbolBinarioDeBusqueda<T extends Comparable<T>> implements InterfazArbol<T> {

    // variables privadas de la clase
    private Nodo<T> raiz;

    // Constructores
    ArbolBinarioDeBusqueda() { // al principio el arbol esta vacio
        this.raiz = null;
    }

    ArbolBinarioDeBusqueda(Nodo<T> raiz) { // para crear un arbol con una raiz dada
        this.raiz = raiz;
    }

    ArbolBinarioDeBusqueda(T dato) { // para crear un arbol con un elemento dado
        Nodo<T> nuevaRaiz = new Nodo<>(dato);
        this.raiz = nuevaRaiz;
    }

    // Getters y Setters para la variable raiz
    @Override
    public Nodo<T> getRaiz() {
        return raiz;
    }

    @Override
    public void setRaiz(Nodo<T> raiz) {
        this.raiz = raiz;
    }

    /**
     * Todas las "preguntas" a las que puede responder el arbol
     */

    // Metodo para saber si el arbol esta vacio o no
    @Override
    public boolean isEmpty() {
        return raiz == null; // devuelve true si el arbol esta vacio
    }

    // Metodo que dado un nodo devuelve el nodo que tiene a la izquierda
    @Override
    public Nodo<T> getIzquierda(Nodo<T> nodo) {
        if (nodo != null) {
            return nodo.getIzquierda();
        }
        // Si esta vacio
        return null;
    }

    // Metodo que dado un nodo devuelve el nodo que tiene a la derecha
    @Override
    public Nodo<T> getDerecha(Nodo<T> nodo) {
        if (nodo != null) {
            return nodo.getDerecha();
        }
        // Si esta vacio
        return null;
    }

    // Metodo que devuelve el valor de un nodo
    @Override
    public T getDato(Nodo<T> nodo) {
        if (nodo != null) {
            return nodo.getDatos().get(0);
        }
        // Si esta vacio
        return null;
    }

    // Metodo que devuelve el valor de un nodo
    @Override
    public ListaSimplementeEnlazada<T> getDatos(Nodo<T> nodo) {
        if (nodo != null) {
            return nodo.getDatos();
        }
        // Si esta vacio
        return new ListaSimplementeEnlazada<>(); // Devolvemos una lista vacia
    }

    // Metodo que devuelve cuantas veces se ha introducido un mismo dato dado el nodo
    public int getRepeticionesNodo(Nodo<T> nodo) {
        if (nodo != null) {
            ListaSimplementeEnlazada<T> datosNodo = nodo.getDatos();
            return datosNodo.getSize();
        }
        // Si esta vacio
        return 0;
    }

    // Metodo que devuelve cuantas veces se ha introducido un mismo dato dado el dato
    @Override
    public int getRepeticionesDato(T dato) {
        if (dato != null && raiz != null) {
            return raiz.getRepeticionesNodo(dato);
        }
        // Si esta vacio
        return 0;
    }

    // Metodo para obtener el grado del arbol de manera recursiva
    @Override
    public int getGrado() {
        if (raiz == null) { // si el arbol esta vacio el grado es 0
            return 0;
        } else {
            return raiz.getGrado();
        }
    }

    // Metodo para obtener si un nodo pertenece a un arbol o no, (se aplica a la raiz del arbol)
    @Override
    public boolean isNodoInArbol(T dato) {
        if (raiz == null || dato == null) { // si el arbol esta vacio el nodo no puede pertenecer al arbol
            return false;
        } else {
            return raiz.isNodoInArbol(dato);
        }
    }

    // Metodo para obtener la profundidad o altura del arbol de manera recursiva
    @Override
    public int getAltura() {
        if (raiz == null) { // si el arbol estaba vacio la altura es 0
            return -1;
        } else {
            return raiz.getAltura() - 1; // Hacemos -1 porque sino el metodo cuenta la raiz como nivel 1 y queremos que la cuente como nivel 0
        }
    }

    // Metodo para obtener el nivel de un nodo
    @Override
    public int getNivel(T dato) {
        if (raiz == null || dato == null) { // si el arbol estaba vacio el nivel es 0
            return -1;
        } else {
            return raiz.getNivel(dato);
        }
    }

    // Metodo para obtener una lista de todos los datos de un nivel dado
    @Override
    public ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> getListaDatosNivel(int nivel) {
        // Creamos una lista para devolver
        ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> elementos = new ListaSimplementeEnlazada<>();

        // Comprobamos que el nivel dado es valido
        if (nivel > raiz.getAltura() || nivel < 0) {
            return new ListaSimplementeEnlazada<>(); // Devolvemos una lista vacia
        } else if (nivel == 0) { // el nivel 0 del arbol es la raiz
            elementos.addEnd(raiz.getDatos());
            return elementos;
        } else {
            // Utilizamos el metodo que tenemos en la clase Hoja2a.arbolesbinarios.Nodo para añadir todos los elementos del nivel dado
            raiz.getListaDatosNivel(elementos, nivel);
            return elementos;
        }
    }

    // Metodo para obtener una lista de todos los nodos de un nivel dado
    @Override
    public ListaSimplementeEnlazada<Nodo<T>> getListaNodosNivel(int nivel) {
        // Creamos una lista para devolver
        ListaSimplementeEnlazada<Nodo<T>> elementos = new ListaSimplementeEnlazada<>();

        // Comprobamos que el nivel dado es valido
        if (nivel > raiz.getAltura() || nivel < 0) {
            return new ListaSimplementeEnlazada<>(); // Devolvemos una lista vacia
        } else if (nivel == 0) { // el nivel 0 del arbol es la raiz
            elementos.addEnd(raiz);
            return elementos;
        } else {
            // Utilizamos el metodo que tenemos en la clase Hoja2a.arbolesbinarios.Nodo para añadir todos los elementos del nivel dado
            raiz.getListaNodosNivel(elementos, nivel);
            return elementos;
        }
    }

    // Metodo que devuelve una lista de todos los nodos hoja de nuestro arbol
    @Override
    public ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> getNodosHoja() {
        // Creamos la lista donde añadir los datos
        ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> hijos = new ListaSimplementeEnlazada<>();

        if (raiz == null) { // si el arbol estaba vacio no tiene hijos (devolvemos la lista vacia)
            return hijos;
        } else {
            raiz.getNodosHoja(hijos);
            return hijos;
        }
    }

    @Override
    public Nodo<T> getNodoMasPequeño() {
        if (raiz == null) { // si el arbol estaba vacio no tiene hijos
            return null;
        } else {
            return raiz.getNodoMasPequeño();
        }
    }

    @Override
    public Nodo<T> getNodoMasGrande() {
        if (raiz == null) { // si el arbol estaba vacio no tiene hijos
            return null;
        } else {
            return raiz.getNodoMasGrande();
        }
    }

    // Metodo que devuelve si el arbol es homogeneo o no con recursividad a traves de los subarboles
    @Override
    public boolean isArbolHomogeneo1() {
        int numeroHijos = raiz.numeroHijos();
        // queremos ver que todos los subarboles tienen el mismo numero de hijos
        ArbolBinarioDeBusqueda<T> arbolActual = new ArbolBinarioDeBusqueda<>(raiz);

        // creamos variables para ver si los subarboles tienen el mismo numero de hijos o no
        boolean izquierda = true;
        boolean derecha = true;

        if (numeroHijos != 0) { // si es 0 y no tiene hijos no hay problema
            ArbolBinarioDeBusqueda<T> subarbolIzquierda = arbolActual.getSubArbolIzquierda();
            if (subarbolIzquierda != null) { // comprobar que el subarbol no este vacio
                int HijosIzquierda = subarbolIzquierda.getRaiz().numeroHijos();
                if (HijosIzquierda == 0) { // si es una hoja y no tiene hijos
                    izquierda = true;
                } else if (numeroHijos != HijosIzquierda) { // ya no es homogeneo
                    izquierda = false;
                } else { // si el numero de hijos coincide repetimos el proceso
                    izquierda = subarbolIzquierda.isArbolHomogeneo1();
                }
            }

            // lo mismo para el subarbol derecho
            ArbolBinarioDeBusqueda<T> subarbolDerecha = arbolActual.getSubArbolDerecha();
            if (subarbolDerecha != null) { // comprobar que el subarbol no este vacio
                int HijosDerecha = subarbolDerecha.getRaiz().numeroHijos(); // miramos cuantos hijos tiene el subarbol
                if (HijosDerecha == 0) { // si es una hoja y no tiene hijos
                    derecha = true;
                } else if (numeroHijos != HijosDerecha) { // ya no es homogeneo
                    derecha = false;
                } else { // si el numero de hijos coincide repetimos el proceso
                    derecha = subarbolDerecha.isArbolHomogeneo1();
                }
            }
        }
        // para que sea homogeneo ambas ramas tienen que ser "true" por lo que tenemos la estructura de una puerta and
        return (izquierda && derecha);
    }

    // Metodo que devuelve si el arbol es homogeneo o no con recursividad a traves de los nodos
    @Override
    public boolean isArbolHomogeneo2() {
        // ultilizamos el metodo recursivo que tenemos en la clase Hoja2a.arbolesbinarios.Nodo
        return raiz.isArbolHomogeneo(raiz);
    }

    // Metodo que devuelve si el arbol es completo o no
    @Override
    public boolean isArbolCompleto() {
        if (raiz != null) {
            // Miramos la profundidad del arbol
            int profundidad = getAltura();
            // Todas las hojas deben tener esa misma profundidad
            return raiz.isArbolCompleto(0, profundidad);
        }
        // Si no hay raiz tecnicamente el arbol es completo
        return true;
    }

    // Metodo que devuelve si el arbol es completo o no
    public boolean isArbolCompleto2() {
        // Comprobamos que el arbol no esta vacio para que no de errores
        if (raiz == null) {
            return true;
        }
        // Vamos a usar otra manera mas facil de ver si un arbol es completo o no reutilizando los metodos que hemos hecho
        int numeroNodosUltimoNivel = getListaDatosNivel(getAltura()).getSize();
        // El numero de nodos que hay en el ultimo deben ser igual al maximo de nodos hijos posibles que puede haber (2^n)
        // Lo que nos indica que el ultimo nivel esta lleno y el arbol es completo
        int maxNodosPosibles = (int) Math.pow(2, getAltura());
        return (numeroNodosUltimoNivel == maxNodosPosibles);
    }

    // Metodo que devuelve si el arbol es casi completo o no
    @Override
    public boolean isArbolCasiCompleto() {
        // Comprobamos que el arbol no esta vacio para que no de errores
        if (raiz == null) {
            return true;
        }
        // Para que el arbol sea casi completo se deben dar dos cosas
        // El penultimo nivel este completo
        // El ultimo nivel este ordenado de izquierda a derecha

        // Para ver que el penultimo nivel esta completo usamos el mismo metodo que en isArbolCompleto2
        int numeroNodosHoja = getNodosHoja().getSize();
        int numeroNodosPenultimoNivel = getListaDatosNivel(getAltura() - 1).getSize();
        int maxNodosPosibles = (int) Math.pow(2, getAltura() - 1);
        boolean PrimeraComprobacion = (numeroNodosPenultimoNivel == maxNodosPosibles);

        // para ver que el ultimo nivel esta ordenado de izquierda a derecha hacemos lo siguiente
        // obtenemos la lista de todos los elementos del penultimo nivel
        ListaSimplementeEnlazada<Nodo<T>> penultimoNivel = getListaNodosNivel(getAltura() - 1);
        // los recorremos de izquierda a derecha y cuando haya alguno que no tenga los dos hijos, ninguno de los demas puede tener
        boolean SegundaComprobacion = true;
        boolean hijosValidos = true;
        for (int i = 0; i < penultimoNivel.getSize(); i++) {
            Nodo<T> nodoActual = penultimoNivel.get(i);
            if ((nodoActual.getIzquierda() != null && !hijosValidos) || (nodoActual.getDerecha() != null && !hijosValidos)) {
                // Si hay algun hijo vacio y despues hay otro mas, no se cumple
                SegundaComprobacion = false;
            }
            if (nodoActual.getIzquierda() == null) {
                if (nodoActual.getDerecha() == null) {
                    // Puede pasar pero marcamos la condicion como falsa para que no pueda haber mas hijos
                    hijosValidos = false;
                } else { // si no hay izquierda y hay derecha no puede ser casi completo
                    SegundaComprobacion = false;
                }
            }
            if (nodoActual.getIzquierda() != null && nodoActual.getDerecha() == null) {
                // Puede pasar pero marcamos la condicion como falsa para que no pueda haber mas hijos
                hijosValidos = false;
            }
            // En el resto de casos no hay problema
        }
        // Para que sea casi completo se tienen que cumplir ambas condiciones
        return PrimeraComprobacion && SegundaComprobacion;
    }

    // Metodo para saber si un arbol esta equilibrado o no
    @Override
    public ArbolBinarioDeBusqueda<T> equilibrar() {
        if (raiz == null) { // si el arbol estaba vacio no se puede equilibrar
            return this;
        } else {
            raiz = raiz.equilibrarBase();
            return this;
        }
    }

    /**
     * Operaciones que se pueden hacer sobre el arbol
     */

    // Metodo para añadir elemento al arbol
    @Override
    public void add(T dato) {
        if (dato == null) { // si no se introduce un dato valido
            return;
        }
        if (raiz == null) { // si el arbol estaba vacio se crea un nuevo arbol con el elemento dado como raiz
            this.raiz = new Nodo<>(dato);
        } else { // si ya habia algun elemento lo añadimos a traves de la clase nodo
            raiz.add(dato);
        }
    }

    // Metodo para eliminar datos del arbol
    @Override
    public void delDato(T dato) {
        if (dato != null && raiz != null) { // hay que ver que se introduce un dato valido y que el arbol estaba vacio
            raiz.delDato(dato);
        }
    }

    // Metodo para eliminar elementos del arbol
    @Override
    public void delNodo(T dato) {
        if (dato != null && raiz != null) { // hay que ver que se introduce un dato valido y que el arbol estaba vacio
            raiz.delNodo(dato);
        }
    }

    // Metodo que devuelve la secuencia de nodos que hay que recorrer hasta llegar a un dato dado
    @Override
    public ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> getCaminoDatos(T dato) {
        if (dato == null || raiz == null) { // si no se introduce un dato valido o si el arbol estaba vacio
            return null;
        } else {
            ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> camino = new ListaSimplementeEnlazada<>();
            raiz.getCaminoDatos(camino, dato);
            // Ahora damos la vuelta a los elementos de la lista para que esten en el orden adecuado para llegar al elemento buscado
            ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> caminoCorrecto = new ListaSimplementeEnlazada<>();
            for (int i = camino.getSize(); i >= 0; i--) { // Bucle empezando desde el final
                caminoCorrecto.addEnd(camino.get(i));
            }
            return caminoCorrecto;
        }
    }

    // Metodo que devuelve la secuencia de nodos que hay que recorrer hasta llegar a un nodo dado
    @Override
    public ListaSimplementeEnlazada<Nodo<T>> getCaminoNodos(T dato) {
        if (dato == null || raiz == null) { // si no se introduce un dato valido o si el arbol estaba vacio
            return null;
        } else {
            ListaSimplementeEnlazada<Nodo<T>> camino = new ListaSimplementeEnlazada<>();
            raiz.getCaminoNodos(camino, dato);
            // Ahora damos la vuelta a los elementos de la lista para que esten en el orden adecuado para llegar al elemento buscado
            ListaSimplementeEnlazada<Nodo<T>> caminoCorrecto = new ListaSimplementeEnlazada<>();
            for (int i = camino.getSize(); i >= 0; i--) { // Bucle empezando desde el final
                caminoCorrecto.addEnd(camino.get(i));
            }
            return caminoCorrecto;
        }
    }

    // Metodo para obtener el subarbol de la parte izquierda del arbol principal
    @Override
    public ArbolBinarioDeBusqueda<T> getSubArbolIzquierda() {
        if (getIzquierda(raiz) != null) {
            Nodo<T> nuevaRaiz = getIzquierda(raiz);
            ArbolBinarioDeBusqueda<T> subArbol = new ArbolBinarioDeBusqueda<>(nuevaRaiz);
            return subArbol;
        } else {
            return new ArbolBinarioDeBusqueda<>(); // Devolvemos un arbol vacio
        }
    }

    // Metodo para obtener el subarbol de la parte derecha del arbol principal
    @Override
    public ArbolBinarioDeBusqueda<T> getSubArbolDerecha() {
        if (getDatos(raiz) != null) {
            Nodo<T> nuevaRaiz = getDerecha(raiz);
            ArbolBinarioDeBusqueda<T> subArbol = new ArbolBinarioDeBusqueda<>(nuevaRaiz);
            return subArbol;
        } else {
            return new ArbolBinarioDeBusqueda<>(); // Devolvemos un arbol vacio
        }
    }

    /**
     * 3 tipos de listas de datos para representar los datos que contiene el arbol con distinto orden
     */

    // Muestra por pantalla los elementos del arbol con orden central
    public ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> getListaOrdenCentral() { // mostrando los elementos con orden central
        ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> elementosArbol = new ListaSimplementeEnlazada<>();
        if (raiz != null) {
            raiz.ordenCentral(elementosArbol);
            return elementosArbol;
        }
        return elementosArbol;
    }

    // Muestra por pantalla los elementos del arbol con preorden
    public ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> getListaPreOrden() { // mostrando los elementos con orden central
        ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> elementosArbol = new ListaSimplementeEnlazada<>();
        if (raiz != null) {
            raiz.preOrden(elementosArbol);
            return elementosArbol;
        }
        return elementosArbol;
    }

    // Muestra por pantalla los elementos del arbol con postorden
    public ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> getListaPostOrden() { // mostrando los elementos con orden central
        ListaSimplementeEnlazada<ListaSimplementeEnlazada<T>> elementosArbol = new ListaSimplementeEnlazada<>();
        if (raiz != null) {
            raiz.postOrden(elementosArbol);
            return elementosArbol;
        }
        return elementosArbol;
    }

    /**
     * Metodo toString para poder visualizar el arbol por niveles
     */
    public String toString() {
        String arbol = "";
        for (int i = 0; i < getAltura() + 1; i++) {
            arbol += getListaDatosNivel(i) + "\n";
        }
        return arbol;
    }
}
