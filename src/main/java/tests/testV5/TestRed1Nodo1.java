package tests.testV5;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import constantes.MaximoDeBloques;
import tradicional.conexion.Entrada;
import tradicional.nodo.gatewayVersion.Nodo;
import constantes.Direccion;

public class TestRed1Nodo1 {

    public static void main(String[] args) throws IOException {
        Nodo nodo = new Nodo(1, Direccion.DIRECCION_1);
        Entrada hiloEntrada = new Entrada(nodo);
        hiloEntrada.start();
        while (true) {
            if (nodo.comprobarCantidadMinimaDeNodos()) break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            if (nodo.getRed().getBlockchain().obtenerCantidadDeBloques() - 1 == MaximoDeBloques.MAX.getCantidad()) {
                break;
            }
            int a = (int) (((Math.random()) * 2) + 1);
            if (a == 1)
                nodo.enviarDinero(1.23, Direccion.DIRECCION_2.getDireccionIP());
            else
                nodo.enviarDinero(3.47, Direccion.DIRECCION_3.getDireccionIP());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        while (true) {
            if (nodo.getRed().getBlockchain().obtenerCantidadDeBloques() - 1 == MaximoDeBloques.MAX.getCantidad()) {
                try {
                    BufferedWriter archivo = new BufferedWriter(
                            new FileWriter("Blockchain V5 (Gateway-Tradicional) - Resultado.txt", true));
                    archivo.write(nodo.getRed().getStats());
                    archivo.newLine();
                    archivo.close();
                    System.out.println("Archivo guardado");
                } catch (IOException e) {
                }
                System.exit(0);
            }
        }
    }
}
