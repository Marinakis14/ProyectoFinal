package Valdris.persistence;

/**
 * DTO plano que representa la configuracion inicial del mundo en JSON.
 *
 * <p>Esta clase no contiene logica de juego. Sus campos publicos permiten que
 * Gson lea directamente salas, celdas, conexiones, enemigos, posicion inicial
 * y objetivo sin introducir dependencias desde las capas inferiores.</p>
 */
public class GameConfig {

    /** Version del formato de configuracion. */
    public int version;

    /** ID de la sala inicial. */
    public String initialRoomId;

    /** ID de la sala objetivo de la partida. */
    public String objectiveRoomId;

    /** Posicion inicial del jugador. */
    public PositionDTO initialPlayerPosition;

    /** Reglas de variacion controlada al cargar el mundo. */
    public RandomizationConfigDTO randomization;

    /** Salas declaradas en el mapa. */
    public RoomConfigDTO[] rooms;

    /** Conexiones del grafo de salas. */
    public ConnectionConfigDTO[] connections;

    /** Casillas validas para recolocar enemigos normales por sala. */
    public SpawnCandidatesConfigDTO[] spawnCandidates;

    /** Enemigos iniciales del mundo. */
    public EnemyConfigDTO[] enemies;

    /** Datos fijos del combate final. */
    public FinalCombatConfigDTO finalCombat;

    /**
     * Coordenada dentro de una sala.
     */
    public static class PositionDTO {

        /** ID de sala asociado, si aplica. */
        public String roomId;

        /** Fila de la coordenada. */
        public int row;

        /** Columna de la coordenada. */
        public int col;
    }

    /**
     * Reglas globales de aleatoriedad permitidas por la configuracion.
     */
    public static class RandomizationConfigDTO {

        /** Indica si los enemigos normales usan candidatos de sala. */
        public boolean randomEnemyPositions;

        /** Indica si los drops normales se calculan por tipo de enemigo. */
        public boolean randomEnemyDrops;

        /** Indica si los puzzles permutan su secuencia al cargar. */
        public boolean randomPuzzleSequences;

        /** Indica si los items de suelo se eligen desde pools declarados. */
        public boolean randomGroundItems;
    }

    /**
     * Configuracion serializable de una sala.
     */
    public static class RoomConfigDTO {

        /** ID unico de sala. */
        public String id;

        /** Nombre visible. */
        public String name;

        /** Numero de filas. */
        public int rows;

        /** Numero de columnas. */
        public int cols;

        /** Posicion de entrada por defecto. */
        public PositionDTO entry;

        /** Limite de turnos, o negativo si no hay limite. */
        public int timer;

        /** Matriz textual completa de la sala. */
        public String[] layout;

        /** Metadatos de celdas especiales. */
        public CellConfigDTO[] cells;

        /** Dialogos narrativos por personaje. */
        public DialogueConfigDTO[] dialogues;

        /** Triggers secretos de la sala. */
        public SecretTriggerConfigDTO[] secretTriggers;

        /** Puzzle de la sala, si existe. */
        public PuzzleConfigDTO puzzle;
    }

    /**
     * Metadatos de una celda concreta.
     */
    public static class CellConfigDTO {

        /** Fila de la celda. */
        public int row;

        /** Columna de la celda. */
        public int col;

        /** Tipo explicito de celda, si se quiere sobrescribir el layout. */
        public String type;

        /** Sala destino del acceso. */
        public String targetRoomId;

        /** Fila de llegada en la sala destino. */
        public int targetRow;

        /** Columna de llegada en la sala destino. */
        public int targetCol;

        /** ID de item narrativo requerido para usar el acceso. */
        public String requiredItemId;

        /** Trigger asociado a la celda. */
        public String triggerId;

        /** ID de item de suelo. */
        public String itemId;

        /** Pool de items de suelo posibles. */
        public String[] itemPool;

        /** Orientacion de uso para escaleras. */
        public AccessFacingDTO accessFacing;

        /** Contenedor colocado en la celda. */
        public ContainerConfigDTO container;
    }

    /**
     * Orientacion frontal de un acceso.
     */
    public static class AccessFacingDTO {

        /** Delta de fila desde el acceso hasta la celda de uso. */
        public int rowDelta;

        /** Delta de columna desde el acceso hasta la celda de uso. */
        public int colDelta;
    }

    /**
     * Contenedor inicial de una celda.
     */
    public static class ContainerConfigDTO {

        /** ID del contenedor. */
        public String id;

        /** Nombre visible del contenedor. */
        public String name;

        /** IDs de items dentro del contenedor. */
        public String[] items;
    }

    /**
     * Dialogo de sala para un personaje.
     */
    public static class DialogueConfigDTO {

        /** CharacterType.name(). */
        public String character;

        /** Texto del dialogo. */
        public String text;
    }

    /**
     * Asociacion entre trigger de celda y pasadizo o puerta activable.
     */
    public static class SecretTriggerConfigDTO {

        /** ID del trigger pisado o activado. */
        public String triggerId;

        /** ID del destino activado. */
        public String targetId;
    }

    /**
     * Configuracion de puzzle de secuencia.
     */
    public static class PuzzleConfigDTO {

        /** Tipo textual del puzzle. */
        public String type;

        /** Danio aplicado al fallar. */
        public int failureDamage;

        /** Trigger o pasadizo activado al resolver. */
        public String successTarget;

        /** Secuencia correcta determinista. */
        public int[] correctSequence;

        /** Valores que deben permutarse para la secuencia correcta. */
        public int[] sequenceValues;
    }

    /**
     * Casillas validas para aparicion aleatoria de enemigos en una sala.
     */
    public static class SpawnCandidatesConfigDTO {

        /** ID de sala asociada. */
        public String roomId;

        /** Candidatos transitables para enemigos normales. */
        public PositionDTO[] cells;
    }

    /**
     * Conexion del grafo de salas.
     */
    public static class ConnectionConfigDTO {

        /** ID de sala origen. */
        public String from;

        /** ID de sala destino. */
        public String to;

        /** Descripcion de la arista. */
        public String description;

        /** Modo: BIDIRECTIONAL, ONE_WAY o HIDDEN. */
        public String mode;

        /** ID de pasadizo oculto o puerta activable. */
        public String id;

        /** Indica si una conexion oculta se activa en ambos sentidos. */
        public boolean bidirectionalOnActivation;
    }

    /**
     * Enemigo inicial de una sala.
     */
    public static class EnemyConfigDTO {

        /** ID de sala del enemigo. */
        public String roomId;

        /** EnemyType.name() si es enemigo normal. */
        public String type;

        /** MiniBossType.name() si es mini-boss. */
        public String miniBossType;

        /** Fila inicial. */
        public int row;

        /** Columna inicial. */
        public int col;

        /** ID de item que suelta al morir. */
        public String dropItemId;
    }

    /**
     * Parametros fijos del combate final.
     */
    public static class FinalCombatConfigDTO {

        /** Sala donde ocurre el combate final. */
        public String roomId;

        /** Posicion inicial de Malachar. */
        public PositionDTO malachar;

        /** Posicion inicial del Parasito. */
        public PositionDTO parasito;

        /** Radio del pulso del Parasito. */
        public int parasitoPulseRadius;

        /** Danio base del pulso. */
        public int parasitoBasePulseDamage;

        /** Danio del pulso intensificado. */
        public int parasitoIntensifiedPulseDamage;

        /** Danio de Devorar Luz. */
        public int parasitoDevourLightDamage;
    }
}
