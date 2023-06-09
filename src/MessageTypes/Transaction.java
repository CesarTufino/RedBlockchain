package MessageTypes;

import Utils.HashUtil;
import Utils.RsaUtil;

import java.security.PrivateKey;

/**
 * Clase Transaction.
 */
public class Transaction {

    /**
     * Identificador de la transacción.
     */
    private final String transactionID;
    /**
     * Hash de la transacción.
     */
    private final String transactionHash;
    /**
     * Dirección del emisor de la transacción.
     */
    private final String fromAddress;
    /**
     * Dirección del destinatario de la transacción.
     */
    private final String toAddress;
    /**
     * Monto.
     */
    private final double amount;
    /**
     * Tarifa de transacción.
     */
    private final double transactionFee;
    /**
     * Fecha de creación de la transacción.
     */
    private final long timeStamp;
    /**
     * Firma del bloque.
     */
    private String signature;
    /**
     * Confirmación de la transacción.
     */
    private boolean confirmedTrans = false;

    /**
     * Constructor de Transaction.
     * 
     * @param transactionID Identificador de la transacción.
     * @param fromAddress Dirección del emisor del mensaje.
     * @param toAddress Dirección del destinatario del mensaje.
     * @param amount Monto.
     * @param timeStamp Fecha de creación del mensaje.
     * @param transactionFee Tarifa de transacción.
     * @param pv Clave privada.
     */
    public Transaction(String transactionID, String fromAddress, String toAddress, double amount, long timeStamp, double transactionFee, PrivateKey pv) {
        this.transactionID = transactionID;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
        this.timeStamp = timeStamp;
        this.transactionFee = transactionFee;
        this.transactionHash = HashUtil.SHA256(this.toString());
        try {
            this.signature = RsaUtil.sign(this.toString(), pv);
        } catch (Exception e) {
            e.printStackTrace();
            this.signature = null;
        }
    }

    /**
     * Método para confirmar la transacción.
     */
    public synchronized void confirmed() {
        confirmedTrans = true;
    }

    /**
     * Getter fromAddress.
     *
     * @return fromAddress.
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * Getter toAddress.
     *
     * @return toAddress.
     */
    public String getToAddress() {
        return toAddress;
    }

    /**
     * Getter amount.
     *
     * @return amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Getter timeStamp.
     *
     * @return timeStamp.
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Getter signature.
     *
     * @return signature.
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Function which return the latest blockchain's block
     *
     * @return latest blockchain's block
     */
    public String getTransactionHash() {
        return transactionHash;
    }

    /**
     * Getter transactionFee.
     *
     * @return transactionFee.
     */
    public double getTransactionFee() {
        return transactionFee;
    }

    /**
     * Getter transactionID.
     *
     * @return transactionID.
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * Getter confirmedTrans.
     *
     * @return Si la transacción está confirmada.
     */
    public synchronized boolean isConfirmedTrans() {
        return confirmedTrans;
    }

    /**
     * Método que devuelve la información de la transacción como String.
     *
     * @return Información de la transacción.
     */
    public String toString() {
        return "" + toAddress + " sent " + amount + "LD to " + fromAddress + " timestamp : " + timeStamp + " Transaction fee : " + transactionFee + "\n";
    }

}
