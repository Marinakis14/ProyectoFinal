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
| Primera parte de capa 5 | BFS, visión, combate, árbol de decisión IA, IA enemiga, TurnManager e ItemGenerator completados |
| Tests JUnit actuales | 316 tests pasando |
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

---

## Capa 3 — Unidades

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Implementar `Unit` como clase abstracta base | ✅ Completado |
| 2 | Implementar gestión común de HP, daño, curación y vida | ✅ Completado |
| 3 | Implementar lista de efectos activos en `Unit` | ✅ Completado |
| 4 | Implementar reemplazo de efectos repetidos | ✅ Completado |
| 5 | Implementar procesamiento de efectos `CURSE` y `BURN` | ✅ Completado |
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

Última verificación completa:

```powershell
.\mvnw.cmd -q test
```

Resultado:

```text
316 tests, 0 failures, 0 errors, 0 skipped
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

---

## Pendiente a partir de aquí

| Nº | Tarea pendiente | Estado |
|---:|-----------------|--------|
| 1 | Fijar mini-bosses y asignación garantizada de accesorios AC1-AC4 antes de `DungeonGenerator` | ⬜ Pendiente |
| 2 | Añadir soporte de diálogos por personaje en `Room` y `TurnManager` | ⬜ Pendiente |
| 3 | Añadir soporte de palancas, runas y pasadizos secretos | ⬜ Pendiente |
| 4 | Implementar `DungeonGenerator` | ⬜ Pendiente |
| 5 | Crear `DungeonGeneratorTest` | ⬜ Pendiente |
| 6 | Implementar persistencia (`GameState`, `LectorJSON`) | ⬜ Pendiente |
| 7 | Implementar capa JavaFX | ⬜ Pendiente |
