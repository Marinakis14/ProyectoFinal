package MisEstructurasDeDatos.Grafos;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;

public interface InterfazGrafo<DN, DA> {
    // -- Getters y Setters --
    ListaSimplementeEnlazada<NodoGrafo<DN>> getNodos();

    ListaSimplementeEnlazada<Arista<DN, DA>> getAristas();

    ListaSimplementeEnlazada<String> getTipos();

    // -- Metodos para añadir cosas al grafo --
    void addNodo(NodoGrafo<DN> nodo);

    void addNodo(DN datos);

    void addArista(Arista<DN, DA> arista);

    void addArista(NodoGrafo<DN> origen, DA predicado, NodoGrafo<DN> destino);

    void addTipo(String tipo);

    ListaSimplementeEnlazada<InterfazDatosNodo> getNodosValidos();

    ListaSimplementeEnlazada<InterfazDatosArista> getAristasValidas();

    // -- Metodos para trabajar con el grafo --
    NodoGrafo<DN> buscarNodoPorId(long id);

    NodoGrafo<DN> buscarNodoPorDato(DN dato);

    NodoGrafo<DN> buscarNodoPorNombre(String nombreBusqueda);

    NodoGrafo<DN> buscarNodoPorTipo(String tipo);

    int NumeroNodosTipo(String tipo);

    ListaSimplementeEnlazada<String> getTiposDeNodos();

    ListaSimplementeEnlazada<NodoGrafo<DN>> getVecinos(NodoGrafo<DN> nodo);

    ListaSimplementeEnlazada<NodoGrafo<DN>> caminoMinimo(long idOrigen, long idDestino);

    ListaSimplementeEnlazada<NodoGrafo<DN>> caminoMinimoAmbosSentidos(long idOrigen, long idDestino);

    String mostrarIdsCamino(ListaSimplementeEnlazada<NodoGrafo<DN>> camino);

    String mostrarDatosCamino(ListaSimplementeEnlazada<NodoGrafo<DN>> camino);

    String mostrarDatosNodosYAristasCamino(ListaSimplementeEnlazada<NodoGrafo<DN>> camino);

    ListaSimplementeEnlazada<NodoGrafo<DN>> caminoMinimo(String nombreOrigen, String nombreDestino);

    ListaSimplementeEnlazada<NodoGrafo<DN>> caminoMinimoAmbosSentidos(String nombreOrigen, String nombreDestino);

    ListaSimplementeEnlazada<NodoGrafo<DN>> getVecinosNoDirigidos(NodoGrafo<DN> nodo);

    boolean esDisjunto();

    Arista<DN, DA> getAristaPorNodos(NodoGrafo<DN> origen, NodoGrafo<DN> destino);

    ListaSimplementeEnlazada<NodoGrafo<DN>> getDestinosPorPredicado(NodoGrafo<DN> origen, DA predicado);

    ListaSimplementeEnlazada<NodoGrafo<DN>> getDestinosPorPredicado(NodoGrafo<DN> origen, String dato);

    ListaSimplementeEnlazada<NodoGrafo<DN>> getOrigenesPorPredicadoYDestino(DA predicado, NodoGrafo<DN> destino);

    ListaSimplementeEnlazada<NodoGrafo<DN>> getOrigenesPorPredicadoYDestino(String dato, NodoGrafo<DN> destino);

    ListaSimplementeEnlazada<NodoGrafo<DN>> personasMismaCiudadQue(String tipoPersona);

    ListaSimplementeEnlazada<NodoGrafo<DN>> fisicosMismaCiudadQue(String tipoPersona);

    ListaSimplementeEnlazada<NodoGrafo<DN>> lugaresNacimientoPremiosNobel();

    ListaSimplementeEnlazada<NodoGrafo<DN>> Dijkstra(NodoGrafo<DN> origen, NodoGrafo<DN> destino);

    String toString();
}
