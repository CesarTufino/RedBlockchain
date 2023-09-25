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
 * La clase RedMultipleGateway representa la información que se encuentra en todos los nodos de la red blockchain
 * multiple con gateway.
 */
public class RedMultipleGateway extends Red implements Serializable {
    /**
     * Número de bloques del primer blockchain lógico.
     **/
    public List<Integer> NB_OF_BLOCK_OF_TYPE1_CREATED = new ArrayList<>();
    /**
     * Número de bloques del segundo blockchain lógico.
     */
    public List<Integer> NB_OF_BLOCK_OF_TYPE2_CREATED = new ArrayList<>();
    /**
     * Listas de los primeros nodos escogidos en cada iteración.
     */
    private HashMap<Tipo, List<Integer>> nodosEscogidos1 = new HashMap<>();
    /**
     * Listas de los segundos nodos escogidos en cada iteración.
     */
    private HashMap<Tipo, List<Integer>> nodosEscogidos2 = new HashMap<>();
    /**
     * Número de transacciones de cada blockchain lógico.
     */
    private Map<Tipo, Integer> nbTransParType = new HashMap<>();
    /**
     * Intercambios de dinero del segundo blockchain lógico.
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

    /**
     * Agrega la información de un nodo a la red.
     * @param infoNodo información de un nodo.
     */
    @Override
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

    /**
     * Obtiene los resultados finales de una ejecución de la red blockchain.
     * @return resultados finales.
     */
    @Override
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

    /**
     * Obtiene el blockchain de la red.
     * @return blockchain de la red.
     */
    public BlockchainMultiple getBlockchain() {
        return blockchainMultiple;
    }

    /**
     * Agrega un bloque al blockchain de la red.
     * @param bloqueMultiple nuevo bloque.
     */
    public void agregarBloque(BloqueMultiple bloqueMultiple) {
        blockchainMultiple.agregarBloque(bloqueMultiple);
    }

    /**
     * Obtiene el HashMap con los primeros nodos escogidos de cada iteración.
     * @return HashMap con los primeros nodos escogidos.
     */
    public HashMap<Tipo, List<Integer>> getNodosEscogidos1() {
        return nodosEscogidos1;
    }

    /**
     * Obtiene el HashMap con los segundos nodos escogidos de cada iteración.
     * @return HashMap con los segundos nodos escogidos.
     */
    public HashMap<Tipo, List<Integer>> getNodosEscogidos2() {
        return nodosEscogidos2;
    }

    /**
     * Obtiene una lista con los totales de dinero intercambiado del segundo tipo de blockchain en cada iteración.
     * @return lista con los totales de dinero intercambiado del segundo tipo de blockchain en cada iteración.
     */
    public List<Double> getExchangeMoney2() {
        return exchangeMoney2;
    }

}
