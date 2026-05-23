package Valdris.persistence;

/**
 * Snapshot plano y serializable del estado completo de una partida.
 *
 * <p>La clase evita referencias directas entre objetos del dominio. Las salas,
 * unidades, items y accesos se guardan mediante IDs, coordenadas y valores
 * primitivos para que Gson pueda serializar el estado sin ciclos.</p>
 */
public class GameState {

    // -- Estado global --------------------------------------------------------

    /** ID de la sala actual del jugador. */
    public String idRoomActual;

    /** Tipo de personaje elegido, como CharacterType.name(). */
    public String tipoPersonaje;

    /** Fase actual del TurnManager, como Phase.name(). */
    public String faseActual;

    /** Turno global acumulado. */
    public int turnoGlobal;

    /** Último diálogo pendiente para la interfaz. */
    public String lastDialogue;

    // -- Estado del jugador --------------------------------------------------

    /** HP actual del jugador. */
    public int hpJugador;

    /** Fila actual del jugador. */
    public int filaJugador;

    /** Columna actual del jugador. */
    public int colJugador;

    /** Indica si el jugador ya movió este turno. */
    public boolean haMovido;

    /** Indica si el jugador ya resolvió recogida/interacción este turno. */
    public boolean haRecogido;

    /** Indica si el jugador ya usó item este turno. */
    public boolean haUsadoItem;

    /** Indica si el jugador ya atacó este turno. */
    public boolean haAtacado;

    /** Bonus temporal pendiente del siguiente ataque. */
    public int bonusAtaqueTemporal;

    /** IDs de items del inventario normal. */
    public String[] itemsInventario;

    /** IDs de items narrativos de progresión. */
    public String[] itemsNarrativos;

    /** ID del arma equipada, o null. */
    public String armaEquipada;

    /** ID de la armadura equipada, o null. */
    public String armaduraEquipada;

    /** ID del escudo equipado, o null. */
    public String escudoEquipado;

    /** ID del accesorio equipado, o null. */
    public String accesorioEquipado;

    /** Efectos activos del jugador. */
    public EffectStateDTO[] efectosJugador;

    // -- Mundo ---------------------------------------------------------------

    /** Estados dinámicos de salas. */
    public RoomStateDTO[] salas;

    /** Estados de enemigos. */
    public EnemyStateDTO[] enemigos;

    /** IDs de pasadizos ocultos activos. */
    public String[] pasadizosActivos;

    /** Log estructurado acumulativo de la partida. */
    public GameLogEntryDTO[] logEventos;

    // -- DTOs internos --------------------------------------------------------

    /**
     * Estado serializable de un efecto activo.
     */
    public static class EffectStateDTO {

        /** EffectType.name(). */
        public String tipo;

        /** Turnos restantes. */
        public int turnos;
    }

    /**
     * Estado serializable de una entrada del log de partida.
     */
    public static class GameLogEntryDTO {

        /** Turno global del evento. */
        public int turno;

        /** LogEventType.name(). */
        public String tipo;

        /** Actor principal del evento. */
        public String actor;

        /** ID de sala asociado. */
        public String salaId;

        /** Mensaje visible. */
        public String mensaje;

        /** Detalle estructurado opcional. */
        public String detalle;
    }

    /**
     * Estado serializable de una sala.
     */
    public static class RoomStateDTO {

        /** ID de la sala. */
        public String idSala;

        /** Si la sala fue explorada. */
        public boolean explorada;

        /** Fila de entrada registrada en la sala. */
        public int filaJugador;

        /** Columna de entrada registrada en la sala. */
        public int colJugador;

        /** Si la sala tiene temporizador. */
        public boolean hasRoomTimer;

        /** Turnos restantes del temporizador. */
        public int turnosRestantes;

        /** Si el diálogo de Kael ya fue mostrado. */
        public boolean dialogoKaelMostrado;

        /** Si el diálogo de Syra ya fue mostrado. */
        public boolean dialogoSyraMostrado;

        /** Si el diálogo de Dorath ya fue mostrado. */
        public boolean dialogoDorathMostrado;

        /** Si el puzzle ya fue resuelto. */
        public boolean puzzleResolved;

        /** Objetivo de éxito del puzzle. */
        public String puzzleSuccessTarget;

        /** Daño de fallo del puzzle. */
        public int puzzleFailureDamage;

        /** Secuencia correcta del puzzle. */
        public int[] correctSequence;

        /** Secuencia activada parcialmente. */
        public int[] activeSequence;

        /** Celdas dinámicas de la sala. */
        public CellStateDTO[] celdas;
    }

    /**
     * Estado serializable de una celda dinámica.
     */
    public static class CellStateDTO {

        /** Fila de la celda. */
        public int fila;

        /** Columna de la celda. */
        public int col;

        /** CellType.name(). */
        public String tipo;

        /** Si la celda está descubierta. */
        public boolean descubierta;

        /** ID de item en suelo, o null. */
        public String itemId;

        /** Estado de contenedor, o null. */
        public ContainerStateDTO container;
    }

    /**
     * Estado serializable de un contenedor.
     */
    public static class ContainerStateDTO {

        /** ID del contenedor. */
        public String id;

        /** Nombre visible. */
        public String nombre;

        /** Si ya fue abierto. */
        public boolean abierto;

        /** IDs de items restantes. */
        public String[] itemsRestantes;
    }

    /**
     * Estado serializable de un enemigo.
     */
    public static class EnemyStateDTO {

        /** ID de sala donde está o estaba el enemigo. */
        public String idSala;

        /** EnemyType.name(). */
        public String tipoEnemigo;

        /** MiniBossType.name() si aplica. */
        public String miniBossType;

        /** Fila actual. */
        public int fila;

        /** Columna actual. */
        public int col;

        /** HP actual. */
        public int hp;

        /** Si sigue vivo. */
        public boolean vivo;

        /** ID del drop asignado, o null. */
        public String dropItemId;

        /** Turnos de cooldown acumulados. */
        public int turnosSinActuar;

        /** Si el enemigo está marcado como mini-jefe. */
        public boolean esMiniJefe;

        /** Efectos activos. */
        public EffectStateDTO[] efectos;
    }
}
