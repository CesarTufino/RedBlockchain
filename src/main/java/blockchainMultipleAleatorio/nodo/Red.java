package blockchainMultipleAleatorio.nodo;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import blockchainMultipleAleatorio.mensajes.InfoNodo;
import blockchainMultipleAleatorio.blockchain.*;

public class Red implements Serializable {

    /**
     * Número de bloques del primer blockchain lógico.
     **/
    public List<Integer> NB_OF_BLOCK_OF_TYPE1_CREATED = new ArrayList<>();
    /**
     * Número de bloques del segundo blockchain lógico.
     */
    public List<Integer> NB_OF_BLOCK_OF_TYPE2_CREATED = new ArrayList<>();
    /**
     * Lista de tiempos (lapso que se demora en encontrar el bloque previo de un
     * blockchain lógico.)
     */
    public List<Double> searchTimes = new ArrayList<>();
    /**
     * Intercambios de dinero del primer blockchain lógico.
     */
    public List<Double> exchangeMoney1 = new ArrayList<>();
    /**
     * Intercambios de dinero del segundo blockchain lógico.
     */
    public List<Double> exchangeMoney2 = new ArrayList<>();
    /**
     * Identificador del primer blockchain lógico.
     */
    public final String type1 = "Type1";
    /**
     * Identificador del segundo blockchain lógico.
     */
    public final String type2 = "Type2";
    /**
     * Tabla de mapeo de NodeAddress y PublicKey para verificar firmas.
     */
    private Map<String, PublicKey> keyTable = new HashMap<>();
    /**
     * Número de transacciones de cada blockchain lógico.
     */
    private Map<String, Integer> nbTransParType = new HashMap<>();
    private Map<String, Double> mapStakeAmount1 = new HashMap<>();
    private Map<String, Double> mapStakeAmount2 = new HashMap<>();
    private Map<String, Long> mapStakeTime1 = new HashMap<>();
    private Map<String, Long> mapStakeTime2 = new HashMap<>();
    private Blockchain blockchain;

    public Red() {
        this.blockchain = new Blockchain();
        nbTransParType.put(type1, 0);
        nbTransParType.put(type2, 0);
        NB_OF_BLOCK_OF_TYPE1_CREATED.add(1);
        NB_OF_BLOCK_OF_TYPE2_CREATED.add(1);
    }

    public void addNode(InfoNodo infoNodo) {
        String direccion = infoNodo.getDireccion();

        mapStakeAmount1.put(direccion, infoNodo.getMontoDeApuesta1());
        mapStakeAmount2.put(direccion, infoNodo.getMontoDeApuesta2());
        mapStakeTime1.put(direccion, infoNodo.getTiempoDeApuesta1());
        mapStakeTime2.put(direccion, infoNodo.getTiempoDeApuesta2());
        try {
            keyTable.put(direccion, infoNodo.getClavePublica());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStats() {
        String stats = "";
        stats += "\n////////////////////////////////////////////";
        stats += "\nST=" + searchTimes;
        stats += "\nWTT1=" + blockchain.getWTT1();
        stats += "\nWTT2=" + blockchain.getWTT2();
        stats += "\nType_1_currency_exchanged=" + exchangeMoney1;
        stats += "\nType_2_currency_exchanged=" + exchangeMoney2;
        stats += "\n////////////////////////////////////////////\n";
        return stats;
    }

    public PublicKey obtenerClavePublicaPorDireccion(String direccion) {
        return keyTable.get(direccion);
    }

    public Map<String, Integer> getNbTransParType() {
        return nbTransParType;
    }

    public void setNbTransParType(String tipo, int nb) {
        this.nbTransParType.put(tipo, nb);
    }

    public String[] getNodosSeleccionados() {
        String[] direccionesSeleccionadas = new String[2];
        Map<String, Double> probabilidades1 = new HashMap<>();
        Map<String, Double> probabilidades2 = new HashMap<>();
        double suma1 = 0, suma2 = 0, sumaAcumulada1 = 0, sumaAcumulada2 = 0, numeroPseudoaleatorio1 = 0,
                numeroPseudoaleatorio2 = 0, probabilidadBloque;
        long semilla1 = 0, semilla2 = 0;
        Random rnd;

        for (String direccion : mapStakeAmount1.keySet()) {
            suma1 += mapStakeAmount1.get(direccion);
            semilla1 += mapStakeAmount1.get(direccion) * mapStakeTime1.get(direccion);
        }
        rnd = new Random(semilla1);
        numeroPseudoaleatorio1 = rnd.nextInt(101);
        System.out.println("Número pseudoaleatorio 1,1: " + numeroPseudoaleatorio1);

        probabilidadBloque = obtenerProbabilidadDeCreacionBloqueTipo1();
        //System.out.println("P: " + probabilidadBloque);

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

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public void agregarBloque(Bloque bloque) {
        blockchain.agregarBloque(bloque);
    }

    public Map<String, Long> getMapStakeTime1() {
        return mapStakeTime1;
    }

    public Map<String, Long> getMapStakeTime2() {
        return mapStakeTime2;
    }

    public int obtenerCantidadDeNodos() {
        return keyTable.keySet().size();
    }

    public double obtenerProbabilidadDeCreacionBloqueTipo1() {
        double cantidadTransaccionesTipo1 = nbTransParType.get(type1);
        double cantidadTransaccionesTipo2 = nbTransParType.get(type2);
        return (cantidadTransaccionesTipo1 / (cantidadTransaccionesTipo1 + cantidadTransaccionesTipo2)) * 100;
    }

}