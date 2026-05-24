# AGENTS.md — Instrucciones para Codex
## Proyecto: Valdris: El Nucleo Profundo
### Grupo H12GEXTRA — Java 21 + JavaFX

---

## REGLAS ABSOLUTAS — leer antes de generar cualquier codigo

1. **NUNCA usar `java.util.*` para estructuras de datos.**
   - En lugar de `ArrayList` → usar `ListaSimplementeEnlazada<T>`
   - En lugar de `Queue` / `LinkedList` → usar `Cola<T>`
   - En lugar de `HashMap` / `Map` → usar `Grafo<DN,DA>` o arrays indexados
   - En lugar de `Stack` → usar la `Pila<T>` del repo
   - `java.util.Scanner`, `java.util.Random` y `java.util.Optional` SÍ están permitidos

2. **Respetar el orden de capas.** Nunca importar una capa superior desde una inferior:
   - Capa 2 (model/enums, model/effects, model/items) → solo Java puro
   - Capa 3 (model/units) → puede usar Capa 2 + LSE
   - Capa 4 (model/map) → puede usar Capas 2 y 3
   - Capa 5 (logic/) → puede usar Capas 2, 3 y 4
   - Capa 6 (persistence/) → puede usar Capas 2-5 + Gson
   - Capa 7 (ui/) → puede usar todo

3. **Una clase por fichero.** No meter dos clases publicas en el mismo `.java`.

4. **Encapsulamiento estricto.** Todos los atributos `private`. Getters y setters donde sea necesario. Nunca atributos `public` salvo en DTOs de GameState.

5. **Excepciones personalizadas.** Usar siempre `InvalidMoveException`, `InvalidAttackException` o `GameStateException` en lugar de lanzar excepciones genericas. Envolver en `try-catch` donde corresponda.

6. **Sin magia ni atajos.** No usar reflection, no usar streams de java.util, no usar lambdas complejas que oculten la logica. El codigo debe ser legible y explicable por un estudiante.

7. **Javadoc obligatorio** en todas las clases publicas y metodos publicos. Al menos una linea de descripcion.

---

## Estructura del proyecto

```
src/
├── MisEstructurasDeDatos/       ← NO MODIFICAR. Contiene LSE, Cola, Pila, Grafo.
└── Valdris/
    ├── model/
    │   ├── enums/               ← CellType, EffectType, ItemType, CharacterType, EnemyType, Phase
    │   ├── effects/             ← Effect
    │   ├── items/               ← Item (abstracta), Weapon, Armor, Potion, Accessory
    │   ├── units/               ← Unit (abstracta), Player, Enemy
    │   └── map/                 ← Cell, Room, Dungeon
    ├── logic/
    │   ├── bfs/                 ← BFSMovimiento, BFSCaminoMinimo
    │   ├── vision/              ← LineaDeVision
    │   ├── combat/              ← CombatManager
    │   ├── ai/                  ← IAEnemigo, ArbolDecisionIA
    │   ├── turn/                ← TurnManager
    │   └── generation/          ← DungeonGenerator, ItemGenerator
    ├── persistence/             ← GameState, LectorJSON
    ├── exceptions/              ← InvalidMoveException, InvalidAttackException, GameStateException
    └── ui/
        ├── MainApp.java
        ├── model/               ← GameModel, GameModelListener
        ├── view/                ← GameView, CharacterSelectView, InventoryView, CombatLogView
        └── controller/          ← GameController

test/Valdris/                    ← Tests JUnit5. Espeja src/ sin el paquete ui/
```

---

## Como trabajar con este proyecto

### Flujo obligatorio por cada clase

1. Leer la ficha de la clase en `guia_codex.pdf`
2. Generar la clase con el prompt correspondiente
3. Compilar — si hay errores, corregirlos antes de continuar
4. Ejecutar los tests de esa clase — si fallan, corregir
5. Hacer commit con mensaje descriptivo
6. Pasar a la siguiente ficha

### Orden de implementacion

Seguir estrictamente este orden. No saltarse ni adelantar clases:

**Bloque 1 — Enums y clases base (empezar aqui)**
- [ ] CellType, EffectType, ItemType, CharacterType, EnemyType, Phase
- [ ] Effect
- [ ] Item (abstracta), Weapon, Armor, Potion, Accessory

**Bloque 2 — Unidades**
- [ ] Unit (abstracta)
- [ ] Player
- [ ] Enemy

**Bloque 3 — Mapa**
- [ ] Cell
- [ ] Room
- [ ] Dungeon

**Bloque 4 — Logica**
- [ ] BFSMovimiento
- [ ] BFSCaminoMinimo
- [ ] LineaDeVision
- [ ] CombatManager
- [ ] ArbolDecisionIA
- [ ] IAEnemigo
- [ ] TurnManager
- [ ] ItemGenerator
- [ ] DungeonGenerator

**Bloque 5 — Persistencia**
- [ ] InvalidMoveException, InvalidAttackException, GameStateException
- [ ] GameState
- [ ] LectorJSON

**Bloque 6 — JavaFX**
- [ ] MainApp
- [ ] GameModelListener, GameModel
- [ ] CharacterSelectView
- [ ] GameView
- [ ] GameController
- [ ] InventoryView
- [ ] CombatLogView

---

## Plantilla de prompt para cada clase

Usar exactamente esta estructura al pedir cada clase a Codex:

```
Proyecto: Valdris El Nucleo Profundo. Java 21.
REGLAS: Sin java.util.* para estructuras. Usar ListaSimplementeEnlazada<T>, Cola<T>, Grafo<DN,DA>.
Paquete: [PAQUETE_EXACTO]

Genera la clase [NOMBRE_CLASE].

Atributos:
[pegar tabla de atributos de la ficha]

Metodos:
[pegar tabla de metodos de la ficha]

Notas:
[pegar notas adicionales de la ficha si las hay]

Genera tambien el test JUnit5 [NOMBRE_TEST] con estos casos de prueba:
[pegar lista de tests de la ficha]
```

---

## Stats de referencia rapida

### Personajes
| Personaje | HP | Ataque base | Mov | Rango |
|-----------|-----|-------------|-----|-------|
| KAEL      | 110 | 18          | 3   | 1     |
| SYRA      | 75  | 12          | 5   | 3     |
| DORATH    | 80  | 14          | 2   | 4     |

### Formula de combate
```
aleatorio = Math.random() * 1.0 + 0.5   // rango [0.5, 1.5]
defEfectiva = max(0, defensor.getDefensaTotal() - arma.getPenetracion())
danio = max(0, (int)(atacante.getAtaqueTotal() * aleatorio) - defEfectiva)
```

### Efectos de estado
| Efecto    | Duracion | Impacto                              |
|-----------|----------|--------------------------------------|
| SLOW      | 2 turnos | mov = ceil(movBase / 2.0)            |
| BLIND     | 2 turnos | 25% de fallo de ataque               |
| CURSE     | 2 turnos | +3 danio de ataques enemigos recibidos |
| PARALYSIS | 1 turno  | sin movimiento ni ataque             |
| BURN      | 1 turno  | +3 danio al inicio del turno         |

---

## Lo que NO debe hacer Codex nunca

- No generar metodos `main()` en clases que no sean `MainApp`
- No usar `System.out.println()` en logica del juego (solo en tests y Main)
- No crear clases que no esten en la guia sin consultar primero
- No modificar nada dentro de `MisEstructurasDeDatos/`
- No generar codigo con `TODO` sin implementar — si algo no esta claro, preguntar
- No usar `instanceof` masivamente — usar polimorfismo y metodos abstractos
