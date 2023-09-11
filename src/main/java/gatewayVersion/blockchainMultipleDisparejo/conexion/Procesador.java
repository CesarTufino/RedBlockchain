package gatewayVersion.blockchainMultipleDisparejo.conexion;

import direcciones.Direccion;
import gatewayVersion.blockchainMultipleDisparejo.mensajes.InfoNodo;
import gatewayVersion.blockchainMultipleDisparejo.mensajes.Mensaje;
import gatewayVersion.blockchainMultipleDisparejo.mensajes.Paquete;
import gatewayVersion.blockchainMultipleDisparejo.nodo.Gateway;
import gatewayVersion.blockchainMultipleDisparejo.nodo.Nodo;
import gatewayVersion.blockchainMultipleDisparejo.nodo.Red;

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
            if (nodo != null) {
                nodo.recibirMensaje(message);
            }
            if (gateway != null) {
                gateway.recibirMensaje(message);
            }
        }
        if (objeto instanceof Paquete) {
            System.out.println("Se han recibido un grupo de transacciones");
            Paquete paquete = (Paquete) objeto;
            if (nodo != null) {
                nodo.generarBloque(paquete);
            }
        }
        // Información del nodo
        if (objeto instanceof InfoNodo) {
            System.out.println("Se ha recibido la información de un nodo");
            InfoNodo infoNodo = (InfoNodo) objeto;
            if (nodo != null) {
                nodo.getRed().addNode(infoNodo);
            }
            if (gateway != null) {
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
        if (objeto instanceof Direccion) {
            System.out.println("Se envia red");
            Direccion direccion = (Direccion) objeto;
            nodo.enviarInfoRed(direccion);
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
