package Valdris.logic.turn;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidAttackException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.ai.AIActionResult;
import Valdris.logic.ai.IAEnemigo;
import Valdris.logic.ai.ArbolDecisionIA.AccionIA;
import Valdris.logic.bfs.BFSMovimiento;
import Valdris.logic.combat.CombatManager;
import Valdris.logic.combat.CombatResult;
import Valdris.logic.puzzle.PuzzleManager;
import Valdris.model.effects.EffectProcessingResult;
import Valdris.model.enums.CellType;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.EnemyType;
import Valdris.model.enums.GameResult;
import Valdris.model.enums.LogEventType;
import Valdris.model.enums.Phase;
import Valdris.model.effects.Effect;
import Valdris.model.items.Item;
import Valdris.model.log.GameLogEntry;
import Valdris.model.map.Cell;
import Valdris.model.map.Container;
import Valdris.model.map.Room;
import Valdris.model.map.Dungeon;
import Valdris.model.units.Enemy;
import Valdris.model.units.MalacharAlly;
import Valdris.model.units.ParasitoEnemy;
import Valdris.model.units.Player;
import Valdris.model.units.Unit;

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

    /** ID de la sala final del Núcleo. */
    private static final String SALA_FINAL_ID = "S5-D";

    /** Posición fija de Malachar en la sala final. */
    private static final int MALACHAR_FILA = 7;

    /** Posición fija de Malachar en la sala final. */
    private static final int MALACHAR_COL = 3;

    /** Posición donde se manifiesta el Parásito. */
    private static final int PARASITO_FILA = 5;

    /** Posición donde se manifiesta el Parásito. */
    private static final int PARASITO_COL = 8;

    /** Radio de los pulsos en área del Parásito. */
    private static final int RADIO_PULSO_PARASITO = 2;

    /** Daño fijo de Pulso del Núcleo. */
    private static final int DANIO_PULSO_NUCLEO = 12;

    /** Daño fijo de Pulso Intensificado. */
    private static final int DANIO_PULSO_INTENSIFICADO = 15;

    /** Daño fijo de Devorar Luz. */
    private static final int DANIO_DEVORAR_LUZ = 20;

    // -- Atributos ------------------------------------------------------------

    /** Fase actual del ciclo de turnos. */
    private Phase faseActual;

    /** Dungeon activo de la partida. */
    private final Dungeon dungeon;

    /** Jugador controlado por la partida. */
    private final Player player;

    /** Contador de turnos enemigos resueltos. */
    private int turnoGlobal;

    /** Historial acumulativo y estructurado de acciones de la partida. */
    private final ListaSimplementeEnlazada<GameLogEntry> log;

    /** Último diálogo generado al entrar en una sala. */
    private String lastDialogue;

    /** Resultado actual de la partida. */
    private GameResult gameResult;

    /** Texto de desenlace final, si la partida terminó. */
    private String endingText;

    /** Frase final de Malachar asociada al personaje. */
    private String finalQuote;

    /** Motivo de derrota, si existe. */
    private String defeatReason;

    /** Indica si el combate final contra el Parásito ya comenzó. */
    private boolean finalCombatStarted;

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
        this.gameResult = GameResult.IN_PROGRESS;
        this.endingText = null;
        this.finalQuote = null;
        this.defeatReason = null;
        this.finalCombatStarted = false;
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

        int filaOrigen = player.getFilaActual();
        int colOrigen = player.getColActual();
        moverJugadorEnSala(room, filaDestino, colDestino);
        resolverItemDeSuelo(room.getCell(filaDestino, colDestino));
        activarTriggerActual();
        activarRunaActual();
        player.setHaMovido(true);
        addLog(LogEventType.MOVEMENT, nombreJugador(),
            nombreJugador() + " se mueve de (" + filaOrigen + "," + colOrigen + ") a ("
                + filaDestino + "," + colDestino + ") en " + room.getId() + ".",
            "origen=" + filaOrigen + "," + colOrigen + ";destino=" + filaDestino + "," + colDestino);

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
        addLog(LogEventType.MOVEMENT, nombreJugador(),
            nombreJugador() + " decide no moverse en " + idSalaActual() + ".", null);
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
            addLog(LogEventType.PICKUP, nombreJugador(),
                nombreJugador() + " abre " + container.getNombre() + ".", "containerId=" + container.getId());
        } else {
            addLog(LogEventType.PICKUP, nombreJugador(),
                nombreJugador() + " no encuentra contenedores adyacentes.", null);
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
        addLog(LogEventType.PICKUP, nombreJugador(),
            nombreJugador() + " decide no recoger ni interactuar.", null);
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
        addLog(LogEventType.ACCESS, nombreJugador(),
            nombreJugador() + " usa un acceso hacia " + acceso.getSalaDestino().getId() + ".",
            "destino=" + acceso.getSalaDestino().getId());
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
        addLog(LogEventType.PUZZLE, nombreJugador(),
            nombreJugador() + " activa una palanca en " + idSalaActual() + ".", null);
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
        addLog(LogEventType.ACCESS, nombreJugador(),
            nombreJugador() + " desbloquea una puerta con " + puerta.getRequiredItemId() + ".",
            "itemId=" + puerta.getRequiredItemId());
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
        if (player.tieneItemNarrativo(itemId)) {
            return true;
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
     * @param item item que se usa o equipa
     * @throws GameStateException si no se está en fase de uso de item
     */
    public void ejecutarUsoItem(Item item) throws GameStateException {
        validarFase(Phase.USE_ITEM);
        if (player.isHaUsadoItem()) {
            throw new GameStateException("El jugador ya usó un item este turno.");
        }
        if (item == null) {
            throw new GameStateException("Para saltar el uso de item debe llamarse a saltarUsoItem().");
        }
        player.equip(item);
        addLog(LogEventType.ITEM, nombreJugador(),
            nombreJugador() + " usa " + item.getId() + " - " + item.getNombre() + ".",
            "itemId=" + item.getId());
        player.setHaUsadoItem(true);
        faseActual = Phase.ATTACK;
    }

    /**
     * Salta la fase de uso de item.
     *
     * @throws GameStateException si no se está en fase de uso de item
     */
    public void saltarUsoItem() throws GameStateException {
        validarFase(Phase.USE_ITEM);
        if (player.isHaUsadoItem()) {
            throw new GameStateException("El jugador ya usó un item este turno.");
        }
        addLog(LogEventType.ITEM, nombreJugador(), nombreJugador() + " decide no usar item.", null);
        player.setHaUsadoItem(true);
        faseActual = Phase.ATTACK;
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

        CombatResult result = CombatManager.resolverAtaqueJugador(player, objetivo, getRoomActualObligatoria());
        registrarResultadoAtaqueJugador(objetivo, result);
        if (objetivo instanceof ParasitoEnemy) {
            resolverEstadoParasitoTrasDanio((ParasitoEnemy) objetivo, getRoomActualObligatoria());
        }
        player.setHaAtacado(true);
        if (gameResult == GameResult.IN_PROGRESS) {
            faseActual = Phase.ENEMY_TURN;
        }
    }

    /**
     * Cede el turno directamente a los enemigos desde cualquier fase del jugador.
     */
    public void cederTurno() {
        addLog(LogEventType.COMBAT, nombreJugador(), nombreJugador() + " cede el turno.", null);
        faseActual = Phase.ENEMY_TURN;
    }

    // -- Combate final --------------------------------------------------------

    /**
     * Inicia el combate final tras hablar con Malachar.
     *
     * <p>El jugador debe estar en S5-D y adyacente a Malachar. Al iniciarse el
     * diálogo, el Parásito se manifiesta en su posición fija y el ciclo táctico
     * vuelve a movimiento para que la batalla empiece limpia.</p>
     *
     * @throws GameStateException si no se cumplen las condiciones del combate final
     */
    public void iniciarCombateFinal() throws GameStateException {
        validarPartidaEnCurso();
        Room room = getRoomActualObligatoria();
        if (!SALA_FINAL_ID.equals(room.getId())) {
            throw new GameStateException("El combate final solo puede iniciarse en " + SALA_FINAL_ID + ".");
        }
        if (finalCombatStarted) {
            throw new GameStateException("El combate final ya está iniciado.");
        }

        MalacharAlly malachar = asegurarMalacharEnSalaFinal(room);
        if (distanciaManhattan(player.getFilaActual(), player.getColActual(),
            malachar.getFilaActual(), malachar.getColActual()) != 1) {
            throw new GameStateException("El jugador debe estar junto a Malachar para iniciar el diálogo.");
        }
        if (!room.isEnRango(PARASITO_FILA, PARASITO_COL)) {
            throw new GameStateException("La posición del Parásito no pertenece a la sala final.");
        }

        ParasitoEnemy parasito = new ParasitoEnemy(PARASITO_FILA, PARASITO_COL, room.getId());
        room.addEnemigo(parasito);
        finalCombatStarted = true;
        lastDialogue = crearDialogoMalachar(player.getTipo());
        addLog(LogEventType.ROOM, "Malachar", lastDialogue, "finalDialogue=true");
        addLog(LogEventType.GAME, "PARASITO",
            "El Parásito se manifiesta en el Núcleo Profundo.", "phase=1");
        player.resetAcciones();
        faseActual = Phase.MOVEMENT;
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
        validarPartidaEnCurso();

        Room room = getRoomActualObligatoria();
        turnoGlobal++;
        room.decrementarTimer();

        if (finalCombatStarted) {
            ejecutarTurnoAliadoFinal(room);
            if (gameResult != GameResult.IN_PROGRESS) {
                return;
            }
        }

        ListaSimplementeEnlazada<Enemy> enemigos = room.getEnemigos();
        Enemy[] enemigosTurno = new Enemy[enemigos.getSize()];
        for (int i = 0; i < enemigosTurno.length; i++) {
            enemigosTurno[i] = enemigos.get(i);
        }

        int enemigosIniciales = enemigosTurno.length;
        for (int i = 0; i < enemigosTurno.length; i++) {
            Enemy enemy = enemigosTurno[i];
            if (enemy instanceof ParasitoEnemy) {
                ejecutarTurnoParasito((ParasitoEnemy) enemy, room);
            } else {
                AIActionResult result = IAEnemigo.executeTurn(enemy, room, player, null);
                registrarResultadoIA(result);
            }
            if (gameResult != GameResult.IN_PROGRESS) {
                return;
            }
        }

        EffectProcessingResult efectosJugador = player.procesarEfectos();
        registrarResultadoEfectos(nombreJugador(), efectosJugador, player.getHp(), player.getHpMax());
        comprobarDerrotaJugador("efectos de estado");
        player.resetAcciones();
        addLog(LogEventType.ENEMY_TURN, "ENEMIGOS",
            "Turno enemigo resuelto en " + room.getId() + ".", "enemigosIniciales=" + enemigosIniciales);
        if (gameResult == GameResult.IN_PROGRESS) {
            faseActual = Phase.MOVEMENT;
        }
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
        addLog(LogEventType.ROOM, nombreJugador(),
            nombreJugador() + " entra en " + room.getId() + " - " + room.getNombre() + ".",
            "salaId=" + room.getId());

        lastDialogue = null;
        if (room.hasCharacterDialogue(player.getTipo()) && !room.wasDialogueShown(player.getTipo())) {
            lastDialogue = room.getCharacterDialogue(player.getTipo());
            room.markDialogueShown(player.getTipo());
            addLog(LogEventType.ROOM, nombreJugador(),
                "Diálogo de " + nombreJugador() + " mostrado en " + room.getId() + ".", null);
        }
        if (SALA_FINAL_ID.equals(room.getId()) && !finalCombatStarted) {
            asegurarMalacharEnSalaFinal(room);
            addLog(LogEventType.ROOM, "Malachar",
                "Malachar espera en silencio en el Núcleo.", "fila=" + MALACHAR_FILA + ";col=" + MALACHAR_COL);
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
                addLog(LogEventType.ACCESS, nombreJugador(),
                    "Pasadizo oculto activado: " + target + ".", "target=" + target);
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
                addLog(LogEventType.PUZZLE, nombreJugador(),
                    nombreJugador() + " activa una runa en " + room.getId() + ".", null);
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
     * Añade una entrada estructurada al historial acumulativo.
     *
     * @param tipo tipo de evento
     * @param actor actor principal
     * @param mensaje mensaje visible
     * @param detalle detalle opcional
     */
    public void addLog(LogEventType tipo, String actor, String mensaje, String detalle) {
        if (mensaje != null && !mensaje.isEmpty()) {
            log.addEnd(new GameLogEntry(turnoGlobal, tipo, actor, idSalaActual(), mensaje, detalle));
        }
    }

    /**
     * Añade una entrada ya reconstruida desde persistencia.
     *
     * @param entry entrada estructurada
     */
    public void addLogEntry(GameLogEntry entry) {
        if (entry != null && !entry.getMensaje().isEmpty()) {
            log.addEnd(entry);
        }
    }

    /**
     * Devuelve el historial estructurado completo de la partida.
     *
     * @return lista acumulativa de eventos
     */
    public ListaSimplementeEnlazada<GameLogEntry> getLog() {
        return log;
    }

    /**
     * Devuelve el historial como textos visibles.
     *
     * @return array con cada entrada formateada
     */
    public String[] getLogTextos() {
        String[] textos = new String[log.getSize()];
        for (int i = 0; i < log.getSize(); i++) {
            textos[i] = log.get(i).toString();
        }
        return textos;
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

    /**
     * Devuelve el resultado actual de partida.
     *
     * @return resultado de partida
     */
    public GameResult getGameResult() {
        return gameResult;
    }

    /**
     * Devuelve el texto de desenlace final.
     *
     * @return texto final, o null si la partida sigue en curso
     */
    public String getEndingText() {
        return endingText;
    }

    /**
     * Devuelve la frase final de Malachar.
     *
     * @return frase final, o null si no existe
     */
    public String getFinalQuote() {
        return finalQuote;
    }

    /**
     * Devuelve el motivo de derrota.
     *
     * @return motivo de derrota, o null si no hay derrota
     */
    public String getDefeatReason() {
        return defeatReason;
    }

    /**
     * Indica si el combate final ya comenzó.
     *
     * @return true si el Parásito ya se manifestó
     */
    public boolean isFinalCombatStarted() {
        return finalCombatStarted;
    }

    /**
     * Restaura la fase actual desde persistencia.
     *
     * @param faseActual fase guardada
     */
    public void setFaseActual(Phase faseActual) {
        if (faseActual != null) {
            this.faseActual = faseActual;
        }
    }

    /**
     * Restaura el turno global desde persistencia.
     *
     * @param turnoGlobal turno guardado
     */
    public void setTurnoGlobal(int turnoGlobal) {
        if (turnoGlobal < 0) {
            this.turnoGlobal = 0;
        } else {
            this.turnoGlobal = turnoGlobal;
        }
    }

    /**
     * Restaura el último diálogo pendiente desde persistencia.
     *
     * @param lastDialogue diálogo pendiente
     */
    public void setLastDialogue(String lastDialogue) {
        this.lastDialogue = lastDialogue;
    }

    /**
     * Restaura el resultado de partida desde persistencia.
     *
     * @param gameResult resultado guardado
     */
    public void setGameResult(GameResult gameResult) {
        if (gameResult != null) {
            this.gameResult = gameResult;
        }
    }

    /**
     * Restaura el texto de desenlace.
     *
     * @param endingText texto guardado
     */
    public void setEndingText(String endingText) {
        this.endingText = endingText;
    }

    /**
     * Restaura la frase final.
     *
     * @param finalQuote frase guardada
     */
    public void setFinalQuote(String finalQuote) {
        this.finalQuote = finalQuote;
    }

    /**
     * Restaura el motivo de derrota.
     *
     * @param defeatReason motivo guardado
     */
    public void setDefeatReason(String defeatReason) {
        this.defeatReason = defeatReason;
    }

    /**
     * Restaura si el combate final ya estaba iniciado.
     *
     * @param finalCombatStarted estado guardado
     */
    public void setFinalCombatStarted(boolean finalCombatStarted) {
        this.finalCombatStarted = finalCombatStarted;
    }

    // -- Métodos auxiliares ---------------------------------------------------

    /**
     * Ejecuta el turno aliado de Malachar en el combate final.
     *
     * @param room sala final
     */
    private void ejecutarTurnoAliadoFinal(Room room) {
        MalacharAlly malachar = room.getAllyNpc();
        ParasitoEnemy parasito = buscarParasito(room);
        if (malachar == null || parasito == null || !parasito.isVivo()) {
            return;
        }
        if (malachar.procesarRecuperacionTurno()) {
            addLog(LogEventType.STATE, "Malachar",
                "Malachar permanece paralizado y se recupera. Turnos restantes: "
                    + malachar.getTurnosRecuperacion() + ".",
                "recuperacion=" + malachar.getTurnosRecuperacion());
            return;
        }

        try {
            if (CombatManager.estaEnRango(malachar, parasito, room)) {
                int danio = CombatManager.calcularDanio(malachar, parasito);
                parasito.recibirDanio(danio);
                addLog(LogEventType.COMBAT, "Malachar",
                    "Malachar golpea al Parásito e inflige " + danio + " daño. HP Parásito: "
                        + parasito.getHp() + "/" + parasito.getHpMax() + ".",
                    "danio=" + danio + ";phase=" + parasito.getPhase());
                resolverEstadoParasitoTrasDanio(parasito, room);
            } else if (moverUnidadHacia(malachar, room, parasito.getFilaActual(), parasito.getColActual())) {
                addLog(LogEventType.ENEMY_TURN, "Malachar",
                    "Malachar avanza hacia el Parásito.", "fila=" + malachar.getFilaActual()
                        + ";col=" + malachar.getColActual());
            } else {
                addLog(LogEventType.ENEMY_TURN, "Malachar",
                    "Malachar no encuentra una ruta clara hacia el Parásito.", null);
            }
        } catch (InvalidAttackException e) {
            addLog(LogEventType.ENEMY_TURN, "Malachar",
                "Malachar no consigue canalizar su ataque.", "ATAQUE_INVALIDO");
        }
    }

    /**
     * Ejecuta el turno especial del Parásito.
     *
     * @param parasito Parásito activo
     * @param room sala final
     */
    private void ejecutarTurnoParasito(ParasitoEnemy parasito, Room room) {
        if (parasito == null || !parasito.isVivo()) {
            resolverEstadoParasitoTrasDanio(parasito, room);
            return;
        }

        boolean paralizado = parasito.tieneEfecto(EffectType.PARALYSIS);
        EffectProcessingResult efectos = parasito.procesarEfectos();
        registrarResultadoEfectos("Parásito", efectos, parasito.getHp(), parasito.getHpMax());
        resolverEstadoParasitoTrasDanio(parasito, room);
        if (gameResult != GameResult.IN_PROGRESS || !parasito.isVivo()) {
            return;
        }
        if (parasito.consumirSaltoPorTransicion()) {
            addLog(LogEventType.ENEMY_TURN, "Parásito",
                "El Parásito termina de recomponer su nueva forma.", "phase=" + parasito.getPhase());
            return;
        }
        if (paralizado) {
            addLog(LogEventType.ENEMY_TURN, "Parásito", "El Parásito no actúa por PARALYSIS.", null);
            return;
        }

        if (parasito.getPhase() == ParasitoEnemy.FASE_CORAZA) {
            ejecutarAtaqueDirectoParasito(parasito, room, "Zarpazo del Umbral", false);
        } else if (parasito.getPhase() == ParasitoEnemy.FASE_DESGARRADA) {
            ejecutarAccionFaseDosParasito(parasito, room);
        } else {
            ejecutarAccionFaseTresParasito(parasito, room);
        }
    }

    /**
     * Ejecuta la prioridad de acciones de fase 2.
     */
    private void ejecutarAccionFaseDosParasito(ParasitoEnemy parasito, Room room) {
        if (parasito.isAoeListo() && hayObjetivoEnRadioPulso(parasito, room)) {
            ejecutarPulsoParasito(parasito, room, "Pulso del Núcleo", DANIO_PULSO_NUCLEO);
            parasito.resetAoeCooldown();
            return;
        }
        boolean actuo = ejecutarAtaqueDirectoParasito(parasito, room, "Zarpazo Maldito", true);
        if (!actuo) {
            moverParasitoHaciaJugador(parasito, room);
        }
        if (!parasito.isAoeListo()) {
            parasito.incrementarAoeCooldown();
        }
    }

    /**
     * Ejecuta la prioridad de acciones de fase 3.
     */
    private void ejecutarAccionFaseTresParasito(ParasitoEnemy parasito, Room room) {
        if (parasito.isAoeListo() && hayObjetivoEnRadioPulso(parasito, room)) {
            ejecutarPulsoParasito(parasito, room, "Pulso Intensificado", DANIO_PULSO_INTENSIFICADO);
            parasito.resetAoeCooldown();
            return;
        }
        boolean actuo = ejecutarAtaqueDirectoParasito(parasito, room, "Desgarro Profundo", false);
        if (!actuo) {
            moverParasitoHaciaJugador(parasito, room);
        }
        if (!parasito.isAoeListo()) {
            parasito.incrementarAoeCooldown();
        }
    }

    /**
     * Ejecuta un ataque directo del Parásito contra el jugador.
     *
     * @return true si atacó, false si estaba fuera de rango o visión
     */
    private boolean ejecutarAtaqueDirectoParasito(ParasitoEnemy parasito, Room room, String nombreAtaque,
                                                  boolean aplicaCurse) {
        if (!CombatManager.estaEnRango(parasito, player, room)) {
            return false;
        }
        try {
            CombatResult combat = CombatManager.resolverAtaqueEnemigo(parasito, player);
            AIActionResult result = new AIActionResult(AccionIA.ATACAR, EnemyType.PARASITO, room.getId(),
                parasito.getFilaActual(), parasito.getColActual(), parasito.getFilaActual(),
                parasito.getColActual(), combat, null, null, null, -1, -1, null, null,
                "Parásito", nombreAtaque);
            registrarCombateEnemigo(result);
            if (aplicaCurse && !combat.isFalloPorBlind()) {
                player.addEfecto(new Effect(EffectType.CURSE, 2));
                addLog(LogEventType.STATE, "Parásito",
                    "Zarpazo Maldito aplica CURSE a " + nombreJugador() + ".", "efecto=CURSE");
            }
            comprobarDerrotaJugador(nombreAtaque);
            return true;
        } catch (InvalidAttackException e) {
            return false;
        }
    }

    /**
     * Ejecuta un AOE del Parásito que atraviesa paredes y unidades.
     */
    private void ejecutarPulsoParasito(ParasitoEnemy parasito, Room room, String nombreAtaque, int danio) {
        player.recibirDanio(danio);
        CombatResult combat = new CombatResult(danio, false, !player.isVivo(), player.getHp(), player.getHpMax(),
            null, null, null);
        AIActionResult result = new AIActionResult(AccionIA.AOE, EnemyType.PARASITO, room.getId(),
            parasito.getFilaActual(), parasito.getColActual(), parasito.getFilaActual(),
            parasito.getColActual(), combat, null, null, null, -1, -1, null, null,
            "Parásito", nombreAtaque);
        registrarCombateEnemigo(result);
        aplicarDanioAOEAMalachar(room, nombreAtaque, danio);
        comprobarDerrotaJugador(nombreAtaque);
    }

    /**
     * Ejecuta Devorar Luz como explosión global de transición.
     */
    private void ejecutarDevorarLuz(ParasitoEnemy parasito, Room room) {
        player.recibirDanio(DANIO_DEVORAR_LUZ);
        player.addEfecto(new Effect(EffectType.BLIND, 2));
        addLog(LogEventType.ENEMY_TURN, "Parásito",
            "El Parásito usa Devorar Luz: " + DANIO_DEVORAR_LUZ
                + " daño fijo y BLIND sobre " + nombreJugador() + ". HP jugador: "
                + player.getHp() + "/" + player.getHpMax() + ".",
            "habilidad=Devorar Luz;danio=" + DANIO_DEVORAR_LUZ);
        aplicarDanioAOEAMalachar(room, "Devorar Luz", DANIO_DEVORAR_LUZ);
        comprobarDerrotaJugador("Devorar Luz");
    }

    /**
     * Aplica daño de AOE a Malachar si está presente.
     */
    private void aplicarDanioAOEAMalachar(Room room, String nombreAtaque, int danio) {
        MalacharAlly malachar = room.getAllyNpc();
        if (malachar == null) {
            return;
        }
        if (!"Devorar Luz".equals(nombreAtaque)) {
            ParasitoEnemy parasito = buscarParasito(room);
            if (parasito == null || distanciaManhattan(malachar.getFilaActual(), malachar.getColActual(),
                parasito.getFilaActual(), parasito.getColActual()) > RADIO_PULSO_PARASITO) {
                return;
            }
        }
        int hpAntes = malachar.getHp();
        malachar.recibirDanio(danio);
        addLog(LogEventType.COMBAT, "Parásito",
            nombreAtaque + " alcanza a Malachar e inflige " + danio + " daño. HP Malachar: "
                + malachar.getHp() + "/" + malachar.getHpMax() + ".",
            "objetivo=Malachar;danio=" + danio + ";hpAntes=" + hpAntes);
    }

    /**
     * Resuelve transiciones o victoria tras dañar al Parásito.
     */
    private void resolverEstadoParasitoTrasDanio(ParasitoEnemy parasito, Room room) {
        if (parasito == null) {
            return;
        }
        if (!parasito.isVivo() && parasito.getPhase() == ParasitoEnemy.FASE_ESENCIA) {
            triggerEnding(player.getTipo());
            return;
        }
        if (parasito.consumirTransicionPendiente()) {
            addLog(LogEventType.GAME, "Parásito",
                "El Parásito cambia a fase " + parasito.getPhase() + " y recompone su forma. HP: "
                    + parasito.getHp() + "/" + parasito.getHpMax() + ".",
                "phase=" + parasito.getPhase());
            if (parasito.consumirDevorarLuzPendiente()) {
                ejecutarDevorarLuz(parasito, room);
            }
        }
    }

    /**
     * Mueve al Parásito hacia el jugador si no puede atacar.
     */
    private void moverParasitoHaciaJugador(ParasitoEnemy parasito, Room room) {
        int filaOrigen = parasito.getFilaActual();
        int colOrigen = parasito.getColActual();
        boolean movido = moverUnidadHacia(parasito, room, player.getFilaActual(), player.getColActual());
        if (movido) {
            addLog(LogEventType.ENEMY_TURN, "Parásito",
                "El Parásito se mueve de (" + filaOrigen + "," + colOrigen + ") a ("
                    + parasito.getFilaActual() + "," + parasito.getColActual() + ").",
                "origen=" + filaOrigen + "," + colOrigen + ";destino="
                    + parasito.getFilaActual() + "," + parasito.getColActual());
        } else {
            addLog(LogEventType.ENEMY_TURN, "Parásito",
                "El Parásito no encuentra una ruta hacia " + nombreJugador() + ".", null);
        }
    }

    /**
     * Indica si jugador o Malachar están en radio de pulso.
     */
    private boolean hayObjetivoEnRadioPulso(ParasitoEnemy parasito, Room room) {
        if (distanciaManhattan(parasito.getFilaActual(), parasito.getColActual(),
            player.getFilaActual(), player.getColActual()) <= RADIO_PULSO_PARASITO) {
            return true;
        }
        MalacharAlly malachar = room.getAllyNpc();
        return malachar != null && distanciaManhattan(parasito.getFilaActual(), parasito.getColActual(),
            malachar.getFilaActual(), malachar.getColActual()) <= RADIO_PULSO_PARASITO;
    }

    /**
     * Busca el Parásito vivo o persistido en la sala.
     */
    private ParasitoEnemy buscarParasito(Room room) {
        if (room == null) {
            return null;
        }
        for (int i = 0; i < room.getEnemigos().getSize(); i++) {
            Enemy enemy = room.getEnemigos().get(i);
            if (enemy instanceof ParasitoEnemy) {
                return (ParasitoEnemy) enemy;
            }
        }
        return null;
    }

    /**
     * Asegura que Malachar existe en la sala final.
     */
    private MalacharAlly asegurarMalacharEnSalaFinal(Room room) throws GameStateException {
        if (room == null || !SALA_FINAL_ID.equals(room.getId())) {
            throw new GameStateException("Malachar solo puede colocarse en la sala final.");
        }
        MalacharAlly malachar = room.getAllyNpc();
        if (malachar == null) {
            malachar = new MalacharAlly(MALACHAR_FILA, MALACHAR_COL);
            room.setAllyNpc(malachar);
        }
        return malachar;
    }

    /**
     * Mueve una unidad hacia una celda adyacente al objetivo.
     */
    private boolean moverUnidadHacia(Unit unit, Room room, int filaObjetivo, int colObjetivo) {
        if (unit == null || room == null || unit.getMovEfectivo() <= 0) {
            return false;
        }
        Cell destino = buscarMejorDestinoAdyacente(unit, room, filaObjetivo, colObjetivo);
        if (destino == null) {
            return false;
        }
        Posicion posicion = buscarPosicion(room, destino);
        if (posicion == null) {
            return false;
        }
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(room,
            unit.getFilaActual(), unit.getColActual(), posicion.getFila(), posicion.getCol());
        if (camino.getSize() <= 1) {
            return false;
        }
        int pasos = Math.min(unit.getMovEfectivo(), camino.getSize() - 1);
        Posicion pasoDestino = buscarPosicion(room, camino.get(pasos));
        return pasoDestino != null && moverUnidadA(unit, room, pasoDestino.getFila(), pasoDestino.getCol());
    }

    /**
     * Busca la mejor celda adyacente libre a un objetivo.
     */
    private Cell buscarMejorDestinoAdyacente(Unit unit, Room room, int filaObjetivo, int colObjetivo) {
        Cell mejor = null;
        int mejorLongitud = -1;
        for (int i = 0; i < DIRECCIONES.length; i++) {
            int fila = filaObjetivo + DIRECCIONES[i][0];
            int col = colObjetivo + DIRECCIONES[i][1];
            if (!room.isEnRango(fila, col)) {
                continue;
            }
            try {
                Cell cell = room.getCell(fila, col);
                if (!cell.isWalkable()) {
                    continue;
                }
                ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(room,
                    unit.getFilaActual(), unit.getColActual(), fila, col);
                int longitud = camino.getSize();
                if (longitud > 0 && (mejor == null || longitud < mejorLongitud)) {
                    mejor = cell;
                    mejorLongitud = longitud;
                }
            } catch (InvalidMoveException e) {
                // Se ignora una coordenada inválida.
            }
        }
        return mejor;
    }

    /**
     * Mueve una unidad dentro de una sala sincronizando su celda.
     */
    private boolean moverUnidadA(Unit unit, Room room, int fila, int col) {
        try {
            Cell destino = room.getCell(fila, col);
            if (!destino.isWalkable()) {
                return false;
            }
            if (room.isEnRango(unit.getFilaActual(), unit.getColActual())) {
                Cell origen = room.getCell(unit.getFilaActual(), unit.getColActual());
                if (origen.getUnit() == unit) {
                    origen.removeUnit();
                }
            }
            unit.setPosicion(fila, col);
            destino.setUnit(unit);
            return true;
        } catch (InvalidMoveException e) {
            return false;
        }
    }

    /**
     * Busca las coordenadas de una celda dentro de la sala.
     */
    private Posicion buscarPosicion(Room room, Cell cell) {
        if (room == null || cell == null) {
            return null;
        }
        for (int fila = 0; fila < room.getFilas(); fila++) {
            for (int col = 0; col < room.getCols(); col++) {
                try {
                    if (room.getCell(fila, col) == cell) {
                        return new Posicion(fila, col);
                    }
                } catch (InvalidMoveException e) {
                    // La iteración respeta límites.
                }
            }
        }
        return null;
    }

    /**
     * Activa la victoria final.
     *
     * @param tipo personaje que alcanza el desenlace
     */
    public void triggerEnding(CharacterType tipo) {
        if (gameResult != GameResult.IN_PROGRESS) {
            return;
        }
        gameResult = GameResult.VICTORY;
        endingText = crearEndingText(tipo);
        finalQuote = crearFinalQuote(tipo);
        defeatReason = null;
        player.setHp(0);
        try {
            Room room = getRoomActualObligatoria();
            if (room.getAllyNpc() != null) {
                room.getAllyNpc().setHp(0);
            }
        } catch (GameStateException e) {
            // El final puede registrarse aunque no haya sala actual coherente.
        }
        addLog(LogEventType.GAME, "Valdris",
            "El Parásito desaparece. " + nombreJugador() + " y Malachar se sacrifican para salvar Valdris.",
            "result=VICTORY");
    }

    /**
     * Activa una derrota de partida.
     */
    private void triggerDefeat(String motivo) {
        if (gameResult != GameResult.IN_PROGRESS) {
            return;
        }
        gameResult = GameResult.DEFEAT;
        defeatReason = motivo;
        endingText = "El Núcleo Profundo consume la última esperanza de Valdris.";
        finalQuote = null;
        addLog(LogEventType.GAME, "Valdris", "Derrota: " + motivo + ".", "result=DEFEAT");
    }

    /**
     * Comprueba si el jugador ha caído.
     */
    private void comprobarDerrotaJugador(String motivo) {
        if (!player.isVivo() && gameResult == GameResult.IN_PROGRESS) {
            triggerDefeat(nombreJugador() + " cae por " + motivo);
        }
    }

    /**
     * Valida que la partida no haya terminado.
     */
    private void validarPartidaEnCurso() throws GameStateException {
        if (gameResult != GameResult.IN_PROGRESS) {
            throw new GameStateException("La partida ya ha terminado: " + gameResult);
        }
    }

    /**
     * Calcula distancia Manhattan entre dos coordenadas.
     */
    private int distanciaManhattan(int filaA, int colA, int filaB, int colB) {
        return Math.abs(filaA - filaB) + Math.abs(colA - colB);
    }

    /**
     * Crea el diálogo principal de Malachar.
     */
    private String crearDialogoMalachar(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return "Malachar revela a Syra que el Parásito está devorando la raíz mágica de Valdris.";
        }
        if (tipo == CharacterType.DORATH) {
            return "Malachar confirma a Dorath que la Orden ocultó que el sello siempre fue temporal.";
        }
        return "Malachar explica a Kael que el sello no fue una prisión, sino una contención desesperada.";
    }

    /**
     * Crea el texto de desenlace final.
     */
    private String crearEndingText(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return "Syra cae pronunciando los nombres antiguos de Lireth mientras la vida vuelve al bosque.";
        }
        if (tipo == CharacterType.DORATH) {
            return "Dorath entrega la verdad al futuro: los textos sobreviven y la Orden ya no puede enterrarla.";
        }
        return "Kael cumple la deuda del sello y su sacrificio permite que Valdris vuelva a respirar.";
    }

    /**
     * Crea la frase final de Malachar.
     */
    private String crearFinalQuote(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return "Que Lireth recuerde tu nombre cuando vuelva a crecer.";
        }
        if (tipo == CharacterType.DORATH) {
            return "La verdad también puede ser una forma de misericordia.";
        }
        return "No heredaste mi culpa, Kael. La cerraste.";
    }

    /**
     * Coordenada auxiliar de sala.
     */
    private static final class Posicion {

        /** Fila de la posición. */
        private final int fila;

        /** Columna de la posición. */
        private final int col;

        /**
         * Crea una posición.
         *
         * @param fila fila
         * @param col columna
         */
        private Posicion(int fila, int col) {
            this.fila = fila;
            this.col = col;
        }

        /**
         * Devuelve la fila.
         *
         * @return fila
         */
        private int getFila() {
            return fila;
        }

        /**
         * Devuelve la columna.
         *
         * @return columna
         */
        private int getCol() {
            return col;
        }
    }

    /**
     * Valida que el gestor esté en la fase esperada.
     *
     * @param esperada fase requerida
     * @throws GameStateException si la fase actual no coincide
     */
    private void validarFase(Phase esperada) throws GameStateException {
        validarPartidaEnCurso();
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
        addLog(LogEventType.PICKUP, nombreJugador(),
            nombreJugador() + " recoge " + item.getId() + " - " + item.getNombre() + ".",
            "itemId=" + item.getId());
    }

    /**
     * Registra el resultado de un ataque del jugador.
     *
     * @param objetivo enemigo atacado
     * @param result resultado de combate
     */
    private void registrarResultadoAtaqueJugador(Enemy objetivo, CombatResult result) {
        if (result.isFalloPorBlind()) {
            addLog(LogEventType.COMBAT, nombreJugador(),
                nombreJugador() + " falla el ataque contra " + objetivo.getTipo() + " por BLIND.", null);
            return;
        }
        addLog(LogEventType.COMBAT, nombreJugador(),
            nombreJugador() + " inflige " + result.getDanioAplicado() + " daño a " + objetivo.getTipo()
                + ". HP enemigo: " + result.getHpRestanteObjetivo() + "/" + result.getHpMaxObjetivo() + ".",
            "objetivo=" + objetivo.getTipo() + ";danio=" + result.getDanioAplicado());
        if (result.getEfectoPrimarioAplicado() != null) {
            addLog(LogEventType.STATE, nombreJugador(),
                objetivo.getTipo() + " recibe " + result.getEfectoPrimarioAplicado() + ".", null);
        }
        if (result.getEfectoSecundarioAplicado() != null) {
            addLog(LogEventType.STATE, nombreJugador(),
                objetivo.getTipo() + " recibe " + result.getEfectoSecundarioAplicado() + ".", null);
        }
        if (result.isObjetivoMuerto()) {
            addLog(LogEventType.COMBAT, nombreJugador(), objetivo.getTipo() + " muere.", null);
            if (result.getDropItemId() != null) {
                addLog(LogEventType.PICKUP, objetivo.getTipo().name(),
                    objetivo.getTipo() + " deja caer " + result.getDropItemId() + ".",
                    "dropItemId=" + result.getDropItemId());
            }
        }
    }

    /**
     * Registra el resultado de una acción enemiga.
     *
     * @param result resultado de IA
     */
    private void registrarResultadoIA(AIActionResult result) {
        if (result == null) {
            return;
        }
        String actor = result.getNombreActor();
        registrarResultadoEfectos(actor, result.getEffectProcessingResult(), -1, -1);
        if (result.getMotivo() != null && result.getAccion() == AccionIA.ESPERAR) {
            addLog(LogEventType.ENEMY_TURN, actor, actor + " no actúa: " + result.getMotivo() + ".",
                result.getMotivo());
            if ("MUERTO_POR_EFECTOS".equals(result.getMotivo())) {
                addLog(LogEventType.COMBAT, actor, actor + " muere por efectos.", null);
                if (result.getDropItemId() != null) {
                    addLog(LogEventType.PICKUP, actor,
                        actor + " deja caer " + result.getDropItemId() + ".",
                        "dropItemId=" + result.getDropItemId());
                }
            }
            return;
        }
        if (result.getCombatResult() != null) {
            registrarCombateEnemigo(result);
        }
        if (result.getEfectoAplicado() != null) {
            addLog(LogEventType.STATE, actor,
                actor + " aplica " + result.getEfectoAplicado() + " a " + nombreJugador() + ".",
                "efecto=" + result.getEfectoAplicado());
        }
        if (result.getTipoInvocado() != null) {
            addLog(LogEventType.ENEMY_TURN, actor,
                actor + " invoca " + result.getTipoInvocado() + " en ("
                    + result.getFilaInvocado() + "," + result.getColInvocado() + ").",
                "invocado=" + result.getTipoInvocado());
        }
        if (result.huboMovimiento()) {
            addLog(LogEventType.ENEMY_TURN, actor,
                actor + " se mueve de (" + result.getFilaOrigen() + ","
                    + result.getColOrigen() + ") a (" + result.getFilaDestino() + ","
                    + result.getColDestino() + ").",
                "origen=" + result.getFilaOrigen() + "," + result.getColOrigen()
                    + ";destino=" + result.getFilaDestino() + "," + result.getColDestino());
        }
        if (result.getMotivo() != null && result.getAccion() != AccionIA.ESPERAR) {
            addLog(LogEventType.ENEMY_TURN, actor, actor + ": " + result.getMotivo() + ".",
                result.getMotivo());
        }
    }

    /**
     * Registra un ataque enemigo normal o AOE.
     *
     * @param result resultado de IA con combate
     */
    private void registrarCombateEnemigo(AIActionResult result) {
        CombatResult combat = result.getCombatResult();
        String actor = result.getNombreActor();
        if (combat.isFalloPorBlind()) {
            String accionFallida = result.getHabilidadEspecial() == null
                ? "su ataque" : result.getHabilidadEspecial();
            addLog(LogEventType.ENEMY_TURN, actor,
                actor + " falla " + accionFallida + " por BLIND.", null);
            return;
        }
        if (result.getAccion() == AccionIA.HABILIDAD_ESPECIAL) {
            addLog(LogEventType.ENEMY_TURN, actor,
                actor + " usa " + result.getHabilidadEspecial() + " contra " + nombreJugador()
                    + " e inflige " + combat.getDanioAplicado() + " daño. HP jugador: "
                    + combat.getHpRestanteObjetivo() + "/" + combat.getHpMaxObjetivo() + ".",
                "habilidad=" + result.getHabilidadEspecial() + ";danio=" + combat.getDanioAplicado());
        } else {
            String accion = result.getAccion() == AccionIA.AOE ? "alcanza con AOE a" : "ataca a";
            addLog(LogEventType.ENEMY_TURN, actor,
                actor + " " + accion + " " + nombreJugador() + " e inflige "
                    + combat.getDanioAplicado() + " daño. HP jugador: "
                    + combat.getHpRestanteObjetivo() + "/" + combat.getHpMaxObjetivo() + ".",
                "danio=" + combat.getDanioAplicado());
        }
        if (combat.isObjetivoMuerto()) {
            addLog(LogEventType.COMBAT, actor, nombreJugador() + " cae derrotado por " + actor + ".", null);
        }
    }

    /**
     * Registra daño periódico y expiraciones de efectos.
     *
     * @param actor unidad afectada
     * @param result resultado de procesamiento
     * @param hpActual HP actual, o negativo si no se quiere mostrar
     * @param hpMax HP máximo, o negativo si no se quiere mostrar
     */
    private void registrarResultadoEfectos(String actor, EffectProcessingResult result, int hpActual, int hpMax) {
        if (result == null || !result.tieneEventos()) {
            return;
        }
        if (result.getDanioAplicado() > 0) {
            String hpTexto = hpActual >= 0 && hpMax >= 0 ? " HP: " + hpActual + "/" + hpMax + "." : "";
            addLog(LogEventType.STATE, actor,
                actor + " recibe " + result.getDanioAplicado() + " daño por efectos." + hpTexto,
                "danioEfectos=" + result.getDanioAplicado());
        }
        EffectType[] expirados = result.getEfectosExpirados();
        for (int i = 0; i < expirados.length; i++) {
            addLog(LogEventType.STATE, actor, expirados[i] + " expira en " + actor + ".", "efecto=" + expirados[i]);
        }
    }

    /**
     * Devuelve el nombre del jugador para logs.
     *
     * @return nombre del personaje
     */
    private String nombreJugador() {
        return player.getTipo().name();
    }

    /**
     * Devuelve la sala actual para logs sin lanzar excepciones.
     *
     * @return id de sala, o null si no hay sala actual
     */
    private String idSalaActual() {
        if (dungeon == null || dungeon.getRoomActual() == null) {
            return null;
        }
        return dungeon.getRoomActual().getId();
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
