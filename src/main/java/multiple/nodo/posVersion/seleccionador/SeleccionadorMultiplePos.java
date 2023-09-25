package multiple.nodo.posVersion.seleccionador;

import general.constantes.Tipo;
import multiple.blockchain.BlockchainMultiple;
import multiple.nodo.posVersion.NodoMultiplePos;
import multiple.nodo.posVersion.RedMultiplePos;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * La clase SeleccionadorAleatorioGateway se encarga de realizar las iteraciones de selección de dos nodos, mediante un
 * algoritmo POS, para la creación de un bloque de cada tipo cada 10 segundos.
 */
public class SeleccionadorMultiplePos extends Thread {
    private NodoMultiplePos nodoMultiplePos;

    public SeleccionadorMultiplePos(NodoMultiplePos nodoMultiplePos) {
        this.nodoMultiplePos = nodoMultiplePos;
    }

    /**
     * Llama al método para que el nodo cree un bloque del tipo especificado si la dirección de este nodo coincide con
     * la dirección dada.
     * @param direccion dirección del nodo que debe crear el bloque.
     * @param tipo tipo de bloque que se va a crear.
     */
    public void mandarACrear(String direccion, Tipo tipo) {
        if (direccion.equals(nodoMultiplePos.getDireccion().getDireccionIP())) {
            nodoMultiplePos.generarBloque(tipo);
        }
    }

    /**
     * Selecciona dos nodo para que creen un bloque de cada tipo.
     * @return un arreglo con los nodos seleccionados.
     */
    private String[] seleccionar() {
        RedMultiplePos red = nodoMultiplePos.getRed();
        String[] direccionesSeleccionadas = new String[2];
        Map<String, Double> mapStakeAmount1 = red.getMapStakeAmount1();
        Map<String, Double> mapStakeAmount2 = red.getMapStakeAmount2();
        Map<String, Long> mapStakeTime1 = red.getMapStakeTime1();
        Map<String, Long> mapStakeTime2 = red.getMapStakeTime2();
        Map<String, Double> probabilidades1 = new HashMap<>();
        Map<String, Double> probabilidades2 = new HashMap<>();
        double suma1 = 0, suma2 = 0, sumaAcumulada1 = 0, sumaAcumulada2 = 0,
                numeroPseudoaleatorio1, numeroPseudoaleatorio2;
        long semilla1 = 0, semilla2 = 0;
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
                direccionesSeleccionadas[0] = direccion;
                mapStakeTime1.put(direccion, mapStakeTime1.get(direccion) + 10000);
                break;
            }
        }

        for (String direccion : mapStakeAmount2.keySet()) {
            if (!direccion.equals(direccionesSeleccionadas[0])) {
                suma2 += mapStakeAmount2.get(direccion);
                semilla2 += mapStakeAmount2.get(direccion) * mapStakeTime2.get(direccion);
            }
        }
        rnd = new Random(semilla2);
        numeroPseudoaleatorio2 = rnd.nextInt(101);
        // System.out.println("Número pseudoaleatorio 2: " + numeroPseudoaleatorio2);

        for (String direccion : mapStakeAmount2.keySet()) {
            if (!direccion.equals(direccionesSeleccionadas[0])) {
                probabilidades2.put(direccion, (mapStakeAmount2.get(direccion) / suma2) * 100);
            }
        }
        for (String direccion : probabilidades2.keySet()) {
            sumaAcumulada2 += probabilidades2.get(direccion);
            if (numeroPseudoaleatorio2 <= Math.round(sumaAcumulada2)) {
                direccionesSeleccionadas[1] = direccion;
                mapStakeTime2.put(direccion, mapStakeTime2.get(direccion) + 10000);
                break;
            }
        }
        System.out.println(
                "Nodos seleccionados: 1:" + direccionesSeleccionadas[0] + ", 2:" + direccionesSeleccionadas[1]);
        return direccionesSeleccionadas;
    }

    /**
     * Calcula el tiempo que falta para que hayan transcurrido 10 segundos reloj.
     * @return tiempo restante para que hayan transcurrido 10 segundos reloj.
     */
    private long calcularTiempoParaIniciar() {
        return 10000 - (System.currentTimeMillis() % 10000);
    }

    /**
     * Realiza una iteración, donde se escoge al azar dos nodos para los dos nuevos bloques.
     */
    private void iniciarIteracion() {
        System.out.println("Seleccionando...");

        String[] seleccionados = seleccionar();
        mandarACrear(seleccionados[0], Tipo.LOGICO1);

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mandarACrear(seleccionados[1], Tipo.LOGICO2);
    }

    /**
     * Llama cada 10 segundos, mediante un bucle, al método que realiza la iteración de creación de bloques.
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
