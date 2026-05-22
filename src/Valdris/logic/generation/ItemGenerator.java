package Valdris.logic.generation;

import Valdris.model.enums.CharacterType;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.EnemyType;
import Valdris.model.items.Accessory;
import Valdris.model.items.Armor;
import Valdris.model.items.Item;
import Valdris.model.items.NarrativeItem;
import Valdris.model.items.Potion;
import Valdris.model.items.Weapon;

/**
 * Fábrica central de items del juego.
 *
 * <p>Cada llamada crea una instancia nueva para evitar compartir estado entre
 * inventario, cofres, drops y persistencia. Los IDs coinciden con la guía de
 * diseño v5: armas W1-W12, armaduras A1-A8, pociones P1-P5, accesorios
 * AC5-AC8 y objetos narrativos AC1-AC4/N1.</p>
 *
 * <p>Los métodos aleatorios tienen sobrecargas deterministas para tests. La
 * lógica de juego debe usar las firmas principales de la guía.</p>
 */
public final class ItemGenerator {

    // -- Constructor privado -------------------------------------------------

    /**
     * Evita instanciar la clase porque todos sus métodos son estáticos.
     */
    private ItemGenerator() {
    }

    // -- Creación por ID -----------------------------------------------------

    /**
     * Crea un item a partir de su identificador oficial.
     *
     * @param id identificador del item
     * @return nueva instancia del item, o null si el ID no existe
     */
    public static Item crearItem(String id) {
        if (id == null) {
            return null;
        }
        if ("W1".equals(id)) {
            return crearW1();
        }
        if ("W2".equals(id)) {
            return crearW2();
        }
        if ("W3".equals(id)) {
            return crearW3();
        }
        if ("W4".equals(id)) {
            return crearW4();
        }
        if ("W5".equals(id)) {
            return crearW5();
        }
        if ("W6".equals(id)) {
            return crearW6();
        }
        if ("W7".equals(id)) {
            return crearW7();
        }
        if ("W8".equals(id)) {
            return crearW8();
        }
        if ("W9".equals(id)) {
            return crearW9();
        }
        if ("W10".equals(id)) {
            return crearW10();
        }
        if ("W11".equals(id)) {
            return crearW11();
        }
        if ("W12".equals(id)) {
            return crearW12();
        }
        if ("A1".equals(id)) {
            return new Armor("A1", "Escudo de Madera", 4, true);
        }
        if ("A2".equals(id)) {
            return new Armor("A2", "Cota de Malla", 6, false);
        }
        if ("A3".equals(id)) {
            Armor armor = new Armor("A3", "Escudo de Raíces", 5, true);
            armor.setInmunidad(EffectType.SLOW);
            return armor;
        }
        if ("A4".equals(id)) {
            return new Armor("A4", "Armadura de Cuero Élfico", 7, false);
        }
        if ("A5".equals(id)) {
            return new Armor("A5", "Escudo de Piedra", 8, true);
        }
        if ("A6".equals(id)) {
            return new Armor("A6", "Coraza de Karath", 8, false);
        }
        if ("A7".equals(id)) {
            return new Armor("A7", "Manto Arcano", 9, false);
        }
        if ("A8".equals(id)) {
            Armor armor = new Armor("A8", "Manto de los Cinco Sellos", 8, false);
            armor.setInmunidad(EffectType.CURSE);
            return armor;
        }
        if ("P1".equals(id)) {
            return new Potion("P1", "Poción Pequeña", 25);
        }
        if ("P2".equals(id)) {
            return new Potion("P2", "Poción Mediana", 40);
        }
        if ("P3".equals(id)) {
            return new Potion("P3", "Poción Grande", 60);
        }
        if ("P4".equals(id)) {
            Potion potion = new Potion("P4", "Antídoto", 0);
            potion.setEfectosALimpiar(EffectType.CURSE, EffectType.BLIND);
            return potion;
        }
        if ("P5".equals(id)) {
            Potion potion = new Potion("P5", "Elixir de Combate", 20);
            potion.setBonusAtaqueTemporal(5);
            return potion;
        }
        if ("AC1".equals(id)) {
            return new NarrativeItem("AC1", "Llave de Hierro", "Abre puertas de la Zona 1");
        }
        if ("AC2".equals(id)) {
            return new NarrativeItem("AC2", "Semilla Resonante", "Resalta trampas cercanas");
        }
        if ("AC3".equals(id)) {
            return new NarrativeItem("AC3", "Fragmento de Sello", "Permite acceder a S5-SEC");
        }
        if ("AC4".equals(id)) {
            return new NarrativeItem("AC4", "Fragmento de Voluntad", "Potencia la habilidad especial");
        }
        if ("AC5".equals(id)) {
            Accessory accesorio = new Accessory("AC5", "Amuleto de Fuerza");
            accesorio.setBonus(4, 0, 0);
            return accesorio;
        }
        if ("AC6".equals(id)) {
            Accessory accesorio = new Accessory("AC6", "Anillo de Velocidad");
            accesorio.setBonus(0, 1, 0);
            return accesorio;
        }
        if ("AC7".equals(id)) {
            Accessory accesorio = new Accessory("AC7", "Amuleto Arcano");
            accesorio.setBonus(4, 0, 0);
            return accesorio;
        }
        if ("AC8".equals(id)) {
            Accessory accesorio = new Accessory("AC8", "Sello Roto");
            accesorio.setBonus(0, 0, 3);
            return accesorio;
        }
        if ("N1".equals(id)) {
            return new NarrativeItem("N1", "Pergamino Sellado",
                "Añade diálogo extra con Malachar en el Núcleo Profundo");
        }
        return null;
    }

    // -- Pools por zona ------------------------------------------------------

    /**
     * Devuelve un item aleatorio del pool de una zona.
     *
     * @param zona zona consultada, de 1 a 5
     * @return item del pool, o null si la zona no tiene pool
     */
    public static Item itemAleatorioZona(int zona) {
        return itemAleatorioZona(zona, Math.random());
    }

    /**
     * Devuelve un item del pool de zona usando una tirada determinista.
     *
     * @param zona zona consultada, de 1 a 5
     * @param tirada valor usado para elegir una de las tres opciones
     * @return item elegido, o null si la zona no tiene pool
     */
    public static Item itemAleatorioZona(int zona, double tirada) {
        int opcion = elegirIndice(tirada, 3);
        if (zona == 1) {
            return elegir(opcion, "A1", "P1", "AC5");
        }
        if (zona == 2) {
            return elegir(opcion, "A3", "P2", "AC6");
        }
        if (zona == 3) {
            return elegir(opcion, "A5", "P3", "AC8");
        }
        if (zona == 4) {
            return elegir(opcion, "A7", "P5", "AC7");
        }
        if (zona == 5) {
            return elegir(opcion, "P3", "P4", "P5");
        }
        return null;
    }

    // -- Drops ---------------------------------------------------------------

    /**
     * Crea un drop aleatorio para un tipo de enemigo normal.
     *
     * @param tipo tipo de enemigo derrotado
     * @return item generado, o null si no hay drop
     */
    public static Item crearDropEnemigo(EnemyType tipo) {
        return crearDropEnemigo(tipo, Math.random(), Math.random());
    }

    /**
     * Crea un drop usando tiradas deterministas.
     *
     * @param tipo tipo de enemigo derrotado
     * @param tiradaDrop tirada para determinar si hay drop
     * @param tiradaOpcion tirada para elegir entre los items posibles
     * @return item generado, o null si no hay drop
     */
    public static Item crearDropEnemigo(EnemyType tipo, double tiradaDrop, double tiradaOpcion) {
        if (tipo == null) {
            return null;
        }
        if (tipo == EnemyType.WARRIOR || tipo == EnemyType.BERSERKER) {
            return crearDropConProbabilidad(0.40, tiradaDrop, tiradaOpcion, "P1", "A1");
        }
        if (tipo == EnemyType.GUARDIAN) {
            return crearDropConProbabilidad(0.50, tiradaDrop, tiradaOpcion, "P2", "A2");
        }
        if (tipo == EnemyType.ARCHER || tipo == EnemyType.SNIPER) {
            return crearDropConProbabilidad(0.45, tiradaDrop, tiradaOpcion, "P1", "AC5");
        }
        if (tipo == EnemyType.DESTRUCTOR || tipo == EnemyType.CONTROLLER) {
            return crearDropConProbabilidad(0.60, tiradaDrop, tiradaOpcion, "P2", "AC6");
        }
        if (tipo == EnemyType.SUMMONER) {
            return crearDropConProbabilidad(0.70, tiradaDrop, tiradaOpcion, "P3", "AC8");
        }
        return null;
    }

    // -- Armas ---------------------------------------------------------------

    /**
     * Crea W1.
     *
     * @return Espada Oxidada
     */
    private static Weapon crearW1() {
        Weapon weapon = new Weapon("W1", "Espada Oxidada", 16, 0, 1);
        weapon.setAfinidad(CharacterType.KAEL, 4);
        return weapon;
    }

    /**
     * Crea W2.
     *
     * @return Arco de Madera
     */
    private static Weapon crearW2() {
        Weapon weapon = new Weapon("W2", "Arco de Madera", 10, 0, 3);
        weapon.setAfinidad(CharacterType.SYRA, 4);
        return weapon;
    }

    /**
     * Crea W3.
     *
     * @return Bastón Astillado
     */
    private static Weapon crearW3() {
        Weapon weapon = new Weapon("W3", "Bastón Astillado", 12, 0, 4);
        weapon.setAfinidad(CharacterType.DORATH, 4);
        return weapon;
    }

    /**
     * Crea W4.
     *
     * @return Espada Larga
     */
    private static Weapon crearW4() {
        Weapon weapon = new Weapon("W4", "Espada Larga", 18, 0, 1);
        weapon.setAfinidad(CharacterType.KAEL, 6);
        return weapon;
    }

    /**
     * Crea W5.
     *
     * @return Puñal del Errante
     */
    private static Weapon crearW5() {
        Weapon weapon = new Weapon("W5", "Puñal del Errante", 14, 0, 1);
        weapon.setAfinidad(CharacterType.SYRA, 5);
        return weapon;
    }

    /**
     * Crea W6.
     *
     * @return Arco Élfico
     */
    private static Weapon crearW6() {
        Weapon weapon = new Weapon("W6", "Arco Élfico", 16, 0, 4);
        weapon.setAfinidad(CharacterType.SYRA, 6);
        weapon.setEfectoEspecial(EffectType.SLOW, 1.0);
        return weapon;
    }

    /**
     * Crea W7.
     *
     * @return Tomo de Llamas
     */
    private static Weapon crearW7() {
        Weapon weapon = new Weapon("W7", "Tomo de Llamas", 15, 2, 4);
        weapon.setAfinidad(CharacterType.DORATH, 4);
        weapon.setEfectoEspecial(EffectType.BURN, 0.25);
        return weapon;
    }

    /**
     * Crea W8.
     *
     * @return Martillo de Mina
     */
    private static Weapon crearW8() {
        Weapon weapon = new Weapon("W8", "Martillo de Mina", 19, 5, 1);
        weapon.setAfinidad(CharacterType.KAEL, 4);
        weapon.setAfinidad(CharacterType.SYRA, -2);
        weapon.setAfinidad(CharacterType.DORATH, -2);
        return weapon;
    }

    /**
     * Crea W9.
     *
     * @return Bastón Arcano
     */
    private static Weapon crearW9() {
        Weapon weapon = new Weapon("W9", "Bastón Arcano", 16, 4, 4);
        weapon.setAfinidad(CharacterType.DORATH, 8);
        weapon.setEfectoEspecial(EffectType.PARALYSIS, 0.30);
        return weapon;
    }

    /**
     * Crea W10.
     *
     * @return Espada del Vacío
     */
    private static Weapon crearW10() {
        Weapon weapon = new Weapon("W10", "Espada del Vacío", 22, 5, 1);
        weapon.setAfinidad(CharacterType.KAEL, 6);
        weapon.setEfectoEspecial(EffectType.BLIND, 0.20);
        return weapon;
    }

    /**
     * Crea W11.
     *
     * @return Arco del Eclipse
     */
    private static Weapon crearW11() {
        Weapon weapon = new Weapon("W11", "Arco del Eclipse", 20, 0, 4);
        weapon.setAfinidad(CharacterType.SYRA, 6);
        weapon.setEfectoEspecial(EffectType.SLOW, 1.0);
        weapon.setEfectoEspecialSecundario(EffectType.BLIND, 1.0);
        return weapon;
    }

    /**
     * Crea W12.
     *
     * @return Grimorio Abismal
     */
    private static Weapon crearW12() {
        Weapon weapon = new Weapon("W12", "Grimorio Abismal", 19, 5, 4);
        weapon.setAfinidad(CharacterType.DORATH, 8);
        weapon.setEfectoEspecial(EffectType.PARALYSIS, 0.30);
        return weapon;
    }

    // -- Auxiliares ----------------------------------------------------------

    /**
     * Elige uno de tres IDs y crea el item correspondiente.
     *
     * @param opcion índice elegido
     * @param id0 primera opción
     * @param id1 segunda opción
     * @param id2 tercera opción
     * @return item creado
     */
    private static Item elegir(int opcion, String id0, String id1, String id2) {
        if (opcion == 0) {
            return crearItem(id0);
        }
        if (opcion == 1) {
            return crearItem(id1);
        }
        return crearItem(id2);
    }

    /**
     * Crea un drop si la tirada supera la probabilidad requerida.
     *
     * @param probabilidad probabilidad de obtener drop
     * @param tiradaDrop tirada para drop
     * @param tiradaOpcion tirada para opción
     * @param id0 primera opción de drop
     * @param id1 segunda opción de drop
     * @return item o null
     */
    private static Item crearDropConProbabilidad(double probabilidad, double tiradaDrop,
                                                 double tiradaOpcion, String id0, String id1) {
        if (normalizarTirada(tiradaDrop) >= probabilidad) {
            return null;
        }
        if (elegirIndice(tiradaOpcion, 2) == 0) {
            return crearItem(id0);
        }
        return crearItem(id1);
    }

    /**
     * Convierte una tirada en índice de selección.
     *
     * @param tirada valor de entrada
     * @param opciones número de opciones
     * @return índice entre 0 y opciones-1
     */
    private static int elegirIndice(double tirada, int opciones) {
        if (opciones <= 1) {
            return 0;
        }
        int indice = (int) (normalizarTirada(tirada) * opciones);
        if (indice >= opciones) {
            return opciones - 1;
        }
        return indice;
    }

    /**
     * Limita una tirada al rango [0.0, 0.999999].
     *
     * @param tirada valor recibido
     * @return tirada normalizada
     */
    private static double normalizarTirada(double tirada) {
        if (tirada < 0.0) {
            return 0.0;
        }
        if (tirada >= 1.0) {
            return 0.999999;
        }
        return tirada;
    }
}
