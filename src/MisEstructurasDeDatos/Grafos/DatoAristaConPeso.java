package MisEstructurasDeDatos.Grafos;

public class DatoAristaConPeso implements InterfazAristasConPeso{
    private int peso;
    private String dato;

    public DatoAristaConPeso(int peso, String dato) {
        this.peso = peso;
        this.dato = dato;
    }

    public Integer getPeso() { return peso; }

    public String getDato() {
        return dato;
    }

    public String toString() {
        return "[peso: " + peso + ", dato: " + dato + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DatoAristaConPeso)) return false;
        DatoAristaConPeso otro = (DatoAristaConPeso) obj;
        return peso == otro.getPeso();
    }

    // Para poder hacer una lista de aristas deben ser comparables
    @Override
    public int compareTo(InterfazAristasConPeso o) {
        return 0;
    }
}
