package tests.testV3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import general.constantes.MaximoDeBloques;
import general.constantes.Tipo;
import general.conexion.Entrada;
import multiple.nodo.posVersion.NodoMultiplePos;
import general.constantes.Direccion;
import multiple.nodo.posVersion.seleccionador.SeleccionadorProbabilidadDefinidaPos;

public class TestV3Nodo1 {

    public static void main(String[] args) throws IOException {
        NodoMultiplePos nodoMultiplePos = new NodoMultiplePos(1, Direccion.DIRECCION_1, false);
        // Poner el stake
        nodoMultiplePos.apostar(30, Tipo.LOGICO1);
        nodoMultiplePos.apostar(40, Tipo.LOGICO2);
        // Hilo para escuchar
        Entrada hiloEntrada = new Entrada(nodoMultiplePos);
        hiloEntrada.start();
        // Buscar datos en la red
        nodoMultiplePos.buscarRed();
        // Espera hasta que exitan tres nodos en la red
        while (true) {
            if (nodoMultiplePos.comprobarCantidadMinimaDeNodos())
                break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Hilo para validación PoS
        SeleccionadorProbabilidadDefinidaPos hiloSeleccionadorProbabilidadDefinidaPos = new SeleccionadorProbabilidadDefinidaPos(nodoMultiplePos);
        hiloSeleccionadorProbabilidadDefinidaPos.start();
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            if (nodoMultiplePos.getRed().getBlockchainMultiple().obtenerCantidadDeBloques() - 2 == MaximoDeBloques.MAX.getCantidad()) {
                break;
            }
            int a = (int) (((Math.random()) * 2) + 1);
            if (a == 1)
                nodoMultiplePos.enviarDinero(1.23, Direccion.DIRECCION_2.getDireccionIP(), Tipo.LOGICO1);
            else
                nodoMultiplePos.enviarDinero(3.47, Direccion.DIRECCION_3.getDireccionIP(), Tipo.LOGICO2);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        while (true) {
            if (nodoMultiplePos.getRed().getBlockchainMultiple().obtenerCantidadDeBloques() - 2 == MaximoDeBloques.MAX.getCantidad()) {
                try {
                    BufferedWriter archivo = new BufferedWriter(
                            new FileWriter("Blockchain V3 (POS-Probabilidad Definida) - Resultado.txt", true));
                    archivo.write(nodoMultiplePos.getRed().getStats());
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
