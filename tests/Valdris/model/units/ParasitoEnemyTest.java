package Valdris.model.units;

import Valdris.model.effects.Effect;
import Valdris.model.enums.EffectType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link ParasitoEnemy}.
 *
 * <p>Patrón: Arrange -> Act -> Assert. Verifica fases, estadísticas dinámicas
 * y marcas de transición del enemigo final.</p>
 */
class ParasitoEnemyTest {

    @Test
    void constructor_iniciaEnFaseUnoConStatsDeCoraza() {
        ParasitoEnemy parasito = new ParasitoEnemy(5, 8, "S5-D");

        assertEquals(1, parasito.getPhase());
        assertEquals(180, parasito.getHp());
        assertEquals(180, parasito.getHpMax());
        assertEquals(22, parasito.getAtaqueTotal());
        assertEquals(1, parasito.getMovEfectivo());
        assertEquals(3, parasito.getRangoEfectivo());
    }

    @Test
    void recibirDanio_bajoUmbralFaseUnoPasaAFaseDosYRecuperaHp() {
        ParasitoEnemy parasito = new ParasitoEnemy(5, 8, "S5-D");

        parasito.recibirDanio(70);

        assertEquals(2, parasito.getPhase());
        assertEquals(140, parasito.getHp());
        assertEquals(140, parasito.getHpMax());
        assertEquals(20, parasito.getAtaqueTotal());
        assertEquals(2, parasito.getMovEfectivo());
        assertTrue(parasito.isPhaseTransitionPending());
        assertTrue(parasito.isSkipNextActionByTransition());
    }

    @Test
    void recibirDanio_bajoUmbralFaseDosPasaAFaseTresYPreparaDevorarLuz() {
        ParasitoEnemy parasito = new ParasitoEnemy(5, 8, "S5-D");
        parasito.setPhase(2);
        parasito.setHp(61);

        parasito.recibirDanio(1);

        assertEquals(3, parasito.getPhase());
        assertEquals(75, parasito.getHp());
        assertEquals(75, parasito.getHpMax());
        assertEquals(18, parasito.getAtaqueTotal());
        assertEquals(3, parasito.getMovEfectivo());
        assertTrue(parasito.consumirDevorarLuzPendiente());
        assertTrue(parasito.isDevorarLuzUsado());
    }

    @Test
    void getMovEfectivo_respetaSlowSobreMovimientoDeFase() {
        ParasitoEnemy parasito = new ParasitoEnemy(5, 8, "S5-D");
        parasito.setPhase(3);

        parasito.addEfecto(new Effect(EffectType.SLOW, 2));

        assertEquals(2, parasito.getMovEfectivo());
    }
}
