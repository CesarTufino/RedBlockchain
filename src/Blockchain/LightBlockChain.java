package Blockchain;

import Network.Network;

import java.util.ArrayList;

/**
 * Clase LightBlockChain.
 * Blockchain formado por LightBlocks.
 */
public class LightBlockChain extends Blockchain {

    /**
     * Lista de los LightBlocks.
     */
    private final ArrayList<LightBlock> LightBlkchain = new ArrayList<>();

    /**
     * Constructor del LightBlockChain.
     *
     * @param network Representación de la red.
     */
    public LightBlockChain(Network network) {
        super(network);
    }

    /**
     * Método para agregar un nuevo LightBlock.
     * Si el LightBlockChain tiene 10 bloques, se elimina 
     * el bloque inicial para añadir el nuevo.
     * 
     * @param h Header del nuevo bloque.
     * @param f Footer del nuevo bloque.
     */
    public void addLightHeader(Header h, Footer f) {
        LightBlock ln = new LightBlock(h, f);
        LightBlkchain.add(ln);
        while (LightBlkchain.size() >= 11) {
            LightBlkchain.remove(0);
        }
    }

    /**
     * Método que imprime todos los bloques.
     */
    public void printBlk() {
        for (LightBlock lb : LightBlkchain) {
            System.out.print(lb.toString());
        }
    }
}
