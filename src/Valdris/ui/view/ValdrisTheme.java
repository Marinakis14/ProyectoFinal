package Valdris.ui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

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

    /** Color base de la Zona 1: aldea y ruinas grises. */
    private static final Color ZONA_1 = Color.web("#807b70");

    /** Color base de la Zona 2: bosque corrompido. */
    private static final Color ZONA_2 = Color.web("#3f7f49");

    /** Color base de la Zona 3: minas y cristal apagado. */
    private static final Color ZONA_3 = Color.web("#4a7389");

    /** Color base de la Zona 4: torre y magia antigua. */
    private static final Color ZONA_4 = Color.web("#755090");

    /** Color base de la Zona 5: nucleo azul profundo. */
    private static final Color ZONA_5 = Color.web("#1e4f8f");

    /** Color final del pasillo de no retorno hacia el nucleo. */
    private static final Color NUCLEO_PROFUNDO = Color.web("#152c58");

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
     * Aplica a un color base el matiz ambiental de la zona o pasillo indicado.
     *
     * @param roomId identificador de sala
     * @param fila fila de la celda
     * @param col columna de la celda
     * @param filas numero de filas de la sala
     * @param cols numero de columnas de la sala
     * @param base color funcional de la celda
     * @param intensidad peso del matiz ambiental entre 0 y 1
     * @return color final tintado
     */
    public static Color aplicarMatizZona(String roomId, int fila, int col, int filas, int cols,
                                         Color base, double intensidad) {
        double peso = limitar(intensidad, 0.0, 1.0);
        return mezclar(base, colorAmbientalZona(roomId, fila, col, filas, cols), peso);
    }

    /**
     * Devuelve el color de acento principal asociado a una sala o pasillo.
     *
     * @param roomId identificador de sala
     * @return color hexadecimal CSS
     */
    public static String getColorAcentoZona(String roomId) {
        return toHex(colorAmbientalZona(roomId, 0, 0, 1, 1));
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

    /**
     * Calcula el color ambiental de una zona o una transicion entre zonas.
     *
     * @param roomId identificador de sala
     * @param fila fila de la celda
     * @param col columna de la celda
     * @param filas numero de filas de la sala
     * @param cols numero de columnas de la sala
     * @return color ambiental
     */
    private static Color colorAmbientalZona(String roomId, int fila, int col, int filas, int cols) {
        if ("PASILLO_1_2".equals(roomId)) {
            return mezclar(ZONA_1, ZONA_2, progresoPasillo(fila, col, filas, cols));
        }
        if ("PASILLO_2_3".equals(roomId)) {
            return mezclar(ZONA_2, ZONA_3, progresoPasillo(fila, col, filas, cols));
        }
        if ("PASILLO_3_4".equals(roomId)) {
            return mezclar(ZONA_3, ZONA_4, progresoPasillo(fila, col, filas, cols));
        }
        if ("PASILLO_4_5".equals(roomId)) {
            return mezclar(ZONA_4, ZONA_5, progresoPasillo(fila, col, filas, cols));
        }
        if ("PASILLO_FINAL".equals(roomId)) {
            return mezclar(ZONA_5, NUCLEO_PROFUNDO, progresoPasillo(fila, col, filas, cols));
        }
        return colorZona(roomId);
    }

    /**
     * Obtiene el color base de zona a partir del prefijo S1, S2, S3, S4 o S5.
     *
     * @param roomId identificador de sala
     * @return color base de la zona
     */
    private static Color colorZona(String roomId) {
        if (roomId != null) {
            if (roomId.startsWith("S1-")) {
                return ZONA_1;
            }
            if (roomId.startsWith("S2-")) {
                return ZONA_2;
            }
            if (roomId.startsWith("S3-")) {
                return ZONA_3;
            }
            if (roomId.startsWith("S4-")) {
                return ZONA_4;
            }
            if (roomId.startsWith("S5-")) {
                return ZONA_5;
            }
        }
        return Color.web("#8f7651");
    }

    /**
     * Calcula el avance visual dentro de un pasillo de transicion.
     *
     * @param fila fila de la celda
     * @param col columna de la celda
     * @param filas numero de filas
     * @param cols numero de columnas
     * @return valor entre 0 y 1
     */
    private static double progresoPasillo(int fila, int col, int filas, int cols) {
        if (cols >= filas && cols > 1) {
            return limitar((double) col / (double) (cols - 1), 0.0, 1.0);
        }
        if (filas > 1) {
            return limitar((double) fila / (double) (filas - 1), 0.0, 1.0);
        }
        return 0.0;
    }

    /**
     * Mezcla dos colores con un peso controlado.
     *
     * @param origen color inicial
     * @param destino color final
     * @param peso peso del color final
     * @return color mezclado
     */
    private static Color mezclar(Color origen, Color destino, double peso) {
        double p = limitar(peso, 0.0, 1.0);
        double r = origen.getRed() + (destino.getRed() - origen.getRed()) * p;
        double g = origen.getGreen() + (destino.getGreen() - origen.getGreen()) * p;
        double b = origen.getBlue() + (destino.getBlue() - origen.getBlue()) * p;
        double a = origen.getOpacity() + (destino.getOpacity() - origen.getOpacity()) * p;
        return new Color(r, g, b, a);
    }

    /**
     * Limita un valor numerico a un rango.
     *
     * @param valor valor original
     * @param minimo limite inferior
     * @param maximo limite superior
     * @return valor limitado
     */
    private static double limitar(double valor, double minimo, double maximo) {
        if (valor < minimo) {
            return minimo;
        }
        if (valor > maximo) {
            return maximo;
        }
        return valor;
    }

    /**
     * Convierte un color JavaFX a hexadecimal CSS.
     *
     * @param color color consultado
     * @return valor hexadecimal
     */
    private static String toHex(Color color) {
        int r = (int) Math.round(color.getRed() * 255.0);
        int g = (int) Math.round(color.getGreen() * 255.0);
        int b = (int) Math.round(color.getBlue() * 255.0);
        return String.format("#%02x%02x%02x", r, g, b);
    }
}
