package PoS;

import Blockchain.Block;
import Blockchain.Blockchain;
import Blockchain.LightBlockChain;
import MessageTypes.Message;
import MessageTypes.Transaction;
import Network.Network;
import Network.Node;
import Network.ValidatorNode;
import Utils.RsaUtil;

import java.util.HashMap;

/**
 * Clase LightNode.
 */
public class LightNode extends Node {

    /**
     * Tarifa de transacción que se aplica cuando un LightNode envía dinero a otro LightNode
     */
    private final static double TRANSACTION_FEE = 0.1;
    /**
     * Valor inicial en la wallet.
     */
    private final static int INIT_WALLET = 100000000;
    /**
     * Wallet para el primer blockchain lógico.
     */
    private double wallet1;
    /**
     * Wallet para el segundo blockchain lógico.
     */
    private double wallet2;
    /**
     * Monto de apuesta para el primer blockchain lógico.
     */
    private double stakeAmount1;
    /**
     * Monto de apuesta para el segundo blockchain lógico.
     */
    private double stakeAmount2;
    /**
     * Fecha de la última apuesta.
     */
    private double stakeTime;
    /**
     * Ultimo bloque recibido por el LightNode
     */
    private Block lastBlock;
    /**
     * ValidatorNode asociado.
     */
    private ValidatorNode validator = null;

    /**
     * Constructor LightNode
     *
     * @param name Nombre del LightNode
     * @param network Red a la que pertenece.
     */
    public LightNode(String name, Network network) {
        super(name, network);
        this.blockchain = new LightBlockChain(network);
        this.wallet1 = INIT_WALLET;
        this.wallet2 = INIT_WALLET;
        this.stakeAmount1 = 0;
        this.stakeAmount2 = 0;
        this.stakeTime = System.currentTimeMillis();
    }

    /**
     * Getter wallet.
     * 
     * @param ID Identificador del blockchain lógico.
     * @return wallet
     */
    public double getWallet(String type) {
        if (type.equals(network.TYPE1)) {
            return wallet1;
        } else {
            return wallet2;
        }
    }

    /**
     * Getter stakeAmount1.
     *
     * @return stakeAmount1.
     */
    public double getStakeAmount1() {
        return stakeAmount1;
    }

    /**
     * Getter stakeAmount2.
     *
     * @return stakeAmount2.
     */
    public double getStakeAmount2() {
        return stakeAmount2;
    }

    /**
     * Getter stakeTime.
     *
     * @return stakeTime.
     */
    public double getStakeTime() {
        return stakeTime;
    }

    /**
     * Getter lastBlock.
     *
     * @return lastBlock.
     */
    public Block getLastBlock() {
        return lastBlock;
    }

    /**
     * Getter validator.
     *
     * @return validator.
     */
    public ValidatorNode getValidator() {
        return validator;
    }

    /**
     * Setter validator.
     *
     * @param validator ValidatorNode asociado.
     */
    public void setValidator(ValidatorNode validator) {
        this.validator = validator;
    }

    /**
     * Método para enviar un monto a un nodo utilizando su dirección.
     *
     * @param amount Monto a ser enviado.
     * @param nodeAddress Dirección del nodo destinatario.
     * @param transactionType Identificador del blockchain lógico.
     */
    public void sendMoneyTo(double amount, String nodeAddress, String transactionType) {
        if (transactionType.equals(network.TYPE1)) {
            if (wallet1 - amount * (1 + TRANSACTION_FEE) < 0) {
                System.out.println(name + " Not enough currency of type " + transactionType + " to send"); // Whatever the currency
                System.out.println("Rejected transaction");
                return;
            }
        } else {
            if (wallet2 - amount * (1 + TRANSACTION_FEE) < 0) {
                System.out.println(name + " Not enough currency of type " + transactionType + " to send"); // Whatever the currency
                System.out.println("Rejected transaction");
                return;
            }
        }
        Transaction toSend = new Transaction(transactionType, this.getNodeAddress(), nodeAddress, amount, System.currentTimeMillis(), TRANSACTION_FEE, privateKey);
        Message m = null;
        try {
            m = new Message(this.nodeAddress, nodeAddress, RsaUtil.sign(toSend.toString(), privateKey), System.currentTimeMillis(), 0, toSend);
        } catch (Exception e) {
            e.printStackTrace();
        }
        network.broadcastMessage(m);

        //compter le nb de transaction par son type
        //contar el número de transacciones por tipo?
        HashMap<String, Integer> nbTransParType = (HashMap<String, Integer>) network.getNbTransParType();
        network.setNbTransParType(transactionType, nbTransParType.get(transactionType) + 1);
        //System.out.println(this.name + " Broadcasted a transaction");
    }


    /**
     * Método que aumenta o reduce el saldo de la wallet.
     *
     * @param amount Monto.
     * @param type Identificador del blockchain lógico.
     */
    public void receiptCoin(double amount, String type) {
        String order = amount < 0 ? " Lost " : " received "; // if amount < 0 than order = Lost else Received
        if (type.equals(network.TYPE1)) {
            wallet1 += amount;
        } else {
            wallet2 += amount;
        }
        //System.out.println(this.name + order + amount + " currency of type : " + type);
    }

    /**
     * Método para recibir un bloque.
     *
     * @param b Bloque recibido.
     * @param signature Firma.
     * @param nodeAddress Dirección del nodo.
     * @param blk Blockchain donde se va a añadir el bloque.
     */
    @Override
    public void receiptBlock(Block b, String signature, String nodeAddress, Blockchain blk) {
        lastBlock = b;
        ((LightBlockChain) this.blockchain).addLightHeader(b.getHeader(), b.getFooter());
    }

    /**
     * Método para realizar una apuesta.
     *
     * @param amount Monsto apostado.
     * @param type Identificador del blockchain lógico.
     */
    public void stake(int amount, String type) {
        if (type.equals(network.TYPE1)) {
            if (wallet1 < amount) {
                System.out.println(name + " don't have enough money for stake in wallet1");
            }
            stakeAmount1 = amount;
            this.wallet1 -= amount;
        } else {
            if (wallet1 < amount) {
                System.out.println(name + " don't have enough money for stake in wallet1");
            }
            stakeAmount2 = amount;
            this.wallet2 -= amount;
        }
        stakeTime = System.currentTimeMillis();
        //System.out.println(name + " deposit " + amount + " as stake");
    }
}
