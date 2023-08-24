package blockchainTradicional.conexion;

import blockchainTradicional.mensajes.InfoNodo;
import blockchainTradicional.mensajes.Mensaje;
import blockchainTradicional.nodo.Nodo;
import blockchainTradicional.nodo.Red;

public class Procesador extends Thread {
    private Nodo nodo;
    private Object objeto;

    public Procesador(Nodo nodo, Object obj) {
        this.nodo = nodo;
        this.objeto = obj;
    }

    public void procesarObjeto() {
        // Mensajes
        if (objeto instanceof Mensaje) {
            Mensaje message = (Mensaje) objeto;
            nodo.recibirMensaje(message);
        }
        // Información del nodo
        if (objeto instanceof InfoNodo) {
            System.out.println("Se ha recibido la información de un nodo");
            InfoNodo infoNodo = (InfoNodo) objeto;
            nodo.getRed().addNode(infoNodo);
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
        procesarObjeto();
    }
}
