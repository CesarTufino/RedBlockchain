package multiple.nodo.gatewayVersion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.constantes.Tipo;
import multiple.blockchain.BlockchainMultiple;
import multiple.blockchain.BloqueMultiple;
import general.mensajes.InfoNodo;
import general.nodo.Red;

/**
 *  Clase red
 */
public class RedMultipleGateway extends Red implements Serializable {

    /**
     * Número de bloques del primer general.blockchain lógico.
     **/
    public List<Integer> NB_OF_BLOCK_OF_TYPE1_CREATED = new ArrayList<>();
    /**
     * Número de bloques del segundo general.blockchain lógico.
     */
    public List<Integer> NB_OF_BLOCK_OF_TYPE2_CREATED = new ArrayList<>();
    private HashMap<Tipo, List<Integer>> nodosEscogidos1 = new HashMap<>();
    private HashMap<Tipo, List<Integer>> nodosEscogidos2 = new HashMap<>();
    /**
     * Número de transacciones de cada general.blockchain lógico.
     */
    private Map<Tipo, Integer> nbTransParType = new HashMap<>();
    /**
     * Intercambios de dinero del segundo general.blockchain lógico.
     */
    private List<Double> exchangeMoney2 = new ArrayList<>();
    private BlockchainMultiple blockchainMultiple;

    public RedMultipleGateway() {
        this.blockchainMultiple = new BlockchainMultiple();
        this.nbTransParType.put(Tipo.LOGICO1, 0);
        this.nbTransParType.put(Tipo.LOGICO2, 0);
        this.nodosEscogidos1.put(Tipo.LOGICO1,new ArrayList<>());
        this.nodosEscogidos1.put(Tipo.LOGICO2,new ArrayList<>());
        this.nodosEscogidos2.put(Tipo.LOGICO1,new ArrayList<>());
        this.nodosEscogidos2.put(Tipo.LOGICO2,new ArrayList<>());
        this.NB_OF_BLOCK_OF_TYPE1_CREATED.add(1);
        this.NB_OF_BLOCK_OF_TYPE2_CREATED.add(1);
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

    public BlockchainMultiple getBlockchain() {
        return blockchainMultiple;
    }

    public void agregarBloque(BloqueMultiple bloqueMultiple) {
        blockchainMultiple.agregarBloque(bloqueMultiple);
    }

    public HashMap<Tipo, List<Integer>> getNodosEscogidos1() {
        return nodosEscogidos1;
    }

    public HashMap<Tipo, List<Integer>> getNodosEscogidos2() {
        return nodosEscogidos2;
    }

    public List<Double> getExchangeMoney2() {
        return exchangeMoney2;
    }

}