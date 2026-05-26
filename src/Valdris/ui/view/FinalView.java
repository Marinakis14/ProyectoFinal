package Valdris.ui.view;

import Valdris.model.enums.GameResult;
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
 * Pantalla final de partida para victoria o derrota.
 */
public class FinalView {

    /** Ventana principal de la aplicacion. */
    private final Stage stage;

    /** Modelo de la partida terminada. */
    private final GameModel modelo;

    /** Controlador asociado. */
    private final GameController controller;

    /** Escena final construida. */
    private final Scene scene;

    /**
     * Crea la pantalla final asociada a una partida terminada.
     *
     * @param stage ventana principal
     * @param modelo modelo de partida
     * @param controller controlador de partida
     */
    public FinalView(Stage stage, GameModel modelo, GameController controller) {
        this.stage = stage;
        this.modelo = modelo;
        this.controller = controller;
        this.scene = new Scene(crearContenido(), MainApp.WINDOW_WIDTH, MainApp.WINDOW_HEIGHT);
    }

    /**
     * Devuelve la escena final.
     *
     * @return escena JavaFX
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Construye el contenido principal.
     *
     * @return panel raiz
     */
    private BorderPane crearContenido() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(42));
        ValdrisTheme.aplicarFondo(root);

        VBox contenido = new VBox(18);
        contenido.setAlignment(Pos.CENTER);
        contenido.setMaxWidth(720);
        contenido.setPadding(new Insets(32));
        ValdrisTheme.aplicarPanelDestacado(contenido);

        Label titulo = new Label(tituloResultado());
        titulo.setFont(Font.font("Serif", 44));
        titulo.setStyle("-fx-text-fill: #f5f0e6;");

        Label personaje = crearTextoSecundario("Personaje: " + modelo.getPlayer().getTipo().name());
        Label sala = crearTextoSecundario("Sala final: " + modelo.getIdSalaActual() + " - " + modelo.getNombreSalaActual());
        Label turno = crearTextoSecundario("Turno alcanzado: " + modelo.getTurnoGlobal());
        Label desenlace = crearTextoPrincipal(textoDesenlace());
        Label frase = crearTextoSecundario(textoFraseFinal());

        HBox acciones = new HBox(12);
        acciones.setAlignment(Pos.CENTER);
        Button exportar = crearBoton("Exportar resumen");
        exportar.setOnAction(event -> exportarResumen());
        Button menu = crearBoton("Menú principal");
        menu.setOnAction(event -> controller.onBotonMenuPrincipal(stage));
        acciones.getChildren().addAll(exportar, menu);

        contenido.getChildren().addAll(titulo, ValdrisTheme.crearOrnamentoHorizontal(), personaje, sala, turno, desenlace);
        if (frase.getText() != null && !frase.getText().isEmpty()) {
            contenido.getChildren().add(frase);
        }
        contenido.getChildren().add(acciones);

        root.setCenter(ValdrisTheme.crearMarcoConEsquinas(contenido));
        return root;
    }

    /**
     * Devuelve el titulo visible segun el resultado.
     *
     * @return titulo de resultado
     */
    private String tituloResultado() {
        if (modelo.getResultadoPartida() == GameResult.VICTORY) {
            return "Victoria";
        }
        return "Derrota";
    }

    /**
     * Devuelve el texto principal del desenlace.
     *
     * @return texto narrativo final
     */
    private String textoDesenlace() {
        if (modelo.getResultadoPartida() == GameResult.DEFEAT && modelo.getMotivoDerrota() != null) {
            return modelo.getMotivoDerrota();
        }
        if (modelo.getTextoDesenlace() != null) {
            return modelo.getTextoDesenlace();
        }
        return "La partida ha terminado.";
    }

    /**
     * Devuelve la frase final de Malachar si existe.
     *
     * @return frase final formateada o cadena vacia
     */
    private String textoFraseFinal() {
        String frase = modelo.getFraseFinal();
        if (frase == null || frase.isEmpty()) {
            return "";
        }
        return "Malachar: \"" + frase + "\"";
    }

    /**
     * Exporta el resumen final y comunica el resultado al jugador.
     */
    private void exportarResumen() {
        if (controller.onExportarResumenFinal()) {
            mostrarInfo("Resumen exportado", "Resumen guardado en " + GameModel.SUMMARY_PATH + ".");
        } else {
            mostrarError("No se pudo exportar el resumen", modelo.getUltimoMensaje());
        }
    }

    /**
     * Crea un texto principal con ajuste de linea.
     *
     * @param texto texto visible
     * @return etiqueta configurada
     */
    private Label crearTextoPrincipal(String texto) {
        Label label = new Label(texto);
        label.setWrapText(true);
        label.setMaxWidth(680);
        label.setFont(Font.font("Serif", 24));
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-text-fill: #f5f0e6;");
        return label;
    }

    /**
     * Crea un texto secundario con ajuste de linea.
     *
     * @param texto texto visible
     * @return etiqueta configurada
     */
    private Label crearTextoSecundario(String texto) {
        Label label = new Label(texto);
        label.setWrapText(true);
        label.setMaxWidth(680);
        label.setFont(Font.font("SansSerif", 16));
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-text-fill: #c9b99c;");
        return label;
    }

    /**
     * Crea un boton de accion final.
     *
     * @param texto texto visible
     * @return boton configurado
     */
    private Button crearBoton(String texto) {
        Button button = new Button(texto);
        button.setPrefWidth(190);
        button.setPrefHeight(42);
        button.setFont(Font.font("SansSerif", 15));
        ValdrisTheme.aplicarBoton(button);
        return button;
    }

    /**
     * Muestra un aviso informativo.
     *
     * @param titulo titulo del aviso
     * @param mensaje contenido del aviso
     */
    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un aviso de error.
     *
     * @param titulo titulo del error
     * @param mensaje detalle del error
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje == null ? "Error desconocido." : mensaje);
        alert.showAndWait();
    }
}
