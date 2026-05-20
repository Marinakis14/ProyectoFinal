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
| Primera parte de capa 5 | BFS, visión y combate completados |
| Tests JUnit actuales | 202 tests pasando |
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

---

## Capa 3 — Unidades

| Nº | Tarea | Estado |
|---:|-------|--------|
| 1 | Implementar `Unit` como clase abstracta base | ✅ Completado |
| 2 | Implementar gestión común de HP, daño, curación y vida | ✅ Completado |
| 3 | Implementar lista de efectos activos en `Unit` | ✅ Completado |
| 4 | Implementar reemplazo de efectos repetidos | ✅ Completado |
| 5 | Implementar procesamiento de efectos `CURSE` y `BURN` | ✅ Completado |
| 6 | Implementar movimiento efectivo reducido por `SLOW` y `BLIND` | ✅ Completado |
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
| 11 | Decidir que solo `WALL` bloquea línea de visión por ahora | ✅ Completado |
| 12 | Implementar `CombatManager` | ✅ Completado |
| 13 | Mantener fórmula oficial de daño con aleatoriedad `[0.5, 1.5]` | ✅ Completado |
| 14 | Añadir sobrecarga determinista de `calcularDanio` para tests | ✅ Completado |
| 15 | Implementar ataque del jugador | ✅ Completado |
| 16 | Implementar ataque de enemigo | ✅ Completado |
| 17 | Implementar AOE del Destructor | ✅ Completado |
| 18 | Implementar comprobación de rango y línea de visión en combate | ✅ Completado |

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

Última verificación completa:

```powershell
.\mvnw.cmd -q test
```

Resultado:

```text
202 tests, 0 failures, 0 errors, 0 skipped
```

---

## Decisiones técnicas aceptadas

| Nº | Decisión | Estado |
|---:|----------|--------|
| 1 | Las clases del modelo implementan `Comparable` cuando necesitan entrar en LSE/Cola propias | ✅ Aceptada |
| 2 | No modificar `ListaSimplementeEnlazada` para eliminar la restricción `Comparable<T>` | ✅ Aceptada |
| 3 | Mejorar la API pública mínima de `Grafo`, `NodoGrafo` y `Arista` con permiso explícito | ✅ Aceptada |
| 4 | `BFSCaminoMinimo` se mantiene aunque no tenga ficha propia detallada en la guía | ✅ Aceptada |
| 5 | `LineaDeVision` bloquea solo `WALL` por ahora | ✅ Aceptada |
| 6 | `STAIRS`, `TRAP`, `LEVER` y `RUNE` no bloquean visión | ✅ Aceptada |
| 7 | `CombatManager` conserva la aleatoriedad oficial y añade sobrecarga determinista para tests | ✅ Aceptada |
| 8 | Cualquier cambio futuro en `MisEstructurasDeDatos` debe consultarse antes | ✅ Aceptada |

---

## Pendiente a partir de aquí

| Nº | Tarea pendiente | Estado |
|---:|-----------------|--------|
| 1 | Implementar `ArbolDecisionIA` | ⬜ Pendiente |
| 2 | Crear `ArbolDecisionIATest` | ⬜ Pendiente |
| 3 | Implementar `IAEnemigo` | ⬜ Pendiente |
| 4 | Crear `IAEnemigoTest` | ⬜ Pendiente |
| 5 | Implementar `TurnManager` | ⬜ Pendiente |
| 6 | Crear `TurnManagerTest` | ⬜ Pendiente |
| 7 | Implementar `ItemGenerator` | ⬜ Pendiente |
| 8 | Crear `ItemGeneratorTest` | ⬜ Pendiente |
| 9 | Implementar `DungeonGenerator` | ⬜ Pendiente |
| 10 | Crear `DungeonGeneratorTest` | ⬜ Pendiente |
| 11 | Implementar persistencia (`GameState`, `LectorJSON`) | ⬜ Pendiente |
| 12 | Implementar capa JavaFX | ⬜ Pendiente |

