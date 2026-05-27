# Valdris: El Núcleo Profundo

Proyecto final del grupo H12GEXTRA desarrollado en Java 21 y JavaFX.

## Integrantes

- Marcos Castro Rubio
- Ventura Pacheco Pastilla
- Marino Rodríguez Moreno

## Descripción

`Valdris: El Núcleo Profundo` es un juego de exploración por turnos ambientado
en una mazmorra fantástica. El jugador elige entre tres personajes, avanza por
salas conectadas mediante un grafo, resuelve puzzles, descubre secretos,
combate enemigos y llega al Núcleo Profundo para cerrar la historia principal.

El proyecto combina lógica propia de juego con una interfaz JavaFX completa:
menú inicial, introducción narrativa, selección de personaje, partida principal,
inventario, diálogos, combate final, desenlace y resumen final visible.

## Tecnologías

- Java 21.
- JavaFX 21.
- Maven Wrapper.
- JUnit 5.
- Gson para persistencia JSON.
- Estructuras de datos propias del proyecto.

## Ejecución

Desde la raíz del proyecto:

```powershell
.\mvnw.cmd javafx:run
```

Si Maven usa un JRE antiguo, configurar antes `JAVA_HOME` a un JDK compatible:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-25'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd javafx:run
```

## Pruebas

Para ejecutar la suite completa:

```powershell
.\mvnw.cmd test
```

Última verificación registrada:

```text
498 tests, 0 failures, 0 errors, 0 skipped
```

## Estructura Principal

```text
src/
├── MisEstructurasDeDatos/  Estructuras propias: listas, pilas, colas, grafos
└── Valdris/
    ├── model/              Modelo de dominio: unidades, mapa, items, efectos
    ├── logic/              BFS, visión, combate, IA, turnos, generación
    ├── persistence/        Carga JSON, guardado, resumen y configuración
    ├── exceptions/         Excepciones propias del juego
    └── ui/                 Aplicación JavaFX, vistas, modelo y controlador

tests/                      Tests JUnit 5
config/                     Configuración inicial JSON del mundo
docs/                       Guías de diseño y material de apoyo
diagramas/                  Diagramas UML y fuentes editables PlantUML
imagenes/                   Bocetos, retratos y recursos visuales
```

## Ficheros de Entrega

- `MEMORIA_DISENO_VALDRIS.md`: memoria final y documento de diseño.
- `INTRUCCIONES_GENERALES.md`: enunciado y requisitos de entrega.
- `TASKS.md`: registro técnico de tareas completadas.
- `COMMIT_LOG.md`: diario de uso de IA y evolución del proyecto.
- `config/configuracion_inicial_valdris.json`: configuración inicial del mundo.
- `partida_valdris.json`: ejemplo de partida guardada.
- `resumen_valdris.json`: ejemplo de resumen final exportado.
- `diagramas/`: diagramas UML y diagramas de apoyo del proyecto.

## Controles

La interfaz muestra las acciones disponibles y sus teclas asociadas durante la
partida. Las acciones principales permiten moverse, recoger o interactuar,
usar objetos, atacar, revelar la ruta hacia el objetivo y abrir el inventario.

## Notas de Diseño

El proyecto evita `java.util.*` para estructuras de datos y utiliza las
estructuras propias incluidas en `MisEstructurasDeDatos`. La configuración
inicial del mundo se carga desde JSON y declara salas, celdas, accesos, cofres,
puzzles, enemigos, posición inicial y objetivo.

La memoria final contiene la justificación completa de requisitos, modelo de
dominio, estructuras usadas, costes, algoritmos, persistencia, interfaz,
contratos, invariantes, pruebas, uso de IA, decisiones de diseño y crítica del
proyecto.
