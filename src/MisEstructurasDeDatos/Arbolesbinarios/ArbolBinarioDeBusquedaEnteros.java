package MisEstructurasDeDatos.Arbolesbinarios;

public class ArbolBinarioDeBusquedaEnteros<Integer extends Comparable<Integer>> extends ArbolBinarioDeBusqueda<Integer> {

    // Metodo que devuelve la suma de los numeros de un arbol con ordencentral (no tiene en cuenta numeros repetidos)
    public int getSumaOrdenCentral(Nodo actual) {
        if (actual == null) return 0;
        return getSumaOrdenCentral(actual.getIzquierda()) + (java.lang.Integer) actual.getDatos().get(0) + getSumaOrdenCentral(actual.getDerecha());
    }

    // Metodo que devuelve la suma de los numeros de un arbol con preorden (no tiene en cuenta numeros repetidos)
    public int getSumaPreorder(Nodo actual) {
        if (actual == null) return 0;
        return (java.lang.Integer) actual.getDatos().get(0) + getSumaPreorder(actual.getIzquierda()) + getSumaPreorder(actual.getDerecha());
    }

    // Metodo que devuelve la suma de los numeros de un arbol con postorden (no tiene en cuenta numeros repetidos)
    public int getSumaPostorder(Nodo actual) {
        if (actual == null) return 0;
        return getSumaPostorder(actual.getIzquierda()) + getSumaPostorder(actual.getDerecha()) + (java.lang.Integer) actual.getDatos().get(0);
    }
}
