package tradicional.nodo.gatewayVersion;

import general.constantes.Direccion;
import general.nodo.Gateway;
import general.constantes.MinimoDeNodos;
import tradicional.blockchain.BloqueTradicional;
import general.mensajes.InfoNodo;
import general.mensajes.Mensaje;
import tradicional.mensajes.TransaccionTradicional;
import general.utils.HashUtil;
import general.utils.RsaUtil;

import java.util.ArrayList;
import java.util.List;

public class GatewayTradicional extends Gateway {

    private ArrayList<TransaccionTradicional> transaccionesPendientes = new ArrayList<>();
    private ArrayList<TransaccionTradicional> transaccionesEscogidas = new ArrayList<>();
    private List<BloqueTradicional> bloquesEnEspera = new ArrayList<>();

    public GatewayTradicional(Direccion direccion) {
        super(direccion);
    }

    public boolean comprobarCantidadMinimaDeNodos() {
        return puertos.keySet().size() >= MinimoDeNodos.MIN_GATEWAY_TRADICIONAL.getCantidad();
    }

    public synchronized void recibirMensaje(Mensaje mensaje) throws Exception {
        int tipoDeMensaje = mensaje.getTipoDeContenido();
        Object contenido = mensaje.getContenido();
        if (!RsaUtil.verify(HashUtil.SHA256(contenido.toString()), mensaje.getFirma(),
                keyTable.get(mensaje.getDireccionRemitente()))){
            System.out.println("Error");
            return;
        }
        if (tipoDeMensaje == 0) {
            // System.out.println("Transaccion recibida");
            TransaccionTradicional transaccionTradicional = (TransaccionTradicional) (contenido);
            recibirTransaccion(transaccionTradicional);
        }
        if (tipoDeMensaje == 1) {
            System.out.println("Bloque recibido");
            BloqueTradicional bloqueTradicional = (BloqueTradicional) contenido;
            recibirBloque(bloqueTradicional);
        }
    }

    public synchronized void recibirBloque(BloqueTradicional bloqueTradicional) {
        try {
                bloquesEnEspera.add(bloqueTradicional);
                if (bloquesEnEspera.size()==2){
                    compararBloques();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void compararBloques() {
        BloqueTradicional primerBloqueTradicional = bloquesEnEspera.get(0);
        BloqueTradicional segundoBloqueTradicional = bloquesEnEspera.get(1);
        if (primerBloqueTradicional.getFooter().getHash().equals(segundoBloqueTradicional.getFooter().getHash())){
            System.out.println("Creación correcta");
            actualizarTransaccionesPendientes();
            actualizarTransaccionesEscogidas(true);
            System.out.println("Cantidad de bloques: " + ++contadorDeBloques);
        } else{
            System.out.println("---------------ERROR--------------");
            actualizarTransaccionesEscogidas(false);
        }
        bloquesEnEspera = new ArrayList<>();
    }

    public synchronized void recibirTransaccion(TransaccionTradicional transaccionTradicional) {
        transaccionesPendientes.add(transaccionTradicional);
    }

    public List<TransaccionTradicional> escogerTransacciones() {
        for (int i = 0; (i < TRANSACCIONES_MAXIMAS_POR_BLOQUE) && (i < transaccionesPendientes.size()); i++) {
            transaccionesEscogidas.add(transaccionesPendientes.get(i));
        }
        return transaccionesEscogidas;
    }

    public void actualizarTransaccionesEscogidas(boolean seCreoExitosamenteElBloque){
        transaccionesEscogidas = new ArrayList<>();
        if (seCreoExitosamenteElBloque) {
            escogerTransacciones();
        }
    }

    public void actualizarTransaccionesPendientes() {
        for (TransaccionTradicional transaccionTradicional : transaccionesEscogidas) {
            transaccionesPendientes.remove(transaccionTradicional);
        }
    }

    public void agregarNodo(InfoNodo infoNodo){
        String direccion = infoNodo.getDireccion();
        try {
            keyTable.put(direccion, infoNodo.getClavePublica());
        } catch (Exception e) {
            e.printStackTrace();
        }
        puertos.put(direccion, infoNodo.getPuerto());
        nodosPosibles.add(direccion);
    }

    public void mandarCrearBloque(String direccionDeNodo, int puerto, List<TransaccionTradicional> transacciones) {
        salida.mandarACrearBloque(direccionDeNodo, puerto, transacciones);
    }

    public void reiniciarNodosPosibles() {
        nodosPosibles.addAll(nodosSeleccionados);
        nodosSeleccionados = new ArrayList<>();
    }

}
