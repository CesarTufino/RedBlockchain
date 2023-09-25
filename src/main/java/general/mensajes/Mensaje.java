package general.mensajes;

import java.io.Serializable;

/**
 * La clase Mensaje representa la información protegida que se envía a través de la red.
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

    public Mensaje(String direccionRemitente, String direccionDestinatario, String firma, long marcaDeTiempoDeCreacion, int tipoDeContenido, Object contenido) {
        this.direccionRemitente = direccionRemitente;
        this.direccionDestinatario = direccionDestinatario;
        this.firma = firma;
        this.marcaDeTiempoDeCreacion = marcaDeTiempoDeCreacion;
        this.tipoDeContenido = tipoDeContenido;
        this.contenido = contenido;
    }

    /**
     * Obtiene la dirección del remitente del mensaje.
     * @return dirección del remitente.
     */
    public String getDireccionRemitente() {
        return direccionRemitente;
    }

    /**
     * Obtiene el tipo del contenido del mensaje.
     * @return tipo del contenido.
     */
    public int getTipoDeContenido() {
        return tipoDeContenido;
    }

    /**
     * Obtiene la dirección del remitente del mensaje.
     * @return dirección del remitente.
     */
    public String getFirma() {
        return firma;
    }

    /**
     * Obtiene la dirección del remitente del mensaje.
     * @return dirección del remitente.
     */
    public Object getContenido() {
        return contenido;
    }

}
