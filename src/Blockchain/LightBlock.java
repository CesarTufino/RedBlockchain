package Blockchain;

/**
 * Clase LightBlock
 * Versión más ligera del Bloque: solo tiene el hash sin la transacción en el bloque.
 * 
 */
public class LightBlock extends Header {

    /**
     * Footer del bloque.
     */
    private final Footer footer;

    /**
     * Constructor del LightBlock.
     * 
     * @param h Header del nuevo bloque.
     * @param f Footer del nuevo bloque.
     */
    public LightBlock(Header h, Footer f) {
        super(h.getPrevHash(), h.getPrevIDHash());
        this.footer = f;
    }
}
