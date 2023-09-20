package multiple.nodo.gatewayVersion.seleccionador;

import general.constantes.Tipo;
import multiple.mensajes.Paquete;
import multiple.mensajes.TransaccionMultiple;
import multiple.nodo.gatewayVersion.GatewayMultiple;

import java.util.List;

public class SeleccionadorMultipleGateway extends Thread {

    private GatewayMultiple gatewayMultiple;

    public SeleccionadorMultipleGateway(GatewayMultiple gatewayMultiple) {
        this.gatewayMultiple = gatewayMultiple;
    }

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

    private long calcularTiempoParaIniciar() {
        return 10000 - (System.currentTimeMillis() % 10000);
    }

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
