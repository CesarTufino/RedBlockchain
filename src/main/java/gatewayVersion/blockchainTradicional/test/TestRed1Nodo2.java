package gatewayVersion.blockchainTradicional.test;

import java.io.IOException;

import gatewayVersion.blockchainTradicional.conexion.Entrada;
import gatewayVersion.blockchainTradicional.nodo.Nodo;
import gatewayVersion.blockchainTradicional.nodo.Temporizador;
import direcciones.Direccion;

public class TestRed1Nodo2 {

    public static void main(String[] args) throws IOException {
        int puertoRecepcion = 12342;
        Nodo nodo = new Nodo(3, Direccion.DIRECCION2.getDireccionIP(), puertoRecepcion); // Mi nodo
        nodo.iniciarProceso();
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
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            nodo.enviarDinero(1.23, Direccion.DIRECCION1.getDireccionIP());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
