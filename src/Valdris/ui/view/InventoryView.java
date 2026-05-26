package Valdris.ui.view;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.model.enums.Phase;
import Valdris.model.items.Item;
import Valdris.model.units.Player;
import Valdris.ui.controller.GameController;
import Valdris.ui.model.GameModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Ventana modal de inventario del jugador.
 *
 * <p>Puede abrirse en cualquier fase. Fuera de {@link Phase#USE_ITEM} funciona
 * como consulta. En fase de uso de item permite usar o equipar un item del
 * inventario normal y cierra la ventana si la accion se resuelve correctamente.</p>
 */
public class InventoryView {

    /** Ancho de la ventana modal. */
    private static final int WINDOW_WIDTH = 820;

    /** Alto de la ventana modal. */
    private static final int WINDOW_HEIGHT = 560;

    /** Modelo consultado por la ventana. */
    private final GameModel modelo;

    /** Controlador que ejecuta acciones de item. */
    private final GameController controller;

    /** Ventana modal. */
    private final Stage stage;

    /** Raiz visual. */
    private final BorderPane root;

    /**
     * Crea la ventana modal de inventario.
     *
     * @param modelo modelo de partida
     * @param controller controlador principal
     * @param owner ventana propietaria
     */
    public InventoryView(GameModel modelo, GameController controller, Stage owner) {
        this.modelo = modelo;
        this.controller = controller;
        this.stage = new Stage();
        this.root = new BorderPane();

        stage.setTitle("Inventario");
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));

        construirLayout();
    }

    /**
     * Muestra la ventana modal.
     */
    public void show() {
        stage.showAndWait();
    }

    /**
     * Construye el contenido del inventario.
     */
    private void construirLayout() {
        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #171717;");

        Label titulo = new Label("Inventario");
        titulo.setFont(Font.font("Serif", 28));
        titulo.setStyle("-fx-text-fill: #f5f0e6;");

        Label subtitulo = new Label(textoModo());
        subtitulo.setStyle("-fx-text-fill: #c9b99c;");

        VBox cabecera = new VBox(4, titulo, subtitulo);
        cabecera.setPadding(new Insets(0, 0, 14, 0));
        root.setTop(cabecera);

        HBox contenido = new HBox(18);
        contenido.getChildren().addAll(mostrarSilueta(), mostrarInventario());
        root.setCenter(contenido);

        Button cerrar = crearBoton("Cerrar");
        cerrar.setOnAction(event -> stage.close());
        HBox pie = new HBox(cerrar);
        pie.setAlignment(Pos.CENTER_RIGHT);
        pie.setPadding(new Insets(14, 0, 0, 0));
        root.setBottom(pie);
    }

    /**
     * Muestra el equipo actual del jugador.
     *
     * @return nodo de equipo
     */
    public Node mostrarSilueta() {
        Player player = modelo.getPlayer();
        VBox equipo = new VBox(10);
        equipo.setPadding(new Insets(14));
        equipo.setPrefWidth(285);
        equipo.setStyle("-fx-background-color: #242424; -fx-border-color: #3b3429;");

        Label titulo = crearTituloSeccion("Equipo");
        equipo.getChildren().add(titulo);
        equipo.getChildren().add(crearSlot("Arma", player.getArmaEquipada()));
        equipo.getChildren().add(crearSlot("Escudo", player.getEscudoEquipado()));
        equipo.getChildren().add(crearSlot("Armadura", player.getArmaduraEquipada()));
        equipo.getChildren().add(crearSlot("Accesorio", player.getAccesorioEquipado()));
        equipo.getChildren().add(crearSeparador());
        equipo.getChildren().add(crearDato("Ataque", String.valueOf(player.getAtaqueTotal())));
        equipo.getChildren().add(crearDato("Defensa", String.valueOf(player.getDefensaTotal())));
        equipo.getChildren().add(crearDato("Movimiento", String.valueOf(player.getMovEfectivo())));
        equipo.getChildren().add(crearDato("Rango", String.valueOf(player.getRangoEfectivo())));

        return equipo;
    }

    /**
     * Muestra inventario normal e items narrativos.
     *
     * @return nodo de inventario
     */
    public Node mostrarInventario() {
        VBox contenido = new VBox(12);
        contenido.setPadding(new Insets(14));
        contenido.setPrefWidth(465);
        contenido.setStyle("-fx-background-color: #242424; -fx-border-color: #3b3429;");

        contenido.getChildren().add(crearTituloSeccion("Objetos"));
        agregarListaItems(contenido, modelo.getPlayer().getInventario(), true);
        contenido.getChildren().add(crearSeparador());
        contenido.getChildren().add(crearTituloSeccion("Objetos narrativos"));
        agregarListaItems(contenido, modelo.getPlayer().getItemsNarrativos(), false);

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #242424; -fx-background-color: #242424;");
        return scroll;
    }

    /**
     * Agrega los items de una lista a un contenedor.
     *
     * @param contenedor contenedor visual
     * @param items lista de items
     * @param permitirUso true si puede mostrarse el boton de uso
     */
    private void agregarListaItems(VBox contenedor, ListaSimplementeEnlazada<Item> items, boolean permitirUso) {
        if (items == null || items.getSize() == 0) {
            Label vacio = new Label("Sin objetos.");
            vacio.setStyle("-fx-text-fill: #8f8575;");
            contenedor.getChildren().add(vacio);
            return;
        }

        for (int i = 0; i < items.getSize(); i++) {
            Item item = items.get(i);
            if (item != null && esPrimeraAparicion(items, i)) {
                contenedor.getChildren().add(crearFilaItem(item, contarItems(items, item.getId()), permitirUso));
            }
        }
    }

    /**
     * Crea la fila visual de un item.
     *
     * @param item item mostrado
     * @param cantidad cantidad agrupada del item
     * @param permitirUso true si puede mostrarse boton de uso
     * @return fila visual
     */
    private Node crearFilaItem(Item item, int cantidad, boolean permitirUso) {
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(8));
        fila.setStyle("-fx-background-color: #1d1d1d; -fx-border-color: #3b3429;");

        VBox textos = new VBox(3);
        textos.setPrefWidth(300);

        Label nombre = new Label(item.getNombre() + " [" + item.getTipo() + "]");
        nombre.setStyle("-fx-text-fill: #f5f0e6; -fx-font-weight: bold;");

        Label descripcion = new Label(item.getDescripcion());
        descripcion.setWrapText(true);
        descripcion.setStyle("-fx-text-fill: #c9b99c;");
        Label cantidadLabel = new Label("Cantidad: " + cantidad);
        cantidadLabel.setStyle("-fx-text-fill: #d7c8aa;");
        textos.getChildren().addAll(nombre, cantidadLabel, descripcion);

        fila.getChildren().add(textos);

        if (permitirUso) {
            Button usar = crearBoton("Usar");
            usar.setDisable(modelo.getTurnManager().getFaseActual() != Phase.USE_ITEM);
            usar.setOnAction(event -> usarItem(item));
            fila.getChildren().add(usar);
        }

        return fila;
    }

    /**
     * Indica si un item es la primera aparicion de su ID dentro de la lista.
     *
     * @param items lista consultada
     * @param indice indice del item actual
     * @return true si no existe otro item con el mismo ID antes
     */
    private boolean esPrimeraAparicion(ListaSimplementeEnlazada<Item> items, int indice) {
        Item item = items.get(indice);
        if (item == null) {
            return false;
        }
        for (int i = 0; i < indice; i++) {
            Item anterior = items.get(i);
            if (anterior != null && item.getId().equals(anterior.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cuenta cuantas unidades de un item existen en una lista.
     *
     * @param items lista consultada
     * @param id id del item
     * @return cantidad encontrada
     */
    private int contarItems(ListaSimplementeEnlazada<Item> items, String id) {
        int cantidad = 0;
        if (items == null || id == null) {
            return cantidad;
        }
        for (int i = 0; i < items.getSize(); i++) {
            Item item = items.get(i);
            if (item != null && id.equals(item.getId())) {
                cantidad++;
            }
        }
        return cantidad;
    }

    /**
     * Usa un item y cierra el modal si la accion fue correcta.
     *
     * @param item item seleccionado
     */
    private void usarItem(Item item) {
        boolean usado = controller.onUsarItemInventario(item);
        if (usado) {
            stage.close();
        }
    }

    /**
     * Crea un slot de equipo.
     *
     * @param nombre nombre del slot
     * @param item item equipado
     * @return nodo visual
     */
    private Node crearSlot(String nombre, Item item) {
        String valor = item == null ? "-" : item.getNombre();
        return crearDato(nombre, valor);
    }

    /**
     * Crea una fila de dato simple.
     *
     * @param nombre nombre del dato
     * @param valor valor mostrado
     * @return fila visual
     */
    private Node crearDato(String nombre, String valor) {
        HBox fila = new HBox(8);
        fila.setAlignment(Pos.CENTER_LEFT);
        Label nombreLabel = new Label(nombre + ":");
        nombreLabel.setMinWidth(92);
        nombreLabel.setStyle("-fx-text-fill: #c9b99c;");
        Label valorLabel = new Label(valor);
        valorLabel.setWrapText(true);
        valorLabel.setStyle("-fx-text-fill: #f5f0e6;");
        fila.getChildren().addAll(nombreLabel, valorLabel);
        return fila;
    }

    /**
     * Crea un titulo de seccion.
     *
     * @param texto texto visible
     * @return label configurado
     */
    private Label crearTituloSeccion(String texto) {
        Label label = new Label(texto);
        label.setFont(Font.font("Serif", 20));
        label.setStyle("-fx-text-fill: #f5f0e6;");
        return label;
    }

    /**
     * Crea un separador textual.
     *
     * @return separador
     */
    private Label crearSeparador() {
        Label label = new Label("--------------------");
        label.setStyle("-fx-text-fill: #5e5140;");
        return label;
    }

    /**
     * Crea un boton de inventario.
     *
     * @param texto texto visible
     * @return boton configurado
     */
    private Button crearBoton(String texto) {
        Button button = new Button(texto);
        button.setPrefHeight(32);
        button.setStyle(
            "-fx-background-color: #3a3328;"
                + "-fx-text-fill: #f5f0e6;"
                + "-fx-border-color: #8f7651;"
        );
        return button;
    }

    /**
     * Devuelve el texto que explica el modo del inventario.
     *
     * @return texto de modo
     */
    private String textoModo() {
        if (modelo.getTurnManager().getFaseActual() == Phase.USE_ITEM) {
            return "Fase USE_ITEM: puedes usar o equipar un objeto.";
        }
        return "Modo lectura: puedes consultar el inventario en cualquier momento.";
    }
}
