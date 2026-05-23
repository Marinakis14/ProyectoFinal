package Valdris.model.units;

import Valdris.model.enums.EffectType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link MalacharAlly}.
 *
 * <p>Patrón: Arrange -> Act -> Assert. Cubre estadísticas y recuperación
 * especial del aliado final.</p>
 */
class MalacharAllyTest {

    @Test
    void constructor_inicializaStatsFinales() {
        MalacharAlly malachar = new MalacharAlly(7, 3);

        assertEquals(35, malachar.getHp());
        assertEquals(9, malachar.getAtaqueTotal());
        assertEquals(4, malachar.getDefensaTotal());
        assertEquals(1, malachar.getMovEfectivo());
        assertEquals(2, malachar.getRangoEfectivo());
        assertEquals(7, malachar.getFilaActual());
        assertEquals(3, malachar.getColActual());
    }

    @Test
    void recibirDanio_alLlegarACeroEntraEnRecuperacion() {
        MalacharAlly malachar = new MalacharAlly(7, 3);

        malachar.recibirDanio(50);

        assertEquals(1, malachar.getHp());
        assertEquals(2, malachar.getTurnosRecuperacion());
        assertTrue(malachar.estaEnRecuperacion());
        assertTrue(malachar.tieneEfecto(EffectType.PARALYSIS));
    }

    @Test
    void procesarRecuperacionTurno_pierdeDosTurnosYDespuesRecuperaHp() {
        MalacharAlly malachar = new MalacharAlly(7, 3);
        malachar.recibirDanio(50);

        assertTrue(malachar.procesarRecuperacionTurno());
        assertEquals(1, malachar.getTurnosRecuperacion());
        assertEquals(1, malachar.getHp());

        assertTrue(malachar.procesarRecuperacionTurno());
        assertEquals(0, malachar.getTurnosRecuperacion());
        assertEquals(18, malachar.getHp());
        assertFalse(malachar.tieneEfecto(EffectType.PARALYSIS));

        assertFalse(malachar.procesarRecuperacionTurno());
    }
}
