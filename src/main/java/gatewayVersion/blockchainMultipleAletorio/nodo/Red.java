package gatewayVersion.blockchainMultipleAletorio.nodo;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gatewayVersion.blockchainMultipleAletorio.blockchain.Blockchain;
import gatewayVersion.blockchainMultipleAletorio.blockchain.Bloque;
import gatewayVersion.blockchainMultipleAletorio.mensajes.InfoNodo;

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
    private List<Integer> nodosEscogidos1Tipo1 = new ArrayList<>();
    private List<Integer> nodosEscogidos2Tipo1 = new ArrayList<>();
    private List<Integer> nodosEscogidos1Tipo2 = new ArrayList<>();
    private List<Integer> nodosEscogidos2Tipo2 = new ArrayList<>();
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

    private Map<String, Integer> puertos = new HashMap<>();
    /**
     * Número de transacciones de cada blockchain lógico.
     */
    private Map<String, Integer> nbTransParType = new HashMap<>();
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
        stats += "\nWTT2=" + blockchain.getWTT2();
        stats += "\nType_1_currency_exchanged=" + exchangeMoney1;
        stats += "\nType_2_currency_exchanged=" + exchangeMoney2;
        stats += "\nNodos_escogidos1Tipo1=" + nodosEscogidos1Tipo1;
        stats += "\nNodos_escogidos2Tipo1=" + nodosEscogidos2Tipo1;
        stats += "\nNodos_escogidos1Tipo2=" + nodosEscogidos1Tipo2;
        stats += "\nNodos_escogidos2Tipo2=" + nodosEscogidos2Tipo2;
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

    public List<Integer> getNodosEscogidos1Tipo1() {
        return nodosEscogidos1Tipo1;
    }

    public List<Integer> getNodosEscogidos2Tipo1() {
        return nodosEscogidos2Tipo1;
    }

    public List<Integer> getNodosEscogidos1Tipo2() {
        return nodosEscogidos1Tipo2;
    }

    public List<Integer> getNodosEscogidos2Tipo2() {
        return nodosEscogidos2Tipo2;
    }
}