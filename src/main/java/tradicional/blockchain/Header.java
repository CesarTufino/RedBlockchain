package tradicional.blockchain;

import java.io.Serializable;

/**
 * Clase Header.
 */
public class Header  implements Serializable{

    protected final String hashBloquePrevio;
    /**
     * Fecha de creación del bloque.
     */
    private final long marcaDeTiempo;

    /**
     * Constructor del Header.
     * Utilizado para el header del primer bloque.
     */
    public Header() {
        marcaDeTiempo = System.currentTimeMillis();
        hashBloquePrevio = "";
    }

    /**
     * Constructor del Header.
     * Utilizado para todos los bloques excepto el primero.
     *
     * @param hashBloquePrevio Hash del header del último bloque.
     */
    public Header(String hashBloquePrevio) {
        this.marcaDeTiempo = System.currentTimeMillis();
        this.hashBloquePrevio = hashBloquePrevio; // Get header's hash
    }

    public String getHashBloquePrevio() {
        return hashBloquePrevio;
    }

    public long getMarcaDeTiempo(){
        return this.marcaDeTiempo;
    }

    @Override
    public String toString() {
        return "\nTS : " + marcaDeTiempo + "\nPrevBlockHash :" + this.hashBloquePrevio;
    }
}
