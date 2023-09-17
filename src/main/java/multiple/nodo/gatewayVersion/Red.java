package multiple.nodo.gatewayVersion;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constantes.Tipo;
import multiple.blockchain.BlockchainMultiple;
import multiple.blockchain.BloqueMultiple;
import multiple.mensajes.InfoNodo;

/**
 *  Clase red
 */
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
     * Lista de tiempos (lapso que se demora en encontrar los bloques previos)
     */
    public List<Double> searchTimes = new ArrayList<>();
    private HashMap<Tipo, List<Integer>> nodosEscogidos1 = new HashMap<>();
    private HashMap<Tipo, List<Integer>> nodosEscogidos2 = new HashMap<>();
    /**
     * Intercambios de dinero del primer blockchain lógico.
     */
    public List<Double> exchangeMoney1 = new ArrayList<>();
    /**
     * Intercambios de dinero del segundo blockchain lógico.
     */
    public List<Double> exchangeMoney2 = new ArrayList<>();
    /**
     * Tabla de mapeo de NodeAddress y PublicKey para verificar firmas.
     */
    private Map<String, PublicKey> keyTable = new HashMap<>();

    private Map<String, Integer> puertos = new HashMap<>();
    /**
     * Número de transacciones de cada blockchain lógico.
     */
    private Map<Tipo, Integer> nbTransParType = new HashMap<>();
    private BlockchainMultiple blockchainMultiple;

    public Red() {
        this.blockchainMultiple = new BlockchainMultiple();
        nbTransParType.put(Tipo.LOGICO1, 0);
        nbTransParType.put(Tipo.LOGICO2, 0);
        nodosEscogidos1.put(Tipo.LOGICO1,new ArrayList<>());
        nodosEscogidos1.put(Tipo.LOGICO2,new ArrayList<>());
        nodosEscogidos2.put(Tipo.LOGICO1,new ArrayList<>());
        nodosEscogidos2.put(Tipo.LOGICO2,new ArrayList<>());
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
        stats += "\nWTT1=" + blockchainMultiple.getTiempoEntreCreacionDeBloques().get(Tipo.LOGICO1);
        stats += "\nWTT2=" + blockchainMultiple.getTiempoEntreCreacionDeBloques().get(Tipo.LOGICO2);
        stats += "\nType_1_currency_exchanged=" + exchangeMoney1;
        stats += "\nType_2_currency_exchanged=" + exchangeMoney2;
        stats += "\nNodos_escogidos1Tipo1=" + nodosEscogidos1.get(Tipo.LOGICO1);
        stats += "\nNodos_escogidos2Tipo1=" + nodosEscogidos2.get(Tipo.LOGICO1);
        stats += "\nNodos_escogidos1Tipo2=" + nodosEscogidos1.get(Tipo.LOGICO2);
        stats += "\nNodos_escogidos2Tipo2=" + nodosEscogidos2.get(Tipo.LOGICO2);
        stats += "\n////////////////////////////////////////////\n";
        return stats;
    }

    public PublicKey obtenerClavePublicaPorDireccion(String direccion) {
        return keyTable.get(direccion);
    }

    public Map<Tipo, Integer> getNbTransParType() {
        return nbTransParType;
    }

    public void setNbTransParType(Tipo tipo, int nb) {
        this.nbTransParType.put(tipo, nb);
    }

    public BlockchainMultiple getBlockchain() {
        return blockchainMultiple;
    }

    public void agregarBloque(BloqueMultiple bloqueMultiple) {
        blockchainMultiple.agregarBloque(bloqueMultiple);
    }


    public int obtenerCantidadDeNodos() {
        return keyTable.keySet().size();
    }

    public Map<String, Integer> getPuertos() {
        return puertos;
    }

    public HashMap<Tipo, List<Integer>> getNodosEscogidos1() {
        return nodosEscogidos1;
    }

    public HashMap<Tipo, List<Integer>> getNodosEscogidos2() {
        return nodosEscogidos2;
    }

}