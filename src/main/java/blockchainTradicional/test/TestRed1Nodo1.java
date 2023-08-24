package blockchainTradicional.test;

import java.io.IOException;
import blockchainTradicional.conexion.Entrada;
import blockchainTradicional.nodo.*;

public class TestRed1Nodo1 {

    public static void main(String[] args) throws IOException {
        int puertoRecepcion = 12341;
        Nodo nodo = new Nodo(1, "192.168.0.103"); // Mi nodo
        nodo.apostar(30); // Poner el stake
        // Hilo para escuchar
        Entrada hiloEntrada = new Entrada(nodo, puertoRecepcion);
        hiloEntrada.start();
        // Buscar datos en la red
        nodo.buscarRed();
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
                nodo.enviarDinero(1.23, "192.168.0.101");
            else
                nodo.enviarDinero(3.47, "192.168.0.100");
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
