package blockchainTradicional.nodo;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;

public class Validador3 extends Thread {

    private Red red = null;
    private Nodo miNodo = null;
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_BLUE = "\u001B[34m";
    private final String ANSI_RESET = "\u001B[0m";
    private String ntpServer = "pool.ntp.org";
    private NTPUDPClient ntpClient = new NTPUDPClient();
    private InetAddress inetAddress;
    private TimeInfo timeInfo;

    public Validador3(Red infoRed, Nodo miNodo) {
        this.red = infoRed;
        this.miNodo = miNodo;
        try {
            this.inetAddress = InetAddress.getByName(ntpServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                timeInfo = ntpClient.getTime(inetAddress);
                long actualTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                long tiempoParaIniciar = 10000 - (actualTime % 10000);
                Thread.sleep(tiempoParaIniciar);

                System.out.println("Bandera");
                Seleccionador hiloSeleccionador = new Seleccionador(miNodo.getRed(), miNodo);
                hiloSeleccionador.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
