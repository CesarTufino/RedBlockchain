package pos.blockchainMultipleAleatorio.nodo;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pos.blockchainMultipleAleatorio.blockchain.Blockchain;
import pos.blockchainMultipleAleatorio.blockchain.Bloque;
import pos.blockchainMultipleAleatorio.conexion.Salida;
import pos.blockchainMultipleAleatorio.mensajes.Mensaje;
import pos.blockchainMultipleAleatorio.mensajes.Transaccion;
import utils.*;

public class Nodo {

    private Salida salida;
    private PublicKey clavePublica;
    private PrivateKey clavePrivada;
    private String direccion;
    private final int id;
    private final double TARIFA_TRANSACCION = 0.1;
    private final int DINERO_INICIAL = 100000000;
    private double billetera1;
    private double billetera2;
    private double montoDeApuesta1;
    private double montoDeApuesta2;
    private long tiempoDeApuesta1;
    private long tiempoDeApuesta2;
    private final int TRANSACCIONES_MAXIMAS_POR_BLOQUE = 10;
    private final ArrayList<Transaccion> transaccionesPendientes = new ArrayList<>();
    private final ArrayList<Transaccion> transaccionesFraudulentas = new ArrayList<>();
    private Red red = null;
    private final String TYPE1 = "Type1";
    //private final String TYPE2 = "Type2";

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
        this.billetera1 = DINERO_INICIAL;
        this.billetera2 = DINERO_INICIAL;
        this.montoDeApuesta1 = 0;
        this.montoDeApuesta2 = 0;
        this.salida = new Salida(this);
    }

    public PublicKey getClavePublica() {
        return clavePublica;
    }

    public double getMontoDeApuesta1() {
        return montoDeApuesta1;
    }

    public double getMontoDeApuesta2() {
        return montoDeApuesta2;
    }

    public long getTiempoDeApuesta1() {
        return tiempoDeApuesta1;
    }

    public long getTiempoDeApuesta2() {
        return tiempoDeApuesta2;
    }

    public String getDireccion() {
        return direccion;
    }

    public Red getRed() {
        return red;
    }

    public void setRed(Red infoRed) {
        this.red = infoRed;
    }

    public int getId() {
        return id;
    }

    public void buscarRed() {
        salida.buscarInformacionRed();
    }

    public void enviarDinero(double monto, String direccionDestinatario, String tipo) {
        System.out.println("Inicio de transacción " + tipo);
        if (tipo.equals(TYPE1)) {
            if (billetera1 - monto * (1 + TARIFA_TRANSACCION) < 0) {
                System.out.println("-Transacción rechazada-");
                return;
            }
        } else {
            if (billetera2 - monto * (1 + TARIFA_TRANSACCION) < 0) {
                System.out.println("-Transacción rechazada-");
                return;
            }
        }
        try {
            Transaccion transaccion = new Transaccion(tipo, this.getDireccion(), direccionDestinatario, monto,
                    System.currentTimeMillis(), TARIFA_TRANSACCION, clavePrivada);
            Mensaje mensaje = new Mensaje(this.direccion, direccionDestinatario,
                    RsaUtil.sign(transaccion.toString(), clavePrivada),
                    System.currentTimeMillis(), 0, transaccion);
            // System.out.println("Mensaje creado");
            salida.broadcastMensaje(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recibirDinero(double monto, String tipo) {
        // System.out.println("Cantidad recibida: " + amount);
        if (tipo.equals(TYPE1)) {
            billetera1 += monto;
            // System.out.println("Nuevo valor: " + wallet1);
        } else {
            billetera2 += monto;
            // System.out.println("Nuevo valor: " + wallet2);
        }
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
            actualizarNbTransPorTipo(transaccion.getTipo(), 1);
            // System.out.println("\n///-----------------------------------///");
            // System.out.println("Información de la transacción recibida:");
            // System.out.println(transaccion);
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
                actualizarNBOfBlockOfType(bloque.getTipo());
                System.out.println("\n///-----------------------------------///");
                System.out.println("Información del bloque recibido:");
                // System.out.println(bloque);
                // for (Transaccion t : bloque.getTransaction())
                // System.out.println(t);
                System.out.println("///-----------------------------------///\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizarListaDeTransacciones(Bloque bloque) {
        List<Transaccion> transacciones = bloque.getTransaction();
        for (Transaccion transaccion : transacciones) {
            transaccionesPendientes.remove(transaccion);
            actualizarNbTransPorTipo(transaccion.getTipo(), -1);
        }
    }

    public void generarBloque(String tipo) {
        // System.out.println("---------------------------------------------------");
        List<Transaccion> transaccionesDelBloque = new ArrayList<>();
        for (int i = 0; (i < TRANSACCIONES_MAXIMAS_POR_BLOQUE) && (i < transaccionesPendientes.size()); i++) {
            if (transaccionesPendientes.get(i).getTipo().equals(tipo)) {
                transaccionesDelBloque.add(transaccionesPendientes.get(i));
            }
        }
        long inicioBusqueda = System.nanoTime();
        Blockchain blockchain = red.getBlockchain();
        Bloque bloquePrevioFisico = blockchain.obtenerUltimoBloque();
        Bloque bloquePrevioLogico = blockchain.buscarBloquePrevioLogico(tipo,
                blockchain.obtenerCantidadDeBloques() - 1);
        long finBusqueda = System.nanoTime();
        Bloque bloque = new Bloque(bloquePrevioFisico, bloquePrevioLogico, transaccionesDelBloque,
                (double) (finBusqueda - inicioBusqueda), tipo);
        bloque.setIdNodo(this.id);
        bloque.setDireccionNodo(this.direccion);
        // System.out.println("Block has been forged by " + this.name);
        try {

            List<Object> contenidoMensaje = new ArrayList<>();
            contenidoMensaje.add(bloque);
            // messageContent.add(blockchain);
            Mensaje mensaje = new Mensaje(this.direccion, "ALL",
                    RsaUtil.sign(HashUtil.SHA256(bloque.toString()), this.clavePrivada),
                    System.currentTimeMillis(),
                    1, contenidoMensaje);
            salida.broadcastMensaje(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("---------------------------------------------------");
    }

    public void apostar(double monto, String tipo) {
        if (tipo.equals(TYPE1)) {
            if (billetera1 < monto) {
                // System.out.println(name + " don't have enough money for stake in wallet1");
            }
            montoDeApuesta1 = monto;
            billetera1 -= monto;
            tiempoDeApuesta1 = System.currentTimeMillis();
        } else {
            if (billetera2 < monto) {
                // System.out.println(name + " don't have enough money for stake in wallet2");
            }
            montoDeApuesta2 = monto;
            billetera2 -= monto;
            tiempoDeApuesta2 = System.currentTimeMillis();
        }
        System.out.println(id + " deposita " + monto + "  como apuesta para " + tipo);
    }

    public void enviarInfoRed(String direccion) {
        salida.enviarInfoRed(red, direccion);
    }

    public void actualizarNbTransPorTipo(String tipo, int cantidad) {
        HashMap<String, Integer> nbTransParType = (HashMap<String, Integer>) red.getNbTransParType();
        red.setNbTransParType(tipo, nbTransParType.get(tipo) + cantidad);
    }

    public void actualizarST(double st) {
        red.searchTimes.add(st);
    }

    public void actualizarNBOfBlockOfType(String tipo) {
        if (tipo.equals(TYPE1)) {
            red.NB_OF_BLOCK_OF_TYPE1_CREATED.add(red.NB_OF_BLOCK_OF_TYPE1_CREATED.size() + 1);
        } else {
            red.NB_OF_BLOCK_OF_TYPE2_CREATED.add(red.NB_OF_BLOCK_OF_TYPE2_CREATED.size() + 1);
        }
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
        String tipo = bloque.getTipo();
        for (Transaccion transaccion : transacciones) {
            transaccion.confirmar();
            double montoTransaccion = transaccion.getMonto();
            double tarifaTransaccion = transaccion.getTarifa() * montoTransaccion;
            String toAddress = transaccion.getDireccionDestinatario();
            montoTotal += montoTransaccion;
            totalFee += tarifaTransaccion;
            // Actualización de la billetera del destinatario de la transacción.
            if (toAddress.equals(direccion)) {
                recibirDinero(montoTransaccion, tipo);
            }
            // Actualización de la billetera del emisor de la transacción.
            if (transaccion.getDireccionRemitente().equals(direccion)) {
                recibirDinero(-(montoTransaccion + tarifaTransaccion), tipo);
            }
        }

        // Actualización del minero.
        if (bloque.getDireccionNodo().equals(direccion)) {
            recibirDinero(totalFee, tipo);
        }
        actualizarExchangeMoneyPorTipo(tipo, montoTotal);
    }

    public void actualizarExchangeMoneyPorTipo(String tipo, double amount) {
        if (tipo.equals(TYPE1)) {
            red.exchangeMoney1.add(amount);
            red.exchangeMoney2.add(0.);
        } else {
            red.exchangeMoney1.add(0.);
            red.exchangeMoney2.add(amount);
        }
    }
}
