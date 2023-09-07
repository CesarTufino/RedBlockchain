package gatewayVersion.blockchainTradicional.test;

import java.io.IOException;

import gatewayVersion.blockchainTradicional.nodo.Nodo;
import direcciones.Direccion;

public class TestRed1Nodo3 {

    public static void main(String[] args) throws IOException {
        Nodo nodo = new Nodo(3, Direccion.DIRECCION_3); // Mi nodo
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
            nodo.enviarDinero(3.47, Direccion.DIRECCION_1.getDireccionIP());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
