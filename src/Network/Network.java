package Network;

import Blockchain.Block;
import Blockchain.Blockchain;
import MessageTypes.Message;
import MessageTypes.Transaction;

import java.security.PublicKey;
import java.util.*;

/**
 * Clase Network.
 * Simula una red (Broadcast P2P).
 */
public class Network {

    /**
     * Dificultad inicial.
     */
    private final static int INIT_DIFFICULTY = 4;
    /**
     * Cambio de dificultad.
     */
    private final static int CHANGE_DIFFICULTY = 50;
    /**
     * Número de bloques del primer blockchain lógico.
     */
    public static List<Integer> NB_OF_BLOCK_OF_TYPE1_CREATED = new ArrayList<>();
    /**
     * Número de bloques del segundo blockchain lógico.
     */
    public static List<Integer> NB_OF_BLOCK_OF_TYPE2_CREATED = new ArrayList<>();
    /**
     * Lista de tiempos (lapso que se demora en encontrar el bloque previo de un blockchain lógico.)
     */
    public List<Double> ST = new ArrayList<>();
    /**
     * Lista de las probabilidades  de las trasacciones del primer blockchain lógico.
     */
    public List<Double>  PT1 = new ArrayList<>();
    /**
     * Lista de las probabilidades  de las trasacciones del segundo blockchain lógico.
     */
    public List<Double>  PT2 = new ArrayList<>();
    /**
     * Lista de número de trasacciones del primer blockchain lógico.
     */
    public List<Integer>  T1 = new ArrayList<>();
    /**
     * Lista de número de trasacciones del segundo blockchain lógico.
     */
    public List<Integer>  T2 = new ArrayList<>();
    /**
     * Lista de los nodos elegidos para la creación de bloques.
     */
    public List<Integer>  ELECTED = new ArrayList<>();
    /**
     * Intercambios de dinero del primer blockchain lógico.
     */
    public List<Double> EXCHANGE_MONEY1 = new ArrayList<>();
    /**
     * Intercambios de dinero del segundo blockchain lógico.
     */
    public List<Double> EXCHANGE_MONEY2 = new ArrayList<>();
    /**
     * Identificador del primer blockchain lógico.
     */
    public final String TYPE1;
    /**
     * Identificador del segundo blockchain lógico.
     */
    public final String TYPE2;
    /**
     * Lista de nodos en la red.
     */
    private final List<Node> network = new ArrayList<>();
    /**
     * Tabla de mapeo de NodeAddress y PublicKey para verificar firmas.
     */
    private final Map<String, PublicKey> keyTable = new HashMap<>();
    /**
     * Algoritmo de consenso.
     */
    public String mode = "POS";
    /**
     * Dificultad actual.
     */
    private int difficulty = INIT_DIFFICULTY;
    /**
     * Número de transacciones de cada blockchain lógico.
     */
    private final Map<String, Integer> nbTransParType = new HashMap<>();

    /**
     * Constructor de Network.
     * Red con dos blockchains.
     * 
     * @param type1 Identificador del primer blockchain lógico.
     * @param type2 Identificador del segundo blockchain lógico.
     */
    public Network(String type1, String type2) {
        TYPE1 = type1;
        TYPE2 = type2;
        nbTransParType.put(TYPE1, 0);
        nbTransParType.put(TYPE2, 0);
        NB_OF_BLOCK_OF_TYPE1_CREATED.add(1);
        NB_OF_BLOCK_OF_TYPE2_CREATED.add(1);
        EXCHANGE_MONEY1.add(0.);
        EXCHANGE_MONEY2.add(0.);

    }

    /**
     * Constructor de Network.
     * Red con un blockchain.
     * 
     * @param type1 Identificador del primer blockchain lógico.
     */
    public Network(String type1) {
        TYPE1 = type1;
        TYPE2 = null;
    }

    /**
     * Getter difficulty
     *
     * @return difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Getter network
     *
     * @return network.
     */
    public List<Node> getNetwork() {
        return network;
    }

    /**
     * Método que devuelve la clave pública de un nodo.
     *
     * @param address -> Dirección del nodo.
     * @return Clave pública del nodo.
     */
    public PublicKey getPkWithAddress(String address) {
        return keyTable.get(address);
    }

     /**
     * Getter nbTransParType.
     *
     * @return nbTransParType.
     */
    public Map<String, Integer> getNbTransParType() {
        return nbTransParType;
    }

    /**
     * Método para añadir a nbTransParType un nuevo 
     * número de transacción de un determinado blockchain.
     *
     * @param type Identificador del blockchain lógico.
     * @param nb Número de transacción. 
     */
    public void setNbTransParType(String type, int nb) {
        this.nbTransParType.put(type, nb);
    }

    /**
     * Método que agrega un nodo a la red y aumenta la dificultad.
     *
     * @param node Nuevo nodo.
     */
    public void addNode(Node node) {
        network.add(node);
        difficulty = network.size() / CHANGE_DIFFICULTY + INIT_DIFFICULTY;
        try {
            keyTable.put(node.getNodeAddress(), node.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que envía un mensaje a todos los nodos de la red.
     * Si el contenido del mensaje es un bloque y 
     * si el último bloque físico no es el primer bloque,
     * entonces se actualizan todas las billeteras con ese bloque.
     * 
     * @param m Mensaje.
     */
    public void broadcastMessage(Message m) {
        for (Node n : network) {
            n.receiptMessage(m);
        }
        if (m.getType() == 1) {
            //this.copyBlockchainFromFN().printBlk();
            Block block;
            try {
                block = this.copyBlockchainFromFN().getLatestBlock();
                if (!block.getNodeAddress().equals("Master"))
                    updateAllWallet(block);
                //System.out.println("--Wallet--");
                //printWallets();
            } catch (NullPointerException ignored) {

            }
        }
    }

    /**
     * Método que actualiza las billeteras de todos los nodos que participarón.
     *
     * @param b Bloque.
     */
    private void updateAllWallet(Block b) {
        double totalFee = 0;
        List<Transaction> t = b.getTransaction();
        ValidatorNode vn = null;
        if (mode.equals("POS")) {
            for (Node n : network) {
                if (n.nodeAddress.equals(b.getNodeAddress())) { //Busqueda del nodo que minó el bloque.
                    vn = ((ValidatorNode) n);
                }
            }
        }
        double amount = 0;
        for (Transaction transaction : t) {
            transaction.confirmed();
            double takenFromTrans = (transaction.getTransactionFee()) * transaction.getAmount();
            totalFee += takenFromTrans; //Cálculo de la tarifa, va aumentando por cada iteración.
            amount += transaction.getAmount(); //Cálculo del monto, va aumentando por cada iteración.
            String toAddress = transaction.getToAddress();
            updateWalletWithAddress(amount, toAddress, transaction.getTransactionID()); //Actualización de la billetera del destinarario de la transacción.
            updateWalletWithAddress(-(amount + takenFromTrans), transaction.getFromAddress(), transaction.getTransactionID()); //Actualización de la billetera del emisor de la transacción.


            Set<String> investorList;
            if (vn != null) {
                if (b.getBlockID().equals(TYPE1)) { // Se obtiene la lista de inversores del blockchain lógico.
                    investorList = vn.getInvestorList1();
                } else {
                    investorList = vn.getInvestorList2();
                }

                double otherNodeReward = takenFromTrans * ValidatorNode.INVEST_RATE;
                double thisNodeReward = takenFromTrans - otherNodeReward;
                vn.fullNodeAccount.receiptCoin(thisNodeReward, transaction.getTransactionID()); //updateWalletWithAddress?
                for (String s : investorList) {
                    updateWalletWithAddress(otherNodeReward, s, transaction.getTransactionID()); //Actualización de la billetera de los inversores.
                }
            }

        }
        if (b.getBlockID().equals(this.TYPE1)) { //Los intercambios de dinero se agregan a la lista de la red.
            //System.out.println(transaction.getTransactionID());
            this.EXCHANGE_MONEY1.add(EXCHANGE_MONEY1.get(EXCHANGE_MONEY1.size() - 1) + amount);
            this.EXCHANGE_MONEY2.add(EXCHANGE_MONEY2.get(EXCHANGE_MONEY2.size()-1));
        }
        if (b.getBlockID().equals(this.TYPE2)){
            //System.out.println(transaction.getTransactionID());
            this.EXCHANGE_MONEY2.add(EXCHANGE_MONEY2.get(EXCHANGE_MONEY2.size() - 1) + amount);
            this.EXCHANGE_MONEY1.add(EXCHANGE_MONEY1.get(EXCHANGE_MONEY1.size()-1));
        }
        //System.out.println("Validator address : " + b.getNodeAddress());
        updateWalletWithAddress(totalFee, b.getNodeAddress(), b.getBlockID()); //Actualización de la billetera del minero
    }


    /**
     * Método para actualizar la billetera del cliente con su dirección.
     *
     * @param amount Monto de la transacción.
     * @param clientAddress Dirección del beneficiario.
     * @param currency Identificador de la transacción.
     */
    public void updateWalletWithAddress(double amount, String clientAddress, String currency) {
        int i = 0;
        Node associatedLightNode = network.get(i);
        while (!associatedLightNode.getNodeAddress().equals(clientAddress)) {
            associatedLightNode = network.get(++i);
        }

        if (associatedLightNode instanceof PoS.LightNode)
            ((PoS.LightNode) associatedLightNode).receiptCoin(amount, currency);
    }

    /**
     * Método que devuelve el blockchain actual de un nodo full.
     *
     * @return Blockchain.
     */
    public Blockchain copyBlockchainFromFN() {
        for (Node node : network) {
            if (node instanceof PoS.FullNode) {
                return node.getBlockchain();
            }
        }
        throw new NullPointerException();
    }

    /**
     * Método que imprime toda la información de la red.
     */
    public void printStats(){
        System.out.println("ST="+ ST);
        System.out.println("NBT1="+NB_OF_BLOCK_OF_TYPE1_CREATED);
        System.out.println("NBT2="+NB_OF_BLOCK_OF_TYPE2_CREATED);
        System.out.println("WTT1="+copyBlockchainFromFN().WTT1);
        System.out.println("WTT2="+copyBlockchainFromFN().WTT2);
        System.out.println("PT1="+PT1);
        System.out.println("PT2="+PT2);
        System.out.println("T1="+T1);
        System.out.println("T2="+T2);
        System.out.println("ELECTED="+ELECTED);
        System.out.println("Type_1_currency_exchanged="+EXCHANGE_MONEY1);
        System.out.println("Type_2_currency_exchanged="+EXCHANGE_MONEY2);
    }


    /**
     * Método que imprime todo el estado de las billeteras.
     */
    public void printWallets() {
        for (var node : network) {
            if (node instanceof PoS.LightNode) {
                //System.out.println("Nom client : " + node.name + " \nWallet 1 : " + ((PoS.LightNode) node).getWallet(TYPE1) + "\n" + "Wallet 2 : " + ((PoS.LightNode) node).getWallet(TYPE2));
            }
        }
    }
}
