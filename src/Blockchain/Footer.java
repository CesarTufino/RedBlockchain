package Blockchain;

/**
 * Clase Footer
 * 
 */
public class Footer {

    /**
     * Hash del Footer.
     */
    private String hash;

    /**
     * Constructor del Footer.
     * Crea un Footer con un hash vacío.
     */
    public Footer() {
        this.hash = "";
    }

    /**
     * Constructor del Footer.
     * Crea un Footer con el hash indicado.
     *
     * @param hash 
     */
    public Footer(String hash) {
        this.hash = hash;
    }

    /**
     * Getter del hash.
     *
     * @return hash.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Setter del hash
     *
     * @param hash Hash.
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Método que devuelve el hash del Footer como String.
     *
     * @return Hash del Footer.
     */
    public String toString() {
        return "\nFooter hash : " + hash + "\n------------------------------";
    }
}

