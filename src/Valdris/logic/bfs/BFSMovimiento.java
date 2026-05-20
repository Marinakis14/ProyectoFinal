package Valdris.logic.bfs;

import MisEstructurasDeDatos.ListasPilasYColas.Cola;
import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.InvalidMoveException;
import Valdris.model.map.Cell;
import Valdris.model.map.Room;

/**
 * Calcula celdas alcanzables y caminos cortos dentro de una sala.
 *
 * <p>El movimiento en Valdris se resuelve con busqueda en anchura porque cada
 * paso ortogonal cuesta lo mismo. La clase no modifica la sala ni mueve
 * unidades: solo consulta {@link Room} y {@link Cell#isWalkable()} para devolver
 * resultados que luego podran usar TurnManager, IA o la interfaz.</p>
 *
 * <p>La celda de origen se usa como punto de partida aunque pueda estar ocupada
 * por la unidad que se mueve. Las celdas ocupadas, paredes, puertas cerradas y
 * puertas ocultas no reveladas no se consideran destinos validos.</p>
 */
public final class BFSMovimiento {

    // -- Constantes -----------------------------------------------------------

    /** Desplazamientos ortogonales: norte, sur, oeste y este. */
    private static final int[][] DIRECCIONES = {
        {-1, 0},
        {1, 0},
        {0, -1},
        {0, 1}
    };

    // -- Constructor privado -------------------------------------------------

    /**
     * Evita instanciar la clase porque todos sus metodos son estaticos.
     */
    private BFSMovimiento() {
    }

    // -- Metodos de logica ----------------------------------------------------

    /**
     * Devuelve todas las celdas alcanzables desde una posicion con un numero
     * maximo de puntos de movimiento.
     *
     * <p>La celda de origen no se incluye en el resultado. Solo se anaden celdas
     * transitables, por lo que una celda con enemigo o jugador queda excluida
     * del movimiento aunque este dentro del rango numerico.</p>
     *
     * @param room sala donde se calcula el movimiento
     * @param filaOrigen fila inicial
     * @param colOrigen columna inicial
     * @param movPoints puntos de movimiento disponibles
     * @return lista de celdas alcanzables
     */
    public static ListaSimplementeEnlazada<Cell> getCellsInRange(
        Room room, int filaOrigen, int colOrigen, int movPoints) {

        ListaSimplementeEnlazada<Cell> alcanzables = new ListaSimplementeEnlazada<>();
        if (room == null || movPoints <= 0 || !room.isEnRango(filaOrigen, colOrigen)) {
            return alcanzables;
        }

        boolean[][] visitado = new boolean[room.getFilas()][room.getCols()];
        Cola<PasoBFS> cola = new Cola<>();

        visitado[filaOrigen][colOrigen] = true;
        cola.enqueue(new PasoBFS(filaOrigen, colOrigen, 0));

        while (!cola.isEmpty()) {
            PasoBFS actual = cola.dequeue();
            if (actual.getPasosUsados() >= movPoints) {
                continue;
            }

            for (int i = 0; i < DIRECCIONES.length; i++) {
                int nuevaFila = actual.getFila() + DIRECCIONES[i][0];
                int nuevaCol = actual.getCol() + DIRECCIONES[i][1];

                if (!room.isEnRango(nuevaFila, nuevaCol) || visitado[nuevaFila][nuevaCol]) {
                    continue;
                }

                Cell celda = getCellSegura(room, nuevaFila, nuevaCol);
                if (celda == null || !celda.isWalkable()) {
                    continue;
                }

                visitado[nuevaFila][nuevaCol] = true;
                alcanzables.addEnd(celda);
                cola.enqueue(new PasoBFS(nuevaFila, nuevaCol, actual.getPasosUsados() + 1));
            }
        }

        return alcanzables;
    }

    /**
     * Calcula el camino mas corto entre dos celdas de una sala.
     *
     * <p>El camino devuelto incluye origen y destino. Si el destino esta fuera
     * de rango, bloqueado, ocupado o no existe una ruta transitable, devuelve
     * una lista vacia.</p>
     *
     * @param room sala donde se busca el camino
     * @param filaOrigen fila de origen
     * @param colOrigen columna de origen
     * @param filaDestino fila de destino
     * @param colDestino columna de destino
     * @return camino mas corto, o lista vacia si no hay ruta
     */
    public static ListaSimplementeEnlazada<Cell> getCamino(
        Room room, int filaOrigen, int colOrigen, int filaDestino, int colDestino) {

        ListaSimplementeEnlazada<Cell> camino = new ListaSimplementeEnlazada<>();
        if (room == null || !room.isEnRango(filaOrigen, colOrigen)
            || !room.isEnRango(filaDestino, colDestino)) {
            return camino;
        }

        Cell origen = getCellSegura(room, filaOrigen, colOrigen);
        Cell destino = getCellSegura(room, filaDestino, colDestino);
        if (origen == null || destino == null) {
            return camino;
        }
        if (filaOrigen == filaDestino && colOrigen == colDestino) {
            camino.addEnd(origen);
            return camino;
        }
        if (!destino.isWalkable()) {
            return camino;
        }

        boolean[][] visitado = new boolean[room.getFilas()][room.getCols()];
        int[][] padreFila = crearMatrizPadres(room.getFilas(), room.getCols());
        int[][] padreCol = crearMatrizPadres(room.getFilas(), room.getCols());
        Cola<PasoBFS> cola = new Cola<>();

        visitado[filaOrigen][colOrigen] = true;
        cola.enqueue(new PasoBFS(filaOrigen, colOrigen, 0));

        boolean encontrado = false;
        while (!cola.isEmpty() && !encontrado) {
            PasoBFS actual = cola.dequeue();

            for (int i = 0; i < DIRECCIONES.length; i++) {
                int nuevaFila = actual.getFila() + DIRECCIONES[i][0];
                int nuevaCol = actual.getCol() + DIRECCIONES[i][1];

                if (!room.isEnRango(nuevaFila, nuevaCol) || visitado[nuevaFila][nuevaCol]) {
                    continue;
                }

                Cell celda = getCellSegura(room, nuevaFila, nuevaCol);
                if (celda == null || !celda.isWalkable()) {
                    continue;
                }

                visitado[nuevaFila][nuevaCol] = true;
                padreFila[nuevaFila][nuevaCol] = actual.getFila();
                padreCol[nuevaFila][nuevaCol] = actual.getCol();
                cola.enqueue(new PasoBFS(nuevaFila, nuevaCol, actual.getPasosUsados() + 1));

                if (nuevaFila == filaDestino && nuevaCol == colDestino) {
                    encontrado = true;
                    break;
                }
            }
        }

        if (!encontrado) {
            return camino;
        }

        reconstruirCamino(room, filaOrigen, colOrigen, filaDestino, colDestino, padreFila, padreCol, camino);
        return camino;
    }

    // -- Metodos auxiliares ---------------------------------------------------

    /**
     * Crea una matriz de padres inicializada a -1.
     *
     * @param filas numero de filas
     * @param cols numero de columnas
     * @return matriz inicializada
     */
    private static int[][] crearMatrizPadres(int filas, int cols) {
        int[][] matriz = new int[filas][cols];
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < cols; col++) {
                matriz[fila][col] = -1;
            }
        }
        return matriz;
    }

    /**
     * Reconstruye el camino desde destino hasta origen usando las matrices de
     * padres y lo inserta en orden origen-destino.
     *
     * @param room sala consultada
     * @param filaOrigen fila de origen
     * @param colOrigen columna de origen
     * @param filaDestino fila de destino
     * @param colDestino columna de destino
     * @param padreFila matriz de filas padre
     * @param padreCol matriz de columnas padre
     * @param camino lista donde se inserta el camino reconstruido
     */
    private static void reconstruirCamino(Room room, int filaOrigen, int colOrigen,
                                          int filaDestino, int colDestino,
                                          int[][] padreFila, int[][] padreCol,
                                          ListaSimplementeEnlazada<Cell> camino) {
        int fila = filaDestino;
        int col = colDestino;

        while (fila != -1 && col != -1) {
            Cell celda = getCellSegura(room, fila, col);
            if (celda != null) {
                camino.addStart(celda);
            }

            if (fila == filaOrigen && col == colOrigen) {
                break;
            }

            int siguienteFila = padreFila[fila][col];
            int siguienteCol = padreCol[fila][col];
            fila = siguienteFila;
            col = siguienteCol;
        }
    }

    /**
     * Obtiene una celda evitando propagar excepciones en posiciones ya validadas.
     *
     * @param room sala consultada
     * @param fila fila de la celda
     * @param col columna de la celda
     * @return celda solicitada, o null si la sala informa de un movimiento invalido
     */
    private static Cell getCellSegura(Room room, int fila, int col) {
        try {
            return room.getCell(fila, col);
        } catch (InvalidMoveException e) {
            return null;
        }
    }

    /**
     * Nodo interno usado por la cola de BFS.
     *
     * <p>La cola propia del proyecto exige elementos comparables porque se apoya
     * en listas enlazadas comparables. La comparacion no ordena el BFS; solo da
     * compatibilidad con esa estructura.</p>
     */
    private static final class PasoBFS implements Comparable<PasoBFS> {

        // -- Atributos --------------------------------------------------------

        /** Fila del paso almacenado en la cola. */
        private final int fila;

        /** Columna del paso almacenado en la cola. */
        private final int col;

        /** Distancia desde el origen. */
        private final int pasosUsados;

        // -- Constructor ------------------------------------------------------

        /**
         * Crea un paso de BFS.
         *
         * @param fila fila del paso
         * @param col columna del paso
         * @param pasosUsados distancia desde el origen
         */
        private PasoBFS(int fila, int col, int pasosUsados) {
            this.fila = fila;
            this.col = col;
            this.pasosUsados = pasosUsados;
        }

        // -- Getters ----------------------------------------------------------

        /**
         * Devuelve la fila del paso.
         *
         * @return fila almacenada
         */
        private int getFila() {
            return fila;
        }

        /**
         * Devuelve la columna del paso.
         *
         * @return columna almacenada
         */
        private int getCol() {
            return col;
        }

        /**
         * Devuelve la distancia desde el origen.
         *
         * @return pasos usados
         */
        private int getPasosUsados() {
            return pasosUsados;
        }

        // -- Comparacion ------------------------------------------------------

        /**
         * Compara pasos por posicion y distancia para cumplir el contrato de la cola.
         *
         * @param other paso con el que se compara
         * @return resultado de comparar fila, columna y pasos
         */
        @Override
        public int compareTo(PasoBFS other) {
            if (other == null) {
                return 1;
            }
            if (fila != other.fila) {
                return fila - other.fila;
            }
            if (col != other.col) {
                return col - other.col;
            }
            return pasosUsados - other.pasosUsados;
        }
    }
}
