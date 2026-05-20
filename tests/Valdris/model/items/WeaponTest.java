package Valdris.model.items;

import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.ItemType;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Weapon}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class WeaponTest {

    // -- Fixture -------------------------------------------------------------

    private Weapon arma;
    private Player jugador;

    @BeforeEach
    void setUp() {
        arma = new Weapon("W3", "Lanza de Prueba", 12, 2, 2);
        jugador = new Player(CharacterType.KAEL);
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_inicializaDatosPropios() {
        assertEquals(ItemType.WEAPON, arma.getTipo());
        assertEquals(12, arma.getDanoBase());
        assertEquals(2, arma.getPenetracion());
        assertEquals(2, arma.getRango());
        assertNull(arma.getEfectoEspecial());
        assertEquals(0.0, arma.getProbEfecto());
    }

    // -- Afinidad ------------------------------------------------------------

    @Test
    void setAfinidad_modificaDanoEfectivoDelPersonaje() {
        // Act
        arma.setAfinidad(CharacterType.KAEL, 3);
        arma.setAfinidad(CharacterType.SYRA, -2);

        // Assert
        assertEquals(15, arma.getDanoEfectivo(CharacterType.KAEL));
        assertEquals(10, arma.getDanoEfectivo(CharacterType.SYRA));
        assertEquals(12, arma.getDanoEfectivo(CharacterType.DORATH));
    }

    @Test
    void setAfinidad_nullNoModificaAfinidades() {
        // Act
        arma.setAfinidad(null, 5);

        // Assert
        assertEquals(0, arma.getAfinidad(CharacterType.KAEL));
        assertEquals(0, arma.getAfinidad(null));
    }

    // -- use -----------------------------------------------------------------

    @Test
    void use_equipaArmaEnJugador() {
        // Act
        arma.use(jugador);

        // Assert
        assertSame(arma, jugador.getArmaEquipada());
    }

    @Test
    void use_nullNoLanzaExcepcion() {
        assertDoesNotThrow(() -> arma.use(null));
    }

    // -- Efecto especial -----------------------------------------------------

    @Test
    void tryAplicarEfecto_sinEfectoDevuelveNull() {
        assertNull(arma.tryAplicarEfecto());
    }

    @Test
    void tryAplicarEfecto_probabilidadUnoSiempreDevuelveEfecto() {
        // Arrange
        arma.setEfectoEspecial(EffectType.BURN, 1.0);

        // Act + Assert
        assertEquals(EffectType.BURN, arma.tryAplicarEfecto());
    }

    @Test
    void tryAplicarEfecto_probabilidadCeroNuncaDevuelveEfecto() {
        // Arrange
        arma.setEfectoEspecial(EffectType.SLOW, 0.0);

        // Act + Assert
        assertNull(arma.tryAplicarEfecto());
    }

    @Test
    void setEfectoEspecial_limitaProbabilidadAlRangoValido() {
        // Act + Assert
        arma.setEfectoEspecial(EffectType.CURSE, -1.0);
        assertEquals(0.0, arma.getProbEfecto());

        arma.setEfectoEspecial(EffectType.CURSE, 2.0);
        assertEquals(1.0, arma.getProbEfecto());
    }
}
