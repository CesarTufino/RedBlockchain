package Blockchain;

import MessageTypes.Transaction;
import Utils.HashUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clase que representa los Bloques de un Blockchain.
 * 
 */
public class Block {

    /**
     * Encabezado del bloque.
     */
    private final Header header;
    /**
     * Pie de pagina del bloque.
     */
    private final Footer footer;
    /**
     * Identificador que indica el blockchain lógico al cual pertenece.
     */
    private final String blockID; // Type
    /**
     * Lista de todas las transacciones encapsuladas en el bloque.
     */
    private final List<Transaction> transactions;
    /**
     * ID de nodo que minó(extrajo) o apostó por el bloque.
     */
    private int nodeID;
    /**
     * Dirección del minero que minó(extrajo) o apostó por el bloque.
     */
    private String nodeAddress;

    /**
     * Constructor del Bloque.
     *
     * @param blockPrev Último bloque, en la cadena de bloques, necesario para obtener el hash.
     * @param transaction Lista de transacciones que se agregarán a un nuevo bloque.
     */
    public Block(Block blockPrev, Block blockIDPrev, List<Transaction> transaction, String blockID) {
        this.transactions = new ArrayList<>(transaction);
        String trs = this.toStringAllTransaction();
        header = new Header(blockPrev.getFooter().getHash(), blockIDPrev.getFooter().getHash());
        footer = new Footer();
        footer.setHash(HashUtil.SHA256(trs + header.PrevIDHash + header.headerHashPrev));
        this.blockID = blockID;
        // System.out.println(this);
    }

    /**
     * Constructor del bloque.
     * Usado para crear el primer bloque del primer blockchain lógico.
     * 
     * @param blockID Identificador del tipo de bloque.
     */
    public Block(String blockID) {
        header = new Header();
        footer = new Footer();
        footer.setHash(HashUtil.SHA256("Master")); // First block has Hash(Master)
        this.transactions = new ArrayList<>();
        this.blockID = blockID;
        // System.out.println(this);
    }

    /**
     * Constructor del bloque.
     * Usado para crear el primer bloque del segundo blockchain lógico.
     * 
     * @param ID Identificador del tipo de bloque.
     * @param firstBlock Primer(anterior) bloque físico.
     */
    public Block(Block firstBlock, String ID) {
        this.transactions = new ArrayList<>();
        header = new Header(firstBlock.getFooter().getHash(), "");
        footer = new Footer();
        footer.setHash(HashUtil.SHA256("Master" + header.headerHashPrev));
        this.blockID = ID;
        // System.out.println(this);
    }

    /**
     * Getter de header
     *
     * @return header
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Getter de blockId
     *
     * @return blockId
     */
    public String getBlockID() {
        return blockID;
    }

    /**
     * Getter de footer
     *
     * @return footer
     */
    public Footer getFooter() {
        return footer;
    }

    /**
     * Getter de nodeID
     *
     * @return nodeId
     */
    public int getNodeID() {
        return nodeID;
    }

    /**
     * Setter de nodeID.
     *
     * @param nodeID ID de nodo minero.
     */
    public void setNodeID(int nodeID) {
        this.nodeID = nodeID;
    }

    /**
     * Getter del nodeAdress.
     *
     * @return nodeAddress.
     */
    public String getNodeAddress() {
        return nodeAddress;
    }

    /**
     * Setter de nodeAddress.
     *
     * @param nodeAddress Dirección del nodo minero.
     */
    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    /**
     * Getter de transaction.
     *
     * @return transaction.
     */
    public List<Transaction> getTransaction() {
        return transactions;
    }

    /**
     * Método para comparar bloques.
     *
     * @return Si el bloque es igual.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Block block = (Block) o;
        return header.equals(block.header) && footer.equals(block.footer);
    }

    /**
     * Método para obtener el hash del encabezado.
     *
     * @return Hash del encabezado.
     */
    @Override
    public int hashCode() {
        return Objects.hash(header);
    }

    /**
     * Método para obtener los datos del bloque.
     *
     * @return String con los datos del bloque.
     */
    @Override
    public String toString() {
        return "\nID : " + blockID + header.toString() + footer.toString();
    }

    /**
     * Método para transformar en String toda la información de la transacción.
     *
     * @return Toda la información de la transacción en un String.
     */
    public String toStringAllTransaction() {
        StringBuilder trs = new StringBuilder();
        for (Transaction transaction : transactions) {
            trs.append(transaction.toString());
        }
        return trs.toString();
    }

    /**
     * Método que imprime toda la información de la transacción.
     */
    public void printTransactions() {
        for (Transaction transaction : transactions) {
            System.out.println(transaction.getTransactionHash());
        }
    }

}
