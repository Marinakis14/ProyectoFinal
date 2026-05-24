package Valdris.ui.view;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.bfs.BFSMovimiento;
import Valdris.model.enums.CellType;
import Valdris.model.enums.GameResult;
import Valdris.model.enums.Phase;
import Valdris.model.items.Item;
import Valdris.model.map.Cell;
import Valdris.model.map.Container;
import Valdris.model.map.Room;
import Valdris.model.units.Player;
import Valdris.model.units.Unit;
import Valdris.ui.MainApp;
import Valdris.ui.controller.GameController;
import Valdris.ui.model.GameModel;
import Valdris.ui.model.GameModelListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Vista principal de partida en modo solo lectura.
 */
public class GameView implements GameModelListener {

    /** Tamano visual de una celda del mapa. */
    private static final int CELL_SIZE = 40;

    /** Ventana principal de la aplicacion. */
    private final Stage stage;

    /** Modelo observable de la partida. */
    private final GameModel modelo;

    /** Controlador asociado a la vista. */
    private final GameController controller;

    /** Layout raiz. */
    private final BorderPane root;

    /** Grid central de la sala actual. */
    private final GridPane gridSala;

    /** Panel lateral con datos del jugador y partida. */
    private final VBox panelLateral;

    /** Log inferior. */
    private final CombatLogView logCombate;

    /** Escena principal. */
    private final Scene scene;

    /**
     * Crea la vista principal de la partida.
     *
     * @param stage ventana principal
     * @param modelo modelo de partida
     * @param controller controlador de la vista
     */
    public GameView(Stage stage, GameModel modelo, GameController controller) {
        this.stage = stage;
        this.modelo = modelo;
        this.controller = controller;
        this.root = new BorderPane();
        this.gridSala = new GridPane();
        this.panelLateral = new VBox(10);
        this.logCombate = new CombatLogView();
        this.scene = new Scene(root, MainApp.WINDOW_WIDTH, MainApp.WINDOW_HEIGHT);

        construirLayout();
        modelo.addListener(this);
        onEstadoCambiado(modelo);
    }

    /**
     * Devuelve la escena principal del juego.
     *
     * @return escena JavaFX
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Refresca el mapa, panel lateral y log cuando cambia el modelo.
     *
     * @param modelo modelo actualizado
     */
    @Override
    public void onEstadoCambiado(GameModel modelo) {
        renderizarSala(modelo.getDungeon().getRoomActual());
        actualizarPanelLateral();
        logCombate.mostrarMensajes(modelo.getTurnManager().getLogTextos());
        if (modelo.getUltimoMensaje() != null && !modelo.getUltimoMensaje().isEmpty()) {
            logCombate.addMensaje(modelo.getUltimoMensaje());
        }
    }

    /**
     * Dibuja la sala actual en el grid central.
     *
     * @param room sala que se renderiza
     */
    public void renderizarSala(Room room) {
        gridSala.getChildren().clear();
        if (room == null) {
            return;
        }

        for (int fila = 0; fila < room.getFilas(); fila++) {
            for (int col = 0; col < room.getCols(); col++) {
                gridSala.add(crearCeldaVisual(room, fila, col), col, fila);
            }
        }
    }

    /**
     * Construye el layout base de la pantalla principal.
     */
    private void construirLayout() {
        root.setStyle("-fx-background-color: #171717;");
        root.setPadding(new Insets(12));

        gridSala.setAlignment(Pos.CENTER);
        gridSala.setHgap(1);
        gridSala.setVgap(1);
        StackPane mapaWrapper = new StackPane(gridSala);
        mapaWrapper.setPadding(new Insets(18));
        mapaWrapper.setStyle("-fx-background-color: #202020;");

        panelLateral.setPadding(new Insets(16));
        panelLateral.setPrefWidth(300);
        panelLateral.setStyle("-fx-background-color: #242424; -fx-border-color: #3b3429; -fx-border-width: 0 0 0 1;");

        root.setCenter(mapaWrapper);
        root.setRight(panelLateral);
        root.setBottom(logCombate.getNode());
    }

    /**
     * Crea la representacion visual de una celda.
     *
     * @param room sala consultada
     * @param fila fila de la celda
     * @param col columna de la celda
     * @return nodo visual de la celda
     */
    private StackPane crearCeldaVisual(Room room, int fila, int col) {
        StackPane stack = new StackPane();
        stack.setPrefSize(CELL_SIZE, CELL_SIZE);
        try {
            Cell cell = room.getCell(fila, col);
            Rectangle fondo = new Rectangle(CELL_SIZE, CELL_SIZE);
            fondo.setFill(colorCelda(cell));
            if (isCeldaAlcanzableEnMovimiento(room, cell)) {
                fondo.setStroke(Color.web("#3fbf5f"));
                fondo.setStrokeWidth(3);
            } else {
                fondo.setStroke(Color.web("#111111"));
                fondo.setStrokeWidth(1);
            }
            stack.getChildren().add(fondo);
            agregarContenidoCelda(stack, cell);
            stack.setOnMouseClicked(event -> controller.onCeldaClick(fila, col));
        } catch (InvalidMoveException e) {
            Rectangle fondo = new Rectangle(CELL_SIZE, CELL_SIZE);
            fondo.setFill(Color.BLACK);
            stack.getChildren().add(fondo);
        }
        return stack;
    }

    /**
     * Agrega texto sobre una celda si contiene unidad, item o contenedor.
     *
     * @param stack celda visual
     * @param cell celda del modelo
     */
    private void agregarContenidoCelda(StackPane stack, Cell cell) {
        String texto = contenidoCelda(cell);
        if (texto == null || texto.isEmpty()) {
            return;
        }
        Label label = new Label(texto);
        label.setFont(Font.font("Monospaced", 15));
        label.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
        stack.getChildren().add(label);
    }

    /**
     * Devuelve el texto de contenido para una celda.
     *
     * @param cell celda consultada
     * @return texto corto, o vacio si no hay contenido visible
     */
    private String contenidoCelda(Cell cell) {
        Unit unit = cell.getUnit();
        if (unit != null) {
            if (unit == modelo.getPlayer()) {
                return letraJugador(modelo.getPlayer());
            }
            return "E";
        }

        Container container = cell.getContainer();
        if (container != null) {
            return "C";
        }

        Item item = cell.getItem();
        if (item != null) {
            return "*";
        }

        if (cell.getTipo() == CellType.LEVER) {
            return "L";
        }
        if (cell.getTipo() == CellType.RUNE) {
            return "R";
        }
        if (cell.getTipo() == CellType.STAIRS_UP) {
            return "^";
        }
        if (cell.getTipo() == CellType.STAIRS_DOWN) {
            return "v";
        }
        return "";
    }

    /**
     * Actualiza el panel lateral con los datos actuales de la partida.
     */
    private void actualizarPanelLateral() {
        panelLateral.getChildren().clear();

        Player player = modelo.getPlayer();
        Room room = modelo.getDungeon().getRoomActual();

        Label titulo = crearTitulo("Estado");
        panelLateral.getChildren().add(titulo);
        panelLateral.getChildren().add(crearDato("Personaje", player.getTipo().name()));
        panelLateral.getChildren().add(crearDato("HP", player.getHp() + " / " + player.getHpMax()));
        panelLateral.getChildren().add(crearDato("Ataque", String.valueOf(player.getAtaqueTotal())));
        panelLateral.getChildren().add(crearDato("Defensa", String.valueOf(player.getDefensaTotal())));
        panelLateral.getChildren().add(crearDato("Movimiento", String.valueOf(player.getMovEfectivo())));
        panelLateral.getChildren().add(crearDato("Rango", String.valueOf(player.getRangoEfectivo())));

        panelLateral.getChildren().add(crearSeparador());
        panelLateral.getChildren().add(crearDato("Sala", room == null ? "-" : room.getId()));
        panelLateral.getChildren().add(crearDato("Nombre", room == null ? "-" : room.getNombre()));
        panelLateral.getChildren().add(crearDato("Fase", modelo.getTurnManager().getFaseActual().name()));
        panelLateral.getChildren().add(crearDato("Turno", String.valueOf(modelo.getTurnManager().getTurnoGlobal())));

        panelLateral.getChildren().add(crearSeparador());
        agregarBotonesDeTurno(room);
        panelLateral.getChildren().add(crearSeparador());
        Button inventario = crearBoton("Inventario");
        inventario.setDisable(true);
        Button menu = crearBoton("Menú principal");
        menu.setOnAction(event -> controller.onBotonMenuPrincipal(stage));
        panelLateral.getChildren().addAll(inventario, menu);
    }

    /**
     * Agrega botones de accion tactica habilitados segun la fase actual.
     *
     * @param room sala actual
     */
    private void agregarBotonesDeTurno(Room room) {
        Phase fase = modelo.getTurnManager().getFaseActual();
        boolean partidaActiva = modelo.getTurnManager().getGameResult() == GameResult.IN_PROGRESS;

        Button saltarMovimiento = crearBoton("Saltar movimiento");
        saltarMovimiento.setDisable(!partidaActiva || fase != Phase.MOVEMENT);
        saltarMovimiento.setOnAction(event -> controller.onSaltarMovimiento());

        Button recoger = crearBoton("Recoger");
        recoger.setDisable(!partidaActiva || fase != Phase.PICKUP);
        recoger.setOnAction(event -> controller.onRecoger());

        Button usarAcceso = crearBoton("Usar acceso");
        usarAcceso.setDisable(!partidaActiva || fase != Phase.PICKUP);
        usarAcceso.setOnAction(event -> controller.onUsarAcceso());

        Button activarPalanca = crearBoton("Activar palanca");
        activarPalanca.setDisable(!partidaActiva || fase != Phase.PICKUP);
        activarPalanca.setOnAction(event -> controller.onActivarPalanca());

        Button saltarRecogida = crearBoton("Saltar recogida");
        saltarRecogida.setDisable(!partidaActiva || fase != Phase.PICKUP);
        saltarRecogida.setOnAction(event -> controller.onSaltarRecogida());

        Button saltarUsoItem = crearBoton("Saltar uso item");
        saltarUsoItem.setDisable(!partidaActiva || fase != Phase.USE_ITEM);
        saltarUsoItem.setOnAction(event -> controller.onSaltarUsoItem());

        Button cederTurno = crearBoton("Ceder turno");
        cederTurno.setDisable(!partidaActiva);
        cederTurno.setOnAction(event -> controller.onCederTurno());

        Button combateFinal = crearBoton("Iniciar combate final");
        combateFinal.setDisable(!puedeIntentarCombateFinal(room));
        combateFinal.setOnAction(event -> controller.onIniciarCombateFinal());

        panelLateral.getChildren().addAll(
            saltarMovimiento,
            recoger,
            usarAcceso,
            activarPalanca,
            saltarRecogida,
            saltarUsoItem,
            cederTurno,
            combateFinal
        );
    }

    /**
     * Crea una etiqueta de titulo para el panel lateral.
     *
     * @param texto texto visible
     * @return etiqueta configurada
     */
    private Label crearTitulo(String texto) {
        Label label = new Label(texto);
        label.setFont(Font.font("Serif", 26));
        label.setStyle("-fx-text-fill: #f5f0e6;");
        return label;
    }

    /**
     * Crea una fila de dato del panel lateral.
     *
     * @param nombre nombre del dato
     * @param valor valor visible
     * @return fila visual
     */
    private HBox crearDato(String nombre, String valor) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        Label nombreLabel = new Label(nombre + ":");
        nombreLabel.setMinWidth(95);
        nombreLabel.setStyle("-fx-text-fill: #c9b99c;");
        Label valorLabel = new Label(valor);
        valorLabel.setWrapText(true);
        valorLabel.setStyle("-fx-text-fill: #f5f0e6;");
        row.getChildren().addAll(nombreLabel, valorLabel);
        return row;
    }

    /**
     * Crea un separador visual simple.
     *
     * @return separador
     */
    private Label crearSeparador() {
        Label label = new Label("--------------------");
        label.setStyle("-fx-text-fill: #5e5140;");
        return label;
    }

    /**
     * Crea un boton del panel lateral.
     *
     * @param texto texto visible
     * @return boton configurado
     */
    private Button crearBoton(String texto) {
        Button button = new Button(texto);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(36);
        button.setStyle(
            "-fx-background-color: #3a3328;"
                + "-fx-text-fill: #f5f0e6;"
                + "-fx-border-color: #8f7651;"
        );
        return button;
    }

    /**
     * Devuelve el color de fondo de una celda.
     *
     * @param cell celda consultada
     * @return color JavaFX
     */
    private Color colorCelda(Cell cell) {
        CellType tipo = cell.getTipo();
        if (tipo == CellType.WALL) {
            return Color.web("#3a3a3a");
        }
        if (tipo == CellType.DOOR) {
            return Color.web("#7b5736");
        }
        if (tipo == CellType.DOOR_LOCKED) {
            return Color.web("#4f2d20");
        }
        if (tipo == CellType.DOOR_HIDDEN && !cell.isDescubierta()) {
            return Color.web("#3a3a3a");
        }
        if (tipo == CellType.STAIRS_UP || tipo == CellType.STAIRS_DOWN) {
            return Color.web("#5f5b74");
        }
        if (tipo == CellType.LEVER) {
            return Color.web("#6f613b");
        }
        if (tipo == CellType.RUNE) {
            return Color.web("#324f64");
        }
        if (tipo == CellType.TRAP) {
            return Color.web("#b8b1a3");
        }
        return Color.web("#b8b1a3");
    }

    /**
     * Devuelve la letra que representa al jugador.
     *
     * @param player jugador consultado
     * @return inicial visible
     */
    private String letraJugador(Player player) {
        if (player.getTipo().name().equals("SYRA")) {
            return "S";
        }
        if (player.getTipo().name().equals("DORATH")) {
            return "D";
        }
        return "K";
    }

    /**
     * Indica si una celda debe resaltarse como alcanzable por movimiento.
     *
     * @param room sala actual
     * @param cell celda consultada
     * @return true si la celda esta en rango BFS durante MOVEMENT
     */
    private boolean isCeldaAlcanzableEnMovimiento(Room room, Cell cell) {
        if (modelo.getTurnManager().getFaseActual() != Phase.MOVEMENT || room == null || cell == null) {
            return false;
        }
        Player player = modelo.getPlayer();
        ListaSimplementeEnlazada<Cell> alcanzables = BFSMovimiento.getCellsInRange(
            room, player.getFilaActual(), player.getColActual(), player.getMovEfectivo());
        return alcanzables.contains(cell);
    }

    /**
     * Indica si el boton de combate final debe estar disponible.
     *
     * @param room sala actual
     * @return true si se puede intentar iniciar el combate final
     */
    private boolean puedeIntentarCombateFinal(Room room) {
        return room != null
            && "S5-D".equals(room.getId())
            && !modelo.getTurnManager().isFinalCombatStarted()
            && modelo.getTurnManager().getGameResult() == GameResult.IN_PROGRESS;
    }
}
