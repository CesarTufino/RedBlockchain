package tradicional.nodo.gatewayVersion.seleccionador;

import tradicional.mensajes.TransaccionTradicional;
import tradicional.nodo.gatewayVersion.GatewayTradicional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class SeleccionadorTradicionalGateway extends Thread {

    private GatewayTradicional gatewayTradicional;

    public SeleccionadorTradicionalGateway(GatewayTradicional gatewayTradicional) {
        this.gatewayTradicional = gatewayTradicional;
    }

    public void seleccionar() {
        String direccionNodoSeleccionado1 = seleccionarNodo();
        String direccionNodoSeleccionado2 = seleccionarNodo();

        List<TransaccionTradicional> transacciones = gatewayTradicional.escogerTransacciones();
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado1);
        gatewayTradicional.mandarCrearBloque(direccionNodoSeleccionado1, gatewayTradicional.getPuertos().get(direccionNodoSeleccionado1), transacciones);
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado2);
        gatewayTradicional.mandarCrearBloque(direccionNodoSeleccionado2, gatewayTradicional.getPuertos().get(direccionNodoSeleccionado2), transacciones);
    }

    private String seleccionarNodo() {
        SecureRandom secureRandom = new SecureRandom();
        int semilla = secureRandom.nextInt();
        Random rnd = new Random(semilla);
        int numeroPseudoaleatorio = rnd.nextInt(gatewayTradicional.getNodosPosibles().size());
        String direccionSeleciconada = gatewayTradicional.getNodosPosibles().get(numeroPseudoaleatorio);
        gatewayTradicional.getNodosSeleccionados().add(direccionSeleciconada);
        gatewayTradicional.getNodosPosibles().remove(direccionSeleciconada);
        return direccionSeleciconada;
    }

    private long calcularTiempoParaIniciar() {
        return 10000 - (System.currentTimeMillis() % 10000);
    }

    private void iniciarIteracion() {
        System.out.println("Seleccionando...");
        seleccionar();
        gatewayTradicional.reiniciarNodosPosibles();
    }

    @Override
    public void run() {
        try {
            while (true) {
                long tiempoParaIniciar = calcularTiempoParaIniciar();
                Thread.sleep(tiempoParaIniciar);
                iniciarIteracion();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
