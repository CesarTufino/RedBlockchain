package gatewayVersion.blockchainMultipleDisparejo.blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import gatewayVersion.blockchainMultipleDisparejo.mensajes.Transaccion;
import utils.HashUtil;

public class Bloque implements Serializable {

    private final Header header;
    private final Footer footer;
    private final String tipo;
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
     * @param bloqueFisicoPrevio Último bloque físico en la cadena de bloques.
     * @param bloqueLogicoPrevio Último bloque lógico en la cadena de bloques.
     * @param transacciones Lista de transacciones que se agregarán al nuevo bloque.
     * @param tiempoDeBusqueda Tiempo de busqueda del bloque previo
     *
     */
    public Bloque(Bloque bloqueFisicoPrevio, Bloque bloqueLogicoPrevio, List<Transaccion> transacciones, double tiempoDeBusqueda, String tipo) {
        this.transacciones = new ArrayList<>(transacciones);
        String stringTransacciones = this.obtenerStringDeTransacciones();
        this.header = new Header(bloqueFisicoPrevio.getFooter().getHash(), bloqueLogicoPrevio.getFooter().getHash());
        this.footer = new Footer(HashUtil.SHA256(stringTransacciones + header.hashBloqueLogicoPrevio + header.hashBloqueFisicoPrevio));
        this.tipo = tipo;
        this.tiempoDeBusqueda = tiempoDeBusqueda;
    }

    /**
     * Constructor del primer bloque.
     * Usado para crear el primer bloque del primer blockchain lógico.
     *
     * @param tipo Tipo de bloque lógico.
     */
    public Bloque(String tipo) {
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
     * @param tipo Identificador del tipo de bloque.
     * @param primerBloque Primer(anterior) bloque físico.
     */
    public Bloque(Bloque primerBloque, String tipo) {
        this.transacciones = new ArrayList<>();
        this.header = new Header(primerBloque.getFooter().getHash(), "");
        this.footer = new Footer(HashUtil.SHA256("Master" + header.hashBloqueFisicoPrevio));
        this.tipo = tipo;
        this.tiempoDeBusqueda = 0;
    }
    public Header getHeader() {
        return header;
    }
    public Footer getFooter() {
        return footer;
    }

    public String getTipo() {
        return tipo;
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
        return "\nTipo de bloque: " + tipo + header.toString() + footer.toString() + "\n";
    }
}