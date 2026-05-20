package Valdris.logic.bfs;

import MisEstructurasDeDatos.Grafos.NodoGrafo;
import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;

/**
 * Calcula caminos minimos entre salas del dungeon.
 *
 * <p>Esta clase trabaja a nivel de grafo de salas, no a nivel de celdas. Se usa
 * para saber por que salas debe pasar el jugador, la generacion o una logica de
 * navegacion global para ir desde una sala de origen hasta una sala destino.</p>
 *
 * <p>El calculo delega en {@code Grafo.caminoMinimo}, que usa busqueda en
 * anchura sobre aristas salientes. Por eso respeta conexiones unidireccionales:
 * si existe Pasillo Final -> S5-D, el camino desde el pasillo al jefe puede
 * existir, pero el camino inverso no.</p>
 *
 * @see Dungeon
 * @see Room
 */
public final class BFSCaminoMinimo {

    // -- Constructor privado -------------------------------------------------

    /**
     * Evita instanciar la clase porque todos sus metodos son estaticos.
     */
    private BFSCaminoMinimo() {
    }

    // -- Metodos de logica ----------------------------------------------------

    /**
     * Devuelve el camino minimo entre dos salas del dungeon.
     *
     * <p>El resultado incluye la sala de origen y la sala de destino. Si el
     * dungeon es null, alguna sala no existe en el grafo o no hay ruta dirigida
     * entre ambas, devuelve una lista vacia.</p>
     *
     * @param dungeon dungeon donde se busca la ruta
     * @param origen sala de origen
     * @param destino sala de destino
     * @return lista de salas en orden origen-destino
     */
    public static ListaSimplementeEnlazada<Room> getCamino(
        Dungeon dungeon, Room origen, Room destino) {

        ListaSimplementeEnlazada<Room> caminoSalas = new ListaSimplementeEnlazada<>();
        if (dungeon == null || origen == null || destino == null) {
            return caminoSalas;
        }

        Room origenRegistrada = dungeon.getRoomById(origen.getId());
        Room destinoRegistrada = dungeon.getRoomById(destino.getId());
        if (origenRegistrada == null || destinoRegistrada == null) {
            return caminoSalas;
        }

        NodoGrafo<Room> nodoOrigen = dungeon.getGrafo().buscarNodoPorDato(origenRegistrada);
        NodoGrafo<Room> nodoDestino = dungeon.getGrafo().buscarNodoPorDato(destinoRegistrada);
        if (nodoOrigen == null || nodoDestino == null) {
            return caminoSalas;
        }

        ListaSimplementeEnlazada<NodoGrafo<Room>> caminoNodos =
            dungeon.getGrafo().caminoMinimo(nodoOrigen.getId(), nodoDestino.getId());

        for (int i = 0; i < caminoNodos.getSize(); i++) {
            NodoGrafo<Room> nodo = caminoNodos.get(i);
            if (nodo != null && nodo.getDatos() != null) {
                caminoSalas.addEnd(nodo.getDatos());
            }
        }
        return caminoSalas;
    }

    /**
     * Devuelve el camino minimo entre dos salas identificadas por id.
     *
     * <p>Este metodo es util para capas superiores y persistencia, donde muchas
     * veces se conserva el identificador de sala en lugar de una referencia
     * directa a {@link Room}.</p>
     *
     * @param dungeon dungeon donde se busca la ruta
     * @param idOrigen identificador de la sala de origen
     * @param idDestino identificador de la sala de destino
     * @return lista de salas en orden origen-destino
     */
    public static ListaSimplementeEnlazada<Room> getCaminoPorId(
        Dungeon dungeon, String idOrigen, String idDestino) {

        if (dungeon == null || idOrigen == null || idDestino == null) {
            return new ListaSimplementeEnlazada<>();
        }
        return getCamino(dungeon, dungeon.getRoomById(idOrigen), dungeon.getRoomById(idDestino));
    }

    /**
     * Calcula la distancia en numero de aristas entre dos salas.
     *
     * <p>Si no existe camino, devuelve -1. Si origen y destino son la misma
     * sala, devuelve 0.</p>
     *
     * @param dungeon dungeon donde se busca la ruta
     * @param origen sala de origen
     * @param destino sala de destino
     * @return numero de conexiones recorridas, o -1 si no hay ruta
     */
    public static int getDistancia(Dungeon dungeon, Room origen, Room destino) {
        ListaSimplementeEnlazada<Room> camino = getCamino(dungeon, origen, destino);
        if (camino.isEmpty()) {
            return -1;
        }
        return camino.getSize() - 1;
    }
}
