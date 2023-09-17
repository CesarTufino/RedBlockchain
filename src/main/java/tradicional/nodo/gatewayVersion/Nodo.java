package tradicional.nodo.gatewayVersion;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import constantes.Direccion;
import tradicional.blockchain.BloqueTradicional;
import tradicional.conexion.Entrada;
import tradicional.conexion.Salida;
import tradicional.mensajes.InfoNodo;
import tradicional.mensajes.Mensaje;
import tradicional.mensajes.Transaccion;
import utils.*;

public class Nodo {

    private Salida salida;
    private PublicKey clavePublica;
    private PrivateKey clavePrivada;
    private Direccion direccion;
    private int id;
    private final double TARIFA_TRANSACCION = 0.1;
    private final int DINERO_INICIAL = 100000000;
    private double billetera;
    private Red red = null;
    private List<BloqueTradicional> bloquesEnEspera = new ArrayList<>();

    public Nodo(int id, Direccion direccion) {
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
        this.salida = new Salida();
    }

    public PublicKey getClavePublica() {
        return clavePublica;
    }

    public Direccion getDireccion() {
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
        for (Direccion direccionNodo : Direccion.getNodos()) {
            if (!direccionNodo.getDireccionIP().equals(direccion.getDireccionIP())) {
                salida.pedirRed(direccionNodo.getDireccionIP(), direccionNodo.getPuerto(), direccion);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            if (getRed() != null) {
                break;
            }
        }
        InfoNodo infoNodo = new InfoNodo(direccion.getDireccionIP(), clavePublica, direccion.getPuerto());
        if (red == null) {
            System.out.println("No se pudo copiar un Informacion de la Red");
            System.out.println("Se crea la Informacion de la Red");
            red = new Red();
        } else {
            System.out.println("Copia de InfoRed creada");
            salida.broadcastInformacionNodo(red.getPuertos(), infoNodo);
        }
        red.addNode(infoNodo);
        salida.enviarInfoNodo(Direccion.DIRECCION_GATEWAY.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getPuerto(), infoNodo);
    }

    public void enviarInfoRed(Direccion direccion) {
        salida.enviarInfoRed(red, direccion.getDireccionIP(), direccion.getPuerto());
    }

    public void enviarDinero(double monto, String direccionDestinatario) {
        System.out.println("Inicio de transacción");
        if (billetera - monto * (1 + TARIFA_TRANSACCION) < 0) {
            System.out.println("-Transacción rechazada-");
            return;
        }
        try {
            Transaccion transaccion = new Transaccion(direccion.getDireccionIP(), direccionDestinatario, monto,
                    System.currentTimeMillis(), TARIFA_TRANSACCION, clavePrivada);
            Mensaje mensaje = new Mensaje(direccion.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getDireccionIP(), RsaUtil.sign(transaccion.toString(), clavePrivada),
                    System.currentTimeMillis(), 0, transaccion);
            // System.out.println("Mensaje creado");
            salida.enviarMensaje(Direccion.DIRECCION_GATEWAY.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getPuerto(), mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recibirDinero(double monto) {
        billetera += monto;
    }

    public synchronized void recibirMensaje(Mensaje mensaje) {
        int tipoDeMensaje = mensaje.getTipoDeContenido();
        List<Object> contenido = mensaje.getContenido();
        if (tipoDeMensaje == 1) {
            // System.out.println("Bloque recibido");
            BloqueTradicional bloqueTradicional = (BloqueTradicional) contenido.get(0);
            String direccionDelNodo = mensaje.getDireccionRemitente();
            String firma = mensaje.getFirma();
            recibirBloque(bloqueTradicional, firma, direccionDelNodo);
        }
    }

    public boolean verificarTransaccion(Transaccion transaccion) throws Exception {
        return RsaUtil.verify(transaccion.toString(), transaccion.getFirma(),
                red.obtenerClavePublicaPorDireccion(transaccion.getDireccionRemitente()));
    }

    public synchronized void recibirBloque(BloqueTradicional bloqueTradicional, String firma, String direccionDelNodo) {
        try {
            if (RsaUtil.verify(HashUtil.SHA256(bloqueTradicional.toString()), firma,
                    red.obtenerClavePublicaPorDireccion(direccionDelNodo))) {
                bloquesEnEspera.add(bloqueTradicional);
                System.out.println("Bloque recibido");
                if (bloquesEnEspera.size() == 2) {
                    compararBloques();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compararBloques() {
        BloqueTradicional primerBloqueTradicional = bloquesEnEspera.get(0);
        BloqueTradicional segundoBloqueTradicional = bloquesEnEspera.get(1);
        if (primerBloqueTradicional.getFooter().getHash().equals(segundoBloqueTradicional.getFooter().getHash())) {
            System.out.println("Creación correcta");
            if (primerBloqueTradicional.getIdNodoMinero() > segundoBloqueTradicional.getIdNodoMinero()) {
                red.getNodosEscogidos1().add(segundoBloqueTradicional.getIdNodoMinero());
                red.getNodosEscogidos2().add(primerBloqueTradicional.getIdNodoMinero());
                agregarBloque(segundoBloqueTradicional);
            } else {
                red.getNodosEscogidos1().add(primerBloqueTradicional.getIdNodoMinero());
                red.getNodosEscogidos2().add(segundoBloqueTradicional.getIdNodoMinero());
                agregarBloque(primerBloqueTradicional);
            }
        } else {
            System.out.println("---------------ERROR--------------");
        }
        bloquesEnEspera = new ArrayList<>();
        System.out.println(red.getStats());
    }

    public synchronized void agregarBloque(BloqueTradicional bloqueTradicional) {
        red.agregarBloque(bloqueTradicional);
        if (!bloqueTradicional.getDireccionNodoMinero().equals("Master"))
            updateAllWallet(bloqueTradicional);
        actualizarST(bloqueTradicional.getTiempoDeBusqueda());
        actualizarNBOfBlock();
        System.out.println("\n///-----------------------------------///");
        System.out.println("Bloque agregado");
        System.out.println("///-----------------------------------///\n");
    }

    public void generarBloque(List<Transaccion> transaccionesDelBloque) throws Exception {
        // System.out.println("---------------------------------------------------");
        for (Transaccion transaccion : transaccionesDelBloque) {
            if (!verificarTransaccion(transaccion)) {
                System.out.println("---------------ERROR--------------");
                return;
            }
        }
        System.out.println("---------------Se crea bloque---------------");
        long inicioBusqueda = System.nanoTime();
        BloqueTradicional bloqueTradicionalPrevio = red.getBlockchain().obtenerUltimoBloque();
        long finBusqueda = System.nanoTime();
        long tiempoDelUltimoBloque = bloqueTradicionalPrevio.getHeader().getMarcaDeTiempo();
        while (true) {
            if (System.currentTimeMillis() - tiempoDelUltimoBloque > 10000) { // Garantiza los 10 segundos minimos
                break;
            }
            System.out.print("");
        }
        BloqueTradicional bloqueTradicional = new BloqueTradicional(bloqueTradicionalPrevio, transaccionesDelBloque, (double) (finBusqueda - inicioBusqueda));
        bloqueTradicional.setIdNodoMinero(this.id);
        bloqueTradicional.setDireccionNodoMinero(this.direccion.getDireccionIP());
        // System.out.println("Block has been forged by " + this.name);
        try {
            List<Object> contenidoMensaje = new ArrayList<>();
            contenidoMensaje.add(bloqueTradicional);
            Mensaje mensaje = new Mensaje(this.direccion.getDireccionIP(), "ALL",
                    RsaUtil.sign(HashUtil.SHA256(bloqueTradicional.toString()), this.clavePrivada),
                    System.currentTimeMillis(),
                    1, contenidoMensaje);
            System.out.println("Envio de bloque");
            salida.broadcastMensaje(mensaje, red.getPuertos());
            salida.enviarMensaje(Direccion.DIRECCION_GATEWAY.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getPuerto(), mensaje);
            System.out.println("Fin de envio de bloque");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error signing");
        }
        // System.out.println("---------------------------------------------------");
    }

    public void actualizarST(double st) {
        red.searchTimes.add(st);
    }

    public void actualizarNBOfBlock() {
        red.NB_OF_BLOCK_CREATED.add(red.NB_OF_BLOCK_CREATED.size() + 1);
        // Último en ejecutarse

    }

    public boolean comprobarCantidadMinimaDeNodos() {
        if (red.obtenerCantidadDeNodos() >= 3) {
            return true;
        }
        return false;
    }

    /**
     * Método que actualiza las billeteras de todos los nodos que participaron.
     *
     * @param bloqueTradicional Bloque.
     */
    private void updateAllWallet(BloqueTradicional bloqueTradicional) {
        double totalFee = 0;
        List<Transaccion> transacciones = bloqueTradicional.getTransaction();
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
        if (bloqueTradicional.getDireccionNodoMinero().equals(direccion)) {
            recibirDinero(totalFee);
        }
        actualizarExchangeMoney(montoTotal);
    }

    public void actualizarExchangeMoney(double monto) {
        red.exchangeMoney1.add(monto);
    }

}
