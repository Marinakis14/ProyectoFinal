# Guía de Diseño v4.0 — Edición Final
## Valdris: El Núcleo Profundo
**Documento operativo completo — Puntos 1, 2 y 3**

Proyecto Valdris: El Núcleo Profundo — Juego por Turnos JavaFX  
Grupo H12GEXTRA — Marcos Castro · Ventura Pacheco · Marino Rodríguez  
Versión v4.0 Final — Sistema de turnos + IA enemiga + Mapa completo (33 salas)  
*Companion: Guía de Proyecto v3.0 (narrativa, arquitectura, advertencias técnicas)*

**Contenido completo:**
- Punto 1: Sistema de turnos — fases, acciones, walkability, fórmula de combate, contadores.
- Punto 2: IA enemiga — 8 subtipos con stats, pseudocódigo executeTurn() y notas.
- Punto 3: Mapa de contenidos — 33 salas, grafos por zona, tamaños, enemigos y contenidos.
- Puntos 4 y 5 (tabla de items y plan de 17 días) se añaden en la siguiente sesión.

---

## Punto 1 — Sistema de Turnos Completo
*Referencia definitiva para implementar TurnManager.*

### 1.0 Diagrama de flujo de un turno

```
INICIO DE TURNO
│
▼
FASE 1 - MOVIMIENTO
  BFS 4 dir. cardinales → jugador elige destino
  ├── ¿El jugador se mueve?
  │     └── SÍ → ¿Item en suelo en celda destino? → Popup: recoger/equipar/dejar
  │             ¿Celda es DOOR o STAIRS? → Cambiar sala (onRoomEnter())
  └── NO → salta a Fase 2
│
▼
FASE 2 - RECOGIDA (opcional)
  Contenedor contiguo → recoger/equipar/dejar
│
▼
FASE 3 - USO DE ITEM (opcional)
  Max 1/turno — usar arma = equipar (no atacar)
│
▼
FASE 4 - ATAQUE (opcional)
  max(0, ataque*(rand 0.5-1.5) - defensa)
│
▼
TURNO DE ENEMIGOS (Cola de Unit)
  executeTurn() — defensa automática del jugador
│
▼
VERIFICAR CONDICIONES DE FIN
  HP · turnos sala · turnos globales · sala despejada
  ├── Sala OK/boss → Victoria → avanzar sala
  └── HP=0/turnos=0 → Derrota
│
▼
INCREMENTAR TURNO
  globalTurnsLeft-- · roomTurnsLeft-- (si hasRoomTimer)
  [LOOP]
```

---

### 1.1 Acciones disponibles

| Acción | Max/turno | Restricción | Notas |
|--------|-----------|-------------|-------|
| Movimiento | 1 | Siempre primera | BFS 4 dir. Diagonal = 2 pasos BFS. |
| Recogida | 1 | Fase 1 (suelo) o Fase 2 (contenedor contiguo) | Item suelo: popup auto. Contenedor: acción manual Fase 2. |
| Uso de item | 1 | Solo inventario. Usar arma = equipar | No poción Y ataque con arma mismo turno. |
| Ataque | 1 | Enemigo en rango BFS del arma | Sin arma: rango 1, daño base. |
| Ceder turno | — | En cualquier momento | Pasa al turno enemigo. |

**Caso especial — equipar vs atacar:**
- Usar arma del inventario (Fase 3) = equiparla. **NO** es atacar.
- Atacar con arma ya equipada (Fase 4) SÍ es la acción de ataque.
- Se puede recoger una espada y atacar con ella en el mismo turno.

---

### 1.2 Fases del turno — orden fijo

**Fase 1 — Movimiento**  
El jugador elige moverse o no. Siempre va primero.
- `BFSMovimiento.getCellsInRange()` calcula celdas alcanzables.
- JavaFX resalta celdas. El jugador hace clic en destino.
- Si celda es DOOR/STAIRS: `TurnManager.changeRoom()`.
- Si celda tiene item en suelo: popup automático recoger/equipar/dejar.
- Si no se mueve: salta a Fase 2.

**Fase 2 — Recogida (opcional)**  
Solo si hay contenedor (`requiresAdjacent=true`) en celda contigua.
- TurnManager comprueba 4 celdas adyacentes (N,S,E,O).
- Si hay contenedor contiguo: ofrece recoger/equipar/guardar/ignorar.
- Si no hay contenedor: fase se salta.

> Item en suelo = se pisa (Fase 1). Contenedor = celda adyacente, acción manual (Fase 2).

**Fase 3 — Uso de item (opcional)**  
El jugador puede usar un item del inventario.
- El jugador selecciona item del panel lateral JavaFX.
- `Item.use(player)` ejecuta el efecto.
- Usar arma = equiparla. No cuenta como ataque.
- Solo 1 item por turno.

**Fase 4 — Ataque (opcional)**  
El jugador puede atacar un enemigo en rango.
- El jugador selecciona enemigo en rango.
- Daño = `max(0, ataque*(rand[0.5-1.5]) - defensa)`.
- Si muere: `Enemy.onDeath()` con `dropItem` si tiene.
- Solo 1 ataque por turno.

---

### 1.3 Walkability y movimiento cardinal

| Condición | Transitable | Bloqueado |
|-----------|-------------|-----------|
| Tipo de celda | FLOOR, DOOR, DOOR_HIDDEN(activa), RUNE, LEVER, STAIRS | WALL, DOOR_LOCKED |
| Contenido | unit==null Y (item==null O !requiresAdjacent) | unit!=null O item.requiresAdjacent==true |

```java
// Cell.java
public boolean isWalkable() {
    if (type==WALL || type==DOOR_LOCKED) return false;
    if (unit != null) return false;
    if (item != null && item.isRequiresAdjacent()) return false;
    return true;
}
```

---

### 1.4 Fórmula de combate

**Fórmula oficial de daño:**
```
vida_defensor = vida_defensor - máximo(0, ataque * aleatorio - defensa)
aleatorio = Math.random() * 1.0 + 0.5  →  rango [0.5, 1.5]
ataque = ataque base + modificadores arma + otros
defensa = defensa base + modificadores armadura + otros
```

```java
public int calcularDanio(Unit atacante, Unit defensor) {
    double aleatorio = Math.random() * 1.0 + 0.5;
    int ataque = atacante.getAtaqueTotal();
    int defensa = defensor.getDefensaTotal();
    return Math.max(0, (int)(ataque * aleatorio) - defensa);
}
```

| Caso | Aleatorio | Ejemplo (atq=20, def=5) | Resultado |
|------|-----------|------------------------|-----------|
| Mínimo | 0.5 | max(0,20*0.5-5)=5 | 5 de daño |
| Normal | 1.0 | max(0,20*1.0-5)=15 | 15 de daño |
| Crítico | 1.5 | max(0,20*1.5-5)=25 | 25 de daño |
| Bloqueado | 0.5 | max(0,20*0.5-15)=0 | 0 de daño |

---

### 1.5 Contadores y condiciones de fin

| Contador | Dónde | Cuándo decrementa | Derrota si |
|----------|-------|-------------------|------------|
| Turnos globales | `TurnManager.globalTurnsLeft` | Siempre al fin de turno | ==0 |
| Turnos de sala | `Room.roomTurnsLeft` | Solo si `Room.hasRoomTimer==true` | ==0 |

| Condición | Resultado | Quién detecta |
|-----------|-----------|---------------|
| HP jugador <= 0 | Derrota | TurnManager tras takeDamage |
| globalTurnsLeft == 0 | Derrota | TurnManager fin de turno |
| roomTurnsLeft == 0 | Derrota | TurnManager fin de turno |
| Todos los enemigos muertos | Sala despejada | TurnManager tras onDeath |
| ParasitoEnemy muerto (Zona 5) | Victoria | TurnManager.triggerEnding |

---

### 1.6 TurnManager — estructura

```java
public class TurnManager {
    private Cola<Unit> turnQueue;
    private LSE<String> log;
    private GameState gameState;
    private int globalTurnsLeft;
    private TurnPhase currentPhase;

    public enum TurnPhase {
        PLAYER_MOVE, PLAYER_PICKUP, PLAYER_USE_ITEM, PLAYER_ATTACK,
        ENEMY_TURNS, CHECK_CONDITIONS, TURN_END
    }

    public void nextTurn() { ... }
    public void addLog(String msg) { ... }
    public void onRoomEnter() { ... }
    public void changeRoom(String dir) { ... }
    public void triggerEnding(CharacterType t) { ... }
    public void reconstruirReferencias() { ... }
}
```

---

### 1.7 División de trabajo

| Miembro | Bloque | Qué incluye |
|---------|--------|-------------|
| Marino | Motor + integración | Modelo completo (Fase 1), lógica (Fase 2: BFS, TurnManager, combate, acertijos), integración JavaFX. |
| Pacheco | Persistencia + tests | Fase 3 (LectorJSON, GameState, serialización plana). Tests JUnit desde día 1. |
| Castro | Interfaz JavaFX | Fase 4: GridPane, paneles, selección personaje, diálogos, finales. Empieza día 10. |

**Regla de oro para Castro (JavaFX):**
- Los Controllers **NO** pueden contener lógica de juego.
- Un Controller solo llama a métodos de TurnManager o Player.
- Si el método devuelve un int o modifica un modelo: no va en el Controller.

---

## Punto 2 — IA Enemiga por Tipo y Subtipo
*Define el comportamiento exacto de cada tipo y subtipo de enemigo.*

### Principios generales
- Todas las IAs usan **BFS real** sobre `Cell[][]`.
- El BFS del jugador se cachea al inicio del turno de enemigos — 1 BFS por turno, no N.
- `BFSMovimiento.getCellsInRange(row,col,range,room)` se reutiliza para movimiento Y rango de ataque.
- Movimiento cardinal (4 dir.). Diagonal = 2 pasos BFS naturalmente.
- Línea de visión (Bresenham) requerida para Archer, Francotirador, Destructor y Controlador.

---

### 2.1 Línea de visión — Bresenham

```java
public boolean tieneLineaDeVision(int r1, int c1, int r2, int c2, Room room) {
    int dr = Math.abs(r2-r1), dc = Math.abs(c2-c1);
    int sr = (r1 < r2) ? 1 : -1, sc = (c1 < c2) ? 1 : -1;
    int err = dr - dc;
    int r = r1, c = c1;
    while (r != r2 || c != c2) {
        if (r != r1 || c != c1) {
            if (room.getCell(r,c).getType() == CellType.WALL) return false;
        }
        int e2 = 2 * err;
        if (e2 > -dc) { err -= dc; r += sr; }
        if (e2 < dr)  { err += dr; c += sc; }
    }
    return true;
}
```

---

### 2.2 Sistema de efectos de estado

```java
public class Efecto implements Comparable<Efecto> {
    public enum TipoEfecto { SLOW, BLIND, CURSE }
    private TipoEfecto tipo;
    private int turnosRestantes;
}

// TurnManager — inicio de cada turno del jugador:
public void decrementarEfectos(Player p) {
    LSE<Efecto> ef = p.getEfectosActivos();
    for (int i = 0; i < ef.getSize(); i++) {
        ef.get(i).decrementarTurno();
        if (ef.get(i).getTurnosRestantes() == 0) ef.del(ef.get(i));
    }
}
```

| Efecto | Duración | Impacto | Implementación |
|--------|----------|---------|----------------|
| SLOW | 2 turnos | movePoints = `Math.max(1, Math.ceil(original/2.0))` | setMovePointsModificado(). Al expirar: restaurar. |
| BLIND | 2 turnos | movePoints = `Math.ceil(original/2.0)` — Kael(3)→2, Syra(5)→3, Dorath(2)→1 | Mismo mecanismo. JavaFX no muestra resaltado BFS. |
| CURSE | 2 turnos | Daño recibido x1.5 | En calcularDanio(): `if tieneEfecto(CURSE) daño*=1.5` |

---

### 2.3 Familia Warrior

#### Warrior — Subtipo Base
*Persigue al jugador por BFS y ataca si queda adyacente.*

| Stat | Valor | Stat | Valor |
|------|-------|------|-------|
| movePoints | 2 | attackRange | 1 |
| damage | 15 | defense | 8 |
| HP base | 40 | línea visión | No |

```
executeTurn(player, room):
  camino = BFS(miPos, playerPos, room)
  mover min(movePoints, camino.size()-1) pasos
  si distBFS(miPos, playerPos) == 1: atacar(player)
```

#### Berserker — Subtipo Warrior
*Más rápido y agresivo. Muy frágil. Invocado por el Mage Invocador.*

| Stat | Valor | Stat | Valor |
|------|-------|------|-------|
| movePoints | 4 | attackRange | 1 |
| damage | 13 | defense | 3 |
| HP base | 25 | línea visión | No |

```
executeTurn(player, room):
  camino = BFS(miPos, playerPos, room)
  mover min(movePoints, camino.size()-1) pasos directo
  si distBFS(miPos, playerPos) == 1: atacar(player)
```
> Invocado por el Mage Invocador con stats reducidos.

#### Guardian — Subtipo Warrior
*Defiende zona fija. Si jugador a BFS<=3 se acerca; si se aleja, vuelve a guardPosition.*

| Stat | Valor | Stat | Valor |
|------|-------|------|-------|
| movePoints | 1 | attackRange | 1 |
| damage | 15 | defense | 18 |
| HP base | 60 | línea visión | No |

```
executeTurn(player, room):
  dist = distBFS(miPos, playerPos, room)
  si dist <= 3:
    mover 1 paso; si dist==1: atacar(player)
  sino si miPos != guardPosition:
    mover 1 paso hacia guardPosition
```
> guardPosition asignado en spawn. Nunca más de 3 celdas BFS de ella.

---

### 2.4 Familia Archer

#### Archer — Subtipo Base
*Zona de confort entre distancia mínima y máxima. Huye si el jugador se acerca. Línea de visión obligatoria.*

| Stat | Valor | Stat | Valor |
|------|-------|------|-------|
| movePoints | 3 | attackRange | 4 |
| minComfortRange | 2 | damage | 10 |
| defense | 4 | HP base | 30 |
| línea visión | Sí | | |

```
executeTurn(player, room):
  dist = distBFS(miPos, playerPos, room)
  si dist < minComfortRange:
    mover en dirección opuesta al jugador
  sino si dist <= attackRange Y lineaVision(...):
    atacar(player)
  sino:
    mover min(movePoints, pasos) acercándose
```
> Dirección opuesta = invertir el primer paso del BFS hacia el jugador.

#### Francotirador — Subtipo Archer
*Mayor rango y daño. Dispara cada 2 turnos. El turno de cooldown lo usa para posicionarse.*

| Stat | Valor | Stat | Valor |
|------|-------|------|-------|
| movePoints | 2 | attackRange | 5 |
| minComfortRange | 4 | damage | 15 |
| defense | 3 | HP base | 28 |
| cooldown | 2 turnos | línea visión | Sí |

```
executeTurn(player, room):
  si cooldownRestante > 0: cooldownRestante--; posicionarse()
  sino:
    si dist <= attackRange Y lineaVision(...):
      atacar(player); cooldownRestante = 2
    sino: posicionarse()
```
> posicionarse() = misma lógica que Archer base con minComfortRange=4.

---

### 2.5 Familia Mage

#### Destructor — Subtipo Mage A (Área)
*No se mueve. Lanza AOE centrado en el jugador radio 2. No discrimina entre aliados y enemigos.*

| Stat | Valor | Stat | Valor |
|------|-------|------|-------|
| movePoints | 0 | attackRange | 5 |
| aoeRadius | 2 | damage | 8/celda |
| defense | 5 | HP base | 45 |
| línea visión | Sí — centro AOE | | |

```
executeTurn(player, room):
  si distBFS(miPos, playerPos) <= attackRange
     Y lineaVision(miPos, playerPos):
    para cada celda en room:
      si distManhattan(celda, playerPos) <= aoeRadius:
        si celda.getUnit() != null:
          celda.getUnit().takeDamage(calcularDanio(this, celda.getUnit()))
```
> distManhattan para el radio AOE. Línea de visión solo para el centro.

#### Controlador — Subtipo Mage B (Efectos)
*Bajo daño. Aplica efecto aleatorio (SLOW/BLIND/CURSE). Si jugador ya tiene el efecto: reinicia duración.*

| Stat | Valor | Stat | Valor |
|------|-------|------|-------|
| movePoints | 2 | attackRange | 3 |
| damage | 4 | defense | 4 |
| HP base | 35 | efectoDuración | 2 turnos |
| línea visión | Sí | | |

```
executeTurn(player, room):
  si dist <= attackRange Y lineaVision(...):
    player.takeDamage(calcularDanio(this, player))
    efecto = elegirEfectoAleatorio()
    player.addEfecto(new Efecto(efecto, 2))
  sino: mover hacia player
```

#### Invocador — Subtipo Mage C (Invocaciones)
*No ataca. Invoca Berserkers cada 2 turnos en celdas vacías adyacentes.*

| Stat | Valor | Stat | Valor |
|------|-------|------|-------|
| movePoints | 2 | attackRange | 0 |
| damage | 0 | defense | 6 |
| HP base | 50 | cooldown | 2 turnos |
| línea visión | No | | |

```
executeTurn(player, room):
  cooldownRestante--
  si cooldownRestante <= 0:
    celdas = getCeldasVaciasAdyacentes(miPos, radio=2)
    si celdas no vacía:
      mejor = celdaMásCercanaA(celdas, playerPos)
      berserker = new Berserker(mejor.row, mejor.col, room.getId())
      room.addEnemy(berserker); turnQueue.addEnd(berserker)
      cooldownRestante = 2
  mover min(movePoints, pasos) hacia player
```
> Solo invoca en celdas `isWalkable()==true`. Berserker actúa desde el siguiente turno.

---

### 2.6 Tabla resumen

| Enemigo | Tipo | Move | Rng | Daño | Def | HP | Especial |
|---------|------|------|-----|------|-----|----|---------|
| Warrior | Base Warrior | 2 | 1 | 15 | 8 | 40 | BFS hacia jugador |
| Berserker | Sub Warrior | 4 | 1 | 13 | 3 | 25 | Carga directa — invocado |
| Guardian | Sub Warrior | 1 | 1 | 15 | 18 | 60 | Zona fija radio 3 |
| Archer | Base Archer | 3 | 4 | 10 | 4 | 30 | Zona confort — línea visión |
| Francotirador | Sub Archer | 2 | 5 | 15 | 3 | 28 | Cooldown 2 turnos |
| Destructor | Sub Mage A | 0 | 5 | 8x | 5 | 45 | AOE radio 2 |
| Controlador | Sub Mage B | 2 | 3 | 4 | 4 | 35 | Efecto aleatorio 2 turnos |
| Invocador | Sub Mage C | 2 | 0 | 0 | 6 | 50 | Invoca Berserkers c/2 turnos |

---

## Punto 3 — Mapa de Contenidos por Zona
*Las 33 salas del juego como Grafo bidireccional. Generación aleatoria una vez al inicio, serializada a JSON.*

### Principios del mapa
- **Grafo bidireccional.** El jugador puede volver a cualquier sala visitada.
- **Excepción única:** arista unidireccional Pasillo Final → S5-D (punto de no retorno).
- Enemigos fijos desde la generación inicial. Estado persistente durante la partida.
- Salas secretas: DOOR_HIDDEN, excepto S5-SEC que requiere Fragmento de Sello.
- Pasillos de transición: 3x8 fijo, hasRoomTimer=false, 1 item aleatorio zona siguiente.

---

### 3.1 Tipos de sala

| Tipo | Rango filas | Rango cols | Cuándo se usa |
|------|-------------|------------|---------------|
| Pequeña | 5-6 | 5-6 | Transición, secretas, salas de item |
| Mediana | 7-8 | 7-9 | Combate estándar — la mayoría del juego |
| Grande | 9-10 | 9-11 | Acertijos, mini-boss, salas finales |
| Pasillo | 3 (fijo) | 8 (fijo) | Entre zonas. Sin enemigos, sin timer |

---

### 3.2 Zona 1 — Los Campos Grises

```
S1-A (Aldea) ── S1-B (Camino) ── S1-D (Ayuntamiento)
                    │
                  [oculta]
                  S1-SEC (Molino)

S1-C (Puente) conectado al conjunto
PASILLO 1→2 hacia Zona 2
```

| ID | Nombre | Tipo | Tamaño | Enemigos | Contenido especial |
|----|--------|------|--------|----------|-------------------|
| S1-A | Aldea Abandonada | Mediana | 7x8 | 2 Warrior, 1 Archer | Tutorial implícito. Primera sala del juego |
| S1-B | El Camino Roto | Pequeña | 5x6 | 1 Warrior, 1 Archer | Bifurcación hacia S1-SEC. Introduce movimiento táctico |
| S1-C | El Puente Gris | Grande | 9x10 | 1 Controlador | Acertijo palancas del puente. Sin timer |
| S1-D | Ayuntamiento Corrupto | Grande | 9x9 | Mini-boss: Alcalde + 2 Warrior | Suelta Llave de Hierro al morir |
| S1-SEC | El Molino | Pequeña | 5x5 | Ninguno | Cofre Poción de Fuerza. DOOR_HIDDEN desde S1-B |
| PASILLO 1→2 | Linde del Bosque | Transición | 3x8 | Ninguno | Item aleatorio Zona 2. hasRoomTimer=false |

---

### 3.3 Zona 2 — El Bosque de Lireth

```
PASILLO 1→2 ── S2-A (Entrada) ── S2-B (Claro) ── S2-C (Laberinto)
                                      │
                                   S2-D (Sendero)
                                      │
                                  [oculta]
                                  S2-SEC (Raíces)

S2-E (Corazón) — boss de zona
PASILLO 2→3 hacia Zona 3
```

| ID | Nombre | Tipo | Tamaño | Enemigos | Contenido especial |
|----|--------|------|--------|----------|-------------------|
| S2-A | Entrada del Bosque | Pequeña | 6x6 | 1 Archer, 1 Warrior | Syra tiene diálogo adicional |
| S2-B | Claro Corrompido | Mediana | 7x8 | 2 Archer, 1 Destructor | Bifurcación: S2-C y S2-D |
| S2-C | Laberinto de Raíces | Grande | 9x10 | 1 Controlador | Acertijo laberinto vegetal. Sin timer |
| S2-D | Sendero Oscuro | Pequeña | 5x6 | 2 Warrior, 1 Berserker | Primer Berserker del juego |
| S2-E | Corazón del Bosque | Grande | 10x10 | Mini-boss: Espíritu Madre + 2 Archer | Suelta Semilla Resonante |
| S2-SEC | Raíces Profundas | Pequeña | 5x5 | 1 Guardian | Cofre Armadura Ligera. DOOR_HIDDEN desde S2-B |
| PASILLO 2→3 | Boca de la Mina | Transición | 3x8 | Ninguno | Item aleatorio Zona 3. hasRoomTimer=false |

---

### 3.4 Zona 3 — Las Minas de Karath (3 pisos)

```
PASILLO 2→3 ── S3-A (Entrada -1) ── S3-B (Vagoneta -1)
                                          │ escaleras
                                     S3-C (Túnel -2) ── S3-D (Cristal -2)
                                          │        └── [oculta] S3-SEC (Cámara)
                                     escaleras
                                          │
                                     S3-E (Profund. -3) ── S3-F (Golem -3)
                                                                │
                                                          PASILLO 3→4
```

| ID | Nombre | Piso | Tamaño | Enemigos | Contenido especial |
|----|--------|------|--------|----------|-------------------|
| S3-A | Entrada de la Mina | -1 | 7x7 | 2 Warrior, 1 Berserker | Primera sala piso superior |
| S3-B | Sala de Vagonetas | -1 | 9x10 | 1 Invocador | Acertijo mecanismo vagoneta piso 1 |
| S3-C | Túnel Central | -2 | 8x8 | 2 Guardian, 1 Archer | Escaleras a -1 y -3. Acceso a S3-SEC |
| S3-D | Cámara de Cristal | -2 | 7x9 | 2 Destructor, 1 Berserker | Sala difícil — dos Destructores |
| S3-E | Profundidades | -3 | 9x9 | 3 Warrior, 1 Francotirador | Primer Francotirador del juego |
| S3-F | Cámara del Golem | -3 | 10x11 | Mini-boss: Golem + 2 Guardian | Suelta Fragmento de Sello |
| S3-SEC | Cámara Enana | -2 | 5x6 | Ninguno | Cofre Arma Zona 3. DOOR_HIDDEN desde S3-C |
| PASILLO 3→4 | Base de la Torre | — | 3x8 | Ninguno | Item aleatorio Zona 4. hasRoomTimer=false |

---

### 3.5 Zona 4 — La Torre de Embrath

```
PASILLO 3→4 ── S4-A (Planta B.) ── S4-B (Biblioteca) ── S4-C (Runas)
                                         │             └── S4-D (Cámara A.)
                                      [oculta]
                                      S4-SEC (Celda D.)

S4-E (Cúspide) — boss de zona
PASILLO 4→5 hacia Zona 5
```

| ID | Nombre | Tipo | Tamaño | Enemigos | Contenido especial |
|----|--------|------|--------|----------|-------------------|
| S4-A | Planta Baja | Mediana | 7x8 | 2 Constructo (Warrior), 1 Controlador | Kael tiene diálogo adicional aquí |
| S4-B | Biblioteca | Mediana | 8x8 | 2 Francotirador, 1 Guardian | Acceso a S4-C, S4-D y S4-SEC |
| S4-C | Sala de Runas | Grande | 9x10 | 1 Destructor, 1 Controlador | Acertijo suelo de runas. Sin timer |
| S4-D | Cámara Alta | Pequeña | 6x6 | 3 Berserker | Sala rápida e intensa antes del boss |
| S4-E | Cúspide de la Torre | Grande | 10x10 | Mini-boss: Guardian Sin Nombre + 2 Constructo | Suelta Fragmento de Voluntad. Maestro de Kael |
| S4-SEC | La Celda de Dorath | Pequeña | 5x5 | Ninguno | Dorath tiene diálogos adicionales. Suelta Pergamino Sellado. DOOR_HIDDEN desde S4-B |
| PASILLO 4→5 | Descenso al Núcleo | Transición | 3x8 | Ninguno | Item aleatorio Zona 5. hasRoomTimer=false |

---

### 3.6 Zona 5 — El Núcleo Profundo

```
PASILLO 4→5 ── S5-A (Antecam.) ── S5-B (Corredor) ── S5-SEC (Memorias)
                                        │
                                   S5-C (El Filtro)
                                        │
                               PASILLO FINAL (solo ida)
                                        │
                                    S5-D (EL NÚCLEO)
```

| ID | Nombre | Tipo | Tamaño | Enemigos | Contenido especial |
|----|--------|------|--------|----------|-------------------|
| S5-A | Antecámara | Mediana | 7x7 | 2 Sombra Absorbida, 1 Eco de Magia | Primera sala del final |
| S5-B | Corredor de Sombras | Pequeña | 5x8 | 3 Sombra Absorbida, 1 Eco de Magia | Acceso a S5-SEC |
| S5-C | La Puerta del Filtro | Grande | 9x9 | El Filtro (Mage especial) | Último combate antes de Malachar |
| S5-SEC | Cámara de Memorias | Pequeña | 5x5 | Ninguno | Lore de Malachar. Abre con Fragmento de Sello |
| PASILLO FINAL | El Umbral | Transición | 3x8 | Ninguno | Aviso punto de no retorno. hasRoomTimer=false |
| S5-D | El Núcleo | Grande | 10x11 | Malachar (diálogo) + ParasitoEnemy | Sala final. Conversación → batalla → desenlace |

**Pasillo Final — arista unidireccional:**
- La arista Pasillo Final → S5-D es la **ÚNICA** unidireccional del grafo.
- `Dungeon.connect(pasillo, S5-D, 'adelante')` sin arista de retorno.
- TurnManager muestra aviso explícito al jugador al entrar al Pasillo Final.
- Desde S5-D: `Dungeon.getAdjacentRooms()` no devuelve el pasillo.

---

### 3.7 Resumen global

| Zona | Normales | Secretas | Pasillos | Total | Predominante |
|------|----------|----------|----------|-------|--------------|
| Campos Grises | 4 | 1 | 1 | 6 | Mediana/Grande |
| Bosque de Lireth | 5 | 1 | 1 | 7 | Mediana/Grande |
| Minas de Karath | 6 | 1 | 1 | 8 | Mediana/Grande (3 pisos) |
| Torre de Embrath | 5 | 1 | 1 | 7 | Mediana/Grande |
| Núcleo Profundo | 4 | 1 | 1 | 6 | Pequeña/Grande |
| **TOTAL** | **24** | **5** | **5** | **34** | — |

---

### 3.8 Generación aleatoria y serialización

| Aleatorio al generar | Siempre fijo |
|---------------------|--------------|
| Tamaño exacto (dentro del rango del tipo) | Número de salas y sus conexiones |
| Subtipo de enemigo por sala | Tipo base de enemigo por sala |
| Item concreto en pasillos de transición | Cada pasillo tiene exactamente 1 item |
| Posición de spawn de enemigos | Qué enemigos aparecen en cada sala |

```java
// Nueva partida:
GameState estado = DungeonGenerator.generarMundo();
LectorJSON.guardar(estado, "partida_nueva.json");

// Al morir y recargar:
GameState estado = LectorJSON.cargar("partida_nueva.json");
TurnManager.reconstruirReferencias(estado);
```

---

*Guía de Diseño v4.0 Final — Puntos 1, 2 y 3 — Valdris: El Núcleo Profundo — Grupo H12GEXTRA*  
*Próximo: Punto 4 — Tabla de items · Punto 5 — Plan de 17 días*
