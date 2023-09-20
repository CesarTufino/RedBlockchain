package multiple.blockchain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clase Header.
 */
public class HeaderMultiple implements Serializable {

    private final String hashBloqueFisicoPrevio;
    private final String hashBloqueLogicoPrevio;
    private final long marcaDeTiempoDeCreacion;

    /**
     * Constructor del Header.
     * Utilizado para el header del primer bloque.
     */
    public HeaderMultiple() {
        marcaDeTiempoDeCreacion = System.currentTimeMillis();
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
    public HeaderMultiple(String hashBloqueFisicoPrevio, String hashBloqueLogicoPrevio) {
        this.marcaDeTiempoDeCreacion = System.currentTimeMillis();
        this.hashBloqueFisicoPrevio = hashBloqueFisicoPrevio;
        this.hashBloqueLogicoPrevio = Objects.requireNonNullElse(hashBloqueLogicoPrevio, "");
    }

    public String getHashBloqueFisicoPrevio() {
        return hashBloqueFisicoPrevio;
    }

    public String getHashBloqueLogicoPrevio() {
        return hashBloqueLogicoPrevio;
    }

    public long getMarcaDeTiempoDeCreacion() {
        return this.marcaDeTiempoDeCreacion;
    }

    @Override
    public String toString() {
        return "\nTS : " + marcaDeTiempoDeCreacion + "\nHashBloqueFisicoPrevio :" + this.hashBloqueFisicoPrevio
                + "\nHashBloqueLogicoPrevio : " + this.hashBloqueLogicoPrevio;
    }
}
