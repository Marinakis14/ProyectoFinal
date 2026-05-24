package Valdris.ui.controller;

import Valdris.ui.model.GameModel;
import Valdris.ui.view.MainMenuView;
import javafx.stage.Stage;

/**
 * Controlador principal de la interfaz JavaFX.
 *
 * <p>En este subbloque solo gestiona navegacion basica. Las acciones jugables
 * se conectaran en subbloques posteriores para mantener separadas las fases de
 * implementacion.</p>
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
}
