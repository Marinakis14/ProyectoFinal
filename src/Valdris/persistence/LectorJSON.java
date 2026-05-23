package Valdris.persistence;

import MisEstructurasDeDatos.Grafos.NodoGrafo;
import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.generation.DungeonGenerator;
import Valdris.logic.generation.ItemGenerator;
import Valdris.logic.turn.TurnManager;
import Valdris.model.effects.Effect;
import Valdris.model.enums.CellType;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.EnemyType;
import Valdris.model.enums.GameResult;
import Valdris.model.enums.LogEventType;
import Valdris.model.enums.MiniBossType;
import Valdris.model.enums.Phase;
import Valdris.model.items.Accessory;
import Valdris.model.items.Armor;
import Valdris.model.items.Item;
import Valdris.model.items.Weapon;
import Valdris.model.log.GameLogEntry;
import Valdris.model.map.Cell;
import Valdris.model.map.Chest;
import Valdris.model.map.Container;
import Valdris.model.map.Dungeon;
import Valdris.model.map.HiddenPassage;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.MalacharAlly;
import Valdris.model.units.MiniBossEnemy;
import Valdris.model.units.ParasitoEnemy;
import Valdris.model.units.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Lee y escribe ficheros JSON de partida y de resumen final.
 *
 * <p>La persistencia usa DTOs planos para evitar ciclos entre dungeon, salas,
 * celdas y accesos. Al cargar, primero se reconstruye el mundo base con
 * {@link DungeonGenerator} y después se aplica el estado dinámico guardado.</p>
 */
public final class LectorJSON {

    // -- Constantes -----------------------------------------------------------

    /** Gson configurado con salida legible. */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // -- Constructor privado -------------------------------------------------

    /**
     * Evita instanciar la clase porque todos sus métodos son estáticos.
     */
    private LectorJSON() {
    }

    // -- Guardado y carga -----------------------------------------------------

    /**
     * Guarda una partida en un fichero JSON.
     *
     * @param dungeon dungeon activo
     * @param player jugador activo
     * @param tm gestor de turnos activo
     * @param rutaArchivo ruta destino
     * @throws GameStateException si no se puede escribir el fichero
     */
    public static void guardarPartida(Dungeon dungeon, Player player, TurnManager tm, String rutaArchivo)
        throws GameStateException {

        GameState state = extraerGameState(dungeon, player, tm);
        try (FileWriter writer = new FileWriter(rutaArchivo, StandardCharsets.UTF_8)) {
            GSON.toJson(state, writer);
        } catch (IOException e) {
            throw new GameStateException("No se pudo guardar la partida: " + e.getMessage());
        }
    }

    /**
     * Carga una partida desde un fichero JSON.
     *
     * @param rutaArchivo ruta origen
     * @return partida reconstruida
     * @throws GameStateException si no se puede leer o reconstruir
     */
    public static LoadedGame cargarPartida(String rutaArchivo) throws GameStateException {
        try (FileReader reader = new FileReader(rutaArchivo, StandardCharsets.UTF_8)) {
            GameState state = GSON.fromJson(reader, GameState.class);
            return reconstruirDesdeGameState(state);
        } catch (IOException e) {
            throw new GameStateException("No se pudo cargar la partida: " + e.getMessage());
        }
    }

    /**
     * Construye un GameState plano desde los objetos vivos de la partida.
     *
     * @param dungeon dungeon activo
     * @param player jugador activo
     * @param tm gestor de turnos activo
     * @return snapshot serializable
     * @throws GameStateException si falta estado obligatorio
     */
    public static GameState extraerGameState(Dungeon dungeon, Player player, TurnManager tm)
        throws GameStateException {

        if (dungeon == null || player == null || tm == null || dungeon.getRoomActual() == null) {
            throw new GameStateException("No se puede extraer GameState sin dungeon, jugador y turn manager.");
        }

        GameState state = new GameState();
        state.idRoomActual = dungeon.getRoomActual().getId();
        state.tipoPersonaje = player.getTipo().name();
        state.faseActual = tm.getFaseActual().name();
        state.turnoGlobal = tm.getTurnoGlobal();
        state.lastDialogue = tm.getLastDialogue();
        state.gameResult = tm.getGameResult().name();
        state.endingText = tm.getEndingText();
        state.finalQuote = tm.getFinalQuote();
        state.defeatReason = tm.getDefeatReason();
        state.finalCombatStarted = tm.isFinalCombatStarted();
        state.hpJugador = player.getHp();
        state.filaJugador = player.getFilaActual();
        state.colJugador = player.getColActual();
        state.haMovido = player.isHaMovido();
        state.haRecogido = player.isHaRecogido();
        state.haUsadoItem = player.isHaUsadoItem();
        state.haAtacado = player.isHaAtacado();
        state.bonusAtaqueTemporal = player.getBonusAtaqueTemporal();
        state.itemsInventario = idsItems(player.getInventario());
        state.itemsNarrativos = idsItems(player.getItemsNarrativos());
        state.armaEquipada = idItem(player.getArmaEquipada());
        state.armaduraEquipada = idItem(player.getArmaduraEquipada());
        state.escudoEquipado = idItem(player.getEscudoEquipado());
        state.accesorioEquipado = idItem(player.getAccesorioEquipado());
        state.efectosJugador = efectos(player.getEfectosActivos());
        state.salas = extraerSalas(dungeon);
        state.enemigos = extraerEnemigos(dungeon);
        state.pasadizosActivos = extraerPasadizosActivos(dungeon);
        state.logEventos = extraerLog(tm);
        state.malachar = extraerMalachar(dungeon);
        return state;
    }

    /**
     * Reconstruye los objetos de partida desde un GameState.
     *
     * @param state estado plano
     * @return partida reconstruida
     * @throws GameStateException si el estado no es válido
     */
    public static LoadedGame reconstruirDesdeGameState(GameState state) throws GameStateException {
        if (state == null || state.tipoPersonaje == null || state.idRoomActual == null) {
            throw new GameStateException("GameState incompleto.");
        }

        Dungeon dungeon = DungeonGenerator.generarMundo();
        Player player = new Player(parseCharacterType(state.tipoPersonaje));
        TurnManager tm = new TurnManager(dungeon, player);

        restaurarSalas(dungeon, state.salas);
        restaurarPasadizos(dungeon, state.pasadizosActivos);
        restaurarJugador(player, state);
        limpiarUnidadesDeSalas(dungeon);
        restaurarMalachar(dungeon, state.malachar);
        restaurarEnemigos(dungeon, state.enemigos);
        restaurarSalaActual(dungeon, player, state);
        restaurarTurnManager(tm, state);
        return new LoadedGame(dungeon, player, tm);
    }

    // -- Resumen --------------------------------------------------------------

    /**
     * Extrae un resumen exportable de la partida.
     *
     * @param dungeon dungeon activo
     * @param player jugador activo
     * @param tm gestor de turnos activo
     * @return resumen plano
     * @throws GameStateException si faltan datos obligatorios
     */
    public static GameSummary extraerGameSummary(Dungeon dungeon, Player player, TurnManager tm)
        throws GameStateException {

        if (dungeon == null || player == null || tm == null || dungeon.getRoomActual() == null) {
            throw new GameStateException("No se puede extraer GameSummary sin estado completo.");
        }
        GameSummary summary = new GameSummary();
        summary.tipoPersonaje = player.getTipo().name();
        summary.idRoomActual = dungeon.getRoomActual().getId();
        summary.hpJugador = player.getHp();
        summary.turnoGlobal = tm.getTurnoGlobal();
        summary.gameResult = tm.getGameResult().name();
        summary.endingText = tm.getEndingText();
        summary.finalQuote = tm.getFinalQuote();
        summary.defeatReason = tm.getDefeatReason();
        summary.itemsInventario = idsItems(player.getInventario());
        summary.itemsNarrativos = idsItems(player.getItemsNarrativos());
        summary.salasExploradas = extraerSalasExploradas(dungeon);
        summary.logEventos = extraerLog(tm);
        return summary;
    }

    /**
     * Exporta un resumen ya construido.
     *
     * @param summary resumen
     * @param rutaArchivo ruta destino
     * @throws GameStateException si no se puede escribir
     */
    public static void exportarResumen(GameSummary summary, String rutaArchivo) throws GameStateException {
        try (FileWriter writer = new FileWriter(rutaArchivo, StandardCharsets.UTF_8)) {
            GSON.toJson(summary, writer);
        } catch (IOException e) {
            throw new GameStateException("No se pudo exportar el resumen: " + e.getMessage());
        }
    }

    /**
     * Extrae y exporta un resumen de la partida.
     *
     * @param dungeon dungeon activo
     * @param player jugador activo
     * @param tm gestor de turnos
     * @param rutaArchivo ruta destino
     * @throws GameStateException si no se puede exportar
     */
    public static void exportarResumen(Dungeon dungeon, Player player, TurnManager tm, String rutaArchivo)
        throws GameStateException {

        exportarResumen(extraerGameSummary(dungeon, player, tm), rutaArchivo);
    }

    // -- Extracción de salas --------------------------------------------------

    /**
     * Extrae todos los estados de sala.
     */
    private static GameState.RoomStateDTO[] extraerSalas(Dungeon dungeon) {
        ListaSimplementeEnlazada<NodoGrafo<Room>> nodos = dungeon.getGrafo().getNodos();
        GameState.RoomStateDTO[] salas = new GameState.RoomStateDTO[nodos.getSize()];
        for (int i = 0; i < nodos.getSize(); i++) {
            salas[i] = extraerSala(nodos.get(i).getDatos());
        }
        return salas;
    }

    /**
     * Extrae el estado de una sala.
     */
    private static GameState.RoomStateDTO extraerSala(Room room) {
        GameState.RoomStateDTO dto = new GameState.RoomStateDTO();
        dto.idSala = room.getId();
        dto.explorada = room.isExplorada();
        dto.filaJugador = room.getFilaJugador();
        dto.colJugador = room.getColJugador();
        dto.hasRoomTimer = room.hasRoomTimer();
        dto.turnosRestantes = room.getTurnosRestantes();
        dto.dialogoKaelMostrado = room.wasDialogueShown(CharacterType.KAEL);
        dto.dialogoSyraMostrado = room.wasDialogueShown(CharacterType.SYRA);
        dto.dialogoDorathMostrado = room.wasDialogueShown(CharacterType.DORATH);
        dto.puzzleResolved = room.isPuzzleResolved();
        dto.puzzleSuccessTarget = room.getPuzzleSuccessTarget();
        dto.puzzleFailureDamage = room.getPuzzleFailureDamage();
        dto.correctSequence = room.getCorrectSequence();
        dto.activeSequence = room.getSecuenciaActivada();
        dto.celdas = extraerCeldas(room);
        return dto;
    }

    /**
     * Extrae celdas con estado dinámico.
     */
    private static GameState.CellStateDTO[] extraerCeldas(Room room) {
        int total = 0;
        for (int fila = 0; fila < room.getFilas(); fila++) {
            for (int col = 0; col < room.getCols(); col++) {
                try {
                    Cell cell = room.getCell(fila, col);
                    if (esCeldaDinamica(cell)) {
                        total++;
                    }
                } catch (InvalidMoveException e) {
                    // La iteración respeta límites de la sala.
                }
            }
        }

        GameState.CellStateDTO[] celdas = new GameState.CellStateDTO[total];
        int indice = 0;
        for (int fila = 0; fila < room.getFilas(); fila++) {
            for (int col = 0; col < room.getCols(); col++) {
                try {
                    Cell cell = room.getCell(fila, col);
                    if (esCeldaDinamica(cell)) {
                        celdas[indice] = extraerCelda(cell, fila, col);
                        indice++;
                    }
                } catch (InvalidMoveException e) {
                    // La iteración respeta límites de la sala.
                }
            }
        }
        return celdas;
    }

    /**
     * Comprueba si una celda necesita persistencia explícita.
     */
    private static boolean esCeldaDinamica(Cell cell) {
        return cell != null && (cell.getItem() != null || cell.getContainer() != null || cell.isAccessCell()
            || cell.getTipo() == CellType.DOOR_HIDDEN || cell.getTipo() == CellType.DOOR_LOCKED);
    }

    /**
     * Extrae una celda dinámica.
     */
    private static GameState.CellStateDTO extraerCelda(Cell cell, int fila, int col) {
        GameState.CellStateDTO dto = new GameState.CellStateDTO();
        dto.fila = fila;
        dto.col = col;
        dto.tipo = cell.getTipo().name();
        dto.descubierta = cell.isDescubierta();
        dto.itemId = idItem(cell.getItem());
        dto.container = extraerContainer(cell.getContainer());
        return dto;
    }

    /**
     * Extrae un contenedor.
     */
    private static GameState.ContainerStateDTO extraerContainer(Container container) {
        if (container == null) {
            return null;
        }
        GameState.ContainerStateDTO dto = new GameState.ContainerStateDTO();
        dto.id = container.getId();
        dto.nombre = container.getNombre();
        dto.abierto = container.isAbierto();
        dto.itemsRestantes = idsItems(container.getItems());
        return dto;
    }

    // -- Extracción de enemigos ----------------------------------------------

    /**
     * Extrae todos los enemigos del mundo.
     */
    private static GameState.EnemyStateDTO[] extraerEnemigos(Dungeon dungeon) {
        ListaSimplementeEnlazada<NodoGrafo<Room>> nodos = dungeon.getGrafo().getNodos();
        int total = 0;
        for (int i = 0; i < nodos.getSize(); i++) {
            Room room = nodos.get(i).getDatos();
            total += room.getEnemigos().getSize();
        }

        GameState.EnemyStateDTO[] enemigos = new GameState.EnemyStateDTO[total];
        int indice = 0;
        for (int i = 0; i < nodos.getSize(); i++) {
            Room room = nodos.get(i).getDatos();
            for (int j = 0; j < room.getEnemigos().getSize(); j++) {
                enemigos[indice] = extraerEnemigo(room.getEnemigos().get(j));
                indice++;
            }
        }
        return enemigos;
    }

    /**
     * Extrae el estado de un enemigo.
     */
    private static GameState.EnemyStateDTO extraerEnemigo(Enemy enemy) {
        GameState.EnemyStateDTO dto = new GameState.EnemyStateDTO();
        dto.idSala = enemy.getIdSala();
        dto.tipoEnemigo = enemy.getTipo().name();
        if (enemy instanceof MiniBossEnemy) {
            dto.miniBossType = ((MiniBossEnemy) enemy).getTipoMiniBoss().name();
        }
        dto.fila = enemy.getFilaActual();
        dto.col = enemy.getColActual();
        dto.hp = enemy.getHp();
        dto.vivo = enemy.isVivo();
        dto.dropItemId = idItem(enemy.getDropItem());
        dto.turnosSinActuar = enemy.getTurnosSinActuar();
        dto.esMiniJefe = enemy.isMiniJefe();
        if (enemy instanceof ParasitoEnemy) {
            ParasitoEnemy parasito = (ParasitoEnemy) enemy;
            dto.parasitoPhase = parasito.getPhase();
            dto.parasitoAoeCooldown = parasito.getAoeCooldown();
            dto.parasitoDevorarLuzUsado = parasito.isDevorarLuzUsado();
            dto.parasitoPhaseTransitionPending = parasito.isPhaseTransitionPending();
            dto.parasitoDevorarLuzPendiente = parasito.isDevorarLuzPendiente();
            dto.parasitoSkipNextActionByTransition = parasito.isSkipNextActionByTransition();
        }
        dto.efectos = efectos(enemy.getEfectosActivos());
        return dto;
    }

    /**
     * Extrae IDs de pasadizos activos.
     */
    private static String[] extraerPasadizosActivos(Dungeon dungeon) {
        ListaSimplementeEnlazada<String> activos = new ListaSimplementeEnlazada<>();
        ListaSimplementeEnlazada<HiddenPassage> passages = dungeon.getHiddenPassages();
        for (int i = 0; i < passages.getSize(); i++) {
            HiddenPassage passage = passages.get(i);
            if (passage != null && passage.isActive()) {
                activos.addEnd(passage.getId());
            }
        }
        return strings(activos);
    }

    /**
     * Extrae el log estructurado del turn manager.
     */
    private static GameState.GameLogEntryDTO[] extraerLog(TurnManager tm) {
        GameState.GameLogEntryDTO[] result = new GameState.GameLogEntryDTO[tm.getLog().getSize()];
        for (int i = 0; i < tm.getLog().getSize(); i++) {
            GameLogEntry entry = tm.getLog().get(i);
            GameState.GameLogEntryDTO dto = new GameState.GameLogEntryDTO();
            dto.turno = entry.getTurno();
            dto.tipo = entry.getTipo().name();
            dto.actor = entry.getActor();
            dto.salaId = entry.getSalaId();
            dto.mensaje = entry.getMensaje();
            dto.detalle = entry.getDetalle();
            result[i] = dto;
        }
        return result;
    }

    /**
     * Extrae el estado de Malachar si ya apareció en la sala final.
     */
    private static GameState.MalacharStateDTO extraerMalachar(Dungeon dungeon) {
        Room salaFinal = dungeon.getRoomById("S5-D");
        MalacharAlly malachar = salaFinal == null ? null : salaFinal.getAllyNpc();
        if (malachar == null) {
            return null;
        }
        GameState.MalacharStateDTO dto = new GameState.MalacharStateDTO();
        dto.fila = malachar.getFilaActual();
        dto.col = malachar.getColActual();
        dto.hp = malachar.getHp();
        dto.turnosRecuperacion = malachar.getTurnosRecuperacion();
        dto.efectos = efectos(malachar.getEfectosActivos());
        return dto;
    }

    // -- Restauración ---------------------------------------------------------

    /**
     * Restaura estados de sala.
     */
    private static void restaurarSalas(Dungeon dungeon, GameState.RoomStateDTO[] salas) {
        if (salas == null) {
            return;
        }
        for (int i = 0; i < salas.length; i++) {
            GameState.RoomStateDTO dto = salas[i];
            Room room = dto == null ? null : dungeon.getRoomById(dto.idSala);
            if (room != null) {
                restaurarSala(room, dto);
            }
        }
    }

    /**
     * Restaura una sala.
     */
    private static void restaurarSala(Room room, GameState.RoomStateDTO dto) {
        room.setExplorada(dto.explorada);
        room.setFilaJugador(dto.filaJugador);
        room.setColJugador(dto.colJugador);
        if (dto.hasRoomTimer) {
            room.setTurnosRestantes(dto.turnosRestantes);
        } else {
            room.setHasRoomTimer(false);
        }
        if (dto.dialogoKaelMostrado) {
            room.markDialogueShown(CharacterType.KAEL);
        }
        if (dto.dialogoSyraMostrado) {
            room.markDialogueShown(CharacterType.SYRA);
        }
        if (dto.dialogoDorathMostrado) {
            room.markDialogueShown(CharacterType.DORATH);
        }
        room.setPuzzleFailureDamage(dto.puzzleFailureDamage);
        room.setPuzzleSuccessTarget(dto.puzzleSuccessTarget);
        room.setCorrectSequence(dto.correctSequence);
        if (dto.activeSequence != null) {
            for (int i = 0; i < dto.activeSequence.length; i++) {
                room.registrarActivacion(dto.activeSequence[i]);
            }
        }
        room.setPuzzleResolved(dto.puzzleResolved);
        restaurarCeldas(room, dto.celdas);
    }

    /**
     * Restaura celdas dinámicas.
     */
    private static void restaurarCeldas(Room room, GameState.CellStateDTO[] celdas) {
        if (celdas == null) {
            return;
        }
        for (int i = 0; i < celdas.length; i++) {
            GameState.CellStateDTO dto = celdas[i];
            if (dto == null || !room.isEnRango(dto.fila, dto.col)) {
                continue;
            }
            try {
                Cell cell = room.getCell(dto.fila, dto.col);
                if (dto.tipo != null) {
                    cell.setTipo(CellType.valueOf(dto.tipo));
                }
                if (dto.descubierta && cell.getTipo() == CellType.DOOR_HIDDEN) {
                    cell.revelar();
                }
                cell.setItem(ItemGenerator.crearItem(dto.itemId));
                restaurarContainer(cell, dto.container);
            } catch (InvalidMoveException | IllegalArgumentException e) {
                // Se ignora una celda corrupta y se mantiene la versión base generada.
            }
        }
    }

    /**
     * Restaura un contenedor en una celda.
     */
    private static void restaurarContainer(Cell cell, GameState.ContainerStateDTO dto) {
        if (dto == null) {
            if (cell.getContainer() != null) {
                cell.removeContainer();
            }
            return;
        }
        Container container = cell.getContainer();
        if (container == null || !dto.id.equals(container.getId())) {
            container = new Chest(dto.id, dto.nombre);
            cell.setContainer(container);
        }
        container.getItems().clear();
        if (dto.itemsRestantes != null) {
            for (int i = 0; i < dto.itemsRestantes.length; i++) {
                container.addItem(ItemGenerator.crearItem(dto.itemsRestantes[i]));
            }
        }
        container.restaurarAbierto(dto.abierto);
    }

    /**
     * Activa pasadizos guardados.
     */
    private static void restaurarPasadizos(Dungeon dungeon, String[] activos) {
        if (activos == null) {
            return;
        }
        for (int i = 0; i < activos.length; i++) {
            dungeon.activateHiddenPassage(activos[i]);
        }
    }

    /**
     * Restaura jugador, inventario y equipo.
     */
    private static void restaurarJugador(Player player, GameState state) {
        player.setHp(state.hpJugador);
        player.setPosicion(state.filaJugador, state.colJugador);
        restaurarItems(player, state.itemsInventario);
        restaurarItems(player, state.itemsNarrativos);
        equipar(player, state.armaEquipada);
        equipar(player, state.armaduraEquipada);
        equipar(player, state.escudoEquipado);
        equipar(player, state.accesorioEquipado);
        player.consumirBonusAtaqueTemporal();
        player.addBonusAtaqueTemporal(state.bonusAtaqueTemporal);
        player.getEfectosActivos().clear();
        restaurarEfectos(player.getEfectosActivos(), state.efectosJugador);
        player.setHaMovido(state.haMovido);
        player.setHaRecogido(state.haRecogido);
        player.setHaUsadoItem(state.haUsadoItem);
        player.setHaAtacado(state.haAtacado);
    }

    /**
     * Restaura items en el jugador.
     */
    private static void restaurarItems(Player player, String[] ids) {
        if (ids == null) {
            return;
        }
        for (int i = 0; i < ids.length; i++) {
            player.addItem(ItemGenerator.crearItem(ids[i]));
        }
    }

    /**
     * Equipa un item por id.
     */
    private static void equipar(Player player, String itemId) {
        Item item = ItemGenerator.crearItem(itemId);
        if (item != null) {
            player.equip(item);
        }
    }

    /**
     * Limpia las unidades generadas antes de restaurar enemigos guardados.
     */
    private static void limpiarUnidadesDeSalas(Dungeon dungeon) {
        ListaSimplementeEnlazada<NodoGrafo<Room>> nodos = dungeon.getGrafo().getNodos();
        for (int i = 0; i < nodos.getSize(); i++) {
            Room room = nodos.get(i).getDatos();
            for (int fila = 0; fila < room.getFilas(); fila++) {
                for (int col = 0; col < room.getCols(); col++) {
                    try {
                        room.getCell(fila, col).removeUnit();
                    } catch (InvalidMoveException e) {
                        // La iteración respeta límites.
                    }
                }
            }
            room.getEnemigos().clear();
            room.setAllyNpc(null);
        }
    }

    /**
     * Restaura a Malachar como aliado separado de la lista de enemigos.
     */
    private static void restaurarMalachar(Dungeon dungeon, GameState.MalacharStateDTO dto) {
        if (dto == null) {
            return;
        }
        Room salaFinal = dungeon.getRoomById("S5-D");
        if (salaFinal == null || !salaFinal.isEnRango(dto.fila, dto.col)) {
            return;
        }
        MalacharAlly malachar = new MalacharAlly(dto.fila, dto.col);
        malachar.setHp(dto.hp);
        malachar.setTurnosRecuperacion(dto.turnosRecuperacion);
        restaurarEfectos(malachar.getEfectosActivos(), dto.efectos);
        if (dto.turnosRecuperacion > 0) {
            malachar.setTurnosRecuperacion(dto.turnosRecuperacion);
        }
        salaFinal.setAllyNpc(malachar);
    }

    /**
     * Restaura enemigos vivos guardados.
     */
    private static void restaurarEnemigos(Dungeon dungeon, GameState.EnemyStateDTO[] enemigos) {
        if (enemigos == null) {
            return;
        }
        for (int i = 0; i < enemigos.length; i++) {
            GameState.EnemyStateDTO dto = enemigos[i];
            if (dto == null || !dto.vivo) {
                continue;
            }
            Room room = dungeon.getRoomById(dto.idSala);
            Enemy enemy = crearEnemigo(dto);
            if (room != null && enemy != null) {
                enemy.setHp(dto.hp);
                enemy.setDropItem(ItemGenerator.crearItem(dto.dropItemId));
                enemy.setTurnosSinActuar(dto.turnosSinActuar);
                enemy.setMiniJefe(dto.esMiniJefe);
                restaurarEstadoParasito(enemy, dto);
                restaurarEfectos(enemy.getEfectosActivos(), dto.efectos);
                room.addEnemigo(enemy);
            }
        }
    }

    /**
     * Restaura campos específicos del Parásito.
     */
    private static void restaurarEstadoParasito(Enemy enemy, GameState.EnemyStateDTO dto) {
        if (!(enemy instanceof ParasitoEnemy)) {
            return;
        }
        ParasitoEnemy parasito = (ParasitoEnemy) enemy;
        parasito.setPhase(dto.parasitoPhase);
        parasito.setAoeCooldown(dto.parasitoAoeCooldown);
        parasito.setDevorarLuzUsado(dto.parasitoDevorarLuzUsado);
        parasito.setPhaseTransitionPending(dto.parasitoPhaseTransitionPending);
        parasito.setDevorarLuzPendiente(dto.parasitoDevorarLuzPendiente);
        parasito.setSkipNextActionByTransition(dto.parasitoSkipNextActionByTransition);
    }

    /**
     * Crea un enemigo normal o mini-boss desde DTO.
     */
    private static Enemy crearEnemigo(GameState.EnemyStateDTO dto) {
        try {
            if (dto.tipoEnemigo != null && EnemyType.valueOf(dto.tipoEnemigo) == EnemyType.PARASITO) {
                return new ParasitoEnemy(dto.fila, dto.col, dto.idSala);
            }
            if (dto.miniBossType != null) {
                return new MiniBossEnemy(MiniBossType.valueOf(dto.miniBossType), dto.fila, dto.col, dto.idSala);
            }
            return new Enemy(EnemyType.valueOf(dto.tipoEnemigo), dto.fila, dto.col, dto.idSala);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Restaura la sala actual y coloca al jugador.
     */
    private static void restaurarSalaActual(Dungeon dungeon, Player player, GameState state)
        throws GameStateException {

        Room room = dungeon.getRoomById(state.idRoomActual);
        if (room == null) {
            throw new GameStateException("La sala guardada no existe: " + state.idRoomActual);
        }
        dungeon.setRoomActual(room);
        try {
            Cell cell = room.getCell(state.filaJugador, state.colJugador);
            cell.setUnit(player);
            room.setFilaJugador(state.filaJugador);
            room.setColJugador(state.colJugador);
        } catch (InvalidMoveException e) {
            throw new GameStateException("La posición guardada del jugador no es válida.");
        }
    }

    /**
     * Restaura datos del TurnManager.
     */
    private static void restaurarTurnManager(TurnManager tm, GameState state) {
        try {
            if (state.faseActual == null) {
                tm.setFaseActual(Phase.MOVEMENT);
            } else {
                tm.setFaseActual(Phase.valueOf(state.faseActual));
            }
        } catch (IllegalArgumentException e) {
            tm.setFaseActual(Phase.MOVEMENT);
        }
        tm.setTurnoGlobal(state.turnoGlobal);
        tm.setLastDialogue(state.lastDialogue);
        try {
            tm.setGameResult(state.gameResult == null ? GameResult.IN_PROGRESS : GameResult.valueOf(state.gameResult));
        } catch (IllegalArgumentException e) {
            tm.setGameResult(GameResult.IN_PROGRESS);
        }
        tm.setEndingText(state.endingText);
        tm.setFinalQuote(state.finalQuote);
        tm.setDefeatReason(state.defeatReason);
        tm.setFinalCombatStarted(state.finalCombatStarted);
        if (state.logEventos != null) {
            for (int i = 0; i < state.logEventos.length; i++) {
                GameLogEntry entry = crearLogEntry(state.logEventos[i]);
                tm.addLogEntry(entry);
            }
        }
    }

    /**
     * Reconstruye una entrada de log desde DTO.
     */
    private static GameLogEntry crearLogEntry(GameState.GameLogEntryDTO dto) {
        if (dto == null || dto.mensaje == null) {
            return null;
        }
        LogEventType tipo = LogEventType.GAME;
        if (dto.tipo != null) {
            try {
                tipo = LogEventType.valueOf(dto.tipo);
            } catch (IllegalArgumentException e) {
                tipo = LogEventType.GAME;
            }
        }
        return new GameLogEntry(dto.turno, tipo, dto.actor, dto.salaId, dto.mensaje, dto.detalle);
    }

    // -- Efectos --------------------------------------------------------------

    /**
     * Extrae efectos activos.
     */
    private static GameState.EffectStateDTO[] efectos(ListaSimplementeEnlazada<Effect> efectos) {
        GameState.EffectStateDTO[] result = new GameState.EffectStateDTO[efectos.getSize()];
        for (int i = 0; i < efectos.getSize(); i++) {
            Effect effect = efectos.get(i);
            GameState.EffectStateDTO dto = new GameState.EffectStateDTO();
            dto.tipo = effect.getTipo().name();
            dto.turnos = effect.getTurnosRestantes();
            result[i] = dto;
        }
        return result;
    }

    /**
     * Restaura efectos en una lista.
     */
    private static void restaurarEfectos(ListaSimplementeEnlazada<Effect> destino,
                                         GameState.EffectStateDTO[] efectos) {
        destino.clear();
        if (efectos == null) {
            return;
        }
        for (int i = 0; i < efectos.length; i++) {
            GameState.EffectStateDTO dto = efectos[i];
            if (dto != null && dto.tipo != null && dto.turnos > 0) {
                try {
                    destino.addEnd(new Effect(EffectType.valueOf(dto.tipo), dto.turnos));
                } catch (IllegalArgumentException e) {
                    // Efecto corrupto: se ignora.
                }
            }
        }
    }

    // -- Utilidades -----------------------------------------------------------

    /**
     * Devuelve el id de un item.
     */
    private static String idItem(Item item) {
        return item == null ? null : item.getId();
    }

    /**
     * Convierte una lista de items a IDs.
     */
    private static String[] idsItems(ListaSimplementeEnlazada<Item> items) {
        String[] ids = new String[items.getSize()];
        for (int i = 0; i < items.getSize(); i++) {
            ids[i] = idItem(items.get(i));
        }
        return ids;
    }

    /**
     * Convierte una lista de String a array.
     */
    private static String[] strings(ListaSimplementeEnlazada<String> lista) {
        String[] result = new String[lista.getSize()];
        for (int i = 0; i < lista.getSize(); i++) {
            result[i] = lista.get(i);
        }
        return result;
    }

    /**
     * Extrae IDs de salas exploradas.
     */
    private static String[] extraerSalasExploradas(Dungeon dungeon) {
        ListaSimplementeEnlazada<String> exploradas = new ListaSimplementeEnlazada<>();
        ListaSimplementeEnlazada<NodoGrafo<Room>> nodos = dungeon.getGrafo().getNodos();
        for (int i = 0; i < nodos.getSize(); i++) {
            Room room = nodos.get(i).getDatos();
            if (room != null && room.isExplorada()) {
                exploradas.addEnd(room.getId());
            }
        }
        return strings(exploradas);
    }

    /**
     * Convierte texto a CharacterType.
     */
    private static CharacterType parseCharacterType(String tipo) throws GameStateException {
        try {
            return CharacterType.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new GameStateException("Tipo de personaje inválido: " + tipo);
        }
    }
}
