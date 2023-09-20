package tests.testV5;

import general.conexion.Entrada;
import general.constantes.Direccion;
import general.constantes.MaximoDeBloques;
import tradicional.nodo.gatewayVersion.GatewayTradicional;
import tradicional.nodo.gatewayVersion.seleccionador.SeleccionadorTradicionalGateway;

import java.io.IOException;

public class TestV5Gateway {

    public static void main(String[] args) throws IOException {
        GatewayTradicional gatewayTradicional = new GatewayTradicional(Direccion.DIRECCION_GATEWAY);
        Entrada hiloEntrada = new Entrada(gatewayTradicional);
        hiloEntrada.start();
        while (true) {
            if (gatewayTradicional.comprobarCantidadMinimaDeNodos()) break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SeleccionadorTradicionalGateway hiloSeleccionadorTradicionalGateway = new SeleccionadorTradicionalGateway(gatewayTradicional);
        hiloSeleccionadorTradicionalGateway.start();
        while (true) {
            if (gatewayTradicional.getContadorDeBloques() == MaximoDeBloques.MAX.getCantidad()) {
                System.exit(0);
            }
            System.out.print("");
        }
    }
}
