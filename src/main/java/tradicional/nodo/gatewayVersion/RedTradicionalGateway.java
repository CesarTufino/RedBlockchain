package tradicional.nodo.gatewayVersion;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import general.nodo.Red;
import tradicional.blockchain.BlockchainTradicional;
import tradicional.blockchain.BloqueTradicional;
import general.mensajes.InfoNodo;

public class RedTradicionalGateway extends Red implements Serializable {

    /**
     * Número de bloques
     **/
    private List<Integer> NB_OF_BLOCK_CREATED = new ArrayList<>();
    private List<Integer> nodosEscogidos1 = new ArrayList<>();
    private List<Integer> nodosEscogidos2 = new ArrayList<>();
    /**
     * Número de transacciones
     */
    private int nbTrans;
    private BlockchainTradicional blockchainTradicional;

    public RedTradicionalGateway() {
        super();
        this.blockchainTradicional = new BlockchainTradicional();
        this.nbTrans = 0;
        this.NB_OF_BLOCK_CREATED.add(1);
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

    public BlockchainTradicional getBlockchain() {
        return blockchainTradicional;
    }

    public void agregarBloque(BloqueTradicional bloqueTradicional) {
        blockchainTradicional.agregarBloque(bloqueTradicional);
    }

    public List<Integer> getNodosEscogidos1() {
        return nodosEscogidos1;
    }

    public List<Integer> getNodosEscogidos2() {
        return nodosEscogidos2;
    }

    public List<Integer> getNB_OF_BLOCK_CREATED() {
        return NB_OF_BLOCK_CREATED;
    }
}