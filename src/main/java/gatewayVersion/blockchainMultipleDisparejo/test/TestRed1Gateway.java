package gatewayVersion.blockchainMultipleDisparejo.test;

import direcciones.Direccion;
import gatewayVersion.blockchainMultipleDisparejo.nodo.Gateway;

import java.io.IOException;

public class TestRed1Gateway {

    public static void main(String[] args) throws IOException {
        Gateway gateway = new Gateway(Direccion.DIRECCION_GATEWAY);
        gateway.iniciarProceso();
    }
}
