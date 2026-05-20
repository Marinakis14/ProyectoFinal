package Valdris.model.map;

import MisEstructurasDeDatos.Grafos.Grafo;
import MisEstructurasDeDatos.Grafos.Arista;
import MisEstructurasDeDatos.Grafos.NodoGrafo;
import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;

/**
 * Representa el mapa completo del juego como un grafo de salas.
 *
 * <p>El dungeon de Valdris no es una matriz gigante, sino una red de salas
 * conectadas. Cada {@link Room} es un nodo del grafo y cada arista guarda un
 * texto descriptivo de la conexion: puerta norte, pasillo, escaleras, acceso
 * secreto o punto de no retorno.</p>
 *
 * <p>La mayoria de conexiones son bidireccionales, porque el jugador puede
 * volver por la misma puerta. La excepcion importante del diseno es el Pasillo
 * Final hacia S5-D, que se modela con {@link #conectarUnidireccional(Room, Room, String)}
 * para representar el punto de no retorno antes del jefe final.</p>
 *
 * @see Room
 * @see Grafo
 */
public class Dungeon implements Comparable<Dungeon> {

    // -- Atributos ------------------------------------------------------------

    /** Grafo principal de salas. Nodos=Room, aristas=descripcion de conexion. */
    private final Grafo<Room, String> grafo;

    /** Sala donde se encuentra el jugador en este momento. */
    private Room roomActual;

    /** Nodo del grafo correspondiente a {@code roomActual}. */
    private NodoGrafo<Room> nodoActual;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un dungeon vacio, sin salas ni sala actual.
     */
    public Dungeon() {
        this.grafo = new Grafo<>();
        this.roomActual = null;
        this.nodoActual = null;
    }

    // -- Metodos de logica ----------------------------------------------------

    /**
     * Anade una sala como nodo del grafo.
     *
     * <p>Si la sala ya existe, el grafo no la duplica. Si todavia no habia sala
     * actual, la primera sala anadida pasa a ser la sala inicial por defecto.</p>
     *
     * @param room sala que se incorpora al dungeon
     */
    public void addRoom(Room room) {
        if (room == null) {
            return;
        }
        Room existente = getRoomById(room.getId());
        if (existente != null) {
            return;
        }
        grafo.addNodo(room);
        if (roomActual == null) {
            setRoomActual(room);
        }
    }

    /**
     * Conecta dos salas en ambos sentidos.
     *
     * <p>Se usa para puertas y pasillos normales. Si alguna sala aun no estaba
     * en el dungeon, se anade antes de crear las aristas.</p>
     *
     * @param a sala origen de la primera arista
     * @param b sala destino de la primera arista
     * @param desc descripcion de la conexion
     */
    public void conectar(Room a, Room b, String desc) {
        if (a == null || b == null) {
            return;
        }
        addRoom(a);
        addRoom(b);

        NodoGrafo<Room> nodoA = grafo.buscarNodoPorDato(getRoomById(a.getId()));
        NodoGrafo<Room> nodoB = grafo.buscarNodoPorDato(getRoomById(b.getId()));
        addAristaSiNoExiste(nodoA, nodoB, desc);
        addAristaSiNoExiste(nodoB, nodoA, desc);
    }

    /**
     * Conecta dos salas en un unico sentido.
     *
     * <p>Esta operacion existe para modelar accesos especiales que no permiten
     * volver atras, especialmente el Pasillo Final hacia S5-D.</p>
     *
     * @param a sala origen
     * @param b sala destino
     * @param desc descripcion de la conexion
     */
    public void conectarUnidireccional(Room a, Room b, String desc) {
        if (a == null || b == null) {
            return;
        }
        addRoom(a);
        addRoom(b);

        NodoGrafo<Room> nodoA = grafo.buscarNodoPorDato(getRoomById(a.getId()));
        NodoGrafo<Room> nodoB = grafo.buscarNodoPorDato(getRoomById(b.getId()));
        addAristaSiNoExiste(nodoA, nodoB, desc);
    }

    /**
     * Devuelve las salas conectadas mediante aristas salientes desde una sala.
     *
     * <p>En conexiones bidireccionales apareceran las salas vecinas normales.
     * En conexiones unidireccionales solo aparecera el destino cuando se consulta
     * desde el origen, respetando el punto de no retorno.</p>
     *
     * @param room sala de la que se consultan vecinos
     * @return lista de salas adyacentes; vacia si la sala no existe
     */
    public ListaSimplementeEnlazada<Room> getSalasAdyacentes(Room room) {
        ListaSimplementeEnlazada<Room> salas = new ListaSimplementeEnlazada<>();
        Room registrada = null;
        if (room != null) {
            registrada = getRoomById(room.getId());
        }
        NodoGrafo<Room> nodo = grafo.buscarNodoPorDato(registrada);
        if (nodo == null) {
            return salas;
        }

        ListaSimplementeEnlazada<NodoGrafo<Room>> vecinos = grafo.getVecinos(nodo);
        for (int i = 0; i < vecinos.getSize(); i++) {
            NodoGrafo<Room> vecino = vecinos.get(i);
            if (vecino != null && vecino.getDatos() != null) {
                salas.addEnd(vecino.getDatos());
            }
        }
        return salas;
    }

    /**
     * Busca una sala por su identificador.
     *
     * @param id identificador de sala buscado
     * @return sala encontrada, o null si no existe
     */
    public Room getRoomById(String id) {
        if (id == null) {
            return null;
        }
        ListaSimplementeEnlazada<NodoGrafo<Room>> nodos = grafo.getNodos();
        for (int i = 0; i < nodos.getSize(); i++) {
            Room room = nodos.get(i).getDatos();
            if (room != null && id.equals(room.getId())) {
                return room;
            }
        }
        return null;
    }

    /**
     * Anade una arista si no existe ya una conexion saliente equivalente.
     *
     * <p>Se compara origen, destino y descripcion. Asi el generador puede llamar
     * varias veces a la misma conexion sin duplicar vecinos ni alterar caminos
     * minimos.</p>
     *
     * @param origen nodo origen de la conexion
     * @param destino nodo destino de la conexion
     * @param desc descripcion de la conexion
     */
    private void addAristaSiNoExiste(NodoGrafo<Room> origen, NodoGrafo<Room> destino, String desc) {
        if (origen == null || destino == null || existeArista(origen, destino, desc)) {
            return;
        }
        grafo.addArista(origen, desc, destino);
    }

    /**
     * Indica si ya existe una arista igual en el grafo.
     *
     * @param origen nodo origen
     * @param destino nodo destino
     * @param desc descripcion esperada
     * @return true si ya existe esa conexion
     */
    private boolean existeArista(NodoGrafo<Room> origen, NodoGrafo<Room> destino, String desc) {
        ListaSimplementeEnlazada<Arista<Room, String>> aristas = grafo.getAristas();
        for (int i = 0; i < aristas.getSize(); i++) {
            Arista<Room, String> arista = aristas.get(i);
            if (arista.getOrigen().equals(origen) && arista.getDestino().equals(destino)
                && mismaDescripcion(arista.getDato(), desc)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compara descripciones de aristas admitiendo null.
     *
     * @param a primera descripcion
     * @param b segunda descripcion
     * @return true si ambas descripciones son equivalentes
     */
    private boolean mismaDescripcion(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    /**
     * Actualiza la sala actual y su nodo asociado.
     *
     * <p>Si la sala no pertenece todavia al grafo, se anade antes de marcarla
     * como actual. Asi se evita que {@code roomActual} y {@code nodoActual}
     * queden desincronizados.</p>
     *
     * @param room nueva sala actual
     */
    public void setRoomActual(Room room) {
        if (room == null) {
            this.roomActual = null;
            this.nodoActual = null;
            return;
        }
        Room existente = getRoomById(room.getId());
        if (existente == null) {
            grafo.addNodo(room);
            existente = room;
        }
        this.roomActual = existente;
        this.nodoActual = grafo.buscarNodoPorDato(existente);
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el grafo completo de salas.
     *
     * @return grafo del dungeon
     */
    public Grafo<Room, String> getGrafo() {
        return grafo;
    }

    /**
     * Devuelve la sala actual del jugador.
     *
     * @return sala actual, o null si no hay sala seleccionada
     */
    public Room getRoomActual() {
        return roomActual;
    }

    /**
     * Devuelve el nodo del grafo correspondiente a la sala actual.
     *
     * @return nodo actual, o null si no hay sala seleccionada
     */
    public NodoGrafo<Room> getNodoActual() {
        return nodoActual;
    }

    // -- Comparacion ----------------------------------------------------------

    /**
     * Compara dungeons por numero de salas y sala actual.
     *
     * <p>Esta comparacion existe para compatibilidad con las estructuras
     * enlazadas propias. No representa una ordenacion de progreso de partida.</p>
     *
     * @param other dungeon con el que se compara
     * @return resultado de comparar tamano del grafo y sala actual
     */
    @Override
    public int compareTo(Dungeon other) {
        if (other == null) {
            return 1;
        }
        int resultado = grafo.getNodos().getSize() - other.grafo.getNodos().getSize();
        if (resultado != 0) {
            return resultado;
        }
        if (roomActual == other.roomActual) {
            return 0;
        }
        if (roomActual == null) {
            return -1;
        }
        if (other.roomActual == null) {
            return 1;
        }
        return roomActual.compareTo(other.roomActual);
    }
}
