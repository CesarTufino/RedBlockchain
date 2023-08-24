package blockchainMultipleAleatorio.mensajes;

import java.io.Serializable;
import java.security.PrivateKey;

import utils.*;

/**
 * Clase Transaction.
 */
public class Transaccion implements Serializable {

    /**
     * Tipo de transacción, del blockchain lógico
     */
    private final String tipo;
    private final String hash;
    private final String direccionRemitente;
    private final String direccionDestinatario;
    private final double monto;
    private final double tarifa;
    /**
     * Fecha de creación de la transacción.
     */
    private final long marcaDeTiempo;
    private String firma;
    private boolean transaccionConfirmada = false;

    public Transaccion(String tipo, String direccionRemitente, String direccionDestinatario, double monto,
            long marcaDeTiempo, double tarifa, PrivateKey clavePrivada) {
        this.tipo = tipo;
        this.direccionRemitente = direccionRemitente;
        this.direccionDestinatario = direccionDestinatario;
        this.monto = monto;
        this.marcaDeTiempo = marcaDeTiempo;
        this.tarifa = tarifa;
        this.hash = HashUtil.SHA256(this.toString());
        try {
            this.firma = RsaUtil.sign(this.toString(), clavePrivada);
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

    public long getMarcaDeTiempo() {
        return marcaDeTiempo;
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

    public String getTipo() {
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
        return direccionDestinatario + " envia $" + monto + " a " + direccionRemitente + " Marca de tiempo : " + marcaDeTiempo
                + " Tarifa de transaccion : " + tarifa;
    }

}
