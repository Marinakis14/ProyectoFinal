package Valdris.logic.ai;

import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.ai.ArbolDecisionIA.AccionIA;
import Valdris.model.effects.Effect;
import Valdris.model.enums.CellType;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.MiniBossType;
import Valdris.model.map.Room;
import Valdris.model.units.MiniBossEnemy;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link MiniBossAI}.
 *
 * <p>Patrón: Arrange -> Act -> Assert.
 * Cubre las habilidades especiales acordadas para los mini-bosses antes de la
 * capa JavaFX.</p>
 */
class MiniBossAITest {

    // -- Fixture -------------------------------------------------------------

    private Room room;
    private Player player;

    @BeforeEach
    void setUp() {
        room = new Room("BOSS", "Sala de mini-boss", 7, 7);
        player = new Player(CharacterType.KAEL);
    }

    // -- Habilidades listas --------------------------------------------------

    @Test
    void alcaldeConCooldownUsaEstocadaCorrupta() {
        // Arrange
        MiniBossEnemy alcalde = crearBoss(MiniBossType.ALCALDE_CORRUPTO, 3, 3);
        alcalde.setTurnosSinActuar(3);
        player.setPosicion(3, 4);
        int hpInicial = player.getHp();

        // Act
        AIActionResult result = IAEnemigo.executeTurn(alcalde, room, player, null);

        // Assert
        assertEquals(hpInicial - 35, player.getHp());
        assertEquals(0, alcalde.getTurnosSinActuar());
        assertEquals(AccionIA.HABILIDAD_ESPECIAL, result.getAccion());
        assertEquals("Estocada Corrupta", result.getHabilidadEspecial());
        assertEquals(35, result.getCombatResult().getDanioAplicado());
    }

    @Test
    void habilidadEspecial_sumaDanioPorCursePrevio() {
        // Arrange
        MiniBossEnemy alcalde = crearBoss(MiniBossType.ALCALDE_CORRUPTO, 3, 3);
        alcalde.setTurnosSinActuar(3);
        player.setPosicion(3, 4);
        player.addEfecto(new Effect(EffectType.CURSE, 2));
        int hpInicial = player.getHp();

        // Act
        AIActionResult result = IAEnemigo.executeTurn(alcalde, room, player, null);

        // Assert
        assertEquals(38, result.getCombatResult().getDanioAplicado());
        assertEquals(hpInicial - 38, player.getHp());
    }

    @Test
    void espirituMadreConCooldownAplicaDanioYParalysis() {
        // Arrange
        MiniBossEnemy espiritu = crearBoss(MiniBossType.ESPIRITU_MADRE, 3, 1);
        espiritu.setTurnosSinActuar(3);
        player.setPosicion(3, 5);
        int hpInicial = player.getHp();

        // Act
        AIActionResult result = IAEnemigo.executeTurn(espiritu, room, player, null);

        // Assert
        assertEquals(hpInicial - 12, player.getHp());
        assertEquals(0, espiritu.getTurnosSinActuar());
        assertEquals("Enredadera Paralizante", result.getHabilidadEspecial());
        assertEquals(EffectType.PARALYSIS, result.getEfectoAplicado());
        assertTrue(player.tieneEfecto(EffectType.PARALYSIS));
    }

    @Test
    void golemConCooldownUsaPisotonRadioDosAtravesandoPared() throws InvalidMoveException {
        // Arrange
        MiniBossEnemy golem = crearBoss(MiniBossType.GOLEM, 3, 3);
        golem.setTurnosSinActuar(3);
        room.setCellType(3, 4, CellType.WALL);
        player.setPosicion(3, 5);
        int hpInicial = player.getHp();

        // Act
        AIActionResult result = IAEnemigo.executeTurn(golem, room, player, null);

        // Assert
        assertEquals(hpInicial - 28, player.getHp());
        assertEquals(0, golem.getTurnosSinActuar());
        assertEquals("Pisotón Sísmico", result.getHabilidadEspecial());
        assertEquals(28, result.getCombatResult().getDanioAplicado());
    }

    @Test
    void guardianConCooldownAplicaSentenciaYCurse() {
        // Arrange
        MiniBossEnemy guardian = crearBoss(MiniBossType.GUARDIAN_SIN_NOMBRE, 3, 3);
        guardian.setTurnosSinActuar(2);
        player.setPosicion(3, 4);
        int hpInicial = player.getHp();

        // Act
        AIActionResult result = IAEnemigo.executeTurn(guardian, room, player, null);

        // Assert
        assertEquals(hpInicial - 20, player.getHp());
        assertEquals(0, guardian.getTurnosSinActuar());
        assertEquals("Sentencia Arcana", result.getHabilidadEspecial());
        assertEquals(EffectType.CURSE, result.getEfectoAplicado());
        assertTrue(player.tieneEfecto(EffectType.CURSE));
    }

    @Test
    void guardianNoSumaCurseQueAplicaEnEseMismoGolpe() {
        // Arrange
        MiniBossEnemy guardian = crearBoss(MiniBossType.GUARDIAN_SIN_NOMBRE, 3, 3);
        guardian.setTurnosSinActuar(2);
        player.setPosicion(3, 4);
        int hpInicial = player.getHp();

        // Act
        AIActionResult result = IAEnemigo.executeTurn(guardian, room, player, null);

        // Assert
        assertEquals(20, result.getCombatResult().getDanioAplicado());
        assertEquals(hpInicial - 20, player.getHp());
        assertTrue(player.tieneEfecto(EffectType.CURSE));
    }

    // -- Cooldown y colocación -----------------------------------------------

    @Test
    void miniBossSinCooldownHaceAccionNormalEIncrementaContador() {
        // Arrange
        MiniBossEnemy alcalde = crearBoss(MiniBossType.ALCALDE_CORRUPTO, 3, 3);
        player.setPosicion(3, 4);
        int hpInicial = player.getHp();

        // Act
        AIActionResult result = IAEnemigo.executeTurn(alcalde, room, player, null);

        // Assert
        assertTrue(player.getHp() < hpInicial);
        assertEquals(1, alcalde.getTurnosSinActuar());
        assertEquals(AccionIA.ATACAR, result.getAccion());
        assertNull(result.getHabilidadEspecial());
        assertEquals("Alcalde Corrupto", result.getNombreActor());
    }

    @Test
    void especialCargadoFueraDeRangoSeMueveYSiLlegaLoUsa() {
        // Arrange
        MiniBossEnemy alcalde = crearBoss(MiniBossType.ALCALDE_CORRUPTO, 3, 2);
        alcalde.setTurnosSinActuar(3);
        player.setPosicion(3, 5);
        int hpInicial = player.getHp();

        // Act
        AIActionResult result = IAEnemigo.executeTurn(alcalde, room, player, null);

        // Assert
        assertTrue(result.huboMovimiento());
        assertEquals(hpInicial - 35, player.getHp());
        assertEquals(0, alcalde.getTurnosSinActuar());
        assertEquals(AccionIA.HABILIDAD_ESPECIAL, result.getAccion());
    }

    @Test
    void especialCargadoFueraDeRangoSiNoLlegaPierdeIntento() {
        // Arrange
        MiniBossEnemy alcalde = crearBoss(MiniBossType.ALCALDE_CORRUPTO, 0, 0);
        alcalde.setTurnosSinActuar(3);
        player.setPosicion(6, 6);
        int hpInicial = player.getHp();

        // Act
        AIActionResult result = IAEnemigo.executeTurn(alcalde, room, player, null);

        // Assert
        assertEquals(hpInicial, player.getHp());
        assertEquals(0, alcalde.getTurnosSinActuar());
        assertEquals("ESPECIAL_PERDIDO_FUERA_DE_RANGO", result.getMotivo());
        assertNotEquals(AccionIA.HABILIDAD_ESPECIAL, result.getAccion());
    }

    // -- Métodos auxiliares --------------------------------------------------

    /**
     * Crea y coloca un mini-boss en la sala de pruebas.
     *
     * @param tipo tipo de mini-boss
     * @param fila fila inicial
     * @param col columna inicial
     * @return mini-boss creado
     */
    private MiniBossEnemy crearBoss(MiniBossType tipo, int fila, int col) {
        MiniBossEnemy boss = new MiniBossEnemy(tipo, fila, col, room.getId());
        room.addEnemigo(boss);
        return boss;
    }
}
