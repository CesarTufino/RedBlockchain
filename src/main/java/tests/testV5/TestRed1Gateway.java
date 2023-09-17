package tests.testV5;

import constantes.Direccion;
import constantes.MaximoDeBloques;
import tradicional.conexion.Entrada;
import tradicional.nodo.gatewayVersion.Gateway;
import tradicional.nodo.gatewayVersion.seleccionador.SeleccionadorTradicional;

import java.io.IOException;

public class TestRed1Gateway {

    public static void main(String[] args) throws IOException {
        Gateway gateway = new Gateway(Direccion.DIRECCION_GATEWAY);
        Entrada hiloEntrada = new Entrada(gateway);
        hiloEntrada.start();
        while (true) {
            if (gateway.comprobarCantidadMinimaDeNodos()) break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SeleccionadorTradicional hiloSeleccionadorTradicional = new SeleccionadorTradicional(gateway);
        hiloSeleccionadorTradicional.start();
        while (true) {
            if (gateway.getContadorDeBloques() == MaximoDeBloques.MAX.getCantidad()) {
                System.exit(0);
            }
            System.out.print("");
        }
    }
}
