package Valdris.logic.combat;

import Valdris.exceptions.InvalidAttackException;
import Valdris.model.effects.Effect;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.EnemyType;
import Valdris.model.items.Accessory;
import Valdris.model.items.Armor;
import Valdris.model.items.Weapon;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link CombatManager}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class CombatManagerTest {

    // -- Fixture -------------------------------------------------------------

    private Player jugador;
    private Enemy enemigo;
    private Room room;

    @BeforeEach
    void setUp() {
        jugador = new Player(CharacterType.KAEL);
        enemigo = new Enemy(EnemyType.WARRIOR, 0, 1, "R-COMBAT");
        jugador.setPosicion(0, 0);
        room = new Room("R-COMBAT", "Sala de combate", 5, 5);
    }

    // -- calcularDanio -------------------------------------------------------

    @Test
    void calcularDanio_conMultiplicadorFijoAplicaFormulaBase() throws InvalidAttackException {
        // Act
        int danio = CombatManager.calcularDanio(jugador, enemigo, 1.0);

        // Assert
        assertEquals(10, danio);
    }

    @Test
    void calcularDanio_aplicaPenetracionDelArma() throws InvalidAttackException {
        // Arrange
        Weapon arma = new Weapon("W-PEN", "Lanza perforante", 20, 5, 1);
        jugador.equip(arma);
        Enemy guardian = new Enemy(EnemyType.GUARDIAN, 0, 1, "R-COMBAT");

        // Act
        int danio = CombatManager.calcularDanio(jugador, guardian, 1.0);

        // Assert
        assertEquals(15, danio);
    }

    @Test
    void calcularDanio_sumaBonusDeAccesorio() throws InvalidAttackException {
        // Arrange
        Accessory accesorio = new Accessory("AC-ATK", "Anillo ofensivo");
        accesorio.setBonus(4, 0, 0);
        jugador.equip(accesorio);

        // Act
        int danio = CombatManager.calcularDanio(jugador, enemigo, 1.0);

        // Assert
        assertEquals(14, danio);
    }

    @Test
    void calcularDanio_noDevuelveValoresNegativos() throws InvalidAttackException {
        // Arrange
        Enemy guardian = new Enemy(EnemyType.GUARDIAN, 0, 1, "R-COMBAT");

        // Act
        int danio = CombatManager.calcularDanio(jugador, guardian, 0.5);

        // Assert
        assertEquals(0, danio);
    }

    @Test
    void calcularDanio_metodoOficialRespetaRangoAleatorio() throws InvalidAttackException {
        // Act
        int danio = CombatManager.calcularDanio(jugador, enemigo);

        // Assert
        assertTrue(danio >= 1);
        assertTrue(danio <= 19);
    }

    @Test
    void calcularDanio_unidadesNullLanzaInvalidAttackException() {
        assertThrows(InvalidAttackException.class,
            () -> CombatManager.calcularDanio(null, enemigo, 1.0));
        assertThrows(InvalidAttackException.class,
            () -> CombatManager.calcularDanio(jugador, null, 1.0));
    }

    // -- estaEnRango ---------------------------------------------------------

    @Test
    void estaEnRango_cuerpoACuerpoUsaDistanciaManhattan() {
        // Act + Assert
        assertTrue(CombatManager.estaEnRango(jugador, enemigo));

        enemigo.setPosicion(0, 2);
        assertFalse(CombatManager.estaEnRango(jugador, enemigo));
    }

    @Test
    void estaEnRango_rangoMayorQueUnoRequiereVisionSiHaySala() throws Exception {
        // Arrange
        Player syra = new Player(CharacterType.SYRA);
        syra.setPosicion(0, 0);
        enemigo.setPosicion(0, 3);
        room.setCellType(0, 1, Valdris.model.enums.CellType.WALL);

        // Act + Assert
        assertTrue(CombatManager.estaEnRango(syra, enemigo));
        assertFalse(CombatManager.estaEnRango(syra, enemigo, room));
    }

    @Test
    void estaEnRango_nullDevuelveFalse() {
        assertFalse(CombatManager.estaEnRango(null, enemigo));
        assertFalse(CombatManager.estaEnRango(jugador, null));
    }

    // -- resolverAtaqueJugador ----------------------------------------------

    @Test
    void resolverAtaqueJugador_reduceHpDelEnemigo() throws InvalidAttackException {
        // Arrange
        int hpInicial = enemigo.getHp();

        // Act
        int danio = CombatManager.resolverAtaqueJugador(jugador, enemigo);

        // Assert
        assertTrue(danio > 0);
        assertEquals(hpInicial - danio, enemigo.getHp());
    }

    @Test
    void resolverAtaqueJugador_fueraDeRangoLanzaInvalidAttackException() {
        // Arrange
        enemigo.setPosicion(0, 3);

        // Act + Assert
        assertThrows(InvalidAttackException.class,
            () -> CombatManager.resolverAtaqueJugador(jugador, enemigo, room));
    }

    @Test
    void resolverAtaqueJugador_aplicaEfectoEspecialDelArma() throws InvalidAttackException {
        // Arrange
        Weapon arma = new Weapon("W-FIRE", "Espada ardiente", 20, 0, 1);
        arma.setEfectoEspecial(EffectType.BURN, 1.0);
        jugador.equip(arma);

        // Act
        CombatManager.resolverAtaqueJugador(jugador, enemigo);

        // Assert
        assertTrue(enemigo.tieneEfecto(EffectType.BURN));
    }

    @Test
    void resolverAtaqueJugador_aplicaDosEfectosEspecialesDelArma() throws InvalidAttackException {
        // Arrange
        Weapon arma = new Weapon("W-ECLIPSE", "Arco del Eclipse", 20, 0, 4);
        arma.setEfectoEspecial(EffectType.SLOW, 1.0);
        arma.setEfectoEspecialSecundario(EffectType.BLIND, 1.0);
        jugador.equip(arma);

        // Act
        CombatManager.resolverAtaqueJugador(jugador, enemigo);

        // Assert
        assertTrue(enemigo.tieneEfecto(EffectType.SLOW));
        assertTrue(enemigo.tieneEfecto(EffectType.BLIND));
    }

    @Test
    void resolverAtaqueJugador_consumeBonusAtaqueTemporal() throws InvalidAttackException {
        // Arrange
        jugador.addBonusAtaqueTemporal(5);

        // Act
        int danio = CombatManager.resolverAtaqueJugador(jugador, enemigo);

        // Assert
        assertTrue(danio > 0);
        assertEquals(0, jugador.getBonusAtaqueTemporal());
    }

    @Test
    void resolverAtaqueJugador_enemigoMuertoColocaDropEnSala() throws Exception {
        // Arrange
        Weapon arma = new Weapon("W-KILL", "Mandoble", 100, 0, 1);
        Weapon drop = new Weapon("W-DROP", "Botin", 5, 0, 1);
        jugador.equip(arma);
        enemigo.setHp(1);
        enemigo.setDropItem(drop);
        room.addEnemigo(enemigo);

        // Act
        CombatManager.resolverAtaqueJugador(jugador, enemigo, room);

        // Assert
        assertFalse(enemigo.isVivo());
        assertSame(drop, room.getCell(enemigo.getFilaActual(), enemigo.getColActual()).getItem());
    }

    // -- resolverAtaqueEnemigo ----------------------------------------------

    @Test
    void resolverAtaqueEnemigo_reduceHpDelJugador() throws InvalidAttackException {
        // Arrange
        int hpInicial = jugador.getHp();

        // Act
        int danio = CombatManager.resolverAtaqueEnemigo(enemigo, jugador);

        // Assert
        assertTrue(danio >= 7);
        assertTrue(danio <= 22);
        assertEquals(hpInicial - danio, jugador.getHp());
    }

    @Test
    void resolverAtaqueEnemigo_sumaDanioPorCurse() throws InvalidAttackException {
        // Arrange
        jugador.addEfecto(new Effect(EffectType.CURSE, 2));

        // Act
        int danio = CombatManager.resolverAtaqueEnemigo(enemigo, jugador);

        // Assert
        assertTrue(danio >= 10);
        assertTrue(danio <= 25);
        assertEquals(jugador.getHpMax() - danio, jugador.getHp());
    }

    @Test
    void resolverAtaqueEnemigo_respetaDefensaDelJugador() throws InvalidAttackException {
        // Arrange
        Armor armadura = new Armor("A-DEF", "Cota", 30, false);
        jugador.equip(armadura);

        // Act
        int danio = CombatManager.resolverAtaqueEnemigo(enemigo, jugador);

        // Assert
        assertEquals(0, danio);
        assertEquals(jugador.getHpMax(), jugador.getHp());
    }

    // -- BLIND ---------------------------------------------------------------

    @Test
    void fallaAtaquePorBlind_respetaProbabilidadDeterminista() {
        // Arrange
        jugador.addEfecto(new Effect(EffectType.BLIND, 2));

        // Act + Assert
        assertTrue(CombatManager.fallaAtaquePorBlind(jugador, 0.24));
        assertFalse(CombatManager.fallaAtaquePorBlind(jugador, 0.25));
        assertFalse(CombatManager.fallaAtaquePorBlind(jugador, 0.80));
    }

    @Test
    void fallaAtaquePorBlind_sinBlindNoFalla() {
        assertFalse(CombatManager.fallaAtaquePorBlind(jugador, 0.0));
        assertFalse(CombatManager.fallaAtaquePorBlind(null, 0.0));
    }

    // -- resolverAOEDestructor ----------------------------------------------

    @Test
    void resolverAOEDestructor_enRadioDosInfligeDanoBase() throws InvalidAttackException {
        // Arrange
        Enemy destructor = new Enemy(EnemyType.DESTRUCTOR, 2, 2, "R-COMBAT");
        jugador.setPosicion(2, 4);

        // Act
        int danio = CombatManager.resolverAOEDestructor(destructor, room, jugador);

        // Assert
        assertEquals(destructor.getDanoBase(), danio);
        assertEquals(jugador.getHpMax() - danio, jugador.getHp());
    }

    @Test
    void resolverAOEDestructor_fueraDeRadioNoInfligeDano() throws InvalidAttackException {
        // Arrange
        Enemy destructor = new Enemy(EnemyType.DESTRUCTOR, 0, 0, "R-COMBAT");
        jugador.setPosicion(4, 4);

        // Act
        int danio = CombatManager.resolverAOEDestructor(destructor, room, jugador);

        // Assert
        assertEquals(0, danio);
        assertEquals(jugador.getHpMax(), jugador.getHp());
    }

    @Test
    void resolverAOEDestructor_nullLanzaInvalidAttackException() {
        assertThrows(InvalidAttackException.class,
            () -> CombatManager.resolverAOEDestructor(null, room, jugador));
        assertThrows(InvalidAttackException.class,
            () -> CombatManager.resolverAOEDestructor(enemigo, null, jugador));
        assertThrows(InvalidAttackException.class,
            () -> CombatManager.resolverAOEDestructor(enemigo, room, null));
    }
}
