# COMMIT_LOG.md — Diario de uso de IA
## Valdris: El Núcleo Profundo | Grupo H12GEXTRA

Este fichero es el diario oficial de uso de agentes de IA en el proyecto.
Cubre el criterio de evaluación: *"Diario de utilización del agente de IA: configuración, operaciones, resultados, crítica, reajustes y metodología final"*.

`TASKS.md` contiene el checklist detallado de tareas completadas. Este documento resume las sesiones de trabajo con Codex, los problemas encontrados y las decisiones técnicas tomadas.

---

## Configuración inicial del agente

- **Agente principal**: Codex (OpenAI) vía entorno de desarrollo local.
- **Documentos de contexto dados a Codex**: `AGENTS.md`, `PROJECT_SPEC.md`, `guia_codex.pdf`.
- **Documentos de diseño usados como contexto adicional**: `guia_diseno_v3.pdf`, `guia_diseno_v4_final.pdf`, `guia_diseno_v5_final.pdf`.
- **Restricción principal**: no usar `java.util.*` para estructuras de datos.
- **Estructuras propias obligatorias**: `ListaSimplementeEnlazada`, `Cola`, `Pila`, `Grafo`.
- **Flujo acordado**: leer ficha, avisar antes de cambios relevantes, implementar, compilar, testear y registrar cambios.
- **Regla añadida durante el desarrollo**: si la mejor solución requiere modificar `MisEstructurasDeDatos`, parar y pedir permiso antes de hacerlo.

---

## Plantilla de entrada de diario

```markdown
### Sesión [N] — [FECHA] — Codex

**Clases o ficheros trabajados:**
- [NombreClase] en [paquete]

**Prompt usado (detalle):**
[descripción detallada del objetivo, restricciones, matices y condiciones indicadas por el equipo]

**Resultado:**
- Compila: SÍ / NO
- Tests pasan: SÍ / NO / PARCIAL
- Cambios manuales necesarios: [lista o "ninguno"]

**Problemas encontrados:**
[descripción o "ninguno"]

**Solución aplicada:**
[cómo se corrigió o "n/a"]

**Decisiones técnicas:**
- [decisión]

**Commit sugerido:** `git commit -m "[mensaje]"`
```

---

## Registro de sesiones

### Sesión 1 — 20 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `AGENTS.md`
- `PROJECT_SPEC.md`
- `guia_codex.pdf`
- PDF de diseño añadidos por el equipo

**Prompt usado (detalle):**
El equipo pidió leer detenidamente `AGENTS.md` y `PROJECT_SPEC.md` sin modificar nada, resumir las instrucciones y proponer un plan de trabajo. 
Después añadió `guia_codex.pdf` y pidió revisar el estado real del repositorio, leer la guía y no hacer cambios todavía. 
También aclaró que, si más adelante se editaba documentación, el texto debía conservar acentos correctos y revisarse la codificación.

**Resultado:**
- Compila: no aplica
- Tests pasan: no aplica
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- La consola mostraba caracteres mal decodificados al leer documentación.

**Solución aplicada:**
- Se acordó editar documentación con texto acentuado correctamente y revisar codificación cuando se modificasen `.md`.

**Decisiones técnicas:**
- `AGENTS.md`, `PROJECT_SPEC.md` y `guia_codex.pdf` son las fuentes principales.
- Los PDF de diseño se usan como contexto para comentarios y decisiones de juego.

**Commit sugerido:** `git commit -m "docs: registrar analisis inicial del proyecto"`

---

### Sesión 2 — 20 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `pom.xml`
- Imports mínimos en `MisEstructurasDeDatos`

**Prompt usado (detalle):**
El equipo aceptó el plan inicial y pidió ejecutar una fase de saneamiento del proyecto. La tarea consistía en revisar configuración, 
estructura y compilación, manteniendo las reglas de `AGENTS.md`, sin avanzar todavía en nuevas capas de juego. 
Más adelante autorizó tocar `MisEstructurasDeDatos` solo una vez y solo en lo estrictamente necesario para compilar, 
dejando claro que cualquier modificación futura en esa carpeta debía consultarse otra vez.

**Resultado:**
- Compila: SÍ
- Tests pasan: no había suite completa todavía
- Cambios manuales necesarios: ajuste de configuración Maven

**Problemas encontrados:**
- Configuración Maven desalineada con Java 21.
- Dependencias y `mainClass` no estaban ajustadas al proyecto real.
- Algunas clases de estructuras propias tenían problemas de paquetes/imports.

**Solución aplicada:**
- `maven.compiler.release` ajustado a 21.
- `src` configurado como `sourceDirectory`.
- `tests` configurado como `testSourceDirectory`.
- Añadido Gson 2.10.1.
- Retirado FXGL.
- Ajustado `mainClass` a `Valdris.ui.MainApp`.
- Con permiso puntual, se corrigieron imports/paquetes mínimos de estructuras propias sin cambiar su lógica.

**Decisiones técnicas:**
- No modificar lógica interna de `MisEstructurasDeDatos` sin permiso explícito.

**Commit sugerido:** `git commit -m "build: sanear configuracion inicial del proyecto"`

---

### Sesión 3 — 20 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `EffectType`
- `ItemType`
- `CharacterType`
- `EnemyType`
- `Phase`
- `Effect`
- `Item`
- `Weapon`
- `Armor`
- `Potion`
- `Accessory`

**Prompt usado (detalle):**
El equipo pidió continuar con la capa 2 de `guia_codex.pdf`, empezando por la ficha 2.5 `Item` y continuando por el 
resto de items hasta completar la capa. También pidió que los comentarios fueran más extensos, con el estilo de 
`CellType.java` y después con secciones como en `Effect.java`: atributos, constructor, métodos de lógica, getters, 
comparación, etc. Se indicó expresamente que los PDF de diseño debían usarse como contexto para que los comentarios 
reflejasen mejor el juego.

**Resultado:**
- Compila: SÍ
- Tests pasan: se añadieron después en una sesión específica
- Cambios manuales necesarios: adaptación a `Comparable` por uso de LSE propia

**Problemas encontrados:**
- `ListaSimplementeEnlazada<T>` exige `T extends Comparable<T>`.

**Solución aplicada:**
- `Effect` e `Item` se hicieron comparables.
- Los items concretos heredan comparación por `id`.

**Decisiones técnicas:**
- Mantener `ListaSimplementeEnlazada` sin cambios.
- Añadir comentarios extensos por secciones: atributos, constructor, lógica, getters y comparación.

**Commit sugerido:** `git commit -m "feat: implementar capa 2 de modelo base e items"`

---

### Sesión 4 — 20 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `Unit`
- `Player`
- `Enemy`
- `InvalidMoveException`
- `InvalidAttackException`
- `GameStateException`

**Prompt usado (detalle):**
El equipo pidió pasar a la capa 3, empezando por `Unit`, después `Player` y finalmente `Enemy`. Antes de seguir, 
preguntó cómo funcionaba `recibirDanio(int cantidad)` para confirmar que el parámetro representaba daño final y no 
ataque bruto. También pidió separar responsabilidades para que el cálculo de daño quedara en `CombatManager`. 
Cuando surgió el problema de imports/paquetes y estructuras propias, autorizó tocar `MisEstructurasDeDatos` solo lo 
necesario y compilar al terminar.

**Resultado:**
- Compila: SÍ
- Tests pasan: se añadieron después en una sesión específica
- Cambios manuales necesarios: ajustes de `Comparable`

**Problemas encontrados:**
- `Unit` no podía implementar `Comparable<Unit>` de forma limpia y luego permitir `ListaSimplementeEnlazada<Player>` o `ListaSimplementeEnlazada<Enemy>`.

**Solución aplicada:**
- `Unit` quedó sin `Comparable`.
- `Player` implementa `Comparable<Player>`.
- `Enemy` implementa `Comparable<Enemy>`.

**Decisiones técnicas:**
- `recibirDanio(int cantidad)` recibe daño final ya calculado por `CombatManager`, no ataque bruto.
- `Enemy.onDeath(Room)` coloca el drop en la sala si existe.

**Commit sugerido:** `git commit -m "feat: implementar unidades y excepciones base"`

---

### Sesión 5 — 20 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `Cell`
- `Room`

**Prompt usado (detalle):**
El equipo recomendó crear primero tests de lo ya hecho, pero antes pidió implementar `Cell` y
luego `Room`, manteniendo una tabla de pasos al final de cada respuesta.

Después pidió compilar las capas actuales y, al aparecer la necesidad de excepciones
personalizadas y `Comparable<Enemy>`, autorizó realizar el punto 6.1 de excepciones para poder
compilar correctamente.

**Resultado:**
- Compila: SÍ
- Tests pasan: se añadieron después en una sesión específica
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- No había `Dungeon` todavía, por lo que se cerró primero `Cell` y `Room`.

**Solución aplicada:**
- `Cell` implementa tipo, ocupante, item y revelado.
- `Room` implementa matriz de celdas, enemigos, temporizador y búsqueda de celda libre.

**Decisiones técnicas:**
- `Cell.isWalkable()` centraliza la transitabilidad.
- `Room.getCell(...)` lanza `InvalidMoveException` si está fuera de rango.

**Commit sugerido:** `git commit -m "feat: implementar celdas y salas del mapa"`

---

### Sesión 6 — 20 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `EffectTest`
- `TypeEnumsTest`
- `ItemTest`
- `WeaponTest`
- `ArmorTest`
- `PotionTest`
- `AccessoryTest`
- `UnitTest`
- `PlayerTest`
- `EnemyTest`
- `CellTest`
- `RoomTest`

**Prompt usado (detalle):**
El equipo pidió crear todos los tests necesarios para las capas 2 y 3, basándose en `EffectTest`,
permitiendo modificarlo y mejorarlo, pero exigiendo que el resto de tests mantuvieran la misma
estructura.

Después pidió completar el último paso creando tests para `Cell` y `Room`, con el mismo patrón
Arrange -> Act -> Assert y verificando compilación y ejecución con Maven.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: normalizar cabeceras de tests a ASCII para evitar problemas de consola

**Problemas encontrados:**
- `EffectTest` tenía separadores decorativos que se veían mal en consola.

**Solución aplicada:**
- Se reescribieron cabeceras con estilo `// -- Sección --`.
- Se mantuvo el patrón Arrange -> Act -> Assert.

**Decisiones técnicas:**
- Tests por clase, pequeños y centrados en comportamiento observable.

**Commit sugerido:** `git commit -m "test: cubrir capas 2 3 y mapa inicial"`

---

### Sesión 7 — 20 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `NodoGrafo`
- `Arista`
- `Grafo`
- `InterfazGrafo`
- `Dungeon`
- `DungeonTest`

**Prompt usado (detalle):**
El equipo pidió revisar la ficha de `Dungeon`, implementar la clase, crear `DungeonTest`, compilar
y ejecutar tests. Durante el análisis se detectó que la solución limpia requería acceder
públicamente a datos de `NodoGrafo` y `Arista`.

El equipo interrumpió la tarea para preguntar cuál sería la mejor solución si se pudiera modificar
`MisEstructurasDeDatos`. Después autorizó hacer públicos los métodos necesarios y añadir la API
mínima en `Grafo`, siempre con la condición de consultar cualquier cambio futuro en estructuras.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: API pública mínima en estructuras de grafo

**Problemas encontrados:**
- `NodoGrafo.getDatos()` y `NodoGrafo.getId()` eran `protected`, por lo que `Dungeon` no podía recuperar salas desde nodos.
- La alternativa sin tocar estructuras obligaba a duplicar estado en `Dungeon`.

**Solución aplicada:**
- Con permiso explícito, se hicieron públicos getters necesarios de `NodoGrafo` y `Arista`.
- Se añadió `buscarNodoPorDato(DN dato)` en `Grafo` e `InterfazGrafo`.
- `Dungeon` usa `Grafo<Room, String>` directamente.

**Decisiones técnicas:**
- Evitar soluciones peores si la mejor opción requiere tocar estructuras: parar y consultar.
- `Dungeon` evita duplicar salas por `id`.

**Commit sugerido:** `git commit -m "feat: implementar dungeon sobre grafo de salas"`

---

### Sesión 8 — 21 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `BFSMovimiento`
- `BFSMovimientoTest`
- `BFSCaminoMinimo`
- `BFSCaminoMinimoTest`

**Prompt usado (detalle):**
El equipo pidió empezar la capa 5 revisando la guía e implementando `BFSMovimiento`, recordando
que al final debía aparecer la tabla de pasos completados. Después aclaró que el siguiente punto
debía hacerse igual con `BFSCaminoMinimo`.

Para `BFSCaminoMinimo`, como no existía ficha detallada en la guía aunque sí aparecía en la
estructura del proyecto, se pidió seguir el mismo criterio: basarse en la guía, usar las capas
existentes y comprobar todo con tests.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: clase interna comparable para usar `Cola`

**Problemas encontrados:**
- `Cola<T>` exige `T extends Comparable<T>`.
- La guía no trae ficha detallada para `BFSCaminoMinimo`, aunque aparece en la estructura de paquetes.

**Solución aplicada:**
- `BFSMovimiento` usa una clase privada `PasoBFS implements Comparable<PasoBFS>`.
- `BFSCaminoMinimo` se implementó como utilidad conservadora sobre `Dungeon` y `Grafo.caminoMinimo`.

**Decisiones técnicas:**
- `BFSMovimiento` trabaja a nivel de celdas.
- `BFSCaminoMinimo` trabaja a nivel de salas.
- Las aristas unidireccionales se respetan usando `Grafo.caminoMinimo`.

**Commit sugerido:** `git commit -m "feat: implementar bfs de movimiento y salas"`

---

### Sesión 9 — 21 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `Dungeon`
- `DungeonTest`
- `LineaDeVision`
- `LineaDeVisionTest`

**Prompt usado (detalle):**
Antes de pasar a `LineaDeVision`, el equipo pidió revisar si había cosas ya hechas que pudieran
mejorarse frente a la guía. Se propuso evitar aristas duplicadas en `Dungeon` y aclarar la regla
de visión.

El equipo aceptó las mejoras de `Dungeon` y decidió que, para `LineaDeVision`, solo `WALL`
bloquearía visión, porque `TRAP`, `LEVER`, `RUNE` y `STAIRS` no deberían bloquear y las puertas
cerradas u ocultas estarían en bordes de sala. Con esa decisión, se pidió continuar con los puntos
de la lista.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- `Dungeon.conectar(...)` podía duplicar aristas si se llamaba dos veces.
- La guía podía interpretarse de dos formas sobre qué celdas bloquean visión.

**Solución aplicada:**
- `Dungeon` evita aristas duplicadas en conexiones bidireccionales y unidireccionales.
- `LineaDeVision` implementa Bresenham y bloquea solo `WALL`.

**Decisiones técnicas:**
- `STAIRS`, `TRAP`, `LEVER`, `RUNE` y `DOOR` no bloquean visión.
- `DOOR_LOCKED` y `DOOR_HIDDEN` no preocupan por ahora porque se colocan en bordes.

**Commit sugerido:** `git commit -m "feat: implementar linea de vision y evitar aristas duplicadas"`

---

### Sesión 10 — 21 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `CombatManager`
- `CombatManagerTest`

**Prompt usado (detalle):**
El equipo pidió terminar todos los puntos restantes de la lista, lo que implicaba implementar
`CombatManager` y `CombatManagerTest`. Antes se aclaró que la aleatoriedad oficial del daño
`[0.5, 1.5]` no debía eliminarse, porque era intencionada.

Se aceptó añadir un método auxiliar o sobrecarga determinista solo para tests, siempre que el
método oficial mantuviera `Math.random()`. También se mantuvo el criterio de avisar ante
inconsistencias de la guía, como la necesidad de `Room` para resolver drops al morir un enemigo.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: sobrecargas para conservar guía y permitir contexto de sala

**Problemas encontrados:**
- La guía indica `resolverAtaqueJugador(Player, Enemy)` pero también dice que si el enemigo muere debe llamar a `enemigo.onDeath(room)`, aunque `room` no está en la firma.
- `Math.random()` dificulta pruebas exactas de daño.

**Solución aplicada:**
- Se mantuvo `resolverAtaqueJugador(Player, Enemy)`.
- Se añadió `resolverAtaqueJugador(Player, Enemy, Room)` para drops.
- Se mantuvo `calcularDanio(Unit, Unit)` con aleatoriedad oficial.
- Se añadió `calcularDanio(Unit, Unit, double)` para tests deterministas.

**Decisiones técnicas:**
- No se eliminó la aleatoriedad oficial `[0.5, 1.5]`.
- El AOE del Destructor usa radio Manhattan 2 por ahora.

**Commit sugerido:** `git commit -m "feat: implementar gestor de combate"`

---

### Sesión 11 — 21 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo creó `TASKS.md` y pidió convertirlo en un fichero de seguimiento con todas las tareas
completadas hasta el momento, similar a la tabla de pasos que se había ido usando al final de las
respuestas, pero con absolutamente todas las tareas realizadas.

También pidió añadir documentación al principio explicando para qué sirve el fichero y que el
estilo fuera parecido al resto de `.md`. Después pidió actualizar también `COMMIT_LOG.md`.
Finalmente pidió ampliar este mismo apartado para que no fuera un resumen corto, sino una
descripción más extensa y detallada de los prompts enviados.

**Resultado:**
- Compila: no aplica
- Tests pasan: no aplica
- Cambios manuales necesarios: documentación en Markdown

**Problemas encontrados:**
- `TASKS.md` estaba vacío.
- `COMMIT_LOG.md` seguía con plantilla inicial sin reflejar el avance real.

**Solución aplicada:**
- `TASKS.md` se convirtió en checklist completo de tareas realizadas y pendientes.
- `COMMIT_LOG.md` se actualizó con sesiones de trabajo, problemas, soluciones y decisiones técnicas.

**Decisiones técnicas:**
- `TASKS.md` registra tareas.
- `COMMIT_LOG.md` registra sesiones y metodología de uso de IA.

**Commit sugerido:** `git commit -m "docs: actualizar registro de tareas y diario de IA"`

---

## Progreso actual

### Checklist de clases implementadas

**Bloque 1 — Enums y clases base**
- [x] CellType
- [x] EffectType
- [x] ItemType
- [x] CharacterType
- [x] EnemyType
- [x] Phase
- [x] Effect
- [x] Item (abstracta)
- [x] Weapon
- [x] Armor
- [x] Potion
- [x] Accessory

**Bloque 2 — Unidades**
- [x] Unit (abstracta)
- [x] Player
- [x] Enemy

**Bloque 3 — Mapa**
- [x] Cell
- [x] Room
- [x] Dungeon

**Bloque 4 — Lógica**
- [x] BFSMovimiento
- [x] BFSCaminoMinimo
- [x] LineaDeVision
- [x] CombatManager
- [ ] ArbolDecisionIA
- [ ] IAEnemigo
- [ ] TurnManager
- [ ] ItemGenerator
- [ ] DungeonGenerator

**Bloque 5 — Persistencia**
- [x] InvalidMoveException
- [x] InvalidAttackException
- [x] GameStateException
- [ ] GameState
- [ ] LectorJSON

**Bloque 6 — JavaFX**
- [ ] MainApp
- [ ] GameModelListener
- [ ] GameModel
- [ ] CharacterSelectView
- [ ] GameView
- [ ] GameController
- [ ] InventoryView
- [ ] CombatLogView

### Checklist de tests implementados

- [x] EffectTest
- [x] TypeEnumsTest
- [x] ItemTest
- [x] WeaponTest
- [x] ArmorTest
- [x] PotionTest
- [x] AccessoryTest
- [x] UnitTest
- [x] PlayerTest
- [x] EnemyTest
- [x] CellTest
- [x] RoomTest
- [x] DungeonTest
- [x] BFSMovimientoTest
- [x] BFSCaminoMinimoTest
- [x] LineaDeVisionTest
- [x] CombatManagerTest
- [ ] ArbolDecisionIATest
- [ ] IAEnemigoTest
- [ ] TurnManagerTest
- [ ] ItemGeneratorTest
- [ ] DungeonGeneratorTest
- [ ] GameStateTest

---

## Última verificación registrada

```powershell
.\mvnw.cmd -q test
```

Resultado:

```text
202 tests, 0 failures, 0 errors, 0 skipped
```

---

## Metodología extraída hasta ahora

### Lo que ha funcionado bien

- Leer la ficha de la guía antes de cada clase.
- Implementar clases pequeñas y testear inmediatamente.
- Mantener comentarios extensos y por secciones.
- Usar tests deterministas para fijar reglas de negocio.
- Consultar antes de tocar `MisEstructurasDeDatos`.

### Lo que NO ha funcionado bien

- Algunas fichas tienen pequeñas ambigüedades o firmas incompletas.
- La consola puede mostrar mal caracteres UTF-8 aunque el fichero esté bien codificado.
- Algunas estructuras propias tenían getters demasiado restrictivos para reutilizarlas fuera de su paquete.

### Ajustes hechos al workflow inicial

- Se añadió `TASKS.md` como registro detallado de tareas.
- Se aceptó modificar mínimamente la API pública del grafo con permiso explícito.
- Se añadieron sobrecargas auxiliares cuando la guía necesitaba contexto adicional, sin eliminar las firmas base.
- Se acordó que cualquier mejora frente a la guía debe proponerse antes de implementarse.

### Metodología recomendada para lo que queda

- Continuar con `ArbolDecisionIA`.
- Antes de implementar IA, revisar si el AOE del Destructor debe ser Manhattan, cuadrado o euclídeo.
- Mantener `mvn test` completo al cerrar cada bloque.
- Actualizar `TASKS.md` y `COMMIT_LOG.md` al terminar cada grupo de tareas.

---

## Notas del equipo

- [20 mayo 2026] Se acuerda que los comentarios deben ser extensos y con contexto del juego.
- [20 mayo 2026] Se acuerda pedir permiso antes de cambios relevantes.
- [20 mayo 2026] Se autoriza una modificación mínima en `MisEstructurasDeDatos` para corregir imports.
- [20 mayo 2026] Se autoriza una modificación mínima en la API pública del grafo.
- [21 mayo 2026] Se decide que `LineaDeVision` bloquea solo `WALL`.
- [21 mayo 2026] Se decide conservar la aleatoriedad oficial de combate y añadir una sobrecarga determinista para tests.
