package blockchainTradicional.test;

import java.io.IOException;
import blockchainTradicional.conexion.Entrada;
import blockchainTradicional.nodo.*;

public class TestRed3Nodo1 {

    public static void main(String[] args) throws IOException {
        int puertoRecepcion = 12341;
        Nodo nodo = new Nodo(1, "26.20.111.124"); // Mi nodo
        nodo.apostar(30); // Poner el stake
        // Hilo para escuchar
        Entrada hiloEntrada = new Entrada(nodo, puertoRecepcion);
        hiloEntrada.start();
        // Buscar datos en la red
        nodo.buscarRed();
        // Espera hasta que exitan tres nodos en la red
        while (true) {
            if (nodo.comprobarCantidadMinimaDeNodos()) break;
            System.out.print("");
        }
        // Hilo para validación PoS
        Validador hiloValidador = new Validador(nodo.getRed(), nodo);
        hiloValidador.start();
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            int a = (int) (((Math.random()) * 2) + 1);
            if (a == 1)
                nodo.enviarDinero(1.23, "26.143.218.218");
            else
                nodo.enviarDinero(3.47, "26.37.38.157");
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
