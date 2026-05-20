package MisEstructurasDeDatos.Grafos;

// Nodo del grafo identificado por un id y un tipo
public class NodoGrafo<DN> implements Comparable<NodoGrafo<DN>> {

    // Identificador del nodo
    private long id;

    // para tener un recuento de los ids y que no se repitan
    private static long contadorId = 0;

    // Datos del nodo
    private DN datos;

    // Constructores
    protected NodoGrafo(DN datos) {
        this.id = ++ contadorId;
        this.datos = datos;
    }

    // Getters y setters
    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    public DN getDatos() {
        return datos;
    }

    protected void setDatos(DN datos) {
        this.datos = datos;
    }

    // Compara nodos por id
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NodoGrafo)) {
            return false;
        }

        NodoGrafo<DN> nodo = (NodoGrafo<DN>) obj;

        return id == nodo.id;
    }

    // Devuelve el id como texto
    @Override
    public String toString() {
        return "ID: " + id + " | Datos: " + (datos != null ? datos.toString() : "vacío");
    }

    @Override
    public int compareTo(NodoGrafo otro) {
        return Long.compare(this.id, otro.id);
    }
}
