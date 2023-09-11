package gatewayVersion.blockchainMultiple.test;

import direcciones.Direccion;
import gatewayVersion.blockchainMultiple.nodo.Gateway;
import gatewayVersion.blockchainMultiple.nodo.Temporizador;

import java.io.IOException;

public class TestRed1Gateway {

    public static void main(String[] args) throws IOException {
        Gateway gateway = new Gateway(Direccion.DIRECCION_GATEWAY);
        gateway.empezarAEscuchar();
        while (true) {
            if (gateway.comprobarCantidadMinimaDeNodos()) break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Temporizador hiloTemporizador = new Temporizador(gateway);
        hiloTemporizador.start();
    }
}
