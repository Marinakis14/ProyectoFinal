package Valdris.ui.view;

import Valdris.ui.MainApp;
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
     * Muestra el mensaje temporal de carga pendiente.
     */
    public void mostrarCargaPendiente() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cargar partida");
        alert.setHeaderText(null);
        alert.setContentText("La carga de partida se implementará en un bloque posterior.");
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
        cargarPartida.setOnAction(event -> mostrarCargaPendiente());

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
