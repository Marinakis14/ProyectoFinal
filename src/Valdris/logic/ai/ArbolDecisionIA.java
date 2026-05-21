package Valdris.logic.ai;

import Valdris.logic.combat.CombatManager;
import Valdris.model.enums.EnemyType;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.Player;

/**
 * Árbol de decisión que selecciona la acción táctica de un enemigo.
 *
 * <p>La clase pertenece a la capa de lógica y solo decide qué debería hacer la
 * IA. No mueve unidades, no inflige daño y no modifica la sala. Esa ejecución
 * concreta queda para {@code IAEnemigo}, que usará la acción devuelta por este
 * árbol.</p>
 *
 * <p>Cada instancia construye un árbol binario según el tipo de enemigo. Los
 * nodos internos evalúan condiciones simples y las hojas contienen una acción
 * final. Se mantiene así una estructura explícita de árbol, útil para explicar
 * y testear la decisión sin ocultarla detrás de atajos.</p>
 */
public class ArbolDecisionIA {

    // -- Constantes -----------------------------------------------------------

    /** Radio fijo que defiende el Guardian desde su celda de aparición. */
    private static final int RADIO_GUARDIAN = 3;

    /** Radio del AOE del Destructor, coherente con CombatManager. */
    private static final int RADIO_DESTRUCTOR = 2;

    /** Turnos necesarios para que el Invocador pueda invocar. */
    private static final int COOLDOWN_INVOCADOR = 2;

    // -- Atributos ------------------------------------------------------------

    /** Raíz del árbol de decisión asociado al tipo de enemigo. */
    private final NodoArbol raiz;

    // -- Constructor ----------------------------------------------------------

    /**
     * Crea el árbol de decisión para un tipo concreto de enemigo.
     *
     * @param tipo tipo de enemigo que usará este árbol
     */
    public ArbolDecisionIA(EnemyType tipo) {
        this.raiz = construirArbol(tipo);
    }

    // -- Métodos de lógica ----------------------------------------------------

    /**
     * Recorre el árbol desde la raíz hasta alcanzar una hoja de acción.
     *
     * @param enemy enemigo que decide su acción
     * @param room sala donde está el enemigo
     * @param player jugador objetivo
     * @return acción elegida por el árbol
     */
    public AccionIA decidirAccion(Enemy enemy, Room room, Player player) {
        if (enemy == null || room == null || player == null) {
            return AccionIA.ESPERAR;
        }

        NodoArbol actual = raiz;
        while (actual != null && !actual.isHoja()) {
            if (actual.evaluar(enemy, room, player)) {
                actual = actual.getSiTrue();
            } else {
                actual = actual.getSiFalse();
            }
        }

        if (actual == null || actual.getAccion() == null) {
            return AccionIA.ESPERAR;
        }
        return actual.getAccion();
    }

    // -- Construcción del árbol ----------------------------------------------

    /**
     * Construye el árbol correspondiente a un tipo de enemigo.
     *
     * @param tipo tipo de enemigo
     * @return raíz del árbol creado
     */
    private NodoArbol construirArbol(EnemyType tipo) {
        if (tipo == EnemyType.GUARDIAN) {
            return nodo(new CondicionZonaGuardian(), construirArbolWarrior(), hoja(AccionIA.ESPERAR));
        }
        if (tipo == EnemyType.ARCHER || tipo == EnemyType.SNIPER) {
            return construirArbolArquero();
        }
        if (tipo == EnemyType.DESTRUCTOR) {
            return nodo(new CondicionRadioDestructor(), hoja(AccionIA.AOE), hoja(AccionIA.ESPERAR));
        }
        if (tipo == EnemyType.CONTROLLER) {
            return construirArbolController();
        }
        if (tipo == EnemyType.SUMMONER) {
            return construirArbolInvocador();
        }
        return construirArbolWarrior();
    }

    /**
     * Construye el comportamiento base de enemigos cuerpo a cuerpo.
     *
     * @return raíz del árbol de Warrior/Berserker
     */
    private NodoArbol construirArbolWarrior() {
        return nodo(new CondicionPuedeAtacar(), hoja(AccionIA.ATACAR),
            nodo(new CondicionPuedeMoverse(), hoja(AccionIA.MOVER), hoja(AccionIA.ESPERAR)));
    }

    /**
     * Construye el comportamiento de Archer y Sniper.
     *
     * @return raíz del árbol de enemigos a distancia
     */
    private NodoArbol construirArbolArquero() {
        return nodo(new CondicionPuedeAtacar(), hoja(AccionIA.ATACAR),
            nodo(new CondicionPuedeMoverse(), hoja(AccionIA.MOVER_A_ZONA), hoja(AccionIA.ESPERAR)));
    }

    /**
     * Construye el comportamiento del Controller.
     *
     * @return raíz del árbol del Controller
     */
    private NodoArbol construirArbolController() {
        return nodo(new CondicionPuedeAtacar(), hoja(AccionIA.APLICAR_EFECTO),
            nodo(new CondicionPuedeMoverse(), hoja(AccionIA.MOVER), hoja(AccionIA.ESPERAR)));
    }

    /**
     * Construye el comportamiento del Summoner.
     *
     * @return raíz del árbol del Invocador
     */
    private NodoArbol construirArbolInvocador() {
        return nodo(new CondicionCooldownInvocador(), hoja(AccionIA.INVOCAR),
            nodo(new CondicionPuedeMoverse(), hoja(AccionIA.MOVER), hoja(AccionIA.ESPERAR)));
    }

    /**
     * Crea una hoja de acción.
     *
     * @param accion acción de la hoja
     * @return nodo hoja
     */
    private NodoArbol hoja(AccionIA accion) {
        return new NodoArbol(null, null, null, accion);
    }

    /**
     * Crea un nodo interno de condición.
     *
     * @param condicion condición evaluada
     * @param siTrue rama tomada cuando la condición se cumple
     * @param siFalse rama tomada cuando la condición no se cumple
     * @return nodo interno
     */
    private NodoArbol nodo(CondicionIA condicion, NodoArbol siTrue, NodoArbol siFalse) {
        return new NodoArbol(condicion, siTrue, siFalse, null);
    }

    // -- Condiciones ----------------------------------------------------------

    /**
     * Contrato interno para condiciones del árbol.
     */
    private interface CondicionIA {

        /**
         * Evalúa una condición con el estado táctico actual.
         *
         * @param enemy enemigo que decide
         * @param room sala actual
         * @param player jugador objetivo
         * @return true si la condición se cumple
         */
        boolean evaluar(Enemy enemy, Room room, Player player);
    }

    /**
     * Condición que comprueba si el enemigo puede atacar al jugador.
     */
    private static final class CondicionPuedeAtacar implements CondicionIA {

        /**
         * Evalúa rango y línea de visión cuando corresponde.
         *
         * @param enemy enemigo que decide
         * @param room sala actual
         * @param player jugador objetivo
         * @return true si el jugador está en rango válido
         */
        @Override
        public boolean evaluar(Enemy enemy, Room room, Player player) {
            return CombatManager.estaEnRango(enemy, player, room);
        }
    }

    /**
     * Condición que comprueba si el enemigo tiene puntos de movimiento.
     */
    private static final class CondicionPuedeMoverse implements CondicionIA {

        /**
         * Evalúa si el enemigo tiene movimiento efectivo positivo.
         *
         * @param enemy enemigo que decide
         * @param room sala actual
         * @param player jugador objetivo
         * @return true si puede intentar moverse
         */
        @Override
        public boolean evaluar(Enemy enemy, Room room, Player player) {
            return enemy.getMovEfectivo() > 0;
        }
    }

    /**
     * Condición que limita al Guardian a su zona fija de defensa.
     */
    private static final class CondicionZonaGuardian implements CondicionIA {

        /**
         * Usa distancia Manhattan desde el punto de aparición del Guardian.
         *
         * @param enemy enemigo Guardian
         * @param room sala actual
         * @param player jugador objetivo
         * @return true si el jugador está en radio 3 del spawn
         */
        @Override
        public boolean evaluar(Enemy enemy, Room room, Player player) {
            int distancia = Math.abs(enemy.getFilaSpawn() - player.getFilaActual())
                + Math.abs(enemy.getColSpawn() - player.getColActual());
            return distancia <= RADIO_GUARDIAN;
        }
    }

    /**
     * Condición que comprueba el radio del AOE del Destructor.
     */
    private static final class CondicionRadioDestructor implements CondicionIA {

        /**
         * Usa distancia Manhattan, igual que el AOE actual de CombatManager.
         *
         * @param enemy enemigo Destructor
         * @param room sala actual
         * @param player jugador objetivo
         * @return true si el jugador está a distancia 2 o menos
         */
        @Override
        public boolean evaluar(Enemy enemy, Room room, Player player) {
            int distancia = Math.abs(enemy.getFilaActual() - player.getFilaActual())
                + Math.abs(enemy.getColActual() - player.getColActual());
            return distancia <= RADIO_DESTRUCTOR;
        }
    }

    /**
     * Condición que comprueba si el Invocador tiene lista su habilidad.
     */
    private static final class CondicionCooldownInvocador implements CondicionIA {

        /**
         * Evalúa el contador de cooldown del Invocador.
         *
         * @param enemy enemigo Invocador
         * @param room sala actual
         * @param player jugador objetivo
         * @return true si puede invocar
         */
        @Override
        public boolean evaluar(Enemy enemy, Room room, Player player) {
            return enemy.isCooldownListo(COOLDOWN_INVOCADOR);
        }
    }

    // -- Nodo interno ---------------------------------------------------------

    /**
     * Nodo binario del árbol de decisión.
     */
    private static final class NodoArbol {

        // -- Atributos --------------------------------------------------------

        /** Condición evaluada por el nodo interno. */
        private final CondicionIA condicion;

        /** Rama que se recorre si la condición es verdadera. */
        private final NodoArbol siTrue;

        /** Rama que se recorre si la condición es falsa. */
        private final NodoArbol siFalse;

        /** Acción de la hoja, o null si el nodo es interno. */
        private final AccionIA accion;

        // -- Constructor ------------------------------------------------------

        /**
         * Crea un nodo interno o una hoja de acción.
         *
         * @param condicion condición del nodo
         * @param siTrue rama verdadera
         * @param siFalse rama falsa
         * @param accion acción de hoja
         */
        private NodoArbol(CondicionIA condicion, NodoArbol siTrue, NodoArbol siFalse, AccionIA accion) {
            this.condicion = condicion;
            this.siTrue = siTrue;
            this.siFalse = siFalse;
            this.accion = accion;
        }

        // -- Métodos de lógica ------------------------------------------------

        /**
         * Indica si el nodo es una hoja de acción.
         *
         * @return true si contiene acción final
         */
        private boolean isHoja() {
            return accion != null;
        }

        /**
         * Evalúa la condición del nodo.
         *
         * @param enemy enemigo que decide
         * @param room sala actual
         * @param player jugador objetivo
         * @return resultado de la condición
         */
        private boolean evaluar(Enemy enemy, Room room, Player player) {
            return condicion != null && condicion.evaluar(enemy, room, player);
        }

        // -- Getters ----------------------------------------------------------

        /**
         * Devuelve la rama verdadera.
         *
         * @return rama verdadera
         */
        private NodoArbol getSiTrue() {
            return siTrue;
        }

        /**
         * Devuelve la rama falsa.
         *
         * @return rama falsa
         */
        private NodoArbol getSiFalse() {
            return siFalse;
        }

        /**
         * Devuelve la acción de una hoja.
         *
         * @return acción final
         */
        private AccionIA getAccion() {
            return accion;
        }
    }

    /**
     * Acciones posibles que puede devolver el árbol de decisión.
     */
    public enum AccionIA {
        /** Ataque normal contra el jugador. */
        ATACAR,

        /** Movimiento directo hacia el jugador. */
        MOVER,

        /** Reposicionamiento para mantener distancia táctica. */
        MOVER_A_ZONA,

        /** Aplicación de efecto de estado del Controller. */
        APLICAR_EFECTO,

        /** Invocación de un Berserker por parte del Summoner. */
        INVOCAR,

        /** Ataque en área del Destructor. */
        AOE,

        /** Sin acción útil este turno. */
        ESPERAR
    }
}
