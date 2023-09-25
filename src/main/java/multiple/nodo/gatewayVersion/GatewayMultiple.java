package multiple.nodo.gatewayVersion;

import general.constantes.Direccion;
import general.constantes.Tipo;
import general.constantes.MinimoDeNodos;
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

/**
 * La clase GatewayMultiple representa el dispositivo intermedio utilizado para gestionar las transacciones de los
 * bloques en una red de blockchain multiple.
 */
public class GatewayMultiple extends Gateway {
    private HashMap<Tipo, ArrayList<TransaccionMultiple>> transaccionesPendientes = new HashMap<>();
    private HashMap<Tipo, ArrayList<TransaccionMultiple>> transaccionesEscogidas = new HashMap<>();
    private HashMap<Tipo, List<BloqueMultiple>> bloquesEnEspera = new HashMap<>();

    public GatewayMultiple(Direccion direccion) {
        super(direccion);
        this.transaccionesPendientes.put(Tipo.LOGICO1, new ArrayList<>());
        this.transaccionesPendientes.put(Tipo.LOGICO2, new ArrayList<>());
        this.transaccionesEscogidas.put(Tipo.LOGICO1, new ArrayList<>());
        this.transaccionesEscogidas.put(Tipo.LOGICO2, new ArrayList<>());
        this.bloquesEnEspera.put(Tipo.LOGICO1, new ArrayList<>());
        this.bloquesEnEspera.put(Tipo.LOGICO2, new ArrayList<>());
    }

    /**
     * Obtiene el HashMap de las transacciones pendientes por tipo de blockchain.
     * @return HashMap de las transacciones pendientes.
     */
    public HashMap<Tipo, ArrayList<TransaccionMultiple>> getTransaccionesPendientes() {
        return transaccionesPendientes;
    }

    /**
     * Comprueba la cantidad de nodos mínimos para el inicio de una ejecución.
     * @return true si existe la cantidad de nodos mínimos.
     */
    public boolean comprobarCantidadMinimaDeNodos() {
        return puertos.keySet().size() >= MinimoDeNodos.MIN_GATEWAY_MULTIPLE.getCantidad();
    }

    /**
     * Recibe el mensaje y proceso su contenido.
     * @param mensaje mensaje recibido.
     * @throws Exception si al verificar la firma del mensaje surge una excepción
     */
    @Override
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

    /**
     * Procesa el bloloque recibido: almacena los bloques y si se reciben dos del mismo tipo se llama al método para
     * comparar los bloques.
     * @param bloqueMultiple bloque recibido.
     */
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

    /**
     * Compara los hashes de los dos bloques de un tipo especificado. En caso de que los hashes sean iguales, se agrega
     * el bloque del nodo con el menor id y se vacia el buffer de bloques en espera del tipo epecificado.
     * @param tipo tipo de blockchain de lo bloques a comparar.
     */
    private synchronized void compararBloques(Tipo tipo) {
        List<BloqueMultiple> bloquesAComparar = bloquesEnEspera.get(tipo);
        BloqueMultiple primerBloqueMultiple = bloquesAComparar.get(0);
        BloqueMultiple segundoBloqueMultiple = bloquesAComparar.get(1);
        if (primerBloqueMultiple.getFooter().getHash().equals(segundoBloqueMultiple.getFooter().getHash())) {
            System.out.println("Creación correcta");
            actualizarTransaccionesPendientes(tipo, true);
            System.out.println("Cantidad de bloques: " + ++contadorDeBloques);
        } else {
            System.out.println("---------------ERROR--------------");
            actualizarTransaccionesPendientes(tipo, false);
        }
        transaccionesEscogidas.put(tipo, new ArrayList<>());
        bloquesEnEspera.put(tipo, new ArrayList<>());
    }

    /**
     * Agrega una transacción recibida a la lista de transacciones en el hashmap de transacciones pendientes.
     * @param transaccionMultiple transacción recibida.
     */
    public synchronized void recibirTransaccion(TransaccionMultiple transaccionMultiple) {
        Tipo tipo = transaccionMultiple.getTipo();
        transaccionesPendientes.get(tipo).add(transaccionMultiple);
    }

    /**
     * Escoge transacciones de un tipo específicado de la lista de transacciones de ese tipo en el HashMap de
     * transacciones pendientes.
     * @param tipo tipo de las transacciones.
     * @return transacciones escogidas del tipo especificado.
     */
    public List<TransaccionMultiple> escogerTransacciones(Tipo tipo) {
        for (int i = 0; (i < TRANSACCIONES_MAXIMAS_POR_BLOQUE) && (i < transaccionesPendientes.get(tipo).size()); i++) {
            transaccionesEscogidas.get(tipo).add(transaccionesPendientes.get(tipo).get(i));
        }
        return transaccionesEscogidas.get(tipo);
    }

    /**
     * Si la comparación de dos bloques del mismo tipo fue correcta se eliminan las transacciones procesadas por los
     * bloques, pero si la compración no fue correcta se incrementa el número de transacciones fallidas.
     * A las 3 comparaciones fallidas se eliminan las transacciones que están causando problemas.
     * @param tipo tipo de boques.
     * @param seCreoExitosamenteElBloque booleano que indica el resultado de la comparación de dos bloques.
     */
    public void actualizarTransaccionesPendientes(Tipo tipo, boolean seCreoExitosamenteElBloque) {
        if (seCreoExitosamenteElBloque || numeroDeCreacionesFallidas == 3){
            for (TransaccionMultiple transaccionMultiple : transaccionesEscogidas.get(tipo)) {
                transaccionesPendientes.get(tipo).remove(transaccionMultiple);
            }
            numeroDeCreacionesFallidas = 0;
        } else {
            numeroDeCreacionesFallidas++;
        }
    }

    /**
     * Agrega la información de un nodo que se conectó a la red.
     * @param infoNodo información de un nodo.
     */
    @Override
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

    /**
     * Envía un paquete de transacciones a un nodo para que cree un bloque con las transacciones.
     * @param direccionDeNodo dirección IP del nodo seleccionado.
     * @param puerto puerto del nodo seleccionado.
     * @param paquete paquete de transacciones.
     */
    public void mandarCrearBloque(String direccionDeNodo, int puerto, Paquete paquete) {
        salida.mandarACrearBloque(direccionDeNodo, puerto, paquete);
    }

    /**
     * Agrega los nodos seleccionados a los nodos posibles y crea una nueva lista para nodos seleccionados.
     */
    public void reiniciarNodosPosibles() {
        nodosPosibles.addAll(nodosSeleccionados);
        nodosSeleccionados = new ArrayList<>();
    }

    /**
     * Obtiene un nodo y lo pasa de la lista de nodos posibles a la lista de nodos seleccionados.
     * @return dirección IP del nodo seleccionado.
     */
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
