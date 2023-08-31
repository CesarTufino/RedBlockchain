package gatewayVersion.blockchainTradicional.nodo;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import direcciones.Direccion;
import gatewayVersion.blockchainTradicional.blockchain.Bloque;
import gatewayVersion.blockchainTradicional.conexion.Entrada;
import gatewayVersion.blockchainTradicional.conexion.Salida;
import gatewayVersion.blockchainTradicional.mensajes.InfoNodo;
import gatewayVersion.blockchainTradicional.mensajes.Mensaje;
import gatewayVersion.blockchainTradicional.mensajes.Transaccion;
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
    private Red red = null;
    private int puertoRecepcion;
    private String direccionGateway = "";
    private int puertoGateway = 12344;
    private List<Bloque> bloquesEnEspera = new ArrayList<>();
    private final String TYPE1 = "Type1";

    public Nodo(int id, String direccion, int puertoRecepcion) {
        try {
            KeyPair keys = RsaUtil.generateKeyPair();
            this.clavePublica = keys.getPublic();
            this.clavePrivada = keys.getPrivate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.id = id;
        this.direccion = direccion;
        this.billetera = DINERO_INICIAL;
        this.puertoRecepcion = puertoRecepcion;
        this.salida = new Salida();
    }

    public PublicKey getClavePublica() {
        return clavePublica;
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

    public void iniciarProceso() throws IOException {
        // Hilo para escuchar
        Entrada serverThread = new Entrada(this, puertoRecepcion);
        serverThread.start();
        // Buscar datos en la red
        buscarRed();
    }

    public void buscarRed() {
        for (String direccionNodo : Direccion.getDirecciones()) {
            salida.pedirRed(direccionNodo, direccion);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (red != null) {
                break;
            }
        }
        InfoNodo infoNodo = new InfoNodo(direccion, clavePublica, puertoRecepcion);
        if (red == null) {
            System.out.println("No se pudo copiar un Informacion de la Red");
            System.out.println("Se crea la Informacion de la Red");
            red = new Red();
            red.addNode(infoNodo);
        } else {
            System.out.println("Copia de InfoRed creada");
            salida.enviarInfoNodo(direccionGateway,puertoGateway,infoNodo);
            salida.broadcastInformacionNodo(red.getPuertos(), infoNodo);
        }
    }

    public void enviarInfoRed(String direccion) {
        salida.enviarInfoRed(red, direccion);
    }

    public void enviarDinero(double monto, String direccionDestinatario) {
        System.out.println("Inicio de transacción");
        if (billetera - monto * (1 + TARIFA_TRANSACCION) < 0) {
            System.out.println("-Transacción rechazada-");
            return;
        }
        try {
            Transaccion transaccion = new Transaccion(direccion, direccionDestinatario, monto,
                    System.currentTimeMillis(), TARIFA_TRANSACCION, clavePrivada);
            Mensaje mensaje = new Mensaje(direccion, direccionDestinatario, RsaUtil.sign(transaccion.toString(), clavePrivada),
                    System.currentTimeMillis(), 0, transaccion);
            // System.out.println("Mensaje creado");
            salida.enviarMensaje(direccionGateway, puertoGateway, mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recibirDinero(double monto) {
        billetera += monto;
    }

    public synchronized void recibirMensaje(Mensaje mensaje) {
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
            //transaccionesPendientes.add(transaccion);
            actualizarNbTrans(1);
            // System.out.println("\n///-----------------------------------///");
            // System.out.println("Información de la transacción recibida:");
            // System.out.println(t);
            // System.out.println("///-----------------------------------///\n");
        } else {
            //transaccionesFraudulentas.add(transaccion);
        }
    }

    public boolean verificarTransaccion(Transaccion transaccion) throws Exception {
        return RsaUtil.verify(transaccion.toString(), transaccion.getFirma(),
                red.obtenerClavePublicaPorDireccion(transaccion.getDireccionRemitente()));
    }

    public synchronized void recibirBloque(Bloque bloque, String firma, String direccionDelNodo) {
        //actualizarListaDeTransacciones(bloque);
        try {
            if (RsaUtil.verify(HashUtil.SHA256(bloque.toString()), firma,
                    red.obtenerClavePublicaPorDireccion(direccionDelNodo))) {
                bloquesEnEspera.add(bloque);
                if (bloquesEnEspera.size()==2){
                    compararBloques();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compararBloques() {
        if (bloquesEnEspera.get(0).getFooter().getHash().equals(bloquesEnEspera.get(1).getFooter().getHash())){
            agregarBloque(bloquesEnEspera.get(0));
            System.out.println("Creación correcta");
        } else{
            System.out.println("---------------ERROR--------------");
        }
        bloquesEnEspera = new ArrayList<>();
    }

    public synchronized void agregarBloque(Bloque bloque) {
        red.agregarBloque(bloque);
        if (!bloque.getDireccionNodo().equals("Master"))
            updateAllWallet(bloque);
        actualizarST(bloque.getTiempoDeBusqueda());
        actualizarNBOfBlock();
        System.out.println("\n///-----------------------------------///");
        System.out.println("Bloque recibido:");
        System.out.println("///-----------------------------------///\n");
    }

    public void generarBloque(List<Transaccion> transaccionesDelBloque) throws Exception {
        // System.out.println("---------------------------------------------------");
        for (Transaccion transaccion : transaccionesDelBloque){
            if (!verificarTransaccion(transaccion)){
                System.out.println("---------------ERROR--------------");
                return;
            }
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
            Mensaje mensaje = new Mensaje(this.direccion, "ALL",
                    RsaUtil.sign(HashUtil.SHA256(bloque.toString()), this.clavePrivada),
                    System.currentTimeMillis(),
                    1, contenidoMensaje);
            salida.broadcastMensaje(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error signing");
        }
        // System.out.println("---------------------------------------------------");
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
