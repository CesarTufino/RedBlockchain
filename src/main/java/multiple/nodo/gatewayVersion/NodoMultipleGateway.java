package multiple.nodo.gatewayVersion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import general.constantes.Direccion;
import general.constantes.Tipo;
import general.constantes.MinimoDeNodos;
import multiple.blockchain.BloqueMultiple;
import general.mensajes.InfoNodo;
import general.mensajes.Mensaje;
import multiple.mensajes.TransaccionMultiple;
import multiple.blockchain.BlockchainMultiple;
import general.nodo.Nodo;
import general.nodo.Red;
import general.utils.*;

/**
 * La clase NodoMultipleGateway representa los dispositivos finales que generan transaciones, crean bloques y almacenan
 * una copia del blockchain multiple con gateway.
 */
public class NodoMultipleGateway extends Nodo {
    private double billetera2;
    private RedMultipleGateway redMultipleGateway;
    private HashMap<Tipo, List<BloqueMultiple>> bloquesEnEspera = new HashMap<>();

    public NodoMultipleGateway(int id, Direccion direccion) {
        super(id, direccion);
        this.bloquesEnEspera.put(Tipo.LOGICO1, new ArrayList<>());
        this.bloquesEnEspera.put(Tipo.LOGICO2, new ArrayList<>());
        this.billetera2 = DINERO_INICIAL;
    }

    /**
     * Obtiene la red.
     * @return red.
     */
    @Override
    public RedMultipleGateway getRed() {
        return redMultipleGateway;
    }

    /**
     * Establece una instancia de la red RedMultipleGateway.
     * @param red
     */
    @Override
    public void setRed(Red red) {
        this.redMultipleGateway = (RedMultipleGateway) red;
    }

    /**
     * Busaca la red intentando enviar su dirección a todos los nodos de las direcciones de la enumeración Direccion.
     * En caso de encontrar la red, realiza una copia, envía su información a los nodos conectados y al gateway, y
     * añade su información a su propia copia de la red.
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
        InfoNodo infoNodo = new InfoNodo(direccion.getDireccionIP(), clavePublica, direccion.getPuerto());
        if (redMultipleGateway == null) {
            System.out.println("No se pudo copiar un Informacion de la Red");
            System.out.println("Se crea la Informacion de la Red");
            redMultipleGateway = new RedMultipleGateway();
        } else {
            System.out.println("Copia de InfoRed creada");
            salida.broadcastInformacionNodo(redMultipleGateway.getPuertos(), infoNodo);
        }
        redMultipleGateway.addNode(infoNodo);
        salida.enviarInfoNodo(Direccion.DIRECCION_GATEWAY.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getPuerto(), infoNodo);
    }

    /**
     * Envía la red de este nodo a otro nodo que envió su dirección para pedir la información actual de la red.
     * @param direccion dirección del nodo que pidió la información de la red.
     */
    @Override
    public void enviarInfoRed(Direccion direccion) {
        salida.enviarInfoRed(redMultipleGateway, direccion.getDireccionIP(), direccion.getPuerto());
    }

    /**
     * Comprueba la cantidad de nodos mínimos para el inicio de una ejecución.
     * @return true si existe la cantidad de nodos mínimos.
     */
    public boolean comprobarCantidadMinimaDeNodos() {
        return redMultipleGateway.obtenerCantidadDeNodos() >= MinimoDeNodos.MIN_GATEWAY_MULTIPLE.getCantidad();
    }

    /**
     * Crea una transacción, crea un mensaje con esa transacción y se lo envía al gateway.
     * @param monto dinero de la transacción.
     * @param direccionDestinatario dirección IP del nodo destinatario.
     * @param tipo tipo de la transacción.
     */
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
            TransaccionMultiple transaccionMultiple = new TransaccionMultiple(tipo, direccion.getDireccionIP(), direccionDestinatario, monto,
                    System.currentTimeMillis(), TARIFA_TRANSACCION, clavePrivada);
            Mensaje mensaje = new Mensaje(direccion.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getDireccionIP(),
                    RsaUtil.sign(HashUtil.SHA256(transaccionMultiple.toString()), clavePrivada),
                    System.currentTimeMillis(), 0, transaccionMultiple);
            // System.out.println("Mensaje creado");
            salida.enviarMensaje(Direccion.DIRECCION_GATEWAY.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getPuerto(), mensaje);
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
    public synchronized void recibirMensaje(Mensaje mensaje) {
        int tipoDeMensaje = mensaje.getTipoDeContenido();
        Object contenido = mensaje.getContenido();
        if (tipoDeMensaje == 1) {
            // System.out.println("Bloque recibido");
            BloqueMultiple bloqueMultiple = (BloqueMultiple) contenido;
            String direccionDelNodo = mensaje.getDireccionRemitente();
            String firma = mensaje.getFirma();
            recibirBloque(bloqueMultiple, firma, direccionDelNodo);
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
                redMultipleGateway.obtenerClavePublicaPorDireccion(transaccionMultiple.getDireccionRemitente()));
    }

    /**
     * Agrega el bloque a la lista de bloques del HashMap de bloques en espera. En caso de agregar dos bloques del mismo
     * tipo se llamará al método que realiza la comparación de bloques.
     * @param bloqueMultiple bloque recibido.
     * @param firma firma del mensaje con el bloque recibido.
     * @param direccionDelNodo dirección del nodo que envió el mensaje.
     */
    public synchronized void recibirBloque(BloqueMultiple bloqueMultiple, String firma, String direccionDelNodo) {
        try {
            if (RsaUtil.verify(HashUtil.SHA256(bloqueMultiple.toString()), firma,
                    redMultipleGateway.obtenerClavePublicaPorDireccion(direccionDelNodo))) {
                Tipo tipo = bloqueMultiple.getTipo();
                bloquesEnEspera.get(tipo).add(bloqueMultiple);
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

    /**
     * Compara los hashes de dos bloques del tipo especificado. En caso de que los hashes sean iguales, se agrega
     * el bloque del nodo con el menor id y se vacia el buffer de bloques en espera del tipo epecificado. Se almacena
     * el id del nodo con el menor id como nodo escogido.
     * @param tipo tipo de blockchain especificado.
     */
    private void compararBloques(Tipo tipo) {
        List<BloqueMultiple> bloquesAComparar = bloquesEnEspera.get(tipo);
        BloqueMultiple primerBloqueMultiple = bloquesAComparar.get(0);
        BloqueMultiple segundoBloqueMultiple = bloquesAComparar.get(1);
        if (primerBloqueMultiple.getFooter().getHash().equals(segundoBloqueMultiple.getFooter().getHash())) {
            System.out.println("Creación correcta");
            if (tipo.equals(Tipo.LOGICO1)) {
                redMultipleGateway.getNodosEscogidos1().get(Tipo.LOGICO2).add(-1);
                redMultipleGateway.getNodosEscogidos2().get(Tipo.LOGICO2).add(-1);
            } else {
                redMultipleGateway.getNodosEscogidos1().get(Tipo.LOGICO1).add(-1);
                redMultipleGateway.getNodosEscogidos2().get(Tipo.LOGICO1).add(-1);
            }
            if (primerBloqueMultiple.getIdNodoMinero() > segundoBloqueMultiple.getIdNodoMinero()) {
                redMultipleGateway.getNodosEscogidos1().get(tipo).add(segundoBloqueMultiple.getIdNodoMinero());
                redMultipleGateway.getNodosEscogidos2().get(tipo).add(primerBloqueMultiple.getIdNodoMinero());
                agregarBloque(segundoBloqueMultiple);
            } else {
                redMultipleGateway.getNodosEscogidos1().get(tipo).add(primerBloqueMultiple.getIdNodoMinero());
                redMultipleGateway.getNodosEscogidos2().get(tipo).add(segundoBloqueMultiple.getIdNodoMinero());
                agregarBloque(primerBloqueMultiple);
            }
        } else {
            System.out.println("---------------ERROR--------------");
        }
        System.out.println(redMultipleGateway.getStats());
        bloquesEnEspera.put(tipo, new ArrayList<>());
    }

    /**
     * Agrega el bloque recibido al blockchain de la red, también se llama a los métodos para actualizar el tiempo de
     * busqueda y el número de bloques por tipo.
     * @param bloqueMultiple bloque recibido.
     */
    public synchronized void agregarBloque(BloqueMultiple bloqueMultiple) {
        if (!bloqueMultiple.getDireccionNodoMinero().equals("Master"))
            updateAllWallet(bloqueMultiple);
        actualizarST(bloqueMultiple.getTiempoDeBusqueda());
        actualizarNBOfBlockOfType(bloqueMultiple.getTipo());
        redMultipleGateway.agregarBloque(bloqueMultiple);
        System.out.println("\n///-----------------------------------///");
        System.out.println("Bloque agregado");
        System.out.println("///-----------------------------------///\n");
    }

    /**
     * Instancia un bloque y lo envía a todos los nodos de la red y al gateway.
     * @param tipo tipo de blockchain.
     * @param posiblesTransacciones transacciones que se van a utilizar en el bloque.
     * @throws Exception si no se verifican las transacciones.
     */
    public void generarBloque(Tipo tipo, List<TransaccionMultiple> posiblesTransacciones) throws Exception {
        System.out.println("--- Creando bloque " + tipo + " ---");
        for (TransaccionMultiple transaccionMultiple : posiblesTransacciones) {
            //System.out.println("Firma: " + transaccion.getFirma());
            if (!verificarTransaccion(transaccionMultiple)) {
                System.out.println("---------------ERROR--------------");
                return;
            }
        }
        long inicioBusqueda = System.nanoTime();
        BlockchainMultiple blockchainMultiple = redMultipleGateway.getBlockchain();
        BloqueMultiple bloqueMultiplePrevioFisico = blockchainMultiple.obtenerUltimoBloque();
        BloqueMultiple bloqueMultiplePrevioLogico = blockchainMultiple.buscarBloquePrevioLogico(tipo,
                blockchainMultiple.obtenerCantidadDeBloques() - 1);
        long finBusqueda = System.nanoTime();
        long tiempoDelUltimoBloqueLogico = bloqueMultiplePrevioLogico.getHeader().getMarcaDeTiempoDeCreacion();
        while (true) {
            if (System.currentTimeMillis() - tiempoDelUltimoBloqueLogico > 10000) { // Garantiza los 10 segundos minimos
                break;
            }
            System.out.print("");
        }
        BloqueMultiple bloqueMultiple = new BloqueMultiple(bloqueMultiplePrevioFisico, bloqueMultiplePrevioLogico, posiblesTransacciones,
                (double) (finBusqueda - inicioBusqueda), tipo);
        bloqueMultiple.setIdNodoMinero(this.id);
        bloqueMultiple.setDireccionNodoMinero(this.direccion.getDireccionIP());
        // System.out.println("Block has been forged by " + this.name);
        try {
            Mensaje mensaje = new Mensaje(this.direccion.getDireccionIP(), "ALL",
                    RsaUtil.sign(HashUtil.SHA256(bloqueMultiple.toString()), this.clavePrivada),
                    System.currentTimeMillis(),
                    1, bloqueMultiple);
            System.out.println("Envio de bloque");
            salida.broadcastMensaje(mensaje, redMultipleGateway.getPuertos());
            salida.enviarMensaje(Direccion.DIRECCION_GATEWAY.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getPuerto(), mensaje);
            System.out.println("Fin de envio de bloque");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error signing");
        }
        // System.out.println("---------------------------------------------------");
    }

    /**
     * Actualiza el tiempo de busqueda de cada bloque en la red.
     * @param st tiempo de busqueda del último bloque.
     */
    public void actualizarST(double st) {
        redMultipleGateway.getSearchTimes().add(st);
    }

    /**
     * Actualiza el número de bloques por tipo de la red.
     * @param tipo tipo de la lista de número de bloques que se va a actualizar.
     */
    public void actualizarNBOfBlockOfType(Tipo tipo) {
        if (tipo.equals(Tipo.LOGICO1)) {
            redMultipleGateway.NB_OF_BLOCK_OF_TYPE1_CREATED.add(redMultipleGateway.NB_OF_BLOCK_OF_TYPE1_CREATED.size() + 1);
        } else {
            redMultipleGateway.NB_OF_BLOCK_OF_TYPE2_CREATED.add(redMultipleGateway.NB_OF_BLOCK_OF_TYPE2_CREATED.size() + 1);
        }
    }

    /**
     * Actualiza las billeteras de todos los nodos que participaron en las transacciones procesadas en un bloque.
     * @param bloqueMultiple bloque recibido.
     */
    private void updateAllWallet(BloqueMultiple bloqueMultiple) {
        double totalFee = 0;
        List<TransaccionMultiple> transacciones = bloqueMultiple.getTransaction();
        double montoTotal = 0;
        Tipo tipo = bloqueMultiple.getTipo();
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
        if (bloqueMultiple.getDireccionNodoMinero().equals(direccion.getDireccionIP())) {
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
            redMultipleGateway.getExchangeMoney1().add(amount);
            redMultipleGateway.getExchangeMoney2().add(0.);
        } else {
            redMultipleGateway.getExchangeMoney1().add(0.);
            redMultipleGateway.getExchangeMoney2().add(amount);
        }
    }
}
