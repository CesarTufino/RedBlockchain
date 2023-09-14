package gatewayVersion.blockchainMultipleAletorio.test;

import constantes.Direccion;
import multiple.conexion.Entrada;
import multiple.nodo.gatewayVersion.Gateway;
import multiple.nodo.gatewayVersion.seleccionador.Seleccionador;

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
        Seleccionador hiloSeleccionador = new Seleccionador(gateway);
        hiloSeleccionador.start();
    }
}
