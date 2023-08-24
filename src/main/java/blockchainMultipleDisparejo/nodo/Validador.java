package blockchainMultipleDisparejo.nodo;

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
    private final String type1 = "Type1";
    private final String type2 = "Type2";
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

                if (seleccionados[0] != null && seleccionados[0].equals(miNodo.getDireccion())) {
                    // Garantiza los 10 segundos minimos
                    lastBlockTime = red.getBlockchain()
                            .buscarBloquePrevioLogico(type1, red.getBlockchain().obtenerCantidadDeBloques() - 1)
                            .getHeader().getMarcaDeTiempo();
                    while (true) {
                        timeInfo = ntpClient.getTime(inetAddress);
                        actualTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                        if (actualTime - lastBlockTime > 10000) {
                            break;
                        }
                    }

                    System.out.println(ANSI_GREEN + "/////////////// Se crea el Bloque Tipo 1 ////////////" + ANSI_RESET);
                    miNodo.generarBloque(type1);
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (seleccionados[1] != null && seleccionados[1].equals(miNodo.getDireccion())) {
                    // Garantiza los 10 segundos minimos
                    lastBlockTime = red.getBlockchain()
                            .buscarBloquePrevioLogico(type2, red.getBlockchain().obtenerCantidadDeBloques() - 1)
                            .getHeader().getMarcaDeTiempo();
                    while (true) {
                        timeInfo = ntpClient.getTime(inetAddress);
                        actualTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                        if (actualTime - lastBlockTime > 10000) {
                            break;
                        }
                    }
                    System.out.println(ANSI_GREEN + "/////////////// Se crea el Bloque Tipo 2 ////////////" + ANSI_RESET);
                    miNodo.generarBloque(type2);
                }

                timeInfo = ntpClient.getTime(inetAddress);
                actualTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                long tiempoParaContinuar = 10000 - (actualTime % 10000);
                Thread.sleep(tiempoParaContinuar);
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
        if (red.NB_OF_BLOCK_OF_TYPE1_CREATED.size() + red.NB_OF_BLOCK_OF_TYPE2_CREATED.size() > 201) {
            if (miNodo.getId() == 1) {
                try {
                    BufferedWriter archivo = new BufferedWriter(
                            new FileWriter("Blockchain V3 (Multiple Disparejo) - Resultado.txt", true));
                    archivo.write(red.getStats());
                    archivo.newLine();
                    archivo.close();
                    System.out.println("Archivo guardado");
                } catch (IOException e) {
                }
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
