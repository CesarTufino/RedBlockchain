package multiple.nodo.posVersion;

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

public class RedMultiplePos extends Red implements Serializable {

    /**
     * Número de bloques del primer general.blockchain lógico.
     **/
    public List<Integer> NB_OF_BLOCK_OF_TYPE1_CREATED = new ArrayList<>();
    /**
     * Número de bloques del segundo general.blockchain lógico.
     */
    public List<Integer> NB_OF_BLOCK_OF_TYPE2_CREATED = new ArrayList<>();
    private HashMap<Tipo, List<Integer>> nodosEscogidos = new HashMap<>();
    /**
     * Número de transacciones de cada general.blockchain lógico.
     */
    private Map<Tipo, Integer> nbTransParType = new HashMap<>();
    /**
     * Intercambios de dinero del segundo general.blockchain lógico.
     */
    private List<Double> exchangeMoney2 = new ArrayList<>();
    private Map<String, Double> mapStakeAmount1 = new HashMap<>();
    private Map<String, Double> mapStakeAmount2 = new HashMap<>();
    private Map<String, Long> mapStakeTime1 = new HashMap<>();
    private Map<String, Long> mapStakeTime2 = new HashMap<>();
    private BlockchainMultiple blockchainMultiple;

    public RedMultiplePos() {
        this.blockchainMultiple = new BlockchainMultiple();
        this.nbTransParType.put(Tipo.LOGICO1, 0);
        this.nbTransParType.put(Tipo.LOGICO2, 0);
        this.nodosEscogidos.put(Tipo.LOGICO1,new ArrayList<>());
        this.nodosEscogidos.put(Tipo.LOGICO2,new ArrayList<>());
        this.NB_OF_BLOCK_OF_TYPE1_CREATED.add(1);
        this.NB_OF_BLOCK_OF_TYPE2_CREATED.add(1);
    }

    public void addNode(InfoNodo infoNodo) {
        String direccion = infoNodo.getDireccion();
        mapStakeAmount1.put(direccion, infoNodo.getMontoDeApuesta1());
        mapStakeAmount2.put(direccion, infoNodo.getMontoDeApuesta2());
        mapStakeTime1.put(direccion, infoNodo.getTiempoDeApuesta1());
        mapStakeTime2.put(direccion, infoNodo.getTiempoDeApuesta2());
        try {
            keyTable.put(direccion, infoNodo.getClavePublica());
            puertos.put(direccion, infoNodo.getPuerto());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStats() {
        String stats = "";
        stats += "\n////////////////////////////////////////////";
        stats += "\nST=" + searchTimes;
        stats += "\nWTT1=" + blockchainMultiple.getTiempoEntreCreacionDeBloques().get(Tipo.LOGICO1);
        stats += "\nWTT2=" + blockchainMultiple.getTiempoEntreCreacionDeBloques().get(Tipo.LOGICO2);
        stats += "\nType_1_currency_exchanged=" + exchangeMoney1;
        stats += "\nType_2_currency_exchanged=" + exchangeMoney2;
        stats += "\nNodos_escogidos1Tipo1=" + nodosEscogidos.get(Tipo.LOGICO1);
        stats += "\nNodos_escogidos1Tipo2=" + nodosEscogidos.get(Tipo.LOGICO2);
        stats += "\n////////////////////////////////////////////\n";
        return stats;
    }

    public Map<Tipo, Integer> getNbTransParType() {
        return nbTransParType;
    }

    public BlockchainMultiple getBlockchainMultiple() {
        return blockchainMultiple;
    }

    public void agregarBloque(BloqueMultiple bloque) {
        blockchainMultiple.agregarBloque(bloque);
    }

    public HashMap<Tipo, List<Integer>> getNodosEscogidos() {
        return nodosEscogidos;
    }

    public Map<String, Double> getMapStakeAmount1() {
        return mapStakeAmount1;
    }

    public Map<String, Double> getMapStakeAmount2() {
        return mapStakeAmount2;
    }

    public Map<String, Long> getMapStakeTime1() {
        return mapStakeTime1;
    }

    public Map<String, Long> getMapStakeTime2() {
        return mapStakeTime2;
    }

    public List<Double> getExchangeMoney2() {
        return exchangeMoney2;
    }

    public double obtenerProporcionBloquesTipo1() {
        double cantidadTransaccionesTipo1 = nbTransParType.get(Tipo.LOGICO1);
        double cantidadTransaccionesTipo2 = nbTransParType.get(Tipo.LOGICO2);
        return (cantidadTransaccionesTipo1 / (cantidadTransaccionesTipo1 + cantidadTransaccionesTipo2)) * 100;
    }

}