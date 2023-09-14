package gatewayVersion.blockchainMultiple.conexion;

import java.io.*;
import java.net.*;
import java.util.Map;

import constantes.Direccion;
import gatewayVersion.blockchainMultiple.mensajes.InfoNodo;
import gatewayVersion.blockchainMultiple.mensajes.Mensaje;
import gatewayVersion.blockchainMultiple.mensajes.Paquete;
import gatewayVersion.blockchainMultiple.nodo.Red;

public class Salida {
    private final int timeout = 1000;

    public Salida() {
    }

    public void broadcastMensaje(Mensaje mensaje, Map<String, Integer> direccionesPuertos) {
        // System.out.println("Broadcast mensaje");
        direccionesPuertos.forEach((d, p) -> enviarMensaje(d, p, mensaje));
    }

    public void enviarMensaje(String direccion, Integer puerto, Mensaje mensaje) {
        Socket socket;
        try {
            socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(direccion, puerto);
            socket.connect(isa);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(mensaje);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void broadcastInformacionNodo(Map<String, Integer> puertos, InfoNodo infoNodo) {
        puertos.forEach((d, p) -> enviarInfoNodo(d, p, infoNodo));
    }

    public void enviarInfoNodo(String direccion, Integer puerto, InfoNodo infoNodo) {
        Socket socket;
        try {
            socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(direccion, puerto);
            socket.connect(isa, timeout);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(infoNodo);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void pedirRed(String direccion, int puerto, Direccion direccionQuePide) {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(direccion, puerto);
            socket.connect(isa, timeout);
            // System.out.println("Conexion iniciada");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(direccionQuePide);
            socket.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void enviarInfoRed(Red infoRed, String direccion, int puerto) {
        Socket socket;
        try {
            socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(direccion, puerto);
            socket.connect(isa, timeout);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(infoRed);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void mandarACrearBloque(String direccion, int puerto, Paquete paquete) {
        Socket socket;
        try {
            socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(direccion, puerto);
            socket.connect(isa);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(paquete);
        } catch (IOException e) {
            //e.printStackTrace();
        }

    }
}
