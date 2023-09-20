package tradicional.nodo.posVersion;

import general.constantes.Direccion;
import general.mensajes.InfoNodo;
import general.mensajes.Mensaje;
import general.nodo.Nodo;
import general.nodo.Red;
import general.utils.HashUtil;
import general.utils.RsaUtil;
import tradicional.blockchain.BloqueTradicional;
import tradicional.mensajes.TransaccionTradicional;

import java.util.ArrayList;
import java.util.List;

public class NodoTradicionalPos extends Nodo {

    private double montoDeApuesta;
    private long tiempoDeApuesta;
    private final int TRANSACCIONES_MAXIMAS_POR_BLOQUE = 10;
    private ArrayList<TransaccionTradicional> transaccionesPendientes = new ArrayList<>();
    private ArrayList<TransaccionTradicional> transaccionesFraudulentas = new ArrayList<>();
    private RedTradicionalPos redTradicionalPos = null;

    public NodoTradicionalPos(int id, Direccion direccion) {
        super(id, direccion);
        this.montoDeApuesta = 0;
    }

    public RedTradicionalPos getRed() {
        return redTradicionalPos;
    }

    public void setRed(Red red) {
        this.redTradicionalPos = (RedTradicionalPos) red;
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
        InfoNodo infoNodo = new InfoNodo(direccion.getDireccionIP(), clavePublica,
                direccion.getPuerto(), montoDeApuesta, tiempoDeApuesta);
        if (redTradicionalPos == null) {
            System.out.println("No se pudo copiar un Informacion de la Red");
            System.out.println("Se crea la Informacion de la Red");
            redTradicionalPos = new RedTradicionalPos();
        } else {
            System.out.println("Copia de InfoRed creada");
            salida.broadcastInformacionNodo(redTradicionalPos.getPuertos(), infoNodo);
        }
        redTradicionalPos.addNode(infoNodo);
    }

    public void enviarInfoRed(Direccion direccion) {
        salida.enviarInfoRed(redTradicionalPos, direccion.getDireccionIP(), direccion.getPuerto());
    }

    public void enviarDinero(double monto, String direccionDestinatario) {
        System.out.println("Inicio de transacción");
        if (billetera1 - monto * (1 + TARIFA_TRANSACCION) < 0) {
            System.out.println("-Transacción rechazada-");
            return;
        }
        try {
            TransaccionTradicional transaccionTradicional = new TransaccionTradicional(this.getDireccion().getDireccionIP(),
                    direccionDestinatario, monto, System.currentTimeMillis(), TARIFA_TRANSACCION, clavePrivada);
            Mensaje mensaje = new Mensaje(this.direccion.getDireccionIP(), direccionDestinatario,
                    RsaUtil.sign(HashUtil.SHA256(transaccionTradicional.toString()), clavePrivada),
                    System.currentTimeMillis(), 0, transaccionTradicional);
            // System.out.println("Mensaje creado");
            salida.broadcastMensaje(mensaje, redTradicionalPos.getPuertos());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recibirDinero(double monto) {
        billetera1 += monto;
    }

    public void recibirMensaje(Mensaje mensaje) {
        int tipoDeMensaje = mensaje.getTipoDeContenido();
        Object contenido = mensaje.getContenido();
        if (tipoDeMensaje == 0) {
            // System.out.println("Transaccion recibida");
            TransaccionTradicional transaccion = (TransaccionTradicional) contenido;
            recibirTransaccion(transaccion);
        }
        if (tipoDeMensaje == 1) {
            // System.out.println("Bloque recibido");
            BloqueTradicional bloque = (BloqueTradicional) contenido;
            String direccionDelNodo = mensaje.getDireccionRemitente();
            String firma = mensaje.getFirma();
            recibirBloque(bloque, firma, direccionDelNodo);
        }
    }

    public synchronized void recibirTransaccion(TransaccionTradicional transaccion) {
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

    public boolean verificarTransaccion(TransaccionTradicional transaccion) throws Exception {
        return RsaUtil.verify(transaccion.toString(), transaccion.getFirma(),
                redTradicionalPos.obtenerClavePublicaPorDireccion(transaccion.getDireccionRemitente()));
    }

    public void actualizarNbTrans(int cantidad) {
        redTradicionalPos.setNbTrans(redTradicionalPos.getNbTrans() + cantidad);
    }

    public synchronized void recibirBloque(BloqueTradicional bloque, String firma, String direccionDelNodo) {
        actualizarListaDeTransacciones(bloque);
        try {
            if (RsaUtil.verify(HashUtil.SHA256(bloque.toString()), firma,
                    redTradicionalPos.obtenerClavePublicaPorDireccion(direccionDelNodo))) {
                if (!bloque.getDireccionNodoMinero().equals("Master"))
                    updateAllWallet(bloque);
                actualizarST(bloque.getTiempoDeBusqueda());
                actualizarNBOfBlock();
                redTradicionalPos.getNodosEscogidos().add(bloque.getIdNodoMinero());
                redTradicionalPos.agregarBloque(bloque);
                System.out.println("\n///-----------------------------------///");
                System.out.println("Bloque agregado");
                System.out.println("///-----------------------------------///\n");
                System.out.println("--- Cantidad de bloques actuales: " + (redTradicionalPos.getBlockchainTradicional().obtenerCantidadDeBloques() - 1) + " ---");
                System.out.println(redTradicionalPos.getStats());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizarListaDeTransacciones(BloqueTradicional bloque) {
        List<TransaccionTradicional> transacciones = bloque.getTransaction();
        for (TransaccionTradicional transaccion : transacciones) {
            transaccionesPendientes.remove(transaccion);
            actualizarNbTrans(-1);
        }
    }

    public void generarBloque() {
        // System.out.println("---------------------------------------------------");
        List<TransaccionTradicional> transaccionesDelBloque = new ArrayList<>();
        for (int i = 0; (i < TRANSACCIONES_MAXIMAS_POR_BLOQUE) && (i < transaccionesPendientes.size()); i++) {
            transaccionesDelBloque.add(transaccionesPendientes.get(i));
        }
        long inicioBusqueda = System.nanoTime();
        BloqueTradicional bloquePrevio = redTradicionalPos.getBlockchainTradicional().obtenerUltimoBloque();
        long finBusqueda = System.nanoTime();
        long tiempoDelUltimoBloqueLogico = bloquePrevio.getHeader().getMarcaDeTiempoDeCreacion();
        while (true) {
            if (System.currentTimeMillis() - tiempoDelUltimoBloqueLogico > 10000) { // Garantiza los 10 segundos minimos
                break;
            }
            System.out.print("");
        }
        BloqueTradicional bloque = new BloqueTradicional(bloquePrevio, transaccionesDelBloque, (double) (finBusqueda - inicioBusqueda));
        bloque.setIdNodoMinero(this.id);
        bloque.setDireccionNodoMinero(this.direccion.getDireccionIP());
        // System.out.println("Block has been forged by " + this.name);
        try {
            Mensaje mensaje = new Mensaje(this.direccion.getDireccionIP(), "ALL",
                    RsaUtil.sign(HashUtil.SHA256(bloque.toString()), this.clavePrivada),
                    System.currentTimeMillis(),
                    1, bloque);
            salida.broadcastMensaje(mensaje, redTradicionalPos.getPuertos());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error signing");
        }
        // System.out.println("---------------------------------------------------");
    }

    public void apostar(double monto) {
        if (billetera1 < monto) {
            System.out.println(id + " no tiene suficiente dinero para apostar en wallet1");
            return;
        }
        montoDeApuesta = monto;
        billetera1 -= monto;
        tiempoDeApuesta = System.currentTimeMillis();
        System.out.println(id + " deposita " + monto + " como apuesta");
    }

    public void actualizarST(double st) {
        redTradicionalPos.getSearchTimes().add(st);
    }

    public void actualizarNBOfBlock() {
        redTradicionalPos.getNB_OF_BLOCK_CREATED().add(redTradicionalPos.getNB_OF_BLOCK_CREATED().size() + 1);
        // Último en ejecutarse

    }

    public boolean comprobarCantidadMinimaDeNodos() {
        // System.out.println(infoRed.comprobarCantidadDeNodos());
        return redTradicionalPos.obtenerCantidadDeNodos() >= 3;
    }

    /**
     * Método que actualiza las billeteras de todos los nodos que participaron.
     *
     * @param bloque Bloque.
     */
    private void updateAllWallet(BloqueTradicional bloque) {
        double totalFee = 0;
        List<TransaccionTradicional> transacciones = bloque.getTransaction();
        double montoTotal = 0;
        for (TransaccionTradicional transaccion : transacciones) {
            transaccion.confirmar();
            double montoTransaccion = transaccion.getMonto();
            double tarifaTransaccion = transaccion.getTarifa() * montoTransaccion;
            String toAddress = transaccion.getDireccionDestinatario();
            montoTotal += montoTransaccion;
            totalFee += tarifaTransaccion;
            // Actualización de la billetera del destinatario de la transacción.
            if (toAddress.equals(direccion.getDireccionIP())) {
                recibirDinero(montoTransaccion);
            }
            // Actualización de la billetera del emisor de la transacción.
            if (transaccion.getDireccionRemitente().equals(direccion.getDireccionIP())) {
                recibirDinero(-(montoTransaccion + tarifaTransaccion));
            }
        }
        // Actualización del minero.
        if (bloque.getDireccionNodoMinero().equals(direccion.getDireccionIP())) {
            recibirDinero(totalFee);
        }
        actualizarExchangeMoney(montoTotal);
    }

    public void actualizarExchangeMoney(double monto) {
        redTradicionalPos.getExchangeMoney1().add(monto);
    }

}
