package tradicional.blockchain;

import java.io.Serializable;

/**
 * La clase HeaderTradicional representa la parte de los bloques que contiene el hash del bloque previo y la marca de
 * tiempo con la fecha de creación del bloque.
 */
public class HeaderTradicional implements Serializable{
    private final String hashBloquePrevio;
    /**
     * Fecha de creación del bloque.
     */
    private final long marcaDeTiempoDeCreacion;

    /**
     * Constructor de HeaderTradicional para el primer bloque.
     */
    public HeaderTradicional() {
        marcaDeTiempoDeCreacion = System.currentTimeMillis();
        hashBloquePrevio = "";
    }

    /**
     * Constructor de HeaderTradicional para todos los bloques excepto el primero.
     * @param hashBloquePrevio hash del header del último bloque.
     */
    public HeaderTradicional(String hashBloquePrevio) {
        this.marcaDeTiempoDeCreacion = System.currentTimeMillis();
        this.hashBloquePrevio = hashBloquePrevio; // Get header's hash
    }

    /**
     * Obtiene el hash del bloque previo.
     * @return hash del bloque previo.
     */
    public String getHashBloquePrevio() {
        return hashBloquePrevio;
    }

    /**
     * Obtiene la marca de tiempo de creación del bloque.
     * @return marca de tiempo de creación.
     */
    public long getMarcaDeTiempoDeCreacion(){
        return this.marcaDeTiempoDeCreacion;
    }

    /**
     * Obtiene una cadena con la información del header.
     * @return Una cadena con la información del header.
     */
    @Override
    public String toString() {
        return "\nTS : " + marcaDeTiempoDeCreacion + "\nPrevBlockHash :" + this.hashBloquePrevio;
    }

}
