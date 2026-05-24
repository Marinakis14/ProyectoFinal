package Valdris.model.units;

import Valdris.model.effects.Effect;
import Valdris.model.effects.EffectProcessingResult;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.EnemyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la logica comun heredada de {@link Unit}.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico comportamiento de la clase base.</p>
 */
class UnitTest {

    // -- Fixture -------------------------------------------------------------

    private Enemy unidad;

    @BeforeEach
    void setUp() {
        unidad = new Enemy(EnemyType.WARRIOR, 2, 3, "R1");
    }

    // -- Vida ----------------------------------------------------------------

    @Test
    void recibirDanio_reduceHpSinBajarDeCero() {
        // Act
        unidad.recibirDanio(999);

        // Assert
        assertEquals(0, unidad.getHp());
        assertFalse(unidad.isVivo());
    }

    @Test
    void recibirDanio_cantidadNoPositivaNoCambiaHp() {
        // Arrange
        int hpInicial = unidad.getHp();

        // Act
        unidad.recibirDanio(0);
        unidad.recibirDanio(-5);

        // Assert
        assertEquals(hpInicial, unidad.getHp());
    }

    @Test
    void curar_recuperaHpSinSuperarMaximo() {
        // Arrange
        unidad.recibirDanio(10);

        // Act
        unidad.curar(999);

        // Assert
        assertEquals(unidad.getHpMax(), unidad.getHp());
    }

    @Test
    void setHp_limitaValorEntreCeroYMaximo() {
        // Act + Assert
        unidad.setHp(-10);
        assertEquals(0, unidad.getHp());

        unidad.setHp(999);
        assertEquals(unidad.getHpMax(), unidad.getHp());
    }

    // -- Efectos -------------------------------------------------------------

    @Test
    void addEfecto_reemplazaEfectoDelMismoTipo() {
        // Arrange
        unidad.addEfecto(new Effect(EffectType.SLOW, 1));

        // Act
        unidad.addEfecto(new Effect(EffectType.SLOW, 3));

        // Assert
        assertEquals(1, unidad.getEfectosActivos().getSize());
        assertEquals(3, unidad.getEfectosActivos().get(0).getTurnosRestantes());
    }

    @Test
    void tieneEfecto_detectaEfectoActivo() {
        // Arrange
        unidad.addEfecto(new Effect(EffectType.BLIND, 2));

        // Act + Assert
        assertTrue(unidad.tieneEfecto(EffectType.BLIND));
        assertFalse(unidad.tieneEfecto(EffectType.CURSE));
        assertFalse(unidad.tieneEfecto(null));
    }

    @Test
    void removeEfecto_eliminaSoloElTipoIndicado() {
        // Arrange
        unidad.addEfecto(new Effect(EffectType.BLIND, 2));
        unidad.addEfecto(new Effect(EffectType.SLOW, 2));

        // Act
        unidad.removeEfecto(EffectType.BLIND);

        // Assert
        assertFalse(unidad.tieneEfecto(EffectType.BLIND));
        assertTrue(unidad.tieneEfecto(EffectType.SLOW));
        assertEquals(1, unidad.getEfectosActivos().getSize());
    }

    @Test
    void removeEfecto_nullNoModificaLista() {
        // Arrange
        unidad.addEfecto(new Effect(EffectType.BLIND, 2));

        // Act
        unidad.removeEfecto(null);

        // Assert
        assertTrue(unidad.tieneEfecto(EffectType.BLIND));
        assertEquals(1, unidad.getEfectosActivos().getSize());
    }

    @Test
    void procesarEfectos_aplicaDanioYEliminaExpirados() {
        // Arrange
        unidad.addEfecto(new Effect(EffectType.BURN, 1));
        unidad.addEfecto(new Effect(EffectType.CURSE, 2));

        // Act
        EffectProcessingResult result = unidad.procesarEfectos();

        // Assert
        assertEquals(32, unidad.getHp());
        assertFalse(unidad.tieneEfecto(EffectType.BURN));
        assertTrue(unidad.tieneEfecto(EffectType.CURSE));
        assertEquals(1, unidad.getEfectosActivos().getSize());
        assertEquals(3, result.getDanioAplicado());
        assertEquals(1, result.getEfectosExpirados().length);
        assertEquals(EffectType.BURN, result.getEfectosExpirados()[0]);
    }

    @Test
    void getMovEfectivo_slowReduceMovimientoConTecho() {
        // Arrange
        Enemy berserker = new Enemy(EnemyType.BERSERKER, 0, 0, "R1");
        berserker.addEfecto(new Effect(EffectType.SLOW, 2));

        // Act + Assert
        assertEquals(2, berserker.getMovEfectivo());
    }

    @Test
    void getMovEfectivo_blindNoReduceMovimiento() {
        // Arrange
        Enemy berserker = new Enemy(EnemyType.BERSERKER, 0, 0, "R1");
        berserker.addEfecto(new Effect(EffectType.BLIND, 2));

        // Act + Assert
        assertEquals(4, berserker.getMovEfectivo());
    }

    // -- Posicion ------------------------------------------------------------

    @Test
    void setPosicion_actualizaFilaYColumna() {
        // Act
        unidad.setPosicion(4, 5);

        // Assert
        assertEquals(4, unidad.getFilaActual());
        assertEquals(5, unidad.getColActual());
    }

    // -- Estadisticas base ---------------------------------------------------

    @Test
    void getters_devuelvenEstadisticasBase() {
        assertEquals(35, unidad.getHpMax());
        assertEquals(15, unidad.getAtaqueBase());
        assertEquals(8, unidad.getDefensaBase());
        assertEquals(2, unidad.getMovBase());
        assertEquals(1, unidad.getRango());
        assertEquals(15, unidad.getAtaqueTotal());
        assertEquals(8, unidad.getDefensaTotal());
        assertEquals(1, unidad.getRangoEfectivo());
    }
}
