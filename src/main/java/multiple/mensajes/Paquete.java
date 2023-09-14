package multiple.mensajes;

import constantes.Tipo;
import multiple.mensajes.Transaccion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Paquete implements Serializable {

    private final Tipo tipo;

    private List<Transaccion> transacciones = new ArrayList<>();

    public Paquete(Tipo tipo, List<Transaccion> transacciones) {
        this.tipo = tipo;
        this.transacciones = transacciones;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }
}
