package multiple.nodo.gatewayVersion.seleccionador;

import general.constantes.Tipo;
import multiple.mensajes.Paquete;
import multiple.mensajes.TransaccionMultiple;
import multiple.nodo.gatewayVersion.GatewayMultiple;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class SeleccionadorProbabilidadDefinidaGateway extends Thread {

    private GatewayMultiple gatewayMultiple;
    private final double PROBABILIDAD_BLOQUES_TIPO_1 = 95;

    public SeleccionadorProbabilidadDefinidaGateway(GatewayMultiple gatewayMultiple) {
        this.gatewayMultiple = gatewayMultiple;
    }

    public void seleccionar(Tipo tipo) {
        String direccionNodoSeleccionado1 = gatewayMultiple.obtenerDireccionNodoPosible();
        String direccionNodoSeleccionado2 = gatewayMultiple.obtenerDireccionNodoPosible();

        List<TransaccionMultiple> transacciones = gatewayMultiple.escogerTransacciones(tipo);
        Paquete paquete = new Paquete(tipo, transacciones);
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado1);
        gatewayMultiple.mandarCrearBloque(direccionNodoSeleccionado1, gatewayMultiple.getPuertos().get(direccionNodoSeleccionado1), paquete);
        System.out.println("Se envía a crear a "+ direccionNodoSeleccionado2);
        gatewayMultiple.mandarCrearBloque(direccionNodoSeleccionado2, gatewayMultiple.getPuertos().get(direccionNodoSeleccionado2), paquete);
    }

    private long calcularTiempoParaIniciar() {
        return 10000 - (System.currentTimeMillis() % 10000);
    }

    private void iniciarIteracion() {
        System.out.println("Seleccionando...");

        SecureRandom secureRandom = new SecureRandom();
        int semilla = secureRandom.nextInt();
        Random rnd = new Random(semilla);
        int numeroPseudoaleatorio = rnd.nextInt(100);

        if (numeroPseudoaleatorio < PROBABILIDAD_BLOQUES_TIPO_1) {
            seleccionar(Tipo.LOGICO1);
        } else {
            seleccionar(Tipo.LOGICO2);
        }
        gatewayMultiple.reiniciarNodosPosibles();
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
