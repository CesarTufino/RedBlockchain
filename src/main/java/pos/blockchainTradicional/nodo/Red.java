package pos.blockchainTradicional.nodo;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pos.blockchainTradicional.blockchain.Blockchain;
import pos.blockchainTradicional.blockchain.Bloque;
import pos.blockchainTradicional.mensajes.InfoNodo;

public class Red implements Serializable {

    /**
     * Número de bloques del primer blockchain lógico.
     **/
    public List<Integer> NB_OF_BLOCK_OF_TYPE1_CREATED = new ArrayList<>();
    /**
     * Lista de tiempos (lapso que se demora en encontrar el bloque previo de un
     * blockchain lógico.)
     */
    public List<Double> searchTimes = new ArrayList<>();
    private List<String> nodos = new ArrayList<>();
    /**
     * Intercambios de dinero del primer blockchain lógico.
     */
    public List<Double> exchangeMoney1 = new ArrayList<>();
    /**
     * Identificador del primer blockchain lógico.
     */
    public final String type1 = "Type1";
    /**
     * Tabla de mapeo de NodeAddress y PublicKey para verificar firmas.
     */
    private Map<String, PublicKey> keyTable = new HashMap<>();
    /**
     * Número de transacciones de cada blockchain lógico.
     */
    private Map<String, Integer> nbTrans = new HashMap<>();
    private Map<String, Double> mapStakeAmount1 = new HashMap<>();
    private Map<String, Long> mapStakeTime1 = new HashMap<>();
    private Blockchain blockchain;

    public Red() {
        this.blockchain = new Blockchain();
        nbTrans.put(type1, 0);
        NB_OF_BLOCK_OF_TYPE1_CREATED.add(1);
    }

    public void addNode(InfoNodo infoNodo) {
        String direccion = infoNodo.getDireccion();

        mapStakeAmount1.put(direccion, infoNodo.getMontoDeApuesta());
        mapStakeTime1.put(direccion, infoNodo.getTiempoDeApuesta());
        try {
            keyTable.put(direccion, infoNodo.getClavePublica());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("nodos totales: " + obtenerCantidadDeNodos());
    }

    public String getStats() {
        String stats = "";
        stats += "\n////////////////////////////////////////////";
        stats += "\nST=" + searchTimes;
        stats += "\nWTT1=" + blockchain.getWTT1();
        stats += "\nType_1_currency_exchanged=" + exchangeMoney1;
        stats += "\nNodos=" + nodos;
        stats += "\n////////////////////////////////////////////\n";
        return stats;
    }

    public PublicKey obtenerClavePublicaPorDireccion(String direccion) {
        return keyTable.get(direccion);
    }

    public Map<String, Integer> getNbTrans() {
        return nbTrans;
    }

    public void setNbTrans(int nb) {
        this.nbTrans.put(type1, nb);
    }

    public String getNodosSeleccionados() {
        String direccionesSeleccionada = "";
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
            if (numeroPseudoaleatorio1 <= sumaAcumulada1) {
                direccionesSeleccionada = direccion;
                mapStakeTime1.put(direccion, mapStakeTime1.get(direccion) + 10000);
                break;
            }
        }

        System.out.println(
                "Nodo seleccionado: 1:" + direccionesSeleccionada);
        return direccionesSeleccionada;
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public void agregarBloque(Bloque bloque) {
        blockchain.agregarBloque(bloque);
    }

    public int obtenerCantidadDeNodos() {
        return keyTable.keySet().size();
    }

    public List<String> getNodos(){
        return nodos;
    }
}