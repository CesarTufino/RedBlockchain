package gatewayVersion.blockchainMultiple.nodo;

import gatewayVersion.blockchainMultiple.mensajes.Paquete;
import gatewayVersion.blockchainMultiple.mensajes.Transaccion;

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
        List<Transaccion> transaccionesTipo1 = gateway.escogerTransacciones("Type1");
        Paquete paquete1 = new Paquete("Type1",transaccionesTipo1);
        List<Transaccion> transaccionesTipo2 = gateway.escogerTransacciones("Type2");
        Paquete paquete2 = new Paquete("Type2",transaccionesTipo2);
        String direccionNodoSeleccionado1 = seleccionarNodo();
        String direccionNodoSeleccionado2 = seleccionarNodo();
        String direccionNodoSeleccionado3 = seleccionarNodo();
        String direccionNodoSeleccionado4 = seleccionarNodo();
        gateway.reiniciarNodosPosibles();

        while (true) {
            if (System.currentTimeMillis() - gateway.getTiempoDeCreacionDeUltimoBloqueTipo1() > 10000) { // Garantiza los 10 segundos minimos
                break;
            }
        }
        gateway.mandarCrearBloque(direccionNodoSeleccionado1, gateway.getPuertos().get(direccionNodoSeleccionado1), paquete1);
        gateway.mandarCrearBloque(direccionNodoSeleccionado2, gateway.getPuertos().get(direccionNodoSeleccionado2), paquete1);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {
            if (System.currentTimeMillis() - gateway.getTiempoDeCreacionDeUltimoBloqueTipo2() > 10000) { // Garantiza los 10 segundos minimos
                break;
            }
        }
        gateway.mandarCrearBloque(direccionNodoSeleccionado3, gateway.getPuertos().get(direccionNodoSeleccionado3), paquete2);
        gateway.mandarCrearBloque(direccionNodoSeleccionado4, gateway.getPuertos().get(direccionNodoSeleccionado4), paquete2);
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
