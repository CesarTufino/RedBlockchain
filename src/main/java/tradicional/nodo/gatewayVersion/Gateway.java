package tradicional.nodo.gatewayVersion;

import constantes.Direccion;
import tradicional.blockchain.BloqueTradicional;
import tradicional.conexion.Entrada;
import tradicional.conexion.Salida;
import tradicional.mensajes.InfoNodo;
import tradicional.mensajes.Mensaje;
import tradicional.mensajes.Transaccion;
import utils.HashUtil;
import utils.RsaUtil;

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
    private List<BloqueTradicional> bloquesEnEspera = new ArrayList<>();
    private long tiempoDeCreacionDeUltimoBloque;
    private int contadorDeBloques;

    public Gateway(Direccion direccion) {
        this.direccion = direccion;
        this.salida = new Salida();
        this.contadorDeBloques = 0;
        this.tiempoDeCreacionDeUltimoBloque = 0;
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

    public int getContadorDeBloques() {
        return contadorDeBloques;
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
            Transaccion transaccion = (Transaccion) (contenido);
            recibirTransaccion(transaccion);
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
            if (primerBloqueTradicional.getIdNodoMinero() > segundoBloqueTradicional.getIdNodoMinero()) {
                tiempoDeCreacionDeUltimoBloque = segundoBloqueTradicional.getHeader().getMarcaDeTiempo();
            } else{
                tiempoDeCreacionDeUltimoBloque = primerBloqueTradicional.getHeader().getMarcaDeTiempo();
            }
            actualizarTransaccionesPendientes();
            actualizarTransaccionesEscogidas(true);
            System.out.println("Cantidad de bloques: " + ++contadorDeBloques);
        } else{
            System.out.println("---------------ERROR--------------");
            actualizarTransaccionesEscogidas(false);
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
