package gatewayVersion.blockchainMultiple.mensajes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Message.
 */
public class Mensaje implements Serializable{

    private final String direccionRemitente;
    private final String direccionDestinatario;
    /**
     * Fecha de creación del mensaje.
     */
    private final long marcaDeTiempo;
    private final String firma;
    private final List<Object> contenido = new ArrayList<>();
    /**
     * Tipo del contenido del mensaje.
     * 0 -> Transaction, 1 -> Block.
     */
    private final int tipo;

    /**
     * Constructor del Message.
     *
     * @param direccionRemitente Dirección del emisor del mensaje.
     * @param direccionDestinatario Dirección del destinatario del mensaje.
     * @param firma Firma del bloque.
     * @param marcaDeTiempo Fecha de creación del mensaje.
     * @param tipo Tipo del contenido del mensaje.
     * @param contenido Contenido del mensaje.
     */
    @SuppressWarnings("unchecked") 
    public Mensaje(String direccionRemitente, String direccionDestinatario, String firma, long marcaDeTiempo, int tipo, Object contenido) {
        this.direccionRemitente = direccionRemitente;
        this.direccionDestinatario = direccionDestinatario;
        this.firma = firma;
        this.marcaDeTiempo = marcaDeTiempo;
        this.tipo = tipo;
        if (tipo == 1) {
            if (contenido instanceof List) {
                this.contenido.add(((List<Object>) contenido).get(0));
            }
        } else {
            this.contenido.add(contenido);
        }
    }

    public String getDireccionRemitente() {
        return direccionRemitente;
    }

    public String getDireccionDestinatario() {
        return direccionDestinatario;
    }

    public long getMarcaDeTiempo() {
        return marcaDeTiempo;
    }

    public int getTipo() {
        return tipo;
    }

    public String getFirma() {
        return firma;
    }

    public List<Object> getContenido() {
        return contenido;
    }
}
