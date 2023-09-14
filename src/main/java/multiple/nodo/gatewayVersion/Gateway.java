package multiple.nodo.gatewayVersion;

import constantes.Direccion;
import constantes.MaximoDeBloques;
import constantes.Tipo;
import multiple.blockchain.Bloque;
import multiple.conexion.Salida;
import multiple.mensajes.InfoNodo;
import multiple.mensajes.Mensaje;
import multiple.mensajes.Paquete;
import multiple.mensajes.Transaccion;

import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.*;

public class Gateway {

    private final int TRANSACCIONES_MAXIMAS_POR_BLOQUE = 10;
    private HashMap<Tipo, ArrayList<Transaccion>> transaccionesPendientes = new HashMap<>();
    private HashMap<Tipo, ArrayList<Transaccion>> transaccionesEscogidas = new HashMap<>();
    private List<String> nodosSeleccionados = new ArrayList<>();
    private List<String> nodosPosibles = new ArrayList<>();
    private Direccion direccion;
    private Salida salida;
    private HashMap<Tipo, List<Bloque>> bloquesEnEspera = new HashMap<>();
    private Map<String, PublicKey> keyTable = new HashMap<>();
    private Map<String, Integer> puertos = new HashMap<>();
    private Map<Tipo, Long> tiempoDeCreacionDeUltimoBloque = new HashMap<>();
    private int contadorDeBloques;

    public Gateway(Direccion direccion) {
        this.direccion = direccion;
        this.salida = new Salida();
        this.contadorDeBloques = 0;
        this.transaccionesPendientes.put(Tipo.LOGICO1, new ArrayList<>());
        this.transaccionesPendientes.put(Tipo.LOGICO2, new ArrayList<>());
        this.transaccionesEscogidas.put(Tipo.LOGICO1, new ArrayList<>());
        this.transaccionesEscogidas.put(Tipo.LOGICO2, new ArrayList<>());
        this.bloquesEnEspera.put(Tipo.LOGICO1, new ArrayList<>());
        this.bloquesEnEspera.put(Tipo.LOGICO2, new ArrayList<>());
        this.tiempoDeCreacionDeUltimoBloque.put(Tipo.LOGICO1, 0L);
        this.tiempoDeCreacionDeUltimoBloque.put(Tipo.LOGICO2, 0L);
    }

    public HashMap<Tipo, ArrayList<Transaccion>> getTransaccionesPendientes() {
        return transaccionesPendientes;
    }

    public HashMap<Tipo, ArrayList<Transaccion>> getTransaccionesEscogidas() {
        return transaccionesEscogidas;
    }

    public Map<Tipo, Long> getTiempoDeCreacionDeUltimoBloque() {
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
        return puertos.keySet().size() >= 4;
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
            System.out.println("Bloque recibido");
            Bloque bloque = (Bloque) contenido.get(0);
            recibirBloque(bloque);
        }
    }

    public synchronized void recibirBloque(Bloque bloque) {
        try {
            Tipo tipo = bloque.getTipo();
            bloquesEnEspera.get(tipo).add(bloque);
            if (bloquesEnEspera.get(tipo).size() == 2) {
                compararBloques(tipo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void compararBloques(Tipo tipo) {
        List<Bloque> bloquesAComparar = bloquesEnEspera.get(tipo);
        Bloque primerBloque = bloquesAComparar.get(0);
        Bloque segundoBloque = bloquesAComparar.get(1);
        if (primerBloque.getFooter().getHash().equals(segundoBloque.getFooter().getHash())) {
            System.out.println("Creación correcta");
            if (primerBloque.getIdNodoMinero() > segundoBloque.getIdNodoMinero()) {
                tiempoDeCreacionDeUltimoBloque.put(tipo, segundoBloque.getHeader().getMarcaDeTiempoDeCreacion());
            } else {
                tiempoDeCreacionDeUltimoBloque.put(tipo, primerBloque.getHeader().getMarcaDeTiempoDeCreacion());
            }
            actualizarTransaccionesPendientes(tipo);
            actualizarTransaccionesEscogidas(true, tipo);
            contadorDeBloques++;
        } else {
            System.out.println("---------------ERROR--------------");

            actualizarTransaccionesEscogidas(false, tipo);
        }
        bloquesEnEspera.put(tipo, new ArrayList<>());
        if (contadorDeBloques == MaximoDeBloques.MAX.getCantidad()) {
            System.exit(0);
        }
    }

    public synchronized void recibirTransaccion(Transaccion transaccion) {
        Tipo tipo = transaccion.getTipo();
        transaccionesPendientes.get(tipo).add(transaccion);
    }

    public List<Transaccion> escogerTransacciones(Tipo tipo) {
        for (int i = 0; (i < TRANSACCIONES_MAXIMAS_POR_BLOQUE) && (i < transaccionesPendientes.get(tipo).size()); i++) {
            transaccionesEscogidas.get(tipo).add(transaccionesPendientes.get(tipo).get(i));
        }
        return transaccionesEscogidas.get(tipo);
    }

    public void actualizarTransaccionesEscogidas(boolean seCreoExitosamenteElBloque, Tipo tipo) {
        transaccionesEscogidas.put(tipo, new ArrayList<>());
        if (seCreoExitosamenteElBloque) {
            escogerTransacciones(tipo);
        }
    }

    public void actualizarTransaccionesPendientes(Tipo tipo) {
        for (Transaccion transaccion : transaccionesEscogidas.get(tipo)) {
            transaccionesPendientes.get(tipo).remove(transaccion);
        }
    }

    public void agregarNodo(InfoNodo infoNodo) {
        String direccion = infoNodo.getDireccion();
        try {
            keyTable.put(direccion, infoNodo.getClavePublica());
        } catch (Exception e) {
            e.printStackTrace();
        }
        puertos.put(direccion, infoNodo.getPuerto());
        nodosPosibles.add(direccion);
    }

    public void mandarCrearBloque(String direccionDeNodo, int puerto, Paquete paquete) {
        salida.mandarACrearBloque(direccionDeNodo, puerto, paquete);
    }

    public void reiniciarNodosPosibles() {
        nodosPosibles.addAll(nodosSeleccionados);
        nodosSeleccionados = new ArrayList<>();
    }

    public String obtenerDireccionNodoPosible() {
        SecureRandom secureRandom = new SecureRandom();
        int semilla = secureRandom.nextInt();
        Random rnd = new Random(semilla);
        int numeroPseudoaleatorio = rnd.nextInt(getNodosPosibles().size());
        String direccionSeleccionada = getNodosPosibles().get(numeroPseudoaleatorio);
        getNodosSeleccionados().add(direccionSeleccionada);
        getNodosPosibles().remove(direccionSeleccionada);
        return direccionSeleccionada;
    }
}
