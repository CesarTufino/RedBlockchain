package gatewayVersion.blockchainTradicional.conexion;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gatewayVersion.blockchainTradicional.mensajes.InfoNodo;
import gatewayVersion.blockchainTradicional.mensajes.Mensaje;
import gatewayVersion.blockchainTradicional.mensajes.Transaccion;
import gatewayVersion.blockchainTradicional.nodo.Red;

public class Salida {
    private String host;
    private int puertoEnvio;
    private HashMap<String, Integer> direcciones = new HashMap<>();
    private final int timeout = 1000;

    public Salida() {
    }

    public void broadcastMensaje(Mensaje m) {
        // System.out.println("Broadcast mensaje");
        direcciones.forEach((d, p) -> enviarMensaje(d, p, m));
    }

    public void enviarMensaje(String d, Integer p, Mensaje m) {
        this.host = d;
        this.puertoEnvio = p;
        Socket socket;
        try {
            socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(host, puertoEnvio);
            socket.connect(isa);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastInformacionNodo(Map<String, Integer> puertos, InfoNodo infoNodo) {
        puertos.forEach((d, p) -> enviarInfoNodo(d, p, infoNodo));
    }

    public void enviarInfoNodo(String d, Integer p, InfoNodo infoNodo) {
        this.host = d;
        this.puertoEnvio = p;
        Socket socket;
        try {
            socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(host, puertoEnvio);
            socket.connect(isa, timeout);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(infoNodo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pedirRed(String direccion, String s) {
        this.host = direccion;
        this.puertoEnvio = direcciones.get(direccion);
        Socket socket = new Socket();
        try {
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(host, puertoEnvio);
            socket.connect(isa, timeout);
            // System.out.println("Conexion iniciada");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject("InfoRed" + s);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarInfoRed(Red infoRed, String direccion) {
        this.host = direccion;
        this.puertoEnvio = direcciones.get(direccion);
        Socket socket;
        try {
            socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(host, puertoEnvio);
            socket.connect(isa, timeout);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(infoRed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mandarACrearBloque(String direccionDeNodo, int puerto, List<Transaccion> transacciones) {
        this.host = direccionDeNodo;
        this.puertoEnvio = puerto;
        Socket socket;
        try {
            socket = new Socket();
            socket.bind(null);
            InetSocketAddress isa = new InetSocketAddress(host, puertoEnvio);
            socket.connect(isa);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(transacciones);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
