package Valdris.ui.view;

import Valdris.ui.MainApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Pantalla narrativa inicial que presenta el mundo antes de elegir personaje.
 */
public class StoryIntroView {

    /** Ventana principal compartida por la navegacion inicial. */
    private final Stage stage;

    /** Escena construida para la introduccion narrativa. */
    private final Scene scene;

    /**
     * Crea la pantalla de historia inicial.
     *
     * @param stage ventana principal de la aplicacion
     */
    public StoryIntroView(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(crearContenido(), MainApp.WINDOW_WIDTH, MainApp.WINDOW_HEIGHT);
    }

    /**
     * Devuelve la escena de introduccion narrativa.
     *
     * @return escena JavaFX de historia
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Avanza hacia la seleccion de personaje.
     */
    public void continuarASeleccion() {
        CharacterSelectView characterSelectView = new CharacterSelectView(stage);
        stage.setScene(characterSelectView.getScene());
    }

    /**
     * Vuelve al menu principal.
     */
    public void volverAlMenu() {
        MainMenuView mainMenuView = new MainMenuView(stage);
        stage.setScene(mainMenuView.getScene());
    }

    /**
     * Construye el contenido visual de la pantalla.
     *
     * @return panel raiz
     */
    private BorderPane crearContenido() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(52, 76, 46, 76));
        ValdrisTheme.aplicarFondo(root);

        VBox contenido = new VBox(22);
        contenido.setAlignment(Pos.CENTER_LEFT);
        contenido.setMaxWidth(920);
        contenido.setPadding(new Insets(30));
        ValdrisTheme.aplicarPanelDestacado(contenido);

        Label marca = crearEtiqueta("Valdris: El Núcleo Profundo", "Serif", 46, "#f6ead3");
        Label subtitulo = crearEtiqueta("El sello se apaga. Algo bajo la tierra vuelve a respirar.", "SansSerif", 18, "#c9b99c");

        HBox ornamento = ValdrisTheme.crearOrnamentoHorizontal();

        Label parrafoUno = crearParrafo(
            "Hace trescientos años, los cinco reinos encerraron a Malachar bajo el corazón del continente. "
                + "Lo llamaron monstruo, levantaron una fortaleza sobre su tumba y dejaron cinco guardianes "
                + "manteniendo vivo el sello con su propia sangre."
        );
        Label parrafoDos = crearParrafo(
            "Ahora los guardianes han muerto. Los campos se han vuelto grises, los bosques crecen hacia abajo "
                + "y las criaturas mágicas atacan como si algo les estuviera arrancando el alma desde las profundidades."
        );
        Label parrafoTres = crearParrafo(
            "Los reinos discuten mientras Valdris se hunde. Solo queda descender al Núcleo Profundo, atravesar sus "
                + "salas selladas y descubrir si Malachar fue la amenaza... o la última advertencia."
        );

        HBox acciones = new HBox(16);
        acciones.setAlignment(Pos.CENTER_LEFT);
        Button continuar = crearBotonPrimario("Elegir quién descenderá");
        continuar.setOnAction(event -> continuarASeleccion());
        Button volver = crearBotonSecundario("Volver");
        volver.setOnAction(event -> volverAlMenu());
        acciones.getChildren().addAll(continuar, volver);

        contenido.getChildren().addAll(marca, subtitulo, ornamento, parrafoUno, parrafoDos, parrafoTres, acciones);
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
        return label;
    }

    /**
     * Crea un parrafo narrativo.
     *
     * @param texto contenido del parrafo
     * @return etiqueta configurada
     */
    private Label crearParrafo(String texto) {
        Label label = crearEtiqueta(texto, "Serif", 20, "#e5dcc8");
        label.setLineSpacing(5);
        label.setMaxWidth(900);
        return label;
    }

    /**
     * Crea el boton principal de avance.
     *
     * @param texto texto visible
     * @return boton configurado
     */
    private Button crearBotonPrimario(String texto) {
        Button button = new Button(texto);
        button.setPrefWidth(260);
        button.setPrefHeight(44);
        button.setFont(Font.font("SansSerif", 15));
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #d4b16e 0%, #a77c3d 100%);"
                + "-fx-text-fill: #15120d;"
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
}
