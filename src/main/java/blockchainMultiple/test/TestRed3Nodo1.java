package blockchainMultiple.test;

import java.io.IOException;
import blockchainMultiple.conexion.Entrada;
import blockchainMultiple.nodo.*;

public class TestRed3Nodo1 {

    public static void main(String[] args) throws IOException {
        String type1 = "Type1";
        String type2 = "Type2";
        int puertoRecepcion = 12341; // A donde se va a enviar
        Nodo nodo = new Nodo(1, "26.20.111.124"); // Mi nodo
        // Poner el stake
        nodo.apostar(30, type1);
        nodo.apostar(40, type2);
        // Hilo para escuchar
        Entrada hiloEntrada = new Entrada(nodo, puertoRecepcion);
        hiloEntrada.start();
        // Buscar datos en la red
        nodo.buscarRed();
        // Espera hasta que exitan tres nodos en la red
        while (true) {
            if (nodo.comprobarCantidadMinimaDeNodos())
                break;
            System.out.print("");
        }
        // Hilo para validación PoS
        Validador hiloValidador = new Validador(nodo.getRed(), nodo);
        hiloValidador.start();
        // Generación de transacciones
        for (int i = 0; i < 700; i++) {
            int a = (int) (((Math.random()) * 2) + 1);
            if (a == 1)
                nodo.enviarDinero(1.23, "26.143.218.218", type1);
            else
                nodo.enviarDinero(3.47, "26.37.38.157", type2);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
