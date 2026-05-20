package MisEstructurasDeDatos.Grafos;

public interface InterfazAristasConPeso extends Comparable<InterfazAristasConPeso> {
    // Interfaz para garantizar que hay un metodo que devuelve un entero representando el peso de la arista (para Djistra)
    Integer getPeso();
}
