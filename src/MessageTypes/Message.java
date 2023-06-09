package MessageTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase Message.
 */
public class Message {

    /**
     * Dirección del emisor del mensaje.
     */
    private final String fromAddress;
    /**
     * Dirección del destinatario del mensaje.
     */
    private final String toAddress;
    /**
     * Fecha de creación del mensaje.
     */
    private final long timeStamp;
    /**
     * Firma del bloque.
     */
    private final String signature;
    /**
     * Contenido del mensaje.
     */
    private final List<Object> messageContent = new ArrayList<>();
    /**
     * Tipo del contenido del mensaje.
     * 0 -> Transaction, 1 -> Block.
     */
    private final int type;

    /**
     * Constructor del Message.
     *
     * @param fromAddress Dirección del emisor del mensaje.
     * @param toAddress Dirección del destinatario del mensaje.
     * @param signature Firma del bloque.
     * @param timeStamp Fecha de creación del mensaje.
     * @param messageType Tipo del contenido del mensaje.
     * @param obj Contenido del mensaje.
     */
    @SuppressWarnings("unchecked") 
    public Message(String fromAddress, String toAddress, String signature, long timeStamp, int messageType, Object obj) { //Firma del bloque
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.signature = signature;
        this.timeStamp = timeStamp;
        this.type = messageType;
        if (messageType == 1) {
            if (obj instanceof List) {
                this.messageContent.add(((List<Object>) obj).get(0));
                this.messageContent.add(((List<Object>) obj).get(1));
            }

        } else {
            this.messageContent.add(obj);
        }
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
     * Getter timeStamp.
     *
     * @return timeStamp.
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Getter type.
     *
     * @return typ.
     */
    public int getType() {
        return type;
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
     * Getter messageContent.
     *
     * @return messageContent.
     */
    public List<Object> getMessageContent() {
        return messageContent;
    }
}
