package blockchainTradicional.test;

import java.io.IOException;
import blockchainTradicional.conexion.Entrada;
import blockchainTradicional.nodo.*;
import direcciones.Direccion;

public class TestRed1Nodo3 {

    public static void main(String[] args) throws IOException {
        int puertoRecepcion = 12343; // A donde se va a enviar
        Nodo nodo = new Nodo(3, Direccion.DIRECCION3.getDireccionIP()); // Mi nodo
        nodo.apostar(20);// Poner el stake
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
        Temporizador hiloTemporizador = new Temporizador(nodo.getRed(), nodo);
        hiloTemporizador.start();
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            nodo.enviarDinero(3.47, Direccion.DIRECCION1.getDireccionIP());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
