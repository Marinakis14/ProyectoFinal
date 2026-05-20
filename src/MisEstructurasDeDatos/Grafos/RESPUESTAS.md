¿Qué tipos de nodos tiene el grafo?
En tu implementación actual, el grafo es dinámico y los tipos de nodos dependen del tipo de dato que se cargue ya que 
trabajan con tipos de datos abstractos

Tienes nodos de tipo NodoGrafo<DN>. Si DN es un String, el tipo es simplemente texto. 
Si usas la InterfazDatosNodo, el tipo se define por el prefijo de los datos (ej. "persona", "lugar", "profesion").

¿Qué es una ontología?
Una ontología es un modelo formal que define los conceptos (clases), las propiedades (relaciones) y las reglas de un 
dominio específico. Es, en esencia, el "diccionario" y el "manual de instrucciones" de cómo debe estructurarse la 
información para que tanto humanos como máquinas la entiendan sin ambigüedades.

¿Qué relación tiene con los grafos?
La relación es de estructura vs. significado:

El Grafo es el contenedor: Es la red física de nodos y aristas (quién está conectado con quién).
La Ontología es el esquema: Define qué significan esas conexiones. Por ejemplo, en un grafo puedes conectar un nodo A 
con un nodo B, pero la ontología es la que dicta que si A es de tipo "Persona" y B es de tipo "Lugar", la arista entre
ellos debería ser de tipo "nace_en" o "vive_en", y no "es_un".

¿Podríamos crear una ontología para nuestro problema?
Sí, de hecho, este proyecto esta muy cerca de ser una ontologia.

¿Qué haríamos con ella?
Validación de Datos: Evitar errores como decir que un "Lugar" nació en una "Persona". El código podría consultar la 
ontología antes de usar addArista.

Inferencia: Si la ontología dice que "Si X nace en Y, e Y está en Z, entonces X es originario de Z", tu grafo podría 
deducir que Einstein es alemán aunque no exista una arista directa entre ellos.

Normalización: Asegurarnos de que todos usen "nace_en" y nadie use "nacimiento_lugar", permitiendo que las consultas 
como personasMismaCiudadQue funcionen siempre correctamente.

Interoperabilidad: Permitir que tu JSON pueda ser leído por otros sistemas que sigan el estándar RDF, facilitando el 
intercambio de conocimiento.