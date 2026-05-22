package Valdris.logic.puzzle;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.model.map.Cell;
import Valdris.model.map.Dungeon;
import Valdris.model.map.Room;
import Valdris.model.units.Player;

/**
 * Gestiona puzzles de secuencia basados en palancas y runas.
 *
 * <p>La guía original habla de LeverManager, pero el sistema real también
 * cubre runas. Por eso esta clase usa un nombre más amplio y centraliza la
 * lógica común: registrar activaciones, comprobar la secuencia, activar el
 * objetivo de éxito y aplicar penalización al fallar.</p>
 *
 * @see Room
 * @see Cell
 */
public final class PuzzleManager {

    // -- Constantes -----------------------------------------------------------

    // -- Constructor privado -------------------------------------------------

    /**
     * Evita instanciar la clase porque todos sus métodos son estáticos.
     */
    private PuzzleManager() {
    }

    // -- Activación -----------------------------------------------------------

    /**
     * Activa una celda de puzzle registrada en la sala.
     *
     * @param room sala donde ocurre la activación
     * @param cell celda de palanca o runa
     * @return true si se registró la activación
     */
    public static boolean activate(Room room, Cell cell) {
        if (room == null || cell == null || room.isPuzzleResolved()) {
            return false;
        }
        int indice = getIndicePuzzle(room, cell);
        if (indice < 0) {
            return false;
        }
        room.registrarActivacion(indice);
        return true;
    }

    /**
     * Activa una celda de puzzle por su triggerId.
     *
     * @param room sala donde ocurre la activación
     * @param triggerId identificador de la celda
     * @return true si se encontró y registró la activación
     */
    public static boolean activate(Room room, String triggerId) {
        if (room == null || triggerId == null || triggerId.isEmpty() || room.isPuzzleResolved()) {
            return false;
        }
        Cell cell = buscarCeldaPorTrigger(room, triggerId);
        return activate(room, cell);
    }

    /**
     * Resuelve una activación completa, aplicando éxito o fallo si la secuencia acaba.
     *
     * @param room sala del puzzle
     * @param cell celda activada
     * @param dungeon dungeon usado para activar pasadizos
     * @param player jugador afectado por fallos
     * @return true si la activación fue aceptada
     */
    public static boolean resolverActivacion(Room room, Cell cell, Dungeon dungeon, Player player) {
        boolean activada = activate(room, cell);
        if (!activada || room == null || !room.isSequenceComplete()) {
            return activada;
        }
        if (checkSequence(room)) {
            applySuccess(room, dungeon);
        } else {
            applyFailure(room, player);
        }
        return true;
    }

    /**
     * Evalúa si la secuencia actual es correcta.
     *
     * @param room sala evaluada
     * @return true si la secuencia está completa y es correcta
     */
    public static boolean checkSequence(Room room) {
        return room != null && room.checkSequence();
    }

    /**
     * Reinicia la secuencia actual de una sala.
     *
     * @param room sala que se reinicia
     */
    public static void resetSequence(Room room) {
        if (room != null) {
            room.limpiarSecuenciaActivada();
        }
    }

    /**
     * Aplica el resultado positivo del puzzle.
     *
     * <p>Si la sala tiene un objetivo de éxito, se intenta activar como pasadizo
     * oculto del dungeon. Aunque no exista en el dungeon, el puzzle queda marcado
     * como resuelto para permitir que TurnManager o DungeonGenerator lo usen
     * después para abrir puertas locales.</p>
     *
     * @param room sala cuyo puzzle se resolvió
     * @param dungeon dungeon donde activar el pasadizo
     * @return true si se marcó el puzzle como resuelto
     */
    public static boolean applySuccess(Room room, Dungeon dungeon) {
        if (room == null) {
            return false;
        }
        room.setPuzzleResolved(true);
        room.openAccessByTrigger(room.getPuzzleSuccessTarget());
        if (dungeon != null && room.getPuzzleSuccessTarget() != null) {
            dungeon.activateHiddenPassage(room.getPuzzleSuccessTarget());
        }
        return true;
    }

    /**
     * Aplica el resultado negativo del puzzle.
     *
     * @param room sala cuyo puzzle falló
     * @param player jugador penalizado
     */
    public static void applyFailure(Room room, Player player) {
        if (player != null) {
            int danio = room == null ? 3 : room.getPuzzleFailureDamage();
            player.recibirDanio(danio);
        }
        resetSequence(room);
    }

    // -- Métodos auxiliares ---------------------------------------------------

    /**
     * Localiza el índice lógico de una celda dentro de palancas o runas.
     *
     * @param room sala consultada
     * @param cell celda buscada
     * @return índice de secuencia, o -1 si no está registrada
     */
    private static int getIndicePuzzle(Room room, Cell cell) {
        int indice = getPosicionPorReferencia(room.getLeverCells(), cell);
        if (indice >= 0) {
            return indice;
        }
        return getPosicionPorReferencia(room.getRuneCells(), cell);
    }

    /**
     * Busca una celda de puzzle por triggerId.
     *
     * @param room sala consultada
     * @param triggerId identificador buscado
     * @return celda encontrada, o null si no existe
     */
    private static Cell buscarCeldaPorTrigger(Room room, String triggerId) {
        for (int i = 0; i < room.getLeverCells().getSize(); i++) {
            Cell cell = room.getLeverCells().get(i);
            if (cell != null && triggerId.equals(cell.getTriggerId())) {
                return cell;
            }
        }
        for (int i = 0; i < room.getRuneCells().getSize(); i++) {
            Cell cell = room.getRuneCells().get(i);
            if (cell != null && triggerId.equals(cell.getTriggerId())) {
                return cell;
            }
        }
        return null;
    }

    /**
     * Busca una celda por identidad de objeto.
     *
     * @param cells lista de celdas
     * @param cell celda buscada
     * @return posición de la misma instancia, o -1 si no está
     */
    private static int getPosicionPorReferencia(ListaSimplementeEnlazada<Cell> cells, Cell cell) {

        for (int i = 0; i < cells.getSize(); i++) {
            if (cells.get(i) == cell) {
                return i;
            }
        }
        return -1;
    }
}
