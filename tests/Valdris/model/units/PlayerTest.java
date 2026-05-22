package Valdris.model.units;

import Valdris.model.effects.Effect;
import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.items.Accessory;
import Valdris.model.items.Armor;
import Valdris.model.items.Potion;
import Valdris.model.items.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Player}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class PlayerTest {

    // -- Fixture -------------------------------------------------------------

    private Player kael;

    @BeforeEach
    void setUp() {
        kael = new Player(CharacterType.KAEL);
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_kaelInicializaStatsCorrectas() {
        assertEquals(CharacterType.KAEL, kael.getTipo());
        assertEquals(110, kael.getHpMax());
        assertEquals(18, kael.getAtaqueBase());
        assertEquals(3, kael.getMovBase());
        assertEquals(1, kael.getRango());
        assertEquals(0, kael.getInventario().getSize());
    }

    @Test
    void constructor_syraInicializaStatsCorrectas() {
        // Arrange + Act
        Player syra = new Player(CharacterType.SYRA);

        // Assert
        assertEquals(75, syra.getHpMax());
        assertEquals(12, syra.getAtaqueBase());
        assertEquals(5, syra.getMovBase());
        assertEquals(3, syra.getRango());
    }

    @Test
    void constructor_dorathInicializaStatsCorrectas() {
        // Arrange + Act
        Player dorath = new Player(CharacterType.DORATH);

        // Assert
        assertEquals(80, dorath.getHpMax());
        assertEquals(14, dorath.getAtaqueBase());
        assertEquals(2, dorath.getMovBase());
        assertEquals(4, dorath.getRango());
    }

    // -- Inventario ----------------------------------------------------------

    @Test
    void addItem_y_removeItem_actualizanInventario() {
        // Arrange
        Potion pocion = new Potion("P1", "Pocion Menor", 20);

        // Act
        kael.addItem(pocion);
        kael.removeItem(pocion);

        // Assert
        assertEquals(0, kael.getInventario().getSize());
    }

    @Test
    void addItem_nullNoModificaInventario() {
        // Act
        kael.addItem(null);

        // Assert
        assertEquals(0, kael.getInventario().getSize());
    }

    // -- Equipo --------------------------------------------------------------

    @Test
    void equip_armaActualizaAtaqueYRango() {
        // Arrange
        Weapon arma = new Weapon("W1", "Espada", 20, 0, 2);
        arma.setAfinidad(CharacterType.KAEL, 3);

        // Act
        kael.equip(arma);

        // Assert
        assertSame(arma, kael.getArmaEquipada());
        assertEquals(23, kael.getAtaqueTotal());
        assertEquals(2, kael.getRangoEfectivo());
    }

    @Test
    void equip_armaduraEscudoYAccesorioActualizanDefensa() {
        // Arrange
        Armor armadura = new Armor("A1", "Cota", 4, false);
        Armor escudo = new Armor("S1", "Escudo", 2, true);
        Accessory accesorio = new Accessory("AC1", "Anillo");
        accesorio.setBonus(1, 1, 3);

        // Act
        kael.equip(armadura);
        kael.equip(escudo);
        kael.equip(accesorio);

        // Assert
        assertEquals(9, kael.getDefensaTotal());
        assertEquals(19, kael.getAtaqueTotal());
        assertEquals(4, kael.getMovEfectivo());
    }

    @Test
    void equip_nullNoLanzaExcepcion() {
        assertDoesNotThrow(() -> kael.equip(null));
    }

    // -- Efectos e inmunidades ----------------------------------------------

    @Test
    void addEfecto_respetaInmunidadDeArmadura() {
        // Arrange
        Armor armadura = new Armor("A2", "Cota Bendita", 4, false);
        armadura.setInmunidad(EffectType.CURSE);
        kael.equip(armadura);

        // Act
        kael.addEfecto(new Effect(EffectType.CURSE, 2));

        // Assert
        assertFalse(kael.tieneEfecto(EffectType.CURSE));
        assertTrue(kael.tieneInmunidad(EffectType.CURSE));
    }

    @Test
    void getMovEfectivo_aplicaSlowYBonusAccesorio() {
        // Arrange
        Accessory accesorio = new Accessory("AC2", "Botas");
        accesorio.setBonus(0, 1, 0);
        kael.equip(accesorio);
        kael.addEfecto(new Effect(EffectType.SLOW, 2));

        // Act + Assert
        assertEquals(3, kael.getMovEfectivo());
    }

    @Test
    void bonusAtaqueTemporal_seSumaAlAtaqueYSePuedeConsumir() {
        // Arrange
        kael.addBonusAtaqueTemporal(5);

        // Act + Assert
        assertEquals(23, kael.getAtaqueTotal());
        assertEquals(5, kael.getBonusAtaqueTemporal());

        kael.consumirBonusAtaqueTemporal();

        assertEquals(18, kael.getAtaqueTotal());
        assertEquals(0, kael.getBonusAtaqueTemporal());
    }

    @Test
    void addBonusAtaqueTemporal_noAceptaValoresNoPositivos() {
        // Act
        kael.addBonusAtaqueTemporal(0);
        kael.addBonusAtaqueTemporal(-5);

        // Assert
        assertEquals(0, kael.getBonusAtaqueTemporal());
        assertEquals(18, kael.getAtaqueTotal());
    }

    // -- Acciones de turno ---------------------------------------------------

    @Test
    void resetAcciones_dejaTodasLasAccionesDisponibles() {
        // Arrange
        kael.setHaMovido(true);
        kael.setHaRecogido(true);
        kael.setHaUsadoItem(true);
        kael.setHaAtacado(true);

        // Act
        kael.resetAcciones();

        // Assert
        assertFalse(kael.isHaMovido());
        assertFalse(kael.isHaRecogido());
        assertFalse(kael.isHaUsadoItem());
        assertFalse(kael.isHaAtacado());
    }

    // -- compareTo -----------------------------------------------------------

    @Test
    void compareTo_comparaPorTipoYPosicion() {
        // Arrange
        Player otroKael = new Player(CharacterType.KAEL);
        Player syra = new Player(CharacterType.SYRA);
        otroKael.setPosicion(1, 0);

        // Act + Assert
        assertTrue(kael.compareTo(syra) < 0);
        assertTrue(otroKael.compareTo(kael) > 0);
        assertTrue(kael.compareTo(null) > 0);
    }
}
