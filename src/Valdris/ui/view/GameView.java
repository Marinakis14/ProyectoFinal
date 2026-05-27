package Valdris.ui.view;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;
import Valdris.exceptions.InvalidMoveException;
import Valdris.logic.bfs.BFSMovimiento;
import Valdris.logic.combat.CombatManager;
import Valdris.model.effects.Effect;
import Valdris.model.enums.CellType;
import Valdris.model.enums.GameResult;
import Valdris.model.enums.ItemType;
import Valdris.model.enums.Phase;
import Valdris.model.items.Item;
import Valdris.model.map.Cell;
import Valdris.model.map.Container;
import Valdris.model.map.Room;
import Valdris.model.units.Enemy;
import Valdris.model.units.MiniBossEnemy;
import Valdris.model.units.ParasitoEnemy;
import Valdris.model.units.Player;
import Valdris.model.units.Unit;
import Valdris.ui.MainApp;
import Valdris.ui.controller.GameController;
import Valdris.ui.model.GameModel;
import Valdris.ui.model.GameModelListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
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
        actualizarTituloSala(room);
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
            fondo.setFill(colorCelda(room, cell, fila, col));
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

        Node contenido = crearMarcaContenido(room, cell);
        if (contenido == null) {
            return;
        }
        stack.getChildren().add(contenido);
    }

    /**
     * Crea la marca visual para items, cofres y elementos interactivos de celda.
     *
     * @param room sala consultada
     * @param cell celda consultada
     * @return nodo visual, o null si no hay nada que dibujar sobre la celda
     */
    private Node crearMarcaContenido(Room room, Cell cell) {
        Container container = cell.getContainer();
        if (container != null) {
            return crearMarcaCofre(container);
        }

        Item item = cell.getItem();
        if (item != null) {
            return crearMarcaItem(item);
        }

        if (esTriggerSecretoDeSuelo(room, cell)) {
            return crearMarcaSecreto();
        }
        if (esAccesoSecretoRevelado(room, cell)) {
            return crearMarcaPuertaSecreta();
        }
        if (cell.getTipo() == CellType.LEVER) {
            return crearMarcaPalanca(isPuzzleCellActive(room, cell));
        }
        if (cell.getTipo() == CellType.RUNE) {
            return crearMarcaRuna(isPuzzleCellActive(room, cell));
        }
        if (cell.getTipo() == CellType.STAIRS_UP) {
            return crearMarcaEscalera(true);
        }
        if (cell.getTipo() == CellType.STAIRS_DOWN) {
            return crearMarcaEscalera(false);
        }
        return null;
    }

    /**
     * Crea la marca visual de un cofre.
     *
     * @param container contenedor mostrado
     * @return nodo visual del cofre
     */
    private StackPane crearMarcaCofre(Container container) {
        StackPane sprite = new StackPane();
        sprite.setPrefSize(40, 34);
        sprite.setMaxSize(40, 34);

        boolean abierto = container.isAbierto();
        Rectangle cuerpo = new Rectangle(31, 18);
        cuerpo.setArcWidth(4);
        cuerpo.setArcHeight(4);
        cuerpo.setTranslateY(6);
        cuerpo.setFill(Color.web(abierto ? "#6b5946" : "#8a5a2d"));
        cuerpo.setStroke(Color.web("#22170d"));
        cuerpo.setStrokeWidth(1.3);

        Rectangle tapa = new Rectangle(33, 12);
        tapa.setArcWidth(10);
        tapa.setArcHeight(10);
        tapa.setTranslateY(abierto ? -8 : -3);
        tapa.setRotate(abierto ? -12 : 0);
        tapa.setFill(Color.web(abierto ? "#5a4734" : "#b77b32"));
        tapa.setStroke(Color.web("#22170d"));
        tapa.setStrokeWidth(1.2);

        Rectangle cierre = new Rectangle(7, 9);
        cierre.setArcWidth(2);
        cierre.setArcHeight(2);
        cierre.setTranslateY(5);
        cierre.setFill(Color.web(abierto ? "#3f352b" : "#e0bd65"));
        cierre.setStroke(Color.web("#2a2118"));
        cierre.setStrokeWidth(0.8);

        sprite.getChildren().addAll(cuerpo, tapa, cierre);
        return sprite;
    }

    /**
     * Crea la marca visual de un item de suelo.
     *
     * @param item item mostrado
     * @return nodo visual del item
     */
    private StackPane crearMarcaItem(Item item) {
        StackPane sprite = new StackPane();
        sprite.setPrefSize(34, 34);
        sprite.setMaxSize(34, 34);

        Circle brillo = new Circle(15);
        brillo.setFill(Color.web(colorBrilloItem(item.getTipo()), 0.28));
        brillo.setStroke(Color.web(colorBrilloItem(item.getTipo())));
        brillo.setStrokeWidth(1);

        Node figura = crearFiguraItem(item.getTipo());
        Label inicial = new Label(etiquetaItem(item.getTipo()));
        inicial.setFont(Font.font("Monospaced", 8));
        inicial.setStyle("-fx-text-fill: #fff7d8; -fx-font-weight: bold;");
        inicial.setTranslateY(10);

        sprite.getChildren().addAll(brillo, figura, inicial);
        return sprite;
    }

    /**
     * Crea la figura principal de un item segun su categoria.
     *
     * @param tipo tipo funcional del item
     * @return nodo con la figura del item
     */
    private Node crearFiguraItem(ItemType tipo) {
        if (tipo == ItemType.WEAPON) {
            Polygon hoja = new Polygon(17.0, 2.0, 21.0, 18.0, 17.0, 28.0, 13.0, 18.0);
            hoja.setFill(Color.web("#d7dbe2"));
            hoja.setStroke(Color.web("#2b3138"));
            hoja.setStrokeWidth(1);
            return hoja;
        }
        if (tipo == ItemType.ARMOR || tipo == ItemType.SHIELD) {
            Polygon escudo = new Polygon(17.0, 3.0, 28.0, 8.0, 25.0, 24.0, 17.0, 31.0, 9.0, 24.0, 6.0, 8.0);
            escudo.setFill(Color.web(tipo == ItemType.SHIELD ? "#6f8ea8" : "#8c8f93"));
            escudo.setStroke(Color.web("#20252a"));
            escudo.setStrokeWidth(1.1);
            return escudo;
        }
        if (tipo == ItemType.POTION) {
            VBox frasco = new VBox(0);
            frasco.setAlignment(Pos.CENTER);
            Rectangle cuello = new Rectangle(8, 7);
            cuello.setFill(Color.web("#d8c9aa"));
            cuello.setStroke(Color.web("#2b2118"));
            cuello.setStrokeWidth(0.8);
            Circle base = new Circle(10);
            base.setFill(Color.web("#b44870"));
            base.setStroke(Color.web("#2b2118"));
            base.setStrokeWidth(1);
            frasco.getChildren().addAll(cuello, base);
            return frasco;
        }
        if (tipo == ItemType.ACCESSORY) {
            Circle aro = new Circle(10);
            aro.setFill(Color.TRANSPARENT);
            aro.setStroke(Color.web("#d8b55a"));
            aro.setStrokeWidth(4);
            return aro;
        }
        Polygon sello = new Polygon(17.0, 3.0, 28.0, 17.0, 17.0, 31.0, 6.0, 17.0);
        sello.setFill(Color.web("#6bc0d0"));
        sello.setStroke(Color.web("#173f4f"));
        sello.setStrokeWidth(1.1);
        return sello;
    }

    /**
     * Crea la marca visual de un trigger secreto.
     *
     * @return nodo visual del secreto
     */
    private StackPane crearMarcaSecreto() {
        StackPane sprite = new StackPane();
        Circle base = new Circle(13);
        base.setFill(Color.web("#d5ca9d", 0.30));
        base.setStroke(Color.web("#d5ca9d"));
        base.setStrokeWidth(1.2);
        Label marca = new Label("?");
        marca.setFont(Font.font("Serif", 18));
        marca.setStyle("-fx-text-fill: #fff1bd; -fx-font-weight: bold;");
        sprite.getChildren().addAll(base, marca);
        return sprite;
    }

    /**
     * Crea la marca visual de una puerta secreta revelada.
     *
     * @return nodo visual de puerta secreta
     */
    private StackPane crearMarcaPuertaSecreta() {
        StackPane sprite = new StackPane();
        Rectangle puerta = new Rectangle(26, 32);
        puerta.setArcWidth(12);
        puerta.setArcHeight(12);
        puerta.setFill(Color.web("#2f8f7a", 0.78));
        puerta.setStroke(Color.web("#9ef0d9"));
        puerta.setStrokeWidth(1.8);
        Circle nucleo = new Circle(4);
        nucleo.setFill(Color.web("#d8fff4"));
        sprite.getChildren().addAll(puerta, nucleo);
        return sprite;
    }

    /**
     * Crea la marca visual de una palanca.
     *
     * @param activa true si la palanca ya cuenta como activada
     * @return nodo visual de palanca
     */
    private StackPane crearMarcaPalanca(boolean activa) {
        StackPane sprite = new StackPane();
        Rectangle base = new Rectangle(25, 8);
        base.setArcWidth(3);
        base.setArcHeight(3);
        base.setTranslateY(10);
        base.setFill(Color.web("#4a3824"));
        base.setStroke(Color.web("#d0a45b"));
        base.setStrokeWidth(1);

        Rectangle brazo = new Rectangle(5, 26);
        brazo.setArcWidth(3);
        brazo.setArcHeight(3);
        brazo.setTranslateY(-3);
        brazo.setRotate(activa ? 28 : -28);
        brazo.setFill(Color.web(activa ? "#67c26e" : "#b88a42"));
        brazo.setStroke(Color.web("#1e160d"));
        brazo.setStrokeWidth(0.8);

        Circle pomo = new Circle(5);
        pomo.setTranslateY(-15);
        pomo.setTranslateX(activa ? 6 : -6);
        pomo.setFill(Color.web(activa ? "#9df0a0" : "#e0bd65"));
        pomo.setStroke(Color.web("#1e160d"));
        pomo.setStrokeWidth(0.8);

        sprite.getChildren().addAll(base, brazo, pomo);
        return sprite;
    }

    /**
     * Crea la marca visual de una runa.
     *
     * @param activa true si la runa ya cuenta como activada
     * @return nodo visual de runa
     */
    private StackPane crearMarcaRuna(boolean activa) {
        StackPane sprite = new StackPane();
        Polygon losa = new Polygon(17.0, 2.0, 30.0, 17.0, 17.0, 32.0, 4.0, 17.0);
        losa.setFill(Color.web(activa ? "#3f8f4f" : "#324f64"));
        losa.setStroke(Color.web(activa ? "#b8ffc2" : "#9bc0dd"));
        losa.setStrokeWidth(1.2);
        Label marca = new Label("R");
        marca.setFont(Font.font("Serif", 15));
        marca.setStyle("-fx-text-fill: #f5f0e6; -fx-font-weight: bold;");
        sprite.getChildren().addAll(losa, marca);
        return sprite;
    }

    /**
     * Crea la marca visual de una escalera.
     *
     * @param subida true si sube, false si baja
     * @return nodo visual de escalera
     */
    private StackPane crearMarcaEscalera(boolean subida) {
        StackPane sprite = new StackPane();
        for (int i = 0; i < 4; i++) {
            Rectangle escalon = new Rectangle(10 + i * 5, 4);
            escalon.setTranslateY(subida ? 9 - i * 5 : -9 + i * 5);
            escalon.setFill(Color.web("#c5bfd8"));
            escalon.setStroke(Color.web("#2c2838"));
            escalon.setStrokeWidth(0.5);
            sprite.getChildren().add(escalon);
        }
        Label marca = new Label(subida ? "^" : "v");
        marca.setFont(Font.font("Monospaced", 11));
        marca.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
        marca.setTranslateY(subida ? -13 : 13);
        sprite.getChildren().add(marca);
        return sprite;
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
        if (enemy instanceof ParasitoEnemy) {
            return crearSpriteParasito((ParasitoEnemy) enemy);
        }
        if (enemy instanceof MiniBossEnemy) {
            return crearSpriteMiniBoss((MiniBossEnemy) enemy);
        }

        StackPane sprite = new StackPane();
        sprite.setPrefSize(34, 30);
        sprite.setMaxSize(34, 30);

        String tipo = enemy.getTipo().name();
        Node silueta = crearSiluetaEnemigo(tipo, colorEnemigo(enemy));
        Label icono = new Label(etiquetaEnemigo(tipo));
        icono.setFont(Font.font("Monospaced", 8));
        icono.setStyle("-fx-text-fill: #fff5ce; -fx-font-weight: bold;");
        icono.setTranslateY(6);

        Circle ojoIzq = new Circle(1.8);
        ojoIzq.setFill(Color.web("#ffd166"));
        ojoIzq.setTranslateX(-4.5);
        ojoIzq.setTranslateY(-3);

        Circle ojoDer = new Circle(1.8);
        ojoDer.setFill(Color.web("#ffd166"));
        ojoDer.setTranslateX(4.5);
        ojoDer.setTranslateY(-3);

        sprite.getChildren().addAll(silueta, ojoIzq, ojoDer, icono);
        return sprite;
    }

    /**
     * Crea una silueta especial para mini-bosses narrativos.
     *
     * @param miniBoss mini-boss representado
     * @return nodo visual diferenciado
     */
    private StackPane crearSpriteMiniBoss(MiniBossEnemy miniBoss) {
        StackPane sprite = new StackPane();
        sprite.setPrefSize(42, 36);
        sprite.setMaxSize(42, 36);

        Circle aura = new Circle(20);
        aura.setFill(Color.web(colorAuraMiniBoss(miniBoss), 0.24));
        aura.setStroke(Color.web("#e0bd65"));
        aura.setStrokeWidth(1.4);

        Node silueta = crearSiluetaMiniBoss(miniBoss);
        Label sello = new Label(etiquetaMiniBoss(miniBoss));
        sello.setFont(Font.font("Monospaced", 8));
        sello.setStyle("-fx-text-fill: #fff2bd; -fx-font-weight: bold;");
        sello.setTranslateY(9);

        sprite.getChildren().addAll(aura, silueta, sello);
        return sprite;
    }

    /**
     * Crea la silueta principal de un mini-boss.
     *
     * @param miniBoss mini-boss representado
     * @return nodo visual de silueta
     */
    private Node crearSiluetaMiniBoss(MiniBossEnemy miniBoss) {
        String tipo = miniBoss.getTipoMiniBoss().name();
        if ("ALCALDE_CORRUPTO".equals(tipo)) {
            return crearSiluetaAlcaldeCorrupto();
        }
        if ("ESPIRITU_MADRE".equals(tipo)) {
            return crearSiluetaEspirituMadre();
        }
        if ("GOLEM".equals(tipo)) {
            return crearSiluetaGolemMiniBoss();
        }
        if ("GUARDIAN_SIN_NOMBRE".equals(tipo)) {
            return crearSiluetaGuardianMiniBoss();
        }
        return crearSiluetaFiltroMiniBoss();
    }

    /**
     * Crea el sprite del Alcalde Corrupto.
     *
     * @return silueta del Alcalde
     */
    private StackPane crearSiluetaAlcaldeCorrupto() {
        StackPane silueta = new StackPane();
        Polygon capa = new Polygon(21.0, 1.0, 35.0, 34.0, 7.0, 34.0);
        capa.setFill(Color.web("#63212d"));
        capa.setStroke(Color.web("#15090b"));
        capa.setStrokeWidth(1.2);

        Rectangle torso = new Rectangle(21, 22);
        torso.setArcWidth(6);
        torso.setArcHeight(6);
        torso.setTranslateY(8);
        torso.setFill(Color.web("#8c2f3c"));
        torso.setStroke(Color.web("#2a1015"));
        torso.setStrokeWidth(1);

        Circle rostro = new Circle(7);
        rostro.setTranslateY(-6);
        rostro.setFill(Color.web("#d1a06d"));
        rostro.setStroke(Color.web("#2b160f"));
        rostro.setStrokeWidth(0.9);

        Polygon corona = new Polygon(13.0, -10.0, 16.0, -18.0, 21.0, -11.0, 26.0, -18.0, 29.0, -10.0);
        corona.setFill(Color.web("#d8b55a"));
        corona.setStroke(Color.web("#2b2118"));
        corona.setStrokeWidth(0.8);

        Rectangle baston = new Rectangle(3, 26);
        baston.setTranslateX(15);
        baston.setTranslateY(6);
        baston.setRotate(-12);
        baston.setFill(Color.web("#c29b5a"));
        baston.setStroke(Color.web("#2b2118"));
        baston.setStrokeWidth(0.7);

        silueta.getChildren().addAll(capa, baston, torso, rostro, corona);
        return silueta;
    }

    /**
     * Crea el sprite del Espiritu Madre.
     *
     * @return silueta espectral
     */
    private StackPane crearSiluetaEspirituMadre() {
        StackPane silueta = new StackPane();
        Polygon manto = new Polygon(21.0, 0.0, 36.0, 30.0, 27.0, 26.0, 21.0, 35.0, 15.0, 26.0, 6.0, 30.0);
        manto.setFill(Color.web("#416f52"));
        manto.setStroke(Color.web("#102016"));
        manto.setStrokeWidth(1.2);

        Circle nucleo = new Circle(8);
        nucleo.setFill(Color.web("#a4e6a4", 0.62));
        nucleo.setStroke(Color.web("#e2ffd8"));
        nucleo.setStrokeWidth(1);

        Rectangle ramaIzq = new Rectangle(3, 21);
        ramaIzq.setTranslateX(-11);
        ramaIzq.setTranslateY(-6);
        ramaIzq.setRotate(-34);
        ramaIzq.setFill(Color.web("#7f9a5c"));

        Rectangle ramaDer = new Rectangle(3, 21);
        ramaDer.setTranslateX(11);
        ramaDer.setTranslateY(-6);
        ramaDer.setRotate(34);
        ramaDer.setFill(Color.web("#7f9a5c"));

        silueta.getChildren().addAll(manto, ramaIzq, ramaDer, nucleo);
        return silueta;
    }

    /**
     * Crea el sprite del Golem.
     *
     * @return silueta petrea
     */
    private StackPane crearSiluetaGolemMiniBoss() {
        StackPane silueta = new StackPane();
        Rectangle cuerpo = new Rectangle(28, 29);
        cuerpo.setArcWidth(4);
        cuerpo.setArcHeight(4);
        cuerpo.setTranslateY(4);
        cuerpo.setFill(Color.web("#5c6465"));
        cuerpo.setStroke(Color.web("#111111"));
        cuerpo.setStrokeWidth(1.5);

        Rectangle cabeza = new Rectangle(18, 12);
        cabeza.setArcWidth(3);
        cabeza.setArcHeight(3);
        cabeza.setTranslateY(-12);
        cabeza.setFill(Color.web("#707b7c"));
        cabeza.setStroke(Color.web("#111111"));
        cabeza.setStrokeWidth(1.1);

        Polygon grieta = new Polygon(19.0, -16.0, 22.0, -8.0, 18.0, -4.0, 23.0, 4.0, 18.0, 14.0);
        grieta.setFill(Color.TRANSPARENT);
        grieta.setStroke(Color.web("#c8d0cc"));
        grieta.setStrokeWidth(1.1);

        silueta.getChildren().addAll(cuerpo, cabeza, grieta);
        return silueta;
    }

    /**
     * Crea el sprite del Guardian Sin Nombre.
     *
     * @return silueta blindada
     */
    private StackPane crearSiluetaGuardianMiniBoss() {
        StackPane silueta = new StackPane();
        Rectangle torso = new Rectangle(24, 27);
        torso.setArcWidth(8);
        torso.setArcHeight(8);
        torso.setTranslateY(5);
        torso.setFill(Color.web("#4b5560"));
        torso.setStroke(Color.web("#101317"));
        torso.setStrokeWidth(1.4);

        Polygon escudo = new Polygon(21.0, 0.0, 34.0, 5.0, 31.0, 22.0, 21.0, 31.0, 11.0, 22.0, 8.0, 5.0);
        escudo.setFill(Color.web("#8090a0", 0.86));
        escudo.setStroke(Color.web("#e0bd65"));
        escudo.setStrokeWidth(1.1);

        Rectangle visor = new Rectangle(17, 5);
        visor.setArcWidth(2);
        visor.setArcHeight(2);
        visor.setTranslateY(-9);
        visor.setFill(Color.web("#c9f0ff"));
        visor.setStroke(Color.web("#101317"));
        visor.setStrokeWidth(0.7);

        silueta.getChildren().addAll(torso, escudo, visor);
        return silueta;
    }

    /**
     * Crea el sprite de El Filtro.
     *
     * @return silueta arcana
     */
    private StackPane crearSiluetaFiltroMiniBoss() {
        StackPane silueta = new StackPane();
        Polygon tunica = new Polygon(21.0, -2.0, 36.0, 34.0, 6.0, 34.0);
        tunica.setFill(Color.web("#3b2a62"));
        tunica.setStroke(Color.web("#130c20"));
        tunica.setStrokeWidth(1.3);

        Circle mascara = new Circle(9);
        mascara.setTranslateY(-5);
        mascara.setFill(Color.web("#c5bfd8"));
        mascara.setStroke(Color.web("#130c20"));
        mascara.setStrokeWidth(1);

        Polygon runa = new Polygon(21.0, -12.0, 27.0, -5.0, 21.0, 2.0, 15.0, -5.0);
        runa.setFill(Color.web("#72d6ff", 0.58));
        runa.setStroke(Color.web("#bfeeff"));
        runa.setStrokeWidth(0.9);

        silueta.getChildren().addAll(tunica, mascara, runa);
        return silueta;
    }

    /**
     * Crea una silueta especial para el Parásito final.
     *
     * @param parasito Parásito representado
     * @return nodo visual del Parásito
     */
    private StackPane crearSpriteParasito(ParasitoEnemy parasito) {
        StackPane sprite = new StackPane();
        sprite.setPrefSize(44, 38);
        sprite.setMaxSize(44, 38);

        Circle aura = new Circle(21);
        aura.setFill(Color.web(colorParasito(parasito), 0.24));
        aura.setStroke(Color.web("#72d6ff", 0.75));
        aura.setStrokeWidth(1.4);

        Polygon tentaculoIzq = new Polygon(19.0, 16.0, 4.0, 28.0, 10.0, 32.0, 23.0, 20.0);
        tentaculoIzq.setFill(Color.web("#21122e"));
        tentaculoIzq.setStroke(Color.web("#0b0710"));
        tentaculoIzq.setStrokeWidth(0.8);

        Polygon tentaculoDer = new Polygon(25.0, 16.0, 40.0, 28.0, 34.0, 32.0, 21.0, 20.0);
        tentaculoDer.setFill(Color.web("#21122e"));
        tentaculoDer.setStroke(Color.web("#0b0710"));
        tentaculoDer.setStrokeWidth(0.8);

        Polygon cuerpo = new Polygon(22.0, -2.0, 37.0, 12.0, 32.0, 32.0, 22.0, 38.0, 12.0, 32.0, 7.0, 12.0);
        cuerpo.setFill(Color.web(colorParasito(parasito)));
        cuerpo.setStroke(Color.web("#07040a"));
        cuerpo.setStrokeWidth(1.5);

        Circle nucleo = new Circle(8);
        nucleo.setFill(Color.web("#72d6ff", 0.50));
        nucleo.setStroke(Color.web("#d8fbff"));
        nucleo.setStrokeWidth(1.1);

        Circle ojoCentral = new Circle(3);
        ojoCentral.setFill(Color.web("#ffd166"));
        ojoCentral.setStroke(Color.web("#15050a"));
        ojoCentral.setStrokeWidth(0.6);

        Label fase = new Label(String.valueOf(parasito.getPhase()));
        fase.setFont(Font.font("Monospaced", 8));
        fase.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
        fase.setTranslateY(11);

        sprite.getChildren().addAll(aura, tentaculoIzq, tentaculoDer, cuerpo, nucleo, ojoCentral, fase);
        return sprite;
    }

    /**
     * Crea la silueta principal de un enemigo segun su familia.
     *
     * @param tipo nombre del tipo de enemigo
     * @param color color principal
     * @return nodo visual de silueta
     */
    private Node crearSiluetaEnemigo(String tipo, String color) {
        if ("ARCHER".equals(tipo) || "SNIPER".equals(tipo)) {
            StackPane silueta = new StackPane();
            Polygon cuerpo = new Polygon(17.0, 2.0, 25.0, 15.0, 20.0, 29.0, 14.0, 29.0, 9.0, 15.0);
            cuerpo.setFill(Color.web(color));
            cuerpo.setStroke(Color.web("#140c0c"));
            cuerpo.setStrokeWidth(1.2);
            Rectangle arco = new Rectangle(3, 27);
            arco.setArcWidth(4);
            arco.setArcHeight(4);
            arco.setTranslateX(12);
            arco.setFill(Color.web("#c29b5a"));
            arco.setStroke(Color.web("#1d130b"));
            arco.setStrokeWidth(0.6);
            silueta.getChildren().addAll(cuerpo, arco);
            return silueta;
        }
        if ("GUARDIAN".equals(tipo) || "CONSTRUCTO".equals(tipo) || "DESTRUCTOR".equals(tipo)) {
            StackPane silueta = new StackPane();
            Rectangle torso = new Rectangle(23, 25);
            torso.setArcWidth(5);
            torso.setArcHeight(5);
            torso.setFill(Color.web(color));
            torso.setStroke(Color.web("#111111"));
            torso.setStrokeWidth(1.4);
            Rectangle placa = new Rectangle(15, 7);
            placa.setTranslateY(8);
            placa.setFill(Color.web("#8c949a"));
            placa.setStroke(Color.web("#20252a"));
            placa.setStrokeWidth(0.7);
            silueta.getChildren().addAll(torso, placa);
            return silueta;
        }
        if ("CONTROLLER".equals(tipo) || "SUMMONER".equals(tipo) || "ECO_DE_MAGIA".equals(tipo)) {
            StackPane silueta = new StackPane();
            Polygon tunica = new Polygon(17.0, 1.0, 29.0, 29.0, 5.0, 29.0);
            tunica.setFill(Color.web(color));
            tunica.setStroke(Color.web("#140c0c"));
            tunica.setStrokeWidth(1.2);
            Circle foco = new Circle(6);
            foco.setTranslateY(9);
            foco.setFill(Color.web("#72d6ff", 0.38));
            foco.setStroke(Color.web("#bfeeff"));
            foco.setStrokeWidth(1);
            silueta.getChildren().addAll(tunica, foco);
            return silueta;
        }
        if ("BERSERKER".equals(tipo)) {
            StackPane silueta = new StackPane();
            Polygon cuerpo = new Polygon(17.0, 2.0, 30.0, 17.0, 24.0, 30.0, 10.0, 30.0, 4.0, 17.0);
            cuerpo.setFill(Color.web(color));
            cuerpo.setStroke(Color.web("#140c0c"));
            cuerpo.setStrokeWidth(1.3);
            Polygon cuernos = new Polygon(8.0, 4.0, 1.0, 1.0, 5.0, 10.0, 26.0, 4.0, 33.0, 1.0, 29.0, 10.0);
            cuernos.setFill(Color.web("#d7c8aa"));
            cuernos.setStroke(Color.web("#140c0c"));
            cuernos.setStrokeWidth(0.7);
            silueta.getChildren().addAll(cuernos, cuerpo);
            return silueta;
        }

        Polygon sombra = new Polygon(
            17.0, 2.0,
            30.0, 14.0,
            25.0, 29.0,
            9.0, 29.0,
            4.0, 14.0
        );
        sombra.setFill(Color.web(color));
        sombra.setStroke(Color.web("#140c0c"));
        sombra.setStrokeWidth(1.5);
        return sombra;
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
                controller.onRecoger(stage);
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

        ValdrisTheme.mostrarDialogoNarrativo(stage, "Eco de " + nombreSalaActual(),
            "La sala parece recordar algo que solo tu personaje puede escuchar.", dialogo);
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
        recoger.setOnAction(event -> controller.onRecoger(stage));

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
            logCombate.addMensaje("No hay ruta disponible hacia el Núcleo.");
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
     * @param room sala consultada
     * @param cell celda consultada
     * @param fila fila de la celda
     * @param col columna de la celda
     * @return color JavaFX
     */
    private Color colorCelda(Room room, Cell cell, int fila, int col) {
        CellType tipo = cell.getTipo();
        if (tipo == CellType.WALL) {
            return colorConZona(room, fila, col, Color.web("#3a3a3a"), 0.32);
        }
        if (esAccesoSecretoRevelado(room, cell)) {
            return colorConZona(room, fila, col, Color.web("#2f8f7a"), 0.18);
        }
        if (tipo == CellType.DOOR) {
            return colorConZona(room, fila, col, Color.web("#7b5736"), 0.24);
        }
        if (tipo == CellType.DOOR_LOCKED) {
            return colorConZona(room, fila, col, Color.web("#4f2d20"), 0.22);
        }
        if (tipo == CellType.DOOR_HIDDEN && !cell.isDescubierta()) {
            return colorConZona(room, fila, col, Color.web("#3a3a3a"), 0.32);
        }
        if (tipo == CellType.STAIRS_UP || tipo == CellType.STAIRS_DOWN) {
            return colorConZona(room, fila, col, Color.web("#5f5b74"), 0.40);
        }
        if (tipo == CellType.LEVER) {
            if (isPuzzleCellActive(room, cell)) {
                return colorConZona(room, fila, col, Color.web("#3f8f4f"), 0.18);
            }
            return colorConZona(room, fila, col, Color.web("#6f613b"), 0.30);
        }
        if (tipo == CellType.RUNE) {
            if (isPuzzleCellActive(room, cell)) {
                return colorConZona(room, fila, col, Color.web("#3f8f4f"), 0.18);
            }
            return colorConZona(room, fila, col, Color.web("#324f64"), 0.30);
        }
        if (tipo == CellType.TRAP) {
            return colorConZona(room, fila, col, Color.web("#b8b1a3"), 0.48);
        }
        if (esTriggerSecretoDeSuelo(room, cell)) {
            return colorConZona(room, fila, col, Color.web("#c9bf9f"), 0.26);
        }
        return colorConZona(room, fila, col, Color.web("#b8b1a3"), 0.54);
    }

    /**
     * Aplica el matiz ambiental de la sala sobre un color funcional.
     *
     * @param room sala consultada
     * @param fila fila de la celda
     * @param col columna de la celda
     * @param base color funcional inicial
     * @param intensidad peso del matiz de zona
     * @return color final de la celda
     */
    private Color colorConZona(Room room, int fila, int col, Color base, double intensidad) {
        if (room == null) {
            return base;
        }
        return ValdrisTheme.aplicarMatizZona(room.getId(), fila, col, room.getFilas(), room.getCols(),
            base, intensidad);
    }

    /**
     * Ajusta el titulo de la sala con un acento coherente con la zona actual.
     *
     * @param room sala actual
     */
    private void actualizarTituloSala(Room room) {
        String acento = ValdrisTheme.getColorAcentoZona(room == null ? null : room.getId());
        tituloSala.setStyle("-fx-text-fill: #f5f0e6;"
            + "-fx-border-color: transparent transparent " + acento + " transparent;"
            + "-fx-border-width: 0 0 2 0;"
            + "-fx-padding: 0 12 4 12;");
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
     * Devuelve el color de aura de un mini-boss.
     *
     * @param miniBoss mini-boss consultado
     * @return color CSS hexadecimal
     */
    private String colorAuraMiniBoss(MiniBossEnemy miniBoss) {
        String tipo = miniBoss.getTipoMiniBoss().name();
        if ("ESPIRITU_MADRE".equals(tipo)) {
            return "#7dbf73";
        }
        if ("GOLEM".equals(tipo)) {
            return "#8c949a";
        }
        if ("GUARDIAN_SIN_NOMBRE".equals(tipo)) {
            return "#8ca3c0";
        }
        if ("EL_FILTRO".equals(tipo)) {
            return "#8b6fd8";
        }
        return "#b04c5a";
    }

    /**
     * Devuelve una etiqueta corta para mini-bosses narrativos.
     *
     * @param miniBoss mini-boss consultado
     * @return texto compacto
     */
    private String etiquetaMiniBoss(MiniBossEnemy miniBoss) {
        String tipo = miniBoss.getTipoMiniBoss().name();
        if ("ESPIRITU_MADRE".equals(tipo)) {
            return "EM";
        }
        if ("GOLEM".equals(tipo)) {
            return "GM";
        }
        if ("GUARDIAN_SIN_NOMBRE".equals(tipo)) {
            return "GS";
        }
        if ("EL_FILTRO".equals(tipo)) {
            return "FI";
        }
        return "AC";
    }

    /**
     * Devuelve el color del Parásito segun su fase.
     *
     * @param parasito Parásito consultado
     * @return color CSS hexadecimal
     */
    private String colorParasito(ParasitoEnemy parasito) {
        if (parasito.getPhase() == ParasitoEnemy.FASE_ESENCIA) {
            return "#201137";
        }
        if (parasito.getPhase() == ParasitoEnemy.FASE_DESGARRADA) {
            return "#3b1238";
        }
        return "#2f1d35";
    }

    /**
     * Devuelve una etiqueta corta para distinguir tipos de enemigo en el mapa.
     *
     * @param tipo nombre del tipo de enemigo
     * @return texto compacto
     */
    private String etiquetaEnemigo(String tipo) {
        if ("ARCHER".equals(tipo)) {
            return "AR";
        }
        if ("SNIPER".equals(tipo)) {
            return "SN";
        }
        if ("GUARDIAN".equals(tipo)) {
            return "G";
        }
        if ("CONSTRUCTO".equals(tipo)) {
            return "CO";
        }
        if ("DESTRUCTOR".equals(tipo)) {
            return "D";
        }
        if ("CONTROLLER".equals(tipo)) {
            return "CT";
        }
        if ("SUMMONER".equals(tipo)) {
            return "IN";
        }
        if ("ECO_DE_MAGIA".equals(tipo)) {
            return "EC";
        }
        if ("SOMBRA_ABSORBIDA".equals(tipo)) {
            return "SO";
        }
        if ("PARASITO".equals(tipo)) {
            return "P";
        }
        if ("BERSERKER".equals(tipo)) {
            return "B";
        }
        return "E";
    }

    /**
     * Devuelve el color de brillo asociado a una categoria de item.
     *
     * @param tipo tipo funcional del item
     * @return color hexadecimal CSS
     */
    private String colorBrilloItem(ItemType tipo) {
        if (tipo == ItemType.WEAPON) {
            return "#d8dce5";
        }
        if (tipo == ItemType.ARMOR || tipo == ItemType.SHIELD) {
            return "#9fb6c8";
        }
        if (tipo == ItemType.POTION) {
            return "#d95f8a";
        }
        if (tipo == ItemType.ACCESSORY) {
            return "#d8b55a";
        }
        return "#6bc0d0";
    }

    /**
     * Devuelve una etiqueta corta para un item de suelo.
     *
     * @param tipo tipo funcional del item
     * @return texto compacto
     */
    private String etiquetaItem(ItemType tipo) {
        if (tipo == ItemType.WEAPON) {
            return "W";
        }
        if (tipo == ItemType.ARMOR) {
            return "A";
        }
        if (tipo == ItemType.SHIELD) {
            return "S";
        }
        if (tipo == ItemType.POTION) {
            return "P";
        }
        if (tipo == ItemType.ACCESSORY) {
            return "AC";
        }
        return "N";
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
