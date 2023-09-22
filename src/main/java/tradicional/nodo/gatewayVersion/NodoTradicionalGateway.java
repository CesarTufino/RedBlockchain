package tradicional.nodo.gatewayVersion;

import java.util.ArrayList;
import java.util.List;

import general.constantes.Direccion;
import general.nodo.Nodo;
import general.nodo.Red;
import general.constantes.MinimoDeNodos;
import tradicional.blockchain.BloqueTradicional;
import general.mensajes.InfoNodo;
import general.mensajes.Mensaje;
import tradicional.mensajes.TransaccionTradicional;
import general.utils.*;

public class NodoTradicionalGateway extends Nodo {

    private RedTradicionalGateway redTradicionalGateway;
    private List<BloqueTradicional> bloquesEnEspera = new ArrayList<>();

    public NodoTradicionalGateway(int id, Direccion direccion) {
        super(id, direccion);
    }

    public RedTradicionalGateway getRed() {
        return redTradicionalGateway;
    }

    public void setRed(Red red) {
        this.redTradicionalGateway = (RedTradicionalGateway) red;
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
        if (redTradicionalGateway == null) {
            System.out.println("No se pudo copiar un Informacion de la Red");
            System.out.println("Se crea la Informacion de la Red");
            redTradicionalGateway = new RedTradicionalGateway();
        } else {
            System.out.println("Copia de InfoRed creada");
            salida.broadcastInformacionNodo(redTradicionalGateway.getPuertos(), infoNodo);
        }
        redTradicionalGateway.addNode(infoNodo);
        salida.enviarInfoNodo(Direccion.DIRECCION_GATEWAY.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getPuerto(), infoNodo);
    }

    public void enviarInfoRed(Direccion direccion) {
        salida.enviarInfoRed(redTradicionalGateway, direccion.getDireccionIP(), direccion.getPuerto());
    }

    public boolean comprobarCantidadMinimaDeNodos() {
        return redTradicionalGateway.obtenerCantidadDeNodos() >= MinimoDeNodos.MIN_GATEWAY_TRADICIONAL.getCantidad();
    }

    public void enviarDinero(double monto, String direccionDestinatario) {
        System.out.println("Inicio de transacción");
        if (billetera1 - monto * (1 + TARIFA_TRANSACCION) < 0) {
            System.out.println("-Transacción rechazada-");
            return;
        }
        try {
            TransaccionTradicional transaccionTradicional = new TransaccionTradicional(direccion.getDireccionIP(),
                    direccionDestinatario, monto, System.currentTimeMillis(), TARIFA_TRANSACCION, clavePrivada);
            Mensaje mensaje = new Mensaje(direccion.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getDireccionIP(), RsaUtil.sign(HashUtil.SHA256(transaccionTradicional.toString()), clavePrivada),
                    System.currentTimeMillis(), 0, transaccionTradicional);
            // System.out.println("Mensaje creado");
            salida.enviarMensaje(Direccion.DIRECCION_GATEWAY.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getPuerto(), mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void recibirDinero(double monto) {
        billetera1 += monto;
    }

    public synchronized void recibirMensaje(Mensaje mensaje) {
        int tipoDeMensaje = mensaje.getTipoDeContenido();
        Object contenido = mensaje.getContenido();
        if (tipoDeMensaje == 1) {
            // System.out.println("Bloque recibido");
            BloqueTradicional bloqueTradicional = (BloqueTradicional) contenido;
            String direccionDelNodo = mensaje.getDireccionRemitente();
            String firma = mensaje.getFirma();
            recibirBloque(bloqueTradicional, firma, direccionDelNodo);
        }
    }

    public boolean verificarTransaccion(TransaccionTradicional transaccionTradicional) throws Exception {
        return RsaUtil.verify(transaccionTradicional.toString(), transaccionTradicional.getFirma(),
                redTradicionalGateway.obtenerClavePublicaPorDireccion(transaccionTradicional.getDireccionRemitente()));
    }

    public synchronized void recibirBloque(BloqueTradicional bloqueTradicional, String firma, String direccionDelNodo) {
        try {
            if (RsaUtil.verify(HashUtil.SHA256(bloqueTradicional.toString()), firma,
                    redTradicionalGateway.obtenerClavePublicaPorDireccion(direccionDelNodo))) {
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
                redTradicionalGateway.getNodosEscogidos1().add(segundoBloqueTradicional.getIdNodoMinero());
                redTradicionalGateway.getNodosEscogidos2().add(primerBloqueTradicional.getIdNodoMinero());
                agregarBloque(segundoBloqueTradicional);
            } else {
                redTradicionalGateway.getNodosEscogidos1().add(primerBloqueTradicional.getIdNodoMinero());
                redTradicionalGateway.getNodosEscogidos2().add(segundoBloqueTradicional.getIdNodoMinero());
                agregarBloque(primerBloqueTradicional);
            }
        } else {
            System.out.println("---------------ERROR--------------");
        }
        bloquesEnEspera = new ArrayList<>();
        System.out.println(redTradicionalGateway.getStats());
    }

    public synchronized void agregarBloque(BloqueTradicional bloqueTradicional) {
        if (!bloqueTradicional.getDireccionNodoMinero().equals("Master"))
            updateAllWallet(bloqueTradicional);
        actualizarST(bloqueTradicional.getTiempoDeBusqueda());
        actualizarNBOfBlock();
        redTradicionalGateway.agregarBloque(bloqueTradicional);
        System.out.println("\n///-----------------------------------///");
        System.out.println("Bloque agregado");
        System.out.println("///-----------------------------------///\n");
    }

    public void generarBloque(List<TransaccionTradicional> transaccionesDelBloque) throws Exception {
        // System.out.println("---------------------------------------------------");
        for (TransaccionTradicional transaccionTradicional : transaccionesDelBloque) {
            if (!verificarTransaccion(transaccionTradicional)) {
                System.out.println("---------------ERROR--------------");
                return;
            }
        }
        System.out.println("---------------Se crea bloque---------------");
        long inicioBusqueda = System.nanoTime();
        BloqueTradicional bloqueTradicionalPrevio = redTradicionalGateway.getBlockchain().obtenerUltimoBloque();
        long finBusqueda = System.nanoTime();
        long tiempoDelUltimoBloque = bloqueTradicionalPrevio.getHeader().getMarcaDeTiempoDeCreacion();
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
            Mensaje mensaje = new Mensaje(this.direccion.getDireccionIP(), "ALL",
                    RsaUtil.sign(HashUtil.SHA256(bloqueTradicional.toString()), this.clavePrivada),
                    System.currentTimeMillis(),
                    1, bloqueTradicional);
            System.out.println("Envio de bloque");
            salida.broadcastMensaje(mensaje, redTradicionalGateway.getPuertos());
            salida.enviarMensaje(Direccion.DIRECCION_GATEWAY.getDireccionIP(), Direccion.DIRECCION_GATEWAY.getPuerto(), mensaje);
            System.out.println("Fin de envio de bloque");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error signing");
        }
        // System.out.println("---------------------------------------------------");
    }

    public void actualizarST(double st) {
        redTradicionalGateway.getSearchTimes().add(st);
    }

    public void actualizarNBOfBlock() {
        redTradicionalGateway.getNB_OF_BLOCK_CREATED().add(redTradicionalGateway.getNB_OF_BLOCK_CREATED().size() + 1);
        // Último en ejecutarse

    }

    /**
     * Método que actualiza las billeteras de todos los nodos que participaron.
     *
     * @param bloqueTradicional Bloque.
     */
    private void updateAllWallet(BloqueTradicional bloqueTradicional) {
        double totalFee = 0;
        List<TransaccionTradicional> transacciones = bloqueTradicional.getTransaction();
        double montoTotal = 0;
        for (TransaccionTradicional transaccionTradicional : transacciones) {
            transaccionTradicional.confirmar();
            double montoTransaccion = transaccionTradicional.getMonto();
            double tarifaTransaccion = transaccionTradicional.getTarifa() * montoTransaccion;
            String toAddress = transaccionTradicional.getDireccionDestinatario();
            montoTotal += montoTransaccion;
            totalFee += tarifaTransaccion;
            // Actualización de la billetera del destinatario de la transacción.
            if (toAddress.equals(direccion.getDireccionIP())) {
                recibirDinero(montoTransaccion);
            }
            // Actualización de la billetera del emisor de la transacción.
            if (transaccionTradicional.getDireccionRemitente().equals(direccion.getDireccionIP())) {
                recibirDinero(-(montoTransaccion + tarifaTransaccion));
            }
        }
        // Actualización del minero.
        if (bloqueTradicional.getDireccionNodoMinero().equals(direccion.getDireccionIP())) {
            recibirDinero(totalFee);
        }
        actualizarExchangeMoney(montoTotal);
    }

    public void actualizarExchangeMoney(double monto) {
        redTradicionalGateway.getExchangeMoney1().add(monto);
    }

}
