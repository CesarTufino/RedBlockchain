package multiple.mensajes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Mensaje.
 */
public class Mensaje implements Serializable{

    private final String direccionRemitente;
    private final String direccionDestinatario;
    private final long marcaDeTiempoDeCreacion;
    private final String firma;
    private final Object contenido;
    /**
     * Tipo del contenido del mensaje.
     * 0 -> Transaction, 1 -> Block.
     */
    private final int tipoDeContenido;

    /**
     * Constructor del Message.
     *
     * @param direccionRemitente Dirección del emisor del mensaje.
     * @param direccionDestinatario Dirección del destinatario del mensaje.
     * @param firma Firma del bloque.
     * @param marcaDeTiempoDeCreacion Fecha de creación del mensaje.
     * @param tipoDeContenido Tipo del contenido del mensaje.
     * @param contenido Contenido del mensaje.
     */
    @SuppressWarnings("unchecked") 
    public Mensaje(String direccionRemitente, String direccionDestinatario, String firma, long marcaDeTiempoDeCreacion, int tipoDeContenido, Object contenido) {
        this.direccionRemitente = direccionRemitente;
        this.direccionDestinatario = direccionDestinatario;
        this.firma = firma;
        this.marcaDeTiempoDeCreacion = marcaDeTiempoDeCreacion;
        this.tipoDeContenido = tipoDeContenido;
        this.contenido = contenido;
    }

    public String getDireccionRemitente() {
        return direccionRemitente;
    }

    public String getDireccionDestinatario() {
        return direccionDestinatario;
    }

    public int getTipoDeContenido() {
        return tipoDeContenido;
    }

    public String getFirma() {
        return firma;
    }

    public Object getContenido() {
        return contenido;
    }
}
