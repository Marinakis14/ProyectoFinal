package Valdris.ui.view;

import Valdris.exceptions.GameStateException;
import Valdris.persistence.GameState;
import Valdris.persistence.GameSummary;
import Valdris.ui.controller.GameController;
import Valdris.ui.model.GameModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Ventana modal que muestra el resumen final de una partida terminada.
 */
public class FinalSummaryView {

    /** Ventana propietaria. */
    private final Stage owner;

    /** Modelo de partida finalizada. */
    private final GameModel modelo;

    /** Controlador de acciones finales. */
    private final GameController controller;

    /**
     * Crea la ventana de resumen final.
     *
     * @param owner ventana principal
     * @param modelo modelo de partida
     * @param controller controlador asociado
     */
    public FinalSummaryView(Stage owner, GameModel modelo, GameController controller) {
        this.owner = owner;
        this.modelo = modelo;
        this.controller = controller;
    }

    /**
     * Muestra el resumen final de la partida.
     */
    public void show() {
        GameSummary summary;
        try {
            summary = modelo.crearResumenFinal();
        } catch (GameStateException e) {
            mostrarError("No se pudo crear el resumen", e.getMessage());
            return;
        }

        Stage dialogo = new Stage();
        dialogo.setTitle("Resumen de la partida");
        if (owner != null) {
            dialogo.initOwner(owner);
        }
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setResizable(true);

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));
        ValdrisTheme.aplicarFondo(root);

        VBox panel = new VBox(14);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(820);
        panel.setPadding(new Insets(26));
        ValdrisTheme.aplicarPanelDestacado(panel);

        Label titulo = crearTitulo("Resumen de la partida");
        Label subtitulo = crearSubtitulo("El eco de lo vivido queda reunido antes de abandonar el Núcleo.");
        ScrollPane scroll = crearScrollResumen(summary);

        HBox acciones = new HBox(12);
        acciones.setAlignment(Pos.CENTER);
        Button salir = crearBoton("Salir");
        salir.setOnAction(event -> dialogo.close());
        Button exportar = crearBoton("Exportar resumen");
        exportar.setOnAction(event -> exportarResumen());
        acciones.getChildren().addAll(salir, exportar);

        panel.getChildren().addAll(titulo, ValdrisTheme.crearOrnamentoHorizontal(), subtitulo,
            scroll, ValdrisTheme.crearSeparador(), acciones);
        root.getChildren().add(ValdrisTheme.crearMarcoConEsquinas(panel));

        Scene scene = new Scene(root, 940, 700);
        dialogo.setScene(scene);
        dialogo.showAndWait();
    }

    /**
     * Crea el bloque desplazable con los datos del resumen.
     *
     * @param summary resumen que se muestra
     * @return scroll configurado
     */
    private ScrollPane crearScrollResumen(GameSummary summary) {
        VBox contenido = new VBox(12);
        contenido.setPadding(new Insets(6, 12, 6, 6));
        contenido.setFillWidth(true);

        contenido.getChildren().add(crearSeccion("Resultado"));
        contenido.getChildren().add(crearLineaDato("Estado", textoResultado(summary.gameResult)));
        contenido.getChildren().add(crearLineaDato("Personaje", valor(summary.tipoPersonaje)));
        contenido.getChildren().add(crearLineaDato("Sala final", valor(summary.idRoomActual)));
        contenido.getChildren().add(crearLineaDato("Turno alcanzado", String.valueOf(summary.turnoGlobal)));
        contenido.getChildren().add(crearLineaDato("HP final", String.valueOf(summary.hpJugador)));

        contenido.getChildren().add(crearSeccion("Desenlace"));
        contenido.getChildren().add(crearParrafo(valor(summary.endingText)));
        if (summary.finalQuote != null && !summary.finalQuote.isEmpty()) {
            contenido.getChildren().add(crearParrafo("Malachar: \"" + summary.finalQuote + "\""));
        }
        if (summary.defeatReason != null && !summary.defeatReason.isEmpty()) {
            contenido.getChildren().add(crearParrafo("Causa de derrota: " + summary.defeatReason));
        }

        contenido.getChildren().add(crearSeccion("Inventario y exploración"));
        contenido.getChildren().add(crearLineaDato("Inventario", textoArray(summary.itemsInventario)));
        contenido.getChildren().add(crearLineaDato("Objetos narrativos", textoArray(summary.itemsNarrativos)));
        contenido.getChildren().add(crearLineaDato("Salas exploradas", textoArray(summary.salasExploradas)));

        contenido.getChildren().add(crearSeccion("Registro de la partida"));
        contenido.getChildren().add(crearParrafo(textoLog(summary.logEventos)));

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(430);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        return scroll;
    }

    /**
     * Exporta el resumen final desde la ventana modal.
     */
    private void exportarResumen() {
        if (controller.onExportarResumenFinal()) {
            ValdrisTheme.mostrarDialogoNarrativo(owner, "Resumen exportado", "Archivo de viaje",
                "El resumen ha quedado guardado en " + GameModel.SUMMARY_PATH + ".");
        } else {
            mostrarError("No se pudo exportar el resumen", modelo.getUltimoMensaje());
        }
    }

    /**
     * Crea un titulo principal.
     *
     * @param texto texto visible
     * @return etiqueta configurada
     */
    private Label crearTitulo(String texto) {
        Label label = new Label(texto);
        label.setWrapText(true);
        label.setMaxWidth(760);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font("Serif", 36));
        label.setStyle("-fx-text-fill: #f5f0e6;");
        return label;
    }

    /**
     * Crea un subtitulo.
     *
     * @param texto texto visible
     * @return etiqueta configurada
     */
    private Label crearSubtitulo(String texto) {
        Label label = new Label(texto);
        label.setWrapText(true);
        label.setMaxWidth(760);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font("SansSerif", 15));
        label.setStyle("-fx-text-fill: #c9b99c;");
        return label;
    }

    /**
     * Crea una cabecera de seccion.
     *
     * @param texto texto visible
     * @return etiqueta configurada
     */
    private Label crearSeccion(String texto) {
        Label label = new Label(texto);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setFont(Font.font("Serif", 22));
        label.setStyle("-fx-text-fill: #f5f0e6; -fx-border-color: transparent transparent #8f7651 transparent;"
            + "-fx-border-width: 0 0 1 0; -fx-padding: 10 0 4 0;");
        return label;
    }

    /**
     * Crea una linea compacta de dato.
     *
     * @param clave nombre del dato
     * @param valor valor mostrado
     * @return etiqueta configurada
     */
    private Label crearLineaDato(String clave, String valor) {
        Label label = new Label(clave + ": " + valor);
        label.setWrapText(true);
        label.setMaxWidth(760);
        label.setFont(Font.font("SansSerif", 15));
        label.setStyle("-fx-text-fill: #d8c8aa;");
        return label;
    }

    /**
     * Crea un parrafo narrativo.
     *
     * @param texto texto visible
     * @return etiqueta configurada
     */
    private Label crearParrafo(String texto) {
        Label label = new Label(texto);
        label.setWrapText(true);
        label.setMaxWidth(760);
        label.setFont(Font.font("Serif", 18));
        label.setStyle("-fx-text-fill: #f5f0e6; -fx-line-spacing: 4;");
        return label;
    }

    /**
     * Crea un boton de accion.
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
     * Convierte un resultado interno a texto visible.
     *
     * @param resultado resultado serializado
     * @return texto visible
     */
    private String textoResultado(String resultado) {
        if ("VICTORY".equals(resultado)) {
            return "Victoria";
        }
        if ("DEFEAT".equals(resultado)) {
            return "Derrota";
        }
        return valor(resultado);
    }

    /**
     * Convierte un array de texto a una linea legible.
     *
     * @param valores valores consultados
     * @return texto visible
     */
    private String textoArray(String[] valores) {
        if (valores == null || valores.length == 0) {
            return "-";
        }
        String texto = "";
        for (int i = 0; i < valores.length; i++) {
            if (valores[i] == null || valores[i].isEmpty()) {
                continue;
            }
            if (!texto.isEmpty()) {
                texto += ", ";
            }
            texto += valores[i];
        }
        return texto.isEmpty() ? "-" : texto;
    }

    /**
     * Convierte el log estructurado completo a texto legible.
     *
     * @param eventos eventos registrados
     * @return texto visible del log
     */
    private String textoLog(GameState.GameLogEntryDTO[] eventos) {
        if (eventos == null || eventos.length == 0) {
            return "-";
        }
        String texto = "";
        for (int i = 0; i < eventos.length; i++) {
            GameState.GameLogEntryDTO evento = eventos[i];
            if (evento == null || evento.mensaje == null) {
                continue;
            }
            if (!texto.isEmpty()) {
                texto += "\n";
            }
            texto += "[T" + evento.turno + "] " + valor(evento.tipo) + " | " + valor(evento.actor)
                + " | " + valor(evento.salaId) + " - " + evento.mensaje;
        }
        return texto.isEmpty() ? "-" : texto;
    }

    /**
     * Normaliza valores vacios.
     *
     * @param texto texto consultado
     * @return texto visible
     */
    private String valor(String texto) {
        return texto == null || texto.isEmpty() ? "-" : texto;
    }

    /**
     * Muestra un error con estetica narrativa.
     *
     * @param titulo titulo del error
     * @param mensaje mensaje visible
     */
    private void mostrarError(String titulo, String mensaje) {
        ValdrisTheme.mostrarDialogoNarrativo(owner, titulo, "El archivo no responde",
            mensaje == null ? "Error desconocido." : mensaje);
    }
}
