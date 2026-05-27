package Valdris.logic.ai;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.InvalidAttackException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.ai.ArbolDecisionIA.AccionIA;
import Valdris.logic.bfs.BFSMovimiento;
import Valdris.logic.combat.CombatManager;
import Valdris.logic.combat.CombatResult;
import Valdris.logic.vision.LineaDeVision;
import Valdris.model.effects.Effect;
import Valdris.model.effects.EffectProcessingResult;
import Valdris.model.enums.EffectType;
import Valdris.model.enums.EnemyType;
import Valdris.model.items.Item;
import Valdris.model.map.Cell;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.MiniBossEnemy;
import Valdris.model.units.Player;

/**
 * Ejecuta las acciones concretas de los enemigos durante su turno.
 *
 * <p>La clase usa {@link ArbolDecisionIA} para decidir la intención táctica y
 * después traduce esa acción en movimiento, ataque, aplicación de efectos,
 * invocación o espera. A diferencia del árbol, esta clase sí modifica el estado
 * de la sala y de las unidades.</p>
 *
 * <p>El movimiento se calcula dentro de la sala con {@link BFSMovimiento}. La
 * guía menciona camino mínimo para la IA, pero en este proyecto
 * {@code BFSCaminoMinimo} trabaja entre salas del dungeon; por eso el movimiento
 * de enemigos usa el BFS de celdas.</p>
 */
public final class IAEnemigo {

    // -- Constantes -----------------------------------------------------------

    /** Duración de los efectos aplicados por el Controller. */
    private static final int DURACION_EFECTO_CONTROLLER = 2;

    /** Cooldown de ataque del Sniper. */
    private static final int COOLDOWN_SNIPER = 2;

    /** Efectos que puede aplicar un Controller. */
    private static final EffectType[] EFECTOS_CONTROLLER = {
        EffectType.SLOW,
        EffectType.BLIND,
        EffectType.CURSE
    };

    // -- Constructor privado -------------------------------------------------

    /**
     * Evita instanciar la clase porque todos sus métodos son estáticos.
     */
    private IAEnemigo() {
    }

    // -- Ejecución principal -------------------------------------------------

    /**
     * Ejecuta el turno completo de un enemigo.
     *
     * @param enemy enemigo activo
     * @param room sala donde se encuentra
     * @param player jugador objetivo
     * @param cm parámetro conservado por compatibilidad con la guía
     * @return resultado de la acción ejecutada, o null si faltan datos
     */
    public static AIActionResult executeTurn(Enemy enemy, Room room, Player player, CombatManager cm) {
        if (enemy == null || room == null || player == null || !enemy.isVivo()) {
            return null;
        }

        int filaOrigen = enemy.getFilaActual();
        int colOrigen = enemy.getColActual();
        boolean paralizado = enemy.tieneEfecto(EffectType.PARALYSIS);
        EffectProcessingResult effects = enemy.procesarEfectos();
        if (paralizado || !enemy.isVivo()) {
            String dropItemId = null;
            if (!enemy.isVivo()) {
                dropItemId = resolverMuertePorEfectos(enemy, room);
            }
            String motivo = enemy.isVivo() ? "PARALYSIS" : "MUERTO_POR_EFECTOS";
            return new AIActionResult(AccionIA.ESPERAR, enemy.getTipo(), room.getId(),
                filaOrigen, colOrigen, enemy.getFilaActual(), enemy.getColActual(),
                null, effects, null, null, -1, -1,
                motivo, dropItemId);
        }

        if (enemy instanceof MiniBossEnemy) {
            return MiniBossAI.executeTurn((MiniBossEnemy) enemy, room, player, cm,
                filaOrigen, colOrigen, effects);
        }

        return executeBaseAction(enemy, room, player, cm, filaOrigen, colOrigen, effects);
    }

    /**
     * Ejecuta la acción base de un enemigo no especial.
     *
     * <p>Este método queda accesible dentro del paquete para que la IA de
     * mini-bosses pueda reutilizar el comportamiento normal cuando la habilidad
     * especial no está cargada o no corresponde usarla.</p>
     *
     * @param enemy enemigo activo
     * @param room sala actual
     * @param player jugador objetivo
     * @param cm parámetro conservado por compatibilidad con la guía
     * @param filaOrigen fila inicial
     * @param colOrigen columna inicial
     * @param effects efectos procesados antes de actuar
     * @return resultado de la acción base
     */
    static AIActionResult executeBaseAction(Enemy enemy, Room room, Player player, CombatManager cm,
                                            int filaOrigen, int colOrigen, EffectProcessingResult effects) {
        ArbolDecisionIA arbol = new ArbolDecisionIA(enemy.getTipo());
        AccionIA accion = arbol.decidirAccion(enemy, room, player);
        AIActionResult result = ejecutarAccion(accion, enemy, room, player, cm, filaOrigen, colOrigen);
        if (result == null) {
            return new AIActionResult(AccionIA.ESPERAR, enemy.getTipo(), room.getId(),
                filaOrigen, colOrigen, enemy.getFilaActual(), enemy.getColActual(),
                null, effects, null, null, -1, -1, "SIN_ACCION", null);
        }
        return new AIActionResult(result.getAccion(), result.getEnemyType(), result.getSalaId(),
            result.getFilaOrigen(), result.getColOrigen(), result.getFilaDestino(), result.getColDestino(),
            result.getCombatResult(), effects, result.getEfectoAplicado(), result.getTipoInvocado(),
            result.getFilaInvocado(), result.getColInvocado(), result.getMotivo(), result.getDropItemId());
    }

    /**
     * Ejecuta la acción elegida por el árbol.
     *
     * @param accion acción decidida
     * @param enemy enemigo activo
     * @param room sala actual
     * @param player jugador objetivo
     * @param cm parámetro conservado por compatibilidad con la guía
     */
    private static AIActionResult ejecutarAccion(AccionIA accion, Enemy enemy, Room room, Player player,
                                                 CombatManager cm, int filaOrigen, int colOrigen) {
        if (accion == AccionIA.ATACAR) {
            return ejecutarAtaque(enemy, player, cm);
        }
        if (accion == AccionIA.APLICAR_EFECTO) {
            EffectType efecto = aplicarEfectoController(player);
            return crearResultado(accion, enemy, room, filaOrigen, colOrigen, null, null, efecto, null,
                -1, -1, null);
        }
        if (accion == AccionIA.AOE) {
            return ejecutarAOE(enemy, room, player);
        }
        if (accion == AccionIA.INVOCAR) {
            return invocarBerserker(enemy, room);
        }
        if (accion == AccionIA.MOVER_A_ZONA) {
            ejecutarMovimientoAZona(enemy, room, player);
            incrementarCooldownSiCorresponde(enemy);
            return crearResultado(accion, enemy, room, filaOrigen, colOrigen, null, null, null, null,
                -1, -1, null);
        }
        if (accion == AccionIA.MOVER) {
            if (enemy.getTipo() == EnemyType.SUMMONER) {
                ejecutarHuida(enemy, room, player);
            } else {
                ejecutarMovimiento(enemy, room, player);
            }
            incrementarCooldownSiCorresponde(enemy);
            return crearResultado(accion, enemy, room, filaOrigen, colOrigen, null, null, null, null,
                -1, -1, null);
        }
        return crearResultado(AccionIA.ESPERAR, enemy, room, filaOrigen, colOrigen, null, null, null, null,
            -1, -1, "ESPERA");
    }

    // -- Movimiento -----------------------------------------------------------

    /**
     * Mueve un enemigo hacia una celda libre adyacente al jugador.
     *
     * @param enemy enemigo que se mueve
     * @param room sala actual
     * @param player jugador objetivo
     */
    public static void ejecutarMovimiento(Enemy enemy, Room room, Player player) {
        if (!datosMovimientoValidos(enemy, room, player)) {
            return;
        }

        Posicion destino = buscarMejorCeldaAdyacente(enemy, room, player);
        if (destino != null) {
            moverHacia(enemy, room, destino.getFila(), destino.getCol());
        }
    }

    /**
     * Reposiciona a un enemigo a distancia táctica del jugador.
     *
     * <p>Se busca una celda alcanzable desde la que el jugador quede dentro del
     * rango del enemigo, con línea de visión y evitando la adyacencia si es
     * posible. Si no existe una celda ideal, se usa el movimiento básico de
     * persecución.</p>
     *
     * @param enemy enemigo que se reposiciona
     * @param room sala actual
     * @param player jugador objetivo
     */
    public static void ejecutarMovimientoAZona(Enemy enemy, Room room, Player player) {
        if (!datosMovimientoValidos(enemy, room, player)) {
            return;
        }

        Posicion destino = buscarCeldaDeZona(enemy, room, player);
        if (destino != null) {
            moverA(enemy, room, destino.getFila(), destino.getCol());
        } else {
            ejecutarMovimiento(enemy, room, player);
        }
    }

    /**
     * Aleja al Summoner del jugador escogiendo la celda alcanzable más lejana.
     *
     * @param enemy enemigo que huye
     * @param room sala actual
     * @param player jugador objetivo
     */
    public static void ejecutarHuida(Enemy enemy, Room room, Player player) {
        if (!datosMovimientoValidos(enemy, room, player)) {
            return;
        }

        Posicion destino = buscarCeldaMasLejana(enemy, room, player);
        if (destino != null) {
            moverA(enemy, room, destino.getFila(), destino.getCol());
        }
    }

    // -- Ataques y habilidades ------------------------------------------------

    /**
     * Ejecuta el ataque de un enemigo contra el jugador.
     *
     * @param enemy enemigo atacante
     * @param player jugador objetivo
     * @param cm parámetro conservado por compatibilidad con la guía
     * @return resultado de la acción de ataque
     */
    public static AIActionResult ejecutarAtaque(Enemy enemy, Player player, CombatManager cm) {
        if (enemy == null || player == null) {
            return null;
        }

        if (enemy.getTipo() == EnemyType.CONTROLLER) {
            EffectType efecto = aplicarEfectoController(player);
            return new AIActionResult(AccionIA.APLICAR_EFECTO, enemy.getTipo(), enemy.getIdSala(),
                enemy.getFilaActual(), enemy.getColActual(), enemy.getFilaActual(), enemy.getColActual(),
                null, null, efecto, null, -1, -1, null, null);
        }
        if (enemy.getTipo() == EnemyType.SNIPER && !enemy.isCooldownListo(COOLDOWN_SNIPER)) {
            enemy.incrementarCooldown();
            return new AIActionResult(AccionIA.ESPERAR, enemy.getTipo(), enemy.getIdSala(),
                enemy.getFilaActual(), enemy.getColActual(), enemy.getFilaActual(), enemy.getColActual(),
                null, null, null, null, -1, -1, "COOLDOWN", null);
        }

        try {
            CombatResult combat = CombatManager.resolverAtaqueEnemigo(enemy, player);
            if (enemy.getTipo() == EnemyType.SNIPER) {
                enemy.resetCooldown();
            }
            return new AIActionResult(AccionIA.ATACAR, enemy.getTipo(), enemy.getIdSala(),
                enemy.getFilaActual(), enemy.getColActual(), enemy.getFilaActual(), enemy.getColActual(),
                combat, null, null, null, -1, -1, null, null);
        } catch (InvalidAttackException e) {
            incrementarCooldownSiCorresponde(enemy);
            return new AIActionResult(AccionIA.ESPERAR, enemy.getTipo(), enemy.getIdSala(),
                enemy.getFilaActual(), enemy.getColActual(), enemy.getFilaActual(), enemy.getColActual(),
                null, null, null, null, -1, -1, "ATAQUE_INVALIDO", null);
        }
    }

    /**
     * Ejecuta el AOE del Destructor.
     *
     * @param enemy enemigo Destructor
     * @param room sala actual
     * @param player jugador objetivo
     */
    private static AIActionResult ejecutarAOE(Enemy enemy, Room room, Player player) {
        try {
            CombatResult combat = CombatManager.resolverAOEDestructor(enemy, room, player);
            return crearResultado(AccionIA.AOE, enemy, room, enemy.getFilaActual(), enemy.getColActual(),
                combat, null, null, null, -1, -1, null);
        } catch (InvalidAttackException e) {
            // Si faltan datos o el ataque no es válido, el enemigo pierde la acción.
            return crearResultado(AccionIA.ESPERAR, enemy, room, enemy.getFilaActual(), enemy.getColActual(),
                null, null, null, null, -1, -1, "AOE_INVALIDO");
        }
    }

    /**
     * Invoca un Berserker en una celda libre cercana al Summoner.
     *
     * @param summoner enemigo invocador
     * @param room sala actual
     * @return resultado de la invocación
     */
    public static AIActionResult invocarBerserker(Enemy summoner, Room room) {
        if (summoner == null || room == null) {
            return null;
        }

        Cell celda = room.getCeldaLibreCercana(summoner.getFilaActual(), summoner.getColActual());
        Posicion posicion = buscarPosicion(room, celda);
        if (posicion == null) {
            return crearResultado(AccionIA.ESPERAR, summoner, room, summoner.getFilaActual(),
                summoner.getColActual(), null, null, null, null, -1, -1, "SIN_CELDA_INVOCACION");
        }

        Enemy berserker = new Enemy(EnemyType.BERSERKER, posicion.getFila(), posicion.getCol(), room.getId());
        room.addEnemigo(berserker);
        summoner.resetCooldown();
        return crearResultado(AccionIA.INVOCAR, summoner, room, summoner.getFilaActual(),
            summoner.getColActual(), null, null, null, EnemyType.BERSERKER,
            posicion.getFila(), posicion.getCol(), null);
    }

    /**
     * Aplica al jugador un efecto aleatorio de Controller.
     *
     * @param player jugador afectado
     */
    private static EffectType aplicarEfectoController(Player player) {
        if (player == null) {
            return null;
        }
        EffectType efecto = elegirEfectoController(player);
        player.addEfecto(new Effect(efecto, DURACION_EFECTO_CONTROLLER));
        return efecto;
    }

    /**
     * Elige uno de los efectos posibles del Controller.
     *
     * @param player jugador afectado
     * @return efecto elegido
     */
    private static EffectType elegirEfectoController(Player player) {
        int inicio = (int) (Math.random() * EFECTOS_CONTROLLER.length);
        for (int i = 0; i < EFECTOS_CONTROLLER.length; i++) {
            EffectType candidato = EFECTOS_CONTROLLER[(inicio + i) % EFECTOS_CONTROLLER.length];
            if (player == null || !player.tieneEfecto(candidato)) {
                return candidato;
            }
        }
        return EFECTOS_CONTROLLER[inicio];
    }

    /**
     * Crea un resultado usando la posición final actual del enemigo.
     *
     * @param accion acción ejecutada
     * @param enemy enemigo que actuó
     * @param room sala actual
     * @param filaOrigen fila inicial
     * @param colOrigen columna inicial
     * @param combat resultado de combate
     * @param effects resultado de efectos
     * @param efecto efecto aplicado
     * @param invocado tipo invocado
     * @param filaInvocado fila invocada
     * @param colInvocado columna invocada
     * @param motivo motivo adicional
     * @return resultado estructurado
     */
    private static AIActionResult crearResultado(AccionIA accion, Enemy enemy, Room room,
                                                 int filaOrigen, int colOrigen,
                                                 CombatResult combat, EffectProcessingResult effects,
                                                 EffectType efecto, EnemyType invocado,
                                                 int filaInvocado, int colInvocado, String motivo) {
        if (enemy == null) {
            return null;
        }
        String salaId = room == null ? enemy.getIdSala() : room.getId();
        return new AIActionResult(accion, enemy.getTipo(), salaId, filaOrigen, colOrigen,
            enemy.getFilaActual(), enemy.getColActual(), combat, effects, efecto, invocado,
            filaInvocado, colInvocado, motivo, null);
    }

    /**
     * Resuelve drop y retirada cuando un enemigo muere por efectos al inicio de turno.
     *
     * @param enemy enemigo muerto
     * @param room sala donde estaba
     * @return id del drop generado, o null
     */
    private static String resolverMuertePorEfectos(Enemy enemy, Room room) {
        Item drop = enemy.getDropItem();
        String dropItemId = drop == null ? null : drop.getId();
        enemy.onDeath(room);
        room.removeEnemigo(enemy);
        return dropItemId;
    }

    // -- Búsqueda de destinos -------------------------------------------------

    /**
     * Busca la celda libre adyacente al jugador con camino más corto.
     *
     * @param enemy enemigo que se mueve
     * @param room sala actual
     * @param player jugador objetivo
     * @return posición elegida, o null si no hay ruta
     */
    private static Posicion buscarMejorCeldaAdyacente(Enemy enemy, Room room, Player player) {
        int[][] direcciones = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
        };

        Posicion mejor = null;
        int mejorLongitud = -1;
        for (int i = 0; i < direcciones.length; i++) {
            int fila = player.getFilaActual() + direcciones[i][0];
            int col = player.getColActual() + direcciones[i][1];
            if (!esDestinoLibre(room, fila, col)) {
                continue;
            }

            ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(room,
                enemy.getFilaActual(), enemy.getColActual(), fila, col);
            int longitud = camino.getSize();
            if (longitud > 0 && (mejor == null || longitud < mejorLongitud)) {
                mejor = new Posicion(fila, col);
                mejorLongitud = longitud;
            }
        }
        return mejor;
    }

    /**
     * Busca una celda de reposicionamiento para enemigos a distancia.
     *
     * @param enemy enemigo que se mueve
     * @param room sala actual
     * @param player jugador objetivo
     * @return posición elegida, o null si no hay celda adecuada
     */
    private static Posicion buscarCeldaDeZona(Enemy enemy, Room room, Player player) {
        ListaSimplementeEnlazada<Cell> alcanzables = BFSMovimiento.getCellsInRange(room,
            enemy.getFilaActual(), enemy.getColActual(), enemy.getMovEfectivo());

        Posicion mejor = null;
        int mejorDistancia = -1;
        for (int i = 0; i < alcanzables.getSize(); i++) {
            Cell celda = alcanzables.get(i);
            Posicion posicion = buscarPosicion(room, celda);
            if (posicion == null || !esZonaValida(enemy, room, player, posicion)) {
                continue;
            }

            int distancia = distanciaManhattan(posicion.getFila(), posicion.getCol(),
                player.getFilaActual(), player.getColActual());
            if (mejor == null || distancia > mejorDistancia) {
                mejor = posicion;
                mejorDistancia = distancia;
            }
        }
        return mejor;
    }

    /**
     * Busca la celda alcanzable más lejana del jugador.
     *
     * @param enemy enemigo que huye
     * @param room sala actual
     * @param player jugador objetivo
     * @return posición elegida, o null si no hay movimiento posible
     */
    private static Posicion buscarCeldaMasLejana(Enemy enemy, Room room, Player player) {
        ListaSimplementeEnlazada<Cell> alcanzables = BFSMovimiento.getCellsInRange(room,
            enemy.getFilaActual(), enemy.getColActual(), enemy.getMovEfectivo());

        Posicion mejor = null;
        int mejorDistancia = distanciaManhattan(enemy.getFilaActual(), enemy.getColActual(),
            player.getFilaActual(), player.getColActual());
        for (int i = 0; i < alcanzables.getSize(); i++) {
            Cell celda = alcanzables.get(i);
            Posicion posicion = buscarPosicion(room, celda);
            if (posicion == null) {
                continue;
            }

            int distancia = distanciaManhattan(posicion.getFila(), posicion.getCol(),
                player.getFilaActual(), player.getColActual());
            if (distancia > mejorDistancia) {
                mejor = posicion;
                mejorDistancia = distancia;
            }
        }
        return mejor;
    }

    // -- Movimiento de unidad -------------------------------------------------

    /**
     * Mueve hacia un destino siguiendo el camino BFS hasta agotar movimiento.
     *
     * @param enemy enemigo que se mueve
     * @param room sala actual
     * @param filaDestino fila de destino táctico
     * @param colDestino columna de destino táctico
     */
    private static void moverHacia(Enemy enemy, Room room, int filaDestino, int colDestino) {
        ListaSimplementeEnlazada<Cell> camino = BFSMovimiento.getCamino(room,
            enemy.getFilaActual(), enemy.getColActual(), filaDestino, colDestino);
        if (camino.getSize() <= 1) {
            return;
        }

        int pasos = Math.min(enemy.getMovEfectivo(), camino.getSize() - 1);
        Cell celdaDestino = camino.get(pasos);
        Posicion posicion = buscarPosicion(room, celdaDestino);
        if (posicion != null) {
            moverA(enemy, room, posicion.getFila(), posicion.getCol());
        }
    }

    /**
     * Mueve directamente a una celda alcanzable.
     *
     * @param enemy enemigo que se mueve
     * @param room sala actual
     * @param fila nueva fila
     * @param col nueva columna
     */
    private static void moverA(Enemy enemy, Room room, int fila, int col) {
        if (!esDestinoLibre(room, fila, col)) {
            return;
        }

        try {
            room.getCell(enemy.getFilaActual(), enemy.getColActual()).removeUnit();
            enemy.setPosicion(fila, col);
            room.getCell(fila, col).setUnit(enemy);
        } catch (InvalidMoveException e) {
            // Las posiciones se validan antes de mover; si fallan, se pierde la acción.
        }
    }

    // -- Validaciones y utilidades -------------------------------------------

    /**
     * Comprueba datos mínimos para movimiento.
     *
     * @param enemy enemigo que se mueve
     * @param room sala actual
     * @param player jugador objetivo
     * @return true si el movimiento puede intentarse
     */
    private static boolean datosMovimientoValidos(Enemy enemy, Room room, Player player) {
        return enemy != null && room != null && player != null && enemy.getMovEfectivo() > 0
            && room.isEnRango(enemy.getFilaActual(), enemy.getColActual());
    }

    /**
     * Comprueba si una celda puede usarse como destino.
     *
     * @param room sala actual
     * @param fila fila consultada
     * @param col columna consultada
     * @return true si la celda está libre y es transitable
     */
    private static boolean esDestinoLibre(Room room, int fila, int col) {
        if (room == null || !room.isEnRango(fila, col)) {
            return false;
        }
        try {
            return room.getCell(fila, col).isWalkable();
        } catch (InvalidMoveException e) {
            return false;
        }
    }

    /**
     * Comprueba si una posición sirve como zona de ataque a distancia.
     *
     * @param enemy enemigo que se reposiciona
     * @param room sala actual
     * @param player jugador objetivo
     * @param posicion posición candidata
     * @return true si permite atacar desde distancia táctica
     */
    private static boolean esZonaValida(Enemy enemy, Room room, Player player, Posicion posicion) {
        int distancia = distanciaManhattan(posicion.getFila(), posicion.getCol(),
            player.getFilaActual(), player.getColActual());
        if (distancia <= 1 || distancia > enemy.getRangoEfectivo()) {
            return false;
        }
        return LineaDeVision.tieneVision(room, posicion.getFila(), posicion.getCol(),
            player.getFilaActual(), player.getColActual());
    }

    /**
     * Busca las coordenadas de una celda dentro de la sala.
     *
     * @param room sala actual
     * @param celda celda buscada
     * @return posición de la celda, o null si no pertenece a la sala
     */
    private static Posicion buscarPosicion(Room room, Cell celda) {
        if (room == null || celda == null) {
            return null;
        }
        for (int fila = 0; fila < room.getFilas(); fila++) {
            for (int col = 0; col < room.getCols(); col++) {
                try {
                    if (room.getCell(fila, col) == celda) {
                        return new Posicion(fila, col);
                    }
                } catch (InvalidMoveException e) {
                    // La iteración respeta límites; si falla, se continúa con la siguiente celda.
                }
            }
        }
        return null;
    }

    /**
     * Incrementa cooldown de enemigos que usan contador temporal.
     *
     * @param enemy enemigo consultado
     */
    private static void incrementarCooldownSiCorresponde(Enemy enemy) {
        if (enemy == null) {
            return;
        }
        if (enemy.getTipo() == EnemyType.SNIPER || enemy.getTipo() == EnemyType.SUMMONER) {
            enemy.incrementarCooldown();
        }
    }

    /**
     * Calcula distancia Manhattan entre dos posiciones.
     *
     * @param filaA fila de la primera posición
     * @param colA columna de la primera posición
     * @param filaB fila de la segunda posición
     * @param colB columna de la segunda posición
     * @return distancia ortogonal
     */
    private static int distanciaManhattan(int filaA, int colA, int filaB, int colB) {
        return Math.abs(filaA - filaB) + Math.abs(colA - colB);
    }

    // -- Clase auxiliar -------------------------------------------------------

    /**
     * Coordenada interna de sala.
     */
    private static final class Posicion {

        // -- Atributos --------------------------------------------------------

        /** Fila de la posición. */
        private final int fila;

        /** Columna de la posición. */
        private final int col;

        // -- Constructor ------------------------------------------------------

        /**
         * Crea una coordenada.
         *
         * @param fila fila
         * @param col columna
         */
        private Posicion(int fila, int col) {
            this.fila = fila;
            this.col = col;
        }

        // -- Getters ----------------------------------------------------------

        /**
         * Devuelve la fila.
         *
         * @return fila
         */
        private int getFila() {
            return fila;
        }

        /**
         * Devuelve la columna.
         *
         * @return columna
         */
        private int getCol() {
            return col;
        }
    }
}
