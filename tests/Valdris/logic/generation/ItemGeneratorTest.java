package Valdris.logic.generation;

import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.EnemyType;
import Valdris.model.enums.ItemType;
import Valdris.model.items.Accessory;
import Valdris.model.items.Armor;
import Valdris.model.items.Item;
import Valdris.model.items.NarrativeItem;
import Valdris.model.items.Potion;
import Valdris.model.items.Weapon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link ItemGenerator}.
 *
 * <p>Patrón: Arrange -> Act -> Assert.
 * Cubre creación exhaustiva por ID, pools de zona y drops deterministas.</p>
 */
class ItemGeneratorTest {

    // -- crearItem -----------------------------------------------------------

    @Test
    void crearItem_todosLosIdsOficialesDevuelvenItem() {
        // Arrange
        String[] ids = {
            "W1", "W2", "W3", "W4", "W5", "W6", "W7", "W8", "W9", "W10", "W11", "W12",
            "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8",
            "P1", "P2", "P3", "P4", "P5",
            "AC1", "AC2", "AC3", "AC4", "AC5", "AC6", "AC7", "AC8", "N1"
        };

        // Act + Assert
        for (int i = 0; i < ids.length; i++) {
            Item item = ItemGenerator.crearItem(ids[i]);
            assertNotNull(item, "No se creó el item " + ids[i]);
            assertEquals(ids[i], item.getId());
        }
    }

    @Test
    void crearItem_idInvalidoDevuelveNull() {
        assertNull(ItemGenerator.crearItem(null));
        assertNull(ItemGenerator.crearItem("NO_EXISTE"));
    }

    @Test
    void crearItem_devuelveInstanciasNuevas() {
        // Act
        Item primero = ItemGenerator.crearItem("W1");
        Item segundo = ItemGenerator.crearItem("W1");

        // Assert
        assertNotSame(primero, segundo);
        assertEquals(0, primero.compareTo(segundo));
    }

    // -- Armas ---------------------------------------------------------------

    @Test
    void crearItem_armasInicialesConfiguranAfinidadesYRangos() {
        // Act
        Weapon w1 = (Weapon) ItemGenerator.crearItem("W1");
        Weapon w2 = (Weapon) ItemGenerator.crearItem("W2");
        Weapon w3 = (Weapon) ItemGenerator.crearItem("W3");

        // Assert
        assertEquals(30, w1.getDanoEfectivo(CharacterType.KAEL));
        assertEquals(24, w2.getDanoEfectivo(CharacterType.SYRA));
        assertEquals(26, w3.getDanoEfectivo(CharacterType.DORATH));
        assertEquals(1, w1.getRango());
        assertEquals(3, w2.getRango());
        assertEquals(4, w3.getRango());
    }

    @Test
    void crearItem_armasDeZonaConfiguranPenetracionYEfectos() {
        // Act
        Weapon w7 = (Weapon) ItemGenerator.crearItem("W7");
        Weapon w8 = (Weapon) ItemGenerator.crearItem("W8");
        Weapon w9 = (Weapon) ItemGenerator.crearItem("W9");

        // Assert
        assertEquals(2, w7.getPenetracion());
        assertEquals(EffectType.BURN, w7.getEfectoEspecial());
        assertEquals(0.25, w7.getProbEfecto());

        assertEquals(5, w8.getPenetracion());
        assertEquals(33, w8.getDanoEfectivo(CharacterType.KAEL));
        assertEquals(27, w8.getDanoEfectivo(CharacterType.SYRA));

        assertEquals(4, w9.getPenetracion());
        assertEquals(EffectType.PARALYSIS, w9.getEfectoEspecial());
        assertEquals(0.30, w9.getProbEfecto());
    }

    @Test
    void crearItem_armasLegendariasConfiguranEfectosEspeciales() {
        // Act
        Weapon w10 = (Weapon) ItemGenerator.crearItem("W10");
        Weapon w11 = (Weapon) ItemGenerator.crearItem("W11");
        Weapon w12 = (Weapon) ItemGenerator.crearItem("W12");

        // Assert
        assertEquals(5, w10.getPenetracion());
        assertEquals(EffectType.BLIND, w10.getEfectoEspecial());
        assertEquals(0.20, w10.getProbEfecto());

        assertEquals(EffectType.SLOW, w11.getEfectoEspecial());
        assertEquals(EffectType.BLIND, w11.getEfectoEspecialSecundario());
        assertEquals(1.0, w11.getProbEfecto());
        assertEquals(1.0, w11.getProbEfectoSecundario());

        assertEquals(5, w12.getPenetracion());
        assertEquals(EffectType.PARALYSIS, w12.getEfectoEspecial());
        assertEquals(0.30, w12.getProbEfecto());
    }

    // -- Armaduras, pociones y accesorios -----------------------------------

    @Test
    void crearItem_armadurasConfiguranDefensaRanuraEInmunidad() {
        // Act
        Armor a3 = (Armor) ItemGenerator.crearItem("A3");
        Armor a8 = (Armor) ItemGenerator.crearItem("A8");

        // Assert
        assertEquals(ItemType.SHIELD, a3.getTipo());
        assertTrue(a3.isEscudo());
        assertEquals(5, a3.getDefensa());
        assertEquals(EffectType.SLOW, a3.getInmunidad());

        assertEquals(ItemType.ARMOR, a8.getTipo());
        assertFalse(a8.isEscudo());
        assertEquals(8, a8.getDefensa());
        assertEquals(EffectType.CURSE, a8.getInmunidad());
    }

    @Test
    void crearItem_pocionesConfiguranCuracionLimpiezaYBonus() {
        // Act
        Potion p4 = (Potion) ItemGenerator.crearItem("P4");
        Potion p5 = (Potion) ItemGenerator.crearItem("P5");

        // Assert
        assertEquals(0, p4.getCuracionHP());
        assertEquals(EffectType.CURSE, p4.getEfectoALimpiarPrimario());
        assertEquals(EffectType.BLIND, p4.getEfectoALimpiarSecundario());

        assertEquals(20, p5.getCuracionHP());
        assertEquals(5, p5.getBonusAtaqueTemporal());
    }

    @Test
    void crearItem_narrativosYAccesoriosConfiguranTiposYBonus() {
        // Act
        NarrativeItem ac1 = (NarrativeItem) ItemGenerator.crearItem("AC1");
        NarrativeItem n1 = (NarrativeItem) ItemGenerator.crearItem("N1");
        Accessory ac7 = (Accessory) ItemGenerator.crearItem("AC7");
        Accessory ac8 = (Accessory) ItemGenerator.crearItem("AC8");

        // Assert
        assertEquals(ItemType.NARRATIVE, ac1.getTipo());
        assertEquals(ItemType.NARRATIVE, n1.getTipo());
        assertTrue(n1.getDescripcion().contains("Malachar"));

        assertEquals(4, ac7.getBonusAtaque());
        assertEquals(0, ac7.getBonusDef());

        assertEquals(0, ac8.getBonusAtaque());
        assertEquals(3, ac8.getBonusDef());
    }

    // -- Pools de zona -------------------------------------------------------

    @Test
    void itemAleatorioZona_tiradaDeterministaEligeItemDelPool() {
        assertEquals("A1", ItemGenerator.itemAleatorioZona(1, 0.00).getId());
        assertEquals("P1", ItemGenerator.itemAleatorioZona(1, 0.34).getId());
        assertEquals("AC5", ItemGenerator.itemAleatorioZona(1, 0.70).getId());

        assertEquals("A3", ItemGenerator.itemAleatorioZona(2, 0.00).getId());
        assertEquals("P2", ItemGenerator.itemAleatorioZona(2, 0.34).getId());
        assertEquals("AC6", ItemGenerator.itemAleatorioZona(2, 0.70).getId());

        assertEquals("A5", ItemGenerator.itemAleatorioZona(3, 0.00).getId());
        assertEquals("P3", ItemGenerator.itemAleatorioZona(3, 0.34).getId());
        assertEquals("AC8", ItemGenerator.itemAleatorioZona(3, 0.70).getId());

        assertEquals("A7", ItemGenerator.itemAleatorioZona(4, 0.00).getId());
        assertEquals("P5", ItemGenerator.itemAleatorioZona(4, 0.34).getId());
        assertEquals("AC7", ItemGenerator.itemAleatorioZona(4, 0.70).getId());

        assertEquals("P3", ItemGenerator.itemAleatorioZona(5, 0.00).getId());
        assertEquals("P4", ItemGenerator.itemAleatorioZona(5, 0.34).getId());
        assertEquals("P5", ItemGenerator.itemAleatorioZona(5, 0.70).getId());
    }

    @Test
    void itemAleatorioZona_zonaInvalidaDevuelveNull() {
        assertNull(ItemGenerator.itemAleatorioZona(0, 0.0));
        assertNull(ItemGenerator.itemAleatorioZona(6, 0.0));
    }

    // -- Drops ---------------------------------------------------------------

    @Test
    void crearDropEnemigo_tiradaAltaSigueDevolviendoDropGarantizado() {
        assertEquals("P1", ItemGenerator.crearDropEnemigo(EnemyType.WARRIOR, 0.99, 0.0).getId());
        assertEquals("P2", ItemGenerator.crearDropEnemigo(EnemyType.GUARDIAN, 0.99, 0.0).getId());
        assertEquals("P3", ItemGenerator.crearDropEnemigo(EnemyType.SUMMONER, 0.99, 0.0).getId());
        assertNull(ItemGenerator.crearDropEnemigo(null, 0.0, 0.0));
    }

    @Test
    void crearDropEnemigo_guerrerosDevuelvenP1OA1() {
        assertEquals("P1", ItemGenerator.crearDropEnemigo(EnemyType.WARRIOR, 0.39, 0.0).getId());
        assertEquals("A1", ItemGenerator.crearDropEnemigo(EnemyType.BERSERKER, 0.39, 0.9).getId());
    }

    @Test
    void crearDropEnemigo_guardianDevuelveP2OA2() {
        assertEquals("P2", ItemGenerator.crearDropEnemigo(EnemyType.GUARDIAN, 0.49, 0.0).getId());
        assertEquals("A2", ItemGenerator.crearDropEnemigo(EnemyType.GUARDIAN, 0.49, 0.9).getId());
    }

    @Test
    void crearDropEnemigo_arquerosDevuelvenP1OAC5() {
        assertEquals("P1", ItemGenerator.crearDropEnemigo(EnemyType.ARCHER, 0.44, 0.0).getId());
        assertEquals("AC5", ItemGenerator.crearDropEnemigo(EnemyType.SNIPER, 0.44, 0.9).getId());
    }

    @Test
    void crearDropEnemigo_magosDevuelvenP2OAC6() {
        assertEquals("P2", ItemGenerator.crearDropEnemigo(EnemyType.DESTRUCTOR, 0.59, 0.0).getId());
        assertEquals("AC6", ItemGenerator.crearDropEnemigo(EnemyType.CONTROLLER, 0.59, 0.9).getId());
    }

    @Test
    void crearDropEnemigo_invocadorDevuelveP3OAC8() {
        assertEquals("P3", ItemGenerator.crearDropEnemigo(EnemyType.SUMMONER, 0.69, 0.0).getId());
        assertEquals("AC8", ItemGenerator.crearDropEnemigo(EnemyType.SUMMONER, 0.69, 0.9).getId());
    }

    @Test
    void crearDropEnemigo_enemigosNuevosTienenDropGarantizado() {
        assertEquals("P5", ItemGenerator.crearDropEnemigo(EnemyType.CONSTRUCTO, 0.99, 0.0).getId());
        assertEquals("A7", ItemGenerator.crearDropEnemigo(EnemyType.CONSTRUCTO, 0.99, 0.9).getId());

        assertEquals("P4", ItemGenerator.crearDropEnemigo(EnemyType.SOMBRA_ABSORBIDA, 0.99, 0.0).getId());
        assertEquals("P5", ItemGenerator.crearDropEnemigo(EnemyType.SOMBRA_ABSORBIDA, 0.99, 0.9).getId());

        assertEquals("P4", ItemGenerator.crearDropEnemigo(EnemyType.ECO_DE_MAGIA, 0.99, 0.0).getId());
        assertEquals("AC7", ItemGenerator.crearDropEnemigo(EnemyType.ECO_DE_MAGIA, 0.99, 0.9).getId());
    }
}
