package tradicional.nodo.gatewayVersion;

import general.constantes.Direccion;
import general.nodo.Gateway;
import general.constantes.MinimoDeNodos;
import multiple.mensajes.TransaccionMultiple;
import tradicional.blockchain.BloqueTradicional;
import general.mensajes.InfoNodo;
import general.mensajes.Mensaje;
import tradicional.mensajes.TransaccionTradicional;
import general.utils.HashUtil;
import general.utils.RsaUtil;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * La clase GatewayTradicional representa el dispositivo intermedio utilizado para gestionar las transacciones de los
 * bloques en una red de blockchain tradicional.
 */
public class GatewayTradicional extends Gateway {
    private ArrayList<TransaccionTradicional> transaccionesPendientes = new ArrayList<>();
    private ArrayList<TransaccionTradicional> transaccionesEscogidas = new ArrayList<>();
    private List<BloqueTradicional> bloquesEnEspera = new ArrayList<>();

    public GatewayTradicional(Direccion direccion) {
        super(direccion);
    }

    /**
     * Comprueba la cantidad de nodos mínimos para el inicio de una ejecución.
     * @return true si existe la cantidad de nodos mínimos.
     */
    public boolean comprobarCantidadMinimaDeNodos() {
        return puertos.keySet().size() >= MinimoDeNodos.MIN_GATEWAY_TRADICIONAL.getCantidad();
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
            TransaccionTradicional transaccionTradicional = (TransaccionTradicional) (contenido);
            recibirTransaccion(transaccionTradicional);
        }
        if (tipoDeMensaje == 1) {
            System.out.println("Bloque recibido");
            BloqueTradicional bloqueTradicional = (BloqueTradicional) contenido;
            recibirBloque(bloqueTradicional);
        }
    }

    /**
     * Procesa el bloloque recibido: almacena los bloques y si se reciben dos se llama al método para comparar los
     * bloques.
     * @param bloqueTradicional bloque recibido.
     */
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

    /**
     * Compara los hashes de los dos bloques. En caso de que los hashes sean iguales, se agregamel bloque del nodo con
     * el menor id y se vacia el buffer de bloques en espera.
     */
    private synchronized void compararBloques() {
        BloqueTradicional primerBloqueTradicional = bloquesEnEspera.get(0);
        BloqueTradicional segundoBloqueTradicional = bloquesEnEspera.get(1);
        if (primerBloqueTradicional.getFooter().getHash().equals(segundoBloqueTradicional.getFooter().getHash())){
            System.out.println("Creación correcta");
            actualizarTransaccionesPendientes(true);
            System.out.println("Cantidad de bloques: " + ++contadorDeBloques);
        } else{
            System.out.println("---------------ERROR--------------");
            actualizarTransaccionesPendientes(false);
        }
        transaccionesEscogidas = new ArrayList<>();
        bloquesEnEspera = new ArrayList<>();
    }

    /**
     * Agrega una transacción recibida a la lista de transacciones en el hashmap de transacciones pendientes.
     * @param transaccionTradicional transacción recibida.
     */
    public synchronized void recibirTransaccion(TransaccionTradicional transaccionTradicional) {
        transaccionesPendientes.add(transaccionTradicional);
    }

    /**
     * Escoge transacciones de la lista de transacciones pendientes.
     * @return transacciones escogidas.
     */
    public List<TransaccionTradicional> escogerTransacciones() {
        for (int i = 0; (i < TRANSACCIONES_MAXIMAS_POR_BLOQUE) && (i < transaccionesPendientes.size()); i++) {
            transaccionesEscogidas.add(transaccionesPendientes.get(i));
        }
        return transaccionesEscogidas;
    }

    /**
     * Si la comparación de dos bloques fue correcta se eliminan las transacciones procesadas por los bloques, pero si
     * la compración no fue correcta se incrementa el número de transacciones fallidas. A las 3 comparaciones fallidas
     * se eliminan las transacciones que están causando problemas.
     * @param seCreoExitosamenteElBloque booleano que indica el resultado de la comparación de dos bloques.
     */
    public void actualizarTransaccionesPendientes(boolean seCreoExitosamenteElBloque) {
        if (seCreoExitosamenteElBloque || numeroDeCreacionesFallidas == 3){
            for (TransaccionTradicional transaccionTradicional : transaccionesEscogidas) {
                transaccionesPendientes.remove(transaccionTradicional);
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

    /**
     * Envía una lista de transacciones a un nodo para que cree un bloque con las transacciones.
     * @param direccionDeNodo dirección IP del nodo seleccionado.
     * @param puerto puerto del nodo seleccionado.
     * @param transacciones transacciones enviadas.
     */
    public void mandarCrearBloque(String direccionDeNodo, int puerto, List<TransaccionTradicional> transacciones) {
        salida.mandarACrearBloque(direccionDeNodo, puerto, transacciones);
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
        String direccionSeleciconada = getNodosPosibles().get(numeroPseudoaleatorio);
        getNodosSeleccionados().add(direccionSeleciconada);
        getNodosPosibles().remove(direccionSeleciconada);
        return direccionSeleciconada;
    }

}
