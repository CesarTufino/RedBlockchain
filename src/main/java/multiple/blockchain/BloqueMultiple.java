package multiple.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import general.blockchain.Footer;
import general.constantes.Tipo;
import multiple.mensajes.TransaccionMultiple;
import general.utils.HashUtil;

/**
 * La clase BloqueMultiple representa un bloque que guarda información de un grupo de transacciones mediante hashes y
 * pertenece a un tipo de blockchain lógico.
 */
public class BloqueMultiple implements Serializable {
    private final HeaderMultiple headerMultiple;
    private final Footer footer;
    private final Tipo tipo;
    private final List<TransaccionMultiple> transacciones;
    private int idNodoMinero;
    private String direccionNodoMinero;
    private double tiempoDeBusqueda;

    /**
     * Constructor de todos los bloques excepto del primero.
     * @param bloqueMultipleFisicoPrevio último bloque físico en la cadena de bloques.
     * @param bloqueMultipleLogicoPrevio último bloque lógico en la cadena de bloques.
     * @param transacciones      lista de transacciones del bloque.
     * @param tiempoDeBusqueda   tiempo de busqueda de los bloques previos.
     */
    public BloqueMultiple(BloqueMultiple bloqueMultipleFisicoPrevio, BloqueMultiple bloqueMultipleLogicoPrevio, List<TransaccionMultiple> transacciones, double tiempoDeBusqueda, Tipo tipo) {
        this.transacciones = new ArrayList<>(transacciones);
        String stringTransacciones = this.obtenerStringDeTransacciones();
        this.headerMultiple = new HeaderMultiple(bloqueMultipleFisicoPrevio.getFooter().getHash(), bloqueMultipleLogicoPrevio.getFooter().getHash());
        this.footer = new Footer(HashUtil.SHA256(stringTransacciones + headerMultiple.getHashBloqueLogicoPrevio() + headerMultiple.getHashBloqueFisicoPrevio()));
        this.tipo = tipo;
        this.tiempoDeBusqueda = tiempoDeBusqueda;
    }

    /**
     * Constructor del primer bloque del primer blockchain lógico.
     * @param tipo tipo de bloque lógico.
     */
    public BloqueMultiple(Tipo tipo) {
        this.headerMultiple = new HeaderMultiple();
        this.footer = new Footer(HashUtil.SHA256("Master"));
        this.transacciones = new ArrayList<>();
        this.tipo = tipo;
        this.tiempoDeBusqueda = 0;
    }

    /**
     * Constructor del primer bloque del segundo blockchain lógico.
     * @param tipo         tipo de bloque lógico.
     * @param primerBloqueMultiple primer (anterior) bloque físico.
     */
    public BloqueMultiple(BloqueMultiple primerBloqueMultiple, Tipo tipo) {
        this.transacciones = new ArrayList<>();
        this.headerMultiple = new HeaderMultiple(primerBloqueMultiple.getFooter().getHash(), "");
        this.footer = new Footer(HashUtil.SHA256("Master" + headerMultiple.getHashBloqueFisicoPrevio()));
        this.tipo = tipo;
        this.tiempoDeBusqueda = 0;
    }

    /**
     * Obtiene el header del bloque.
     * @return header del bloque.
     */
    public HeaderMultiple getHeader() {
        return headerMultiple;
    }

    /**
     * Obtiene el footer del bloque.
     * @return footer del bloque.
     */
    public Footer getFooter() {
        return footer;
    }

    /**
     * Obtiene el tipo del bloque.
     * @return tipo del bloque.
     */
    public Tipo getTipo() {
        return tipo;
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
    public List<TransaccionMultiple> getTransaction() {
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
    private String obtenerStringDeTransacciones() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TransaccionMultiple transaccionMultiple : transacciones) {
            stringBuilder.append(transaccionMultiple.toString());
        }
        return stringBuilder.toString();
    }

    /**
     * Obtiene una cadena con la información del bloque.
     * @return Una cadena con la información del bloque.
     */
    @Override
    public String toString() {
        return "\nTipo de bloque: " + tipo + headerMultiple.toString() + footer.toString() + "\n";
    }

}
