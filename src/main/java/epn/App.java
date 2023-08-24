package epn;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String ntpServer = "pool.ntp.org"; // Puedes cambiarlo al servidor NTP de tu elección

        NTPUDPClient ntpClient = new NTPUDPClient();
        try {
            InetAddress inetAddress = InetAddress.getByName(ntpServer);
            TimeInfo timeInfo = ntpClient.getTime(inetAddress);
            long ntpTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();

            long currentTime = System.currentTimeMillis();
            long timeDifference = ntpTime - currentTime;

            System.out.println("Diferencia de tiempo: " + timeDifference + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ntpClient.close();
        }
    }
}
