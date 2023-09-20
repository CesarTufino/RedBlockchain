package multiple.nodo.posVersion.seleccionador;

import general.constantes.Tipo;
import multiple.blockchain.BlockchainMultiple;
import multiple.nodo.posVersion.NodoMultiplePos;
import multiple.nodo.posVersion.RedMultiplePos;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SeleccionadorProbabilidadDefinidaPos extends Thread {

    private NodoMultiplePos nodoMultiplePos;
    private final double PROBABILIDAD_BLOQUES_TIPO_1 = 95;

    public SeleccionadorProbabilidadDefinidaPos(NodoMultiplePos nodoMultiplePos) {
        this.nodoMultiplePos = nodoMultiplePos;
    }

    public void mandarACrear(String direccion, Tipo tipo) {
        if (direccion.equals(nodoMultiplePos.getDireccion().getDireccionIP())) {
            nodoMultiplePos.generarBloque(tipo);
        }
    }

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
                numeroPseudoaleatorio1, numeroPseudoaleatorio2, probabilidadBloque;
        long semilla1 = 0, semilla2 = 0;
        Random rnd;

        for (String direccion : mapStakeAmount1.keySet()) {
            suma1 += mapStakeAmount1.get(direccion);
            semilla1 += mapStakeAmount1.get(direccion) * mapStakeTime1.get(direccion);
        }
        rnd = new Random(semilla1);
        numeroPseudoaleatorio1 = rnd.nextInt(101);
        // System.out.println("Número pseudoaleatorio 1,1: " + numeroPseudoaleatorio1);

        probabilidadBloque = PROBABILIDAD_BLOQUES_TIPO_1;
        if (probabilidadBloque >= numeroPseudoaleatorio1) {
            numeroPseudoaleatorio1 = rnd.nextInt(101);
            // System.out.println("Número pseudoaleatorio 1,2: " + numeroPseudoaleatorio1);

            for (String direccion : mapStakeAmount1.keySet()) {
                probabilidades1.put(direccion, (mapStakeAmount1.get(direccion) / suma1) * 100);
            }
            for (String direccion : probabilidades1.keySet()) {
                sumaAcumulada1 += probabilidades1.get(direccion);
                if (numeroPseudoaleatorio1 <= sumaAcumulada1) {
                    direccionesSeleccionadas[0] = direccion;
                    mapStakeTime1.put(direccion, mapStakeTime1.get(direccion) + 10000);
                    break;
                }
            }
            direccionesSeleccionadas[1] = null;
            mapStakeTime2.put(direccionesSeleccionadas[0], mapStakeTime2.get(direccionesSeleccionadas[0]) + 10000);
        } else {
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
                if (numeroPseudoaleatorio2 <= sumaAcumulada2) {
                    direccionesSeleccionadas[1] = direccion;
                    mapStakeTime2.put(direccion, mapStakeTime2.get(direccion) + 10000);
                    break;
                }
            }
            direccionesSeleccionadas[0] = null;
            mapStakeTime1.put(direccionesSeleccionadas[1], mapStakeTime1.get(direccionesSeleccionadas[1]) + 10000);
        }

        System.out.println(
                "Nodos seleccionados: 1:" + direccionesSeleccionadas[0] + ", 2:" + direccionesSeleccionadas[1]);
        return direccionesSeleccionadas;
    }

    @Override
    public void run() {
        // tiempo de espera inicial
        try {
            long tiempoParaIniciar = 10000 - (System.currentTimeMillis() % 10000);
            Thread.sleep(tiempoParaIniciar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long tiempoInicio;
        long tiempoActual;
        long tiempoDelUltimoBloqueTipo1;
        long tiempoDelUltimoBloqueTipo2;
        BlockchainMultiple blockchainMultiple = nodoMultiplePos.getRed().getBlockchainMultiple();
        while (true) {
            tiempoInicio = System.currentTimeMillis();
            System.out.println("Seleccionando...");

            String[] seleccionados = seleccionar();

            if (seleccionados[0] != null) {
                mandarACrear(seleccionados[0], Tipo.LOGICO1);
            } else {
                mandarACrear(seleccionados[1], Tipo.LOGICO2);
            }

            while (true) {
                tiempoActual = System.currentTimeMillis();
                tiempoDelUltimoBloqueTipo1 = blockchainMultiple.buscarBloquePrevioLogico(
                                Tipo.LOGICO1, blockchainMultiple.obtenerCantidadDeBloques() - 1)
                        .getHeader().getMarcaDeTiempoDeCreacion();
                tiempoDelUltimoBloqueTipo2 = blockchainMultiple.buscarBloquePrevioLogico(
                                Tipo.LOGICO2, blockchainMultiple.obtenerCantidadDeBloques() - 1)
                        .getHeader().getMarcaDeTiempoDeCreacion();
                if ((tiempoActual - tiempoInicio > 10000) &&
                        (tiempoActual - tiempoDelUltimoBloqueTipo1 > 10000) &&
                        (tiempoActual - tiempoDelUltimoBloqueTipo2 > 10000)) {
                    break;
                }
            }
        }
    }

}
