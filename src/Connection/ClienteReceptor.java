package Connection;

import java.io.*;
import java.net.*;

public class ClienteReceptor {
    public static void main(String[] args) throws IOException {
        final int puerto = 12345;

        try {
            // Crear el socket del servidor y esperar conexiones
            ServerSocket serverSocket = new ServerSocket(puerto);
            System.out.println("Esperando conexión...");

            // Aceptar la conexión entrante
            Socket socket = serverSocket.accept();
            System.out.println("Conexión aceptada del cliente emisor");

            // Obtener los flujos de entrada y salida
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Crear hilos para leer los mensajes del emisor y enviar mensajes al emisor
            Thread recibirMensajes = new Thread(() -> {
                try {
                    String mensaje;
                    while ((mensaje = in.readLine()) != null) {
                        System.out.println("Cliente emisor: " + mensaje);
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

        }
        catch(FileNotFoundException ex){

            System.out.println("Error");
        }}}
