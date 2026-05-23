package Valdris.persistence;

import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.generation.DungeonGenerator;
import Valdris.logic.generation.ItemGenerator;
import Valdris.logic.turn.TurnManager;
import Valdris.model.effects.Effect;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.LogEventType;
import Valdris.model.enums.MiniBossType;
import Valdris.model.enums.Phase;
import Valdris.model.items.Item;
import Valdris.model.map.Cell;
import Valdris.model.map.Container;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.MiniBossEnemy;
import Valdris.model.units.Player;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link LectorJSON}.
 *
 * <p>Patrón: Arrange -> Act -> Assert. Verifica extracción, guardado, carga y
 * resumen final sin depender de la interfaz JavaFX.</p>
 */
class LectorJSONTest {

    // -- Extracción ----------------------------------------------------------

    @Test
    void extraerGameState_guardaJugadorTurnoSalasPasadizosYLog()
        throws GameStateException, InvalidMoveException {

        Escenario escenario = crearEscenario();

        GameState state = LectorJSON.extraerGameState(escenario.dungeon, escenario.player, escenario.turnManager);

        assertEquals("S2-C", state.idRoomActual);
        assertEquals("KAEL", state.tipoPersonaje);
        assertEquals("ATTACK", state.faseActual);
        assertEquals(7, state.turnoGlobal);
        assertEquals("Diálogo de prueba", state.lastDialogue);
        assertEquals(73, state.hpJugador);
        assertEquals(3, state.filaJugador);
        assertEquals(2, state.colJugador);
        assertTrue(state.haMovido);
        assertTrue(state.haRecogido);
        assertFalse(state.haUsadoItem);
        assertTrue(state.haAtacado);
        assertEquals(5, state.bonusAtaqueTemporal);
        assertEquals("W1", state.armaEquipada);
        assertEquals("A4", state.armaduraEquipada);
        assertEquals("A1", state.escudoEquipado);
        assertEquals("AC5", state.accesorioEquipado);
        assertTrue(contiene(state.itemsInventario, "P3"));
        assertTrue(contiene(state.itemsNarrativos, "AC1"));
        assertTrue(contiene(state.pasadizosActivos, "S1_SECRET"));
        assertTrue(contieneLog(state.logEventos, "Movimiento registrado."));

        GameState.RoomStateDTO s2c = buscarSala(state, "S2-C");
        assertNotNull(s2c);
        assertTrue(s2c.explorada);
        assertEquals(6, s2c.puzzleFailureDamage);
        assertEquals(1, s2c.activeSequence.length);
        assertTrue(s2c.dialogoKaelMostrado);

        GameState.RoomStateDTO s1sec = buscarSala(state, "S1-SEC");
        assertNotNull(s1sec);
        GameState.CellStateDTO celdaCofre = buscarCeldaConContenedor(s1sec);
        assertNotNull(celdaCofre);
        assertTrue(celdaCofre.container.abierto);
        assertEquals(0, celdaCofre.container.itemsRestantes.length);
    }

    @Test
    void extraerGameState_guardaEnemigosVivosYMuertos() throws GameStateException, InvalidMoveException {
        Escenario escenario = crearEscenario();

        GameState state = LectorJSON.extraerGameState(escenario.dungeon, escenario.player, escenario.turnManager);

        GameState.EnemyStateDTO vivo = buscarEnemigo(state, escenario.enemigoVivo.getIdSala(),
            escenario.enemigoVivo.getFilaActual(), escenario.enemigoVivo.getColActual());
        GameState.EnemyStateDTO muerto = buscarEnemigo(state, escenario.enemigoMuerto.getIdSala(),
            escenario.enemigoMuerto.getFilaActual(), escenario.enemigoMuerto.getColActual());

        assertNotNull(vivo);
        assertTrue(vivo.vivo);
        assertEquals(11, vivo.hp);
        assertEquals("P3", vivo.dropItemId);
        assertEquals(2, vivo.turnosSinActuar);
        assertEquals("CURSE", vivo.efectos[0].tipo);

        assertNotNull(muerto);
        assertFalse(muerto.vivo);
        assertEquals(0, muerto.hp);
    }

    // -- Guardado y carga ----------------------------------------------------

    @Test
    void guardarYCargar_reconstruyeJugadorTurnoInventariosYResumenDeSala()
        throws GameStateException, InvalidMoveException {

        Escenario escenario = crearEscenario();
        File archivo = archivoTemporal("partida-reconstruccion.json");

        LectorJSON.guardarPartida(escenario.dungeon, escenario.player, escenario.turnManager, archivo.getPath());
        LoadedGame loaded = LectorJSON.cargarPartida(archivo.getPath());

        assertEquals("S2-C", loaded.getDungeon().getRoomActual().getId());
        assertEquals(CharacterType.KAEL, loaded.getPlayer().getTipo());
        assertEquals(73, loaded.getPlayer().getHp());
        assertEquals(3, loaded.getPlayer().getFilaActual());
        assertEquals(2, loaded.getPlayer().getColActual());
        assertEquals(Phase.ATTACK, loaded.getTurnManager().getFaseActual());
        assertEquals(7, loaded.getTurnManager().getTurnoGlobal());
        assertEquals("Diálogo de prueba", loaded.getTurnManager().getLastDialogue());
        assertEquals(2, loaded.getTurnManager().getLog().getSize());
        assertEquals(LogEventType.MOVEMENT, loaded.getTurnManager().getLog().get(0).getTipo());
        assertEquals("Movimiento registrado.", loaded.getTurnManager().getLog().get(0).getMensaje());

        assertTrue(tieneItem(loaded.getPlayer(), "P3"));
        assertTrue(tieneItem(loaded.getPlayer(), "AC1"));
        assertEquals("W1", loaded.getPlayer().getArmaEquipada().getId());
        assertEquals("A4", loaded.getPlayer().getArmaduraEquipada().getId());
        assertEquals("A1", loaded.getPlayer().getEscudoEquipado().getId());
        assertEquals("AC5", loaded.getPlayer().getAccesorioEquipado().getId());
        assertTrue(loaded.getPlayer().tieneEfecto(EffectType.SLOW));
    }

    @Test
    void guardarYCargar_reconstruyeContenedoresPuzzlesPasadizosYEnemigos()
        throws GameStateException, InvalidMoveException {

        Escenario escenario = crearEscenario();
        File archivo = archivoTemporal("partida-mundo.json");

        LectorJSON.guardarPartida(escenario.dungeon, escenario.player, escenario.turnManager, archivo.getPath());
        LoadedGame loaded = LectorJSON.cargarPartida(archivo.getPath());

        assertTrue(loaded.getDungeon().isHiddenPassageActive("S1_SECRET"));

        Room s2c = loaded.getDungeon().getRoomById("S2-C");
        assertTrue(s2c.isExplorada());
        assertTrue(s2c.wasDialogueShown(CharacterType.KAEL));
        assertEquals(1, s2c.getSecuenciaActivada().length);

        Room s1sec = loaded.getDungeon().getRoomById("S1-SEC");
        Cell celdaCofre = s1sec.getCell(s1sec.getFilas() / 2, s1sec.getCols() / 2);
        assertTrue(celdaCofre.getContainer().isAbierto());
        assertTrue(celdaCofre.getContainer().isVacio());

        Enemy vivo = buscarEnemigoEnSala(loaded.getDungeon().getRoomById(escenario.enemigoVivo.getIdSala()),
            escenario.enemigoVivo.getFilaActual(), escenario.enemigoVivo.getColActual());
        assertNotNull(vivo);
        assertEquals(11, vivo.getHp());
        assertEquals(2, vivo.getTurnosSinActuar());
        assertTrue(vivo.tieneEfecto(EffectType.CURSE));
        assertEquals("P3", vivo.getDropItem().getId());

        Enemy muerto = buscarEnemigoEnSala(loaded.getDungeon().getRoomById(escenario.enemigoMuerto.getIdSala()),
            escenario.enemigoMuerto.getFilaActual(), escenario.enemigoMuerto.getColActual());
        assertNull(muerto);

        MiniBossEnemy filtro = buscarMiniBoss(loaded.getDungeon(), MiniBossType.EL_FILTRO);
        assertNotNull(filtro);
        assertEquals(5, filtro.getPenetracionDefensa());
    }

    @Test
    void cargarPartida_archivoInexistente_lanzaGameStateException() {
        File archivo = archivoTemporal("no-existe.json");
        archivo.delete();

        assertThrows(GameStateException.class, () -> LectorJSON.cargarPartida(archivo.getPath()));
    }

    // -- Resumen -------------------------------------------------------------

    @Test
    void extraerGameSummary_guardaSoloDatosFinalesFiables() throws GameStateException, InvalidMoveException {
        Escenario escenario = crearEscenario();

        GameSummary summary = LectorJSON.extraerGameSummary(escenario.dungeon, escenario.player,
            escenario.turnManager);

        assertEquals("KAEL", summary.tipoPersonaje);
        assertEquals("S2-C", summary.idRoomActual);
        assertEquals(73, summary.hpJugador);
        assertEquals(7, summary.turnoGlobal);
        assertTrue(contiene(summary.itemsInventario, "P3"));
        assertTrue(contiene(summary.itemsNarrativos, "AC1"));
        assertTrue(contiene(summary.salasExploradas, "S2-C"));
        assertTrue(contieneLog(summary.logEventos, "Movimiento registrado."));
    }

    @Test
    void exportarResumen_escribeJsonUTF8Legible() throws Exception {
        Escenario escenario = crearEscenario();
        File archivo = archivoTemporal("resumen.json");

        LectorJSON.exportarResumen(escenario.dungeon, escenario.player, escenario.turnManager, archivo.getPath());

        assertTrue(archivo.exists());
        assertTrue(archivo.length() > 0);
        try (FileReader reader = new FileReader(archivo, StandardCharsets.UTF_8)) {
            GameSummary summary = new Gson().fromJson(reader, GameSummary.class);
            assertEquals("KAEL", summary.tipoPersonaje);
            assertTrue(contieneLog(summary.logEventos, "Ataque registrado."));
        }
    }

    // -- Helpers de escenario ------------------------------------------------

    /**
     * Crea una partida con estado dinámico suficiente para probar persistencia.
     */
    private Escenario crearEscenario() throws GameStateException, InvalidMoveException {
        double[] tiradas = new double[80];
        Dungeon dungeon = DungeonGenerator.generarMundo(tiradas, tiradas, tiradas);
        Player player = new Player(CharacterType.KAEL);
        TurnManager turnManager = new TurnManager(dungeon, player);

        dungeon.setRoomActual(dungeon.getRoomById("S2-C"));
        player.setPosicion(3, 2);
        player.setHp(73);
        player.setHaMovido(true);
        player.setHaRecogido(true);
        player.setHaUsadoItem(false);
        player.setHaAtacado(true);
        player.addBonusAtaqueTemporal(5);
        player.addEfecto(new Effect(EffectType.SLOW, 2));
        player.addEfecto(new Effect(EffectType.BLIND, 1));
        addYEquipar(player, "W1");
        addYEquipar(player, "A4");
        addYEquipar(player, "A1");
        addYEquipar(player, "AC5");
        player.addItem(ItemGenerator.crearItem("P3"));
        player.addItem(ItemGenerator.crearItem("AC1"));

        Room s2c = dungeon.getRoomById("S2-C");
        s2c.setExplorada(true);
        s2c.markDialogueShown(CharacterType.KAEL);
        s2c.registrarActivacion(s2c.getCorrectSequence()[0]);

        Room s1sec = dungeon.getRoomById("S1-SEC");
        Container chest = s1sec.getCell(s1sec.getFilas() / 2, s1sec.getCols() / 2).getContainer();
        chest.abrir(player);
        dungeon.activateHiddenPassage("S1_SECRET");

        Enemy enemigoVivo = dungeon.getRoomById("S5-A").getEnemigos().get(0);
        enemigoVivo.setHp(11);
        enemigoVivo.setTurnosSinActuar(2);
        enemigoVivo.setDropItem(ItemGenerator.crearItem("P3"));
        enemigoVivo.addEfecto(new Effect(EffectType.CURSE, 2));

        Enemy enemigoMuerto = dungeon.getRoomById("S1-D").getEnemigos().get(0);
        enemigoMuerto.setHp(0);

        turnManager.setFaseActual(Phase.ATTACK);
        turnManager.setTurnoGlobal(7);
        turnManager.setLastDialogue("Diálogo de prueba");
        turnManager.addLog(LogEventType.MOVEMENT, "KAEL", "Movimiento registrado.", null);
        turnManager.addLog(LogEventType.COMBAT, "KAEL", "Ataque registrado.", null);

        Escenario escenario = new Escenario();
        escenario.dungeon = dungeon;
        escenario.player = player;
        escenario.turnManager = turnManager;
        escenario.enemigoVivo = enemigoVivo;
        escenario.enemigoMuerto = enemigoMuerto;
        return escenario;
    }

    /**
     * Añade un item y lo equipa si corresponde.
     */
    private void addYEquipar(Player player, String id) {
        Item item = ItemGenerator.crearItem(id);
        player.addItem(item);
        player.equip(item);
    }

    /**
     * Crea una ruta de fichero bajo target para los tests.
     */
    private File archivoTemporal(String nombre) {
        File directorio = new File("target/persistence-tests");
        directorio.mkdirs();
        File archivo = new File(directorio, System.nanoTime() + "-" + nombre);
        archivo.deleteOnExit();
        return archivo;
    }

    // -- Helpers de búsqueda -------------------------------------------------

    /**
     * Indica si un array contiene un texto.
     */
    private boolean contiene(String[] valores, String esperado) {
        if (valores == null) {
            return false;
        }
        for (int i = 0; i < valores.length; i++) {
            if (esperado.equals(valores[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indica si un array de eventos contiene un mensaje como fragmento.
     */
    private boolean contieneLog(GameState.GameLogEntryDTO[] valores, String esperado) {
        if (valores == null) {
            return false;
        }
        for (int i = 0; i < valores.length; i++) {
            if (valores[i] != null && valores[i].mensaje != null && valores[i].mensaje.contains(esperado)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Busca una sala dentro del estado serializable.
     */
    private GameState.RoomStateDTO buscarSala(GameState state, String idSala) {
        for (int i = 0; i < state.salas.length; i++) {
            if (idSala.equals(state.salas[i].idSala)) {
                return state.salas[i];
            }
        }
        return null;
    }

    /**
     * Busca una celda con contenedor dentro de una sala serializable.
     */
    private GameState.CellStateDTO buscarCeldaConContenedor(GameState.RoomStateDTO sala) {
        for (int i = 0; i < sala.celdas.length; i++) {
            if (sala.celdas[i].container != null) {
                return sala.celdas[i];
            }
        }
        return null;
    }

    /**
     * Busca un enemigo dentro del estado serializable.
     */
    private GameState.EnemyStateDTO buscarEnemigo(GameState state, String idSala, int fila, int col) {
        for (int i = 0; i < state.enemigos.length; i++) {
            GameState.EnemyStateDTO enemy = state.enemigos[i];
            if (idSala.equals(enemy.idSala) && enemy.fila == fila && enemy.col == col) {
                return enemy;
            }
        }
        return null;
    }

    /**
     * Busca un enemigo en una sala reconstruida.
     */
    private Enemy buscarEnemigoEnSala(Room room, int fila, int col) {
        for (int i = 0; i < room.getEnemigos().getSize(); i++) {
            Enemy enemy = room.getEnemigos().get(i);
            if (enemy.getFilaActual() == fila && enemy.getColActual() == col) {
                return enemy;
            }
        }
        return null;
    }

    /**
     * Busca un mini-boss concreto en todo el dungeon.
     */
    private MiniBossEnemy buscarMiniBoss(Dungeon dungeon, MiniBossType tipo) {
        Room[] rooms = {
            dungeon.getRoomById("S1-D"),
            dungeon.getRoomById("S2-E"),
            dungeon.getRoomById("S3-F"),
            dungeon.getRoomById("S4-E"),
            dungeon.getRoomById("S5-C")
        };
        for (int i = 0; i < rooms.length; i++) {
            for (int j = 0; j < rooms[i].getEnemigos().getSize(); j++) {
                Enemy enemy = rooms[i].getEnemigos().get(j);
                if (enemy instanceof MiniBossEnemy && ((MiniBossEnemy) enemy).getTipoMiniBoss() == tipo) {
                    return (MiniBossEnemy) enemy;
                }
            }
        }
        return null;
    }

    /**
     * Comprueba si el jugador conserva un item normal o narrativo.
     */
    private boolean tieneItem(Player player, String id) {
        for (int i = 0; i < player.getInventario().getSize(); i++) {
            if (id.equals(player.getInventario().get(i).getId())) {
                return true;
            }
        }
        for (int i = 0; i < player.getItemsNarrativos().getSize(); i++) {
            if (id.equals(player.getItemsNarrativos().get(i).getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Contenedor simple de objetos del escenario de prueba.
     */
    private static class Escenario {

        /** Dungeon preparado. */
        private Dungeon dungeon;

        /** Jugador preparado. */
        private Player player;

        /** Gestor preparado. */
        private TurnManager turnManager;

        /** Enemigo vivo modificado. */
        private Enemy enemigoVivo;

        /** Enemigo muerto modificado. */
        private Enemy enemigoMuerto;
    }
}
