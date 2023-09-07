package pos.blockchainTradicional.test;

import java.io.IOException;

import pos.blockchainTradicional.conexion.Entrada;
import pos.blockchainTradicional.nodo.Nodo;
import pos.blockchainTradicional.nodo.Temporizador;
import direcciones.Direccion;

public class TestRed1Nodo2 {

    public static void main(String[] args) throws IOException {
        int puertoRecepcion = 12342; // A donde se va a enviar
        Nodo nodo = new Nodo(3, Direccion.DIRECCION_2.getDireccionIP()); // Mi nodo
        nodo.apostar(25); // Poner el stake
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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Hilo para validación PoS
        Temporizador hiloTemporizador = new Temporizador(nodo);
        hiloTemporizador.start();
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            nodo.enviarDinero(1.23, Direccion.DIRECCION_1.getDireccionIP());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
