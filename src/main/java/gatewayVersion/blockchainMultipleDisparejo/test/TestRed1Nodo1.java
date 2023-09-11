package gatewayVersion.blockchainMultipleDisparejo.test;

import java.io.IOException;

import gatewayVersion.blockchainMultipleDisparejo.nodo.Nodo;
import direcciones.Direccion;

public class TestRed1Nodo1 {

    public static void main(String[] args) throws IOException {
        Nodo nodo = new Nodo(1, Direccion.DIRECCION_1);
        nodo.iniciarProceso();
        while (true) {
            if (nodo.comprobarCantidadMinimaDeNodos()) break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            int a = (int) (((Math.random()) * 2) + 1);
            if (a == 1)
                nodo.enviarDinero(1.23, Direccion.DIRECCION_2.getDireccionIP(), "Type1");
            else
                nodo.enviarDinero(3.47, Direccion.DIRECCION_3.getDireccionIP(), "Type2");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
