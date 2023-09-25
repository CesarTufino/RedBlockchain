package general.blockchain;

import java.io.Serializable;

/**
 * La clase Footer representa el hash principal de un bloque.
 */
public class Footer  implements Serializable{
    private String hash;

    public Footer(String hash) {
        this.hash = hash;
    }

    /**
     * Obtiene el hash del bloque.
     * @return el hash del bloque.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Obtiene una cadena con el hash del bloque.
     * @return Una cadena con la información del footer.
     */
    @Override
    public String toString() {
        return "\nFooter hash : " + hash + "\n------------------------------";
    }

}
