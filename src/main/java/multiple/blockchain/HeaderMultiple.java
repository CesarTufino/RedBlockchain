package multiple.blockchain;

import java.io.Serializable;
import java.util.Objects;

/**
 * La clase HeaderMultiple representa la parte de los bloques que contiene los hashes de los bloques previos y la marca de
 * tiempo con la fecha de creación del bloque.
 */
public class HeaderMultiple implements Serializable {
    private final String hashBloqueFisicoPrevio;
    private final String hashBloqueLogicoPrevio;
    private final long marcaDeTiempoDeCreacion;

    /**
     * Constructor de HeaderMultiple para el primer bloque.
     */
    public HeaderMultiple() {
        marcaDeTiempoDeCreacion = System.currentTimeMillis();
        hashBloqueFisicoPrevio = "";
        hashBloqueLogicoPrevio = "";
    }

    /**
     * Constructor de HHeaderMultiple para todos los bloques excepto el primero.
     * @param hashBloqueFisicoPrevio hash del header del último bloque físico.
     * @param hashBloqueLogicoPrevio hash del header del último bloque lógico.
     */
    public HeaderMultiple(String hashBloqueFisicoPrevio, String hashBloqueLogicoPrevio) {
        this.marcaDeTiempoDeCreacion = System.currentTimeMillis();
        this.hashBloqueFisicoPrevio = hashBloqueFisicoPrevio;
        this.hashBloqueLogicoPrevio = Objects.requireNonNullElse(hashBloqueLogicoPrevio, "");
    }

    /**
     * Obtiene el hash del bloque físico previo.
     * @return hash del bloque físico previo.
     */
    public String getHashBloqueFisicoPrevio() {
        return hashBloqueFisicoPrevio;
    }

    /**
     * Obtiene el hash del bloque lógico previo.
     * @return hash del bloque lógico previo.
     */
    public String getHashBloqueLogicoPrevio() {
        return hashBloqueLogicoPrevio;
    }

    /**
     * Obtiene la marca de tiempo de creación del bloque.
     * @return marca de tiempo de creación.
     */
    public long getMarcaDeTiempoDeCreacion() {
        return this.marcaDeTiempoDeCreacion;
    }

    /**
     * Obtiene una cadena con la información del header.
     * @return Una cadena con la información del header.
     */
    @Override
    public String toString() {
        return "\nTS : " + marcaDeTiempoDeCreacion + "\nHashBloqueFisicoPrevio :" + this.hashBloqueFisicoPrevio
                + "\nHashBloqueLogicoPrevio : " + this.hashBloqueLogicoPrevio;
    }

}
