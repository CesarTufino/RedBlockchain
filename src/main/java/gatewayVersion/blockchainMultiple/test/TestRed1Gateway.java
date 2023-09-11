package gatewayVersion.blockchainMultiple.test;

import direcciones.Direccion;
import gatewayVersion.blockchainMultiple.nodo.Gateway;

import java.io.IOException;

public class TestRed1Gateway {

    public static void main(String[] args) throws IOException {
        Gateway gateway = new Gateway(Direccion.DIRECCION_GATEWAY);
        gateway.iniciarProceso();
    }
}
