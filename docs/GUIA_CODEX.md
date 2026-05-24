# Guía de Especificaciones para Codex
## Valdris: El Núcleo Profundo
**Ficha de implementación completa — 50 clases — Java 21 + JavaFX**

> - Este documento contiene una ficha por cada clase del juego con atributos, métodos, dependencias y tests.
> - Dar a Codex **UNA ficha a la vez**. No varias juntas. Esperar a que genere y compile antes de pasar a la siguiente.
> - **Orden obligatorio**: respetar las capas (1 → 2 → 3 → 4 → 5 → 6 → 7). Cada capa depende de la anterior.
> - Nunca usar `java.util.*` para listas, colas, pilas o grafos. Usar **SIEMPRE** las implementaciones propias.
> - Tras cada clase generada: compilar, ejecutar los tests indicados, y solo entonces pasar a la siguiente.

---

## Estructura de paquetes del proyecto

```
src/
  MisEstructurasDeDatos/     <- reutilizar del repo anterior (LSE, Cola, Grafo)
  Valdris/
    model/
      enums/                 <- CellType, EffectType, ItemType, CharacterType, EnemyType, Phase
      items/                 <- Item, Weapon, Armor, Potion, Accessory
      units/                 <- Unit, Player, Enemy
      map/                   <- Cell, Room, Dungeon
      effects/               <- Effect
    logic/
      bfs/                   <- BFSMovimiento, BFSCaminoMinimo
      vision/                <- LineaDeVision
      combat/                <- CombatManager
      ai/                    <- IAEnemigo, ArbolDecisionIA
      turn/                  <- TurnManager
      generation/            <- DungeonGenerator, ItemGenerator
      persistence/           <- GameState, LectorJSON
      exceptions/            <- InvalidMoveException, InvalidAttackException, GameStateException
    ui/
      MainApp.java
      model/                 <- GameModel
      view/                  <- GameView, CharacterSelectView, InventoryView, CombatLogView
      controller/            <- GameController
  test/
    Valdris/                 <- Tests JUnit5 espejando la estructura de src
```

---

## Reglas de dependencia entre capas

| Capa | Puede usar | NO puede usar |
|------|-----------|---------------|
| Capa 2 — Modelo base | Solo Java puro (String, int, boolean, Math) | Nada de otras capas |
| Capa 3 — Unidades | Capa 2 + LSE[Effect] + LSE[Item] | Lógica, UI, persistencia |
| Capa 4 — Mapa | Capas 2+3 + LSE[Enemy] | Lógica, UI, persistencia |
| Capa 5 — Lógica | Capas 2+3+4 + Cola + Grafo | UI, persistencia |
| Capa 6 — Persistencia | Capas 2+3+4+5 + Gson | UI (JavaFX) |
| Capa 7 — JavaFX | Todas las capas anteriores | — |

---

## CAPA 1 — Estructuras de Datos (reutilizar del repo)

- Copiar directamente del repositorio anterior: `ListaSimplementeEnlazada`, `Cola`, `Grafo`, `NodoGrafo`, `Arista`.
- **NO modificar nada**. Solo verificar que compilan en el nuevo proyecto.
- Verificar que `Grafo[DN,DA]` tiene: `addNodo`, `addArista`, `getVecinos`, `caminoMinimo` (BFS), `Dijkstra`.
- Si falta algún método en `Grafo`, añadirlo antes de continuar con las demás capas.

---

## CAPA 2 — Modelo base

### 2.1. CellType — Capa 2 — Enum
**Paquete:** `Valdris.model.enums`

Enum que define todos los tipos posibles de celda en la sala. Determina si una celda es transitable o no y cómo interactúa con el jugador.

```java
public enum CellType {
    FLOOR,       // suelo normal, transitable
    WALL,        // pared, no transitable nunca
    DOOR,        // puerta abierta, transitable
    DOOR_HIDDEN, // puerta oculta, parece WALL hasta que se descubre
    DOOR_LOCKED, // puerta cerrada con llave, no transitable sin llave equipada
    STAIRS,      // escaleras, cambian de piso en minas de Karath
    RUNE,        // runa del suelo, transitable, activa mecanismo al pisarla
    LEVER,       // palanca, transitable, se activa con acción de uso
    TRAP         // trampa oculta, transitable, activa efecto al pisarla
}
```

---

### 2.2. EffectType — Capa 2 — Enum
**Paquete:** `Valdris.model.enums`

Enum de efectos de estado que pueden afectar a unidades (jugador y enemigos).

```java
public enum EffectType {
    SLOW,      // reduce mov a ceil(mov/2.0). Duración: 2 turnos
    BLIND,     // 25% de fallo de ataque. Duración: 2 turnos
    CURSE,     // +3 daño recibido de ataques enemigos. Duración: 2 turnos
    PARALYSIS, // sin movimiento ni ataque. Duración: 1 turno
    BURN       // +3 daño al inicio del turno del afectado. Duración: 1 turno
}
```

---

### 2.3. ItemType + CharacterType + EnemyType + Phase — Capa 2 — Enums
**Paquete:** `Valdris.model.enums`

Cuatro enums auxiliares. Pedir a Codex que los genere todos en un mismo mensaje.

```java
public enum ItemType { WEAPON, ARMOR, SHIELD, ACCESSORY, POTION }

public enum CharacterType { KAEL, SYRA, DORATH }

public enum EnemyType {
    WARRIOR, BERSERKER, GUARDIAN,        // familia Warrior
    ARCHER, SNIPER,                      // familia Archer
    DESTRUCTOR, CONTROLLER, SUMMONER     // familia Mage
}

public enum Phase { MOVEMENT, PICKUP, USE_ITEM, ATTACK, ENEMY_TURN }
```

---

### 2.4. Effect — Capa 2 — Clase
**Paquete:** `Valdris.model.effects`

Representa un efecto de estado activo sobre una unidad. Se almacena en `LSE[Effect]` dentro de `Unit`. Al final de cada turno se decrementa `turnosRestantes`. Si llega a 0 se elimina de la lista.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `EffectType` | `tipo` | Tipo de efecto (SLOW, BLIND, etc.) |
| `int` | `turnosRestantes` | Turnos que quedan hasta que el efecto desaparece |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `Effect(EffectType tipo, int turnos)` | Constructor. Inicializa tipo y turnosRestantes. | `void` |
| `decrementar()` | Resta 1 a turnosRestantes. | `void` |
| `isExpired()` | Devuelve true si turnosRestantes <= 0. | `boolean` |
| `getTipo()` | Getter de tipo. | `EffectType` |
| `getTurnos()` | Getter de turnosRestantes. | `int` |

---

### 2.5. Item (abstracta) — Capa 2 — Clase abstracta
**Paquete:** `Valdris.model.items`

Clase base de todos los items del juego. `Weapon`, `Armor`, `Potion` y `Accessory` extienden esta clase. El método `use()` es abstracto y cada subclase lo implementa con su efecto específico.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `String` | `id` | Identificador único del item (ej: "W1", "P2", "AC3") |
| `String` | `nombre` | Nombre visible en UI (ej: "Espada Oxidada") |
| `ItemType` | `tipo` | Tipo de item para categorizar en el inventario |
| `String` | `descripcion` | Texto descriptivo para tooltip en UI |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `Item(String id, String nombre, ItemType tipo, String desc)` | Constructor base. | `void` |
| `abstract void use(Player player)` | Aplica el efecto del item al jugador. Implementado en subclases. | `void` |
| `getId(), getNombre(), getTipo(), getDescripcion()` | Getters estándar. | varios |
| `toString()` | Devuelve `"[tipo] nombre"` para mostrar en UI. | `String` |

---

### 2.6. Weapon — Capa 2 — Clase
**Paquete:** `Valdris.model.items`

Arma equipable. El daño base **REEMPLAZA** el daño base del personaje (no se suma). La afinidad es un mapa `CharacterType -> int` con el bonus/penalización por personaje. `use(player)` equipa el arma en el slot de mano principal del jugador.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `int` | `danoBase` | Daño base del arma antes de afinidad |
| `int` | `penetracion` | Puntos de defensa enemiga que ignora (0 si ninguno) |
| `Map[CharacterType, Int]` | `afinidades` | Bonus (+) o penalización (-) por personaje. Usar LSE de pares o array indexado por `CharacterType.ordinal()` |
| `EffectType` | `efectoEspecial` | Efecto que puede aplicar al atacar (null si ninguno) |
| `double` | `probEfecto` | Probabilidad 0.0-1.0 de aplicar el efecto especial |
| `int` | `rango` | Rango de ataque (1=cuerpo, 3=arco, 4=largo) |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `Weapon(String id, String nombre, int danoBase, int pen, int rango)` | Constructor base. | `void` |
| `setAfinidad(CharacterType tipo, int bonus)` | Establece el bonus de afinidad para un personaje. | `void` |
| `getAfinidad(CharacterType tipo)` | Devuelve el bonus de afinidad. 0 si no tiene. | `int` |
| `getDanoEfectivo(CharacterType tipo)` | Devuelve `danoBase + getAfinidad(tipo)`. | `int` |
| `use(Player player)` | Equipa el arma en `player.setArmaEquipada(this)`. | `void` |
| `tryAplicarEfecto()` | Lanza `Math.random()`. Si < probEfecto devuelve `efectoEspecial`, sino null. | `EffectType` |

---

### 2.7. Armor — Capa 2 — Clase
**Paquete:** `Valdris.model.items`

Armadura o escudo equipable. Incrementa la defensa del jugador mientras está equipada. Puede tener una inmunidad a un efecto de estado específico. `use(player)` equipa la armadura en el slot correspondiente (torso o mano secundaria).

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `int` | `defensa` | Puntos de defensa que aporta al jugador |
| `EffectType` | `inmunidad` | Efecto al que es inmune el portador (null si ninguno) |
| `boolean` | `esEscudo` | true si va al slot de mano secundaria, false si va al torso |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `Armor(String id, String nombre, int defensa, boolean esEscudo)` | Constructor base. | `void` |
| `use(Player player)` | Equipa en slot torso o mano secundaria según esEscudo. | `void` |
| `getDefensa(), isEscudo(), getInmunidad()` | Getters estándar. | varios |

---

### 2.8. Potion — Capa 2 — Clase
**Paquete:** `Valdris.model.items`

Poción consumible. `use(player)` aplica la curación y el efecto extra si tiene. Tras usarse se elimina del inventario del jugador. La curación no puede superar el `hpMax` del jugador.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `int` | `curacionHP` | HP que recupera al usarse |
| `EffectType` | `efectoExtra` | Efecto adicional que aplica (null si solo cura) |
| `int` | `valorEfecto` | Valor del efecto (ej: +5 ataque para Elixir). 0 si no aplica |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `Potion(String id, String nombre, int curacionHP)` | Constructor base. | `void` |
| `use(Player player)` | `player.curar(curacionHP)`. Luego aplicar `efectoExtra` si tiene. Eliminar del inventario. | `void` |

---

### 2.9. Accessory — Capa 2 — Clase
**Paquete:** `Valdris.model.items`

Accesorio equipable en el slot de accesorio. Efecto pasivo mientras está equipado (bonus ataque, bonus mov, o efecto narrativo). Los accesorios AC1-AC4 son de progresión narrativa y no tienen efecto de combate directo.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `int` | `bonusAtaque` | Bonus de ataque mientras equipado (0 si ninguno) |
| `int` | `bonusMov` | Bonus de movimiento mientras equipado (0 si ninguno) |
| `int` | `bonusDef` | Bonus de defensa mientras equipado (0 si ninguno) |
| `boolean` | `esNarrativo` | true si es item de progresión (llave, fragmento, etc.) |
| `String` | `efectoNarrativo` | Descripción del efecto especial narrativo (null si combate) |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `Accessory(String id, String nombre)` | Constructor base. | `void` |
| `use(Player player)` | Equipa el accesorio en `player.setAccesorioEquipado(this)`. | `void` |
| `getBonusAtaque(), getBonusMov(), getBonusDef()` | Getters estándar. | varios |

---

## CAPA 3 — Unidades

### 3.1. Unit (abstracta) — Capa 3 — Clase abstracta
**Paquete:** `Valdris.model.units`

Clase base de `Player` y `Enemy`. Contiene todos los stats comunes y la gestión de efectos de estado. Los efectos se guardan en `LSE[Effect]`. Al inicio de cada turno de la unidad se llama a `procesarEfectos()`.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `int` | `hp` | HP actual |
| `int` | `hpMax` | HP máximo. Nunca cambia durante la partida |
| `int` | `ataqueBase` | Daño base sin arma ni modificadores |
| `int` | `defensaBase` | Defensa base sin armadura |
| `int` | `movBase` | Puntos de movimiento base |
| `int` | `rango` | Rango de ataque base |
| `int` | `filaActual` | Posición fila en la sala actual |
| `int` | `colActual` | Posición columna en la sala actual |
| `LSE[Effect]` | `efectosActivos` | Lista de efectos de estado activos |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `recibirDanio(int cantidad)` | `hp = max(0, hp - cantidad)`. Lanzar `GameStateException` si hp < 0. | `void` |
| `curar(int cantidad)` | `hp = min(hpMax, hp + cantidad)`. | `void` |
| `isVivo()` | Devuelve `hp > 0`. | `boolean` |
| `addEfecto(Effect ef)` | Añadir a efectosActivos. Si ya existe el mismo tipo, reemplazar. | `void` |
| `tieneEfecto(EffectType tipo)` | true si efectosActivos contiene un Effect del tipo dado. | `boolean` |
| `procesarEfectos()` | Aplicar daño de BURN si está activo. Luego decrementar todos los efectos, incluido CURSE, y eliminar los expirados. | `void` |
| `getMovEfectivo()` | Si SLOW activo: `ceil(movBase/2.0)`. BLIND no modifica el movimiento. Sino: `movBase`. | `int` |
| `getDefensaTotal()` | `defensaBase` + bonus de armadura equipada. | `int` |
| `getAtaqueTotal()` | ataqueBase del arma equipada + afinidad + bonus de accesorio. | `int` |
| `setPosicion(int fila, int col)` | Actualiza filaActual y colActual. | `void` |

---

### 3.2. Player — Capa 3 — Clase
**Paquete:** `Valdris.model.units`

El personaje controlado por el jugador. Extiende `Unit`. Tiene inventario (`LSE[Item]`) y 4 slots de equipo (arma, escudo/secundaria, torso, accesorio). Los stats base dependen del `CharacterType` elegido al inicio.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `CharacterType` | `tipo` | Personaje elegido: KAEL, SYRA o DORATH |
| `LSE[Item]` | `inventario` | Todos los items que lleva el jugador |
| `Weapon` | `armaEquipada` | Arma activa en mano principal (null si ninguna) |
| `Armor` | `escudoEquipado` | Escudo o arma secundaria (null si ninguno) |
| `Armor` | `armaduraEquipada` | Armadura en torso (null si ninguna) |
| `Accessory` | `accesorioEquipado` | Accesorio activo (null si ninguno) |
| `boolean` | `haMovido` | true si ya usó la acción de movimiento este turno |
| `boolean` | `haRecogido` | true si ya usó la acción de recogida este turno |
| `boolean` | `haUsadoItem` | true si ya usó la acción de item este turno |
| `boolean` | `haAtacado` | true si ya usó la acción de ataque este turno |

**Stats base por personaje:**
- `KAEL`: hpMax=110, ataqueBase=18, defensaBase=0, movBase=3, rango=1
- `SYRA`: hpMax=75, ataqueBase=12, defensaBase=0, movBase=5, rango=3
- `DORATH`: hpMax=80, ataqueBase=14, defensaBase=0, movBase=2, rango=4

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `Player(CharacterType tipo)` | Constructor. Inicializa stats según tipo. Crea LSE vacía para inventario. | `void` |
| `addItem(Item item)` | Añadir item al inventario. | `void` |
| `removeItem(Item item)` | Eliminar item del inventario. | `void` |
| `equip(Item item)` | Llamar a `item.use(this)`. Actualiza el slot correspondiente. | `void` |
| `setArmaEquipada(Weapon w)` | Actualiza armaEquipada. Si null, el jugador ataca con ataqueBase. | `void` |
| `getAtaqueTotal()` | Si armaEquipada != null: `armaEquipada.getDanoEfectivo(tipo) + bonusAccesorio`. Sino: ataqueBase. | `int` |
| `getDefensaTotal()` | `defensaBase + (armadura != null ? armadura.getDefensa() : 0) + (escudo != null ? escudo.getDefensa() : 0) + bonusDef accesorio`. | `int` |
| `getRangoEfectivo()` | Si armaEquipada != null: `armaEquipada.getRango()`. Sino: rango base del personaje. | `int` |
| `resetAcciones()` | `haMovido = haRecogido = haUsadoItem = haAtacado = false`. Llamar al inicio de cada turno del jugador. | `void` |
| `tieneInmunidad(EffectType t)` | true si armaduraEquipada o escudoEquipado tienen inmunidad a t. | `boolean` |

---

### 3.3. Enemy — Capa 3 — Clase
**Paquete:** `Valdris.model.units`

Enemigo controlado por la IA. Extiende `Unit`. Los stats base se inicializan según `EnemyType` en el constructor. `dropItem` es el item que suelta al morir (puede ser null si no tiene drop).

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `EnemyType` | `tipo` | Tipo de enemigo que determina sus stats y comportamiento |
| `Item` | `dropItem` | Item que suelta al morir. Null si no tiene drop. |
| `int` | `turnosSinActuar` | Contador para habilidades con cooldown (Francotirador: cooldown 2t, Invocador: invoca c/2t) |
| `String` | `idSala` | ID de la sala donde fue generado (para Guardian: zona fija) |
| `int` | `filaSpawn` | Fila de spawn original (para Guardian: calcular zona fija radio 3) |
| `int` | `colSpawn` | Columna de spawn original |
| `boolean` | `esMiniJefe` | true si es mini-boss con stats reforzados |

**Stats por tipo:**
- `WARRIOR`: hp=35, atk=15, def=8, mov=2, rng=1
- `BERSERKER`: hp=25, atk=13, def=3, mov=4, rng=1
- `GUARDIAN`: hp=50, atk=15, def=10, mov=1, rng=1
- `ARCHER`: hp=28, atk=10, def=4, mov=3, rng=4
- `SNIPER`: hp=28, atk=18, def=3, mov=2, rng=5
- `DESTRUCTOR`: hp=40, atk=6, def=5, mov=0, rng=5 (AOE, daño por celda)
- `CONTROLLER`: hp=35, atk=4, def=4, mov=2, rng=3
- `SUMMONER`: hp=45, atk=0, def=6, mov=2, rng=0

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `Enemy(EnemyType tipo, int fila, int col, String idSala)` | Constructor. Inicializa stats según tipo. Guarda posición de spawn. | `void` |
| `onDeath(Room room)` | Llamar cuando hp <= 0. Si dropItem != null, colocarlo en la celda actual de la sala. | `void` |
| `incrementarCooldown()` | `turnosSinActuar++`. Para Sniper y Summoner. | `void` |
| `resetCooldown()` | `turnosSinActuar = 0`. | `void` |
| `isCooldownListo(int n)` | Devuelve `turnosSinActuar >= n`. | `boolean` |

---

## CAPA 4 — Mapa

### 4.1. Cell — Capa 4 — Clase
**Paquete:** `Valdris.model.map`

Representa una celda individual de la sala. Contiene su tipo, la unidad que la ocupa (si hay alguna) y el item en el suelo (si hay alguno). `isWalkable()` es el método más importante.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `CellType` | `tipo` | Tipo de celda |
| `Unit` | `unit` | Unidad que ocupa la celda (null si vacía) |
| `Item` | `item` | Item en el suelo (null si no hay) |
| `boolean` | `descubierta` | false si es DOOR_HIDDEN y no ha sido revelada aún |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `Cell(CellType tipo)` | Constructor. unit=null, item=null, descubierta=true (excepto DOOR_HIDDEN). | `void` |
| `isWalkable()` | false si tipo==WALL o tipo==DOOR_LOCKED. false si unit!=null. false si item!=null && item.isRequiresAdjacent(). true en el resto. | `boolean` |
| `setUnit(Unit u)` | Establece la unidad en la celda. | `void` |
| `removeUnit()` | unit = null. | `void` |
| `setItem(Item i)` | Coloca un item en el suelo. | `void` |
| `removeItem()` | item = null. Devuelve el item que había. | `Item` |
| `revelar()` | Si tipo==DOOR_HIDDEN: descubierta=true. La celda pasa a comportarse como DOOR. | `void` |
| `getTipo(), getUnit(), getItem(), isDescubierta()` | Getters estándar. | varios |

---

### 4.2. Room — Capa 4 — Clase
**Paquete:** `Valdris.model.map`

Sala del juego. Contiene una matriz de `Cell[][]` y la lista de enemigos vivos. El tamaño varía según el tipo (Pequeña 5-6x5-6, Mediana 7-8x7-9, Grande 9-10x9-11). Los pasillos de transición son siempre 3x8.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `String` | `id` | Identificador único (ej: "S1-A", "S3-F", "PASILLO-1-2") |
| `String` | `nombre` | Nombre visible (ej: "Aldea Abandonada") |
| `Cell[][]` | `celdas` | Matriz de celdas. `celdas[fila][col]` |
| `int` | `filas` | Número de filas de la sala |
| `int` | `cols` | Número de columnas de la sala |
| `LSE[Enemy]` | `enemigos` | Lista de enemigos vivos en la sala |
| `boolean` | `hasRoomTimer` | true si la sala tiene límite de turnos |
| `int` | `turnosRestantes` | Turnos restantes si hasRoomTimer==true. -1 si no tiene timer |
| `boolean` | `explorada` | true si el jugador ya ha entrado alguna vez |
| `int` | `filaJugador` | Fila donde aparece el jugador al entrar a esta sala |
| `int` | `colJugador` | Columna donde aparece el jugador al entrar a esta sala |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `Room(String id, String nombre, int filas, int cols)` | Constructor. Inicializa celdas[][] con FLOOR. Crea LSE vacía para enemigos. | `void` |
| `getCell(int fila, int col)` | Devuelve `celdas[fila][col]`. Lanzar `InvalidMoveException` si fuera de rango. | `Cell` |
| `setCellType(int f, int c, CellType t)` | Cambia el tipo de una celda. | `void` |
| `addEnemigo(Enemy e)` | Añadir enemigo a la lista y colocarlo en su celda. | `void` |
| `removeEnemigo(Enemy e)` | Eliminar enemigo de la lista y limpiar su celda. | `void` |
| `getEnemigos()` | Devuelve `LSE[Enemy]` de enemigos vivos. | `LSE[Enemy]` |
| `isEnRango(int f, int c)` | true si `0<=f<filas` y `0<=c<cols`. | `boolean` |
| `decrementarTimer()` | Si hasRoomTimer: `turnosRestantes--`. Si llega a 0 lanzar `GameStateException("tiempo agotado")`. | `void` |
| `getCeldaLibreCercana(int f, int c)` | Busca la celda libre más cercana a (f,c) para spawn de Berserker invocado. | `Cell` |

---

### 4.3. Dungeon — Capa 4 — Clase
**Paquete:** `Valdris.model.map`

El mapa completo del juego. Usa `Grafo[Room,String]` para conectar las salas. El grafo es bidireccional excepto la arista Pasillo Final → S5-D que es unidireccional. `roomActual` es la sala donde está el jugador.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `Grafo[Room,String]` | `grafo` | Grafo de salas. Nodos=Room, Aristas=String (descripción conexión) |
| `Room` | `roomActual` | Sala donde está el jugador actualmente |
| `NodoGrafo[Room]` | `nodoActual` | Nodo del grafo correspondiente a roomActual |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `Dungeon()` | Constructor. Inicializa grafo vacío. | `void` |
| `addRoom(Room room)` | Crea `NodoGrafo[Room]` y lo añade al grafo. | `void` |
| `conectar(Room a, Room b, String desc)` | Conecta bidireccional. `grafo.addArista(nodoA, desc, nodoB)` y viceversa. | `void` |
| `conectarUnidireccional(Room a, Room b, String desc)` | Solo `grafo.addArista(nodoA, desc, nodoB)`. Para Pasillo Final → S5-D. | `void` |
| `getSalasAdyacentes(Room r)` | Devuelve `LSE[Room]` con las salas conectadas a r (vecinos en el grafo). | `LSE[Room]` |
| `getRoomById(String id)` | Busca en el grafo la sala con ese id. Null si no existe. | `Room` |
| `setRoomActual(Room r)` | Actualiza roomActual y nodoActual. | `void` |
| `getRoomActual()` | Getter de roomActual. | `Room` |

---

## CAPA 5 — Lógica del juego

### 5.1. BFSMovimiento — Capa 5 — Clase estática
**Paquete:** `Valdris.logic.bfs`

Calcula las celdas alcanzables por una unidad dado su punto de origen y sus puntos de movimiento. Usa BFS en 4 direcciones (N, S, E, O). Las diagonales cuestan 2 pasos.

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `getCellsInRange(Room room, int filaOrigen, int colOrigen, int movPoints)` | BFS desde (filaOrigen, colOrigen). Expande en 4 dir. Solo añade celda si isWalkable(). | `LSE[Cell]` |
| `getCamino(Room room, int filaOrigen, int colOrigen, int filaDestino, int colDestino)` | BFS para encontrar el camino más corto entre dos celdas. LSE vacía si no hay camino. | `LSE[Cell]` |

> **Implementación BFS:** usar `Cola` donde `int[] = {fila, col, pasosUsados}`. Mantener una matriz `boolean[][] visitado`. Solo expandir celdas con `pasosUsados < movPoints`. La celda destino del jugador puede tener un enemigo: **NO incluirla** en celdas alcanzables de movimiento.

---

### 5.2. LineaDeVision — Capa 5 — Clase estática
**Paquete:** `Valdris.logic.vision`

Implementa el algoritmo de Bresenham para determinar si hay línea de visión directa entre dos celdas. Usado por Archer, Sniper, Destructor y Controller.

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `tieneVision(Room room, int f1, int c1, int f2, int c2)` | Algoritmo de Bresenham entre (f1,c1) y (f2,c2). Devuelve true si todas las celdas intermedias son de tipo FLOOR, DOOR o RUNE (no WALL). | `boolean` |

```java
// Pseudocódigo Bresenham para referencia de Codex:
int dx = abs(c2-c1), dy = abs(f2-f1);
int sx = c1<c2 ? 1 : -1, sy = f1<f2 ? 1 : -1;
int err = dx - dy;
int f = f1, c = c1;
while (!(f==f2 && c==c2)) {
    int e2 = 2*err;
    if (e2 > -dy) { err -= dy; c += sx; }
    if (e2 < dx)  { err += dx; f += sy; }
    if (f==f2 && c==c2) break;
    if (room.getCell(f,c).getTipo()==WALL) return false;
}
return true;
```

---

### 5.3. CombatManager — Capa 5 — Clase estática
**Paquete:** `Valdris.logic.combat`

Gestiona toda la lógica de combate. **Fórmula oficial:** `daño = max(0, ataque * random[0.5-1.5] - defensaEfectiva)`. La defensa efectiva se reduce por la penetración del arma del atacante. Si el atacante tiene BLIND, el ataque tiene un 25% de probabilidad de fallar antes de calcular daño.

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `calcularDanio(Unit atacante, Unit defensor)` | `aleatorio = Math.random()*1.0+0.5`. `defEf = max(0, defensor.getDefensaTotal()-pen)`. `return max(0, (int)(ataqueTotal*aleatorio) - defEf)`. | `int` |
| `resolverAtaqueJugador(Player jugador, Enemy enemigo)` | Si jugador tiene BLIND y falla la tirada del 25%, devuelve fallo sin daño. Si acierta, calcula daño, aplica efectos del arma y si !enemigo.isVivo(): `enemigo.onDeath(room)`. | `CombatResult` |
| `resolverAtaqueEnemigo(Enemy enemigo, Player jugador)` | Si enemigo tiene BLIND y falla la tirada del 25%, devuelve fallo sin daño. Si acierta, calcula daño. Si jugador.tieneEfecto(CURSE): danio+=3. `jugador.recibirDanio(danio)`. | `CombatResult` |
| `resolverAOEDestructor(Enemy destructor, Room room, Player jugador)` | Calcular celdas en radio 2. Por cada celda: si hay jugador, infligir `destructor.getDanoBase()` directamente (sin fórmula aleatoria) y sumar CURSE si el jugador lo tiene activo. | `CombatResult` |
| `estaEnRango(Unit atacante, Unit defensor)` | Calcular distancia Manhattan. Si <= `atacante.getRangoEfectivo()`: true. Para arcos/mágicos: además verificar `LineaDeVision.tieneVision()`. | `boolean` |

---

### 5.4. ArbolDecisionIA — Capa 5 — Clase
**Paquete:** `Valdris.logic.ai`

Árbol de decisión que determina la acción de un enemigo en su turno. Cada nodo interno es una condición boolean. Las hojas son acciones. Este árbol cubre el criterio de evaluación de árboles del enunciado.

**Estructura del árbol por tipo de enemigo:**
- `WARRIOR/BERSERKER`: ¿puede atacar? → ATACAR. ¿puede moverse hacia jugador? → MOVER. Sino ESPERAR.
- `GUARDIAN`: ¿jugador en zona fija (radio 3 del spawn)? → comportarse como Warrior. Sino ESPERAR.
- `ARCHER/SNIPER`: ¿tiene visión Y en rango? → ATACAR. ¿jugador fuera zona confort? → MOVER_A_ZONA. Sino ESPERAR.
- `DESTRUCTOR`: ¿jugador en radio 2? → AOE. Sino ESPERAR (no se mueve).
- `CONTROLLER`: ¿tiene visión Y en rango? → APLICAR_EFECTO. Sino MOVER. Sino ESPERAR.
- `SUMMONER`: ¿cooldown listo? → INVOCAR_BERSERKER. Sino MOVER (huir del jugador).

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `NodoArbol` | `raiz` | Raíz del árbol de decisión para el tipo de enemigo |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `ArbolDecisionIA(EnemyType tipo)` | Constructor. Construye el árbol de decisión para ese tipo de enemigo. | `void` |
| `decidirAccion(Enemy enemy, Room room, Player player)` | Recorre el árbol desde la raíz evaluando condiciones. Devuelve la `AccionIA` de la hoja alcanzada. | `AccionIA` |

```java
// Clase interna NodoArbol:
class NodoArbol {
    Condition condicion; // función boolean (Enemy, Room, Player) -> boolean
    NodoArbol siTrue;    // rama si condición es verdadera
    NodoArbol siFalse;   // rama si condición es falsa
    AccionIA accion;     // null si es nodo interno, AccionIA si es hoja
    boolean isHoja() { return accion != null; }
}

// Enum AccionIA:
enum AccionIA { ATACAR, MOVER, MOVER_A_ZONA, APLICAR_EFECTO, INVOCAR, AOE, ESPERAR }
```

---

### 5.5. IAEnemigo — Capa 5 — Clase estática
**Paquete:** `Valdris.logic.ai`

Ejecuta el turno de un enemigo usando `ArbolDecisionIA` para decidir la acción y `BFSCaminoMinimo` para calcular el movimiento.

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `executeTurn(Enemy enemy, Room room, Player player, CombatManager cm)` | Llamar a `procesarEfectos()`. Si PARALYSIS activo: return. Obtener acción de ArbolDecisionIA. Ejecutar la acción correspondiente. | `void` |
| `ejecutarMovimiento(Enemy enemy, Room room, Player player)` | BFSCaminoMinimo desde posición enemigo hasta posición jugador. Mover enemy tantos pasos como `enemy.getMovEfectivo()` a lo largo del camino. | `void` |
| `ejecutarAtaque(Enemy enemy, Player player, CombatManager cm)` | Si estaEnRango: `CombatManager.resolverAtaqueEnemigo()`. Si CONTROLLER: aplicar efecto aleatorio (SLOW/BLIND/CURSE) en lugar de daño. | `void` |
| `invocarBerserker(Enemy summoner, Room room)` | Crear nuevo `Enemy(BERSERKER)`. Colocarlo en celda libre cercana al Summoner. Añadirlo a `room.getEnemigos()`. `summoner.resetCooldown()`. | `void` |

---

### 5.6. TurnManager — Capa 5 — Clase
**Paquete:** `Valdris.logic.turn`

Gestiona el ciclo de turnos del juego. Orden de fases: `MOVEMENT → PICKUP → USE_ITEM → ATTACK → ENEMY_TURN`. El jugador puede ceder el turno en cualquier momento.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `Phase` | `faseActual` | Fase actual del turno |
| `Dungeon` | `dungeon` | Referencia al dungeon actual |
| `Player` | `player` | Referencia al jugador |
| `int` | `turnoGlobal` | Contador de turnos totales. Siempre incrementa. |
| `GameModel` | `modelo` | Referencia al GameModel de JavaFX para notificar cambios (puede ser null en tests) |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `TurnManager(Dungeon dungeon, Player player)` | Constructor. faseActual=MOVEMENT. turnoGlobal=0. | `void` |
| `ejecutarMovimiento(int filaDestino, int colDestino)` | Verificar faseActual==MOVEMENT y !player.haMovido. Verificar celda en BFS range. Mover jugador. | `void` |
| `ejecutarRecogida()` | Verificar PICKUP y !player.haRecogido. Buscar contenedor adyacente (4 dirs). | `void` |
| `ejecutarUsoItem(Item item)` | Verificar USE_ITEM y !player.haUsadoItem. `player.equip(item)` o `item.use(player)`. | `void` |
| `ejecutarAtaque(Enemy objetivo)` | Verificar ATTACK y !player.haAtacado. Verificar estaEnRango. `CombatManager.resolverAtaqueJugador()`. | `void` |
| `cederTurno()` | Avanzar directamente a ENEMY_TURN independientemente de la fase actual. | `void` |
| `ejecutarTurnoEnemigos()` | Verificar ENEMY_TURN. turnoGlobal++. Para cada enemy en sala: `IAEnemigo.executeTurn()`. `procesarEfectos()` del jugador. `player.resetAcciones()`. faseActual=MOVEMENT. | `void` |
| `changeRoom(Room destino)` | `dungeon.setRoomActual(destino)`. Colocar jugador en `destino.filaJugador/colJugador`. `destino.explorada=true`. | `void` |

---

### 5.7. DungeonGenerator — Capa 5 — Clase estática
**Paquete:** `Valdris.logic.generation`

Genera el Dungeon completo con todas las salas, conexiones, enemigos e items. El tamaño exacto de cada sala se elige aleatoriamente dentro del rango del tipo.

**Salas a generar (12 salas jugables + 4 pasillos = 16 en total):**
- Z1: S1-A(7x8 med), S1-B(5x6 peq), S1-C(9x10 gr), S1-D(9x9 gr), S1-SEC(5x5 peq), PASILLO-1-2(3x8)
- Z2: S2-A(6x6 peq), S2-B(7x8 med), S2-C(9x10 gr), S2-D(5x6 peq), S2-E(10x10 gr), S2-SEC(5x5 peq), PASILLO-2-3(3x8)
- Z3: S3-A(7x7 med), S3-SEC(5x6 peq) — versión reducida para 7 días
- Conexiones: ver grafo en guia_proyecto_v3. La única arista unidireccional es PASILLO-FINAL → S5-D.

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `generarMundo()` | Crea todas las salas, las conecta en el Dungeon, genera enemigos e items, devuelve Dungeon listo. | `Dungeon` |
| `generarSala(String id, String nombre, int filas, int cols, boolean hasTimer)` | Crea Room. Rellena bordes con WALL. Coloca DOOR en posiciones de conexión. | `Room` |
| `poblarEnemigos(Room room, LSE specs)` | Para cada EnemySpec: elegir subtipo aleatorio, crear Enemy, colocarlo en posición aleatoria libre. | `void` |
| `colocarItem(Room room, Item item, int fila, int col)` | `room.getCell(fila,col).setItem(item)`. | `void` |
| `elegirSubtipo(String familia)` | Familia "warrior" → aleatorio entre WARRIOR/BERSERKER/GUARDIAN. "archer" → ARCHER/SNIPER. "mage" → DESTRUCTOR/CONTROLLER/SUMMONER. | `EnemyType` |

---

### 5.8. ItemGenerator — Capa 5 — Clase estática
**Paquete:** `Valdris.logic.generation`

Fábrica de items. Genera cualquier item del juego por ID o por zona. Contiene los stats de todos los items de la guía de diseño v5.

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `crearItem(String id)` | Devuelve el item correspondiente al ID (W1-W12, A1-A8, P1-P5, AC1-AC8) con todos sus stats configurados. | `Item` |
| `itemAleatorioZona(int z)` | Devuelve un item aleatorio del pool de la zona z (1-4). Ver sección 4.7 de la guía. | `Item` |
| `crearDropEnemigo(EnemyType tipo)` | Según probabilidad del tipo (ver tabla 4.6): devuelve Item o null. | `Item` |

> - Todos los stats de items están en la `guia_diseno_v5.pdf` sección 4.2-4.5.
> - `crearItem()` debe ser un switch/if-else exhaustivo con todos los IDs. **No usar reflection.**
> - Los items son objetos nuevos cada vez (no singletons). Cada llamada crea una instancia nueva.

---

## CAPA 6 — Persistencia

### 6.1. Excepciones personalizadas — Capa 6 — Clases
**Paquete:** `Valdris.exceptions`

Tres excepciones personalizadas para cubrir los casos de error del juego. Todas extienden `Exception` (checked). Pedir a Codex que las genere todas en un mensaje.

```java
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) { super(message); }
}
public class InvalidAttackException extends Exception {
    public InvalidAttackException(String message) { super(message); }
}
public class GameStateException extends Exception {
    public GameStateException(String message) { super(message); }
}
```

- `InvalidMoveException`: lanzar cuando el jugador intenta moverse a celda no alcanzable o fuera de rango.
- `InvalidAttackException`: lanzar cuando el jugador intenta atacar a enemigo fuera de rango o sin enemigo seleccionado.
- `GameStateException`: lanzar en errores de estado (tiempo agotado, HP negativo, sala inexistente).

---

### 6.2. GameState — Capa 6 — Clase
**Paquete:** `Valdris.persistence`

Snapshot serializable del estado completo del juego. Gson serializa/deserializa esta clase a JSON. Todos los campos deben ser tipos primitivos, String, arrays o listas anidadas. Las referencias a objetos se guardan por ID (String) para evitar referencias circulares.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `String` | `idRoomActual` | ID de la sala donde está el jugador |
| `String` | `tipoPersonaje` | `CharacterType.name()` del jugador |
| `int` | `hpJugador` | HP actual del jugador |
| `String[]` | `itemsInventario` | Array de IDs de items en el inventario |
| `String` | `armaEquipada` | ID del arma equipada (null si ninguna) |
| `String` | `armaduraEquipada` | ID de armadura equipada (null si ninguna) |
| `String` | `accesorioEquipado` | ID de accesorio equipado (null si ninguno) |
| `int` | `turnoGlobal` | Turno global actual |
| `EnemyStateDTO[]` | `enemigos` | Array de estados de todos los enemigos del mundo |
| `String[]` | `itemsSuelo` | Array de `"idSala:fila:col:idItem"` para items en el suelo |

```java
// EnemyStateDTO (clase interna estática de GameState):
static class EnemyStateDTO {
    String idSala;
    String tipoEnemigo; // EnemyType.name()
    int fila, col;
    int hp;
    boolean vivo;
    String dropItemId; // null si no tiene drop
}
```

---

### 6.3. LectorJSON — Capa 6 — Clase estática
**Paquete:** `Valdris.persistence`

Serialización y deserialización del GameState usando Gson.

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `guardarPartida(Dungeon dungeon, Player player, TurnManager tm, String rutaArchivo)` | Crear GameState desde los objetos. `Gson.toJson(gameState, writer)`. Envolver en try-catch IOException. Lanzar `GameStateException` si falla. | `void` |
| `cargarPartida(String rutaArchivo)` | `Gson.fromJson(reader, GameState.class)`. Reconstruir Dungeon con DungeonGenerator. Restaurar estado de enemigos. Restaurar inventario del jugador. Devolver array `[Dungeon, Player, int turnoGlobal]`. | `Object[]` |
| `extraerGameState(Dungeon d, Player p, TurnManager tm)` | Método auxiliar que construye el GameState desde los objetos del juego. | `GameState` |
| `reconstruirDesdeGameState(GameState gs)` | Método auxiliar que reconstruye los objetos del juego desde el GameState. | `Object[]` |

> - **CRÍTICO:** Al deserializar, los enemigos con `vivo=false` deben eliminarse de la sala (no recrearlos).
> - Los items del inventario se recrean llamando a `ItemGenerator.crearItem(id)` para cada ID guardado.
> - Usar try-with-resources para `FileReader` y `FileWriter`.
> - Lanzar `GameStateException` (no IOException) para que la capa de UI no dependa de `java.io`.

---

## CAPA 7 — Interfaz JavaFX (MVC)

> - Patrón MVC: `GameModel` (datos) → `GameView` (visualización) → `GameController` (eventos).
> - `GameModel` implementa el patrón Observer: `GameView` se registra como listener y se actualiza cuando cambia el estado.
> - **NUNCA** llamar a lógica del juego directamente desde `GameView`. Siempre pasar por `GameController`.
> - La UI solo lee datos de `GameModel`. Nunca modifica `Player`, `Room` o `Enemy` directamente.

---

### 7.1. MainApp — Capa 7 — JavaFX
**Paquete:** `Valdris.ui`

Punto de entrada de la aplicación JavaFX. `start()` muestra primero `CharacterSelectView`.

```java
public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Valdris: El Núcleo Profundo");
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setResizable(false);
        CharacterSelectView selectView = new CharacterSelectView(primaryStage);
        primaryStage.setScene(selectView.getScene());
        primaryStage.show();
    }
    public static void main(String[] args) { launch(args); }
}
```

---

### 7.2. GameModel — Capa 7 — MVC Model
**Paquete:** `Valdris.ui.model`

Contiene referencias a todos los objetos del juego y notifica a los listeners cuando algo cambia.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `Dungeon` | `dungeon` | El dungeon actual |
| `Player` | `player` | El jugador |
| `TurnManager` | `turnManager` | El gestor de turnos |
| `LSE[GameModelListener]` | `listeners` | Lista de objetos que escuchan cambios |
| `String` | `ultimoMensaje` | Último mensaje del log de combate |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `GameModel(CharacterType tipo)` | Constructor. `DungeonGenerator.generarMundo()`. new Player(tipo). new TurnManager(dungeon, player). | `void` |
| `addListener(GameModelListener l)` | Añadir listener a la LSE. | `void` |
| `notificarCambio()` | Para cada listener en LSE: `listener.onEstadoCambiado(this)`. | `void` |
| `notificarMensaje(String msg)` | `ultimoMensaje = msg`. `notificarCambio()`. | `void` |
| `getDungeon(), getPlayer(), getTurnManager()` | Getters estándar. | varios |

```java
// Interfaz listener (en el mismo paquete):
public interface GameModelListener {
    void onEstadoCambiado(GameModel modelo);
}
```

---

### 7.3. GameView — Capa 7 — MVC View
**Paquete:** `Valdris.ui.view`

Vista principal del juego. Layout `BorderPane`: Centro=GridPane con el mapa, Derecha=panel lateral, Inferior=CombatLogView.

**Atributos:**

| Tipo | Atributo | Descripción |
|------|----------|-------------|
| `BorderPane` | `root` | Layout raíz de la vista |
| `GridPane` | `gridSala` | Grid donde se renderizan las celdas de la sala |
| `VBox` | `panelLateral` | Panel derecho con stats del jugador |
| `Label` | `labelHP` | Muestra HP actual / HP máximo |
| `Label` | `labelFase` | Muestra la fase actual del turno |
| `HBox` | `barraRapida` | 4-5 slots de items equipados y pociones |
| `CombatLogView` | `logCombate` | Panel inferior con mensajes de combate |

**Métodos:**

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `GameView(GameModel modelo, GameController controller)` | Constructor. Construir layout. `modelo.addListener(this)`. | `void` |
| `onEstadoCambiado(GameModel modelo)` | Llamado por GameModel. Redibujar sala, actualizar panel lateral, actualizar barra rápida. | `void` |
| `renderizarSala(Room room)` | Limpiar gridSala. Para cada celda: crear Rectangle 40x40px con color según CellType. Añadir sprites de unidades e items encima. | `void` |
| `resaltarCeldas(LSE[Cell] celdas)` | Añadir overlay verde transparente a las celdas alcanzables por BFS. | `void` |
| `limpiarResaltado()` | Eliminar todos los overlays de resaltado. | `void` |
| `getScene()` | Devuelve `new Scene(root, 1280, 720)`. | `Scene` |

**Colores y sprites:**
- FLOOR=gris claro, WALL=gris oscuro, DOOR=marrón, DOOR_HIDDEN=igual que WALL, TRAP=rojo muy oscuro
- Kael=azul, Syra=verde, Dorath=morado, enemigos=rojo
- Items en suelo: punto amarillo pequeño sobre la celda
- Celda resaltada (BFS): overlay verde 30% opacidad
- Cada Rectangle del grid tiene `setOnMouseClicked()` que notifica al GameController

---

### 7.4. GameController — Capa 7 — MVC Controller
**Paquete:** `Valdris.ui.controller`

Recibe todos los eventos de la UI y los traduce a llamadas a `TurnManager`. Es el **único** que llama a métodos de TurnManager.

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `onCeldaClick(int fila, int col)` | Según fase actual: MOVEMENT → `turnManager.ejecutarMovimiento(f,c)` catch InvalidMoveException. ATTACK → buscar enemigo en celda y `turnManager.ejecutarAtaque(enemigo)` catch InvalidAttackException. | `void` |
| `onBotonCederTurno()` | `turnManager.cederTurno()`. `turnManager.ejecutarTurnoEnemigos()`. | `void` |
| `onBotonGuardar()` | `LectorJSON.guardarPartida()`. `modelo.notificarMensaje("Partida guardada.")`. | `void` |
| `onBotonCargar()` | `LectorJSON.cargarPartida()`. Reconstruir modelo. `notificarCambio()`. | `void` |
| `onBotonInventario()` | Abrir `InventoryView` en nueva ventana modal. Solo disponible en fase USE_ITEM. | `void` |
| `onItemBarraRapidaClick(Item item)` | `turnManager.ejecutarUsoItem(item)` si fase==USE_ITEM. | `void` |
| `onBotonNuevaPartida()` | Crear nuevo GameModel. Actualizar vistas. | `void` |

---

### 7.5. CharacterSelectView — Capa 7 — Vista
**Paquete:** `Valdris.ui.view`

Pantalla de inicio donde el jugador elige su personaje.

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `CharacterSelectView(Stage stage)` | Constructor. Construir layout con 3 botones de personaje. | `void` |
| `crearBotonPersonaje(CharacterType tipo)` | VBox con nombre, stats (HP/MOV/RANGO) y botón Elegir. | `VBox` |
| `iniciarJuego(CharacterType tipo)` | new GameModel(tipo). new GameController(modelo). new GameView(modelo, controller). `stage.setScene(view.getScene())`. | `void` |
| `getScene()` | Devuelve la Scene de selección. | `Scene` |

---

### 7.6. InventoryView — Capa 7 — Vista
**Paquete:** `Valdris.ui.view`

Ventana modal de inventario completo. Lado izquierdo: silueta del personaje con 4 slots de equipo. Lado derecho: lista de todos los items del inventario.

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `InventoryView(GameModel modelo, GameController controller)` | Constructor. Construir layout. | `void` |
| `mostrarSilueta()` | BorderPane con 4 Labels/Rectangles para los slots: ARMA, ESCUDO, TORSO, ACCESORIO. | `Node` |
| `mostrarInventario()` | ListView o VBox con todos los items del inventario. Cada fila: nombre + tipo + botón Usar. | `Node` |
| `show()` | Abrir en nueva Stage modal (`initModality(APPLICATION_MODAL)`). | `void` |

---

### 7.7. CombatLogView — Capa 7 — Vista
**Paquete:** `Valdris.ui.view`

Panel inferior que muestra los últimos mensajes de combate. Máximo 5 mensajes visibles.

| Firma | Descripción | Retorna |
|-------|-------------|---------|
| `CombatLogView()` | Constructor. VBox con 5 Labels inicialmente vacíos. | `void` |
| `addMensaje(String msg)` | Desplazar labels hacia arriba. Poner msg en el último label. | `void` |
| `onEstadoCambiado(GameModel m)` | Si `m.getUltimoMensaje() != null`: `addMensaje()`. | `void` |
| `getNode()` | Devuelve el VBox raíz para incluir en el layout principal. | `Node` |

---

## Tests JUnit5 — Especificaciones completas

> - Escribir los tests **DESPUÉS** de que Codex genere cada clase.
> - Estructura de cada test: Arrange (preparar datos) → Act (ejecutar método) → Assert (verificar resultado).
> - No testear métodos privados directamente. Testear el comportamiento público.
> - Las clases JavaFX (Capa 7) **NO** se testean con JUnit. Solo las capas 2-6.

---

### T.1. CellTest
**Paquete:** `Valdris.model.map`

| Nombre del test | Qué verifica |
|----------------|--------------|
| `testIsWalkable_FLOOR_sinUnidad` | Cell FLOOR sin unidad → isWalkable() == true |
| `testIsWalkable_WALL` | Cell WALL → isWalkable() == false siempre |
| `testIsWalkable_conUnidad` | Cell FLOOR con unidad → isWalkable() == false |
| `testIsWalkable_DOOR_LOCKED` | Cell DOOR_LOCKED → isWalkable() == false |
| `testRevelar_DOOR_HIDDEN` | Cell DOOR_HIDDEN → revelar() → comportarse como DOOR |
| `testRemoveItem_devuelveItem` | Colocar item, removeItem() → devuelve el item y la celda queda vacía |

### T.2. RoomTest
**Paquete:** `Valdris.model.map`

| Nombre del test | Qué verifica |
|----------------|--------------|
| `testConstructor_creaMatrizCorrecta` | Room 7x8 → getCell(0,0) existe, getCell(6,7) existe |
| `testGetCell_fueraDeRango` | getCell(-1,0) → lanza InvalidMoveException |
| `testAddEnemigo_colocaEnCelda` | addEnemigo(enemy) → getCell(f,c).getUnit() == enemy |
| `testRemoveEnemigo_limpiaCelda` | addEnemigo + removeEnemigo → celda.getUnit() == null |
| `testDecrementarTimer_lanzaExcepcion` | Room con timer=1 → decrementarTimer() → GameStateException |

### T.3. BFSMovimientoTest
**Paquete:** `Valdris.logic.bfs`

| Nombre del test | Qué verifica |
|----------------|--------------|
| `testGetCellsInRange_salaAbierta` | Sala 5x5 FLOOR, origen centro, mov=2 → al menos 8 celdas alcanzables |
| `testGetCellsInRange_bloqueadoPorWall` | WALL entre origen y destino → destino no en lista |
| `testGetCellsInRange_bloqueadoPorUnidad` | Unidad en celda adyacente → esa celda no en lista |
| `testGetCamino_encuentraCamino` | Camino entre dos celdas sin obstáculos → lista no vacía, primer elemento = origen |
| `testGetCamino_sinCamino` | Destino rodeado de WALL → lista vacía |
| `testGetCamino_longitudCorrecta` | Camino recto de 3 celdas → lista.getSize() == 3 |

### T.4. LineaDeVisionTest
**Paquete:** `Valdris.logic.vision`

| Nombre del test | Qué verifica |
|----------------|--------------|
| `testTieneVision_lineaRecta` | Dos celdas en misma fila sin obstáculos → true |
| `testTieneVision_bloqueadaPorWall` | WALL entre dos celdas → false |
| `testTieneVision_diagonal` | Dos celdas en diagonal sin obstáculos → true |
| `testTieneVision_mismaCelda` | Misma celda origen y destino → true |

### T.5. CombatManagerTest
**Paquete:** `Valdris.logic.combat`

| Nombre del test | Qué verifica |
|----------------|--------------|
| `testCalcularDanio_rangoValido` | calcularDanio 1000 veces → siempre >= 0 |
| `testCalcularDanio_conPenetracion` | Arma pen=5, defensor def=4 → def efectiva=0 → daño=ataque*aleatorio |
| `testCalcularDanio_defensaMayorAtaque` | Ataque bajo, defensa alta → daño == 0 |
| `testResolverAtaqueJugador_reducaHP` | HP enemigo disminuye tras ataque válido |
| `testResolverAtaqueJugador_mataEnemigo` | HP enemigo <= 0 → !enemigo.isVivo() |
| `testEstaEnRango_dentro` | Unidades adyacentes con rango 1 → true |
| `testEstaEnRango_fuera` | Unidades a distancia 5 con rango 1 → false |

### T.6. PlayerTest
**Paquete:** `Valdris.model.units`

| Nombre del test | Qué verifica |
|----------------|--------------|
| `testConstructor_KAEL` | Player(KAEL) → hp=110, movBase=3, rango=1 |
| `testConstructor_SYRA` | Player(SYRA) → hp=75, movBase=5, rango=3 |
| `testConstructor_DORATH` | Player(DORATH) → hp=80, movBase=2, rango=4 |
| `testCurar_noSuperaHpMax` | HP=50, curar(100) → hp==hpMax |
| `testRecibirDanio_noNegativo` | HP=10, recibirDanio(20) → hp==0 |
| `testGetAtaqueTotal_conArmaYAfinidad` | Equipar W1(Kael): ataque = 16+4 = 20 |
| `testGetDefensaTotal_conArmadura` | Equipar A2(+6 def): defensaTotal >= 6 |
| `testGetMovEfectivo_conSlow` | Syra (mov5) + SLOW → getMovEfectivo() == 3 |
| `testResetAcciones` | Marcar todas las acciones como usadas → resetAcciones() → todas false |
| `testTieneInmunidad_escudoRaices` | Equipar A3 → tieneInmunidad(SLOW) == true |

### T.7. EnemyTest
**Paquete:** `Valdris.model.units`

| Nombre del test | Qué verifica |
|----------------|--------------|
| `testConstructor_WARRIOR` | Enemy(WARRIOR,...) → hp=35, ataque=15, def=8, mov=2 |
| `testConstructor_GUARDIAN` | Enemy(GUARDIAN,...) → hp=50, def=10, mov=1 |
| `testIsCooldownListo` | turnosSinActuar=2, isCooldownListo(2) → true |
| `testOnDeath_colocaDropEnCelda` | Enemy con dropItem → onDeath(room) → celda.getItem() != null |
| `testOnDeath_sinDrop` | Enemy sin dropItem → onDeath(room) → celda.getItem() == null |

### T.8. ItemTest
**Paquete:** `Valdris.model.items`

| Nombre del test | Qué verifica |
|----------------|--------------|
| `testWeapon_getDanoEfectivo_conAfinidad` | W1 con Kael: getDanoEfectivo(KAEL) == 20 |
| `testWeapon_getDanoEfectivo_sinAfinidad` | W1 con Syra: getDanoEfectivo(SYRA) == 16 |
| `testWeapon_use_equipaEnJugador` | W1.use(player) → player.getArmaEquipada() == W1 |
| `testPotion_use_curaHP` | Jugador HP=50, P1.use(player) → hp==75 |
| `testPotion_use_noSuperaMax` | Jugador HP=100, hpMax=110, P2.use(player) → hp==110 |
| `testArmor_use_equipaEnSlot` | A2.use(player) → player.getArmaduraEquipada() == A2 |
| `testArmor_escudo_equipaEnSecundaria` | A1(escudo).use(player) → player.getEscudoEquipado() == A1 |
| `testAccessory_use_equipaEnSlot` | AC5.use(player) → player.getAccesorioEquipado() == AC5 |

### T.9. TurnManagerTest
**Paquete:** `Valdris.logic.turn`

| Nombre del test | Qué verifica |
|----------------|--------------|
| `testFaseInicial_MOVEMENT` | new TurnManager → faseActual == MOVEMENT |
| `testEjecutarMovimiento_avanzaFase` | Movimiento válido → faseActual == PICKUP |
| `testEjecutarMovimiento_celdaInvalida` | Celda WALL → lanza InvalidMoveException |
| `testCederTurno_vaAENEMY_TURN` | cederTurno() → faseActual == ENEMY_TURN |
| `testEjecutarTurnoEnemigos_incrementaTurno` | ejecutarTurnoEnemigos() → turnoGlobal == 1 |
| `testEjecutarAtaque_fueraDeRango` | Enemigo fuera de rango → lanza InvalidAttackException |
| `testResetAcciones_trasEnemyTurn` | Tras ejecutarTurnoEnemigos → player.haMovido == false |

### T.10. GameStateTest
**Paquete:** `Valdris.persistence`

| Nombre del test | Qué verifica |
|----------------|--------------|
| `testGuardarYCargar_hpJugador` | Guardar con HP=75, cargar → HP==75 |
| `testGuardarYCargar_roomActual` | Guardar en sala S1-A, cargar → roomActual.getId()=="S1-A" |
| `testGuardarYCargar_inventario` | Guardar con W1 en inventario, cargar → inventario contiene W1 |
| `testGuardarYCargar_enemigoMuerto` | Matar enemigo, guardar, cargar → ese enemigo no está en la sala |
| `testCargar_archivoInexistente` | Ruta inválida → lanza GameStateException |
| `testGuardar_creaArchivo` | guardarPartida() → el archivo existe en disco |

### T.11. DungeonGeneratorTest
**Paquete:** `Valdris.logic.generation`

| Nombre del test | Qué verifica |
|----------------|--------------|
| `testGenerarMundo_noNull` | generarMundo() → dungeon != null |
| `testGenerarMundo_salasSuficientes` | El dungeon tiene al menos 12 salas |
| `testGenerarMundo_salaS1A_existe` | getRoomById("S1-A") != null |
| `testGenerarMundo_conexionBidireccional` | S1-A conectada a S1-B y viceversa |
| `testGenerarMundo_enemigosEnSala` | S1-A tiene al menos 1 enemigo |

---

## Instrucciones de uso con Codex

- **ORDEN OBLIGATORIO**: seguir las fichas en el orden numerado. Nunca saltar capas.
- **Una ficha = un mensaje a Codex.** No mezclar dos fichas en el mismo prompt.
- Siempre empezar el prompt con: `"Proyecto: Valdris El Nucleo Profundo. Java 21. Sin java.util.* para estructuras."`
- Siempre terminar el prompt con: `"Usa LSE[T] en lugar de ArrayList, Cola en lugar de Queue, Grafo en lugar de Map para conexiones."`
- Tras cada clase: compilar. Si hay errores de compilación, corregirlos antes de pasar a la siguiente ficha.
- Tras cada clase: ejecutar los tests de esa clase. Si fallan, pedir a Codex que corrija.

### Plantilla de prompt para Codex

```
Proyecto: Valdris El Nucleo Profundo. Java 21. Sin java.util.* para listas/colas/grafos.
Usa siempre: ListaSimplementeEnlazada<T>, Cola<T>, Grafo[DN,DA] (implementaciones propias).

Genera la clase [NOMBRE_CLASE] en el paquete [PAQUETE].

Atributos:
[copiar sección de atributos de la ficha]

Métodos:
[copiar sección de métodos de la ficha]

Notas adicionales:
[copiar info_box de la ficha si existe]

Genera también el test JUnit5 [NOMBRE_TEST] con estos casos:
[copiar tests de la ficha]
```

---

## Checklist de integración por capa

| Capa | Verificar antes de pasar a la siguiente |
|------|-----------------------------------------|
| Capa 1 | LSE, Cola y Grafo compilan. Grafo.caminoMinimo() devuelve lista no vacía en test básico. |
| Capa 2 | Todos los enums y clases base compilan. Effect.isExpired() funciona. Weapon.getDanoEfectivo() devuelve valor correcto. |
| Capa 3 | Player(KAEL).getMovEfectivo() == 3. Player con SLOW → getMovEfectivo() == 2. Enemy(WARRIOR).isVivo() == true. |
| Capa 4 | Room(7,8): getCell(0,0) ok, getCell(7,8) lanza excepción. Cell WALL → isWalkable()==false. |
| Capa 5 | BFS en sala 5x5 devuelve celdas correctas. CombatManager.calcularDanio() siempre >= 0. TurnManager pasa por todas las fases. |
| Capa 6 | guardarPartida() crea archivo JSON. cargarPartida() reconstruye el mismo HP y sala. GameStateException se lanza correctamente. |
| Capa 7 | MainApp abre ventana 1280x720. CharacterSelectView muestra 3 personajes. GameView renderiza sala. Click en celda mueve jugador. |

---

*Guía de Especificaciones para Codex — Valdris: El Núcleo Profundo — H12GEXTRA — 50 clases — Java 21 + JavaFX*
