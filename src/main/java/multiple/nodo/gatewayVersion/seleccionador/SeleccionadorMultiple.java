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
            seleccionar(Tipo.LOGICO1);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tiempoDelUltimoBloqueTipo2 = System.currentTimeMillis();
            seleccionar(Tipo.LOGICO2);
            gateway.reiniciarNodosPosibles();

            while (true) {
                tiempoActual = System.currentTimeMillis();
                if ((tiempoActual - tiempoDelUltimoBloqueTipo2 > 10000)) {
                    break;
                }
            }
        }
    }

}
