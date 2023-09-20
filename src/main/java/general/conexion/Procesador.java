package general.conexion;

import general.constantes.Direccion;
import general.mensajes.InfoNodo;
import general.mensajes.Mensaje;
import multiple.mensajes.Paquete;
import multiple.nodo.gatewayVersion.NodoMultipleGateway;
import general.nodo.Gateway;
import general.nodo.Nodo;
import general.nodo.Red;
import tradicional.mensajes.TransaccionTradicional;
import tradicional.nodo.gatewayVersion.NodoTradicionalGateway;

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
            if (nodo != null) {
                nodo.recibirMensaje(message);
            }
            if (gateway != null) {
                gateway.recibirMensaje(message);
            }
        }
        if (objeto instanceof List<?>) {
            System.out.println("Se han recibido un grupo de transacciones");
            List<TransaccionTradicional> transacciones = (List<TransaccionTradicional>) objeto;
            if (nodo != null && nodo instanceof NodoTradicionalGateway nodoTradicionalGateway) {
                nodoTradicionalGateway.generarBloque(transacciones);
            }
        }
        if (objeto instanceof Paquete) {
            System.out.println("Se han recibido un grupo de transacciones");
            Paquete paquete = (Paquete) objeto;
            if (nodo != null && nodo instanceof NodoMultipleGateway nodoMultipleGateway) {
                nodoMultipleGateway.generarBloque(paquete.getTipo(), paquete.getTransacciones());
            }
        }
        // Información del general.nodo
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
            Red red = (Red) objeto;
            nodo.setRed(red);
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
