package tests.testV6;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import general.constantes.MaximoDeBloques;
import general.constantes.Tipo;
import general.conexion.Entrada;
import multiple.nodo.gatewayVersion.NodoMultipleGateway;
import general.constantes.Direccion;

public class TestV6Nodo1 {

    public static void main(String[] args) throws IOException {
        NodoMultipleGateway nodoMultipleGateway = new NodoMultipleGateway(1, Direccion.DIRECCION_1, true);
        Entrada hiloEntrada = new Entrada(nodoMultipleGateway);
        hiloEntrada.start();
        nodoMultipleGateway.buscarRed();
        while (true) {
            if (nodoMultipleGateway.comprobarCantidadMinimaDeNodos()) break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            if (nodoMultipleGateway.getRed().getBlockchain().obtenerCantidadDeBloques() - 2 == MaximoDeBloques.MAX.getCantidad()) {
                break;
            }
            int a = (int) (((Math.random()) * 2) + 1);
            if (a == 1)
                nodoMultipleGateway.enviarDinero(1.23, Direccion.DIRECCION_2.getDireccionIP(), Tipo.LOGICO1);
            else
                nodoMultipleGateway.enviarDinero(3.47, Direccion.DIRECCION_3.getDireccionIP(), Tipo.LOGICO2);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        while (true) {
            if (nodoMultipleGateway.getRed().getBlockchain().obtenerCantidadDeBloques() - 2 == MaximoDeBloques.MAX.getCantidad()) {
                try {
                    BufferedWriter archivo = new BufferedWriter(
                            new FileWriter("Blockchain V6 (Gateway-Multiple) - Resultado.txt", true));
                    archivo.write(nodoMultipleGateway.getRed().getStats());
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
