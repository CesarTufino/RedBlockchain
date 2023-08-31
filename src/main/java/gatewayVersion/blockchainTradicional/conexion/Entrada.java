package gatewayVersion.blockchainTradicional.conexion;

import java.io.*;
import java.net.*;

import gatewayVersion.blockchainTradicional.nodo.Gateway;
import gatewayVersion.blockchainTradicional.nodo.Nodo;

public class Entrada extends Thread {
    private ServerSocket serverSocket;
    private Nodo nodo;
    private Gateway gateway;

    public Entrada(Nodo nodo, int puerto) throws IOException {
        this.serverSocket = new ServerSocket(puerto);
        this.nodo = nodo;
    }

    public Entrada(Gateway gateway, int puerto) throws IOException {
        this.serverSocket = new ServerSocket(puerto);
        this.gateway = gateway;
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                //System.out.println("Conexion aceptada");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object obj = ois.readObject();
                Procesador procesador;
                if (nodo == null) {
                    procesador = new Procesador(nodo, obj);
                } else{
                    procesador = new Procesador(gateway, obj);
                }
                procesador.start();
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
