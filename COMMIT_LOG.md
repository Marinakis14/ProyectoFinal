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

### Sesión 22 — 23 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `MiniBossAI`
- `IAEnemigo`
- `AIActionResult`
- `ArbolDecisionIA`
- `TurnManager`
- `MiniBossEnemy`
- `MiniBossType`
- `MiniBossAITest`
- `MiniBossEnemyTest`
- `TurnManagerTest`
- `GUIA_DISENO_V4_FINAL.md`
- `GUIA_DISENO_V5_FINAL.md`
- `GUIA_PROYECTO_JUEGO_V3.md`
- `TASKS.md`

**Prompt usado (detalle):**
El equipo cerró las habilidades especiales de los mini-bosses y pidió implementarlas antes de JavaFX.
Se decidió que el Golem dejara de ocupar 2x2 de forma real o conceptual y pasara a ser una unidad 1x1,
con Pisotón Sísmico como área de radio Manhattan 2 que atraviesa paredes por tratarse de una onda sísmica.
También se fijó que los especiales respetan BLIND y que el sistema completo de game over queda para más adelante.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: actualización de tests de IA, turnos, mini-bosses y documentación de diseño

**Problemas encontrados:**
- `MiniBossEnemy` todavía exponía `isOcupa2x2()`, que ya no representa una regla vigente.
- La IA enemiga solo conocía comportamientos por `EnemyType`, sin punto claro para habilidades narrativas.
- El log estructurado necesitaba nombre de actor y nombre de habilidad para no registrar mini-bosses como tipos genéricos.

**Solución aplicada:**
- Se añadió `MiniBossAI` para resolver habilidades especiales sin ensuciar `Enemy`.
- `IAEnemigo.executeTurn(...)` delega en `MiniBossAI` cuando el enemigo es `MiniBossEnemy`.
- `AIActionResult` puede transportar nombre narrativo de actor y nombre de habilidad especial.
- `ArbolDecisionIA.AccionIA` incluye `HABILIDAD_ESPECIAL`.
- `TurnManager` registra habilidades especiales, fallos por BLIND, efectos aplicados y derrotas del jugador en el log.
- Se eliminaron `ocupa2x2` e `isOcupa2x2()` de `MiniBossEnemy`.
- Se actualizaron las guías Markdown para reflejar que el Golem es 1x1 y usa Pisotón Sísmico.

**Decisiones técnicas:**
- Alcalde Corrupto: Estocada Corrupta, 35 daño fijo, rango 1, cooldown 3.
- Espíritu Madre: Enredadera Paralizante, 12 daño fijo + PARALYSIS 1 turno, rango 4 con visión, cooldown 3.
- Golem: Pisotón Sísmico, 28 daño fijo, radio Manhattan 2, sin línea de visión, cooldown 3.
- Guardián Sin Nombre: Sentencia Arcana, 20 daño fijo + CURSE 2 turnos, rango 1, cooldown 2.
- El Filtro mantiene solo su pasivo de penetración 5.
- Si un especial cargado no alcanza tras intentar moverse, se pierde el intento y se reinicia cooldown.
- No se modificó `MisEstructurasDeDatos`.

**Verificación:**
- `.\mvnw.cmd -q "-Dtest=MiniBossAITest,IAEnemigoTest,TurnManagerTest,MiniBossEnemyTest,TypeEnumsTest,DungeonGeneratorTest,LectorJSONTest" test`
- `.\mvnw.cmd -q test`
- Resultado suite completa: `379 tests, 0 failures, 0 errors, 0 skipped`

**Commit sugerido:** `git commit -m "feat: implementar habilidades de mini-bosses"`

---

## 23 mayo 2026 — Bloque final antes de JavaFX

### Objetivo

Cerrar la lógica jugable final antes de empezar la capa JavaFX: Malachar como aliado NPC, Parásito
como enemigo final con fases, desenlace de victoria/derrota y persistencia del estado especial.

### Decisiones aplicadas

- Se añadió `GameResult` separado de `Phase`, porque el resultado de partida no forma parte del
  ciclo táctico de turno.
- `MalacharAlly` se implementó como `Unit`, no como `Enemy`, para que bloquee celda y visión pero
  no pueda ser atacado por el jugador ni tratado como objetivo enemigo.
- `ParasitoEnemy` extiende `Enemy` y usa `EnemyType.PARASITO` para persistencia e identidad de log.
- El Parásito tiene tres fases con stats dinámicas, transiciones con acción consumida y Devorar Luz
  como explosión global garantizada al entrar en fase 3.
- Los AOE del Parásito atraviesan paredes y unidades, afectan al jugador y a Malachar, y no usan
  línea de visión.
- `Room` guarda a Malachar en un campo separado de `enemigos`.
- `TurnManager` gestiona inicio explícito del combate final, turno aliado, turno especial del
  Parásito, victoria por sacrificio y derrota si el jugador cae antes del desenlace.
- `GameState`, `GameSummary` y `LectorJSON` guardan/cargan resultado final, textos de desenlace,
  estado de Malachar y campos especiales del Parásito.

### Verificación

- Se añadieron tests de `MalacharAlly`, `ParasitoEnemy`, flujo final en `TurnManager` y persistencia
  del combate final.
- Se actualizó `TypeEnumsTest` para incluir `EnemyType.PARASITO`.
- Suite completa ejecutada con `mvn test`: correcta.

---

### Sesión 23 — 24 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `MainApp` en `Valdris.ui`
- `MainMenuView` en `Valdris.ui.view`
- `CharacterSelectView` en `Valdris.ui.view`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo pidió empezar la capa JavaFX por el subbloque 1, dejando antes fijado el alcance:
arranque de aplicación, menú inicial y selección de personaje. Se acordó que la pantalla inicial
debe permitir nueva partida, carga futura y salida; que la selección muestre Kael, Syra y Dorath
con stats y textos correctamente acentuados; y que los botones todavía no conectados a la partida
real muestren mensajes temporales controlados.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- Ninguno bloqueante. La capa `ui` estaba limpia y sin clases Java tras el saneamiento realizado por el equipo.

**Solución aplicada:**
- Se creó `MainApp` como punto de entrada JavaFX con ventana fija de 1280x720.
- Se creó `MainMenuView` con botones `Nueva partida`, `Cargar partida` y `Salir`.
- Se creó `CharacterSelectView` con tarjetas para Kael, Syra y Dorath, incluyendo stats, rol y descripción.
- `Cargar partida` y `Elegir` muestran avisos temporales hasta que se implemente `GameModel` y la carga real.

**Decisiones técnicas:**
- JavaFX se implementa de forma programática, sin FXML en este subbloque.
- El menú inicial precede a la selección de personaje para permitir carga de partida en un bloque posterior.
- Se usan textos visuales con tildes correctas.
- Este subbloque no llama todavía a `DungeonGenerator`, `Player` ni `TurnManager`.

**Verificación:**
- Suite completa ejecutada con `mvn test`: `401 tests, 0 failures, 0 errors, 0 skipped`.

**Commit sugerido:** `git commit -m "feat(ui): add initial JavaFX navigation"`

---

### Sesión 24 — 24 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `GameModelListener` en `Valdris.ui.model`
- `GameModel` en `Valdris.ui.model`
- `CharacterSelectView` en `Valdris.ui.view`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo autorizó el subbloque 2 de JavaFX, dejando fijado que `GameModelListener`
debe resolver la restricción de `ListaSimplementeEnlazada<T extends Comparable<T>)`
mediante un `compareTo`, y que `GameView` debe quedar fuera de este subbloque.
El objetivo era crear una partida real desde la selección de personaje sin renderizar todavía
la pantalla principal.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- Ninguno bloqueante. La LSE exige `Comparable`, por lo que el listener se definió como comparable.

**Solución aplicada:**
- Se creó `GameModelListener` con `compareTo` por identidad para poder almacenarlo en la LSE propia.
- Se creó `GameModel` con referencias a `Dungeon`, `Player`, `TurnManager`, listeners y último mensaje.
- `GameModel` genera el mundo, crea el jugador, crea `TurnManager` y coloca al jugador en la sala inicial con `changeRoom(...)`.
- `CharacterSelectView` ahora crea un `GameModel` real y muestra una confirmación temporal hasta que exista `GameView`.

**Decisiones técnicas:**
- `GameModel.addListener(...)` evita duplicados por referencia, no por comparación, para no depender del hash de identidad.
- `GameModel` no ejecuta acciones jugables; solo inicializa estado y notifica cambios.
- Los errores de inicialización se exponen con `GameStateException` y se muestran desde JavaFX con un `Alert`.
- `GameView` se mantiene fuera de este subbloque.

**Verificación:**
- Suite completa ejecutada con `mvn test`: `401 tests, 0 failures, 0 errors, 0 skipped`.

**Commit sugerido:** `git commit -m "feat(ui): add game model initialization"`

---

### Sesión 25 — 24 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `GameController` en `Valdris.ui.controller`
- `GameView` en `Valdris.ui.view`
- `CombatLogView` en `Valdris.ui.view`
- `CharacterSelectView` en `Valdris.ui.view`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo autorizó el subbloque 3 de JavaFX sin pedir más decisiones. Se acordó implementar
una pantalla principal solo lectura, conectar la selección de personaje a esa pantalla, mostrar
mapa, panel lateral y log, dejar el inventario visible pero deshabilitado, y no conectar todavía
acciones jugables de turno.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- Ninguno bloqueante.

**Solución aplicada:**
- Se creó `GameController` mínimo con navegación al menú principal.
- Se creó `CombatLogView` para mostrar los últimos cinco mensajes del log de partida.
- Se creó `GameView` con `BorderPane`, mapa central en `GridPane`, panel lateral de estado y log inferior.
- `CharacterSelectView` ahora crea `GameModel`, `GameController`, `GameView` y cambia la escena al elegir personaje.

**Decisiones técnicas:**
- El mapa se renderiza en modo solo lectura con `StackPane` por celda.
- El jugador se muestra con `K`, `S` o `D`; enemigos con `E`; items con `*`; contenedores con `C`.
- Las trampas no se revelan visualmente en este subbloque y se muestran como suelo.
- El botón `Inventario` queda visible pero deshabilitado hasta el subbloque correspondiente.

**Verificación:**
- Suite completa ejecutada con `mvn test`: `401 tests, 0 failures, 0 errors, 0 skipped`.

**Commit sugerido:** `git commit -m "feat(ui): add read-only game screen"`

---

### Sesión 26 — 24 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `GameController` en `Valdris.ui.controller`
- `GameView` en `Valdris.ui.view`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo pidió revisar de nuevo la API pública de `TurnManager` antes de implementar el subbloque 4.
Se detectó que además de las acciones tácticas normales faltaba contemplar `saltarRecogida()` y
`iniciarCombateFinal()`. El equipo decidió incluir ambas en el subbloque 4.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- Ninguno bloqueante.

**Solución aplicada:**
- `GameController` conecta clicks de celda a movimiento o ataque según fase.
- `GameController` conecta botones para saltar movimiento, recoger, saltar recogida, usar acceso,
  activar palanca, saltar uso de item, ceder turno e iniciar combate final.
- `GameView` muestra botones tácticos habilitados según fase.
- `GameView` resalta con borde verde las celdas alcanzables por BFS en fase `MOVEMENT`.
- Los errores de `InvalidMoveException`, `InvalidAttackException` y `GameStateException` se muestran en el log visual mediante `GameModel`.

**Decisiones técnicas:**
- `Ceder turno` llama a `cederTurno()` y después a `ejecutarTurnoEnemigos()` para devolver el flujo al jugador.
- El inventario sigue deshabilitado hasta el subbloque específico.
- `Iniciar combate final` queda disponible en S5-D si el combate final no ha empezado; si no se cumplen condiciones, `TurnManager` informa el error.

**Verificación:**
- Suite completa ejecutada con `mvn test`: `401 tests, 0 failures, 0 errors, 0 skipped`.

**Commit sugerido:** `git commit -m "feat(ui): connect basic turn controls"`

---

### Sesión 27 — 24 mayo 2026 — Codex

**Clases o ficheros trabajados:**
- `InventoryView` en `Valdris.ui.view`
- `GameController` en `Valdris.ui.controller`
- `GameView` en `Valdris.ui.view`
- `TASKS.md`
- `COMMIT_LOG.md`

**Prompt usado (detalle):**
El equipo autorizó el subbloque 5 de JavaFX, aceptando que el inventario pueda abrirse en cualquier fase,
que fuera de `USE_ITEM` funcione como modo lectura, y que durante `USE_ITEM` permita usar o equipar un
item y cierre el modal tras una acción correcta.

**Resultado:**
- Compila: SÍ
- Tests pasan: SÍ
- Cambios manuales necesarios: ninguno

**Problemas encontrados:**
- La pausa accidental de la conversación ocurrió antes de hacer cambios. Se comprobó `git status` y el
  workspace estaba limpio, por lo que no hubo cambios parciales.

**Solución aplicada:**
- Se creó `InventoryView` como ventana modal con equipo, inventario normal e items narrativos.
- `GameView` habilita el botón `Inventario` en todo momento.
- `GameController` abre el modal y ejecuta `TurnManager.ejecutarUsoItem(item)` desde el inventario.
- Los items narrativos se muestran solo en lectura.

**Decisiones técnicas:**
- El inventario se puede consultar en cualquier fase.
- Los botones `Usar` se deshabilitan fuera de `USE_ITEM`.
- El modal se cierra automáticamente si el item se usa o equipa correctamente.
- Si `ejecutarUsoItem(...)` falla, el modal permanece abierto y el error se muestra en el log.

**Verificación:**
- Suite completa ejecutada con `mvn test`: `401 tests, 0 failures, 0 errors, 0 skipped`.

**Commit sugerido:** `git commit -m "feat(ui): add inventory modal"`

---

### Sesión 28 — 24 mayo 2026 — Codex

**Objetivo:** JavaFX subbloque 6: integrar carga básica desde menú y autoguardado en checkpoints.

**Archivos trabajados:**
- `src/Valdris/ui/model/GameModel.java`
- `src/Valdris/ui/view/MainMenuView.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Añadido slot único de guardado `partida_valdris.json` como ruta compartida por JavaFX.
- Añadido autoguardado desde `GameModel` al entrar en los checkpoints acordados: sala inicial, pasillos de transición y antes de mini-boss/final.
- Evitado repetir guardado mientras el jugador permanece dentro del mismo checkpoint.
- Añadida carga real desde `MainMenuView` usando `LectorJSON.cargarPartida(...)`, `LoadedGame`, `GameModel` y `GameController`.
- Sustituido el placeholder de carga por un diálogo de error si la partida no existe o no se puede reconstruir.
- Mantenido fuera de `GameView` cualquier botón de carga o guardado manual.

**Decisiones aplicadas:**
- De momento existe una única partida guardada para no aumentar complejidad.
- El guardado es automático y solo ocurre en puntos importantes de progreso.
- El mensaje visual de guardado correcto es `Progreso guardado.`.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta.

**Commit sugerido:** `git commit -m "feat(ui): add checkpoint autosave"`

---

### Sesión 29 — 24 mayo 2026 — Codex

**Objetivo:** JavaFX subbloque 7: integrar diálogos narrativos, pantalla final y exportación manual del resumen.

**Archivos trabajados:**
- `src/Valdris/ui/model/GameModel.java`
- `src/Valdris/ui/controller/GameController.java`
- `src/Valdris/ui/view/GameView.java`
- `src/Valdris/ui/view/MainMenuView.java`
- `src/Valdris/ui/view/FinalView.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Añadida fachada en `GameModel` para consumir diálogos pendientes, consultar resultado final y exportar el resumen final.
- Creada `FinalView` como pantalla dedicada para victoria o derrota.
- Integrada exportación manual de `resumen_valdris.json` desde la pantalla final.
- `GameView` muestra automáticamente los diálogos de sala en modal y los deja también en el log visual.
- `GameView` cambia a pantalla final cuando `GameResult` deja de ser `IN_PROGRESS`.
- `MainMenuView` abre directamente `FinalView` si se carga una partida ya terminada.

**Decisiones aplicadas:**
- Los diálogos se muestran una sola vez usando el estado ya marcado por `Room` y `TurnManager`.
- La exportación del resumen final no es automática; el jugador la solicita con un botón.
- La pantalla final mantiene navegación al menú principal y no añade carga manual dentro de partida.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta.

**Commit sugerido:** `git commit -m "feat(ui): add ending screen"`

---

### Sesión 30 — 24 mayo 2026 — Codex

**Objetivo:** Corregir errores detectados en la primera prueba jugable de JavaFX: movimiento fuera de rango y ataque poco claro.

**Archivos trabajados:**
- `src/Valdris/logic/turn/TurnManager.java`
- `src/Valdris/ui/controller/GameController.java`
- `src/Valdris/ui/view/GameView.java`
- `tests/Valdris/logic/turn/TurnManagerTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Corregida la validación de movimiento para comparar celdas alcanzables por referencia real.
- Corregido el resaltado JavaFX de movimiento para usar la misma comparación por referencia.
- Añadido test de regresión que impide mover a una casilla de suelo fuera de rango aunque sea equivalente por `compareTo`.
- Añadido resaltado rojo para enemigos atacables durante la fase `ATTACK`.
- Añadido resaltado secundario para enemigos visibles pero no atacables en fase `ATTACK`.
- Añadidos mensajes más claros cuando se intenta atacar antes de llegar a `ATTACK`.
- Renombrado visualmente `Ceder turno` a `Saltar ataque` durante la fase de ataque.

**Problemas encontrados:**
- `ListaSimplementeEnlazada.contains()` usa `compareTo()`, y `Cell.compareTo()` compara tipo/contenido, no coordenadas ni identidad.
- Eso permitía que una celda de suelo fuera de rango se aceptase como si estuviese en la lista BFS.

**Solución aplicada:**
- No se modificó `MisEstructurasDeDatos`.
- No se cambió `Cell.compareTo()`, porque se usa como compatibilidad general con estructuras propias.
- La validación de movimiento y el resaltado de UI comparan ahora instancias exactas de `Cell`.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Test específico ejecutado con `mvn -Dtest=TurnManagerTest test`: correcto.
- Suite completa ejecutada con `mvn test`: correcta.

**Commit sugerido:** `git commit -m "fix(ui): improve movement and attack feedback"`

---

### Sesión 31 — 24 mayo 2026 — Codex

**Objetivo:** Mejorar la visibilidad de información esencial en la primera versión jugable de JavaFX: vida de enemigos, inventario del jugador y log de acciones.

**Archivos trabajados:**
- `src/Valdris/ui/view/GameView.java`
- `src/Valdris/ui/view/CombatLogView.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Añadida vida actual/máxima de los enemigos directamente en la celda del mapa.
- Convertido el panel lateral en desplazable para que pueda contener más información sin perder controles.
- Añadido resumen siempre visible de equipo, inventario normal e inventario narrativo del jugador.
- Mantenido el modal de inventario como vista detallada y zona de uso/equipamiento durante `USE_ITEM`.
- Ampliado el log inferior a siete mensajes visibles, con título y altura fija.

**Problemas encontrados:**
- La información existía en el modelo y en el modal, pero la pantalla principal no la enseñaba de forma suficiente durante la partida.
- El panel lateral no tenía scroll, por lo que añadir más datos podía provocar pérdida visual de botones en resoluciones ajustadas.

**Solución aplicada:**
- Se mantiene la lógica intacta y la UI solo consulta getters ya existentes de `Player`, `Enemy` y `TurnManager`.
- La pantalla principal muestra ahora información resumida; el modal conserva el detalle completo del inventario.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta.

**Commit sugerido:** `git commit -m "fix(ui): show combat status panels"`

---

### Sesión 32 — 24 mayo 2026 — Codex

**Objetivo:** Redistribuir la pantalla principal para aprovechar mejor el espacio disponible y abrir el juego ocupando la pantalla completa de trabajo.

**Archivos trabajados:**
- `src/Valdris/ui/MainApp.java`
- `src/Valdris/ui/view/GameView.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Aumentado el tamaño base de la ventana a `1600x900`.
- La ventana principal ahora es redimensionable, tiene tamaño mínimo `1280x720` y se abre maximizada.
- La pantalla de juego pasa a usar panel izquierdo para estado, sala y acciones.
- El mapa queda en el centro y aumenta el tamaño visual de celda.
- El inventario y el equipo pasan a un panel derecho independiente, visible junto al resto de información.
- Los botones de acción se organizan en dos columnas para reducir altura ocupada.

**Problemas encontrados:**
- Con una sola columna lateral, inventario, acciones y estado competían por el mismo espacio vertical.
- La ventana fija `1280x720` hacía que la interfaz pareciese pequeña y obligaba a comprimir demasiada información.

**Solución aplicada:**
- No se cambia lógica de juego.
- La UI usa el ancho de una ventana maximizada para separar estado/acciones, mapa e inventario.
- Se usa ventana maximizada en lugar de modo fullscreen exclusivo para evitar ocultar controles del sistema y permitir redimensionar.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta.

**Commit sugerido:** `git commit -m "fix(ui): improve game screen layout"`

---

### Sesión 33 — 24 mayo 2026 — Codex

**Objetivo:** Mejorar la lectura del mapa sustituyendo letras simples por representaciones visuales del jugador y los enemigos.

**Archivos trabajados:**
- `src/Valdris/ui/view/GameView.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Añadido mini-sprite visual para el jugador dentro de su celda.
- El color del jugador cambia según personaje: Kael, Syra o Dorath.
- Añadido mini-sprite visual para enemigos con silueta y ojos.
- El color de los enemigos cambia por familia: cuerpo a cuerpo, distancia, mágicos, constructos o entidades oscuras.
- Se mantiene el HP actual/máximo debajo del sprite enemigo.

**Problemas encontrados:**
- Las letras `K`, `S`, `D` y `E` no diferenciaban bien unidades en el tablero.
- La vida de los enemigos era visible, pero la celda seguía pareciendo demasiado textual.

**Solución aplicada:**
- Se usan formas JavaFX (`Circle`, `Polygon`) para crear sprites ligeros sin depender de rutas de imagen externas.
- No se cambia lógica de juego ni persistencia.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta.

**Commit sugerido:** `git commit -m "fix(ui): add unit sprites to map"`

---

### Sesión 34 — 24 mayo 2026 — Codex

**Objetivo:** Mejorar el feedback de puzzles y estados para que la primera sala de palancas no parezca inactiva o confusa.

**Archivos trabajados:**
- `src/Valdris/logic/turn/TurnManager.java`
- `src/Valdris/ui/view/GameView.java`
- `src/Valdris/ui/view/CombatLogView.java`
- `tests/Valdris/logic/turn/TurnManagerTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- `TurnManager` registra mensajes explícitos cuando una secuencia de puzzle termina correctamente o falla.
- Las palancas y runas activadas se muestran en verde durante la secuencia actual; si el puzzle queda resuelto, permanecen verdes.
- El panel de estado muestra los efectos activos del jugador con turnos restantes.
- El log inferior aumenta su padding inferior para que los últimos mensajes queden ligeramente más arriba.
- Se añade test de regresión para verificar el mensaje de combinación incorrecta y reinicio del puzzle.

**Problemas encontrados:**
- El `CONTROLLER` de la primera sala de puzzle no tiene por qué hacer daño directo: su acción normal es aplicar efectos, pero la UI no mostraba esos efectos.
- `PuzzleManager` resolvía éxito o fallo de secuencia, pero la pantalla no dejaba claro qué había pasado.

**Solución aplicada:**
- Se mantiene la IA existente y se mejora la visibilidad de sus consecuencias.
- El estado de puzzle se infiere desde `Room.getSecuenciaActivada()` y `Room.isPuzzleResolved()` sin añadir estado duplicado en `Cell`.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta.

**Commit sugerido:** `git commit -m "fix(ui): improve puzzle and status feedback"`

---

### Sesión 35 — 26 mayo 2026 — Codex

**Objetivo:** Ajustar el recorte inferior del log y añadir pistas progresivas tras los fallos de puzzle.

**Archivos trabajados:**
- `src/Valdris/ui/view/CombatLogView.java`
- `src/Valdris/model/map/Room.java`
- `src/Valdris/logic/puzzle/PuzzleManager.java`
- `src/Valdris/logic/turn/TurnManager.java`
- `src/Valdris/persistence/GameState.java`
- `src/Valdris/persistence/LectorJSON.java`
- `tests/Valdris/logic/turn/TurnManagerTest.java`
- `tests/Valdris/persistence/LectorJSONTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- El log vuelve a mostrar cinco mensajes y reserva una franja inferior vacía para que las últimas líneas no queden cortadas.
- `Room` guarda un contador de fallos acumulados del puzzle.
- `PuzzleManager.applyFailure(...)` incrementa el contador cuando una secuencia completa falla.
- `TurnManager` muestra pistas progresivas: primero `Pista: empieza por ...`, después `Pista: la siguiente es la ...`.
- Cuando ya se han revelado todas las piezas de la secuencia, también muestra `Pista: la combinación correcta es: ...`.
- La secuencia de las pistas se muestra con posiciones visibles empezando en 1, coherente con el orden real de activación del puzzle.
- `GameState` y `LectorJSON` guardan y restauran el contador de fallos.
- Se añade test de regresión para las pistas progresivas y se amplían comprobaciones de persistencia.

**Problemas encontrados:**
- El ejemplo `1,0,1` describe una máscara binaria, pero la lógica real del proyecto usa una secuencia ordenada de palancas/runas.

**Solución aplicada:**
- La pista muestra el orden correcto de activación, por ejemplo `2, 1`, usando numeración natural para el jugador.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta.

**Commit sugerido:** `git commit -m "fix(ui): add progressive puzzle hints"`

---

### Sesión 36 — 26 mayo 2026 — Codex

**Objetivo:** Corregir nuevos problemas jugables detectados en pruebas: palancas ocultas por unidades, daño demasiado bajo y movilidad excesiva de Syra con el anillo temprano.

**Archivos trabajados:**
- `src/Valdris/model/map/Cell.java`
- `src/Valdris/model/units/Player.java`
- `src/Valdris/logic/generation/ItemGenerator.java`
- `src/Valdris/ui/view/CharacterSelectView.java`
- `tests/Valdris/model/map/CellTest.java`
- `tests/Valdris/model/units/PlayerTest.java`
- `tests/Valdris/model/items/PotionTest.java`
- `tests/Valdris/logic/combat/CombatManagerTest.java`
- `tests/Valdris/logic/generation/ItemGeneratorTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- `Cell.isWalkable()` considera `LEVER` no transitable para impedir que jugador o enemigos se coloquen sobre palancas.
- Las runas siguen siendo transitables porque su interacción actual se activa al pisarlas.
- Aumentado en +5 el ataque base de Kael, Syra y Dorath.
- Aumentado en +5 el daño base de todas las armas oficiales W1-W12.
- Reducido el movimiento base de Syra de 5 a 4.
- Actualizados los textos visibles de selección de personaje con los nuevos stats.
- Añadido test para fijar que `LEVER` no es transitable y `RUNE` sí lo es.
- Actualizados tests de jugador, combate, pociones e items al nuevo balance.

**Problemas encontrados:**
- La primera ejecución de tests falló porque varias expectativas seguían fijadas al daño antiguo de Kael.
- No era correcto hacer `RUNE` no transitable sin rediseñar la activación de runas.

**Solución aplicada:**
- Se actualizan las expectativas de test al balance aprobado.
- Se limita el cambio de transitabilidad a `LEVER`.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta, 405 tests.

**Commit sugerido:** `git commit -m "fix(balance): adjust puzzle cells and damage"`

---

### Sesión 37 — 26 mayo 2026 — Codex

**Objetivo:** Mejorar la supervivencia del jugador y conectar los drops de enemigos normales detectados como pieza pendiente durante la prueba jugable.

**Archivos trabajados:**
- `src/Valdris/model/units/Player.java`
- `src/Valdris/logic/generation/ItemGenerator.java`
- `src/Valdris/logic/generation/DungeonGenerator.java`
- `src/Valdris/ui/view/CharacterSelectView.java`
- `tests/Valdris/model/units/PlayerTest.java`
- `tests/Valdris/logic/combat/CombatManagerTest.java`
- `tests/Valdris/logic/generation/ItemGeneratorTest.java`
- `tests/Valdris/logic/generation/DungeonGeneratorTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Los tres personajes jugables pasan a tener defensa base 3.
- Las armaduras y escudos oficiales A1-A8 reciben +3 defensa.
- `AC8` no se modifica y mantiene su bonus de defensa actual.
- La pantalla de selección de personaje muestra ahora `Defensa 3`.
- `DungeonGenerator` asigna drops probabilísticos a enemigos normales usando `ItemGenerator.crearDropEnemigo(...)`.
- Los acompañantes normales de mini-bosses también reciben drops probabilísticos.
- Los mini-bosses mantienen sus drops narrativos fijos.
- Se añade una sobrecarga determinista de `generarMundo(...)` con tiradas de drops para tests.
- Se añade test de regresión para confirmar que un enemigo normal generado recibe drop cuando la tirada lo permite.

**Problemas encontrados:**
- El sistema de drops ya existía en `Enemy`, `CombatManager`, `TurnManager` e `ItemGenerator`, pero los enemigos normales creados por `DungeonGenerator` no recibían ningún `dropItem`.

**Solución aplicada:**
- Conectar la asignación de drops en la generación del mundo sin tocar la resolución de muerte, que ya colocaba correctamente el item en la celda.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta, 406 tests.

**Commit sugerido:** `git commit -m "fix(balance): add defense and normal enemy drops"`

---

### Sesión 38 — 26 mayo 2026 — Codex

**Objetivo:** Reforzar de nuevo el daño del jugador, hacer los drops más satisfactorios y evitar que el jugador pueda abandonar una sala sin derrotar a los enemigos.

**Archivos trabajados:**
- `src/Valdris/model/units/Player.java`
- `src/Valdris/logic/generation/ItemGenerator.java`
- `src/Valdris/logic/turn/TurnManager.java`
- `src/Valdris/ui/view/CharacterSelectView.java`
- `tests/Valdris/model/units/PlayerTest.java`
- `tests/Valdris/model/items/PotionTest.java`
- `tests/Valdris/logic/combat/CombatManagerTest.java`
- `tests/Valdris/logic/generation/ItemGeneratorTest.java`
- `tests/Valdris/logic/turn/TurnManagerTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Aplicado un segundo +5 al ataque base de Kael, Syra y Dorath.
- Aplicado un segundo +5 al daño base de las armas oficiales W1-W12.
- Actualizada la pantalla de selección de personaje con los nuevos valores de ataque.
- `ItemGenerator.crearDropEnemigo(...)` ahora devuelve drops garantizados para enemigos normales.
- Añadidos drops para `CONSTRUCTO`, `SOMBRA_ABSORBIDA` y `ECO_DE_MAGIA`.
- `TurnManager.usarAccesoAdyacente()` bloquea puertas y escaleras mientras queden enemigos vivos en la sala actual.
- Se mantienen las demás condiciones de acceso: fase correcta, puerta/escala usable, llave, puzzle, orientación y llegada válida.
- Añadidos tests para drops garantizados de enemigos nuevos y para impedir cambiar de sala con enemigos vivos.

**Problemas encontrados:**
- La firma determinista de `crearDropEnemigo(tipo, tiradaDrop, tiradaOpcion)` conservaba una tirada de probabilidad que ya no decide nada.

**Solución aplicada:**
- Se mantiene el parámetro por compatibilidad con tests y llamadas existentes, pero se documenta que ya no decide si hay drop.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta, 408 tests.

**Commit sugerido:** `git commit -m "fix(balance): increase damage and gate room exits"`

---

### Sesión 39 — 26 mayo 2026 — Codex

**Objetivo:** Mejorar la lectura del inventario y agilizar las acciones repetidas de turno con atajos de teclado visibles.

**Archivos trabajados:**
- `src/Valdris/ui/view/GameView.java`
- `src/Valdris/ui/view/InventoryView.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- El resumen lateral de inventario agrupa items repetidos por ID y muestra cantidades con formato `xN`.
- El modal de inventario agrupa items repetidos por ID y muestra una línea `Cantidad: N`.
- El uso de un item agrupado sigue actuando sobre una unidad concreta del grupo.
- Añadidos atajos de teclado en la pantalla de partida:
  - `M`: saltar movimiento.
  - `R`: recoger.
  - `A`: usar acceso.
  - `L`: activar palanca.
  - `S`: saltar recogida.
  - `U`: saltar uso de item.
  - `C`: ceder turno o saltar ataque.
  - `F`: combate final.
  - `I`: abrir inventario completo.
- Los botones muestran la tecla asociada con texto compacto para mantener la disposición de dos columnas.

**Problemas encontrados:**
- No convenía cambiar la estructura real del inventario porque afectaría a persistencia, uso de items y equipamiento.

**Solución aplicada:**
- La agrupación se implementa solo en la capa visual, recorriendo la lista propia y agrupando por `item.getId()`.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta, 408 tests.

**Commit sugerido:** `git commit -m "fix(ui): group inventory and add shortcuts"`

---

### Sesión 40 — 26 mayo 2026 — Codex

**Objetivo:** Ajustar el balance defensivo tras nuevas pruebas, manteniendo la defensa base de personajes pero retirando el refuerzo extra de items defensivos.

**Archivos trabajados:**
- `src/Valdris/logic/generation/ItemGenerator.java`
- `tests/Valdris/logic/generation/ItemGeneratorTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- A1-A8 vuelven a sus valores defensivos anteriores al ajuste de +3.
- Se mantiene la defensa base 3 de Kael, Syra y Dorath.
- `AC8` sigue sin cambios.
- Actualizado `ItemGeneratorTest` para fijar de nuevo la defensa original de A3 y A8.

**Problemas encontrados:**
- Ninguno en código; el ajuste responde a balance jugable tras pruebas.

**Solución aplicada:**
- Revertir solo la defensa extra de armaduras y escudos oficiales, sin tocar ataque, drops, accesos ni defensa base de personajes.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta, 408 tests.

**Commit sugerido:** `git commit -m "fix(balance): restore armor defense values"`

---

### Sesión 41 — 26 mayo 2026 — Codex

**Objetivo:** Añadir tests de conectividad estructural y mostrar en la partida la distancia a la salida abierta más cercana.

**Archivos trabajados:**
- `src/Valdris/logic/turn/TurnManager.java`
- `src/Valdris/ui/view/GameView.java`
- `tests/Valdris/logic/turn/TurnManagerTest.java`
- `tests/Valdris/logic/generation/DungeonGeneratorTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Añadida consulta pública en `TurnManager` para calcular la distancia mínima hasta la celda desde la que se puede usar una puerta o escalera abierta.
- Añadida consulta pública para saber si quedan enemigos vivos en la sala actual.
- `GameView` muestra en el panel de estado la salida más cercana:
  - `0 casillas` si el jugador ya está junto a la salida.
  - `N casillas` si hay una ruta caminable hasta una salida abierta.
  - `Derrota a todos los enemigos.` si la regla de accesos bloquea la salida por enemigos vivos.
  - `No hay salidas abiertas.` si no existe ninguna puerta/escalera abierta alcanzable.
- Añadidos tests de `TurnManager` para fijar distancia 0, distancia positiva, puerta cerrada y bloqueo por enemigos vivos.
- Añadidos tests de conectividad en `DungeonGeneratorTest` para proteger llegadas de accesos, salidas abiertas, cofres, palancas, runas y triggers de suelo.

**Problemas encontrados:**
- Ninguno. La regla encaja con `BFSMovimiento` y con el modelo actual de accesos no transitables.

**Solución aplicada:**
- Reutilizar BFS por celdas y `Cell.isUsableFrom(...)` para que el cálculo respete paredes, contenedores, unidades, orientación de escaleras y puertas cerradas/ocultas.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta, 415 tests.
- `git diff --check`: correcto, solo avisos esperados de normalización LF/CRLF.

**Commit sugerido:** `git commit -m "feat(ui): show nearest exit distance"`

---

### Sesión 42 — 26 mayo 2026 — Codex

**Objetivo:** Añadir límite global de turnos, límites por sala y contadores visibles en la pantalla de juego.

**Archivos trabajados:**
- `src/Valdris/model/map/Room.java`
- `src/Valdris/logic/turn/TurnManager.java`
- `src/Valdris/logic/generation/DungeonGenerator.java`
- `src/Valdris/persistence/GameState.java`
- `src/Valdris/persistence/LectorJSON.java`
- `src/Valdris/ui/view/GameView.java`
- `tests/Valdris/model/map/RoomTest.java`
- `tests/Valdris/logic/turn/TurnManagerTest.java`
- `tests/Valdris/logic/generation/DungeonGeneratorTest.java`
- `tests/Valdris/persistence/LectorJSONTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- `Room` conserva ahora `turnosMaximos` además de `turnosRestantes`, con métodos para configurar y reiniciar el temporizador de sala.
- `TurnManager` fija 500 turnos globales y `DungeonGenerator` configura límites por sala: normales 20/25, mini-bosses 35, S5-D 50, puzzles y pasillos sin límite.
- `TurnManager` activa derrota limpia si se agota el límite global o el temporizador de la sala, sin lanzar una excepción jugable al usuario.
- Al entrar en una sala se reinicia su temporizador; al cargar partida se respeta el valor persistido porque la reconstrucción coloca al jugador sin `changeRoom(...)`.
- `GameState` y `LectorJSON` persisten el máximo y los turnos restantes de cada sala.
- `GameView` muestra el nombre de la sala en grande sobre el mapa y sustituye `Nombre` por los contadores `Turno global` y `Turnos sala`.
- Añadidos tests de temporizador de sala, derrota por tiempo, configuración del generador y persistencia de máximos/restantes.

**Problemas encontrados:**
- Ninguno. La carga existente seguía siendo compatible; en partidas antiguas sin `turnosMaximos`, el máximo se reconstruye a partir de los turnos restantes.

**Solución aplicada:**
- Mantener el contador real en lógica pura y exponer solo getters simples para la UI, sin duplicar estado en JavaFX.

**Verificación:**
- Compilación ejecutada con `mvn -DskipTests compile`: correcta.
- Suite completa ejecutada con `mvn test`: correcta, 418 tests.
- `rg "import java\.util" src tests`: sin resultados.
- `git diff --check`: correcto, solo avisos esperados de normalización LF/CRLF.

**Commit sugerido:** `git commit -m "feat(turns): add room and global turn limits"`

---

### Sesión 43 — 26 mayo 2026 — Codex

**Objetivo:** Sincronizar la documentación de seguimiento tras el commit de los últimos cambios y dejar claro el estado real de tests y ficheros de entrega.

**Archivos trabajados:**
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- `TASKS.md` pasa de 415 a 418 tests registrados.
- Añadidos al checklist los tests finales `MalacharAllyTest`, `ParasitoEnemyTest`, `TurnManagerFinalBossTest` y `LectorJSONFinalBossTest`.
- Añadidas verificaciones para boss final, persistencia del combate final, límites de turno y saneamiento documental.
- Registrada la decisión de conservar versionados `partida_valdris.json` y `resumen_valdris.json` como ficheros de entrega.
- Actualizada la metodología final para sustituir referencias obsoletas a continuar con `DungeonGenerator` por una guía de pulido final.

**Problemas encontrados:**
- `TASKS.md` había quedado desfasado frente a `COMMIT_LOG.md`: seguía indicando 415 tests y no listaba varios tests finales.
- La sección de metodología recomendada aún hablaba de tareas ya completadas.

**Solución aplicada:**
- Sincronizar ambos ficheros sin tocar lógica de juego ni los nuevos documentos añadidos para evaluación.

**Verificación:**
- `rg "^import java\.util" src tests`: sin resultados.
- `git diff --check`: correcto, solo avisos esperados de normalización LF/CRLF.
- Suite completa ejecutada con `mvn test`: correcta, 418 tests.

**Commit sugerido:** `git commit -m "docs: sync project progress records"`

---

### Sesion 44 — 26 mayo 2026 — Codex

**Objetivo:** Integrar los tests de estructuras propias pegados desde otro proyecto, corrigiendo rutas, paquetes e imports para que formen parte de la suite Maven del proyecto.

**Archivos trabajados:**
- `tests/MisEstructurasDeDatos/Arbolesbinarios/ArbolBinarioDeBusquedaEnterosTest.java`
- `tests/MisEstructurasDeDatos/Arbolesbinarios/ArbolBinarioDeBusquedaEquilibradoTest.java`
- `tests/MisEstructurasDeDatos/Arbolesbinarios/ArbolBinarioDeBusquedaTest.java`
- `tests/MisEstructurasDeDatos/Arbolesbinarios/MainTest.java`
- `tests/MisEstructurasDeDatos/Arbolesbinarios/NodoTest.java`
- `tests/MisEstructurasDeDatos/Grafos/AristaTest.java`
- `tests/MisEstructurasDeDatos/Grafos/DatosGrafoJsonTest.java`
- `tests/MisEstructurasDeDatos/Grafos/GrafoTest.java`
- `tests/MisEstructurasDeDatos/Grafos/LectorGrafoJsonTest.java`
- `tests/MisEstructurasDeDatos/Grafos/NodoTest.java`
- `tests/MisEstructurasDeDatos/Grafos/TripletaJsonTest.java`
- `tests/MisEstructurasDeDatos/ListasPilasYColas/ListaSimplementeEnlazadaTest.java`
- `tests/MisEstructurasDeDatos/ListasPilasYColas/ListaCircularTest.java`
- `tests/MisEstructurasDeDatos/ListasPilasYColas/ColaTest.java`
- `tests/MisEstructurasDeDatos/ListasPilasYColas/PilaTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Reubicados los tests desde `tests/java/Hoja2a/...` y `tests/java/Hoja2b/...` a rutas coherentes con los paquetes reales `MisEstructurasDeDatos.Arbolesbinarios` y `MisEstructurasDeDatos.Grafos`.
- Corregidos los `package` que usaban `java.Hoja2a...` y `java.Hoja2b...`, evitando declarar tests bajo `java.*`.
- Corregidos imports a `MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada`.
- Normalizados imports estaticos de JUnit para compilar todos los `assertEquals`, `assertTrue`, `assertFalse`, `assertNotEquals`, etc.
- Reescritos los tests en UTF-8 sin BOM para que `javac` no falle con `illegal character: '\ufeff'`.
- Añadidos tests directos para `ListaSimplementeEnlazada`, `ListaCircular`, `Cola` y `Pila`.
- Registrados 60 tests adicionales de estructuras propias.

**Problemas encontrados:**
- Maven no usaba una carpeta `test`; el proyecto tiene `tests` como `testSourceDirectory`.
- Los tests venian de otro proyecto y no coincidían con los paquetes reales del repositorio.
- La primera reescritura mecanica dejo BOM al principio de los ficheros.
- `LectorGrafoJsonTest` imprime un error esperado al probar la carga de un fichero inexistente.

**Solucion aplicada:**
- Mantener los tests dentro del arbol Maven actual y hacer que pertenezcan al mismo paquete que las clases probadas, lo que permite acceder a constructores y metodos con visibilidad de paquete/protegida sin modificar `MisEstructurasDeDatos`.

**Verificacion:**
- Suite completa ejecutada con `mvn test`: correcta, 478 tests.

**Commit sugerido:** `git commit -m "test: integrate data structure tests"`

---

### Sesion 45 — 26 mayo 2026 — Codex

**Objetivo:** Sustituir la distancia local a una salida por una ruta global hacia `S5-D` y añadir una acción visual para revelar el camino recomendado.

**Archivos trabajados:**
- `src/Valdris/logic/turn/TurnManager.java`
- `src/Valdris/ui/view/GameView.java`
- `src/Valdris/ui/view/CombatLogView.java`
- `tests/Valdris/logic/turn/TurnManagerTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- `TurnManager` calcula ahora la mejor salida global hacia `S5-D`, sumando distancia en celdas dentro de la sala actual y distancia de salas restantes.
- Añadidas consultas públicas para distancia global, salas restantes, siguiente sala destino y camino de celdas revelable.
- Si varias salidas tienen el mismo coste, el desempate queda fijado por distancia de salas, distancia de celdas, id de destino y coordenadas.
- `GameView` muestra la distancia global con número de casillas, salas restantes y siguiente sala.
- Añadido botón y atajo `V` para revelar u ocultar la ruta recomendada, con resaltado azul propio en el mapa.
- Reubicado el botón de ruta junto al dato de salida para que quede siempre visible sin añadir una quinta fila a la rejilla de acciones.
- Compactado `CombatLogView` retirando el margen inferior excesivo para recuperar espacio real de mensajes.
- Acortado el texto de ayuda de ataque para evitar cortes y desplazamientos del panel lateral.
- El cálculo de ruta revelada deja de bloquearse por enemigos vivos: el acceso sigue sin poder usarse hasta derrotarlos, pero la ayuda visual puede orientar al jugador.
- El cálculo de ruta revelada considera puertas bloqueadas conocidas como continuidad de progreso, para que la guía funcione antes de resolver puzzles obligatorios.
- El camino visual de celdas ignora unidades ocupantes para que un enemigo sobre la ruta no oculte la guía hacia la puerta.
- El camino revelado incluye también la celda de puerta o escalera objetivo para que el destino quede claro en el mapa.
- Añadidos tests para elegir la ruta de menor coste global, desempatar por id de destino y devolver vacío si no existe ruta hacia `S5-D`.
- Añadido test para comprobar que la ruta visual se muestra aunque queden enemigos vivos en la sala.
- Añadidos tests para una puerta de progreso bloqueada fuera del grafo activo y para la ruta en una partida generada desde `S1-A`.
- Añadido test para enemigo ocupando una celda del camino visual hacia el acceso recomendado.

**Problemas encontrados:**
- El cálculo anterior solo miraba la salida abierta más cercana de la sala actual, por lo que podía recomendar una puerta corta que alejase al jugador del objetivo final.
- La primera ubicación del botón de ruta como novena acción creaba una quinta fila, empujaba el panel lateral y dejaba el log demasiado abajo en partida real.
- La primera versión de la ruta global reutilizaba la misma restricción que el uso real de accesos y devolvía camino vacío mientras hubiera enemigos vivos, por eso el botón cambiaba pero no se pintaban casillas.
- La segunda versión dependía solo del grafo activo hasta `S5-D`; como los puzzles obligatorios registran puertas bloqueadas antes de activar su arista, la partida nueva mostraba siempre que no había ruta disponible.
- El BFS de celdas usado por movimiento evita unidades, pero para una ayuda visual eso hacía que un enemigo situado en el pasillo pudiera ocultar la ruta.

**Solución aplicada:**
- Combinar `BFSMovimiento` para el camino dentro de sala con `BFSCaminoMinimo` para el tramo entre salas, manteniendo `TurnManager` sin dependencias de JavaFX.

**Verificación:**
- Suite completa ejecutada con `mvn test`: correcta, 485 tests.
- `git diff --check`: correcto.

**Commit sugerido:** `git commit -m "feat(ui): reveal global route to final room"`

---

### Sesion 46 — 26 mayo 2026 — Codex

**Objetivo:** Definir la configuración inicial determinista del mundo completo en JSON para cubrir el requisito de carga de configuración inicial desde JSON.

**Archivos trabajados:**
- `config/configuracion_inicial_valdris.json`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Creado `config/configuracion_inicial_valdris.json` como fuente declarativa del mundo inicial.
- Incluidos `initialRoomId`, `objectiveRoomId` y posición inicial del jugador.
- Definidas las 34 salas del mapa con `layout` completo por matriz, incluyendo paredes, suelo, puertas, puertas bloqueadas, puertas ocultas, escaleras, palancas y runas.
- Añadidos metadatos de celdas para destinos de accesos, requisitos de item narrativo, triggers, orientación de escaleras, cofres e items de suelo.
- Registradas 35 conexiones de grafo, incluyendo conexiones bidireccionales, conexiones ocultas activables por puzzles/secretos y el punto de no retorno `PASILLO_FINAL -> S5-D`.
- Definidos puzzles con daño, objetivo y secuencia determinista.
- Registrados diálogos narrativos por personaje.
- Definidos 61 enemigos iniciales con posiciones y drops deterministas, incluyendo mini-bosses y drops narrativos AC1-AC4.
- Añadida sección `finalCombat` con posiciones de Malachar y el Parásito y parámetros principales del combate final.

**Problemas encontrados:**
- El mundo anterior vivía implícitamente en `DungeonGenerator`, por lo que había que trasladar toda la información fija a un formato auditable sin cambiar todavía la lógica Java.
- Algunas validaciones PowerShell dentro del sandbox fallaron al ejecutar parseo JSON; se repitió la validación con permiso escalado por tratarse de una comprobación local de lectura.

**Solución aplicada:**
- Mantener el JSON como primer subbloque independiente, sin conectar todavía `GameModel` ni `LectorJSON`.
- Usar `layout` para describir todas las celdas de cada habitación y reservar `cells` para metadatos que no caben en un símbolo simple.

**Verificación:**
- Validación local de JSON: parseo correcto.
- Comprobación estructural: 34 salas, 35 conexiones, 61 enemigos.
- Comprobación de layouts: todas las filas y columnas coinciden con las dimensiones declaradas.
- No se ejecutó `mvn test` porque todavía no se ha modificado código Java.

**Decisiones técnicas:**
- La configuración inicial oficial de entrega será determinista y estará en `config/configuracion_inicial_valdris.json`.
- `DungeonGenerator` se mantiene por ahora sin cambios hasta implementar el cargador de configuración.

**Commit sugerido:** `git commit -m "feat(config): add initial dungeon json"`

---

### Sesion 47 — 26 mayo 2026 — Codex

**Objetivo:** Conectar la configuracion inicial JSON con la creacion de partida nueva y con la reconstruccion de partidas guardadas.

**Archivos trabajados:**
- `src/Valdris/persistence/GameConfig.java`
- `src/Valdris/persistence/DungeonConfigLoader.java`
- `src/Valdris/model/map/Dungeon.java`
- `src/Valdris/ui/model/GameModel.java`
- `src/Valdris/persistence/LectorJSON.java`
- `src/Valdris/logic/turn/TurnManager.java`
- `tests/Valdris/persistence/DungeonConfigLoaderTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Creado `GameConfig` como DTO plano de Gson para salas, layouts, celdas especiales, conexiones, enemigos, puzzles, dialogos, posicion inicial y objetivo.
- Creado `DungeonConfigLoader` en capa de persistencia para leer `config/configuracion_inicial_valdris.json` en UTF-8 y construir un `Dungeon` vivo.
- El cargador crea salas, aplica `layout`, configura conexiones normales, unidireccionales y ocultas, asigna destinos de accesos, cofres, items de suelo, triggers, puzzles, dialogos y enemigos.
- `Dungeon` guarda ahora `idSalaInicial` e `idSalaObjetivo`.
- `GameModel` crea partidas nuevas desde `DungeonConfigLoader` en lugar de llamar directamente a `DungeonGenerator`.
- `LectorJSON.reconstruirDesdeGameState(...)` reconstruye el mundo base desde la configuracion JSON antes de aplicar el estado guardado.
- `TurnManager` usa el objetivo configurado en `Dungeon` para la ruta global, manteniendo fallback a `S5-D`.
- Añadido `DungeonConfigLoaderTest` con 5 pruebas para salas, objetivo, no retorno, accesos especiales, puzzles, cofres, items, enemigos, mini-bosses, dialogos y temporizadores.

**Problemas encontrados:**
- La configuracion inicial pertenece a persistencia porque usa Gson y E/S; meterla dentro de `DungeonGenerator` habria roto el orden de capas.
- `partida_valdris.json` ya aparecia modificado en el working tree antes de este subbloque; se dejo fuera de los cambios realizados.

**Solucion aplicada:**
- Separar el loader JSON en `Valdris.persistence` y mantener `DungeonGenerator` como utilidad historica/testeable.
- Construir primero todas las salas, despues el grafo, y por ultimo aplicar metadatos de celda y enemigos para resolver correctamente referencias entre salas.

**Verificacion:**
- `mvn -q "-Dtest=DungeonConfigLoaderTest" test`: correcto.
- `mvn -q test`: correcto, 490 tests, 0 fallos, 0 errores, 0 omitidos.
- `rg "import java\\.util" src tests -n`: sin resultados.
- `git diff --check`: correcto, solo avisos esperados LF/CRLF.

**Decisiones tecnicas:**
- La ruta oficial de partida nueva y reconstruccion desde guardado usa `config/configuracion_inicial_valdris.json`.
- El objetivo global de ruta se lee desde `Dungeon.getIdSalaObjetivo()` con fallback conservador a `S5-D`.

**Commit sugerido:** `git commit -m "feat(config): load initial dungeon from json"`

---

### Sesion 48 — 27 mayo 2026 — Codex

**Objetivo:** Ajustar la configuracion inicial JSON para conservar estructura fija del mapa y recuperar variacion jugable controlada.

**Archivos trabajados:**
- `config/configuracion_inicial_valdris.json`
- `src/Valdris/persistence/GameConfig.java`
- `src/Valdris/persistence/DungeonConfigLoader.java`
- `tests/Valdris/persistence/DungeonConfigLoaderTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- La configuracion JSON deja de ser una foto completamente fija para enemigos normales, drops, items de suelo y puzzles.
- Añadida seccion `randomization` para declarar que posiciones de enemigos normales, drops normales, secuencias de puzzle e items de suelo se resuelven al cargar.
- Añadida seccion `spawnCandidates` con casillas validas por sala para recolocar enemigos normales sin salir del mapa ni bloquear accesos.
- Declaradas paredes interiores en layouts de salas principales, manteniendo puertas, escaleras, cofres, triggers, palancas y runas alcanzables.
- Los mini-bosses y el combate final conservan posiciones fijas por ser hitos narrativos y de progreso.
- `GameConfig` admite pools de item de suelo, valores permutables de puzzle, reglas de aleatoriedad y candidatos de aparicion.
- `DungeonConfigLoader` elige item de suelo desde `itemPool`, permuta `sequenceValues`, coloca enemigos normales en candidatos libres y asigna drops normales con `ItemGenerator.crearDropEnemigo(...)`.
- `DungeonConfigLoaderTest` valida secuencias como permutacion, drops normales por pool de tipo, posiciones de enemigo sin duplicados y alcanzabilidad estructural tras las paredes interiores.

**Problemas encontrados:**
- La primera version del JSON conectado era demasiado determinista y reducia variacion jugable que ya existia en `DungeonGenerator`.
- Un primer transformador de PowerShell reescribio el JSON con codificacion de consola incorrecta; se rehizo desde la version confirmada usando lectura/escritura UTF-8 y se verifico que no quedaran cadenas corruptas.

**Solucion aplicada:**
- Mantener en JSON lo que exige el enunciado como configuracion inicial auditable: grafo, salas, matriz de celdas, accesos, cofres, paredes interiores, posicion inicial y objetivo.
- Dejar la aleatoriedad dentro de limites declarados por el propio JSON: candidatos de sala, pools de items y valores de puzzle.

**Verificacion:**
- `mvn -q "-Dtest=DungeonConfigLoaderTest" test`: correcto.
- `mvn -q test`: correcto, 491 tests, 0 fallos, 0 errores, 0 omitidos.
- `rg "import java\\.util" src tests -n`: sin resultados.
- `git diff --check`: correcto, solo avisos esperados LF/CRLF.

**Decisiones tecnicas:**
- La configuracion inicial oficial es hibrida: estructura fija y variacion controlada por JSON.
- Los enemigos normales mantienen cantidad y tipo por sala, pero no coordenada exacta.
- Los drops narrativos de mini-bosses siguen fijos; los drops de enemigos normales se generan por tipo.

**Commit sugerido:** `git commit -m "feat(config): randomize json dungeon setup"`

---

### Sesion 49 — 27 mayo 2026 — Codex

**Objetivo:** Mejorar la lectura de secretos, reiniciar correctamente los turnos al cambiar de sala y registrar causas concretas de derrota.

**Archivos trabajados:**
- `src/Valdris/logic/turn/TurnManager.java`
- `src/Valdris/ui/view/GameView.java`
- `tests/Valdris/logic/turn/TurnManagerTest.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Las celdas de suelo con trigger secreto se muestran de forma diferenciada en el mapa con color propio y marcador `?`.
- Al pisar un trigger secreto, `TurnManager` activa el pasadizo y revela la puerta oculta asociada mediante el `triggerId`.
- Las puertas secretas ya reveladas se renderizan con color verde y marcador `D`, distinguiendose de paredes y puertas normales.
- `changeRoom(...)` limpia acciones usadas del jugador y reinicia la fase a `MOVEMENT` al entrar en una nueva sala.
- `usarAccesoAdyacente()` deja de sobrescribir la fase a `USE_ITEM` cuando el acceso cambia realmente de sala.
- Las derrotas por ataque enemigo se cierran inmediatamente con el actor o habilidad responsable.
- Las derrotas por `BURN` y por fallo de puzzle registran esa causa concreta en `defeatReason`.
- Al abrir un cofre, el log muestra un texto narrativo con el nombre e id del item encontrado antes de enviarlo al inventario.
- `TurnManagerTest` cubre revelado de puerta secreta, reinicio de fase/acciones al cambiar de sala, causas de derrota por ataque enemigo y `BURN`, y mensaje de botin de cofre.

**Problemas encontrados:**
- Los triggers secretos ya existian en logica, pero se veian como suelo normal y la puerta oculta no cambiaba visualmente al activarse.
- La muerte por ataque enemigo podia detectarse tarde, despues del procesamiento de efectos del jugador, y por eso terminaba mostrando "efectos de estado".
- El acceso entre salas llamaba a `changeRoom(...)`, pero despues la accion de acceso volvia a dejar la fase en `USE_ITEM`.

**Solucion aplicada:**
- Reutilizar `triggerId` y `Room.getSecretTarget(...)` para que la UI identifique secretos sin duplicar metadatos.
- Revelar el acceso oculto con `Room.openAccessByTrigger(...)` en el mismo momento en que se activa el pasadizo.
- Centralizar el reseteo de fase y acciones dentro de `changeRoom(...)`.
- Comprobar derrota justo despues del resultado de combate enemigo y construir el motivo desde `AIActionResult` o `EffectProcessingResult`.

**Verificacion:**
- `mvn -q "-Dtest=TurnManagerTest" test`: correcto.
- `mvn -q test`: correcto, 493 tests, 0 fallos, 0 errores, 0 omitidos.
- `rg "import java\\.util" src tests -n`: sin resultados.
- `git diff --check`: correcto, solo avisos esperados LF/CRLF.

**Decisiones tecnicas:**
- Los secretos usan una pista visual sutil antes de activarse y una puerta claramente visible despues.
- Entrar en cualquier sala nueva reinicia el ciclo de turno en movimiento.
- La pantalla final debe recibir la causa concreta de muerte siempre que la logica pueda conocerla.

**Commit sugerido:** `git commit -m "fix(gameplay): reveal secrets and reset room turns"`

---

### Sesion 50 — 27 mayo 2026 — Codex

**Objetivo:** Mejorar la presentacion inicial del juego con una introduccion narrativa, una transicion de descenso y retratos para la seleccion de personaje.

**Archivos trabajados:**
- `src/Valdris/ui/view/MainMenuView.java`
- `src/Valdris/ui/view/StoryIntroView.java`
- `src/Valdris/ui/view/CharacterSelectView.java`
- `src/Valdris/ui/view/DescentIntroView.java`
- `imagenes/Kael sin fondo.png`
- `imagenes/Syra sin fondo.png`
- `imagenes/Dorath sin fondo.png`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- `MainMenuView` deja de abrir directamente la seleccion de personaje al iniciar partida nueva y muestra primero `StoryIntroView`.
- Creada `StoryIntroView` como pantalla narrativa sobre Valdris, Malachar, el sello debilitado y el descenso al Nucleo Profundo.
- `CharacterSelectView` incorpora retratos de Kael, Syra y Dorath desde la carpeta `imagenes/` y mejora las tarjetas visuales manteniendo rol, stats y descripcion.
- La seleccion de personaje abre ahora `DescentIntroView` en lugar de crear la partida inmediatamente.
- Creada `DescentIntroView` con texto personalizado por personaje, aviso integrado de movimientos/acciones/teclas visibles y boton final para entrar en Valdris.
- La creacion de `GameModel`, `GameController` y `GameView` queda retrasada hasta confirmar la entrada desde la pantalla de descenso.

**Problemas encontrados:**
- El fichero indicado inicialmente como `GUIA_DISENO_PROJECTO_V3.md` no existia en la raiz; se confirmo que la referencia correcta era `docs/GUIA_PROYECTO_JUEGO_V3.md`.
- Los retratos estaban en `imagenes/` como archivos no versionados, por lo que deben incluirse en el commit de este bloque para que la seleccion funcione en otros entornos.
- Los primeros intentos de Maven dentro del sandbox no llegaron a arrancar por un problema de ejecucion del entorno.

**Solucion aplicada:**
- Usar `docs/GUIA_PROYECTO_JUEGO_V3.md` para adaptar el tono narrativo de las pantallas.
- Cargar las imagenes con rutas relativas mediante `File.toURI()` para soportar espacios en los nombres de archivo.
- Ejecutar la suite completa fuera del sandbox tras el fallo de arranque local.

**Verificacion:**
- `mvn -q test`: correcto, 493 tests, 0 fallos, 0 errores, 0 omitidos.
- La salida mantiene el aviso esperado del test que intenta leer `archivo_que_no_existe.json` y warnings del JDK/Maven.

**Decisiones tecnicas:**
- El flujo de partida nueva queda como `menu -> historia -> seleccion -> descenso -> partida`.
- Las pantallas narrativas pertenecen solo a JavaFX y no modifican reglas ni estado de juego.
- Las imagenes de personaje se tratan como assets de interfaz y deben versionarse junto al cambio visual.

**Commit sugerido:** `git commit -m "feat(ui): add narrative start screens and portraits"`

---

### Sesion 51 — 27 mayo 2026 — Codex

**Objetivo:** Reducir la sensacion plana de la interfaz con una decoracion sutil y coherente con la ambientacion de Valdris.

**Archivos trabajados:**
- `src/Valdris/ui/view/ValdrisTheme.java`
- `src/Valdris/ui/view/MainMenuView.java`
- `src/Valdris/ui/view/StoryIntroView.java`
- `src/Valdris/ui/view/CharacterSelectView.java`
- `src/Valdris/ui/view/DescentIntroView.java`
- `src/Valdris/ui/view/GameView.java`
- `src/Valdris/ui/view/InventoryView.java`
- `src/Valdris/ui/view/FinalView.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Creado `ValdrisTheme` como helper visual compartido para fondo profundo, paneles, botones, separadores, ornamentos horizontales y marcos con esquinas.
- El menu principal usa ahora un panel destacado enmarcado y una ornamentacion central bajo el subtitulo.
- Las pantallas de historia y descenso incorporan panel destacado, esquinas ornamentales y separadores decorativos sin aumentar el texto.
- La seleccion de personaje conserva retratos y tarjetas, pero refuerza sombras, fondos y marcos para integrarlas mejor en el tono de fantasia oscura.
- `GameView` cambia el fondo plano por un fondo profundo, enmarca los paneles laterales, destaca el mapa y sustituye separadores de texto por lineas decorativas.
- `InventoryView` y `FinalView` reciben el mismo tema visual para que modales y cierre no rompan la estetica general.

**Problemas encontrados:**
- Maven no arranco dentro del sandbox por el mismo problema de ejecucion del entorno visto en el bloque anterior.
- La mejora debia mantenerse puramente visual para no mezclar reglas de juego con presentacion.

**Solucion aplicada:**
- Centralizar estilos en `ValdrisTheme` y aplicarlos desde cada vista sin cambiar controladores ni modelo.
- Ejecutar la verificacion completa fuera del sandbox tras el fallo de arranque local.

**Verificacion:**
- `mvn -q test`: correcto, 493 tests, 0 fallos, 0 errores, 0 omitidos.
- La salida mantiene el aviso esperado del test que intenta leer `archivo_que_no_existe.json` y warnings del JDK/Maven.

**Decisiones tecnicas:**
- La decoracion comun se implementa con JavaFX/CSS y regiones ligeras, sin introducir nuevos assets obligatorios.
- `ValdrisTheme` queda como punto unico para estilos compartidos de pantallas JavaFX.
- La pantalla principal mejora marco y jerarquia visual sin alterar el mapa ni sus colores funcionales.

**Commit sugerido:** `git commit -m "style(ui): add subtle fantasy theme"`

---

### Sesion 52 — 27 mayo 2026 — Codex

**Objetivo:** Diferenciar visualmente las cinco zonas del mapa y mostrar transiciones suaves en los pasillos entre zonas.

**Archivos trabajados:**
- `src/Valdris/ui/view/ValdrisTheme.java`
- `src/Valdris/ui/view/GameView.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Añadidos colores ambientales para Zona 1, Zona 2, Zona 3, Zona 4 y Zona 5, con el Núcleo en azul profundo.
- Añadido cálculo de matiz por sala usando los prefijos `S1-`, `S2-`, `S3-`, `S4-` y `S5-`.
- Añadido degradado horizontal para `PASILLO_1_2`, `PASILLO_2_3`, `PASILLO_3_4`, `PASILLO_4_5` y `PASILLO_FINAL`.
- Aplicado el matiz sobre los colores funcionales ya existentes de paredes, suelo, puertas, escaleras, trampas, runas, palancas y secretos.
- Ajustado el título de la sala para que use un acento coherente con la zona actual.

**Verificación:**
- `mvn test` ejecutado correctamente.
- Resultado: 493 tests, 0 failures, 0 errors, 0 skipped.
- `rg "import java\.util" src tests -n` sin resultados.

**Decisiones:**
- El cambio queda limitado a JavaFX y no altera reglas de movimiento, acceso, combate, puzzles ni persistencia.
- Los pasillos usan el eje más largo para el degradado, por lo que los pasillos este-oeste actuales transicionan de izquierda a derecha.
- La Zona 5 usa azul profundo para reforzar la identidad del Núcleo.

**Commit sugerido:** `git commit -m "style(ui): tint map zones and transition corridors"`

---

### Sesion 53 — 27 mayo 2026 — Codex

**Objetivo:** Hacer que los tintes de zona sean bastante mas perceptibles manteniendo la lectura clara de celdas y elementos interactivos.

**Archivos trabajados:**
- `src/Valdris/ui/view/ValdrisTheme.java`
- `src/Valdris/ui/view/GameView.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Reforzados los colores base de Zona 2, Zona 3, Zona 4 y Zona 5 para que el contraste ambiental sea mas evidente.
- Aumentada la intensidad de mezcla del suelo y trampas, que son las celdas donde mejor debe percibirse la identidad de cada zona.
- Aumentada con mas moderacion la mezcla en paredes, puertas, secretos, escaleras, palancas y runas para conservar su lectura funcional.

**Verificacion:**
- `mvn test` ejecutado correctamente.
- Resultado: 493 tests, 0 failures, 0 errors, 0 skipped.
- `rg "import java\.util" src tests -n` sin resultados.

**Decision:**
- La identidad de zona debe notarse claramente en el tablero, pero los colores funcionales de interaccion siguen teniendo prioridad visual.

**Commit sugerido:** `git commit -m "style(ui): strengthen map zone colors"`

---

### Sesion 54 — 27 mayo 2026 — Codex

**Objetivo:** Mejorar la representacion visual del contenido del tablero para diferenciar con claridad enemigos, items, cofres y elementos interactivos.

**Archivos trabajados:**
- `src/Valdris/ui/view/GameView.java`
- `TASKS.md`
- `COMMIT_LOG.md`

**Cambios realizados:**
- Sustituidas las letras planas de cofres, items, secretos, puertas secretas, palancas, runas y escaleras por marcas 2D con forma y color propios.
- Añadidos sprites compactos para items segun categoria: arma, armadura, escudo, pocion, accesorio y objeto narrativo.
- Añadido cofre visual con estado cerrado/abierto para que no dependa solo de la letra `C`.
- Añadidas siluetas diferenciadas para familias de enemigos: arqueros/francotiradores, guardianes/constructos, magos/invocadores, berserkers y sombras.
- Añadidas etiquetas cortas sobre enemigos e items cuando ayudan a distinguir tipos sin tapar la celda.
- Eliminado el renderizado textual antiguo de contenido de celda dentro de `GameView`.

**Verificacion:**
- `mvn test` ejecutado correctamente.
- Resultado: 493 tests, 0 failures, 0 errors, 0 skipped.
- `rg "import java\.util" src tests -n` sin resultados.

**Decision:**
- El tablero usa sprites JavaFX sencillos y explicables, manteniendo estilo 2D de fantasia sin introducir assets nuevos para cada elemento.

**Commit sugerido:** `git commit -m "style(ui): improve map entity sprites"`

---

### Sesión 55 — Diálogos narrativos temáticos y textos ampliados

**Objetivo:**
Sustituir las ventanas genéricas de diálogo por una presentación coherente con la estética de Valdris y ampliar los textos narrativos de personaje, Malachar y desenlace final.

**Cambios realizados:**
- Añadido `ValdrisTheme.mostrarDialogoNarrativo(...)` como modal JavaFX propio con fondo profundo, panel destacado, ornamento, separador y botón temático.
- Sustituido el `Alert` de diálogos pendientes de `GameView` por el nuevo modal narrativo.
- Ajustada `FinalView` para mostrar el desenlace con más espacio, subtítulo narrativo, separador central y avisos temáticos al exportar resumen.
- Ampliados los diálogos por personaje de la configuración inicial JSON y del `DungeonGenerator` de respaldo.
- Ampliado el diálogo de Malachar antes del combate final y los tres textos de desenlace por personaje.

**Verificación:**
- `mvn test` ejecutado correctamente.
- Resultado: 493 tests, 0 failures, 0 errors, 0 skipped.
- `rg "import java\.util" src tests -n` sin resultados.
- `git diff --check` sin errores; solo avisos esperados de CRLF.

**Decisión:**
- Los diálogos narrativos importantes no usan `Alert` genérico; se muestran con un modal común de `ValdrisTheme` para conservar una estética consistente.

**Commit sugerido:** `git commit -m "style(ui): improve narrative dialogs"`

---

### Sesión 56 — Armas iniciales, cofres de elección y balance V5

**Objetivo:**
Introducir armas reales en la progresión jugable: arma inicial por personaje, cofres secretos con elección de recompensa y ajuste de daños a los valores finales de la guía V5.

**Cambios realizados:**
- Añadida apertura selectiva en `Container` y `TurnManager` para cofres con varias recompensas, sin consumir la fase si falta elección.
- Declaradas en JSON y en `DungeonGenerator` las opciones de armas de `S1-SEC`, `S2-SEC`, `S3-SEC` y `S4-SEC`.
- Añadido modal temático `ValdrisTheme.mostrarEleccionRecompensa(...)` y conexión desde `GameController`/`GameView` para elegir entre 2 o 3 armas.
- Colocada el arma inicial W1/W2/W3 delante del jugador al crear partida nueva según Kael, Syra o Dorath.
- Restaurado el balance final V5: Kael 18, Syra 12, Dorath 14, Syra movimiento 5 y armas W1-W12 sin el aumento provisional de daño.
- Actualizados tests de contenedores, turnos, generadores, configuración JSON, combate, pociones y jugador.

**Verificación:**
- `mvn test` ejecutado correctamente.
- Resultado: 496 tests, 0 failures, 0 errors, 0 skipped.
- `rg "import java\.util" src tests -n` sin resultados.
- `git diff --check` sin errores; solo avisos esperados de CRLF.

**Decisiones:**
- Las armas iniciales se colocan dinámicamente al iniciar partida porque dependen del personaje elegido.
- Los cofres secretos contienen varias opciones declaradas, pero solo entregan una recompensa seleccionada por el jugador.
- El balance provisional de daño queda sustituido por los valores finales de `GUIA_DISENO_V5_FINAL.md`.

**Commit sugerido:** `git commit -m "feat(items): add weapon choices and v5 balance"`

---

### Sesión 57 — Ajuste de dificultad inicial

**Objetivo:**
Suavizar las primeras salas sin cambiar la estructura de armas, cofres ni progresión ya implementada.

**Cambios realizados:**
- Aumentado en +5 el daño base de Kael, Syra y Dorath.
- Aumentado en +5 el daño base de todas las armas oficiales W1-W12.
- Reducido `WARRIOR` normal a ataque 12 y defensa 5.
- Ajustado `GUARDIAN` normal a ataque 12 y defensa 15.
- Actualizados textos visibles de selección de personaje y documentación de enums para reflejar las nuevas estadísticas.
- Actualizados tests de jugador, enemigo, unidad base, combate, pociones e `ItemGenerator`.

**Verificación:**
- `mvn test` ejecutado correctamente.
- Resultado: 496 tests, 0 failures, 0 errors, 0 skipped.

**Decisión:**
- Se aplica un ajuste jugable sobre V5 para que el arranque sea menos punitivo: más daño general del jugador y enemigos iniciales menos letales.

**Commit sugerido:** `git commit -m "balance: soften early combat difficulty"`

---

### Sesión 58 — Corrección ortográfica de pantallas y mensajes visibles

**Objetivo:**
Corregir textos de interfaz donde faltaban tildes, eñes o grafías españolas en pantallas narrativas y mensajes visibles.

**Cambios realizados:**
- Corregidos textos de `StoryIntroView`: Núcleo, años, corazón, mágicas, última y el botón de elección de personaje.
- Corregidos textos de `DescentIntroView` y `CharacterSelectView` relacionados con el Núcleo Profundo, descenso único y frase de Syra.
- Corregido el modal de recompensa de `ValdrisTheme`: botín, responderá y etiqueta visible de daño.
- Corregido el mensaje de ruta en `GameView` y el aviso de celda válida en `GameController`.
- Ajustados mensajes de error de configuración inicial en `DungeonConfigLoader` y la descripción JSON de entrada al núcleo.

**Verificación:**
- `mvn test` ejecutado correctamente.
- Resultado: 496 tests, 0 failures, 0 errors, 0 skipped.
- `rg "import java\.util" src tests -n` sin resultados.

**Decisión:**
- Se corrigen solo textos visibles o mensajes de error; se conservan nombres internos como `danio`, `getDanoBase` o claves de log para no romper API ni persistencia.

**Commit sugerido:** `git commit -m "fix(ui): correct spanish text accents"`

---

### Sesión 59 — Corrección de duración de BLIND

**Objetivo:**
Evitar que `BLIND` pueda quedarse activo de forma indefinida o consumir duración antes de que el jugador tenga oportunidad real de jugar su turno.

**Cambios realizados:**
- Movido el procesamiento de efectos del jugador en `TurnManager` al cierre del turno completo del jugador, antes de resolver las acciones enemigas.
- Eliminado el consumo inmediato de efectos al final del turno enemigo, para que un `BLIND` recién aplicado conserve sus turnos visibles.
- Ajustada la IA de `CONTROLLER` para elegir un efecto no activo siempre que existan alternativas entre `SLOW`, `BLIND` y `CURSE`.
- Añadidos tests para comprobar que `BLIND` expira tras sus turnos y que `CONTROLLER` no refresca `BLIND` si puede aplicar otro efecto.

**Verificación:**
- `mvn -q "-Dtest=TurnManagerTest,IAEnemigoTest" test`: correcto.
- `mvn test` ejecutado correctamente.
- Resultado: 498 tests, 0 failures, 0 errors, 0 skipped.
- `rg "import java\.util" src tests -n` sin resultados.
- `git diff --check` sin errores; solo avisos esperados de CRLF.

**Decisión:**
- Los efectos aplicados por enemigos durante su turno empiezan a contar después de que el jugador haya tenido un turno útil bajo ese estado.

**Commit sugerido:** `git commit -m "fix(turns): expire blind reliably"`

---

### Sesión 60 — Sprites diferenciados de mini-bosses y Parásito

**Objetivo:**
Hacer que los mini-bosses narrativos y el Parásito final se distingan claramente de enemigos normales que comparten su tipo de IA base.

**Cambios realizados:**
- Añadido render específico en `GameView` para `MiniBossEnemy` antes de usar la silueta normal por `EnemyType`.
- Creado sprite propio para el Alcalde Corrupto con aura, capa, corona y bastón para que no se confunda con un `WARRIOR`.
- Añadidas siluetas diferenciadas para Espíritu Madre, Golem, Guardián Sin Nombre y El Filtro.
- Creado sprite propio para `ParasitoEnemy` con aura, núcleo, tentáculos y etiqueta de fase.
- Mantenido el cambio en la capa JavaFX sin alterar estadísticas, IA, combate ni persistencia.

**Verificación:**
- `mvn test` ejecutado correctamente.
- Resultado: 498 tests, 0 failures, 0 errors, 0 skipped.

**Decisión:**
- Los mini-bosses se diferencian visualmente por clase concreta (`MiniBossEnemy`) y no por `EnemyType`, porque varios reutilizan IA de enemigos normales.

**Commit sugerido:** `git commit -m "style(ui): distinguish boss sprites"`

---

### Sesión 61 — Sprite visible de Malachar

**Objetivo:**
Corregir que Malachar no aparecía en el mapa de la sala final y darle un aspecto visual coherente con su papel de mago aliado.

**Cambios realizados:**
- Añadida detección explícita de `MalacharAlly` en `GameView.agregarContenidoCelda(...)`.
- Creado `crearMarcaMalachar(...)` para mostrar a Malachar con su HP visible igual que otras unidades relevantes.
- Añadido sprite de mago con aura arcana, túnica, sombrero, bastón, cristal y barba.
- Mantenido el cambio limitado a JavaFX, sin tocar reglas de combate final, IA ni persistencia.

**Verificación:**
- `mvn test` ejecutado correctamente.
- Resultado: 498 tests, 0 failures, 0 errors, 0 skipped.

**Decisión:**
- Malachar se renderiza por su clase concreta `MalacharAlly` porque es una unidad aliada, no jugador ni enemigo.

**Commit sugerido:** `git commit -m "fix(ui): render malachar ally sprite"`

---

### Sesión 62 — Fases finales del Parásito y desenlace ampliado

**Objetivo:**
Diferenciar de forma clara las tres fases visuales del Parásito y ampliar el desenlace de victoria para explicar que el protagonista y Malachar mueren por el último ataque del Parásito, mientras Valdris sobrevive.

**Cambios realizados:**
- Sustituido el sprite único del Parásito por tres variantes JavaFX específicas: coraza grande, forma desgarrada intermedia y esencia final más pequeña.
- La tercera fase se representa como una esencia oscura con rasgos de mago corrompido para reforzar la presencia maligna vinculada a Malachar.
- Ampliados los tres textos finales por personaje en `TurnManager.crearEndingText(...)` manteniendo el tono narrativo previo.
- Aclarado en cada desenlace que el Parásito usa sus últimas fuerzas para matar al protagonista y a Malachar antes de extinguirse, pero no consigue destruir Valdris.

**Verificación:**
- `mvn test` ejecutado correctamente.
- Resultado: 498 tests, 0 failures, 0 errors, 0 skipped.
- `rg "import java\.util" src tests -n` sin resultados.
- `git diff --check` sin errores; solo avisos esperados de CRLF.

**Decisión:**
- Las fases del Parásito deben cambiar de tamaño, silueta y motivo visual, no solo de color, para que la evolución del combate sea legible en el tablero.

**Commit sugerido:** `git commit -m "style(ui): distinguish parasite phases"`

---

### Sesión 63 — Resumen final visible

**Objetivo:**
Permitir que el jugador vea el resumen de la partida al terminar antes de decidir si quiere exportarlo a JSON.

**Cambios realizados:**
- Cambiado el botón de la pantalla final de `Exportar resumen` a `Ver resumen`.
- Creada `FinalSummaryView` como ventana modal con resultado, personaje, sala final, turno, HP, desenlace, inventario, salas exploradas y log estructurado.
- Movida la acción `Exportar resumen` a la ventana de resumen, junto a un botón `Salir` que cierra solo el modal.
- Añadido `GameModel.crearResumenFinal()` para construir el `GameSummary` en memoria sin escribir fichero.
- Conservada la exportación JSON existente mediante `GameController.onExportarResumenFinal()`.

**Verificación:**
- `mvn test` ejecutado correctamente.
- Resultado: 498 tests, 0 failures, 0 errors, 0 skipped.
- `rg "import java\.util" src tests -n` sin resultados.
- `git diff --check` sin errores; solo avisos esperados de CRLF.

**Decisión:**
- El resumen final se muestra desde la capa JavaFX usando el mismo `GameSummary` que se exporta, para evitar divergencias entre lo que ve el jugador y lo que queda guardado.

**Commit sugerido:** `git commit -m "feat(ui): show final summary before export"`

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
- [x] GameConfig
- [x] DungeonConfigLoader
- [x] GameState
- [x] LoadedGame
- [x] GameSummary
- [x] LectorJSON

**Bloque 6 — JavaFX**
- [x] MainApp
- [x] ValdrisTheme
- [x] MainMenuView
- [x] StoryIntroView
- [x] GameModelListener
- [x] GameModel
- [x] CharacterSelectView
- [x] DescentIntroView
- [x] GameView
- [x] GameController
- [x] InventoryView
- [x] CombatLogView
- [x] FinalView

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
- [x] DungeonConfigLoaderTest
- [x] MalacharAllyTest
- [x] ParasitoEnemyTest
- [x] TurnManagerFinalBossTest
- [x] LectorJSONFinalBossTest
- [x] MisEstructurasDeDatos.Arbolesbinarios.ArbolBinarioDeBusquedaEnterosTest
- [x] MisEstructurasDeDatos.Arbolesbinarios.ArbolBinarioDeBusquedaEquilibradoTest
- [x] MisEstructurasDeDatos.Arbolesbinarios.ArbolBinarioDeBusquedaTest
- [x] MisEstructurasDeDatos.Arbolesbinarios.NodoTest
- [x] MisEstructurasDeDatos.Arbolesbinarios.MainTest
- [x] MisEstructurasDeDatos.Grafos.AristaTest
- [x] MisEstructurasDeDatos.Grafos.DatosGrafoJsonTest
- [x] MisEstructurasDeDatos.Grafos.GrafoTest
- [x] MisEstructurasDeDatos.Grafos.LectorGrafoJsonTest
- [x] MisEstructurasDeDatos.Grafos.NodoTest
- [x] MisEstructurasDeDatos.Grafos.TripletaJsonTest
- [x] MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazadaTest
- [x] MisEstructurasDeDatos.ListasPilasYColas.ListaCircularTest
- [x] MisEstructurasDeDatos.ListasPilasYColas.ColaTest
- [x] MisEstructurasDeDatos.ListasPilasYColas.PilaTest

---

## Última verificación registrada

```powershell
.\mvnw.cmd -q test
```

Resultado:

```text
498 tests, 0 failures, 0 errors, 0 skipped
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

### Metodología recomendada para el pulido final

- Mantener cambios pequeños y revisables, centrados en errores jugables, claridad de interfaz o documentación de entrega.
- Ejecutar `mvn test` completo antes de cerrar cada bloque de cambios.
- Revisar manualmente guardado, carga, inventario, combate final, derrota y pantalla final cuando se toque JavaFX o persistencia.
- Mantener sincronizados `TASKS.md` y `COMMIT_LOG.md` al terminar cada tarea relevante.
- Conservar `partida_valdris.json` y `resumen_valdris.json` como ficheros versionados de entrega para facilitar la revisión de persistencia y resumen final.
- Hacer commit solo tras revisión del equipo y con un mensaje descriptivo.

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
- [24 mayo 2026] Se decide que `CURSE` no aplica daño periódico: suma +3 a todo daño enemigo
  recibido mientras está activo, sin afectar daño ambiental como puzzles. `BURN` mantiene el
  daño periódico al procesar efectos.
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
- [26 mayo 2026] Se decide conservar versionados `partida_valdris.json` y `resumen_valdris.json`
  como ejemplos de entrega para guardado, carga y exportación de resumen final.
