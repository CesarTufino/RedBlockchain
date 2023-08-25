package blockchainTradicional.blockchain;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Clase Header.
 */
public class Header  implements Serializable{

    protected final String hashBloquePrevio;
    /**
     * Fecha de creación del bloque.
     */
    private final long marcaDeTiempo;
    private String ntpServer = "pool.ntp.org";
    private NTPUDPClient ntpClient = new NTPUDPClient();
    private InetAddress inetAddress;
    private TimeInfo timeInfo;

    /**
     * Constructor del Header.
     * Utilizado para el header del primer bloque.
     */
    public Header() {
        marcaDeTiempo = System.currentTimeMillis();
        hashBloquePrevio = "";
    }

    /**
     * Constructor del Header.
     * Utilizado para todos los bloques excepto el primero.
     *
     * @param hashBloquePrevio Hash del header del último bloque.
     */
    public Header(String hashBloquePrevio) {
        long actualtime = 0;
        try {
            this.inetAddress = InetAddress.getByName(ntpServer);
            timeInfo = ntpClient.getTime(inetAddress);
            actualtime = timeInfo.getMessage().getTransmitTimeStamp().getTime(); // Get the current date
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.marcaDeTiempo = actualtime;
        this.hashBloquePrevio = hashBloquePrevio; // Get header's hash
    }

    public String getHashBloquePrevio() {
        return hashBloquePrevio;
    }

    public long getMarcaDeTiempo(){
        return this.marcaDeTiempo;
    }

    @Override
    public String toString() {
        return "\nTS : " + marcaDeTiempo + "\nPrevBlockHash :" + this.hashBloquePrevio;
    }
}
