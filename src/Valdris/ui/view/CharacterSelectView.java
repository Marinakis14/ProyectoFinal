package Valdris.ui.view;

import Valdris.exceptions.GameStateException;
import Valdris.model.enums.CharacterType;
import Valdris.ui.MainApp;
import Valdris.ui.model.GameModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Pantalla de seleccion del personaje jugable.
 */
public class CharacterSelectView {

    /** Ventana principal compartida por la navegacion inicial. */
    private final Stage stage;

    /** Escena construida para la seleccion de personaje. */
    private final Scene scene;

    /**
     * Crea la pantalla de seleccion de personaje.
     *
     * @param stage ventana principal de la aplicacion
     */
    public CharacterSelectView(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(crearContenido(), MainApp.WINDOW_WIDTH, MainApp.WINDOW_HEIGHT);
    }

    /**
     * Devuelve la escena de seleccion de personaje.
     *
     * @return escena JavaFX de seleccion
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Crea la tarjeta visual de un personaje.
     *
     * @param tipo personaje representado
     * @return tarjeta visual del personaje
     */
    public VBox crearBotonPersonaje(CharacterType tipo) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(24));
        card.setPrefWidth(300);
        card.setMinHeight(360);
        card.setStyle(
            "-fx-background-color: #242424;"
                + "-fx-border-color: " + colorPersonaje(tipo) + ";"
                + "-fx-border-width: 2;"
        );

        Label nombre = new Label(nombrePersonaje(tipo));
        nombre.setFont(Font.font("Serif", 28));
        nombre.setStyle("-fx-text-fill: #f5f0e6;");

        Label rol = new Label(rolPersonaje(tipo));
        rol.setFont(Font.font("SansSerif", 14));
        rol.setStyle("-fx-text-fill: #c9b99c;");

        Label stats = new Label(statsPersonaje(tipo));
        stats.setFont(Font.font("Monospaced", 15));
        stats.setStyle("-fx-text-fill: #e5dcc8;");
        stats.setWrapText(true);

        Label descripcion = new Label(descripcionPersonaje(tipo));
        descripcion.setFont(Font.font("SansSerif", 14));
        descripcion.setStyle("-fx-text-fill: #d2c6af;");
        descripcion.setWrapText(true);
        descripcion.setMaxWidth(245);

        Button elegir = new Button("Elegir");
        elegir.setPrefWidth(160);
        elegir.setPrefHeight(38);
        elegir.setStyle(
            "-fx-background-color: " + colorPersonaje(tipo) + ";"
                + "-fx-text-fill: #111111;"
                + "-fx-font-weight: bold;"
        );
        elegir.setOnAction(event -> iniciarJuego(tipo));

        card.getChildren().addAll(nombre, rol, stats, descripcion, elegir);
        return card;
    }

    /**
     * Crea una partida real y muestra una confirmacion temporal hasta tener GameView.
     *
     * @param tipo personaje elegido
     */
    public void iniciarJuego(CharacterType tipo) {
        try {
            GameModel modelo = new GameModel(tipo);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nueva partida");
            alert.setHeaderText(nombrePersonaje(tipo));
            alert.setContentText(crearMensajePartidaCreada(modelo));
            alert.showAndWait();
        } catch (GameStateException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al crear partida");
            alert.setHeaderText("No se pudo iniciar la partida");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Vuelve al menu principal.
     */
    public void volverAlMenu() {
        MainMenuView menuView = new MainMenuView(stage);
        stage.setScene(menuView.getScene());
    }

    /**
     * Construye el contenido visual de la pantalla de seleccion.
     *
     * @return panel raiz
     */
    private BorderPane crearContenido() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(36));
        root.setStyle("-fx-background-color: #171717;");

        VBox cabecera = new VBox(8);
        cabecera.setAlignment(Pos.CENTER);
        Label titulo = new Label("Elige tu personaje");
        titulo.setFont(Font.font("Serif", 36));
        titulo.setStyle("-fx-text-fill: #f5f0e6;");
        Label subtitulo = new Label("Cada camino atraviesa Valdris de una forma distinta.");
        subtitulo.setFont(Font.font("SansSerif", 15));
        subtitulo.setStyle("-fx-text-fill: #c9b99c;");
        cabecera.getChildren().addAll(titulo, subtitulo);

        HBox personajes = new HBox(28);
        personajes.setAlignment(Pos.CENTER);
        personajes.getChildren().addAll(
            crearBotonPersonaje(CharacterType.KAEL),
            crearBotonPersonaje(CharacterType.SYRA),
            crearBotonPersonaje(CharacterType.DORATH)
        );

        Button volver = new Button("Volver");
        volver.setPrefWidth(160);
        volver.setPrefHeight(38);
        volver.setOnAction(event -> volverAlMenu());
        volver.setStyle(
            "-fx-background-color: #3a3328;"
                + "-fx-text-fill: #f5f0e6;"
                + "-fx-border-color: #8f7651;"
        );

        VBox centro = new VBox(28);
        centro.setAlignment(Pos.CENTER);
        centro.getChildren().addAll(cabecera, personajes, volver);
        root.setCenter(centro);
        return root;
    }

    /**
     * Devuelve el nombre visible del personaje.
     *
     * @param tipo personaje consultado
     * @return nombre visible
     */
    private String nombrePersonaje(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return "Syra";
        }
        if (tipo == CharacterType.DORATH) {
            return "Dorath";
        }
        return "Kael";
    }

    /**
     * Devuelve el rol visible del personaje.
     *
     * @param tipo personaje consultado
     * @return rol del personaje
     */
    private String rolPersonaje(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return "Rastreadora élfica";
        }
        if (tipo == CharacterType.DORATH) {
            return "Clérigo caído";
        }
        return "Espadachín ígneo";
    }

    /**
     * Devuelve los stats base del personaje.
     *
     * @param tipo personaje consultado
     * @return texto con stats base
     */
    private String statsPersonaje(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return "HP 75\nAtaque 12\nMovimiento 5\nRango 3";
        }
        if (tipo == CharacterType.DORATH) {
            return "HP 80\nAtaque 14\nMovimiento 2\nRango 4";
        }
        return "HP 110\nAtaque 18\nMovimiento 3\nRango 1";
    }

    /**
     * Devuelve una descripcion breve del personaje.
     *
     * @param tipo personaje consultado
     * @return descripcion visible
     */
    private String descripcionPersonaje(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return "Rápida y precisa. Controla la distancia y aprovecha su movilidad para sobrevivir.";
        }
        if (tipo == CharacterType.DORATH) {
            return "Frágil pero peligroso a larga distancia. Sus armas mágicas brillan en combates prolongados.";
        }
        return "Resistente y directo. Aguanta mejor los golpes y domina el combate cuerpo a cuerpo.";
    }

    /**
     * Devuelve el color principal del personaje.
     *
     * @param tipo personaje consultado
     * @return color CSS hexadecimal
     */
    private String colorPersonaje(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return "#72b37e";
        }
        if (tipo == CharacterType.DORATH) {
            return "#a58bd5";
        }
        return "#7ba4d8";
    }

    /**
     * Crea el mensaje temporal de confirmacion de partida.
     *
     * @param modelo modelo recien creado
     * @return mensaje visible para el jugador
     */
    private String crearMensajePartidaCreada(GameModel modelo) {
        return modelo.getUltimoMensaje()
            + "\nLa pantalla principal del juego se conectará en el siguiente subbloque.";
    }
}
