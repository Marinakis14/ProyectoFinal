package Valdris.ui.view;

import Valdris.exceptions.GameStateException;
import Valdris.persistence.LectorJSON;
import Valdris.persistence.LoadedGame;
import Valdris.ui.MainApp;
import Valdris.ui.controller.GameController;
import Valdris.ui.model.GameModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Pantalla inicial del juego con opciones de nueva partida, carga y salida.
 */
public class MainMenuView {

    /** Ventana principal compartida por las pantallas iniciales. */
    private final Stage stage;

    /** Escena construida para el menu principal. */
    private final Scene scene;

    /**
     * Crea la pantalla inicial asociada a la ventana principal.
     *
     * @param stage ventana principal de la aplicacion
     */
    public MainMenuView(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(crearContenido(), MainApp.WINDOW_WIDTH, MainApp.WINDOW_HEIGHT);
    }

    /**
     * Devuelve la escena del menu principal.
     *
     * @return escena JavaFX del menu
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Cambia a la pantalla de seleccion de personaje.
     */
    public void iniciarNuevaPartida() {
        CharacterSelectView characterSelectView = new CharacterSelectView(stage);
        stage.setScene(characterSelectView.getScene());
    }

    /**
     * Carga la partida guardada y abre la pantalla de juego.
     */
    public void cargarPartida() {
        try {
            LoadedGame loadedGame = LectorJSON.cargarPartida(GameModel.SAVE_PATH);
            GameModel modelo = new GameModel(loadedGame);
            GameController controller = new GameController(modelo);
            GameView gameView = new GameView(stage, modelo, controller);
            stage.setScene(gameView.getScene());
        } catch (GameStateException e) {
            mostrarErrorCarga(e.getMessage());
        }
    }

    /**
     * Muestra un error al cargar la partida.
     *
     * @param mensaje detalle del problema encontrado
     */
    public void mostrarErrorCarga(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Cargar partida");
        alert.setHeaderText("No se pudo cargar la partida");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Cierra la ventana principal.
     */
    public void salir() {
        stage.close();
    }

    /**
     * Construye el contenido visual de la pantalla inicial.
     *
     * @return panel raiz
     */
    private BorderPane crearContenido() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(48));
        root.setStyle("-fx-background-color: #171717;");

        VBox centro = new VBox(22);
        centro.setAlignment(Pos.CENTER);

        Label titulo = new Label("Valdris: El Núcleo Profundo");
        titulo.setFont(Font.font("Serif", 42));
        titulo.setStyle("-fx-text-fill: #f5f0e6;");

        Label subtitulo = new Label("Grupo H12GEXTRA");
        subtitulo.setFont(Font.font("SansSerif", 16));
        subtitulo.setStyle("-fx-text-fill: #c9b99c;");

        Button nuevaPartida = crearBotonMenu("Nueva partida");
        nuevaPartida.setOnAction(event -> iniciarNuevaPartida());

        Button cargarPartida = crearBotonMenu("Cargar partida");
        cargarPartida.setOnAction(event -> cargarPartida());

        Button salir = crearBotonMenu("Salir");
        salir.setOnAction(event -> salir());

        centro.getChildren().addAll(titulo, subtitulo, nuevaPartida, cargarPartida, salir);
        root.setCenter(centro);
        return root;
    }

    /**
     * Crea un boton uniforme para el menu principal.
     *
     * @param texto texto visible del boton
     * @return boton configurado
     */
    private Button crearBotonMenu(String texto) {
        Button button = new Button(texto);
        button.setPrefWidth(260);
        button.setPrefHeight(44);
        button.setFont(Font.font("SansSerif", 16));
        button.setStyle(
            "-fx-background-color: #3a3328;"
                + "-fx-text-fill: #f5f0e6;"
                + "-fx-border-color: #8f7651;"
                + "-fx-border-width: 1;"
        );
        return button;
    }
}
