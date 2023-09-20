package tradicional.nodo.posVersion.seleccionador;

import tradicional.blockchain.BlockchainTradicional;
import tradicional.nodo.posVersion.NodoTradicionalPos;
import tradicional.nodo.posVersion.RedTradicionalPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SeleccionadorTradicionalPos extends Thread {

    private NodoTradicionalPos nodoTradicionalPos;
    private final double PROBABILIDAD_BLOQUES_TIPO_1 = 95;

    public SeleccionadorTradicionalPos(NodoTradicionalPos nodoTradicionalPos) {
        this.nodoTradicionalPos = nodoTradicionalPos;
    }

    public void mandarACrear(String direccion) {
        if (direccion.equals(nodoTradicionalPos.getDireccion().getDireccionIP())) {
            nodoTradicionalPos.generarBloque();
        }
    }

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

    private long calcularTiempoParaIniciar() {
        return 10000 - (System.currentTimeMillis() % 10000);
    }

    private void iniciarIteracion() {
        System.out.println("Seleccionando...");
        String seleccionado = seleccionar();
        mandarACrear(seleccionado);
    }

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
