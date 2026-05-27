package Valdris.logic.generation;

import Valdris.exceptions.InvalidMoveException;
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

/**
 * Genera el mapa fijo de Valdris: El Núcleo Profundo.
 *
 * <p>La estructura de salas, tamaños, nombres y conexiones sigue el mapa de
 * contenidos de la guía de diseño. El mapa tiene 34 salas reales porque el
 * Pasillo Final se modela como sala independiente de advertencia antes de
 * entrar en S5-D, con conexión de solo ida hacia el Núcleo.</p>
 *
 * <p>El trazado de puertas, cofres, puzzles y escaleras es fijo. Las posiciones
 * concretas de enemigos se eligen al generar la partida dentro de casillas
 * candidatas seguras, evitando accesos, contenedores, puzzles y duplicados.</p>
 */
public final class DungeonGenerator {

    // -- Constantes de salas --------------------------------------------------

    /** ID del pasillo entre Zona 1 y Zona 2. */
    public static final String PASILLO_1_2 = "PASILLO_1_2";

    /** ID del pasillo entre Zona 2 y Zona 3. */
    public static final String PASILLO_2_3 = "PASILLO_2_3";

    /** ID del pasillo entre Zona 3 y Zona 4. */
    public static final String PASILLO_3_4 = "PASILLO_3_4";

    /** ID del pasillo entre Zona 4 y Zona 5. */
    public static final String PASILLO_4_5 = "PASILLO_4_5";

    /** ID del pasillo final antes del punto de no retorno. */
    public static final String PASILLO_FINAL = "PASILLO_FINAL";

    // -- Constructor privado -------------------------------------------------

    /**
     * Evita instanciar la clase porque todos sus métodos son estáticos.
     */
    private DungeonGenerator() {
    }

    // -- Generación principal ------------------------------------------------

    /**
     * Genera una partida nueva con mapa fijo y variación aleatoria controlada.
     *
     * @return dungeon generado
     */
    public static Dungeon generarMundo() {
        return generarMundo(null, null, null, null);
    }

    /**
     * Genera el mundo usando tiradas deterministas opcionales para tests.
     *
     * @param tiradasPuzzle tiradas para ordenar secuencias de puzzles
     * @param tiradasSpawns tiradas para elegir posiciones de enemigos
     * @param tiradasItems tiradas para items aleatorios de pasillos
     * @return dungeon generado
     */
    public static Dungeon generarMundo(double[] tiradasPuzzle, double[] tiradasSpawns, double[] tiradasItems) {
        return generarMundo(tiradasPuzzle, tiradasSpawns, tiradasItems, null);
    }

    /**
     * Genera el mundo usando tiradas deterministas opcionales para tests.
     *
     * @param tiradasPuzzle tiradas para ordenar secuencias de puzzles
     * @param tiradasSpawns tiradas para elegir posiciones de enemigos
     * @param tiradasItems tiradas para items aleatorios de pasillos
     * @param tiradasDrops tiradas para drops de enemigos normales
     * @return dungeon generado
     */
    public static Dungeon generarMundo(double[] tiradasPuzzle, double[] tiradasSpawns,
                                       double[] tiradasItems, double[] tiradasDrops) {
        SecuenciaAleatoria randomPuzzle = new SecuenciaAleatoria(tiradasPuzzle);
        SecuenciaAleatoria randomSpawns = new SecuenciaAleatoria(tiradasSpawns);
        SecuenciaAleatoria randomItems = new SecuenciaAleatoria(tiradasItems);
        SecuenciaAleatoria randomDrops = new SecuenciaAleatoria(tiradasDrops);

        Dungeon dungeon = new Dungeon();
        Rooms rooms = crearRooms();
        configurarTemporizadores(rooms);
        registrarRooms(dungeon, rooms);
        configurarConexiones(dungeon, rooms);
        configurarPuzzles(rooms, randomPuzzle);
        configurarSecretos(dungeon, rooms);
        configurarDialogos(rooms);
        configurarItems(rooms, randomItems);
        configurarEnemigos(rooms, randomSpawns, randomDrops);
        dungeon.setRoomActual(rooms.s1a);
        return dungeon;
    }

    // -- Creación de salas ----------------------------------------------------

    /**
     * Crea todas las salas con tamaños oficiales.
     *
     * @return contenedor interno de salas
     */
    private static Rooms crearRooms() {
        Rooms r = new Rooms();

        r.s1a = sala("S1-A", "Aldea Abandonada", 7, 8);
        r.s1b = sala("S1-B", "El Camino Roto", 5, 6);
        r.s1c = sala("S1-C", "El Puente Gris", 9, 10);
        r.s1d = sala("S1-D", "Ayuntamiento Corrupto", 9, 9);
        r.s1sec = sala("S1-SEC", "El Molino", 5, 5);
        r.p12 = sala(PASILLO_1_2, "Linde del Bosque", 3, 8);

        r.s2a = sala("S2-A", "Entrada del Bosque", 6, 6);
        r.s2b = sala("S2-B", "Claro Corrompido", 7, 8);
        r.s2c = sala("S2-C", "Laberinto de Raíces", 9, 10);
        r.s2d = sala("S2-D", "Sendero Oscuro", 5, 6);
        r.s2e = sala("S2-E", "Corazón del Bosque", 10, 10);
        r.s2sec = sala("S2-SEC", "Raíces Profundas", 5, 5);
        r.p23 = sala(PASILLO_2_3, "Boca de la Mina", 3, 8);

        r.s3a = sala("S3-A", "Entrada de la Mina", 7, 7);
        r.s3b = sala("S3-B", "Sala de Vagonetas", 9, 10);
        r.s3c = sala("S3-C", "Túnel Central", 8, 8);
        r.s3d = sala("S3-D", "Cámara de Cristal", 7, 9);
        r.s3e = sala("S3-E", "Profundidades", 9, 9);
        r.s3f = sala("S3-F", "Cámara del Golem", 10, 11);
        r.s3sec = sala("S3-SEC", "Cámara Enana", 5, 6);
        r.p34 = sala(PASILLO_3_4, "Base de la Torre", 3, 8);

        r.s4a = sala("S4-A", "Planta Baja", 7, 8);
        r.s4b = sala("S4-B", "Biblioteca", 8, 8);
        r.s4c = sala("S4-C", "Sala de Runas", 9, 10);
        r.s4d = sala("S4-D", "Cámara Alta", 6, 6);
        r.s4e = sala("S4-E", "Cúspide de la Torre", 10, 10);
        r.s4sec = sala("S4-SEC", "La Celda de Dorath", 5, 5);
        r.p45 = sala(PASILLO_4_5, "Descenso al Núcleo", 3, 8);

        r.s5a = sala("S5-A", "Antecámara", 7, 7);
        r.s5b = sala("S5-B", "Corredor de Sombras", 5, 8);
        r.s5c = sala("S5-C", "La Puerta del Filtro", 9, 9);
        r.s5sec = sala("S5-SEC", "Cámara de Memorias", 5, 5);
        r.pfinal = sala(PASILLO_FINAL, "Pasillo Final", 3, 8);
        r.s5d = sala("S5-D", "El Núcleo", 10, 11);

        return r;
    }

    /**
     * Crea una sala con borde de paredes.
     *
     * @param id id oficial
     * @param nombre nombre visible
     * @param filas filas
     * @param cols columnas
     * @return sala creada
     */
    private static Room sala(String id, String nombre, int filas, int cols) {
        Room room = new Room(id, nombre, filas, cols);
        aplicarParedes(room);
        room.setFilaJugador(filas / 2);
        room.setColJugador(1);
        return room;
    }

    /**
     * Añade todas las salas al dungeon.
     *
     * @param dungeon dungeon destino
     * @param r salas creadas
     */
    private static void registrarRooms(Dungeon dungeon, Rooms r) {
        Room[] rooms = r.toArray();
        for (int i = 0; i < rooms.length; i++) {
            dungeon.addRoom(rooms[i]);
        }
    }

    // -- Conexiones -----------------------------------------------------------

    /**
     * Configura las conexiones visibles, ocultas y de punto de no retorno.
     *
     * @param dungeon dungeon generado
     * @param r salas del mundo
     */
    private static void configurarConexiones(Dungeon dungeon, Rooms r) {
        conectarEsteOeste(dungeon, r.s1a, r.s1b, "camino");
        conectarEsteOeste(dungeon, r.s1b, r.s1c, "camino");
        conectarEsteOesteControlado(dungeon, r.s1c, r.s1d, "puente", "PUZZLE_S1_C");
        conectarEsteOeste(dungeon, r.s1d, r.p12, "pasillo a Zona 2");
        conectarSecretoVertical(dungeon, r.s1b, r.s1sec, "S1_SECRET");

        conectarEsteOeste(dungeon, r.p12, r.s2a, "entrada al bosque");
        conectarEsteOeste(dungeon, r.s2a, r.s2b, "sendero");
        conectarDiagonalControlado(dungeon, r.s2b, r.s2c, r.s2e, "PUZZLE_S2_C");
        conectarDiagonal(dungeon, r.s2b, r.s2d, r.s2e);
        conectarSecretoVertical(dungeon, r.s2b, r.s2sec, "S2_SECRET");
        conectarEsteOeste(dungeon, r.s2e, r.p23, "pasillo a Zona 3");

        conectarEsteOeste(dungeon, r.p23, r.s3a, "entrada a la mina");
        conectarEsteOeste(dungeon, r.s3a, r.s3b, "galería -1");
        conectarEscaleras(dungeon, r.s3b, r.s3c, "escalera -1 a -2");
        conectarEsteOeste(dungeon, r.s3c, r.s3d, "túnel -2");
        conectarEscaleras(dungeon, r.s3d, r.s3e, "escalera -2 a -3");
        conectarEsteOeste(dungeon, r.s3e, r.s3f, "profundidad");
        conectarEsteOeste(dungeon, r.s3f, r.p34, "pasillo a Zona 4");
        conectarSecretoHorizontal(dungeon, r.s3c, r.s3sec, "S3_SECRET");

        conectarEsteOeste(dungeon, r.p34, r.s4a, "entrada a la torre");
        conectarEsteOeste(dungeon, r.s4a, r.s4b, "biblioteca");
        conectarRamaSuperior(dungeon, r.s4b, r.s4d, r.s4e);
        conectarRamaInferiorControlada(dungeon, r.s4b, r.s4c, r.s4e, "PUZZLE_S4_C");
        conectarSecretoVertical(dungeon, r.s4b, r.s4sec, "S4_SECRET");
        conectarEsteOeste(dungeon, r.s4e, r.p45, "pasillo a Zona 5");

        conectarEsteOeste(dungeon, r.p45, r.s5a, "entrada al núcleo");
        conectarEsteOeste(dungeon, r.s5a, r.s5b, "corredor");
        conectarEsteOeste(dungeon, r.s5b, r.s5c, "puerta del filtro");
        conectarBloqueadoVertical(dungeon, r.s5b, r.s5sec, "sello", "AC3");
        conectarEsteOeste(dungeon, r.s5c, r.pfinal, "pasillo final");
        conectarSoloIda(dungeon, r.pfinal, r.s5d, "punto de no retorno");
    }

    /**
     * Conecta dos salas horizontalmente con puertas normales.
     */
    private static void conectarEsteOeste(Dungeon dungeon, Room oeste, Room este, String desc) {
        dungeon.conectar(oeste, este, desc);
        puerta(oeste, medio(oeste), oeste.getCols() - 1, este, medio(este), 1, CellType.DOOR, null, null);
        puerta(este, medio(este), 0, oeste, medio(oeste), oeste.getCols() - 2, CellType.DOOR, null, null);
    }

    /**
     * Conecta dos salas con puerta de avance abierta por puzzle.
     */
    private static void conectarEsteOesteControlado(Dungeon dungeon, Room oeste, Room este,
                                                    String desc, String target) {
        dungeon.connectHidden(oeste, este, desc, target);
        puerta(oeste, medio(oeste), oeste.getCols() - 1, este, medio(este), 1,
            CellType.DOOR_LOCKED, null, target);
        puerta(este, medio(este), 0, oeste, medio(oeste), oeste.getCols() - 2, CellType.DOOR, null, null);
    }

    /**
     * Configura conexión de solo ida desde el pasillo final.
     */
    private static void conectarSoloIda(Dungeon dungeon, Room origen, Room destino, String desc) {
        dungeon.conectarUnidireccional(origen, destino, desc);
        puerta(origen, medio(origen), origen.getCols() - 1, destino, medio(destino), 1,
            CellType.DOOR, null, null);
        setTipo(destino, medio(destino), 0, CellType.DOOR_LOCKED);
    }

    /**
     * Conecta una sala principal con una sala secreta vertical inferior.
     */
    private static void conectarSecretoVertical(Dungeon dungeon, Room origen, Room secreto, String id) {
        dungeon.connectHidden(origen, secreto, "pasadizo oculto", id);
        puerta(origen, origen.getFilas() - 1, origen.getCols() / 2, secreto, 1, secreto.getCols() / 2,
            CellType.DOOR_HIDDEN, null, id);
        puerta(secreto, 0, secreto.getCols() / 2, origen, origen.getFilas() - 2, origen.getCols() / 2,
            CellType.DOOR, null, null);
        trigger(origen, origen.getFilas() - 2, origen.getCols() / 2, id);
    }

    /**
     * Conecta una sala principal con una sala secreta horizontal.
     */
    private static void conectarSecretoHorizontal(Dungeon dungeon, Room origen, Room secreto, String id) {
        dungeon.connectHidden(origen, secreto, "pasadizo oculto", id);
        puerta(origen, medio(origen), 0, secreto, medio(secreto), secreto.getCols() - 2,
            CellType.DOOR_HIDDEN, null, id);
        puerta(secreto, medio(secreto), secreto.getCols() - 1, origen, medio(origen), 1,
            CellType.DOOR, null, null);
        trigger(origen, medio(origen), 1, id);
    }

    /**
     * Conecta una sala con otra bajo requisito narrativo.
     */
    private static void conectarBloqueadoVertical(Dungeon dungeon, Room origen, Room destino,
                                                  String desc, String requisito) {
        dungeon.conectar(origen, destino, desc);
        puerta(origen, origen.getFilas() - 1, origen.getCols() / 2, destino, 1, destino.getCols() / 2,
            CellType.DOOR_LOCKED, requisito, null);
        puerta(destino, 0, destino.getCols() / 2, origen, origen.getFilas() - 2, origen.getCols() / 2,
            CellType.DOOR, null, null);
    }

    /**
     * Configura las dos ramas de Zona 2.
     */
    private static void conectarDiagonal(Dungeon dungeon, Room origen, Room intermedia, Room destino) {
        dungeon.conectar(origen, intermedia, "rama inferior");
        dungeon.conectar(intermedia, destino, "rama inferior");
        puerta(origen, origen.getFilas() - 1, origen.getCols() - 2, intermedia, 1, 1, CellType.DOOR, null, null);
        puerta(intermedia, 0, 1, origen, origen.getFilas() - 2, origen.getCols() - 2, CellType.DOOR, null, null);
        puerta(intermedia, medio(intermedia), intermedia.getCols() - 1, destino, medio(destino), 1,
            CellType.DOOR, null, null);
        puerta(destino, medio(destino), 0, intermedia, medio(intermedia), intermedia.getCols() - 2,
            CellType.DOOR, null, null);
    }

    /**
     * Configura la rama superior de Zona 2 con puzzle.
     */
    private static void conectarDiagonalControlado(Dungeon dungeon, Room origen, Room intermedia,
                                                   Room destino, String target) {
        dungeon.conectar(origen, intermedia, "rama superior");
        dungeon.connectHidden(intermedia, destino, "raíces abiertas", target);
        puerta(origen, 0, origen.getCols() - 2, intermedia, intermedia.getFilas() - 2, 1,
            CellType.DOOR, null, null);
        puerta(intermedia, intermedia.getFilas() - 1, 1, origen, 1, origen.getCols() - 2,
            CellType.DOOR, null, null);
        puerta(intermedia, medio(intermedia), intermedia.getCols() - 1, destino, 1, 1,
            CellType.DOOR_LOCKED, null, target);
        puerta(destino, 1, 0, intermedia, medio(intermedia), intermedia.getCols() - 2,
            CellType.DOOR, null, null);
    }

    /**
     * Configura rama superior de Zona 4.
     */
    private static void conectarRamaSuperior(Dungeon dungeon, Room origen, Room intermedia, Room destino) {
        conectarDiagonal(dungeon, origen, intermedia, destino);
    }

    /**
     * Configura rama inferior de Zona 4 controlada por runas.
     */
    private static void conectarRamaInferiorControlada(Dungeon dungeon, Room origen, Room intermedia,
                                                       Room destino, String target) {
        conectarDiagonalControlado(dungeon, origen, intermedia, destino, target);
    }

    /**
     * Conecta dos salas mediante escaleras no transitables.
     */
    private static void conectarEscaleras(Dungeon dungeon, Room arriba, Room abajo, String desc) {
        dungeon.conectar(arriba, abajo, desc);
        int filaArriba = arriba.getFilas() / 2;
        int colArriba = arriba.getCols() / 2;
        int filaAbajo = abajo.getFilas() / 2;
        int colAbajo = abajo.getCols() / 2;
        escalera(arriba, filaArriba, colArriba, abajo, filaAbajo + 1, colAbajo, CellType.STAIRS_DOWN);
        escalera(abajo, filaAbajo, colAbajo, arriba, filaArriba + 1, colArriba, CellType.STAIRS_UP);
    }

    // -- Puzzles, secretos, diálogos e items ---------------------------------

    /**
     * Configura puzzles fijos con orden aleatorio por partida.
     */
    private static void configurarPuzzles(Rooms r, SecuenciaAleatoria random) {
        puzzlePalancas(r.s1c, 5, "PUZZLE_S1_C", random);
        puzzlePalancas(r.s2c, 6, "PUZZLE_S2_C", random);
        puzzlePalancas(r.s3b, 7, "PUZZLE_S3_B", random);
        puzzleRunas(r.s4c, 8, "PUZZLE_S4_C", random);
    }

    /**
     * Configura datos de pasadizos secretos en las salas origen.
     */
    private static void configurarSecretos(Dungeon dungeon, Rooms r) {
        r.s1b.addSecretTrigger("S1_SECRET", "S1_SECRET");
        r.s2b.addSecretTrigger("S2_SECRET", "S2_SECRET");
        r.s3c.addSecretTrigger("S3_SECRET", "S3_SECRET");
        r.s4b.addSecretTrigger("S4_SECRET", "S4_SECRET");
    }

    /**
     * Configura diálogos relevantes de zona.
     */
    private static void configurarDialogos(Rooms r) {
        r.s2a.addCharacterDialogue(CharacterType.SYRA,
            "Syra se detiene al primer soplo de aire verde. Lireth aún recuerda su nombre, pero lo pronuncia "
                + "con hojas enfermas y raíces que crecen hacia abajo. No ha vuelto para despedirse del bosque: "
                + "ha vuelto para arrancar de él la causa de su dolor.");
        r.s4a.addCharacterDialogue(CharacterType.KAEL,
            "Kael reconoce la piedra de la Torre de Embrath antes incluso de ver sus muros. Aquí aprendió a "
                + "obedecer juramentos que nadie supo explicarle del todo, y el eco de cada sala parece señalar "
                + "el guantelete que todavía cubre su mano quemada.");
        r.s4sec.addCharacterDialogue(CharacterType.DORATH,
            "Dorath lee los primeros símbolos y siente que la celda se estrecha a su alrededor. Estos textos no "
                + "deberían haber sobrevivido, pero ahí están: la prueba de que su excomunión no nació de una "
                + "herejía, sino de una verdad que la Orden no pudo soportar.");
        r.s5sec.addCharacterDialogue(CharacterType.DORATH,
            "Las memorias del sello encajan por fin. Dorath no encuentra consuelo, pero sí una forma de paz: "
                + "la certeza amarga de que cada sospecha tenía nombre, fecha y tinta seca en los archivos que "
                + "intentaron borrar.");
        r.pfinal.addCharacterDialogue(CharacterType.KAEL,
            "Kael siente que el aire del último umbral pesa más que cualquier armadura. Si cruza esta puerta no "
                + "habrá vuelta atrás, pero por primera vez la deuda del sello no parece una condena: parece una "
                + "respuesta esperando su mano.");
        r.pfinal.addCharacterDialogue(CharacterType.SYRA,
            "Syra escucha el Núcleo detrás de la piedra, profundo y vivo como una raíz que late. Después de esto "
                + "solo queda avanzar; si Lireth aún puede sanar, la respuesta está al otro lado del silencio.");
        r.pfinal.addCharacterDialogue(CharacterType.DORATH,
            "Dorath apoya la mano sobre el último umbral y entiende el precio antes de pagarlo. La verdad no "
                + "abre caminos limpios, pero sí impide volver a vivir de rodillas ante una mentira.");
    }

    /**
     * Configura los limites de turnos por sala.
     */
    private static void configurarTemporizadores(Rooms r) {
        Room[] rooms = r.toArray();
        for (int i = 0; i < rooms.length; i++) {
            Room room = rooms[i];
            if (esPasillo(room) || esSalaPuzzle(room)) {
                room.configurarTimerSala(-1);
            } else if ("S5-D".equals(room.getId())) {
                room.configurarTimerSala(50);
            } else if (esSalaMiniBoss(room)) {
                room.configurarTimerSala(35);
            } else {
                room.configurarTimerSala(limiteSalaNormal(room));
            }
        }
    }

    /**
     * Indica si una sala es un pasillo de transicion sin limite de turnos.
     */
    private static boolean esPasillo(Room room) {
        String id = room.getId();
        return PASILLO_1_2.equals(id)
            || PASILLO_2_3.equals(id)
            || PASILLO_3_4.equals(id)
            || PASILLO_4_5.equals(id)
            || PASILLO_FINAL.equals(id);
    }

    /**
     * Indica si una sala contiene puzzle principal sin presion de turnos.
     */
    private static boolean esSalaPuzzle(Room room) {
        String id = room.getId();
        return "S1-C".equals(id)
            || "S2-C".equals(id)
            || "S3-B".equals(id)
            || "S4-C".equals(id);
    }

    /**
     * Indica si una sala contiene mini-boss.
     */
    private static boolean esSalaMiniBoss(Room room) {
        String id = room.getId();
        return "S1-D".equals(id)
            || "S2-E".equals(id)
            || "S3-F".equals(id)
            || "S4-E".equals(id)
            || "S5-C".equals(id);
    }

    /**
     * Devuelve el limite de una sala normal segun su tamano.
     */
    private static int limiteSalaNormal(Room room) {
        if (room.getFilas() >= 9 || room.getCols() >= 9) {
            return 25;
        }
        return 20;
    }

    /**
     * Coloca cofres e items de pasillo.
     */
    private static void configurarItems(Rooms r, SecuenciaAleatoria random) {
        cofre(r.s1sec, "CH-S1-SEC", "Cofre del Molino", "W4", "W5");
        cofre(r.s2sec, "CH-S2-SEC", "Cofre de Raíces", "W6", "W7");
        cofre(r.s3sec, "CH-S3-SEC", "Cofre Enano", "W8", "W9");
        cofre(r.s4sec, "CH-S4-SEC", "Cofre de Dorath", "W10", "W11", "W12");

        itemSuelo(r.p12, ItemGenerator.itemAleatorioZona(2, random.next()));
        itemSuelo(r.p23, ItemGenerator.itemAleatorioZona(3, random.next()));
        itemSuelo(r.p34, ItemGenerator.itemAleatorioZona(4, random.next()));
        itemSuelo(r.p45, ItemGenerator.itemAleatorioZona(5, random.next()));
    }

    // -- Enemigos -------------------------------------------------------------

    /**
     * Coloca enemigos normales y mini-bosses.
     */
    private static void configurarEnemigos(Rooms r, SecuenciaAleatoria random, SecuenciaAleatoria randomDrops) {
        enemigos(r.s1a, tipos(EnemyType.WARRIOR, EnemyType.WARRIOR, EnemyType.ARCHER), random, randomDrops);
        enemigos(r.s1b, tipos(EnemyType.WARRIOR, EnemyType.ARCHER), random, randomDrops);
        enemigos(r.s1c, tipos(EnemyType.CONTROLLER), random, randomDrops);
        miniBoss(r.s1d, MiniBossType.ALCALDE_CORRUPTO, "AC1", tipos(EnemyType.WARRIOR, EnemyType.WARRIOR),
            random, randomDrops);

        enemigos(r.s2a, tipos(EnemyType.ARCHER, EnemyType.WARRIOR), random, randomDrops);
        enemigos(r.s2b, tipos(EnemyType.ARCHER, EnemyType.ARCHER, EnemyType.DESTRUCTOR), random, randomDrops);
        enemigos(r.s2c, tipos(EnemyType.CONTROLLER), random, randomDrops);
        enemigos(r.s2d, tipos(EnemyType.WARRIOR, EnemyType.WARRIOR, EnemyType.BERSERKER), random, randomDrops);
        miniBoss(r.s2e, MiniBossType.ESPIRITU_MADRE, "AC2", tipos(EnemyType.ARCHER, EnemyType.ARCHER),
            random, randomDrops);
        enemigos(r.s2sec, tipos(EnemyType.GUARDIAN), random, randomDrops);

        enemigos(r.s3a, tipos(EnemyType.WARRIOR, EnemyType.WARRIOR, EnemyType.BERSERKER), random, randomDrops);
        enemigos(r.s3b, tipos(EnemyType.SUMMONER), random, randomDrops);
        enemigos(r.s3c, tipos(EnemyType.GUARDIAN, EnemyType.GUARDIAN, EnemyType.ARCHER), random, randomDrops);
        enemigos(r.s3d, tipos(EnemyType.DESTRUCTOR, EnemyType.DESTRUCTOR, EnemyType.BERSERKER),
            random, randomDrops);
        enemigos(r.s3e, tipos(EnemyType.WARRIOR, EnemyType.WARRIOR, EnemyType.WARRIOR, EnemyType.SNIPER),
            random, randomDrops);
        miniBoss(r.s3f, MiniBossType.GOLEM, "AC3", tipos(EnemyType.GUARDIAN, EnemyType.GUARDIAN),
            random, randomDrops);

        enemigos(r.s4a, tipos(EnemyType.CONSTRUCTO, EnemyType.CONSTRUCTO, EnemyType.CONTROLLER), random,
            randomDrops);
        enemigos(r.s4b, tipos(EnemyType.SNIPER, EnemyType.SNIPER, EnemyType.GUARDIAN), random, randomDrops);
        enemigos(r.s4c, tipos(EnemyType.DESTRUCTOR, EnemyType.CONTROLLER), random, randomDrops);
        enemigos(r.s4d, tipos(EnemyType.BERSERKER, EnemyType.BERSERKER, EnemyType.BERSERKER), random, randomDrops);
        miniBoss(r.s4e, MiniBossType.GUARDIAN_SIN_NOMBRE, "AC4",
            tipos(EnemyType.CONSTRUCTO, EnemyType.CONSTRUCTO), random, randomDrops);

        enemigos(r.s5a, tipos(EnemyType.SOMBRA_ABSORBIDA, EnemyType.SOMBRA_ABSORBIDA, EnemyType.ECO_DE_MAGIA),
            random, randomDrops);
        enemigos(r.s5b, tipos(EnemyType.SOMBRA_ABSORBIDA, EnemyType.SOMBRA_ABSORBIDA,
            EnemyType.SOMBRA_ABSORBIDA, EnemyType.ECO_DE_MAGIA), random, randomDrops);
        miniBoss(r.s5c, MiniBossType.EL_FILTRO, null, new EnemyType[0], random, randomDrops);
    }

    /**
     * Crea un array de tipos de enemigo.
     */
    private static EnemyType[] tipos(EnemyType a) {
        return new EnemyType[] {a};
    }

    /**
     * Crea un array de tipos de enemigo.
     */
    private static EnemyType[] tipos(EnemyType a, EnemyType b) {
        return new EnemyType[] {a, b};
    }

    /**
     * Crea un array de tipos de enemigo.
     */
    private static EnemyType[] tipos(EnemyType a, EnemyType b, EnemyType c) {
        return new EnemyType[] {a, b, c};
    }

    /**
     * Crea un array de tipos de enemigo.
     */
    private static EnemyType[] tipos(EnemyType a, EnemyType b, EnemyType c, EnemyType d) {
        return new EnemyType[] {a, b, c, d};
    }

    /**
     * Coloca enemigos normales evitando duplicar casillas.
     */
    private static void enemigos(Room room, EnemyType[] tipos, SecuenciaAleatoria random,
                                 SecuenciaAleatoria randomDrops) {
        boolean[][] ocupadas = new boolean[room.getFilas()][room.getCols()];
        for (int i = 0; i < tipos.length; i++) {
            int[] pos = elegirSpawn(room, ocupadas, random);
            Enemy enemy = new Enemy(tipos[i], pos[0], pos[1], room.getId());
            enemy.setDropItem(ItemGenerator.crearDropEnemigo(tipos[i], randomDrops.next(), randomDrops.next()));
            room.addEnemigo(enemy);
            ocupadas[pos[0]][pos[1]] = true;
        }
    }

    /**
     * Coloca un mini-boss y sus acompañantes.
     */
    private static void miniBoss(Room room, MiniBossType tipo, String dropId,
                                 EnemyType[] acompanantes, SecuenciaAleatoria random,
                                 SecuenciaAleatoria randomDrops) {
        boolean[][] ocupadas = new boolean[room.getFilas()][room.getCols()];
        int fila = room.getFilas() / 2;
        int col = room.getCols() / 2;
        MiniBossEnemy boss = new MiniBossEnemy(tipo, fila, col, room.getId());
        boss.setDropItem(ItemGenerator.crearItem(dropId));
        room.addEnemigo(boss);
        ocupadas[fila][col] = true;

        for (int i = 0; i < acompanantes.length; i++) {
            int[] pos = elegirSpawn(room, ocupadas, random);
            Enemy enemy = new Enemy(acompanantes[i], pos[0], pos[1], room.getId());
            enemy.setDropItem(ItemGenerator.crearDropEnemigo(acompanantes[i], randomDrops.next(), randomDrops.next()));
            room.addEnemigo(enemy);
            ocupadas[pos[0]][pos[1]] = true;
        }
    }

    /**
     * Elige una celda de spawn segura.
     */
    private static int[] elegirSpawn(Room room, boolean[][] ocupadas, SecuenciaAleatoria random) {
        int total = contarSpawnsDisponibles(room, ocupadas);
        int objetivo = elegirIndice(random.next(), total);
        int vistos = 0;
        for (int fila = 1; fila < room.getFilas() - 1; fila++) {
            for (int col = 1; col < room.getCols() - 1; col++) {
                if (esSpawnDisponible(room, ocupadas, fila, col)) {
                    if (vistos == objetivo) {
                        return new int[] {fila, col};
                    }
                    vistos++;
                }
            }
        }
        return new int[] {room.getFilas() - 2, room.getCols() - 2};
    }

    /**
     * Cuenta spawns disponibles.
     */
    private static int contarSpawnsDisponibles(Room room, boolean[][] ocupadas) {
        int total = 0;
        for (int fila = 1; fila < room.getFilas() - 1; fila++) {
            for (int col = 1; col < room.getCols() - 1; col++) {
                if (esSpawnDisponible(room, ocupadas, fila, col)) {
                    total++;
                }
            }
        }
        return Math.max(total, 1);
    }

    /**
     * Comprueba si una celda puede ser spawn enemigo.
     */
    private static boolean esSpawnDisponible(Room room, boolean[][] ocupadas, int fila, int col) {
        if (ocupadas[fila][col]) {
            return false;
        }
        if (Math.abs(fila - room.getFilaJugador()) + Math.abs(col - room.getColJugador()) < 3) {
            return false;
        }
        try {
            Cell cell = room.getCell(fila, col);
            return cell.isWalkable() && cell.getItem() == null && cell.getContainer() == null
                && !cell.isReservedForAccess() && cell.getTipo() != CellType.LEVER
                && cell.getTipo() != CellType.RUNE;
        } catch (InvalidMoveException e) {
            return false;
        }
    }

    // -- Utilidades de celdas -------------------------------------------------

    /**
     * Aplica paredes a todo el borde de una sala.
     */
    private static void aplicarParedes(Room room) {
        for (int fila = 0; fila < room.getFilas(); fila++) {
            setTipo(room, fila, 0, CellType.WALL);
            setTipo(room, fila, room.getCols() - 1, CellType.WALL);
        }
        for (int col = 0; col < room.getCols(); col++) {
            setTipo(room, 0, col, CellType.WALL);
            setTipo(room, room.getFilas() - 1, col, CellType.WALL);
        }
    }

    /**
     * Configura una puerta.
     */
    private static void puerta(Room origen, int fila, int col, Room destino, int filaDestino, int colDestino,
                               CellType tipo, String requisito, String trigger) {
        try {
            Cell cell = origen.getCell(fila, col);
            cell.setTipo(tipo);
            cell.setDestinoAcceso(destino, filaDestino, colDestino);
            cell.setRequiredItemId(requisito);
            cell.setTriggerId(trigger);
            destino.getCell(filaDestino, colDestino).setReservedForAccess(true);
        } catch (InvalidMoveException e) {
            // Las coordenadas del generador son fijas; si alguna falla, la sala queda sin ese acceso.
        }
    }

    /**
     * Configura una escalera con frente inferior.
     */
    private static void escalera(Room origen, int fila, int col, Room destino,
                                 int filaDestino, int colDestino, CellType tipo) {
        try {
            Cell cell = origen.getCell(fila, col);
            cell.setTipo(tipo);
            cell.setDestinoAcceso(destino, filaDestino, colDestino);
            cell.setAccessFacing(1, 0);
            origen.getCell(fila + 1, col).setReservedForAccess(true);
            destino.getCell(filaDestino, colDestino).setReservedForAccess(true);
        } catch (InvalidMoveException e) {
            // Las coordenadas del generador son fijas; si alguna falla, la escalera queda incompleta.
        }
    }

    /**
     * Marca una celda como trigger secreto.
     */
    private static void trigger(Room room, int fila, int col, String id) {
        try {
            room.getCell(fila, col).setTriggerId(id);
        } catch (InvalidMoveException e) {
            // Trigger opcional; las coordenadas fijas se validan en tests.
        }
    }

    /**
     * Configura un puzzle de palancas.
     */
    private static void puzzlePalancas(Room room, int danio, String target, SecuenciaAleatoria random) {
        int fila = Math.max(2, room.getFilas() / 2 - 1);
        int colA = 2;
        int colB = room.getCols() / 2;
        int colC = room.getCols() - 3;
        palanca(room, fila, colA, target + "_0");
        palanca(room, fila, colB, target + "_1");
        palanca(room, fila, colC, target + "_2");
        room.setPuzzleFailureDamage(danio);
        room.setPuzzleSuccessTarget(target);
        room.setCorrectSequence(permutacion(3, random));
    }

    /**
     * Configura un puzzle de runas.
     */
    private static void puzzleRunas(Room room, int danio, String target, SecuenciaAleatoria random) {
        int fila = room.getFilas() / 2;
        int colA = 2;
        int colB = room.getCols() / 2;
        int colC = room.getCols() - 3;
        runa(room, fila, colA, target + "_0");
        runa(room, fila, colB, target + "_1");
        runa(room, fila, colC, target + "_2");
        room.setPuzzleFailureDamage(danio);
        room.setPuzzleSuccessTarget(target);
        room.setCorrectSequence(permutacion(3, random));
    }

    /**
     * Crea una palanca.
     */
    private static void palanca(Room room, int fila, int col, String trigger) {
        try {
            Cell cell = room.getCell(fila, col);
            cell.setTipo(CellType.LEVER);
            cell.setTriggerId(trigger);
            room.addLeverCell(cell);
        } catch (InvalidMoveException e) {
            // Las coordenadas del puzzle son fijas y se verifican por tests.
        }
    }

    /**
     * Crea una runa.
     */
    private static void runa(Room room, int fila, int col, String trigger) {
        try {
            Cell cell = room.getCell(fila, col);
            cell.setTipo(CellType.RUNE);
            cell.setTriggerId(trigger);
            room.addRuneCell(cell);
        } catch (InvalidMoveException e) {
            // Las coordenadas del puzzle son fijas y se verifican por tests.
        }
    }

    /**
     * Coloca un cofre fijo.
     */
    private static void cofre(Room room, String id, String nombre, String... itemIds) {
        try {
            Chest chest = new Chest(id, nombre);
            for (int i = 0; i < itemIds.length; i++) {
                chest.addItem(ItemGenerator.crearItem(itemIds[i]));
            }
            room.getCell(room.getFilas() / 2, room.getCols() / 2).setContainer(chest);
        } catch (InvalidMoveException e) {
            // Las coordenadas de cofres son fijas y se verifican por tests.
        }
    }

    /**
     * Coloca un item en el suelo del pasillo.
     */
    private static void itemSuelo(Room room, Item item) {
        try {
            room.getCell(room.getFilas() / 2, room.getCols() / 2).setItem(item);
        } catch (InvalidMoveException e) {
            // Las coordenadas de pasillos son fijas y se verifican por tests.
        }
    }

    /**
     * Cambia el tipo de una celda sin propagar excepción de coordenada fija.
     */
    private static void setTipo(Room room, int fila, int col, CellType tipo) {
        try {
            room.setCellType(fila, col, tipo);
        } catch (InvalidMoveException e) {
            // Las coordenadas de borde son generadas desde los límites de la propia sala.
        }
    }

    /**
     * Devuelve la fila central de una sala.
     */
    private static int medio(Room room) {
        return room.getFilas() / 2;
    }

    /**
     * Elige un índice para un número de opciones.
     */
    private static int elegirIndice(double tirada, int opciones) {
        if (opciones <= 1) {
            return 0;
        }
        double normalizada = tirada;
        if (normalizada < 0.0) {
            normalizada = 0.0;
        }
        if (normalizada >= 1.0) {
            normalizada = 0.999999;
        }
        int indice = (int) (normalizada * opciones);
        if (indice >= opciones) {
            return opciones - 1;
        }
        return indice;
    }

    /**
     * Genera una permutación aleatoria de índices.
     */
    private static int[] permutacion(int size, SecuenciaAleatoria random) {
        int[] valores = new int[size];
        for (int i = 0; i < size; i++) {
            valores[i] = i;
        }
        for (int i = size - 1; i > 0; i--) {
            int j = elegirIndice(random.next(), i + 1);
            int tmp = valores[i];
            valores[i] = valores[j];
            valores[j] = tmp;
        }
        return valores;
    }

    // -- Clases internas ------------------------------------------------------

    /**
     * Secuencia de tiradas deterministas con fallback a Math.random().
     */
    private static final class SecuenciaAleatoria {

        /** Tiradas predefinidas. */
        private final double[] tiradas;

        /** Siguiente posición de lectura. */
        private int posicion;

        /**
         * Crea una secuencia.
         *
         * @param tiradas tiradas opcionales
         */
        private SecuenciaAleatoria(double[] tiradas) {
            this.tiradas = tiradas;
            this.posicion = 0;
        }

        /**
         * Devuelve la siguiente tirada.
         *
         * @return valor entre 0 y 1 si la entrada era válida
         */
        private double next() {
            if (tiradas == null || posicion >= tiradas.length) {
                return Math.random();
            }
            double valor = tiradas[posicion];
            posicion++;
            return valor;
        }
    }

    /**
     * Contenedor interno de referencias a las salas del mapa.
     */
    private static final class Rooms {

        private Room s1a;
        private Room s1b;
        private Room s1c;
        private Room s1d;
        private Room s1sec;
        private Room p12;
        private Room s2a;
        private Room s2b;
        private Room s2c;
        private Room s2d;
        private Room s2e;
        private Room s2sec;
        private Room p23;
        private Room s3a;
        private Room s3b;
        private Room s3c;
        private Room s3d;
        private Room s3e;
        private Room s3f;
        private Room s3sec;
        private Room p34;
        private Room s4a;
        private Room s4b;
        private Room s4c;
        private Room s4d;
        private Room s4e;
        private Room s4sec;
        private Room p45;
        private Room s5a;
        private Room s5b;
        private Room s5c;
        private Room s5sec;
        private Room pfinal;
        private Room s5d;

        /**
         * Devuelve todas las salas en orden de recorrido.
         *
         * @return array de salas
         */
        private Room[] toArray() {
            return new Room[] {
                s1a, s1b, s1c, s1d, s1sec, p12,
                s2a, s2b, s2c, s2d, s2e, s2sec, p23,
                s3a, s3b, s3c, s3d, s3e, s3f, s3sec, p34,
                s4a, s4b, s4c, s4d, s4e, s4sec, p45,
                s5a, s5b, s5c, s5sec, pfinal, s5d
            };
        }
    }
}
