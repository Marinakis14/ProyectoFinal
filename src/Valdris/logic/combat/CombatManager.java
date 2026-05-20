package Valdris.logic.combat;

import Valdris.exceptions.InvalidAttackException;
import Valdris.logic.vision.LineaDeVision;
import Valdris.model.effects.Effect;
import Valdris.model.enums.EffectType;
import Valdris.model.items.Weapon;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.Player;
import Valdris.model.units.Unit;

/**
 * Gestiona las reglas centrales de combate de Valdris.
 *
 * <p>Esta clase concentra el calculo de danio, la resolucion de ataques del
 * jugador y de enemigos, el ataque en area del Destructor y la validacion de
 * rango. Las unidades siguen siendo responsables de almacenar HP, posicion,
 * equipo y efectos; CombatManager solo orquesta esas piezas.</p>
 *
 * <p>El metodo oficial de danio usa el factor aleatorio de la guia, en el rango
 * [0.5, 1.5]. La sobrecarga con multiplicador fijo existe para pruebas
 * deterministas y no sustituye la tirada aleatoria de juego.</p>
 */
public final class CombatManager {

    // -- Constantes -----------------------------------------------------------

    /** Danio aplicado por efectos de arma cuando no se especifica otra duracion. */
    private static final int DURACION_EFECTO_ARMA = 1;

    /** Radio del ataque en area del Destructor. */
    private static final int RADIO_DESTRUCTOR = 2;

    // -- Constructor privado -------------------------------------------------

    /**
     * Evita instanciar la clase porque todos sus metodos son estaticos.
     */
    private CombatManager() {
    }

    // -- Calculo de danio -----------------------------------------------------

    /**
     * Calcula el danio final usando la formula oficial con aleatoriedad.
     *
     * @param atacante unidad que ataca
     * @param defensor unidad que recibe el ataque
     * @return danio final, nunca negativo
     * @throws InvalidAttackException si atacante o defensor son null
     */
    public static int calcularDanio(Unit atacante, Unit defensor) throws InvalidAttackException {
        double multiplicador = Math.random() * 1.0 + 0.5;
        return calcularDanio(atacante, defensor, multiplicador);
    }

    /**
     * Calcula el danio final usando un multiplicador fijo.
     *
     * <p>Se usa para tests y para cualquier calculo determinista que necesite
     * comprobar la formula sin depender de {@code Math.random()}.</p>
     *
     * @param atacante unidad que ataca
     * @param defensor unidad que recibe el ataque
     * @param multiplicador factor aplicado al ataque total
     * @return danio final, nunca negativo
     * @throws InvalidAttackException si atacante o defensor son null
     */
    public static int calcularDanio(Unit atacante, Unit defensor, double multiplicador)
        throws InvalidAttackException {

        validarUnidades(atacante, defensor);
        int penetracion = getPenetracion(atacante);
        int defensaEfectiva = Math.max(0, defensor.getDefensaTotal() - penetracion);
        int danio = (int) (atacante.getAtaqueTotal() * multiplicador) - defensaEfectiva;
        return Math.max(0, danio);
    }

    // -- Resolucion de ataques ------------------------------------------------

    /**
     * Resuelve un ataque del jugador contra un enemigo sin contexto de sala.
     *
     * <p>Esta firma conserva la forma descrita en la guia. Si el enemigo muere,
     * no se puede colocar drop porque no se ha recibido una sala; para eso debe
     * usarse {@link #resolverAtaqueJugador(Player, Enemy, Room)}.</p>
     *
     * @param jugador jugador atacante
     * @param enemigo enemigo defensor
     * @return danio infligido
     * @throws InvalidAttackException si el ataque no es valido
     */
    public static int resolverAtaqueJugador(Player jugador, Enemy enemigo)
        throws InvalidAttackException {

        return resolverAtaqueJugador(jugador, enemigo, null);
    }

    /**
     * Resuelve un ataque del jugador contra un enemigo con contexto de sala.
     *
     * <p>Si el jugador tiene arma con efecto especial y la tirada del arma se
     * activa, el efecto se aplica al enemigo. Si el enemigo muere, se resuelve
     * su drop en la sala recibida.</p>
     *
     * @param jugador jugador atacante
     * @param enemigo enemigo defensor
     * @param room sala donde ocurre el ataque, o null si solo se calcula danio
     * @return danio infligido
     * @throws InvalidAttackException si el ataque no es valido
     */
    public static int resolverAtaqueJugador(Player jugador, Enemy enemigo, Room room)
        throws InvalidAttackException {

        validarUnidades(jugador, enemigo);
        validarRango(jugador, enemigo, room);

        int danio = calcularDanio(jugador, enemigo);
        enemigo.recibirDanio(danio);
        aplicarEfectoDeArma(jugador, enemigo);

        if (!enemigo.isVivo()) {
            enemigo.onDeath(room);
        }
        return danio;
    }

    /**
     * Resuelve un ataque normal de enemigo contra el jugador.
     *
     * <p>Si el jugador tiene CURSE activo, se suman 3 puntos al danio final,
     * segun la regla de la guia.</p>
     *
     * @param enemigo enemigo atacante
     * @param jugador jugador defensor
     * @return danio infligido
     * @throws InvalidAttackException si el ataque no es valido
     */
    public static int resolverAtaqueEnemigo(Enemy enemigo, Player jugador)
        throws InvalidAttackException {

        validarUnidades(enemigo, jugador);
        int danio = calcularDanio(enemigo, jugador);
        if (jugador.tieneEfecto(EffectType.CURSE)) {
            danio += 3;
        }
        jugador.recibirDanio(danio);
        return danio;
    }

    /**
     * Resuelve el ataque en area del Destructor.
     *
     * <p>El Destructor inflige su danio base directamente si el jugador esta a
     * distancia Manhattan menor o igual que 2. No se usa formula aleatoria ni
     * defensa.</p>
     *
     * @param destructor enemigo de tipo Destructor
     * @param room sala donde ocurre el ataque
     * @param jugador jugador potencialmente afectado
     * @return danio total aplicado al jugador
     * @throws InvalidAttackException si faltan datos de combate
     */
    public static int resolverAOEDestructor(Enemy destructor, Room room, Player jugador)
        throws InvalidAttackException {

        if (destructor == null || room == null || jugador == null) {
            throw new InvalidAttackException("No se puede resolver AOE sin destructor, sala y jugador.");
        }
        int distancia = distanciaManhattan(destructor, jugador);
        if (distancia > RADIO_DESTRUCTOR) {
            return 0;
        }
        int danio = destructor.getDanoBase();
        jugador.recibirDanio(danio);
        return danio;
    }

    // -- Rango ----------------------------------------------------------------

    /**
     * Comprueba si un defensor esta dentro del rango numerico del atacante.
     *
     * @param atacante unidad atacante
     * @param defensor unidad defensora
     * @return true si la distancia Manhattan esta dentro del rango efectivo
     */
    public static boolean estaEnRango(Unit atacante, Unit defensor) {
        if (atacante == null || defensor == null) {
            return false;
        }
        return distanciaManhattan(atacante, defensor) <= atacante.getRangoEfectivo();
    }

    /**
     * Comprueba si un defensor esta en rango y, para ataques de alcance, con
     * linea de vision valida.
     *
     * <p>Los ataques de rango 1 solo usan distancia Manhattan. Para rango mayor
     * que 1 se exige vision si se recibe una sala.</p>
     *
     * @param atacante unidad atacante
     * @param defensor unidad defensora
     * @param room sala donde ocurre el ataque
     * @return true si el ataque tiene rango suficiente y vision cuando aplica
     */
    public static boolean estaEnRango(Unit atacante, Unit defensor, Room room) {
        if (!estaEnRango(atacante, defensor)) {
            return false;
        }
        if (atacante.getRangoEfectivo() <= 1 || room == null) {
            return true;
        }
        return LineaDeVision.tieneVision(room, atacante.getFilaActual(), atacante.getColActual(),
            defensor.getFilaActual(), defensor.getColActual());
    }

    // -- Metodos auxiliares ---------------------------------------------------

    /**
     * Valida que atacante y defensor existan.
     *
     * @param atacante unidad atacante
     * @param defensor unidad defensora
     * @throws InvalidAttackException si falta alguna unidad
     */
    private static void validarUnidades(Unit atacante, Unit defensor) throws InvalidAttackException {
        if (atacante == null || defensor == null) {
            throw new InvalidAttackException("Atacante y defensor son obligatorios.");
        }
    }

    /**
     * Valida rango y linea de vision antes de resolver un ataque real.
     *
     * @param atacante unidad atacante
     * @param defensor unidad defensora
     * @param room sala donde ocurre el ataque
     * @throws InvalidAttackException si el defensor no esta en rango
     */
    private static void validarRango(Unit atacante, Unit defensor, Room room)
        throws InvalidAttackException {

        if (!estaEnRango(atacante, defensor, room)) {
            throw new InvalidAttackException("El objetivo esta fuera de rango o sin linea de vision.");
        }
    }

    /**
     * Devuelve la penetracion del arma del atacante si es jugador.
     *
     * @param atacante unidad atacante
     * @return penetracion aplicable al ataque
     */
    private static int getPenetracion(Unit atacante) {
        if (atacante instanceof Player) {
            Weapon arma = ((Player) atacante).getArmaEquipada();
            if (arma != null) {
                return arma.getPenetracion();
            }
        }
        return 0;
    }

    /**
     * Aplica el efecto especial del arma del jugador si se activa.
     *
     * @param jugador jugador atacante
     * @param enemigo enemigo afectado
     */
    private static void aplicarEfectoDeArma(Player jugador, Enemy enemigo) {
        Weapon arma = jugador.getArmaEquipada();
        if (arma == null) {
            return;
        }
        EffectType efecto = arma.tryAplicarEfecto();
        if (efecto != null) {
            enemigo.addEfecto(new Effect(efecto, DURACION_EFECTO_ARMA));
        }
    }

    /**
     * Calcula distancia Manhattan entre dos unidades.
     *
     * @param a primera unidad
     * @param b segunda unidad
     * @return distancia ortogonal entre posiciones
     */
    private static int distanciaManhattan(Unit a, Unit b) {
        return Math.abs(a.getFilaActual() - b.getFilaActual())
            + Math.abs(a.getColActual() - b.getColActual());
    }
}
