package gatewayVersion.blockchainMultipleAletorio.nodo;

import direcciones.Direccion;
import gatewayVersion.blockchainMultipleAletorio.blockchain.Bloque;
import gatewayVersion.blockchainMultipleAletorio.conexion.Entrada;
import gatewayVersion.blockchainMultipleAletorio.conexion.Salida;
import gatewayVersion.blockchainMultipleAletorio.mensajes.InfoNodo;
import gatewayVersion.blockchainMultipleAletorio.mensajes.Mensaje;
import gatewayVersion.blockchainMultipleAletorio.mensajes.Paquete;
import gatewayVersion.blockchainMultipleAletorio.mensajes.Transaccion;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gateway {

    private final int TRANSACCIONES_MAXIMAS_POR_BLOQUE = 10;
    private ArrayList<Transaccion> transaccionesPendientesTipo1 = new ArrayList<>();
    private ArrayList<Transaccion> transaccionesPendientesTipo2 = new ArrayList<>();
    private ArrayList<Transaccion> transaccionesEscogidasTipo1 = new ArrayList<>();
    private ArrayList<Transaccion> transaccionesEscogidasTipo2 = new ArrayList<>();
    private Direccion direccion;
    private Map<String, PublicKey> keyTable = new HashMap<>();

    private Map<String, Integer> puertos = new HashMap<>();
    private List<String> nodosSeleccionados = new ArrayList<>();
    private List<String> nodosPosibles = new ArrayList<>();
    private Salida salida;
    private List<Bloque> bloquesEnEsperaTipo1 = new ArrayList<>();
    private List<Bloque> bloquesEnEsperaTipo2 = new ArrayList<>();
    private long tiempoDeCreacionDeUltimoBloqueTipo1;
    private long tiempoDeCreacionDeUltimoBloqueTipo2;
    private final String TYPE1 = "Type1";

    public Gateway(Direccion direccion) {
        this.direccion = direccion;
        this.salida = new Salida();
    }

    public long getTiempoDeCreacionDeUltimoBloqueTipo1() {
        return tiempoDeCreacionDeUltimoBloqueTipo1;
    }

    public long getTiempoDeCreacionDeUltimoBloqueTipo2() {
        return tiempoDeCreacionDeUltimoBloqueTipo2;
    }

    public List<Bloque> getBloquesEnEsperaTipo1() {
        return bloquesEnEsperaTipo1;
    }

    public List<Bloque> getBloquesEnEsperaTipo2() {
        return bloquesEnEsperaTipo2;
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

    public ArrayList<Transaccion> getTransaccionesPendientesTipo1() {
        return transaccionesPendientesTipo1;
    }

    public ArrayList<Transaccion> getTransaccionesPendientesTipo2() {
        return transaccionesPendientesTipo2;
    }

    public boolean comprobarCantidadMinimaDeNodos() {
        return puertos.keySet().size() >= 4;
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
            if (bloque.getTipo().equals("Type1")) {
                bloquesEnEsperaTipo1.add(bloque);
            } else {
                bloquesEnEsperaTipo2.add(bloque);
            }
            if (bloquesEnEsperaTipo1.size() == 2) {
                compararBloques("Type1");
            }
            if (bloquesEnEsperaTipo2.size() == 2) {
                compararBloques("Type2");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void compararBloques(String tipo) {
        if (tipo.equals("Type1")) {
            if (bloquesEnEsperaTipo1.get(0).getFooter().getHash().equals(bloquesEnEsperaTipo1.get(1).getFooter().getHash())) {
                tiempoDeCreacionDeUltimoBloqueTipo1 = bloquesEnEsperaTipo1.get(0).getHeader().getMarcaDeTiempo();
                actualizarTransaccionesPendientes(tipo);
                actualizarTransaccionesEscogidas(true, tipo);
            } else {
                System.out.println("---------------ERROR--------------");
            }
            bloquesEnEsperaTipo1 = new ArrayList<>();
        } else {
            if (bloquesEnEsperaTipo2.get(0).getFooter().getHash().equals(bloquesEnEsperaTipo2.get(1).getFooter().getHash())) {
                tiempoDeCreacionDeUltimoBloqueTipo2 = bloquesEnEsperaTipo2.get(0).getHeader().getMarcaDeTiempo();
                actualizarTransaccionesPendientes(tipo);
                actualizarTransaccionesEscogidas(true, tipo);
            } else {
                System.out.println("---------------ERROR--------------");
            }
            bloquesEnEsperaTipo2 = new ArrayList<>();
        }

    }

    public synchronized void recibirTransaccion(Transaccion transaccion) {
        if (transaccion.getTipo().equals("Type1")) {
            transaccionesPendientesTipo1.add(transaccion);
        } else {
            transaccionesPendientesTipo2.add(transaccion);
        }
    }

    public List<Transaccion> escogerTransacciones(String tipo) {
        if (tipo.equals("Type1")) {
            for (int i = 0; (i < TRANSACCIONES_MAXIMAS_POR_BLOQUE) && (i < transaccionesPendientesTipo1.size()); i++) {
                transaccionesEscogidasTipo1.add(transaccionesPendientesTipo1.get(i));
            }
            return transaccionesEscogidasTipo1;
        } else {
            for (int i = 0; (i < TRANSACCIONES_MAXIMAS_POR_BLOQUE) && (i < transaccionesPendientesTipo2.size()); i++) {
                transaccionesEscogidasTipo2.add(transaccionesPendientesTipo2.get(i));
            }
            return transaccionesEscogidasTipo2;
        }

    }

    public void actualizarTransaccionesEscogidas(boolean seCreoExitosamenteElBloque, String tipo) {
        if (tipo.equals("Type1")) {
            transaccionesEscogidasTipo1 = new ArrayList<>();
        } else {
            transaccionesEscogidasTipo2 = new ArrayList<>();
        }
        if (seCreoExitosamenteElBloque) {
            escogerTransacciones(tipo);
        }
    }

    public void actualizarTransaccionesPendientes(String tipo) {
        if (tipo.equals("Type1")){
            for (Transaccion transaccion : transaccionesEscogidasTipo1) {
                transaccionesPendientesTipo1.remove(transaccion);
            }
        } else{
            for (Transaccion transaccion : transaccionesEscogidasTipo2) {
                transaccionesPendientesTipo2.remove(transaccion);
            }
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
        for (String direccion : nodosSeleccionados) {
            nodosPosibles.add(direccion);
        }
        nodosSeleccionados = new ArrayList<>();
    }
}
