# PROJECT_SPEC.md — Especificacion del Proyecto
## Valdris: El Nucleo Profundo
### Grupo H12GEXTRA | Java 21 + JavaFX | Entrega: 28 mayo 2026

---

## Descripcion general

Juego de turnos por celdas en JavaFX. El jugador explora un dungeon de salas interconectadas en grafo,
combate enemigos con IA, recoge items y progresa hasta enfrentarse al jefe final.

**Un solo personaje por partida.** El jugador elige al inicio y no puede cambiar.
**El mapa se genera al inicio y se serializa a JSON.** Al morir se recarga ese JSON (estado inicial).

---

## Documentacion de referencia

| Documento            | Contenido                                              |
|----------------------|--------------------------------------------------------|
| `guia_codex.pdf`     | Ficha de implementacion de cada clase (usar con Codex) |
| `guia_diseno_v5.pdf` | Sistema de turnos, IA, mapa, items, balanceo completo  |
| `guia_proyecto_v3.pdf` | Narrativa, mundo, personajes, arquitectura tecnica   |
| `plan_7dias.pdf`     | Calendario de desarrollo dia a dia                     |

---

## Tecnologias y dependencias

- **Java 21**
- **JavaFX** (incluido en el proyecto)
- **Gson 2.10.1** — unica libreria externa permitida (solo para JSON)
- **JUnit 5** — tests unitarios
- **MisEstructurasDeDatos** — LSE, Cola, Pila, Grafo del repositorio anterior (NO modificar)

---

## Personajes jugables

### Kael — Guerrero
- HP: 110 | Ataque base: 18 | Mov: 3 | Rango: 1
- Arma inicial: Espada Oxidada (W1) — daño 16, afinidad Kael +4
- Estilo: tanque cuerpo a cuerpo, alta resistencia

### Syra — Exploradora
- HP: 75 | Ataque base: 12 | Mov: 5 | Rango: 3
- Arma inicial: Arco de Madera (W2) — daño 10, afinidad Syra +4
- Estilo: agil, distancia media, alta movilidad

### Dorath — Mago
- HP: 80 | Ataque base: 14 | Mov: 2 | Rango: 4
- Arma inicial: Baston Astillado (W3) — daño 12, afinidad Dorath +4
- Estilo: largo alcance, daño magico con penetracion de armadura

---

## Sistema de turnos

Orden de fases (cada turno del jugador):
```
MOVEMENT -> PICKUP -> USE_ITEM -> ATTACK -> ENEMY_TURN
```
- Maximo 1 accion de cada tipo por turno
- El jugador puede ceder el turno en cualquier momento (salta a ENEMY_TURN)
- Movimiento: BFS 4 direcciones, diagonal = 2 pasos
- Combate: `danio = max(0, ataque * rand[0.5-1.5] - defensaEfectiva)`

---

## Mapa — Dungeon como Grafo

- **Grafo bidireccional** `Grafo<Room, String>` con 34 salas en total
- **Unica arista unidireccional**: Pasillo Final → S5-D (punto de no retorno)
- **5 zonas**: Los Campos Grises, El Bosque de Lireth, Las Minas de Karath, La Torre de Embrath, El Nucleo Profundo
- **Tipos de sala**: Pequeña (5-6x5-6), Mediana (7-8x7-9), Grande (9-10x9-11), Pasillo (3x8 fijo)
- **Salas secretas**: acceso por DOOR_HIDDEN, excepto S5-SEC (requiere Fragmento de Sello)

---

## Enemigos — 8 subtipos

| Enemigo       | Tipo        | Mov | Rng | Daño | Def | HP | Especial                    |
|---------------|-------------|-----|-----|------|-----|----|-----------------------------|
| Warrior       | Base Warrior| 2   | 1   | 15   | 8   | 35 | BFS hacia jugador           |
| Berserker     | Sub Warrior | 4   | 1   | 13   | 3   | 25 | Carga directa               |
| Guardian      | Sub Warrior | 1   | 1   | 15   | 10  | 50 | Zona fija radio 3           |
| Archer        | Base Archer | 3   | 4   | 10   | 4   | 28 | Zona confort + linea vision |
| Francotirador | Sub Archer  | 2   | 5   | 18   | 3   | 28 | Cooldown 2 turnos           |
| Destructor    | Sub Mage A  | 0   | 5   | 6x   | 5   | 40 | AOE radio 2                 |
| Controlador   | Sub Mage B  | 2   | 3   | 4    | 4   | 35 | Efecto aleatorio 2 turnos   |
| Invocador     | Sub Mage C  | 2   | 0   | 0    | 6   | 45 | Invoca Berserkers c/2t      |

---

## Items — resumen

### Armas (12 total — ver guia_diseno_v5 seccion 4.2)
- W1-W3: armas iniciales de cada personaje
- W4-W5: cofre S1-SEC (Espada Larga para Kael / Punal del Errante para Syra)
- W6-W7: cofre S2-SEC (Arco Elfico para Syra / Tomo de Llamas para Dorath)
- W8-W9: cofre S3-SEC (Martillo de Mina / Baston Arcano)
- W10-W12: cofre S4-SEC — 3 armas legendarias, una por personaje

### Armaduras (8 total — A1-A8)
- A6 Coraza de Karath (+8 def) — item legendario, drop del Golem en S3-F

### Pociones (5 tipos — P1-P5)
- Cura desde +25 HP (P1) hasta +60 HP (P3)

### Accesorios (8 total — AC1-AC8)
- AC1-AC4: items de progresion narrativa (drops garantizados de mini-bosses)
- AC5-AC8: items de combate (drops probabilisticos)

---

## Persistencia JSON

- Al iniciar nueva partida: `DungeonGenerator.generarMundo()` → `LectorJSON.guardarPartida()`
- Al morir: `LectorJSON.cargarPartida()` → reconstruir estado inicial
- Al guardar manualmente: sobrescribe el archivo con el estado actual
- Formato: `GameState` serializado con Gson en `partida.json`

---

## Criterios de evaluacion cubiertos

| Criterio                        | Como se cubre                                              |
|---------------------------------|------------------------------------------------------------|
| Diseno OO, herencia, polimorfismo | Unit→Player/Enemy, Item→Weapon/Armor/Potion/Accessory    |
| Gestion de excepciones          | 3 excepciones personalizadas + try-catch en toda la logica |
| JSON serialización              | GameState + LectorJSON con Gson                            |
| JavaFX funcional                | GameView + GameController + MVC basico                     |
| LSE / Cola / Pila               | Inventario, BFS, efectos de estado, log de combate         |
| Grafo + BFS                     | Dungeon como Grafo, BFSMovimiento obligatorio              |
| Dijkstra                        | Disponible en Grafo del repo, usar si hay tiempo           |
| Arboles                         | ArbolDecisionIA para logica de enemigos                    |
| Tests JUnit5                    | Una suite por clase no visual, cobertura >= 70%            |
| Uso de IA                       | COMMIT_LOG.md + seccion en memoria                         |

---

## Notas criticas de implementacion

1. **BFS usa `Cola<int[]>`** donde `int[] = {fila, col, pasosUsados}`. Nunca `java.util.Queue`.
2. **Los efectos de estado** se guardan en `LSE<Effect>` dentro de `Unit`. Se decrementan al final de cada turno.
3. **El Guardian** defiende zona fija radio 3 desde su posicion de spawn. Guardar `filaSpawn` y `colSpawn`.
4. **El Destructor** hace 6 de daño por celda en AOE radio 2. No usa la formula estandar.
5. **El Invocador** invoca un Berserker cada 2 turnos. Usar `turnosSinActuar` como contador.
6. **Las referencias circulares** en GameState se evitan guardando IDs (String) en lugar de objetos.
7. **JavaFX no se testea** con JUnit. Solo capas 2-6.
