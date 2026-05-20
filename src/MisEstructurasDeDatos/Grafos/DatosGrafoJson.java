package MisEstructurasDeDatos.Grafos;

// Clase que representa los datos del JSON del grafo
public class DatosGrafoJson {

    // Lista de tipos de nodos
    private String[] tipos;

    // Lista de tripletas (relaciones)
    private TripletaJson[] tripletas;

    // Getter de tipos
    public String[] getTipos() {
        return tipos;
    }

    // Setter de tipos
    public void setTipos(String[] tipos) {
        this.tipos = tipos;
    }

    // Getter de tripletas
    public TripletaJson[] getTripletas() {
        return tripletas;
    }

    // Setter de tripletas
    public void setTripletas(TripletaJson[] tripletas) {
        this.tripletas = tripletas;
    }
}