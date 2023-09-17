package tradicional.nodo.gatewayVersion;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tradicional.blockchain.BlockchainTradicional;
import tradicional.blockchain.BloqueTradicional;
import tradicional.mensajes.InfoNodo;

public class Red implements Serializable {

    /**
     * Número de bloques
     **/
    public List<Integer> NB_OF_BLOCK_CREATED = new ArrayList<>();
    /**
     * Lista de tiempos (lapso que se demora en encontrar el bloque previo)
     */
    public List<Double> searchTimes = new ArrayList<>();
    private List<Integer> nodosEscogidos1 = new ArrayList<>();
    private List<Integer> nodosEscogidos2 = new ArrayList<>();
    /**
     * Intercambios de dinero.
     */
    public List<Double> exchangeMoney1 = new ArrayList<>();
    /**
     * Tabla de mapeo de NodeAddress y PublicKey para verificar firmas.
     */
    private Map<String, PublicKey> keyTable = new HashMap<>();

    private Map<String, Integer> puertos = new HashMap<>();
    /**
     * Número de transacciones
     */
    private int nbTrans;
    private BlockchainTradicional blockchainTradicional;

    public Red() {
        this.blockchainTradicional = new BlockchainTradicional();
        nbTrans = 0;
        NB_OF_BLOCK_CREATED.add(1);
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
        stats += "\nWTT1=" + blockchainTradicional.getTiempoEntreCreacionDeBloques();
        stats += "\nType_1_currency_exchanged=" + exchangeMoney1;
        stats += "\nNodos_escogidos1=" + nodosEscogidos1;
        stats += "\nNodos_escogidos2=" + nodosEscogidos2;
        stats += "\n////////////////////////////////////////////\n";
        return stats;
    }

    public PublicKey obtenerClavePublicaPorDireccion(String direccion) {
        return keyTable.get(direccion);
    }

    public int getNbTrans() {
        return nbTrans;
    }

    public void setNbTrans(int nb) {
        this.nbTrans = nb;
    }

    public BlockchainTradicional getBlockchain() {
        return blockchainTradicional;
    }

    public void agregarBloque(BloqueTradicional bloqueTradicional) {
        blockchainTradicional.agregarBloque(bloqueTradicional);
    }

    public int obtenerCantidadDeNodos() {
        return keyTable.keySet().size();
    }

    public Map<String, Integer> getPuertos() {
        return puertos;
    }

    public List<Integer> getNodosEscogidos1() {
        return nodosEscogidos1;
    }

    public List<Integer> getNodosEscogidos2() {
        return nodosEscogidos2;
    }

}