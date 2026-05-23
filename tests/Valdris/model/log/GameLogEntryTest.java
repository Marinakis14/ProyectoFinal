package Valdris.model.log;

import Valdris.model.enums.LogEventType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link GameLogEntry}.
 *
 * <p>Patrón: Arrange -> Act -> Assert. Fija el formato textual y los campos
 * estructurados que usará la UI y la persistencia.</p>
 */
class GameLogEntryTest {

    // -- Constructor y getters ----------------------------------------------

    @Test
    void constructor_guardaCamposEstructurados() {
        // Act
        GameLogEntry entry = new GameLogEntry(4, LogEventType.COMBAT, "KAEL", "S2-C",
            "KAEL inflige 12 daño a WARRIOR.", "danio=12");

        // Assert
        assertEquals(4, entry.getTurno());
        assertEquals(LogEventType.COMBAT, entry.getTipo());
        assertEquals("KAEL", entry.getActor());
        assertEquals("S2-C", entry.getSalaId());
        assertEquals("KAEL inflige 12 daño a WARRIOR.", entry.getMensaje());
        assertEquals("danio=12", entry.getDetalle());
    }

    @Test
    void constructor_normalizaValoresNulosYTurnoNegativo() {
        // Act
        GameLogEntry entry = new GameLogEntry(-1, null, null, null, null, null);

        // Assert
        assertEquals(0, entry.getTurno());
        assertEquals(LogEventType.GAME, entry.getTipo());
        assertEquals("", entry.getMensaje());
    }

    // -- Texto y comparación -------------------------------------------------

    @Test
    void toString_devuelveFormatoVisibleConActor() {
        // Arrange
        GameLogEntry entry = new GameLogEntry(2, LogEventType.PICKUP, "SYRA", "S1-A",
            "SYRA recoge P3 - Poción Grande.", null);

        // Act + Assert
        assertEquals("Turno 2 | PICKUP | SYRA: SYRA recoge P3 - Poción Grande.", entry.toString());
    }

    @Test
    void toString_devuelveFormatoVisibleSinActor() {
        // Arrange
        GameLogEntry entry = new GameLogEntry(1, LogEventType.GAME, null, null,
            "Evento restaurado.", null);

        // Act + Assert
        assertEquals("Turno 1 | GAME | Evento restaurado.", entry.toString());
    }

    @Test
    void compareTo_ordenaPorTurnoTipoYMensaje() {
        // Arrange
        GameLogEntry a = new GameLogEntry(1, LogEventType.COMBAT, "KAEL", "S1-A", "A", null);
        GameLogEntry b = new GameLogEntry(2, LogEventType.COMBAT, "KAEL", "S1-A", "B", null);

        // Act + Assert
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertTrue(a.compareTo(null) > 0);
    }
}
