package Valdris.ui;

import Valdris.ui.view.MainMenuView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicacion JavaFX de Valdris.
 */
public class MainApp extends Application {

    /** Ancho fijo de la ventana principal. */
    public static final int WINDOW_WIDTH = 1280;

    /** Alto fijo de la ventana principal. */
    public static final int WINDOW_HEIGHT = 720;

    /**
     * Inicializa la ventana principal y muestra el menu inicial.
     *
     * @param primaryStage ventana principal de JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Valdris: El Núcleo Profundo");
        primaryStage.setWidth(WINDOW_WIDTH);
        primaryStage.setHeight(WINDOW_HEIGHT);
        primaryStage.setResizable(false);

        MainMenuView menuView = new MainMenuView(primaryStage);
        primaryStage.setScene(menuView.getScene());
        primaryStage.show();
    }

    /**
     * Lanza la aplicacion JavaFX.
     *
     * @param args argumentos de arranque
     */
    public static void main(String[] args) {
        launch(args);
    }
}
