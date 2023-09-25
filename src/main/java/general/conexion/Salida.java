package general.conexion;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

import general.constantes.Direccion;
import general.mensajes.InfoNodo;
import general.mensajes.Mensaje;
import multiple.mensajes.Paquete;
import general.nodo.Red;
import tradicional.mensajes.TransaccionTradicional;

/**
 * La clase Salida se encarga de enviar objetos a otros dispositivos mediante sockets.
 */
public class Salida {

    /**
     * Tiempo de espera máximo para intentar enviar objetos
     */
    private final int TIMEOUT = 1000;

    public Salida() {
    }

    /**
     * Llama al método para enviar un mensaje a todas las direcciones indicadas.
     * @param mensaje el mensaje que se va a enviar.
     * @param direccionesPuertos conjunto de direcciones y puertos que se utilizarán para el envío.
     */
    public void broadcastMensaje(Mensaje mensaje, Map<String, Integer> direccionesPuertos) {
        // System.out.println("Broadcast mensaje");
        direccionesPuertos.forEach((d, p) -> enviarMensaje(d, p, mensaje));
    }

    /**
     * Envía un objeto mensaje a la dirección y puerto indicados mediante socket.
     * @param direccion la dirección a la que se va a enviar el mensaje.
     * @param puerto el puerto por el cual el dispotivo recibirá el objeto.
     * @param mensaje el mensaje que se va a enviar.
     */
    public void enviarMensaje(String direccion, Integer puerto, Mensaje mensaje) {
        try {
            Socket socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(direccion, puerto);
            socket.connect(isa);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(mensaje);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Llama al método para enviar la información de un nodo a todas las direcciones indicadas.
     * @param puertos conjunto de direcciones y puertos que se utilizarán para el envío.
     * @param infoNodo información del nodo que se va a enviar.
     */
    public void broadcastInformacionNodo(Map<String, Integer> puertos, InfoNodo infoNodo) {
        puertos.forEach((d, p) -> enviarInfoNodo(d, p, infoNodo));
    }

    /**
     * Envía un objeto con la información de un nodo a la dirección y puerto indicados.
     * @param direccion la dirección a la que se va a enviar el objeto.
     * @param puerto el puerto por el cual el dispotivo recibirá el objeto.
     * @param infoNodo información del nodo que se va a enviar.
     */
    public void enviarInfoNodo(String direccion, Integer puerto, InfoNodo infoNodo) {
        try {
            Socket socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(direccion, puerto);
            socket.connect(isa, TIMEOUT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(infoNodo);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Envía un objeto con la dirección de un nodo a la dirección y puerto indicados.
     * @param direccion la dirección a la que se va a enviar el objeto.
     * @param puerto el puerto por el cual el dispotivo recibirá el objeto.
     * @param direccionQuePide dirección del nodo que se va a enviar.
     */
    public void pedirRed(String direccion, int puerto, Direccion direccionQuePide) {
        try {
            Socket socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(direccion, puerto);
            socket.connect(isa, TIMEOUT);
            // System.out.println("Conexion iniciada");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(direccionQuePide);
            socket.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Envía un objeto con la información de la red a la dirección y puerto indicados.
     * @param red red que se va a enviar.
     * @param direccion la dirección a la que se va a enviar el objeto.
     * @param puerto el puerto por el cual el dispotivo recibirá el objeto.
     */
    public void enviarInfoRed(Red red, String direccion, int puerto) {
        try {
            Socket socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(direccion, puerto);
            socket.connect(isa, TIMEOUT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(red);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Envía un objeto con un paquete de transacciones a la dirección y puerto indicados.
     * @param direccion la dirección a la que se va a enviar el objeto.
     * @param puerto el puerto por el cual el dispotivo recibirá el objeto.
     * @param paquete paquete de transacciones que se va a enviar.
     */
    public void mandarACrearBloque(String direccion, int puerto, Paquete paquete) {
        try {
            Socket socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(direccion, puerto);
            socket.connect(isa);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(paquete);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Envía un objeto con una lista de transacciones a la dirección y puerto indicados.
     * @param direccion la dirección a la que se va a enviar el objeto.
     * @param puerto  el puerto por el cual el dispotivo recibirá el objeto.
     * @param transacciones lista de transacciones que se va a enviar.
     */
    public void mandarACrearBloque(String direccion, int puerto, List<TransaccionTradicional> transacciones) {
        try {
            Socket socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(direccion, puerto);
            socket.connect(isa);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(transacciones);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

}
