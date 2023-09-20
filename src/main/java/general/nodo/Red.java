package general.nodo;

import general.mensajes.InfoNodo;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    protected Map<String, Integer> puertos = new HashMap<>();

    public Red() {
    }

    public abstract String getStats();

    public Map<String, Integer> getPuertos() {
        return puertos;
    }

    public List<Double> getExchangeMoney1() {
        return exchangeMoney1;
    }

    public List<Double> getSearchTimes() {
        return searchTimes;
    }

    public PublicKey obtenerClavePublicaPorDireccion(String direccion) {
        return keyTable.get(direccion);
    }

    public int obtenerCantidadDeNodos() {
        return keyTable.keySet().size();
    }

    public abstract void addNode(InfoNodo infoNodo);

}
