package multiple.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import constantes.Tipo;
import multiple.mensajes.Transaccion;
import utils.HashUtil;

/**
 * Clase Bloque.
 */
public class Bloque implements Serializable {

    private final Header header;
    private final Footer footer;
    private final Tipo tipo;
    private final List<Transaccion> transacciones;
    private int idNodoMinero;
    private String direccionNodoMinero;
    private double tiempoDeBusqueda;

    /**
     * Constructor de todos los bloques excepto del primero.
     *
     * @param bloqueFisicoPrevio Último bloque físico en la cadena de bloques.
     * @param bloqueLogicoPrevio Último bloque lógico en la cadena de bloques.
     * @param transacciones      Lista de transacciones del bloque.
     * @param tiempoDeBusqueda   Tiempo de busqueda de los bloques previos.
     */
    public Bloque(Bloque bloqueFisicoPrevio, Bloque bloqueLogicoPrevio, List<Transaccion> transacciones, double tiempoDeBusqueda, Tipo tipo) {
        this.transacciones = new ArrayList<>(transacciones);
        String stringTransacciones = this.obtenerStringDeTransacciones();
        this.header = new Header(bloqueFisicoPrevio.getFooter().getHash(), bloqueLogicoPrevio.getFooter().getHash());
        this.footer = new Footer(HashUtil.SHA256(stringTransacciones + header.getHashBloqueLogicoPrevio() + header.getHashBloqueFisicoPrevio()));
        this.tipo = tipo;
        this.tiempoDeBusqueda = tiempoDeBusqueda;
    }

    /**
     * Constructor del primer bloque.
     * Usado para crear el primer bloque del primer blockchain lógico.
     *
     * @param tipo Tipo de bloque lógico.
     */
    public Bloque(Tipo tipo) {
        this.header = new Header();
        this.footer = new Footer(HashUtil.SHA256("Master"));
        this.transacciones = new ArrayList<>();
        this.tipo = tipo;
        this.tiempoDeBusqueda = 0;
    }

    /**
     * Constructor del bloque.
     * Usado para crear el primer bloque del segundo blockchain lógico.
     *
     * @param tipo         Identificador del tipo de bloque.
     * @param primerBloque Primer(anterior) bloque físico.
     */
    public Bloque(Bloque primerBloque, Tipo tipo) {
        this.transacciones = new ArrayList<>();
        this.header = new Header(primerBloque.getFooter().getHash(), "");
        this.footer = new Footer(HashUtil.SHA256("Master" + header.getHashBloqueFisicoPrevio()));
        this.tipo = tipo;
        this.tiempoDeBusqueda = 0;
    }

    public Header getHeader() {
        return header;
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

    public List<Transaccion> getTransaction() {
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
        for (Transaccion transaccion : transacciones) {
            stringBuilder.append(transaccion.toString());
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "\nTipo de bloque: " + tipo + header.toString() + footer.toString() + "\n";
    }
}