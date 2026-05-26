package Valdris.ui.view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Panel inferior que muestra los ultimos mensajes de la partida.
 */
public class CombatLogView {

    /** Numero maximo de mensajes visibles. */
    private static final int MAX_MENSAJES = 5;

    /** Contenedor visual del log. */
    private final VBox root;

    /** Etiquetas visibles del log. */
    private final Label[] labels;

    /**
     * Crea el panel de log con cinco lineas vacias.
     */
    public CombatLogView() {
        this.root = new VBox(4);
        this.root.setPadding(new Insets(10, 10, 46, 10));
        this.root.setStyle("-fx-background-color: #111111; -fx-border-color: #3b3429; -fx-border-width: 1 0 0 0;");
        this.root.setPrefHeight(170);
        this.root.setMinHeight(150);
        this.labels = new Label[MAX_MENSAJES];
        agregarTitulo();
        inicializarLabels();
    }

    /**
     * Devuelve el nodo raiz del log.
     *
     * @return nodo JavaFX del panel
     */
    public Node getNode() {
        return root;
    }

    /**
     * Anade un mensaje desplazando los anteriores hacia arriba.
     *
     * @param mensaje mensaje que se muestra
     */
    public void addMensaje(String mensaje) {
        if (mensaje == null || mensaje.isEmpty()) {
            return;
        }
        for (int i = 0; i < labels.length - 1; i++) {
            labels[i].setText(labels[i + 1].getText());
        }
        labels[labels.length - 1].setText(mensaje);
    }

    /**
     * Muestra los ultimos mensajes recibidos desde el modelo.
     *
     * @param mensajes mensajes completos del log
     */
    public void mostrarMensajes(String[] mensajes) {
        limpiar();
        if (mensajes == null || mensajes.length == 0) {
            return;
        }
        int inicio = mensajes.length - MAX_MENSAJES;
        if (inicio < 0) {
            inicio = 0;
        }
        for (int i = inicio; i < mensajes.length; i++) {
            addMensaje(mensajes[i]);
        }
    }

    /**
     * Limpia todas las lineas visibles.
     */
    public void limpiar() {
        for (int i = 0; i < labels.length; i++) {
            labels[i].setText("");
        }
    }

    /**
     * Inicializa las etiquetas del panel.
     */
    private void inicializarLabels() {
        for (int i = 0; i < labels.length; i++) {
            Label label = new Label("");
            label.setFont(Font.font("Monospaced", 12));
            label.setStyle("-fx-text-fill: #d7c8aa;");
            label.setMinHeight(18);
            label.setWrapText(true);
            label.setMaxWidth(Double.MAX_VALUE);
            labels[i] = label;
            root.getChildren().add(label);
        }
    }

    /**
     * Agrega el titulo visible del panel de log.
     */
    private void agregarTitulo() {
        Label titulo = new Label("Log de acciones");
        titulo.setFont(Font.font("Serif", 17));
        titulo.setStyle("-fx-text-fill: #f5f0e6;");
        root.getChildren().add(titulo);
    }
}
