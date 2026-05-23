# Guía de Proyecto v3.0
## Valdris: El Núcleo Profundo
*Juego por Turnos · JavaFX · Estructuras de Datos Propias*

Asignatura: Metodología de la Programación + Estructuras de Datos  
Grupo H12GEXTRA — Marcos Castro · Ventura Pacheco · Marino Rodríguez  
Versión v3.0 — Final único, Fragmento de Voluntad, pasadizo Zona 4, desenlaces por personaje  
Lenguaje: Java 21 · JavaFX · Gson 2.10.1 · IntelliJ IDEA

**Cambios respecto a v2.0:**
- Final único `ENDING_SACRIFICE`: el jugador y Malachar combaten juntos contra el parásito. Ambos mueren. Valdris vive.
- Desenlace personal distinto según el personaje elegido (Kael, Syra, Dorath) con texto ampliado.
- Item de Zona 4 renombrado a **Fragmento de Voluntad** — potencia a los tres personajes con efectos distintos.
- Pasadizo secreto de Zona 4 rediseñado: **La Celda de Dorath** (Opción C) con textos originales del sellado.
- Zona 4 ahora tiene diálogos extra para los tres personajes (Kael ya los tenía, añadidos Syra y Dorath).

---

## 1. El Mundo — Valdris

Valdris es un continente de fantasía clásica dividido en cinco zonas con ecosistemas, criaturas y ambientaciones distintas. Hace trescientos años, los cinco reinos de Valdris unieron fuerzas para sellar a Malachar — un archimago que había descubierto cómo absorber la esencia vital de las criaturas mágicas para volverse inmortal — dentro de una fortaleza subterránea construida bajo el corazón del continente: El Núcleo Profundo.

Cinco guardianes, uno por reino, fueron encargados de mantener el sello activo con su propia vida. Trescientos años después, los guardianes han muerto de viejos. El sello se debilita. Las criaturas mágicas de cada zona empiezan a enloquecerse porque algo las está absorbiendo desde abajo. Los cinco reinos no se ponen de acuerdo en qué hacer. Tres personas deciden actuar por su cuenta.

### La verdad sobre Malachar
*Lo que los reinos ocultaron*

- Malachar no buscaba el poder por ambición. Descubrió que Valdris tiene un **parásito**: una entidad exterior al mundo que se alimenta lentamente de toda vida mágica.
- Su cálculo: en dos o tres generaciones el parásito lo habría consumido todo.
- Su método: concentrar toda la magia del mundo en un ser inmortal que pudiera resistir al parásito cuando llegara.
- El problema: hacerlo mataba a miles de criaturas inocentes. Los reinos lo sellaron sin escucharle.
- Ahora el parásito sigue ahí. Más cerca que nunca. Y el sello era lo único que lo frenaba.

### El final — ENDING_SACRIFICE

El jugador llega al Núcleo Profundo esperando una batalla de boss y encuentra a un anciano agotado que lleva trescientos años solo. No hay combate inicial contra Malachar. Hay una conversación. Malachar explica la verdad. Y entonces el parásito, sintiendo que el sello está a punto de romperse definitivamente, se manifiesta.

> *"Trescientos años esperando que alguien llegara lo suficientemente lejos como para escuchar. Y habéis llegado justo a tiempo para ver lo que viene."*

La batalla final no es contra Malachar. Es **junto a él**. El anciano canaliza toda la esencia que ha acumulado durante tres siglos — el mismo poder que los reinos consideraron monstruoso — y lo une al del jugador para enfrentarse al parásito. Los dos saben que no van a sobrevivir. Los dos lo aceptan. Esa es la redención de Malachar: no pedir perdón, sino usar lo que hizo para salvar lo que intentaba proteger.

### Desenlaces por personaje

La batalla y el sacrificio son iguales para los tres personajes. Lo que cambia es el texto de cierre — lo que Valdris recuerda de cada uno. Técnicamente es un bloque de texto condicional según `Player.characterType`. Sin complejidad añadida.

---

#### Desenlace de Kael — El Portador de la Llama Rota

En el último instante, cuando la energía del parásito los envuelve a los dos, el guantelete de hierro de Kael se derrite. No de calor — de alivio. La mano que el sello rechazó trescientos años antes, la mano quemada hasta el hueso que había cubierto durante años porque no soportaba mirarla, se abre por primera vez sin dolor. Kael la mira. La cierra. Y sonríe. No murió cargando una deuda. Murió saldándola. En Embrath, donde había fallado, pusieron su nombre en la lista de guardianes. El último. El que terminó lo que los demás no pudieron.

---

#### Desenlace de Syra — La Voz Sin Eco

Syra no cerró los ojos. Los mantuvo abiertos hasta el final, mirando hacia arriba, hacia donde sabía que estaba la superficie. Sus últimas palabras no fueron para Malachar ni para el jugador. Fueron los nombres de las criaturas de Lireth — cada uno pronunciado despacio, con la cadencia antigua que su clan usaba para los rituales de recuerdo. El espíritu madre. Los arqueros. Los árboles que crecían hacia abajo. Los nombraba para que alguien, aunque fuera el vacío, los oyera una última vez antes de que empezaran a sanar. Tres días después de que el parásito desapareciera, en el bosque de Lireth, un árbol creció en la dirección correcta por primera vez en años. Nadie lo vio. Pero ocurrió.

---

#### Desenlace de Dorath — El Excomulgado

Dorath encontró la verdad. Eso era lo único que había ido a buscar, y la encontró. En los últimos segundos, mientras la energía los consumía a los dos, pensó en la Orden. En los archiveros que habían pasado décadas borrando registros, sellando cámaras, asegurándose de que nadie supiera que el sello siempre fue temporal. Pensó en ellos y sintió, con absoluta claridad, que ya no le importaban. Había algo más grande que tener razón. Los textos que encontró en la Torre de Embrath sobrevivieron. Alguien los encontraría. Alguien los leería. Y cuando los reinos preguntaran qué había pasado en el Núcleo Profundo, habría palabras escritas por un excomulgado que lo explicaban todo. La Orden intentó quemar su nombre de los registros. Pero el nombre ya estaba en demasiados sitios.

---

## 2. Los Tres Personajes Jugables

Los tres personajes comparten el mismo objetivo pero llegan a él desde ángulos distintos. Sus stats reflejan su historia. El jugador elige uno al inicio y juega toda la partida con él. Los otros dos aparecen mencionados en el lore de las zonas — existen en el mundo aunque no sean el personaje elegido.

---

### Kael — El Portador de la Llama Rota

Aprendiz del último guardián del sello del reino de Embrath. Cuando su maestro murió, Kael intentó asumir el rol pero no tenía el linaje necesario — el sello lo rechazó y le quemó la mano derecha hasta el hueso. Ahora lleva un guantelete de hierro sobre esa mano y una deuda que no sabe cómo saldar. Va al Núcleo Profundo porque es lo único que le quedaba que hacer. No es exactamente un guerrero: es un espadachín con magia residual quemada en el cuerpo. Sus ataques cuerpo a cuerpo pueden activar descargas de energía ígnea que no controla del todo. Tiene diálogos adicionales en la Zona 4 (Torre de Embrath), lugar donde entrenó con su maestro.

| Stat | Valor | Nota |
|------|-------|------|
| HP | 110 | El más resistente de los tres |
| Daño base | 18 | El más alto de los tres |
| Mov. points | 3 | Movilidad media |
| Rango ataque | 1 | Solo cuerpo a cuerpo |
| Habilidad | Descarga Ígnea | 30% de prob. de infligir quemadura al atacar. Con Fragmento de Voluntad: sube al 50%. |

---

### Syra — La Voz Sin Eco

Elfa de los bosques de Lireth cuyo clan fue el primero en notar el enloquecimiento de los animales mágicos. Pasó dos años rastreando el origen antes de entender que venía de abajo. Es la más informada de los tres: tiene mapas parciales del Núcleo Profundo y sabe qué esperar. Lo que no tiene es fuerza para hacerlo sola. No es exactamente una arquera: es una rastreadora que usa flechas encantadas cargadas con efectos según los materiales recogidos en cada zona. Tiene diálogos adicionales en la Zona 2 (Bosque de Lireth), su hogar, y en la Zona 4 al ver los textos del sellado.

| Stat | Valor | Nota |
|------|-------|------|
| HP | 75 | La más frágil de los tres |
| Daño base | 12 | Bajo sin encantamiento activo |
| Mov. points | 5 | La más rápida del mapa |
| Rango ataque | 3 | Ataca desde lejos |
| Habilidad | Flecha Encantada | El efecto varía según item equipado (fuego, hielo, parálisis). Con Fragmento de Voluntad: +5 daño base además del efecto. |

---

### Dorath — El Excomulgado

El mejor clérigo de la Orden de los Cinco Sellos — la orden religiosa creada para venerar a los guardianes. Lo excomulgaron cuando investigó los textos originales del sellado y encontró inconsistencias: la Orden sabía desde el principio que el sello era temporal y lo ocultó. Va al Núcleo Profundo porque quiere la verdad, y porque la Orden le quitó todo lo demás. No es exactamente un mago: es un clérigo caído con acceso a magia sagrada y oscura a la vez. Tiene diálogos adicionales en la Zona 4 al descubrir La Celda de Dorath — una sala oculta con los textos originales que desencadenaron su excomulgación.

| Stat | Valor | Nota |
|------|-------|------|
| HP | 80 | Frágil en combate directo |
| Daño base | 14 | Mágico, ignora armadura física |
| Mov. points | 2 | El más lento de los tres |
| Rango ataque | 4 | Mayor rango de ataque |
| Habilidad | Palabra Dual | Alterna cura (turno impar) y maldición (turno par). Con Fragmento de Voluntad: ambos efectos simultáneos. |

---

## 3. Las Cinco Zonas de Valdris

El mapa se divide en cinco zonas que el jugador recorre en orden. Cada zona tiene su propia ambientación, enemigos temáticos, un acertijo o mecánica de bloqueo obligatoria para avanzar, y al menos un item de zona único. Las zonas 1-4 culminan con un enemigo élite (mini-boss) que suelta el item necesario para entrar a la siguiente zona.

---

### Zona 1 — Los Campos Grises

Llanuras muertas y aldeas abandonadas. Primera zona, tutorial implícito. La tierra está reseca, los pozos envenenados. Los aldeanos que no huyeron han sido corrompidos por la absorción mágica del parásito y atacan sin reconocer a nadie.

| | |
|--|--|
| **Enemigos** | Aldeanos corrompidos (Warrior), Lobos enloquecidos (Archer rápido), Espectro de campo (Mage débil) |
| **Acertijo** | Puente roto sobre el río gris: cuatro palancas en las orillas, solo una secuencia correcta lo reconstruye. Orden erróneo → trampa de flechas que daña al jugador. |
| **Mini-boss** | El Alcalde Corrompido — Warrior de alto HP que al morir suelta la **Llave de Hierro**. |
| **Item de zona** | **Llave de Hierro** — abre la puerta norte hacia el Bosque de Lireth. |
| **Pasadizo secreto** | Detrás del molino: celda DOOR_HIDDEN que se activa al empujar una piedra específica. Lleva a almacén con Poción de Fuerza. |

---

### Zona 2 — El Bosque de Lireth

Bosque mágico antiguo en proceso de corrupción. Los árboles crecen retorcidos hacia abajo, la luz es verde y enfermiza. Los espíritus del bosque que antes protegían a los elfos ahora los atacan. Syra tiene diálogos adicionales en toda esta zona.

| | |
|--|--|
| **Enemigos** | Espíritus de árbol (Warrior lento, golpe alto), Arqueros elfos poseídos (Archer preciso), Guardián de raíces (Mage que inmoviliza celdas) |
| **Acertijo** | Laberinto vegetal: raíces bloquean rutas. Hay que cortarlas en el orden correcto — el orden incorrecto hace crecer más raíces y bloquea el retroceso. |
| **Mini-boss** | El Espíritu Madre — Mage de alto rango que invoca refuerzos. Al morir suelta la **Semilla Resonante**. |
| **Item de zona** | **Semilla Resonante** — resuena cerca de trampas ocultas en zonas posteriores, actúa como detector pasivo. |
| **Escalera** | Acceso a planta subterránea del bosque (raíces profundas) con cofre oculto y enemigo extra. |

---

### Zona 3 — Las Minas de Karath

Minas enanas excavadas hace siglos, ahora abandonadas. Varios pisos conectados por escaleras y ascensores de vagoneta. La piedra misma está viva — los gólems emergieron solos cuando la absorción mágica despertó los minerales encantados que los enanos dejaron atrás.

| | |
|--|--|
| **Enemigos** | Gólems de piedra (Warrior muy lento, altísima defensa), Murciélagos de cristal (Archer rápido, frágil), Enano espectral (Mage con ataque de área) |
| **Acertijo** | Mecanismo de vagoneta en tres niveles: activar palancas en pisos distintos en el orden correcto para abrir la compuerta inferior. Orden incorrecto → vagoneta aplasta celda bloqueando el paso temporalmente. |
| **Mini-boss** | El Gólem Maestro — Warrior pesado con Pisotón Sísmico de área. Al morir suelta el **Fragmento de Sello**. |
| **Item de zona** | **Fragmento de Sello** — necesario para interactuar con los mecanismos del Núcleo Profundo. Desbloquea diálogos extra con Malachar. |
| **Escaleras** | Tres pisos: Piso -1 (entrada), Piso -2 (minas activas), Piso -3 (cámaras profundas con el mini-boss). |

---

### Zona 4 — La Torre de Embrath

Torre mágica en ruinas donde entrenaban los guardianes del sello. Los constructos mágicos que guardaban la torre siguen activos con sus últimas órdenes: no dejar salir a nadie. Los tres personajes tienen diálogos específicos en esta zona: Kael reconoce el lugar donde entrenó con su maestro; Syra encuentra referencias a Lireth en los archivos de la torre; Dorath descubre La Celda de Dorath en el pasadizo secreto.

| | |
|--|--|
| **Enemigos** | Constructos de energía (Warrior rápido), Guardianes espectrales (Archer de largo rango), Archivero corrompido (Mage que debilita stats) |
| **Acertijo** | Suelo de runas: baldosas con símbolos que hay que pisar en el orden del antiguo juramento de los guardianes. Descifrable con un pergamino encontrado en Zona 1. Orden incorrecto → daño a toda la habitación. |
| **Mini-boss** | El Guardián Sin Nombre — espectro del último guardián, el maestro de Kael. Al morir suelta el **Fragmento de Voluntad**. |
| **Item de zona** | **Fragmento de Voluntad** — potencia la habilidad de cada personaje: Kael (Descarga Ígnea 30%→50%), Syra (+5 daño base además del efecto de flecha), Dorath (Palabra Dual activa ambos efectos simultáneamente). |
| **Pasadizo secreto — La Celda de Dorath** | Una celda sellada con cerradura antigua oculta tras una estantería en la biblioteca de la torre. Dentro hay los textos originales del sellado que Dorath encontró antes de ser excomulgado — los mismos que la Orden intentó destruir. Si Dorath es el personaje: escena completa con texto adicional y reflexión personal. Si es otro personaje: encuentran los textos pero no comprenden su alcance completo. En ambos casos se obtiene un **Pergamino Sellado** que añade un diálogo extra con Malachar en el Núcleo Profundo. |

---

### Zona 5 — El Núcleo Profundo

La fortaleza subterránea donde fue sellado Malachar. No es una mazmorra de combate normal: es un lugar que respira, que recuerda, que ha estado esperando. Las sombras aquí tienen forma propia. Y en el centro hay algo que lleva trescientos años solo.

| | |
|--|--|
| **Enemigos** | Sombras absorbidas (Warrior sin HP fijo — escalan con el parásito), Ecos de magia (Archer que copia el último ataque del jugador), El Filtro (Mage jefe de sala que bloquea el acceso al núcleo) |
| **Sin acertijo clásico** | La última sala no tiene puzzle de mecánica. Tiene una conversación. Malachar habla. El jugador escucha. El parásito se manifiesta. |
| **Boss — Fase 1 (Malachar)** | NO hay combate contra Malachar. Conversación con el anciano. Si el jugador lleva el Pergamino Sellado, hay diálogo adicional sobre los textos. Si lleva el Fragmento de Sello, Malachar reconoce el trabajo del jugador explícitamente. |
| **Boss — Fase 2 (El Parásito)** | Malachar y el jugador combaten juntos contra la entidad. Malachar actúa como aliado NPC con sus propias acciones cada turno. Mecánicamente: Enemy especial con múltiples fases de HP. Al derrotarlo, secuencia de cierre. |
| **Desenlace** | Secuencia no interactiva. Texto de cierre según `Player.characterType` (ver Sección 1). Pantalla final con el nombre del personaje y una frase de Malachar distinta según quién haya llegado. |

---

## 4. Mecánicas Especiales y su Implementación Técnica

| Mecánica | Implementación en código |
|----------|--------------------------|
| Llaves y puertas | Cell de tipo `DOOR_LOCKED`. Al intentar entrar: `TurnManager` comprueba `Player.inventory.contains(keyItem)`. Si no tiene la llave → mensaje de bloqueo. Si tiene → `Cell.type = DOOR`. |
| Pasadizos secretos | Arista oculta en el Grafo. La celda trigger es FLOOR normal. Al pisar: `Room.checkSecretTrigger(row,col)` → `Dungeon.activateHiddenPassage(roomId)` añade la arista al Grafo. |
| Escaleras (pisos) | `NodoGrafo` con id que contiene `'stairs_up'` o `'stairs_down'`. Arista con dirección `'arriba'` o `'abajo'`. `Dungeon.getAdjacentRooms()` las incluye normalmente. |
| Enemigo suelta item | Enemy tiene campo `dropItem: Item` (puede ser null). En `Enemy.onDeath()`: si `dropItem != null` → `room.placeItemOnCell(row, col, dropItem)`. El item queda en la celda hasta que el jugador la pisa. |
| Acertijos de palancas | Room tiene `LSE leverCells` y `int[] correctSequence`. `PuzzleManager.activate(cell)` registra el orden. `PuzzleManager.checkSequence()` compara → abre puerta, activa pasadizo o activa trampa. |
| Acertijo de runas | Igual que palancas pero Cell son tipo RUNE. Se activan al pisarlas. La secuencia correcta viene de un pergamino (`SpecialItem` con `effectData = 'rune_sequence:3,1,4,2'`). |
| Fragmento de Voluntad | `SpecialItem` con `effectData` por personaje. `onEquip()` comprueba `Player.characterType`: KAEL → modifica probabilidad descarga; SYRA → suma 5 a daño base; DORATH → activa flag `dualSimultaneous` en Palabra Dual. |
| Diálogos por personaje | Room tiene `Map characterDialogues`. `TurnManager.onRoomEnter()` comprueba si hay diálogo para el personaje actual y lo muestra en el log / pantalla de diálogo JavaFX. |
| La Celda de Dorath | Celda `DOOR_HIDDEN` en biblioteca de Zona 4. Al activarse muestra texto distinto según `Player.characterType`. En ambos casos añade `PergaminoSellado` al inventario del jugador. |
| Malachar como aliado NPC | Enemy especial `MalacharAlly` con `executeAllyTurn()` que actúa en favor del jugador. `TurnManager` lo inserta en `turnQueue` tras el jugador. Sus acciones se muestran en el log con formato distinto. |
| Fases del parásito | `ParasitoEnemy` extiende `Enemy` con `int phase`. En `executeTurn()`: comprueba HP para cambiar de fase y comportamiento. Al morir: `TurnManager.triggerEnding(Player.characterType)`. |
| Desenlace por personaje | `TurnManager.triggerEnding(type)` carga el texto de cierre según `CharacterType` enum y lo pasa al `EndingController` de JavaFX. Sin lógica adicional — solo texto condicional. |

---

## 5. Arquitectura y Estructura de Clases

### Capas del sistema

| Capa | Paquete | Responsabilidad |
|------|---------|----------------|
| Estructuras base | `MisEstructurasDeDatos` | LSE, Cola, Grafo, ABB — ya implementadas, no tocar |
| Modelo / Dominio juego | `juego.modelo` | Cell, Room, Dungeon, Unit, Player, Enemy, Item, GameState |
| Lógica del juego | `juego.logica` | TurnManager, BFSMovimiento, PuzzleManager, LectorJSON |
| Presentación JavaFX | `juego.vista` | Controllers, GridPane de habitación, HUD, menús, EndingController |

### Clases clave — atributos añadidos o modificados en v3

| Clase | Cambios en v3 |
|-------|--------------|
| `CellType` (enum) | Añadir: `DOOR_HIDDEN`, `RUNE`. Mantener: `FLOOR`, `WALL`, `DOOR`, `DOOR_LOCKED`, `LEVER`, `STAIRS_UP`, `STAIRS_DOWN` |
| `Cell` | Añadir: `isHighlighted` (para BFS visual), `triggerId: String` (para pasadizos y acertijos) |
| `Room` | Añadir: `characterDialogues: Map`, `checkSecretTrigger(r,c)` |
| `Player` | Añadir: `characterType: CharacterType` enum (KAEL, SYRA, DORATH) |
| `Enemy` | Añadir: `dropItem: Item`, `onDeath()`, `phase: int` para boss final |
| `SpecialItem` | `effectData: String` codifica comportamiento del Fragmento de Voluntad por personaje |
| `TurnManager` | Añadir: `triggerEnding(CharacterType)`, `onRoomEnter()`, `insertAllyNPC(MalacharAlly)` |
| `GameState` | `ending: EndingType` eliminado — solo `ENDING_SACRIFICE`. Añadir: `characterType` para el desenlace. |

---

## 6. Roadmap de Desarrollo

### Fase 0 — Diseño previo — COMPLETADO
- UML Capa 1 y Capa 2 generados. Historia, personajes, zonas y final definidos (este documento).
- Checklist de requisitos del enunciado validado. Advertencias técnicas identificadas.

### Fase 1 — Núcleo de datos — EMPEZAR AQUÍ
- `CellType`: FLOOR, WALL, DOOR, DOOR_LOCKED, DOOR_HIDDEN, LEVER, RUNE, STAIRS_UP, STAIRS_DOWN.
- `Cell`: type, unit, item, moveCost, isRevealed, isHighlighted, triggerId.
- `Room`: `Cell[][]`, id, name, leverCells, correctSequence, characterDialogues, checkSecretTrigger.
- `Room` implements `InterfazDatosNodo` (getNombre=id, getTipo='room').
- `Dungeon`: Grafo, addRoom, connect, activateHiddenPassage.
- `CharacterType` enum: KAEL, SYRA, DORATH.
- `Unit` (abstracta) con `roomId: String`. `Player` con `inventory: LSE` y `characterType`.
- `Enemy` (abstracta) con `dropItem: Item` y `phase: int`. Warrior, Archer, Mage concretas.
- `MalacharAlly`: Enemy especial con `executeAllyTurn()` a favor del jugador.
- `ParasitoEnemy`: Enemy especial con fases de HP y `triggerEnding` al morir.
- `Item` (abstracta), Weapon, Armor, Potion, SpecialItem con `effectData`.
- `TurnManager`: Cola, LSE, nextTurn, addLog, triggerEnding, onRoomEnter.
- `GameState` con `CharacterType` y estructura plana para Gson.
- → **Tests JUnit para todas las clases del modelo antes de continuar.**

### Fase 2 — Lógica del juego
- `BFSMovimiento.getCellsInRange()` sobre `Cell[][]` usando `moveCost` variable.
- `Player.move(r,c)`: consume movePoints, detecta STAIRS/DOOR/DOOR_LOCKED/DOOR_HIDDEN/RUNE.
- `PuzzleManager`: registra secuencia de palancas o runas, compara con `correctSequence`, activa resultado.
- `Room.checkSecretTrigger`: detecta triggerId en celda pisada, llama a `Dungeon.activateHiddenPassage`.
- `Enemy.executeTurn()` por tipo. `MalacharAlly.executeAllyTurn()`. `ParasitoEnemy` con fases.
- `SpecialItem.onEquip()`: aplica efecto del Fragmento de Voluntad según `characterType`.
- `TurnManager.onRoomEnter()`: carga diálogo del personaje actual si Room lo tiene.
- `TurnManager.insertAllyNPC(MalacharAlly)`: añade a Malachar a la Cola de turnos en Zona 5.
- `TurnManager.triggerEnding(CharacterType)`: carga texto de desenlace correspondiente.
- → **Tests JUnit para BFS, palancas, pasadizos, Fragmento de Voluntad, fases del boss.**

### Fase 3 — Persistencia JSON
- `GameState` serializable: `List<RoomDTO>` y `List<UnitDTO>` (estructura plana, sin referencias circulares).
- `LectorJSON.guardar` y `cargar`. `TurnManager.reconstruirReferencias()` tras carga.
- Manejo de excepciones: `IOException`, `JsonSyntaxException`, excepciones propias.
- JSON de ejemplo con partida guardada para entregar con el proyecto.
- → **Tests JUnit para guardar/cargar y reconstrucción de referencias.**

### Fase 4 — Interfaz JavaFX
- Pantalla de selección de personaje: Kael / Syra / Dorath con stats y fragmento de backstory.
- GridPane dinámico con colores por CellType. Resaltado de celdas BFS al seleccionar jugador.
- Indicadores visuales: LEVER, RUNE, STAIRS, DOOR_LOCKED, DOOR_HIDDEN (si Semilla Resonante activa).
- Panel lateral: HP, movePoints, inventario, turno actual, log de acciones.
- Pantalla de diálogo para conversaciones de personaje en zonas y para Malachar en Zona 5.
- Secuencia de final: texto de desenlace por personaje + frase final de Malachar.
- → **Pruebas manuales.**

---

## 7. Advertencias y Decisiones Críticas

### ADVERTENCIA 1 — Serialización del Grafo con Gson
- Gson **no puede** serializar Grafo directamente por referencias circulares.
- Solución: `GameState` guarda `List<RoomDTO>` y `List<UnitDTO>` (estructura plana).
- `LectorJSON` reconstruye el Dungeon desde esas listas al cargar. **NO usar `JsonSerializer` custom.**

### ADVERTENCIA 2 — Referencias circulares Unit ↔ Room
- Unit **NO** guarda `Room room`. Guarda `String roomId`.
- Al cargar: `TurnManager.reconstruirReferencias()` restablece la referencia buscando por id.
- **Aplicar desde el primer día** — cambiarlo después implica refactorizar Player, Enemy y TurnManager.

### ADVERTENCIA 3 — CellType debe incluir todos los tipos desde el inicio
- Incluir `DOOR_HIDDEN`, `RUNE`, `STAIRS_UP`, `STAIRS_DOWN`, `DOOR_LOCKED` desde el principio.
- Añadirlos después obliga a modificar BFSMovimiento, TurnManager y el renderer JavaFX simultáneamente.

### ADVERTENCIA 4 — No empezar por JavaFX
- La Fase 4 no empieza hasta que Fases 1, 2 y 3 pasan **todos** sus tests.
- La UI es un observador pasivo del modelo — no debe contener ningún cálculo de juego.

### ADVERTENCIA 5 — BFS y movimiento son dos operaciones distintas
- `BFSMovimiento.getCellsInRange()` calcula celdas alcanzables. **NO** mueve al jugador.
- `Player.move(r,c)` ejecuta el movimiento real. Se llama solo cuando el jugador confirma destino.
- Si los mezclas no podrás mostrar el resaltado en JavaFX sin ejecutar el movimiento.

### ADVERTENCIA 6 — MalacharAlly en la Cola de turnos
- `MalacharAlly` se inserta en la Cola de TurnManager al llegar a Zona 5.
- Sus acciones deben mostrarse diferenciadas en el log (no como acción del jugador ni como ataque enemigo).
- `executeAllyTurn()` **NO** debe atacar al jugador bajo ninguna circunstancia — verificar antes de ejecutar.

---

## 8. Instrucciones para la IA en Conversaciones Futuras

### Contexto completo del proyecto
- **Juego:** Valdris: El Núcleo Profundo. Turnos, JavaFX, Java 21, Gson.
- **3 personajes:** Kael (espadachín ígnea, daño alto, rango 1), Syra (rastreadora élfica, rápida, rango 3), Dorath (clérigo caído, mágico, rango 4).
- **5 zonas:** Campos Grises → Bosque de Lireth → Minas de Karath → Torre de Embrath → El Núcleo Profundo.
- **Final único** `ENDING_SACRIFICE`: jugador + Malachar vs el Parásito. Desenlace de texto distinto por personaje.
- **Item clave Zona 4:** Fragmento de Voluntad — potencia habilidad de cada personaje de forma distinta.
- **Pasadizo Zona 4:** La Celda de Dorath — textos originales del sellado, diálogo extra por personaje.
- **Código base existente:** Grafo, LSE, Cola, Pila, ABB — funcionando, **NO reimplementar.**
- **Restricción absoluta:** NO usar `java.util.*` para estructuras de datos del juego.

### Reglas de comportamiento para la IA
- Código completo, no fragmentos, cuando el alumno pide implementar una clase.
- Verificar siempre que usa LSE/Cola/Grafo propias, no `java.util.*`.
- Si el alumno contradice una advertencia de la Sección 7, decirlo antes de continuar.
- No repetir lo que ya está decidido. Asumir que el alumno conoce este documento.
- Dar recomendación directa con justificación cuando haya varias opciones.

### Lo que la IA NO debe hacer
- Sugerir `ArrayList`, `HashMap` o cualquier `java.util` para lógica del juego.
- Proponer empezar JavaFX antes de que la lógica esté testeada.
- Añadir `Unit.room` (referencia directa) en lugar de `Unit.roomId` (String).
- Implementar `JsonSerializer` custom de Gson sin que el alumno lo solicite.
- Mezclar lógica de juego en los controllers de JavaFX.
- Hacer que `MalacharAlly` pueda atacar al jugador.
- Dar la razón por defecto si el alumno contradice las decisiones de diseño de este documento.

---

*Guía de Proyecto v3.0 — Valdris: El Núcleo Profundo · Juego por Turnos JavaFX + EEDD · Grupo H12GEXTRA*
