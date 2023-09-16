package tests.testV8;

import constantes.Direccion;
import constantes.MaximoDeBloques;
import multiple.conexion.Entrada;
import multiple.nodo.gatewayVersion.Gateway;
import multiple.nodo.gatewayVersion.seleccionador.SeleccionadorAleatorio;

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
        SeleccionadorAleatorio hiloSeleccionadorAleatorio = new SeleccionadorAleatorio(gateway);
        hiloSeleccionadorAleatorio.start();
        while (true) {
            if (gateway.getContadorDeBloques() == MaximoDeBloques.MAX.getCantidad()) {
                System.exit(0);
            }
            System.out.print("");
        }
    }
}
