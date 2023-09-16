package multiple.nodo.gatewayVersion.seleccionador;

import constantes.Tipo;
import multiple.mensajes.Paquete;
import multiple.mensajes.Transaccion;
import multiple.nodo.gatewayVersion.Gateway;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class SeleccionadorAleatorio extends Thread {

    private Gateway gateway;

    public SeleccionadorAleatorio(Gateway gateway) {
        this.gateway = gateway;
    }

    public void seleccionar(Tipo tipo) {
        String direccionNodoSeleccionado1 = gateway.obtenerDireccionNodoPosible();
        String direccionNodoSeleccionado2 = gateway.obtenerDireccionNodoPosible();

        List<Transaccion> transacciones = gateway.escogerTransacciones(tipo);
        Paquete paquete = new Paquete(tipo, transacciones);
        System.out.println("Se envía a crear a " + direccionNodoSeleccionado1);
        gateway.mandarCrearBloque(direccionNodoSeleccionado1, gateway.getPuertos().get(direccionNodoSeleccionado1), paquete);
        System.out.println("Se envía a crear a "+ direccionNodoSeleccionado2);
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

            SecureRandom secureRandom = new SecureRandom();
            int semilla = secureRandom.nextInt();
            Random rnd = new Random(semilla);
            int numeroPseudoaleatorio = rnd.nextInt(
                    gateway.getTransaccionesPendientes().get(Tipo.LOGICO1).size()
                            + gateway.getTransaccionesPendientes().get(Tipo.LOGICO2).size());

            if (numeroPseudoaleatorio < gateway.getTransaccionesPendientes().get(Tipo.LOGICO1).size()) {
                seleccionar(Tipo.LOGICO1);
            } else {
                seleccionar(Tipo.LOGICO2);
            }
            gateway.reiniciarNodosPosibles();
            while (true) {
                tiempoActual = System.currentTimeMillis();
                tiempoDelUltimoBloqueTipo1 = gateway.getTiempoDeCreacionDeUltimoBloque().get(Tipo.LOGICO1);
                tiempoDelUltimoBloqueTipo2 = gateway.getTiempoDeCreacionDeUltimoBloque().get(Tipo.LOGICO2);
                if ((tiempoActual - tiempoInicio > 10000) &&
                        (tiempoActual - tiempoDelUltimoBloqueTipo1  > 10000) &&
                        (tiempoActual - tiempoDelUltimoBloqueTipo2 > 10000)) {
                    break;
                }
            }
        }
    }

}
