package tradicional.mensajes;

import java.io.Serializable;
import java.security.PrivateKey;

import general.utils.*;

/**
 * Clase Transaccion.
 */
public class TransaccionTradicional implements Serializable {

    private final String hash;
    private final String direccionRemitente;
    private final String direccionDestinatario;
    private final double monto;
    private final double tarifa;
    private final long marcaDeTiempoDeCreacion;
    private String firma;
    private boolean transaccionConfirmada = false;

    public TransaccionTradicional(String direccionRemitente, String direccionDestinatario, double monto, long marcaDeTiempoDeCreacion,
                                  double tarifa, PrivateKey clavePrivadaRemitente) {
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

    /**
     * Método para confirmar la transacción.
     */
    public void confirmar() {
        transaccionConfirmada = true;
    }

    @Override
    public String toString() {
        return direccionDestinatario + " envia $" + monto + " a " + direccionRemitente
                + " Marca de tiempo : " + marcaDeTiempoDeCreacion
                + " Tarifa de transaccion : " + tarifa;
    }

}
