package MisEstructurasDeDatos.Arbolesbinarios;

import MisEstructurasDeDatos.ListasPilasYColas.ListaSimplementeEnlazada;

public class Prueba2 {
    static void main(String[] args) {
        // i.Añadir los números de 0 a 128 PERO DE MANERA ALEATORIA y sin repetir.
        ArbolBinarioDeBusquedaEnteros arbol = new ArbolBinarioDeBusquedaEnteros();
            // Para que no se generen numeros repetidos lo que vamos a hacer es crear una lista
        ListaSimplementeEnlazada<Integer> lista = new ListaSimplementeEnlazada<>();
        for (int i = 0; i < 129; i++) {
            lista.addEnd(i);
        }
            // Despues desordenarla de manera aleatoria para esto vamos a ir metiendo de manera aleatoria en otra lista
        ListaSimplementeEnlazada<Integer> listafinal = new ListaSimplementeEnlazada<>();
        while (lista.getSize() > 0) { // mientras nuestra lista no este vacia
            // Marcamos el valor maximo
            int max = lista.getSize();
            // Generamos un numero al azar en ese intervalo
            int numeroAzar = (int) (Math.random() * max);
            // Pasamos el numero que este en esa posicion a la nueva lista
            listafinal.addEnd(lista.del(lista.get(numeroAzar)));
        }
        System.out.println("La lista aleatoria creada esta vez ha sido: ");
        System.out.println(listafinal);
        System.out.println();
            // Por ultimo añadimos todos los elemento a nuestro arbol
        for (int i = 0; i < listafinal.getSize(); i++) {
            arbol.add(listafinal.get(i));
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
            System.out.println("La suma es la misma cuando se suman los elementos del subarbol izquierdo y derecho (mas la raiz)");
            System.out.println();
            // En la mayoria de casos esto no va a ocurrir, porque lo mas probable es que la raiz no sea 0 y por tanto
            // como al sumar el subarbol izquierdo y derecho no tenemos en cuenta la raiz, el resultado es distinto
            // el unico caso en el que seria igual es si da la casualidad de ser justo 0, lo cual es muy poco probable
        }
        if (sumaizquierda + sumaderecha == sumaderecha + sumaizquierda) {
            System.out.println("Ademas la suma es igual independientemente del orden de los subarboles");
            System.out.println();
        }

        // v. ¿Cuál es la altura del árbol?
        int altura = arbol.getAltura();
        System.out.println("La altura del arbol (contando la raiz como altura 0) es: " + altura);
        System.out.println();
        // En este caso la altura va cambiando tambien dependiendo del orden en el que esten los datos en la lista
        // antes de ser insertados al arbol, pero al no ser en orden, el arbol esta mucho mas equilibrado y por ejemplo
        // en una de las pruebas nos ha dado altura 13 y en otra altura 16 (nada que ver con el 118 de antes)

        // vi. ¿Cuál es el camino para llegar al valor 110? ¿Cuál es su longitud de camino?
        ListaSimplementeEnlazada<ListaSimplementeEnlazada<Integer>> camino = arbol.getCaminoDatos(110);
        System.out.println("El camino para llegar al valor 110 es: \n" + camino);
        int longitudcamino = camino.getSize();
        System.out.println("La longitud del camino es: " + longitudcamino);
    }
}
