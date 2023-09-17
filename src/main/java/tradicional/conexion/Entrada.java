package tradicional.conexion;

import java.io.*;
import java.net.*;

import tradicional.nodo.gatewayVersion.Gateway;
import tradicional.nodo.gatewayVersion.Nodo;

public class Entrada extends Thread {
    private ServerSocket serverSocket;
    private Nodo nodo;
    private Gateway gateway;

    public Entrada(Nodo nodo) throws IOException {
        this.serverSocket = new ServerSocket(nodo.getDireccion().getPuerto());
        this.nodo = nodo;
    }

    public Entrada(Gateway gateway) throws IOException {
        this.serverSocket = new ServerSocket(gateway.getDireccion().getPuerto());
        this.gateway = gateway;
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object obj = ois.readObject();
                Procesador procesador;
                if (nodo != null) {
                    procesador = new Procesador(nodo, obj);
                } else{
                    procesador = new Procesador(gateway, obj);
                }
                procesador.start();
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }
}
