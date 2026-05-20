package MisEstructurasDeDatos.Grafos;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;

public class Main {
    public static void main(String[] args) {

        System.out.println("=== PRUEBA CON datos.json ===");
        Grafo<DatoNodo, DatoArista> grafo = LectorGrafoJson.cargarDesdeJson("datos.json");

        System.out.println(grafo);
        System.out.println();

        System.out.println("\nCamino minimo entre Albert Einstein y Ulm:");
        ListaSimplementeEnlazada<NodoGrafo<DatoNodo>> camino1 = grafo.caminoMinimo("Albert Einstein", "Ulm");
        System.out.println(camino1);
        System.out.println(grafo.mostrarIdsCamino(camino1));

        System.out.println("\nCamino minimo entre Marie Curie y Polonia:");
        ListaSimplementeEnlazada<NodoGrafo<DatoNodo>> camino2 = grafo.caminoMinimo("Marie Curie", "Polonia");
        System.out.println(camino2);
        System.out.println(grafo.mostrarIdsCamino(camino2));

        System.out.println("\nCamino minimo entre Albert Einstein y Ulm:");
        ListaSimplementeEnlazada<NodoGrafo<DatoNodo>> camino3 = grafo.caminoMinimo("Albert Einstein", "Varsovia");
        System.out.println(camino3);
        System.out.println(grafo.mostrarIdsCamino(camino3));

        System.out.println("\nCamino minimo entre Albert Einstein y Ulm:");
        ListaSimplementeEnlazada<NodoGrafo<DatoNodo>> camino4 = grafo.caminoMinimoAmbosSentidos("Albert Einstein", "Varsovia");
        System.out.println(camino4);
        System.out.println(grafo.mostrarIdsCamino(camino4));

        System.out.println("\nPersonas nacidas en la misma ciudad que Einstein:");
        System.out.println(grafo.personasMismaCiudadQue("Albert Einstein"));

        System.out.println("\nFisicos nacidos en la misma ciudad que Einstein:");
        System.out.println(grafo.fisicosMismaCiudadQue("Albert Einstein"));

        System.out.println("\nAñadimos un par de personas y fisicos mas");
        DatoNodo origen1 = new DatoNodo("Jaime", "persona");
        DatoNodo destino1 = new DatoNodo("Ulm", "lugar");
        DatoArista dato1 = new DatoArista("nace_en");
        grafo.addArista(new NodoGrafo<>(origen1),dato1, new NodoGrafo<>(destino1));
        DatoNodo origen2 = new DatoNodo("Marino", "persona");
        DatoNodo destino2 = new DatoNodo("Ulm", "lugar");
        DatoArista dato2 = new DatoArista("nace_en");
        grafo.addArista(new NodoGrafo<>(origen2),dato2, new NodoGrafo<>(destino2));
        DatoNodo origen3 = new DatoNodo("Marino", "persona");
        DatoNodo destino3 = new DatoNodo("fisico", "profesion");
        DatoArista dato3 = new DatoArista("profesion");
        grafo.addArista(new NodoGrafo<>(origen3),dato3, new NodoGrafo<>(destino3));
        System.out.println();
        System.out.println(grafo);

        System.out.println("\nPersonas nacidas en la misma ciudad que Einstein:");
        System.out.println(grafo.personasMismaCiudadQue("Albert Einstein"));

        System.out.println("\nFisicos nacidos en la misma ciudad que Einstein:");
        System.out.println(grafo.fisicosMismaCiudadQue("Albert Einstein"));

        System.out.println("\n¿El grafo de datos.json es disjunto?");
        System.out.println(grafo.esDisjunto());

        DatoNodo origen4 = new DatoNodo("Antonio", "persona");
        DatoNodo destino4 = new DatoNodo("Villarrubia de los Caballeros", "lugar");
        DatoArista dato4 = new DatoArista("nace_en");
        grafo.addArista(new NodoGrafo<>(origen4),dato4, new NodoGrafo<>(destino4));
        System.out.println();
        System.out.println(grafo.getNodos());

        System.out.println("\n¿El grafo de datos.json es disjunto añadiendo la tripleta <persona:Antonio, nace_en, lugar:Villarrubia de los Caballeros>?");
        System.out.println(grafo.esDisjunto());

        System.out.println("\nLugares de nacimiento de los premios Nobel:");
        System.out.println(grafo.lugaresNacimientoPremiosNobel());


        System.out.println("\n=== PRUEBA CON disjunto.json ===");
        Grafo<DatoNodo, DatoArista> grafoDisjunto = LectorGrafoJson.cargarDesdeJson("disjunto.json");

        System.out.println(grafoDisjunto);
        System.out.println();

        System.out.println("¿Es disjunto?");
        System.out.println(grafoDisjunto.esDisjunto());


        System.out.println("\n=== PRUEBA CON nodisjunto.json ===");
        Grafo<DatoNodo, DatoArista> grafoNoDisjunto = LectorGrafoJson.cargarDesdeJson("nodisjunto.json");

        System.out.println(grafoNoDisjunto);
        System.out.println();

        System.out.println("¿Es disjunto?");
        System.out.println(grafoNoDisjunto.esDisjunto());
    }
}
