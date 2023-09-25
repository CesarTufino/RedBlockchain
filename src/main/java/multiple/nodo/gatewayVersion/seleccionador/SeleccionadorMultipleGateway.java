package multiple.nodo.gatewayVersion.seleccionador;

import general.constantes.Tipo;
import multiple.mensajes.Paquete;
import multiple.mensajes.TransaccionMultiple;
import multiple.nodo.gatewayVersion.GatewayMultiple;

import java.util.List;

/**
 * La clase SeleccionadorAleatorioGateway se encarga de realizar las iteraciones de selección de cuatro nodos para la
 * creación de un bloque de cada tipo cada 10 segundos.
 */
public class SeleccionadorMultipleGateway extends Thread {
    private GatewayMultiple gatewayMultiple;

    public SeleccionadorMultipleGateway(GatewayMultiple gatewayMultiple) {
        this.gatewayMultiple = gatewayMultiple;
    }

    /**
     * Selecciona dos nodos y envía un paquete de transacciones para que los nodos creen un bloque del tipo
     * especificado.
     * @param tipo tipo del bloque que se va a crear.
     */
    public void seleccionar(Tipo tipo) {
        String direccionNodoSeleccionado1 = gatewayMultiple.obtenerDireccionNodoPosible();
        String direccionNodoSeleccionado2 = gatewayMultiple.obtenerDireccionNodoPosible();

        List<TransaccionMultiple> transacciones = gatewayMultiple.escogerTransacciones(tipo);
        Paquete paquete = new Paquete(tipo, transacciones);
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado1);
        gatewayMultiple.mandarCrearBloque(direccionNodoSeleccionado1, gatewayMultiple.getPuertos().get(direccionNodoSeleccionado1), paquete);
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado2);
        gatewayMultiple.mandarCrearBloque(direccionNodoSeleccionado2, gatewayMultiple.getPuertos().get(direccionNodoSeleccionado2), paquete);
    }

    /**
     * Calcula el tiempo que falta para que hayan transcurrido 10 segundos reloj.
     * @return tiempo restante para que hayan transcurrido 10 segundos reloj.
     */
    private long calcularTiempoParaIniciar() {
        return 10000 - (System.currentTimeMillis() % 10000);
    }

    /**
     * Realiza una iteración, donde se escoge al azar cuatro nodos para los dos nuevos bloques.
     */
    private void iniciarIteracion() {
        System.out.println("Seleccionando...");

        seleccionar(Tipo.LOGICO1);

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        seleccionar(Tipo.LOGICO2);
        gatewayMultiple.reiniciarNodosPosibles();
    }

    /**
     * Llama cada 10 segundos, mediante un bucle, al método que realiza la iteración de creación de bloques.
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
