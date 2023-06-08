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
                BufferedWriter archivo = new BufferedWriter(new FileWriter("mensajes.txt", true));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    System.out.println("Mensaje: " + mensaje);
                    archivo.write(mensaje);
                    archivo.newLine();
                }
                archivo.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
