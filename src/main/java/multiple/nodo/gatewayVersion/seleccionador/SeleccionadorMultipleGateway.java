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
        long tiempoDelUltimoBloqueTipo1;
        long tiempoDelUltimoBloqueTipo2;
        while (true) {
            tiempoInicio = System.currentTimeMillis();
            System.out.println("Seleccionando...");

            seleccionar(Tipo.LOGICO1);

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            seleccionar(Tipo.LOGICO2);
            gatewayMultiple.reiniciarNodosPosibles();

            while (true) {
                tiempoActual = System.currentTimeMillis();
                tiempoDelUltimoBloqueTipo1 = gatewayMultiple.getTiempoDeCreacionDeUltimoBloque().get(Tipo.LOGICO1);
                tiempoDelUltimoBloqueTipo2 = gatewayMultiple.getTiempoDeCreacionDeUltimoBloque().get(Tipo.LOGICO2);
                if ((tiempoActual - tiempoInicio > 10000) && (
                        (tiempoActual - tiempoDelUltimoBloqueTipo1 > 10000) ||
                        (tiempoActual - tiempoDelUltimoBloqueTipo2 > 10000))) {
                    break;
                }
            }
        }
    }

}
