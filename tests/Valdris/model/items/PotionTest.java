package Valdris.model.items;

import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.ItemType;
import Valdris.model.units.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Potion}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class PotionTest {

    // -- Fixture -------------------------------------------------------------

    private Potion pocion;
    private Player jugador;

    @BeforeEach
    void setUp() {
        pocion = new Potion("P1", "Pocion Menor", 20);
        jugador = new Player(CharacterType.KAEL);
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_inicializaDatosPropios() {
        assertEquals(ItemType.POTION, pocion.getTipo());
        assertEquals(20, pocion.getCuracionHP());
        assertNull(pocion.getEfectoExtra());
        assertEquals(0, pocion.getValorEfecto());
        assertNull(pocion.getEfectoALimpiarPrimario());
        assertNull(pocion.getEfectoALimpiarSecundario());
        assertEquals(0, pocion.getBonusAtaqueTemporal());
    }

    // -- use -----------------------------------------------------------------

    @Test
    void use_curaSinSuperarHpMaximo() {
        // Arrange
        jugador.recibirDanio(15);
        jugador.addItem(pocion);

        // Act
        pocion.use(jugador);

        // Assert
        assertEquals(jugador.getHpMax(), jugador.getHp());
    }

    @Test
    void use_eliminaPocionDelInventario() {
        // Arrange
        jugador.addItem(pocion);

        // Act
        pocion.use(jugador);

        // Assert
        assertEquals(0, jugador.getInventario().getSize());
    }

    @Test
    void use_aplicaEfectoExtraSiExiste() {
        // Arrange
        pocion.setEfectoExtra(EffectType.PARALYSIS, 1);
        jugador.addItem(pocion);

        // Act
        pocion.use(jugador);

        // Assert
        assertTrue(jugador.tieneEfecto(EffectType.PARALYSIS));
    }

    @Test
    void use_limpiaEfectosConfigurados() {
        // Arrange
        pocion.setEfectosALimpiar(EffectType.CURSE, EffectType.BLIND);
        jugador.addEfecto(new Valdris.model.effects.Effect(EffectType.CURSE, 2));
        jugador.addEfecto(new Valdris.model.effects.Effect(EffectType.BLIND, 2));
        jugador.addEfecto(new Valdris.model.effects.Effect(EffectType.SLOW, 2));
        jugador.addItem(pocion);

        // Act
        pocion.use(jugador);

        // Assert
        assertFalse(jugador.tieneEfecto(EffectType.CURSE));
        assertFalse(jugador.tieneEfecto(EffectType.BLIND));
        assertTrue(jugador.tieneEfecto(EffectType.SLOW));
    }

    @Test
    void use_aplicaBonusAtaqueTemporal() {
        // Arrange
        pocion.setBonusAtaqueTemporal(5);
        jugador.addItem(pocion);

        // Act
        pocion.use(jugador);

        // Assert
        assertEquals(5, jugador.getBonusAtaqueTemporal());
        assertEquals(23, jugador.getAtaqueTotal());
    }

    @Test
    void use_nullNoLanzaExcepcion() {
        assertDoesNotThrow(() -> pocion.use(null));
    }

    // -- Efecto extra --------------------------------------------------------

    @Test
    void setEfectoExtra_guardaTipoYValor() {
        // Act
        pocion.setEfectoExtra(EffectType.SLOW, 3);

        // Assert
        assertEquals(EffectType.SLOW, pocion.getEfectoExtra());
        assertEquals(3, pocion.getValorEfecto());
        assertEquals(3, pocion.getDuracionEfectoExtra());
    }

    @Test
    void setEfectosALimpiar_guardaAmbosEfectos() {
        // Act
        pocion.setEfectosALimpiar(EffectType.CURSE, EffectType.BLIND);

        // Assert
        assertEquals(EffectType.CURSE, pocion.getEfectoALimpiarPrimario());
        assertEquals(EffectType.BLIND, pocion.getEfectoALimpiarSecundario());
    }

    @Test
    void setBonusAtaqueTemporal_noPermiteNegativo() {
        // Act
        pocion.setBonusAtaqueTemporal(-5);

        // Assert
        assertEquals(0, pocion.getBonusAtaqueTemporal());
    }

    @Test
    void getDuracionEfectoExtra_valorNoPositivoDevuelveUno() {
        // Act
        pocion.setEfectoExtra(EffectType.BLIND, 0);

        // Assert
        assertEquals(1, pocion.getDuracionEfectoExtra());
    }
}
