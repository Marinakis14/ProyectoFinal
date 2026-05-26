package Valdris.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Utilidades visuales compartidas para mantener una estetica coherente en JavaFX.
 */
public final class ValdrisTheme {

    /** Fondo oscuro con matiz de piedra y cobre apagado. */
    public static final String FONDO_PROFUNDO =
        "-fx-background-color: linear-gradient(to bottom, #10100f 0%, #17130f 54%, #24180f 100%);";

    /** Estilo comun para paneles de informacion. */
    public static final String PANEL =
        "-fx-background-color: linear-gradient(to bottom, #28231c 0%, #202020 100%);"
            + "-fx-border-color: #8f7651 #3b3429 #5e5140 #3b3429;"
            + "-fx-border-width: 1;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 10, 0.2, 0, 2);";

    /** Estilo comun para paneles principales con marco mas visible. */
    public static final String PANEL_DESTACADO =
        "-fx-background-color: linear-gradient(to bottom, #2d251b 0%, #181818 100%);"
            + "-fx-border-color: #b4935b #5e5140 #8f7651 #5e5140;"
            + "-fx-border-width: 1.5;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.45), 14, 0.25, 0, 3);";

    /**
     * Constructor privado para impedir instanciacion de clase utilitaria.
     */
    private ValdrisTheme() {
    }

    /**
     * Aplica el fondo comun de Valdris a una region.
     *
     * @param region region que recibe el estilo
     */
    public static void aplicarFondo(Region region) {
        region.setStyle(FONDO_PROFUNDO);
    }

    /**
     * Aplica el estilo comun de panel a una region.
     *
     * @param region region que recibe el estilo
     */
    public static void aplicarPanel(Region region) {
        region.setStyle(PANEL);
    }

    /**
     * Aplica el estilo de panel destacado a una region.
     *
     * @param region region que recibe el estilo
     */
    public static void aplicarPanelDestacado(Region region) {
        region.setStyle(PANEL_DESTACADO);
    }

    /**
     * Aplica el estilo comun de boton de fantasia sobria.
     *
     * @param button boton que recibe el estilo
     */
    public static void aplicarBoton(Button button) {
        button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #4a3b28 0%, #33291d 100%);"
                + "-fx-text-fill: #f5f0e6;"
                + "-fx-border-color: #9b7f50;"
                + "-fx-border-width: 1;"
                + "-fx-font-weight: bold;"
        );
    }

    /**
     * Crea un separador decorativo horizontal.
     *
     * @return region separadora
     */
    public static Region crearSeparador() {
        Region region = new Region();
        region.setMinHeight(2);
        region.setPrefHeight(2);
        region.setMaxHeight(2);
        region.setStyle(
            "-fx-background-color: linear-gradient(to right, transparent 0%, #8f7651 18%, #c9a45f 50%, #8f7651 82%, transparent 100%);"
        );
        return region;
    }

    /**
     * Crea una ornamentacion discreta para cabeceras o huecos sin texto.
     *
     * @return fila ornamental
     */
    public static HBox crearOrnamentoHorizontal() {
        HBox box = new HBox(9);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(2, 0, 2, 0));

        Region izquierda = crearLineaOrnamental();
        Region centro = new Region();
        centro.setPrefSize(10, 10);
        centro.setMinSize(10, 10);
        centro.setMaxSize(10, 10);
        centro.setRotate(45);
        centro.setStyle("-fx-background-color: #c9a45f; -fx-border-color: #5e3f20; -fx-border-width: 1;");
        Region derecha = crearLineaOrnamental();

        box.getChildren().addAll(izquierda, centro, derecha);
        return box;
    }

    /**
     * Envuelve un contenido con esquinas ornamentales ligeras.
     *
     * @param contenido nodo principal
     * @return marco visual con esquinas
     */
    public static StackPane crearMarcoConEsquinas(Node contenido) {
        StackPane marco = new StackPane();
        marco.setPadding(new Insets(14));
        marco.getChildren().add(contenido);

        Region superiorIzquierda = crearEsquina();
        Region superiorDerecha = crearEsquina();
        Region inferiorDerecha = crearEsquina();
        Region inferiorIzquierda = crearEsquina();

        superiorDerecha.setRotate(90);
        inferiorDerecha.setRotate(180);
        inferiorIzquierda.setRotate(270);

        StackPane.setAlignment(superiorIzquierda, Pos.TOP_LEFT);
        StackPane.setAlignment(superiorDerecha, Pos.TOP_RIGHT);
        StackPane.setAlignment(inferiorDerecha, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(inferiorIzquierda, Pos.BOTTOM_LEFT);

        marco.getChildren().addAll(superiorIzquierda, superiorDerecha, inferiorDerecha, inferiorIzquierda);
        return marco;
    }

    /**
     * Crea una linea ornamental usada por las cabeceras.
     *
     * @return region de linea
     */
    private static Region crearLineaOrnamental() {
        Region region = new Region();
        region.setPrefWidth(96);
        region.setMinWidth(48);
        region.setPrefHeight(2);
        region.setMaxHeight(2);
        region.setStyle("-fx-background-color: linear-gradient(to right, transparent, #8f7651);");
        return region;
    }

    /**
     * Crea una esquina ornamental sencilla para marcos.
     *
     * @return region de esquina
     */
    private static Region crearEsquina() {
        Region region = new Region();
        region.setPrefSize(42, 42);
        region.setMinSize(42, 42);
        region.setMaxSize(42, 42);
        region.setStyle("-fx-border-color: #c9a45f; -fx-border-width: 2 0 0 2; -fx-background-color: transparent;");
        return region;
    }
}
