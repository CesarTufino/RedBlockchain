package Network;

import Blockchain.Block;
import Blockchain.Blockchain;
import MessageTypes.Message;
import MessageTypes.Transaction;
import Utils.HashUtil;
import Utils.RsaUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

/**
 * Clase Node
 * Representa cada usuario en la red, incluye nodos de minería, nodos comerciales, nodos de servidor, etc.
 */
public abstract class Node {

    /**
     * Utilizado para la sincronización (hilos).
     */
    private static final Object o = new Object();
    /**
     * Número de nodos totales.
     */
    private static int cpt = 0;
    /**
     * Identificador del nodo.
     */
    protected final int nodeID;
    /**
     * Nombre del nodo.
     */
    public String name;
    /**
     * Representación de la red.
     */
    protected Network network;
    /**
     * Blockchain del nodo. (Puede ser LightBlockChain si el nodo es un LightNode)
     */
    protected Blockchain blockchain;
    /**
     * Clave publica del nodo. (Identificador del segundo nodo, utilizado para verificar las firmas)
     */
    protected PublicKey publicKey;
    /**
     * Clave privada del nodo. (Actúa como su contraseña, necesaria para firmar la transacción)
     */
    protected PrivateKey privateKey;
    /**
     * Par de claves.
     */
    protected KeyPair keys;
    /**
     * Dirección del nodo.
     */
    protected String nodeAddress;

    /**
     * Constructor Node.
     * 
     * @param name Nombre del nodo
     * @param network Red a la que pertenece el nodo.
     */
    public Node(String name, Network network) {
        synchronized (o) {
            this.nodeID = cpt++;
        }
        this.network = network;
        blockchain = new Blockchain(this.network);
        this.name = name;
        try {
            keys = RsaUtil.generateKeyPair();
            publicKey = keys.getPublic();
            privateKey = keys.getPrivate();
            nodeAddress = HashUtil.SHA256(String.valueOf(publicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        network.addNode(this);
    }

    /**
     * Método para recibir bloques cuando se descubre un nuevo bloque.
     *
     * @param b El nuevo bloque.
     * @param signature Firma del minero que encontró el nuevo bloque.
     * @param nodeAddress Dirección del minero.
     * @param blk La blockchain que ha atrapado este minero.
     */
    public abstract void receiptBlock(Block b, String signature, String nodeAddress, Blockchain blk);

    //public abstract void receiptMessage(Message m);

    /**
     * Método para recibir mensajes.
     * 
     * @param m Mensaje.
     */
    public void receiptMessage(Message m) {
        // If message is a transaction
        int messageType = m.getType();
        List<Object> listOfContent = m.getMessageContent();
        if (messageType == 0) { // Si es transacción se ejecuta el método para recibir transacciones.
            //System.out.println(listOfContent.get(0));
            Transaction tr = (Transaction) (listOfContent.get(0));
            receiptTransaction(tr);
        }
        // If message is a block
        if (messageType == 1) { // Si es bloque se ejecuta el método para recibir bloques.
            Block bPrev = (Block) listOfContent.get(0);
            Blockchain blk = (Blockchain) listOfContent.get(1);
            String nodeAddress = m.getFromAddress();
            String signature = m.getSignature();
            receiptBlock(bPrev, signature, nodeAddress, blk);
        }
    }

    /**
     * Método para reciber transacciones de otros nodos comerciales.
     *
     * @param tr Transacción.
     */
    public void receiptTransaction(Transaction tr) {
    }

    /**
     * Getter nodeAddress.
     *
     * @return nodeAddress.
     */
    public String getNodeAddress() {
        return nodeAddress;
    }

    /**
     * Getter blockchain.
     *
     * @return blockchain.
     */
    public Blockchain getBlockchain() {
        return blockchain;
    }

    /**
     * Getter publicKey.
     *
     * @return publicKey.
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Getter nodeID.
     *
     * @return nodeID.
     */
    public int getNodeID() {
        return nodeID;
    }
}
