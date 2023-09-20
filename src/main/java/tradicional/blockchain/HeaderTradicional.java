package tradicional.blockchain;

import java.io.Serializable;

/**
 * Clase Header.
 */
public class HeaderTradicional implements Serializable{

    protected final String hashBloquePrevio;
    /**
     * Fecha de creación del bloque.
     */
    private final long marcaDeTiempoDeCreacion;

    /**
     * Constructor del Header.
     * Utilizado para el header del primer bloque.
     */
    public HeaderTradicional() {
        marcaDeTiempoDeCreacion = System.currentTimeMillis();
        hashBloquePrevio = "";
    }

    /**
     * Constructor del Header.
     * Utilizado para todos los bloques excepto el primero.
     *
     * @param hashBloquePrevio Hash del header del último bloque.
     */
    public HeaderTradicional(String hashBloquePrevio) {
        this.marcaDeTiempoDeCreacion = System.currentTimeMillis();
        this.hashBloquePrevio = hashBloquePrevio; // Get header's hash
    }

    public String getHashBloquePrevio() {
        return hashBloquePrevio;
    }

    public long getMarcaDeTiempoDeCreacion(){
        return this.marcaDeTiempoDeCreacion;
    }

    @Override
    public String toString() {
        return "\nTS : " + marcaDeTiempoDeCreacion + "\nPrevBlockHash :" + this.hashBloquePrevio;
    }
}
