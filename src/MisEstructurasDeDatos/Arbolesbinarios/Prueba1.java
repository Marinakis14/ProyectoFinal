package MisEstructurasDeDatos.Arbolesbinarios;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;

public class Prueba1 {
    static void main(String[] args) {
        // i.Añadir los números de 0 a 128 en orden.
        ArbolBinarioDeBusquedaEnteros arbol = new ArbolBinarioDeBusquedaEnteros();;
        for (int i = 0; i < 129; i++) {
            arbol.add(i);
        }

        // ii. Calcular la suma (getSuma())
        int resultadoSuma = arbol.getSumaOrdenCentral(arbol.getRaiz());
        System.out.println("La suma es: " + resultadoSuma);
        System.out.println();

        // iii. Verifica que la suma es la misma accediendo en los 3 tipos de recorridos posibles.
        int sumaPreorden = arbol.getSumaPreorder(arbol.getRaiz());
        int sumaPostorden = arbol.getSumaPostorder(arbol.getRaiz());
        if (resultadoSuma == sumaPreorden && sumaPreorden == sumaPostorden) {
            System.out.println("Las sumas de los distintos recorridos son iguales");
            System.out.println();
        }

        // iv. Verifica que la suma es la misma cuando se suman los elementos de los subárboles izquierdo y derecho. ¿Por qué?
        ArbolBinarioDeBusqueda subArbolIzquierdo = arbol.getSubArbolIzquierda();
        ArbolBinarioDeBusqueda subarbolDerecho = arbol.getSubArbolDerecha();
        int sumaizquierda = arbol.getSumaOrdenCentral(subArbolIzquierdo.getRaiz());
        int sumaderecha = arbol.getSumaOrdenCentral(subarbolDerecho.getRaiz());
        int sumatotal = sumaizquierda + sumaderecha;
        if (resultadoSuma == sumatotal) {
            System.out.println("La suma es la misma cuando se suman los elementos del subarbol izquierdo y derecho");
            System.out.println();
            // Esto se debe a que la suma es conmutativa y el nodo raiz es 0 con lo cual no hace falta añadirlo
            // porque no afecta al resultado
        }
        if (sumaizquierda + sumaderecha == sumaderecha + sumaizquierda) {
            System.out.println("Ademas la suma es igual independientemente del orden de los subarboles");
            System.out.println();
        }

        // v. ¿Cuál es la altura del árbol?
        int altura = arbol.getAltura();
        System.out.println("La altura del arbol (contando la raiz como altura 0) es: " + altura);
        System.out.println();

        // vi. ¿Cuál es el camino para llegar al valor 110? ¿Cuál es su longitud de camino?
        ListaSimplementeEnlazada<ListaSimplementeEnlazada<Integer>> camino = arbol.getCaminoDatos(110);
        System.out.println("El camino para llegar al valor 110 es: \n" + camino);
        int longitudcamino = camino.getSize();
        System.out.println("La longitud del camino es: " + longitudcamino);
    }
}
