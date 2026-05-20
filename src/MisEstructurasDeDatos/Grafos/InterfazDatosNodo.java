package MisEstructurasDeDatos.Grafos;

public interface InterfazDatosNodo extends Comparable<InterfazDatosNodo> {
    // En esta interfaz garantizamos que los datos tienen un tipo y un nombre y que ambos son Strings
    String getTipo();
    String getNombre();
}
