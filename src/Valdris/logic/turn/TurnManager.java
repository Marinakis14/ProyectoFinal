package Valdris.logic.turn;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidAttackException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.ai.IAEnemigo;
import Valdris.logic.bfs.BFSMovimiento;
import Valdris.logic.combat.CombatManager;
import Valdris.model.enums.CellType;
import Valdris.model.enums.Phase;
import Valdris.model.items.Item;
import Valdris.model.map.Cell;
import Valdris.model.map.Container;
import Valdris.model.map.Room;
import Valdris.model.map.Dungeon;
import Valdris.model.units.Enemy;
import Valdris.model.units.Player;

/**
 * Gestiona el ciclo de turnos del juego.
 *
 * <p>El flujo principal sigue el orden definido por la guía:
 * MOVEMENT, PICKUP, USE_ITEM, ATTACK y ENEMY_TURN. Cada fase valida que el
 * jugador no repita la misma acción y avanza de forma explícita a la fase
 * siguiente.</p>
 *
 * <p>TurnManager pertenece a la capa de lógica y no depende de JavaFX ni de
 * GameModel. La interfaz debe llamar a estos métodos y refrescarse desde fuera
 * tras cada cambio de estado.</p>
 *
 * @see Phase
 * @see Dungeon
 * @see Player
 */
public class TurnManager {

    // -- Constantes -----------------------------------------------------------

    /** Direcciones ortogonales usadas para buscar contenedores adyacentes. */
    private static final int[][] DIRECCIONES = {
        {-1, 0},
        {1, 0},
        {0, -1},
        {0, 1}
    };

    // -- Atributos ------------------------------------------------------------

    /** Fase actual del ciclo de turnos. */
    private Phase faseActual;

    /** Dungeon activo de la partida. */
    private final Dungeon dungeon;

    /** Jugador controlado por la partida. */
    private final Player player;

    /** Contador de turnos enemigos resueltos. */
    private int turnoGlobal;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un gestor de turnos al inicio de la fase de movimiento.
     *
     * @param dungeon dungeon activo
     * @param player jugador de la partida
     */
    public TurnManager(Dungeon dungeon, Player player) {
        this.faseActual = Phase.MOVEMENT;
        this.dungeon = dungeon;
        this.player = player;
        this.turnoGlobal = 0;
    }

    // -- Acciones del jugador -------------------------------------------------

    /**
     * Ejecuta el movimiento del jugador dentro de la sala actual.
     *
     * <p>El destino debe pertenecer al rango calculado por BFS, salvo cuando el
     * jugador elige su propia celda para resolver la fase sin desplazarse. Si la
     * celda destino contiene un item de suelo, se recoge automáticamente. Si es
     * una puerta o escalera con destino configurado, cambia de sala.</p>
     *
     * @param filaDestino fila destino
     * @param colDestino columna destino
     * @throws InvalidMoveException si el destino no es válido
     * @throws GameStateException si la fase o el estado de partida no permiten mover
     */
    public void ejecutarMovimiento(int filaDestino, int colDestino)
        throws InvalidMoveException, GameStateException {

        validarFase(Phase.MOVEMENT);
        if (player.isHaMovido()) {
            throw new GameStateException("El jugador ya se movió este turno.");
        }

        Room room = getRoomActualObligatoria();
        validarDestinoMovimiento(room, filaDestino, colDestino);

        moverJugadorEnSala(room, filaDestino, colDestino);
        resolverItemDeSuelo(room.getCell(filaDestino, colDestino));
        player.setHaMovido(true);

        Cell destino = room.getCell(filaDestino, colDestino);
        if (esAccesoConDestino(destino)) {
            changeRoom(destino.getSalaDestino(), destino.getFilaDestino(), destino.getColDestino());
        }
        faseActual = Phase.PICKUP;
    }

    /**
     * Salta la fase de movimiento sin desplazar al jugador.
     *
     * @throws GameStateException si no se está en fase de movimiento
     */
    public void saltarMovimiento() throws GameStateException {
        validarFase(Phase.MOVEMENT);
        if (player.isHaMovido()) {
            throw new GameStateException("El jugador ya resolvió el movimiento este turno.");
        }
        player.setHaMovido(true);
        faseActual = Phase.PICKUP;
    }

    /**
     * Ejecuta la fase de recogida sobre un contenedor adyacente.
     *
     * <p>Si no hay contenedor en las cuatro celdas ortogonales, la fase se
     * consume igualmente y el turno avanza. Los items de suelo no se resuelven
     * aquí: se recogen automáticamente durante el movimiento.</p>
     *
     * @throws GameStateException si no se está en fase de recogida
     */
    public void ejecutarRecogida() throws GameStateException {
        validarFase(Phase.PICKUP);
        if (player.isHaRecogido()) {
            throw new GameStateException("El jugador ya resolvió la recogida este turno.");
        }

        Container container = buscarContenedorAdyacente();
        if (container != null) {
            container.abrir(player);
        }

        player.setHaRecogido(true);
        faseActual = Phase.USE_ITEM;
    }

    /**
     * Salta la fase de recogida sin interactuar con contenedores.
     *
     * @throws GameStateException si no se está en fase de recogida
     */
    public void saltarRecogida() throws GameStateException {
        validarFase(Phase.PICKUP);
        if (player.isHaRecogido()) {
            throw new GameStateException("El jugador ya resolvió la recogida este turno.");
        }
        player.setHaRecogido(true);
        faseActual = Phase.USE_ITEM;
    }

    /**
     * Usa o equipa un item del inventario durante la fase de uso de item.
     *
     * <p>Si el item es null, la llamada se interpreta como salto de fase para
     * mantener compatibilidad con controladores sencillos. La forma explícita
     * recomendada para no usar item es {@link #saltarUsoItem()}.</p>
     *
     * @param item item que se usa o equipa
     * @throws GameStateException si no se está en fase de uso de item
     */
    public void ejecutarUsoItem(Item item) throws GameStateException {
        validarFase(Phase.USE_ITEM);
        if (player.isHaUsadoItem()) {
            throw new GameStateException("El jugador ya usó un item este turno.");
        }
        if (item != null) {
            player.equip(item);
        }
        player.setHaUsadoItem(true);
        faseActual = Phase.ATTACK;
    }

    /**
     * Salta la fase de uso de item.
     *
     * @throws GameStateException si no se está en fase de uso de item
     */
    public void saltarUsoItem() throws GameStateException {
        ejecutarUsoItem(null);
    }

    /**
     * Ejecuta un ataque del jugador contra un enemigo.
     *
     * @param objetivo enemigo objetivo
     * @throws InvalidAttackException si el objetivo no es válido o está fuera de rango
     * @throws GameStateException si no se está en fase de ataque
     */
    public void ejecutarAtaque(Enemy objetivo) throws InvalidAttackException, GameStateException {
        validarFase(Phase.ATTACK);
        if (player.isHaAtacado()) {
            throw new GameStateException("El jugador ya atacó este turno.");
        }
        if (objetivo == null) {
            throw new InvalidAttackException("Debe seleccionarse un enemigo objetivo.");
        }

        CombatManager.resolverAtaqueJugador(player, objetivo, getRoomActualObligatoria());
        player.setHaAtacado(true);
        faseActual = Phase.ENEMY_TURN;
    }

    /**
     * Cede el turno directamente a los enemigos desde cualquier fase del jugador.
     */
    public void cederTurno() {
        faseActual = Phase.ENEMY_TURN;
    }

    // -- Turno enemigo --------------------------------------------------------

    /**
     * Ejecuta el turno de todos los enemigos presentes al inicio de la fase.
     *
     * <p>Si un invocador crea nuevos enemigos, esos enemigos no actúan hasta el
     * siguiente turno enemigo. Al finalizar, se procesan los efectos del jugador
     * y se reinician sus acciones.</p>
     *
     * @throws GameStateException si no se está en fase de enemigos o falla el temporizador
     */
    public void ejecutarTurnoEnemigos() throws GameStateException {
        validarFase(Phase.ENEMY_TURN);

        Room room = getRoomActualObligatoria();
        turnoGlobal++;
        room.decrementarTimer();

        ListaSimplementeEnlazada<Enemy> enemigos = room.getEnemigos();
        int enemigosIniciales = enemigos.getSize();
        for (int i = 0; i < enemigosIniciales; i++) {
            Enemy enemy = enemigos.get(i);
            IAEnemigo.executeTurn(enemy, room, player, null);
        }

        player.procesarEfectos();
        player.resetAcciones();
        faseActual = Phase.MOVEMENT;
    }

    // -- Cambio de sala -------------------------------------------------------

    /**
     * Cambia a una sala destino usando su posición de entrada configurada.
     *
     * @param destino sala a la que entra el jugador
     * @throws InvalidMoveException si la posición de entrada no es válida
     * @throws GameStateException si la sala destino no existe
     */
    public void changeRoom(Room destino) throws InvalidMoveException, GameStateException {
        if (destino == null) {
            throw new GameStateException("La sala destino no puede ser null.");
        }
        changeRoom(destino, destino.getFilaJugador(), destino.getColJugador());
    }

    /**
     * Cambia a una sala destino y coloca al jugador en una coordenada concreta.
     *
     * @param destino sala a la que entra el jugador
     * @param filaEntrada fila donde aparece el jugador
     * @param colEntrada columna donde aparece el jugador
     * @throws InvalidMoveException si la coordenada de entrada no es transitable
     * @throws GameStateException si la sala destino no existe
     */
    public void changeRoom(Room destino, int filaEntrada, int colEntrada)
        throws InvalidMoveException, GameStateException {

        if (destino == null) {
            throw new GameStateException("La sala destino no puede ser null.");
        }
        if (!destino.isEnRango(filaEntrada, colEntrada)) {
            throw new InvalidMoveException("Entrada fuera de rango en sala destino.");
        }

        limpiarJugadorDeSalaActual();
        dungeon.setRoomActual(destino);
        destino.setExplorada(true);
        moverJugadorEnSala(destino, filaEntrada, colEntrada);
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve la fase actual del turno.
     *
     * @return fase actual
     */
    public Phase getFaseActual() {
        return faseActual;
    }

    /**
     * Devuelve el dungeon gestionado.
     *
     * @return dungeon activo
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * Devuelve el jugador gestionado.
     *
     * @return jugador activo
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Devuelve el contador global de turnos.
     *
     * @return turnos enemigos resueltos
     */
    public int getTurnoGlobal() {
        return turnoGlobal;
    }

    // -- Métodos auxiliares ---------------------------------------------------

    /**
     * Valida que el gestor esté en la fase esperada.
     *
     * @param esperada fase requerida
     * @throws GameStateException si la fase actual no coincide
     */
    private void validarFase(Phase esperada) throws GameStateException {
        if (faseActual != esperada) {
            throw new GameStateException("Fase inválida. Esperada: " + esperada + ", actual: " + faseActual);
        }
    }

    /**
     * Devuelve la sala actual o lanza excepción si no existe.
     *
     * @return sala actual del dungeon
     * @throws GameStateException si no hay sala actual
     */
    private Room getRoomActualObligatoria() throws GameStateException {
        if (dungeon == null || dungeon.getRoomActual() == null) {
            throw new GameStateException("No hay sala actual configurada.");
        }
        return dungeon.getRoomActual();
    }

    /**
     * Valida que el destino de movimiento sea alcanzable por BFS.
     *
     * @param room sala actual
     * @param filaDestino fila destino
     * @param colDestino columna destino
     * @throws InvalidMoveException si el destino no es alcanzable
     */
    private void validarDestinoMovimiento(Room room, int filaDestino, int colDestino)
        throws InvalidMoveException {

        if (!room.isEnRango(filaDestino, colDestino)) {
            throw new InvalidMoveException("Destino fuera de rango.");
        }
        if (filaDestino == player.getFilaActual() && colDestino == player.getColActual()) {
            return;
        }

        ListaSimplementeEnlazada<Cell> alcanzables = BFSMovimiento.getCellsInRange(
            room, player.getFilaActual(), player.getColActual(), player.getMovEfectivo());
        Cell destino = room.getCell(filaDestino, colDestino);
        if (!alcanzables.contains(destino)) {
            throw new InvalidMoveException("La celda destino no está en el rango de movimiento.");
        }
    }

    /**
     * Mueve al jugador dentro de una sala sincronizando Player, Room y Cell.
     *
     * @param room sala donde se coloca el jugador
     * @param fila fila destino
     * @param col columna destino
     * @throws InvalidMoveException si la celda destino no es válida
     */
    private void moverJugadorEnSala(Room room, int fila, int col) throws InvalidMoveException {
        Cell destino = room.getCell(fila, col);
        boolean mismaPosicion = player.getFilaActual() == fila && player.getColActual() == col
            && destino.getUnit() == player;
        if (!mismaPosicion && !destino.isWalkable()) {
            throw new InvalidMoveException("La celda destino no es transitable.");
        }

        if (room.isEnRango(player.getFilaActual(), player.getColActual())) {
            Cell origen = room.getCell(player.getFilaActual(), player.getColActual());
            if (origen.getUnit() == player) {
                origen.removeUnit();
            }
        }

        destino.setUnit(player);
        player.setPosicion(fila, col);
        room.setFilaJugador(fila);
        room.setColJugador(col);
    }

    /**
     * Recoge automáticamente el item de suelo de una celda.
     *
     * @param celda celda de la que se retira el item
     */
    private void resolverItemDeSuelo(Cell celda) {
        if (celda == null || celda.getItem() == null) {
            return;
        }
        Item item = celda.removeItem();
        player.addItem(item);
    }

    /**
     * Comprueba si una celda es puerta o escalera con destino configurado.
     *
     * @param celda celda consultada
     * @return true si debe cambiar de sala
     */
    private boolean esAccesoConDestino(Cell celda) {
        if (celda == null || !celda.hasDestinoAcceso()) {
            return false;
        }
        return celda.getTipo() == CellType.DOOR || celda.getTipo() == CellType.STAIRS;
    }

    /**
     * Busca el primer contenedor en las cuatro celdas adyacentes al jugador.
     *
     * @return contenedor encontrado, o null si no hay ninguno accesible
     */
    private Container buscarContenedorAdyacente() {
        try {
            Room room = getRoomActualObligatoria();
            for (int i = 0; i < DIRECCIONES.length; i++) {
                int fila = player.getFilaActual() + DIRECCIONES[i][0];
                int col = player.getColActual() + DIRECCIONES[i][1];
                if (room.isEnRango(fila, col)) {
                    Container container = room.getCell(fila, col).getContainer();
                    if (container != null) {
                        return container;
                    }
                }
            }
        } catch (GameStateException | InvalidMoveException e) {
            return null;
        }
        return null;
    }

    /**
     * Limpia la referencia del jugador en la celda actual antes de cambiar de sala.
     */
    private void limpiarJugadorDeSalaActual() {
        try {
            Room room = getRoomActualObligatoria();
            if (room.isEnRango(player.getFilaActual(), player.getColActual())) {
                Cell celda = room.getCell(player.getFilaActual(), player.getColActual());
                if (celda.getUnit() == player) {
                    celda.removeUnit();
                }
            }
        } catch (GameStateException | InvalidMoveException e) {
            // Si no hay sala actual coherente, changeRoom validará el nuevo destino.
        }
    }
}
