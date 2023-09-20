package multiple.nodo.posVersion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import general.constantes.Direccion;
import general.constantes.Tipo;
import multiple.blockchain.BlockchainMultiple;
import multiple.blockchain.BloqueMultiple;
import general.mensajes.InfoNodo;
import general.mensajes.Mensaje;
import multiple.mensajes.TransaccionMultiple;
import general.nodo.Nodo;
import general.nodo.Red;
import general.utils.HashUtil;
import general.utils.RsaUtil;

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

    public RedMultiplePos getRed() {
        return redMultiplePos;
    }

    public void setRed(Red red) {
        this.redMultiplePos = (RedMultiplePos) red;
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

    public void enviarInfoRed(Direccion direccion) {
        salida.enviarInfoRed(redMultiplePos, direccion.getDireccionIP(), direccion.getPuerto());
    }

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

    public boolean verificarTransaccion(TransaccionMultiple transaccionMultiple) throws Exception {
        return RsaUtil.verify(transaccionMultiple.toString(), transaccionMultiple.getFirma(),
                redMultiplePos.obtenerClavePublicaPorDireccion(transaccionMultiple.getDireccionRemitente()));
    }

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

    public synchronized void actualizarListaDeTransacciones(BloqueMultiple bloque) {
        List<TransaccionMultiple> transacciones = bloque.getTransaction();
        for (TransaccionMultiple transaccionMultipleDeBloque : transacciones) {
            transaccionesPendientes.remove(transaccionMultipleDeBloque);
            actualizarNbTransPorTipo(transaccionMultipleDeBloque.getTipo(), -1);
        }
    }

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

    public void actualizarNbTransPorTipo(Tipo tipo, int cantidad) {
        HashMap<Tipo, Integer> nbTransParType = (HashMap<Tipo, Integer>) redMultiplePos.getNbTransParType();
        nbTransParType.put(tipo, nbTransParType.get(tipo) + cantidad);
        //red.setNbTransParType();
    }

    public void actualizarST(double st) {
        redMultiplePos.getSearchTimes().add(st);
    }

    public void actualizarNBOfBlockOfType(Tipo tipo) {
        if (tipo.equals(Tipo.LOGICO1)) {
            redMultiplePos.NB_OF_BLOCK_OF_TYPE1_CREATED.add(redMultiplePos.NB_OF_BLOCK_OF_TYPE1_CREATED.size() + 1);
        } else {
            redMultiplePos.NB_OF_BLOCK_OF_TYPE2_CREATED.add(redMultiplePos.NB_OF_BLOCK_OF_TYPE2_CREATED.size() + 1);
        }
        // Último en ejecutarse

    }

    public boolean comprobarCantidadMinimaDeNodos() {
        // System.out.println(infoRed.comprobarCantidadDeNodos());
        return redMultiplePos.obtenerCantidadDeNodos() >= 3;
    }

    /**
     * Método que actualiza las billeteras de todos los nodos que participaron.
     *
     * @param bloque Bloque.
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
