package PoS;

import Blockchain.Block;
import Blockchain.Blockchain;
import Network.Network;
import Network.Node;

/**
 * Clase FullNode extending Node.
 * Esta clase simula un nodo que descarga todo el blockchain actuando como uno de los servidores de la red.
 */
public class FullNode extends Node {

    /**
     * Constructor FullNode.
     *
     * @param nom nombre del FullNode.
     * @param network Red a la que pertenece.
     */
    public FullNode(String nom, Network network) {
        super(nom, network);
    }

    /**
     * Método para recibir un bloque.
     * 
     * @param block Bloque recibido.
     * @param signature Firma.
     * @param nodeID Identificador del nodo.
     * @param blk Blockchain donde se va a añadir el bloque.
     */
    @Override
    public void receiptBlock(Block block, String signature, String nodeID, Blockchain blk) {
        this.blockchain.addBlock(block);
    }
}
