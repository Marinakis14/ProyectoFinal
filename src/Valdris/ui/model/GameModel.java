package Valdris.ui.model;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.generation.DungeonGenerator;
import Valdris.logic.turn.TurnManager;
import Valdris.model.enums.CharacterType;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import Valdris.model.units.Player;
import Valdris.persistence.LectorJSON;
import Valdris.persistence.LoadedGame;

/**
 * Modelo observable usado por la capa JavaFX.
 *
 * <p>Conserva las referencias vivas de partida que la interfaz puede consultar:
 * dungeon, jugador y gestor de turnos. La vista no debe modificar estos objetos
 * directamente; las acciones jugables se incorporaran desde el controlador.</p>
 */
public class GameModel {

    /** Ruta fija del unico slot de guardado actual. */
    public static final String SAVE_PATH = "partida_valdris.json";

    /** Dungeon activo de la partida. */
    private final Dungeon dungeon;

    /** Jugador activo de la partida. */
    private final Player player;

    /** Gestor de turnos de la partida. */
    private final TurnManager turnManager;

    /** Observadores registrados para refrescar la interfaz. */
    private final ListaSimplementeEnlazada<GameModelListener> listeners;

    /** Ultimo mensaje visible generado para la interfaz. */
    private String ultimoMensaje;

    /** ID de la ultima sala donde se autoguardo para evitar repeticiones. */
    private String ultimoCheckpointGuardado;

    /**
     * Crea una partida nueva con el personaje indicado.
     *
     * @param tipo personaje elegido por el jugador
     * @throws GameStateException si la partida inicial no puede construirse
     */
    public GameModel(CharacterType tipo) throws GameStateException {
        if (tipo == null) {
            throw new GameStateException("Debe elegirse un personaje para iniciar la partida.");
        }

        this.dungeon = DungeonGenerator.generarMundo();
        this.player = new Player(tipo);
        this.turnManager = new TurnManager(dungeon, player);
        this.listeners = new ListaSimplementeEnlazada<>();
        this.ultimoMensaje = null;
        this.ultimoCheckpointGuardado = null;

        colocarJugadorEnSalaInicial();
        this.ultimoMensaje = crearMensajeInicio();
        intentarAutoguardadoCheckpoint();
    }

    /**
     * Crea un modelo a partir de una partida cargada.
     *
     * @param loadedGame partida reconstruida desde JSON
     * @throws GameStateException si la partida cargada esta incompleta
     */
    public GameModel(LoadedGame loadedGame) throws GameStateException {
        if (loadedGame == null || loadedGame.getDungeon() == null
            || loadedGame.getPlayer() == null || loadedGame.getTurnManager() == null) {
            throw new GameStateException("La partida cargada no contiene estado completo.");
        }
        this.dungeon = loadedGame.getDungeon();
        this.player = loadedGame.getPlayer();
        this.turnManager = loadedGame.getTurnManager();
        this.listeners = new ListaSimplementeEnlazada<>();
        this.ultimoMensaje = "Partida cargada.";
        this.ultimoCheckpointGuardado = idSalaActual();
    }

    /**
     * Registra un observador del modelo si no estaba ya registrado.
     *
     * @param listener observador que se quiere registrar
     */
    public void addListener(GameModelListener listener) {
        if (listener == null || contieneListener(listener)) {
            return;
        }
        listeners.addEnd(listener);
    }

    /**
     * Notifica a todos los observadores registrados.
     */
    public void notificarCambio() {
        for (int i = 0; i < listeners.getSize(); i++) {
            GameModelListener listener = listeners.get(i);
            if (listener != null) {
                listener.onEstadoCambiado(this);
            }
        }
    }

    /**
     * Registra un mensaje visible y notifica a los observadores.
     *
     * @param mensaje mensaje que debe ver la interfaz
     */
    public void notificarMensaje(String mensaje) {
        this.ultimoMensaje = mensaje;
        if (mensaje == null) {
            intentarAutoguardadoCheckpoint();
        }
        notificarCambio();
    }

    /**
     * Intenta guardar la partida si la sala actual es un checkpoint.
     */
    public void intentarAutoguardadoCheckpoint() {
        String salaId = idSalaActual();
        if (salaId == null || !esCheckpointGuardado(salaId)) {
            ultimoCheckpointGuardado = null;
            return;
        }
        if (salaId.equals(ultimoCheckpointGuardado)) {
            return;
        }
        try {
            LectorJSON.guardarPartida(dungeon, player, turnManager, SAVE_PATH);
            ultimoCheckpointGuardado = salaId;
            ultimoMensaje = "Progreso guardado.";
        } catch (GameStateException e) {
            ultimoMensaje = "No se pudo guardar el progreso: " + e.getMessage();
        }
    }

    /**
     * Indica si una sala es punto de autoguardado.
     *
     * @param salaId identificador de sala
     * @return true si la sala debe activar autoguardado
     */
    public boolean esCheckpointGuardado(String salaId) {
        if (salaId == null) {
            return false;
        }
        return "S1-A".equals(salaId)
            || "S1-C".equals(salaId)
            || "S2-C".equals(salaId)
            || "S2-D".equals(salaId)
            || "S3-E".equals(salaId)
            || "S4-C".equals(salaId)
            || "S4-D".equals(salaId)
            || "S5-B".equals(salaId)
            || "PASILLO_1_2".equals(salaId)
            || "PASILLO_2_3".equals(salaId)
            || "PASILLO_3_4".equals(salaId)
            || "PASILLO_4_5".equals(salaId)
            || "PASILLO_FINAL".equals(salaId);
    }

    /**
     * Devuelve el dungeon activo.
     *
     * @return dungeon de la partida
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * Devuelve el jugador activo.
     *
     * @return jugador de la partida
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Devuelve el gestor de turnos.
     *
     * @return turn manager de la partida
     */
    public TurnManager getTurnManager() {
        return turnManager;
    }

    /**
     * Devuelve el ultimo mensaje visible.
     *
     * @return ultimo mensaje, o null si no existe
     */
    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    /**
     * Devuelve el numero de listeners registrados.
     *
     * @return cantidad de listeners
     */
    public int getNumeroListeners() {
        return listeners.getSize();
    }

    /**
     * Coloca el jugador en la sala inicial configurada por el generador.
     *
     * @throws GameStateException si no hay sala inicial o la entrada no es valida
     */
    private void colocarJugadorEnSalaInicial() throws GameStateException {
        try {
            Room inicial = dungeon.getRoomActual();
            if (inicial == null) {
                throw new GameStateException("El dungeon no tiene sala inicial.");
            }
            turnManager.changeRoom(inicial);
        } catch (InvalidMoveException e) {
            throw new GameStateException("No se pudo colocar al jugador en la sala inicial: " + e.getMessage());
        }
    }

    /**
     * Comprueba si un listener ya esta registrado por referencia.
     *
     * @param listener listener buscado
     * @return true si ya existe en la lista
     */
    private boolean contieneListener(GameModelListener listener) {
        for (int i = 0; i < listeners.getSize(); i++) {
            if (listeners.get(i) == listener) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crea el mensaje inicial de partida.
     *
     * @return texto visible de inicio
     */
    private String crearMensajeInicio() {
        Room room = dungeon.getRoomActual();
        String sala = room == null ? "sala inicial" : room.getId() + " - " + room.getNombre();
        return "Partida creada con " + player.getTipo() + " en " + sala + ".";
    }

    /**
     * Devuelve el ID de la sala actual.
     *
     * @return id de sala, o null si no hay sala
     */
    private String idSalaActual() {
        Room room = dungeon.getRoomActual();
        return room == null ? null : room.getId();
    }
}
