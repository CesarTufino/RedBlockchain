package tests.testV1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import general.conexion.Entrada;
import general.constantes.Direccion;
import general.constantes.MaximoDeBloques;
import tradicional.nodo.posVersion.NodoTradicionalPos;
import tradicional.nodo.posVersion.seleccionador.SeleccionadorTradicionalPos;

public class TestV1Nodo2 {

    public static void main(String[] args) throws IOException {
        NodoTradicionalPos nodoTradicionalPos = new NodoTradicionalPos(2, Direccion.DIRECCION_2);
        nodoTradicionalPos.apostar(25); // Poner el stake
        // Hilo para escuchar
        general.conexion.Entrada hiloEntrada = new Entrada(nodoTradicionalPos);
        hiloEntrada.start();
        // Buscar datos en la red
        nodoTradicionalPos.buscarRed();
        // Espera hasta que exitan tres nodos en la red
        while (true) {
            if (nodoTradicionalPos.comprobarCantidadMinimaDeNodos()) break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Hilo para validación PoS
        SeleccionadorTradicionalPos hiloSeleccionadorTradicionalPos = new SeleccionadorTradicionalPos(nodoTradicionalPos);
        hiloSeleccionadorTradicionalPos.start();
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            if (nodoTradicionalPos.getRed().getBlockchainTradicional().obtenerCantidadDeBloques() - 1 == MaximoDeBloques.MAX.getCantidad()) {
                break;
            }
            int a = (int) (((Math.random()) * 2) + 1);
            if (a == 1)
                nodoTradicionalPos.enviarDinero(1.23, Direccion.DIRECCION_3.getDireccionIP());
            else
                nodoTradicionalPos.enviarDinero(3.47, Direccion.DIRECCION_1.getDireccionIP());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        while (true) {
            if (nodoTradicionalPos.getRed().getBlockchainTradicional().obtenerCantidadDeBloques() - 1 == MaximoDeBloques.MAX.getCantidad()) {
                try {
                    BufferedWriter archivo = new BufferedWriter(
                            new FileWriter("Blockchain V1 (POS-Tradicional) - Resultado.txt", true));
                    archivo.write(nodoTradicionalPos.getRed().getStats());
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
