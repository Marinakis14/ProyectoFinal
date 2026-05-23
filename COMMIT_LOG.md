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

### Sesión 12 — 21 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `ArbolDecisionIA`
- `ArbolDecisionIATest`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo pidió continuar desde el punto pendiente de la capa 5, confirmando primero con
`guia_codex.pdf`, `TASKS.md` y `COMMIT_LOG.md` que la siguiente clase era `ArbolDecisionIA`.
Antes de modificar código se revisó la ficha exacta, las clases relacionadas (`Enemy`, `Player`,
`Room`, `CombatManager`, `LineaDeVision`) y las ambigüedades de diseño.

El equipo aceptó tres decisiones previas: usar distancia Manhattan para el Guardian y el Destructor,
mantener una zona de confort simple para Archer/Sniper y representar las condiciones con una
interfaz privada propia, sin `java.util.function`. También pidió mantener una tabla visual en la
conversación, pero conservar el formato Markdown existente dentro de `TASKS.md`.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: configurar `JAVA_HOME` a `C:\Program Files\Java\jdk-25` para que Maven use un JDK y no un JRE

**Problemas encontrados:**
- El entorno tenía `JAVA_HOME` apuntando a un JRE, por lo que Maven no encontraba compilador.
- El primer intento de ejecutar un test concreto con `-Dtest` y nombre completo de paquete fue interpretado mal por PowerShell.

**Solución aplicada:**
- Se relanzó Maven con `JAVA_HOME` apuntando al JDK instalado.
- Se ejecutó el test nuevo usando `"-Dtest=ArbolDecisionIATest"`.
- Se ejecutó después la suite completa.

**Decisiones técnicas:**
- `ArbolDecisionIA` solo decide acciones; no mueve unidades, no ataca y no modifica estado.
- `AccionIA` queda como enum público anidado para que pueda usarlo después `IAEnemigo`.
- Guardian y Destructor usan distancia Manhattan para sus radios tácticos.
- Archer y Sniper atacan si tienen rango y visión; si no, intentan `MOVER_A_ZONA`.

**Commit sugerido:** `git commit -m "feat: implementar arbol de decision de IA"`

---

### Sesión 13 — 21 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `IAEnemigo`
- `IAEnemigoTest`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo pidió continuar con los primeros pasos del siguiente punto de la capa 5, `IAEnemigo`, y
revisar antes la ficha 5.5 de `guia_codex.pdf`, el estado de `TASKS.md`, `COMMIT_LOG.md` y las
clases relacionadas. Se detectó que la guía indicaba usar `BFSCaminoMinimo`, pero en el proyecto
esa clase trabaja caminos entre salas, mientras que el movimiento de enemigos ocurre dentro de una
sala. Se propuso usar `BFSMovimiento` para celdas y el equipo aceptó esa decisión.

También se revisaron las opciones de comportamiento del `SNIPER`, `MOVER_A_ZONA` y la huida del
`SUMMONER`. El equipo rechazó una solución demasiado genérica y pidió implementar desde el
principio tres variantes diferenciadas: persecución normal, reposicionamiento de enemigos a
distancia y huida del Invocador.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: configurar `JAVA_HOME` a `C:\Program Files\Java\jdk-25` para Maven

**Problemas encontrados:**
- El primer pase de suite completa falló en un test antiguo de `CombatManagerTest` por la
  aleatoriedad oficial de daño `[0.5, 1.5]`; al relanzar la suite pasó completa.
- `BFSMovimiento.getCamino(...)` no puede usar como destino la celda ocupada por el jugador, por
  lo que la persecución debe buscar una celda libre adyacente al jugador.
- `Cell` no expone coordenadas, así que `IAEnemigo` localiza coordenadas recorriendo la matriz de
  `Room` sin modificar las estructuras de datos.

**Solución aplicada:**
- `IAEnemigo` usa `BFSMovimiento` para movimiento dentro de sala.
- `MOVER` persigue hacia la mejor celda libre adyacente al jugador.
- `MOVER_A_ZONA` busca una celda alcanzable desde la que el enemigo mantenga distancia, rango y
  línea de visión.
- El `SUMMONER` huye eligiendo la celda alcanzable más lejana del jugador.
- El `SNIPER` ataca solo si tiene `turnosSinActuar >= 2`; si no, consume turno e incrementa
  cooldown.
- El `CONTROLLER` aplica aleatoriamente `SLOW`, `BLIND` o `CURSE` durante 2 turnos.
- El `SUMMONER` invoca un Berserker en una celda libre cercana y reinicia cooldown.

**Decisiones técnicas:**
- Se conserva la firma con parámetro `CombatManager cm` por compatibilidad con la guía, aunque se
  usan métodos estáticos porque `CombatManager` no se instancia.
- No se modificó `MisEstructurasDeDatos`.
- La aleatoriedad oficial de combate se mantiene aunque pueda hacer flaky algún test antiguo si no
  usa la sobrecarga determinista.

**Commit sugerido:** `git commit -m "feat: implementar ejecucion de IA enemiga"`

---

### Sesión 14 — 22 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `Container`
- `Chest`
- `Cell`
- `TurnManager`
- `ContainerTest`
- `ChestTest`
- `CellTest`
- `TurnManagerTest`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo pidió continuar con el bloque pendiente de TurnManager, pero antes se revisaron las guías
convertidas a Markdown en `docs/` para confirmar las reglas exactas de fases, recogida y cofres.
Se autorizó puntualmente crear `Container` y `Chest` como clases nuevas en `Valdris.model.map`, añadir
métodos explícitos para saltar fases y ampliar `Cell` con contenedores y destinos concretos para
`DOOR`/`STAIRS`. También se mantuvo la decisión de separar `TurnManager` de `GameModel` y de la capa UI.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- La ficha de `TurnManager` menciona `GameModel`, pero importarlo desde lógica rompería la regla de capas.
- La guía usa el concepto `item.requiresAdjacent`, pero el modelo actual no tiene ese atributo en `Item`.
- El cambio de sala necesita destino concreto por celda, no solo aristas entre salas en `Dungeon`.

**Solución aplicada:**
- `TurnManager` se implementó como lógica pura, sin dependencia de UI.
- `Container` y `Chest` modelan los cofres y contenedores adyacentes de la fase `PICKUP`.
- `Cell` ahora puede guardar un contenedor opcional y un destino de acceso con sala, fila y columna.
- Los items de suelo siguen en `Cell.item` y se recogen automáticamente al moverse.
- `TurnManager` añade `saltarMovimiento`, `saltarRecogida` y `saltarUsoItem`.
- El turno enemigo captura el número de enemigos inicial para que los invocados actúen en el siguiente turno.

**Decisiones técnicas:**
- No se modificó `MisEstructurasDeDatos`.
- `Container` y `Chest` pertenecen a mapa, no a items, porque no son objetos de inventario.
- Los cofres pueden contener varios items usando `ListaSimplementeEnlazada<Item>`.
- Abrir un cofre por segunda vez no duplica contenido.
- La puerta final de vuelta puede representarse después como `DOOR_LOCKED` estética sin destino funcional.

**Verificación:**
- `.\mvnw.cmd -q -DskipTests compile`
- `.\mvnw.cmd -q "-Dtest=CellTest,ContainerTest,ChestTest,TurnManagerTest" test`
- `.\mvnw.cmd -q test`
- Resultado suite completa: `267 tests, 0 failures, 0 errors, 0 skipped`

**Commit sugerido:** `git commit -m "feat: implementar gestion de turnos y contenedores"`

---

### Sesión 15 — 22 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `Unit`
- `Player`
- `Weapon`
- `Potion`
- `CombatManager`
- `ItemGenerator`
- `UnitTest`
- `PlayerTest`
- `WeaponTest`
- `PotionTest`
- `CombatManagerTest`
- `ItemGeneratorTest`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo pidió revisar el siguiente bloque de generación y empezar por `ItemGenerator`, pero antes
quiso cerrar todas las decisiones de diseño relacionadas con efectos, pociones y drops. Se decidió
que W11 conservaría dos efectos (`SLOW` y `BLIND`) ampliando `Weapon` para soportar un efecto
secundario, que `BLIND` dejaría de duplicar la mecánica de `SLOW` y pasaría a provocar fallo de
ataque, y que P4 y P5 necesitaban soporte explícito en el modelo para limpiar efectos negativos y
aplicar bonus temporal de ataque.

También se pidió sustituir cualquier resultado de tipo "material" por items reales o `null`, dejar
la asignación garantizada de mini-bosses y accesorios AC1-AC4 para `DungeonGenerator`, y añadir
tests suficientes para cubrir los casos oficiales y los casos extra necesarios.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- En la guía rápida `BLIND` tenía el mismo efecto práctico que `SLOW`, por lo que no aportaba una
  identidad mecánica propia.
- P4 necesitaba eliminar efectos negativos concretos, pero `Unit` solo permitía añadir y procesar
  efectos, no retirarlos de forma selectiva.
- P5 requería que el daño del siguiente ataque aumentase de forma temporal y se consumiese al
  resolver el ataque.
- W11 necesitaba representar dos efectos sin crear un efecto combinado artificial.
- Algunos drops previstos como "materiales" no encajaban con el modelo actual de items.

**Solución aplicada:**
- `BLIND` se rediseñó como 25% de probabilidad de fallo de ataque.
- `SLOW` queda como el único efecto que reduce movimiento efectivo.
- `Unit` añade `removeEfecto(EffectType)` para limpiar un efecto concreto.
- `Player` añade bonus temporal de ataque, consumible al resolver el siguiente ataque.
- `Potion` añade campos concretos para efectos a limpiar y bonus temporal de ataque.
- `Weapon` permite un efecto especial secundario con probabilidad independiente.
- `CombatManager` consume el bonus temporal en ataques resueltos, incluidos fallos por `BLIND`, y
  no aplica daño ni efectos cuando el ataque falla por `BLIND`.
- `ItemGenerator` crea items oficiales por id, genera items reales por zona y devuelve drops reales
  o `null` para enemigos.

**Decisiones técnicas:**
- Un ataque fallado por `BLIND` consume acción y bonus temporal de P5.
- Un ataque fallado por `BLIND` no aplica daño ni efectos de arma.
- W11 se representa con dos efectos en `Weapon`: `SLOW` y `BLIND`.
- P4 limpia `CURSE` y `BLIND` llamando a `removeEfecto` para cada efecto.
- P5 cura y añade bonus temporal al siguiente ataque.
- La asignación garantizada de mini-bosses y accesorios AC1-AC4 queda pendiente para
  `DungeonGenerator`.

**Verificación:**
- `.\mvnw.cmd -q -DskipTests compile`
- `.\mvnw.cmd -q "-Dtest=UnitTest,PlayerTest,WeaponTest,PotionTest,CombatManagerTest,ItemGeneratorTest" test`
- `.\mvnw.cmd -q test`
- Resultado suite completa: `303 tests, 0 failures, 0 errors, 0 skipped`

**Commit sugerido:** `git commit -m "feat: implementar generador de items y ajustar efectos"`

---

### Sesión 16 — 22 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `CellType`
- `Cell`
- `LineaDeVision`
- `TurnManager`
- `EffectType`
- `Unit`
- `TypeEnumsTest`
- `CellTest`
- `LineaDeVisionTest`
- `TurnManagerTest`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo añadió `GUIA_PROYECTO_JUEGO_V3.md` al proyecto y pidió compararla con el código actual.
Después se cerraron varias decisiones antes de tocar código: reemplazar el tipo genérico `STAIRS`
por `STAIRS_UP` y `STAIRS_DOWN`, hacer que las escaleras no fueran transitables, exigir que se
usen desde una celda frontal configurada y compartir una acción genérica `usarAccesoAdyacente()`
para puertas y escaleras.

También se decidió que `STAIRS_UP` bloquearía línea de visión y ataques a distancia si aparece
como celda intermedia, mientras que `STAIRS_DOWN` no bloquearía visión. Se descartó `clearLog()`
porque el log de acciones deberá conservarse completo para resumen y persistencia JSON al final de
la partida. `BLIND` mantiene la regla acordada de 25% de fallo de ataque, por lo que se corrigieron
comentarios obsoletos que lo describían como reducción de movimiento.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- La mecánica anterior permitía pisar una puerta para cambiar de sala, pero el nuevo diseño trata
  puertas y escaleras como accesos situados en pared o como elementos del mapa no ocupables.
- Las escaleras colocadas dentro de la sala necesitan orientación para impedir que se usen desde
  cualquier lado.
- `LineaDeVision` tenía codificada directamente la regla "solo WALL bloquea", por lo que convenía
  mover la decisión a `Cell.bloqueaVision()`.

**Solución aplicada:**
- `CellType.STAIRS` se reemplazó por `STAIRS_UP` y `STAIRS_DOWN`.
- `Cell.isWalkable()` trata puertas y escaleras como no transitables.
- `Cell` añade helpers de acceso, orientación frontal, requisito de item narrativo, trigger,
  resaltado visual, reserva de llegada y bloqueo de visión.
- `LineaDeVision` usa `Cell.bloqueaVision()`.
- `TurnManager` añade `usarAccesoAdyacente()`, búsqueda de acceso usable, validación de destino,
  desbloqueo de `DOOR_LOCKED` por item requerido y comprobación de item narrativo por id.
- Los tests se ampliaron para cubrir accesos no transitables, orientación de escaleras,
  `STAIRS_UP` bloqueando visión, `STAIRS_DOWN` sin bloquear, puertas cerradas con llave y destino
  bloqueado.

**Decisiones técnicas:**
- Las puertas no necesitan orientación porque se colocan en paredes.
- Las escaleras sí necesitan orientación porque pueden estar en medio de una sala.
- La celda destino de un acceso debe estar libre y ser transitable; no se busca una alternativa
  automática si está bloqueada.
- El log de partida debe ser acumulativo y persistible, sin método destructivo `clearLog()`.

**Verificación:**
- `.\mvnw.cmd -q -DskipTests compile`
- `.\mvnw.cmd -q "-Dtest=TypeEnumsTest,CellTest,LineaDeVisionTest,TurnManagerTest" test`
- `.\mvnw.cmd -q test`
- Resultado suite completa: `316 tests, 0 failures, 0 errors, 0 skipped`

**Commit sugerido:** `git commit -m "feat: ajustar accesos y escaleras"`

---

### Sesión 17 — 22 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `Room`
- `Dungeon`
- `HiddenPassage`
- `PuzzleManager`
- `TurnManager`
- `RoomTest`
- `DungeonTest`
- `PuzzleManagerTest`
- `TurnManagerTest`
- `GUIA_PROYECTO_JUEGO_V3.md`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo confirmó el siguiente bloque para enriquecer salas con diálogos por personaje, palancas,
runas, pasadizos secretos y log acumulativo. Antes de implementar se cerraron varias decisiones:
usar campos concretos por personaje en `Room` en vez de `Map`, compartir la lógica de `LEVER` y
`RUNE` en un único gestor, llamar a ese gestor `PuzzleManager` en lugar de `LeverManager`, mantener
los pasadizos ocultos fuera del grafo hasta activarlos, y guardar el log de partida de forma
acumulativa sin `clearLog()`.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- `ListaSimplementeEnlazada<String>` es válida porque `String` implementa `Comparable<String>`.
- Al registrar varias celdas equivalentes en una LSE, `contains()` y `getPosicion()` no sirven para
  distinguir celdas por identidad porque `Cell.compareTo()` puede devolver 0 para dos celdas
  distintas pero equivalentes.

**Solución aplicada:**
- `Room` añade diálogos por personaje, flags de diálogo mostrado, secuencias de puzzle,
  `leverCells`, `runeCells`, triggers secretos, objetivo de éxito, limpieza de resaltado y
  validación de celda de llegada.
- El registro de palancas y runas compara por referencia de objeto para permitir varias celdas
  equivalentes en una misma sala.
- `HiddenPassage` modela conexiones ocultas comparables.
- `Dungeon` registra pasadizos ocultos fuera del grafo y los añade como aristas al activarse.
- `PuzzleManager` gestiona secuencias de palancas y runas, aplica éxito activando pasadizos y
  aplica fallo dañando al jugador y reiniciando la secuencia.
- `TurnManager` añade log acumulativo, `onRoomEnter()`, `lastDialogue`, activación de triggers
  secretos, activación automática de runas al moverse y activación de palancas adyacentes desde
  `PICKUP`.
- `GUIA_PROYECTO_JUEGO_V3.md` se actualizó para sustituir `LeverManager` por `PuzzleManager`.

**Decisiones técnicas:**
- No se usa `Map` para diálogos; se usan campos concretos para Kael, Syra y Dorath.
- `PuzzleManager` sustituye conceptualmente a `LeverManager` porque también resuelve runas.
- El log de partida es acumulativo y persistible.
- `changeRoom(...)` llama automáticamente a `onRoomEnter()`.

**Verificación:**
- `.\mvnw.cmd -q -DskipTests compile`
- `.\mvnw.cmd -q "-Dtest=RoomTest,DungeonTest,PuzzleManagerTest,TurnManagerTest" test`
- `.\mvnw.cmd -q test`
- Resultado suite completa: `337 tests, 0 failures, 0 errors, 0 skipped`

**Commit sugerido:** `git commit -m "feat: implementar puzzles y pasadizos secretos"`

---

### Sesión 18 — 22 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `EnemyType`
- `MiniBossType`
- `Enemy`
- `MiniBossEnemy`
- `ItemType`
- `NarrativeItem`
- `Player`
- `ItemGenerator`
- `CombatManager`
- `ArbolDecisionIA`
- `Room`
- `PuzzleManager`
- `DungeonGenerator`
- `TypeEnumsTest`
- `EnemyTest`
- `MiniBossEnemyTest`
- `PlayerTest`
- `ItemGeneratorTest`
- `PuzzleManagerTest`
- `DungeonGeneratorTest`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo pidió cerrar las decisiones pendientes de `DungeonGenerator` antes de implementar. Se añadieron imágenes
del mapa en `docs/mapas/` para recuperar las conexiones perdidas al convertir los PDF a Markdown. Después se fijó que
`PASILLO_FINAL` debía ser una sala real, igual que los pasillos entre zonas, pero sin cofre y con una advertencia de
no retorno antes de `S5-D`. También se decidió dejar constancia de que el mapa implementado tiene 34 salas reales.

Se cerraron las reglas de puzzles: celdas fijas, orden correcto aleatorio al generar partida, daño por fallo 5/6/7/8
según zona, reinicio de secuencia al fallar y puzzle no repetible tras resolverse. Para enemigos se aceptó que la
cantidad y tipo por sala son fijos según la guía, pero que la posición concreta sea aleatoria dentro de casillas
candidatas seguras, sin duplicados y lejos de la entrada.

El equipo proporcionó una tabla balanceada de enemigos nuevos y mini-bosses. Se decidió crear `CONSTRUCTO`,
`SOMBRA_ABSORBIDA` y `ECO_DE_MAGIA` como `EnemyType` normales, separar los mini-bosses en `MiniBossEnemy`, dejar sus
habilidades especiales para más adelante, y tratar `EL_FILTRO` como mini-boss. También se decidió que los objetos
narrativos deben ser items reales con espacio propio, no accesorios equipables.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: actualización de tests existentes por nuevos enums y tipo de item narrativo

**Problemas encontrados:**
- `DungeonGenerator` todavía no existía en `src`.
- La guía hablaba de 33 salas, pero el diseño jugable fijado por el equipo requiere contar `PASILLO_FINAL` como sala real.
- Los items AC1-AC4 estaban modelados como accesorios narrativos, pero el equipo pidió reservarles un espacio propio.
- `PuzzleManager` tenía daño fijo de fallo y necesitaba variar por zona.
- `Enemy` no permitía estadísticas explícitas para mini-bosses porque las calculaba solo por `EnemyType`.

**Solución aplicada:**
- Se creó `DungeonGenerator` con 34 salas, tamaños oficiales, conexiones de las imágenes, secretos, puzzles, cofres,
  items de pasillo, diálogos puntuales, mini-bosses y spawns enemigos seguros.
- `PASILLO_FINAL` se modeló como sala propia con conexión bidireccional desde `S5-C` y conexión unidireccional hacia `S5-D`.
- Se añadió `MiniBossType` y `MiniBossEnemy` para mini-bosses con estadísticas propias.
- Se añadieron `CONSTRUCTO`, `SOMBRA_ABSORBIDA` y `ECO_DE_MAGIA` a `EnemyType`.
- `ECO_DE_MAGIA` ignora 3 defensa del jugador y `EL_FILTRO` ignora 5.
- Se añadió `ItemType.NARRATIVE`, `NarrativeItem` e inventario narrativo separado en `Player`.
- `ItemGenerator` crea AC1-AC4 y N1 como items narrativos y añade Zona 5 con P3/P4/P5.
- `Room` permite configurar daño de fallo de puzzle y abrir accesos marcados por trigger al resolverlo.
- `PuzzleManager` usa el daño configurado por sala.

**Decisiones técnicas:**
- No se modificó `MisEstructurasDeDatos`.
- El mapa estructural es fijo; solo varían el orden de puzzles y las posiciones concretas de enemigos.
- Los spawns enemigos se eligen desde casillas candidatas seguras y se descarta cualquier celda ya ocupada.
- Los mini-bosses quedan separados para permitir habilidades especiales futuras sin ensuciar `Enemy`.
- `MalacharAlly` y `ParasitoEnemy` quedan para un bloque posterior de boss final.

**Verificación:**
- `.\mvnw.cmd -q -DskipTests compile`
- `.\mvnw.cmd -q test`
- Resultado suite completa: `349 tests, 0 failures, 0 errors, 0 skipped`

**Commit sugerido:** `git commit -m "feat: implementar generador de dungeon"`

---

### Sesión 19 — 23 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `GameState`
- `LoadedGame`
- `GameSummary`
- `LectorJSON`
- `TurnManager`
- `Container`
- `GameStateTest`
- `LectorJSONTest`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo pidió empezar el bloque de persistencia después de cerrar la estructura de `DungeonGenerator`.
Antes de implementar se fijó que la guía define la estructura base, pero que se ampliaría para guardar el
estado real actual: inventario narrativo, puzzles, pasadizos ocultos, cofres, enemigos vivos y muertos,
fase actual, flags de acciones y log acumulativo. También se decidió desviarse de la propuesta `Object[]`
de la guía y usar una clase tipada `LoadedGame`. Se cerró además crear `GameSummary` ya en este bloque,
pero solo con datos fiables disponibles ahora.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- Los DTOs de `GameState` no podían guardarse temporalmente en `ListaSimplementeEnlazada` porque la LSE
  propia exige `Comparable<T>`.
- El primer intento de ejecutar una suite concreta quedó bloqueado por permisos de red/sandbox al resolver
  Maven Wrapper.

**Solución aplicada:**
- Se usaron arrays de DTOs y recorridos en dos pasadas donde hacía falta contar elementos antes de construir
  el array, sin tocar `MisEstructurasDeDatos`.
- Se repitió la ejecución de tests con permiso para Maven en el caso bloqueado por sandbox.
- `LectorJSON` lee y escribe JSON explícitamente con UTF-8.
- La carga reconstruye primero el mundo base con `DungeonGenerator` y después aplica estado dinámico:
  salas, celdas, contenedores, pasadizos, jugador, enemigos, sala actual y turno.
- Los enemigos muertos se guardan como `vivo=false` y no se reinsertan en la reconstrucción.
- `Container` recibió `restaurarAbierto(boolean)` para restaurar cofres sin entregar loot de nuevo.
- `TurnManager` recibió setters controlados para restaurar fase, turno global y último diálogo.

**Decisiones técnicas:**
- `GameState` queda como DTO plano público para Gson, sin referencias directas entre objetos del dominio.
- `LoadedGame` sustituye a `Object[]` para evitar casts frágiles al cargar.
- `GameSummary` guarda personaje, sala, HP, turno, inventario normal, inventario narrativo, salas exploradas y log.
- La mejora de mensajes del log queda como bloque posterior separado de persistencia.

**Verificación:**
- `.\mvnw.cmd -q -DskipTests compile`
- `.\mvnw.cmd -q "-Dtest=LectorJSONTest" test`
- `.\mvnw.cmd -q test`
- Resultado suite completa: `360 tests, 0 failures, 0 errors, 0 skipped`

**Commit sugerido:** `git commit -m "feat: implementar persistencia json"`

---

### Sesión 20 — 23 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `LogEventType`
- `GameLogEntry`
- `CombatResult`
- `TurnManager`
- `CombatManager`
- `LectorJSON`
- `TypeEnumsTest`
- `GameLogEntryTest`
- `CombatManagerTest`
- `TurnManagerTest`
- `LectorJSONTest`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo decidió cerrar antes de JavaFX los puntos pendientes del log de operaciones. Se acordó que el
guardado y cargado de partida no entran en el log de partida, sino que más adelante se mostrarán como mensajes
de UI. También se decidió registrar expiración de efectos relevantes cuando se procesen en turno.

Antes de implementar se evaluó cambiar de `ListaSimplementeEnlazada<String>` a un log estructurado con
`GameLogEntry`. El equipo aceptó crear `LogEventType`, colocar `GameLogEntry` en `Valdris.model.log`,
mantener salida textual de utilidad con `getLogTextos()` y partir el trabajo en dos sub-bloques. Este sub-bloque
cubre la base del log estructurado y `CombatResult`; IA, efectos y persistencia estructurada quedan para el
siguiente sub-bloque.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: actualización de tests que asumían log como texto exacto

**Problemas encontrados:**
- Cambiar `TurnManager.getLog()` a `ListaSimplementeEnlazada<GameLogEntry>` afectaba a persistencia, que en ese
  momento aún guardaba texto plano.
- Los tests de persistencia esperaban entradas exactas como `"Movimiento registrado."`, pero el log textual ahora
  sale formateado con turno y tipo.

**Solución aplicada:**
- Se añadió `TurnManager.getLogTextos()` como utilidad visual para UI, tests y resúmenes textuales.
- En este sub-bloque `LectorJSON` quedó temporalmente conectado a esa salida textual hasta completar el diseño
  único estructurado en la sesión siguiente.
- Se creó `CombatResult` para devolver daño aplicado, fallo por `BLIND`, muerte, HP restante, efectos aplicados y drop.
- `CombatManager` devuelve `CombatResult` en ataques del jugador, ataques enemigos y AOE del Destructor.
- `TurnManager` registra eventos estructurados para movimiento, recogida, uso de item, combate, accesos, sala,
  puzzle y turno enemigo.

**Decisiones técnicas:**
- `GameLogEntry` implementa `Comparable<GameLogEntry>` para poder almacenarse en la LSE propia.
- `LogEventType` evita usar categorías de log como texto libre.
- La persistencia JSON estructurada del log se deja para el sub-bloque 2.
- No se modificó `MisEstructurasDeDatos`.

**Verificación:**
- `.\mvnw.cmd -q -DskipTests compile`
- `.\mvnw.cmd -q "-Dtest=CombatManagerTest,TurnManagerTest,LectorJSONTest,GameStateTest,TypeEnumsTest,GameLogEntryTest" test`
- `.\mvnw.cmd -q test`
- Resultado suite completa: `367 tests, 0 failures, 0 errors, 0 skipped`

**Commit sugerido:** pendiente al cerrar el bloque completo de log estructurado.

---

### Sesión 21 — 23 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `LogEventType`
- `GameLogEntry`
- `AIActionResult`
- `IAEnemigo`
- `EffectProcessingResult`
- `Unit`
- `TurnManager`
- `GameState`
- `GameSummary`
- `LectorJSON`
- `IAEnemigoTest`
- `TurnManagerTest`
- `UnitTest`
- `GameStateTest`
- `LectorJSONTest`
- `TypeEnumsTest`
- `GameLogEntryTest`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo pidió revisar si alguna decisión del bloque anterior había sido conservadora por posibles partidas
antiguas. Se aclaró que no existen partidas guardadas reales que conservar, por lo que el log estructurado debe
ser el diseño único y no una capa paralela al texto plano. También se decidió que `LogEventType.SYSTEM` era una
categoría demasiado técnica y debía reemplazarse por `GAME`, reservada para eventos globales de partida. Por
último, se cerró que `ejecutarUsoItem(null)` ya no debe saltar fase: el método correcto para no usar item es
`saltarUsoItem()`.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: actualización de tests de persistencia, IA, turnos y enums

**Problemas encontrados:**
- `GameState` y `GameSummary` seguían exponiendo `String[] log`.
- `LectorJSON` reconstruía logs de texto usando la sobrecarga legacy de `TurnManager`.
- `Unit.procesarEfectos()` modificaba estado pero no devolvía información para registrar daño o expiraciones.
- `IAEnemigo.executeTurn(...)` ejecutaba acciones enemigas pero no devolvía información para log detallado.

**Solución aplicada:**
- Se eliminó el log textual de persistencia y se añadió `GameLogEntryDTO[] logEventos` como único formato persistido.
- `LectorJSON` extrae y reconstruye `GameLogEntry` desde DTOs estructurados.
- Se añadió `AIActionResult` para describir ataques, movimiento, invocaciones, efectos aplicados y esperas de IA.
- `IAEnemigo.executeTurn(...)`, `ejecutarAtaque(...)` e `invocarBerserker(...)` devuelven resultados estructurados.
- Se añadió `EffectProcessingResult` y `Unit.procesarEfectos()` devuelve daño aplicado y efectos expirados.
- `TurnManager` registra acciones concretas de enemigos, daño por efectos y expiración de efectos.
- Si un enemigo muere por efectos al inicio de su turno, la IA resuelve su drop, lo retira de la sala y mantiene el turno del resto de enemigos.
- `ejecutarUsoItem(null)` lanza `GameStateException`; `saltarUsoItem()` queda como única API para saltar esa fase.
- `LogEventType.SYSTEM` se reemplazó por `LogEventType.GAME`.

**Decisiones técnicas:**
- El log estructurado es el diseño único de persistencia.
- `getLogTextos()` se mantiene solo como utilidad de visualización.
- Guardado y carga de partida no se registran dentro del log jugable.
- No se modificó `MisEstructurasDeDatos`.

**Verificación:**
- `.\mvnw.cmd -q "-Dtest=IAEnemigoTest,TurnManagerTest,UnitTest,GameStateTest,LectorJSONTest,TypeEnumsTest,GameLogEntryTest" test`
- `.\mvnw.cmd -q test`
- Resultado suite completa: `371 tests, 0 failures, 0 errors, 0 skipped`

**Commit sugerido:** `git commit -m "feat: completar log estructurado"`

---

## Progreso actual

### Checklist de clases implementadas

**Bloque 1 — Enums y clases base**
- [x] CellType
- [x] EffectType
- [x] ItemType
- [x] CharacterType
- [x] EnemyType
- [x] MiniBossType
- [x] LogEventType
- [x] Phase
- [x] Effect
- [x] EffectProcessingResult
- [x] Item (abstracta)
- [x] NarrativeItem
- [x] Weapon
- [x] Armor
- [x] Potion
- [x] Accessory
- [x] GameLogEntry

**Bloque 2 — Unidades**
- [x] Unit (abstracta)
- [x] Player
- [x] Enemy
- [x] MiniBossEnemy

**Bloque 3 — Mapa**
- [x] Cell
- [x] Room
- [x] Dungeon
- [x] Container
- [x] Chest
- [x] HiddenPassage

**Bloque 4 — Lógica**
- [x] BFSMovimiento
- [x] BFSCaminoMinimo
- [x] LineaDeVision
- [x] CombatManager
- [x] CombatResult
- [x] ArbolDecisionIA
- [x] IAEnemigo
- [x] AIActionResult
- [x] TurnManager
- [x] ItemGenerator
- [x] PuzzleManager
- [x] DungeonGenerator

**Bloque 5 — Persistencia**
- [x] InvalidMoveException
- [x] InvalidAttackException
- [x] GameStateException
- [x] GameState
- [x] LoadedGame
- [x] GameSummary
- [x] LectorJSON

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
- [x] ArbolDecisionIATest
- [x] IAEnemigoTest
- [x] ContainerTest
- [x] ChestTest
- [x] TurnManagerTest
- [x] ItemGeneratorTest
- [x] PuzzleManagerTest
- [x] MiniBossEnemyTest
- [x] DungeonGeneratorTest
- [x] GameStateTest
- [x] LectorJSONTest
- [x] GameLogEntryTest

---

## Última verificación registrada

```powershell
.\mvnw.cmd -q test
```

Resultado:

```text
371 tests, 0 failures, 0 errors, 0 skipped
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
- No se hicieron commits incrementales durante el desarrollo. Al hacer un único commit final con
  muchas clases, tests, documentación, PDFs y ajustes de configuración, el historial no refleja
  bien la evolución real ni permite revisar con claridad cada bloque de cambios.
- Parte del problema vino del bloqueo de Git por `dubious ownership`, porque el repositorio estaba
  marcado como propiedad de otro usuario de Windows frente al usuario sandbox de Codex. Aun así,
  este problema debería haberse detectado antes con una comprobación temprana de `git status`.

### Ajustes hechos al workflow inicial

- Se añadió `TASKS.md` como registro detallado de tareas.
- Se aceptó modificar mínimamente la API pública del grafo con permiso explícito.
- Se añadieron sobrecargas auxiliares cuando la guía necesitaba contexto adicional, sin eliminar las firmas base.
- Se acordó que cualquier mejora frente a la guía debe proponerse antes de implementarse.
- Al comenzar una sesión nueva, comprobar primero `git status` y resolver cualquier problema de
  acceso al repositorio antes de acumular cambios.
- Hacer commits pequeños al cerrar cada ficha, clase o bloque funcional, siguiendo el flujo
  previsto en `AGENTS.md`, para que GitHub muestre un historial útil y revisable.

### Metodología recomendada para lo que queda

- Cerrar las reglas de mini-bosses y accesorios AC1-AC4 antes de implementar `DungeonGenerator`.
- Fijar la estructura exacta de generación de zonas, salas, accesos, puzzles y pasadizos antes de
  implementar `DungeonGenerator`.
- Continuar con `DungeonGenerator`.
- Mantener la decisión de radio Manhattan en IA y combate salvo que el equipo la cambie explícitamente.
- Mantener `BFSMovimiento` para movimientos dentro de sala; `BFSCaminoMinimo` queda para caminos entre salas.
- Mantener `mvn test` completo al cerrar cada bloque.
- Actualizar `TASKS.md` y `COMMIT_LOG.md` al terminar cada grupo de tareas.
- Hacer commit después de cada tarea cerrada y verificada, no solo al final de varias capas.

---

## Notas del equipo

- [20 mayo 2026] Se acuerda que los comentarios deben ser extensos y con contexto del juego.
- [20 mayo 2026] Se acuerda pedir permiso antes de cambios relevantes.
- [20 mayo 2026] Se autoriza una modificación mínima en `MisEstructurasDeDatos` para corregir imports.
- [20 mayo 2026] Se autoriza una modificación mínima en la API pública del grafo.
- [21 mayo 2026] Se decide que `LineaDeVision` bloquea solo `WALL`.
- [21 mayo 2026] Se decide conservar la aleatoriedad oficial de combate y añadir una sobrecarga determinista para tests.
- [21 mayo 2026] Se detecta que el primer gran commit agrupó demasiados cambios. Se registra como
  fallo de proceso y se acuerda hacer commits incrementales en adelante.
- [21 mayo 2026] Se decide que `ArbolDecisionIA` use Manhattan para Guardian y Destructor, y que
  Archer/Sniper usen una zona de confort simple.
- [21 mayo 2026] Se decide que `IAEnemigo` use `BFSMovimiento` para celdas, implemente persecución,
  reposicionamiento a distancia y huida del `SUMMONER`, y respete cooldown real del `SNIPER`.
- [22 mayo 2026] Se decide que `TurnManager` no dependa de `GameModel` ni de JavaFX; la UI refrescará
  desde fuera.
- [22 mayo 2026] Se autoriza puntualmente crear `Container` y `Chest` en `Valdris.model.map`.
- [22 mayo 2026] Se decide que los items de suelo se recojan automáticamente al moverse y que `PICKUP`
  quede para contenedores adyacentes.
- [22 mayo 2026] Se decide añadir destinos concretos a `Cell` para puertas y escaleras.
- [22 mayo 2026] Se decide rediseñar `BLIND` como 25% de fallo de ataque; ya no reduce movimiento.
- [22 mayo 2026] Se decide que W11 use dos efectos en `Weapon`: `SLOW` y `BLIND`.
- [22 mayo 2026] Se decide que P4 limpie `CURSE` y `BLIND`, y que P5 aplique bonus temporal al
  siguiente ataque.
- [22 mayo 2026] Se decide que `ItemGenerator` genere items reales o `null`, no materiales.
- [22 mayo 2026] Se aplaza la definición cerrada de mini-bosses y accesorios AC1-AC4 para antes de
  `DungeonGenerator`.
- [22 mayo 2026] Se decide reemplazar `STAIRS` por `STAIRS_UP` y `STAIRS_DOWN`.
- [22 mayo 2026] Se decide que puertas y escaleras se usen desde `PICKUP` con
  `usarAccesoAdyacente()`, sin pisar la celda de acceso.
- [22 mayo 2026] Se decide que `STAIRS_UP` bloquee visión como obstáculo intermedio y
  `STAIRS_DOWN` no bloquee visión.
- [22 mayo 2026] Se decide que las escaleras tengan orientación frontal y que las puertas no la
  necesiten por estar en paredes.
- [22 mayo 2026] Se descarta `clearLog()` porque el historial de acciones debe conservarse para
  resumen y persistencia JSON.
- [22 mayo 2026] Se decide implementar diálogos por personaje con campos concretos en `Room`, no
  con `Map`.
- [22 mayo 2026] Se decide que `LEVER` y `RUNE` compartan lógica de secuencia en `PuzzleManager`.
- [22 mayo 2026] Se decide guardar pasadizos ocultos fuera del grafo hasta que un trigger o puzzle
  los active.
- [22 mayo 2026] Se decide que `changeRoom(...)` llame automáticamente a `onRoomEnter()`.
