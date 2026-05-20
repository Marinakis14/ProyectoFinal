package MisEstructurasDeDatos.ListasPilasYColas;


public class Main {
    static void main(String[] args) {
        System.out.println("--- PRUEBA LISTA SIMPLE ---");
        InterfazListas<String> listaSimple = new ListaSimplementeEnlazada<>();
        listaSimple.addEnd("Coche A");
        listaSimple.addEnd("Coche B");
        listaSimple.addEnd("Coche C");

        System.out.println("Tamaño esperado 3: " + listaSimple.getSize());

        System.out.println("Recorriendo lista simple:");
        InterfazMiIterador<String> itSimple = listaSimple.getIterador();
        while (itSimple.hasNext()) {
            System.out.println("- " + itSimple.next());
        }

        System.out.println("\n--- PRUEBA LISTA DOBLE ---");
        ListaDoblementeEnlazada<Integer> listaDoble = new ListaDoblementeEnlazada<>();
        listaDoble.addEnd(10);
        listaDoble.addEnd(20);
        listaDoble.addEnd(30);

        System.out.println("Borramos el 20 (el de en medio)...");
        listaDoble.del(20);

        System.out.println("Recorrido normal (hacia adelante):");
        InterfazMiIterador<Integer> itDobleNormal = listaDoble.getIterador();
        while (itDobleNormal.hasNext()) {
            System.out.println("Dato: " + itDobleNormal.next());
        }

        System.out.println("Recorrido INVERSO (hacia atrás):");
        InterfazMiIterador<Integer> itDobleInverso = listaDoble.getIteradorInverso();
        while (itDobleInverso.hasNext()) {
            System.out.println("Dato: " + itDobleInverso.next());
        }

        System.out.println("\nPrueba de búsqueda (get):");
        Integer buscado = listaDoble.get(30);
        System.out.println("¿Encontrado el 30?: " + (buscado != null ? "SÍ" : "NO"));

        // Creamos una pila de Strings (podría ser de Integers, Coches, etc.)
        Pila<String> miPila = new Pila<>();

        System.out.println("--- Añadiendo elementos ---");
        miPila.push("Libro A");
        miPila.push("Revista");
        miPila.push("Enciclopedia Gigante"); // Este debería recortarse si supera el ancho
        miPila.push("Nota");

        // Mostramos la pila
        System.out.println(miPila.toString());

        System.out.println("\nHaciendo un POP (quitando el de arriba)...");
        String eliminado = miPila.pop();
        System.out.println("Elemento extraído: " + eliminado);

        // Mostramos cómo queda
        System.out.println("\nEstado actual de la pila:");
        System.out.println(miPila.toString());

        System.out.println("\n¿Qué hay en el TOP ahora mismo? " + miPila.top());
        System.out.println("Tamaño total: " + miPila.getSize());

        // Creamos una cola de Strings para simular turnos
        Cola<String> filaBanco = new Cola<>();

        System.out.println("=== 🏦 SIMULADOR DE FILA DEL BANCO ===");

        // 1. Encolamos a los primeros clientes
        filaBanco.enqueue("Cliente_01: Juan");
        filaBanco.enqueue("Cliente_02: María");
        filaBanco.enqueue("Cliente_03: Pedro");

        // Mostramos el estado inicial
        System.out.println("\nEstado inicial de la cola:");
        System.out.println(filaBanco.toString());
        System.out.println("Tamaño actual: " + filaBanco.getSize());

        // 2. Probamos el frente y el final
        System.out.println("\n--- Información de Turnos ---");
        System.out.println("Siguiente en ser atendido (FRONT): " + filaBanco.front());
        System.out.println("Último en llegar (BACK): " + filaBanco.back());

        // 3. Atendemos a alguien (Dequeue)
        System.out.println("\n🔔 Atendiendo al primer cliente...");
        String atendido = filaBanco.dequeue();
        System.out.println("Se ha retirado de la fila: " + atendido);

        // 4. Llega alguien nuevo mientras atendemos
        filaBanco.enqueue("Cliente_04: Lucía");
        System.out.println("Ha llegado un nuevo cliente: Lucía");

        // 5. Mostramos cómo queda la cola finalmente
        System.out.println("\nEstado final de la cola:");
        System.out.println(filaBanco.toString());

        // 6. Prueba de búsqueda
        String buscar = "Cliente_02: María";
        int pos = filaBanco.search(buscar);
        System.out.println("\n¿En qué posición está María? " + (pos != -1 ? "Posición " + pos : "No está en la fila"));

        ListaCircular<Integer> lista = new ListaCircular<>();

        System.out.println("1. Estado inicial:");
        System.out.println(lista.toString());

        System.out.println("\n2. Añadiendo elementos (10, 20, 30):");
        lista.addEnd(10);
        lista.addEnd(20);
        lista.addEnd(30);
        System.out.println(lista.toString());
        System.out.println("Tamaño: " + lista.getSize());

        System.out.println("\n3. Añadiendo al inicio (5):");
        lista.addStart(5);
        System.out.println(lista.toString());

        System.out.println("\n4. Borrando el primero (el 5):");
        lista.delFirst();
        System.out.println(lista.toString());

        System.out.println("\n5. Borrando el último (el 30):");
        lista.del(30);
        System.out.println(lista.toString());

        System.out.println("\n6. Probando búsqueda (¿Está el 20?):");
        System.out.println("Posición del 20: " + lista.getPosicion(20));

        System.out.println("\n7. Vaciando lista:");
        lista.clear();
        System.out.println(lista.toString());
    }
}
