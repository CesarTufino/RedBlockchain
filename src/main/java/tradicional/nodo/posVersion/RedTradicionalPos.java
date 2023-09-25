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

/**
 * La clase RedTradicionalGateway representa la información que se encuentra en todos los nodos de la red blockchain
 * tradicional con algoritmo POS.
 */
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

    /**
     * Obtiene el blockchain de la red.
     * @return blockchain de la red.
     */
    public BlockchainTradicional getBlockchainTradicional() {
        return blockchainTradicional;
    }

    /**
     * Obtiene el número de transacciones pendientes.
     * @return número de transacciones pendientes.
     */
    public int getNbTrans() {
        return nbTrans;
    }

    /**
     * Establece el número de transacciones pendientes.
     * @param nbTrans número de transacciones pendientes.
     */
    public void setNbTrans(int nbTrans) {
        this.nbTrans = nbTrans;
    }

    /**
     * Obtiene la lista con los nodos escogidos de cada iteración.
     * @return lista con los nodos escogidos.
     */
    public List<Integer> getNodosEscogidos(){
        return nodosEscogidos;
    }

    /**
     * Obtiene el número de bloques creados en el blockchain.
     * @return número de bloques creados en el blockchain.
     */
    public List<Integer> getNB_OF_BLOCK_CREATED() {
        return NB_OF_BLOCK_CREATED;
    }

    /**
     *
     * Obtiene el mapeo de las direcciones con las apuestas.
     * @return mapeo de las direcciones con las apuestas.
     */
    public Map<String, Double> getMapStakeAmount1() {
        return mapStakeAmount1;
    }

    /**
     * Obtiene el mapeo de las direcciones con los tiempos de apuestas.
     * @return mapeo de las direcciones con los tiempos de apuestas.
     */
    public Map<String, Long> getMapStakeTime1() {
        return mapStakeTime1;
    }

    /**
     * Obtiene los resultados finales de una ejecución de la red blockchain.
     * @return resultados finales.
     */
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

    /**
     * Agrega la información de un nodo a la red.
     * @param infoNodo información de un nodo.
     */
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

    /**
     * Agrega un bloque al blockchain de la red.
     * @param bloqueTradicional nuevo bloque.
     */
    public void agregarBloque(BloqueTradicional bloqueTradicional) {
        blockchainTradicional.agregarBloque(bloqueTradicional);
    }

}
