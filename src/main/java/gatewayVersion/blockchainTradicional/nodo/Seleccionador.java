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

    public Seleccionador(Gateway gateway) {
        this.gateway = gateway;
    }

    public void seleccionar() {
        System.out.println("Seleccionando...");
        List<Transaccion> transacciones = gateway.escogerTransacciones();
        String direccionNodoSeleccionado1 = seleccionarNodo();
        String direccionNodoSeleccionado2 = seleccionarNodo();
        gateway.reiniciarNodosPosibles();

        while (true) {
            if (System.currentTimeMillis() - gateway.getTiempoDeCreacionDeUltimoBloque() > 10000) { // Garantiza los 10 segundos minimos
                break;
            }
        }

        gateway.mandarCrearBloque(direccionNodoSeleccionado1, gateway.getPuertos().get(direccionNodoSeleccionado1), transacciones);
        gateway.mandarCrearBloque(direccionNodoSeleccionado2, gateway.getPuertos().get(direccionNodoSeleccionado2), transacciones);

    }

    private String seleccionarNodo() {
        SecureRandom secureRandom = new SecureRandom();
        int semilla = secureRandom.nextInt();
        Random rnd = new Random(semilla);
        int numeroPseudoaleatorio = rnd.nextInt(gateway.getNodosPosibles().size());
        String direccionSeleciconada = gateway.getNodosPosibles().get(numeroPseudoaleatorio);
        gateway.getNodosSeleccionados().add(direccionSeleciconada);
        gateway.getNodosPosibles().remove(direccionSeleciconada);
        return direccionSeleciconada;
    }

    @Override
    public void run() {
        seleccionar();
    }
}
