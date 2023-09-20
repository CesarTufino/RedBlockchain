package multiple.nodo.gatewayVersion;

import general.constantes.Direccion;
import general.constantes.Tipo;
import multiple.blockchain.BloqueMultiple;
import general.mensajes.InfoNodo;
import general.mensajes.Mensaje;
import multiple.mensajes.Paquete;
import multiple.mensajes.TransaccionMultiple;
import general.nodo.Gateway;
import general.utils.HashUtil;
import general.utils.RsaUtil;

import java.security.SecureRandom;
import java.util.*;

public class GatewayMultiple extends Gateway {

    private HashMap<Tipo, ArrayList<TransaccionMultiple>> transaccionesPendientes = new HashMap<>();
    private HashMap<Tipo, ArrayList<TransaccionMultiple>> transaccionesEscogidas = new HashMap<>();
    private HashMap<Tipo, List<BloqueMultiple>> bloquesEnEspera = new HashMap<>();
    private Map<Tipo, Long> tiempoDeCreacionDeUltimoBloque = new HashMap<>();

    public GatewayMultiple(Direccion direccion) {
        super(direccion);
        this.transaccionesPendientes.put(Tipo.LOGICO1, new ArrayList<>());
        this.transaccionesPendientes.put(Tipo.LOGICO2, new ArrayList<>());
        this.transaccionesEscogidas.put(Tipo.LOGICO1, new ArrayList<>());
        this.transaccionesEscogidas.put(Tipo.LOGICO2, new ArrayList<>());
        this.bloquesEnEspera.put(Tipo.LOGICO1, new ArrayList<>());
        this.bloquesEnEspera.put(Tipo.LOGICO2, new ArrayList<>());
        this.tiempoDeCreacionDeUltimoBloque.put(Tipo.LOGICO1, 0L);
        this.tiempoDeCreacionDeUltimoBloque.put(Tipo.LOGICO2, 0L);
    }

    public HashMap<Tipo, ArrayList<TransaccionMultiple>> getTransaccionesPendientes() {
        return transaccionesPendientes;
    }

    public Map<Tipo, Long> getTiempoDeCreacionDeUltimoBloque() {
        return tiempoDeCreacionDeUltimoBloque;
    }

    public boolean comprobarCantidadMinimaDeNodos() {
        return puertos.keySet().size() >= 4;
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
            TransaccionMultiple transaccionMultiple = (TransaccionMultiple) (contenido);
            recibirTransaccion(transaccionMultiple);
        }
        if (tipoDeMensaje == 1) {
            System.out.println("Bloque recibido");
            BloqueMultiple bloqueMultiple = (BloqueMultiple) contenido;
            recibirBloque(bloqueMultiple);
        }
    }

    public synchronized void recibirBloque(BloqueMultiple bloqueMultiple) {
        try {
            Tipo tipo = bloqueMultiple.getTipo();
            bloquesEnEspera.get(tipo).add(bloqueMultiple);
            if (bloquesEnEspera.get(tipo).size() == 2) {
                compararBloques(tipo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void compararBloques(Tipo tipo) {
        List<BloqueMultiple> bloquesAComparar = bloquesEnEspera.get(tipo);
        BloqueMultiple primerBloqueMultiple = bloquesAComparar.get(0);
        BloqueMultiple segundoBloqueMultiple = bloquesAComparar.get(1);
        if (primerBloqueMultiple.getFooter().getHash().equals(segundoBloqueMultiple.getFooter().getHash())) {
            System.out.println("Creación correcta");
            if (primerBloqueMultiple.getIdNodoMinero() > segundoBloqueMultiple.getIdNodoMinero()) {
                tiempoDeCreacionDeUltimoBloque.put(tipo, segundoBloqueMultiple.getHeader().getMarcaDeTiempoDeCreacion());
            } else {
                tiempoDeCreacionDeUltimoBloque.put(tipo, primerBloqueMultiple.getHeader().getMarcaDeTiempoDeCreacion());
            }
            actualizarTransaccionesPendientes(tipo);
            actualizarTransaccionesEscogidas(true, tipo);
            System.out.println("Cantidad de bloques: " + ++contadorDeBloques);
        } else {
            System.out.println("---------------ERROR--------------");
            actualizarTransaccionesEscogidas(false, tipo);
        }
        bloquesEnEspera.put(tipo, new ArrayList<>());
    }

    public synchronized void recibirTransaccion(TransaccionMultiple transaccionMultiple) {
        Tipo tipo = transaccionMultiple.getTipo();
        transaccionesPendientes.get(tipo).add(transaccionMultiple);
    }

    public List<TransaccionMultiple> escogerTransacciones(Tipo tipo) {
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
        for (TransaccionMultiple transaccionMultiple : transaccionesEscogidas.get(tipo)) {
            transaccionesPendientes.get(tipo).remove(transaccionMultiple);
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
