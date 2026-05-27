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

    /** Limite global de turnos de una partida completa. */
    private static final int TURNO_GLOBAL_MAXIMO = 500;

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
        resolverRecogida(null);
    }

    /**
     * Ejecuta la fase de recogida escogiendo un item concreto de un contenedor con alternativas.
     *
     * @param itemIdElegido identificador del item elegido
     * @throws GameStateException si no se esta en fase de recogida o la eleccion no es valida
     */
    public void ejecutarRecogida(String itemIdElegido) throws GameStateException {
        resolverRecogida(itemIdElegido);
    }

    /**
     * Indica si el contenedor adyacente requiere elegir una recompensa.
     *
     * @return true si hay un contenedor cerrado con mas de una opcion
     */
    public boolean requiereEleccionContenedorAdyacente() {
        return requiereEleccion(buscarContenedorAdyacente());
    }

    /**
     * Devuelve las opciones del contenedor adyacente que requiere eleccion.
     *
     * @return array de items disponibles, o vacio si no hay eleccion pendiente
     */
    public Item[] getOpcionesContenedorAdyacente() {
        Container container = buscarContenedorAdyacente();
        if (!requiereEleccion(container)) {
            return new Item[0];
        }
        Item[] opciones = new Item[container.getItems().getSize()];
        for (int i = 0; i < container.getItems().getSize(); i++) {
            opciones[i] = container.getItems().get(i);
        }
        return opciones;
    }

    /**
     * Resuelve la fase de recogida, con o sin eleccion de recompensa.
     *
     * @param itemIdElegido item elegido para cofres de alternativa
     * @throws GameStateException si la fase o la eleccion no son validas
     */
    private void resolverRecogida(String itemIdElegido) throws GameStateException {
        validarFase(Phase.PICKUP);
        if (player.isHaRecogido()) {
            throw new GameStateException("El jugador ya resolvió la recogida este turno.");
        }

        Container container = buscarContenedorAdyacente();
        if (container != null) {
            boolean estabaAbierto = container.isAbierto();
            String contenido = textoContenidoContainer(container);
            Item seleccionado = null;
            if (requiereEleccion(container)) {
                if (itemIdElegido == null || itemIdElegido.isEmpty()) {
                    throw new GameStateException("El cofre contiene varias armas. Elige una recompensa antes de abrirlo.");
                }
                seleccionado = container.abrirSeleccionando(player, itemIdElegido);
                if (seleccionado == null) {
                    throw new GameStateException("La recompensa elegida no esta en este cofre.");
                }
            } else {
                container.abrir(player);
            }
            addLog(LogEventType.PICKUP, nombreJugador(),
                mensajeAperturaContainer(container, contenido, estabaAbierto, seleccionado),
                "containerId=" + container.getId() + ";items=" + contenido);
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
        if (hayEnemigosVivos(getRoomActualObligatoria())) {
            throw new GameStateException("No se puede usar el acceso hasta derrotar a todos los enemigos.");
        }
        if (acceso.getTipo() == CellType.DOOR_LOCKED && !intentarDesbloquearPuerta(acceso)) {
            throw new GameStateException("La puerta está bloqueada.");
        }

        Room salaOrigen = getRoomActualObligatoria();
        resolverAcceso(acceso);
        addLog(LogEventType.ACCESS, nombreJugador(),
            nombreJugador() + " usa un acceso hacia " + acceso.getSalaDestino().getId() + ".",
            "destino=" + acceso.getSalaDestino().getId());
        if (dungeon.getRoomActual() == salaOrigen) {
            player.setHaRecogido(true);
            faseActual = Phase.USE_ITEM;
        }
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
        Room room = getRoomActualObligatoria();
        boolean estabaResuelto = room.isPuzzleResolved();
        int hpAntes = player.getHp();
        PuzzleManager.resolverActivacion(room, palanca, dungeon, player);
        addLog(LogEventType.PUZZLE, nombreJugador(),
            nombreJugador() + " activa una palanca en " + idSalaActual() + ".", null);
        registrarResultadoPuzzle(room, estabaResuelto, hpAntes);
        player.setHaRecogido(true);
        faseActual = Phase.USE_ITEM;
    }

    /**
     * Construye el mensaje narrativo de apertura de un contenedor.
     *
     * @param container contenedor abierto
     * @param contenido texto con los items que tenia antes de abrirse
     * @param estabaAbierto true si ya se habia abierto antes
     * @return mensaje para el log visible
     */
    private String mensajeAperturaContainer(Container container, String contenido, boolean estabaAbierto,
                                           Item seleccionado) {
        String nombreContainer = container == null ? "el cofre" : container.getNombre();
        if (estabaAbierto) {
            return nombreJugador() + " revisa " + nombreContainer
                + ", pero dentro solo queda el polvo removido de antes.";
        }
        if (seleccionado != null) {
            return nombreJugador() + " abre " + nombreContainer
                + "; las armas reposan bajo una luz antigua y elige " + seleccionado.getNombre()
                + " [" + seleccionado.getId() + "].";
        }
        if (contenido == null || contenido.isEmpty()) {
            return nombreJugador() + " abre " + nombreContainer
                + "; la tapa cruje, pero el interior esta vacio.";
        }
        return nombreJugador() + " abre " + nombreContainer
            + "; entre madera vieja y polvo antiguo encuentra " + contenido + ".";
    }

    /**
     * Devuelve una lista legible con los items pendientes de un contenedor.
     *
     * @param container contenedor consultado
     * @return nombres de items separados por coma
     */
    private String textoContenidoContainer(Container container) {
        if (container == null || container.getItems().isEmpty()) {
            return "";
        }
        String texto = "";
        for (int i = 0; i < container.getItems().getSize(); i++) {
            Item item = container.getItems().get(i);
            if (item == null) {
                continue;
            }
            if (!texto.isEmpty()) {
                texto += ", ";
            }
            texto += item.getNombre() + " [" + item.getId() + "]";
        }
        return texto;
    }

    /**
     * Indica si un contenedor cerrado contiene varias recompensas alternativas.
     *
     * @param container contenedor consultado
     * @return true si se debe elegir una recompensa
     */
    private boolean requiereEleccion(Container container) {
        return container != null && !container.isAbierto() && container.getItems().getSize() > 1;
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
     * Calcula la distancia hasta la celda usable de la salida abierta mas cercana.
     *
     * <p>Las puertas y escaleras no son transitables, asi que la distancia se
     * mide hasta una celda adyacente desde la que el jugador podria usar el
     * acceso. Si ya esta junto a la salida, devuelve 0. Si hay enemigos vivos o
     * no existe ninguna salida abierta alcanzable, devuelve -1.</p>
     *
     * @return distancia minima en casillas, o -1 si no hay salida usable
     */
    public int getDistanciaSalidaAbiertaMasCercana() {
        try {
            Room room = getRoomActualObligatoria();
            if (hayEnemigosVivos(room)) {
                return -1;
            }
            int mejorDistancia = -1;
            for (int fila = 0; fila < room.getFilas(); fila++) {
                for (int col = 0; col < room.getCols(); col++) {
                    Cell acceso = room.getCell(fila, col);
                    if (!esSalidaAbierta(acceso)) {
                        continue;
                    }
                    int distancia = getDistanciaAAcceso(room, acceso, fila, col);
                    if (distancia >= 0 && (mejorDistancia < 0 || distancia < mejorDistancia)) {
                        mejorDistancia = distancia;
                    }
                }
            }
            return mejorDistancia;
        } catch (GameStateException | InvalidMoveException e) {
            return -1;
        }
    }

    /**
     * Indica si la sala actual conserva enemigos vivos.
     *
     * @return true si queda al menos un enemigo vivo en la sala actual
     */
    public boolean hayEnemigosVivosSalaActual() {
        try {
            return hayEnemigosVivos(getRoomActualObligatoria());
        } catch (GameStateException e) {
            return false;
        }
    }

    /**
     * Calcula la distancia de celdas hasta el mejor acceso hacia la sala final.
     *
     * <p>La ruta elegida minimiza el coste total: pasos dentro de la sala actual
     * hasta una celda de uso del acceso mas numero de salas restantes hasta
     * la sala objetivo configurada. Si hay empate, se prioriza menor distancia
     * de sala, menor distancia de celdas y finalmente el id de la sala destino.</p>
     *
     * @return distancia en casillas hasta el acceso elegido, o -1 si no hay ruta
     */
    public int getDistanciaSalidaGlobal() {
        try {
            Room room = getRoomActualObligatoria();
            if (getIdSalaObjetivo().equals(room.getId())) {
                return 0;
            }
            RutaObjetivo ruta = buscarMejorRutaObjetivoGlobal(room);
            return ruta == null ? -1 : ruta.distanciaCeldas;
        } catch (GameStateException | InvalidMoveException e) {
            return -1;
        }
    }

    /**
     * Calcula el numero minimo de conexiones de sala hasta la sala final.
     *
     * @return numero de salas restantes, o -1 si no hay ruta
     */
    public int getSalasHastaObjetivoGlobal() {
        try {
            Room room = getRoomActualObligatoria();
            return getDistanciaSalasHastaObjetivo(room);
        } catch (GameStateException e) {
            return -1;
        }
    }

    /**
     * Devuelve el id de la siguiente sala que conviene tomar hacia el objetivo.
     *
     * @return id de sala destino elegida, o null si no hay ruta
     */
    public String getIdSiguienteSalaObjetivoGlobal() {
        try {
            Room room = getRoomActualObligatoria();
            if (getIdSalaObjetivo().equals(room.getId())) {
                return room.getId();
            }
            RutaObjetivo ruta = buscarMejorRutaObjetivoGlobal(room);
            return ruta == null || ruta.destino == null ? null : ruta.destino.getId();
        } catch (GameStateException | InvalidMoveException e) {
            return null;
        }
    }

    /**
     * Devuelve el camino de celdas que debe resaltarse para llegar al acceso global.
     *
     * <p>El camino pertenece siempre a la sala actual. Incluye la celda actual
     * del jugador, la celda desde la que puede usarse la puerta o escalera
     * elegida y la propia celda de acceso para que la interfaz pueda mostrar el
     * objetivo de forma clara aunque el acceso no sea transitable.</p>
     *
     * @return camino de celdas en la sala actual, o lista vacia si no hay ruta
     */
    public ListaSimplementeEnlazada<Cell> getCaminoReveladoSalaActual() {
        ListaSimplementeEnlazada<Cell> vacio = new ListaSimplementeEnlazada<>();
        try {
            Room room = getRoomActualObligatoria();
            if (getIdSalaObjetivo().equals(room.getId())) {
                return vacio;
            }
            RutaObjetivo ruta = buscarMejorRutaObjetivoGlobal(room);
            return ruta == null ? vacio : copiarCamino(ruta.caminoCeldas);
        } catch (GameStateException | InvalidMoveException e) {
            return vacio;
        }
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

        Room roomActual = getRoomActualObligatoria();
        CombatResult result = CombatManager.resolverAtaqueJugador(player, objetivo, roomActual);
        registrarResultadoAtaqueJugador(objetivo, result);
        if (objetivo instanceof ParasitoEnemy) {
            resolverEstadoParasitoTrasDanio((ParasitoEnemy) objetivo, roomActual);
        } else {
            retirarEnemigoDerrotado(roomActual, objetivo, result);
        }
        player.setHaAtacado(true);
        if (gameResult == GameResult.IN_PROGRESS) {
            faseActual = Phase.ENEMY_TURN;
        }
    }

    /**
     * Cede el turno directamente a los enemigos desde cualquier fase del jugador.
     *
     * @throws GameStateException si la partida ya terminó
     */
    public void cederTurno() throws GameStateException {
        validarPartidaEnCurso();
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
        if (turnoGlobal >= TURNO_GLOBAL_MAXIMO) {
            triggerDefeat("se agota el limite global de " + TURNO_GLOBAL_MAXIMO + " turnos");
            return;
        }
        if (!decrementarTimerSalaActual(room)) {
            return;
        }

        procesarEfectosJugadorAlCerrarTurno();
        if (gameResult != GameResult.IN_PROGRESS) {
            return;
        }

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

        player.resetAcciones();
        addLog(LogEventType.ENEMY_TURN, "ENEMIGOS",
            "Turno enemigo resuelto en " + room.getId() + ".", "enemigosIniciales=" + enemigosIniciales);
        if (gameResult == GameResult.IN_PROGRESS) {
            faseActual = Phase.MOVEMENT;
        }
    }

    /**
     * Procesa los efectos del jugador despues de que haya resuelto su turno completo.
     */
    private void procesarEfectosJugadorAlCerrarTurno() {
        EffectProcessingResult efectosJugador = player.procesarEfectos();
        registrarResultadoEfectos(nombreJugador(), efectosJugador, player.getHp(), player.getHpMax());
        comprobarDerrotaJugador(motivoDerrotaPorEfectos(efectosJugador));
    }

    /**
     * Decrementa el temporizador de la sala y convierte su agotamiento en derrota.
     *
     * @param room sala actual
     * @return true si la partida puede continuar
     */
    private boolean decrementarTimerSalaActual(Room room) {
        try {
            room.decrementarTimer();
            return true;
        } catch (GameStateException e) {
            triggerDefeat("se agota el limite de turnos de " + room.getId());
            return false;
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
        player.resetAcciones();
        faseActual = Phase.MOVEMENT;
        destino.reiniciarTimerSala();
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
        validarPartidaEnCurso();
        try {
            Room room = getRoomActualObligatoria();
            Cell actual = room.getCell(player.getFilaActual(), player.getColActual());
            if (!room.checkSecretTrigger(player.getFilaActual(), player.getColActual())) {
                return false;
            }
            String target = room.getSecretTarget(actual.getTriggerId());
            boolean activado = dungeon.activateHiddenPassage(target);
            if (activado) {
                room.openAccessByTrigger(actual.getTriggerId());
                addLog(LogEventType.ACCESS, nombreJugador(),
                    "Una corriente de aire revela un pasadizo oculto: " + target + ".",
                    "target=" + target + ";trigger=" + actual.getTriggerId());
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
        validarPartidaEnCurso();
        try {
            Room room = getRoomActualObligatoria();
            Cell actual = room.getCell(player.getFilaActual(), player.getColActual());
            if (actual.getTipo() != CellType.RUNE) {
                return false;
            }
            boolean estabaResuelto = room.isPuzzleResolved();
            int hpAntes = player.getHp();
            boolean activada = PuzzleManager.resolverActivacion(room, actual, dungeon, player);
            if (activada) {
                addLog(LogEventType.PUZZLE, nombreJugador(),
                    nombreJugador() + " activa una runa en " + room.getId() + ".", null);
                registrarResultadoPuzzle(room, estabaResuelto, hpAntes);
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
     * Devuelve el limite global de turnos de la partida.
     *
     * @return turnos maximos permitidos
     */
    public int getTurnoGlobalMaximo() {
        return TURNO_GLOBAL_MAXIMO;
    }

    /**
     * Devuelve los turnos consumidos en la sala actual.
     *
     * @return turnos gastados, o -1 si la sala no tiene limite
     */
    public int getTurnosSalaConsumidos() {
        try {
            Room room = getRoomActualObligatoria();
            if (!room.hasRoomTimer()) {
                return -1;
            }
            int consumidos = room.getTurnosMaximos() - room.getTurnosRestantes();
            return Math.max(consumidos, 0);
        } catch (GameStateException e) {
            return -1;
        }
    }

    /**
     * Devuelve el limite de turnos de la sala actual.
     *
     * @return turnos maximos de sala, o -1 si no hay limite
     */
    public int getTurnosSalaMaximos() {
        try {
            Room room = getRoomActualObligatoria();
            return room.hasRoomTimer() ? room.getTurnosMaximos() : -1;
        } catch (GameStateException e) {
            return -1;
        }
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
        int danioJugador = CombatManager.aplicarBonusCurse(player, danio);
        player.recibirDanio(danioJugador);
        CombatResult combat = new CombatResult(danioJugador, false, !player.isVivo(), player.getHp(), player.getHpMax(),
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
        int danioJugador = CombatManager.aplicarBonusCurse(player, DANIO_DEVORAR_LUZ);
        player.recibirDanio(danioJugador);
        player.addEfecto(new Effect(EffectType.BLIND, 2));
        addLog(LogEventType.ENEMY_TURN, "Parásito",
            "El Parásito usa Devorar Luz: " + danioJugador
                + " daño y BLIND sobre " + nombreJugador() + ". HP jugador: "
                + player.getHp() + "/" + player.getHpMax() + ".",
            "habilidad=Devorar Luz;danio=" + danioJugador);
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
            return "Malachar baja la voz al reconocer a Syra. Le cuenta que Lireth no enfermó por abandono ni por "
                + "una maldición del bosque: el Parásito lleva siglos bebiendo de sus raíces mágicas desde abajo. "
                + "Cada espíritu corrompido, cada árbol doblado hacia la tierra, era una señal de que Valdris se "
                + "estaba quedando sin aliento.";
        }
        if (tipo == CharacterType.DORATH) {
            return "Malachar mira a Dorath sin intentar defenderse. Confirma lo que la Orden enterró en archivos "
                + "cerrados: el sello siempre fue temporal, los guardianes lo sabían y el silencio se convirtió en "
                + "dogma. No pide absolución; solo le entrega la verdad completa antes de que el Núcleo termine de "
                + "abrirse.";
        }
        return "Malachar observa el guantelete de Kael y entiende la marca del sello. Le explica que aquello nunca "
            + "fue una simple prisión, sino una contención desesperada contra algo más antiguo que su culpa. El "
            + "sello rechazó a Kael porque ya estaba roto, no porque él no fuera digno de cargarlo.";
    }

    /**
     * Crea el texto de desenlace final.
     */
    private String crearEndingText(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return "Syra no cierra los ojos cuando la luz del Núcleo la envuelve. Pronuncia despacio los nombres "
                + "antiguos de Lireth, uno por uno, como si cada palabra pudiera guiar a los espíritus perdidos de "
                + "vuelta a casa. Tres días después, en la superficie, un árbol del bosque crece hacia el cielo por "
                + "primera vez en años. Nadie ve el milagro, pero Valdris lo recuerda.";
        }
        if (tipo == CharacterType.DORATH) {
            return "Dorath muere con la certeza que había perseguido desde su excomunión. Los textos que rescató "
                + "de la Torre de Embrath sobreviven, y con ellos la prueba de que la Orden mintió por miedo. Cuando "
                + "los reinos pregunten qué ocurrió bajo el continente, habrá palabras escritas por un excomulgado "
                + "que ya nadie podrá borrar.";
        }
        return "En el último instante, el guantelete de Kael se abre y la mano quemada deja de doler. No cae como "
            + "aprendiz rechazado ni como heredero fallido, sino como el guardián que llegó cuando todos los demás "
            + "ya no podían. En Embrath, su nombre será añadido al final de la lista: el que cerró la deuda del "
            + "sello y permitió que Valdris volviera a respirar.";
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
        if (!contieneCeldaPorReferencia(alcanzables, destino)) {
            throw new InvalidMoveException("La celda destino no está en el rango de movimiento.");
        }
    }

    /**
     * Comprueba si una lista contiene exactamente la misma instancia de celda.
     *
     * <p>Las listas propias comparan con {@code compareTo}. Para movimiento se
     * necesita identidad real de celda, porque dos casillas de suelo vacias no
     * representan el mismo destino aunque tengan el mismo contenido.</p>
     *
     * @param celdas lista consultada
     * @param buscada celda buscada
     * @return true si la instancia exacta esta en la lista
     */
    private boolean contieneCeldaPorReferencia(ListaSimplementeEnlazada<Cell> celdas, Cell buscada) {
        if (celdas == null || buscada == null) {
            return false;
        }
        for (int i = 0; i < celdas.getSize(); i++) {
            if (celdas.get(i) == buscada) {
                return true;
            }
        }
        return false;
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
     * Retira de la sala a un enemigo derrotado por el ataque del jugador.
     *
     * <p>El drop ya se ha colocado antes en {@link CombatManager}, por lo que
     * esta limpieza solo elimina la unidad y la entrada de la lista de enemigos
     * vivos. El Parásito se excluye porque su muerte activa el desenlace final y
     * se gestiona con reglas propias de fase.</p>
     *
     * @param room sala donde ocurre el combate
     * @param objetivo enemigo atacado
     * @param result resultado del ataque
     */
    private void retirarEnemigoDerrotado(Room room, Enemy objetivo, CombatResult result) {
        if (room != null && objetivo != null && result != null && result.isObjetivoMuerto()) {
            room.removeEnemigo(objetivo);
        }
    }

    /**
     * Registra el resultado visible de una secuencia de puzzle si acaba de resolverse o fallar.
     *
     * @param room sala del puzzle
     * @param estabaResuelto estado anterior del puzzle
     * @param hpAntes HP del jugador antes de activar la pieza
     */
    private void registrarResultadoPuzzle(Room room, boolean estabaResuelto, int hpAntes) {
        if (room == null) {
            return;
        }
        if (!estabaResuelto && room.isPuzzleResolved()) {
            addLog(LogEventType.PUZZLE, "PUZZLE",
                "Combinación correcta. El mecanismo se ha activado.",
                "target=" + room.getPuzzleSuccessTarget());
            return;
        }
        int danio = hpAntes - player.getHp();
        if (danio > 0) {
            addLog(LogEventType.PUZZLE, "PUZZLE",
                "Combinación incorrecta. El puzzle se reinicia. " + nombreJugador()
                    + " recibe " + danio + " daño.",
                "danio=" + danio);
            registrarPistaPuzzle(room);
            comprobarDerrotaJugador("fallo de puzzle en " + room.getId());
        }
    }

    /**
     * Crea un motivo de derrota legible para ataques enemigos normales.
     *
     * @param result resultado de IA que ha dañado al jugador
     * @return texto de causa para la pantalla final
     */
    private String motivoDerrotaPorIA(AIActionResult result) {
        if (result == null) {
            return "ataque enemigo";
        }
        String actor = result.getNombreActor();
        if (result.getHabilidadEspecial() != null && !result.getHabilidadEspecial().isEmpty()) {
            return result.getHabilidadEspecial() + " de " + actor;
        }
        return "ataque de " + actor;
    }

    /**
     * Crea un motivo de derrota concreto cuando el daño viene de efectos.
     *
     * @param result resultado de procesar efectos del jugador
     * @return texto de causa para la pantalla final
     */
    private String motivoDerrotaPorEfectos(EffectProcessingResult result) {
        if (result == null || result.getDanioAplicado() <= 0) {
            return "efectos de estado";
        }
        EffectType[] expirados = result.getEfectosExpirados();
        for (int i = 0; i < expirados.length; i++) {
            if (expirados[i] == EffectType.BURN) {
                return "BURN";
            }
        }
        return "efectos de estado";
    }

    /**
     * Registra una pista progresiva según los fallos acumulados del puzzle.
     *
     * @param room sala del puzzle
     */
    private void registrarPistaPuzzle(Room room) {
        if (room == null) {
            return;
        }
        int[] secuencia = room.getCorrectSequence();
        int fallos = room.getPuzzleFailureCount();
        if (secuencia.length == 0 || fallos <= 0) {
            return;
        }
        if (fallos == 1) {
            addLog(LogEventType.PUZZLE, "PUZZLE",
                "Pista: empieza por " + posicionVisible(secuencia[0]) + ".",
                "pista=" + posicionVisible(secuencia[0]));
        } else if (fallos <= secuencia.length) {
            addLog(LogEventType.PUZZLE, "PUZZLE",
                "Pista: la siguiente es la " + posicionVisible(secuencia[fallos - 1]) + ".",
                "pista=" + posicionVisible(secuencia[fallos - 1]));
        }
        if (fallos >= secuencia.length) {
            String textoSecuencia = textoSecuenciaCorrecta(room);
            addLog(LogEventType.PUZZLE, "PUZZLE",
                "Pista: la combinación correcta es: " + textoSecuencia + ".",
                "secuencia=" + textoSecuencia);
        }
    }

    /**
     * Devuelve la secuencia correcta del puzzle en posiciones visibles para el jugador.
     *
     * @param room sala del puzzle
     * @return texto con posiciones empezando en 1
     */
    private String textoSecuenciaCorrecta(Room room) {
        if (room == null) {
            return "-";
        }
        int[] secuencia = room.getCorrectSequence();
        if (secuencia.length == 0) {
            return "-";
        }
        String texto = "";
        for (int i = 0; i < secuencia.length; i++) {
            if (i > 0) {
                texto += ", ";
            }
            texto += String.valueOf(secuencia[i] + 1);
        }
        return texto;
    }

    /**
     * Convierte un índice interno de puzzle a posición visible.
     *
     * @param indice índice interno desde 0
     * @return posición visible desde 1
     */
    private String posicionVisible(int indice) {
        return String.valueOf(indice + 1);
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
            comprobarDerrotaJugador(motivoDerrotaPorIA(result));
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
            String detalleHabilidad = result.getAccion() == AccionIA.AOE
                && result.getHabilidadEspecial() != null ? " con " + result.getHabilidadEspecial() : "";
            String detalleLog = "danio=" + combat.getDanioAplicado();
            if (result.getHabilidadEspecial() != null) {
                detalleLog = "habilidad=" + result.getHabilidadEspecial() + ";" + detalleLog;
            }
            addLog(LogEventType.ENEMY_TURN, actor,
                actor + " " + accion + " " + nombreJugador() + detalleHabilidad + " e inflige "
                    + combat.getDanioAplicado() + " daño. HP jugador: "
                    + combat.getHpRestanteObjetivo() + "/" + combat.getHpMaxObjetivo() + ".",
                detalleLog);
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
     * Comprueba si quedan enemigos vivos en una sala.
     *
     * @param room sala consultada
     * @return true si hay al menos un enemigo vivo
     */
    private boolean hayEnemigosVivos(Room room) {
        if (room == null) {
            return false;
        }
        ListaSimplementeEnlazada<Enemy> enemigos = room.getEnemigos();
        for (int i = 0; i < enemigos.getSize(); i++) {
            Enemy enemy = enemigos.get(i);
            if (enemy != null && enemy.isVivo()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comprueba si una celda es una salida abierta que puede contarse en la UI.
     *
     * @param cell celda consultada
     * @return true si es puerta abierta o escalera con destino
     */
    private boolean esSalidaAbierta(Cell cell) {
        if (cell == null || !cell.hasDestinoAcceso()) {
            return false;
        }
        return cell.getTipo() == CellType.DOOR || cell.isStairs();
    }

    /**
     * Comprueba si una celda puede formar parte de la ruta orientativa al objetivo.
     *
     * @param cell celda consultada
     * @return true si es un acceso conocido hacia otra sala
     */
    private boolean esAccesoRutaObjetivo(Cell cell) {
        if (cell == null || !cell.hasDestinoAcceso()) {
            return false;
        }
        return cell.getTipo() == CellType.DOOR || cell.getTipo() == CellType.DOOR_LOCKED || cell.isStairs();
    }

    /**
     * Busca la mejor salida global desde una sala hacia la sala final.
     *
     * @param room sala actual
     * @return ruta elegida, o null si no hay ruta viable
     * @throws InvalidMoveException si alguna celda configurada no puede leerse
     */
    private RutaObjetivo buscarMejorRutaObjetivoGlobal(Room room) throws InvalidMoveException {
        if (room == null) {
            return null;
        }
        RutaObjetivo mejor = null;
        for (int fila = 0; fila < room.getFilas(); fila++) {
            for (int col = 0; col < room.getCols(); col++) {
                Cell acceso = room.getCell(fila, col);
                if (!esAccesoRutaObjetivo(acceso)) {
                    continue;
                }
                Room destino = acceso.getSalaDestino();
                int distanciaSalas = getDistanciaSalasHastaObjetivo(destino);
                if (distanciaSalas < 0) {
                    continue;
                }
                for (int i = 0; i < DIRECCIONES.length; i++) {
                    int filaUso = fila + DIRECCIONES[i][0];
                    int colUso = col + DIRECCIONES[i][1];
                    if (!room.isEnRango(filaUso, colUso)
                        || !acceso.isUsableFrom(filaUso, colUso, fila, col)) {
                        continue;
                    }
                    ListaSimplementeEnlazada<Cell> camino =
                        getCaminoHastaCelda(room, filaUso, colUso);
                    if (camino.isEmpty()) {
                        continue;
                    }
                    ListaSimplementeEnlazada<Cell> caminoVisual = copiarCamino(camino);
                    caminoVisual.addEnd(acceso);
                    RutaObjetivo candidata = new RutaObjetivo(destino, fila, col,
                        filaUso, colUso, camino.getSize() - 1, distanciaSalas, caminoVisual);
                    if (esMejorRuta(candidata, mejor)) {
                        mejor = candidata;
                    }
                }
            }
        }
        return mejor;
    }

    /**
     * Decide si una candidata mejora la ruta actual.
     *
     * @param candidata ruta candidata
     * @param actual ruta actualmente elegida
     * @return true si candidata debe sustituir a actual
     */
    private boolean esMejorRuta(RutaObjetivo candidata, RutaObjetivo actual) {
        if (candidata == null) {
            return false;
        }
        if (actual == null) {
            return true;
        }
        int costeCandidata = candidata.getCosteTotal();
        int costeActual = actual.getCosteTotal();
        if (costeCandidata != costeActual) {
            return costeCandidata < costeActual;
        }
        if (candidata.distanciaSalas != actual.distanciaSalas) {
            return candidata.distanciaSalas < actual.distanciaSalas;
        }
        if (candidata.distanciaCeldas != actual.distanciaCeldas) {
            return candidata.distanciaCeldas < actual.distanciaCeldas;
        }
        String idCandidata = candidata.destino == null ? "" : candidata.destino.getId();
        String idActual = actual.destino == null ? "" : actual.destino.getId();
        int comparacionId = idCandidata.compareTo(idActual);
        if (comparacionId != 0) {
            return comparacionId < 0;
        }
        if (candidata.filaAcceso != actual.filaAcceso) {
            return candidata.filaAcceso < actual.filaAcceso;
        }
        if (candidata.colAcceso != actual.colAcceso) {
            return candidata.colAcceso < actual.colAcceso;
        }
        if (candidata.filaUso != actual.filaUso) {
            return candidata.filaUso < actual.filaUso;
        }
        return candidata.colUso < actual.colUso;
    }

    /**
     * Calcula la distancia de salas desde una sala hasta la sala final.
     *
     * @param origen sala de origen
     * @return numero de conexiones hasta la sala objetivo, o -1 si no hay ruta
     */
    private int getDistanciaSalasHastaObjetivo(Room origen) {
        if (origen == null || dungeon == null) {
            return -1;
        }
        Room finalRoom = dungeon.getRoomById(getIdSalaObjetivo());
        if (finalRoom == null) {
            return -1;
        }
        return getDistanciaSalasPorRutaObjetivo(origen, finalRoom);
    }

    /**
     * Devuelve la sala objetivo configurada, con fallback al nucleo final.
     *
     * @return id de sala objetivo
     */
    private String getIdSalaObjetivo() {
        if (dungeon != null && dungeon.getIdSalaObjetivo() != null && !dungeon.getIdSalaObjetivo().isEmpty()) {
            return dungeon.getIdSalaObjetivo();
        }
        return SALA_FINAL_ID;
    }

    /**
     * Calcula distancia entre salas usando grafo activo y accesos conocidos de celdas.
     *
     * <p>La ruta visual debe orientar al jugador hacia el objetivo final aunque
     * una puerta de progreso siga cerrada por un puzzle. Por eso se consideran
     * las conexiones activas del grafo y tambien los destinos configurados en
     * puertas cerradas conocidas.</p>
     *
     * @param origen sala de origen
     * @param destino sala final
     * @return numero de conexiones, o -1 si no hay ruta conocida
     */
    private int getDistanciaSalasPorRutaObjetivo(Room origen, Room destino) {
        if (origen == null || destino == null || dungeon == null) {
            return -1;
        }
        int capacidad = dungeon.getGrafo().getNodos().getSize();
        Room[] cola = new Room[capacidad];
        int[] distancias = new int[capacidad];
        Room[] visitadas = new Room[capacidad];
        int visitadasSize = 0;
        int inicio = 0;
        int fin = 0;

        cola[fin] = origen;
        distancias[fin] = 0;
        fin++;
        visitadas[visitadasSize] = origen;
        visitadasSize++;

        while (inicio < fin) {
            Room actual = cola[inicio];
            int distanciaActual = distancias[inicio];
            inicio++;

            if (mismaSala(actual, destino)) {
                return distanciaActual;
            }

            ListaSimplementeEnlazada<Room> vecinos = getSalasRutaObjetivo(actual);
            for (int i = 0; i < vecinos.getSize(); i++) {
                Room vecino = vecinos.get(i);
                if (vecino == null || contieneSala(visitadas, visitadasSize, vecino) || fin >= capacidad) {
                    continue;
                }
                cola[fin] = vecino;
                distancias[fin] = distanciaActual + 1;
                fin++;
                visitadas[visitadasSize] = vecino;
                visitadasSize++;
            }
        }
        return -1;
    }

    /**
     * Devuelve vecinas de una sala para la ruta orientativa al objetivo.
     *
     * @param room sala consultada
     * @return salas vecinas activas o configuradas como accesos conocidos
     */
    private ListaSimplementeEnlazada<Room> getSalasRutaObjetivo(Room room) {
        ListaSimplementeEnlazada<Room> vecinos = new ListaSimplementeEnlazada<>();
        if (room == null || dungeon == null) {
            return vecinos;
        }

        ListaSimplementeEnlazada<Room> adyacentes = dungeon.getSalasAdyacentes(room);
        for (int i = 0; i < adyacentes.getSize(); i++) {
            addSalaSiNoExiste(vecinos, adyacentes.get(i));
        }

        for (int fila = 0; fila < room.getFilas(); fila++) {
            for (int col = 0; col < room.getCols(); col++) {
                try {
                    Cell acceso = room.getCell(fila, col);
                    if (esAccesoRutaObjetivo(acceso)) {
                        addSalaSiNoExiste(vecinos, acceso.getSalaDestino());
                    }
                } catch (InvalidMoveException e) {
                    // Las coordenadas vienen del rango de la sala; si falla se ignora esa celda.
                }
            }
        }
        return vecinos;
    }

    /**
     * Anade una sala a una lista si no aparece ya por id.
     *
     * @param salas lista destino
     * @param sala sala candidata
     */
    private void addSalaSiNoExiste(ListaSimplementeEnlazada<Room> salas, Room sala) {
        if (sala == null || contieneSala(salas, sala)) {
            return;
        }
        salas.addEnd(sala);
    }

    /**
     * Comprueba si una lista contiene una sala por id.
     *
     * @param salas lista consultada
     * @param sala sala buscada
     * @return true si existe una sala equivalente
     */
    private boolean contieneSala(ListaSimplementeEnlazada<Room> salas, Room sala) {
        if (salas == null) {
            return false;
        }
        for (int i = 0; i < salas.getSize(); i++) {
            if (mismaSala(salas.get(i), sala)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comprueba si un array parcial contiene una sala por id.
     *
     * @param salas array consultado
     * @param size posiciones validas del array
     * @param sala sala buscada
     * @return true si existe una sala equivalente
     */
    private boolean contieneSala(Room[] salas, int size, Room sala) {
        for (int i = 0; i < size; i++) {
            if (mismaSala(salas[i], sala)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compara dos salas por identificador.
     *
     * @param a primera sala
     * @param b segunda sala
     * @return true si representan la misma sala
     */
    private boolean mismaSala(Room a, Room b) {
        if (a == null || b == null || a.getId() == null) {
            return false;
        }
        return a.getId().equals(b.getId());
    }

    /**
     * Calcula el camino BFS hasta una celda transitable.
     *
     * @param room sala actual
     * @param filaDestino fila destino
     * @param colDestino columna destino
     * @return camino encontrado, o lista vacia si no hay ruta
     */
    private ListaSimplementeEnlazada<Cell> getCaminoHastaCelda(Room room, int filaDestino, int colDestino) {
        ListaSimplementeEnlazada<Cell> camino = new ListaSimplementeEnlazada<>();
        if (room == null || !room.isEnRango(player.getFilaActual(), player.getColActual())
            || !room.isEnRango(filaDestino, colDestino)) {
            return camino;
        }
        if (player.getFilaActual() == filaDestino && player.getColActual() == colDestino) {
            agregarCeldaSiExiste(room, player.getFilaActual(), player.getColActual(), camino);
            return camino;
        }
        Cell destino = getCellSegura(room, filaDestino, colDestino);
        if (!esTransitableRutaVisual(destino)) {
            return camino;
        }

        boolean[][] visitado = new boolean[room.getFilas()][room.getCols()];
        int[][] padreFila = crearMatrizPadres(room);
        int[][] padreCol = crearMatrizPadres(room);
        int capacidad = room.getFilas() * room.getCols();
        int[] colaFila = new int[capacidad];
        int[] colaCol = new int[capacidad];
        int inicio = 0;
        int fin = 0;

        colaFila[fin] = player.getFilaActual();
        colaCol[fin] = player.getColActual();
        fin++;
        visitado[player.getFilaActual()][player.getColActual()] = true;

        boolean encontrado = false;
        while (inicio < fin && !encontrado) {
            int filaActual = colaFila[inicio];
            int colActual = colaCol[inicio];
            inicio++;

            for (int i = 0; i < DIRECCIONES.length; i++) {
                int nuevaFila = filaActual + DIRECCIONES[i][0];
                int nuevaCol = colActual + DIRECCIONES[i][1];
                if (!room.isEnRango(nuevaFila, nuevaCol) || visitado[nuevaFila][nuevaCol]) {
                    continue;
                }
                Cell celda = getCellSegura(room, nuevaFila, nuevaCol);
                if (!esTransitableRutaVisual(celda)) {
                    continue;
                }
                visitado[nuevaFila][nuevaCol] = true;
                padreFila[nuevaFila][nuevaCol] = filaActual;
                padreCol[nuevaFila][nuevaCol] = colActual;
                colaFila[fin] = nuevaFila;
                colaCol[fin] = nuevaCol;
                fin++;
                if (nuevaFila == filaDestino && nuevaCol == colDestino) {
                    encontrado = true;
                    break;
                }
            }
        }

        if (encontrado) {
            reconstruirCaminoRutaVisual(room, filaDestino, colDestino, padreFila, padreCol, camino);
        }
        return camino;
    }

    /**
     * Crea matriz de padres inicializada.
     *
     * @param room sala usada para dimensiones
     * @return matriz con -1
     */
    private int[][] crearMatrizPadres(Room room) {
        int[][] matriz = new int[room.getFilas()][room.getCols()];
        for (int fila = 0; fila < room.getFilas(); fila++) {
            for (int col = 0; col < room.getCols(); col++) {
                matriz[fila][col] = -1;
            }
        }
        return matriz;
    }

    /**
     * Reconstruye el camino visual desde destino hasta el jugador.
     *
     * @param room sala consultada
     * @param filaDestino fila destino
     * @param colDestino columna destino
     * @param padreFila padres de fila
     * @param padreCol padres de columna
     * @param camino lista donde se inserta el resultado
     */
    private void reconstruirCaminoRutaVisual(Room room, int filaDestino, int colDestino,
                                             int[][] padreFila, int[][] padreCol,
                                             ListaSimplementeEnlazada<Cell> camino) {
        int fila = filaDestino;
        int col = colDestino;
        while (fila != -1 && col != -1) {
            agregarCeldaSiExiste(room, fila, col, camino);
            if (fila == player.getFilaActual() && col == player.getColActual()) {
                break;
            }
            int siguienteFila = padreFila[fila][col];
            int siguienteCol = padreCol[fila][col];
            fila = siguienteFila;
            col = siguienteCol;
        }
    }

    /**
     * Inserta una celda al principio si la coordenada existe.
     *
     * @param room sala consultada
     * @param fila fila de celda
     * @param col columna de celda
     * @param camino camino destino
     */
    private void agregarCeldaSiExiste(Room room, int fila, int col, ListaSimplementeEnlazada<Cell> camino) {
        Cell celda = getCellSegura(room, fila, col);
        if (celda != null) {
            camino.addStart(celda);
        }
    }

    /**
     * Comprueba si una celda puede formar parte de la ruta visual.
     *
     * @param cell celda consultada
     * @return true si la ruta puede atravesarla
     */
    private boolean esTransitableRutaVisual(Cell cell) {
        if (cell == null) {
            return false;
        }
        if (cell.getTipo() == CellType.WALL || cell.isAccessCell() || cell.getTipo() == CellType.LEVER) {
            return false;
        }
        return cell.getContainer() == null;
    }

    /**
     * Obtiene una celda evitando propagar excepciones de coordenada.
     *
     * @param room sala consultada
     * @param fila fila solicitada
     * @param col columna solicitada
     * @return celda encontrada, o null si no existe
     */
    private Cell getCellSegura(Room room, int fila, int col) {
        try {
            return room.getCell(fila, col);
        } catch (InvalidMoveException e) {
            return null;
        }
    }

    /**
     * Copia una lista de celdas conservando el orden.
     *
     * @param original camino original
     * @return copia del camino
     */
    private ListaSimplementeEnlazada<Cell> copiarCamino(ListaSimplementeEnlazada<Cell> original) {
        ListaSimplementeEnlazada<Cell> copia = new ListaSimplementeEnlazada<>();
        if (original == null) {
            return copia;
        }
        for (int i = 0; i < original.getSize(); i++) {
            copia.addEnd(original.get(i));
        }
        return copia;
    }

    /**
     * Calcula la distancia minima desde el jugador hasta una celda de uso de un acceso.
     *
     * @param room sala actual
     * @param acceso acceso consultado
     * @param filaAcceso fila del acceso
     * @param colAcceso columna del acceso
     * @return distancia minima, o -1 si no hay ruta
     */
    private int getDistanciaAAcceso(Room room, Cell acceso, int filaAcceso, int colAcceso) {
        int mejorDistancia = -1;
        for (int i = 0; i < DIRECCIONES.length; i++) {
            int filaUso = filaAcceso + DIRECCIONES[i][0];
            int colUso = colAcceso + DIRECCIONES[i][1];
            if (!room.isEnRango(filaUso, colUso)
                || !acceso.isUsableFrom(filaUso, colUso, filaAcceso, colAcceso)) {
                continue;
            }
            int distancia = getDistanciaHastaCelda(room, filaUso, colUso);
            if (distancia >= 0 && (mejorDistancia < 0 || distancia < mejorDistancia)) {
                mejorDistancia = distancia;
            }
        }
        return mejorDistancia;
    }

    /**
     * Calcula la distancia por BFS desde el jugador hasta una celda transitable.
     *
     * @param room sala actual
     * @param filaDestino fila destino
     * @param colDestino columna destino
     * @return distancia en pasos, o -1 si no hay camino
     */
    private int getDistanciaHastaCelda(Room room, int filaDestino, int colDestino) {
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(
            room, player.getFilaActual(), player.getColActual(), filaDestino, colDestino);
        if (camino.isEmpty()) {
            return -1;
        }
        return camino.getSize() - 1;
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

    /**
     * Datos internos de una ruta candidata hacia el objetivo global.
     */
    private static final class RutaObjetivo {

        /** Sala destino inmediata. */
        private final Room destino;

        /** Fila del acceso. */
        private final int filaAcceso;

        /** Columna del acceso. */
        private final int colAcceso;

        /** Fila de la celda desde la que se usa el acceso. */
        private final int filaUso;

        /** Columna de la celda desde la que se usa el acceso. */
        private final int colUso;

        /** Distancia de celdas hasta la celda de uso. */
        private final int distanciaCeldas;

        /** Distancia de salas desde el destino inmediato hasta S5-D. */
        private final int distanciaSalas;

        /** Camino de celdas hasta la celda de uso. */
        private final ListaSimplementeEnlazada<Cell> caminoCeldas;

        /**
         * Crea una ruta candidata.
         *
         * @param destino sala destino inmediata
         * @param filaAcceso fila del acceso
         * @param colAcceso columna del acceso
         * @param filaUso fila usable
         * @param colUso columna usable
         * @param distanciaCeldas distancia de celdas
         * @param distanciaSalas distancia de salas restante
         * @param caminoCeldas camino en la sala actual
         */
        private RutaObjetivo(Room destino, int filaAcceso, int colAcceso,
                             int filaUso, int colUso, int distanciaCeldas, int distanciaSalas,
                             ListaSimplementeEnlazada<Cell> caminoCeldas) {
            this.destino = destino;
            this.filaAcceso = filaAcceso;
            this.colAcceso = colAcceso;
            this.filaUso = filaUso;
            this.colUso = colUso;
            this.distanciaCeldas = distanciaCeldas;
            this.distanciaSalas = distanciaSalas;
            this.caminoCeldas = caminoCeldas;
        }

        /**
         * Devuelve el coste total usado para ordenar rutas.
         *
         * @return coste total de celdas y salas restantes
         */
        private int getCosteTotal() {
            return distanciaCeldas + distanciaSalas;
        }
    }
}
