package tradicional.nodo.gatewayVersion.seleccionador;

import constantes.Tipo;
import tradicional.mensajes.Transaccion;
import tradicional.nodo.gatewayVersion.Gateway;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class SeleccionadorTradicional extends Thread {

    private Gateway gateway;

    public SeleccionadorTradicional(Gateway gateway) {
        this.gateway = gateway;
    }

    public void seleccionar() {
        String direccionNodoSeleccionado1 = seleccionarNodo();
        String direccionNodoSeleccionado2 = seleccionarNodo();

        List<Transaccion> transacciones = gateway.escogerTransacciones();
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado1);
        gateway.mandarCrearBloque(direccionNodoSeleccionado1, gateway.getPuertos().get(direccionNodoSeleccionado1), transacciones);
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado2);
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
        // tiempo de espera inicial
        try {
            long tiempoParaIniciar = 10000 - (System.currentTimeMillis() % 10000);
            Thread.sleep(tiempoParaIniciar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long tiempoInicio;
        long tiempoActual;
        long tiempoDelUltimoBloque;
        while (true) {
            System.out.println("Seleccionando...");
            tiempoInicio = System.currentTimeMillis();

            seleccionar();
            gateway.reiniciarNodosPosibles();

            while (true) {
                tiempoActual = System.currentTimeMillis();
                tiempoDelUltimoBloque = gateway.getTiempoDeCreacionDeUltimoBloque();
                if ((tiempoActual - tiempoInicio > 10000) &&
                        (tiempoActual - tiempoDelUltimoBloque  > 10000)) {
                    break;
                }
            }
        }
    }
}
