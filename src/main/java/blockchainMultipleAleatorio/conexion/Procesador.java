package blockchainMultipleAleatorio.conexion;

import blockchainMultipleAleatorio.mensajes.InfoNodo;
import blockchainMultipleAleatorio.mensajes.Mensaje;
import blockchainMultipleAleatorio.nodo.Red;
import blockchainMultipleAleatorio.nodo.Nodo;

public class Procesador extends Thread {
    private Nodo nodo;
    private Object objeto;

    public Procesador(Nodo nodo, Object objeto) {
        this.nodo = nodo;
        this.objeto = objeto;
    }

    public void procesarobjetoeto() {
        // Mensajes
        if (objeto instanceof Mensaje) {
            Mensaje message = (Mensaje) objeto;
            // System.out.println("El objetoeto es un mensaje");
            nodo.recibirMensaje(message);
        }
        // Claves
        if (objeto instanceof InfoNodo) {
            System.out.println("Se ha recibido la información de un nodo");
            InfoNodo infoNodo = (InfoNodo) objeto;
            Red infoRed = nodo.getRed();
            infoRed.addNode(infoNodo);
        }
        // InfoRed
        if (objeto instanceof Red) {
            System.out.println("Se ha recibido la información de la red");
            Red infoRed = (Red) objeto;
            nodo.setRed(infoRed);
        }
        // Strings
        if (objeto instanceof String) {
            String peticion = (String) objeto;
            // System.out.println("Se recibio un string");
            // System.out.println("La petición es " + peticion);
            if (peticion.length() >= 7) {
                switch (peticion.substring(0, 7)) {
                    case "InfoRed":
                        nodo.enviarInfoRed(peticion.substring(7));
                }
            }
        }
    }

    public void run() {
        procesarobjetoeto();
    }
}