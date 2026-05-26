# TASKS.md — Registro de tareas completadas
## Valdris: El Núcleo Profundo
### Grupo H12GEXTRA | Java 21 + JavaFX

---

## Propósito del fichero

Este documento guarda el historial de tareas ya realizadas en el proyecto.
Sirve como checklist técnico de avance y como complemento operativo de `COMMIT_LOG.md`.

La idea es mantener aquí una lista clara de qué se ha completado, qué ficheros o zonas del proyecto se han tocado y con qué verificación se cerró cada bloque.

Este fichero debe actualizarse al terminar cada tarea relevante:

- Implementación de clases.
- Creación o ampliación de tests.
- Correcciones de compilación.
- Ajustes autorizados en estructuras propias.
- Decisiones técnicas aceptadas por el equipo.

---

## Estado global actual

| Área | Estado |
|------|--------|
| Capas 2, 3 y 4 | Completadas y testeadas |
| Primera parte de capa 5 | BFS, visión, combate, árbol de decisión IA, IA enemiga, TurnManager, ItemGenerator, PuzzleManager y DungeonGenerator completados |
| Capa 6 inicial | GameState, LoadedGame, GameSummary, LectorJSON, GameConfig y DungeonConfigLoader completados |
| Capa 7 JavaFX | Pantallas principales, partida, inventario, autoguardado, diálogos, pantalla final, correcciones jugables, redistribución de pantalla, sprites de unidades, feedback visual de puzzles y secretos, distancia/ruta global a salida y contadores de turno completados |
| Tests JUnit actuales | 493 tests pasando |
| Última verificación completa | `mvn test` con 0 fallos, 0 errores, 0 omitidos |

---

## Documentación y análisis inicial

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Leer `AGENTS.md` y extraer reglas obligatorias de trabajo | ✅ Completado |
| 2 | Leer `PROJECT_SPEC.md` y resumir arquitectura general del proyecto | ✅ Completado |
| 3 | Revisar `guia_codex.pdf` como guía principal de implementación | ✅ Completado |
| 4 | Revisar los PDF de diseño añadidos como contexto del juego | ✅ Completado |
| 5 | Definir que los comentarios/Javadoc deben ser extensos y con secciones claras | ✅ Completado |
| 6 | Confirmar que cualquier cambio importante debe avisarse antes de aplicarse | ✅ Completado |

---

## Saneamiento inicial del proyecto

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Revisar el estado real del repositorio antes de modificar código | ✅ Completado |
| 2 | Sanear `pom.xml` para Java 21 con `maven.compiler.release` | ✅ Completado |
| 3 | Configurar `src` como directorio principal de código | ✅ Completado |
| 4 | Configurar `tests` como directorio de tests | ✅ Completado |
| 5 | Añadir Gson 2.10.1 como dependencia permitida | ✅ Completado |
| 6 | Retirar FXGL del `pom.xml` | ✅ Completado |
| 7 | Ajustar `mainClass` JavaFX a `Valdris.ui.MainApp` | ✅ Completado |
| 8 | Compilar el proyecto tras el saneamiento inicial | ✅ Completado |

---

## Ajustes autorizados en `MisEstructurasDeDatos`

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Corregir imports/paquetes mínimos para compilar estructuras propias | ✅ Completado |
| 2 | Mantener intacta la lógica interna de listas, colas, pilas y grafos durante ese ajuste | ✅ Completado |
| 3 | Hacer públicos getters necesarios de `NodoGrafo` (`getId`, `getDatos`) | ✅ Completado |
| 4 | Hacer públicos getters necesarios de `Arista` (`getOrigen`, `getDato`, `getDestino`) | ✅ Completado |
| 5 | Añadir `buscarNodoPorDato(DN dato)` en `Grafo` | ✅ Completado |
| 6 | Añadir `buscarNodoPorDato(DN dato)` en `InterfazGrafo` | ✅ Completado |

> Nota: estos cambios se hicieron con permiso explícito. Cualquier cambio futuro en `MisEstructurasDeDatos` debe consultarse antes.

---

## Capa 2 — Modelo base

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Revisar y mejorar comentarios de enums con estilo similar a `CellType` | ✅ Completado |
| 2 | Implementar/completar `EffectType` | ✅ Completado |
| 3 | Implementar/completar `ItemType` | ✅ Completado |
| 4 | Implementar/completar `CharacterType` | ✅ Completado |
| 5 | Implementar/completar `EnemyType` | ✅ Completado |
| 6 | Implementar/completar `Phase` | ✅ Completado |
| 7 | Ajustar `Effect` con `getTurnos()` | ✅ Completado |
| 8 | Hacer `Effect` compatible con `Comparable<Effect>` | ✅ Completado |
| 9 | Implementar `Item` como clase abstracta comparable | ✅ Completado |
| 10 | Implementar `Weapon` | ✅ Completado |
| 11 | Implementar `Armor` | ✅ Completado |
| 12 | Implementar `Potion` | ✅ Completado |
| 13 | Implementar `Accessory` | ✅ Completado |
| 14 | Añadir comentarios por secciones en items: atributos, constructor, lógica, getters y comparación | ✅ Completado |
| 15 | Ampliar `Weapon` para permitir hasta dos efectos especiales | ✅ Completado |
| 16 | Rediseñar `BLIND` para probabilidad de fallo de ataque en vez de reducción de movimiento | ✅ Completado |
| 17 | Ampliar `Potion` con efectos concretos a limpiar y bonus temporal de ataque | ✅ Completado |
| 18 | Reemplazar `STAIRS` por `STAIRS_UP` y `STAIRS_DOWN` en `CellType` | ✅ Completado |
| 19 | Añadir `MiniBossType` para mini-bosses narrativos separados de enemigos normales | ✅ Completado |
| 20 | Añadir `ItemType.NARRATIVE` para objetos de progresión con espacio propio | ✅ Completado |
| 21 | Implementar `NarrativeItem` para llaves, fragmentos y documentos de historia | ✅ Completado |

---

## Capa 3 — Unidades

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Implementar `Unit` como clase abstracta base | ✅ Completado |
| 2 | Implementar gestión común de HP, daño, curación y vida | ✅ Completado |
| 3 | Implementar lista de efectos activos en `Unit` | ✅ Completado |
| 4 | Implementar reemplazo de efectos repetidos | ✅ Completado |
| 5 | Implementar `BURN` como daño periódico y `CURSE` como bonus al daño enemigo recibido | ✅ Completado |
| 6 | Implementar movimiento efectivo reducido por `SLOW` | ✅ Completado |
| 7 | Implementar `Player` | ✅ Completado |
| 8 | Implementar inventario y equipo del jugador | ✅ Completado |
| 9 | Implementar cálculo de ataque, defensa, movimiento y rango efectivo del jugador | ✅ Completado |
| 10 | Implementar inmunidades por armadura o escudo | ✅ Completado |
| 11 | Implementar flags de acciones del turno del jugador | ✅ Completado |
| 12 | Implementar `Enemy` | ✅ Completado |
| 13 | Implementar estadísticas base por `EnemyType` | ✅ Completado |
| 14 | Implementar cooldown de enemigo | ✅ Completado |
| 15 | Implementar drop de enemigo con `onDeath(Room)` | ✅ Completado |
| 16 | Hacer `Player` compatible con `Comparable<Player>` | ✅ Completado |
| 17 | Hacer `Enemy` compatible con `Comparable<Enemy>` | ✅ Completado |
| 18 | Añadir `removeEfecto(EffectType)` en `Unit` para limpiar efectos concretos | ✅ Completado |
| 19 | Añadir bonus temporal de ataque en `Player` para la poción P5 | ✅ Completado |
| 20 | Añadir inventario narrativo separado en `Player` | ✅ Completado |
| 21 | Añadir `CONSTRUCTO`, `SOMBRA_ABSORBIDA` y `ECO_DE_MAGIA` a `EnemyType` | ✅ Completado |
| 22 | Ajustar estadísticas de enemigos nuevos según tabla acordada | ✅ Completado |
| 23 | Implementar penetración natural de defensa para `ECO_DE_MAGIA` | ✅ Completado |
| 24 | Implementar `MiniBossEnemy` con estadísticas propias y tipo narrativo | ✅ Completado |
| 25 | Ajustar el ataque base de Kael, Syra y Dorath con +5 de daño inicial | ✅ Completado |
| 26 | Reducir el movimiento base de Syra de 5 a 4 para compensar el anillo de velocidad inicial | ✅ Completado |
| 27 | Añadir 3 puntos de defensa base a todos los personajes jugables | ✅ Completado |
| 28 | Aplicar un segundo aumento de +5 al ataque base de Kael, Syra y Dorath | ✅ Completado |

---

## Capa 4 — Mapa

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Implementar `Cell` | ✅ Completado |
| 2 | Implementar transitabilidad de celda con `isWalkable()` | ✅ Completado |
| 3 | Implementar gestión de unidad en celda | ✅ Completado |
| 4 | Implementar gestión de item en celda | ✅ Completado |
| 5 | Implementar revelado de `DOOR_HIDDEN` | ✅ Completado |
| 6 | Implementar `Room` | ✅ Completado |
| 7 | Inicializar matriz de celdas de `Room` como `FLOOR` | ✅ Completado |
| 8 | Implementar validación de límites con `InvalidMoveException` | ✅ Completado |
| 9 | Implementar gestión de enemigos en sala | ✅ Completado |
| 10 | Implementar temporizador de sala con `GameStateException` | ✅ Completado |
| 11 | Implementar búsqueda de celda libre cercana | ✅ Completado |
| 12 | Implementar `Dungeon` | ✅ Completado |
| 13 | Implementar `Dungeon` sobre `Grafo<Room, String>` | ✅ Completado |
| 14 | Implementar conexiones bidireccionales entre salas | ✅ Completado |
| 15 | Implementar conexiones unidireccionales para punto de no retorno | ✅ Completado |
| 16 | Implementar búsqueda de salas por id | ✅ Completado |
| 17 | Implementar sala actual y nodo actual | ✅ Completado |
| 18 | Evitar duplicado de salas por id en `Dungeon` | ✅ Completado |
| 19 | Evitar aristas duplicadas en `Dungeon.conectar` | ✅ Completado |
| 20 | Evitar aristas duplicadas en `Dungeon.conectarUnidireccional` | ✅ Completado |
| 21 | Implementar `Container` como base de contenedores del mapa | ✅ Completado |
| 22 | Implementar `Chest` como cofre concreto | ✅ Completado |
| 23 | Ampliar `Cell` con contenedor opcional no transitable | ✅ Completado |
| 24 | Ampliar `Cell` con destino concreto para accesos entre salas | ✅ Completado |
| 25 | Mantener los items de suelo transitables para recogida automática | ✅ Completado |
| 26 | Hacer puertas y escaleras accesos no transitables usados desde celda adyacente | ✅ Completado |
| 27 | Añadir orientación frontal para escaleras `STAIRS_UP` y `STAIRS_DOWN` | ✅ Completado |
| 28 | Añadir helpers de acceso, trigger, resaltado, reserva y requisito de item en `Cell` | ✅ Completado |
| 29 | Centralizar bloqueo de visión en `Cell.bloqueaVision()` | ✅ Completado |
| 30 | Añadir diálogos por personaje en `Room` sin usar `Map` | ✅ Completado |
| 31 | Añadir secuencias comunes para palancas y runas en `Room` | ✅ Completado |
| 32 | Añadir triggers secretos y objetivos de puzzle en `Room` | ✅ Completado |
| 33 | Añadir limpieza de resaltado y validación de celdas de llegada en `Room` | ✅ Completado |
| 34 | Crear `HiddenPassage` como pasadizo oculto comparable | ✅ Completado |
| 35 | Añadir pasadizos ocultos activables en `Dungeon` | ✅ Completado |
| 36 | Hacer las celdas `LEVER` no transitables para que jugador y enemigos no oculten palancas | ✅ Completado |

---

## Excepciones personalizadas

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Implementar `InvalidMoveException` | ✅ Completado |
| 2 | Implementar `InvalidAttackException` | ✅ Completado |
| 3 | Implementar `GameStateException` | ✅ Completado |
| 4 | Usar excepciones personalizadas en mapa y combate cuando corresponde | ✅ Completado |

---

## Capa 5 — Lógica implementada

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Revisar la capa 5 de `guia_codex.pdf` | ✅ Completado |
| 2 | Implementar `BFSMovimiento` | ✅ Completado |
| 3 | Implementar `getCellsInRange(Room, int, int, int)` | ✅ Completado |
| 4 | Implementar `getCamino(Room, int, int, int, int)` | ✅ Completado |
| 5 | Usar `Cola` propia y `ListaSimplementeEnlazada` en BFS de celdas | ✅ Completado |
| 6 | Implementar `BFSCaminoMinimo` como utilidad de caminos entre salas | ✅ Completado |
| 7 | Implementar `getCamino(Dungeon, Room, Room)` | ✅ Completado |
| 8 | Implementar `getCaminoPorId(Dungeon, String, String)` | ✅ Completado |
| 9 | Implementar `getDistancia(Dungeon, Room, Room)` | ✅ Completado |
| 10 | Implementar `LineaDeVision` con Bresenham | ✅ Completado |
| 11 | Decidir que `WALL` y `STAIRS_UP` bloquean línea de visión | ✅ Completado |
| 12 | Implementar `CombatManager` | ✅ Completado |
| 13 | Mantener fórmula oficial de daño con aleatoriedad `[0.5, 1.5]` | ✅ Completado |
| 14 | Añadir sobrecarga determinista de `calcularDanio` para tests | ✅ Completado |
| 15 | Implementar ataque del jugador | ✅ Completado |
| 16 | Implementar ataque de enemigo | ✅ Completado |
| 17 | Implementar AOE del Destructor | ✅ Completado |
| 18 | Implementar comprobación de rango y línea de visión en combate | ✅ Completado |
| 19 | Implementar `ArbolDecisionIA` | ✅ Completado |
| 20 | Decidir radio Manhattan para Guardian y Destructor en decisiones IA | ✅ Completado |
| 21 | Implementar acciones `ATACAR`, `MOVER`, `MOVER_A_ZONA`, `APLICAR_EFECTO`, `INVOCAR`, `AOE` y `ESPERAR` | ✅ Completado |
| 22 | Mantener `ArbolDecisionIA` como clase de decisión sin modificar estado del juego | ✅ Completado |
| 23 | Implementar `IAEnemigo` | ✅ Completado |
| 24 | Usar `BFSMovimiento` para movimiento de enemigos dentro de sala | ✅ Completado |
| 25 | Implementar persecución hacia celda libre adyacente al jugador | ✅ Completado |
| 26 | Implementar reposicionamiento `MOVER_A_ZONA` para enemigos a distancia | ✅ Completado |
| 27 | Implementar huida del `SUMMONER` alejándose del jugador | ✅ Completado |
| 28 | Implementar cooldown real del `SNIPER` | ✅ Completado |
| 29 | Implementar aplicación de efectos del `CONTROLLER` | ✅ Completado |
| 30 | Implementar invocación de Berserker por `SUMMONER` | ✅ Completado |
| 31 | Implementar `TurnManager` | ✅ Completado |
| 32 | Mantener `TurnManager` separado de `GameModel` y de la capa UI | ✅ Completado |
| 33 | Implementar movimiento del jugador con `BFSMovimiento` | ✅ Completado |
| 34 | Implementar recogida automática de items de suelo al moverse | ✅ Completado |
| 35 | Implementar interacción `PICKUP` con contenedor adyacente | ✅ Completado |
| 36 | Implementar cambio de sala por acceso adyacente con destino configurado | ✅ Completado |
| 37 | Implementar `saltarMovimiento`, `saltarRecogida` y `saltarUsoItem` | ✅ Completado |
| 38 | Implementar turno enemigo con iteración sobre enemigos iniciales del turno | ✅ Completado |
| 39 | Ajustar `CombatManager` para fallo de ataque por `BLIND` | ✅ Completado |
| 40 | Consumir bonus temporal de ataque en ataques resueltos, incluido fallo por `BLIND` | ✅ Completado |
| 41 | Aplicar efectos primario y secundario de armas en combate | ✅ Completado |
| 42 | Implementar `ItemGenerator` | ✅ Completado |
| 43 | Implementar creación por id de armas, armaduras, pociones y accesorios oficiales | ✅ Completado |
| 44 | Implementar generación aleatoria por zona con items reales | ✅ Completado |
| 45 | Implementar drops de enemigos con items reales o `null` | ✅ Completado |
| 46 | Implementar `usarAccesoAdyacente()` para puertas y escaleras | ✅ Completado |
| 47 | Implementar validación de llegada antes de cambiar de sala por acceso | ✅ Completado |
| 48 | Implementar desbloqueo de `DOOR_LOCKED` por item narrativo requerido | ✅ Completado |
| 49 | Crear `PuzzleManager` para secuencias de `LEVER` y `RUNE` | ✅ Completado |
| 50 | Implementar `onRoomEnter()` con diálogos por personaje en `TurnManager` | ✅ Completado |
| 51 | Implementar log acumulativo de partida en `TurnManager` | ✅ Completado |
| 52 | Implementar activación de triggers secretos y runas al moverse | ✅ Completado |
| 53 | Implementar activación de palancas adyacentes desde `PICKUP` | ✅ Completado |
| 54 | Implementar daño de fallo de puzzle configurable por sala | ✅ Completado |
| 55 | Ajustar `PuzzleManager` para usar daño por zona 5, 6, 7 y 8 | ✅ Completado |
| 56 | Ampliar `ItemGenerator` con objetos narrativos AC1-AC4 y N1 | ✅ Completado |
| 57 | Añadir pool de Zona 5 con P3, P4 y P5 | ✅ Completado |
| 58 | Implementar `DungeonGenerator` con mapa fijo de 34 salas | ✅ Completado |
| 59 | Generar conexiones de sala según mapas de `docs/mapas/` | ✅ Completado |
| 60 | Modelar `PASILLO_FINAL` como sala real con aviso de no retorno | ✅ Completado |
| 61 | Modelar conexión `PASILLO_FINAL -> S5-D` como solo ida | ✅ Completado |
| 62 | Colocar cofres, items de pasillo, puzzles, secretos y accesos fijos | ✅ Completado |
| 63 | Colocar enemigos con posiciones aleatorias dentro de casillas seguras | ✅ Completado |
| 64 | Evitar duplicados de enemigos en una misma celda durante generación | ✅ Completado |
| 65 | Asignar drops garantizados AC1-AC4 a mini-bosses de Zona 1-4 | ✅ Completado |
| 66 | Crear `LogEventType` para clasificar eventos del log de partida | ✅ Completado |
| 67 | Crear `GameLogEntry` como evento estructurado comparable | ✅ Completado |
| 68 | Adaptar `TurnManager` a `ListaSimplementeEnlazada<GameLogEntry>` | ✅ Completado |
| 69 | Añadir `getLogTextos()` en `TurnManager` como utilidad visual | ✅ Completado |
| 70 | Crear `CombatResult` para devolver resultados completos de combate | ✅ Completado |
| 71 | Adaptar `CombatManager` para devolver `CombatResult` en ataques y AOE | ✅ Completado |
| 72 | Registrar eventos estructurados de movimiento, recogida, item, combate, acceso, sala y puzzle en `TurnManager` | ✅ Completado |
| 73 | Reemplazar `LogEventType.SYSTEM` por `LogEventType.GAME` | ✅ Completado |
| 74 | Crear `AIActionResult` para resultados estructurados de IA enemiga | ✅ Completado |
| 75 | Adaptar `IAEnemigo.executeTurn(...)` para devolver `AIActionResult` | ✅ Completado |
| 76 | Crear `EffectProcessingResult` para daño y expiración de efectos | ✅ Completado |
| 77 | Adaptar `Unit.procesarEfectos()` para devolver resultado estructurado | ✅ Completado |
| 78 | Registrar acciones concretas de enemigos en `TurnManager` | ✅ Completado |
| 79 | Registrar daño y expiración de efectos relevantes en `TurnManager` | ✅ Completado |
| 80 | Hacer que `ejecutarUsoItem(null)` lance `GameStateException` | ✅ Completado |
| 81 | Resolver muerte, retirada de sala y drop de enemigos por efectos al inicio de su turno | ✅ Completado |
| 82 | Crear `MiniBossAI` para habilidades especiales de mini-bosses | ✅ Completado |
| 83 | Integrar habilidades especiales de mini-bosses en `IAEnemigo` | ✅ Completado |
| 84 | Registrar habilidades especiales de mini-bosses en el log estructurado | ✅ Completado |
| 85 | Convertir el Golem a unidad 1x1 y eliminar `isOcupa2x2()` | ✅ Completado |
| 86 | Aumentar en +5 el daño base de todas las armas oficiales W1-W12 | ✅ Completado |
| 87 | Aumentar en +3 la defensa de armaduras y escudos oficiales A1-A8 | ✅ Completado |
| 88 | Conectar drops de enemigos normales generados en `DungeonGenerator` usando `ItemGenerator.crearDropEnemigo(...)` | ✅ Completado |
| 89 | Aplicar un segundo aumento de +5 al daño base de todas las armas oficiales W1-W12 | ✅ Completado |
| 90 | Convertir los drops de enemigos normales en garantizados al 100%, incluyendo `CONSTRUCTO`, `SOMBRA_ABSORBIDA` y `ECO_DE_MAGIA` | ✅ Completado |
| 91 | Bloquear el uso de puertas y escaleras mientras queden enemigos vivos en la sala actual | ✅ Completado |
| 92 | Calcular la mejor ruta global hacia `S5-D` combinando distancia de celdas y distancia de salas | ✅ Completado |
| 93 | Exponer desde `TurnManager` el camino de celdas de la sala actual para revelar la ruta al objetivo | ✅ Completado |
| 94 | Resolver empates de ruta global de forma determinista por coste, distancia e id de sala destino | ✅ Completado |
| 95 | Permitir revelar la ruta visual aunque los enemigos sigan bloqueando el uso real del acceso | ✅ Completado |
| 96 | Revelar la puerta oculta asociada cuando el jugador pisa un trigger secreto | ✅ Completado |
| 97 | Reiniciar fase y acciones del jugador a `MOVEMENT` al entrar en una nueva sala | ✅ Completado |
| 98 | Registrar la causa concreta de derrota por ataque enemigo, `BURN` o fallo de puzzle | ✅ Completado |
| 99 | Mostrar en el log el contenido concreto obtenido al abrir un cofre | ✅ Completado |
| 96 | Considerar puertas bloqueadas conocidas como ruta de progreso para que la guía no falle antes de resolver puzzles | ✅ Completado |
| 97 | Calcular el camino visual ignorando unidades para que los enemigos no oculten la guía de ruta | ✅ Completado |

---

## Capa 6 — Persistencia

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Implementar `GameState` como DTO plano serializable | ✅ Completado |
| 2 | Guardar sala actual, personaje, fase, turno, flags de acción y último diálogo | ✅ Completado |
| 3 | Guardar HP, posición, inventario normal, inventario narrativo, equipo, bonus temporal y efectos del jugador | ✅ Completado |
| 4 | Guardar salas exploradas, temporizadores, diálogos mostrados, puzzles y celdas dinámicas | ✅ Completado |
| 5 | Guardar contenedores con id, estado abierto e items restantes | ✅ Completado |
| 6 | Guardar enemigos vivos y muertos con sala, posición, HP, drop, cooldown, tipo y efectos | ✅ Completado |
| 7 | Guardar pasadizos ocultos activos y log acumulativo | ✅ Completado |
| 8 | Implementar `LoadedGame` como alternativa tipada a `Object[]` | ✅ Completado |
| 9 | Implementar `GameSummary` para resumen final exportable | ✅ Completado |
| 10 | Implementar `LectorJSON.guardarPartida(...)` en UTF-8 | ✅ Completado |
| 11 | Implementar `LectorJSON.cargarPartida(...)` reconstruyendo base con `DungeonGenerator` y aplicando estado dinámico | ✅ Completado |
| 12 | Implementar `extraerGameState(...)` y `reconstruirDesdeGameState(...)` | ✅ Completado |
| 13 | Implementar exportación de resumen final con `exportarResumen(...)` | ✅ Completado |
| 14 | Añadir setters controlados en `TurnManager` para restaurar fase, turno y último diálogo | ✅ Completado |
| 15 | Añadir restauración de apertura en `Container` para carga sin entregar loot duplicado | ✅ Completado |
| 16 | Crear `config/configuracion_inicial_valdris.json` como configuración inicial del mundo completo | ✅ Completado |
| 17 | Implementar `GameConfig` como DTO plano para la configuración inicial JSON | ✅ Completado |
| 18 | Implementar `DungeonConfigLoader` para construir el dungeon desde JSON sin depender de `DungeonGenerator` | ✅ Completado |
| 19 | Añadir `idSalaInicial` e `idSalaObjetivo` a `Dungeon` | ✅ Completado |
| 20 | Conectar `GameModel` y `LectorJSON` para usar configuración inicial JSON al crear o reconstruir partidas | ✅ Completado |
| 21 | Mantener `TurnManager` compatible con objetivo configurable y fallback a `S5-D` | ✅ Completado |
| 22 | Ampliar la configuración JSON con aleatoriedad controlada para enemigos normales, drops, puzzles e items de suelo | ✅ Completado |
| 23 | Declarar paredes interiores en los layouts JSON de salas principales manteniendo accesos y objetivos alcanzables | ✅ Completado |
| 24 | Añadir candidatos de aparición por sala para que los enemigos normales se coloquen en casillas válidas del JSON | ✅ Completado |

---

## Capa 7 — JavaFX

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Fijar pantallas principales y dividir JavaFX en subbloques revisables | ✅ Completado |
| 2 | Implementar `MainApp` como punto de entrada JavaFX | ✅ Completado |
| 3 | Implementar `MainMenuView` con nueva partida, carga pendiente y salida | ✅ Completado |
| 4 | Implementar `CharacterSelectView` con Kael, Syra y Dorath | ✅ Completado |
| 5 | Mostrar mensajes temporales para carga y creación de partida hasta conectar `GameModel` | ✅ Completado |
| 6 | Implementar `GameModelListener` | ✅ Completado |
| 7 | Implementar `GameModel` | ✅ Completado |
| 8 | Implementar `GameView` principal en modo solo lectura | ✅ Completado |
| 9 | Implementar `GameController` mínimo para navegación | ✅ Completado |
| 10 | Implementar `CombatLogView` con últimos 5 mensajes | ✅ Completado |
| 11 | Conectar movimiento, ataque y acciones básicas de turno desde JavaFX | ✅ Completado |
| 12 | Añadir resaltado BFS de celdas alcanzables en fase `MOVEMENT` | ✅ Completado |
| 13 | Añadir acción JavaFX para iniciar combate final con Malachar | ✅ Completado |
| 14 | Implementar `InventoryView` en modo lectura y uso desde `USE_ITEM` | ✅ Completado |
| 15 | Implementar autoguardado en checkpoints y carga básica desde JavaFX | ✅ Completado |
| 16 | Implementar diálogos y pantalla final | ✅ Completado |
| 17 | Implementar exportación manual de `resumen_valdris.json` desde la pantalla final | ✅ Completado |
| 18 | Corregir validación de movimiento para comparar celdas por referencia real | ✅ Completado |
| 19 | Mejorar feedback de ataque en JavaFX con resaltado de enemigos y mensajes de fase | ✅ Completado |
| 20 | Mostrar HP actual/máximo de enemigos directamente en las celdas del mapa | ✅ Completado |
| 21 | Mostrar resumen de equipo e inventario del jugador en el panel lateral de partida | ✅ Completado |
| 22 | Hacer el log de acciones más visible con título, más líneas y altura fija | ✅ Completado |
| 23 | Maximizar y permitir redimensionar la ventana principal de JavaFX | ✅ Completado |
| 24 | Redistribuir `GameView` en panel izquierdo de estado/acciones, mapa central y panel derecho de inventario | ✅ Completado |
| 25 | Sustituir letras de jugador y enemigos por mini-sprites visuales en el mapa | ✅ Completado |
| 26 | Mostrar efectos activos del jugador en el panel de estado para entender acciones de enemigos como `CONTROLLER` | ✅ Completado |
| 27 | Mostrar palancas y runas activadas en verde durante la secuencia y tras resolver el puzzle | ✅ Completado |
| 28 | Registrar mensajes claros de combinación correcta o incorrecta al cerrar una secuencia de puzzle | ✅ Completado |
| 29 | Ajustar el margen inferior del log para que los últimos mensajes queden ligeramente más arriba | ✅ Completado |
| 30 | Recortar las líneas visibles del log y reservar una franja inferior vacía equivalente a dos líneas | ✅ Completado |
| 31 | Mostrar pistas progresivas de puzzle tras cada fallo con daño y revelar la combinación completa al completar las pistas | ✅ Completado |
| 32 | Persistir el contador de fallos acumulados de puzzle en guardado/carga | ✅ Completado |
| 33 | Actualizar los stats visibles de selección de personaje tras el ajuste de balance | ✅ Completado |
| 34 | Agrupar visualmente items repetidos por ID en el panel lateral y en el modal de inventario | ✅ Completado |
| 35 | Añadir atajos de teclado visibles en los botones de acción de la pantalla de partida | ✅ Completado |
| 36 | Revertir el +3 de defensa de armaduras y escudos oficiales A1-A8 manteniendo defensa base 3 en personajes | ✅ Completado |
| 37 | Mostrar en la pantalla de partida la distancia a la salida abierta más cercana o el bloqueo por enemigos/salidas cerradas | ✅ Completado |
| 38 | Mostrar nombre de sala, turno global y turnos de sala en la pantalla principal | ✅ Completado |
| 39 | Añadir acción y atajo `V` para revelar u ocultar la ruta global hacia el Núcleo con resaltado propio | ✅ Completado |
| 40 | Reubicar la acción de ruta junto al dato de salida y compactar el log para que no tape mensajes en partida | ✅ Completado |
| 41 | Diferenciar visualmente los suelos con trigger secreto y las puertas secretas reveladas | ✅ Completado |

---

## Tests implementados

| Nº | Test | Estado |
|---:|------|--------|
| 1 | `EffectTest` | ✅ Completado |
| 2 | `TypeEnumsTest` | ✅ Completado |
| 3 | `ItemTest` | ✅ Completado |
| 4 | `WeaponTest` | ✅ Completado |
| 5 | `ArmorTest` | ✅ Completado |
| 6 | `PotionTest` | ✅ Completado |
| 7 | `AccessoryTest` | ✅ Completado |
| 8 | `UnitTest` | ✅ Completado |
| 9 | `PlayerTest` | ✅ Completado |
| 10 | `EnemyTest` | ✅ Completado |
| 11 | `CellTest` | ✅ Completado |
| 12 | `RoomTest` | ✅ Completado |
| 13 | `DungeonTest` | ✅ Completado |
| 14 | `BFSMovimientoTest` | ✅ Completado |
| 15 | `BFSCaminoMinimoTest` | ✅ Completado |
| 16 | `LineaDeVisionTest` | ✅ Completado |
| 17 | `CombatManagerTest` | ✅ Completado |
| 18 | `ArbolDecisionIATest` | ✅ Completado |
| 19 | `IAEnemigoTest` | ✅ Completado |
| 20 | `ContainerTest` | ✅ Completado |
| 21 | `ChestTest` | ✅ Completado |
| 22 | `TurnManagerTest` | ✅ Completado |
| 23 | `ItemGeneratorTest` | ✅ Completado |
| 24 | `PuzzleManagerTest` | ✅ Completado |
| 25 | `MiniBossEnemyTest` | ✅ Completado |
| 26 | `DungeonGeneratorTest` | ✅ Completado |
| 27 | `GameStateTest` | ✅ Completado |
| 28 | `LectorJSONTest` | ✅ Completado |
| 29 | `GameLogEntryTest` | ✅ Completado |
| 30 | `DungeonConfigLoaderTest` | ✅ Completado |
| 31 | `MiniBossAITest` | ✅ Completado |
| 32 | `MalacharAllyTest` | ✅ Completado |
| 33 | `ParasitoEnemyTest` | ✅ Completado |
| 34 | `TurnManagerFinalBossTest` | ✅ Completado |
| 35 | `LectorJSONFinalBossTest` | ✅ Completado |
| 36 | `MisEstructurasDeDatos.Arbolesbinarios.ArbolBinarioDeBusquedaEnterosTest` | ✅ Completado |
| 37 | `MisEstructurasDeDatos.Arbolesbinarios.ArbolBinarioDeBusquedaEquilibradoTest` | ✅ Completado |
| 38 | `MisEstructurasDeDatos.Arbolesbinarios.ArbolBinarioDeBusquedaTest` | ✅ Completado |
| 39 | `MisEstructurasDeDatos.Arbolesbinarios.NodoTest` | ✅ Completado |
| 40 | `MisEstructurasDeDatos.Arbolesbinarios.MainTest` | ✅ Completado |
| 41 | `MisEstructurasDeDatos.Grafos.AristaTest` | ✅ Completado |
| 42 | `MisEstructurasDeDatos.Grafos.DatosGrafoJsonTest` | ✅ Completado |
| 43 | `MisEstructurasDeDatos.Grafos.GrafoTest` | ✅ Completado |
| 44 | `MisEstructurasDeDatos.Grafos.LectorGrafoJsonTest` | ✅ Completado |
| 45 | `MisEstructurasDeDatos.Grafos.NodoTest` | ✅ Completado |
| 46 | `MisEstructurasDeDatos.Grafos.TripletaJsonTest` | ✅ Completado |
| 47 | `MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazadaTest` | ✅ Completado |
| 48 | `MisEstructurasDeDatos.ListasPilasYColas.ListaCircularTest` | ✅ Completado |
| 49 | `MisEstructurasDeDatos.ListasPilasYColas.ColaTest` | ✅ Completado |
| 50 | `MisEstructurasDeDatos.ListasPilasYColas.PilaTest` | ✅ Completado |

---

## Verificaciones realizadas

| Nº | Verificación | Resultado |
|---:|--------------|-----------|
| 1 | Compilación tras saneamiento inicial | ✅ Correcta |
| 2 | Compilación tras `Player` y `Enemy` | ✅ Correcta |
| 3 | Compilación tras `Cell` y `Room` | ✅ Correcta |
| 4 | Suite tras tests de capas 2 y 3 | ✅ Correcta |
| 5 | Suite tras `CellTest` y `RoomTest` | ✅ Correcta |
| 6 | Suite tras `Dungeon` y `DungeonTest` | ✅ Correcta |
| 7 | Suite tras `BFSMovimiento` | ✅ Correcta |
| 8 | Suite tras `BFSCaminoMinimo` | ✅ Correcta |
| 9 | Suite tras `LineaDeVision` | ✅ Correcta |
| 10 | Suite tras `CombatManager` | ✅ Correcta |
| 11 | Suite tras `ArbolDecisionIA` | ✅ Correcta |
| 12 | Suite tras `IAEnemigo` | ✅ Correcta |
| 13 | Suite tras `Container`, `Chest`, `Cell` ampliado y `TurnManager` | ✅ Correcta |
| 14 | Suite tras rediseño de `BLIND`, P4, P5, armas con doble efecto e `ItemGenerator` | ✅ Correcta |
| 15 | Suite tras `STAIRS_UP/DOWN`, accesos adyacentes y visión actualizada | ✅ Correcta |
| 16 | Suite tras diálogos, puzzles, pasadizos ocultos y log acumulativo | ✅ Correcta |
| 17 | Suite tras enemigos nuevos, mini-bosses, items narrativos y `DungeonGenerator` | ✅ Correcta |
| 18 | Suite tras persistencia (`GameState`, `LoadedGame`, `GameSummary`, `LectorJSON`) | ✅ Correcta |
| 19 | Suite tras sub-bloque 1 de log estructurado y `CombatResult` | ✅ Correcta |
| 20 | Suite tras sub-bloque 2 de log estructurado, IA, efectos y persistencia final | ✅ Correcta |
| 21 | Suite tras habilidades especiales de mini-bosses | ✅ Correcta |
| 22 | Suite tras JavaFX subbloque 1: arranque, menú inicial y selección de personaje | ✅ Correcta |
| 23 | Suite tras JavaFX subbloque 2: modelo observable y partida nueva real | ✅ Correcta |
| 24 | Suite tras JavaFX subbloque 3: pantalla principal solo lectura | ✅ Correcta |
| 25 | Suite tras JavaFX subbloque 4: control de turno básico | ✅ Correcta |
| 26 | Suite tras JavaFX subbloque 5: inventario modal | ✅ Correcta |
| 27 | Suite tras JavaFX subbloque 6: autoguardado en checkpoints y carga básica | ✅ Correcta |
| 28 | Suite tras JavaFX subbloque 7: diálogos y pantalla final | ✅ Correcta |
| 29 | Suite tras corrección JavaFX de movimiento y feedback de ataque | ✅ Correcta |
| 30 | Suite tras mejoras de visibilidad de enemigos, inventario y log | ✅ Correcta |
| 31 | Suite tras ventana maximizada y redistribución de `GameView` | ✅ Correcta |
| 32 | Suite tras mini-sprites visuales de jugador y enemigos | ✅ Correcta |
| 33 | Suite tras feedback visual de puzzles, efectos activos y ajuste de log | ✅ Correcta |
| 34 | Suite tras pistas progresivas de puzzle y recorte inferior del log | ✅ Correcta |
| 35 | Suite tras palancas no transitables, +5 de daño a personajes/armas y Syra con movimiento 4 | ✅ Correcta |
| 36 | Suite tras defensa base de jugadores, armaduras/escudos reforzados y drops normales conectados | ✅ Correcta |
| 37 | Suite tras segundo ajuste de daño, drops garantizados y accesos bloqueados por enemigos vivos | ✅ Correcta |
| 38 | Suite tras agrupación visual de inventario y atajos de teclado de acciones JavaFX | ✅ Correcta |
| 39 | Suite tras revertir defensa extra de A1-A8 y mantener defensa base de personajes | ✅ Correcta |
| 40 | Suite tras tests de conectividad estructural y distancia a salida abierta más cercana | ✅ Correcta |
| 41 | Suite tras lógica final con `MalacharAlly`, `ParasitoEnemy`, desenlace y persistencia del boss final | ✅ Correcta |
| 42 | Suite tras límites de turno global/sala y contadores visibles en JavaFX | ✅ Correcta |
| 43 | Suite tras saneamiento documental de `TASKS.md` y `COMMIT_LOG.md` | ✅ Correcta |
| 44 | Suite tras reubicar, corregir y completar tests de `MisEstructurasDeDatos` | ✅ Correcta |
| 45 | Suite tras ruta global hacia `S5-D`, acción JavaFX de revelar camino con enemigos vivos, puertas bloqueadas de progreso, unidades en ruta y ajuste visual de panel/log | ✅ Correcta |
| 46 | Validación JSON de configuración inicial: parseo correcto, 34 salas, 35 conexiones, 61 enemigos y layouts con dimensiones coherentes | ✅ Correcta |
| 47 | Suite tras cargar la configuración inicial desde JSON en `GameModel` y reconstrucción de `LectorJSON` | ✅ Correcta |
| 48 | Suite tras configuración JSON híbrida con paredes interiores, candidatos de enemigo, pools de items y puzzles permutados | ✅ Correcta |
| 49 | Suite tras pistas visuales de secretos, reinicio de fase al cambiar de sala y causas concretas de derrota | ✅ Correcta |

Última verificación completa:

```powershell
.\mvnw.cmd -q test
```

Resultado:

```text
493 tests, 0 failures, 0 errors, 0 skipped
```

---

## Decisiones técnicas aceptadas

| Nº | Decisión | Estado |
|---:|----------|--------|
| 1 | Las clases del modelo implementan `Comparable` cuando necesitan entrar en LSE/Cola propias | ✅ Aceptada |
| 2 | No modificar `ListaSimplementeEnlazada` para eliminar la restricción `Comparable<T>` | ✅ Aceptada |
| 3 | Mejorar la API pública mínima de `Grafo`, `NodoGrafo` y `Arista` con permiso explícito | ✅ Aceptada |
| 4 | `BFSCaminoMinimo` se mantiene aunque no tenga ficha propia detallada en la guía | ✅ Aceptada |
| 5 | `LineaDeVision` bloquea `WALL` y `STAIRS_UP` como obstáculos intermedios | ✅ Aceptada |
| 6 | `STAIRS_DOWN`, `TRAP`, `LEVER`, `RUNE` y `DOOR` no bloquean visión | ✅ Aceptada |
| 7 | `CombatManager` conserva la aleatoriedad oficial y añade sobrecarga determinista para tests | ✅ Aceptada |
| 8 | Cualquier cambio futuro en `MisEstructurasDeDatos` debe consultarse antes | ✅ Aceptada |
| 9 | `ArbolDecisionIA` usa distancia Manhattan para Guardian y Destructor | ✅ Aceptada |
| 10 | Archer y Sniper usan una zona de confort simple: atacan si pueden y se reposicionan si no | ✅ Aceptada |
| 11 | `IAEnemigo` usa `BFSMovimiento`, no `BFSCaminoMinimo`, para moverse dentro de sala | ✅ Aceptada |
| 12 | `SNIPER` ataca solo si `turnosSinActuar >= 2`; si no, consume turno e incrementa cooldown | ✅ Aceptada |
| 13 | `MOVER`, `MOVER_A_ZONA` y huida de `SUMMONER` tienen comportamientos diferenciados | ✅ Aceptada |
| 14 | `TurnManager` no importa ni referencia `GameModel`; la UI refrescará desde fuera | ✅ Aceptada |
| 15 | `Container` y `Chest` se añaden en `Valdris.model.map` con autorización puntual | ✅ Aceptada |
| 16 | Los items de suelo se recogen automáticamente en movimiento; `PICKUP` queda para contenedores adyacentes | ✅ Aceptada |
| 17 | `Cell` guarda destinos concretos para accesos entre salas | ✅ Aceptada |
| 18 | Los enemigos invocados durante `ENEMY_TURN` actúan a partir del siguiente turno enemigo | ✅ Aceptada |
| 19 | `BLIND` deja de reducir movimiento y pasa a provocar un 25% de fallo de ataque | ✅ Aceptada |
| 20 | Un ataque fallado por `BLIND` consume la acción de ataque y el bonus temporal de P5 | ✅ Aceptada |
| 21 | Un ataque fallado por `BLIND` no aplica daño ni efectos de arma | ✅ Aceptada |
| 22 | `Weapon` puede tener dos efectos especiales para representar W11 con `SLOW` y `BLIND` | ✅ Aceptada |
| 23 | `P4` limpia `CURSE` y `BLIND` llamando a `removeEfecto` para cada efecto | ✅ Aceptada |
| 24 | `P5` cura y añade bonus temporal al siguiente ataque del jugador | ✅ Aceptada |
| 25 | `ItemGenerator` devuelve items reales o `null`, no materiales abstractos | ✅ Aceptada |
| 26 | La asignación garantizada de mini-bosses y accesorios AC1-AC4 se aplaza para `DungeonGenerator` | ✅ Aceptada |
| 27 | `STAIRS_UP` y `STAIRS_DOWN` sustituyen al tipo genérico `STAIRS` | ✅ Aceptada |
| 28 | Puertas y escaleras se usan con `usarAccesoAdyacente()` desde `PICKUP` | ✅ Aceptada |
| 29 | Las escaleras son no transitables y solo se usan desde su frente configurado | ✅ Aceptada |
| 30 | Las puertas no necesitan orientación porque se colocan en paredes | ✅ Aceptada |
| 31 | `DOOR_LOCKED` puede requerir un item narrativo por id para desbloquearse | ✅ Aceptada |
| 32 | El log de partida debe ser acumulativo y no debe tener `clearLog()` destructivo | ✅ Aceptada |
| 33 | Los diálogos de sala se guardan con campos concretos por personaje, no con `Map` | ✅ Aceptada |
| 34 | `LEVER` y `RUNE` comparten un sistema común de secuencia | ✅ Aceptada |
| 35 | `PuzzleManager` sustituye conceptualmente a `LeverManager` porque también gestiona runas | ✅ Aceptada |
| 36 | Los pasadizos ocultos se guardan fuera del grafo hasta activarse | ✅ Aceptada |
| 37 | `HiddenPassage` se usa como clase comparable para pasadizos secretos | ✅ Aceptada |
| 38 | `changeRoom(...)` llama automáticamente a `onRoomEnter()` | ✅ Aceptada |
| 39 | El mapa usa 34 salas porque `PASILLO_FINAL` es una sala real de advertencia | ✅ Aceptada |
| 40 | `PASILLO_FINAL -> S5-D` es de solo ida y S5-D no conecta de vuelta | ✅ Aceptada |
| 41 | Los puzzles tienen celdas fijas, orden aleatorio por partida y no se repiten tras resolverse | ✅ Aceptada |
| 42 | El daño por fallo de puzzle es 5, 6, 7 y 8 para zonas 1, 2, 3 y 4 | ✅ Aceptada |
| 43 | Los enemigos tienen cantidad y tipo fijos por sala, pero posición aleatoria segura | ✅ Aceptada |
| 44 | Los objetos narrativos tienen sección separada del inventario de combate | ✅ Aceptada |
| 45 | `CONSTRUCTO`, `SOMBRA_ABSORBIDA` y `ECO_DE_MAGIA` son enemigos normales nuevos | ✅ Aceptada |
| 46 | Los mini-bosses usan `MiniBossEnemy`, dejando habilidades especiales para más adelante | ✅ Aceptada |
| 47 | `EL_FILTRO` pertenece a `MiniBossEnemy` e ignora 5 defensa del jugador | ✅ Aceptada |
| 48 | `ECO_DE_MAGIA` ignora 3 defensa del jugador | ✅ Aceptada |
| 49 | Los items aleatorios de Zona 5 son P3, P4 o P5 | ✅ Aceptada |
| 50 | La persistencia usa DTOs planos y referencias por id, no referencias directas entre objetos vivos | ✅ Aceptada |
| 51 | La carga reconstruye el mundo base con `DungeonGenerator` y aplica después el estado dinámico guardado | ✅ Aceptada |
| 52 | `LoadedGame` sustituye a `Object[]` para devolver dungeon, jugador y turn manager de forma tipada | ✅ Aceptada |
| 53 | `GameSummary` se crea ahora con datos fiables: personaje, sala, HP, turno, inventarios, salas exploradas y log | ✅ Aceptada |
| 54 | Los enemigos muertos se guardan con `vivo=false` y no se reinsertan al reconstruir la partida | ✅ Aceptada |
| 55 | Los JSON de partida y resumen se escriben y leen explícitamente en UTF-8 | ✅ Aceptada |
| 56 | El log interno pasa a eventos estructurados con `GameLogEntry` antes de JavaFX | ✅ Aceptada |
| 57 | `LogEventType` se usa para evitar categorías de log escritas como texto libre | ✅ Aceptada |
| 58 | `CombatResult` se usa para registrar daño, fallo por BLIND, muerte, drops y efectos sin deducirlo desde HP | ✅ Aceptada |
| 59 | La persistencia usa solo `GameLogEntryDTO[] logEventos` como diseño único del log | ✅ Aceptada |
| 60 | `getLogTextos()` se mantiene solo como utilidad visual para UI y resumen textual | ✅ Aceptada |
| 61 | `LogEventType.GAME` sustituye a `SYSTEM` para eventos globales de partida, no técnicos | ✅ Aceptada |
| 62 | `ejecutarUsoItem(null)` lanza `GameStateException`; saltar item se hace con `saltarUsoItem()` | ✅ Aceptada |
| 63 | `AIActionResult` y `EffectProcessingResult` alimentan el log estructurado de turnos enemigos y estados | ✅ Aceptada |
| 64 | Si un enemigo muere por efectos al empezar su turno, deja drop, sale de la sala y no bloquea el turno del resto | ✅ Aceptada |
| 65 | El Golem deja de ocupar 2x2 y funciona como unidad 1x1 con Pisotón Sísmico de radio 2 | ✅ Aceptada |
| 66 | Las habilidades especiales de mini-bosses usan cooldown, respetan BLIND y se registran en el log | ✅ Aceptada |
| 67 | Si un especial cargado no alcanza al jugador tras intentar moverse, se pierde el intento y se reinicia cooldown | ✅ Aceptada |
| 68 | JavaFX usa un único slot `partida_valdris.json` y autoguarda solo al entrar en checkpoints acordados | ✅ Aceptada |
| 69 | Los diálogos narrativos de sala se muestran automáticamente en modal y se consumen una sola vez desde `GameModel` | ✅ Aceptada |
| 70 | La pantalla final permite exportar manualmente `resumen_valdris.json`; no se exporta automáticamente | ✅ Aceptada |
| 71 | La validación de rango de movimiento debe comparar instancias de `Cell` por referencia, no mediante `compareTo` | ✅ Aceptada |
| 72 | En fase `ATTACK`, JavaFX resalta en rojo enemigos atacables y muestra ayuda contextual antes de seleccionar objetivo | ✅ Aceptada |
| 73 | La pantalla principal debe mostrar información jugable esencial sin depender de modales: HP enemigo, equipo, inventario resumido y log visible | ✅ Aceptada |
| 74 | La ventana JavaFX debe abrir maximizada y `GameView` debe repartir información en paneles laterales para aprovechar todo el ancho disponible | ✅ Aceptada |
| 75 | Jugador y enemigos deben representarse en el mapa con mini-sprites visuales, no solo letras | ✅ Aceptada |
| 76 | Los efectos activos del jugador y el estado de palancas/runas deben ser visibles en la pantalla principal para evitar que acciones de IA o puzzles parezcan no responder | ✅ Aceptada |
| 77 | Los puzzles deben mostrar pistas progresivas tras cada fallo con daño y revelar la combinación completa cuando ya se hayan mostrado todas las piezas | ✅ Aceptada |
| 78 | Las palancas `LEVER` no son transitables porque se activan desde una celda adyacente; las runas `RUNE` siguen siendo transitables porque se activan al pisarlas | ✅ Aceptada |
| 79 | El balance provisional aumenta +5 el ataque base de personajes y armas oficiales, y deja a Syra con movimiento base 4 | ✅ Aceptada |
| 80 | Los jugadores tienen defensa base 3 y los objetos defensivos A1-A8 reciben +3 defensa; `AC8` conserva su bonus de defensa actual | ✅ Aceptada |
| 81 | Los enemigos normales reciben drop probabilístico al generarse; los mini-bosses conservan sus drops narrativos fijos | ✅ Aceptada |
| 82 | Los enemigos normales pasan a tener drop garantizado al 100%; `PARASITO` queda fuera porque es la entidad final y no se genera como enemigo normal de sala | ✅ Aceptada |
| 83 | Las puertas y escaleras usadas desde `PICKUP` exigen limpiar primero todos los enemigos vivos de la sala actual, además de llave, puzzle, orientación y llegada válida | ✅ Aceptada |
| 84 | La agrupación de inventario es visual por `item.getId()`; la lista interna conserva unidades individuales para no cambiar persistencia ni reglas de uso | ✅ Aceptada |
| 85 | La defensa base de personajes se mantiene en 3, pero A1-A8 vuelven a sus defensas originales para no sobrerreducir el daño recibido | ✅ Aceptada |
| 86 | La distancia visible a salida se mide hasta la celda desde la que puede usarse la puerta/escalera abierta; si el jugador ya está al lado, marca 0 casillas | ✅ Aceptada |
| 87 | La partida tiene 500 turnos globales; las salas normales tienen 20/25, mini-bosses 35, S5-D 50, y puzzles/pasillos no tienen límite de sala | ✅ Aceptada |
| 88 | `partida_valdris.json` y `resumen_valdris.json` se versionan como ficheros de entrega para facilitar la revisión de guardado, carga y resumen final | ✅ Aceptada |
| 89 | Si dos accesos tienen el mismo coste global hacia `S5-D`, se elige de forma estable por distancia de salas, distancia de celdas, id de destino y coordenadas | ✅ Aceptada |
| 90 | Revelar ruta es una ayuda visual y puede mostrarse aunque queden enemigos vivos; usar la puerta o escalera sigue bloqueado hasta limpiar la sala | ✅ Aceptada |
| 91 | La ruta revelada considera puertas bloqueadas conocidas como continuidad de progreso, sin desbloquear ni permitir cruzar esas puertas hasta resolver sus condiciones | ✅ Aceptada |
| 92 | El camino visual de la ruta ignora unidades ocupantes porque es una guía de progreso posterior al combate, no una ruta de movimiento ejecutable en el turno actual | ✅ Aceptada |
| 93 | La configuración inicial oficial de entrega se define en JSON con estructura fija y aleatoriedad controlada: layouts, conexiones, accesos, cofres, diálogos, posición inicial y objetivo quedan declarados; enemigos normales, drops, items de suelo y secuencias de puzzle se eligen desde pools/candidatos del propio JSON | ✅ Aceptada |
| 94 | La partida nueva y la reconstrucción de guardados cargan el mundo base desde JSON; `DungeonGenerator` queda como utilidad histórica/testeable | ✅ Aceptada |
| 95 | Los mini-bosses y el combate final mantienen posiciones fijas en JSON porque son hitos narrativos; la variación se limita a enemigos normales y recompensas no narrativas | ✅ Aceptada |
| 96 | Los triggers secretos deben tener una pista visual sutil y las puertas secretas reveladas deben distinguirse claramente de paredes y puertas normales | ✅ Aceptada |
| 97 | Cambiar de sala reinicia siempre el ciclo de turno en `MOVEMENT` y limpia las acciones usadas del jugador | ✅ Aceptada |
| 98 | La derrota debe guardar la causa concreta disponible en el momento de la muerte, no un motivo genérico posterior | ✅ Aceptada |

---

## Pendiente a partir de aquí

| Nº | Tarea pendiente | Estado |
|---:|-----------------|--------|
| 1 | Definir `MalacharAlly` como aliado NPC del combate final | ✅ Completado |
| 2 | Definir `ParasitoEnemy`, sus fases, derrota y desenlace final | ✅ Completado |
| 3 | Implementar sistema de game over y desenlace final | ✅ Completado |
| 4 | Implementar capa JavaFX | ✅ Completado |
