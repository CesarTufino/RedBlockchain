package Blockchain;

import java.util.Objects;

/**
 * Clase Header.
 * 
 */
public class Header {

    /**
     * Hash del header del último bloque físico.
     */
    protected final String headerHashPrev;
    /**
     * Hash del header del último bloque lógico.
     */
    protected final String PrevIDHash;
    /**
     * Fecha de creación del bloque.
     */
    private final long timeStamp;
    /**
     * Nonce found by a Miner/Validator ?
     */
    private int nonce;

    /**
     * Constructor del Header.
     * Utilizado para el header del primer bloque.
     */
    public Header() {
        timeStamp = System.currentTimeMillis();
        headerHashPrev = "";
        PrevIDHash = "";
        nonce = 0;
    }

    /**
     * Constructor del Header.
     * Utilizado para todos los bloques excepto el primero.
     *
     * @param hashBlockPrev Hash del header del último bloque físico.
     * @param hashBlockIdPrev Hash del header del último bloque lógico.
     */
    public Header(String hashBlockPrev, String hashBlockIdPrev) {
        timeStamp = System.currentTimeMillis(); // Get the current date
        headerHashPrev = hashBlockPrev; // Get header's hash
        PrevIDHash = Objects.requireNonNullElse(hashBlockIdPrev, "");
    }

    /**
     * Getter del headerHashPrev.
     *
     * @return headerHashPrev.
     */
    public String getPrevHash() {
        return headerHashPrev;
    }

    /**
     * Getter del PrevIDHash.
     *
     * @return PrevIDHash.
     */
    public String getPrevIDHash() {
        return PrevIDHash;
    }

    /**
     * Getter del timeStamp
     *
     * @return timeStamp.
     */
    public long getTimeStamp(){return this.timeStamp;}

    /**
     * Método que devuelve la información del header como String.
     *
     * @return Información del header.
     */
    public String toString() {
        return "\nTS : " + timeStamp + "\nPrevBlockHash :" + this.headerHashPrev
                + "\nPrevIDBlockHash : " + this.PrevIDHash + "\nNonce : " + nonce;
    }
}
