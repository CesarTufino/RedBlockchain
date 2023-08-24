package blockchainTradicional.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import blockchainTradicional.mensajes.Transaccion;
import utils.HashUtil;

public class Bloque implements Serializable {

    private final Header header;
    private final Footer footer;
    private final List<Transaccion> transacciones;
    /**
     * ID de nodo que extrajo el bloque.
     */
    private int idNodo;
    /**
     * Dirección del nodo que extrajo el bloque.
     */
    private String direccionNodo;
    private double tiempoDeBusqueda;

    /**
     * Constructor de todos los bloques excepto el primero
     * 
     * @param bloquePrevio Último bloque, en la cadena de bloques.
     * @param transacciones Lista de transacciones que se agregarán al nuevo bloque.
     * @param tiempoDeBusqueda Tiempo de busqueda del bloque previo
     * 
     */
    public Bloque(Bloque bloquePrevio, List<Transaccion> transacciones, double tiempoDeBusqueda) {
        this.transacciones = new ArrayList<>(transacciones);
        String stringTransacciones = obtenerStringDeTransacciones();
        this.header = new Header(bloquePrevio.getFooter().getHash());
        this.footer = new Footer(HashUtil.SHA256(stringTransacciones + header.hashBloquePrevio));
        this.tiempoDeBusqueda = tiempoDeBusqueda;
    }

    /**
     * Constructor del primer bloque.
     * 
     */
    public Bloque() {
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

    public int getIdNodo() {
        return idNodo;
    }

    public void setIdNodo(int nodeID) {
        this.idNodo = nodeID;
    }

    public String getDireccionNodo() {
        return direccionNodo;
    }

    public void setDireccionNodo(String nodeAddress) {
        this.direccionNodo = nodeAddress;
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