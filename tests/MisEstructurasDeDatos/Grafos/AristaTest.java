package MisEstructurasDeDatos.Grafos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AristaTest {

    @Test
    public void testConstructorYGetters() {
        NodoGrafo<String> origen = new NodoGrafo<>("persona:Albert Einstein");
        NodoGrafo<String> destino = new NodoGrafo<>("lugar:Ulm");
        Arista<String, String> arista = new Arista<>(origen, "nace_en", destino);

        assertEquals(origen, arista.getOrigen());
        assertEquals("nace_en", arista.getDato());
        assertEquals(destino, arista.getDestino());
    }

    @Test
    public void testSetOrigen() {
        NodoGrafo<String> origenInicial = new NodoGrafo<>("persona:Albert Einstein");
        NodoGrafo<String> destino = new NodoGrafo<>("lugar:Ulm");
        Arista<String, String> arista = new Arista<>(origenInicial, "nace_en", destino);

        NodoGrafo<String> nuevoOrigen = new NodoGrafo<>("persona:Marie Curie");
        arista.setOrigen(nuevoOrigen);

        assertEquals(nuevoOrigen, arista.getOrigen());
    }

    @Test
    public void testSetPredicado() {
        NodoGrafo<String> origen = new NodoGrafo<>("persona:Albert Einstein");
        NodoGrafo<String> destino = new NodoGrafo<>("lugar:Ulm");
        Arista<String, String> arista = new Arista<>(origen, "nace_en", destino);

        arista.setDato("vive_en");

        assertEquals("vive_en", arista.getDato());
    }

    @Test
    public void testSetDestino() {
        NodoGrafo<String> origen = new NodoGrafo<>("persona:Albert Einstein");
        NodoGrafo<String> destinoInicial = new NodoGrafo<>("lugar:Ulm");
        Arista<String, String> arista = new Arista<>(origen, "nace_en", destinoInicial);

        NodoGrafo<String> nuevoDestino = new NodoGrafo<>("lugar:Varsovia");
        arista.setDestino(nuevoDestino);

        assertEquals(nuevoDestino, arista.getDestino());
    }

    @Test
    public void testToString() {
        NodoGrafo<String> origen = new NodoGrafo<>("persona:Albert Einstein");
        NodoGrafo<String> destino = new NodoGrafo<>("lugar:Ulm");
        Arista<String, String> arista = new Arista<>(origen, "nace_en", destino);

        String esperado = "  " + origen.getId() + " --(nace_en)--> " + destino.getId();

        assertEquals(esperado, arista.toString());
    }
}
