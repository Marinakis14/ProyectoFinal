package Valdris.ui.view;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.bfs.BFSMovimiento;
import Valdris.logic.combat.CombatManager;
import Valdris.model.effects.Effect;
import Valdris.model.enums.CellType;
import Valdris.model.enums.GameResult;
import Valdris.model.enums.Phase;
import Valdris.model.items.Item;
import Valdris.model.map.Cell;
import Valdris.model.map.Container;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.Player;
import Valdris.model.units.Unit;
import Valdris.ui.MainApp;
import Valdris.ui.controller.GameController;
import Valdris.ui.model.GameModel;
import Valdris.ui.model.GameModelListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Vista principal de partida en modo solo lectura.
 */
public class GameView implements GameModelListener {

    /** Tamano visual de una celda del mapa. */
    private static final int CELL_SIZE = 52;

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

    /** Titulo visible de la sala sobre el mapa. */
    private final Label tituloSala;

    /** Panel lateral con datos del jugador y partida. */
    private final VBox panelLateral;

    /** Panel lateral con equipo e inventario. */
    private final VBox panelInventario;

    /** Log inferior. */
    private final CombatLogView logCombate;

    /** Escena principal. */
    private final Scene scene;

    /** Evita abrir la pantalla final mas de una vez. */
    private boolean pantallaFinalMostrada;

    /** Indica si el camino global debe resaltarse en el mapa. */
    private boolean caminoRevelado;

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
        this.tituloSala = new Label();
        this.panelLateral = new VBox(9);
        this.panelInventario = new VBox(9);
        this.logCombate = new CombatLogView();
        this.scene = new Scene(root, MainApp.WINDOW_WIDTH, MainApp.WINDOW_HEIGHT);
        this.pantallaFinalMostrada = false;
        this.caminoRevelado = false;

        construirLayout();
        configurarAtajosTeclado();
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
        mostrarDialogoPendiente();
        mostrarPantallaFinalSiProcede();
    }

    /**
     * Dibuja la sala actual en el grid central.
     *
     * @param room sala que se renderiza
     */
    public void renderizarSala(Room room) {
        gridSala.getChildren().clear();
        tituloSala.setText(room == null ? "Valdris" : room.getId() + " - " + room.getNombre());
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
        ValdrisTheme.aplicarFondo(root);
        root.setPadding(new Insets(12));

        gridSala.setAlignment(Pos.CENTER);
        gridSala.setHgap(1);
        gridSala.setVgap(1);
        tituloSala.setFont(Font.font("Serif", 31));
        tituloSala.setStyle("-fx-text-fill: #f5f0e6;");
        StackPane mapaWrapper = new StackPane(gridSala);
        mapaWrapper.setPadding(new Insets(18));
        ValdrisTheme.aplicarPanelDestacado(mapaWrapper);
        VBox mapaPanel = new VBox(10);
        mapaPanel.setAlignment(Pos.CENTER);
        mapaPanel.getChildren().addAll(tituloSala, ValdrisTheme.crearOrnamentoHorizontal(),
            ValdrisTheme.crearMarcoConEsquinas(mapaWrapper));

        panelLateral.setPadding(new Insets(14));
        panelLateral.setPrefWidth(300);
        panelLateral.setMinWidth(280);
        ValdrisTheme.aplicarPanel(panelLateral);

        panelInventario.setPadding(new Insets(14));
        panelInventario.setPrefWidth(360);
        panelInventario.setMinWidth(330);
        ValdrisTheme.aplicarPanel(panelInventario);

        root.setCenter(mapaPanel);
        root.setLeft(panelLateral);
        root.setRight(panelInventario);
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
            fondo.setFill(colorCelda(room, cell));
            if (isCeldaEnCaminoRevelado(cell)) {
                fondo.setStroke(Color.web("#45c7d8"));
                fondo.setStrokeWidth(4);
            } else if (isCeldaAlcanzableEnMovimiento(room, cell)) {
                fondo.setStroke(Color.web("#3fbf5f"));
                fondo.setStrokeWidth(3);
            } else if (isEnemigoAtacable(room, cell)) {
                fondo.setStroke(Color.web("#d94b4b"));
                fondo.setStrokeWidth(3);
            } else if (isEnemigoVisibleEnAtaque(cell)) {
                fondo.setStroke(Color.web("#8f7651"));
                fondo.setStrokeWidth(2);
            } else {
                fondo.setStroke(Color.web("#111111"));
                fondo.setStrokeWidth(1);
            }
            stack.getChildren().add(fondo);
            agregarContenidoCelda(stack, room, cell);
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
     * @param room sala consultada
     * @param cell celda del modelo
     */
    private void agregarContenidoCelda(StackPane stack, Room room, Cell cell) {
        Unit unit = cell.getUnit();
        if (unit instanceof Enemy) {
            stack.getChildren().add(crearMarcaEnemigo((Enemy) unit));
            return;
        }
        if (unit == modelo.getPlayer()) {
            stack.getChildren().add(crearMarcaJugador(modelo.getPlayer()));
            return;
        }

        String texto = contenidoCelda(room, cell);
        if (texto == null || texto.isEmpty()) {
            return;
        }
        Label label = new Label(texto);
        label.setFont(Font.font("Monospaced", 15));
        label.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
        stack.getChildren().add(label);
    }

    /**
     * Crea la marca visual de un enemigo con su HP actual visible.
     *
     * @param enemy enemigo mostrado
     * @return nodo con simbolo y vida
     */
    private VBox crearMarcaEnemigo(Enemy enemy) {
        VBox box = new VBox(0);
        box.setAlignment(Pos.CENTER);

        StackPane sprite = crearSpriteEnemigo(enemy);

        Label vida = new Label(enemy.getHp() + "/" + enemy.getHpMax());
        vida.setFont(Font.font("Monospaced", 8));
        vida.setStyle("-fx-text-fill: #ffd2d2; -fx-font-weight: bold;");

        box.getChildren().addAll(sprite, vida);
        return box;
    }

    /**
     * Crea la marca visual del jugador con color segun el personaje elegido.
     *
     * @param player jugador mostrado
     * @return nodo visual del jugador
     */
    private StackPane crearMarcaJugador(Player player) {
        StackPane sprite = new StackPane();
        sprite.setPrefSize(36, 36);
        sprite.setMaxSize(36, 36);

        Circle aura = new Circle(17);
        aura.setFill(Color.web(colorJugador(player)));
        aura.setStroke(Color.web("#f5f0e6"));
        aura.setStrokeWidth(2);

        Circle rostro = new Circle(9);
        rostro.setTranslateY(-3);
        rostro.setFill(Color.web("#f2d1a3"));
        rostro.setStroke(Color.web("#2b2118"));
        rostro.setStrokeWidth(1);

        Polygon cuerpo = new Polygon(
            18.0, 16.0,
            8.0, 33.0,
            28.0, 33.0
        );
        cuerpo.setFill(Color.web(colorJugadorOscuro(player)));
        cuerpo.setStroke(Color.web("#111111"));
        cuerpo.setStrokeWidth(1);

        Label inicial = new Label(letraJugador(player));
        inicial.setFont(Font.font("Monospaced", 10));
        inicial.setStyle("-fx-text-fill: #111111; -fx-font-weight: bold;");
        inicial.setTranslateY(-3);

        sprite.getChildren().addAll(aura, cuerpo, rostro, inicial);
        return sprite;
    }

    /**
     * Crea la silueta visual de un enemigo.
     *
     * @param enemy enemigo representado
     * @return nodo visual del enemigo
     */
    private StackPane crearSpriteEnemigo(Enemy enemy) {
        StackPane sprite = new StackPane();
        sprite.setPrefSize(34, 30);
        sprite.setMaxSize(34, 30);

        Polygon sombra = new Polygon(
            17.0, 2.0,
            30.0, 14.0,
            25.0, 29.0,
            9.0, 29.0,
            4.0, 14.0
        );
        sombra.setFill(Color.web(colorEnemigo(enemy)));
        sombra.setStroke(Color.web("#140c0c"));
        sombra.setStrokeWidth(1.5);

        Circle ojoIzq = new Circle(2.2);
        ojoIzq.setFill(Color.web("#ffd166"));
        ojoIzq.setTranslateX(-5);
        ojoIzq.setTranslateY(-2);

        Circle ojoDer = new Circle(2.2);
        ojoDer.setFill(Color.web("#ffd166"));
        ojoDer.setTranslateX(5);
        ojoDer.setTranslateY(-2);

        sprite.getChildren().addAll(sombra, ojoIzq, ojoDer);
        return sprite;
    }

    /**
     * Devuelve el texto de contenido para una celda.
     *
     * @param room sala consultada
     * @param cell celda consultada
     * @return texto corto, o vacio si no hay contenido visible
     */
    private String contenidoCelda(Room room, Cell cell) {
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

        if (esTriggerSecretoDeSuelo(room, cell)) {
            return "?";
        }
        if (esAccesoSecretoRevelado(room, cell)) {
            return "D";
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
        panelInventario.getChildren().clear();

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
        panelLateral.getChildren().add(crearDato("Efectos", textoEfectos(player)));

        panelLateral.getChildren().add(crearSeparador());
        panelLateral.getChildren().add(crearDato("Sala", room == null ? "-" : room.getId()));
        panelLateral.getChildren().add(crearDato("Fase", modelo.getTurnManager().getFaseActual().name()));
        panelLateral.getChildren().add(crearDato("Turno global", textoTurnoGlobal()));
        panelLateral.getChildren().add(crearDato("Turnos sala", textoTurnosSala()));
        panelLateral.getChildren().add(crearDatoSalida());

        panelLateral.getChildren().add(crearSeparador());
        agregarBotonesDeTurno(room);
        panelLateral.getChildren().add(crearSeparador());
        agregarResumenInventario(player);
        Button menu = crearBoton("Menú principal");
        menu.setOnAction(event -> controller.onBotonMenuPrincipal(stage));
        panelLateral.getChildren().add(menu);
    }

    /**
     * Agrega un resumen siempre visible del equipo e inventario del jugador.
     *
     * @param player jugador consultado
     */
    private void agregarResumenInventario(Player player) {
        panelInventario.getChildren().add(crearTitulo("Inventario"));
        panelInventario.getChildren().add(crearTituloSeccion("Equipo"));
        panelInventario.getChildren().add(crearDato("Arma", nombreItem(player.getArmaEquipada())));
        panelInventario.getChildren().add(crearDato("Escudo", nombreItem(player.getEscudoEquipado())));
        panelInventario.getChildren().add(crearDato("Armadura", nombreItem(player.getArmaduraEquipada())));
        panelInventario.getChildren().add(crearDato("Accesorio", nombreItem(player.getAccesorioEquipado())));

        panelInventario.getChildren().add(crearSeparador());
        panelInventario.getChildren().add(crearTituloSeccion("Objetos"));
        agregarResumenListaItems("Objetos", player.getInventario(), 8);
        agregarResumenListaItems("Narrativos", player.getItemsNarrativos(), 6);

        panelInventario.getChildren().add(crearSeparador());
        Button inventario = crearBoton("Abrir inventario completo (I)");
        inventario.setOnAction(event -> controller.onBotonInventario(stage));
        panelInventario.getChildren().add(inventario);
    }

    /**
     * Agrega un resumen corto de una lista de items.
     *
     * @param titulo titulo visible de la lista
     * @param items items consultados
     * @param maxVisibles numero maximo de nombres mostrados
     */
    private void agregarResumenListaItems(String titulo, ListaSimplementeEnlazada<Item> items, int maxVisibles) {
        int total = items == null ? 0 : items.getSize();
        panelInventario.getChildren().add(crearDato(titulo, String.valueOf(total)));
        if (total == 0) {
            return;
        }

        int gruposMostrados = 0;
        int gruposTotales = contarGruposItems(items);
        for (int i = 0; i < total && gruposMostrados < maxVisibles; i++) {
            Item item = items.get(i);
            if (item != null && esPrimeraAparicion(items, i)) {
                panelInventario.getChildren().add(crearLineaInventario(
                    "- " + item.getNombre() + " x" + contarItems(items, item.getId())));
                gruposMostrados++;
            }
        }
        if (gruposTotales > gruposMostrados) {
            panelInventario.getChildren().add(crearLineaInventario("- +" + (gruposTotales - gruposMostrados)
                + " más..."));
        }
    }

    /**
     * Configura atajos de teclado para las acciones principales de turno.
     */
    private void configurarAtajosTeclado() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.M) {
                controller.onSaltarMovimiento();
                event.consume();
            } else if (code == KeyCode.R) {
                controller.onRecoger();
                event.consume();
            } else if (code == KeyCode.A) {
                controller.onUsarAcceso();
                event.consume();
            } else if (code == KeyCode.L) {
                controller.onActivarPalanca();
                event.consume();
            } else if (code == KeyCode.S) {
                controller.onSaltarRecogida();
                event.consume();
            } else if (code == KeyCode.U) {
                controller.onSaltarUsoItem();
                event.consume();
            } else if (code == KeyCode.C) {
                controller.onCederTurno();
                event.consume();
            } else if (code == KeyCode.F) {
                controller.onIniciarCombateFinal();
                event.consume();
            } else if (code == KeyCode.I) {
                controller.onBotonInventario(stage);
                event.consume();
            } else if (code == KeyCode.V) {
                alternarCaminoRevelado();
                event.consume();
            }
        });
    }

    /**
     * Muestra el dialogo narrativo pendiente de la sala actual.
     */
    private void mostrarDialogoPendiente() {
        String dialogo = modelo.consumirDialogoPendiente();
        if (dialogo == null || dialogo.isEmpty()) {
            return;
        }
        logCombate.addMensaje("Diálogo: " + dialogo);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Diálogo");
        alert.setHeaderText(nombreSalaActual());
        alert.setContentText(dialogo);
        alert.showAndWait();
    }

    /**
     * Cambia a la pantalla final cuando la partida ya termino.
     */
    private void mostrarPantallaFinalSiProcede() {
        if (!pantallaFinalMostrada && modelo.isPartidaTerminada()) {
            pantallaFinalMostrada = true;
            controller.onMostrarPantallaFinal(stage);
        }
    }

    /**
     * Devuelve el nombre visible de la sala actual.
     *
     * @return nombre de sala para el dialogo
     */
    private String nombreSalaActual() {
        Room room = modelo.getDungeon().getRoomActual();
        if (room == null) {
            return "Valdris";
        }
        return room.getId() + " - " + room.getNombre();
    }

    /**
     * Agrega botones de accion tactica habilitados segun la fase actual.
     *
     * @param room sala actual
     */
    private void agregarBotonesDeTurno(Room room) {
        Phase fase = modelo.getTurnManager().getFaseActual();
        boolean partidaActiva = modelo.getTurnManager().getGameResult() == GameResult.IN_PROGRESS;

        Button saltarMovimiento = crearBoton("Saltar mov. (M)");
        saltarMovimiento.setDisable(!partidaActiva || fase != Phase.MOVEMENT);
        saltarMovimiento.setOnAction(event -> controller.onSaltarMovimiento());

        Button recoger = crearBoton("Recoger (R)");
        recoger.setDisable(!partidaActiva || fase != Phase.PICKUP);
        recoger.setOnAction(event -> controller.onRecoger());

        Button usarAcceso = crearBoton("Acceso (A)");
        usarAcceso.setDisable(!partidaActiva || fase != Phase.PICKUP);
        usarAcceso.setOnAction(event -> controller.onUsarAcceso());

        Button activarPalanca = crearBoton("Palanca (L)");
        activarPalanca.setDisable(!partidaActiva || fase != Phase.PICKUP);
        activarPalanca.setOnAction(event -> controller.onActivarPalanca());

        Button saltarRecogida = crearBoton("Saltar rec. (S)");
        saltarRecogida.setDisable(!partidaActiva || fase != Phase.PICKUP);
        saltarRecogida.setOnAction(event -> controller.onSaltarRecogida());

        Button saltarUsoItem = crearBoton("Saltar item (U)");
        saltarUsoItem.setDisable(!partidaActiva || fase != Phase.USE_ITEM);
        saltarUsoItem.setOnAction(event -> controller.onSaltarUsoItem());

        Button cederTurno = crearBoton(fase == Phase.ATTACK ? "Saltar atk. (C)" : "Ceder (C)");
        cederTurno.setDisable(!partidaActiva);
        cederTurno.setOnAction(event -> controller.onCederTurno());

        Button combateFinal = crearBoton("Final (F)");
        combateFinal.setDisable(!puedeIntentarCombateFinal(room));
        combateFinal.setOnAction(event -> controller.onIniciarCombateFinal());

        if (partidaActiva && fase == Phase.ATTACK) {
            panelLateral.getChildren().add(crearAyuda("Ataca enemigo rojo."));
        }

        GridPane acciones = new GridPane();
        acciones.setHgap(8);
        acciones.setVgap(8);
        agregarBotonAccion(acciones, saltarMovimiento, 0);
        agregarBotonAccion(acciones, recoger, 1);
        agregarBotonAccion(acciones, usarAcceso, 2);
        agregarBotonAccion(acciones, activarPalanca, 3);
        agregarBotonAccion(acciones, saltarRecogida, 4);
        agregarBotonAccion(acciones, saltarUsoItem, 5);
        agregarBotonAccion(acciones, cederTurno, 6);
        agregarBotonAccion(acciones, combateFinal, 7);
        panelLateral.getChildren().add(acciones);
    }

    /**
     * Activa o desactiva el resaltado de la ruta global hacia el objetivo.
     */
    private void alternarCaminoRevelado() {
        boolean nuevoEstado = !caminoRevelado;
        if (nuevoEstado && modelo.getTurnManager().getCaminoReveladoSalaActual().isEmpty()) {
            logCombate.addMensaje("No hay ruta disponible hacia el Nucleo.");
            return;
        }
        caminoRevelado = nuevoEstado;
        renderizarSala(modelo.getDungeon().getRoomActual());
        actualizarPanelLateral();
        if (caminoRevelado) {
            logCombate.addMensaje("Ruta revelada hacia el Núcleo.");
        } else {
            logCombate.addMensaje("Ruta oculta.");
        }
    }

    /**
     * Inserta un boton de accion en una rejilla compacta de dos columnas.
     *
     * @param acciones rejilla de botones
     * @param boton boton que se inserta
     * @param indice posicion lineal dentro de la rejilla
     */
    private void agregarBotonAccion(GridPane acciones, Button boton, int indice) {
        boton.setPrefWidth(132);
        acciones.add(boton, indice % 2, indice / 2);
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
        label.setStyle("-fx-text-fill: #f5f0e6; -fx-border-color: transparent transparent #8f7651 transparent; -fx-border-width: 0 0 1 0;");
        return label;
    }

    /**
     * Crea una etiqueta de titulo secundaria para secciones del panel lateral.
     *
     * @param texto texto visible
     * @return etiqueta configurada
     */
    private Label crearTituloSeccion(String texto) {
        Label label = new Label(texto);
        label.setFont(Font.font("Serif", 19));
        label.setStyle("-fx-text-fill: #f5f0e6; -fx-background-color: rgba(143, 118, 81, 0.16); -fx-padding: 4 6 4 6;");
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
     * Crea la fila de salida con el acceso directo para revelar la ruta.
     *
     * @return fila visual con distancia y boton de ruta
     */
    private HBox crearDatoSalida() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Label nombreLabel = new Label("Salida:");
        nombreLabel.setMinWidth(55);
        nombreLabel.setStyle("-fx-text-fill: #c9b99c;");

        Label valorLabel = new Label(textoSalidaMasCercana());
        valorLabel.setWrapText(true);
        valorLabel.setMaxWidth(Double.MAX_VALUE);
        valorLabel.setStyle("-fx-text-fill: #f5f0e6;");
        HBox.setHgrow(valorLabel, Priority.ALWAYS);

        Button ruta = crearBoton(caminoRevelado ? "Ocultar" : "Ruta (V)");
        ruta.setPrefWidth(86);
        ruta.setMinWidth(86);
        ruta.setPrefHeight(30);
        ruta.setDisable(modelo.getTurnManager().getGameResult() != GameResult.IN_PROGRESS);
        ruta.setOnAction(event -> alternarCaminoRevelado());

        row.getChildren().addAll(nombreLabel, valorLabel, ruta);
        return row;
    }

    /**
     * Crea un separador visual simple.
     *
     * @return separador
     */
    private Region crearSeparador() {
        return ValdrisTheme.crearSeparador();
    }

    /**
     * Crea una ayuda contextual para la fase actual.
     *
     * @param texto texto visible
     * @return etiqueta configurada
     */
    private Label crearAyuda(String texto) {
        Label label = new Label(texto);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setStyle("-fx-text-fill: #d7c8aa;");
        return label;
    }

    /**
     * Crea una linea compacta para mostrar nombres de items en el panel lateral.
     *
     * @param texto texto visible
     * @return etiqueta configurada
     */
    private Label crearLineaInventario(String texto) {
        Label label = new Label(texto);
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: #c9b99c;");
        return label;
    }

    /**
     * Cuenta cuantos grupos de item distintos existen por ID.
     *
     * @param items lista consultada
     * @return numero de grupos visuales
     */
    private int contarGruposItems(ListaSimplementeEnlazada<Item> items) {
        int grupos = 0;
        if (items == null) {
            return grupos;
        }
        for (int i = 0; i < items.getSize(); i++) {
            if (items.get(i) != null && esPrimeraAparicion(items, i)) {
                grupos++;
            }
        }
        return grupos;
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
     * Devuelve el nombre visible de un item o guion si el slot esta vacio.
     *
     * @param item item consultado
     * @return nombre visible
     */
    private String nombreItem(Item item) {
        return item == null ? "-" : item.getNombre();
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
        ValdrisTheme.aplicarBoton(button);
        return button;
    }

    /**
     * Devuelve el color de fondo de una celda.
     *
     * @param cell celda consultada
     * @return color JavaFX
     */
    private Color colorCelda(Room room, Cell cell) {
        CellType tipo = cell.getTipo();
        if (tipo == CellType.WALL) {
            return Color.web("#3a3a3a");
        }
        if (esAccesoSecretoRevelado(room, cell)) {
            return Color.web("#2f8f7a");
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
            if (isPuzzleCellActive(room, cell)) {
                return Color.web("#3f8f4f");
            }
            return Color.web("#6f613b");
        }
        if (tipo == CellType.RUNE) {
            if (isPuzzleCellActive(room, cell)) {
                return Color.web("#3f8f4f");
            }
            return Color.web("#324f64");
        }
        if (tipo == CellType.TRAP) {
            return Color.web("#b8b1a3");
        }
        if (esTriggerSecretoDeSuelo(room, cell)) {
            return Color.web("#c9bf9f");
        }
        return Color.web("#b8b1a3");
    }

    /**
     * Indica si la celda actual es una pista de pasadizo secreto pisable.
     *
     * @param room sala consultada
     * @param cell celda consultada
     * @return true si el suelo activa un secreto
     */
    private boolean esTriggerSecretoDeSuelo(Room room, Cell cell) {
        return esTriggerSecreto(room, cell) && cell.getTipo() == CellType.FLOOR;
    }

    /**
     * Indica si la celda es una puerta secreta ya revelada.
     *
     * @param room sala consultada
     * @param cell celda consultada
     * @return true si es un acceso secreto visible
     */
    private boolean esAccesoSecretoRevelado(Room room, Cell cell) {
        return esTriggerSecreto(room, cell) && cell.getTipo() == CellType.DOOR && cell.isDescubierta();
    }

    /**
     * Comprueba si el trigger de la celda corresponde a un secreto de la sala.
     *
     * @param room sala consultada
     * @param cell celda consultada
     * @return true si el trigger apunta a un secreto
     */
    private boolean esTriggerSecreto(Room room, Cell cell) {
        return room != null && cell != null && cell.hasTrigger()
            && room.getSecretTarget(cell.getTriggerId()) != null;
    }

    /**
     * Devuelve un texto compacto con los efectos activos del jugador.
     *
     * @param player jugador consultado
     * @return efectos activos o guion si no hay ninguno
     */
    private String textoEfectos(Player player) {
        if (player == null || player.getEfectosActivos().getSize() == 0) {
            return "-";
        }
        String texto = "";
        for (int i = 0; i < player.getEfectosActivos().getSize(); i++) {
            Effect efecto = player.getEfectosActivos().get(i);
            if (efecto == null) {
                continue;
            }
            if (!texto.isEmpty()) {
                texto += ", ";
            }
            texto += efecto.getTipo().name() + " (" + efecto.getTurnos() + "t)";
        }
        return texto.isEmpty() ? "-" : texto;
    }

    /**
     * Devuelve el texto del contador global de turnos.
     *
     * @return contador global visible
     */
    private String textoTurnoGlobal() {
        return modelo.getTurnManager().getTurnoGlobal() + "/"
            + modelo.getTurnManager().getTurnoGlobalMaximo();
    }

    /**
     * Devuelve el texto del contador de turnos de sala.
     *
     * @return contador de sala visible
     */
    private String textoTurnosSala() {
        int maximos = modelo.getTurnManager().getTurnosSalaMaximos();
        if (maximos < 0) {
            return "Sin límite";
        }
        return modelo.getTurnManager().getTurnosSalaConsumidos() + "/" + maximos;
    }

    /**
     * Devuelve el texto visible de distancia al objetivo global.
     *
     * @return mensaje de salida para el panel de estado
     */
    private String textoSalidaMasCercana() {
        if (modelo.getTurnManager().hayEnemigosVivosSalaActual()) {
            return "Derrota a todos los enemigos.";
        }
        int distancia = modelo.getTurnManager().getDistanciaSalidaGlobal();
        int salas = modelo.getTurnManager().getSalasHastaObjetivoGlobal();
        String siguiente = modelo.getTurnManager().getIdSiguienteSalaObjetivoGlobal();
        if (distancia == 0 && salas == 0) {
            return "Objetivo alcanzado.";
        }
        if (distancia < 0 || salas < 0) {
            return "Sin ruta al Núcleo.";
        }
        String textoDistancia = distancia == 1 ? "1 casilla" : distancia + " casillas";
        String textoSalas = salas == 1 ? "1 sala" : salas + " salas";
        if (siguiente == null || siguiente.isEmpty()) {
            return textoDistancia + ", " + textoSalas;
        }
        return textoDistancia + ", " + textoSalas + " -> " + siguiente;
    }

    /**
     * Indica si una palanca o runa debe mostrarse como activada.
     *
     * @param room sala actual
     * @param cell celda de puzzle consultada
     * @return true si la celda esta activada o el puzzle ya esta resuelto
     */
    private boolean isPuzzleCellActive(Room room, Cell cell) {
        if (room == null || cell == null) {
            return false;
        }
        int indice = getIndicePuzzle(room, cell);
        if (indice < 0) {
            return false;
        }
        if (room.isPuzzleResolved()) {
            return true;
        }
        int[] secuencia = room.getSecuenciaActivada();
        for (int i = 0; i < secuencia.length; i++) {
            if (secuencia[i] == indice) {
                return true;
            }
        }
        return false;
    }

    /**
     * Busca el indice de una celda dentro de las piezas de puzzle registradas.
     *
     * @param room sala consultada
     * @param cell celda buscada por referencia
     * @return indice de puzzle, o -1 si no esta registrada
     */
    private int getIndicePuzzle(Room room, Cell cell) {
        int indice = getIndicePorReferencia(room.getLeverCells(), cell);
        if (indice >= 0) {
            return indice;
        }
        return getIndicePorReferencia(room.getRuneCells(), cell);
    }

    /**
     * Busca una celda por identidad real dentro de una lista propia.
     *
     * @param celdas lista consultada
     * @param cell celda buscada
     * @return posicion por referencia, o -1 si no existe
     */
    private int getIndicePorReferencia(ListaSimplementeEnlazada<Cell> celdas, Cell cell) {
        if (celdas == null || cell == null) {
            return -1;
        }
        for (int i = 0; i < celdas.getSize(); i++) {
            if (celdas.get(i) == cell) {
                return i;
            }
        }
        return -1;
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
     * Devuelve el color principal del sprite del jugador.
     *
     * @param player jugador consultado
     * @return color CSS hexadecimal
     */
    private String colorJugador(Player player) {
        if (player.getTipo().name().equals("SYRA")) {
            return "#72b37e";
        }
        if (player.getTipo().name().equals("DORATH")) {
            return "#a58bd5";
        }
        return "#7ba4d8";
    }

    /**
     * Devuelve el color secundario del cuerpo del sprite del jugador.
     *
     * @param player jugador consultado
     * @return color CSS hexadecimal
     */
    private String colorJugadorOscuro(Player player) {
        if (player.getTipo().name().equals("SYRA")) {
            return "#2f6b42";
        }
        if (player.getTipo().name().equals("DORATH")) {
            return "#5b478b";
        }
        return "#355d8a";
    }

    /**
     * Devuelve el color de silueta segun el tipo de enemigo.
     *
     * @param enemy enemigo consultado
     * @return color CSS hexadecimal
     */
    private String colorEnemigo(Enemy enemy) {
        String tipo = enemy.getTipo().name();
        if ("ARCHER".equals(tipo) || "SNIPER".equals(tipo)) {
            return "#6a4b2d";
        }
        if ("CONTROLLER".equals(tipo) || "SUMMONER".equals(tipo) || "ECO_DE_MAGIA".equals(tipo)) {
            return "#563b78";
        }
        if ("GUARDIAN".equals(tipo) || "CONSTRUCTO".equals(tipo) || "DESTRUCTOR".equals(tipo)) {
            return "#4b5560";
        }
        if ("SOMBRA_ABSORBIDA".equals(tipo) || "PARASITO".equals(tipo)) {
            return "#2f1d35";
        }
        if ("BERSERKER".equals(tipo)) {
            return "#7c2f2f";
        }
        return "#5c2424";
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
        return contieneCeldaPorReferencia(alcanzables, cell);
    }

    /**
     * Indica si una celda forma parte del camino global revelado.
     *
     * @param cell celda consultada
     * @return true si debe resaltarse con el color de ruta revelada
     */
    private boolean isCeldaEnCaminoRevelado(Cell cell) {
        if (!caminoRevelado || cell == null) {
            return false;
        }
        ListaSimplementeEnlazada<Cell> camino = modelo.getTurnManager().getCaminoReveladoSalaActual();
        return contieneCeldaPorReferencia(camino, cell);
    }

    /**
     * Indica si una celda contiene un enemigo atacable en la fase actual.
     *
     * @param room sala actual
     * @param cell celda consultada
     * @return true si contiene un enemigo dentro de rango y vision
     */
    private boolean isEnemigoAtacable(Room room, Cell cell) {
        if (modelo.getTurnManager().getFaseActual() != Phase.ATTACK || room == null || cell == null) {
            return false;
        }
        Unit unit = cell.getUnit();
        return unit instanceof Enemy && CombatManager.estaEnRango(modelo.getPlayer(), unit, room);
    }

    /**
     * Indica si la celda contiene un enemigo durante la fase de ataque.
     *
     * @param cell celda consultada
     * @return true si hay enemigo visible para orientar al jugador
     */
    private boolean isEnemigoVisibleEnAtaque(Cell cell) {
        return modelo.getTurnManager().getFaseActual() == Phase.ATTACK
            && cell != null
            && cell.getUnit() instanceof Enemy;
    }

    /**
     * Comprueba identidad real de celda dentro de una lista de resultados BFS.
     *
     * @param celdas lista consultada
     * @param buscada celda buscada
     * @return true si la instancia exacta esta presente
     */
    private boolean contieneCeldaPorReferencia(ListaSimplementeEnlazada<Cell> celdas, Cell buscada) {
        if (celdas == null || buscada == null) {
            return false;
        }
        for (int i = 0; i < celdas.getSize(); i++) {
            if (celdas.get(i) == buscada) {
                return true;
            }
        }
        return false;
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
