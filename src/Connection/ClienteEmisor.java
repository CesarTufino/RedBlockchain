package Connection;

import java.io.*;
import java.net.*;

public class ClienteEmisor {
    public static void main(String[] args) {
        final String host = "26.20.111.124";
        final int puerto = 12345;


        try {
            // Establecer conexión con el cliente receptor
            Socket socket = new Socket(host, puerto);            System.out.println("Conectado al cliente receptor");

            // Obtener los flujos de entrada y salida
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Crear hilos para leer los mensajes del receptor y enviar mensajes al receptor
            Thread recibirMensajes = new Thread(() -> {
                try {
                    String mensaje;

                    while ((mensaje = in.readLine()) != null) {
                        System.out.println("Cliente receptor: ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Thread enviarMensajes = new Thread(() -> {
                try {

                    String mensaje;
                    while ((mensaje = stdIn.readLine()) != null) {


                        out.println(mensaje);
                        if (mensaje.equals("fin")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Iniciar los hilos
            recibirMensajes.start();
            enviarMensajes.start();

            // Esperar a que los hilos terminen
            recibirMensajes.join();
            enviarMensajes.join();

            // Cerrar la conexión
            socket.close();
            System.out.println("Conexión cerrada");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}