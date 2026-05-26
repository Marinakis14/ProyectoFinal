package Valdris.ui;

import Valdris.ui.view.MainMenuView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicacion JavaFX de Valdris.
 */
public class MainApp extends Application {

    /** Ancho base de la ventana principal antes de maximizar. */
    public static final int WINDOW_WIDTH = 1600;

    /** Alto base de la ventana principal antes de maximizar. */
    public static final int WINDOW_HEIGHT = 900;

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
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setResizable(true);

        MainMenuView menuView = new MainMenuView(primaryStage);
        primaryStage.setScene(menuView.getScene());
        primaryStage.setMaximized(true);
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
