package tradicional.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import general.blockchain.Footer;
import tradicional.mensajes.TransaccionTradicional;
import general.utils.HashUtil;

/**
 * Clase BloqueTradicional.
 */
/**
 * La clase BloqueTradicional representa un bloque que guarda información de un grupo de transacciones mediante hashes.
 */
public class BloqueTradicional implements Serializable {
    private final HeaderTradicional headerTradicional;
    private final Footer footer;
    private final List<TransaccionTradicional> transacciones;
    private int idNodoMinero;
    private String direccionNodoMinero;
    private double tiempoDeBusqueda;

    /**
     * Constructor de todos los bloques excepto el primero.
     * @param bloqueTradicionalPrevio último bloque en la cadena de bloques.
     * @param transacciones lista de transacciones que se agregarán al nuevo bloque.
     * @param tiempoDeBusqueda tiempo de busqueda del bloque previo.
     */
    public BloqueTradicional(BloqueTradicional bloqueTradicionalPrevio, List<TransaccionTradicional> transacciones, double tiempoDeBusqueda) {
        this.transacciones = new ArrayList<>(transacciones);
        String stringTransacciones = obtenerStringDeTransacciones();
        this.headerTradicional = new HeaderTradicional(bloqueTradicionalPrevio.getFooter().getHash());
        this.footer = new Footer(HashUtil.SHA256(stringTransacciones + headerTradicional.getHashBloquePrevio()));
        this.tiempoDeBusqueda = tiempoDeBusqueda;
    }

    /**
     * Constructor del primer bloque del blockchain.
     */
    public BloqueTradicional() {
        this.headerTradicional = new HeaderTradicional();
        this.footer = new Footer(HashUtil.SHA256("Master"));
        this.transacciones = new ArrayList<>();
        this.tiempoDeBusqueda = 0;
    }

    /**
     * Obtiene el header del bloque.
     * @return header del bloque.
     */
    public HeaderTradicional getHeader() {
        return headerTradicional;
    }

    /**
     * Obtiene el footer del bloque.
     * @return footer del bloque.
     */
    public Footer getFooter() {
        return footer;
    }

    /**
     * Obtiene el id del nodo que creó el bloque.
     * @return id del nodo minero.
     */
    public int getIdNodoMinero() {
        return idNodoMinero;
    }

    /**
     * Establece el id de nodo que creó el bloque.
     * @param nodeID id de nodo que creó el bloque.
     */
    public void setIdNodoMinero(int nodeID) {
        this.idNodoMinero = nodeID;
    }

    /**
     * Obtiene la dirección del nodo que creó el bloque.
     * @return dirección del nodo minero.
     */
    public String getDireccionNodoMinero() {
        return direccionNodoMinero;
    }

    /**
     * Establece la dirección de nodo que creó el bloque.
     * @param nodeAddress dirección de nodo que creó el bloque.
     */
    public void setDireccionNodoMinero(String nodeAddress) {
        this.direccionNodoMinero = nodeAddress;
    }

    /**
     * Obtiene la lista de transacciones que contiene el bloque.
     * @return lista de transacciones.
     */
    public List<TransaccionTradicional> getTransaction() {
        return transacciones;
    }

    /**
     * Obtiene el el tiempo que demoró la busqueda de los bloques previos.
     * @return tiempo de busqueda.
     */
    public double getTiempoDeBusqueda() {
        return tiempoDeBusqueda;
    }

    /**
     * Transforma en String toda la información de las transacciones.
     * @return string de las transacciones.
     */
    public String obtenerStringDeTransacciones() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TransaccionTradicional transaccionTradicional : transacciones) {
            stringBuilder.append(transaccionTradicional.toString());
        }
        return stringBuilder.toString();
    }

    /**
     * Obtiene una cadena con la información del bloque.
     * @return Una cadena con la información del bloque.
     */
    @Override
    public String toString() {
        return "\n" + headerTradicional.toString() + footer.toString() + "\n";
    }

}
