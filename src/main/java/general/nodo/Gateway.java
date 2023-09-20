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

public abstract class Gateway {

    protected final int TRANSACCIONES_MAXIMAS_POR_BLOQUE = 10;
    protected List<String> nodosSeleccionados = new ArrayList<>();
    protected List<String> nodosPosibles = new ArrayList<>();
    protected Direccion direccion;
    protected Salida salida;
    protected int contadorDeBloques;
    protected Map<String, PublicKey> keyTable = new HashMap<>();
    protected Map<String, Integer> puertos = new HashMap<>();

    public Gateway(Direccion direccion) {
        this.direccion = direccion;
        this.salida = new Salida();
        this.contadorDeBloques = 0;
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

    public int getContadorDeBloques() {
        return contadorDeBloques;
    }

    public abstract void recibirMensaje(Mensaje mensaje) throws Exception;

    public abstract void agregarNodo(InfoNodo infoNodo);
}
