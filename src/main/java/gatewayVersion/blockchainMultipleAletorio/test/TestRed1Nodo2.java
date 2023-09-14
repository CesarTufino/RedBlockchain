package gatewayVersion.blockchainMultipleAletorio.test;

import java.io.IOException;

import constantes.Tipo;
import multiple.conexion.Entrada;
import multiple.nodo.gatewayVersion.Nodo;
import constantes.Direccion;

public class TestRed1Nodo2 {

    public static void main(String[] args) throws IOException {
        Nodo nodo = new Nodo(2, Direccion.DIRECCION_2);
        Entrada hiloEntrada = new Entrada(nodo);
        hiloEntrada.start();
        nodo.buscarRed();
        while (true) {
            if (nodo.comprobarCantidadMinimaDeNodos())
                break;
            System.out.print("");
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            int a = (int) (((Math.random()) * 2) + 1);
            if (a == 1)
                nodo.enviarDinero(1.23, Direccion.DIRECCION_3.getDireccionIP(), Tipo.LOGICO1);
            else
                nodo.enviarDinero(3.47, Direccion.DIRECCION_1.getDireccionIP(), Tipo.LOGICO2);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
