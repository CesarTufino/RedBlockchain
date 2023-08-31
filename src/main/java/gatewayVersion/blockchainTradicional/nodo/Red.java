package gatewayVersion.blockchainTradicional.nodo;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gatewayVersion.blockchainTradicional.blockchain.Blockchain;
import gatewayVersion.blockchainTradicional.blockchain.Bloque;
import gatewayVersion.blockchainTradicional.mensajes.InfoNodo;

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

    private Map<String, Integer> puertos = new HashMap<>();
    /**
     * Número de transacciones de cada blockchain lógico.
     */
    private Map<String, Integer> nbTrans = new HashMap<>();
    private Blockchain blockchain;

    public Red() {
        this.blockchain = new Blockchain();
        nbTrans.put(type1, 0);
        NB_OF_BLOCK_OF_TYPE1_CREATED.add(1);
    }

    public void addNode(InfoNodo infoNodo) {
        String direccion = infoNodo.getDireccion();

        try {
            keyTable.put(direccion, infoNodo.getClavePublica());
            puertos.put(direccion, infoNodo.getPuerto());
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

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public void agregarBloque(Bloque bloque) {
        blockchain.agregarBloque(bloque);
    }


    public int obtenerCantidadDeNodos() {
        return keyTable.keySet().size();
    }

    public Map<String, Integer> getPuertos() {
        return puertos;
    }
}