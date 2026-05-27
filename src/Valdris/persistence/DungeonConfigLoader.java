package Valdris.persistence;

import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.generation.ItemGenerator;
import Valdris.model.enums.CellType;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EnemyType;
import Valdris.model.enums.MiniBossType;
import Valdris.model.items.Item;
import Valdris.model.map.Cell;
import Valdris.model.map.Chest;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.MiniBossEnemy;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Carga la configuracion inicial del dungeon desde JSON.
 *
 * <p>La clase pertenece a persistencia porque usa Gson y E/S de ficheros. El
 * resultado es un {@link Dungeon} vivo, construido con las mismas clases de
 * dominio que usa la logica del juego.</p>
 */
public final class DungeonConfigLoader {

    /** Ruta por defecto de la configuracion inicial de entrega. */
    public static final String DEFAULT_CONFIG_PATH = "config/configuracion_inicial_valdris.json";

    /** Lector JSON sencillo para DTOs de configuracion. */
    private static final Gson GSON = new Gson();

    /**
     * Evita instanciar la clase.
     */
    private DungeonConfigLoader() {
    }

    /**
     * Carga el dungeon desde la ruta por defecto.
     *
     * @return dungeon construido desde JSON
     * @throws GameStateException si la configuracion no se puede leer o aplicar
     */
    public static Dungeon cargarConfiguracionInicial() throws GameStateException {
        return cargarConfiguracionInicial(DEFAULT_CONFIG_PATH);
    }

    /**
     * Carga el dungeon desde un fichero JSON de configuracion inicial.
     *
     * @param rutaArchivo ruta del JSON
     * @return dungeon construido
     * @throws GameStateException si la configuracion no es valida
     */
    public static Dungeon cargarConfiguracionInicial(String rutaArchivo) throws GameStateException {
        GameConfig config = leerConfig(rutaArchivo);
        validarConfigBasica(config);

        Dungeon dungeon = new Dungeon();
        crearSalas(dungeon, config.rooms);
        configurarMetadatosDungeon(dungeon, config);
        configurarConexiones(dungeon, config.connections);
        configurarSalas(dungeon, config.rooms, config.randomization);
        configurarEnemigos(dungeon, config.enemies, config.spawnCandidates, config.randomization);
        fijarSalaInicial(dungeon, config.initialRoomId);
        return dungeon;
    }

    /**
     * Lee un DTO de configuracion desde disco.
     */
    private static GameConfig leerConfig(String rutaArchivo) throws GameStateException {
        try (FileReader reader = new FileReader(rutaArchivo, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, GameConfig.class);
        } catch (IOException e) {
            throw new GameStateException("No se pudo cargar la configuracion inicial: " + e.getMessage());
        }
    }

    /**
     * Valida los campos globales imprescindibles.
     */
    private static void validarConfigBasica(GameConfig config) throws GameStateException {
        if (config == null || config.rooms == null || config.initialRoomId == null
            || config.objectiveRoomId == null) {
            throw new GameStateException("Configuracion inicial incompleta.");
        }
    }

    /**
     * Crea salas, aplica layouts y las registra en el dungeon.
     */
    private static void crearSalas(Dungeon dungeon, GameConfig.RoomConfigDTO[] rooms)
        throws GameStateException {

        for (int i = 0; i < rooms.length; i++) {
            GameConfig.RoomConfigDTO dto = rooms[i];
            validarRoom(dto);
            Room room = new Room(dto.id, dto.name, dto.rows, dto.cols);
            if (dto.entry != null) {
                room.setFilaJugador(dto.entry.row);
                room.setColJugador(dto.entry.col);
            }
            room.configurarTimerSala(dto.timer);
            aplicarLayout(room, dto.layout);
            dungeon.addRoom(room);
        }
    }

    /**
     * Valida una sala antes de construirla.
     */
    private static void validarRoom(GameConfig.RoomConfigDTO dto) throws GameStateException {
        if (dto == null || dto.id == null || dto.name == null || dto.rows <= 0 || dto.cols <= 0
            || dto.layout == null || dto.layout.length != dto.rows) {
            throw new GameStateException("Sala inválida en configuración inicial.");
        }
        for (int fila = 0; fila < dto.layout.length; fila++) {
            if (dto.layout[fila] == null || dto.layout[fila].length() != dto.cols) {
                throw new GameStateException("Layout invalido en sala " + dto.id + ".");
            }
        }
    }

    /**
     * Aplica los simbolos del layout a las celdas de una sala.
     */
    private static void aplicarLayout(Room room, String[] layout) throws GameStateException {
        for (int fila = 0; fila < layout.length; fila++) {
            for (int col = 0; col < layout[fila].length(); col++) {
                CellType tipo = tipoPorSimbolo(layout[fila].charAt(col));
                try {
                    room.setCellType(fila, col, tipo);
                    Cell cell = room.getCell(fila, col);
                    if (tipo == CellType.LEVER) {
                        room.addLeverCell(cell);
                    } else if (tipo == CellType.RUNE) {
                        room.addRuneCell(cell);
                    }
                } catch (InvalidMoveException e) {
                    throw new GameStateException("Celda fuera de rango en layout de " + room.getId() + ".");
                }
            }
        }
    }

    /**
     * Traduce un simbolo textual a CellType.
     */
    private static CellType tipoPorSimbolo(char simbolo) throws GameStateException {
        if (simbolo == '#') {
            return CellType.WALL;
        }
        if (simbolo == '.') {
            return CellType.FLOOR;
        }
        if (simbolo == 'D') {
            return CellType.DOOR;
        }
        if (simbolo == 'K') {
            return CellType.DOOR_LOCKED;
        }
        if (simbolo == 'H') {
            return CellType.DOOR_HIDDEN;
        }
        if (simbolo == 'U') {
            return CellType.STAIRS_UP;
        }
        if (simbolo == 'S') {
            return CellType.STAIRS_DOWN;
        }
        if (simbolo == 'L') {
            return CellType.LEVER;
        }
        if (simbolo == 'R') {
            return CellType.RUNE;
        }
        if (simbolo == 'T') {
            return CellType.TRAP;
        }
        throw new GameStateException("Simbolo de celda no reconocido: " + simbolo);
    }

    /**
     * Configura sala inicial y objetivo en el dungeon.
     */
    private static void configurarMetadatosDungeon(Dungeon dungeon, GameConfig config) {
        dungeon.setIdSalaInicial(config.initialRoomId);
        dungeon.setIdSalaObjetivo(config.objectiveRoomId);
    }

    /**
     * Configura las conexiones del grafo.
     */
    private static void configurarConexiones(Dungeon dungeon, GameConfig.ConnectionConfigDTO[] connections)
        throws GameStateException {

        if (connections == null) {
            return;
        }
        for (int i = 0; i < connections.length; i++) {
            GameConfig.ConnectionConfigDTO dto = connections[i];
            Room from = dto == null ? null : dungeon.getRoomById(dto.from);
            Room to = dto == null ? null : dungeon.getRoomById(dto.to);
            if (from == null || to == null || dto.mode == null) {
                throw new GameStateException("Conexión inválida en configuración inicial.");
            }
            if ("BIDIRECTIONAL".equals(dto.mode)) {
                dungeon.conectar(from, to, dto.description);
            } else if ("ONE_WAY".equals(dto.mode)) {
                dungeon.conectarUnidireccional(from, to, dto.description);
            } else if ("HIDDEN".equals(dto.mode)) {
                dungeon.connectHidden(from, to, dto.description, dto.id, dto.bidirectionalOnActivation);
            } else {
                throw new GameStateException("Modo de conexion no reconocido: " + dto.mode);
            }
        }
    }

    /**
     * Aplica celdas especiales, dialogos, secretos y puzzles.
     */
    private static void configurarSalas(Dungeon dungeon, GameConfig.RoomConfigDTO[] rooms,
                                        GameConfig.RandomizationConfigDTO randomization)
        throws GameStateException {

        for (int i = 0; i < rooms.length; i++) {
            GameConfig.RoomConfigDTO dto = rooms[i];
            Room room = dungeon.getRoomById(dto.id);
            configurarCeldas(dungeon, room, dto.cells, randomization);
            configurarDialogos(room, dto.dialogues);
            configurarSecretos(room, dto.secretTriggers);
            configurarPuzzle(room, dto.puzzle, randomization);
        }
    }

    /**
     * Aplica metadatos de celdas.
     */
    private static void configurarCeldas(Dungeon dungeon, Room room, GameConfig.CellConfigDTO[] cells,
                                         GameConfig.RandomizationConfigDTO randomization)
        throws GameStateException {

        if (cells == null) {
            return;
        }
        for (int i = 0; i < cells.length; i++) {
            GameConfig.CellConfigDTO dto = cells[i];
            if (dto == null || !room.isEnRango(dto.row, dto.col)) {
                throw new GameStateException("Celda especial inválida en sala " + room.getId() + ".");
            }
            try {
                Cell cell = room.getCell(dto.row, dto.col);
                configurarTipoCelda(room, cell, dto);
                configurarAcceso(dungeon, cell, dto);
                cell.setRequiredItemId(dto.requiredItemId);
                cell.setTriggerId(dto.triggerId);
                cell.setItem(crearItemDeCelda(dto, randomization));
                configurarContenedor(cell, dto.container);
            } catch (InvalidMoveException e) {
                throw new GameStateException("Celda especial fuera de rango en sala " + room.getId() + ".");
            }
        }
    }

    /**
     * Crea el item de suelo fijo o elegido desde un pool declarado.
     */
    private static Item crearItemDeCelda(GameConfig.CellConfigDTO dto,
                                         GameConfig.RandomizationConfigDTO randomization) {
        if (esRandomGroundItems(randomization) && dto.itemPool != null && dto.itemPool.length > 0) {
            return ItemGenerator.crearItem(dto.itemPool[indiceAleatorio(dto.itemPool.length)]);
        }
        return ItemGenerator.crearItem(dto.itemId);
    }

    /**
     * Sobrescribe el tipo de celda si el DTO lo indica.
     */
    private static void configurarTipoCelda(Room room, Cell cell, GameConfig.CellConfigDTO dto) {
        if (dto.type != null) {
            CellType tipo = CellType.valueOf(dto.type);
            cell.setTipo(tipo);
            if (tipo == CellType.LEVER) {
                room.addLeverCell(cell);
            } else if (tipo == CellType.RUNE) {
                room.addRuneCell(cell);
            }
        }
    }

    /**
     * Configura el destino de una puerta o escalera.
     */
    private static void configurarAcceso(Dungeon dungeon, Cell cell, GameConfig.CellConfigDTO dto)
        throws InvalidMoveException {

        if (dto.targetRoomId == null) {
            return;
        }
        Room destino = dungeon.getRoomById(dto.targetRoomId);
        cell.setDestinoAcceso(destino, dto.targetRow, dto.targetCol);
        if (dto.accessFacing != null) {
            cell.setAccessFacing(dto.accessFacing.rowDelta, dto.accessFacing.colDelta);
        }
        if (destino != null && destino.isEnRango(dto.targetRow, dto.targetCol)) {
            destino.getCell(dto.targetRow, dto.targetCol).setReservedForAccess(true);
        }
    }

    /**
     * Configura un contenedor de celda.
     */
    private static void configurarContenedor(Cell cell, GameConfig.ContainerConfigDTO dto) {
        if (dto == null) {
            return;
        }
        Chest chest = new Chest(dto.id, dto.name);
        if (dto.items != null) {
            for (int i = 0; i < dto.items.length; i++) {
                chest.addItem(ItemGenerator.crearItem(dto.items[i]));
            }
        }
        cell.setContainer(chest);
    }

    /**
     * Configura dialogos por personaje.
     */
    private static void configurarDialogos(Room room, GameConfig.DialogueConfigDTO[] dialogues)
        throws GameStateException {

        if (dialogues == null) {
            return;
        }
        for (int i = 0; i < dialogues.length; i++) {
            GameConfig.DialogueConfigDTO dto = dialogues[i];
            if (dto != null && dto.character != null && dto.text != null) {
                try {
                    room.addCharacterDialogue(CharacterType.valueOf(dto.character), dto.text);
                } catch (IllegalArgumentException e) {
                    throw new GameStateException("Dialogo con personaje invalido en sala " + room.getId() + ".");
                }
            }
        }
    }

    /**
     * Configura triggers secretos.
     */
    private static void configurarSecretos(Room room, GameConfig.SecretTriggerConfigDTO[] triggers) {
        if (triggers == null) {
            return;
        }
        for (int i = 0; i < triggers.length; i++) {
            GameConfig.SecretTriggerConfigDTO dto = triggers[i];
            if (dto != null) {
                room.addSecretTrigger(dto.triggerId, dto.targetId);
            }
        }
    }

    /**
     * Configura un puzzle de sala.
     */
    private static void configurarPuzzle(Room room, GameConfig.PuzzleConfigDTO puzzle,
                                         GameConfig.RandomizationConfigDTO randomization) {
        if (puzzle == null) {
            return;
        }
        room.setPuzzleFailureDamage(puzzle.failureDamage);
        room.setPuzzleSuccessTarget(puzzle.successTarget);
        if (esRandomPuzzleSequences(randomization) && puzzle.sequenceValues != null
            && puzzle.sequenceValues.length > 0) {
            room.setCorrectSequence(permutar(puzzle.sequenceValues));
        } else {
            room.setCorrectSequence(puzzle.correctSequence);
        }
    }

    /**
     * Crea y coloca enemigos iniciales.
     */
    private static void configurarEnemigos(Dungeon dungeon, GameConfig.EnemyConfigDTO[] enemies,
                                           GameConfig.SpawnCandidatesConfigDTO[] spawnCandidates,
                                           GameConfig.RandomizationConfigDTO randomization)
        throws GameStateException {

        if (enemies == null) {
            return;
        }
        for (int i = 0; i < enemies.length; i++) {
            GameConfig.EnemyConfigDTO dto = enemies[i];
            Room room = dto == null ? null : dungeon.getRoomById(dto.roomId);
            Enemy enemy = crearEnemigo(dto, room, spawnCandidates, randomization);
            if (room == null || enemy == null) {
                throw new GameStateException("Enemigo invalido en configuracion inicial.");
            }
            configurarDrop(enemy, dto, randomization);
            room.addEnemigo(enemy);
        }
    }

    /**
     * Crea un enemigo normal o mini-boss desde DTO.
     */
    private static Enemy crearEnemigo(GameConfig.EnemyConfigDTO dto, Room room,
                                      GameConfig.SpawnCandidatesConfigDTO[] spawnCandidates,
                                      GameConfig.RandomizationConfigDTO randomization)
        throws GameStateException {

        if (dto == null || room == null) {
            return null;
        }
        try {
            if (dto.miniBossType != null) {
                return new MiniBossEnemy(MiniBossType.valueOf(dto.miniBossType), dto.row, dto.col, dto.roomId);
            }
            GameConfig.PositionDTO posicion = resolverPosicionEnemigo(room, dto, spawnCandidates, randomization);
            return new Enemy(EnemyType.valueOf(dto.type), posicion.row, posicion.col, dto.roomId);
        } catch (IllegalArgumentException e) {
            throw new GameStateException("Tipo de enemigo invalido en configuracion inicial.");
        }
    }

    /**
     * Configura un drop fijo o generado por tipo para enemigos normales.
     */
    private static void configurarDrop(Enemy enemy, GameConfig.EnemyConfigDTO dto,
                                       GameConfig.RandomizationConfigDTO randomization) {
        if (enemy instanceof MiniBossEnemy || !esRandomEnemyDrops(randomization)) {
            enemy.setDropItem(ItemGenerator.crearItem(dto.dropItemId));
            return;
        }
        enemy.setDropItem(ItemGenerator.crearDropEnemigo(enemy.getTipo()));
    }

    /**
     * Resuelve la posicion inicial de un enemigo normal.
     */
    private static GameConfig.PositionDTO resolverPosicionEnemigo(Room room, GameConfig.EnemyConfigDTO dto,
                                                                  GameConfig.SpawnCandidatesConfigDTO[] candidates,
                                                                  GameConfig.RandomizationConfigDTO randomization)
        throws GameStateException {

        if (!esRandomEnemyPositions(randomization)) {
            return posicionFija(dto.roomId, dto.row, dto.col);
        }
        GameConfig.PositionDTO[] celdas = buscarCandidatos(dto.roomId, candidates);
        if (celdas == null || celdas.length == 0) {
            return posicionFija(dto.roomId, dto.row, dto.col);
        }
        int inicio = indiceAleatorio(celdas.length);
        for (int offset = 0; offset < celdas.length; offset++) {
            GameConfig.PositionDTO candidato = celdas[(inicio + offset) % celdas.length];
            if (esCandidatoLibre(room, candidato)) {
                return posicionFija(dto.roomId, candidato.row, candidato.col);
            }
        }
        throw new GameStateException("No quedan candidatos libres para enemigos en sala " + dto.roomId + ".");
    }

    /**
     * Busca la lista de candidatos asociada a una sala.
     */
    private static GameConfig.PositionDTO[] buscarCandidatos(String roomId,
                                                            GameConfig.SpawnCandidatesConfigDTO[] candidates) {
        if (roomId == null || candidates == null) {
            return null;
        }
        for (int i = 0; i < candidates.length; i++) {
            if (candidates[i] != null && roomId.equals(candidates[i].roomId)) {
                return candidates[i].cells;
            }
        }
        return null;
    }

    /**
     * Comprueba si un candidato puede recibir un enemigo.
     */
    private static boolean esCandidatoLibre(Room room, GameConfig.PositionDTO candidato) throws GameStateException {
        if (candidato == null || !room.isEnRango(candidato.row, candidato.col)) {
            return false;
        }
        try {
            Cell cell = room.getCell(candidato.row, candidato.col);
            return cell.isWalkable() && !cell.isReservedForAccess() && cell.getItem() == null;
        } catch (InvalidMoveException e) {
            throw new GameStateException("Candidato de enemigo fuera de rango en sala " + room.getId() + ".");
        }
    }

    /**
     * Crea una posicion simple para un enemigo.
     */
    private static GameConfig.PositionDTO posicionFija(String roomId, int row, int col) {
        GameConfig.PositionDTO posicion = new GameConfig.PositionDTO();
        posicion.roomId = roomId;
        posicion.row = row;
        posicion.col = col;
        return posicion;
    }

    /**
     * Devuelve una permutacion aleatoria de un array de enteros.
     */
    private static int[] permutar(int[] origen) {
        int[] copia = new int[origen.length];
        for (int i = 0; i < origen.length; i++) {
            copia[i] = origen[i];
        }
        for (int i = copia.length - 1; i > 0; i--) {
            int j = indiceAleatorio(i + 1);
            int tmp = copia[i];
            copia[i] = copia[j];
            copia[j] = tmp;
        }
        return copia;
    }

    /**
     * Elige un indice entero en el rango [0, opciones).
     */
    private static int indiceAleatorio(int opciones) {
        if (opciones <= 1) {
            return 0;
        }
        int indice = (int) (Math.random() * opciones);
        if (indice >= opciones) {
            return opciones - 1;
        }
        return indice;
    }

    /**
     * Indica si se aleatorizan posiciones de enemigos normales.
     */
    private static boolean esRandomEnemyPositions(GameConfig.RandomizationConfigDTO randomization) {
        return randomization != null && randomization.randomEnemyPositions;
    }

    /**
     * Indica si se aleatorizan drops de enemigos normales.
     */
    private static boolean esRandomEnemyDrops(GameConfig.RandomizationConfigDTO randomization) {
        return randomization != null && randomization.randomEnemyDrops;
    }

    /**
     * Indica si se aleatorizan secuencias de puzzle.
     */
    private static boolean esRandomPuzzleSequences(GameConfig.RandomizationConfigDTO randomization) {
        return randomization != null && randomization.randomPuzzleSequences;
    }

    /**
     * Indica si se aleatorizan items de suelo.
     */
    private static boolean esRandomGroundItems(GameConfig.RandomizationConfigDTO randomization) {
        return randomization != null && randomization.randomGroundItems;
    }

    /**
     * Fija la sala inicial como sala actual.
     */
    private static void fijarSalaInicial(Dungeon dungeon, String idSalaInicial) throws GameStateException {
        Room inicial = dungeon.getRoomById(idSalaInicial);
        if (inicial == null) {
            throw new GameStateException("La sala inicial no existe: " + idSalaInicial);
        }
        dungeon.setRoomActual(inicial);
    }
}
