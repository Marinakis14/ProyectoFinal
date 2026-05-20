package Valdris.model.effects;

import Valdris.model.enums.EffectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Effect}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase.</p>
 */
class EffectTest {

    // -- Fixture -------------------------------------------------------------

    private Effect efectoSlow;
    private Effect efectoParalysis;

    @BeforeEach
    void setUp() {
        efectoSlow = new Effect(EffectType.SLOW, 2);
        efectoParalysis = new Effect(EffectType.PARALYSIS, 1);
    }

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_inicializaTipoCorrectamente() {
        assertEquals(EffectType.SLOW, efectoSlow.getTipo());
    }

    @Test
    void constructor_inicializaTurnosCorrectamente() {
        assertEquals(2, efectoSlow.getTurnosRestantes());
        assertEquals(2, efectoSlow.getTurnos());
    }

    @Test
    void constructor_turnosCeroLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> new Effect(EffectType.BLIND, 0));
    }

    @Test
    void constructor_turnosNegativosLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> new Effect(EffectType.CURSE, -1));
    }

    // -- decrementar ---------------------------------------------------------

    @Test
    void decrementar_reduceTurnosEnUno() {
        // Arrange
        int turnosInicio = efectoSlow.getTurnosRestantes();

        // Act
        efectoSlow.decrementar();

        // Assert
        assertEquals(turnosInicio - 1, efectoSlow.getTurnosRestantes());
    }

    @Test
    void decrementar_dosVecesReduceDosVeces() {
        // Act
        efectoSlow.decrementar();
        efectoSlow.decrementar();

        // Assert
        assertEquals(0, efectoSlow.getTurnosRestantes());
    }

    // -- isExpired -----------------------------------------------------------

    @Test
    void isExpired_falsoConTurnosPositivos() {
        assertFalse(efectoSlow.isExpired());
    }

    @Test
    void isExpired_verdaderoAlLlegarACero() {
        // Act
        efectoParalysis.decrementar();

        // Assert
        assertTrue(efectoParalysis.isExpired());
    }

    @Test
    void isExpired_verdaderoSiTurnosNegativos() {
        // Act
        efectoParalysis.decrementar();
        efectoParalysis.decrementar();

        // Assert
        assertTrue(efectoParalysis.isExpired());
    }

    @Test
    void flujoCompleto_slowDuraDosTurnos() {
        // Assert + Act
        assertFalse(efectoSlow.isExpired());
        efectoSlow.decrementar();

        assertFalse(efectoSlow.isExpired());
        efectoSlow.decrementar();

        assertTrue(efectoSlow.isExpired());
    }

    // -- compareTo -----------------------------------------------------------

    @Test
    void compareTo_mismoTipoDevuelveCero() {
        // Arrange
        Effect otroSlow = new Effect(EffectType.SLOW, 5);

        // Act + Assert
        assertEquals(0, efectoSlow.compareTo(otroSlow));
    }

    @Test
    void compareTo_nullDevuelvePositivo() {
        assertTrue(efectoSlow.compareTo(null) > 0);
    }

    // -- toString ------------------------------------------------------------

    @Test
    void toString_formatoCorrecto() {
        assertEquals("[SLOW(2t)]", efectoSlow.toString());
    }

    @Test
    void toString_actualizaTrasCambiosDeTurnos() {
        // Act
        efectoSlow.decrementar();

        // Assert
        assertEquals("[SLOW(1t)]", efectoSlow.toString());
    }
}
