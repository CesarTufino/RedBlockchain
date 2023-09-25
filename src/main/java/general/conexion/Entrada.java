package general.conexion;

import java.io.*;
import java.net.*;

import general.nodo.Gateway;
import general.nodo.Nodo;

/**
 * La clase Entrada se encarga de recibir la información para un Nodo o Gateway.
 */
public class Entrada extends Thread {
    private ServerSocket serverSocket;
    private Nodo nodo;
    private Gateway gateway;

    /**
     * Constructor de Entrada para un nodo.
     * @param nodo el nodo que recibirá la información.
     * @throws IOException si existe un problema al instanciar el ServerSocket con el puerto del nodo.
     */
    public Entrada(Nodo nodo) throws IOException {
        this.serverSocket = new ServerSocket(nodo.getDireccion().getPuerto());
        this.nodo = nodo;
    }

    /**
     * Constructor de Entrada para un nodo.
     * @param gateway el gateway que recibirá la información.
     * @throws IOException si existe un problema al instanciar el ServerSocket con el puerto del gateway.
     */
    public Entrada(Gateway gateway) throws IOException {
        this.serverSocket = new ServerSocket(gateway.getDireccion().getPuerto());
        this.gateway = gateway;
    }

    /**
     * Recibe objetos e instancia un hilo para que procese el objeto.
     * Mediante un bucle se realiza constantemente la recepción de objetos.
     */
    @Override
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

