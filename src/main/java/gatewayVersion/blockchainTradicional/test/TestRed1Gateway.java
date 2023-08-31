package gatewayVersion.blockchainTradicional.test;

import direcciones.Direccion;
import gatewayVersion.blockchainTradicional.conexion.Entrada;
import gatewayVersion.blockchainTradicional.nodo.Gateway;
import gatewayVersion.blockchainTradicional.nodo.Temporizador;

import java.io.IOException;

public class TestRed1Gateway {

    public static void main(String[] args) throws IOException {
        int puertoRecepcion = 12344;
        Gateway gateway = new Gateway("", puertoRecepcion);
        gateway.iniciarProceso();
    }
}
