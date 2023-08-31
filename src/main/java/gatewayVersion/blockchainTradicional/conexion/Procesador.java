package gatewayVersion.blockchainTradicional.conexion;

import gatewayVersion.blockchainTradicional.mensajes.InfoNodo;
import gatewayVersion.blockchainTradicional.mensajes.Mensaje;
import gatewayVersion.blockchainTradicional.mensajes.Transaccion;
import gatewayVersion.blockchainTradicional.nodo.Gateway;
import gatewayVersion.blockchainTradicional.nodo.Nodo;
import gatewayVersion.blockchainTradicional.nodo.Red;

import java.util.List;

public class Procesador extends Thread {
    private Nodo nodo;
    private Gateway gateway;
    private Object objeto;

    public Procesador(Nodo nodo, Object obj) {
        this.nodo = nodo;
        this.objeto = obj;
    }

    public Procesador(Gateway gateway, Object obj) {
        this.gateway = gateway;
        this.objeto = obj;
    }

    public void procesarObjeto() throws Exception {
        // Mensajes
        if (objeto instanceof Mensaje) {
            Mensaje message = (Mensaje) objeto;
            if (nodo != null){
                nodo.recibirMensaje(message);
            }
            if (gateway != null){
                gateway.recibirMensaje(message);
            }
        }
        if (objeto instanceof List<?>) {
            System.out.println("Se han recibido un grupo de transacciones");
            List<Transaccion> transacciones = (List<Transaccion>) objeto;
            if (nodo != null){
                nodo.generarBloque(transacciones);
            }
        }
        // Información del nodo
        if (objeto instanceof InfoNodo) {
            System.out.println("Se ha recibido la información de un nodo");
            InfoNodo infoNodo = (InfoNodo) objeto;

            if (nodo != null){
                nodo.getRed().addNode(infoNodo);
            }
            if (gateway != null){
                gateway.agregarNodo(infoNodo);
            }
        }
        // Información de la red
        if (objeto instanceof Red) {
            System.out.println("Se ha recibido la información de la red");
            Red infoRed = (Red) objeto;
            nodo.setRed(infoRed);
        }
        // Strings
        if (objeto instanceof String) {
            String peticion = (String) objeto;
            // System.out.println("Petición " + peticion);
            if (peticion.length() >= 7) {
                switch (peticion.substring(0, 7)) {
                    case "InfoRed":
                        nodo.enviarInfoRed(peticion.substring(7));
                }
            }
        }
    }

    public void run() {
        try {
            procesarObjeto();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
