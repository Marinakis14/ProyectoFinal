package MisEstructurasDeDatos.Grafos;

// Arista que conecta dos nodos con una relación
public class Arista<DN, DA> implements Comparable<Arista<DN, DA>> {

    // identificador
    private long id;

    // para tener un recuento de los ids y que no se repitan
    private static long contadorId = 0;

    // Nodo inicial
    private NodoGrafo<DN> origen;

    // Tipo de relación
    private DA dato;

    // Nodo final
    private NodoGrafo<DN> destino;

    // Constructor
    protected Arista(NodoGrafo<DN> origen, DA predicado, NodoGrafo<DN> destino) {
        this.id = ++ contadorId;
        this.origen = origen;
        this.dato = predicado;
        this.destino = destino;
    }

    // Getters y setters
    protected long getId() { return id; }

    public NodoGrafo<DN> getOrigen(){
        return origen;
    }

    protected void setOrigen(NodoGrafo<DN> origen){
        this.origen = origen;
    }

    public DA getDato(){
        return dato;
    }

    public NodoGrafo<DN> getDestino() {
        return destino;
    }

    public void setDato(DA dato) {
        this.dato = dato;
    }

    public void setDestino(NodoGrafo<DN> destino) {
        this.destino = destino;
    }

    // Devuelve la arista en formato texto
    @Override
    public String toString(){
        return "  " + origen.getId() + " --(" + dato + ")--> " + destino.getId();
    }

    @Override
    public int compareTo(Arista o) {
        return Long.compare(this.id, o.id);
    }
}
