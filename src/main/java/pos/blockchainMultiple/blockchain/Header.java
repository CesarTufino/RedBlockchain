package pos.blockchainMultiple.blockchain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clase Header.
 */
public class Header  implements Serializable{

    protected final String hashBloqueFisicoPrevio;
    protected final String hashBloqueLogicoPrevio;
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
        hashBloqueFisicoPrevio = "";
        hashBloqueLogicoPrevio = "";
    }

    /**
     * Constructor del Header.
     * Utilizado para todos los bloques excepto el primero.
     *
     * @param hashBloqueFisicoPrevio Hash del header del último bloque físico.
     * @param hashBloqueLogicoPrevio Hash del header del último bloque lógico.
     */
    public Header(String hashBloqueFisicoPrevio, String hashBloqueLogicoPrevio) {
        this.marcaDeTiempo = System.currentTimeMillis(); // Get the current date
        this.hashBloqueFisicoPrevio = hashBloqueFisicoPrevio; // Get header's hash
        this.hashBloqueLogicoPrevio = Objects.requireNonNullElse(hashBloqueLogicoPrevio, "");
    }

    public String getHashBloqueFisicoPrevio() {
        return hashBloqueFisicoPrevio;
    }

    public String getHashBloqueLogicoPrevio() {
        return hashBloqueLogicoPrevio;
    }

    public long getMarcaDeTiempo(){return this.marcaDeTiempo;}

    @Override
    public String toString() {
        return "\nTS : " + marcaDeTiempo + "\nHashBloqueFisicoPrevio :" + this.hashBloqueFisicoPrevio
                + "\nHashBloqueLogicoPrevio : " + this.hashBloqueLogicoPrevio;
    }
}
