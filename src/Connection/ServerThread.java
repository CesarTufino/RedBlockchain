package Connection;

import java.io.*;
import java.net.*;

public class ServerThread extends Thread {
    private ServerSocket serverSocket;

    public ServerThread(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Conexión aceptada");
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    System.out.println("Mensaje: " + mensaje);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
