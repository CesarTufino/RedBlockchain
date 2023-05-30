package Blockchain;

import Network.Network;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Clase Blockchain.
 * 
 */
public class Blockchain {

    /**
     * Utilizado para la sincronización (hilos).
     */
    private static final Object o = new Object();
    /**
     * Utilizado para el ID del Blockchain.
     */
    private static int cpt = 0;
    /**
     * ID del Blockchain.
     */
    private final int blockChainId;
    /**
     * Lista de los Bloques.
     */
    private final List<Block> blkchain = new CopyOnWriteArrayList<>();
    /**
     * Diferencia de tiempo de creación entre los bloques del primer blockchain lógico.
     */
    public final List<Double> WTT1 = new CopyOnWriteArrayList<>();
    /**
     * Diferencia de tiempo de creación entre los bloques del segundo blockchain lógico.
     */
    public final List<Double> WTT2 = new CopyOnWriteArrayList<>();
    /**
     * Representación de la red.
     */
    private final Network network;


    /**
     * Constructor del Blockchain.
     * Crea un Blockchain, de le asigna un id y se crean los primeros bloques.
     * 
     * @param network Representación de la red.
     */
    public Blockchain(Network network) {
        synchronized (o) {
            this.blockChainId = cpt++;
            this.network = network;
            this.blkchain.addAll(createFirstBlock());
        }
    }

    /**
     * Método que crea el primer bloque de cada blockchain lógico.
     *
     * @return Primeros bloques.
     */
    public List<Block> createFirstBlock() {
        Block firstBlock = new Block(network.TYPE1);
        Block secondBlock = new Block(firstBlock, network.TYPE2);
        return Arrays.asList(firstBlock, secondBlock);
    }

    /**
     * Método que devuelve el último bloque del blockchain físico.
     *
     * @return Último bloque del blockchain físico.
     */
    public Block getLatestBlock() {
        return blkchain.get(blkchain.size() - 1);
    }

    /**
     * Método que devuelve el último bloque de un blockchain lógico.
     *
     * @param blockID Identificador del blockchain lógico.
     * @param i Posición desde la que se comienza a buscar.
     * @return Último bloque del blockchain lógico.
     */
    public Block searchPrevBlockByID(String blockID, int i) {
        if (i < 0) {
            return null;
        }
        Block b = this.blkchain.get(i);
        if (blockID.equals(b.getBlockID()))
            return b;
        else {
            return searchPrevBlockByID(blockID, --i);
        }
    }


    /**
     * Método que agrega un bloque al blockchain físico y 
     * guarda la diferencia de tiempo de creación entre el bloque actual y el anterior.
     *
     * @param block Bloque que se va a añadir.
     */
    public synchronized void addBlock(Block block){
        String ID = block.getBlockID();
        Block prevBlock = searchPrevBlockByID(ID, this.blkchain.size()-1);
        //System.out.println("-----------Elapse time for block type " + ID +" = "+ (double)(block.getHeader().getTimeStamp() - prevBlock.getHeader().getTimeStamp())/1000 + " s");
        if(ID.equals(network.TYPE1)) {
            WTT1.add((double) (block.getHeader().getTimeStamp() - prevBlock.getHeader().getTimeStamp()) / 1000);
            WTT2.add((double)0);
        }else {
            WTT2.add((double) (block.getHeader().getTimeStamp() - prevBlock.getHeader().getTimeStamp()) / 1000);
            WTT1.add((double)0);
        }
        blkchain.add(block);
    }

    /**
     * Método que imprime la información de todos los bloques.
     */
    public void printBlk() {
        for (Block block : blkchain) {
            System.out.print(block.toString());
        }
    }

    /**
     * Método que devuelve el tamaño del blockchain físico.
     *
     * @return Tamaño del blockchain físico.
     */
    public int getSize() {
        return blkchain.size();
    }

    /**
     * Método que devuelve el sexto bloque válido del blockchain físico.
     *
     * @return Bloque o null si el tamaño del blockchain físico es menor que 6.
     */
    public Block getUpdateBlock() {
        if (blkchain.size() > 6)
            return blkchain.get(blkchain.size() - 6);
        return null;
    }

    /**
     * Método que devuelve una copia del blockchain.
     *
     * @return Copia del blockchain.
     */
    public Blockchain copyBlkch() {
        Blockchain blk = new Blockchain(network);
        blk.blkchain.clear();
        blk.blkchain.addAll(this.blkchain);
        return blk;
    }

}
