package MisEstructurasDeDatos.Grafos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TripletaJsonTest {

    @Test
    public void testSetYGetS() {
        TripletaJson tripleta = new TripletaJson();

        tripleta.setS("persona:Albert Einstein");

        assertEquals("persona:Albert Einstein", tripleta.getS());
    }

    @Test
    public void testSetYGetP() {
        TripletaJson tripleta = new TripletaJson();

        tripleta.setP("nace_en");

        assertEquals("nace_en", tripleta.getD());
    }

    @Test
    public void testSetYGetO() {
        TripletaJson tripleta = new TripletaJson();

        tripleta.setO("lugar:Ulm");

        assertEquals("lugar:Ulm", tripleta.getO());
    }

    @Test
    public void testValoresInicialesNull() {
        TripletaJson tripleta = new TripletaJson();

        assertNull(tripleta.getS());
        assertNull(tripleta.getD());
        assertNull(tripleta.getO());
    }

    @Test
    public void testSetYGetTripletaCompleta() {
        TripletaJson tripleta = new TripletaJson();

        tripleta.setS("persona:Albert Einstein");
        tripleta.setP("premio:Nobel");
        tripleta.setO("1921");

        assertEquals("persona:Albert Einstein", tripleta.getS());
        assertEquals("premio:Nobel", tripleta.getD());
        assertEquals("1921", tripleta.getO());
    }
}