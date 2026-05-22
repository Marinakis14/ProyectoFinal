# Guía de Diseño v5.0 — Edición Final
## Valdris: El Núcleo Profundo
**Documento operativo completo — Puntos 1, 2, 3 y 4**

Proyecto Valdris: El Núcleo Profundo — Juego por Turnos JavaFX  
Grupo H12GEXTRA — Marcos Castro · Ventura Pacheco · Marino Rodríguez  
Versión v5.0 Final — Sistema turnos + IA + Mapa + Items completos + Balanceo verificado  
*Companion: Guía de Proyecto v3.0 (narrativa, arquitectura, advertencias técnicas)*

- Punto 1: Sistema de turnos — fases, acciones, walkability, fórmula de combate, contadores.
- Punto 2: IA enemiga — 8 subtipos con stats, pseudocódigo executeTurn() y notas.
- Punto 3: Mapa de contenidos — 34 salas, grafos por zona, tamaños, enemigos y contenidos.
- **Punto 4: 12 armas balanceadas, tabla de items completa, stats finales de enemigos y mini-bosses.**
- Punto 5 (plan de 17 días) se añade en la siguiente sesión.

> Los Puntos 1, 2 y 3 son idénticos a los de la Guía v4.0. Ver `guia_diseno_v4_final.md` para referencia completa.

---

## Punto 4 — Items y Balanceo Final

### 4.0 Personajes — referencia de stats

> - HP máximo **FIJO** durante toda la partida. Solo se juega con **UN** personaje elegido al inicio.
> - Cualquier personaje puede coger cualquier arma. La **afinidad** determina el rendimiento.
> - La defensa del jugador varía exclusivamente por equipamiento (armaduras y escudos).

| Personaje | HP | Daño base | Mov | Rango | Arma inicial | Estilo |
|-----------|-----|-----------|-----|-------|--------------|--------|
| Kael | 110 | 18 | 3 | 1 | Espada Oxidada (W1) | Tanque — cuerpo a cuerpo |
| Syra | 75 | 12 | 5 | 3 | Arco de Madera (W2) | Ágil — distancia media |
| Dorath | 80 | 14 | 2 | 4 | Bastón Astillado (W3) | Mago — largo alcance |

---

### 4.1 Sistema de inventario y equipamiento

| Slot | Acepta | Max |
|------|--------|-----|
| Mano principal | Arma (espada, arco, bastón) | 1 |
| Mano secundaria | Escudo o arma secundaria | 1 |
| Torso | Armadura | 1 |
| Accesorio | Anillo, amuleto, llave activa | 1 |

- **Barra rápida:** 4-5 slots visibles en pantalla principal (items equipados + pociones).
- **Inventario completo:** pantalla separada con silueta del personaje. Solo accesible en Fase 3. Pausa el juego.
- Equipar accesorio consume la acción de Fase 3. Abrir puertas con llave ocurre en Fase 1 (movimiento).
- Sin durabilidad de items. Sin límite de capacidad de inventario más allá de los 4 slots de equipo.

---

### 4.2 Las 12 armas — tabla completa y balanceada

> - El daño del arma **REEMPLAZA** el daño base del personaje (no se suma).
> - **Afinidad:** bonus o penalización al daño base según el personaje que lo usa.
> - Cofres S1/S2/S3-SEC contienen 2 armas. Cofre S4-SEC contiene 3 armas legendarias (una por personaje).
> - Las armas legendarias son ligeramente mejores que las de Z3 y están calibradas para Z5.

#### Armas iniciales

| ID | Nombre | Daño base | Kael | Syra | Dorath | Ataque efectivo | Efecto |
|----|--------|-----------|------|------|--------|----------------|--------|
| W1 | Espada Oxidada | 16 | +4 | — | — | 20 / 16 / 16 | — |
| W2 | Arco de Madera | 10 | — | +4 | — | 10 / 14 / 10 | — |
| W3 | Bastón Astillado | 12 | — | — | +4 | 12 / 12 / 16 | — |

*Ataque efectivo = Kael / Syra / Dorath (con afinidad aplicada)*

#### Armas de zona (cofres secretos)

| ID | Nombre | Zona | Cofre | Base | Kael | Syra | Dorath | Penetración | Efecto especial |
|----|--------|------|-------|------|------|------|--------|-------------|----------------|
| W4 | Espada Larga | Z1 | S1-SEC | 18 | +6 | — | — | — | — |
| W5 | Puñal del Errante | Z1 | S1-SEC | 14 | — | +5 | — | — | Rango 1 (cuerpo a cuerpo) |
| W6 | Arco Élfico | Z2 | S2-SEC | 16 | — | +6 | — | — | Aplica SLOW 1 turno |
| W7 | Tomo de Llamas | Z2 | S2-SEC | 15 | — | — | +4 | 2 def | Ignora 2 def + 25% quema 1t |
| W8 | Martillo de Mina | Z3 | S3-SEC | 19 | +4 | -2 | -2 | 5 def | Ignora 5 def enemigo |
| W9 | Bastón Arcano | Z3 | S3-SEC | 16 | — | — | +8 | 4 def | Ignora 4 def + 30% parálisis |

#### Armas legendarias (S4-SEC — 3 opciones, una por personaje)

> - Único cofre del juego con 3 armas. El jugador elige la que corresponde a su personaje.
> - Ligeramente mejores que las armas de Z3. Calibradas específicamente para Z5.
> - Efectos dobles: combinan penetración con efecto de estado para maximizar utilidad en el tramo final.

| ID | Nombre | Personaje | Base | Afinidad | Atk efectivo | Penetración | Efecto especial |
|----|--------|-----------|------|----------|--------------|-------------|----------------|
| W10 | Espada del Vacío | Kael | 22 | +6 | 28 | 5 def | Ignora 5 def + 20% BLIND |
| W11 | Arco del Eclipse | Syra | 20 | +6 | 26 | — | Aplica SLOW + BLIND simultáneos |
| W12 | Grimorio Abismal | Dorath | 19 | +8 | 27 | 5 def | Ignora 5 def + 30% parálisis |

#### Progresión de ataque efectivo por zona y personaje

| Zona | Kael | Syra | Dorath | Nota |
|------|------|------|--------|------|
| Inicial | 20 | 14 | 16 | Armas de inicio, sin mejora disponible |
| Z1 | 24 | 19 | 16 | Kael y Syra mejoran. Dorath conserva arma inicial |
| Z2 | 24 | 22 | 17+pen2 | Syra salta con Arco Élfico. Dorath obtiene su primera arma propia |
| Z3 | 23+pen5 | 22 | 24+pen4 | Kael y Dorath ganan penetración. Syra mantiene Arco Élfico |
| Z4 (leg.) | 28+pen5 | 26 | 27+pen5 | Salto final: todas las armas con efectos dobles |

*pen = penetración de armadura (el arma ignora N puntos de defensa del enemigo)*

---

### 4.3 Escudos y Armaduras

| ID | Nombre | Zona | Defensa | Efecto especial | Ubicación |
|----|--------|------|---------|----------------|-----------|
| A1 | Escudo de Madera | Z1 | +4 def | — | Pool aleatorio Z1 |
| A2 | Cota de Malla | Z1 | +6 def | — | Cofre S1-B o S1-C |
| A3 | Escudo de Raíces | Z2 | +5 def | Inmune a SLOW | Pool aleatorio Z2 |
| A4 | Armadura de Cuero Élfico | Z2 | +7 def | — | Cofre secreto S2-SEC |
| A5 | Escudo de Piedra | Z3 | +8 def | — | Pool aleatorio Z3 |
| A6 | Coraza de Karath | Z3 | +8 def | Item legendario | Drop garantizado S3-F (Golem) |
| A7 | Manto Arcano | Z4 | +9 def | Absorbe 2 daño mágico | Pool aleatorio Z4 |
| A8 | Manto de los Cinco Sellos | Z4 | +8 def | Inmune a CURSE | Cofre S4-D |

---

### 4.4 Pociones

| ID | Nombre | Cura HP | Efecto extra | Drop prob. | Disponible desde |
|----|--------|---------|-------------|------------|-----------------|
| P1 | Poción Pequeña | +25 HP | — | 40% | Todas las zonas |
| P2 | Poción Mediana | +40 HP | — | 50% | Z2 en adelante |
| P3 | Poción Grande | +60 HP | — | 60% | Z3 en adelante |
| P4 | Antídoto | +0 HP | Elimina CURSE y BLIND | 30% | Z3 en adelante |
| P5 | Elixir de Combate | +20 HP | +5 ataque próximo turno | Raro | Z4 en adelante |

---

### 4.5 Accesorios

| ID | Nombre | Efecto | Tipo | Ubicación |
|----|--------|--------|------|-----------|
| AC1 | Llave de Hierro | Abre puertas Z1 | Progresión | Drop garantizado mini-boss Z1 |
| AC2 | Semilla Resonante | Resalta celdas con trampa en radio 2 (efecto pasivo en JavaFX) | Progresión | Drop garantizado mini-boss Z2 |
| AC3 | Fragmento de Sello | Acceso a S5-SEC | Progresión | Drop garantizado mini-boss Z3 |
| AC4 | Fragmento de Voluntad | Potencia habilidad especial del personaje | Progresión | Drop garantizado mini-boss Z4 |
| AC5 | Amuleto de Fuerza | +4 ataque mientras equipado | Combate | Pool aleatorio Z2/Z3 |
| AC6 | Anillo de Velocidad | +1 mov mientras equipado | Combate | Pool aleatorio Z2/Z3 |
| AC7 | Amuleto Arcano | +4 ataque mágico, ignora 2 def enemigo | Combate | Pool aleatorio Z4 |
| AC8 | Sello Roto | Reduce daño recibido en 3 | Combate | Pool aleatorio Z3/Z4 |

> Items en amarillo = item de progresión narrativa. Drop **GARANTIZADO** al matar el mini-boss correspondiente.

---

### 4.6 Sistema de drops y pools aleatorios

#### Drops por tipo de enemigo

| Tipo enemigo | Probabilidad | Qué suelta |
|-------------|-------------|------------|
| Warrior / Berserker | 40% | Poción Pequeña (P1) o material |
| Guardian | 50% | Poción Mediana (P2) o armadura baja |
| Archer / Francotirador | 45% | Poción Pequeña (P1) o material |
| Destructor / Controlador | 60% | Item mágico o Poción Mediana (P2) |
| Invocador | 70% | Item raro de zona o Poción Grande (P3) |
| Mini-boss (todos) | **100%** | Accesorio de progresión AC1-AC4 garantizado |

#### Pools aleatorios por zona

| Zona | Items disponibles en el pool | Selección |
|------|------------------------------|-----------|
| Z1 — Los Campos Grises | Escudo de Madera (A1), Poción Pequeña (P1), Amuleto de Fuerza (AC5) | 1 de 3 aleatorio |
| Z2 — El Bosque de Lireth | Escudo de Raíces (A3), Poción Mediana (P2), Anillo de Velocidad (AC6) | 1 de 3 aleatorio |
| Z3 — Las Minas de Karath | Escudo de Piedra (A5), Poción Grande (P3), Sello Roto (AC8) | 1 de 3 aleatorio |
| Z4 — La Torre de Embrath | Manto Arcano (A7), Elixir de Combate (P5), Amuleto Arcano (AC7) | 1 de 3 aleatorio |

> El item se fija al generar el mundo (JSON). En la misma partida siempre es el mismo. Cambia en nueva partida.

---

### 4.7 Stats finales — Enemigos normales

> - Todos verificados con simulación completa (fórmula: `max(0, ataque * rand[0.5-1.5] - defensa)`).
> - Guardian def bajada a 10 (antes 12) para que los 3 personajes puedan hacerle daño útil con su arma de zona.
> - Sombra Absorbida y Eco de Magia son exclusivos de Z5. El Eco ignora 3 def del jugador (daño mágico).

| Enemigo | Tipo | Mov | Rng | Daño | Def | HP | Especial | Zonas |
|---------|------|-----|-----|------|-----|----|---------|-------|
| Warrior | Base Warrior | 2 | 1 | 15 | 8 | 35 | BFS hacia jugador | Z1-Z4 |
| Berserker | Sub Warrior | 4 | 1 | 13 | 3 | 25 | Carga directa. Invocado | Z1-Z5 |
| Guardian | Sub Warrior | 1 | 1 | 15 | 10 | 50 | Zona fija radio 3 | Z2-Z4 |
| Archer | Base Archer | 3 | 4 | 10 | 4 | 28 | Zona confort + línea visión | Z1-Z2 |
| Francotirador | Sub Archer | 2 | 5 | 18 | 3 | 28 | Cooldown 2 turnos | Z3-Z4 |
| Destructor | Sub Mage A | 0 | 5 | 6x | 5 | 40 | AOE radio 2, no se mueve | Z2-Z3 |
| Controlador | Sub Mage B | 2 | 3 | 4 | 4 | 35 | Efecto aleatorio 2 turnos | Z1-Z4 |
| Invocador | Sub Mage C | 2 | 0 | 0 | 6 | 45 | Invoca Berserkers c/2t | Z2-Z4 |
| Sombra Absorbida | Z5 exclusivo | 2 | 1 | 20 | 8 | 45 | Variante oscura de Warrior | Z5 |
| Eco de Magia | Z5 exclusivo | 2 | 3 | 22 | 5 | 35 | Ignora 3 def del jugador | Z5 |

> Daño 6x (Destructor): 6 puntos por celda dentro del AOE. No usa la fórmula estándar.

---

### 4.8 Stats finales — Mini-bosses

> - Cada mini-boss verificado individualmente para los 3 personajes. Todos son viables.
> - Los ataques especiales de mini-bosses se definen en una fase posterior.
> - El Filtro (Z5) ignora 5 def del jugador. Es el último combate antes del desenlace final.

| Boss | Zona | HP | Daño | Def | Acompañantes | Nota de balanceo |
|------|------|----|------|-----|-------------|-----------------|
| Alcalde Corrupto | Z1 | 55 | 18 | 8 | + 2 Warrior | Def 8: Syra hace 11 med (5t). Dorath 6.9t. Kael 3.4t |
| Espíritu Madre | Z2 | 65 | 16 | 8 | + 2 Archer | Syra con Arco Élfico: 14 med, 4.6t — boss de su zona |
| Golem | Z3 | 90 | 20 | 11 | + 2 Guardian | Def 11: Syra sin Martillo 8.2t (viable con pociones). Ocupa 2x2 |
| Guardian Sin Nombre | Z4 | 80 | 22 | 12 | + 2 Constructo | Dorath con Grimorio: 20 med, 4t — Kael 3.8t — Syra 5.7t |
| El Filtro | Z5 | 70 | 22 | 10 | Solo | Ignora 5 def jugador. Syra aguanta 4.2 golpes — tensión máxima |

---

### 4.9 Tabla de viabilidad — verificación final

*Turnos para matar al boss / Golpes que aguanta el jugador. Arma óptima de cada zona. Umbral: <=12t y >=3.5 golpes.*

| Boss | Kael (turnos) | Kael (golpes) | Syra (turnos) | Syra (golpes) | Dorath (turnos) | Dorath (golpes) | Estado |
|------|--------------|--------------|--------------|--------------|----------------|----------------|--------|
| Alcalde Z1 | 3.4t | 6.1 | 5.0t | 4.2 | 6.9t | 4.4 | ✅ OK |
| Espíritu Madre Z2 | 4.1t | 11.0 | 4.6t | 4.7 | 5.0t | 5.0 | ✅ OK |
| Golem Z3 | 5.3t | 8.5 | 8.2t | 5.0 | 5.3t | 6.2 | ✅ OK |
| Guardian SN Z4 | 3.8t | 7.9 | 5.7t | 5.8 | 4.0t | 5.7 | ✅ OK |
| El Filtro Z5 | 3.0t | 5.8 | 4.4t | 4.2 | 3.2t | 4.2 | ✅ OK |

> Syra en Z5 con 4.2 golpes aguantados es diseño intencionado: su velocidad (mov 5) y sus pociones son su seguro de vida.

---

### 4.10 Efectos de estado

| Efecto | Duración | Impacto jugador | Impacto enemigo | Fuente |
|--------|----------|----------------|----------------|--------|
| SLOW | 2 turnos | Mov = `ceil(mov / 2.0)` | — | Controlador, Arco Élfico (W6), Arco del Eclipse (W11) |
| BLIND | 2 turnos | Mov = `ceil(mov / 2.0)` | — | Controlador, Espada del Vacío (W10), Arco del Eclipse (W11) |
| CURSE | 2 turnos | Daño recibido +3 por turno | — | Controlador |
| PARÁLISIS | 1 turno | Sin mov ni ataque | Sin mov ni ataque | Bastón Arcano (W9) 30%, Grimorio Abismal (W12) 30% |
| QUEMA | 1 turno | — | +3 daño al inicio del turno | Tomo de Llamas (W7) 25% |

**Notas de implementación:**
- SLOW aplicado a jugador: Kael (3) → 2 | Syra (5) → 3 | Dorath (2) → 1.
- Inmune a SLOW: Escudo de Raíces (A3). Inmune a CURSE: Manto de los Cinco Sellos (A8).
- Efectos almacenados en LSE en clase `Unit`. Se decrementan al final de cada turno del afectado.
- Arco del Eclipse (W11) es el **único** arma que aplica dos efectos simultáneos (SLOW + BLIND).

---

*Guía de Diseño v5.0 Final — Puntos 1, 2, 3 y 4 completos — Valdris: El Núcleo Profundo — H12GEXTRA*  
*Próximo: Punto 5 — Plan de 17 días*