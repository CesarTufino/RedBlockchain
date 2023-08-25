package blockchainTradicional.nodo;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;

public class Validador extends Thread {

    private Red red = null;
    private Nodo miNodo = null;
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_BLUE = "\u001B[34m";
    private final String ANSI_RESET = "\u001B[0m";
    private String ntpServer = "pool.ntp.org";
    private NTPUDPClient ntpClient = new NTPUDPClient();
    private InetAddress inetAddress;
    private TimeInfo timeInfo;

    public Validador(Red infoRed, Nodo miNodo) {
        this.red = infoRed;
        this.miNodo = miNodo;
        try {
            this.inetAddress = InetAddress.getByName(ntpServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void validar() {
        try {
            long lastBlockTime;
            long actualTime;
            while (true) {
                imprimirInformacion();

                String[] seleccionados = determinarSeleccionadosPoS();

                if (seleccionados[0].equals(miNodo.getDireccion())) {
                    lastBlockTime = red.getBlockchain().obtenerUltimoBloque().getHeader().getMarcaDeTiempo();
                    while (true) {
                        timeInfo = ntpClient.getTime(inetAddress);
                        actualTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                        if (actualTime - lastBlockTime > 10000) { // Garantiza los 10 segundos minimos
                            break;
                        }
                    }
                    System.out.println(ANSI_GREEN + "/////////////// Se crea el Bloque Tipo 1 ////////////" + ANSI_RESET);
                    miNodo.generarBloque();
                }

                timeInfo = ntpClient.getTime(inetAddress);
                actualTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                long tiempoParaContinuar = 10000 - (actualTime % 10000);
                Thread.sleep(tiempoParaContinuar);
                System.out.println("Bandera");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] determinarSeleccionadosPoS() {
        System.out.println(ANSI_BLUE + "/////////////// Seleccionando nodos //////////" + ANSI_RESET);
        return red.getNodosSeleccionados();
    }

    private void imprimirInformacion() {
        System.out.println(red.getStats());
        if (red.NB_OF_BLOCK_OF_TYPE1_CREATED.size() > 20) {
            try {
                BufferedWriter archivo = new BufferedWriter(
                        new FileWriter("Blockchain V1 (Tradicional) - Resultado.txt", true));
                archivo.write(red.getStats());
                archivo.newLine();
                archivo.close();
                System.out.println("Archivo guardado");
            } catch (IOException e) {
            }
            System.exit(0);
        }
    }

    @Override
    public void run() {
        try {
            timeInfo = ntpClient.getTime(inetAddress);
            long actualTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            long tiempoParaIniciar = 10000 - (actualTime % 10000);
            Thread.sleep(tiempoParaIniciar);
            validar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
