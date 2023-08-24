package blockchainMultiple.conexion;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Set;

import blockchainMultiple.nodo.*;
import blockchainMultiple.mensajes.*;

public class Salida {
    private Nodo miNodo;
    private String host;
    private int puertoEnvio;
    private HashMap<String, Integer> direcciones = new HashMap<>();
    private final int timeout = 1000;

    public Salida(Nodo miNodo) {
        this.miNodo = miNodo;
        
        // Test Tipo 1
        // Nodo 1
        direcciones.put("192.168.0.103", 12341);
        // Nodo 2
        direcciones.put("192.168.0.100", 12342);
        // Nodo 3
        direcciones.put("192.168.0.101", 12343);
        ////////////////////////////////////////////
        /*
        
        // Test Tipo 2
        // Nodo 1
        direcciones.put("26.20.111.124", 12341);
        // Nodo 2
        direcciones.put("26.37.38.157", 12342);
        // Nodo 3
        direcciones.put("26.143.218.218", 12343);
        */
        ///////////////////////////////////////////
    }

    public void broadcastMensaje(Mensaje m) {
        //System.out.println("Broadcast mensaje");
        direcciones.forEach((d, p) -> enviarMensaje(d, p, m));
    }

    private void enviarMensaje(String d, Integer p, Mensaje m) {
        this.host = d;
        this.puertoEnvio = p;
        Socket socket;
        //System.out.println("Envio de Mensaje a " + host);
        if (!miNodo.getDireccion().equals(host)) {
            try {
                socket = new Socket();
                socket.bind(null);
                InetSocketAddress isa = new InetSocketAddress(host, puertoEnvio);
                socket.connect(isa);
                // System.out.println("Conexion iniciada");
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(m);
            } catch (IOException e) {
                //System.out.println("-------------------");
                //System.out.println("No se pudo establecer conexión con " + host);
                //System.out.println("-------------------");
            }
        } else {
            //System.out.println("Nodo local");
            miNodo.recibirMensaje(m);
        }
    }

    public void buscarInformacionRed() {
        pedirRed();
        if (miNodo.getRed() == null) {
            System.out.println("No se pudo copiar un InfoRed");
            System.out.println("Se crea la InfoRed");
            miNodo.setRed(new Red());
        } else {
            System.out.println("Copia de InfoRed creada");
        }
        InfoNodo infoNodo = new InfoNodo(miNodo.getDireccion(), miNodo.getClavePublica(), miNodo.getMontoDeApuesta1(),
                miNodo.getMontoDeApuesta2(), miNodo.getTiempoDeApuesta1(), miNodo.getTiempoDeApuesta2());
        broadcastInformacionNodo(infoNodo);
    }

    private void broadcastInformacionNodo(InfoNodo infoNodo) {
        direcciones
                .forEach((d, p) -> enviarInfoNodo(d, p, infoNodo));
    }

    private void enviarInfoNodo(String d, Integer p, InfoNodo infoNodo) {
        this.host = d;
        this.puertoEnvio = p;
        Socket socket;

        //System.out.println("Envio de información del nodo a " + host);
        if (!miNodo.getDireccion().equals(host)) {
            try {
                socket = new Socket();
                socket.bind(null);
                InetSocketAddress isa = new InetSocketAddress(host, puertoEnvio);
                socket.connect(isa, timeout);
                //System.out.println("Conexion iniciada");
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(infoNodo);
            } catch (IOException e) {
                //System.out.println("-------------------");
                //System.out.println("No se pudo establecer conexión con " + host);
                //System.out.println("-------------------");
            }
        } else {
            //System.out.println("Nodo local");
            Red infoRed = miNodo.getRed();
            infoRed.addNode(infoNodo);
        }
    }

    private void pedirRed() {
        Set<String> listaDirecciones = direcciones.keySet();
        for (String direccion : listaDirecciones) {
            if (miNodo.getRed() == null) {
                this.host = direccion;
                this.puertoEnvio = direcciones.get(direccion);
                Socket socket = new Socket();
                //System.out.println("Envío de peticion (Pedir copia de InfoRed) a " + host);
                if (!miNodo.getDireccion().equals(host)) {
                    try {
                        socket.bind(null);
                        InetSocketAddress isa = new InetSocketAddress(host, puertoEnvio);
                        socket.connect(isa, timeout);
                        //System.out.println("Conexion iniciada");
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject("InfoRed" + miNodo.getDireccion());
                        socket.close();
                    } catch (IOException e) {
                        //System.out.println("-------------------");
                        //System.out.println("No se pudo establecer conexión con " + host);
                        //System.out.println("-------------------");
                    }
                } else {
                    // System.out.println("Nodo local");
                    continue;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }

    }

    public void enviarInfoRed(Red infoRed, String direccion) {
        this.host = direccion;
        this.puertoEnvio = direcciones.get(direccion);
        Socket socket;
        //System.out.println("Envio de copia InfoRed a " + host);
        if (!miNodo.getDireccion().equals(host)) {
            try {
                socket = new Socket();
                socket.bind(null);
                InetSocketAddress isa = new InetSocketAddress(host, puertoEnvio);
                socket.connect(isa, timeout);
                //System.out.println("Conexion iniciada");
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(infoRed);
            } catch (IOException e) {
                //System.out.println("-------------------");
                //System.out.println("No se pudo establecer conexión con " + host);
                //System.out.println("-------------------");
            }
        } else {
            //System.out.println("Nodo local");
        }
    }
}
