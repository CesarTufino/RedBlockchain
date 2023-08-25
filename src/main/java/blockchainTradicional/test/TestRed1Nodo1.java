package blockchainTradicional.test;

import java.io.IOException;
import blockchainTradicional.conexion.Entrada;
import blockchainTradicional.nodo.*;
import direcciones.Direccion;

public class TestRed1Nodo1 {

    public static void main(String[] args) throws IOException {
        int puertoRecepcion = 12341;
        Nodo nodo = new Nodo(1, Direccion.DIRECCION1.getDireccionIP()); // Mi nodo
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
        Validador2 hiloValidador = new Validador2(nodo.getRed(), nodo);
        hiloValidador.start();
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            int a = (int) (((Math.random()) * 2) + 1);
            if (a == 1)
                nodo.enviarDinero(1.23, Direccion.DIRECCION3.getDireccionIP());
            else
                nodo.enviarDinero(3.47, Direccion.DIRECCION2.getDireccionIP());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
