package multiple.mensajes;

import java.io.Serializable;
import java.security.PrivateKey;

import constantes.Tipo;
import utils.*;

/**
 * Clase Transaccion.
 */
public class Transaccion implements Serializable {

    private final Tipo tipo;
    private final String hash;
    private final String direccionRemitente;
    private final String direccionDestinatario;
    private final double monto;
    private final double tarifa;
    private final long marcaDeTiempoDeCreacion;
    private String firma;
    private boolean transaccionConfirmada = false;


    public Transaccion(Tipo tipo, String direccionRemitente, String direccionDestinatario, double monto, long marcaDeTiempoDeCreacion,
                       double tarifa, PrivateKey clavePrivadaRemitente) {
        this.tipo = tipo;
        this.direccionRemitente = direccionRemitente;
        this.direccionDestinatario = direccionDestinatario;
        this.monto = monto;
        this.marcaDeTiempoDeCreacion = marcaDeTiempoDeCreacion;
        this.tarifa = tarifa;
        this.hash = HashUtil.SHA256(this.toString());
        try {
            this.firma = RsaUtil.sign(this.toString(), clavePrivadaRemitente);
        } catch (Exception e) {
            e.printStackTrace();
            this.firma = null;
        }
    }

    public String getDireccionRemitente() {
        return direccionRemitente;
    }

    public String getDireccionDestinatario() {
        return direccionDestinatario;
    }

    public double getMonto() {
        return monto;
    }

    public long getMarcaDeTiempoDeCreacion() {
        return marcaDeTiempoDeCreacion;
    }

    public String getFirma() {
        return firma;
    }

    public String getHash() {
        return hash;
    }

    public double getTarifa() {
        return tarifa;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public boolean isTransaccionConfirmada() {
        return transaccionConfirmada;
    }

    /**
     * Método para confirmar la transacción.
     */
    public void confirmar() {
        transaccionConfirmada = true;
    }

    @Override
    public String toString() {
        return direccionDestinatario + " envia $" + monto + " a " + direccionRemitente + " Marca de tiempo : " + marcaDeTiempoDeCreacion
                + " Tarifa de transaccion : " + tarifa;
    }

}
