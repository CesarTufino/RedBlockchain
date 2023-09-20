package tests.testV5;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import general.conexion.Entrada;
import general.constantes.MaximoDeBloques;
import tradicional.nodo.gatewayVersion.NodoTradicionalGateway;
import general.constantes.Direccion;

public class TestV5Nodo2 {

    public static void main(String[] args) throws IOException {
        NodoTradicionalGateway nodoTradicionalGateway = new NodoTradicionalGateway(2, Direccion.DIRECCION_2); // Mi general.nodo
        Entrada hiloEntrada = new Entrada(nodoTradicionalGateway);
        hiloEntrada.start();
        nodoTradicionalGateway.buscarRed();
        // Espera hasta que exitan tres nodos en la red
        while (true) {
            if (nodoTradicionalGateway.comprobarCantidadMinimaDeNodos())
                break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /// Generación de transacciones
        for (int i = 0; i < 700; i++) {
            if (nodoTradicionalGateway.getRed().getBlockchain().obtenerCantidadDeBloques() - 1 == MaximoDeBloques.MAX.getCantidad()) {
                break;
            }
            int a = (int) (((Math.random()) * 2) + 1);
            if (a == 1)
                nodoTradicionalGateway.enviarDinero(1.23, Direccion.DIRECCION_3.getDireccionIP());
            else
                nodoTradicionalGateway.enviarDinero(3.47, Direccion.DIRECCION_1.getDireccionIP());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        while (true) {
            if (nodoTradicionalGateway.getRed().getBlockchain().obtenerCantidadDeBloques() - 1 == MaximoDeBloques.MAX.getCantidad()) {
                try {
                    BufferedWriter archivo = new BufferedWriter(
                            new FileWriter("Blockchain V5 (Gateway-Tradicional) - Resultado.txt", true));
                    archivo.write(nodoTradicionalGateway.getRed().getStats());
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
