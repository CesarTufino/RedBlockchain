package blockchainTradicional.nodo;

import java.net.InetAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import blockchainTradicional.conexion.Salida;
import blockchainTradicional.blockchain.*;
import blockchainTradicional.mensajes.Mensaje;
import blockchainTradicional.mensajes.Transaccion;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import utils.*;

public class Nodo {

    private Salida salida;
    private PublicKey clavePublica;
    private PrivateKey clavePrivada;
    private String direccion;
    private int id;
    private final double TARIFA_TRANSACCION = 0.1;
    private final int DINERO_INICIAL = 100000000;
    private double billetera;
    private double montoDeApuesta;
    private long tiempoDeApuesta;
    private final int TRANSACCIONES_MAXIMAS_POR_BLOQUE = 10;
    private ArrayList<Transaccion> transaccionesPendientes = new ArrayList<>();
    private ArrayList<Transaccion> transaccionesFraudulentas = new ArrayList<>();
    private Red red = null;
    private final String TYPE1 = "Type1";
    private String ntpServer = "pool.ntp.org";
    private NTPUDPClient ntpClient = new NTPUDPClient();
    private InetAddress inetAddress;
    private TimeInfo timeInfo;

    public Nodo(int id, String direccion) {
        KeyPair keys = null;
        try {
            keys = RsaUtil.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.id = id;
        this.clavePublica = keys.getPublic();
        this.clavePrivada = keys.getPrivate();
        this.direccion = direccion;
        this.billetera = DINERO_INICIAL;
        this.montoDeApuesta = 0;
        this.salida = new Salida(this);
        try {
            this.inetAddress = InetAddress.getByName(ntpServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PublicKey getClavePublica() {
        return clavePublica;
    }

    public double getMontoDeApuesta() {
        return montoDeApuesta;
    }

    public long getTiempoDeApuesta() {
        return tiempoDeApuesta;
    }

    public String getDireccion() {
        return direccion;
    }

    public Red getRed() {
        return red;
    }

    public int getId() {
        return id;
    }

    public void setRed(Red infoRed) {
        this.red = infoRed;
    }

    public void buscarRed() {
        salida.buscarInformacionRed();
    }

    public void enviarDinero(double monto, String direccionDestinatario) {
        System.out.println("Inicio de transacción");
        if (billetera - monto * (1 + TARIFA_TRANSACCION) < 0) {
            System.out.println("-Transacción rechazada-");
            return;
        }

        try {
            timeInfo = ntpClient.getTime(inetAddress);
            long actualTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            Transaccion transaccion = new Transaccion(direccion, direccionDestinatario, monto,
                    actualTime, TARIFA_TRANSACCION, clavePrivada);
            Mensaje mensaje = new Mensaje(direccion, direccionDestinatario, RsaUtil.sign(transaccion.toString(), clavePrivada),
                    actualTime, 0, transaccion);
            // System.out.println("Mensaje creado");
            salida.broadcastMensaje(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recibirDinero(double monto) {
        billetera += monto;
    }

    public void recibirMensaje(Mensaje mensaje) {
        int tipoDeMensaje = mensaje.getTipo();
        List<Object> contenido = mensaje.getContenido();
        if (tipoDeMensaje == 0) {
            // System.out.println("Transaccion recibida");
            Transaccion transaccion = (Transaccion) (contenido.get(0));
            recibirTransaccion(transaccion);
        }
        if (tipoDeMensaje == 1) {
            // System.out.println("Bloque recibido");
            Bloque bloque = (Bloque) contenido.get(0);
            String direccionDelNodo = mensaje.getDireccionRemitente();
            String firma = mensaje.getFirma();
            recibirBloque(bloque, firma, direccionDelNodo);
        }
    }

    public synchronized void recibirTransaccion(Transaccion transaccion) {
        boolean estadoDeLaTransaccion = false;
        try {
            estadoDeLaTransaccion = verificarTransaccion(transaccion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (estadoDeLaTransaccion) {
            transaccionesPendientes.add(transaccion);
            actualizarNbTrans(1);
            // System.out.println("\n///-----------------------------------///");
            // System.out.println("Información de la transacción recibida:");
            // System.out.println(t);
            // System.out.println("///-----------------------------------///\n");
        } else {
            transaccionesFraudulentas.add(transaccion);
        }
    }

    public boolean verificarTransaccion(Transaccion transaccion) throws Exception {
        return RsaUtil.verify(transaccion.toString(), transaccion.getFirma(),
                red.obtenerClavePublicaPorDireccion(transaccion.getDireccionRemitente()));
    }

    public synchronized void recibirBloque(Bloque bloque, String firma, String direccionDelNodo) {
        actualizarListaDeTransacciones(bloque);
        try {
            if (RsaUtil.verify(HashUtil.SHA256(bloque.toString()), firma,
                    red.obtenerClavePublicaPorDireccion(direccionDelNodo))) {
                red.agregarBloque(bloque);
                if (!bloque.getDireccionNodo().equals("Master"))
                    updateAllWallet(bloque);
                actualizarST(bloque.getTiempoDeBusqueda());
                actualizarNBOfBlock();
                // System.out.println("\n///-----------------------------------///");
                // System.out.println("Información del bloque recibido:");
                // System.out.println(b);
                // for (Transaccion t : b.getTransaction()) System.out.println(t);
                // System.out.println("///-----------------------------------///\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizarListaDeTransacciones(Bloque bloque) {
        List<Transaccion> transacciones = bloque.getTransaction();
        for (Transaccion transaccion : transacciones) {
            transaccionesPendientes.remove(transaccion);
            actualizarNbTrans(-1);
        }
    }

    public void generarBloque() {
        // System.out.println("---------------------------------------------------");
        List<Transaccion> transaccionesDelBloque = new ArrayList<>();
        for (int i = 0; (i < TRANSACCIONES_MAXIMAS_POR_BLOQUE) && (i < transaccionesPendientes.size()); i++) {
            transaccionesDelBloque.add(transaccionesPendientes.get(i));
        }
        long inicioBusqueda = System.nanoTime();
        Bloque bloquePrevio = red.getBlockchain().obtenerUltimoBloque();
        long finBusqueda = System.nanoTime();
        Bloque bloque = new Bloque(bloquePrevio, transaccionesDelBloque, (double) (finBusqueda - inicioBusqueda));
        bloque.setIdNodo(this.id);
        bloque.setDireccionNodo(this.direccion);
        // System.out.println("Block has been forged by " + this.name);
        try {

            List<Object> contenidoMensaje = new ArrayList<>();
            contenidoMensaje.add(bloque);
            timeInfo = ntpClient.getTime(inetAddress);
            long actualTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            // messageContent.add(blockchain);
            Mensaje mensaje = new Mensaje(this.direccion, "ALL",
                    RsaUtil.sign(HashUtil.SHA256(bloque.toString()), this.clavePrivada),
                    actualTime,
                    1, contenidoMensaje);
            salida.broadcastMensaje(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error signing");
        }
        // System.out.println("---------------------------------------------------");
    }

    public void apostar(double monto) {
        long actualTime = 0;
        try {
            timeInfo = ntpClient.getTime(inetAddress);
            actualTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (billetera < monto) {
            System.out.println(id + " no tiene suficiente dinero para apostar en wallet1");
            return;
        }
        montoDeApuesta = monto;
        billetera -= monto;
        tiempoDeApuesta = actualTime;
        System.out.println(id + " deposita " + monto + " como apuesta");
    }

    public void enviarInfoRed(String direccion) {
        salida.enviarInfoRed(red, direccion);
    }

    public void actualizarNbTrans(int cantidad) {
        HashMap<String, Integer> nbTrans = (HashMap<String, Integer>) red.getNbTrans();
        red.setNbTrans(nbTrans.get(TYPE1) + cantidad);
    }

    public void actualizarST(double st) {
        red.searchTimes.add(st);
    }

    public void actualizarNBOfBlock() {
        red.NB_OF_BLOCK_OF_TYPE1_CREATED.add(red.NB_OF_BLOCK_OF_TYPE1_CREATED.size() + 1);
        // Último en ejecutarse

    }

    public boolean comprobarCantidadMinimaDeNodos() {
        // System.out.println(infoRed.comprobarCantidadDeNodos());
        if (red.obtenerCantidadDeNodos() >= 3) {
            return true;
        }
        return false;
    }

    /**
     * Método que actualiza las billeteras de todos los nodos que participaron.
     *
     * @param bloque Bloque.
     */
    private void updateAllWallet(Bloque bloque) {
        double totalFee = 0;
        List<Transaccion> transacciones = bloque.getTransaction();
        double montoTotal = 0;
        for (Transaccion transaccion : transacciones) {
            transaccion.confirmar();
            double montoTransaccion = transaccion.getMonto();
            double tarifaTransaccion = transaccion.getTarifa() * montoTransaccion;
            String toAddress = transaccion.getDireccionDestinatario();
            montoTotal += montoTransaccion;
            totalFee += tarifaTransaccion;
            // Actualización de la billetera del destinatario de la transacción.
            if (toAddress.equals(direccion)) {
                recibirDinero(montoTransaccion);
            }
            // Actualización de la billetera del emisor de la transacción.
            if (transaccion.getDireccionRemitente().equals(direccion)) {
                recibirDinero(-(montoTransaccion + tarifaTransaccion));
            }
        }
        // Actualización del minero.
        if (bloque.getDireccionNodo().equals(direccion)) {
            recibirDinero(totalFee);
        }
        actualizarExchangeMoney(montoTotal);
    }

    public void actualizarExchangeMoney(double monto) {
        red.exchangeMoney1.add(monto);
    }
}
