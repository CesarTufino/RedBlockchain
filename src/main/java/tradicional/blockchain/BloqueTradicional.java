package tradicional.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tradicional.mensajes.TransaccionTradicional;
import general.utils.HashUtil;

/**
 * Clase BloqueTradicional.
 */
public class BloqueTradicional implements Serializable {

    private final HeaderTradicional headerTradicional;
    private final Footer footer;
    private final List<TransaccionTradicional> transacciones;
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
    public BloqueTradicional(BloqueTradicional bloqueTradicionalPrevio, List<TransaccionTradicional> transacciones, double tiempoDeBusqueda) {
        this.transacciones = new ArrayList<>(transacciones);
        String stringTransacciones = obtenerStringDeTransacciones();
        this.headerTradicional = new HeaderTradicional(bloqueTradicionalPrevio.getFooter().getHash());
        this.footer = new Footer(HashUtil.SHA256(stringTransacciones + headerTradicional.hashBloquePrevio));
        this.tiempoDeBusqueda = tiempoDeBusqueda;
    }

    /**
     * Constructor del primer bloque.
     * 
     */
    public BloqueTradicional() {
        this.headerTradicional = new HeaderTradicional();
        this.footer = new Footer(HashUtil.SHA256("Master"));
        this.transacciones = new ArrayList<>();
        this.tiempoDeBusqueda = 0;
    }

    public HeaderTradicional getHeader() {
        return headerTradicional;
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

    public List<TransaccionTradicional> getTransaction() {
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
        for (TransaccionTradicional transaccionTradicional : transacciones) {
            stringBuilder.append(transaccionTradicional.toString());
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "\n" + headerTradicional.toString() + footer.toString() + "\n";
    }
}