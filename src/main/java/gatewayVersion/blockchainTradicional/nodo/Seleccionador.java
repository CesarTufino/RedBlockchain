package gatewayVersion.blockchainTradicional.nodo;

import gatewayVersion.blockchainTradicional.mensajes.Mensaje;
import gatewayVersion.blockchainTradicional.mensajes.Transaccion;
import utils.RsaUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class Seleccionador extends Thread {

    private Gateway gateway;
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_BLUE = "\u001B[34m";
    private final String ANSI_RESET = "\u001B[0m";

    public Seleccionador(Gateway gateway) {
        this.gateway = gateway;
    }

    public void seleccionar() {
        List<Transaccion> transacciones = gateway.escogerTransacciones();

        SecureRandom secureRandom = new SecureRandom();
        int semilla = secureRandom.nextInt();
        Random rnd = new Random(semilla);
        int numeroPseudoaleatorio = rnd.nextInt(gateway.getDireccionesDeNodos().size());

        gateway.mandarCrearBloque(gateway.getDireccionesDeNodos().get(numeroPseudoaleatorio), transacciones);
    }

    @Override
    public void run() {
        seleccionar();
    }
}
