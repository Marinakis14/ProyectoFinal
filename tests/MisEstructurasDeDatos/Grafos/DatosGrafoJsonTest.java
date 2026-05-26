package MisEstructurasDeDatos.Grafos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DatosGrafoJsonTest {

    @Test
    public void testGetTripletasInicialmenteNull() {
        DatosGrafoJson datos = new DatosGrafoJson();

        assertNull(datos.getTripletas());
    }

    @Test
    public void testSetYGetTripletas() {
        DatosGrafoJson datos = new DatosGrafoJson();
        TripletaJson[] tripletas = new TripletaJson[2];

        TripletaJson tripleta1 = new TripletaJson();
        tripleta1.setS("persona:Albert Einstein");
        tripleta1.setP("nace_en");
        tripleta1.setO("lugar:Ulm");

        TripletaJson tripleta2 = new TripletaJson();
        tripleta2.setS("persona:Albert Einstein");
        tripleta2.setP("premio");
        tripleta2.setO("premio:Nobel");

        tripletas[0] = tripleta1;
        tripletas[1] = tripleta2;

        datos.setTripletas(tripletas);

        assertSame(tripletas, datos.getTripletas());

        assertEquals("persona:Albert Einstein", tripleta1.getS());
        assertEquals("nace_en", tripleta1.getD());
        assertEquals("lugar:Ulm", tripleta1.getO());

        assertEquals("persona:Albert Einstein", tripleta2.getS());
        assertEquals("premio", tripleta2.getD());
        assertEquals("premio:Nobel", tripleta2.getO());
    }

    @Test
    public void testSetTripletasNull() {
        DatosGrafoJson datos = new DatosGrafoJson();
        TripletaJson[] tripletas = new TripletaJson[1];

        datos.setTripletas(tripletas);
        assertNotNull(datos.getTripletas());

        datos.setTripletas(null);
        assertNull(datos.getTripletas());
    }
}
