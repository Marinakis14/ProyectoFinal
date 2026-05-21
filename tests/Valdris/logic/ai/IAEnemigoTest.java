package Valdris.logic.ai;

import Valdris.exceptions.InvalidMoveException;
import Valdris.model.effects.Effect;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.EnemyType;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link IAEnemigo}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica una acción pública de la IA enemiga.</p>
 */
class IAEnemigoTest {

    // -- Fixture -------------------------------------------------------------

    private Room room;
    private Player player;

    @BeforeEach
    void setUp() {
        room = new Room("S1-A", "Sala de prueba", 7, 7);
        player = new Player(CharacterType.KAEL);
    }

    // -- executeTurn ---------------------------------------------------------

    @Test
    void executeTurn_warriorEnRangoAtacaJugador() {
        // Arrange
        Enemy warrior = crearEnemy(EnemyType.WARRIOR, 3, 3);
        player.setPosicion(3, 4);
        int hpInicial = player.getHp();

        // Act
        IAEnemigo.executeTurn(warrior, room, player, null);

        // Assert
        assertTrue(player.getHp() < hpInicial);
    }

    @Test
    void executeTurn_enemigoParalizadoNoActua() {
        // Arrange
        Enemy warrior = crearEnemy(EnemyType.WARRIOR, 3, 3);
        warrior.addEfecto(new Effect(EffectType.PARALYSIS, 1));
        player.setPosicion(3, 4);
        int hpInicial = player.getHp();

        // Act
        IAEnemigo.executeTurn(warrior, room, player, null);

        // Assert
        assertEquals(hpInicial, player.getHp());
    }

    @Test
    void executeTurn_datosNulosNoLanzanExcepcion() {
        // Arrange
        Enemy warrior = crearEnemy(EnemyType.WARRIOR, 3, 3);
        player.setPosicion(3, 4);

        // Act + Assert
        assertDoesNotThrow(() -> IAEnemigo.executeTurn(null, room, player, null));
        assertDoesNotThrow(() -> IAEnemigo.executeTurn(warrior, null, player, null));
        assertDoesNotThrow(() -> IAEnemigo.executeTurn(warrior, room, null, null));
    }

    // -- Movimiento básico ---------------------------------------------------

    @Test
    void ejecutarMovimiento_mueveHaciaCeldaAdyacenteAlJugador() throws InvalidMoveException {
        // Arrange
        Enemy warrior = crearEnemy(EnemyType.WARRIOR, 1, 1);
        player.setPosicion(3, 3);

        // Act
        IAEnemigo.ejecutarMovimiento(warrior, room, player);

        // Assert
        assertTrue(distancia(warrior.getFilaActual(), warrior.getColActual(), 3, 3) < 4);
        assertSame(warrior, room.getCell(warrior.getFilaActual(), warrior.getColActual()).getUnit());
        assertNull(room.getCell(1, 1).getUnit());
    }

    // -- Movimiento a zona ---------------------------------------------------

    @Test
    void ejecutarMovimientoAZona_archerBuscaDistanciaDeAtaque() {
        // Arrange
        Enemy archer = crearEnemy(EnemyType.ARCHER, 3, 2);
        player.setPosicion(3, 3);

        // Act
        IAEnemigo.ejecutarMovimientoAZona(archer, room, player);

        // Assert
        int distancia = distancia(archer.getFilaActual(), archer.getColActual(),
            player.getFilaActual(), player.getColActual());
        assertTrue(distancia > 1);
        assertTrue(distancia <= archer.getRangoEfectivo());
    }

    // -- Huida ---------------------------------------------------------------

    @Test
    void ejecutarHuida_summonerAumentaDistanciaAlJugador() {
        // Arrange
        Enemy summoner = crearEnemy(EnemyType.SUMMONER, 3, 3);
        player.setPosicion(3, 4);
        int distanciaInicial = distancia(summoner.getFilaActual(), summoner.getColActual(),
            player.getFilaActual(), player.getColActual());

        // Act
        IAEnemigo.ejecutarHuida(summoner, room, player);

        // Assert
        int distanciaFinal = distancia(summoner.getFilaActual(), summoner.getColActual(),
            player.getFilaActual(), player.getColActual());
        assertTrue(distanciaFinal > distanciaInicial);
    }

    // -- Ataque y cooldown ---------------------------------------------------

    @Test
    void ejecutarAtaque_sniperSinCooldownNoHaceDanioEIncrementaContador() {
        // Arrange
        Enemy sniper = crearEnemy(EnemyType.SNIPER, 3, 1);
        player.setPosicion(3, 5);
        int hpInicial = player.getHp();

        // Act
        IAEnemigo.ejecutarAtaque(sniper, player, null);

        // Assert
        assertEquals(hpInicial, player.getHp());
        assertEquals(1, sniper.getTurnosSinActuar());
    }

    @Test
    void ejecutarAtaque_sniperConCooldownHaceDanioYReiniciaContador() {
        // Arrange
        Enemy sniper = crearEnemy(EnemyType.SNIPER, 3, 1);
        sniper.setTurnosSinActuar(2);
        player.setPosicion(3, 5);
        int hpInicial = player.getHp();

        // Act
        IAEnemigo.ejecutarAtaque(sniper, player, null);

        // Assert
        assertTrue(player.getHp() < hpInicial);
        assertEquals(0, sniper.getTurnosSinActuar());
    }

    @Test
    void ejecutarAtaque_controllerAplicaEfectoSinDanio() {
        // Arrange
        Enemy controller = crearEnemy(EnemyType.CONTROLLER, 3, 2);
        player.setPosicion(3, 5);
        int hpInicial = player.getHp();

        // Act
        IAEnemigo.ejecutarAtaque(controller, player, null);

        // Assert
        assertEquals(hpInicial, player.getHp());
        assertEquals(1, player.getEfectosActivos().getSize());
        assertTrue(esEfectoController(player.getEfectosActivos().get(0).getTipo()));
        assertEquals(2, player.getEfectosActivos().get(0).getTurnos());
    }

    // -- Invocación ----------------------------------------------------------

    @Test
    void invocarBerserker_creaBerserkerCercaYReiniciaCooldown() {
        // Arrange
        Enemy summoner = crearEnemy(EnemyType.SUMMONER, 3, 3);
        summoner.setTurnosSinActuar(2);
        int enemigosIniciales = room.getEnemigos().getSize();

        // Act
        IAEnemigo.invocarBerserker(summoner, room);

        // Assert
        assertEquals(enemigosIniciales + 1, room.getEnemigos().getSize());
        assertEquals(0, summoner.getTurnosSinActuar());
        assertTrue(existeBerserkerInvocado());
    }

    @Test
    void executeTurn_summonerSinCooldownHuyeEIncrementaContador() {
        // Arrange
        Enemy summoner = crearEnemy(EnemyType.SUMMONER, 3, 3);
        summoner.setTurnosSinActuar(1);
        player.setPosicion(3, 4);
        int distanciaInicial = distancia(summoner.getFilaActual(), summoner.getColActual(),
            player.getFilaActual(), player.getColActual());

        // Act
        IAEnemigo.executeTurn(summoner, room, player, null);

        // Assert
        int distanciaFinal = distancia(summoner.getFilaActual(), summoner.getColActual(),
            player.getFilaActual(), player.getColActual());
        assertTrue(distanciaFinal > distanciaInicial);
        assertEquals(2, summoner.getTurnosSinActuar());
    }

    // -- Métodos auxiliares --------------------------------------------------

    /**
     * Crea y coloca un enemigo en la sala de pruebas.
     *
     * @param tipo tipo de enemigo
     * @param fila fila inicial
     * @param col columna inicial
     * @return enemigo creado
     */
    private Enemy crearEnemy(EnemyType tipo, int fila, int col) {
        Enemy enemy = new Enemy(tipo, fila, col, room.getId());
        room.addEnemigo(enemy);
        return enemy;
    }

    /**
     * Calcula distancia Manhattan entre dos posiciones.
     *
     * @param filaA fila de la primera posición
     * @param colA columna de la primera posición
     * @param filaB fila de la segunda posición
     * @param colB columna de la segunda posición
     * @return distancia ortogonal
     */
    private int distancia(int filaA, int colA, int filaB, int colB) {
        return Math.abs(filaA - filaB) + Math.abs(colA - colB);
    }

    /**
     * Indica si un efecto pertenece al conjunto que puede aplicar el Controller.
     *
     * @param tipo efecto consultado
     * @return true si es un efecto válido de Controller
     */
    private boolean esEfectoController(EffectType tipo) {
        return tipo == EffectType.SLOW || tipo == EffectType.BLIND || tipo == EffectType.CURSE;
    }

    /**
     * Busca si existe un Berserker distinto del Summoner inicial.
     *
     * @return true si hay un Berserker en la sala
     */
    private boolean existeBerserkerInvocado() {
        for (int i = 0; i < room.getEnemigos().getSize(); i++) {
            Enemy enemy = room.getEnemigos().get(i);
            if (enemy != null && enemy.getTipo() == EnemyType.BERSERKER) {
                return true;
            }
        }
        return false;
    }
}
