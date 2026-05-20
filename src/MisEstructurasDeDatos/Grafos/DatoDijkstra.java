package MisEstructurasDeDatos.Grafos;

public class DatoDijkstra<DN> implements Comparable<DatoDijkstra<DN>> {
    // Clase para almacenar la informacion necesaria en una lista para poder hacer dijstra
    NodoGrafo<DN> nodo;
    int distancia;
    NodoGrafo<DN> padre;
    boolean visitado;

    DatoDijkstra(NodoGrafo<DN> n) {
        this.nodo = n;
        this.distancia = Integer.MAX_VALUE;
        this.padre = null;
        this.visitado = false;
    }

    @Override
    public int compareTo(DatoDijkstra<DN> o) {
        return Integer.compare(this.distancia, o.distancia);
    }
}
