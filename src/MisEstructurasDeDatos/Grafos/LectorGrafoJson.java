package MisEstructurasDeDatos.Grafos;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;

public class LectorGrafoJson {

    public static Grafo<DatoNodo, DatoArista> cargarDesdeJson(String ruta) {
        Grafo<DatoNodo, DatoArista> grafo = new Grafo<>();
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(ruta)) {
            DatosGrafoJson datos = gson.fromJson(reader, DatosGrafoJson.class);

            // comprobamos que el json no esta vacio
            if (datos == null) {
                return grafo; // si lo esta devolvemos un grafo vacio
            }

            if (datos.getTripletas() != null) {
                for (TripletaJson tripleta : datos.getTripletas()) {
                    // Tenemos que tener en cuenta que todos los datos tienen el formato
                    // 's': "tipo: nombre"
                    // 'p': "dato"
                    // 'o': "tipo: nombre"

                    // Vemos primero el origen
                    String[] partesS = tripleta.getS().split(":"); // separamos ambos terminos
                    String tipoS = partesS[0];
                    String nombreS = partesS[1];

                    // miramos el nombre, si ya estaba usamos el que ya teniamos, si no estaba creamos uno nuevo
                    NodoGrafo<DatoNodo> origen = grafo.buscarNodoPorNombre(nombreS);
                    if (origen == null) { // si no estaba
                        DatoNodo dato1 = new DatoNodo(nombreS, tipoS);
                        origen = new NodoGrafo<>(dato1);
                    }
                    grafo.addNodo(origen); // añadimos el nodo al grafo

                    // Hacemos lo mismo con el destino
                    String[] partesO = tripleta.getO().split(":"); // separamos ambos terminos
                    String tipoO = partesO[0];
                    String nombreO = partesO[1];

                    NodoGrafo<DatoNodo> destino = grafo.buscarNodoPorNombre(nombreO);
                    if (destino == null) { // si no estaba
                        DatoNodo dato2 = new DatoNodo(nombreO, tipoO);
                        destino = new NodoGrafo<>(dato2);
                    }
                    grafo.addNodo(destino); // añadimos el nodo al grafo

                    // Por ultimo creamos la arista conectando ambos nodos
                    DatoArista dato = new DatoArista(tripleta.getD());
                    grafo.addArista(origen,dato,destino);
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return grafo;
    }
}