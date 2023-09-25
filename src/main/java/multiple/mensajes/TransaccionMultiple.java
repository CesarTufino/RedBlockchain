package multiple.mensajes;

import java.io.Serializable;
import java.security.PrivateKey;

import general.constantes.Tipo;
import general.utils.*;

/**
 * La clase TransaccionMultiple representa las transacciones de cada tipo de blockchain que realizan los nodos.
 */
public class TransaccionMultiple implements Serializable {
    private final Tipo tipo;
    private final String hash;
    private final String direccionRemitente;
    private final String direccionDestinatario;
    private final double monto;
    private final double tarifa;
    private final long marcaDeTiempoDeCreacion;
    private String firma;
    private boolean transaccionConfirmada = false;

    public TransaccionMultiple(Tipo tipo, String direccionRemitente, String direccionDestinatario, double monto,
                               long marcaDeTiempoDeCreacion, double tarifa, PrivateKey clavePrivadaRemitente) {
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

    /**
     * Obtiene la dirección del remitente de la transacción.
     * @return dirección del remitente.
     */
    public String getDireccionRemitente() {
        return direccionRemitente;
    }

    /**
     * Obtiene la dirección del destinatario de la transacción.
     * @return dirección del destinatario.
     */
    public String getDireccionDestinatario() {
        return direccionDestinatario;
    }

    /**
     * Obtiene el monto de la transacción.
     * @return monto.
     */
    public double getMonto() {
        return monto;
    }

    /**
     * Obtiene la firma de la transacción.
     * @return firma.
     */
    public String getFirma() {
        return firma;
    }

    /**
     * Obtiene el hash de la transacción.
     * @return hash de la transacción.
     */
    public String getHash() {
        return hash;
    }

    /**
     * Obtiene la tarifa de la transacción.
     * @return tarifa de la transacción.
     */
    public double getTarifa() {
        return tarifa;
    }

    /**
     * Obtiene el tipo de la transacción.
     * @return tipo de la transacción.
     */
    public Tipo getTipo() {
        return tipo;
    }

    /**
     * Confirma la transacción.
     */
    public void confirmar() {
        transaccionConfirmada = true;
    }

    /**
     * Obtiene una cadena con la información de la transacción.
     * @return Una cadena con la información de la transacción.
     */
    @Override
    public String toString() {
        return direccionDestinatario + " envia $" + monto + " a " + direccionRemitente + " Marca de tiempo : "
                + marcaDeTiempoDeCreacion
                + " Tarifa de transaccion : " + tarifa;
    }

    /**
     * Compara este objeto con un objeto especificado.
     * @return true si los objetos tienen el mismo hash y firma.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TransaccionMultiple transaccionMultipleComparada)){
            return false;
        }
        return transaccionMultipleComparada.getHash().equals(this.getHash())
                && (transaccionMultipleComparada.getFirma().equals(this.getFirma()));
    }

}
