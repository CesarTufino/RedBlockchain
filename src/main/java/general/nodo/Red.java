package general.nodo;

import general.mensajes.InfoNodo;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La clase Red representa la información que se encuentra en todos los nodos de la red blockchain.
 */
public abstract class Red implements Serializable {
    /**
     * Lista de tiempos (lapso que se demora en encontrar los bloques previos)
     */
    protected List<Double> searchTimes = new ArrayList<>();
    /**
     * Intercambios de dinero.
     */
    protected List<Double> exchangeMoney1 = new ArrayList<>();
    /**
     * Tabla de mapeo de direcciones y PublicKey para verificar firmas.
     */
    protected Map<String, PublicKey> keyTable = new HashMap<>();
    /**
     * Tabla de mapeo de direcciones y puertos.
     */
    protected Map<String, Integer> puertos = new HashMap<>();

    public Red() {
    }

    /**
     * Obtiene los resultados finales de una ejecución de la red blockchain.
     * @return resultados finales.
     */
    public abstract String getStats();

    /**
     * Obtiene el conjunto de direcciones IP y puertos almacenados en la red.
     * @return direcciones IP y puertos en la red.
     */
    public Map<String, Integer> getPuertos() {
        return puertos;
    }

    /**
     * Obtiene una lista con los totales de dinero intercambiado en cada iteración.
     * @return lista con los totales de dinero intercambiado en cada iteración.
     */
    public List<Double> getExchangeMoney1() {
        return exchangeMoney1;
    }

    /**
     * Obtiene una lista de los tiempos de busqueda del bloque anterior (o bloques anteriores).
     * @return lista de los tiempos de busqueda.
     */
    public List<Double> getSearchTimes() {
        return searchTimes;
    }

    /**
     * Obtiene la clave publica a partir de la dirección IP de un nodo.
     * @param direccion dirección IP de un nodo.
     * @return clave publica que le corresponde al nodo de la dirección IP.
     */
    public PublicKey obtenerClavePublicaPorDireccion(String direccion) {
        return keyTable.get(direccion);
    }

    /**
     * Obtiene la cantidad de nodos registrados en la tabla de mapeo de direcciones y PublicKey de la red.
     * @return cantidad de nodos en la red.
     */
    public int obtenerCantidadDeNodos() {
        return keyTable.keySet().size();
    }

    /**
     * Agrega la información de un nodo a la red.
     * @param infoNodo información de un nodo.
     */
    public abstract void addNode(InfoNodo infoNodo);

}
