package Connection;

import java.io.*;
import java.net.*;


public class P2P {
    public static void main(String[] args) throws IOException {
        final String host = "26.20.111.124";
        final int puertoEnvio = 12345;
        final int puertoRecepcion = 12346;

        ServerThread serverThread = new ServerThread(puertoRecepcion);
        serverThread.start();

        Socket socket = new Socket(host, puertoEnvio);
        System.out.println("Conectado iniciada");
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        String mensaje;
        while ((mensaje = stdIn.readLine()) != null) {
            out.println(mensaje);
            if (mensaje.equals("fin")) {
                break;
            }
        }

    }

}
