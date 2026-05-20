package MisEstructurasDeDatos.Arbolesbinarios;

public class ArbolBinarioDeBusquedaEquilibrado<T extends Comparable<T>> extends ArbolBinarioDeBusqueda<T> {
    // Hay que sobreescribir el metodo add para que cada vez que se añada un dato el arbol se autoequilibre
    @Override
    public void add(T dato) {
        // Añadimos el dato igual que antes
        super.add(dato);
        // Equilibramos el arbol
        super.equilibrar();
    }

    // Lo mismo para los metodos de borrado
    @Override
    public void delDato(T dato) {
        super.delDato(dato);
        super.equilibrar();
    }

    @Override
    public void delNodo(T dato) {
        super.delNodo(dato);
        super.equilibrar();
    }
}
