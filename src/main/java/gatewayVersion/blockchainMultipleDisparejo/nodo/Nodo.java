package gatewayVersion.blockchainMultipleDisparejo.nodo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import direcciones.Direccion;
import gatewayVersion.blockchainMultipleDisparejo.blockchain.Bloque;
import gatewayVersion.blockchainMultipleDisparejo.conexion.Entrada;
import gatewayVersion.blockchainMultipleDisparejo.conexion.Salida;
import gatewayVersion.blockchainMultipleDisparejo.mensajes.InfoNodo;
import gatewayVersion.blockchainMultipleDisparejo.mensajes.Mensaje;
import gatewayVersion.blockchainMultipleDisparejo.mensajes.Paquete;
import gatewayVersion.blockchainMultipleDisparejo.mensajes.Transaccion;
import gatewayVersion.blockchainMultipleDisparejo.blockchain.Blockchain;
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
    private List<Bloque> bloquesEnEsperaTipo1 = new ArrayList<>();
    private List<Bloque> bloquesEnEsperaTipo2 = new ArrayList<>();
    private final String TYPE1 = "Type1";

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

    public void iniciarProceso() throws IOException {
        // Hilo para escuchar
        Entrada serverThread = new Entrada(this);
        serverThread.start();
        // Buscar datos en la red
        buscarRed();
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

    public void enviarDinero(double monto, String direccionDestinatario, String tipo) {
        System.out.println("Inicio de transacción");
        if (billetera1 - monto * (1 + TARIFA_TRANSACCION) < 0) {
            System.out.println("-Transacción rechazada-");
            return;
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

    public void recibirDinero(double monto, String tipo) {
        if (tipo.equals(TYPE1)) {
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
        //actualizarListaDeTransacciones(bloque);
        try {
            if (RsaUtil.verify(HashUtil.SHA256(bloque.toString()), firma,
                    red.obtenerClavePublicaPorDireccion(direccionDelNodo))) {
                if (bloque.getTipo().equals(TYPE1)) {
                    bloquesEnEsperaTipo1.add(bloque);
                } else {
                    bloquesEnEsperaTipo2.add(bloque);
                }
                System.out.println("Bloque recibido");
                if (bloquesEnEsperaTipo1.size() == 2) {
                    compararBloques(bloque.getTipo());
                }
                if (bloquesEnEsperaTipo2.size() == 2) {
                    compararBloques(bloque.getTipo());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compararBloques(String tipo) {
        if (tipo.equals(TYPE1)) {
            if (bloquesEnEsperaTipo1.get(0).getFooter().getHash().equals(bloquesEnEsperaTipo1.get(1).getFooter().getHash())) {
                System.out.println("Creación correcta");
                if (bloquesEnEsperaTipo1.get(0).getIdNodo() > bloquesEnEsperaTipo1.get(1).getIdNodo()) {
                    red.getNodosEscogidos1Tipo1().add(bloquesEnEsperaTipo1.get(1).getIdNodo());
                    red.getNodosEscogidos2Tipo1().add(bloquesEnEsperaTipo1.get(0).getIdNodo());
                    agregarBloque(bloquesEnEsperaTipo1.get(1));
                } else {
                    red.getNodosEscogidos1Tipo1().add(bloquesEnEsperaTipo1.get(0).getIdNodo());
                    red.getNodosEscogidos2Tipo1().add(bloquesEnEsperaTipo1.get(1).getIdNodo());
                    agregarBloque(bloquesEnEsperaTipo1.get(0));
                }
                red.getNodosEscogidos1Tipo2().add(-1);
                red.getNodosEscogidos2Tipo2().add(-1);
                imprimirInformacion();
            } else {
                System.out.println("---------------ERROR--------------");
            }
            bloquesEnEsperaTipo1 = new ArrayList<>();
        } else {
            if (bloquesEnEsperaTipo2.get(0).getFooter().getHash().equals(bloquesEnEsperaTipo2.get(1).getFooter().getHash())) {
                System.out.println("Creación correcta");
                if (bloquesEnEsperaTipo2.get(0).getIdNodo() > bloquesEnEsperaTipo2.get(1).getIdNodo()) {
                    red.getNodosEscogidos1Tipo2().add(bloquesEnEsperaTipo2.get(1).getIdNodo());
                    red.getNodosEscogidos2Tipo2().add(bloquesEnEsperaTipo2.get(0).getIdNodo());
                    agregarBloque(bloquesEnEsperaTipo2.get(1));
                } else {
                    red.getNodosEscogidos1Tipo2().add(bloquesEnEsperaTipo2.get(0).getIdNodo());
                    red.getNodosEscogidos2Tipo2().add(bloquesEnEsperaTipo2.get(1).getIdNodo());
                    agregarBloque(bloquesEnEsperaTipo2.get(0));
                }
                red.getNodosEscogidos1Tipo1().add(-1);
                red.getNodosEscogidos2Tipo1().add(-1);
                imprimirInformacion();
            } else {
                System.out.println("---------------ERROR--------------");
            }
            bloquesEnEsperaTipo2 = new ArrayList<>();
        }

    }

    public synchronized void agregarBloque(Bloque bloque) {
        red.agregarBloque(bloque);
        if (!bloque.getDireccionNodo().equals("Master"))
            updateAllWallet(bloque);
        actualizarST(bloque.getTiempoDeBusqueda());
        actualizarNBOfBlockOfType(bloque.getTipo());
        System.out.println("\n///-----------------------------------///");
        System.out.println("Bloque agregado");
        System.out.println("///-----------------------------------///\n");
    }

    public void generarBloque(Paquete paquete) throws Exception {
        // System.out.println("---------------------------------------------------");
        String tipo = paquete.getTipo();
        List<Transaccion> transaccionesDelBloque = paquete.getTransacciones();
        for (Transaccion transaccion : transaccionesDelBloque) {
            if (!verificarTransaccion(transaccion)) {
                System.out.println("---------------ERROR--------------");
                return;
            }
        }
        System.out.println("---------------Se crea bloque---------------");
        long inicioBusqueda = System.nanoTime();
        Blockchain blockchain = red.getBlockchain();
        Bloque bloquePrevioFisico = blockchain.obtenerUltimoBloque();
        Bloque bloquePrevioLogico = blockchain.buscarBloquePrevioLogico(tipo,
                blockchain.obtenerCantidadDeBloques() - 1);
        long finBusqueda = System.nanoTime();
        Bloque bloque = new Bloque(bloquePrevioFisico, bloquePrevioLogico, transaccionesDelBloque,
                (double) (finBusqueda - inicioBusqueda), tipo);
        bloque.setIdNodo(this.id);
        bloque.setDireccionNodo(this.direccion.getDireccionIP());
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
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error signing");
        }
        // System.out.println("---------------------------------------------------");
    }

    public void actualizarNbTransPorTipo(String tipo, int cantidad) {
        HashMap<String, Integer> nbTransParType = (HashMap<String, Integer>) red.getNbTransParType();
        red.setNbTransParType(tipo, nbTransParType.get(TYPE1) + cantidad);
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

    private void imprimirInformacion() {
        System.out.println(red.getStats());
        if (red.NB_OF_BLOCK_OF_TYPE1_CREATED.size() + red.NB_OF_BLOCK_OF_TYPE2_CREATED.size() > 201) {
            try {
                BufferedWriter archivo = new BufferedWriter(
                        new FileWriter("Blockchain V3 (Gateway-Disparejo) - Resultado.txt", true));
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
