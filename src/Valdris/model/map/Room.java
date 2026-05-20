package Valdris.model.map;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.model.enums.CellType;
import Valdris.model.units.Enemy;

/**
 * Representa una sala jugable dentro del dungeon.
 *
 * <p>Una sala contiene una matriz de celdas, una lista de enemigos vivos y los
 * datos de entrada del jugador. El tamaño de la matriz depende del tipo de sala
 * generado: pequeña, mediana, grande o pasillo.</p>
 *
 * <p>Room pertenece a la capa de mapa. No decide reglas de turno ni IA, pero sí
 * ofrece operaciones seguras para consultar celdas, colocar enemigos, limpiar
 * posiciones y controlar temporizadores de sala.</p>
 *
 * @see Cell
 * @see Enemy
 */
public class Room implements Comparable<Room> {

    // -- Atributos ------------------------------------------------------------

    /** Identificador único de la sala dentro del dungeon. */
    private final String id;

    /** Nombre visible de la sala para la interfaz y el log. */
    private final String nombre;

    /** Matriz de celdas de la sala, indexada como celdas[fila][col]. */
    private final Cell[][] celdas;

    /** Número de filas de la sala. */
    private final int filas;

    /** Número de columnas de la sala. */
    private final int cols;

    /** Lista de enemigos vivos presentes en la sala. */
    private final ListaSimplementeEnlazada<Enemy> enemigos;

    /** Indica si la sala tiene límite de turnos. */
    private boolean hasRoomTimer;

    /** Turnos restantes si la sala tiene temporizador, o -1 si no lo tiene. */
    private int turnosRestantes;

    /** Indica si el jugador ya ha entrado en esta sala alguna vez. */
    private boolean explorada;

    /** Fila donde aparece el jugador al entrar en la sala. */
    private int filaJugador;

    /** Columna donde aparece el jugador al entrar en la sala. */
    private int colJugador;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea una sala con todas sus celdas inicializadas como suelo.
     *
     * @param id identificador único de la sala
     * @param nombre nombre visible de la sala
     * @param filas número de filas
     * @param cols número de columnas
     */
    public Room(String id, String nombre, int filas, int cols) {
        this.id = id;
        this.nombre = nombre;
        this.filas = filas;
        this.cols = cols;
        this.celdas = new Cell[filas][cols];
        this.enemigos = new ListaSimplementeEnlazada<>();
        this.hasRoomTimer = false;
        this.turnosRestantes = -1;
        this.explorada = false;
        this.filaJugador = 0;
        this.colJugador = 0;
        inicializarCeldas();
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Devuelve una celda de la matriz validando sus límites.
     *
     * @param fila fila consultada
     * @param col columna consultada
     * @return celda en la posición indicada
     * @throws InvalidMoveException si la posición está fuera de la sala
     */
    public Cell getCell(int fila, int col) throws InvalidMoveException {
        if (!isEnRango(fila, col)) {
            throw new InvalidMoveException("Celda fuera de rango: (" + fila + ", " + col + ")");
        }
        return celdas[fila][col];
    }

    /**
     * Cambia el tipo de una celda de la sala.
     *
     * @param fila fila de la celda
     * @param col columna de la celda
     * @param tipo nuevo tipo de celda
     * @throws InvalidMoveException si la posición está fuera de la sala
     */
    public void setCellType(int fila, int col, CellType tipo) throws InvalidMoveException {
        getCell(fila, col).setTipo(tipo);
    }

    /**
     * Añade un enemigo a la sala y lo coloca en su celda actual.
     *
     * @param enemy enemigo que entra en la sala
     */
    public void addEnemigo(Enemy enemy) {
        if (enemy == null) {
            return;
        }
        if (!enemigos.contains(enemy)) {
            enemigos.addEnd(enemy);
        }
        if (isEnRango(enemy.getFilaActual(), enemy.getColActual())) {
            celdas[enemy.getFilaActual()][enemy.getColActual()].setUnit(enemy);
        }
    }

    /**
     * Elimina un enemigo de la lista y limpia la celda que ocupaba.
     *
     * @param enemy enemigo que sale de la sala o muere
     */
    public void removeEnemigo(Enemy enemy) {
        if (enemy == null) {
            return;
        }
        enemigos.del(enemy);
        if (isEnRango(enemy.getFilaActual(), enemy.getColActual())) {
            celdas[enemy.getFilaActual()][enemy.getColActual()].removeUnit();
        }
    }

    /**
     * Indica si una posición pertenece a la matriz de la sala.
     *
     * @param fila fila consultada
     * @param col columna consultada
     * @return true si la posición está dentro de límites
     */
    public boolean isEnRango(int fila, int col) {
        return fila >= 0 && fila < filas && col >= 0 && col < cols;
    }

    /**
     * Decrementa el temporizador de sala si existe.
     *
     * @throws GameStateException si el temporizador llega a 0
     */
    public void decrementarTimer() throws GameStateException {
        if (!hasRoomTimer) {
            return;
        }
        turnosRestantes--;
        if (turnosRestantes <= 0) {
            throw new GameStateException("Tiempo agotado en la sala " + id);
        }
    }

    /**
     * Busca una celda libre cercana a una posición.
     *
     * <p>Se usa principalmente para invocar Berserkers cerca del Invocador. La
     * búsqueda revisa primero la celda indicada y después expande un radio
     * cuadrado sencillo hasta encontrar una celda transitable.</p>
     *
     * @param fila fila de referencia
     * @param col columna de referencia
     * @return celda libre más cercana, o null si no hay ninguna disponible
     */
    public Cell getCeldaLibreCercana(int fila, int col) {
        int maxRadio = Math.max(filas, cols);
        for (int radio = 0; radio < maxRadio; radio++) {
            for (int f = fila - radio; f <= fila + radio; f++) {
                for (int c = col - radio; c <= col + radio; c++) {
                    if (isEnRango(f, c) && celdas[f][c].isWalkable()) {
                        return celdas[f][c];
                    }
                }
            }
        }
        return null;
    }

    /**
     * Inicializa todas las posiciones de la matriz como celdas de suelo.
     */
    private void inicializarCeldas() {
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < cols; col++) {
                celdas[fila][col] = new Cell(CellType.FLOOR);
            }
        }
    }

    // -- Getters y setters ----------------------------------------------------

    /**
     * Devuelve el identificador de la sala.
     *
     * @return id único de sala
     */
    public String getId() {
        return id;
    }

    /**
     * Devuelve el nombre visible de la sala.
     *
     * @return nombre de sala
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el número de filas de la sala.
     *
     * @return filas de la matriz
     */
    public int getFilas() {
        return filas;
    }

    /**
     * Devuelve el número de columnas de la sala.
     *
     * @return columnas de la matriz
     */
    public int getCols() {
        return cols;
    }

    /**
     * Devuelve la lista de enemigos vivos de la sala.
     *
     * @return enemigos vivos
     */
    public ListaSimplementeEnlazada<Enemy> getEnemigos() {
        return enemigos;
    }

    /**
     * Indica si la sala tiene temporizador.
     *
     * @return true si hay límite de turnos
     */
    public boolean hasRoomTimer() {
        return hasRoomTimer;
    }

    /**
     * Configura si la sala tiene temporizador.
     *
     * @param hasRoomTimer true si la sala debe tener límite de turnos
     */
    public void setHasRoomTimer(boolean hasRoomTimer) {
        this.hasRoomTimer = hasRoomTimer;
        if (!hasRoomTimer) {
            this.turnosRestantes = -1;
        }
    }

    /**
     * Devuelve los turnos restantes de la sala.
     *
     * @return turnos restantes, o -1 si no hay temporizador
     */
    public int getTurnosRestantes() {
        return turnosRestantes;
    }

    /**
     * Configura los turnos restantes de la sala.
     *
     * @param turnosRestantes nuevo contador de turnos
     */
    public void setTurnosRestantes(int turnosRestantes) {
        this.turnosRestantes = turnosRestantes;
        this.hasRoomTimer = turnosRestantes >= 0;
    }

    /**
     * Indica si la sala ya fue explorada.
     *
     * @return true si el jugador ya entró en la sala
     */
    public boolean isExplorada() {
        return explorada;
    }

    /**
     * Configura si la sala ya fue explorada.
     *
     * @param explorada nuevo estado de exploración
     */
    public void setExplorada(boolean explorada) {
        this.explorada = explorada;
    }

    /**
     * Devuelve la fila de entrada del jugador.
     *
     * @return fila de aparición del jugador
     */
    public int getFilaJugador() {
        return filaJugador;
    }

    /**
     * Configura la fila de entrada del jugador.
     *
     * @param filaJugador fila de aparición
     */
    public void setFilaJugador(int filaJugador) {
        this.filaJugador = filaJugador;
    }

    /**
     * Devuelve la columna de entrada del jugador.
     *
     * @return columna de aparición del jugador
     */
    public int getColJugador() {
        return colJugador;
    }

    /**
     * Configura la columna de entrada del jugador.
     *
     * @param colJugador columna de aparición
     */
    public void setColJugador(int colJugador) {
        this.colJugador = colJugador;
    }

    // -- Comparación ----------------------------------------------------------

    /**
     * Compara salas por su identificador.
     *
     * @param other sala con la que se compara
     * @return resultado lexicográfico entre ids
     */
    @Override
    public int compareTo(Room other) {
        if (other == null) {
            return 1;
        }
        return id.compareTo(other.id);
    }
}
