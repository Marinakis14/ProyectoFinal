package Valdris.ui.view;

import Valdris.model.enums.CharacterType;
import Valdris.ui.MainApp;
import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        card.setPadding(new Insets(18, 20, 20, 20));
        card.setPrefWidth(310);
        card.setMinHeight(540);
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #2d251b 0%, #1e1e1e 100%);"
                + "-fx-border-color: " + colorPersonaje(tipo) + ";"
                + "-fx-border-width: 2;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.38), 10, 0.2, 0, 2);"
        );

        ImageView retrato = crearRetrato(tipo);

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

        card.getChildren().addAll(retrato, nombre, rol, stats, descripcion, elegir);
        return card;
    }

    /**
     * Abre la pantalla narrativa previa a la partida real.
     *
     * @param tipo personaje elegido
     */
    public void iniciarJuego(CharacterType tipo) {
        DescentIntroView descentIntroView = new DescentIntroView(stage, tipo);
        stage.setScene(descentIntroView.getScene());
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
        root.setPadding(new Insets(28, 36, 28, 36));
        ValdrisTheme.aplicarFondo(root);

        VBox cabecera = new VBox(8);
        cabecera.setAlignment(Pos.CENTER);
        Label titulo = new Label("Elige tu personaje");
        titulo.setFont(Font.font("Serif", 36));
        titulo.setStyle("-fx-text-fill: #f5f0e6;");
        Label subtitulo = new Label("Tres voluntades distintas. Un unico descenso hacia el Nucleo Profundo.");
        subtitulo.setFont(Font.font("SansSerif", 15));
        subtitulo.setStyle("-fx-text-fill: #c9b99c;");
        cabecera.getChildren().addAll(titulo, subtitulo, ValdrisTheme.crearOrnamentoHorizontal());

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
        ValdrisTheme.aplicarBoton(volver);

        VBox centro = new VBox(28);
        centro.setAlignment(Pos.CENTER);
        centro.getChildren().addAll(cabecera, personajes, volver);
        root.setCenter(ValdrisTheme.crearMarcoConEsquinas(centro));
        return root;
    }

    /**
     * Crea el retrato visual del personaje desde la carpeta de imagenes.
     *
     * @param tipo personaje representado
     * @return vista de imagen configurada
     */
    private ImageView crearRetrato(CharacterType tipo) {
        File archivo = new File(rutaImagenPersonaje(tipo));
        Image imagen = new Image(archivo.toURI().toString(), 245, 210, true, true);
        ImageView imageView = new ImageView(imagen);
        imageView.setFitWidth(245);
        imageView.setFitHeight(210);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        return imageView;
    }

    /**
     * Devuelve la ruta local del retrato asociado al personaje.
     *
     * @param tipo personaje consultado
     * @return ruta relativa de la imagen
     */
    private String rutaImagenPersonaje(CharacterType tipo) {
        if (tipo == CharacterType.SYRA) {
            return "imagenes/Syra sin fondo.png";
        }
        if (tipo == CharacterType.DORATH) {
            return "imagenes/Dorath sin fondo.png";
        }
        return "imagenes/Kael sin fondo.png";
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
            return "HP 75\nAtaque 22\nDefensa 3\nMovimiento 4\nRango 3";
        }
        if (tipo == CharacterType.DORATH) {
            return "HP 80\nAtaque 24\nDefensa 3\nMovimiento 2\nRango 4";
        }
        return "HP 110\nAtaque 28\nDefensa 3\nMovimiento 3\nRango 1";
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

}
