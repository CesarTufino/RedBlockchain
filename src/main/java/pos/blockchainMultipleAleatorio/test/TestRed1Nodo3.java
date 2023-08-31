package pos.blockchainMultipleAleatorio.test;

import java.io.IOException;

import pos.blockchainMultipleAleatorio.conexion.Entrada;
import pos.blockchainMultipleAleatorio.nodo.Nodo;
import pos.blockchainMultipleAleatorio.nodo.Temporizador;
import direcciones.Direccion;

public class TestRed1Nodo3 {

    public static void main(String[] args) throws IOException {
        String type1 = "Type1";
        String type2 = "Type2";
        int puertoRecepcion = 12343; // A donde se va a enviar
        Nodo nodo = new Nodo(3, Direccion.DIRECCION3.getDireccionIP()); // Mi nodo
        // Poner el stake
        nodo.apostar(20, type1);
        nodo.apostar(50, type2);
        // Hilo para escuchar
        Entrada serverThread = new Entrada(nodo, puertoRecepcion);
        serverThread.start();
        // Buscar datos en la red
        nodo.buscarRed();
        // Espera hasta que exitan tres nodos en la red
        while (true){
            if (nodo.comprobarCantidadMinimaDeNodos()) break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Hilo para validación PoS
        Temporizador hiloTemporizador = new Temporizador(nodo.getRed(), nodo);
        hiloTemporizador.start();
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            nodo.enviarDinero(3.47, Direccion.DIRECCION1.getDireccionIP(), type2);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}