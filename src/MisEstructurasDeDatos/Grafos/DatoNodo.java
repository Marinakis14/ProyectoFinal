package MisEstructurasDeDatos.Grafos;

public class DatoNodo implements InterfazDatosNodo {
    private String nombre;
    private String tipo;

    public DatoNodo(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    // Getters
    public String getNombre() { return nombre; }

    public String getTipo() { return tipo; }

    public String toString() {
        return "[" + tipo.toUpperCase() + "] " + nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DatoNodo)) return false;
        DatoNodo otro = (DatoNodo) obj;
        return nombre.equals(otro.nombre);
    }

    // Para poder hacer una listaSimplementeEnlazada tienen que ser comparables
    @Override
    public int compareTo(InterfazDatosNodo o) {
        return 0;
    }
}
