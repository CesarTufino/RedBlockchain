package multiple.nodo.gatewayVersion;

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
import constantes.MaximoDeBloques;
import constantes.Tipo;
import multiple.blockchain.Bloque;
import multiple.conexion.Salida;
import multiple.mensajes.InfoNodo;
import multiple.mensajes.Mensaje;
import multiple.mensajes.Paquete;
import multiple.mensajes.Transaccion;
import multiple.blockchain.BlockchainMultiple;
import utils.*;

public class Nodo {

    private Salida salida;
    private PublicKey clavePublica;
    private PrivateKey clavePrivada;
    private Direccion direccion;
    private int id;
    private final double TARIFA_TRANSACCION = 0.1;
    private final int DINERO_INICIAL = 100000000;
    private double billetera1;
    private double billetera2;
    private Red red = null;
    private HashMap<Tipo, List<Bloque>> bloquesEnEspera = new HashMap<>();

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
        this.billetera1 = DINERO_INICIAL;
        this.billetera2 = DINERO_INICIAL;
        bloquesEnEspera.put(Tipo.LOGICO1, new ArrayList<>());
        bloquesEnEspera.put(Tipo.LOGICO2, new ArrayList<>());
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

    public void enviarDinero(double monto, String direccionDestinatario, Tipo tipo) {
        System.out.println("Inicio de transacción");
        if (tipo.equals(Tipo.LOGICO1)) {
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
            Transaccion transaccion = new Transaccion(tipo, direccion.getDireccionIP(), direccionDestinatario, monto,
                    System.currentTimeMillis(), TARIFA_TRANSACCION, clavePrivada);
            Mensaje mensaje = new Mensaje(direccion.getDireccionIP(), direccionDestinatario, RsaUtil.sign(transaccion.toString(), clavePrivada),
                    System.currentTimeMillis(), 0, transaccion);
            // System.out.println("Mensaje creado");
            salida.enviarMensaje(Direccion.DIRECCION_GATEWAY.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getPuerto(), mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recibirDinero(double monto, Tipo tipo) {
        if (tipo.equals(Tipo.LOGICO1)) {
            billetera1 += monto;
            // System.out.println("Nuevo valor: " + wallet1);
        } else {
            billetera2 += monto;
            // System.out.println("Nuevo valor: " + wallet2);
        }
    }

    public synchronized void recibirMensaje(Mensaje mensaje) {
        int tipoDeMensaje = mensaje.getTipo();
        List<Object> contenido = mensaje.getContenido();
        if (tipoDeMensaje == 1) {
            // System.out.println("Bloque recibido");
            Bloque bloque = (Bloque) contenido.get(0);
            String direccionDelNodo = mensaje.getDireccionRemitente();
            String firma = mensaje.getFirma();
            recibirBloque(bloque, firma, direccionDelNodo);
        }
    }

    public boolean verificarTransaccion(Transaccion transaccion) throws Exception {
        return RsaUtil.verify(transaccion.toString(), transaccion.getFirma(),
                red.obtenerClavePublicaPorDireccion(transaccion.getDireccionRemitente()));
    }

    public synchronized void recibirBloque(Bloque bloque, String firma, String direccionDelNodo) {
        try {
            if (RsaUtil.verify(HashUtil.SHA256(bloque.toString()), firma,
                    red.obtenerClavePublicaPorDireccion(direccionDelNodo))) {
                Tipo tipo = bloque.getTipo();
                bloquesEnEspera.get(tipo).add(bloque);
                System.out.println("Bloque recibido :" + bloquesEnEspera.get(Tipo.LOGICO1).size() + "/2, "
                        + bloquesEnEspera.get(Tipo.LOGICO2).size() + "/2");
                if (bloquesEnEspera.get(tipo).size() == 2) {
                    compararBloques(tipo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compararBloques(Tipo tipo) {
        List<Bloque> bloquesAComparar = bloquesEnEspera.get(tipo);
        Bloque primerBloque = bloquesAComparar.get(0);
        Bloque segundoBloque = bloquesAComparar.get(1);
        if (primerBloque.getFooter().getHash().equals(segundoBloque.getFooter().getHash())) {
            System.out.println("Creación correcta");
            if (primerBloque.getIdNodoMinero() > segundoBloque.getIdNodoMinero()) {
                red.getNodosEscogidos1().get(tipo).add(segundoBloque.getIdNodoMinero());
                red.getNodosEscogidos2().get(tipo).add(primerBloque.getIdNodoMinero());
                agregarBloque(segundoBloque);
            } else {
                red.getNodosEscogidos1().get(tipo).add(primerBloque.getIdNodoMinero());
                red.getNodosEscogidos2().get(tipo).add(segundoBloque.getIdNodoMinero());
                agregarBloque(primerBloque);
            }
        } else {
            System.out.println("---------------ERROR--------------");
        }
        if (tipo.equals(Tipo.LOGICO1)) {
            red.getNodosEscogidos1().get(Tipo.LOGICO2).add(-1);
            red.getNodosEscogidos2().get(Tipo.LOGICO2).add(-1);
        } else {
            red.getNodosEscogidos1().get(Tipo.LOGICO1).add(-1);
            red.getNodosEscogidos2().get(Tipo.LOGICO1).add(-1);
        }
        imprimirInformacion();
        bloquesEnEspera.put(tipo, new ArrayList<>());
    }

    public synchronized void agregarBloque(Bloque bloque) {
        red.agregarBloque(bloque);
        if (!bloque.getDireccionNodoMinero().equals("Master"))
            updateAllWallet(bloque);
        actualizarST(bloque.getTiempoDeBusqueda());
        actualizarNBOfBlockOfType(bloque.getTipo());
        System.out.println("\n///-----------------------------------///");
        System.out.println("Bloque agregado");
        System.out.println("///-----------------------------------///\n");
    }

    public void generarBloque(Paquete paquete) throws Exception {
        // System.out.println("---------------------------------------------------");
        Tipo tipo = paquete.getTipo();
        List<Transaccion> transaccionesDelBloque = paquete.getTransacciones();
        for (Transaccion transaccion : transaccionesDelBloque) {
            //System.out.println("Firma: " + transaccion.getFirma());
            if (!verificarTransaccion(transaccion)) {
                System.out.println("---------------ERROR--------------");
                return;
            }
        }
        System.out.println("---------------Se crea bloque---------------");
        long inicioBusqueda = System.nanoTime();
        BlockchainMultiple blockchainMultiple = red.getBlockchain();
        Bloque bloquePrevioFisico = blockchainMultiple.obtenerUltimoBloque();
        Bloque bloquePrevioLogico = blockchainMultiple.buscarBloquePrevioLogico(tipo,
                blockchainMultiple.obtenerCantidadDeBloques() - 1);
        long finBusqueda = System.nanoTime();
        long tiempoDelUltimoBloque = bloquePrevioFisico.getHeader().getMarcaDeTiempoDeCreacion();
        while (true) {
            if (System.currentTimeMillis() - tiempoDelUltimoBloque > 10000) { // Garantiza los 10 segundos minimos
                break;
            }
        }
        Bloque bloque = new Bloque(bloquePrevioFisico, bloquePrevioLogico, transaccionesDelBloque,
                (double) (finBusqueda - inicioBusqueda), tipo);
        bloque.setIdNodoMinero(this.id);
        bloque.setDireccionNodoMinero(this.direccion.getDireccionIP());
        // System.out.println("Block has been forged by " + this.name);
        try {

            List<Object> contenidoMensaje = new ArrayList<>();
            contenidoMensaje.add(bloque);
            Mensaje mensaje = new Mensaje(this.direccion.getDireccionIP(), "ALL",
                    RsaUtil.sign(HashUtil.SHA256(bloque.toString()), this.clavePrivada),
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

    public void actualizarNBOfBlockOfType(Tipo tipo) {
        if (tipo.equals(Tipo.LOGICO1)) {
            red.NB_OF_BLOCK_OF_TYPE1_CREATED.add(red.NB_OF_BLOCK_OF_TYPE1_CREATED.size() + 1);
        } else {
            red.NB_OF_BLOCK_OF_TYPE2_CREATED.add(red.NB_OF_BLOCK_OF_TYPE2_CREATED.size() + 1);
        }
    }

    public boolean comprobarCantidadMinimaDeNodos() {
        if (red.obtenerCantidadDeNodos() >= 4) {
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
        Tipo tipo = bloque.getTipo();
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
        if (bloque.getDireccionNodoMinero().equals(direccion)) {
            recibirDinero(totalFee, tipo);
        }
        actualizarExchangeMoneyPorTipo(tipo, montoTotal);
    }

    public void actualizarExchangeMoneyPorTipo(Tipo tipo, double amount) {
        if (tipo.equals(Tipo.LOGICO1)) {
            red.exchangeMoney1.add(amount);
            red.exchangeMoney2.add(0.);
        } else {
            red.exchangeMoney1.add(0.);
            red.exchangeMoney2.add(amount);
        }
    }

    private void imprimirInformacion() {
        System.out.println(red.getStats());
        if (red.NB_OF_BLOCK_OF_TYPE1_CREATED.size() + red.NB_OF_BLOCK_OF_TYPE2_CREATED.size() - 2 == MaximoDeBloques.MAX.getCantidad()) {
            try {
                BufferedWriter archivo = new BufferedWriter(
                        new FileWriter("Blockchain V4 (Gateway-Aleatorio) - Resultado.txt", true));
                archivo.write(red.getStats());
                archivo.newLine();
                archivo.close();
                System.out.println("Archivo guardado");
            } catch (IOException e) {
            }
            System.exit(0);
        }
    }
}
