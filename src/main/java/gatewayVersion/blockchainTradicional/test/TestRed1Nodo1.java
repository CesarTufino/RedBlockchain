package gatewayVersion.blockchainTradicional.test;

import java.io.IOException;

import gatewayVersion.blockchainTradicional.conexion.Entrada;
import gatewayVersion.blockchainTradicional.nodo.Nodo;
import gatewayVersion.blockchainTradicional.nodo.Temporizador;
import direcciones.Direccion;

public class TestRed1Nodo1 {

    public static void main(String[] args) throws IOException {
        int puertoRecepcion = 12341;
        Nodo nodo = new Nodo(1, Direccion.DIRECCION1.getDireccionIP(), puertoRecepcion);
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
