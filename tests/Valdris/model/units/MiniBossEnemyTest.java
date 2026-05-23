package Valdris.model.units;

import Valdris.model.enums.MiniBossType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link MiniBossEnemy}.
 *
 * <p>Patrón: Arrange -> Act -> Assert.
 * Comprueba las estadísticas acordadas y los datos especiales de mini-boss.</p>
 */
class MiniBossEnemyTest {

    // -- Constructor ---------------------------------------------------------

    @Test
    void constructor_inicializaStatsDeTodosLosMiniBosses() {
        assertStats(MiniBossType.ALCALDE_CORRUPTO, 55, 18, 8, 2, 1, 0);
        assertStats(MiniBossType.ESPIRITU_MADRE, 65, 16, 8, 2, 4, 0);
        assertStats(MiniBossType.GOLEM, 90, 20, 11, 1, 1, 0);
        assertStats(MiniBossType.GUARDIAN_SIN_NOMBRE, 80, 22, 12, 2, 1, 0);
        assertStats(MiniBossType.EL_FILTRO, 70, 22, 10, 2, 3, 5);
    }

    @Test
    void constructor_marcaMiniJefeYNombreNarrativo() {
        // Act
        MiniBossEnemy boss = new MiniBossEnemy(MiniBossType.EL_FILTRO, 2, 3, "S5-C");

        // Assert
        assertTrue(boss.isMiniJefe());
        assertEquals(MiniBossType.EL_FILTRO, boss.getTipoMiniBoss());
        assertEquals("El Filtro", boss.getNombreNarrativo());
        assertEquals("S5-C", boss.getIdSala());
        assertEquals(2, boss.getFilaActual());
        assertEquals(3, boss.getColActual());
    }

    // -- Helpers -------------------------------------------------------------

    private void assertStats(MiniBossType tipo, int hp, int ataque, int defensa,
                             int mov, int rango, int penetracion) {
        // Arrange + Act
        MiniBossEnemy boss = new MiniBossEnemy(tipo, 0, 0, "R-BOSS");

        // Assert
        assertEquals(hp, boss.getHpMax());
        assertEquals(ataque, boss.getAtaqueBase());
        assertEquals(defensa, boss.getDefensaBase());
        assertEquals(mov, boss.getMovBase());
        assertEquals(rango, boss.getRango());
        assertEquals(penetracion, boss.getPenetracionDefensa());
    }
}
