package Valdris.logic.turn;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidAttackException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.ai.IAEnemigo;
import Valdris.logic.bfs.BFSMovimiento;
import Valdris.logic.combat.CombatManager;
import Valdris.logic.puzzle.PuzzleManager;
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

    /** Historial acumulativo de acciones de la partida. */
    private final ListaSimplementeEnlazada<String> log;

    /** Último diálogo generado al entrar en una sala. */
    private String lastDialogue;

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
        this.log = new ListaSimplementeEnlazada<>();
        this.lastDialogue = null;
    }

    // -- Acciones del jugador -------------------------------------------------

    /**
     * Ejecuta el movimiento del jugador dentro de la sala actual.
     *
     * <p>El destino debe pertenecer al rango calculado por BFS, salvo cuando el
     * jugador elige su propia celda para resolver la fase sin desplazarse. Si la
     * celda destino contiene un item de suelo, se recoge automáticamente. Las
     * puertas y escaleras no se pisan: se usan desde una celda adyacente durante
     * la fase de recogida/interacción con {@link #usarAccesoAdyacente()}.</p>
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
        activarTriggerActual();
        activarRunaActual();
        player.setHaMovido(true);

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
            addLog("Contenedor abierto junto al jugador.");
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
     * Usa una puerta o escalera adyacente al jugador.
     *
     * <p>Comparte la fase de interacción con la recogida de contenedores. Las
     * escaleras solo pueden usarse desde su frente configurado; las puertas se
     * pueden usar desde cualquier celda ortogonal adyacente porque se colocan en
     * paredes y solo tienen una celda frontal real dentro de la sala.</p>
     *
     * @throws InvalidMoveException si la llegada configurada no es válida
     * @throws GameStateException si no hay acceso usable o la fase no permite usarlo
     */
    public void usarAccesoAdyacente() throws InvalidMoveException, GameStateException {
        validarFase(Phase.PICKUP);
        if (player.isHaRecogido()) {
            throw new GameStateException("El jugador ya resolvió la interacción este turno.");
        }

        Cell acceso = buscarAccesoInteractuableAdyacente();
        if (acceso == null) {
            throw new GameStateException("No hay puerta o escalera usable junto al jugador.");
        }
        if (acceso.getTipo() == CellType.DOOR_LOCKED && !intentarDesbloquearPuerta(acceso)) {
            throw new GameStateException("La puerta está bloqueada.");
        }

        resolverAcceso(acceso);
        addLog("Acceso usado hacia " + acceso.getSalaDestino().getId() + ".");
        player.setHaRecogido(true);
        faseActual = Phase.USE_ITEM;
    }

    /**
     * Activa una palanca adyacente al jugador.
     *
     * @throws GameStateException si no está en fase de interacción o no hay palanca
     */
    public void activarPalancaAdyacente() throws GameStateException {
        validarFase(Phase.PICKUP);
        if (player.isHaRecogido()) {
            throw new GameStateException("El jugador ya resolvió la interacción este turno.");
        }

        Cell palanca = buscarCeldaAdyacentePorTipo(CellType.LEVER);
        if (palanca == null) {
            throw new GameStateException("No hay palanca adyacente al jugador.");
        }
        PuzzleManager.resolverActivacion(getRoomActualObligatoria(), palanca, dungeon, player);
        addLog("Palanca activada.");
        player.setHaRecogido(true);
        faseActual = Phase.USE_ITEM;
    }

    /**
     * Busca una puerta o escalera usable junto al jugador.
     *
     * @return acceso encontrado, o null si no hay ninguno usable
     */
    public Cell buscarAccesoInteractuableAdyacente() {
        try {
            Room room = getRoomActualObligatoria();
            for (int i = 0; i < DIRECCIONES.length; i++) {
                int fila = player.getFilaActual() + DIRECCIONES[i][0];
                int col = player.getColActual() + DIRECCIONES[i][1];
                if (room.isEnRango(fila, col)) {
                    Cell acceso = room.getCell(fila, col);
                    if (acceso.isUsableFrom(player.getFilaActual(), player.getColActual(), fila, col)) {
                        return acceso;
                    }
                }
            }
        } catch (GameStateException | InvalidMoveException e) {
            return null;
        }
        return null;
    }

    /**
     * Ejecuta el cambio de sala de un acceso ya seleccionado.
     *
     * @param acceso puerta o escalera usada
     * @throws InvalidMoveException si la celda destino no es transitable
     * @throws GameStateException si el acceso no tiene destino funcional
     */
    public void resolverAcceso(Cell acceso) throws InvalidMoveException, GameStateException {
        validarDestinoAcceso(acceso);
        changeRoom(acceso.getSalaDestino(), acceso.getFilaDestino(), acceso.getColDestino());
    }

    /**
     * Valida que un acceso tenga una llegada usable.
     *
     * @param acceso puerta o escalera consultada
     * @throws InvalidMoveException si la coordenada destino es inválida o bloqueada
     * @throws GameStateException si el acceso no está configurado
     */
    public void validarDestinoAcceso(Cell acceso) throws InvalidMoveException, GameStateException {
        if (acceso == null || !acceso.isInteractuableAccess()) {
            throw new GameStateException("El acceso seleccionado no es válido.");
        }
        if (!acceso.hasDestinoAcceso()) {
            throw new GameStateException("El acceso no tiene destino configurado.");
        }
        Room destino = acceso.getSalaDestino();
        int fila = acceso.getFilaDestino();
        int col = acceso.getColDestino();
        if (destino == null) {
            throw new GameStateException("La sala destino no puede ser null.");
        }
        if (!destino.isEnRango(fila, col)) {
            throw new InvalidMoveException("Entrada fuera de rango en sala destino.");
        }
        if (!destino.getCell(fila, col).isWalkable()) {
            throw new InvalidMoveException("La celda de llegada del acceso no es transitable.");
        }
    }

    /**
     * Intenta desbloquear una puerta cerrada con el inventario del jugador.
     *
     * @param puerta puerta que se intenta abrir
     * @return true si queda abierta
     */
    public boolean intentarDesbloquearPuerta(Cell puerta) {
        if (puerta == null || puerta.getTipo() != CellType.DOOR_LOCKED) {
            return false;
        }
        if (!puerta.hasRequiredItem() || !jugadorTieneItemNarrativo(puerta.getRequiredItemId())) {
            return false;
        }
        puerta.setTipo(CellType.DOOR);
        addLog("Puerta desbloqueada con " + puerta.getRequiredItemId() + ".");
        return true;
    }

    /**
     * Comprueba si el jugador tiene un item concreto en el inventario.
     *
     * @param itemId identificador del item narrativo
     * @return true si el inventario contiene ese item
     */
    public boolean jugadorTieneItemNarrativo(String itemId) {
        if (itemId == null || itemId.isEmpty()) {
            return false;
        }
        for (int i = 0; i < player.getInventario().getSize(); i++) {
            Item item = player.getInventario().get(i);
            if (item != null && itemId.equals(item.getId())) {
                return true;
            }
        }
        return false;
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
            addLog("Item usado: " + item.getId() + ".");
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
        addLog("Ataque del jugador contra " + objetivo.getTipo() + ".");
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
        addLog("Turno enemigo resuelto.");
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
        moverJugadorEnSala(destino, filaEntrada, colEntrada);
        onRoomEnter();
    }

    /**
     * Ejecuta la lógica de entrada en la sala actual.
     *
     * @throws GameStateException si no hay sala actual configurada
     */
    public void onRoomEnter() throws GameStateException {
        Room room = getRoomActualObligatoria();
        room.setExplorada(true);
        addLog("Entrada en sala " + room.getId() + ".");

        lastDialogue = null;
        if (room.hasCharacterDialogue(player.getTipo()) && !room.wasDialogueShown(player.getTipo())) {
            lastDialogue = room.getCharacterDialogue(player.getTipo());
            room.markDialogueShown(player.getTipo());
            addLog("Diálogo mostrado en sala " + room.getId() + ".");
        }
    }

    /**
     * Activa el trigger secreto de la celda actual del jugador.
     *
     * @return true si se activó un pasadizo oculto
     * @throws GameStateException si no hay sala actual
     */
    public boolean activarTriggerActual() throws GameStateException {
        try {
            Room room = getRoomActualObligatoria();
            Cell actual = room.getCell(player.getFilaActual(), player.getColActual());
            if (!room.checkSecretTrigger(player.getFilaActual(), player.getColActual())) {
                return false;
            }
            String target = room.getSecretTarget(actual.getTriggerId());
            boolean activado = dungeon.activateHiddenPassage(target);
            if (activado) {
                addLog("Pasadizo oculto activado: " + target + ".");
            }
            return activado;
        } catch (InvalidMoveException e) {
            return false;
        }
    }

    /**
     * Activa una runa si el jugador está sobre ella.
     *
     * @return true si se registró una activación de runa
     * @throws GameStateException si no hay sala actual
     */
    public boolean activarRunaActual() throws GameStateException {
        try {
            Room room = getRoomActualObligatoria();
            Cell actual = room.getCell(player.getFilaActual(), player.getColActual());
            if (actual.getTipo() != CellType.RUNE) {
                return false;
            }
            boolean activada = PuzzleManager.resolverActivacion(room, actual, dungeon, player);
            if (activada) {
                addLog("Runa activada.");
            }
            return activada;
        } catch (InvalidMoveException e) {
            return false;
        }
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

    /**
     * Añade una entrada al historial acumulativo de partida.
     *
     * @param texto texto del evento
     */
    public void addLog(String texto) {
        if (texto != null && !texto.isEmpty()) {
            log.addEnd(texto);
        }
    }

    /**
     * Devuelve el historial completo de la partida.
     *
     * @return lista acumulativa de eventos
     */
    public ListaSimplementeEnlazada<String> getLog() {
        return log;
    }

    /**
     * Devuelve el último diálogo generado por entrada de sala.
     *
     * @return diálogo pendiente para UI o test
     */
    public String getLastDialogue() {
        return lastDialogue;
    }

    /**
     * Devuelve y limpia el último diálogo pendiente.
     *
     * @return diálogo consumido, o null si no hay
     */
    public String consumeLastDialogue() {
        String dialogo = lastDialogue;
        lastDialogue = null;
        return dialogo;
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
     * Busca una celda adyacente de un tipo concreto.
     *
     * @param tipo tipo de celda buscado
     * @return celda encontrada, o null si no hay ninguna
     */
    private Cell buscarCeldaAdyacentePorTipo(CellType tipo) {
        try {
            Room room = getRoomActualObligatoria();
            for (int i = 0; i < DIRECCIONES.length; i++) {
                int fila = player.getFilaActual() + DIRECCIONES[i][0];
                int col = player.getColActual() + DIRECCIONES[i][1];
                if (room.isEnRango(fila, col)) {
                    Cell cell = room.getCell(fila, col);
                    if (cell.getTipo() == tipo) {
                        return cell;
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
