package MisEstructurasDeDatos.Grafos;

public class DatoArista implements InterfazDatosArista {
    private String dato;

    public DatoArista(String predicado) {
        this.dato = predicado;
    }

    public String getDato() { return dato; }

    public boolean esValido() {
        return dato != null && !dato.isEmpty();
    }

    public String toString() {
        return dato;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DatoArista)) return false;
        DatoArista otro = (DatoArista) obj;
        return dato.equals(otro.dato);
    }

    // Para poder hacer una lista de aristas deben ser comparables
    @Override
    public int compareTo(InterfazDatosArista o) {
        return 0;
    }
}
