package Valdris.model.units;

import Valdris.model.enums.EnemyType;
import Valdris.exceptions.InvalidMoveException;
import Valdris.model.items.Item;
import Valdris.model.map.Room;

/**
 * Enemigo controlado por la IA durante la fase de enemigos.
 *
 * <p>Cada enemigo tiene un {@link EnemyType} que define sus estadísticas base y
 * orienta su comportamiento táctico. La ejecución concreta de la IA se hará en
 * la capa de lógica, pero esta clase conserva los datos necesarios para tomar
 * decisiones: posición, sala de origen, cooldowns y drop al morir.</p>
 *
 * <p>Los Guardianes usan la posición de aparición para defender una zona fija.
 * Los Francotiradores e Invocadores usan {@code turnosSinActuar} como contador
 * de cooldown. El resto de enemigos puede ignorar ese contador.</p>
 *
 * @see Unit
 * @see EnemyType
 */
public class Enemy extends Unit implements Comparable<Enemy> {

    // -- Atributos ------------------------------------------------------------

    /** Tipo de enemigo, usado para estadísticas, IA, drops y persistencia. */
    private final EnemyType tipo;

    /** Item que el enemigo deja al morir, o null si no tiene drop. */
    private Item dropItem;

    /** Contador de turnos usado por habilidades con cooldown. */
    private int turnosSinActuar;

    /** ID de la sala donde fue generado el enemigo. */
    private final String idSala;

    /** Fila original de aparición, importante para Guardian. */
    private final int filaSpawn;

    /** Columna original de aparición, importante para Guardian. */
    private final int colSpawn;

    /** Indica si el enemigo representa un mini-jefe con tratamiento especial. */
    private boolean esMiniJefe;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea un enemigo con estadísticas base según su tipo.
     *
     * @param tipo tipo de enemigo
     * @param fila fila inicial dentro de la sala
     * @param col columna inicial dentro de la sala
     * @param idSala identificador de la sala donde aparece
     */
    public Enemy(EnemyType tipo, int fila, int col, String idSala) {
        super(getHpBase(tipo), getAtaqueBase(tipo), getDefensaBase(tipo),
            getMovBase(tipo), getRangoBase(tipo), fila, col);
        this.tipo = tipo;
        this.dropItem = null;
        this.turnosSinActuar = 0;
        this.idSala = idSala;
        this.filaSpawn = fila;
        this.colSpawn = col;
        this.esMiniJefe = false;
    }

    /**
     * Crea un enemigo con estadísticas explícitas.
     *
     * <p>Este constructor queda protegido para subtipos especiales, como
     * mini-bosses, que comparten la identidad de enemigo pero no usan las
     * estadísticas base de {@link EnemyType}.</p>
     *
     * @param tipo tipo de IA base usado por el enemigo
     * @param hpMax HP máximo
     * @param ataqueBase ataque base
     * @param defensaBase defensa base
     * @param movBase movimiento base
     * @param rango rango base
     * @param fila fila inicial
     * @param col columna inicial
     * @param idSala sala de aparición
     */
    protected Enemy(EnemyType tipo, int hpMax, int ataqueBase, int defensaBase,
                    int movBase, int rango, int fila, int col, String idSala) {
        super(hpMax, ataqueBase, defensaBase, movBase, rango, fila, col);
        this.tipo = tipo;
        this.dropItem = null;
        this.turnosSinActuar = 0;
        this.idSala = idSala;
        this.filaSpawn = fila;
        this.colSpawn = col;
        this.esMiniJefe = false;
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Resuelve el drop del enemigo al morir.
     *
     * <p>Si el enemigo tiene un item asignado como drop, se coloca en la celda
     * actual de la sala. La limpieza de la unidad en la celda y de la lista de
     * enemigos vivos corresponde a {@code Room.removeEnemigo(Enemy)}.</p>
     *
     * @param room sala donde muere el enemigo
     */
    public void onDeath(Room room) {
        if (room == null || dropItem == null) {
            return;
        }
        try {
            room.getCell(getFilaActual(), getColActual()).setItem(dropItem);
        } catch (InvalidMoveException e) {
            // La posición del enemigo debería pertenecer a su sala; si no, no se coloca drop.
        }
    }

    /**
     * Incrementa el contador de cooldown del enemigo.
     */
    public void incrementarCooldown() {
        turnosSinActuar++;
    }

    /**
     * Reinicia el contador de cooldown del enemigo.
     */
    public void resetCooldown() {
        turnosSinActuar = 0;
    }

    /**
     * Indica si el contador de cooldown ha llegado al umbral indicado.
     *
     * @param n turnos necesarios para que la habilidad esté lista
     * @return true si {@code turnosSinActuar >= n}
     */
    public boolean isCooldownListo(int n) {
        return turnosSinActuar >= n;
    }

    /**
     * Devuelve el daño base del enemigo.
     *
     * <p>Se ofrece este alias porque algunas reglas de combate, como el AOE del
     * Destructor, hablan de daño base directo en lugar de ataque total.</p>
     *
     * @return ataque base de la unidad
     */
    public int getDanoBase() {
        return getAtaqueBase();
    }

    /**
     * Devuelve cuánta defensa del jugador ignora este enemigo al atacar.
     *
     * <p>Solo algunos enemigos avanzados usan penetración natural. El Eco de
     * Magia ignora 3 puntos de defensa; el resto de enemigos normales no ignora
     * defensa salvo que una subclase sobrescriba este método.</p>
     *
     * @return puntos de defensa ignorados
     */
    public int getPenetracionDefensa() {
        if (tipo == EnemyType.ECO_DE_MAGIA) {
            return 3;
        }
        return 0;
    }

    // -- Getters y setters ----------------------------------------------------

    /**
     * Devuelve el tipo de enemigo.
     *
     * @return tipo de enemigo
     */
    public EnemyType getTipo() {
        return tipo;
    }

    /**
     * Devuelve el item que soltará al morir.
     *
     * @return item de drop, o null si no tiene
     */
    public Item getDropItem() {
        return dropItem;
    }

    /**
     * Configura el item que soltará al morir.
     *
     * @param dropItem item de drop
     */
    public void setDropItem(Item dropItem) {
        this.dropItem = dropItem;
    }

    /**
     * Devuelve los turnos acumulados para cooldown.
     *
     * @return turnos sin actuar o desde la última habilidad
     */
    public int getTurnosSinActuar() {
        return turnosSinActuar;
    }

    /**
     * Ajusta el contador de cooldown.
     *
     * @param turnosSinActuar nuevo valor del contador
     */
    public void setTurnosSinActuar(int turnosSinActuar) {
        if (turnosSinActuar < 0) {
            this.turnosSinActuar = 0;
        } else {
            this.turnosSinActuar = turnosSinActuar;
        }
    }

    /**
     * Devuelve el ID de la sala donde fue generado.
     *
     * @return identificador de sala
     */
    public String getIdSala() {
        return idSala;
    }

    /**
     * Devuelve la fila original de aparición.
     *
     * @return fila de spawn
     */
    public int getFilaSpawn() {
        return filaSpawn;
    }

    /**
     * Devuelve la columna original de aparición.
     *
     * @return columna de spawn
     */
    public int getColSpawn() {
        return colSpawn;
    }

    /**
     * Indica si este enemigo es mini-jefe.
     *
     * @return true si es mini-jefe
     */
    public boolean isMiniJefe() {
        return esMiniJefe;
    }

    /**
     * Configura si este enemigo debe tratarse como mini-jefe.
     *
     * @param esMiniJefe true si es mini-jefe
     */
    public void setMiniJefe(boolean esMiniJefe) {
        this.esMiniJefe = esMiniJefe;
    }

    // -- Métodos auxiliares ---------------------------------------------------

    /**
     * Devuelve el HP base asociado al tipo de enemigo.
     *
     * @param tipo tipo de enemigo
     * @return HP inicial
     */
    private static int getHpBase(EnemyType tipo) {
        if (tipo == EnemyType.BERSERKER) {
            return 25;
        }
        if (tipo == EnemyType.GUARDIAN) {
            return 50;
        }
        if (tipo == EnemyType.ARCHER || tipo == EnemyType.SNIPER) {
            return 28;
        }
        if (tipo == EnemyType.DESTRUCTOR) {
            return 40;
        }
        if (tipo == EnemyType.CONTROLLER) {
            return 35;
        }
        if (tipo == EnemyType.SUMMONER) {
            return 45;
        }
        if (tipo == EnemyType.CONSTRUCTO || tipo == EnemyType.SOMBRA_ABSORBIDA) {
            return 45;
        }
        if (tipo == EnemyType.ECO_DE_MAGIA) {
            return 35;
        }
        return 35;
    }

    /**
     * Devuelve el ataque base asociado al tipo de enemigo.
     *
     * @param tipo tipo de enemigo
     * @return ataque base
     */
    private static int getAtaqueBase(EnemyType tipo) {
        if (tipo == EnemyType.BERSERKER) {
            return 13;
        }
        if (tipo == EnemyType.GUARDIAN) {
            return 12;
        }
        if (tipo == EnemyType.ARCHER) {
            return 10;
        }
        if (tipo == EnemyType.SNIPER) {
            return 18;
        }
        if (tipo == EnemyType.DESTRUCTOR) {
            return 6;
        }
        if (tipo == EnemyType.CONTROLLER) {
            return 4;
        }
        if (tipo == EnemyType.SUMMONER) {
            return 0;
        }
        if (tipo == EnemyType.CONSTRUCTO) {
            return 17;
        }
        if (tipo == EnemyType.SOMBRA_ABSORBIDA) {
            return 20;
        }
        if (tipo == EnemyType.ECO_DE_MAGIA) {
            return 22;
        }
        return 12;
    }

    /**
     * Devuelve la defensa base asociada al tipo de enemigo.
     *
     * @param tipo tipo de enemigo
     * @return defensa base
     */
    private static int getDefensaBase(EnemyType tipo) {
        if (tipo == EnemyType.BERSERKER || tipo == EnemyType.SNIPER) {
            return 3;
        }
        if (tipo == EnemyType.GUARDIAN) {
            return 15;
        }
        if (tipo == EnemyType.ARCHER || tipo == EnemyType.CONTROLLER) {
            return 4;
        }
        if (tipo == EnemyType.DESTRUCTOR) {
            return 5;
        }
        if (tipo == EnemyType.SUMMONER) {
            return 6;
        }
        if (tipo == EnemyType.CONSTRUCTO) {
            return 10;
        }
        if (tipo == EnemyType.SOMBRA_ABSORBIDA) {
            return 8;
        }
        if (tipo == EnemyType.ECO_DE_MAGIA) {
            return 5;
        }
        return 5;
    }

    /**
     * Devuelve el movimiento base asociado al tipo de enemigo.
     *
     * @param tipo tipo de enemigo
     * @return movimiento base
     */
    private static int getMovBase(EnemyType tipo) {
        if (tipo == EnemyType.BERSERKER) {
            return 4;
        }
        if (tipo == EnemyType.GUARDIAN) {
            return 1;
        }
        if (tipo == EnemyType.ARCHER) {
            return 3;
        }
        if (tipo == EnemyType.SNIPER) {
            return 2;
        }
        if (tipo == EnemyType.DESTRUCTOR) {
            return 0;
        }
        if (tipo == EnemyType.CONTROLLER || tipo == EnemyType.SUMMONER) {
            return 2;
        }
        if (tipo == EnemyType.CONSTRUCTO || tipo == EnemyType.SOMBRA_ABSORBIDA
            || tipo == EnemyType.ECO_DE_MAGIA) {
            return 2;
        }
        return 2;
    }

    /**
     * Devuelve el rango base asociado al tipo de enemigo.
     *
     * @param tipo tipo de enemigo
     * @return rango base
     */
    private static int getRangoBase(EnemyType tipo) {
        if (tipo == EnemyType.ARCHER) {
            return 4;
        }
        if (tipo == EnemyType.SNIPER || tipo == EnemyType.DESTRUCTOR) {
            return 5;
        }
        if (tipo == EnemyType.CONTROLLER) {
            return 3;
        }
        if (tipo == EnemyType.SUMMONER) {
            return 0;
        }
        if (tipo == EnemyType.ECO_DE_MAGIA) {
            return 3;
        }
        return 1;
    }

    // -- Comparación ----------------------------------------------------------

    /**
     * Compara enemigos por sala, posición y tipo.
     *
     * <p>Una sala no debería contener dos enemigos en la misma celda. Esta
     * comparación permite que la lista enlazada propia localice y elimine
     * enemigos de forma estable.</p>
     *
     * @param other enemigo con el que se compara
     * @return resultado de comparar sala, posición y tipo
     */
    @Override
    public int compareTo(Enemy other) {
        if (other == null) {
            return 1;
        }
        int resultado = idSala.compareTo(other.idSala);
        if (resultado != 0) {
            return resultado;
        }
        if (getFilaActual() != other.getFilaActual()) {
            return getFilaActual() - other.getFilaActual();
        }
        if (getColActual() != other.getColActual()) {
            return getColActual() - other.getColActual();
        }
        return tipo.compareTo(other.tipo);
    }
}
