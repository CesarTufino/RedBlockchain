package tradicional.nodo.posVersion;

import general.constantes.Direccion;
import general.constantes.MinimoDeNodos;
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

/**
 * La clase NodoTradicionalPos representa los dispositivos finales que generan transaciones, crean bloques y almacenan una
 * copia del blockchain tradicional con algoritmo POS.
 */
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

    /**
     * Obtiene la red.
     * @return red.
     */
    @Override
    public RedTradicionalPos getRed() {
        return redTradicionalPos;
    }

    /**
     * Establece una instancia de la red RedTradicionalPos.
     * @param red
     */
    @Override
    public void setRed(Red red) {
        this.redTradicionalPos = (RedTradicionalPos) red;
    }

    /**
     * Busaca la red intentando enviar su dirección a todos los nodos de las direcciones de la enumeración Direccion.
     * En caso de encontrar la red, realiza una copia, envía su información a los nodos conectados y añade su
     * información a su propia copia de la red.
     */
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

    /**
     * Envía la red de este nodo a otro nodo que envió su dirección para pedir la información actual de la red.
     * @param direccion dirección del nodo que pidió la información de la red.
     */
    @Override
    public void enviarInfoRed(Direccion direccion) {
        salida.enviarInfoRed(redTradicionalPos, direccion.getDireccionIP(), direccion.getPuerto());
    }

    /**
     * Comprueba la cantidad de nodos mínimos para el inicio de una ejecución.
     * @return true si existe la cantidad de nodos mínimos.
     */
    public boolean comprobarCantidadMinimaDeNodos() {
        return redTradicionalPos.obtenerCantidadDeNodos() >= MinimoDeNodos.MIN_POS.getCantidad();
    }

    /**
     * Crea una transacción, crea un mensaje con esa transacción y se lo envía a todos los nodos de la red.
     * @param monto dinero de la transacción.
     * @param direccionDestinatario dirección IP del nodo destinatario.
     */
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

    /**
     * Actualiza el dinero en la billetera.
     * @param monto dinero que se agrega o reduce de la billetera.
     */
    public void recibirDinero(double monto) {
        billetera1 += monto;
    }

    /**
     * Recibe el mensaje y proceso su contenido.
     * @param mensaje mensaje recibido.
     */
    @Override
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

    /**
     * Agrega la transacción a la lista de transacciones pendientes si la transacción se verifica correctamente.
     * @param transaccion transacción recibida.
     */
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

    /**
     * Verifica la firma de una transacción.
     * @param transaccion transacción.
     * @return true si la verificación se realiza correctamente.
     * @throws Exception si la verificación lanza una excepción.
     */
    public boolean verificarTransaccion(TransaccionTradicional transaccion) throws Exception {
        return RsaUtil.verify(transaccion.toString(), transaccion.getFirma(),
                redTradicionalPos.obtenerClavePublicaPorDireccion(transaccion.getDireccionRemitente()));
    }

    /**
     * Agrega el bloque al blockchain de la red, también se llama a los métodos para actualizar el tiempo de busqueda y
     * el número de bloques.
     * @param bloque bloque recibido.
     * @param firma firma del mensaje con el bloque recibido.
     * @param direccionDelNodo dirección del nodo que envió el mensaje.
     */
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

    /**
     * Elimina todas las transacciones del bloque procesado de la lista d transacciones pendientes.
     * @param bloque bloque procesado.
     */
    public void actualizarListaDeTransacciones(BloqueTradicional bloque) {
        List<TransaccionTradicional> transacciones = bloque.getTransaction();
        for (TransaccionTradicional transaccion : transacciones) {
            transaccionesPendientes.remove(transaccion);
            actualizarNbTrans(-1);
        }
    }

    /**
     * Instancia un bloque y lo envía a todos los nodos de la red.
     * @throws Exception si no se verifican las transacciones.
     */
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

    /**
     * Establece el monto de apuesta del nodo.
     * @param monto monto de apuesta del nodo.
     */
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

    /**
     * Actualiza el número de transacciones pendientes en la red.
     * @param cantidad cantidad que se va incrementar.
     */
    public void actualizarNbTrans(int cantidad) {
        redTradicionalPos.setNbTrans(redTradicionalPos.getNbTrans() + cantidad);
    }

    /**
     * Actualiza el tiempo de busqueda de cada bloque en la red.
     * @param st tiempo de busqueda del último bloque.
     */
    public void actualizarST(double st) {
        redTradicionalPos.getSearchTimes().add(st);
    }

    /**
     * Actualiza el número de bloques de la red.
     */
    public void actualizarNBOfBlock() {
        redTradicionalPos.getNB_OF_BLOCK_CREATED().add(redTradicionalPos.getNB_OF_BLOCK_CREATED().size() + 1);
        // Último en ejecutarse

    }

    /**
     * Actualiza las billeteras de todos los nodos que participaron en las transacciones procesadas en un bloque.
     * @param bloque bloque recibido.
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

    /**
     * Acutaliza el la lista de valores totales de las transacciones procesadas por un bloque.
     * @param monto monto total de las transacciones procesadas.
     */
    public void actualizarExchangeMoney(double monto) {
        redTradicionalPos.getExchangeMoney1().add(monto);
    }

}
