package multiple.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import general.blockchain.Footer;
import general.constantes.Tipo;
import multiple.mensajes.TransaccionMultiple;
import general.utils.HashUtil;

/**
 * Clase BloqueMultiple.
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
     *
     * @param bloqueMultipleFisicoPrevio Último bloque físico en la cadena de bloques.
     * @param bloqueMultipleLogicoPrevio Último bloque lógico en la cadena de bloques.
     * @param transacciones      Lista de transacciones del bloque.
     * @param tiempoDeBusqueda   Tiempo de busqueda de los bloques previos.
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
     * Constructor del primer bloque.
     * Usado para crear el primer bloque del primer general.blockchain lógico.
     *
     * @param tipo Tipo de bloque lógico.
     */
    public BloqueMultiple(Tipo tipo) {
        this.headerMultiple = new HeaderMultiple();
        this.footer = new Footer(HashUtil.SHA256("Master"));
        this.transacciones = new ArrayList<>();
        this.tipo = tipo;
        this.tiempoDeBusqueda = 0;
    }

    /**
     * Constructor del bloque.
     * Usado para crear el primer bloque del segundo general.blockchain lógico.
     *
     * @param tipo         Identificador del tipo de bloque.
     * @param primerBloqueMultiple Primer(anterior) bloque físico.
     */
    public BloqueMultiple(BloqueMultiple primerBloqueMultiple, Tipo tipo) {
        this.transacciones = new ArrayList<>();
        this.headerMultiple = new HeaderMultiple(primerBloqueMultiple.getFooter().getHash(), "");
        this.footer = new Footer(HashUtil.SHA256("Master" + headerMultiple.getHashBloqueFisicoPrevio()));
        this.tipo = tipo;
        this.tiempoDeBusqueda = 0;
    }

    public HeaderMultiple getHeader() {
        return headerMultiple;
    }

    public Footer getFooter() {
        return footer;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public int getIdNodoMinero() {
        return idNodoMinero;
    }

    public void setIdNodoMinero(int nodeID) {
        this.idNodoMinero = nodeID;
    }

    public String getDireccionNodoMinero() {
        return direccionNodoMinero;
    }

    public void setDireccionNodoMinero(String nodeAddress) {
        this.direccionNodoMinero = nodeAddress;
    }

    public List<TransaccionMultiple> getTransaction() {
        return transacciones;
    }

    public double getTiempoDeBusqueda() {
        return tiempoDeBusqueda;
    }

    /**
     * Método para transformar en String toda la información de la transacción.
     *
     * @return Toda la información de la transacción en un String.
     */
    private String obtenerStringDeTransacciones() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TransaccionMultiple transaccionMultiple : transacciones) {
            stringBuilder.append(transaccionMultiple.toString());
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "\nTipo de bloque: " + tipo + headerMultiple.toString() + footer.toString() + "\n";
    }
}