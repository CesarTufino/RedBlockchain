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

/**
 * La clase Procesador se encarga de determinar la acción que se debe realizar para cada tipo de objeto.
 */
public class Procesador extends Thread {
    private Nodo nodo;
    private Gateway gateway;
    private Object objeto;

    /**
     * Constructor de Procesador para un nodo.
     * @param nodo el nodo que recibirá la información.
     * @param obj el objeto que se va a procesar.
     */
    public Procesador(Nodo nodo, Object obj) {
        this.nodo = nodo;
        this.objeto = obj;
    }

    /**
     * Constructor de Procesador para un gateway.
     * @param gateway el gateway que recibirá la información.
     * @param obj el objeto que se va a procesar.
     */
    public Procesador(Gateway gateway, Object obj) {
        this.gateway = gateway;
        this.objeto = obj;
    }

    /**
     * Determina el tipo de objeto y llama a los métodos del nodo o gateway según corresponda.
     * @throws Exception si existe un problema de verificación de firma del objeto recibido.
     */
    public void procesarObjeto() throws Exception {
        // Mensajes
        if (objeto instanceof Mensaje mensaje) {
            if (nodo != null) {
                nodo.recibirMensaje(mensaje);
            }
            if (gateway != null) {
                gateway.recibirMensaje(mensaje);
            }
        }
        if (objeto instanceof List<?>) {
            System.out.println("Se han recibido un grupo de transacciones");
            List<TransaccionTradicional> transacciones = (List<TransaccionTradicional>) objeto;
            if (nodo != null && nodo instanceof NodoTradicionalGateway nodoTradicionalGateway) {
                nodoTradicionalGateway.generarBloque(transacciones);
            }
        }
        if (objeto instanceof Paquete paquete) {
            System.out.println("Se han recibido un grupo de transacciones");
            if (nodo != null && nodo instanceof NodoMultipleGateway nodoMultipleGateway) {
                nodoMultipleGateway.generarBloque(paquete.getTipo(), paquete.getTransacciones());
            }
        }
        // Información del general.nodo
        if (objeto instanceof InfoNodo infoNodo) {
            System.out.println("Se ha recibido la información de un nodo");
            if (nodo != null) {
                nodo.getRed().addNode(infoNodo);
            }
            if (gateway != null) {
                gateway.agregarNodo(infoNodo);
            }
        }
        // Información de la red
        if (objeto instanceof Red red) {
            System.out.println("Se ha recibido la información de la red");
            nodo.setRed(red);
        }
        // Strings
        if (objeto instanceof Direccion direccion) {
            System.out.println("Se envia red");
            nodo.enviarInfoRed(direccion);
        }
    }

    /**
     * Llama al método que procesa el objeto.
     */
    @Override
    public void run() {
        try {
            procesarObjeto();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
