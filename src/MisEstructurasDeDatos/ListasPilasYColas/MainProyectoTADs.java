package MisEstructurasDeDatos.ListasPilasYColas;


/**
 * Programa principal que usa todas las TADs a modo de demostración, mostrando su estructura por pantalla y sus metodos
 */

public class MainProyectoTADs {

    public static void main(String[] args) {
        // primero ponemos un "titulo"
        System.out.println("=====================================================");
        System.out.println("      PROGRAMA PRINCIPAL QUE USA TODAS LAS TADs      ");
        System.out.println("=====================================================\n");

        // creamos un subprograma distinto para cada TAD para que quede mas limpio y ordenado
        // Lista Simplemente Enlazada
        probarLSE();
        System.out.println("\n-----------------------------------------------------\n");
        // Lista Simplemente Enlazada Ordenada
        probarLSEOrdenada();
        System.out.println("\n-----------------------------------------------------\n");
        // Lista Doblemente Enlazada
        probarLDE();
        System.out.println("\n-----------------------------------------------------\n");
        // Lista Doblemente Enlazada Ordenada
        probarLDEOrdenada();
        System.out.println("\n-----------------------------------------------------\n");
        // Lista Circular
        probarCircular();
        System.out.println("\n-----------------------------------------------------\n");
        // Pila
        probarPila();
        System.out.println("\n-----------------------------------------------------\n");
        // Cola
        probarCola();

        // para terminar añadimos un mensaje de que todas las pruebas se han finalizado con éxito
        System.out.println("\n=====================================================");
        System.out.println("             PRUEBAS FINALIZADAS CON ÉXITO             ");
        System.out.println("=====================================================");
    }

    private static void probarLSE() {
        System.out.println("[ TEST 1: LISTA SIMPLEMENTE ENLAZADA ]");
        ListaSimplementeEnlazada<String> lse = new ListaSimplementeEnlazada<>();

        lse.addStart("Nodo B");
        lse.addStart("Nodo A"); // Insertamos al principio
        lse.addEnd("Nodo C");   // Insertamos al final
        lse.addAnywhere("Nodo Intermedio", 2); // Insertamos en medio

        System.out.println("Contenido LSE: " + lse.toString()); // Probamos toString()
        System.out.println("Tamaño: " + lse.getSize()); // Mostramos el tamaño de la lista

        System.out.println("Eliminando primero (delFirst): " + lse.delFirst()); // Borrarmos el primer elemento
        System.out.println("Tras eliminar: " + lse.toString());

        System.out.println("Elemento en posición 1: " + lse.get(1)); // Probamos búsqueda por índice
    }

    private static void probarLSEOrdenada() {
        System.out.println("[ TEST 2: LSE ORDENADA]");
        LSEOrdenada<Integer> lseo = new LSEOrdenada<>();

        lseo.addStart(50); // Insertamos al principio
        lseo.addEnd(10); // Insertamos al final
        lseo.addAnywhere(30, 0); // Insertamos en medio
        lseo.addOrdenado(25); // Insertamos de manera ordenada

        System.out.println("Lista tras inserciones: " + lseo.toString()); // Probamos el toString()
        System.out.println("Tamaño (getSize): " + lseo.getSize()); // Mostramos el tamaño de la lista
        System.out.println("¿Está vacía? (isEmpty): " + lseo.isEmpty()); // Vemos si esta vacia o no

        // Métodos para buscar elementos
        System.out.println("Dato en posición 2 (get): " + lseo.get(2));
        System.out.println("Posición del dato 30 (getPosicion): " + lseo.getPosicion(30));
        System.out.println("¿Contiene el 100? (contains): " + lseo.contains(100));

        // Métodos para borrar elementos
        System.out.println("Borrando el 30 (del): " + lseo.del(30));
        System.out.println("Borrando el primero (delFirst): " + lseo.delFirst());

        lseo.clear(); // Probamos a vaciar la lista
        System.out.println("Tras clear, ¿está vacía?: " + lseo.isEmpty());
    }

    private static void probarLDE() {
        System.out.println("[ TEST 3: LISTA DOBLEMENTE ENLAZADA ]");
        ListaDoblementeEnlazada<String> lde = new ListaDoblementeEnlazada<>();

        lde.addStart("Jaimito"); // Insertamos al principio
        lde.addStart("Pepito"); // Insertamos al final
        lde.addEnd("Juanito"); // Insertamos en medio
        lde.addAnywhere("Pepita", 2); // Insertamos por posicion

        System.out.println("Lista actual: " + lde.toString());

        // Iterador normal
        System.out.print("Iterador normal (hasNext/next): ");
        InterfazMiIterador<String> it = lde.getIterador();
        while(it.hasNext()) System.out.print(it.next() + " | ");

        // Iterador inverso
        System.out.print("\nIterador inverso: ");
        InterfazMiIterador<String> itInv = lde.getIteradorInverso();
        while(itInv.hasNext()) System.out.print(itInv.next() + " | ");

        // Metodo para borrar elementos del final (especifico de LDE)
        System.out.println("\nBorrando último (delLast): " + lde.delLast());
        System.out.println("Lista tras delLast: " + lde.toString());
    }

    private static void probarLDEOrdenada() {
        System.out.println("[ TEST 4: LDE ORDENADA ]");
        LDEOrdenada<Integer> ldeo = new LDEOrdenada<>();

        // Probamos que todos los metodos add mantienen el orden
        ldeo.addEnd(100);    // Es el primer elemento
        ldeo.addStart(10);   // Se pone al principio
        ldeo.addAnywhere(50, 1); // Aunque pidamos posición 1 se ordena correctamente
        ldeo.addOrdenado(75);
        ldeo.addOrdenado(5);

        System.out.println("LDE Ordenada (debe ser 5 <-> 10 <-> 50 <-> 75 <-> 100):");
        System.out.println(ldeo.toString()); // Mostramos la lista por pantalla

        // Al ser LDE, podemos usar el iterador inverso para ver si el orden se mantiene atrás-alante
        System.out.print("Comprobando orden inverso: ");
        InterfazMiIterador<Integer> inv = ldeo.getIteradorInverso();
        while(inv.hasNext()) System.out.print(inv.next() + " ");
        System.out.println();
    }

    private static void probarCircular() {
        System.out.println("[TEST 5: LISTA CIRCULAR]");
        ListaCircular<Double> lc = new ListaCircular<>();

        // Insertamos los elementos
        lc.addEnd(1.1);
        lc.addEnd(2.2);
        lc.addEnd(3.3);

        System.out.println("Circular (toString): " + lc.toString()); // Probamos el toString()

        // Probamos metodos get
        System.out.println("Buscando el 3.3 (get): " + lc.get(3.3));
        System.out.println("Posición del 2.2: " + lc.getPosicion(2.2));

        // Probamos metodo del
        lc.del(2.2);
        System.out.println("Tras borrar el del medio (2.2): " + lc.toString());

        // Probamos el iterador circular
        System.out.print("Vuelta completa con iterador: ");
        InterfazMiIterador<Double> it = lc.getIterador();
        while(it.hasNext()) System.out.print(it.next() + " -> ");
        System.out.println("(FIN)");
    }

    private static void probarPila() {
        System.out.println("[ TEST 6: PILA ]");
        Pila<String> p = new Pila<>();

        // Añadimos elementos a la pila
        p.push("A");
        p.push("B");
        p.push("C");

        System.out.println("Pila visualizada:\n" + p.toString()); // Probamos el toString()
        System.out.println("Elemento superior (top): " + p.top()); // Vemos el primer elemento
        System.out.println("Tamaño: " + p.getSize()); // Vemos el tamaño de la pila

        // Metodo para buscar elementos
        System.out.println("Buscando 'A' (search): Posición " + p.search("A"));

        // Probamos a crear una copia de nuestra pila
        InterfazPila<String> copia = p.copy();
        System.out.println("Copia realizada, borramos la original:");
        // Probamos a borrar la lista anterior
        p.clear();
        System.out.println("Original vacía: " + p.isEmpty()); // Vemos si esta vacia
        System.out.println("Confirmamos que el orden se mantiene (primer elemento): " + copia.top()); // Vemos si el primer elemento sigue coincidiendo

        // Probamos el metodo pop()
        System.out.println("Sacando: " + copia.pop());
        System.out.printf(copia.toString()); // Mostramos el resultado final
    }

    private static void probarCola() {
        System.out.println("[TEST 7: COLA ]");
        Cola<String> alumnos = new Cola<>();

        // Añadimos elementos a la cola
        alumnos.enqueue("Alumno 1");
        alumnos.enqueue("Alumno 2");
        alumnos.enqueue("Alumno 3");

        System.out.println("Estado de la cola: " + alumnos.toString()); // Probamos toString()
        System.out.println("Primer alumno (front): " + alumnos.front()); // Vemos el primer elemento
        System.out.println("Saliendo de la cola (dequeue): " + alumnos.dequeue()); // Sacamos un elemento
        System.out.println("Siguiente en la cola: " + alumnos.front()); // Vemos el siguiente
        System.out.println("Tamaño actual: " + alumnos.getSize()); // Vemos el tamaño de la cola

        // Probamos a vaciar la cola y comprobar isEmpty()
        alumnos.clear();
        System.out.println("Tras vaciar, ¿está vacía?: " + alumnos.isEmpty());
    }
}
