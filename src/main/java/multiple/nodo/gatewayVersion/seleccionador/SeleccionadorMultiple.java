package multiple.nodo.gatewayVersion.seleccionador;

import constantes.Tipo;
import multiple.mensajes.Paquete;
import multiple.mensajes.Transaccion;
import multiple.nodo.gatewayVersion.Gateway;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class SeleccionadorMultiple extends Thread {

    private Gateway gateway;

    public SeleccionadorMultiple(Gateway gateway) {
        this.gateway = gateway;
    }

    public void seleccionar(Tipo tipo) {
        String direccionNodoSeleccionado1 = gateway.obtenerDireccionNodoPosible();
        String direccionNodoSeleccionado2 = gateway.obtenerDireccionNodoPosible();
        gateway.reiniciarNodosPosibles();

        List<Transaccion> transacciones = gateway.escogerTransacciones(tipo);
        Paquete paquete = new Paquete(tipo, transacciones);
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado1);
        gateway.mandarCrearBloque(direccionNodoSeleccionado1, gateway.getPuertos().get(direccionNodoSeleccionado1), paquete);
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado2);
        gateway.mandarCrearBloque(direccionNodoSeleccionado2, gateway.getPuertos().get(direccionNodoSeleccionado2), paquete);
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
            System.out.println("Seleccionando...");
            tiempoInicio = System.currentTimeMillis();

            seleccionar(Tipo.LOGICO1);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            seleccionar(Tipo.LOGICO2);

            while (true) {
                tiempoActual = System.currentTimeMillis();
                tiempoDelUltimoBloqueTipo1 = gateway.getTiempoDeCreacionDeUltimoBloque().get(Tipo.LOGICO1);
                tiempoDelUltimoBloqueTipo2 = gateway.getTiempoDeCreacionDeUltimoBloque().get(Tipo.LOGICO2);
                if ((tiempoActual - tiempoInicio > 10000) &&
                        (tiempoActual - tiempoDelUltimoBloqueTipo1 > 10000) &&
                        (tiempoActual - tiempoDelUltimoBloqueTipo2 > 10000)) {
                    break;
                }
            }
        }
    }

}
