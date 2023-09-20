package tests.testV8;

import general.constantes.Direccion;
import general.constantes.MaximoDeBloques;
import general.conexion.Entrada;
import multiple.nodo.gatewayVersion.GatewayMultiple;
import multiple.nodo.gatewayVersion.seleccionador.SeleccionadorAleatorioGateway;

import java.io.IOException;

public class TestV8Gateway {

    public static void main(String[] args) throws IOException {
        GatewayMultiple gatewayMultiple = new GatewayMultiple(Direccion.DIRECCION_GATEWAY);
        Entrada hiloEntrada = new Entrada(gatewayMultiple);
        hiloEntrada.start();
        while (true) {
            if (gatewayMultiple.comprobarCantidadMinimaDeNodos()) break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SeleccionadorAleatorioGateway hiloSeleccionadorAleatorioGateway = new SeleccionadorAleatorioGateway(gatewayMultiple);
        hiloSeleccionadorAleatorioGateway.start();
        while (true) {
            if (gatewayMultiple.getContadorDeBloques() == MaximoDeBloques.MAX.getCantidad()) {
                System.exit(0);
            }
            System.out.print("");
        }
    }
}
