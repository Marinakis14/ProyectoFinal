package Valdris.model.items;

import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.ItemType;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Armor}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class ArmorTest {

    // -- Fixture -------------------------------------------------------------

    private Armor armadura;
    private Armor escudo;
    private Player jugador;

    @BeforeEach
    void setUp() {
        armadura = new Armor("A1", "Cota de Prueba", 4, false);
        escudo = new Armor("S1", "Escudo de Prueba", 2, true);
        jugador = new Player(CharacterType.KAEL);
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_armaduraUsaTipoArmor() {
        assertEquals(ItemType.ARMOR, armadura.getTipo());
        assertEquals(4, armadura.getDefensa());
        assertFalse(armadura.isEscudo());
        assertNull(armadura.getInmunidad());
    }

    @Test
    void constructor_escudoUsaTipoShield() {
        assertEquals(ItemType.SHIELD, escudo.getTipo());
        assertEquals(2, escudo.getDefensa());
        assertTrue(escudo.isEscudo());
    }

    // -- use -----------------------------------------------------------------

    @Test
    void use_armaduraEquipaRanuraDeTorso() {
        // Act
        armadura.use(jugador);

        // Assert
        assertSame(armadura, jugador.getArmaduraEquipada());
        assertNull(jugador.getEscudoEquipado());
    }

    @Test
    void use_escudoEquipaRanuraDeEscudo() {
        // Act
        escudo.use(jugador);

        // Assert
        assertSame(escudo, jugador.getEscudoEquipado());
        assertNull(jugador.getArmaduraEquipada());
    }

    @Test
    void use_nullNoLanzaExcepcion() {
        assertDoesNotThrow(() -> armadura.use(null));
    }

    // -- Inmunidad -----------------------------------------------------------

    @Test
    void setInmunidad_guardaEfectoBloqueado() {
        // Act
        armadura.setInmunidad(EffectType.CURSE);

        // Assert
        assertEquals(EffectType.CURSE, armadura.getInmunidad());
    }
}
