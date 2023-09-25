package multiple.nodo.posVersion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import general.constantes.Direccion;
import general.constantes.Tipo;
import general.constantes.MinimoDeNodos;
import multiple.blockchain.BlockchainMultiple;
import multiple.blockchain.BloqueMultiple;
import general.mensajes.InfoNodo;
import general.mensajes.Mensaje;
import multiple.mensajes.TransaccionMultiple;
import general.nodo.Nodo;
import general.nodo.Red;
import general.utils.HashUtil;
import general.utils.RsaUtil;

/**
 * La clase NodoMultiplePos representa los dispositivos finales que generan transaciones, crean bloques y almacenan una
 * copia del blockchain multiple con algoritmo POS.
 */
public class NodoMultiplePos extends Nodo {

    private double billetera2;
    private double montoDeApuesta1;
    private double montoDeApuesta2;
    private long tiempoDeApuesta1;
    private long tiempoDeApuesta2;
    private final int TRANSACCIONES_MAXIMAS_POR_BLOQUE = 10;
    private ArrayList<TransaccionMultiple> transaccionesPendientes = new ArrayList<>();
    private ArrayList<TransaccionMultiple> transaccionesFraudulentas = new ArrayList<>();
    private RedMultiplePos redMultiplePos = null;

    public NodoMultiplePos(int id, Direccion direccion) {
        super(id, direccion);
        this.montoDeApuesta1 = 0;
        this.montoDeApuesta2 = 0;
        this.billetera2 = DINERO_INICIAL;
    }

    /**
     * Obtiene la red.
     * @return red.
     */
    @Override
    public RedMultiplePos getRed() {
        return redMultiplePos;
    }

    /**
     * Establece una instancia de la red RedMultiplePos.
     * @param red
     */
    @Override
    public void setRed(Red red) {
        this.redMultiplePos = (RedMultiplePos) red;
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
                direccion.getPuerto(), montoDeApuesta1, montoDeApuesta2, tiempoDeApuesta1, tiempoDeApuesta2);
        if (redMultiplePos == null) {
            System.out.println("No se pudo copiar un Informacion de la Red");
            System.out.println("Se crea la Informacion de la Red");
            redMultiplePos = new RedMultiplePos();
        } else {
            System.out.println("Copia de InfoRed creada");
            salida.broadcastInformacionNodo(redMultiplePos.getPuertos(), infoNodo);
        }
        redMultiplePos.addNode(infoNodo);
    }

    /**
     * Envía la red de este nodo a otro nodo que envió su dirección para pedir la información actual de la red.
     * @param direccion dirección del nodo que pidió la información de la red.
     */
    @Override
    public void enviarInfoRed(Direccion direccion) {
        salida.enviarInfoRed(redMultiplePos, direccion.getDireccionIP(), direccion.getPuerto());
    }

    /**
     * Comprueba la cantidad de nodos mínimos para el inicio de una ejecución.
     * @return true si existe la cantidad de nodos mínimos.
     */
    public boolean comprobarCantidadMinimaDeNodos() {
        return redMultiplePos.obtenerCantidadDeNodos() >= MinimoDeNodos.MIN_POS.getCantidad();
    }

    /**
     * Crea una transacción, crea un mensaje con esa transacción y se lo envía a todos los nodos de la red.
     * @param monto dinero de la transacción.
     * @param direccionDestinatario dirección IP del nodo destinatario.
     * @param tipo tipo de la transacción.
     */
    public void enviarDinero(double monto, String direccionDestinatario, Tipo tipo) {
        System.out.println("Inicio de transacción " + tipo);
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
            TransaccionMultiple transaccionMultiple = new TransaccionMultiple(tipo, this.getDireccion().getDireccionIP(), direccionDestinatario, monto,
                    System.currentTimeMillis(), TARIFA_TRANSACCION, clavePrivada);
            Mensaje mensaje = new Mensaje(this.direccion.getDireccionIP(), direccionDestinatario,
                    RsaUtil.sign(HashUtil.SHA256(transaccionMultiple.toString()), clavePrivada),
                    System.currentTimeMillis(), 0, transaccionMultiple);
            // System.out.println("Mensaje creado");
            salida.broadcastMensaje(mensaje, redMultiplePos.getPuertos());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Actualiza el dinero en las billeteras de cada tipo.
     * @param monto dinero que se agrega o reduce de la billetera.
     * @param tipo tipo de blockchain.
     */
    public void recibirDinero(double monto, Tipo tipo) {
        // System.out.println("Cantidad recibida: " + amount);
        if (tipo.equals(Tipo.LOGICO1)) {
            billetera1 += monto;
            // System.out.println("Nuevo valor: " + wallet1);
        } else {
            billetera2 += monto;
            // System.out.println("Nuevo valor: " + wallet2);
        }
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
            TransaccionMultiple transaccionMultiple = (TransaccionMultiple) contenido;
            recibirTransaccion(transaccionMultiple);
        }
        if (tipoDeMensaje == 1) {
            // System.out.println("Bloque recibido");
            BloqueMultiple bloque = (BloqueMultiple) contenido;
            String direccionDelNodo = mensaje.getDireccionRemitente();
            String firma = mensaje.getFirma();
            recibirBloque(bloque, firma, direccionDelNodo);
        }
    }

    /**
     * Agrega la transacción a la lista de transacciones pendientes si la transacción se verifica correctamente.
     * @param transaccionMultiple transacción recibida.
     */
    public synchronized void recibirTransaccion(TransaccionMultiple transaccionMultiple) {
        boolean estadoDeLaTransaccion = false;
        try {
            estadoDeLaTransaccion = verificarTransaccion(transaccionMultiple);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (estadoDeLaTransaccion) {
            transaccionesPendientes.add(transaccionMultiple);
            actualizarNbTransPorTipo(transaccionMultiple.getTipo(), 1);
        } else {
            transaccionesFraudulentas.add(transaccionMultiple);
        }
    }

    /**
     * Verifica la firma de una transacción.
     * @param transaccionMultiple transacción.
     * @return true si la verificación se realiza correctamente.
     * @throws Exception si la verificación lanza una excepción.
     */
    public boolean verificarTransaccion(TransaccionMultiple transaccionMultiple) throws Exception {
        return RsaUtil.verify(transaccionMultiple.toString(), transaccionMultiple.getFirma(),
                redMultiplePos.obtenerClavePublicaPorDireccion(transaccionMultiple.getDireccionRemitente()));
    }

    /**
     * Agrega el bloque al blockchain de la red, también se llama a los métodos para actualizar el tiempo de busqueda y
     * el número de bloques por tipo.
     * @param bloque bloque recibido.
     * @param firma firma del mensaje con el bloque recibido.
     * @param direccionDelNodo dirección del nodo que envió el mensaje.
     */
    public synchronized void recibirBloque(BloqueMultiple bloque, String firma, String direccionDelNodo) {
        actualizarListaDeTransacciones(bloque);
        try {
            if (RsaUtil.verify(HashUtil.SHA256(bloque.toString()), firma,
                    redMultiplePos.obtenerClavePublicaPorDireccion(direccionDelNodo))) {
                if (!bloque.getDireccionNodoMinero().equals("Master"))
                    updateAllWallet(bloque);
                actualizarST(bloque.getTiempoDeBusqueda());
                Tipo tipo = bloque.getTipo();
                actualizarNBOfBlockOfType(tipo);
                redMultiplePos.getNodosEscogidos().get(tipo).add(bloque.getIdNodoMinero());
                if (tipo.equals(Tipo.LOGICO1)) {
                    redMultiplePos.getNodosEscogidos().get(Tipo.LOGICO2).add(-1);
                } else {
                    redMultiplePos.getNodosEscogidos().get(Tipo.LOGICO1).add(-1);
                }
                redMultiplePos.agregarBloque(bloque);
                System.out.println("\n///-----------------------------------///");
                System.out.println("Bloque agregado");
                System.out.println("///-----------------------------------///\n");
                System.out.println("--- Cantidad de bloques actuales: " + (redMultiplePos.getBlockchainMultiple().obtenerCantidadDeBloques() - 2) + " ---");
                System.out.println(redMultiplePos.getStats());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina todas las transacciones del bloque procesado de la lista d transacciones pendientes.
     * @param bloque bloque procesado.
     */
    public synchronized void actualizarListaDeTransacciones(BloqueMultiple bloque) {
        List<TransaccionMultiple> transacciones = bloque.getTransaction();
        for (TransaccionMultiple transaccionMultipleDeBloque : transacciones) {
            transaccionesPendientes.remove(transaccionMultipleDeBloque);
            actualizarNbTransPorTipo(transaccionMultipleDeBloque.getTipo(), -1);
        }
    }

    /**
     * Instancia un bloque y lo envía a todos los nodos de la red.
     * @param tipo tipo de blockchain.
     * @throws Exception si no se verifican las transacciones.
     */
    public void generarBloque(Tipo tipo) {
        System.out.println("--- Creando bloque " + tipo + " ---");
        List<TransaccionMultiple> transaccionesDelBloque = new ArrayList<>();
        for (int i = 0; (i < TRANSACCIONES_MAXIMAS_POR_BLOQUE) && (i < transaccionesPendientes.size()); i++) {
            if (transaccionesPendientes.get(i).getTipo().equals(tipo)) {
                transaccionesDelBloque.add(transaccionesPendientes.get(i));
            }
        }
        long inicioBusqueda = System.nanoTime();
        BlockchainMultiple blockchain = redMultiplePos.getBlockchainMultiple();
        BloqueMultiple bloquePrevioFisico = blockchain.obtenerUltimoBloque();
        BloqueMultiple bloquePrevioLogico = blockchain.buscarBloquePrevioLogico(tipo,
                blockchain.obtenerCantidadDeBloques() - 1);
        long finBusqueda = System.nanoTime();
        long tiempoDelUltimoBloqueLogico = bloquePrevioLogico.getHeader().getMarcaDeTiempoDeCreacion();
        while (true) {
            if (System.currentTimeMillis() - tiempoDelUltimoBloqueLogico > 10000) { // Garantiza los 10 segundos minimos
                break;
            }
            System.out.print("");
        }
        BloqueMultiple bloque = new BloqueMultiple(bloquePrevioFisico, bloquePrevioLogico, transaccionesDelBloque,
                (double) (finBusqueda - inicioBusqueda), tipo);
        bloque.setIdNodoMinero(this.id);
        bloque.setDireccionNodoMinero(this.direccion.getDireccionIP());
        // System.out.println("Block has been forged by " + this.name);
        try {
            // messageContent.add(general.blockchain);
            Mensaje mensaje = new Mensaje(this.direccion.getDireccionIP(), "ALL",
                    RsaUtil.sign(HashUtil.SHA256(bloque.toString()), this.clavePrivada),
                    System.currentTimeMillis(),
                    1, bloque);
            salida.broadcastMensaje(mensaje, redMultiplePos.getPuertos());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("---------------------------------------------------");
    }

    /**
     * Establece el monto de apuesta del nodo para un tipo de blockchain.
     * @param monto monto de apuesta del nodo.
     * @param tipo tipo de blockchain.
     */
    public void apostar(double monto, Tipo tipo) {
        if (tipo.equals(Tipo.LOGICO1)) {
            if (billetera1 < monto) {
                // System.out.println(name + " don't have enough money for stake in wallet1");
                return;
            }
            montoDeApuesta1 = monto;
            billetera1 -= monto;
            tiempoDeApuesta1 = System.currentTimeMillis();
        } else {
            if (billetera2 < monto) {
                // System.out.println(name + " don't have enough money for stake in wallet2");
                return;
            }
            montoDeApuesta2 = monto;
            billetera2 -= monto;
            tiempoDeApuesta2 = System.currentTimeMillis();
        }
        System.out.println(id + " deposita " + monto + "  como apuesta para " + tipo);
    }

    /**
     * Actualiza el número de transacciones pendientes de un tipo de blockchain en la red.
     * @param tipo tipo de blockchain.
     * @param cantidad cantidad que se va incrementar.
     */
    public void actualizarNbTransPorTipo(Tipo tipo, int cantidad) {
        HashMap<Tipo, Integer> nbTransParType = (HashMap<Tipo, Integer>) redMultiplePos.getNbTransParType();
        nbTransParType.put(tipo, nbTransParType.get(tipo) + cantidad);
        //red.setNbTransParType();
    }

    /**
     * Actualiza el tiempo de busqueda de cada bloque en la red.
     * @param st tiempo de busqueda del último bloque.
     */
    public void actualizarST(double st) {
        redMultiplePos.getSearchTimes().add(st);
    }

    /**
     * Actualiza el número de bloques por tipo de la red.
     * @param tipo tipo de la lista de número de bloques que se va a actualizar.
     */
    public void actualizarNBOfBlockOfType(Tipo tipo) {
        if (tipo.equals(Tipo.LOGICO1)) {
            redMultiplePos.NB_OF_BLOCK_OF_TYPE1_CREATED.add(redMultiplePos.NB_OF_BLOCK_OF_TYPE1_CREATED.size() + 1);
        } else {
            redMultiplePos.NB_OF_BLOCK_OF_TYPE2_CREATED.add(redMultiplePos.NB_OF_BLOCK_OF_TYPE2_CREATED.size() + 1);
        }
        // Último en ejecutarse

    }

    /**
     * Actualiza las billeteras de todos los nodos que participaron en las transacciones procesadas en un bloque.
     * @param bloque bloque recibido.
     */
    private void updateAllWallet(BloqueMultiple bloque) {
        double totalFee = 0;
        List<TransaccionMultiple> transacciones = bloque.getTransaction();
        double montoTotal = 0;
        Tipo tipo = bloque.getTipo();
        for (TransaccionMultiple transaccionMultiple : transacciones) {
            transaccionMultiple.confirmar();
            double montoTransaccion = transaccionMultiple.getMonto();
            double tarifaTransaccion = transaccionMultiple.getTarifa() * montoTransaccion;
            String toAddress = transaccionMultiple.getDireccionDestinatario();
            montoTotal += montoTransaccion;
            totalFee += tarifaTransaccion;
            // Actualización de la billetera del destinatario de la transacción.
            if (toAddress.equals(direccion.getDireccionIP())) {
                recibirDinero(montoTransaccion, tipo);
            }
            // Actualización de la billetera del emisor de la transacción.
            if (transaccionMultiple.getDireccionRemitente().equals(direccion.getDireccionIP())) {
                recibirDinero(-(montoTransaccion + tarifaTransaccion), tipo);
            }
        }

        // Actualización del minero.
        if (bloque.getDireccionNodoMinero().equals(direccion.getDireccionIP())) {
            recibirDinero(totalFee, tipo);
        }
        actualizarExchangeMoneyPorTipo(tipo, montoTotal);
    }

    /**
     * Acutaliza el la lista de valores totales de las transacciones procesadas por un bloque.
     * @param tipo tipo de las transacciones.
     * @param amount monto total de las transacciones procesadas.
     */
    public void actualizarExchangeMoneyPorTipo(Tipo tipo, double amount) {
        if (tipo.equals(Tipo.LOGICO1)) {
            redMultiplePos.getExchangeMoney1().add(amount);
            redMultiplePos.getExchangeMoney2().add(0.);
        } else {
            redMultiplePos.getExchangeMoney1().add(0.);
            redMultiplePos.getExchangeMoney2().add(amount);
        }
    }
}
