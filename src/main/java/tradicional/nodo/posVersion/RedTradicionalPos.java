package tradicional.nodo.posVersion;

import general.mensajes.InfoNodo;
import general.nodo.Red;
import tradicional.blockchain.BlockchainTradicional;
import tradicional.blockchain.BloqueTradicional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedTradicionalPos extends Red implements Serializable {

    /**
     * Número de bloques.
     **/
    private List<Integer> NB_OF_BLOCK_CREATED = new ArrayList<>();
    private List<Integer> nodosEscogidos = new ArrayList<>();
    /**
     * Número de transacciones.
     */
    private int nbTrans;
    private Map<String, Double> mapStakeAmount1 = new HashMap<>();
    private Map<String, Long> mapStakeTime1 = new HashMap<>();
    private BlockchainTradicional blockchainTradicional;

    public RedTradicionalPos() {
        this.blockchainTradicional = new BlockchainTradicional();
        this.nbTrans = 0;
        this.NB_OF_BLOCK_CREATED.add(1);
    }

    public int getNbTrans() {
        return nbTrans;
    }

    public void setNbTrans(int nbTrans) {
        this.nbTrans = nbTrans;
    }

    public List<Integer> getNodosEscogidos(){
        return nodosEscogidos;
    }

    public List<Integer> getNB_OF_BLOCK_CREATED() {
        return NB_OF_BLOCK_CREATED;
    }

    public BlockchainTradicional getBlockchainTradicional() {
        return blockchainTradicional;
    }

    public Map<String, Double> getMapStakeAmount1() {
        return mapStakeAmount1;
    }

    public Map<String, Long> getMapStakeTime1() {
        return mapStakeTime1;
    }

    @Override
    public String getStats() {
        String stats = "";
        stats += "\n////////////////////////////////////////////";
        stats += "\nST=" + searchTimes;
        stats += "\nWTT1=" + blockchainTradicional.getTiempoEntreCreacionDeBloques();
        stats += "\nType_1_currency_exchanged=" + exchangeMoney1;
        stats += "\nNodos_escogidos1=" + nodosEscogidos;
        stats += "\n////////////////////////////////////////////\n";
        return stats;
    }

    @Override
    public void addNode(InfoNodo infoNodo) {
        String direccion = infoNodo.getDireccion();

        mapStakeAmount1.put(direccion, infoNodo.getMontoDeApuesta1());
        mapStakeTime1.put(direccion, infoNodo.getTiempoDeApuesta1());
        try {
            keyTable.put(direccion, infoNodo.getClavePublica());
            puertos.put(direccion, infoNodo.getPuerto());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("nodos totales: " + obtenerCantidadDeNodos());
    }

    public void agregarBloque(BloqueTradicional bloqueTradicional) {
        blockchainTradicional.agregarBloque(bloqueTradicional);
    }
}
