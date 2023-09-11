package gatewayVersion.blockchainTradicional.nodo;

import direcciones.Direccion;
import gatewayVersion.blockchainTradicional.blockchain.Bloque;
import gatewayVersion.blockchainTradicional.conexion.Entrada;
import gatewayVersion.blockchainTradicional.conexion.Salida;
import gatewayVersion.blockchainTradicional.mensajes.InfoNodo;
import gatewayVersion.blockchainTradicional.mensajes.Mensaje;
import gatewayVersion.blockchainTradicional.mensajes.Transaccion;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gateway {

    private final int TRANSACCIONES_MAXIMAS_POR_BLOQUE = 10;
    private ArrayList<Transaccion> transaccionesPendientes = new ArrayList<>();
    private ArrayList<Transaccion> transaccionesEscogidas = new ArrayList<>();
    private Direccion direccion;
    private Map<String, PublicKey> keyTable = new HashMap<>();

    private Map<String, Integer> puertos = new HashMap<>();
    private List<String> nodosSeleccionados = new ArrayList<>();
    private List<String> nodosPosibles = new ArrayList<>();
    private Salida salida;
    private List<Bloque> bloquesEnEspera = new ArrayList<>();
    private long tiempoDeCreacionDeUltimoBloque;

    public Gateway(Direccion direccion) {
        this.direccion = direccion;
        this.salida = new Salida();
    }

    public long getTiempoDeCreacionDeUltimoBloque() {
        return tiempoDeCreacionDeUltimoBloque;
    }

    public List<String> getNodosSeleccionados() {
        return nodosSeleccionados;
    }

    public List<String> getNodosPosibles() {
        return nodosPosibles;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public Map<String, Integer> getPuertos() {
        return puertos;
    }

    public boolean comprobarCantidadMinimaDeNodos() {
        return puertos.keySet().size() >= 3;
    }

    public void empezarAEscuchar() throws IOException {
        Entrada hiloEntrada = new Entrada(this);
        hiloEntrada.start();
    }

    public synchronized void recibirMensaje(Mensaje mensaje) {
        int tipoDeMensaje = mensaje.getTipo();
        List<Object> contenido = mensaje.getContenido();
        if (tipoDeMensaje == 0) {
            // System.out.println("Transaccion recibida");
            Transaccion transaccion = (Transaccion) (contenido.get(0));
            recibirTransaccion(transaccion);
        }
        if (tipoDeMensaje == 1) {
            // System.out.println("Bloque recibido");
            Bloque bloque = (Bloque) contenido.get(0);
            String direccionDelNodo = mensaje.getDireccionRemitente();
            String firma = mensaje.getFirma();
            recibirBloque(bloque);
        }
    }

    public synchronized void recibirBloque(Bloque bloque) {
        //actualizarListaDeTransacciones(bloque);
        try {
                bloquesEnEspera.add(bloque);
                if (bloquesEnEspera.size()==2){
                    compararBloques();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compararBloques() {
        if (bloquesEnEspera.get(0).getFooter().getHash().equals(bloquesEnEspera.get(1).getFooter().getHash())){
            tiempoDeCreacionDeUltimoBloque = bloquesEnEspera.get(0).getHeader().getMarcaDeTiempo();
            actualizarTransaccionesPendientes();
            actualizarTransaccionesEscogidas(true);
        } else{
            System.out.println("---------------ERROR--------------");
        }
        bloquesEnEspera = new ArrayList<>();
    }

    public synchronized void recibirTransaccion(Transaccion transaccion) {
        transaccionesPendientes.add(transaccion);
    }

    public List<Transaccion> escogerTransacciones() {
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
        for (Transaccion transaccion : transaccionesEscogidas) {
            transaccionesPendientes.remove(transaccion);
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

    public void mandarCrearBloque(String direccionDeNodo, int puerto, List<Transaccion> transacciones) {
        salida.mandarACrearBloque(direccionDeNodo, puerto, transacciones);
    }

    public void reiniciarNodosPosibles() {
        for (String direccion : nodosSeleccionados) {
            nodosPosibles.add(direccion);
        }
        nodosSeleccionados = new ArrayList<>();
    }
}
