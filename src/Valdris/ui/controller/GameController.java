package Valdris.ui.controller;

import Valdris.exceptions.GameStateException;
import Valdris.exceptions.InvalidAttackException;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.turn.TurnManager;
import Valdris.model.enums.Phase;
import Valdris.model.map.Cell;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.Unit;
import Valdris.ui.model.GameModel;
import Valdris.ui.view.MainMenuView;
import javafx.stage.Stage;

/**
 * Controlador principal de la interfaz JavaFX.
 *
 * <p>Traduce eventos de JavaFX a llamadas del {@link TurnManager}. La vista no
 * ejecuta logica de juego directamente.</p>
 */
public class GameController {

    /** Modelo observable de la partida. */
    private final GameModel modelo;

    /**
     * Crea el controlador asociado a un modelo de partida.
     *
     * @param modelo modelo activo
     */
    public GameController(GameModel modelo) {
        this.modelo = modelo;
    }

    /**
     * Devuelve el modelo asociado al controlador.
     *
     * @return modelo activo
     */
    public GameModel getModelo() {
        return modelo;
    }

    /**
     * Gestiona el click sobre una celda del mapa segun la fase actual.
     *
     * @param fila fila pulsada
     * @param col columna pulsada
     */
    public void onCeldaClick(int fila, int col) {
        if (modelo.getTurnManager().getFaseActual() == Phase.MOVEMENT) {
            ejecutarMovimiento(fila, col);
        } else if (modelo.getTurnManager().getFaseActual() == Phase.ATTACK) {
            ejecutarAtaqueCelda(fila, col);
        } else {
            modelo.notificarMensaje("La celda solo se usa para mover o atacar en la fase correspondiente.");
        }
    }

    /**
     * Salta la fase de movimiento.
     */
    public void onSaltarMovimiento() {
        try {
            modelo.getTurnManager().saltarMovimiento();
            modelo.notificarMensaje(null);
        } catch (GameStateException e) {
            modelo.notificarMensaje(e.getMessage());
        }
    }

    /**
     * Ejecuta la accion de recogida/interaccion con contenedor.
     */
    public void onRecoger() {
        try {
            modelo.getTurnManager().ejecutarRecogida();
            modelo.notificarMensaje(null);
        } catch (GameStateException e) {
            modelo.notificarMensaje(e.getMessage());
        }
    }

    /**
     * Salta la fase de recogida.
     */
    public void onSaltarRecogida() {
        try {
            modelo.getTurnManager().saltarRecogida();
            modelo.notificarMensaje(null);
        } catch (GameStateException e) {
            modelo.notificarMensaje(e.getMessage());
        }
    }

    /**
     * Usa una puerta o escalera adyacente.
     */
    public void onUsarAcceso() {
        try {
            modelo.getTurnManager().usarAccesoAdyacente();
            modelo.notificarMensaje(null);
        } catch (InvalidMoveException | GameStateException e) {
            modelo.notificarMensaje(e.getMessage());
        }
    }

    /**
     * Activa una palanca adyacente.
     */
    public void onActivarPalanca() {
        try {
            modelo.getTurnManager().activarPalancaAdyacente();
            modelo.notificarMensaje(null);
        } catch (GameStateException e) {
            modelo.notificarMensaje(e.getMessage());
        }
    }

    /**
     * Salta la fase de uso de item.
     */
    public void onSaltarUsoItem() {
        try {
            modelo.getTurnManager().saltarUsoItem();
            modelo.notificarMensaje(null);
        } catch (GameStateException e) {
            modelo.notificarMensaje(e.getMessage());
        }
    }

    /**
     * Cede el turno y resuelve inmediatamente el turno enemigo.
     */
    public void onCederTurno() {
        try {
            modelo.getTurnManager().cederTurno();
            modelo.getTurnManager().ejecutarTurnoEnemigos();
            modelo.notificarMensaje(null);
        } catch (GameStateException e) {
            modelo.notificarMensaje(e.getMessage());
        }
    }

    /**
     * Inicia el combate final con Malachar si se cumplen las condiciones.
     */
    public void onIniciarCombateFinal() {
        try {
            modelo.getTurnManager().iniciarCombateFinal();
            modelo.notificarMensaje(null);
        } catch (GameStateException e) {
            modelo.notificarMensaje(e.getMessage());
        }
    }

    /**
     * Vuelve al menu principal.
     *
     * @param stage ventana principal de la aplicacion
     */
    public void onBotonMenuPrincipal(Stage stage) {
        if (stage != null) {
            MainMenuView menuView = new MainMenuView(stage);
            stage.setScene(menuView.getScene());
        }
    }

    /**
     * Ejecuta un movimiento del jugador.
     *
     * @param fila fila destino
     * @param col columna destino
     */
    private void ejecutarMovimiento(int fila, int col) {
        try {
            modelo.getTurnManager().ejecutarMovimiento(fila, col);
            modelo.notificarMensaje(null);
        } catch (InvalidMoveException | GameStateException e) {
            modelo.notificarMensaje(e.getMessage());
        }
    }

    /**
     * Ejecuta un ataque contra el enemigo situado en una celda.
     *
     * @param fila fila pulsada
     * @param col columna pulsada
     */
    private void ejecutarAtaqueCelda(int fila, int col) {
        try {
            Room room = modelo.getDungeon().getRoomActual();
            if (room == null || !room.isEnRango(fila, col)) {
                modelo.notificarMensaje("Debe seleccionarse una celda valida.");
                return;
            }
            Cell cell = room.getCell(fila, col);
            Unit unit = cell.getUnit();
            if (!(unit instanceof Enemy)) {
                modelo.notificarMensaje("Debe seleccionarse un enemigo para atacar.");
                return;
            }
            modelo.getTurnManager().ejecutarAtaque((Enemy) unit);
            modelo.notificarMensaje(null);
        } catch (InvalidMoveException | InvalidAttackException | GameStateException e) {
            modelo.notificarMensaje(e.getMessage());
        }
    }
}
