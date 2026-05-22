package Valdris.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para los enums de las capas de modelo ya implementadas.
 *
 * <p>Patron: Arrange -> Act -> Assert.
 * Cada test verifica un unico grupo de constantes publicas.</p>
 */
class TypeEnumsTest {

    // -- CharacterType -------------------------------------------------------

    @Test
    void characterType_contienePersonajesEnOrdenEsperado() {
        // Arrange
        CharacterType[] esperado = {
            CharacterType.KAEL,
            CharacterType.SYRA,
            CharacterType.DORATH
        };

        // Act + Assert
        assertArrayEquals(esperado, CharacterType.values());
    }

    // -- EffectType ----------------------------------------------------------

    @Test
    void effectType_contieneEfectosEnOrdenEsperado() {
        // Arrange
        EffectType[] esperado = {
            EffectType.SLOW,
            EffectType.BLIND,
            EffectType.CURSE,
            EffectType.PARALYSIS,
            EffectType.BURN
        };

        // Act + Assert
        assertArrayEquals(esperado, EffectType.values());
    }

    // -- ItemType ------------------------------------------------------------

    @Test
    void itemType_contieneCategoriasEnOrdenEsperado() {
        // Arrange
        ItemType[] esperado = {
            ItemType.WEAPON,
            ItemType.ARMOR,
            ItemType.SHIELD,
            ItemType.ACCESSORY,
            ItemType.POTION
        };

        // Act + Assert
        assertArrayEquals(esperado, ItemType.values());
    }

    // -- EnemyType -----------------------------------------------------------

    @Test
    void enemyType_contieneEnemigosEnOrdenEsperado() {
        // Arrange
        EnemyType[] esperado = {
            EnemyType.WARRIOR,
            EnemyType.BERSERKER,
            EnemyType.GUARDIAN,
            EnemyType.ARCHER,
            EnemyType.SNIPER,
            EnemyType.DESTRUCTOR,
            EnemyType.CONTROLLER,
            EnemyType.SUMMONER
        };

        // Act + Assert
        assertArrayEquals(esperado, EnemyType.values());
    }

    // -- CellType ------------------------------------------------------------

    @Test
    void cellType_contieneTiposDeCeldaEnOrdenEsperado() {
        // Arrange
        CellType[] esperado = {
            CellType.FLOOR,
            CellType.WALL,
            CellType.DOOR,
            CellType.DOOR_HIDDEN,
            CellType.DOOR_LOCKED,
            CellType.STAIRS_UP,
            CellType.STAIRS_DOWN,
            CellType.RUNE,
            CellType.LEVER,
            CellType.TRAP
        };

        // Act + Assert
        assertArrayEquals(esperado, CellType.values());
    }

    // -- Phase ---------------------------------------------------------------

    @Test
    void phase_contieneFasesEnOrdenEsperado() {
        // Arrange
        Phase[] esperado = {
            Phase.MOVEMENT,
            Phase.PICKUP,
            Phase.USE_ITEM,
            Phase.ATTACK,
            Phase.ENEMY_TURN
        };

        // Act + Assert
        assertArrayEquals(esperado, Phase.values());
    }
}
