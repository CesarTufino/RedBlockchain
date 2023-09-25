package general.nodo;

import general.conexion.Salida;
import general.constantes.Direccion;
import general.mensajes.InfoNodo;
import general.mensajes.Mensaje;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La clase Gateway representa el dispositivo intermedio utilizado para gestionar las transacciones de los bloques.
 */
public abstract class Gateway {
    protected final int TRANSACCIONES_MAXIMAS_POR_BLOQUE = 10;
    protected List<String> nodosSeleccionados = new ArrayList<>();
    protected List<String> nodosPosibles = new ArrayList<>();
    protected Direccion direccion;
    protected Salida salida;
    protected int contadorDeBloques;
    protected Map<String, PublicKey> keyTable = new HashMap<>();
    protected Map<String, Integer> puertos = new HashMap<>();
    protected int numeroDeCreacionesFallidas;

    public Gateway(Direccion direccion) {
        this.direccion = direccion;
        this.salida = new Salida();
        this.contadorDeBloques = 0;
        this.numeroDeCreacionesFallidas = 0;
    }

    /**
     * Obtiene la lista de nodos seleccionados en una iteración,
     * @return nodos seleccionados.
     */
    public List<String> getNodosSeleccionados() {
        return nodosSeleccionados;
    }

    /**
     * Obtiene una lista de los nodos que pueden ser seleccionados en una iteración.
     * @return nodos posibles.
     */
    public List<String> getNodosPosibles() {
        return nodosPosibles;
    }

    /**
     * Obtiene la dirección del gateway.
     * @return dirección del gateway.
     */
    public Direccion getDireccion() {
        return direccion;
    }

    /**
     * Obtiene el conjunto de direcciones IP y puertos de los nodos que se conectaron a la red y enviaron su información
     * al gateway.
     * @return direcciones IP y puertos de los nodos registrados en el gateway.
     */
    public Map<String, Integer> getPuertos() {
        return puertos;
    }

    /**
     * Obtiene el número de bloques que el gateway ha aceptado luego de la comparación.
     * @return contador de bloques del gateway.
     */
    public int getContadorDeBloques() {
        return contadorDeBloques;
    }

    /**
     * Recibe el mensaje y proceso su contenido.
     * @param mensaje mensaje recibido.
     * @throws Exception si al verificar la firma del mensaje surge una excepción
     */
    public abstract void recibirMensaje(Mensaje mensaje) throws Exception;

    /**
     * Agrega la información de un nodo que se conectó a la red.
     * @param infoNodo información de un nodo.
     */
    public abstract void agregarNodo(InfoNodo infoNodo);

}
