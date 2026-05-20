package MisEstructurasDeDatos.Grafos;

// Representa una tripleta del JSON (sujeto, predicado, objeto)
public class TripletaJson implements Comparable<TripletaJson> {

    // Sujeto
    private String s;

    // Predicado
    private String p;

    // Objeto
    private String o;

    // Getter y setter de s
    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    // Getter y setter de p
    public String getD() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    // Getter y setter de o
    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    @Override
    public int compareTo(TripletaJson o) {
        return 0;
    }
}