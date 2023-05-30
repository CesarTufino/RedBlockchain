package Network;

import Blockchain.Block;
import Blockchain.Blockchain;
import MessageTypes.Message;
import MessageTypes.Transaction;
import PoS.LightNode;
import Utils.HashUtil;
import Utils.RsaUtil;

import java.util.*;

/**
 * Clase ValidatorNode
 * 
 */
public class ValidatorNode extends PoS.FullNode {

    /**
     * Cantidad máxima de las transacciones en un bloque.
     */
    public static int MAX_TRANSACTION = 10;
    /**
     * Tasa de inversión.
     */
    public static double INVEST_RATE = 0.80;
    /**
     * Nodo ligero asociado.
     */
    public final LightNode fullNodeAccount;
    /**
     * Lista de trasacciones pendientes.
     */
    private final ArrayList<Transaction> pendingTransaction = new ArrayList<>();
    /**
     * Lista de trasacciones fraudulentas.
     */
    private final ArrayList<Transaction> fraudulentTransaction = new ArrayList<>();
    /**
     * Lista de inversores del primer blockchain lógico.
     */
    private final Map<String, Double> investorList1 = new HashMap<>();
    /**
     * Lista de inversores del segundo blockchain lógico.
     */
    private final Map<String, Double> investorList2 = new HashMap<>();
    /**
     * Fecha de creación del ValidatorNode.
     */
    private final long stakeTime = System.currentTimeMillis();
    /**
     * Monto de apuesta en el primer blockchain lógico.
     */
    private double stakeAmount1 = 0;
    /**
     * Monto de apuesta en el segundo blockchain lógico.
     */
    private double stakeAmount2 = 0;

    /**
     * Constructor ValidatorNode.
     *
     * @param nom Nombre del ValidatorNode.
     * @param network Red.
     * @param fullNodeAccount Nodo ligero asociado.
     */
    public ValidatorNode(String nom, Network network, LightNode fullNodeAccount) {
        super(nom, network);
        this.fullNodeAccount = fullNodeAccount;
    }

    /**
     * Método para recibir transacciones.
     * Añade la transacción a la lista de pendientes o fraudes.
     * 
     * @param t Transacción recibida.
     */
    public void receiptTransaction(Transaction t) {
        boolean transactionStatus = false;
        try {
            transactionStatus = verifyTransaction(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (transactionStatus) {
            //System.out.println("Transaction" + t.getTransactionHash() + " receipt by " + this.name + " and accepted");
            pendingTransaction.add(t);
        } else {
            //System.out.println("Transaction" + t.getTransactionHash() + " receipt by " + this.name + " but refused (Probably fraudulent)");
            fraudulentTransaction.add(t);
        }
    }

    /**
     * Método que comprueba si la transacción no es fraudulenta mediante la firma.
     * 
     * @param t Identificador del tipo de bloque.
     * @return Si la transacción no es fraudulenta.
     * @throws Exception Excepción.
     */
    public boolean verifyTransaction(Transaction t) throws Exception {
        return RsaUtil.verify(t.toString(), t.getSignature(), network.getPkWithAddress(t.getFromAddress()));
    }

    /**
     * Método que elimina las transacciones de la lista de transacciones pendientes.
     * 
     * @param b Bloque que contiene las transacciones.
     */
    public void updateTransactionList(Block b) {
        List<Transaction> lt = b.getTransaction();
        for (Transaction t : lt) {
            pendingTransaction.remove(t);
        }
        //System.out.println("Transaction list of " + this.name + " successfully updated");
    }

    /**
     * Método que crea un bloque.
     * 
     * @param ID Identificador del blockchain lógico.
     */
    public void forgeBlock(String blockID) {
        System.out.println((blockID));
        List<Transaction> inBlockTransaction = new ArrayList<>();
        for (int i = 0; (i < MAX_TRANSACTION) && (i < pendingTransaction.size()); i++) {
            if (pendingTransaction.get(i).getTransactionID().equals(blockID)) {
                inBlockTransaction.add(pendingTransaction.get(i));
                System.out.println(pendingTransaction.get(i).getTransactionID());
                network.setNbTransParType(blockID, network.getNbTransParType().get(blockID) - 1); //-1?
            }
        }
        long start = System.nanoTime();
        Block prevBlockID = this.blockchain.searchPrevBlockByID(blockID, this.blockchain.getSize() - 1); //
        long end = System.nanoTime();
        network.ST.add((double)end-start); // IMPORTANT time
        Block forgedBlock = new Block(this.blockchain.getLatestBlock(), prevBlockID, inBlockTransaction, blockID);
        forgedBlock.setNodeID(this.nodeID);
        forgedBlock.setNodeAddress(this.nodeAddress);
        //System.out.println("Block has been forged by " + this.name);
        //System.out.println("---------------------------------------------------");
        //System.out.println("Broadcasting");
        try {
            List<Object> messageContent = new ArrayList<>();
            messageContent.add(forgedBlock);
            messageContent.add(this.blockchain);
            Message m = new Message(this.nodeAddress, "ALL", RsaUtil.sign(HashUtil.SHA256(forgedBlock.toString()), this.privateKey), System.currentTimeMillis(), 1, messageContent);
            network.broadcastMessage(m);
            if (blockID.equals(network.TYPE1)){
                Network.NB_OF_BLOCK_OF_TYPE1_CREATED.add(Network.NB_OF_BLOCK_OF_TYPE1_CREATED.get(Network.NB_OF_BLOCK_OF_TYPE1_CREATED.size()-1)+1);
            }else{
                Network.NB_OF_BLOCK_OF_TYPE2_CREATED.add(Network.NB_OF_BLOCK_OF_TYPE2_CREATED.get(Network.NB_OF_BLOCK_OF_TYPE2_CREATED.size()-1)+1);
            }
            //System.out.println("Block forged and broadcast successfully by " + this.name);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error signing");
        }
    }

    /**
     * Método que obtiene y envía la recompensa.
     * 
     * @param amount Monto.
     * @param id Identificador del blockchain lógico.
     */
    public void getAndBroadcastReward(double amount, String id) {
        double otherNodeReward = amount * INVEST_RATE;
        double thisNodeReward = amount - otherNodeReward;
        this.fullNodeAccount.receiptCoin(thisNodeReward, id);
        if (id.equals(network.TYPE1)) {
            for (String s : this.investorList1.keySet()) {
                network.updateWalletWithAddress(otherNodeReward, s, id);
            }
        } else {
            for (String s : this.investorList2.keySet()) {
                network.updateWalletWithAddress(otherNodeReward, s, id);
            }
        }
    }

    /**
     * Método para recibir bloques.
     * 
     * @param b Bloque recibido.
     * @param signature Firma.
     * @param nodeAddress Dirección del nodo.
     * @param blk Blockchain físico.
     */
    public void receiptBlock(Block b, String signature, String nodeAddress, Blockchain blk) {
        updateTransactionList(b);
        try {
            if (RsaUtil.verify(HashUtil.SHA256(b.toString()), signature, network.getPkWithAddress(nodeAddress))) {
                //System.out.println("Block accepted by " + this.name);
                this.blockchain.addBlock(b);
                //this.blockchain.printBlk();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para aumentar el total de la apuesta en el primer blockchain lógico.
     * 
     * @param stake 
     */
    public void addStake1(double stake) {
        this.stakeAmount1 += stake;
    }

    /**
     * Método para aumentar el total de la apuesta en el segundo blockchain lógico.
     * 
     * @param stake 
     */
    public void addStake2(double stake) {
        this.stakeAmount2 += stake;
    }

    /**
     * Método que añade un inversor de la lista de inversores de un determinado blockchain lógico.
     * 
     * @param investorAddress Dirección del inversor.
     * @param stakeAmount Monto de apuesto del inversor.
     * @param type Identificador del blockchain lógico.
     */
    public void addInvestorType(String investorAddress, double stakeAmount, String type) {
        if (type.equals(network.TYPE1)) {
            this.investorList1.put(investorAddress, stakeAmount);
            this.stakeAmount1 += stakeAmount;
        } else {
            this.investorList2.remove(investorAddress, stakeAmount);
            this.stakeAmount2 += stakeAmount;
        }
    }

    /**
     * Método que elimina un inversor de la lista de inversores de un determinado blockchain lógico.
     * 
     * @param investorAddress Dirección del inversor.
     * @param type Identificador del blockchain lógico.
     */
    public void delInvestor(String investorAddress, String type) {
        if (type.equals(network.TYPE1)) {
            this.stakeAmount1 -= investorList1.get(investorAddress);
            this.investorList1.remove(investorAddress);

        } else {
            this.stakeAmount2 -= investorList2.get(investorAddress);
            this.investorList2.remove(investorAddress);
        }
    }

    /**
     * Getter stakeAmount1.
     * 
     * @return stakeAmount1.
     */
    public double getStakeAmount1() {
        return this.stakeAmount1;
    }

    /**
     * Getter stakeAmount2.
     * 
     * @return stakeAmount2.
     */
    public double getStakeAmount2() {
        return this.stakeAmount2;
    }

    /**
     * Getter stakeTime
     * 
     * @return stakeTime.
     */
    public long getStakeTime() {
        return this.stakeTime;
    }

    /**
     * Método que devuelve las claves de los inversores del primer blockchain lógico.
     * 
     * @return Conjunto de claves de los inversores del primer blockchain lógico.
     */
    public Set<String> getInvestorList1() {
        return this.investorList1.keySet();
    }

    /**
     * Método que devuelve las claves de los inversores del segundo blockchain lógico.
     * 
     * @return Conjunto de claves de los inversores del segundo blockchain lógico.
     */
    public Set<String> getInvestorList2() {
        return this.investorList2.keySet();
    }
}