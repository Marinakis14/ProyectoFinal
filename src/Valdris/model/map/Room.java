package Valdris.model.map;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.CellType;
import Valdris.model.units.Enemy;
import Valdris.model.units.MalacharAlly;

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

    /** Aliado NPC especial de la sala final, o null si no existe. */
    private MalacharAlly allyNpc;

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

    /** Palancas registradas para puzzles de secuencia. */
    private final ListaSimplementeEnlazada<Cell> leverCells;

    /** Runas registradas para puzzles de secuencia. */
    private final ListaSimplementeEnlazada<Cell> runeCells;

    /** Secuencia correcta de activación para palancas o runas. */
    private int[] correctSequence;

    /** Secuencia activada actualmente por el jugador. */
    private int[] secuenciaActivada;

    /** Número de entradas registradas en la secuencia activada. */
    private int secuenciaActivadaSize;

    /** Indica si el puzzle de la sala ya fue resuelto. */
    private boolean puzzleResolved;

    /** Identificador de puerta o pasadizo que se activa al resolver el puzzle. */
    private String puzzleSuccessTarget;

    /** Daño aplicado al jugador si falla el puzzle de esta sala. */
    private int puzzleFailureDamage;

    /** IDs de triggers secretos presentes en la sala. */
    private final ListaSimplementeEnlazada<String> secretTriggerIds;

    /** Destinos asociados a los triggers secretos, en el mismo orden. */
    private final ListaSimplementeEnlazada<String> secretTargetIds;

    /** Diálogo especial para Kael en esta sala. */
    private String dialogoKael;

    /** Diálogo especial para Syra en esta sala. */
    private String dialogoSyra;

    /** Diálogo especial para Dorath en esta sala. */
    private String dialogoDorath;

    /** Indica si el diálogo de Kael ya fue mostrado. */
    private boolean dialogoKaelMostrado;

    /** Indica si el diálogo de Syra ya fue mostrado. */
    private boolean dialogoSyraMostrado;

    /** Indica si el diálogo de Dorath ya fue mostrado. */
    private boolean dialogoDorathMostrado;

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
        this.allyNpc = null;
        this.hasRoomTimer = false;
        this.turnosRestantes = -1;
        this.explorada = false;
        this.filaJugador = 0;
        this.colJugador = 0;
        this.leverCells = new ListaSimplementeEnlazada<>();
        this.runeCells = new ListaSimplementeEnlazada<>();
        this.correctSequence = new int[0];
        this.secuenciaActivada = new int[0];
        this.secuenciaActivadaSize = 0;
        this.puzzleResolved = false;
        this.puzzleSuccessTarget = null;
        this.puzzleFailureDamage = 3;
        this.secretTriggerIds = new ListaSimplementeEnlazada<>();
        this.secretTargetIds = new ListaSimplementeEnlazada<>();
        this.dialogoKael = null;
        this.dialogoSyra = null;
        this.dialogoDorath = null;
        this.dialogoKaelMostrado = false;
        this.dialogoSyraMostrado = false;
        this.dialogoDorathMostrado = false;
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
     * Coloca o elimina el aliado NPC especial de la sala.
     *
     * <p>El aliado no entra en la lista de enemigos. Su presencia se refleja en
     * la celda para bloquear movimiento y línea de visión como cualquier otra
     * unidad.</p>
     *
     * @param allyNpc aliado que se coloca, o null para retirarlo
     */
    public void setAllyNpc(MalacharAlly allyNpc) {
        limpiarAllyNpcDeCelda();
        this.allyNpc = allyNpc;
        if (allyNpc != null && isEnRango(allyNpc.getFilaActual(), allyNpc.getColActual())) {
            celdas[allyNpc.getFilaActual()][allyNpc.getColActual()].setUnit(allyNpc);
        }
    }

    /**
     * Limpia la celda ocupada por el aliado actual si sigue apuntando a él.
     */
    private void limpiarAllyNpcDeCelda() {
        if (allyNpc == null || !isEnRango(allyNpc.getFilaActual(), allyNpc.getColActual())) {
            return;
        }
        Cell cell = celdas[allyNpc.getFilaActual()][allyNpc.getColActual()];
        if (cell.getUnit() == allyNpc) {
            cell.removeUnit();
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
     * Registra una palanca de la sala para puzzles de secuencia.
     *
     * @param cell celda de tipo palanca
     */
    public void addLeverCell(Cell cell) {
        if (cell != null && !contieneCeldaPorReferencia(leverCells, cell)) {
            leverCells.addEnd(cell);
        }
    }

    /**
     * Registra una runa de la sala para puzzles de secuencia.
     *
     * @param cell celda de tipo runa
     */
    public void addRuneCell(Cell cell) {
        if (cell != null && !contieneCeldaPorReferencia(runeCells, cell)) {
            runeCells.addEnd(cell);
        }
    }

    /**
     * Configura la secuencia correcta del puzzle de la sala.
     *
     * @param correctSequence orden correcto de activaciones
     */
    public void setCorrectSequence(int[] correctSequence) {
        this.correctSequence = copiarArray(correctSequence);
        this.secuenciaActivada = new int[this.correctSequence.length];
        this.secuenciaActivadaSize = 0;
        this.puzzleResolved = false;
    }

    /**
     * Registra una activación dentro de la secuencia actual.
     *
     * @param indice índice lógico de palanca o runa activada
     */
    public void registrarActivacion(int indice) {
        if (correctSequence.length == 0 || puzzleResolved) {
            return;
        }
        if (secuenciaActivada.length != correctSequence.length) {
            secuenciaActivada = new int[correctSequence.length];
            secuenciaActivadaSize = 0;
        }
        if (secuenciaActivadaSize < secuenciaActivada.length) {
            secuenciaActivada[secuenciaActivadaSize] = indice;
            secuenciaActivadaSize++;
        }
    }

    /**
     * Limpia la secuencia activada por el jugador.
     */
    public void limpiarSecuenciaActivada() {
        this.secuenciaActivada = new int[correctSequence.length];
        this.secuenciaActivadaSize = 0;
    }

    /**
     * Indica si el jugador ya introdujo tantos pasos como exige el puzzle.
     *
     * @return true si la secuencia actual está completa
     */
    public boolean isSequenceComplete() {
        return correctSequence.length > 0 && secuenciaActivadaSize >= correctSequence.length;
    }

    /**
     * Compara la secuencia activada contra la solución.
     *
     * @return true si la secuencia está completa y coincide con la correcta
     */
    public boolean checkSequence() {
        if (!isSequenceComplete()) {
            return false;
        }
        for (int i = 0; i < correctSequence.length; i++) {
            if (correctSequence[i] != secuenciaActivada[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Asocia un trigger de celda con un pasadizo o destino oculto.
     *
     * @param triggerId identificador del trigger
     * @param targetId identificador del pasadizo o destino
     */
    public void addSecretTrigger(String triggerId, String targetId) {
        if (triggerId == null || triggerId.isEmpty() || targetId == null || targetId.isEmpty()) {
            return;
        }
        int posicion = secretTriggerIds.getPosicion(triggerId);
        if (posicion >= 0) {
            return;
        }
        secretTriggerIds.addEnd(triggerId);
        secretTargetIds.addEnd(targetId);
    }

    /**
     * Devuelve el destino asociado a un trigger secreto.
     *
     * @param triggerId identificador del trigger
     * @return id de destino, o null si no existe
     */
    public String getSecretTarget(String triggerId) {
        if (triggerId == null) {
            return null;
        }
        int posicion = secretTriggerIds.getPosicion(triggerId);
        if (posicion < 0) {
            return null;
        }
        return secretTargetIds.get(posicion);
    }

    /**
     * Comprueba si una celda activa un trigger secreto.
     *
     * @param fila fila consultada
     * @param col columna consultada
     * @return true si la celda tiene trigger con destino asociado
     */
    public boolean checkSecretTrigger(int fila, int col) {
        if (!isEnRango(fila, col)) {
            return false;
        }
        Cell cell = celdas[fila][col];
        return cell.hasTrigger() && getSecretTarget(cell.getTriggerId()) != null;
    }

    /**
     * Añade un diálogo especial por personaje.
     *
     * @param tipo personaje al que pertenece el diálogo
     * @param texto texto mostrado al entrar en la sala
     */
    public void addCharacterDialogue(CharacterType tipo, String texto) {
        if (tipo == CharacterType.KAEL) {
            dialogoKael = texto;
            dialogoKaelMostrado = false;
        } else if (tipo == CharacterType.SYRA) {
            dialogoSyra = texto;
            dialogoSyraMostrado = false;
        } else if (tipo == CharacterType.DORATH) {
            dialogoDorath = texto;
            dialogoDorathMostrado = false;
        }
    }

    /**
     * Devuelve el diálogo asociado a un personaje.
     *
     * @param tipo personaje consultado
     * @return diálogo configurado, o null si no hay
     */
    public String getCharacterDialogue(CharacterType tipo) {
        if (tipo == CharacterType.KAEL) {
            return dialogoKael;
        }
        if (tipo == CharacterType.SYRA) {
            return dialogoSyra;
        }
        if (tipo == CharacterType.DORATH) {
            return dialogoDorath;
        }
        return null;
    }

    /**
     * Indica si la sala tiene diálogo para un personaje.
     *
     * @param tipo personaje consultado
     * @return true si hay diálogo no vacío
     */
    public boolean hasCharacterDialogue(CharacterType tipo) {
        String dialogo = getCharacterDialogue(tipo);
        return dialogo != null && !dialogo.isEmpty();
    }

    /**
     * Marca el diálogo de un personaje como mostrado.
     *
     * @param tipo personaje cuyo diálogo se marca
     */
    public void markDialogueShown(CharacterType tipo) {
        if (tipo == CharacterType.KAEL) {
            dialogoKaelMostrado = true;
        } else if (tipo == CharacterType.SYRA) {
            dialogoSyraMostrado = true;
        } else if (tipo == CharacterType.DORATH) {
            dialogoDorathMostrado = true;
        }
    }

    /**
     * Indica si el diálogo de un personaje ya fue mostrado.
     *
     * @param tipo personaje consultado
     * @return true si ya se mostró
     */
    public boolean wasDialogueShown(CharacterType tipo) {
        if (tipo == CharacterType.KAEL) {
            return dialogoKaelMostrado;
        }
        if (tipo == CharacterType.SYRA) {
            return dialogoSyraMostrado;
        }
        if (tipo == CharacterType.DORATH) {
            return dialogoDorathMostrado;
        }
        return false;
    }

    /**
     * Limpia el resaltado visual de todas las celdas.
     */
    public void clearHighlights() {
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < cols; col++) {
                celdas[fila][col].clearHighlight();
            }
        }
    }

    /**
     * Abre o revela accesos que coinciden con un identificador lógico.
     *
     * <p>Se usa al resolver puzzles de sala. El generador puede marcar una
     * puerta, escalera o pasadizo con el mismo {@code triggerId} que el objetivo
     * del puzzle; al completarse la secuencia, esta operación transforma puertas
     * cerradas u ocultas en puertas abiertas.</p>
     *
     * @param triggerId identificador de acceso que debe abrirse
     * @return true si al menos una celda fue modificada
     */
    public boolean openAccessByTrigger(String triggerId) {
        if (triggerId == null || triggerId.isEmpty()) {
            return false;
        }
        boolean abierto = false;
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < cols; col++) {
                Cell cell = celdas[fila][col];
                if (cell != null && triggerId.equals(cell.getTriggerId())) {
                    if (cell.getTipo() == CellType.DOOR_HIDDEN) {
                        cell.revelar();
                        abierto = true;
                    } else if (cell.getTipo() == CellType.DOOR_LOCKED) {
                        cell.setTipo(CellType.DOOR);
                        abierto = true;
                    }
                }
            }
        }
        return abierto;
    }

    /**
     * Valida que una celda pueda usarse como llegada de acceso.
     *
     * @param fila fila destino
     * @param col columna destino
     * @throws InvalidMoveException si la celda no existe o no está libre
     */
    public void validarCeldaLlegada(int fila, int col) throws InvalidMoveException {
        if (!isEnRango(fila, col)) {
            throw new InvalidMoveException("Entrada fuera de rango en sala " + id);
        }
        if (!celdas[fila][col].isWalkable()) {
            throw new InvalidMoveException("La celda de llegada no es transitable en sala " + id);
        }
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
     * Devuelve el aliado NPC especial de esta sala.
     *
     * @return aliado NPC, o null si la sala no lo tiene
     */
    public MalacharAlly getAllyNpc() {
        return allyNpc;
    }

    /**
     * Devuelve las palancas registradas en la sala.
     *
     * @return lista de celdas de palanca
     */
    public ListaSimplementeEnlazada<Cell> getLeverCells() {
        return leverCells;
    }

    /**
     * Devuelve las runas registradas en la sala.
     *
     * @return lista de celdas de runa
     */
    public ListaSimplementeEnlazada<Cell> getRuneCells() {
        return runeCells;
    }

    /**
     * Devuelve una copia de la secuencia correcta.
     *
     * @return secuencia correcta
     */
    public int[] getCorrectSequence() {
        return copiarArray(correctSequence);
    }

    /**
     * Devuelve una copia de la secuencia activada.
     *
     * @return secuencia introducida hasta ahora
     */
    public int[] getSecuenciaActivada() {
        int[] copia = new int[secuenciaActivadaSize];
        for (int i = 0; i < secuenciaActivadaSize; i++) {
            copia[i] = secuenciaActivada[i];
        }
        return copia;
    }

    /**
     * Indica si el puzzle de la sala ya fue resuelto.
     *
     * @return true si el puzzle está resuelto
     */
    public boolean isPuzzleResolved() {
        return puzzleResolved;
    }

    /**
     * Configura si el puzzle de la sala está resuelto.
     *
     * @param puzzleResolved nuevo estado del puzzle
     */
    public void setPuzzleResolved(boolean puzzleResolved) {
        this.puzzleResolved = puzzleResolved;
    }

    /**
     * Configura el destino que se activa al resolver el puzzle.
     *
     * @param puzzleSuccessTarget id de puerta o pasadizo activado
     */
    public void setPuzzleSuccessTarget(String puzzleSuccessTarget) {
        this.puzzleSuccessTarget = puzzleSuccessTarget;
    }

    /**
     * Devuelve el destino que se activa al resolver el puzzle.
     *
     * @return id de puerta o pasadizo
     */
    public String getPuzzleSuccessTarget() {
        return puzzleSuccessTarget;
    }

    /**
     * Configura el daño aplicado al fallar el puzzle de esta sala.
     *
     * @param puzzleFailureDamage daño de fallo, mínimo 0
     */
    public void setPuzzleFailureDamage(int puzzleFailureDamage) {
        if (puzzleFailureDamage < 0) {
            this.puzzleFailureDamage = 0;
        } else {
            this.puzzleFailureDamage = puzzleFailureDamage;
        }
    }

    /**
     * Devuelve el daño aplicado al fallar el puzzle.
     *
     * @return daño de fallo
     */
    public int getPuzzleFailureDamage() {
        return puzzleFailureDamage;
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

    /**
     * Copia un array de enteros sin exponer la referencia interna.
     *
     * @param origen array original
     * @return copia independiente
     */
    private int[] copiarArray(int[] origen) {
        if (origen == null) {
            return new int[0];
        }
        int[] copia = new int[origen.length];
        for (int i = 0; i < origen.length; i++) {
            copia[i] = origen[i];
        }
        return copia;
    }

    /**
     * Comprueba si una lista contiene exactamente la misma instancia de celda.
     *
     * @param lista lista consultada
     * @param cell celda buscada por referencia
     * @return true si la instancia ya está registrada
     */
    private boolean contieneCeldaPorReferencia(ListaSimplementeEnlazada<Cell> lista, Cell cell) {
        for (int i = 0; i < lista.getSize(); i++) {
            if (lista.get(i) == cell) {
                return true;
            }
        }
        return false;
    }
}
