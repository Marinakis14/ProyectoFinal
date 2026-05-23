package Valdris.model.units;

import Valdris.model.enums.EnemyType;
import Valdris.model.enums.MiniBossType;

/**
 * Enemigo especial que representa un mini-boss de zona.
 *
 * <p>Los mini-bosses siguen siendo enemigos para combate, sala, IA y drops,
 * pero guardan un tipo narrativo propio. Esto permite añadir más adelante
 * habilidades concretas desde la capa de lógica sin convertir {@link Enemy} en
 * una colección de casos especiales.</p>
 */
public class MiniBossEnemy extends Enemy {

    // -- Atributos ------------------------------------------------------------

    /** Tipo narrativo de mini-boss. */
    private final MiniBossType tipoMiniBoss;

    /** Nombre visible del mini-boss. */
    private final String nombreNarrativo;

    /** Defensa del jugador ignorada al atacar. */
    private final int penetracionDefensa;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un mini-boss con las estadísticas oficiales acordadas.
     *
     * @param tipoMiniBoss tipo de mini-boss
     * @param fila fila inicial
     * @param col columna inicial
     * @param idSala sala de aparición
     */
    public MiniBossEnemy(MiniBossType tipoMiniBoss, int fila, int col, String idSala) {
        super(getTipoIA(tipoMiniBoss), getHpBase(tipoMiniBoss), getAtaqueBase(tipoMiniBoss),
            getDefensaBase(tipoMiniBoss), getMovBase(tipoMiniBoss), getRangoBase(tipoMiniBoss),
            fila, col, idSala);
        this.tipoMiniBoss = tipoMiniBoss;
        this.nombreNarrativo = getNombreNarrativo(tipoMiniBoss);
        this.penetracionDefensa = tipoMiniBoss == MiniBossType.EL_FILTRO ? 5 : 0;
        setMiniJefe(true);
    }

    // -- Getters --------------------------------------------------------------

    /**
     * Devuelve el tipo narrativo del mini-boss.
     *
     * @return tipo de mini-boss
     */
    public MiniBossType getTipoMiniBoss() {
        return tipoMiniBoss;
    }

    /**
     * Devuelve el nombre visible del mini-boss.
     *
     * @return nombre narrativo
     */
    public String getNombreNarrativo() {
        return nombreNarrativo;
    }

    /**
     * Devuelve cuánta defensa del jugador ignora este mini-boss.
     *
     * @return puntos de defensa ignorados
     */
    @Override
    public int getPenetracionDefensa() {
        return penetracionDefensa;
    }

    // -- Estadísticas ---------------------------------------------------------

    /**
     * Devuelve el tipo de IA base que reutiliza el mini-boss.
     *
     * @param tipo tipo de mini-boss
     * @return tipo de enemigo usado por la IA base
     */
    private static EnemyType getTipoIA(MiniBossType tipo) {
        if (tipo == MiniBossType.ESPIRITU_MADRE) {
            return EnemyType.ARCHER;
        }
        if (tipo == MiniBossType.EL_FILTRO) {
            return EnemyType.ECO_DE_MAGIA;
        }
        if (tipo == MiniBossType.GOLEM || tipo == MiniBossType.GUARDIAN_SIN_NOMBRE) {
            return EnemyType.GUARDIAN;
        }
        return EnemyType.WARRIOR;
    }

    /**
     * Devuelve el HP base del mini-boss.
     *
     * @param tipo tipo de mini-boss
     * @return HP máximo
     */
    private static int getHpBase(MiniBossType tipo) {
        if (tipo == MiniBossType.ESPIRITU_MADRE) {
            return 65;
        }
        if (tipo == MiniBossType.GOLEM) {
            return 90;
        }
        if (tipo == MiniBossType.GUARDIAN_SIN_NOMBRE) {
            return 80;
        }
        if (tipo == MiniBossType.EL_FILTRO) {
            return 70;
        }
        return 55;
    }

    /**
     * Devuelve el ataque base del mini-boss.
     *
     * @param tipo tipo de mini-boss
     * @return ataque base
     */
    private static int getAtaqueBase(MiniBossType tipo) {
        if (tipo == MiniBossType.ESPIRITU_MADRE) {
            return 16;
        }
        if (tipo == MiniBossType.GOLEM) {
            return 20;
        }
        if (tipo == MiniBossType.GUARDIAN_SIN_NOMBRE || tipo == MiniBossType.EL_FILTRO) {
            return 22;
        }
        return 18;
    }

    /**
     * Devuelve la defensa base del mini-boss.
     *
     * @param tipo tipo de mini-boss
     * @return defensa base
     */
    private static int getDefensaBase(MiniBossType tipo) {
        if (tipo == MiniBossType.GOLEM) {
            return 11;
        }
        if (tipo == MiniBossType.GUARDIAN_SIN_NOMBRE) {
            return 12;
        }
        if (tipo == MiniBossType.EL_FILTRO) {
            return 10;
        }
        return 8;
    }

    /**
     * Devuelve el movimiento base del mini-boss.
     *
     * @param tipo tipo de mini-boss
     * @return movimiento base
     */
    private static int getMovBase(MiniBossType tipo) {
        if (tipo == MiniBossType.GOLEM) {
            return 1;
        }
        return 2;
    }

    /**
     * Devuelve el rango base del mini-boss.
     *
     * @param tipo tipo de mini-boss
     * @return rango base
     */
    private static int getRangoBase(MiniBossType tipo) {
        if (tipo == MiniBossType.ESPIRITU_MADRE) {
            return 4;
        }
        if (tipo == MiniBossType.EL_FILTRO) {
            return 3;
        }
        return 1;
    }

    /**
     * Devuelve el nombre narrativo del mini-boss.
     *
     * @param tipo tipo de mini-boss
     * @return nombre visible
     */
    private static String getNombreNarrativo(MiniBossType tipo) {
        if (tipo == MiniBossType.ESPIRITU_MADRE) {
            return "Espíritu Madre";
        }
        if (tipo == MiniBossType.GOLEM) {
            return "Golem";
        }
        if (tipo == MiniBossType.GUARDIAN_SIN_NOMBRE) {
            return "Guardián Sin Nombre";
        }
        if (tipo == MiniBossType.EL_FILTRO) {
            return "El Filtro";
        }
        return "Alcalde Corrupto";
    }
}
