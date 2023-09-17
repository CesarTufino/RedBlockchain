package tradicional.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tradicional.mensajes.Transaccion;
import utils.HashUtil;

/**
 * Clase BloqueTradicional.
 */
public class BloqueTradicional implements Serializable {

    private final Header header;
    private final Footer footer;
    private final List<Transaccion> transacciones;
    private int idNodoMinero;
    private String direccionNodoMinero;
    private double tiempoDeBusqueda;

    /**
     * Constructor de todos los bloques excepto el primero
     * 
     * @param bloqueTradicionalPrevio Último bloque, en la cadena de bloques.
     * @param transacciones Lista de transacciones que se agregarán al nuevo bloque.
     * @param tiempoDeBusqueda Tiempo de busqueda del bloque previo
     * 
     */
    public BloqueTradicional(BloqueTradicional bloqueTradicionalPrevio, List<Transaccion> transacciones, double tiempoDeBusqueda) {
        this.transacciones = new ArrayList<>(transacciones);
        String stringTransacciones = obtenerStringDeTransacciones();
        this.header = new Header(bloqueTradicionalPrevio.getFooter().getHash());
        this.footer = new Footer(HashUtil.SHA256(stringTransacciones + header.hashBloquePrevio));
        this.tiempoDeBusqueda = tiempoDeBusqueda;
    }

    /**
     * Constructor del primer bloque.
     * 
     */
    public BloqueTradicional() {
        this.header = new Header();
        this.footer = new Footer(HashUtil.SHA256("Master"));
        this.transacciones = new ArrayList<>();
        this.tiempoDeBusqueda = 0;
    }

    public Header getHeader() {
        return header;
    }
    public Footer getFooter() {
        return footer;
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
    public String obtenerStringDeTransacciones() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Transaccion transaccion : transacciones) {
            stringBuilder.append(transaccion.toString());
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "\n" + header.toString() + footer.toString() + "\n";
    }
}