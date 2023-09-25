package tradicional.nodo.posVersion.seleccionador;

import tradicional.blockchain.BlockchainTradicional;
import tradicional.nodo.posVersion.NodoTradicionalPos;
import tradicional.nodo.posVersion.RedTradicionalPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * La clase SeleccionadorAleatorioGateway se encarga de realizar las iteraciones de selección de un nodo, mediante un
 * algoritmo POS, para la creación de un bloque cada 10 segundos.
 */
public class SeleccionadorTradicionalPos extends Thread {
    private NodoTradicionalPos nodoTradicionalPos;

    public SeleccionadorTradicionalPos(NodoTradicionalPos nodoTradicionalPos) {
        this.nodoTradicionalPos = nodoTradicionalPos;
    }

    /**
     * Llama al método para que el nodo cree un bloque si la dirección de este nodo coincide con la dirección dada.
     * @param direccion dirección del nodo que debe crear el bloque.
     */
    public void mandarACrear(String direccion) {
        if (direccion.equals(nodoTradicionalPos.getDireccion().getDireccionIP())) {
            nodoTradicionalPos.generarBloque();
        }
    }

    /**
     * Selecciona un nodo para que cree un bloque.
     * @return nodo seleccionado.
     */
    private String seleccionar() {
        String direccionesSeleccionada = "";
        RedTradicionalPos red = nodoTradicionalPos.getRed();
        Map<String, Double> mapStakeAmount1 = red.getMapStakeAmount1();
        Map<String, Long> mapStakeTime1 = red.getMapStakeTime1();
        Map<String, Double> probabilidades1 = new HashMap<>();
        double suma1 = 0, sumaAcumulada1 = 0, numeroPseudoaleatorio1 = 0;
        long semilla1 = 0;
        Random rnd;

        for (String direccion : mapStakeAmount1.keySet()) {
            suma1 += mapStakeAmount1.get(direccion);
            semilla1 += mapStakeAmount1.get(direccion) * mapStakeTime1.get(direccion);
        }
        rnd = new Random(semilla1);
        numeroPseudoaleatorio1 = rnd.nextInt(101);
        // System.out.println("Número pseudoaleatorio 1: " + numeroPseudoaleatorio1);
        for (String direccion : mapStakeAmount1.keySet()) {
            probabilidades1.put(direccion, (mapStakeAmount1.get(direccion) / suma1) * 100);
        }
        for (String direccion : probabilidades1.keySet()) {
            sumaAcumulada1 += probabilidades1.get(direccion);
            if (numeroPseudoaleatorio1 <= Math.round(sumaAcumulada1)) {
                direccionesSeleccionada = direccion;
                mapStakeTime1.put(direccion, mapStakeTime1.get(direccion) + 10000);
                break;
            }
        }

        System.out.println(
                "Nodo seleccionado: 1:" + direccionesSeleccionada);
        return direccionesSeleccionada;
    }

    /**
     * Calcula el tiempo que falta para que hayan transcurrido 10 segundos reloj.
     * @return tiempo restante para que hayan transcurrido 10 segundos reloj.
     */
    private long calcularTiempoParaIniciar() {
        return 10000 - (System.currentTimeMillis() % 10000);
    }

    /**
     * Realiza una iteración, donde se escoge al azar un nodo para el nuevo bloque.
     */
    private void iniciarIteracion() {
        System.out.println("Seleccionando...");
        String seleccionado = seleccionar();
        mandarACrear(seleccionado);
    }

    /**
     * Llama cada 10 segundos, mediante un bucle, al método que realiza la iteración de creación de bloque.
     */
    @Override
    public void run() {
        try {
            while (true) {
                long tiempoParaIniciar = calcularTiempoParaIniciar();
                Thread.sleep(tiempoParaIniciar);
                iniciarIteracion();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
