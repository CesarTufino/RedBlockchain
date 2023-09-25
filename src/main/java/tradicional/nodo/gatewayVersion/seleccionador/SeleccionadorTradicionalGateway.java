package tradicional.nodo.gatewayVersion.seleccionador;

import tradicional.mensajes.TransaccionTradicional;
import tradicional.nodo.gatewayVersion.GatewayTradicional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

/**
 * La clase SeleccionadorAleatorioGateway se encarga de realizar las iteraciones de selección de dos nodos para la
 * creación de un bloque cada 10 segundos.
 */
public class SeleccionadorTradicionalGateway extends Thread {
    private GatewayTradicional gatewayTradicional;

    public SeleccionadorTradicionalGateway(GatewayTradicional gatewayTradicional) {
        this.gatewayTradicional = gatewayTradicional;
    }

    /**
     * Selecciona dos nodos y envía un paquete de transacciones para que los nodos creen un bloque.
     */
    public void seleccionar() {
        String direccionNodoSeleccionado1 = gatewayTradicional.obtenerDireccionNodoPosible();
        String direccionNodoSeleccionado2 = gatewayTradicional.obtenerDireccionNodoPosible();

        List<TransaccionTradicional> transacciones = gatewayTradicional.escogerTransacciones();
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado1);
        gatewayTradicional.mandarCrearBloque(direccionNodoSeleccionado1, gatewayTradicional.getPuertos().get(direccionNodoSeleccionado1), transacciones);
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado2);
        gatewayTradicional.mandarCrearBloque(direccionNodoSeleccionado2, gatewayTradicional.getPuertos().get(direccionNodoSeleccionado2), transacciones);
    }

    /**
     * Calcula el tiempo que falta para que hayan transcurrido 10 segundos reloj.
     * @return tiempo restante para que hayan transcurrido 10 segundos reloj.
     */
    private long calcularTiempoParaIniciar() {
        return 10000 - (System.currentTimeMillis() % 10000);
    }

    /**
     * Realiza una iteración, donde se escoge al azar dos nodos para el nuevo bloque.
     */
    private void iniciarIteracion() {
        System.out.println("Seleccionando...");
        seleccionar();
        gatewayTradicional.reiniciarNodosPosibles();
    }

    /**
     * Llama cada 10 segundos, mediante un bucle, al método que realiza la iteración de creación de bloque.
     */
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
