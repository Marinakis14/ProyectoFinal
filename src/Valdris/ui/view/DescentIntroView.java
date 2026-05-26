package Valdris.ui.view;

import Valdris.exceptions.GameStateException;
import Valdris.model.enums.CharacterType;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Pantalla de transicion entre la seleccion de personaje y la partida.
 */
public class DescentIntroView {

    /** Ventana principal compartida por la navegacion inicial. */
    private final Stage stage;

    /** Personaje elegido por el jugador. */
    private final CharacterType tipo;

    /** Escena construida para la transicion de descenso. */
    private final Scene scene;

    /**
     * Crea la pantalla previa al inicio real de la partida.
     *
     * @param stage ventana principal de la aplicacion
     * @param tipo personaje elegido
     */
    public DescentIntroView(Stage stage, CharacterType tipo) {
        this.stage = stage;
        this.tipo = tipo;
        this.scene = new Scene(crearContenido(), MainApp.WINDOW_WIDTH, MainApp.WINDOW_HEIGHT);
    }

    /**
     * Devuelve la escena de descenso.
     *
     * @return escena JavaFX de transicion
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Crea una partida real y abre la pantalla principal.
     */
    public void iniciarJuego() {
        try {
            GameModel modelo = new GameModel(tipo);
            GameController controller = new GameController(modelo);
            GameView gameView = new GameView(stage, modelo, controller);
            stage.setScene(gameView.getScene());
        } catch (GameStateException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al crear partida");
            alert.setHeaderText("No se pudo iniciar la partida");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Vuelve a la seleccion de personaje.
     */
    public void volverASeleccion() {
        CharacterSelectView characterSelectView = new CharacterSelectView(stage);
        stage.setScene(characterSelectView.getScene());
    }

    /**
     * Construye el contenido visual de la pantalla.
     *
     * @return panel raiz
     */
    private BorderPane crearContenido() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(58, 78, 52, 78));
        ValdrisTheme.aplicarFondo(root);

        VBox contenido = new VBox(20);
        contenido.setAlignment(Pos.CENTER_LEFT);
        contenido.setMaxWidth(920);
        contenido.setPadding(new Insets(30));
        ValdrisTheme.aplicarPanelDestacado(contenido);

        Label titulo = crearEtiqueta(nombrePersonaje() + " desciende", "Serif", 44, "#f6ead3");
        Label frase = crearEtiqueta(frasePersonaje(), "Serif", 22, colorPersonaje());
        frase.setLineSpacing(4);

        HBox ornamento = ValdrisTheme.crearOrnamentoHorizontal();

        Label preparacion = crearParrafo(
            "Bajo la piedra antigua de Valdris, los pasillos del Nucleo Profundo esperan sin luz. "
                + "Afila tus armas, guarda tus pociones y no confundas el silencio con seguridad."
        );
        Label aviso = crearAviso(
            "Durante la partida, los movimientos disponibles, las acciones de turno y sus teclas asociadas "
                + "aparecen indicados en pantalla para que puedas actuar con rapidez."
        );

        HBox acciones = new HBox(16);
        acciones.setAlignment(Pos.CENTER_LEFT);
        Button entrar = crearBotonPrimario("Entrar en Valdris");
        entrar.setOnAction(event -> iniciarJuego());
        Button volver = crearBotonSecundario("Volver");
        volver.setOnAction(event -> volverASeleccion());
        acciones.getChildren().addAll(entrar, volver);

        contenido.getChildren().addAll(titulo, frase, ornamento, preparacion, aviso, acciones);
        root.setCenter(ValdrisTheme.crearMarcoConEsquinas(contenido));
        return root;
    }

    /**
     * Crea una etiqueta con estilo uniforme.
     *
     * @param texto texto visible
     * @param fuente familia tipografica
     * @param tamano tamanio de fuente
     * @param color color CSS
     * @return etiqueta configurada
     */
    private Label crearEtiqueta(String texto, String fuente, int tamano, String color) {
        Label label = new Label(texto);
        label.setFont(Font.font(fuente, tamano));
        label.setStyle("-fx-text-fill: " + color + ";");
        label.setWrapText(true);
        label.setMaxWidth(920);
        return label;
    }

    /**
     * Crea un parrafo narrativo.
     *
     * @param texto contenido del parrafo
     * @return etiqueta configurada
     */
    private Label crearParrafo(String texto) {
        Label label = crearEtiqueta(texto, "Serif", 21, "#e5dcc8");
        label.setLineSpacing(5);
        return label;
    }

    /**
     * Crea el aviso de controles integrado en la pantalla.
     *
     * @param texto texto del aviso
     * @return etiqueta configurada
     */
    private Label crearAviso(String texto) {
        Label label = crearEtiqueta(texto, "SansSerif", 16, "#d8caa8");
        label.setPadding(new Insets(16, 18, 16, 18));
        label.setStyle(
            "-fx-text-fill: #d8caa8;"
                + "-fx-background-color: rgba(58, 48, 34, 0.72);"
                + "-fx-border-color: #8f7651;"
                + "-fx-border-width: 1;"
        );
        return label;
    }

    /**
     * Crea el boton principal de inicio.
     *
     * @param texto texto visible
     * @return boton configurado
     */
    private Button crearBotonPrimario(String texto) {
        Button button = new Button(texto);
        button.setPrefWidth(220);
        button.setPrefHeight(44);
        button.setFont(Font.font("SansSerif", 15));
        button.setStyle(
            "-fx-background-color: " + colorPersonaje() + ";"
                + "-fx-text-fill: #111111;"
                + "-fx-font-weight: bold;"
                + "-fx-border-color: #f4dfaa;"
                + "-fx-border-width: 1;"
        );
        return button;
    }

    /**
     * Crea el boton secundario de regreso.
     *
     * @param texto texto visible
     * @return boton configurado
     */
    private Button crearBotonSecundario(String texto) {
        Button button = new Button(texto);
        button.setPrefWidth(150);
        button.setPrefHeight(44);
        button.setFont(Font.font("SansSerif", 15));
        ValdrisTheme.aplicarBoton(button);
        return button;
    }

    /**
     * Devuelve el nombre visible del personaje elegido.
     *
     * @return nombre visible
     */
    private String nombrePersonaje() {
        if (tipo == CharacterType.SYRA) {
            return "Syra";
        }
        if (tipo == CharacterType.DORATH) {
            return "Dorath";
        }
        return "Kael";
    }

    /**
     * Devuelve una frase narrativa breve del personaje.
     *
     * @return frase de descenso
     */
    private String frasePersonaje() {
        if (tipo == CharacterType.SYRA) {
            return "La Voz Sin Eco baja primero, siguiendo rastros que ningun mapa se atreve a dibujar.";
        }
        if (tipo == CharacterType.DORATH) {
            return "El Excomulgado cruza el umbral con la certeza amarga de quien ya no teme a la verdad.";
        }
        return "El Portador de la Llama Rota aprieta el guantelete de hierro y acepta la deuda que le queda.";
    }

    /**
     * Devuelve el color principal del personaje elegido.
     *
     * @return color CSS hexadecimal
     */
    private String colorPersonaje() {
        if (tipo == CharacterType.SYRA) {
            return "#72b37e";
        }
        if (tipo == CharacterType.DORATH) {
            return "#a58bd5";
        }
        return "#d18a5a";
    }
}
