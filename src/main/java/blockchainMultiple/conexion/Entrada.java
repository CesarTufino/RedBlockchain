package blockchainMultiple.conexion;

import java.io.*;
import java.net.*;
import blockchainMultiple.nodo.Nodo;

public class Entrada extends Thread {
    private ServerSocket serverSocket;
    private Nodo nodo;

    public Entrada(Nodo nodo, int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
        this.nodo = nodo;
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                //System.out.println("Conexion aceptada");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object obj = ois.readObject();
                Procesador procesador = new Procesador(nodo, obj);
                procesador.start();
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}