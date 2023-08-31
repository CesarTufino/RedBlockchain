package pos.blockchainMultipleAleatorio.blockchain;

import java.io.Serializable;

/**
 * Clase Footer.
 */
public class Footer  implements Serializable{

    private String hash;

    public Footer(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "\nFooter hash : " + hash + "\n------------------------------";
    }
}

