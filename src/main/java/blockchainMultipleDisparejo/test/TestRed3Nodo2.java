package blockchainMultipleDisparejo.test;

import java.io.IOException;
import blockchainMultipleDisparejo.conexion.Entrada;
import blockchainMultipleDisparejo.nodo.*;

public class TestRed3Nodo2 {

    public static void main(String[] args) throws IOException {
        String type1 = "Type1";
        String type2 = "Type2";
        int puertoRecepcion = 12342; // A donde se va a enviar
        Nodo nodo = new Nodo(2, "26.37.38.157"); // Mi nodo
        // Poner el stake
        nodo.apostar(25, type1);
        nodo.apostar(45, type2);
        // Hilo para escuchar
        Entrada serverThread = new Entrada(nodo, puertoRecepcion);
        serverThread.start();
        // Buscar datos en la red
        nodo.buscarRed();
        // Espera hasta que exitan tres nodos en la red
        while (true) {
            if (nodo.comprobarCantidadMinimaDeNodos())
                break;
            System.out.print("");
        }
        // Hilo para validación PoS
        Validador hiloValidador = new Validador(nodo.getRed(), nodo);
        hiloValidador.start();
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            nodo.enviarDinero(1.23, "26.20.111.124", type1);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
